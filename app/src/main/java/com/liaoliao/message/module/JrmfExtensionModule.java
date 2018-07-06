package com.liaoliao.message.module;

import com.jrmf360.rylib.JrmfClient;
import com.jrmf360.rylib.common.util.LogUtil;
import com.jrmf360.rylib.common.util.p;
import com.jrmf360.rylib.common.util.r;
import com.jrmf360.rylib.rp.extend.CurrentUser;
import com.jrmf360.rylib.rp.extend.JrmfRedPacketMessage;
import com.jrmf360.rylib.rp.extend.JrmfRedPacketMessageProvider;
import com.jrmf360.rylib.rp.extend.JrmfRedPacketOpenMessageProvider;
import com.jrmf360.rylib.rp.extend.JrmfRedPacketOpenedMessage;


import com.jrmf360.rylib.rp.http.a;
import com.jrmf360.rylib.rp.ui.BaseActivity;
import com.jrmf360.rylib.wallet.JrmfWalletClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import io.rong.eventbus.EventBus;

import io.rong.imkit.RongContext;

import io.rong.imkit.RongIM;
import io.rong.imkit.emoticon.IEmoticonTab;

import io.rong.imkit.widget.IExtensionModule;
import io.rong.imkit.widget.IPluginModule;
import io.rong.imkit.widget.RongExtension;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

/**
 * Created by lv on 2018/7/6 for EasyApp-master
 */
public class JrmfExtensionModule   implements IExtensionModule {
    private String lastName;
    private String lastAvatar;
    private String targetId;

    public JrmfExtensionModule() {
    }

    @Override
    public void onInit(String var1) {
        RongIM.registerMessageType(JrmfRedPacketMessage.class);
        RongIM.registerMessageType(JrmfRedPacketOpenedMessage.class);
        RongIM.registerMessageTemplate(new JrmfRedPacketMessageProvider());
        RongIM.registerMessageTemplate(new JrmfRedPacketOpenMessageProvider());
        a.a(RongContext.getInstance().getApplicationContext());
        p.a().a(RongContext.getInstance().getApplicationContext(), "appkey", var1);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onConnect(String var1) {
        LogUtil.e("onConnect:" + var1);
        JrmfWalletClient.getInstance().init(RongContext.getInstance().getApplicationContext());
        if (RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
            JrmfWalletClient.getInstance().getVendorToken();
        } else {
            LogUtil.e("onConnect:网络连接失败");
            BaseActivity.rongCloudToken = "";
        }

    }

    @Override
    public void onAttachedToExtension(RongExtension var1) {
        this.targetId = var1.getTargetId();
    }

    @Override
    public void onDetachedFromExtension() {
    }

    @Override
    public void onReceivedMessage(Message var1) {
    }

    @Override
    public List<IPluginModule> getPluginModules(Conversation.ConversationType var1) {
        if (var1 == null) {
            return null;
        } else {
            ArrayList var2 = new ArrayList();
            if (var1.equals(Conversation.ConversationType.GROUP)) {
                RedGroupEnvelopePlugin var3 = new RedGroupEnvelopePlugin();
                var2.add(var3);
            } else if (var1.equals(Conversation.ConversationType.PRIVATE) && r.c(CurrentUser.getUserId()) && !CurrentUser.getUserId().equals(this.targetId)) {
                RedSingleEnvelopePlugin var4 = new RedSingleEnvelopePlugin();
                var2.add(var4);
            }

            return var2;
        }
    }

    @Override
    public List<IEmoticonTab> getEmoticonTabs() {
        return null;
    }

    public void onEventMainThread(final UserInfo var1) {
        if (var1.getUserId().equals(CurrentUser.getUserId()) && (!var1.getName().equals(this.lastName) || !var1.getPortraitUri().toString().equals(this.lastAvatar))) {
            LogUtil.i("userInfo", var1.getUserId() + "::" + var1.getName() + "::" + var1.getPortraitUri());
            Executors.newCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    String var1x = JrmfClient.updateUserInfo(CurrentUser.getUserId(), var1.getName(), var1.getPortraitUri().toString());
                    if (r.b(var1x)) {
                      JrmfExtensionModule.this.lastName = var1.getName();
                       JrmfExtensionModule.this.lastAvatar = var1.getPortraitUri().toString();
                    }

                }
            });
        }

    }

    @Override
    public void onDisconnect() {
    }
}
