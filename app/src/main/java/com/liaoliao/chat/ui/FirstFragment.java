package com.liaoliao.chat.ui;/**
 * Created by dell on 2017/4/5/0005.
 */

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.liaoliao.R;
import com.liaoliao.chat.adapter.HomeAdapter;
import com.liaoliao.chat.base.BaseFragment;
import com.liaoliao.chat.loader.GlideImageLoader;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;


/**
 * 首页
 */
public class FirstFragment extends BaseFragment {

    @BindView(R.id.xRecyclerView)
    XRecyclerView mRecyclerView;
    public static List<String> images=new ArrayList<>();

    public static SupportFragment newInstance() {
        Bundle bundle = new Bundle();
        FirstFragment fragment = new FirstFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_first;
    }

    @Override
    protected void setUpView() {
        images.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic1xjab4j30ci08cjrv.jpg");
        images.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic1xjab4j30ci08cjrv.jpg");
        images.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic21363tj30ci08ct96.jpg");
        images.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic259ohaj30ci08c74r.jpg");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        HomeAdapter adapter = new HomeAdapter(R.layout.layout_bottom,null);

        View view = View.inflate(getContext(), R.layout.home_head, null);
        Banner banner = view.findViewById(R.id.banner);
        //默认是CIRCLE_INDICATOR
        banner.setImages(images)
               // .setBannerTitles(App.titles)
                .setImageLoader(new GlideImageLoader())
                .start();
        mRecyclerView.addHeaderView(view);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void setUpData() {

    }


}
