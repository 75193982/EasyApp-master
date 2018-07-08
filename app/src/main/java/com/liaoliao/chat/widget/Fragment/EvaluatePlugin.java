package com.liaoliao.chat.widget.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.liaoliao.chat.widget.IPluginModule;
import com.liaoliao.chat.widget.RongExtension;

import io.rong.imkit.R;
import io.rong.imkit.widget.CSEvaluateDialog;

/**
 * Created by lv on 2018/7/6 for EasyApp-master
 */
public class EvaluatePlugin implements IPluginModule, CSEvaluateDialog.EvaluateClickListener {
    private CSEvaluateDialog mEvaluateDialog;
    private boolean mResolvedButton;

    public EvaluatePlugin(boolean mResolvedButton) {
        this.mResolvedButton = mResolvedButton;
    }

    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.rc_cs_evaluate_selector);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(R.string.rc_cs_evaluate);
    }




    @Override
    public void onClick(Fragment currentFragment, RongExtension extension) {
        this.mEvaluateDialog = new CSEvaluateDialog(currentFragment.getActivity(), extension.getTargetId());
        this.mEvaluateDialog.showStarMessage(this.mResolvedButton);
        this.mEvaluateDialog.setClickListener(this);
        extension.collapseExtension();
    }

    @Override
    public void onEvaluateSubmit() {
        this.mEvaluateDialog.destroy();
        this.mEvaluateDialog = null;
    }

    @Override
    public void onEvaluateCanceled() {
        this.mEvaluateDialog.destroy();
        this.mEvaluateDialog = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

}
