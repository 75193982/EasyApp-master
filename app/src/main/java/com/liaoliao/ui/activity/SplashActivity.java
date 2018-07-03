package com.liaoliao.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;


import com.liaoliao.App;
import com.liaoliao.R;
import com.liaoliao.SealAppContext;

import com.liaoliao.chat.utils.Setting;
import com.liaoliao.server.widget.LoadDialog;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by AMing on 16/8/5.
 * Company RongCloud
 */
public class SplashActivity extends Activity {

    private Context context;
    private android.os.Handler handler = new android.os.Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        context = this;
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        //String cacheToken = sp.getString("loginToken", "");
        final String sign = new Setting(App.getContext()).loadString("sign");



        if (!TextUtils.isEmpty(sign)) {
            RongIM.connect(sign, SealAppContext.getInstance().getConnectCallback());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    RongIM.connect(sign, new RongIMClient.ConnectCallback() {
                        @Override
                        public void onTokenIncorrect() {

                        }

                        @Override
                        public void onSuccess(String s) {

                            goToMain();
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {

                        }
                    });

                }
            }, 800);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToLogin();
                }
            }, 800);
        }
    }


    private void goToMain() {
        startActivity(new Intent(context, MainActivity.class));
        finish();
    }

    private void goToLogin() {
        startActivity(new Intent(context, LoginActivity.class));
        finish();
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

}
