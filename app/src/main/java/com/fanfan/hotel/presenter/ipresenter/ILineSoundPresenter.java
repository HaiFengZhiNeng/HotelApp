package com.fanfan.hotel.presenter.ipresenter;

import com.fanfan.hotel.common.enums.SpecialType;
import com.fanfan.hotel.common.presenter.BasePresenter;
import com.fanfan.hotel.common.presenter.BaseView;
import com.fanfan.hotel.model.xf.service.Cookbook;
import com.fanfan.hotel.model.xf.service.News;
import com.fanfan.hotel.model.xf.service.Poetry;
import com.fanfan.hotel.model.xf.service.englishEveryday.EnglishEveryday;
import com.fanfan.hotel.model.xf.service.radio.Radio;

/**
 * Created by android on 2017/12/18.
 */

public abstract class ILineSoundPresenter implements BasePresenter {

    private ILineSoundView mBaseView;

    public ILineSoundPresenter(ILineSoundView baseView) {
        mBaseView = baseView;
    }

    public abstract void initIat();

    public abstract void initAiui();

    public abstract void buildIat();

    public abstract void updateLocation(String lexiconName, String lexiconContents);

    public abstract void startRecognizerListener();

    public abstract void stopRecognizerListener();

    public abstract void startTextNlp(String result);

    public abstract void aiuiWriteText(String text);

    public abstract void playVoice(String url);

    public abstract void stopVoice();

    public abstract void setSpeech(boolean speech);

    public interface ILineSoundView extends BaseView {


        void doAiuiAnwer(String anwer);

        void refHomePage(String question);

        void refHomePage(String question, String finalText);

        void refHomePage(String question, String finalText, String url);

        void refHomePage(String question, News news);

        void refHomePage(String question, Radio radio);

        void refHomePage(String question, Poetry poetry);

        void refHomePage(String question, Cookbook cookbook);

        void refHomePage(String question, EnglishEveryday englishEveryday);

        void special(String result, SpecialType type);

        void doCallPhone(String value);

        void startPage(SpecialType specialType);

        void spakeMove(SpecialType specialType, String result);

        void spakeLogout();

        void addfromInitinfo(String msg);

        void onCompleted();

        void initIatFinish();
    }

}
