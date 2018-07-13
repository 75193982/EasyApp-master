package com.liaoliao.chat.loader;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.liaoliao.R;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by lv on 2018/7/9 for EasyApp-master
 */
public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //具体方法内容自己去选择，次方法是为了减少banner过多的依赖第三方包，所以将这个权限开放给使用者去选择
        Glide.with(context.getApplicationContext())
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.ALL)// 缓存所有尺寸的图片
                .placeholder(R.drawable.ic_default_cancel_head)
                .into(imageView);
    }
}
