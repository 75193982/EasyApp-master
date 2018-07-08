package com.liaoliao.chat.widget;

import android.content.Context;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.rong.common.RLog;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.utilities.RongUtils;
import io.rong.imlib.model.Message;

/**
 * Created by lv on 2018/7/6 for EasyApp-master
 */
public class RongExtensionManager {
    private static final String TAG = "RongExtensionManager";
    private static String mAppKey;
    private static List<IExtensionModule> mExtModules = new ArrayList();
    private static final String DEFAULT_REDPACKET = "com.liaoliao.message.module.JrmfExtensionModule";
    private static final String DEFAULT_BQMM = "com.melink.bqmmplugin.rc.BQMMExtensionModule";

    private RongExtensionManager() {
        if (mExtModules != null) {
            Class cls;
            Constructor constructor;
           IExtensionModule bqmm;
            try {
                cls = Class.forName("com.liaoliao.message.module.JrmfExtensionModule");
                constructor = cls.getConstructor();
                bqmm = (IExtensionModule)constructor.newInstance();
                RLog.i("RongExtensionManager", "add module " + bqmm.getClass().getSimpleName());
                mExtModules.add(bqmm);
                bqmm.onInit(mAppKey);
            } catch (Exception var5) {
                RLog.i("RongExtensionManager", "Can't find com.jrmf360.rylib.modules.JrmfExtensionModule");
            }

            try {
                cls = Class.forName("com.melink.bqmmplugin.rc.BQMMExtensionModule");
                constructor = cls.getConstructor();
                bqmm = (IExtensionModule)constructor.newInstance();
                RLog.i("RongExtensionManager", "add module " + bqmm.getClass().getSimpleName());
                mExtModules.add(bqmm);
                bqmm.onInit(mAppKey);
            } catch (Exception var4) {
                RLog.i("RongExtensionManager", "Can't find com.melink.bqmmplugin.rc.BQMMExtensionModule");
            }
        }

    }

    public static RongExtensionManager getInstance() {
        return RongExtensionManager.SingletonHolder.sInstance;
    }

    static void init(Context context, String appKey) {
        RLog.d("RongExtensionManager", "init");
        AndroidEmoji.init(context);
        RongUtils.init(context);
        mAppKey = appKey;
    }

    public void registerExtensionModule(IExtensionModule extensionModule) {
        if (mExtModules == null) {
            RLog.e("RongExtensionManager", "Not init in the main process.");
        } else if (extensionModule != null && !mExtModules.contains(extensionModule)) {
            RLog.i("RongExtensionManager", "registerExtensionModule " + extensionModule.getClass().getSimpleName());
            if (mExtModules.size() <= 0 || !((IExtensionModule)mExtModules.get(0)).getClass().getCanonicalName().equals("com.jrmf360.rylib.modules.JrmfExtensionModule") && !((IExtensionModule)mExtModules.get(0)).getClass().getCanonicalName().equals("com.melink.bqmmplugin.rc.BQMMExtensionModule")) {
                mExtModules.add(extensionModule);
            } else {
                mExtModules.add(0, extensionModule);
            }

            extensionModule.onInit(mAppKey);
        } else {
            RLog.e("RongExtensionManager", "Illegal extensionModule.");
        }
    }

    public void unregisterExtensionModule(IExtensionModule extensionModule) {
        if (mExtModules == null) {
            RLog.e("RongExtensionManager", "Not init in the main process.");
        } else if (extensionModule != null && mExtModules.contains(extensionModule)) {
            RLog.i("RongExtensionManager", "unregisterExtensionModule " + extensionModule.getClass().getSimpleName());
            mExtModules.remove(extensionModule);
        } else {
            RLog.e("RongExtensionManager", "Illegal extensionModule.");
        }
    }

    public List<IExtensionModule> getExtensionModules() {
        return mExtModules;
    }

    void connect(String token) {
        Iterator var2 = mExtModules.iterator();

        while(var2.hasNext()) {
          IExtensionModule extensionModule = (IExtensionModule)var2.next();
            extensionModule.onConnect(token);
        }

    }

    void disconnect() {
        if (mExtModules != null) {
            Iterator var1 = mExtModules.iterator();

            while(var1.hasNext()) {
               IExtensionModule extensionModule = (IExtensionModule)var1.next();
                extensionModule.onDisconnect();
            }

        }
    }

    void onReceivedMessage(Message message) {
        Iterator var2 = mExtModules.iterator();

        while(var2.hasNext()) {
           IExtensionModule extensionModule = (IExtensionModule)var2.next();
            extensionModule.onReceivedMessage(message);
        }

    }

    private static class SingletonHolder {
        static RongExtensionManager sInstance = new RongExtensionManager();

        private SingletonHolder() {
        }
    }
}
