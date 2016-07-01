package com.dundunwen.testfordagger;

import android.content.Context;

import com.dundunwen.testfordagger.interfaces.LoginModel;
import com.dundunwen.testfordagger.interfaces.LoginPresenter;
import com.dundunwen.testfordagger.interfaces.LoginView;

import javax.inject.Inject;

/**
 * Created by dun on 16/7/1.
 */
public class TestLoginPresenterImpl implements LoginPresenter{
    LoginView mView;
    LoginModel mModel;

    @Inject
    public TestLoginPresenterImpl(LoginView mView) {
        this.mView = mView;
        mModel = new LoginModelImpl();
    }

    @Override
    public void doLogin(String userName, String pwd) {
        mModel.doLogin(userName, pwd, new LoginModel.loginListener() {
            @Override
            public void success() {
                mView.loginSuccess();
            }

            @Override
            public void failure() {
                mView.showError("login failure");
            }
        });
    }
}
