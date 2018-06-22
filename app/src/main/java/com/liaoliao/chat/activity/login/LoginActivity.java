package com.liaoliao.chat.activity.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.liaoliao.R;
import com.liaoliao.chat.activity.MainActivity;
import com.liaoliao.chat.application.MyApplication;
import com.liaoliao.chat.base.BaseActivity;

import com.liaoliao.chat.model.MUsers;
import com.liaoliao.chat.model.UserAuth;
import com.liaoliao.chat.net.JsonCallback;
import com.liaoliao.chat.net.LzyResponse;
import com.liaoliao.chat.net.URL;
import com.liaoliao.chat.timchat.ui.SplashActivity;
import com.liaoliao.chat.utils.LoginInformation;
import com.liaoliao.chat.utils.Setting;
import com.liaoliao.chat.view.CustomVideoView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;
import com.tencent.qalsdk.util.BaseApplication;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by lv on 2018/6/13 for EasyApp-master
 */

public class LoginActivity extends BaseActivity implements PlatformActionListener {
    @BindView(R.id.videoview)
    CustomVideoView videoview;
    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.iv_wechat)
    ImageView ivWechat;
    @BindView(R.id.iv_qq)
    ImageView ivQq;
    private Platform qq;

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    public void init() {

    }

    @Override
    public void initView() {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sport));
        //获取第一帧
        Bitmap bitmap = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        imageView.setImageBitmap(bitmap);
        videoview = (CustomVideoView) findViewById(R.id.videoview);
        videoview.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sport));
        //播放
        videoview.start();

    }


    //第三方授权登录
    private void authorize(Platform plat) {
        if (plat == null) {
            return;
        }
        //判断指定平台是否已经完成授权
       if (plat.isAuthValid()) {
         //  qq.removeAccount(true);
           /* String token = plat.getDb().getToken();
            String userId = plat.getDb().getUserId();
            String name = plat.getDb().getUserName();
            String gender = plat.getDb().getUserGender();
            String headImageUrl = plat.getDb().getUserIcon();
            String platformNname = plat.getDb().getPlatformNname();
            if (userId != null) {
                //已经授权过，直接下一步操作
                if (platformNname.equals(SinaWeibo.NAME)) {
                    //微博授权
                } else if (platformNname.equals(QQ.NAME)) {
                    //qq授权
                } else if (platformNname.equals(Wechat.NAME)) {
                    //微信授权
                }
                return;
            }*/
        }
        // true不使用SSO授权，false使用SSO授权
        plat.SSOSetting(false);
        plat.setPlatformActionListener(this);
       // plat.authorize();
        //获取用户资料
        plat.showUser(null);
    }


    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            videoview.setBackgroundColor(Color.TRANSPARENT);
                            imageView.setVisibility(View.GONE);
                        }
                        return true;
                    }
                });
            }
        });


        //循环播放
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.start();
            }


        });
    }


    //返回重启加载
    @Override
    protected void onRestart() {
        super.onRestart();
        initView();

    }

    //防止锁屏或者切出的时候，音乐在播放
    @Override
    protected void onStop() {
        super.onStop();
        videoview.stopPlayback();
    }

    private PlatformDb platDB; //平台授权数据DB

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        String headImageUrl = null;//头像
        String userId="";//userId
        String token="";//token
        String gender="";//性别
        String name = null;//用户名
        if (i == Platform.ACTION_USER_INFOR) {
            platDB = platform.getDb();
            // 获取平台数据DB
            if (platform.getName().equals(Wechat.NAME)) {
                //微信登录 // 通过DB获取各种数据
                token = platDB.getToken();
                userId = platDB.getUserId();
                name = platDB.getUserName();
                gender = platDB.getUserGender();
                headImageUrl = platDB.getUserIcon();
                if ("m".equals(gender)) {
                    gender = "1";

                } else {
                    gender = "2";
                }


            } else if (platform.getName().equals(SinaWeibo.NAME)) {

                // 微博登录
                token = platDB.getToken();
                userId = platDB.getUserId();
                name = hashMap.get("nickname").toString();
                // 名字
                gender = hashMap.get("gender").toString();
                // 年龄
                headImageUrl = hashMap.get("figureurl_qq_2").toString();
                // 头像figureurl_qq_2 中等图，figureurl_qq_1缩略图
            } else if (platform.getName().equals(QQ.NAME)) {
                // QQ登录
                token = platDB.getToken();
                userId = platDB.getUserId();
                name = hashMap.get("nickname").toString();
                // 名字
                gender = hashMap.get("gender").toString();
                // 年龄
                headImageUrl = hashMap.get("figureurl_qq_2").toString();
                // 头像figureurl_qq_2 中等图，figureurl_qq_1缩略图
                qq.removeAccount(true);
                onLogin(headImageUrl,userId,token,gender,name,QQ.NAME);
            }
        }

            onLogin(token,name,gender,headImageUrl,userId,platform.getName());
    }



    private void onLogin(final String headImageUrl, final String userId, String token, final String gender, final String name, final String type) {
        OkGo.<LzyResponse<MUsers>>post(URL.getUrl(URL.USER_SIGNIN)) .tag(this).params("headImageUrl", headImageUrl)
                .params("token", token).params("userId", userId)
                .params("gender", gender).params("name", name).params("type", type)
                .execute(new JsonCallback<LzyResponse<MUsers>>() {
            @Override
            public void onSuccess(Response<LzyResponse<MUsers>> response) {
                String token = response.body().token;
                String randomKey = response.body().randomKey;
                String sign = response.body().sign;
                new Setting(LoginActivity.this).saveString("token", token);
                new Setting(LoginActivity.this).saveString("randomKey", randomKey);
                new Setting(LoginActivity.this).saveString("sign", sign);
                Setting setting = new Setting(getApplicationContext());
                UserAuth userAuth = new UserAuth();
                userAuth.setGender(gender);
                userAuth.setHeadImageUrl(headImageUrl);
                userAuth.setName(name);
                userAuth.setToken(token);
                userAuth.setType(type);
                userAuth.setUserId(userId);
                setting.saveString("user", new Gson().toJson(userAuth));
                LoginInformation.getInstance().setUser(userAuth);
                HttpHeaders headerstemp = new HttpHeaders();
                headerstemp.put(MyApplication.token, "Bearer " + token);
                OkGo.getInstance().addCommonHeaders(headerstemp);
                Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onError(Response<LzyResponse<MUsers>> response) {
                super.onError(response);
                ToastUtils.showShort(response.getException().getMessage());
            }
        });



    }


    @Override
    public void onError(Platform platform, int i, Throwable throwable) {

    }

    @Override
    public void onCancel(Platform platform, int i) {

    }



    @OnClick({R.id.iv_wechat, R.id.iv_qq})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wechat:
                // 微信登录
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                authorize(wechat);

               /* Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
                authorize(weibo);*/
                break;
            case R.id.iv_qq:
                // qq登录
                 qq = ShareSDK.getPlatform(QQ.NAME);
                qq.removeAccount(true);
                authorize(qq);
                break;
        }
    }
}
