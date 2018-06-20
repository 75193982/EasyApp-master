package com.liaoliao.chat.net;


import com.alibaba.fastjson.JSON;
import com.liaoliao.chat.application.MyApplication;
import com.liaoliao.chat.utils.Base64Security;
import com.liaoliao.chat.utils.BaseTransferEntity;
import com.liaoliao.chat.utils.MD5Util;
import com.liaoliao.chat.utils.Setting;
import com.tencent.qalsdk.util.BaseApplication;

/**
 * URL路径处理类
 *
 * @author Ht
 */
public class URL {

    public static String IP = "http://192.168.1.102:80/" ;
    public static String getUrl(String url){
        return IP + url;
    }

    public static final String USER_SIGNIN = "auth";

    public static String getRequstJsonString(Object obj) {
        String json = JSON.toJSONString(obj);
        String encode = new Base64Security().doAction(json);
        // md5签名
        String encrypt = MD5Util.encrypt(encode + new Setting(MyApplication.getContext()).loadString("randomKey"));
        BaseTransferEntity baseTransferEntity = new BaseTransferEntity();
        baseTransferEntity.setSign(encrypt);
        baseTransferEntity.setObject(encode);
        return JSON.toJSONString(baseTransferEntity);
    }
}
