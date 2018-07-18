package com.liaoliao.chat.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liaoliao.R;
import com.liaoliao.chat.model.HeaderClass;
import com.liaoliao.chat.model.UserBao;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by lv on 2018/7/17 for EasyApp-master
 */
public class HomeItemAdapter extends BaseQuickAdapter<UserBao, BaseViewHolder> {
        private Context mContext;

    public HomeItemAdapter(Context context,@Nullable List<UserBao> data) {
        super(R.layout.adapter_home_item_child, data);
         mContext = context ;
    }

    @Override
    protected void convert(BaseViewHolder helper, UserBao userBao) {
        ImageView tv_headImg = helper.getView(R.id.tv_headImg);
        Glide.with(mContext).load(userBao.headImg)  .bitmapTransform(new RoundedCornersTransformation(mContext,8,0, RoundedCornersTransformation.CornerType.ALL)).error(R.drawable.drawable_face_beauty)
                .into(tv_headImg);
        helper.setText(R.id.tv_name,userBao.name) ;
        helper.setText(R.id.tv_price,userBao.price) ;
        helper.setText(R.id.tv_uint,userBao.uint) ;
    }
}
