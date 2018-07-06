package com.liaoliao.message.module;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

import com.jrmf360.rylib.rp.extend.CurrentUser;
import com.jrmf360.rylib.rp.extend.JrmfRedPacketMessage;
import com.jrmf360.rylib.rp.ui.SendSingleEnvelopesActivity;


import io.rong.imkit.RongIM;

import io.rong.imkit.widget.IPluginModule;
import io.rong.imkit.widget.RongExtension;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by lv on 2018/7/6 for EasyApp-master
 */
public class RedSingleEnvelopePlugin implements IPluginModule {
    private String targetId;

    public RedSingleEnvelopePlugin() {
    }

    @Override
    public Drawable obtainDrawable(Context var1) {
        return var1.getResources().getDrawable(com.jrmf360.rylib.R.drawable.selector_hongbao);
    }

    @Override
    public String obtainTitle(Context var1) {
        return var1.getString(com.jrmf360.rylib.R.string._s_bribery);
    }

    @Override
    public void onClick(Fragment var1, RongExtension var2) {
        this.targetId = var2.getTargetId();
        Intent var3 = new Intent(var2.getContext(), SendSingleEnvelopesActivity.class);
        var3.putExtra("TargetId", var2.getTargetId());
        var3.putExtra("user_id", CurrentUser.getUserId());
        var3.putExtra("user_name", CurrentUser.getName());
        var3.putExtra("user_icon", CurrentUser.getUserIcon());
        var2.startActivityForPluginResult(var3, 52, this);
    }

    public void onActivityResult(int var1, int var2, Intent var3) {
        if (var2 == -1) {
            this.sendRpMessage(var3);
        }
    }

    private void sendRpMessage(Intent var1) {
        String var2 = var1.getStringExtra("envelopesID");
        String var3 = var1.getStringExtra("envelopeMessage");
        String var4 = var1.getStringExtra("envelopeName");
        JrmfRedPacketMessage var5 = JrmfRedPacketMessage.obtain(var2, var4, var3, "[" + var4 + "] " + var3);
        if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {
            RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.PRIVATE, this.targetId, var5, "您收到了一条消息", (String)null, new RongIMClient.SendMessageCallback() {
                @Override
                public void onError(Integer var1, RongIMClient.ErrorCode var2) {
                }

                @Override
                public void onSuccess(Integer var1) {
                }
            });
        }

    }
}
