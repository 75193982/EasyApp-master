package com.liaoliao.chat.ui.view;/**
 * Created by dell on 2017/4/6/0006.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.liaoliao.R;

import java.util.Calendar;


/**
 * created by LiChengalin at 2017/4/6/0006
 */
public class BottomBar extends LinearLayout {

    private Context mContext;

    private FrameLayout mFirst_bottom, mSecond_bottom, mThird_bottom, mFouth_bottom, mCenter_bottom;
    private OnBottonbarClick mOnBottonbarClick;

    public BottomBar(Context context) {
        super(context);
        init(context);
    }

    public BottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.layout_bottom_1, this, true);
        //获取控件id
        initId();
        onBottomBarClick();
        mFirst_bottom.setSelected(true);
    }



    private void initId() {

        mFirst_bottom = (FrameLayout) findViewById(R.id.first);
        mSecond_bottom = (FrameLayout) findViewById(R.id.second);
        mThird_bottom = (FrameLayout) findViewById(R.id.third);
        mFouth_bottom = (FrameLayout) findViewById(R.id.fouth);
        mCenter_bottom = (FrameLayout) findViewById(R.id.center);

    }


    //设置所有按钮都是默认都不选中
    private void seleted() {
        mFirst_bottom.setSelected(false);
        mSecond_bottom.setSelected(false);
        mThird_bottom.setSelected(false);
        mFouth_bottom.setSelected(false);
    }

    /**
     * 底部按钮点击监听器
     */
    private void onBottomBarClick() {

        mFirst_bottom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                seleted();
                mFirst_bottom.setSelected(true);
                if (mOnBottonbarClick != null) {
                    mOnBottonbarClick.onFirstClick();
                }
            }
        });
        mSecond_bottom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                seleted();
                mSecond_bottom.setSelected(true);
                if (mOnBottonbarClick != null) {
                    mOnBottonbarClick.onSecondClick();
                }
            }
        });
        mThird_bottom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                seleted();
                mThird_bottom.setSelected(true);
                if (mOnBottonbarClick != null) {
                    mOnBottonbarClick.onThirdClick();
                }
            }
        });
        mFouth_bottom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                seleted();
                mFouth_bottom.setSelected(true);
                if (mOnBottonbarClick != null) {
                    mOnBottonbarClick.onFouthClick();
                }
            }
        });
       /* mCenter_bottom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        mCenter_bottom.setOnClickListener(new NoDoubleClickListener1() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (mOnBottonbarClick != null) {
                    mOnBottonbarClick.onCenterClick();
                }
            }
        });

    }
    public void setOnBottombarOnclick(OnBottonbarClick onBottonbarClick) {

        mOnBottonbarClick = onBottonbarClick;
    }

    public interface OnBottonbarClick {
        void onFirstClick();

        void onSecondClick();

        void onThirdClick();

        void onFouthClick();

        void onCenterClick();
    }


    public static abstract class NoDoubleClickListener1 implements View.OnClickListener {
        public static final int MIN_CLICK_DELAY_TIME = 1000;
        public static  long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                onNoDoubleClick(v);
            }
        }

        protected abstract void onNoDoubleClick(View v);

    }
}
