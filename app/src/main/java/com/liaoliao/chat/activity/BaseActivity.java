package com.liaoliao.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.liaoliao.R;

import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by lv on 2018/7/11 for EasyApp-master
 */
public class BaseActivity extends SupportActivity {
    public Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.layout_base);
        mActivity =this;
       /* StatusBarUtil.setTranslucentForImageViewInFragment(UseInFragmentActivity.this, null);*/
        com.jaeger.library.StatusBarUtil.setTranslucentForImageViewInFragment(mActivity,null);
    }


    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(view);
    }
}
