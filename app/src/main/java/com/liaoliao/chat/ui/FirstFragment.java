package com.liaoliao.chat.ui;/**
 * Created by dell on 2017/4/5/0005.
 */

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.liaoliao.R;
import com.liaoliao.chat.adapter.HeaderRecyclerAdapter;
import com.liaoliao.chat.adapter.HomeAdapter;
import com.liaoliao.chat.base.BaseFragment;
import com.liaoliao.chat.loader.GlideImageLoader;
import com.liaoliao.chat.model.HeaderClass;
import com.liaoliao.chat.model.HserSection;
import com.liaoliao.chat.model.UserBao;
import com.liaoliao.chat.model.UserBaoTitle;
import com.liaoliao.chat.widget.CircleImageView;
import com.liaoliao.chat.widget.MyRecyclerView;

import com.liaoliao.chat.widget.PileLayout;
import com.liaoliao.utils.CommonUtils;
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
    @BindView(R.id.tv_city_name)
    TextView tvCityName;
    @BindView(R.id.searchView)
    TextView searchView;

    boolean isTouch ;

    String[] urls = {
            "http://img0.imgtn.bdimg.com/it/u=2263418180,3668836868&fm=206&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=2263418180,3668836868&fm=206&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=2263418180,3668836868&fm=206&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=2263418180,3668836868&fm=206&gp=0.jpg"
    };

    private int height = 600;// 滑动开始变色的高,真实项目中此高度是由广告轮播或其他首页view高度决定
    private int overallXScroll = 0;
    private int bannerHeight;

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
        images.add("http://img.zcool.cn/community/015e6156d009c06ac7252ce62b38cb.JPG");
        images.add("http://image.tupian114.com/20160901/0140004782.jpg");
        images.add("http://img.zcool.cn/community/017f93570b3d876ac7251f056a2209.png");
        images.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic259ohaj30ci08c74r.jpg");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        List<HserSection> list = new ArrayList<HserSection>();
        HserSection hserSection ;
        for (int i = 0; i <10 ; i++) {
            hserSection= new HserSection(true,"视频聊天"+i);
            list.add(hserSection);
            UserBaoTitle title = new UserBaoTitle();
            title.title = "视频聊天"+i ;
            List<UserBao> listS = new ArrayList<>();
            UserBao userBao = new UserBao();
            userBao.headImg="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1531763385496&di=552661e3ea0489549d6d00531babcb3a&imgtype=0&src=http%3A%2F%2Fimg3.duitang.com%2Fuploads%2Fitem%2F201512%2F10%2F20151210131006_xzeKh.jpeg";
            userBao.price = "25元/小时";
            userBao.name="小菠萝"+i;
            userBao.uint="初级";
            listS.add( userBao);

            UserBao userBao5 = new UserBao();
            userBao5.headImg="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1531763530101&di=ef064324ef99bca0833e1cefa16f5b89&imgtype=0&src=http%3A%2F%2Fimg4.duitang.com%2Fuploads%2Fitem%2F201512%2F13%2F20151213190125_KcZnY.jpeg";
            userBao5.price = "125元/小时";
            userBao5.name="小菠萝"+i;
            userBao5.uint="高级";
            listS.add( userBao5);
            UserBao userBao4 = new UserBao();
            userBao4.headImg="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1531763385496&di=552661e3ea0489549d6d00531babcb3a&imgtype=0&src=http%3A%2F%2Fimg3.duitang.com%2Fuploads%2Fitem%2F201512%2F10%2F20151210131006_xzeKh.jpeg";
            userBao4.price = "215元/小时";
            userBao4.name="小菠萝"+i;
            userBao4.uint="初级";
            listS.add( userBao4);
            UserBao userBao3 = new UserBao();
            userBao3.headImg="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1531763578112&di=ee42f926b7d378bfc873ce0c42fda5e9&imgtype=0&src=http%3A%2F%2Fimg4q.duitang.com%2Fuploads%2Fitem%2F201504%2F14%2F20150414H2406_rKehA.jpeg";
            userBao3.price = "25元/小时";
            userBao3.uint="初级";
            userBao3.name="小菠萝"+i;
            listS.add( userBao3);
            UserBao userBao2 = new UserBao();
            userBao2.headImg="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1531763564417&di=db3c18c10b10ad67188c2aecfa1eedac&imgtype=0&src=http%3A%2F%2Fimg4.duitang.com%2Fuploads%2Fitem%2F201501%2F14%2F20150114145326_sQPjW.jpeg";
            userBao2.price = "25元/小时";
            userBao2.name="小菠萝"+i;
            userBao2.uint="高级";
            listS.add( userBao2);
            UserBao userBao1 = new UserBao();
            userBao1.headImg="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1531763545720&di=2846823ec8fc4f3ac169e5135684df01&imgtype=0&src=http%3A%2F%2Fimg3.duitang.com%2Fuploads%2Fitem%2F201512%2F10%2F20151210135838_Y2SvK.jpeg";
            userBao1.price = "25元/小时";
            userBao1.name="小菠萝"+i;
            userBao1.uint="高级";
            listS.add( userBao1);
            title.list = listS;
            list.add(new HserSection(title));
        }




        HomeAdapter adapter = new HomeAdapter(getActivity(),list);

        View view = View.inflate(getContext(), R.layout.home_head, null);
        Banner banner = view.findViewById(R.id.banner);
        MyRecyclerView recyclerView_item = view.findViewById(R.id.recyclerView_item);
        PileLayout pile_layout = view.findViewById(R.id.pile_layout);
        initPraises(pile_layout);
        recyclerViewHeadClass(recyclerView_item);
        globalLayout(banner);
        //默认是CIRCLE_INDICATOR
        banner.setImages(images)
                // .setBannerTitles(App.titles)
                .setImageLoader(new GlideImageLoader())
                .start();
        mRecyclerView.addHeaderView(view);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLoadingMoreEnabled(false);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                overallXScroll = 0 ;
                mRecyclerView.refreshComplete();
            }

            @Override
            public void onTouch(boolean b) {
                overallXScroll = 0 ;
                int paddingBottom = searchView.getPaddingBottom();
                int paddingLeft = searchView.getPaddingLeft();
                int paddingRight = searchView.getPaddingRight();
                int paddingTop = searchView.getPaddingTop();
                searchView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.search_while_shape));
                searchView.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);
                tvCityName.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                toolbar.setBackgroundColor(Color.argb((int) 0, 255, 255, 255));
            }

            @Override
            public void onLoadMore() {
                // load more data here
            }
        });
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
                    Log.d("FirstFragment","1-----------"+overallXScroll);
                    int paddingBottom = searchView.getPaddingBottom();
                    int paddingLeft = searchView.getPaddingLeft();
                    int paddingRight = searchView.getPaddingRight();
                    int paddingTop = searchView.getPaddingTop();
                    searchView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.search_while_shape));
                    searchView.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);
                    tvCityName.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                    toolbar.setBackgroundColor(Color.argb((int) 0, 255, 255, 255));
                } else if (overallXScroll > 0 && overallXScroll <= height) { //滑动距离小于banner图的高度时，设置背景和字体颜色颜色透明度渐变
                    float scale = (float) overallXScroll / height;
                    float alpha = (255 * scale);
                    Log.d("FirstFragment","2-----------"+overallXScroll);
                    tvCityName.setTextColor(ContextCompat.getColor(getActivity(),R.color.black));
                    int paddingBottom = searchView.getPaddingBottom();
                    int paddingLeft = searchView.getPaddingLeft();
                    int paddingRight = searchView.getPaddingRight();
                    int paddingTop = searchView.getPaddingTop();
                    searchView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.search_shape));
                    searchView.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);
                    toolbar.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
                } else {
                    Log.d("FirstFragment","3-----------"+overallXScroll);
                    toolbar.setBackgroundColor(Color.argb((int) 255, 255, 255, 255));


                }
            }
        });
    }



    public void initPraises(PileLayout pileLayout ) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < urls.length; i++) {
            CircleImageView imageView = (CircleImageView) inflater.inflate(R.layout.item_praise, pileLayout, false);
            Glide.with(this).load(urls[i]).into(imageView);
            pileLayout.addView(imageView);
        }

    }

    /**
     * 渲染分类
     * @param recyclerView_item
     */
    private void recyclerViewHeadClass(MyRecyclerView recyclerView_item) {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 5) ;
       // recyclerView_item.setNestedScrollingEnabled(false);
        recyclerView_item.setLayoutManager(layoutManager);
        HeaderRecyclerAdapter adapter = new HeaderRecyclerAdapter(getHeaderList());
        recyclerView_item.setAdapter(adapter);


    }



    private List<HeaderClass> getHeaderList() {

        List<HeaderClass> headerList = new ArrayList<>();
        HeaderClass h1 = new HeaderClass();
        h1.setName("语音聊天");
        h1.setIcon("http://u.uin.com/ddm/uin_pic/image/u9956/20180713/Umeeting20180713143441_985.png");
        HeaderClass h2 = new HeaderClass();
        h2.setName("视频聊天");
        h2.setIcon("http://u.uin.com/ddm/uin_pic/image/u9956/20180713/Umeeting20180713143441_985.png");
        HeaderClass h3 = new HeaderClass();
        h3.setName("声音鉴定");
        h3.setIcon("http://u.uin.com/ddm/uin_pic/image/u9956/20180713/Umeeting20180713143441_985.png");
        HeaderClass h4 = new HeaderClass();
        h4.setName("哄睡觉");
        h4.setIcon("http://u.uin.com/ddm/uin_pic/image/u9956/20180713/Umeeting20180713152603_648.gif");
        HeaderClass h5 = new HeaderClass();
        h5.setName("叫起床");
        h5.setIcon("http://u.uin.com/ddm/uin_pic/image/u9956/20180713/Umeeting20180713143441_985.png");
        HeaderClass h6 = new HeaderClass();
        h6.setName("线上歌手");
        h6.setIcon("http://u.uin.com/ddm/uin_pic/image/u9956/20180713/Umeeting20180713143441_985.png");
        HeaderClass h7 = new HeaderClass();
        h7.setName("情感咨询");
        h7.setIcon("http://u.uin.com/ddm/uin_pic/image/u9956/20180713/Umeeting20180713152603_648.gif");
        HeaderClass h8 = new HeaderClass();
        h8.setName("聊方言");
        h8.setIcon("http://u.uin.com/ddm/uin_pic/image/u9956/20180713/Umeeting20180713143441_985.png");
        HeaderClass h9 = new HeaderClass();
        h9.setName("聊方言");
        h9.setIcon("http://u.uin.com/ddm/uin_pic/image/u9956/20180713/Umeeting20180713143441_985.png");

        HeaderClass h10 = new HeaderClass();
        h10.setName("全部");
        h10.setIcon("http://u.uin.com/ddm/uin_pic/image/u9956/20180713/Umeeting20180713143441_985.png");
        headerList.add(h1);
        headerList.add(h2);
        headerList.add(h3);
        headerList.add(h4);
        headerList.add(h5);
        headerList.add(h6);
        headerList.add(h7);
        headerList.add(h8);
        headerList.add(h9);
        headerList.add(h10);

        return headerList;
    }

    /**
     * 计算高度，获取滑动显示的高度
     *
     * @param banner
     */
    private void globalLayout(Banner banner) {
        int screenHeight = CommonUtils.getScreenHeight(getActivity());
        ViewGroup.LayoutParams layoutParams = banner.getLayoutParams();
        bannerHeight = (screenHeight / 4) + 50;
        layoutParams.height = (screenHeight / 4) + 50;
        banner.setLayoutParams(layoutParams);

        toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= 16) {
                    toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    toolbar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                height = bannerHeight - toolbar.getHeight();

            }
        });

    }


    @Override
    protected void setUpData() {

    }

}
