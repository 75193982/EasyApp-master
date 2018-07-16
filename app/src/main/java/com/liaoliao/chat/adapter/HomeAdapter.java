package com.liaoliao.chat.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liaoliao.R;
import com.liaoliao.chat.model.HserSection;
import com.liaoliao.chat.model.UserBao;

import java.util.List;

/**
 * Created by lv on 2018/7/9 for EasyApp-master
 */
public class HomeAdapter extends BaseSectionQuickAdapter<HserSection, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data             A new list is created out of this one to avoid mutable list
     */
    public HomeAdapter( List<HserSection> data) {
        super(R.layout.adapter_home_item, R.layout.adapter_home_item_head, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, HserSection item) {
        helper.setText(R.id.tv_title, item.header);
    }

    @Override
    protected void convert(BaseViewHolder helper, HserSection item) {
        UserBao userBao  = item.t;
        ImageView tv_headImg = helper.getView(R.id.tv_headImg);
        Glide.with(mContext).load(userBao.headImg).error(R.drawable.drawable_face_beauty)
                .into(tv_headImg);
        helper.setText(R.id.tv_name,userBao.name) ;
        helper.setText(R.id.tv_price,userBao.price) ;
        helper.setText(R.id.tv_uint,userBao.uint) ;

    }
}
