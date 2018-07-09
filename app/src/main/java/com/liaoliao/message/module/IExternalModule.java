package com.liaoliao.message.module;

import android.content.Context;

import com.liaoliao.chat.widget.IPluginModule;

import java.util.List;



import io.rong.imlib.model.Conversation;

/**
 * Created by lv on 2018/7/6 for EasyApp-master
 */
public interface IExternalModule {
    void onCreate(Context var1);

    void onInitialized(String var1);

    void onConnected(String var1);

    void onViewCreated();

    List<IPluginModule> getPlugins(Conversation.ConversationType var1);

    void onDisconnected();
}
