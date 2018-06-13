package com.liaoliao.chat.activity.login;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.liaoliao.R;
import com.liaoliao.chat.base.BaseActivity;
import com.liaoliao.chat.view.CustomVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lv on 2018/6/13 for EasyApp-master
 */

public class LoginActivity extends BaseActivity {
    @BindView(R.id.videoview)
    CustomVideoView videoview;
    @BindView(R.id.image_view)
    ImageView imageView;

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
        media.setDataSource(this, Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.sport));
        //获取第一帧
        Bitmap bitmap  = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC );
        imageView.setImageBitmap(bitmap);
        videoview = (CustomVideoView) findViewById(R.id.videoview);
        videoview.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.sport));
        //播放
        videoview.start();

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
}
