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
public class SecondFragment extends BaseFragment {

    public static SecondFragment newInstance() {

        Bundle bundle = new Bundle();

        SecondFragment fragment = new SecondFragment();
        fragment.setArguments(bundle);
        return fragment;
    }



    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_second;
    }

    @Override
    protected void setUpView() {

    }

    @Override
    protected void setUpData() {

    }
}
