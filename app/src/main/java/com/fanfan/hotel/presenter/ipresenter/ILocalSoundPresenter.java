package com.fanfan.hotel.presenter.ipresenter;

import com.fanfan.hotel.common.enums.SpecialType;
import com.fanfan.hotel.common.presenter.BasePresenter;
import com.fanfan.hotel.common.presenter.BaseView;

/**
 * Created by android on 2017/12/20.
 */

public abstract class ILocalSoundPresenter implements BasePresenter {

    private ILocalSoundView mBaseView;

    public ILocalSoundPresenter(ILocalSoundView baseView) {
        mBaseView = baseView;
    }

    public abstract void initTts();

    public abstract void initIat();

    public abstract void buildTts();

    public abstract void buildIat();

    public abstract void stopTts();

    public abstract void doAnswer(String answer);

    public abstract void startRecognizerListener();

    public abstract void stopRecognizerListener();

    public abstract void stopAll();

    public abstract void stopHandler();

    public abstract void setSpeech(boolean speech);

    public interface ILocalSoundView extends BaseView {

        void refLocalPage(String result);

        void spakeMove(SpecialType specialType, String result);

        void back();
    }
}
