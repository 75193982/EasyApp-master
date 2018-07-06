package io.rong.imkit.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import io.rong.imkit.plugin.location.AMapLocationActivity;
import io.rong.imkit.plugin.location.AMapRealTimeActivity;
import io.rong.imkit.plugin.location.LocationManager;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.utilities.PermissionCheckUtil;

/**
 * Created by lv on 2018/7/6 for EasyApp-master
 */
public class CombineLocationPlugin implements IPluginModule, IPluginRequestPermissionResultCallback {
    public CombineLocationPlugin() {
    }

    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, io.rong.imkit.R.drawable.rc_ext_plugin_location_selector);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(io.rong.imkit.R.string.rc_plugin_location);
    }

    @Override
    public void onClick(Fragment currentFragment, RongExtension extension) {
        String[] permissions = new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_NETWORK_STATE"};
        if (PermissionCheckUtil.checkPermissions(currentFragment.getActivity(), permissions)) {
            this.sendOrShareLocation(currentFragment, extension);
        } else {
            extension.requestPermissionForPluginResult(permissions, 255, this);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    private void sendOrShareLocation(final Fragment currentFragment, final RongExtension extension) {
        String[] items = new String[]{currentFragment.getString(io.rong.imkit.R.string.rc_plugin_location_message), currentFragment.getString(io.rong.imkit.R.string.rc_plugin_location_sharing)};
        OptionsPopupDialog.newInstance(currentFragment.getActivity(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            @Override
            public void onOptionsItemClicked(int which) {
                if (which == 0) {
                    Intent intentx = new Intent(currentFragment.getActivity(), AMapLocationActivity.class);
                    extension.startActivityForPluginResult(intentx, 1, CombineLocationPlugin.this);
                } else if (which == 1) {
                    int result = LocationManager.getInstance().joinLocationSharing();
                    if (result == 0) {
                        Intent intent = new Intent(currentFragment.getActivity(), AMapRealTimeActivity.class);
                        currentFragment.getActivity().startActivity(intent);
                    } else if (result == 1) {
                        Toast.makeText(currentFragment.getActivity(), io.rong.imkit.R.string.rc_network_exception, Toast.LENGTH_SHORT).show();
                    } else if (result == 2) {
                        Toast.makeText(currentFragment.getActivity(), io.rong.imkit.R.string.rc_location_sharing_exceed_max, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }).show();
    }

    @Override
    public boolean onRequestPermissionResult(Fragment fragment, RongExtension extension, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PermissionCheckUtil.checkPermissions(fragment.getActivity(), permissions)) {
            this.sendOrShareLocation(fragment, extension);
        } else {
            extension.showRequestPermissionFailedAlter(PermissionCheckUtil.getNotGrantedPermissionMsg(fragment.getActivity(), permissions, grantResults));
        }

        return true;
    }
}
