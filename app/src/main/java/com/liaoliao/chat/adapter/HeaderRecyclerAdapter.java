package com.liaoliao.chat.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liaoliao.R;
import com.liaoliao.chat.model.HeaderClass;

import java.util.List;

/**
 * Created by lv on 2018/7/12 for EasyApp-master
 */
public class HeaderRecyclerAdapter extends BaseQuickAdapter<HeaderClass, BaseViewHolder> {

    public HeaderRecyclerAdapter(List<HeaderClass> data) {
        super(R.layout.gridlayout_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HeaderClass item) {
        helper.setText(R.id.tv_name, item.getName());
        ImageView logo = helper.getView(R.id.iv_icon);
        String icon = item.getIcon();
        if(null != icon && ( icon.endsWith(".gif") || icon.endsWith(".GIF")) ){
            Glide.with(mContext).load(item.getIcon()) .asGif().error(R.drawable.drawable_face_beauty)
                   .into(logo)  ;
        }else{
            Glide.with(mContext).load(item.getIcon()).error(R.drawable.drawable_face_beauty)
                    .into(logo);
        }


    }
}
