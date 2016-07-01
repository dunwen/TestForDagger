package com.dundunwen.testfordagger;

import com.dundunwen.testfordagger.interfaces.LoginModel;

/**
 * Created by dun on 16/7/1.
 */
public class LoginModelImpl implements LoginModel{
    @Override
    public void doLogin(String userName, String pwd, loginListener listener) {
        listener.success();
    }
}
