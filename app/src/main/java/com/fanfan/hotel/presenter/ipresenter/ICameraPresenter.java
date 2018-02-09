package com.fanfan.hotel.presenter.ipresenter;

import android.graphics.Bitmap;
import android.hardware.Camera;

import com.fanfan.hotel.common.presenter.BasePresenter;
import com.fanfan.hotel.common.presenter.BaseView;

/**
 * Created by zhangyuanyuan on 2017/9/18.
 */

public abstract class ICameraPresenter implements BasePresenter {


    private ICameraView mBaseView;

    public ICameraPresenter(ICameraView baseView) {
        mBaseView = baseView;
    }

    public abstract void closeCamera();

    public abstract void openCamera();

    public abstract void doStartPreview();

    public abstract void changeCamera();

    public abstract void setMatrix(int width, int height);

    public abstract void cameraAutoFocus();

    public abstract void cameraTakePicture();

    public abstract int getCameraId();

    public abstract int getOrientionOfCamera();

    public abstract void pictureTakenFinsih();

    public abstract boolean cameraFaceDetection();

    public interface ICameraView extends BaseView {

        void previewFinish();

        void pictureTakenSuccess(String savePath);

        void pictureTakenFail();

        void autoFocusSuccess();

        void noFace();

        void tranBitmap(Bitmap bitmap, int num);

        void setCameraFaces(Camera.Face[] faces);
    }

}
