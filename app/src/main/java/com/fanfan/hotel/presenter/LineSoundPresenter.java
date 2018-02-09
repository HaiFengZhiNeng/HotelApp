package com.fanfan.hotel.presenter;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Handler;
import android.text.TextUtils;

import com.fanfan.hotel.R;
import com.fanfan.hotel.common.Constants;
import com.fanfan.hotel.common.app.HotelApp;
import com.fanfan.hotel.common.enums.SpecialType;
import com.fanfan.hotel.common.instance.SpeakIat;
import com.fanfan.hotel.common.instance.SpeakTts;
import com.fanfan.hotel.model.hotword.HotWord;
import com.fanfan.hotel.model.hotword.Userword;
import com.fanfan.hotel.model.xf.Telephone;
import com.fanfan.hotel.model.xf.service.Cookbook;
import com.fanfan.hotel.model.xf.service.Flight;
import com.fanfan.hotel.model.xf.service.Joke;
import com.fanfan.hotel.model.xf.service.News;
import com.fanfan.hotel.model.xf.service.Poetry;
import com.fanfan.hotel.model.xf.service.cmd.Slots;
import com.fanfan.hotel.model.xf.service.constellation.Constellation;
import com.fanfan.hotel.model.xf.service.constellation.Fortune;
import com.fanfan.hotel.model.xf.service.englishEveryday.EnglishEveryday;
import com.fanfan.hotel.model.xf.service.radio.Radio;
import com.fanfan.hotel.model.xf.service.riddle.Riddle;
import com.fanfan.hotel.model.xf.service.stock.Detail;
import com.fanfan.hotel.model.xf.service.stock.Stock;
import com.fanfan.hotel.model.xf.service.story.Story;
import com.fanfan.hotel.model.xf.service.train.Train;
import com.fanfan.hotel.model.xf.service.wordFinding.WordFinding;
import com.fanfan.hotel.presenter.ipresenter.ILineSoundPresenter;
import com.fanfan.hotel.service.listener.AiuiListener;
import com.fanfan.hotel.service.listener.IatListener;
import com.fanfan.hotel.service.listener.TtsListener;
import com.fanfan.hotel.utils.AudioUtil;
import com.fanfan.hotel.utils.FileUtil;
import com.fanfan.hotel.utils.FucUtil;
import com.fanfan.hotel.utils.PreferencesUtils;
import com.fanfan.hotel.utils.SpecialUtils;
import com.fanfan.hotel.utils.music.MediaPlayerUtil;
import com.fanfan.hotel.utils.tele.TelNumMatch;
import com.fanfan.hotel.utils.tele.TelePhoneUtils;
import com.fanfan.youtu.utils.GsonUtil;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIMessage;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.seabreeze.log.Print;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by android on 2017/12/18.
 */

