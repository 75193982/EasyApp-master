package com.liaoliao.chat.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import io.rong.imkit.plugin.image.PictureSelectorActivity;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imlib.model.Conversation;

/**
 * Created by lv on 2018/7/6 for EasyApp-master
 */
public class ImagePlugin implements IPluginModule, IPluginRequestPermissionResultCallback {
    Conversation.ConversationType conversationType;
    String targetId;

    public ImagePlugin() {
    }

    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, io.rong.imkit.R.drawable.rc_ext_plugin_image_selector);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(io.rong.imkit.R.string.rc_plugin_image);
    }

    @Override
    public void onClick(Fragment currentFragment, RongExtension extension) {
        this.conversationType = extension.getConversationType();
        this.targetId = extension.getTargetId();
        String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.CAMERA"};
        if (PermissionCheckUtil.checkPermissions(currentFragment.getContext(), permissions)) {
            Intent intent = new Intent(currentFragment.getActivity(), PictureSelectorActivity.class);
            extension.startActivityForPluginResult(intent, 23, this);
        } else {
            extension.requestPermissionForPluginResult(permissions, 255, this);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public boolean onRequestPermissionResult(Fragment fragment, RongExtension extension, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PermissionCheckUtil.checkPermissions(fragment.getActivity(), permissions)) {
            Intent intent = new Intent(fragment.getActivity(), PictureSelectorActivity.class);
            extension.startActivityForPluginResult(intent, 23, this);
        } else {
            extension.showRequestPermissionFailedAlter(PermissionCheckUtil.getNotGrantedPermissionMsg(fragment.getActivity(), permissions, grantResults));
        }

        return true;
    }
}
