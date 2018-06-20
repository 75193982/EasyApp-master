package com.liaoliao.chat.utils;

import com.blankj.utilcode.util.EncodeUtils;

/**
 * 对数据进行base64编码的方式
 *
 * @author fengshuonan
 * @date 2017-09-18 20:43
 */
public class Base64Security implements DataSecurity {

    @Override
    public String doAction(String beProtected) {

        return EncodeUtils.base64Encode2String(beProtected.getBytes());
    }

    @Override
    public String unlock(String securityCode) {
        byte[] bytes = EncodeUtils.base64Decode(securityCode);
        return new String(bytes);
    }
}
