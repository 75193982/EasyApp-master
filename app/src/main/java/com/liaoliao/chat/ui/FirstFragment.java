package com.liaoliao.chat.ui;/**
 * Created by dell on 2017/4/5/0005.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.liaoliao.R;
import com.liaoliao.chat.adapter.HomeAdapter;
import com.liaoliao.chat.base.BaseFragment;
import com.liaoliao.chat.loader.GlideImageLoader;
import com.liaoliao.chat.utils.StatusBarUtil;
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
    public static List<String> images = new ArrayList<>();
    @BindView(R.id.title_layout)
    RelativeLayout titleLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    private int height = 640;// 滑动开始变色的高,真实项目中此高度是由广告轮播或其他首页view高度决定
    private int overallXScroll = 0;

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
        //状态栏透明和间距处理
       /* StatusBarUtil.immersive(getActivity());
        StatusBarUtil.setPaddingSmart(getActivity(), toolbar);*/
        // StatusBarUtil.immersive(getActivity(), ContextCompat.getColor(getContext(),R.color.white));
        images.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic1xjab4j30ci08cjrv.jpg");
        images.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic1xjab4j30ci08cjrv.jpg");
        images.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic21363tj30ci08ct96.jpg");
        images.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic259ohaj30ci08c74r.jpg");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        HomeAdapter adapter = new HomeAdapter(R.layout.layout_bottom, list);

        View view = View.inflate(getContext(), R.layout.home_head, null);
        Banner banner = view.findViewById(R.id.banner);
        //默认是CIRCLE_INDICATOR
        banner.setImages(images)
                // .setBannerTitles(App.titles)
                .setImageLoader(new GlideImageLoader())
                .start();
        mRecyclerView.addHeaderView(view);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                overallXScroll = overallXScroll + dy;// 累加y值 解决滑动一半y值为0
                if (overallXScroll <= 0) {   //设置标题的背景颜色
                    toolbar.setBackgroundColor(Color.argb((int) 0, 255, 255, 255));
                } else if (overallXScroll > 0 && overallXScroll <= height) { //滑动距离小于banner图的高度时，设置背景和字体颜色颜色透明度渐变
                    float scale = (float) overallXScroll / height;
                    float alpha = (255 * scale);
                    toolbar.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
                } else {
                    toolbar.setBackgroundColor(Color.argb((int) 255, 255, 255, 255));
                }
            }
        });
    }


    @Override
    protected void setUpData() {

    }


}
