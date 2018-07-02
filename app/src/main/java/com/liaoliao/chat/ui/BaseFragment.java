package com.liaoliao.chat.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.rong.imkit.RongContext;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by lv on 2018/7/2 for EasyApp-master
 */
public abstract class BaseFragment  extends SupportFragment implements Handler.Callback {
    private static final String TAG = "BaseFragment";
    public static final String TOKEN = "RONG_TOKEN";
    public static final int UI_RESTORE = 1;
    private Handler mHandler;
    private String mToken;

    public BaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mHandler = new Handler(this);
        if (savedInstanceState != null) {
            this.mToken = savedInstanceState.getString("RONG_TOKEN");
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected <T extends View> T findViewById(View view, int id) {
        return view.findViewById(id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("RONG_TOKEN", RongContext.getInstance().getToken());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected Handler getHandler() {
        return this.mHandler;
    }

    public abstract boolean onBackPressed();

    public abstract void onRestoreUI();

    private View obtainView(LayoutInflater inflater, int color, Drawable drawable, CharSequence notice) {
        View view = inflater.inflate(io.rong.imkit.R.layout.rc_wi_notice, (ViewGroup)null);
        ((TextView)view.findViewById(16908299)).setText(notice);
        ((ImageView)view.findViewById(16908294)).setImageDrawable(drawable);
        if (color > 0) {
            view.setBackgroundColor(color);
        }

        return view;
    }

    private View obtainView(LayoutInflater inflater, int color, int res, CharSequence notice) {
        View view = inflater.inflate(io.rong.imkit.R.layout.rc_wi_notice, (ViewGroup)null);
        ((TextView)view.findViewById(16908299)).setText(notice);
        ((ImageView)view.findViewById(16908294)).setImageResource(res);
        view.setBackgroundColor(color);
        return view;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.what) {
            case 1:
                this.onRestoreUI();
            default:
                return true;
        }
    }
}
