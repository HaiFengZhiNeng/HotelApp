package com.fanfan.hotel.presenter.ipresenter;

import com.fanfan.hotel.common.presenter.BasePresenter;
import com.fanfan.hotel.common.presenter.BaseView;
import com.fanfan.hotel.model.UserInfo;

/**
 * Created by android on 2017/12/25.
 */

public abstract class ILoginPresenter implements BasePresenter {

    private ILoginView mBaseView;

    public ILoginPresenter(ILoginView baseView) {
        mBaseView = baseView;
    }

    public abstract void doLogin(UserInfo info);

    public interface ILoginView extends BaseView {


        void loginSuccess();

        void loginFail(String errMsg);
    }

}
