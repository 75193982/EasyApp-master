package cn.rongcloud.contactcard;

import android.support.v4.app.Fragment;


import io.rong.imkit.widget.IPluginModule;
import io.rong.imkit.widget.RongExtension;


/**
 * 用于自定义联系人列表界面(取代 ContactListActivity)，需要实现的接口
 * Created by Beyond on 2017/5/11.
 */

public interface IContactCardSelectListProvider {
    void onContactPluginClick(int requestCode, Fragment currentFragment,
                              RongExtension extension, IPluginModule pluginModule);
}
