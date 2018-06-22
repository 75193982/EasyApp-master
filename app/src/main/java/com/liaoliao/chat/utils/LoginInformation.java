package com.liaoliao.chat.utils;

import com.google.gson.Gson;
import com.liaoliao.chat.application.MyApplication;
import com.liaoliao.chat.model.UserAuth;


public class LoginInformation {

    private UserAuth userAuth;
    private static LoginInformation instance;


    private LoginInformation() {
    }

    public static LoginInformation getInstance() {
        if (instance == null) {
            synchronized (LoginInformation.class) {
                if (instance == null) {
                    instance = new LoginInformation();
                }
            }
        }

        return instance;
    }


    public static void setInstance(LoginInformation instance) {
        LoginInformation.instance = instance;
    }

    public UserAuth getUser() {
        if (userAuth == null) {
            // 进入主页面
            String userString = new Setting(MyApplication.getContext()).loadString("user");
            userAuth = new UserAuth();
            try {
                userAuth = new Gson().fromJson(userString, UserAuth.class);
            } catch (Exception e) {
            }
        }
        return userAuth;
    }

    public void setUser(UserAuth user) {
        this.userAuth = user;
    }
}
