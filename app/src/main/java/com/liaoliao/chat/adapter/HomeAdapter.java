package com.liaoliao.chat.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liaoliao.R;
import com.liaoliao.chat.model.HserSection;

import java.util.List;

/**
 * Created by lv on 2018/7/9 for EasyApp-master
 */
public class HomeAdapter extends BaseSectionQuickAdapter<HserSection, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param layoutResId      The layout resource id of each item.
     * @param sectionHeadResId The section head layout id for each item
     * @param data             A new list is created out of this one to avoid mutable list
     */
    public HomeAdapter(int layoutResId, int sectionHeadResId, List<HserSection> data) {
        super(layoutResId, R.layout.adapter_home_item_head, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, HserSection item) {
        helper.setText(R.id.title, item.header);
    }

    @Override
    protected void convert(BaseViewHolder helper, HserSection item) {

    }
}
