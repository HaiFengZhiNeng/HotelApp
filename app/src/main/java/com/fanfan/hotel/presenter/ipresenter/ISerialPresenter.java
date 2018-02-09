package com.fanfan.hotel.presenter.ipresenter;

import com.fanfan.hotel.common.presenter.BasePresenter;
import com.fanfan.hotel.common.presenter.BaseView;
import com.fanfan.hotel.model.SerialBean;

/**
 * Created by android on 2017/12/26.
 */

public abstract class ISerialPresenter implements BasePresenter {

    private ISerialView mBaseView;

    public ISerialPresenter(ISerialView baseView) {
        mBaseView = baseView;
    }

    public abstract void receiveMotion(int type, String motion);

    public abstract void onDataReceiverd(SerialBean serialBean);

    public interface ISerialView extends BaseView {

        void stopAll();

        void onMoveStop();
    }
}
