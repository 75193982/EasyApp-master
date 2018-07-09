package com.liaoliao.message.module;

import android.content.Context;
import android.content.res.Resources;
import android.view.KeyEvent;
import android.widget.EditText;

import com.liaoliao.chat.widget.CombineLocationPlugin;
import com.liaoliao.chat.widget.DefaultLocationPlugin;
import com.liaoliao.chat.widget.IExtensionModule;
import com.liaoliao.chat.widget.IPluginModule;
import com.liaoliao.chat.widget.ImagePlugin;
import com.liaoliao.chat.widget.RongExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import io.rong.common.RLog;


import io.rong.imkit.emoticon.EmojiTab;
import io.rong.imkit.emoticon.IEmojiItemClickListener;
import io.rong.imkit.emoticon.IEmoticonTab;






import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * Created by lv on 2018/7/6 for EasyApp-master
 */
public class DefaultExtensionModule implements IExtensionModule {
    private static final String TAG = io.rong.imkit.DefaultExtensionModule.class.getSimpleName();
    private EditText mEditText;
    private Stack<EditText> stack;
    String[] types = null;

    public DefaultExtensionModule() {
    }

    @Override
    public void onInit(String appKey) {
        this.stack = new Stack();
    }

    @Override
    public void onConnect(String token) {
    }

    @Override
    public void onAttachedToExtension(RongExtension extension) {
        this.mEditText = extension.getInputEditText();
        Context context = extension.getContext();
        RLog.i(TAG, "attach " + this.stack.size());
        this.stack.push(this.mEditText);
        Resources resources = context.getResources();

        try {
            this.types = resources.getStringArray(resources.getIdentifier("rc_realtime_support_conversation_types", "array", context.getPackageName()));
        } catch (Resources.NotFoundException var5) {
            RLog.i(TAG, "not config rc_realtime_support_conversation_types in rc_config.xml");
        }

    }

    @Override
    public void onDetachedFromExtension() {
        RLog.i(TAG, "detach " + this.stack.size());
        if (this.stack.size() > 0) {
            this.stack.pop();
            this.mEditText = this.stack.size() > 0 ? (EditText)this.stack.peek() : null;
        }

    }

    @Override
    public void onReceivedMessage(Message message) {
    }

    @Override
    public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {
        List<IPluginModule> pluginModuleList = new ArrayList();
        IPluginModule image = new ImagePlugin();
        IPluginModule file = new FilePlugin();
        pluginModuleList.add(image);

        try {
            String clsName = "com.amap.api.netlocation.AMapNetworkLocationClient";
            Class<?> locationCls = Class.forName(clsName);
            if (locationCls != null) {
                IPluginModule combineLocation = new CombineLocationPlugin();
                IPluginModule locationPlugin = new DefaultLocationPlugin();
                boolean typesDefined = false;
                if (this.types != null && this.types.length > 0) {
                    String[] var10 = this.types;
                    int var11 = var10.length;

                    for(int var12 = 0; var12 < var11; ++var12) {
                        String type = var10[var12];
                        if (conversationType.getName().equals(type)) {
                            typesDefined = true;
                            break;
                        }
                    }
                }

                if (typesDefined) {
                    pluginModuleList.add(combineLocation);
                } else if (this.types == null && conversationType.equals(Conversation.ConversationType.PRIVATE)) {
                    pluginModuleList.add(combineLocation);
                } else {
                    pluginModuleList.add(locationPlugin);
                }
            }
        } catch (Exception var14) {
            RLog.i(TAG, "Not include AMap");
            var14.printStackTrace();
        }

        if (conversationType.equals(Conversation.ConversationType.GROUP) || conversationType.equals(Conversation.ConversationType.DISCUSSION) || conversationType.equals(Conversation.ConversationType.PRIVATE)) {
            pluginModuleList.addAll(InternalModuleManager.getInstance().getExternalPlugins(conversationType));
        }

        pluginModuleList.add(file);
        return pluginModuleList;
    }

    @Override
    public List<IEmoticonTab> getEmoticonTabs() {
        EmojiTab emojiTab = new EmojiTab();
        emojiTab.setOnItemClickListener(new IEmojiItemClickListener() {
            @Override
            public void onEmojiClick(String emoji) {
                int start = DefaultExtensionModule.this.mEditText.getSelectionStart();
               DefaultExtensionModule.this.mEditText.getText().insert(start, emoji);
            }

            @Override
            public void onDeleteClick() {
               DefaultExtensionModule.this.mEditText.dispatchKeyEvent(new KeyEvent(0, 67));
            }
        });
        List<IEmoticonTab> list = new ArrayList();
        list.add(emojiTab);
        return list;
    }

    @Override
    public void onDisconnect() {
    }
}
