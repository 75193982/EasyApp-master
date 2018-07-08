package com.liaoliao.chat.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;


/**
 * Created by lv on 2018/7/6 for EasyApp-master
 */
public interface IPluginModule {
    Drawable obtainDrawable(Context var1);

    String obtainTitle(Context var1);

    void onClick(Fragment var1, RongExtension var2);

    void onActivityResult(int var1, int var2, Intent var3);
}
