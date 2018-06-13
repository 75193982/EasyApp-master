package com.liaoliao.chat.activity;

import android.os.Bundle;

import com.liaoliao.R;
import com.liaoliao.chat.ui.MainFragment;


import me.yokeyword.fragmentation.SupportActivity;

public class MainActivity extends SupportActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            loadRootFragment(R.id.fragment_container, MainFragment.newInstance());
        }
    }

}
