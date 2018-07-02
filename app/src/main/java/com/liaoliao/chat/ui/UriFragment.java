package com.liaoliao.chat.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import io.rong.imkit.fragment.BaseFragment;

/**
 * Created by lv on 2018/7/2 for EasyApp-master
 */
public abstract class UriFragment extends com.liaoliao.chat.ui.BaseFragment {
    public static final String RONG_URI = "RONG_URI";
    private boolean mViewCreated;
    private Uri mUri;
    private UriFragment.IActionBarHandler mBarHandler;

    public UriFragment() {
    }

    protected Bundle obtainUriBundle(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable("RONG_URI", uri);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.mUri == null) {
            if (savedInstanceState == null) {
                this.mUri = this.getActivity().getIntent().getData();
            } else {
                this.mUri = (Uri)savedInstanceState.getParcelable("RONG_URI");
            }
        }

        this.mViewCreated = true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.getUri() != null) {
            this.initFragment(this.getUri());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("RONG_URI", this.getUri());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreUI() {
        if (this.getUri() != null) {
            this.initFragment(this.getUri());
        }

    }

    public void setActionBarHandler(UriFragment.IActionBarHandler mBarHandler) {
        this.mBarHandler = mBarHandler;
    }

    protected UriFragment.IActionBarHandler getActionBarHandler() {
        return this.mBarHandler;
    }

    public Uri getUri() {
        return this.mUri;
    }

    public void setUri(Uri uri) {
        this.mUri = uri;
        if (this.mViewCreated) {
            this.initFragment(uri);
        }

    }

    protected abstract void initFragment(Uri var1);

    @Override
    public boolean onBackPressed() {
        return false;
    }

    protected interface IActionBarHandler {
        void onTitleChanged(CharSequence var1);

        void onUnreadCountChanged(int var1);
    }
}
