package com.fanfan.hotel.presenter.ipresenter;

import com.fanfan.hotel.common.presenter.BasePresenter;
import com.fanfan.hotel.common.presenter.BaseView;
import com.fanfan.hotel.model.UserInfo;

/**
 * Created by android on 2017/12/26.
 */

public abstract class IForgetPresenter implements BasePresenter {


    private IForgetView mBaseView;

    public IForgetPresenter(IForgetView baseView) {
        mBaseView = baseView;
    }

    public abstract void doModify(UserInfo info);

    public interface IForgetView extends BaseView {


        void modifySuccess();

        void modifyFail(String errMsg);
    }


}
