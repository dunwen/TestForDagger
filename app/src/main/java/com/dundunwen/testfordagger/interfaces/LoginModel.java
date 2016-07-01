package com.dundunwen.testfordagger.interfaces;

/**
 * Created by dun on 16/7/1.
 */
public interface LoginModel {
    void doLogin(String userName,String pwd,loginListener listener);
    interface loginListener{
        void success();
        void failure();
    }
}
