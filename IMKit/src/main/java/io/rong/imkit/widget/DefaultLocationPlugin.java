package io.rong.imkit.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import io.rong.imkit.plugin.location.AMapLocationActivity;
import io.rong.imkit.utilities.PermissionCheckUtil;

/**
 * Created by lv on 2018/7/6 for EasyApp-master
 */
public class DefaultLocationPlugin implements IPluginModule, IPluginRequestPermissionResultCallback {
    public DefaultLocationPlugin() {
    }

    @Override
    public Drawable obtainDrawable(Context context) {
        return context.getResources().getDrawable(io.rong.imkit.R.drawable.rc_ext_plugin_location_selector);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(io.rong.imkit.R.string.rc_plugin_location);
    }

    @Override
    public void onClick(Fragment currentFragment, RongExtension extension) {
        String[] permissions = new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_NETWORK_STATE"};
        if (PermissionCheckUtil.checkPermissions(currentFragment.getActivity(), permissions)) {
            this.startLocationActivity(currentFragment, extension);
        } else {
            extension.requestPermissionForPluginResult(permissions, 255, this);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    private void startLocationActivity(Fragment fragment, RongExtension extension) {
        Intent intent = new Intent(fragment.getActivity(), AMapLocationActivity.class);
        extension.startActivityForPluginResult(intent, 1, this);
    }

    @Override
    public boolean onRequestPermissionResult(Fragment fragment, RongExtension extension, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PermissionCheckUtil.checkPermissions(fragment.getActivity(), permissions)) {
            this.startLocationActivity(fragment, extension);
        } else {
            extension.showRequestPermissionFailedAlter(PermissionCheckUtil.getNotGrantedPermissionMsg(fragment.getActivity(), permissions, grantResults));
        }

        return true;
    }
}
