package com.liaoliao.chat.widget;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;


/**
 * Created by lv on 2018/7/6 for EasyApp-master
 */
public interface IPluginRequestPermissionResultCallback {


    int REQUEST_CODE_PERMISSION_PLUGIN = 255;

    boolean onRequestPermissionResult(Fragment var1, RongExtension var2, int var3, @NonNull String[] var4, @NonNull int[] var5);

}
