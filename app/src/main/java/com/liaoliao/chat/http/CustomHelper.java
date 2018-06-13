package com.liaoliao.chat.http;

import android.net.Uri;
import android.os.Environment;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TakePhotoOptions;

import java.io.File;


/**
 * - 支持通过相机拍照获取图片
 * - 支持从相册选择图片
 * - 支持从文件选择图片
 * - 支持多图选择
 * - 支持批量图片裁切
 * - 支持批量图片压缩
 * - 支持对图片进行压缩
 * - 支持对图片进行裁剪
 * - 支持对裁剪及压缩参数自定义
 * - 提供自带裁剪工具(可选)
 * - 支持智能选取及裁剪异常处理
 * - 支持因拍照Activity被回收后的自动恢复
 * Author: crazycodeboy
 * Date: 2016/9/21 0007 20:10
 * Version:4.0.0
 * 技术博文：http://www.cboy.me
 * GitHub:https://github.com/crazycodeboy
 * Eamil:crazycodeboy@gmail.com
 */
public class CustomHelper {

    public static CustomHelper of() {
        return new CustomHelper();
    }

    private CustomHelper() {

    }


    public void onClick(int witch, TakePhoto takePhoto) {
        File file = new File(Environment.getExternalStorageDirectory(), "/lialiao /temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        configCompress(takePhoto, witch);
        configTakePhotoOption(takePhoto);
        switch (witch) {
            case 2:
                int limit = 6;
                takePhoto.onPickMultiple(limit);
                break;
            case 1:
                takePhoto.onPickFromGallery();
                break;
            case 0:
                takePhoto.onPickFromCapture(imageUri);
                break;
            default:
                break;
        }
    }

    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setWithOwnGallery(true);
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());

    }

    private void configCompress(TakePhoto takePhoto, int witch) {
        int maxSize = 102400;
        int width = 800;
        int height = 800;
        boolean showProgressBar = true;
        boolean enableRawFile = true;
        CompressConfig config;
        config = new CompressConfig.Builder()
                .setMaxSize(maxSize)
                .setMaxPixel(width >= height ? width : height)
                .enableReserveRaw(enableRawFile)
                .create();//        } else {
//            LubanOptions option = new LubanOptions.Builder()
//                    .setMaxHeight(height)
//                    .setMaxWidth(width)
//                    .setMaxSize(maxSize)
//                    .create();
//            config = CompressConfig.ofLuban(option);
//            config.enableReserveRaw(enableRawFile);
//        }
        takePhoto.onEnableCompress(config, showProgressBar);

        //


    }

    private CropOptions getCropOptions() {
        int height = 800;
        int width = 800;
        boolean withWonCrop = false;
        CropOptions.Builder builder = new CropOptions.Builder();
        // if (rgCropSize.getCheckedRadioButtonId() == R.id.rbAspect) {
        builder.setOutputX(width).setOutputX(height);
        builder.setWithOwnCrop(withWonCrop);
        return builder.create();
    }

}
