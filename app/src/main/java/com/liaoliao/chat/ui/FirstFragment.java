package com.liaoliao.chat.ui;/**
 * Created by dell on 2017/4/5/0005.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.liaoliao.R;
import com.liaoliao.chat.base.BaseFragment;

import me.yokeyword.fragmentation.SupportFragment;


/**
 * created by LiChengalin at 2017/4/5/0005
 */
public class FirstFragment extends  BaseFragment {

    public static SupportFragment newInstance(SupportFragment conversationList) {

        Bundle bundle = new Bundle();

        FirstFragment fragment = new FirstFragment();
        fragment.setArguments(bundle);
        return conversationList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        return view;
    }
}
