package com.fanfan.hotel.presenter;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;

import com.fanfan.hotel.R;
import com.fanfan.hotel.common.Constants;
import com.fanfan.hotel.common.app.HotelApp;
import com.fanfan.hotel.common.enums.SpecialType;
import com.fanfan.hotel.common.instance.SpeakIat;
import com.fanfan.hotel.common.instance.SpeakTts;
import com.fanfan.hotel.presenter.ipresenter.ILocalSoundPresenter;
import com.fanfan.hotel.service.listener.AiuiListener;
import com.fanfan.hotel.service.listener.IatListener;
import com.fanfan.hotel.service.listener.TtsListener;
import com.fanfan.hotel.utils.FucUtil;
import com.fanfan.hotel.utils.PreferencesUtils;
import com.fanfan.hotel.utils.SpecialUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.seabreeze.log.Print;

import java.io.File;
import java.util.Random;

/**
 * Created by android on 2017/12/20.
 */

public class LocalSoundPresenter extends ILocalSoundPresenter implements TtsListener.SynListener, IatListener.RecognListener {

    private static final String LOCAL_GRAMMAR_NAME = "hotel";

    private ILocalSoundView mSoundView;

    private SpeechSynthesizer mTts;
    private SpeechRecognizer mIat;

    private TtsListener mTtsListener;
    private IatListener mIatListener;

    private Handler mHandler = new Handler();

    private String mLaguage;
    private String mSpokesman;

    public LocalSoundPresenter(ILocalSoundView baseView) {
        super(baseView);
        mSoundView = baseView;

        mTtsListener = new TtsListener(this);
        mIatListener = new IatListener(this);
    }

    @Override
    public void start() {
        HotelApp.getInstance().setEngineType(SpeechConstant.TYPE_LOCAL);
        initTts();
        initIat();
    }

    @Override
    public void finish() {
        HotelApp.getInstance().setEngineType(SpeechConstant.TYPE_CLOUD);
        mTtsListener = null;
        mIatListener = null;
    }

    @Override
    public void initTts() {

        mTts = SpeakTts.getInstance().mTts();
        if (mTts == null) {
            SpeakTts.getInstance().initTts(HotelApp.getInstance().getApplicationContext(), new InitListener() {
                @Override
                public void onInit(int code) {
                    if (code != ErrorCode.SUCCESS) {
                        Print.e("初始化失败，错误码：" + code);
                    }
                    Print.e("local initTts success");
                    mTts = SpeakTts.getInstance().mTts();
                }
            });
        }
    }

    @Override
    public void initIat() {

        mIat = SpeakIat.getInstance().mIat();
        if (mIat == null) {

            SpeakIat.getInstance().initIat(HotelApp.getInstance().getApplicationContext(), new InitListener() {
                @Override
                public void onInit(int code) {
                    if (code != ErrorCode.SUCCESS) {
                        Print.e("初始化失败，错误码：" + code);
                    }
                    Print.e("local initIat success");
                    mIat = SpeakIat.getInstance().mIat();
                }
            });
        }
    }

    @Override
    public void buildTts() {
        if (mTts == null) {
            throw new NullPointerException(" mTts is null");
        }
        mTts.setParameter(SpeechConstant.PARAMS, null);
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        mSpokesman = PreferencesUtils.getString(mSoundView.getContext(), Constants.IAT_LOCAL_LANGUAGE_TALKER, "xiaoyan");
        mTts.setParameter(ResourceUtil.TTS_RES_PATH, FucUtil.getResTtsPath(mSoundView.getContext(), mSpokesman));
        mTts.setParameter(SpeechConstant.VOICE_NAME, mSpokesman);
        mTts.setParameter(SpeechConstant.SPEED, "60");
        mTts.setParameter(SpeechConstant.PITCH, "50");
        mTts.setParameter(SpeechConstant.VOLUME, "100");
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }

    @Override
    public void buildIat() {
        if (mIat == null) {
            throw new NullPointerException(" mIat is null");
        }
        mIat.setParameter(SpeechConstant.PARAMS, null);
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        mIat.setParameter(ResourceUtil.ASR_RES_PATH, FucUtil.getResAsrPath(mSoundView.getContext()));
        mIat.setParameter(ResourceUtil.GRM_BUILD_PATH, Constants.GRM_PATH);
        mIat.setParameter(SpeechConstant.LOCAL_GRAMMAR, LOCAL_GRAMMAR_NAME);
        mIat.setParameter(SpeechConstant.MIXED_THRESHOLD, "30");
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        mLaguage = PreferencesUtils.getString(mSoundView.getContext(), Constants.IAT_LANGUAGE_PREFERENCE, "mandarin");
        if (mLaguage.equals("en_us")) {
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
            mIat.setParameter(SpeechConstant.ACCENT, null);
        } else if (mLaguage.equals("cantonese")) {
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mIat.setParameter(SpeechConstant.ACCENT, mLaguage);
        } else {
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mIat.setParameter(SpeechConstant.ACCENT, mLaguage);
        }
        mIat.setParameter(SpeechConstant.VAD_BOS, "99000");
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Constants.GRM_PATH + File.separator + "iat.wav");
    }

    @Override
    public void stopTts() {
        if (mTts.isSpeaking()) {
            mTts.stopSpeaking();
        }
    }

    @Override
    public void doAnswer(String answer) {
        mTts.startSpeaking(answer, mTtsListener);
    }

    @Override
    public void startRecognizerListener() {
        mIat.startListening(mIatListener);
    }


    @Override
    public void stopRecognizerListener() {
        mIat.startListening(null);
        mIat.stopListening();
    }

    @Override
    public void stopAll() {
        stopTts();
        doAnswer(resFoFinal(R.array.wake_up));
    }

    @Override
    public void stopHandler() {
        mHandler.removeCallbacks(runnable);
    }

    @Override
    public void setSpeech(boolean speech) {
        if (speech) {
            startRecognizerListener();
        } else {
            stopRecognizerListener();
        }
    }

    private String resFoFinal(int id) {
        String[] arrResult = ((Activity) mSoundView).getResources().getStringArray(id);
        return arrResult[new Random().nextInt(arrResult.length)];
    }

    @Override
    public void onCompleted() {
        mHandler.postDelayed(runnable, 0);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startRecognizerListener();
        }
    };

    @Override
    public void onSpeakBegin() {
        stopRecognizerListener();
    }

    @Override
    public void onRecognResult(String result) {
        Print.e(result);
        SpecialType specialType = SpecialUtils.doesExistLocal(result);
        if (specialType == SpecialType.NoSpecial) {
            mSoundView.refLocalPage(result);
        } else if (specialType == SpecialType.Forward || specialType == SpecialType.Backoff ||
                specialType == SpecialType.Turnleft || specialType == SpecialType.Turnright) {
            mSoundView.spakeMove(specialType, result);
        } else if (specialType == SpecialType.Map) {
//            mSoundView.openMap();
        } else if (specialType == SpecialType.StopListener) {
            setSpeech(false);
        } else if (specialType == SpecialType.Back) {
            mSoundView.back();
        }
    }

    @Override
    public void onErrInfo() {
        startRecognizerListener();
    }

    @Override
    public void onRecognDown() {
        startRecognizerListener();
    }

    @Override
    public void onNetwork() {
        startRecognizerListener();
    }

    @Override
    public void onLocalError() {
        startRecognizerListener();
    }

    @Override
    public void onInsufficient() {
        mSoundView.showMsg("授权不足");
    }

    @Override
    public void onLevelSmall() {
        onCompleted();
    }
}
