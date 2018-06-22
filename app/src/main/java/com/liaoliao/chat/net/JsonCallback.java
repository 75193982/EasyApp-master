package com.liaoliao.chat.net;

import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

import com.liaoliao.chat.application.MyApplication;
import com.liaoliao.chat.utils.Setting;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.request.base.Request;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/14
 * 描    述：默认将返回的数据解析成需要的Bean,可以是 BaseBean，String，List，Map
 * 修订历史：
 * -
 * -
 * -
 * -
 * -我的注释都已经写的不能再多了,不要再来问我怎么获取数据对象,怎么解析集合数据了,你只要会 gson ,就会解析
 * -
 * -如果你对这里的代码原理不清楚，可以看这里的详细原理说明：https://github.com/jeasonlzy/okhttp-OkGo/blob/master/README_JSONCALLBACK.md
 * -如果你对这里的代码原理不清楚，可以看这里的详细原理说明：https://github.com/jeasonlzy/okhttp-OkGo/blob/master/README_JSONCALLBACK.md
 * -如果你对这里的代码原理不清楚，可以看这里的详细原理说明：https://github.com/jeasonlzy/okhttp-OkGo/blob/master/README_JSONCALLBACK.md
 * -
 * -
 * -
 * ================================================
 */
public abstract class JsonCallback<T> extends AbsCallback<T> {

    private Type type;
    private Class<T> clazz;

    public JsonCallback() {
    }


    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);
        // 主要用于在所有请求之前添加公共的请求头或请求参数
        // 例如登录授权的 token
        // 使用的设备信息
        // 可以随意添加,也可以什么都不传
        // 还可以在这里对所有的参数进行加密，均在这里实现
        String token = new Setting(MyApplication.getContext()).loadString("token");
        request.headers(MyApplication.token, "Bearer " + token);//

    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Override
    public T convertResponse(Response response) throws Throwable {

        /**
         * 该方法是子线程处理，不能做ui相关的工作
         * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
         * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
         */
        try {
            JSONReader reader = new JSONReader(response.body().charStream());
            LzyResponse<T> myResponse = reader.readObject(LzyResponse.class);
            if (myResponse != null) {

                if (myResponse.code.equals("000")) {
                    return (T) myResponse;
                } else if (myResponse.code.equals("700")) {
                    return (T) myResponse;
                } else {
                    throw new IllegalStateException(myResponse.message);
                }
            } else {
                throw new IllegalStateException("网络连接异常，请稍后再试");
            }
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }

        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用

    }

    @Override
    public void onError(com.lzy.okgo.model.Response<T> response) {
        super.onError(response);
        //  MyUtil.showToast("网络连接失败，请检查网络是否连接!");

    }
}