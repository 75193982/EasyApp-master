package com.jcodecraeer.xrecyclerview;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



public class ArrowRefreshHeader extends LinearLayout implements BaseRefreshHeader{
	private LinearLayout mContainer;
	private ImageView mArrowImageView;
	private ImageView mProgressBar;

	private int mState = STATE_NORMAL;
    private Context mContext;
	

	private Animation mRotateUpAnim;
	private Animation mRotateDownAnim;
	
	private final int ROTATE_ANIM_DURATION = 180;
    private XRecyclerView xRecyclerView ;
	public int mMeasuredHeight;
	int reId[] = new int[]{R.drawable.icon_alpaca_01,R.drawable.icon_alpaca_02,R.drawable.icon_alpaca_03,R.drawable.icon_alpaca_04,R.drawable.icon_alpaca_05,R.drawable.icon_alpaca_06,R.drawable.icon_alpaca_07,R.drawable.icon_alpaca_08,R.drawable.icon_alpaca_09,R.drawable.icon_alpaca_10,R.drawable.icon_alpaca_11,R.drawable.icon_alpaca_12,R.drawable.icon_alpaca_13,R.drawable.icon_alpaca_14,R.drawable.icon_alpaca_15,R.drawable.icon_alpaca_16,R.drawable.icon_alpaca_17,R.drawable.icon_alpaca_18,R.drawable.icon_alpaca_19,R.drawable.icon_alpaca_20};
	public ArrowRefreshHeader(Context context, XRecyclerView xRecyclerView) {
		super(context);
		initView(context);
		this.xRecyclerView = xRecyclerView ;
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ArrowRefreshHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {

        mContext = context;
		// 初始情况，设置下拉刷新view高度为0
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.xrecyclerview_header, null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
		this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);

		addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
		setGravity(Gravity.BOTTOM);

		mArrowImageView = (ImageView)findViewById(R.id.listview_header_arrow);


        //init the progress view
		mProgressBar = (ImageView)findViewById(R.id.listview_header_progressbar);
       /* AVLoadingIndicatorView progressView = new  AVLoadingIndicatorView(context);
        progressView.setIndicatorColor(0xffB5B5B5);
        progressView.setIndicatorId(ProgressStyle.BallSpinFadeLoader);
        mProgressBar.setView(progressView);*/


		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
		

		measure(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		mMeasuredHeight = getMeasuredHeight();
	}

    public void setProgressStyle(int style) {
       /* if(style == ProgressStyle.SysProgress){
            mProgressBar.setView(new ProgressBar(mContext, null, android.R.attr.progressBarStyle));
        }else{
            AVLoadingIndicatorView progressView = new  AVLoadingIndicatorView(this.getContext());
            progressView.setIndicatorColor(0xffB5B5B5);
            progressView.setIndicatorId(style);
            mProgressBar.setView(progressView);
        }*/
    }

    public void setArrowImageView(int resid){
        mArrowImageView.setImageResource(resid);
    }

	public void setState(int state) {
		if (state == mState) {
            return ;
        }

		if (state == STATE_REFRESHING) {	// 显示进度
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
			mProgressBar.setImageResource(R.drawable.frame_animation);
		        // 获取iv的背景
		        AnimationDrawable ad = (AnimationDrawable) mProgressBar.getDrawable();
		        ad.start();
		} else if(state == STATE_DONE) {
            mArrowImageView.setVisibility(View.INVISIBLE);
           // mProgressBar.setVisibility(View.INVISIBLE);
        } else {	// 显示箭头图片
			mArrowImageView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);
		}
		
		switch(state){
            case STATE_NORMAL:
                if (mState == STATE_RELEASE_TO_REFRESH) {
                    //mArrowImageView.startAnimation(mRotateDownAnim);
                }
                if (mState == STATE_REFRESHING) {
                    mArrowImageView.clearAnimation();
                }

                break;
            case STATE_RELEASE_TO_REFRESH:
                if (mState != STATE_RELEASE_TO_REFRESH) {
                	 mArrowImageView.clearAnimation();
                	

                }
                break;
            case     STATE_REFRESHING:

                break;
            case    STATE_DONE:

                break;
            default:
		}
		
		mState = state;
	}

    public int getState() {
        return mState;
    }