public class LineSoundPresenter extends ILineSoundPresenter implements IatListener.RecognListener,
        AiuiListener.AiListener {

    private static final String GRAMMAR_BNF = "bnf";
    private static final String GRAMMAR_ABNF = "abnf";

    private static final String LOCAL_GRAMMAR_NAME = "hotel";
    private static final String GRAMMAR_LOCAL_FILE_NAME = "hotel.bnf";
    private static final String GRAMMAR_CLOUD_FILE_NAME = "abnf.abnf";

    private static final String STANDARD_TEXT_ENCODING = "utf-8";

    private static final String ASSESTS_AIUI_CFG = "cfg/aiui_phone.cfg";

    private ILineSoundView mSoundView;

    private SpeechRecognizer mIat;
    private AIUIAgent mAIUIAgent;

    private int ret = 0;

    private IatListener mIatListener;
    private AiuiListener aiuiListener;

    private boolean isMedia;

    //构建语法
    private String content;
    private String grammarType;
    private boolean isBuild;

    private String mLaguage;

    public LineSoundPresenter(ILineSoundView baseView) {
        super(baseView);
        mSoundView = baseView;

        mIatListener = new IatListener(this);
        aiuiListener = new AiuiListener((Activity) mSoundView.getContext(), this);
    }

    @Override
    public void start() {
        HotelApp.getInstance().setEngineType(SpeechConstant.TYPE_CLOUD);
        initAiui();
        initIat();
        isMedia = true;
    }

    @Override
    public void finish() {
        if (mAIUIAgent != null) {
            mAIUIAgent.destroy();
        }
        aiuiListener = null;
        mIatListener = null;
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
                    Print.e("initIat success");
                    mIat = SpeakIat.getInstance().mIat();
                }
            });
        }
    }

    @Override
    public void initAiui() {
        String params = FucUtil.readAssets(mSoundView.getContext(), ASSESTS_AIUI_CFG);
        mAIUIAgent = AIUIAgent.createAgent(mSoundView.getContext(), params, aiuiListener);
        AIUIMessage startMsg = new AIUIMessage(AIUIConstant.CMD_START, 0, 0, null, null);
        mAIUIAgent.sendMessage(startMsg);
    }


    @Override
    public void buildIat() {
        if (mIat == null) {
            initIat();
        }
        isBuild = PreferencesUtils.getBoolean(mSoundView.getContext(), Constants.IAT_CLOUD_BUILD, false);
        if (isBuild) {
            isBuild = PreferencesUtils.getBoolean(mSoundView.getContext(), Constants.IAT_LOCAL_BUILD, false);
            if (!isBuild) {
                HotelApp.getInstance().setEngineType(SpeechConstant.TYPE_LOCAL);
                mIat.setParameter(SpeechConstant.PARAMS, null);
                mIat.setParameter(SpeechConstant.ENGINE_TYPE, HotelApp.getInstance().getEngineType());
                mIat.setParameter(SpeechConstant.TEXT_ENCODING, STANDARD_TEXT_ENCODING);
                FileUtil.mkdir(Constants.GRM_PATH);
                mIat.setParameter(ResourceUtil.GRM_BUILD_PATH, Constants.GRM_PATH);
                mIat.setParameter(ResourceUtil.ASR_RES_PATH, FucUtil.getResAsrPath(mSoundView.getContext()));
                mIat.setParameter(SpeechConstant.LOCAL_GRAMMAR, LOCAL_GRAMMAR_NAME);
                mIat.setParameter(SpeechConstant.MIXED_THRESHOLD, "30");
                content = FucUtil.readFile(mSoundView.getContext(), GRAMMAR_LOCAL_FILE_NAME, STANDARD_TEXT_ENCODING);
                grammarType = GRAMMAR_BNF;
            }
        } else {
            mIat.setParameter(SpeechConstant.PARAMS, null);
            mIat.setParameter(SpeechConstant.ENGINE_TYPE, HotelApp.getInstance().getEngineType());
            mIat.setParameter(SpeechConstant.TEXT_ENCODING, STANDARD_TEXT_ENCODING);
            content = FucUtil.readFile(mSoundView.getContext(), GRAMMAR_CLOUD_FILE_NAME, STANDARD_TEXT_ENCODING);
            grammarType = GRAMMAR_ABNF;
        }

        if (!isBuild) {
            ret = mIat.buildGrammar(grammarType, content, mGrammarListener);
            if (ret != ErrorCode.SUCCESS) {
                Print.e("语法构建失败,错误码：" + ret);
            }
        } else {
            initIatFinish();
        }
    }

    private void initIatFinish() {
        if (PreferencesUtils.getBoolean(mSoundView.getContext(), Constants.IAT_LOCAL_BUILD, false) &&
                PreferencesUtils.getBoolean(mSoundView.getContext(), Constants.IAT_CLOUD_BUILD, false) &&
                PreferencesUtils.getBoolean(mSoundView.getContext(), Constants.IAT_LOCAL_UPDATELEXICON, false) &&
                PreferencesUtils.getBoolean(mSoundView.getContext(), Constants.IAT_CLOUD_UPDATELEXICON, false)) {
            startRecognizerListener();
            mSoundView.initIatFinish();
        }
    }

    public void updateLocation(String lexiconName, String lexiconContents) {
        String engineType = HotelApp.getInstance().getEngineType();
        mIat.setParameter(SpeechConstant.PARAMS, null);
        if (engineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        } else if (engineType.equals(SpeechConstant.TYPE_LOCAL)) {
            mIat.setParameter(SpeechConstant.ENGINE_TYPE, engineType);
            mIat.setParameter(ResourceUtil.ASR_RES_PATH, FucUtil.getResAsrPath(mSoundView.getContext()));
            mIat.setParameter(ResourceUtil.GRM_BUILD_PATH, Constants.GRM_PATH);
            mIat.setParameter(SpeechConstant.GRAMMAR_LIST, "hotel");
            mIat.setParameter(SpeechConstant.TEXT_ENCODING, STANDARD_TEXT_ENCODING);
        }
        int ret = mIat.updateLexicon(lexiconName, lexiconContents, mLexiconListener);
        if (ret != ErrorCode.SUCCESS) {
            Print.e("更新词典失败,错误码：" + ret);
        }
    }

    @Override
    public void startRecognizerListener() {
        setIatparameter();
        mIat.startListening(mIatListener);
    }

    private void setIatparameter() {
        if (mIat == null) {
            return;
        }
        String engineType = HotelApp.getInstance().getEngineType();
        mIat.setParameter(SpeechConstant.PARAMS, null);
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, engineType);
        if (engineType.equals(SpeechConstant.TYPE_CLOUD)) {
        } else if (engineType.equals(SpeechConstant.TYPE_LOCAL)) {
            mIat.setParameter(ResourceUtil.ASR_RES_PATH, FucUtil.getResAsrPath(mSoundView.getContext()));
            mIat.setParameter(ResourceUtil.GRM_BUILD_PATH, Constants.GRM_PATH);
            mIat.setParameter(SpeechConstant.LOCAL_GRAMMAR, LOCAL_GRAMMAR_NAME);
            mIat.setParameter(SpeechConstant.MIXED_THRESHOLD, "30");
        }
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
    public void stopRecognizerListener() {
        if (mIat != null) {
            mIat.startListening(null);
            mIat.stopListening();
        }
    }

    @Override
    public void startTextNlp(String result) {
        aiuiWriteText(result);
    }

    @Override
    public void aiuiWriteText(String text) {
        if (TextUtils.isEmpty(text)) {
            Print.e("translateBack null");
            text = "";
        }

        String params = "data_type=text";
        AIUIMessage msgWakeup = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, null, null);
        mAIUIAgent.sendMessage(msgWakeup);
        AIUIMessage msg = new AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0, params, text.trim().getBytes());
        mAIUIAgent.sendMessage(msg);
    }

    @Override
    public void playVoice(String url) {
        if (TextUtils.isEmpty(url))
            return;

        MediaPlayerUtil.getInstance().playMusic(url, new MediaPlayerUtil.OnMusicCompletionListener() {
            @Override
            public void onCompletion(boolean isPlaySuccess) {
                mSoundView.onCompleted();
            }

            @Override
            public void onPrepare() {
                Print.e("onPrepare music ... ");
            }
        });
    }

    @Override
    public void stopVoice() {
        MediaPlayerUtil.getInstance().stopMusic();
    }

