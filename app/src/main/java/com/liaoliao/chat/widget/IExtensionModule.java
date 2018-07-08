package com.liaoliao.chat.widget;

import java.util.List;

import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * Created by lv on 2018/7/6 for EasyApp-master
 */
public interface IExtensionModule {

    void onInit(String var1);

    void onConnect(String var1);

    void onAttachedToExtension(RongExtension var1);

    void onDetachedFromExtension();

    void onReceivedMessage(Message var1);

    List<IPluginModule> getPluginModules(Conversation.ConversationType var1);

    List<IEmoticonTab> getEmoticonTabs();

    void onDisconnect();
}
