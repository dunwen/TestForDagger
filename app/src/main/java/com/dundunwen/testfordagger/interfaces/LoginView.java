package com.dundunwen.testfordagger.interfaces;

/**
 * Created by dun on 16/7/1.
 */
public interface LoginView {
    void showError(String msg);
    void showProgressDialog(boolean isShow);
    void loginSuccess();
}
