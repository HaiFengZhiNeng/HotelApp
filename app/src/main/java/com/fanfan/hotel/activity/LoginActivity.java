package com.fanfan.hotel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fanfan.hotel.R;
import com.fanfan.hotel.common.activity.BarBaseActivity;
import com.fanfan.hotel.common.base.BaseActivity;
import com.fanfan.hotel.model.UserInfo;
import com.fanfan.hotel.presenter.LoginPresenter;
import com.fanfan.hotel.presenter.ipresenter.ILoginPresenter;
import com.seabreeze.log.Print;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by android on 2017/12/25.
 */

public class LoginActivity extends BarBaseActivity implements ILoginPresenter.ILoginView {

    public static final int LOGIN_TO_REGISTER_REQUESTCODE = 99;
    public static final int LOGIN_TO_REGISTER_RESULTCODE = 199;

    public static void newInstance(Activity context, int requestCode) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivityForResult(intent, requestCode);
    }


    @BindView(R.id.usernameWrapper)
    TextInputLayout usernameWrapper;
    @BindView(R.id.passwordWrapper)
    TextInputLayout passwordWrapper;
    @BindView(R.id.login)
    Button login;
    @BindView(R.id.sign_up)
    TextView signUp;
    @BindView(R.id.forget_pwd)
    TextView forgetPwd;
    @BindView(R.id.span1)
    View span1;
    @BindView(R.id.span2)
    View span2;

    private LoginPresenter mLoginPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("登陆");
        mLoginPresenter = new LoginPresenter(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerKeyboardListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterKeyboardListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_TO_REGISTER_REQUESTCODE) {
            if (resultCode == LOGIN_TO_REGISTER_RESULTCODE) {
                doLogin(UserInfo.getInstance().getIdentifier(), UserInfo.getInstance().getUserPass());
            }
        }
    }

    @Override
    protected void setResult() {
        setResult(SplashActivity.BACK_RESULTCODE);
    }

    @Override
    public void onBackPressed() {
        setResult(SplashActivity.BACK_RESULTCODE);
        super.onBackPressed();
    }

    @OnClick({R.id.login, R.id.sign_up, R.id.forget_pwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login:
                hideKeyboard();
                String username = usernameWrapper.getEditText().getText().toString();
                String password = passwordWrapper.getEditText().getText().toString();
                if (username.isEmpty()) {
                    usernameWrapper.setError("账号不能为空!");
                } else if (!validateEmail(username)) {
                    usernameWrapper.setError("请输入6~8位账号!");
                } else if (password.isEmpty()) {
                    usernameWrapper.setError("密码不能为空!");
                } else if (!validatePassword(password)) {
                    passwordWrapper.setError("密码太短!");
                } else {
                    usernameWrapper.setErrorEnabled(false);
                    passwordWrapper.setErrorEnabled(false);
                    doLogin(username, password);
                }

                break;
            case R.id.sign_up:

                RegisterActivity.newInstance(this, LOGIN_TO_REGISTER_REQUESTCODE);
                break;
            case R.id.forget_pwd:
                ForgetActivity.newInstance(this);
                break;
        }
    }

    private boolean validateEmail(String username) {
        if (username.length() > 5 && username.length() < 9) {
            return true;
        }
        return false;
    }

    public boolean validatePassword(String password) {
        return password.length() > 5;
    }


    private void doLogin(String username, String password) {
        UserInfo.getInstance().setIdentifier(username);
        UserInfo.getInstance().setUserPass(password);
        mLoginPresenter.doLogin(UserInfo.getInstance());
    }


    private void registerKeyboardListener() {
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Print.e("onGlobalLayout");
                if (isKeyboardShown(rootView)) {
                    Print.e("软键盘弹起");
                    span1.setVisibility(View.GONE);
                    span2.setVisibility(View.GONE);
                } else {
                    Print.e("软键盘未弹起");
                    span1.setVisibility(View.INVISIBLE);
                    span2.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void unRegisterKeyboardListener() {
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(null);
    }

    private boolean isKeyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showMsg(String msg) {
        showToast(msg);
    }

    @Override
    public void showMsg(int msg) {
        showToast(msg);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void loginSuccess() {
        setResult(SplashActivity.LOGIN_RESULTCODE);
        finish();
    }

    @Override
    public void loginFail(String errMsg) {
        showToast(errMsg);
    }

}
