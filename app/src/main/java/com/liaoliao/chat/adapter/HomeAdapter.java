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
        final RecyclerView recyclerView = helper.getView(R.id.recyclerView);
        final   LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(layoutManager);
            HomeItemAdapter homeItemAdapter = new HomeItemAdapter(mContext,userBaoTitle.list);
            recyclerView.setAdapter(homeItemAdapter);
            recyclerView. setRecycledViewPool(viewPool);
            if (item.scrollOffset > 0) {
                layoutManager.scrollToPositionWithOffset(item.scrollPosition, item.scrollOffset);
            }
            recyclerView.addOnScrollListener(new MyOnScrollListener(item, layoutManager));



    }



    private class MyOnScrollListener extends RecyclerView.OnScrollListener {
        private LinearLayoutManager mLayoutManager;
        private HserSection mEntity;
        private int mItemWidth;
        private int mItemMargin;
        public MyOnScrollListener(HserSection shopItem, LinearLayoutManager layoutManager) {
            mLayoutManager = layoutManager;
            mEntity = shopItem;
        }
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
        @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    int offset = recyclerView.computeHorizontalScrollOffset();
                    mEntity.scrollPosition = mLayoutManager.findFirstVisibleItemPosition() < 0 ? mEntity.scrollPosition : mLayoutManager.findFirstVisibleItemPosition() + 1;
                    if (mItemWidth <= 0) {
                        View item = mLayoutManager.findViewByPosition(mEntity.scrollPosition);
                        if (item != null) {
                            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) item.getLayoutParams(); mItemWidth = item.getWidth(); mItemMargin = layoutParams.rightMargin;
                        }
                    } if (offset > 0 && mItemWidth > 0) {
                        //offset % mItemWidth：得到当前position的滑动距离 //mEntity.scrollPosition * mItemMargin：得到（0至position）的所有item的margin //用当前item的宽度-所有margin-当前position的滑动距离，就得到offset。
                     mEntity.scrollOffset = mItemWidth - offset % mItemWidth + mEntity.scrollPosition * mItemMargin;
                    }
                    break;
            }
        }
    }


}
