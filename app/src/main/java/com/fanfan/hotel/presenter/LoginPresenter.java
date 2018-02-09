package com.fanfan.hotel.presenter;

import com.fanfan.hotel.im.MyTLSService;
import com.fanfan.hotel.model.UserInfo;
import com.fanfan.hotel.presenter.ipresenter.ILoginPresenter;
import com.fanfan.hotel.service.cache.UserInfoCache;
import com.seabreeze.log.Print;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by android on 2017/12/25.
 */

public class LoginPresenter extends ILoginPresenter implements TLSPwdLoginListener {

    private ILoginView mLoginView;


    public LoginPresenter(ILoginView baseView) {
        super(baseView);
        mLoginView = baseView;

    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }


    @Override
    public void doLogin(UserInfo info) {
        MyTLSService.getInstance().TLSPwdLogin(info.getIdentifier(), info.getUserPass(), this);
    }


    @Override
    public void OnPwdLoginSuccess(TLSUserInfo tlsUserInfo) {
        MyTLSService.getInstance().setLastErrno(0);
        String id = MyTLSService.getInstance().getLastUserIdentifier();
        UserInfo.getInstance().setIdentifier(id);
        UserInfo.getInstance().setUserSig(MyTLSService.getInstance().getUserSig(id));
        UserInfoCache.saveCache(mLoginView.getContext());
        mLoginView.loginSuccess();
    }

    @Override
    public void OnPwdLoginReaskImgcodeSuccess(byte[] bytes) {
        Print.i("OnPwdLoginReaskImgcodeSuccess");
    }

    @Override
    public void OnPwdLoginNeedImgcode(byte[] bytes, TLSErrInfo tlsErrInfo) {
        Print.i("OnPwdLoginNeedImgcode");
    }

    @Override
    public void OnPwdLoginFail(TLSErrInfo tlsErrInfo) {
        MyTLSService.getInstance().setLastErrno(-1);
        Print.e(String.format("%s: %s", tlsErrInfo.ErrCode == TLSErrInfo.TIMEOUT ? "网络超时" : "错误", tlsErrInfo.Msg));
        mLoginView.loginFail(tlsErrInfo.Msg);
    }

    @Override
    public void OnPwdLoginTimeout(TLSErrInfo tlsErrInfo) {
        MyTLSService.getInstance().setLastErrno(-1);
        Print.e(String.format("%s: %s", tlsErrInfo.ErrCode == TLSErrInfo.TIMEOUT ? "网络超时" : "错误", tlsErrInfo.Msg));
        mLoginView.loginFail(tlsErrInfo.Msg);
    }
}