//    @Override
//    public void stopAll() {
//        stopTts();
//        doAnswer(resFoFinal(R.array.wake_up));
//    }

//    @Override
//    public void stopHandler() {
//        mHandler.removeCallbacks(runnable);
//    }

    @Override
    public void setSpeech(boolean speech) {
        if (speech) {
            startRecognizerListener();
        } else {
            stopRecognizerListener();
        }
    }

//    private String resFoFinal(int id) {
//        String[] arrResult = ((Activity) mSoundView).getResources().getStringArray(id);
//        return arrResult[new Random().nextInt(arrResult.length)];
//    }

    //**********************************************************************************************
//    @Override
//    public void onCompleted() {
//        mHandler.postDelayed(runnable, 0);
//    }
//
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            startRecognizerListener();
//        }
//    };
//
//    @Override
//    public void onSpeakBegin() {
//        stopRecognizerListener();
//    }

    //**********************************************************************************************
    @Override
    public void onRecognResult(String result) {
        stopRecognizerListener();
        Print.e(result);
        SpecialType specialType = SpecialUtils.doesExist(((Activity) mSoundView.getContext()), result);
        if (specialType == SpecialType.NoSpecial) {
            startTextNlp(result);
        } else if (specialType == SpecialType.Music) {
            mSoundView.special(result, SpecialType.Music);
        } else if (specialType == SpecialType.Story) {
            startTextNlp(result);
        } else if (specialType == SpecialType.Joke) {
            startTextNlp(result);
        } else if (specialType == SpecialType.StopListener) {
            setSpeech(false);
        } else if (specialType == SpecialType.Servce || specialType == SpecialType.CheckIn
                || specialType == SpecialType.CheckOut) {
            mSoundView.startPage(specialType);
        } else if (specialType == SpecialType.Forward || specialType == SpecialType.Backoff ||
                specialType == SpecialType.Turnleft || specialType == SpecialType.Turnright) {
            mSoundView.spakeMove(specialType, result);
        } else if (specialType == SpecialType.Map) {
//            mSoundView.openMap();
        } else if (specialType == SpecialType.Logout) {
            mSoundView.spakeLogout();
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
        Print.e("授权不足");
    }

    @Override
    public void onLevelSmall() {

    }
    //**********************************************************************************************

    @Override
    public void onDoAnswer(String question, String finalText) {
        if (finalText == null) {
            mSoundView.onCompleted();
        } else {
            mSoundView.doAiuiAnwer(finalText);
            mSoundView.refHomePage(question, finalText);
        }
    }

    @Override
    public void onDoAnswer(String question, String text, News news) {
        if (isMedia) {
            playVoice(news.getUrl());
            mSoundView.refHomePage(question, news);
        } else {
            mSoundView.doAiuiAnwer(text + ", " + news.getContent());
        }
    }

    @Override
    public void onDoAnswer(String question, String text, Cookbook cookbook) {
        mSoundView.doAiuiAnwer(text + ", " + cookbook.getSteps());
        mSoundView.refHomePage(question, cookbook);
    }

    @Override
    public void onDoAnswer(String question, Poetry poetry) {
        mSoundView.doAiuiAnwer(poetry.getContent());
        mSoundView.refHomePage(question, poetry);
    }

    @Override
    public void onDoAnswer(String question, String finalText, Joke joke) {
        if (isMedia) {
            if (TextUtils.isEmpty(joke.getMp3Url())) {
                mSoundView.doAiuiAnwer(joke.getTitle() + " : " + joke.getContent());
                mSoundView.refHomePage(question, joke.getTitle() + " : " + joke.getContent());
            } else {
                mSoundView.refHomePage(question, finalText);
                playVoice(joke.getMp3Url());
            }
        } else {
            mSoundView.special(question, SpecialType.Joke);
        }
    }

    @Override
    public void onDoAnswer(String question, String finalText, Story story) {
        if (isMedia) {
            mSoundView.refHomePage(question, finalText);
            playVoice(story.getPlayUrl());
        } else {
            stopRecognizerListener();
            mSoundView.special(question, SpecialType.Joke);
        }
    }

    @Override
    public void onDoAnswer(String question, String finalText, List<Train> trains, Train train0) {
        mSoundView.doAiuiAnwer(finalText);
        mSoundView.refHomePage(question, finalText);
        for (int i = 0; i < trains.size(); i++) {
            Train train = trains.get(i);
            mSoundView.refHomePage(null, train.getEndtime_for_voice() + "的" + train.getTrainType() + " " + train.getTrainNo() + "" +
                    " " + train.getOriginStation() + " -- " + train.getTerminalStation()
                    + " , 运行时间：" + train.getRunTime());
        }
    }

    @Override
    public void onDoAnswer(String question, String finalText, List<Flight> flights, Flight flight0) {
        mSoundView.doAiuiAnwer(finalText);
        mSoundView.refHomePage(question, finalText);
        int total;
        if (flights.size() < 10) {
            total = flights.size();
        } else {
            total = 10;
        }
        for (int i = 0; i < total; i++) {
            Flight flight = flights.get(i);
            mSoundView.refHomePage(null, flight.getEndtime_for_voice() + "从" + flight.getDepartCity() + "出发， "
                    + flight.getEndtime_for_voice() + "到达" + flight.getArriveCity() + ", " +
                    flight.getCabinInfo() + "价格是：" + flight.getPrice());
        }
    }

    @Override
    public void onNoAnswer(String question, String finalText, String otherText) {
        onDoAnswer(question, finalText);
    }

    @Override
    public void onDoAnswer(String question, String finalText, Radio radio) {
        if (isMedia) {
            mSoundView.refHomePage(question, radio);
            playVoice(radio.getUrl());
        } else {
            stopRecognizerListener();
            mSoundView.special(question, SpecialType.Joke);
        }
    }

    @Override
    public void onMusic(String question, String finalText) {
        onDoAnswer(question, finalText);
    }

    @Override
    public void onTranslation(String question, String value) {
        onDoAnswer(question, value);
    }

    @Override
    public void onDoAnswer(String question, Slots slotsCmd) {
        int volume = AudioUtil.getInstance(mSoundView.getContext()).getMediaVolume();
        int maxVolume = AudioUtil.getInstance(mSoundView.getContext()).getMediaMaxVolume();
        int node = maxVolume / 5;
        String answer = "不支持此音量控制";
        if (slotsCmd.getName().equals("insType")) {
            if (slotsCmd.getValue().equals("volume_plus")) {
                if (volume == maxVolume) {
                    answer = "当前已是最大音量了";
                } else {
                    answer = "已增大音量";
                    volume = volume + node;
                    if (volume > maxVolume) {
                        volume = maxVolume;
                    }
                    AudioUtil.getInstance(mSoundView.getContext()).setMediaVolume(volume);
                }
            } else if (slotsCmd.getValue().equals("volume_minus")) {
                if (volume == 0) {
                    answer = "当前已是最小音量了";
                } else {
                    answer = "已减小音量";
                    volume = volume - node;
                    if (volume < 0) {
                        volume = 0;
                    }
                    AudioUtil.getInstance(mSoundView.getContext()).setMediaVolume(volume);
                }
            } else if (slotsCmd.getValue().equals("unmute")) {
                answer = "您可以说 “增大音量” 或 “减小音量” ，我会帮您改变的";
            }
        }

        onDoAnswer(question, answer);
    }

    @Override
    public void onDoAnswer(String question, String finalText, EnglishEveryday englishEveryday) {
        mSoundView.doAiuiAnwer(englishEveryday.getContent());
        mSoundView.refHomePage(question, englishEveryday);
    }

    @Override
    public void onDoAnswer(String question, String finalText, Constellation constellation) {
        StringBuffer sb = new StringBuffer();
        List<Fortune> fortunes = constellation.getFortune();
        sb.append(finalText);
        for (int i = 0; i < fortunes.size(); i++) {
            Fortune fortune = fortunes.get(i);
            sb.append(fortune.getName() + " : " + fortune.getDescription());
        }
        mSoundView.doAiuiAnwer(sb.toString());
        mSoundView.refHomePage(question, sb.toString());
    }

    @Override
    public void onDoAnswer(String question, String finalText, Stock stock) {
        if (stock == null) {
            onDoAnswer(question, finalText);
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(finalText);
        sb.append("\n截止到" + stock.getUpdateDateTime() + ", " + stock.getName() + " " + stock.getStockCode() +
                ", 当前价格为 ： " + stock.getOpeningPrice() + ", 上升率为 ： " + stock.getRiseRate() +
                " 详情请查看列表信息");
        mSoundView.doAiuiAnwer(sb.toString());

        sb.append("\n最高价 ： " + stock.getHighPrice());
        sb.append("  最低价 ： " + stock.getLowPrice());
        List<Detail> details = stock.getDetail();
        for (int i = 0; i < details.size(); i++) {
            Detail detail = details.get(i);
            sb.append("\n" + detail.getCount() + " " + detail.getRole() + " " + detail.getPrice());
        }
        mSoundView.refHomePage(question, sb.toString());
    }

    @Override
    public void onDoAnswer(String question, String finalText, Riddle riddle) {
        mSoundView.doAiuiAnwer(riddle.getTitle() + "\n谜底请查看列表");
        mSoundView.refHomePage(question, riddle.getTitle() + "\n\n" + riddle.getAnswer() + "\n");
    }

    @Override
    public void onDoAnswer(String question, String finalText, WordFinding wordFinding) {
        List<String> results;
        int count = 5;
        StringBuffer sb = new StringBuffer();
        if (finalText.contains("反义词")) {
            results = wordFinding.getAntonym();
        } else {
            results = wordFinding.getSynonym();
        }
        if (results.size() > count) {
            int random = new Random().nextInt(results.size() - count);
            for (int i = 0; i < count; i++) {
                sb.append("\n" + results.get(random + i));
            }
        } else {
            for (int i = 0; i < results.size(); i++) {
                sb.append("\n" + results.get(i));
            }
        }
        mSoundView.doAiuiAnwer(sb.toString());
        mSoundView.refHomePage(question, sb.toString());
    }

    @Override
    public void onDoDial(String question, String value) {
        if (TelNumMatch.matchNum(value) == 5 || TelNumMatch.matchNum(value) == 4) {
            List<Telephone> telephones = TelePhoneUtils.queryContacts(mSoundView.getContext(), value);
            if (telephones != null && telephones.size() > 0) {
                if (telephones.size() == 1) {
                    List<String> phones = telephones.get(0).getPhone();
                    if (phones != null && phones.size() > 0) {
                        if (phones.size() == 1) {
                            String phoneNumber = phones.get(0);
                            mSoundView.doAiuiAnwer("为您拨打 ： " + phoneNumber);
//                            mSoundView.refHomePage(question, "为您拨打 ： " + phoneNumber);
                            mSoundView.doCallPhone(phoneNumber);
                        } else {
                            mSoundView.doAiuiAnwer("为您找到如下号码 ： ");
                            mSoundView.refHomePage(question, "为您找到如下号码 ： ");
                            for (String phone : phones) {
                                mSoundView.refHomePage(null, phone);
                            }
                        }
                    } else {
                        mSoundView.doAiuiAnwer("暂无此名字电话号码");
                        mSoundView.refHomePage(question, "通讯录中暂无");
                    }
                } else {
                    mSoundView.doAiuiAnwer("为您匹配到如下姓名 ： ");
                    mSoundView.refHomePage(question, "为您匹配到如下姓名 ： ");
                    for (Telephone telephone : telephones) {
                        mSoundView.refHomePage(null, telephone.getName());
                    }
                }
            } else {
                mSoundView.doAiuiAnwer("通讯录中暂无" + value);
                mSoundView.refHomePage(question, "通讯录中暂无" + value);
            }
        } else {
            mSoundView.doAiuiAnwer("为您拨打 ： " + value);
            mSoundView.doCallPhone(value);
        }
    }

    @Override
    public void onError() {
        initAiui();
    }

    @Override
    public void onAIUIDowm() {

    }

    @Override
    public void onNoAnswer(String question) {
        Print.e("noAnswer : " + question);
        mSoundView.onCompleted();
    }


    private GrammarListener mGrammarListener = new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarId, SpeechError error) {
            if (error == null) {
                if (HotelApp.getInstance().getEngineType().equals(SpeechConstant.TYPE_LOCAL)) {
                    Print.e("本地语法构建成功：" + grammarId);
                    PreferencesUtils.putBoolean(mSoundView.getContext(), Constants.IAT_LOCAL_BUILD, true);

                    if (!PreferencesUtils.getBoolean(mSoundView.getContext(), Constants.IAT_LOCAL_UPDATELEXICON, false)) {
                        StringBuffer sb = new StringBuffer();
                        String[] arrStandard = FucUtil.resArray(mSoundView.getContext(), R.array.local_standard);
                        for (int i = 0; i < arrStandard.length; i++) {
                            sb.append(arrStandard[i] + "\n");
                        }

                        String[] arrVoiceQuestion = FucUtil.resArray(mSoundView.getContext(), R.array.local_voice_question);
                        for (int i = 0; i < arrVoiceQuestion.length; i++) {
                            sb.append(arrVoiceQuestion[i] + "\n");
                        }

                        String[] arrVideoQuestion = FucUtil.resArray(mSoundView.getContext(), R.array.local_video_question);
                        for (int i = 0; i < arrVideoQuestion.length; i++) {
                            sb.append(arrVideoQuestion[i] + "\n");
                        }
                        String[] arrNavigationQuestion = FucUtil.resArray(mSoundView.getContext(), R.array.local_navigation_question);
                        for (int i = 0; i < arrNavigationQuestion.length; i++) {
                            sb.append(arrNavigationQuestion[i] + "\n");
                        }

                        updateLocation("voice", sb.toString());
                    } else {
                        HotelApp.getInstance().setEngineType(SpeechConstant.TYPE_CLOUD);
                        buildIat();
                    }
                } else if (HotelApp.getInstance().getEngineType().equals(SpeechConstant.TYPE_CLOUD)) {
                    Print.e("在线语法构建成功：" + grammarId);
                    PreferencesUtils.putBoolean(mSoundView.getContext(), Constants.IAT_CLOUD_BUILD, true);

                    if (!PreferencesUtils.getBoolean(mSoundView.getContext(), Constants.IAT_CLOUD_UPDATELEXICON, false)) {


                        List<String> words = new ArrayList<>();
                        String[] arrStandard = FucUtil.resArray(mSoundView.getContext(), R.array.local_standard);
                        for (int i = 0; i < arrStandard.length; i++) {
                            words.add(arrStandard[i]);
                        }
                        Userword userword = new Userword();
                        userword.setName("userword");
                        userword.setWords(words);
                        List<Userword> userwordList = new ArrayList<>();
                        userwordList.add(userword);
                        HotWord hotWord = new HotWord(userwordList);

                        updateLocation("userword", GsonUtil.GsonString(hotWord));
                    } else {
                        buildIat();
                    }
                }
            } else {
                Print.e("语法构建失败,错误码：" + error.getErrorCode());
            }
        }
    };

    private LexiconListener mLexiconListener = new LexiconListener() {
        @Override
        public void onLexiconUpdated(String s, SpeechError error) {
            if (error == null) {
                String engineType = HotelApp.getInstance().getEngineType();
                if (engineType.equals(SpeechConstant.TYPE_CLOUD)) {
                    Print.e("在线热词上传成功");
                    PreferencesUtils.putBoolean(mSoundView.getContext(), Constants.IAT_CLOUD_UPDATELEXICON, true);
                    buildIat();
                } else if (engineType.equals(SpeechConstant.TYPE_LOCAL)) {
                    Print.e("本地词典更新成功");
                    PreferencesUtils.putBoolean(mSoundView.getContext(), Constants.IAT_LOCAL_UPDATELEXICON, true);

                    HotelApp.getInstance().setEngineType(SpeechConstant.TYPE_CLOUD);
                    buildIat();
                }
            } else {
                Print.e("词典更新失败,错误码：" + error.getErrorCode());
            }
        }
    };
}
