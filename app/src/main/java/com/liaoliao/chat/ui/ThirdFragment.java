package com.liaoliao.chat.ui;/**
 * Created by dell on 2017/4/5/0005.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.liaoliao.R;
import com.liaoliao.chat.base.BaseFragment;


/**
 * created by LiChengalin at 2017/4/5/0005
 */
public class ThirdFragment extends BaseFragment {

    public static ThirdFragment newInstance() {

        Bundle args = new Bundle();

        ThirdFragment fragment = new ThirdFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_third;
    }

    @Override
    protected void setUpView() {

    }

    @Override
    protected void setUpData() {

    }
}
