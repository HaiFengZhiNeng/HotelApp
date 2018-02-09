package com.fanfan.hotel.presenter.ipresenter;

import android.graphics.Bitmap;
import android.os.Handler;

import com.fanfan.hotel.common.presenter.BasePresenter;
import com.fanfan.hotel.common.presenter.BaseView;
import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.api.face.bean.FaceIdentify;

/**
 * Created by android on 2017/12/22.
 */

public abstract class IFaceVerifPresenter implements BasePresenter {

    private IFaceverifView mBaseView;

    public IFaceVerifPresenter(IFaceverifView baseView) {
        mBaseView = baseView;
    }

    public abstract Bitmap bitmapSaturation(Bitmap baseBitmap);

    public abstract void faceCompare(Bitmap bitmapA, Bitmap bitmapB);

    public abstract void newPerson(Bitmap bitmap, String IDCard, String name);

    public abstract void modifyPersonName(String personId, String authId);

    public abstract void faceIdentifyFace(Bitmap bitmap);

    public abstract void compareFace(FaceIdentify faceIdentify);

    public abstract void savePersonFace(Bitmap bitmap, String authId);

    public interface IFaceverifView extends BaseView {

        void onError(BaseEvent event);

        void onError(int code, String msg);

        void compareSuccess();

        void similarityLow(float similarity);

        void transfer(Bitmap bitmap);

        void newpersonSuccess(String personId);

        void personModify(String person_id);

        void identifyNoFace();

        void confidenceLow();

        void identifyFaceFinish(String person);

        void saveFinish(String currentTimeStr);
    }
}
