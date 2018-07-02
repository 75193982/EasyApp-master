package com.liaoliao.chat.ui;/**
 * Created by dell on 2017/4/5/0005.
 */

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.liaoliao.R;
import com.liaoliao.chat.base.BaseFragment;

import com.liaoliao.chat.ui.view.BottomBar;
import com.liaoliao.chat.ui.view.PopupMenuUtil;
import com.liaoliao.chat.utils.StartBrotherEvent;
import com.liaoliao.server.HomeWatcherReceiver;
import com.liaoliao.server.widget.LoadDialog;
import com.liaoliao.ui.activity.LoginActivity;
import com.liaoliao.ui.activity.MainActivity;
import com.liaoliao.ui.activity.NewFriendListActivity;
import com.liaoliao.ui.adapter.ConversationListAdapterEx;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.ContactNotificationMessage;
import me.yokeyword.fragmentation.SupportFragment;

import static android.content.Context.MODE_PRIVATE;

/**
 * created by LiChengalin at 2017/4/5/0005
 */
public class MainFragment extends BaseFragment implements
        IUnReadMessageObserver {

    private static final int FIRST = 0;
    private static final int SECOND = 1;
    private static final int THIRD = 2;
    private static final int FOURTH = 3;
    private BottomBar mBottomBar;
    private ImageView mCenterImage;
    private SupportFragment[] mFragments = new SupportFragment[5];
    private boolean isDebug;
    private int mSelectPosition,mCurrentPosition=0;
    private Conversation.ConversationType[] mConversationsTypes = null;
    /**
     * 会话列表的fragment
     */
    private com.liaoliao.chat.ui.ConversationListFragment mConversationListFragment = null;
    public static MainFragment newInstance() {

        Bundle bundle = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        Fragment conversationList = initConversationList();
        isDebug = getActivity().getSharedPreferences("config", MODE_PRIVATE).getBoolean("isDebug", false);
        mCenterImage = (ImageView) view.findViewById (R.id.center_img);
        if (savedInstanceState == null) {
            mFragments[FIRST] = com.liaoliao.chat.ui.ConversationListFragment.newInstance();
            mFragments[SECOND] = SecondFragment.newInstance();
            mFragments[THIRD] = SecondFragment.newInstance();
            mFragments[FOURTH] = FouthFragment.newInstance();
            loadMultipleRootFragment(R.id.fl_tab_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD], mFragments[FOURTH]);
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题
            // 这里我们需要拿到mFragments的引用,也可以通过getChildFragmentManager.getFragments()自行进行判断查找(效率更高些),用下面的方法查找更方便些
            mFragments[FIRST] = findChildFragment(com.liaoliao.chat.ui.ConversationListFragment.class);
            mFragments[SECOND] = findChildFragment(SecondFragment.class);
            mFragments[THIRD] = findChildFragment(SecondFragment.class);
            mFragments[FOURTH] = findChildFragment(FouthFragment.class);
        }
        //注册
        EventBus.getDefault().register(this);
        initView(view);
        registerHomeKeyReceiver(getActivity());
        return view;

    }


    private Fragment initConversationList() {
        if (mConversationListFragment == null) {
            com.liaoliao.chat.ui.ConversationListFragment listFragment = new com.liaoliao.chat.ui.ConversationListFragment();
            listFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
            Uri uri;
            if (isDebug) {
                uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                        .appendPath("conversationlist")
                        .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "true") //设置私聊会话是否聚合显示
                        .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//群组
                        .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                        .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                        .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//系统
                        .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "true")
                        .build();
                mConversationsTypes = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
                        Conversation.ConversationType.GROUP,
                        Conversation.ConversationType.PUBLIC_SERVICE,
                        Conversation.ConversationType.APP_PUBLIC_SERVICE,
                        Conversation.ConversationType.SYSTEM,
                        Conversation.ConversationType.DISCUSSION
                };

            } else {
                uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                        .appendPath("conversationlist")
                        .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                        .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
                        .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                        .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                        .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//系统
                        .build();
                mConversationsTypes = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
                        Conversation.ConversationType.GROUP,
                        Conversation.ConversationType.PUBLIC_SERVICE,
                        Conversation.ConversationType.APP_PUBLIC_SERVICE,
                        Conversation.ConversationType.SYSTEM
                };
            }
            listFragment.setUri(uri);
            mConversationListFragment = listFragment;
            return listFragment;
        } else {
            return mConversationListFragment;
        }
    }


    private void initView(final View view) {

        mBottomBar = (BottomBar) view.findViewById(R.id.bottomBar);

        mBottomBar.setOnBottombarOnclick(new BottomBar.OnBottonbarClick() {
            @Override
            public void onFirstClick() {
                mSelectPosition = 0;
                showHideFragment(mFragments[mSelectPosition], mFragments[mCurrentPosition]);
                mCurrentPosition = 0;
            }

            @Override
            public void onSecondClick() {
                mSelectPosition = 1;
                showHideFragment(mFragments[mSelectPosition], mFragments[mCurrentPosition]);
                mCurrentPosition = 1;
            }

            @Override
            public void onThirdClick() {
                mSelectPosition = 2;
                showHideFragment(mFragments[mSelectPosition], mFragments[mCurrentPosition]);
                mCurrentPosition = 2;
            }

            @Override
            public void onFouthClick() {
                mSelectPosition = 3;
                showHideFragment(mFragments[mSelectPosition], mFragments[mCurrentPosition]);
                mCurrentPosition = 3;
            }

            @Override
            public void onCenterClick() {
                PopupMenuUtil.getInstance()._show(getContext(), mCenterImage);
                RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.PRIVATE, "Cat", "Cat");
            }


        });

    }
    /**
     * start other BrotherFragment
     */
    @Subscribe
    public void startBrother(StartBrotherEvent event) {
        start(event.targetFragment);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



    protected void initData() {

        final Conversation.ConversationType[] conversationTypes = {
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE
        };

        RongIM.getInstance().addUnReadMessageCountChangedObserver(this, conversationTypes);
        getConversationPush();// 获取 push 的 id 和 target
        getPushMessage();
    }

    private void getConversationPush() {
        if ( getActivity().getIntent() != null && getActivity().getIntent().hasExtra("PUSH_CONVERSATIONTYPE") && getActivity().getIntent().hasExtra("PUSH_TARGETID")) {

            final String conversationType = getActivity().getIntent().getStringExtra("PUSH_CONVERSATIONTYPE");
            final String targetId = getActivity().getIntent().getStringExtra("PUSH_TARGETID");


            RongIM.getInstance().getConversation(Conversation.ConversationType.valueOf(conversationType), targetId, new RongIMClient.ResultCallback<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {

                    if (conversation != null) {

                        if (conversation.getLatestMessage() instanceof ContactNotificationMessage) { //好友消息的push
                            startActivity(new Intent(getActivity(), NewFriendListActivity.class));
                        } else {
                            Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon().appendPath("conversation")
                                    .appendPath(conversationType).appendQueryParameter("targetId", targetId).build();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {

                }
            });
        }
    }

    /**
     * 得到不落地 push 消息
     */
    private void getPushMessage() {
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {
            String path = intent.getData().getPath();
            if (path.contains("push_message")) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("config", MODE_PRIVATE);
                String cacheToken = sharedPreferences.getString("loginToken", "");
                if (TextUtils.isEmpty(cacheToken)) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    if (!RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
                        LoadDialog.show(getActivity());
                        RongIM.connect(cacheToken, new RongIMClient.ConnectCallback() {
                            @Override
                            public void onTokenIncorrect() {
                                LoadDialog.dismiss(getActivity());
                            }

                            @Override
                            public void onSuccess(String s) {
                                LoadDialog.dismiss(getActivity());
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode e) {
                                LoadDialog.dismiss(getActivity());
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public void onCountChanged(int i) {

    }


    private HomeWatcherReceiver mHomeKeyReceiver = null;
    //如果遇见 Android 7.0 系统切换到后台回来无效的情况 把下面注册广播相关代码注释或者删除即可解决。下面广播重写 home 键是为了解决三星 note3 按 home 键花屏的一个问题
    private void registerHomeKeyReceiver(Context context) {
        if (mHomeKeyReceiver == null) {
            mHomeKeyReceiver = new HomeWatcherReceiver();
            final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            try {
                context.registerReceiver(mHomeKeyReceiver, homeFilter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
