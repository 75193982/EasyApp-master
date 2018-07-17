package com.liaoliao.chat.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liaoliao.R;
import com.liaoliao.chat.model.HserSection;
import com.liaoliao.chat.model.UserBao;
import com.liaoliao.chat.model.UserBaoTitle;
import com.liaoliao.chat.widget.MyRecyclerView;

import java.util.List;

/**
 * Created by lv on 2018/7/9 for EasyApp-master
 */
public class HomeAdapter extends BaseSectionQuickAdapter<HserSection, BaseViewHolder> {
    Context mContext;
    private final RecyclerView.RecycledViewPool viewPool;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data             A new list is created out of this one to avoid mutable list
     */
    public HomeAdapter(Context context, List<HserSection> data) {
        super(R.layout.adapter_home_item, R.layout.adapter_home_item_head, data);
        mContext = context;
        viewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    protected void convertHead(BaseViewHolder helper, HserSection item) {
        helper.setText(R.id.tv_title, item.header);
    }

    @Override
    protected void convert(BaseViewHolder helper, HserSection item) {
        UserBaoTitle userBaoTitle  = item.t;
        RecyclerView recyclerView = helper.getView(R.id.recyclerView);
        if(recyclerView.getAdapter() == null){
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(new HomeItemAdapter(userBaoTitle.list));
            recyclerView. setRecycledViewPool(viewPool);
        }






    }
}
