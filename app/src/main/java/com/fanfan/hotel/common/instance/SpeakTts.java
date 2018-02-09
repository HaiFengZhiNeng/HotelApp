package com.fanfan.hotel.common.instance;

import android.content.Context;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.seabreeze.log.Print;

/**
 * Created by android on 2017/12/21.
 */

public class SpeakTts{

    private SpeechSynthesizer mTts;

    private volatile static SpeakTts mSpeakTts;

    public static SpeakTts getInstance() {
        if (mSpeakTts == null) {
            synchronized (SpeakTts.class) {
                if (mSpeakTts == null) {
                    mSpeakTts = new SpeakTts();
                }
            }
        }
        return mSpeakTts;
    }

    private SpeakTts() {
    }

    public void initTts(Context context, InitListener listener) {
        if(mTts == null) {
            mTts = SpeechSynthesizer.createSynthesizer(context, listener);
        }
    }

    public SpeechSynthesizer mTts() {
        return mTts;
    }

}