    @Override
	public void  refreshComplete(){
    	if(null == refreshDate){
    		refreshDate = new Date();
    	}
    	

        setState(STATE_DONE);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                reset();
            }
        }, 500);
	}

	public void setVisiableHeight(int height) {
		if (height < 0) {
            height = 0;
        }
		LayoutParams lp = (LayoutParams) mContainer
				.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	/*public int getVisiableHeight() {
        int height = 0;
        LayoutParams lp = (LayoutParams) mContainer
                .getLayoutParams();
        height = lp.height;
		return height;
	}*/
    public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        return lp.height;
    }
    @Override
    public void onMove(float delta) {
        if(getVisibleHeight() > 0 || delta > 0) {
            if(getVisibleHeight() >mMeasuredHeight/2.6){
                setArrowImageView(reId[0]);

            }
            if( getVisibleHeight() >mMeasuredHeight/2.5){
                setArrowImageView(reId[1]);

            }
            if( getVisibleHeight() >mMeasuredHeight/2.4){
                setArrowImageView(reId[2]);

            }
            if( getVisibleHeight() >mMeasuredHeight/2.3){
                setArrowImageView(reId[3]);

            }
            if( getVisibleHeight() >mMeasuredHeight/2.2){
                setArrowImageView(reId[4]);

            }
            if( getVisibleHeight()>mMeasuredHeight/2.1){
                setArrowImageView(reId[5]);

            }
        	if(getVisibleHeight() >mMeasuredHeight/2.0){
        		setArrowImageView(reId[6]);

        	}
        	if( getVisibleHeight() >mMeasuredHeight/1.9){
        		setArrowImageView(reId[7]);

        	}
        	if( getVisibleHeight() >mMeasuredHeight/1.8){
        		setArrowImageView(reId[8]);

        	}
        	if( getVisibleHeight() >mMeasuredHeight/1.7){
        		setArrowImageView(reId[9]);

        	}
            if(getVisibleHeight() >mMeasuredHeight/1.6){
                setArrowImageView(reId[10]);

            }
            if( getVisibleHeight() >mMeasuredHeight/1.5){
                setArrowImageView(reId[11]);

            }
            if( getVisibleHeight() >mMeasuredHeight/1.4){
                setArrowImageView(reId[12]);

            }
            if( getVisibleHeight() >mMeasuredHeight/1.3){
                setArrowImageView(reId[13]);

            }
            if(getVisibleHeight() >mMeasuredHeight/1.2){
                setArrowImageView(reId[14]);

            }
            if( getVisibleHeight() >mMeasuredHeight/1.1){
                setArrowImageView(reId[15]);

            }
            if( getVisibleHeight() >mMeasuredHeight/1.15){
                setArrowImageView(reId[16]);

            }
            if( getVisibleHeight() >mMeasuredHeight/1.13){
                setArrowImageView(reId[17]);

            }
            if( getVisibleHeight() >mMeasuredHeight/1.10){
                setArrowImageView(reId[18]);

            }
            if( getVisibleHeight() >mMeasuredHeight/1){
                setArrowImageView(reId[19]);

            }

            setVisiableHeight((int) delta + getVisibleHeight());
            if (mState <= STATE_RELEASE_TO_REFRESH) { // 未处于刷新状态，更新箭头
                if (getVisibleHeight() > mMeasuredHeight) {
                    setState(STATE_RELEASE_TO_REFRESH);
                }else {
                    setState(STATE_NORMAL);
                }
            }
        }
    }

    @Override
    public boolean releaseAction() {
        boolean isOnRefresh = false;
        int height = getVisibleHeight();
        if (height == 0) // not visible.
            isOnRefresh = false;

        if(getVisibleHeight() > mMeasuredHeight &&  mState < STATE_REFRESHING){
            setState(STATE_REFRESHING);
            isOnRefresh = true;
        }
        // refreshing and header isn't shown fully. do nothing.
        if (mState == STATE_REFRESHING && height <=  mMeasuredHeight) {
            //return;
        }
        int destHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mState == STATE_REFRESHING) {
            destHeight = mMeasuredHeight;
        }
        smoothScrollTo(destHeight);

        return isOnRefresh;
    }



    public void reset() {
        smoothScrollTo(0);
        setState(STATE_NORMAL);

    }

    private void smoothScrollTo(int destHeight) {
        if( destHeight == 0 ){
            xRecyclerView.getLoadingListener().onTouch(false);
            setArrowImageView(reId[0]);
        }
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                setVisiableHeight((Integer) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    /**
	 * 获得系统的最新时间
	 * 
	 * @return
	 */
	private String getLastUpdateTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
		return sdf.format(System.currentTimeMillis());
	}
    
    private  Date refreshDate;
    public  String friendlyTime(Date time) {
    	refreshDate = time;
        //获取time距离当前的秒数
        int ct = (int)((System.currentTimeMillis() - time.getTime())/1000);

        if(ct == 0) {
            return "刚刚";
        }

        if(ct > 0 && ct < 60) {
            return ct + "秒前";
        }

        if(ct >= 60 && ct < 3600) {
            return Math.max(ct / 60,1) + "分钟前";
        }
        if(ct >= 3600 && ct < 86400) {
            return ct / 3600 + "小时前";
        }
        if(ct >= 86400 && ct < 2592000){ //86400 * 30
            int day = ct / 86400 ;
            return day + "天前";
        }
        if(ct >= 2592000 && ct < 31104000) { //86400 * 30
            return ct / 2592000 + "月前";
        }
        return ct / 31104000 + "年前";
    }


    public void destroy(){
        mProgressBar = null;

        if(mRotateUpAnim != null){
            mRotateUpAnim.cancel();
            mRotateUpAnim = null;
        }
        if(mRotateDownAnim != null){
            mRotateDownAnim.cancel();
            mRotateDownAnim = null;
        }
    }

}
