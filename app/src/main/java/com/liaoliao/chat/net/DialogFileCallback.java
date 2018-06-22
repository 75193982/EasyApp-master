package com.liaoliao.chat.net;

import android.app.ProgressDialog;
import android.content.Context;

import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.base.Request;

import java.io.File;


/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/14
 * 描    述：对于网络请求是否需要弹出进度对话框
 * 修订历史：
 * ================================================
 */
public abstract class DialogFileCallback extends FileCallback {


    private ProgressDialog mProgressDialog;

    private void initDialog(Context activity) {
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMessage("正在下载文件...");
    }

    public DialogFileCallback(Context activity) {
        super();
        initDialog(activity);
    }

    @Override
    public void onStart(Request<File, ? extends Request> request) {
        super.onStart(request);
        try {
            if (mProgressDialog != null && !mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Override
    public void downloadProgress(Progress progress) {
        super.downloadProgress(progress);
        mProgressDialog.setProgress((int) (progress.fraction * 10000));
    }

    @Override
    public void onFinish() {
        //网络请求结束后关闭对话框
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

    }
}
