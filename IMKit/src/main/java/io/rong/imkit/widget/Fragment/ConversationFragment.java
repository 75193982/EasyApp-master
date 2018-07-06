package io.rong.imkit.widget.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import io.rong.common.RLog;
import io.rong.common.SystemUtils;
import io.rong.eventbus.EventBus;
import io.rong.imkit.DeleteClickActions;

import io.rong.imkit.IPublicServiceMenuClickListener;
import io.rong.imkit.InputMenu;
import io.rong.imkit.R;
import io.rong.imkit.RongContext;

import io.rong.imkit.RongIM;
import io.rong.imkit.RongKitReceiver;
import io.rong.imkit.RongMessageItemLongClickActionManager;
import io.rong.imkit.actions.IClickActions;
import io.rong.imkit.actions.OnMoreActionStateListener;
import io.rong.imkit.fragment.IHistoryDataResultCallback;
import io.rong.imkit.fragment.UriFragment;
import io.rong.imkit.manager.AudioPlayManager;
import io.rong.imkit.manager.AudioRecordManager;
import io.rong.imkit.manager.InternalModuleManager;
import io.rong.imkit.manager.SendImageManager;
import io.rong.imkit.manager.UnReadMessageManager;
import io.rong.imkit.mention.RongMentionManager;
import io.rong.imkit.model.ConversationInfo;
import io.rong.imkit.model.Event;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.plugin.DefaultLocationPlugin;


import io.rong.imkit.plugin.location.AMapRealTimeActivity;
import io.rong.imkit.plugin.location.IRealTimeLocationStateListener;
import io.rong.imkit.plugin.location.IUserInfoProvider;
import io.rong.imkit.plugin.location.LocationManager;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imkit.utilities.PromptPopupDialog;
import io.rong.imkit.widget.AutoRefreshListView;
import io.rong.imkit.widget.CSEvaluateDialog;
import io.rong.imkit.widget.IExtensionClickListener;
import io.rong.imkit.widget.IPluginModule;
import io.rong.imkit.widget.RongExtension;
import io.rong.imkit.widget.SingleChoiceDialog;
import io.rong.imkit.widget.adapter.MessageListAdapter;


import io.rong.imkit.widget.provider.MessageItemLongClickAction;
import io.rong.imlib.CustomServiceConfig;
import io.rong.imlib.ICustomServiceListener;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.MessageTag;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.location.RealTimeLocationConstant;
import io.rong.imlib.model.CSCustomServiceInfo;
import io.rong.imlib.model.CSGroupItem;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.CustomServiceMode;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.PublicServiceMenu;
import io.rong.imlib.model.PublicServiceMenuItem;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.ReadReceiptInfo;
import io.rong.imlib.model.UserInfo;
import io.rong.message.CSPullLeaveMessage;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.MediaMessageContent;
import io.rong.message.PublicServiceCommandMessage;
import io.rong.message.ReadReceiptMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import io.rong.push.RongPushClient;

/**
 * Created by lv on 2018/7/6 for EasyApp-master
 */
public class ConversationFragment  extends UriFragment implements AbsListView.OnScrollListener, IExtensionClickListener, IUserInfoProvider, CSEvaluateDialog.EvaluateClickListener {

    private static final String TAG = "ConversationFragment";
    protected PublicServiceProfile mPublicServiceProfile;
    private RongExtension mRongExtension;
    private boolean mEnableMention;
    private float mLastTouchY;
    private boolean mUpDirection;
    private float mOffsetLimit;
    private boolean finishing = false;
    private CSCustomServiceInfo mCustomUserInfo;
    private ConversationInfo mCurrentConversationInfo;
    private String mDraft;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 100;
    private static final int REQUEST_CODE_LOCATION_SHARE = 101;
    private static final int REQUEST_CS_LEAVEL_MESSAGE = 102;
    public static final int SCROLL_MODE_NORMAL = 1;
    public static final int SCROLL_MODE_TOP = 2;
    public static final int SCROLL_MODE_BOTTOM = 3;
    private static final int DEFAULT_HISTORY_MESSAGE_COUNT = 30;
    private static final int DEFAULT_REMOTE_MESSAGE_COUNT = 10;
    private static final int TIP_DEFAULT_MESSAGE_COUNT = 2;
    private static final int SHOW_UNREAD_MESSAGE_COUNT = 10;
    private static final String UN_READ_COUNT = "unReadCount";
    private static final String LIST_STATE = "listState";
    private static final String NEW_MESSAGE_COUNT = "newMessageCount";
    private String mTargetId;
    private Conversation.ConversationType mConversationType;
    private long indexMessageTime;
    private boolean mReadRec;
    private boolean mSyncReadStatus;
    private int mNewMessageCount;
    private int mUnReadCount;
    private AutoRefreshListView mList;
    private LinearLayout mUnreadMsgLayout;
    private TextView mUnreadMsgCountTv;
    private ImageButton mNewMessageBtn;
    private TextView mNewMessageTextView;
    private MessageListAdapter mListAdapter;
    private View mMsgListView;
    private LinearLayout mNotificationContainer;
    private boolean mHasMoreLocalMessagesUp = true;
    private boolean mHasMoreLocalMessagesDown = true;
    private int mLastMentionMsgId;
    private long mSyncReadStatusMsgTime;
    private boolean mCSNeedToQuit = false;
    private List<String> mLocationShareParticipants;
    private CustomServiceConfig mCustomServiceConfig;
    private CSEvaluateDialog mEvaluateDialg;
    private RongKitReceiver mKitReceiver;
    private MessageItemLongClickAction clickAction;
    private OnMoreActionStateListener moreActionStateListener;
    private Bundle mSavedInstanceState;
    private Parcelable mListViewState;
    private final int CS_HUMAN_MODE_CUSTOMER_EXPIRE = 0;
    private final int CS_HUMAN_MODE_SEAT_EXPIRE = 1;
    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (ConversationFragment.this.mListAdapter.getCount() - firstVisibleItem >= ConversationFragment.this.mUnReadCount && ConversationFragment.this.mUnreadMsgLayout != null) {
                TranslateAnimation animation = new TranslateAnimation(0.0F, 700.0F, 0.0F, 0.0F);
                animation.setDuration(700L);
                animation.setFillAfter(true);
                ConversationFragment.this.mUnreadMsgLayout.startAnimation(animation);
                ConversationFragment.this.mUnreadMsgLayout.setClickable(false);
               ConversationFragment.this.mList.removeOnScrollListener(this);
            }

        }
    };
    private boolean robotType = true;
    private long csEnterTime;
    private boolean csEvaluate = true;
    ICustomServiceListener customServiceListener = new ICustomServiceListener() {
        @Override
        public void onSuccess(CustomServiceConfig config) {
           ConversationFragment.this.mCustomServiceConfig = config;
            if (config.isBlack) {
               ConversationFragment.this.onCustomServiceWarning(ConversationFragment.this.getString(R.string.rc_blacklist_prompt), false,ConversationFragment.this.robotType);
            }

            if (config.robotSessionNoEva) {
               ConversationFragment.this.csEvaluate = false;
               ConversationFragment.this.mListAdapter.setEvaluateForRobot(true);
            }

            if (ConversationFragment.this.mRongExtension != null) {
                if (config.evaEntryPoint.equals(CustomServiceConfig.CSEvaEntryPoint.EVA_EXTENSION) &&ConversationFragment.this.mCustomServiceConfig != null) {
                   ConversationFragment.this.mRongExtension.addPlugin(new EvaluatePlugin(ConversationFragment.this.mCustomServiceConfig.isReportResolveStatus));
                }

                if (config.isDisableLocation) {
                    List<IPluginModule> defaultPlugins =ConversationFragment.this.mRongExtension.getPluginModules();
                    IPluginModule location = null;

                    for(int ix = 0; ix < defaultPlugins.size(); ++ix) {
                        if (defaultPlugins.get(ix) instanceof DefaultLocationPlugin) {
                            location = (IPluginModule)defaultPlugins.get(ix);
                        }
                    }

                    if (location != null) {
                       ConversationFragment.this.mRongExtension.removePlugin(location);
                    }
                }
            }

            if (config.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.NONE)) {
                try {
                   ConversationFragment.this.mCSNeedToQuit = RongContext.getInstance().getResources().getBoolean(R.bool.rc_stop_custom_service_when_quit);
                } catch (Resources.NotFoundException var5) {
                    var5.printStackTrace();
                }
            } else {
               ConversationFragment.this.mCSNeedToQuit = config.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.SUSPEND);
            }

            for(int i = 0; i <ConversationFragment.this.mListAdapter.getCount(); ++i) {
                UIMessage uiMessage = (UIMessage)ConversationFragment.this.mListAdapter.getItem(i);
                if (uiMessage.getContent() instanceof CSPullLeaveMessage) {
                    uiMessage.setCsConfig(config);
                }
            }

           ConversationFragment.this.mListAdapter.notifyDataSetChanged();
            if (!TextUtils.isEmpty(config.announceMsg)) {
               ConversationFragment.this.onShowAnnounceView(config.announceMsg, config.announceClickUrl);
            }

        }

        @Override
        public void onError(int code, String msg) {
           ConversationFragment.this.onCustomServiceWarning(msg, false,ConversationFragment.this.robotType);
        }

        @Override
        public void onModeChanged(CustomServiceMode mode) {
            if (ConversationFragment.this.mRongExtension != null &&ConversationFragment.this.isActivityExist()) {
               ConversationFragment.this.mRongExtension.setExtensionBarMode(mode);
                if (!mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN) && !mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN_FIRST)) {
                    if (mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE)) {
                       ConversationFragment.this.csEvaluate = false;
                    }
                } else {
                    if (ConversationFragment.this.mCustomServiceConfig != null &&ConversationFragment.this.mCustomServiceConfig.userTipTime > 0 && !TextUtils.isEmpty(ConversationFragment.this.mCustomServiceConfig.userTipWord)) {
                       ConversationFragment.this.startTimer(0,ConversationFragment.this.mCustomServiceConfig.userTipTime * 60 * 1000);
                    }

                    if (ConversationFragment.this.mCustomServiceConfig != null &&ConversationFragment.this.mCustomServiceConfig.adminTipTime > 0 && !TextUtils.isEmpty(ConversationFragment.this.mCustomServiceConfig.adminTipWord)) {
                       ConversationFragment.this.startTimer(1,ConversationFragment.this.mCustomServiceConfig.adminTipTime * 60 * 1000);
                    }

                   ConversationFragment.this.robotType = false;
                   ConversationFragment.this.csEvaluate = true;
                }

            }
        }

        @Override
        public void onQuit(String msg) {
            RLog.i("ConversationFragment", "CustomService onQuit.");
            if (ConversationFragment.this.getActivity() != null) {
                if (ConversationFragment.this.mCustomServiceConfig != null &&ConversationFragment.this.mCustomServiceConfig.evaEntryPoint.equals(CustomServiceConfig.CSEvaEntryPoint.EVA_END) && !ConversationFragment.this.robotType) {
                   ConversationFragment.this.csQuitEvaluate(msg);
                } else {
                   ConversationFragment.this.csQuit(msg);
                }

            }
        }

        @Override
        public void onPullEvaluation(String dialogId) {
            if (ConversationFragment.this.mEvaluateDialg == null) {
               ConversationFragment.this.onCustomServiceEvaluation(true, dialogId,ConversationFragment.this.robotType,ConversationFragment.this.csEvaluate);
            }

        }

        @Override
        public void onSelectGroup(List<CSGroupItem> groups) {
           ConversationFragment.this.onSelectCustomerServiceGroup(groups);
        }
    };

    public ConversationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSavedInstanceState = savedInstanceState;
        if (savedInstanceState != null) {
            this.mNewMessageCount = savedInstanceState.getInt("newMessageCount");
            this.mListViewState = savedInstanceState.getParcelable("listState");
        }

        RLog.i("ConversationFragment", "onCreate");
        InternalModuleManager.getInstance().onLoaded();

        try {
            this.mEnableMention = this.getActivity().getResources().getBoolean(R.bool.rc_enable_mentioned_message);
        } catch (Resources.NotFoundException var6) {
            RLog.e("ConversationFragment", "rc_enable_mentioned_message not found in rc_config.xml");
        }

        try {
            this.mReadRec = this.getResources().getBoolean(R.bool.rc_read_receipt);
            this.mSyncReadStatus = this.getResources().getBoolean(R.bool.rc_enable_sync_read_status);
        } catch (Resources.NotFoundException var5) {
            RLog.e("ConversationFragment", "rc_read_receipt not found in rc_config.xml");
            var5.printStackTrace();
        }

        this.mKitReceiver = new RongKitReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");

        try {
            this.getActivity().registerReceiver(this.mKitReceiver, filter);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.rc_fr_conversation, container, false);
        this.mRongExtension = (RongExtension)view.findViewById(R.id.rc_extension);
        this.mRongExtension.setFragment(this);
        this.mOffsetLimit = 70.0F * this.getActivity().getResources().getDisplayMetrics().density;
        this.mMsgListView = this.findViewById(view, R.id.rc_layout_msg_list);
        this.mList = (AutoRefreshListView)this.findViewById(this.mMsgListView, R.id.rc_list);
        this.mList.requestDisallowInterceptTouchEvent(true);
        this.mList.setMode(AutoRefreshListView.Mode.BOTH);
        this.mListAdapter = this.onResolveAdapter(this.getActivity());
        this.mList.setAdapter(this.mListAdapter);
        this.mList.setOnRefreshListener(new AutoRefreshListView.OnRefreshListener() {
            @Override
            public void onRefreshFromStart() {
                if (ConversationFragment.this.mHasMoreLocalMessagesUp) {
                   ConversationFragment.this.getHistoryMessage(ConversationFragment.this.mConversationType,ConversationFragment.this.mTargetId, 30, AutoRefreshListView.Mode.START, 1);
                } else {
                   ConversationFragment.this.getRemoteHistoryMessages(ConversationFragment.this.mConversationType,ConversationFragment.this.mTargetId, 10);
                }

            }

            @Override
            public void onRefreshFromEnd() {
                if (ConversationFragment.this.mHasMoreLocalMessagesDown &&ConversationFragment.this.indexMessageTime > 0L) {
                   ConversationFragment.this.getHistoryMessage(ConversationFragment.this.mConversationType,ConversationFragment.this.mTargetId, 30, AutoRefreshListView.Mode.END, 1);
                } else {
                   ConversationFragment.this.mList.onRefreshComplete(0, 0, false);
                }

            }
        });
        this.mList.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 2 &&ConversationFragment.this.mList.getCount() -ConversationFragment.this.mList.getHeaderViewsCount() -ConversationFragment.this.mList.getFooterViewsCount() == 0) {
                    if (ConversationFragment.this.mHasMoreLocalMessagesUp) {
                       ConversationFragment.this.getHistoryMessage(ConversationFragment.this.mConversationType,ConversationFragment.this.mTargetId, 30, AutoRefreshListView.Mode.START, 1);
                    } else if (ConversationFragment.this.mList.getRefreshState() != AutoRefreshListView.State.REFRESHING) {
                       ConversationFragment.this.getRemoteHistoryMessages(ConversationFragment.this.mConversationType,ConversationFragment.this.mTargetId, 10);
                    }

                    return true;
                } else {
                    if (event.getAction() == 1 &&ConversationFragment.this.mRongExtension != null &&ConversationFragment.this.mRongExtension.isExtensionExpanded()) {
                       ConversationFragment.this.mRongExtension.collapseExtension();
                    }

                    return false;
                }
            }
        });
        if (RongContext.getInstance().getNewMessageState()) {
            this.mNewMessageTextView = (TextView)this.findViewById(view, R.id.rc_new_message_number);
            this.mNewMessageBtn = (ImageButton)this.findViewById(view, R.id.rc_new_message_count);
            this.mNewMessageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   ConversationFragment.this.mList.setSelection(ConversationFragment.this.mListAdapter.getCount());
                   ConversationFragment.this.mNewMessageBtn.setVisibility(View.GONE);
                   ConversationFragment.this.mNewMessageTextView.setVisibility(View.GONE);
                   ConversationFragment.this.mNewMessageCount = 0;
                }
            });
        }

        if (RongContext.getInstance().getUnreadMessageState()) {
            this.mUnreadMsgLayout = (LinearLayout)this.findViewById(this.mMsgListView, R.id.rc_unread_message_layout);
            this.mUnreadMsgCountTv = (TextView)this.findViewById(this.mMsgListView, R.id.rc_unread_message_count);
        }

        this.mList.addOnScrollListener(this);
        this.mListAdapter.setOnItemHandlerListener(new MessageListAdapter.OnItemHandlerListener() {
            @Override
            public boolean onWarningViewClick(final int position, final Message data, View v) {
                RongIMClient.getInstance().deleteMessages(new int[]{data.getMessageId()}, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                           ConversationFragment.this.mListAdapter.remove(position);
                            data.setMessageId(0);
                           ConversationFragment.this.onResendItemClick(data);
                        }

                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode e) {
                    }
                });
                return true;
            }

            @Override
            public void onReadReceiptStateClick(Message message) {
               ConversationFragment.this.onReadReceiptStateClick(message);
            }
        });
        this.showNewMessage();
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                view.getWindowVisibleDisplayFrame(r);
                int screenHeight = view.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if ((double)keypadHeight > (double)screenHeight * 0.15D) {
                   ConversationFragment.this.mList.setSelection(ConversationFragment.this.mListAdapter.getCount());
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    if (ConversationFragment.this.mNewMessageCount > 0 &&ConversationFragment.this.mNewMessageBtn != null) {
                       ConversationFragment.this.mNewMessageCount = 0;
                       ConversationFragment.this.mNewMessageBtn.setVisibility(View.GONE);
                       ConversationFragment.this.mNewMessageTextView.setVisibility(View.GONE);
                    }
                }

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.showMoreClickItem()) {
            this.clickAction = (new MessageItemLongClickAction.Builder()).title(this.getResources().getString(R.string.rc_dialog_item_message_more)).actionListener(new MessageItemLongClickAction.MessageItemLongClickListener() {
                @Override
                public boolean onMessageItemLongClick(Context context, UIMessage message) {
                   ConversationFragment.this.setMoreActionState(message);
                    return true;
                }
            }).build();
            RongMessageItemLongClickActionManager.getInstance().addMessageItemLongClickAction(this.clickAction);
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == 1) {
            if (this.mRongExtension != null) {
                this.mRongExtension.collapseExtension();
            }
        } else if (scrollState == 0) {
            int last = this.mList.getLastVisiblePosition();
            if (this.mNewMessageBtn != null && last == this.mList.getCount() - 1) {
                this.mNewMessageCount = 0;
                this.mNewMessageBtn.setVisibility(View.GONE);
                this.mNewMessageTextView.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("unReadCount", this.mUnReadCount);
        outState.putInt("newMessageCount", this.mNewMessageCount);
        outState.putParcelable("listState", this.mList.onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        if (!this.getActivity().isFinishing() && this.mRongExtension != null) {
            RLog.d("ConversationFragment", "onResume when back from other activity.");
            this.mRongExtension.resetEditTextLayoutDrawnStatus();
        }

        RongPushClient.clearAllNotifications(this.getActivity());
        super.onResume();
    }

    @Override
    public final void getUserInfo(String userId, UserInfoCallback callback) {
        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
        if (userInfo != null) {
            callback.onGotUserInfo(userInfo);
        }

    }

    public void setMoreActionStateListener(OnMoreActionStateListener moreActionStateListener) {
        this.moreActionStateListener = moreActionStateListener;
    }

    public void setMoreActionState(UIMessage message) {
        for(int i = 0; i < this.mListAdapter.getCount(); ++i) {
            ((UIMessage)this.mListAdapter.getItem(i)).setChecked(false);
        }

        this.mListAdapter.setMessageCheckedChanged(new MessageListAdapter.OnMessageCheckedChanged() {
            @Override
            public void onCheckedEnable(boolean enable) {
               ConversationFragment.this.mRongExtension.setMoreActionEnable(enable);
            }
        });
        this.mListAdapter.setSelectedCountDidExceed(new MessageListAdapter.OnSelectedCountDidExceed() {
            @Override
            public void onSelectedCountDidExceed() {
               ConversationFragment.this.messageSelectedCountDidExceed();
            }
        });
        this.mRongExtension.showMoreActionLayout(this.getMoreClickActions());
        this.mListAdapter.setShowCheckbox(true);
        message.setChecked(true);
        this.mListAdapter.notifyDataSetChanged();
        if (this.moreActionStateListener != null) {
            this.moreActionStateListener.onShownMoreActionLayout();
        }

    }

    public void resetMoreActionState() {
        this.mRongExtension.hideMoreActionLayout();
        this.mListAdapter.setShowCheckbox(false);
        this.mListAdapter.notifyDataSetChanged();
        if (this.moreActionStateListener != null) {
            this.moreActionStateListener.onHiddenMoreActionLayout();
        }

    }

    public List<IClickActions> getMoreClickActions() {
        List<IClickActions> actions = new ArrayList();
        DeleteClickActions deleteClickActions = new DeleteClickActions();
        actions.add(deleteClickActions);
        return actions;
    }

    public List<Message> getCheckedMessages() {
        return this.mListAdapter != null ? this.mListAdapter.getCheckedMessage() : null;
    }

    public void setMaxMessageSelectedCount(int maxMessageSelectedCount) {
        if (this.mListAdapter != null) {
            this.mListAdapter.setMaxMessageSelectedCount(maxMessageSelectedCount);
        }

    }

    protected void messageSelectedCountDidExceed() {
        Toast.makeText(this.getActivity(), R.string.rc_exceeded_max_limit, Toast.LENGTH_SHORT).show();
    }

    public boolean showMoreClickItem() {
        return false;
    }

    public MessageListAdapter onResolveAdapter(Context context) {
        return new MessageListAdapter(context);
    }

    @Override
    protected void initFragment(Uri uri) {
        this.indexMessageTime = this.getActivity().getIntent().getLongExtra("indexMessageTime", 0L);
        RLog.d("ConversationFragment", "initFragment : " + uri + ",this=" + this + ", time = " + this.indexMessageTime);
        if (uri != null) {
            String typeStr = uri.getLastPathSegment().toUpperCase(Locale.US);
            this.mConversationType = Conversation.ConversationType.valueOf(typeStr);
            this.mTargetId = uri.getQueryParameter("targetId");
            this.mRongExtension.setConversation(this.mConversationType, this.mTargetId);
            RongIMClient.getInstance().getTextMessageDraft(this.mConversationType, this.mTargetId, new RongIMClient.ResultCallback<String>() {
                @Override
                public void onSuccess(String s) {
                   ConversationFragment.this.mDraft = s;
                    if (ConversationFragment.this.mRongExtension != null) {
                        if (!TextUtils.isEmpty(s)) {
                            EditText editText =ConversationFragment.this.mRongExtension.getInputEditText();
                            editText.setText(s);
                            editText.setSelection(editText.length());
                            editText.requestFocus();
                        }

                       ConversationFragment.this.mRongExtension.setExtensionClickListener(ConversationFragment.this);
                    }

                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {
                    if (ConversationFragment.this.mRongExtension != null) {
                       ConversationFragment.this.mRongExtension.setExtensionClickListener(ConversationFragment.this);
                    }

                }
            });
            this.mCurrentConversationInfo = ConversationInfo.obtain(this.mConversationType, this.mTargetId);
            RongContext.getInstance().registerConversationInfo(this.mCurrentConversationInfo);
            this.mNotificationContainer = (LinearLayout)this.mMsgListView.findViewById(R.id.rc_notification_container);
            if (this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE) && this.getActivity() != null && this.getActivity().getIntent() != null && this.getActivity().getIntent().getData() != null) {
                this.mCustomUserInfo = (CSCustomServiceInfo)this.getActivity().getIntent().getParcelableExtra("customServiceInfo");
            }

            LocationManager.getInstance().bindConversation(this.getActivity(), this.mConversationType, this.mTargetId);
            LocationManager.getInstance().setUserInfoProvider(this);
            LocationManager.getInstance().setParticipantChangedListener(new IRealTimeLocationStateListener() {
                private View mRealTimeBar;
                private TextView mRealTimeText;

                @Override
                public void onParticipantChanged(List<String> userIdList) {
                    if (!ConversationFragment.this.isDetached()) {
                        if (this.mRealTimeBar == null) {
                            this.mRealTimeBar =ConversationFragment.this.inflateNotificationView(R.layout.rc_notification_realtime_location);
                            this.mRealTimeText = (TextView)this.mRealTimeBar.findViewById(R.id.real_time_location_text);
                            this.mRealTimeBar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    RealTimeLocationConstant.RealTimeLocationStatus status = RongIMClient.getInstance().getRealTimeLocationCurrentState(ConversationFragment.this.mConversationType,ConversationFragment.this.mTargetId);
                                    if (status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_INCOMING) {
                                        PromptPopupDialog dialog = PromptPopupDialog.newInstance(ConversationFragment.this.getActivity(), "",ConversationFragment.this.getResources().getString(R.string.rc_real_time_join_notification));
                                        dialog.setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
                                            @Override
                                            public void onPositiveButtonClicked() {
                                                int result = LocationManager.getInstance().joinLocationSharing();
                                                if (result == 0) {
                                                    Intent intent = new Intent(ConversationFragment.this.getActivity(), AMapRealTimeActivity.class);
                                                    if (ConversationFragment.this.mLocationShareParticipants != null) {
                                                        intent.putStringArrayListExtra("participants", (ArrayList)ConversationFragment.this.mLocationShareParticipants);
                                                    }

                                                   ConversationFragment.this.startActivity(intent);
                                                } else if (result == 1) {
                                                    Toast.makeText(ConversationFragment.this.getActivity(), R.string.rc_network_exception, Toast.LENGTH_SHORT).show();
                                                } else if (result == 2) {
                                                    Toast.makeText(ConversationFragment.this.getActivity(), R.string.rc_location_sharing_exceed_max, Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                        dialog.show();
                                    } else {
                                        Intent intent = new Intent(ConversationFragment.this.getActivity(), AMapRealTimeActivity.class);
                                        if (ConversationFragment.this.mLocationShareParticipants != null) {
                                            intent.putStringArrayListExtra("participants", (ArrayList)ConversationFragment.this.mLocationShareParticipants);
                                        }

                                       ConversationFragment.this.startActivity(intent);
                                    }

                                }
                            });
                        }

                       ConversationFragment.this.mLocationShareParticipants = userIdList;
                        if (userIdList != null) {
                            if (userIdList.size() == 0) {
                               ConversationFragment.this.hideNotificationView(this.mRealTimeBar);
                            } else {
                                if (userIdList.size() == 1 && userIdList.contains(RongIM.getInstance().getCurrentUserId())) {
                                    this.mRealTimeText.setText(ConversationFragment.this.getResources().getString(R.string.rc_you_are_sharing_location));
                                } else if (userIdList.size() == 1 && !userIdList.contains(RongIM.getInstance().getCurrentUserId())) {
                                    this.mRealTimeText.setText(String.format(ConversationFragment.this.getResources().getString(R.string.rc_other_is_sharing_location),ConversationFragment.this.getNameFromCache((String)userIdList.get(0))));
                                } else {
                                    this.mRealTimeText.setText(String.format(ConversationFragment.this.getResources().getString(R.string.rc_others_are_sharing_location), userIdList.size()));
                                }

                               ConversationFragment.this.showNotificationView(this.mRealTimeBar);
                            }
                        } else {
                           ConversationFragment.this.hideNotificationView(this.mRealTimeBar);
                        }

                    }
                }

                @Override
                public void onErrorException() {
                    if (!ConversationFragment.this.isDetached()) {
                       ConversationFragment.this.hideNotificationView(this.mRealTimeBar);
                        if (ConversationFragment.this.mLocationShareParticipants != null) {
                           ConversationFragment.this.mLocationShareParticipants.clear();
                           ConversationFragment.this.mLocationShareParticipants = null;
                        }
                    }

                }
            });
            if (this.mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
                boolean createIfNotExist = this.isActivityExist() && this.getActivity().getIntent().getBooleanExtra("createIfNotExist", true);
                int pullCount = this.getResources().getInteger(R.integer.rc_chatroom_first_pull_message_count);
                if (createIfNotExist) {
                    RongIMClient.getInstance().joinChatRoom(this.mTargetId, pullCount, new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            RLog.i("ConversationFragment", "joinChatRoom onSuccess : " +ConversationFragment.this.mTargetId);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            RLog.e("ConversationFragment", "joinChatRoom onError : " + errorCode);
                            if (ConversationFragment.this.isActivityExist()) {
                                if (errorCode != RongIMClient.ErrorCode.RC_NET_UNAVAILABLE && errorCode != RongIMClient.ErrorCode.RC_NET_CHANNEL_INVALID) {
                                   ConversationFragment.this.onWarningDialog(ConversationFragment.this.getString(R.string.rc_join_chatroom_failure));
                                } else {
                                   ConversationFragment.this.onWarningDialog(ConversationFragment.this.getString(R.string.rc_notice_network_unavailable));
                                }
                            }

                        }
                    });
                } else {
                    RongIMClient.getInstance().joinExistChatRoom(this.mTargetId, pullCount, new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            RLog.i("ConversationFragment", "joinExistChatRoom onSuccess : " +ConversationFragment.this.mTargetId);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            RLog.e("ConversationFragment", "joinExistChatRoom onError : " + errorCode);
                            if (ConversationFragment.this.isActivityExist()) {
                                if (errorCode != RongIMClient.ErrorCode.RC_NET_UNAVAILABLE && errorCode != RongIMClient.ErrorCode.RC_NET_CHANNEL_INVALID) {
                                   ConversationFragment.this.onWarningDialog(ConversationFragment.this.getString(R.string.rc_join_chatroom_failure));
                                } else {
                                   ConversationFragment.this.onWarningDialog(ConversationFragment.this.getString(R.string.rc_notice_network_unavailable));
                                }
                            }

                        }
                    });
                }
            } else if (this.mConversationType != Conversation.ConversationType.APP_PUBLIC_SERVICE && this.mConversationType != Conversation.ConversationType.PUBLIC_SERVICE) {
                if (this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
                    this.onStartCustomService(this.mTargetId);
                } else if (this.mEnableMention && (this.mConversationType.equals(Conversation.ConversationType.DISCUSSION) || this.mConversationType.equals(Conversation.ConversationType.GROUP))) {
                    RongMentionManager.getInstance().createInstance(this.mConversationType, this.mTargetId, this.mRongExtension.getInputEditText());
                }
            } else {
                PublicServiceCommandMessage msg = new PublicServiceCommandMessage();
                msg.setCommand(PublicServiceMenu.PublicServiceMenuItemType.Entry.getMessage());
                Message message = Message.obtain(this.mTargetId, this.mConversationType, msg);
                RongIMClient.getInstance().sendMessage(message, (String)null, (String)null, new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {
                    }

                    @Override
                    public void onSuccess(Message message) {
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    }
                });
                Conversation.PublicServiceType publicServiceType;
                if (this.mConversationType == Conversation.ConversationType.PUBLIC_SERVICE) {
                    publicServiceType = Conversation.PublicServiceType.PUBLIC_SERVICE;
                } else {
                    publicServiceType = Conversation.PublicServiceType.APP_PUBLIC_SERVICE;
                }

                this.getPublicServiceProfile(publicServiceType, this.mTargetId);
            }
        }

        RongIMClient.getInstance().getConversation(this.mConversationType, this.mTargetId, new RongIMClient.ResultCallback<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                if (conversation != null &&ConversationFragment.this.isActivityExist()) {
                    if (ConversationFragment.this.mSavedInstanceState != null) {
                       ConversationFragment.this.mUnReadCount =ConversationFragment.this.mSavedInstanceState.getInt("unReadCount");
                    } else {
                       ConversationFragment.this.mUnReadCount = conversation.getUnreadMessageCount();
                    }

                    if (ConversationFragment.this.mUnReadCount > 0) {
                        if (ConversationFragment.this.mReadRec &&ConversationFragment.this.mConversationType == Conversation.ConversationType.PRIVATE && RongContext.getInstance().isReadReceiptConversationType(Conversation.ConversationType.PRIVATE)) {
                            RongIMClient.getInstance().sendReadReceiptMessage(ConversationFragment.this.mConversationType,ConversationFragment.this.mTargetId, conversation.getSentTime());
                        }

                        if (ConversationFragment.this.mSyncReadStatus && (!ConversationFragment.this.mReadRec &&ConversationFragment.this.mConversationType == Conversation.ConversationType.PRIVATE ||ConversationFragment.this.mConversationType == Conversation.ConversationType.GROUP ||ConversationFragment.this.mConversationType == Conversation.ConversationType.DISCUSSION)) {
                            RongIMClient.getInstance().syncConversationReadStatus(ConversationFragment.this.mConversationType,ConversationFragment.this.mTargetId, conversation.getSentTime(), (RongIMClient.OperationCallback)null);
                        }
                    }

                    if (conversation.getMentionedCount() > 0) {
                       ConversationFragment.this.getLastMentionedMessageId(ConversationFragment.this.mConversationType,ConversationFragment.this.mTargetId);
                    } else {
                        RongIM.getInstance().clearMessagesUnreadStatus(ConversationFragment.this.mConversationType,ConversationFragment.this.mTargetId, (RongIMClient.ResultCallback)null);
                    }

                    if (ConversationFragment.this.mUnReadCount > 10 &&ConversationFragment.this.mUnreadMsgLayout != null) {
                        if (ConversationFragment.this.mUnReadCount > 150) {
                           ConversationFragment.this.mUnreadMsgCountTv.setText(String.format("%s%s", "150+",ConversationFragment.this.getActivity().getResources().getString(R.string.rc_new_messages)));
                        } else {
                           ConversationFragment.this.mUnreadMsgCountTv.setText(String.format("%s%s",ConversationFragment.this.mUnReadCount,ConversationFragment.this.getActivity().getResources().getString(R.string.rc_new_messages)));
                        }

                       ConversationFragment.this.mUnreadMsgLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               ConversationFragment.this.mUnreadMsgLayout.setClickable(false);
                               ConversationFragment.this.mList.removeOnScrollListener(ConversationFragment.this.mOnScrollListener);
                                TranslateAnimation animation = new TranslateAnimation(0.0F, 500.0F, 0.0F, 0.0F);
                                animation.setDuration(500L);
                               ConversationFragment.this.mUnreadMsgLayout.startAnimation(animation);
                                animation.setFillAfter(true);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                       ConversationFragment.this.mUnreadMsgLayout.setVisibility(View.GONE);
                                        if (ConversationFragment.this.mUnReadCount <= 30) {
                                            if (ConversationFragment.this.mList.getCount() < 30) {
                                               ConversationFragment.this.mList.setSelection(ConversationFragment.this.mListAdapter.getCount() -ConversationFragment.this.mUnReadCount);
                                            } else {
                                               ConversationFragment.this.mList.setSelection(30 -ConversationFragment.this.mUnReadCount);
                                            }
                                        } else if (ConversationFragment.this.mUnReadCount > 30) {
                                           ConversationFragment.this.getHistoryMessage(ConversationFragment.this.mConversationType,ConversationFragment.this.mTargetId,ConversationFragment.this.mUnReadCount - 30, AutoRefreshListView.Mode.START, 2);
                                        }

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                    }
                                });
                            }
                        });
                        TranslateAnimation translateAnimation = new TranslateAnimation(300.0F, 0.0F, 0.0F, 0.0F);
                        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0F, 1.0F);
                        translateAnimation.setDuration(1000L);
                        alphaAnimation.setDuration(2000L);
                        AnimationSet set = new AnimationSet(true);
                        set.addAnimation(translateAnimation);
                        set.addAnimation(alphaAnimation);
                       ConversationFragment.this.mUnreadMsgLayout.setVisibility(View.VISIBLE);
                       ConversationFragment.this.mUnreadMsgLayout.startAnimation(set);
                    }
                }

            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
            }
        });
        AutoRefreshListView.Mode mode = this.indexMessageTime > 0L ? AutoRefreshListView.Mode.END : AutoRefreshListView.Mode.START;
        int scrollMode = this.indexMessageTime > 0L ? 1 : 3;
        this.getHistoryMessage(this.mConversationType, this.mTargetId, 30, mode, scrollMode);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    public void getPublicServiceProfile(Conversation.PublicServiceType publicServiceType, String publicServiceId) {
        RongIM.getInstance().getPublicServiceProfile(publicServiceType, this.mTargetId, new RongIMClient.ResultCallback<PublicServiceProfile>() {
            @Override
            public void onSuccess(PublicServiceProfile publicServiceProfile) {
               ConversationFragment.this.updatePublicServiceMenu(publicServiceProfile);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
            }
        });
    }

    protected void updatePublicServiceMenu(PublicServiceProfile publicServiceProfile) {
        List<InputMenu> inputMenuList = new ArrayList();
        PublicServiceMenu menu = publicServiceProfile.getMenu();
        ArrayList<PublicServiceMenuItem> items = menu != null ? menu.getMenuItems() : null;
        if (items != null && this.mRongExtension != null) {
            this.mPublicServiceProfile = publicServiceProfile;
            Iterator var5 = items.iterator();

            while(var5.hasNext()) {
                PublicServiceMenuItem item = (PublicServiceMenuItem)var5.next();
                InputMenu inputMenu = new InputMenu();
                inputMenu.title = item.getName();
                inputMenu.subMenuList = new ArrayList();
                Iterator var8 = item.getSubMenuItems().iterator();

                while(var8.hasNext()) {
                    PublicServiceMenuItem i = (PublicServiceMenuItem)var8.next();
                    inputMenu.subMenuList.add(i.getName());
                }

                inputMenuList.add(inputMenu);
            }

            this.mRongExtension.setInputMenu(inputMenuList, true);
        }

    }

    public void hideNotificationView(View notificationView) {
        if (notificationView != null) {
            View view = this.mNotificationContainer.findViewById(notificationView.getId());
            if (view != null) {
                this.mNotificationContainer.removeView(view);
                if (this.mNotificationContainer.getChildCount() == 0) {
                    this.mNotificationContainer.setVisibility(View.GONE);
                }
            }

        }
    }

    public void showNotificationView(View notificationView) {
        if (notificationView != null) {
            View view = this.mNotificationContainer.findViewById(notificationView.getId());
            if (view == null) {
                this.mNotificationContainer.addView(notificationView);
                this.mNotificationContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    public View inflateNotificationView(@LayoutRes int layout) {
        return LayoutInflater.from(this.getActivity()).inflate(layout, this.mNotificationContainer, false);
    }

    public void onResendItemClick(Message message) {
        if (message.getContent() instanceof ImageMessage) {
            ImageMessage imageMessage = (ImageMessage)message.getContent();
            if (imageMessage.getRemoteUri() != null && !imageMessage.getRemoteUri().toString().startsWith("file")) {
                RongIM.getInstance().sendMessage(message, (String)null, (String)null, (IRongCallback.ISendMediaMessageCallback)null);
            } else {
                RongIM.getInstance().sendImageMessage(message, (String)null, (String)null, (RongIMClient.SendImageMessageCallback)null);
            }
        } else if (message.getContent() instanceof LocationMessage) {
            RongIM.getInstance().sendLocationMessage(message, (String)null, (String)null, (IRongCallback.ISendMessageCallback)null);
        } else if (message.getContent() instanceof MediaMessageContent) {
            MediaMessageContent mediaMessageContent = (MediaMessageContent)message.getContent();
            if (mediaMessageContent.getMediaUrl() != null) {
                RongIM.getInstance().sendMessage(message, (String)null, (String)null, (IRongCallback.ISendMediaMessageCallback)null);
            } else {
                RongIM.getInstance().sendMediaMessage(message, (String)null, (String)null, (IRongCallback.ISendMediaMessageCallback)null);
            }
        } else {
            RongIM.getInstance().sendMessage(message, (String)null, (String)null, (IRongCallback.ISendMessageCallback)null);
        }

    }

    public void onReadReceiptStateClick(Message message) {
    }

    public void onSelectCustomerServiceGroup(final List<CSGroupItem> groupList) {
        if (!this.isActivityExist()) {
            RLog.w("ConversationFragment", "onSelectCustomerServiceGroup Activity has finished");
        } else {
            List<String> singleDataList = new ArrayList();
            singleDataList.clear();

            for(int i = 0; i < groupList.size(); ++i) {
                if (((CSGroupItem)groupList.get(i)).getOnline()) {
                    singleDataList.add(((CSGroupItem)groupList.get(i)).getName());
                }
            }

            if (singleDataList.size() == 0) {
                RongIMClient.getInstance().selectCustomServiceGroup(this.mTargetId, (String)null);
            } else {
                final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(this.getActivity(), singleDataList);
                singleChoiceDialog.setTitle(this.getActivity().getResources().getString(R.string.rc_cs_select_group));
                singleChoiceDialog.setOnOKButtonListener(new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int selItem = singleChoiceDialog.getSelectItem();
                        RongIMClient.getInstance().selectCustomServiceGroup(ConversationFragment.this.mTargetId, ((CSGroupItem)groupList.get(selItem)).getId());
                    }
                });
                singleChoiceDialog.setOnCancelButtonListener(new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        RongIMClient.getInstance().selectCustomServiceGroup(ConversationFragment.this.mTargetId, (String)null);
                    }
                });
                singleChoiceDialog.show();
            }
        }
    }

    private void csQuit(String msg) {
        if (this.getHandler() != null) {
            this.getHandler().removeCallbacksAndMessages((Object)null);
        }

        if (this.mEvaluateDialg == null) {
            if (this.mCustomServiceConfig != null) {
                this.onCustomServiceWarning(msg, this.mCustomServiceConfig.quitSuspendType == CustomServiceConfig.CSQuitSuspendType.NONE, this.robotType);
            }
        } else {
            this.mEvaluateDialg.destroy();
        }

        if (this.mCustomServiceConfig != null && !this.mCustomServiceConfig.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.NONE)) {
            RongContext.getInstance().getEventBus().post(new Event.CSTerminateEvent(this.getActivity(), msg));
        }

    }

    private void csQuitEvaluateButtonClick(String msg) {
        if (this.mEvaluateDialg != null) {
            this.mEvaluateDialg.destroy();
            this.mEvaluateDialg = null;
        }

        if (this.getHandler() != null) {
            this.getHandler().removeCallbacksAndMessages((Object)null);
        }

        if (this.mEvaluateDialg == null) {
            this.onCustomServiceWarning(msg, false, this.robotType);
        } else {
            this.mEvaluateDialg.destroy();
        }

        if (this.mCustomServiceConfig != null && !this.mCustomServiceConfig.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.NONE)) {
            RongContext.getInstance().getEventBus().post(new Event.CSTerminateEvent(this.getActivity(), msg));
        }

    }

    private void csQuitEvaluate(final String msg) {
        if (this.mEvaluateDialg == null) {
            this.mEvaluateDialg = new CSEvaluateDialog(this.getActivity(), this.mTargetId);
            this.mEvaluateDialg.setClickListener(new CSEvaluateDialog.EvaluateClickListener() {
                @Override
                public void onEvaluateSubmit() {
                   ConversationFragment.this.csQuitEvaluateButtonClick(msg);
                }

                @Override
                public void onEvaluateCanceled() {
                   ConversationFragment.this.csQuitEvaluateButtonClick(msg);
                }
            });
            this.mEvaluateDialg.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (ConversationFragment.this.mEvaluateDialg != null) {
                       ConversationFragment.this.mEvaluateDialg = null;
                    }

                }
            });
            this.mEvaluateDialg.showStar("");
        }

    }

    @Override
    public void onPause() {
        this.finishing = this.getActivity().isFinishing();
        if (this.finishing) {
            this.destroy();
        } else {
            this.stopAudioThingsDependsOnVoipMode();
        }

        super.onPause();
    }

    public void onShowAnnounceView(String announceMsg, String announceUrl) {
    }

    private void destroy() {
        RongIM.getInstance().clearMessagesUnreadStatus(this.mConversationType, this.mTargetId, (RongIMClient.ResultCallback)null);
        if (this.getHandler() != null) {
            this.getHandler().removeCallbacksAndMessages((Object)null);
        }

        if (this.mEnableMention && (this.mConversationType.equals(Conversation.ConversationType.DISCUSSION) || this.mConversationType.equals(Conversation.ConversationType.GROUP))) {
            RongMentionManager.getInstance().destroyInstance(this.mConversationType, this.mTargetId);
        }

        if (this.mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
            SendImageManager.getInstance().cancelSendingImages(this.mConversationType, this.mTargetId);
            RongIM.getInstance().quitChatRoom(this.mTargetId, (RongIMClient.OperationCallback)null);
        }

        if (this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE) && this.mCSNeedToQuit) {
            this.onStopCustomService(this.mTargetId);
        }

        if (this.mSyncReadStatus && this.mSyncReadStatusMsgTime > 0L && (this.mConversationType.equals(Conversation.ConversationType.DISCUSSION) || this.mConversationType.equals(Conversation.ConversationType.GROUP))) {
            RongIMClient.getInstance().syncConversationReadStatus(this.mConversationType, this.mTargetId, this.mSyncReadStatusMsgTime, (RongIMClient.OperationCallback)null);
        }

        EventBus.getDefault().unregister(this);
        this.stopAudioThingsDependsOnVoipMode();

        try {
            if (this.mKitReceiver != null) {
                this.getActivity().unregisterReceiver(this.mKitReceiver);
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        RongContext.getInstance().unregisterConversationInfo(this.mCurrentConversationInfo);
        LocationManager.getInstance().quitLocationSharing();
        LocationManager.getInstance().setParticipantChangedListener((IRealTimeLocationStateListener)null);
        LocationManager.getInstance().setUserInfoProvider((IUserInfoProvider)null);
        LocationManager.getInstance().unBindConversation();
        this.destroyExtension();
    }

    private void stopAudioThingsDependsOnVoipMode() {
        if (!AudioPlayManager.getInstance().isInVOIPMode(this.getActivity())) {
            AudioPlayManager.getInstance().stopPlay();
        }

        AudioRecordManager.getInstance().destroyRecord();
    }

    private void destroyExtension() {
        String text = this.mRongExtension.getInputEditText().getText().toString();
        text = text.trim();
        if (TextUtils.isEmpty(text) && !TextUtils.isEmpty(this.mDraft) || !TextUtils.isEmpty(text) && TextUtils.isEmpty(this.mDraft) || !TextUtils.isEmpty(text) && !TextUtils.isEmpty(this.mDraft) && !text.equals(this.mDraft)) {
            RongIMClient.getInstance().saveTextMessageDraft(this.mConversationType, this.mTargetId, text, (RongIMClient.ResultCallback)null);
            Event.DraftEvent draft = new Event.DraftEvent(this.mConversationType, this.mTargetId, text);
            RongContext.getInstance().getEventBus().post(draft);
        }

        this.mRongExtension.onDestroy();
        this.mRongExtension = null;
    }

    @Override
    public void onDestroy() {
        RongMessageItemLongClickActionManager.getInstance().removeMessageItemLongClickAction(this.clickAction);
        if (!this.finishing) {
            this.destroy();
        }

        super.onDestroy();
    }

    public boolean isLocationSharing() {
        return LocationManager.getInstance().isSharing();
    }

    public void showQuitLocationSharingDialog(final Activity activity) {
        PromptPopupDialog.newInstance(activity, this.getString(R.string.rc_ext_warning), this.getString(R.string.rc_real_time_exit_notification), this.getString(R.string.rc_action_bar_ok)).setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
            @Override
            public void onPositiveButtonClicked() {
                activity.finish();
            }
        }).show();
    }

    @Override
    public boolean onBackPressed() {
        if (this.mRongExtension != null && this.mRongExtension.isExtensionExpanded()) {
            this.mRongExtension.collapseExtension();
            return true;
        } else if (this.mConversationType != null && this.mCustomServiceConfig != null && this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE) && this.mCustomServiceConfig != null && this.mCustomServiceConfig.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.NONE)) {
            return this.onCustomServiceEvaluation(false, "", this.robotType, this.csEvaluate);
        } else if (this.mRongExtension != null && this.mRongExtension.isMoreActionShown()) {
            this.resetMoreActionState();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean handleMessage(android.os.Message msg) {
        InformationNotificationMessage info;
        switch(msg.what) {
            case 0:
                if (this.isActivityExist() && this.mCustomServiceConfig != null) {
                    info = new InformationNotificationMessage(this.mCustomServiceConfig.userTipWord);
                    RongIM.getInstance().insertMessage(Conversation.ConversationType.CUSTOMER_SERVICE, this.mTargetId, this.mTargetId, info, System.currentTimeMillis(), (RongIMClient.ResultCallback)null);
                    return true;
                }

                return true;
            case 1:
                if (this.isActivityExist() && this.mCustomServiceConfig != null) {
                    info = new InformationNotificationMessage(this.mCustomServiceConfig.adminTipWord);
                    RongIM.getInstance().insertMessage(Conversation.ConversationType.CUSTOMER_SERVICE, this.mTargetId, this.mTargetId, info, System.currentTimeMillis(), (RongIMClient.ResultCallback)null);
                    return true;
                }

                return true;
            default:
                return false;
        }
    }

    private boolean isActivityExist() {
        return this.getActivity() != null && !this.getActivity().isFinishing();
    }

    public void onWarningDialog(String msg) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this.getActivity());
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.rc_cs_alert_warning);
        TextView tv = (TextView)window.findViewById(R.id.rc_cs_msg);
        tv.setText(msg);
        window.findViewById(R.id.rc_btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                FragmentManager fm =ConversationFragment.this.getChildFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                   ConversationFragment.this.getActivity().finish();
                }

            }
        });
    }

    public void onCustomServiceWarning(String msg, final boolean evaluate, final boolean robotType) {
        if (!this.isActivityExist()) {
            RLog.w("ConversationFragment", "Activity has finished");
        } else {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this.getActivity());
            builder.setCancelable(false);
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            Window window = alertDialog.getWindow();
            window.setContentView(R.layout.rc_cs_alert_warning);
            TextView tv = (TextView)window.findViewById(R.id.rc_cs_msg);
            tv.setText(msg);
            window.findViewById(R.id.rc_btn_ok).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    @SuppressLint("WrongConstant") InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService("input_method");
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    alertDialog.dismiss();
                    if (evaluate) {
                       ConversationFragment.this.onCustomServiceEvaluation(false, "", robotType, evaluate);
                    } else {
                        FragmentManager fm =ConversationFragment.this.getChildFragmentManager();
                        if (fm.getBackStackEntryCount() > 0) {
                            fm.popBackStack();
                        } else {
                           ConversationFragment.this.getActivity().finish();
                        }
                    }

                }
            });
        }
    }

    public boolean onCustomServiceEvaluation(boolean isPullEva, String dialogId, boolean robotType, boolean evaluate) {
        if (this.isActivityExist()) {
            if (evaluate) {
                long currentTime = System.currentTimeMillis();
                int interval = 60;

                try {
                    interval = this.getActivity().getResources().getInteger(R.integer.rc_custom_service_evaluation_interval);
                } catch (Resources.NotFoundException var10) {
                    var10.printStackTrace();
                }

                if (currentTime - this.csEnterTime < (long)(interval * 1000) && !isPullEva) {
                    @SuppressLint("WrongConstant") InputMethodManager imm = (InputMethodManager)this.getActivity().getSystemService("input_method");
                    if (imm != null && imm.isActive() && this.getActivity().getCurrentFocus() != null && this.getActivity().getCurrentFocus().getWindowToken() != null) {
                        imm.hideSoftInputFromWindow(this.getActivity().getCurrentFocus().getWindowToken(), 2);
                    }

                    FragmentManager fm = this.getChildFragmentManager();
                    if (fm.getBackStackEntryCount() > 0) {
                        fm.popBackStack();
                    } else {
                        this.getActivity().finish();
                    }

                    return false;
                }

                this.mEvaluateDialg = new CSEvaluateDialog(this.getActivity(), this.mTargetId);
                this.mEvaluateDialg.setClickListener(this);
                this.mEvaluateDialg.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (ConversationFragment.this.mEvaluateDialg != null) {
                           ConversationFragment.this.mEvaluateDialg = null;
                        }

                    }
                });
                if (this.mCustomServiceConfig != null && this.mCustomServiceConfig.evaluateType.equals(CustomServiceConfig.CSEvaType.EVA_UNIFIED)) {
                    this.mEvaluateDialg.showStarMessage(this.mCustomServiceConfig.isReportResolveStatus);
                } else if (robotType) {
                    this.mEvaluateDialg.showRobot(true);
                } else {
                    this.onShowStarAndTabletDialog(dialogId);
                }
            } else {
                FragmentManager fm = this.getChildFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    this.getActivity().finish();
                }
            }
        }

        return true;
    }

    public void onShowStarAndTabletDialog(String dialogId) {
        this.mEvaluateDialg.showStar(dialogId);
    }

    @Override
    public void onSendToggleClick(View v, String text) {
        if (!TextUtils.isEmpty(text) && !TextUtils.isEmpty(text.trim())) {
            TextMessage textMessage = TextMessage.obtain(text);
            MentionedInfo mentionedInfo = RongMentionManager.getInstance().onSendButtonClick();
            if (mentionedInfo != null) {
                textMessage.setMentionedInfo(mentionedInfo);
            }

            Message message = Message.obtain(this.mTargetId, this.mConversationType, textMessage);
            RongIM.getInstance().sendMessage(message, (String)null, (String)null, (IRongCallback.ISendMessageCallback)null);
        } else {
            RLog.e("ConversationFragment", "text content must not be null");
        }
    }

    @Override
    public void onImageResult(List<Uri> selectedImages, boolean origin) {
        SendImageManager.getInstance().sendImages(this.mConversationType, this.mTargetId, selectedImages, origin);
        if (this.mConversationType.equals(Conversation.ConversationType.PRIVATE)) {
            RongIMClient.getInstance().sendTypingStatus(this.mConversationType, this.mTargetId, "RC:ImgMsg");
        }

    }

    @Override
    public void onEditTextClick(EditText editText) {
    }

    @Override
    public void onLocationResult(double lat, double lng, String poi, Uri thumb) {
        LocationMessage locationMessage = LocationMessage.obtain(lat, lng, poi, thumb);
        Message message = Message.obtain(this.mTargetId, this.mConversationType, locationMessage);
        RongIM.getInstance().sendLocationMessage(message, (String)null, (String)null, (IRongCallback.ISendMessageCallback)null);
        if (this.mConversationType.equals(Conversation.ConversationType.PRIVATE)) {
            RongIMClient.getInstance().sendTypingStatus(this.mConversationType, this.mTargetId, "RC:LBSMsg");
        }

    }

    @Override
    public void onSwitchToggleClick(View v, ViewGroup inputBoard) {
        if (this.robotType) {
            RongIMClient.getInstance().switchToHumanMode(this.mTargetId);
        }

    }

    @Override
    public void onVoiceInputToggleTouch(View v, MotionEvent event) {
        String[] permissions = new String[]{"android.permission.RECORD_AUDIO"};
        if (!PermissionCheckUtil.checkPermissions(this.getActivity(), permissions)) {
            if (event.getAction() == 0) {
                PermissionCheckUtil.requestPermissions(this, permissions, 100);
            }

        } else {
            if (event.getAction() == 0) {
                if (AudioPlayManager.getInstance().isPlaying()) {
                    AudioPlayManager.getInstance().stopPlay();
                }

                if (!AudioPlayManager.getInstance().isInNormalMode(this.getActivity())) {
                    Toast.makeText(this.getActivity(), this.getActivity().getString(R.string.rc_voip_occupying), Toast.LENGTH_SHORT).show();
                    return;
                }

                AudioRecordManager.getInstance().startRecord(v.getRootView(), this.mConversationType, this.mTargetId);
                this.mLastTouchY = event.getY();
                this.mUpDirection = false;
                ((Button)v).setText(R.string.rc_audio_input_hover);
            } else if (event.getAction() == 2) {
                if (this.mLastTouchY - event.getY() > this.mOffsetLimit && !this.mUpDirection) {
                    AudioRecordManager.getInstance().willCancelRecord();
                    this.mUpDirection = true;
                    ((Button)v).setText(R.string.rc_audio_input);
                } else if (event.getY() - this.mLastTouchY > -this.mOffsetLimit && this.mUpDirection) {
                    AudioRecordManager.getInstance().continueRecord();
                    this.mUpDirection = false;
                    ((Button)v).setText(R.string.rc_audio_input_hover);
                }
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                AudioRecordManager.getInstance().stopRecord();
                ((Button)v).setText(R.string.rc_audio_input);
            }

            if (this.mConversationType.equals(Conversation.ConversationType.PRIVATE)) {
                RongIMClient.getInstance().sendTypingStatus(this.mConversationType, this.mTargetId, "RC:VcMsg");
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100 && grantResults[0] != 0) {
            this.mRongExtension.showRequestPermissionFailedAlter(this.getResources().getString(R.string.rc_permission_grant_needed));
        } else {
            this.mRongExtension.onRequestPermissionResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    public void onEmoticonToggleClick(View v, ViewGroup extensionBoard) {
    }

    @Override
    public void onPluginToggleClick(View v, ViewGroup extensionBoard) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int cursor;
        int offset;
        if (count == 0) {
            cursor = start + before;
            offset = -before;
        } else {
            cursor = start;
            offset = count;
        }

        if (!this.mConversationType.equals(Conversation.ConversationType.GROUP) && !this.mConversationType.equals(Conversation.ConversationType.DISCUSSION)) {
            if (this.mConversationType.equals(Conversation.ConversationType.PRIVATE) && offset != 0) {
                RongIMClient.getInstance().sendTypingStatus(this.mConversationType, this.mTargetId, "RC:TxtMsg");
            }
        } else {
            RongMentionManager.getInstance().onTextEdit(this.mConversationType, this.mTargetId, cursor, offset, s.toString());
        }

    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getKeyCode() == 67 && event.getAction() == 0) {
            EditText editText = (EditText)v;
            int cursorPos = editText.getSelectionStart();
            RongMentionManager.getInstance().onDeleteClick(this.mConversationType, this.mTargetId, editText, cursorPos);
        }

        return false;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onMenuClick(int root, int sub) {
        if (this.mPublicServiceProfile != null) {
            PublicServiceMenuItem item = (PublicServiceMenuItem)this.mPublicServiceProfile.getMenu().getMenuItems().get(root);
            if (sub >= 0) {
                item = (PublicServiceMenuItem)item.getSubMenuItems().get(sub);
            }

            if (item.getType().equals(PublicServiceMenu.PublicServiceMenuItemType.View)) {
                IPublicServiceMenuClickListener menuClickListener = RongContext.getInstance().getPublicServiceMenuClickListener();
                if (menuClickListener == null || !menuClickListener.onClick(this.mConversationType, this.mTargetId, item)) {
                    String action = "io.rong.imkit.intent.action.webview";
                    Intent intent = new Intent(action);
                    intent.setPackage(this.getActivity().getPackageName());
                    intent.addFlags(268435456);
                    intent.putExtra("url", item.getUrl());
                    this.getActivity().startActivity(intent);
                }
            }

            PublicServiceCommandMessage msg = PublicServiceCommandMessage.obtain(item);
            RongIMClient.getInstance().sendMessage(this.mConversationType, this.mTargetId, msg, (String)null, (String)null, new IRongCallback.ISendMessageCallback() {
                @Override
                public void onAttached(Message message) {
                }

                @Override
                public void onSuccess(Message message) {
                }

                @Override
                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                }
            });
        }

    }

    @Override
    public void onPluginClicked(IPluginModule pluginModule, int position) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            this.getActivity().finish();
        } else {
            this.mRongExtension.onActivityPluginResult(requestCode, resultCode, data);
        }

    }

    private String getNameFromCache(String targetId) {
        UserInfo info = RongContext.getInstance().getUserInfoFromCache(targetId);
        return info == null ? targetId : info.getName();
    }

    public void onEventMainThread(Event.ReadReceiptRequestEvent event) {
        RLog.d("ConversationFragment", "ReadReceiptRequestEvent");
        if ((this.mConversationType.equals(Conversation.ConversationType.GROUP) || this.mConversationType.equals(Conversation.ConversationType.DISCUSSION)) && RongContext.getInstance().isReadReceiptConversationType(event.getConversationType()) && event.getConversationType().equals(this.mConversationType) && event.getTargetId().equals(this.mTargetId)) {
            for(int i = 0; i < this.mListAdapter.getCount(); ++i) {
                if (((UIMessage)this.mListAdapter.getItem(i)).getUId().equals(event.getMessageUId())) {
                    final UIMessage uiMessage = (UIMessage)this.mListAdapter.getItem(i);
                    ReadReceiptInfo readReceiptInfo = uiMessage.getReadReceiptInfo();
                    if (readReceiptInfo == null) {
                        readReceiptInfo = new ReadReceiptInfo();
                        uiMessage.setReadReceiptInfo(readReceiptInfo);
                    }

                    if (readReceiptInfo.isReadReceiptMessage() && readReceiptInfo.hasRespond()) {
                        return;
                    }

                    readReceiptInfo.setIsReadReceiptMessage(true);
                    readReceiptInfo.setHasRespond(false);
                    List<Message> messageList = new ArrayList();
                    messageList.add(((UIMessage)this.mListAdapter.getItem(i)).getMessage());
                    RongIMClient.getInstance().sendReadReceiptResponse(event.getConversationType(), event.getTargetId(), messageList, new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            uiMessage.getReadReceiptInfo().setHasRespond(true);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            RLog.e("ConversationFragment", "sendReadReceiptResponse failed, errorCode = " + errorCode);
                        }
                    });
                    break;
                }
            }
        }

    }

    public void onEventMainThread(Event.ReadReceiptResponseEvent event) {
        RLog.d("ConversationFragment", "ReadReceiptResponseEvent");
        if ((this.mConversationType.equals(Conversation.ConversationType.GROUP) || this.mConversationType.equals(Conversation.ConversationType.DISCUSSION)) && RongContext.getInstance().isReadReceiptConversationType(event.getConversationType()) && event.getConversationType().equals(this.mConversationType) && event.getTargetId().equals(this.mTargetId)) {
            for(int i = 0; i < this.mListAdapter.getCount(); ++i) {
                if (((UIMessage)this.mListAdapter.getItem(i)).getUId().equals(event.getMessageUId())) {
                    UIMessage uiMessage = (UIMessage)this.mListAdapter.getItem(i);
                    ReadReceiptInfo readReceiptInfo = uiMessage.getReadReceiptInfo();
                    if (readReceiptInfo == null) {
                        readReceiptInfo = new ReadReceiptInfo();
                        readReceiptInfo.setIsReadReceiptMessage(true);
                        uiMessage.setReadReceiptInfo(readReceiptInfo);
                    }

                    readReceiptInfo.setRespondUserIdList(event.getResponseUserIdList());
                    int first = this.mList.getFirstVisiblePosition();
                    int last = this.mList.getLastVisiblePosition();
                    int position = this.getPositionInListView(i);
                    if (position >= first && position <= last) {
                        this.mListAdapter.getView(i, this.getListViewChildAt(i), this.mList);
                    }
                    break;
                }
            }
        }

    }

    public void onEventMainThread(Event.MessageDeleteEvent deleteEvent) {
        RLog.d("ConversationFragment", "MessageDeleteEvent");
        if (deleteEvent.getMessageIds() != null) {
            Iterator var2 = deleteEvent.getMessageIds().iterator();

            while(var2.hasNext()) {
                long messageId = (long)(Integer)var2.next();
                int position = this.mListAdapter.findPosition(messageId);
                if (position >= 0) {
                    this.mListAdapter.remove(position);
                }
            }

            this.mListAdapter.notifyDataSetChanged();
        }

    }

    public void onEventMainThread(Event.PublicServiceFollowableEvent event) {
        RLog.d("ConversationFragment", "PublicServiceFollowableEvent");
        if (event != null && !event.isFollow()) {
            this.getActivity().finish();
        }

    }

    public void onEventMainThread(Event.MessagesClearEvent clearEvent) {
        RLog.d("ConversationFragment", "MessagesClearEvent");
        if (clearEvent.getTargetId().equals(this.mTargetId) && clearEvent.getType().equals(this.mConversationType)) {
            this.mListAdapter.clear();
            this.mListAdapter.notifyDataSetChanged();
        }

    }

    public void onEventMainThread(Event.MessageRecallEvent event) {
        RLog.d("ConversationFragment", "MessageRecallEvent");
        if (event.isRecallSuccess()) {
            RecallNotificationMessage recallNotificationMessage = event.getRecallNotificationMessage();
            int position = this.mListAdapter.findPosition((long)event.getMessageId());
            if (position != -1) {
                UIMessage uiMessage = (UIMessage)this.mListAdapter.getItem(position);
                if (uiMessage.getMessage().getContent() instanceof VoiceMessage) {
                    AudioPlayManager.getInstance().stopPlay();
                }

                if (uiMessage.getMessage().getContent() instanceof FileMessage) {
                    RongIM.getInstance().cancelDownloadMediaMessage(uiMessage.getMessage(), (RongIMClient.OperationCallback)null);
                }

                ((UIMessage)this.mListAdapter.getItem(position)).setContent(recallNotificationMessage);
                int first = this.mList.getFirstVisiblePosition();
                int last = this.mList.getLastVisiblePosition();
                int listPos = this.getPositionInListView(position);
                if (listPos >= first && listPos <= last) {
                    this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
                }
            }
        } else {
            Toast.makeText(this.getActivity(), R.string.rc_recall_failed, Toast.LENGTH_SHORT).show();
        }

    }

    public void onEventMainThread(Event.RemoteMessageRecallEvent event) {
        RLog.d("ConversationFragment", "RemoteMessageRecallEvent");
        int position = this.mListAdapter.findPosition((long)event.getMessageId());
        int first = this.mList.getFirstVisiblePosition();
        int last = this.mList.getLastVisiblePosition();
        if (position >= 0) {
            UIMessage uiMessage = (UIMessage)this.mListAdapter.getItem(position);
            if (uiMessage.getMessage().getContent() instanceof VoiceMessage) {
                AudioPlayManager.getInstance().stopPlay();
            }

            if (uiMessage.getMessage().getContent() instanceof FileMessage) {
                RongIM.getInstance().cancelDownloadMediaMessage(uiMessage.getMessage(), (RongIMClient.OperationCallback)null);
            }

            uiMessage.setContent(event.getRecallNotificationMessage());
            int listPos = this.getPositionInListView(position);
            if (listPos >= first && listPos <= last) {
                this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
            }
        }

    }

    public void onEventMainThread(Message msg) {
        RLog.d("ConversationFragment", "Event message : " + msg.getMessageId() + ", " + msg.getObjectName() + ", " + msg.getSentStatus());
        if (this.mTargetId.equals(msg.getTargetId()) && this.mConversationType.equals(msg.getConversationType()) && msg.getMessageId() > 0) {
            int position = this.mListAdapter.findPosition((long)msg.getMessageId());
            if (position >= 0) {
                ((UIMessage)this.mListAdapter.getItem(position)).setMessage(msg);
                this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
            } else {
                UIMessage uiMessage = UIMessage.obtain(msg);
                if (msg.getContent() instanceof CSPullLeaveMessage) {
                    uiMessage.setCsConfig(this.mCustomServiceConfig);
                }

                long sentTime = uiMessage.getSentTime();
                if (uiMessage.getMessageDirection() == Message.MessageDirection.SEND && uiMessage.getSentStatus() == Message.SentStatus.SENDING) {
                    sentTime = uiMessage.getSentTime() - RongIMClient.getInstance().getDeltaTime();
                    uiMessage.setSentTime(sentTime);
                }

                int insertPosition = this.mListAdapter.getPositionBySendTime(sentTime);
                this.mListAdapter.add(uiMessage, insertPosition);
                this.mListAdapter.notifyDataSetChanged();
            }

            MessageTag msgTag = (MessageTag)msg.getContent().getClass().getAnnotation(MessageTag.class);
            if (this.mNewMessageCount <= 0 && msgTag != null && msgTag.flag() == 3) {
                this.mList.setTranscriptMode(2);
                this.mList.post(new Runnable() {
                    @Override
                    public void run() {
                        if (ConversationFragment.this.getActivity() != null &&ConversationFragment.this.mList != null) {
                           ConversationFragment.this.mList.setSelection(ConversationFragment.this.mListAdapter.getCount());
                        }

                    }
                });
                this.mList.setTranscriptMode(0);
            }

            if (this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE) && msg.getMessageDirection() == Message.MessageDirection.SEND && !this.robotType && this.mCustomServiceConfig != null && this.mCustomServiceConfig.userTipTime > 0 && !TextUtils.isEmpty(this.mCustomServiceConfig.userTipWord)) {
                this.startTimer(0, this.mCustomServiceConfig.userTipTime * 60 * 1000);
            }
        }

    }

    public void onEventMainThread(Event.FileMessageEvent event) {
        Message msg = event.getMessage();
        RLog.d("ConversationFragment", "FileMessageEvent message : " + msg.getMessageId() + ", " + msg.getObjectName() + ", " + msg.getSentStatus());
        if (this.mTargetId.equals(msg.getTargetId()) && this.mConversationType.equals(msg.getConversationType()) && msg.getMessageId() > 0 && msg.getContent() instanceof MediaMessageContent) {
            int position = this.mListAdapter.findPosition((long)msg.getMessageId());
            if (position >= 0) {
                UIMessage uiMessage = (UIMessage)this.mListAdapter.getItem(position);
                uiMessage.setMessage(msg);
                uiMessage.setProgress(event.getProgress());
                if (msg.getContent() instanceof FileMessage) {
                    ((FileMessage)msg.getContent()).progress = event.getProgress();
                }

                ((UIMessage)this.mListAdapter.getItem(position)).setMessage(msg);
            }
        }

    }

    public void onEventMainThread(GroupUserInfo groupUserInfo) {
        RLog.d("ConversationFragment", "GroupUserInfoEvent " + groupUserInfo.getGroupId() + " " + groupUserInfo.getUserId() + " " + groupUserInfo.getNickname());
        if (groupUserInfo.getNickname() != null && groupUserInfo.getGroupId() != null) {
            int count = this.mListAdapter.getCount();
            int first = this.mList.getFirstVisiblePosition();
            int last = this.mList.getLastVisiblePosition();

            for(int i = 0; i < count; ++i) {
                UIMessage uiMessage = (UIMessage)this.mListAdapter.getItem(i);
                if (uiMessage.getSenderUserId().equals(groupUserInfo.getUserId())) {
                    uiMessage.setNickName(true);
                    UserInfo userInfo = uiMessage.getUserInfo();
                    if (userInfo != null) {
                        userInfo.setName(groupUserInfo.getNickname());
                        uiMessage.setUserInfo(userInfo);
                    }

                    int pos = this.getPositionInListView(i);
                    if (pos >= first && pos <= last) {
                        this.mListAdapter.getView(i, this.getListViewChildAt(i), this.mList);
                    }
                }
            }

        }
    }

    private View getListViewChildAt(int adapterIndex) {
        int header = this.mList.getHeaderViewsCount();
        int first = this.mList.getFirstVisiblePosition();
        return this.mList.getChildAt(adapterIndex + header - first);
    }

    private int getPositionInListView(int adapterIndex) {
        int header = this.mList.getHeaderViewsCount();
        return adapterIndex + header;
    }

    private int getPositionInAdapter(int listIndex) {
        int header = this.mList.getHeaderViewsCount();
        return listIndex <= 0 ? 0 : listIndex - header;
    }

    private void showNewMessage() {
        if (this.mNewMessageCount > 0 && this.mNewMessageBtn != null) {
            this.mNewMessageBtn.setVisibility(View.VISIBLE);
            if (this.mNewMessageTextView != null) {
                this.mNewMessageTextView.setVisibility(View.VISIBLE);
                if (this.mNewMessageCount > 99) {
                    this.mNewMessageTextView.setText("99+");
                } else {
                    this.mNewMessageTextView.setText(this.mNewMessageCount + "");
                }
            }
        }

    }

    public void onEventMainThread(Event.OnMessageSendErrorEvent event) {
        this.onEventMainThread(event.getMessage());
    }

    public void onEventMainThread(Event.OnReceiveMessageEvent event) {
        Message message = event.getMessage();
        RLog.i("ConversationFragment", "OnReceiveMessageEvent, " + message.getMessageId() + ", " + message.getObjectName() + ", " + message.getReceivedStatus().toString());
        Conversation.ConversationType conversationType = message.getConversationType();
        String targetId = message.getTargetId();
        if (this.mConversationType.equals(conversationType) && this.mTargetId.equals(targetId) && this.shouldUpdateMessage(message, event.getLeft())) {
            if (event.getLeft() == 0 && message.getConversationType().equals(Conversation.ConversationType.PRIVATE) && RongContext.getInstance().isReadReceiptConversationType(Conversation.ConversationType.PRIVATE) && message.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                if (this.mReadRec && !TextUtils.isEmpty(message.getUId())) {
                    RongIMClient.getInstance().sendReadReceiptMessage(message.getConversationType(), message.getTargetId(), message.getSentTime());
                }

                if (!this.mReadRec && this.mSyncReadStatus) {
                    RongIMClient.getInstance().syncConversationReadStatus(message.getConversationType(), message.getTargetId(), message.getSentTime(), (RongIMClient.OperationCallback)null);
                }
            }

            if (this.mSyncReadStatus) {
                this.mSyncReadStatusMsgTime = message.getSentTime();
            }

            if (message.getMessageId() > 0) {
                if (!SystemUtils.isInBackground(this.getActivity())) {
                    message.getReceivedStatus().setRead();
                    RongIMClient.getInstance().setMessageReceivedStatus(message.getMessageId(), message.getReceivedStatus(), (RongIMClient.ResultCallback)null);
                    if (message.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                        UnReadMessageManager.getInstance().onMessageReceivedStatusChanged();
                    }
                }

                if (this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE) && !this.robotType && this.mCustomServiceConfig != null && this.mCustomServiceConfig.adminTipTime > 0 && !TextUtils.isEmpty(this.mCustomServiceConfig.adminTipWord)) {
                    this.startTimer(1, this.mCustomServiceConfig.adminTipTime * 60 * 1000);
                }
            }

            RLog.d("ConversationFragment", "mList.getCount(): " + this.mList.getCount() + " getLastVisiblePosition:" + this.mList.getLastVisiblePosition());
            if (this.mNewMessageBtn != null && this.mList.getCount() - this.mList.getLastVisiblePosition() > 2 && Message.MessageDirection.SEND != message.getMessageDirection() && message.getConversationType() != Conversation.ConversationType.CHATROOM && message.getConversationType() != Conversation.ConversationType.CUSTOMER_SERVICE && message.getConversationType() != Conversation.ConversationType.APP_PUBLIC_SERVICE && message.getConversationType() != Conversation.ConversationType.PUBLIC_SERVICE) {
                ++this.mNewMessageCount;
                this.showNewMessage();
            }

            this.onEventMainThread(event.getMessage());
        }

    }

    public void onEventBackgroundThread(final Event.PlayAudioEvent event) {
        this.getHandler().post(new Runnable() {
            @Override
            public void run() {
               ConversationFragment.this.handleAudioPlayEvent(event);
            }
        });
    }

    private void handleAudioPlayEvent(Event.PlayAudioEvent event) {
        RLog.i("ConversationFragment", "PlayAudioEvent");
        int first = this.mList.getFirstVisiblePosition();
        int last = this.mList.getLastVisiblePosition();
        int position = this.mListAdapter.findPosition((long)event.messageId);
        if (event.continuously && position >= 0) {
            while(first <= last) {
                ++position;
                ++first;
                UIMessage uiMessage = (UIMessage)this.mListAdapter.getItem(position);
                if (uiMessage != null && uiMessage.getContent() instanceof VoiceMessage && uiMessage.getMessageDirection().equals(Message.MessageDirection.RECEIVE) && !uiMessage.getReceivedStatus().isListened()) {
                    uiMessage.continuePlayAudio = true;
                    this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
                    break;
                }
            }
        }

    }

    public void onEventMainThread(Event.OnReceiveMessageProgressEvent event) {
        if (this.mList != null) {
            int first = this.mList.getFirstVisiblePosition();

            for(int last = this.mList.getLastVisiblePosition(); first <= last; ++first) {
                int position = this.getPositionInAdapter(first);
                UIMessage uiMessage = (UIMessage)this.mListAdapter.getItem(position);
                if (uiMessage.getMessageId() == event.getMessage().getMessageId()) {
                    uiMessage.setProgress(event.getProgress());
                    if (this.isResumed()) {
                        this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
                    }
                    break;
                }
            }
        }

    }

    public void onEventMainThread(Event.ConnectEvent event) {
        RLog.i("ConversationFragment", "ConnectEvent : " + event.getConnectStatus());
        if (this.mListAdapter.getCount() == 0) {
            AutoRefreshListView.Mode mode = this.indexMessageTime > 0L ? AutoRefreshListView.Mode.END : AutoRefreshListView.Mode.START;
            int scrollMode = this.indexMessageTime > 0L ? 1 : 3;
            this.getHistoryMessage(this.mConversationType, this.mTargetId, 30, mode, scrollMode);
        }

    }

    public void onEventMainThread(UserInfo userInfo) {
        RLog.i("ConversationFragment", "userInfo " + userInfo.getUserId());
        int first = this.mList.getFirstVisiblePosition();
        int last = this.mList.getLastVisiblePosition();

        for(int i = 0; i < this.mListAdapter.getCount(); ++i) {
            UIMessage uiMessage = (UIMessage)this.mListAdapter.getItem(i);
            if (userInfo.getUserId().equals(uiMessage.getSenderUserId()) && !uiMessage.isNickName()) {
                if (uiMessage.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE) && uiMessage.getMessage() != null && uiMessage.getMessage().getContent() != null && uiMessage.getMessage().getContent().getUserInfo() != null) {
                    uiMessage.setUserInfo(uiMessage.getMessage().getContent().getUserInfo());
                } else {
                    uiMessage.setUserInfo(userInfo);
                }

                int position = this.getPositionInListView(i);
                if (position >= first && position <= last) {
                    this.mListAdapter.getView(i, this.getListViewChildAt(i), this.mList);
                }
            }
        }

    }

    public void onEventMainThread(PublicServiceProfile publicServiceProfile) {
        RLog.i("ConversationFragment", "publicServiceProfile");
        if (publicServiceProfile != null && this.mConversationType.equals(publicServiceProfile.getConversationType()) && this.mTargetId.equals(publicServiceProfile.getTargetId())) {
            int first = this.mList.getFirstVisiblePosition();

            for(int last = this.mList.getLastVisiblePosition(); first <= last; ++first) {
                int position = this.getPositionInAdapter(first);
                UIMessage message = (UIMessage)this.mListAdapter.getItem(position);
                if (message != null && (TextUtils.isEmpty(message.getTargetId()) || publicServiceProfile.getTargetId().equals(message.getTargetId()))) {
                    this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
                }
            }

            this.updatePublicServiceMenu(publicServiceProfile);
        }

    }

    public void onEventMainThread(Event.ReadReceiptEvent event) {
        RLog.i("ConversationFragment", "ReadReceiptEvent");
        if (RongContext.getInstance().isReadReceiptConversationType(event.getMessage().getConversationType()) && this.mTargetId.equals(event.getMessage().getTargetId()) && this.mConversationType.equals(event.getMessage().getConversationType()) && event.getMessage().getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
            ReadReceiptMessage content = (ReadReceiptMessage)event.getMessage().getContent();
            long ntfTime = content.getLastMessageSendTime();

            for(int i = this.mListAdapter.getCount() - 1; i >= 0; --i) {
                UIMessage uiMessage = (UIMessage)this.mListAdapter.getItem(i);
                if (uiMessage.getMessageDirection().equals(Message.MessageDirection.SEND) && uiMessage.getSentStatus() == Message.SentStatus.SENT && ntfTime >= uiMessage.getSentTime()) {
                    uiMessage.setSentStatus(Message.SentStatus.READ);
                    int first = this.mList.getFirstVisiblePosition();
                    int last = this.mList.getLastVisiblePosition();
                    int position = this.getPositionInListView(i);
                    if (position >= first && position <= last) {
                        this.mListAdapter.getView(i, this.getListViewChildAt(i), this.mList);
                    }
                }
            }
        }

    }

    public MessageListAdapter getMessageAdapter() {
        return this.mListAdapter;
    }

    public boolean shouldUpdateMessage(Message message, int left) {
        return true;
    }

    public void getHistoryMessage(Conversation.ConversationType conversationType, String targetId, int lastMessageId, int reqCount,ConversationFragment.LoadMessageDirection direction, final IHistoryDataResultCallback<List<Message>> callback) {
        if (direction ==ConversationFragment.LoadMessageDirection.UP) {
            RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, lastMessageId, reqCount, new RongIMClient.ResultCallback<List<Message>>() {
                @Override
                public void onSuccess(List<Message> messages) {
                    if (callback != null) {
                        callback.onResult(messages);
                    }

                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {
                    RLog.e("ConversationFragment", "getHistoryMessages " + e);
                    if (callback != null) {
                        callback.onResult(null);
                    }

                }
            });
        } else {
            int before = 10;
            int after = 10;
            if (this.mListAdapter.getCount() > 0) {
                after = 30;
                before = 0;
            }

            RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, this.indexMessageTime, before, after, new RongIMClient.ResultCallback<List<Message>>() {
                @Override
                public void onSuccess(List<Message> messages) {
                    if (callback != null) {
                        callback.onResult(messages);
                    }

                    if (messages != null && messages.size() > 0 &&ConversationFragment.this.mHasMoreLocalMessagesDown) {
                       ConversationFragment.this.indexMessageTime = ((Message)messages.get(0)).getSentTime();
                    } else {
                       ConversationFragment.this.indexMessageTime = 0L;
                    }

                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {
                    RLog.e("ConversationFragment", "getHistoryMessages " + e);
                    if (callback != null) {
                        callback.onResult(null);
                    }

                   ConversationFragment.this.indexMessageTime = 0L;
                }
            });
        }

    }

    private void getHistoryMessage(Conversation.ConversationType conversationType, String targetId, final int reqCount, AutoRefreshListView.Mode mode, final int scrollMode) {
        this.mList.onRefreshStart(mode);
        if (conversationType.equals(Conversation.ConversationType.CHATROOM)) {
            this.mList.onRefreshComplete(0, 0, false);
            RLog.w("ConversationFragment", "Should not get local message in chatroom");
        } else {
            final int last = this.mListAdapter.getCount() == 0 ? -1 : ((UIMessage)this.mListAdapter.getItem(0)).getMessageId();
            final ConversationFragment.LoadMessageDirection direction = mode == AutoRefreshListView.Mode.START ?ConversationFragment.LoadMessageDirection.UP :ConversationFragment.LoadMessageDirection.DOWN;
            this.getHistoryMessage(conversationType, targetId, last, reqCount, direction, new IHistoryDataResultCallback<List<Message>>() {
                @Override
                public void onResult(List<Message> messages) {
                    int msgCount = messages == null ? 0 : messages.size();
                    RLog.i("ConversationFragment", "getHistoryMessage " + msgCount);
                    if (direction ==ConversationFragment.LoadMessageDirection.DOWN) {
                       ConversationFragment.this.mList.onRefreshComplete(msgCount > 1 ? msgCount : 0, msgCount, false);
                       ConversationFragment.this.mHasMoreLocalMessagesDown = msgCount > 1;
                    } else {
                       ConversationFragment.this.mList.onRefreshComplete(msgCount, reqCount, false);
                       ConversationFragment.this.mHasMoreLocalMessagesUp = msgCount == reqCount;
                    }

                    if (messages != null && messages.size() > 0) {
                        int index = 0;
                        if (direction ==ConversationFragment.LoadMessageDirection.DOWN) {
                            index =ConversationFragment.this.mListAdapter.getCount();
                        }

                        boolean needRefresh = false;
                        Iterator var5 = messages.iterator();

                        while(var5.hasNext()) {
                            Message message = (Message)var5.next();
                            boolean contains = false;

                            for(int i = 0; i <ConversationFragment.this.mListAdapter.getCount(); ++i) {
                                contains = ((UIMessage)ConversationFragment.this.mListAdapter.getItem(i)).getMessageId() == message.getMessageId();
                                if (contains) {
                                    break;
                                }
                            }

                            if (!contains) {
                                UIMessage uiMessage = UIMessage.obtain(message);
                                if (message.getContent() != null && message.getContent().getUserInfo() != null) {
                                    uiMessage.setUserInfo(message.getContent().getUserInfo());
                                }

                                if (message.getContent() instanceof CSPullLeaveMessage) {
                                    uiMessage.setCsConfig(ConversationFragment.this.mCustomServiceConfig);
                                }

                               ConversationFragment.this.mListAdapter.add(uiMessage, index);
                                needRefresh = true;
                            }
                        }

                        if (needRefresh) {
                           ConversationFragment.this.mListAdapter.notifyDataSetChanged();
                            if (ConversationFragment.this.mLastMentionMsgId > 0) {
                                index =ConversationFragment.this.mListAdapter.findPosition((long)ConversationFragment.this.mLastMentionMsgId);
                               ConversationFragment.this.mList.setSelection(index);
                               ConversationFragment.this.mLastMentionMsgId = 0;
                            } else if (2 == scrollMode) {
                               ConversationFragment.this.mList.setSelection(0);
                            } else if (scrollMode == 3) {
                                if (last == -1 &&ConversationFragment.this.mSavedInstanceState != null) {
                                   ConversationFragment.this.mList.onRestoreInstanceState(ConversationFragment.this.mListViewState);
                                } else {
                                   ConversationFragment.this.mList.setSelection(ConversationFragment.this.mListAdapter.getCount());
                                }
                            } else if (direction ==ConversationFragment.LoadMessageDirection.DOWN) {
                                int selected =ConversationFragment.this.mList.getSelectedItemPosition();
                                if (selected <= 0) {
                                    for(int ix = 0; ix <ConversationFragment.this.mListAdapter.getCount(); ++ix) {
                                        if (((UIMessage)ConversationFragment.this.mListAdapter.getItem(ix)).getSentTime() ==ConversationFragment.this.indexMessageTime) {
                                           ConversationFragment.this.mList.setSelection(ix);
                                            break;
                                        }
                                    }
                                } else {
                                   ConversationFragment.this.mList.setSelection(ConversationFragment.this.mListAdapter.getCount() - messages.size());
                                }
                            } else {
                               ConversationFragment.this.mList.setSelection(messages.size() + 1);
                            }

                           ConversationFragment.this.sendReadReceiptResponseIfNeeded(messages);
                            if (last == -1 &&ConversationFragment.this.mUnReadCount > 10) {
                               ConversationFragment.this.mList.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                       ConversationFragment.this.mList.addOnScrollListener(ConversationFragment.this.mOnScrollListener);
                                    }
                                }, 100L);
                            }
                        }
                    }

                }

                @Override
                public void onError() {
                   ConversationFragment.this.mList.onRefreshComplete(reqCount, reqCount, false);
                }
            });
        }
    }

    public void getRemoteHistoryMessages(Conversation.ConversationType conversationType, String targetId, long dateTime, int reqCount, final IHistoryDataResultCallback<List<Message>> callback) {
        RongIMClient.getInstance().getRemoteHistoryMessages(conversationType, targetId, dateTime, reqCount, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                if (callback != null) {
                    callback.onResult(messages);
                }

            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                RLog.e("ConversationFragment", "getRemoteHistoryMessages " + e);
                if (callback != null) {
                    callback.onResult(null);
                }

            }
        });
    }

    private void getRemoteHistoryMessages(Conversation.ConversationType conversationType, String targetId, final int reqCount) {
        this.mList.onRefreshStart(AutoRefreshListView.Mode.START);
        if (this.mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
            this.mList.onRefreshComplete(0, 0, false);
            RLog.w("ConversationFragment", "Should not get remote message in chatroom");
        } else {
            long dateTime = this.mListAdapter.getCount() == 0 ? 0L : ((UIMessage)this.mListAdapter.getItem(0)).getSentTime();
            this.getRemoteHistoryMessages(conversationType, targetId, dateTime, reqCount, new IHistoryDataResultCallback<List<Message>>() {
                @Override
                public void onResult(List<Message> messages) {
                    RLog.i("ConversationFragment", "getRemoteHistoryMessages " + (messages == null ? 0 : messages.size()));
                    Message lastMessage = null;
                    if (messages != null && messages.size() > 0) {
                        if (ConversationFragment.this.mListAdapter.getCount() == 0) {
                            lastMessage = (Message)messages.get(0);
                        }

                        List<UIMessage> remoteListx = new ArrayList();
                        Iterator var4 = messages.iterator();

                        while(var4.hasNext()) {
                            Message message = (Message)var4.next();
                            if (message.getMessageId() > 0) {
                                UIMessage uiMessage = UIMessage.obtain(message);
                                if (message.getContent() instanceof CSPullLeaveMessage) {
                                    uiMessage.setCsConfig(ConversationFragment.this.mCustomServiceConfig);
                                }

                                if (message.getContent() != null && message.getContent().getUserInfo() != null) {
                                    uiMessage.setUserInfo(message.getContent().getUserInfo());
                                }

                                remoteListx.add(uiMessage);
                            }
                        }

                        List<UIMessage> remoteList =ConversationFragment.this.filterMessage(remoteListx);
                        if (remoteList != null && remoteList.size() > 0) {
                            var4 = remoteList.iterator();

                            while(var4.hasNext()) {
                                UIMessage uiMessagex = (UIMessage)var4.next();
                                uiMessagex.setSentStatus(Message.SentStatus.READ);
                               ConversationFragment.this.mListAdapter.add(uiMessagex, 0);
                            }

                           ConversationFragment.this.mListAdapter.notifyDataSetChanged();
                           ConversationFragment.this.mList.setSelection(messages.size() + 1);
                           ConversationFragment.this.sendReadReceiptResponseIfNeeded(messages);
                           ConversationFragment.this.mList.onRefreshComplete(messages.size(), reqCount, false);
                            if (lastMessage != null) {
                                RongContext.getInstance().getEventBus().post(lastMessage);
                            }
                        } else {
                           ConversationFragment.this.mList.onRefreshComplete(0, reqCount, false);
                        }
                    } else {
                       ConversationFragment.this.mList.onRefreshComplete(0, reqCount, false);
                    }

                }

                @Override
                public void onError() {
                   ConversationFragment.this.mList.onRefreshComplete(0, reqCount, false);
                }
            });
        }
    }

    private List<UIMessage> filterMessage(List<UIMessage> srcList) {
        Object destList;
        if (this.mListAdapter.getCount() > 0) {
            destList = new ArrayList();

            for(int i = 0; i < this.mListAdapter.getCount(); ++i) {
                Iterator var4 = srcList.iterator();

                while(var4.hasNext()) {
                    UIMessage msg = (UIMessage)var4.next();
                    if (!((List)destList).contains(msg) && msg.getMessageId() != ((UIMessage)this.mListAdapter.getItem(i)).getMessageId()) {
                        ((List)destList).add(msg);
                    }
                }
            }
        } else {
            destList = srcList;
        }

        return (List)destList;
    }

    private void getLastMentionedMessageId(Conversation.ConversationType conversationType, String targetId) {
        RongIMClient.getInstance().getUnreadMentionedMessages(conversationType, targetId, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                if (messages != null && messages.size() > 0) {
                   ConversationFragment.this.mLastMentionMsgId = ((Message)messages.get(0)).getMessageId();
                    int index =ConversationFragment.this.mListAdapter.findPosition((long)ConversationFragment.this.mLastMentionMsgId);
                    RLog.i("ConversationFragment", "getLastMentionedMessageId " +ConversationFragment.this.mLastMentionMsgId + " " + index);
                    if (ConversationFragment.this.mLastMentionMsgId > 0 && index >= 0) {
                       ConversationFragment.this.mList.setSelection(index);
                       ConversationFragment.this.mLastMentionMsgId = 0;
                    }
                }

                RongIM.getInstance().clearMessagesUnreadStatus(ConversationFragment.this.mConversationType,ConversationFragment.this.mTargetId, (RongIMClient.ResultCallback)null);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                RongIM.getInstance().clearMessagesUnreadStatus(ConversationFragment.this.mConversationType,ConversationFragment.this.mTargetId, (RongIMClient.ResultCallback)null);
            }
        });
    }

    private void sendReadReceiptResponseIfNeeded(List<Message> messages) {
        if (this.mReadRec && (this.mConversationType.equals(Conversation.ConversationType.GROUP) || this.mConversationType.equals(Conversation.ConversationType.DISCUSSION)) && RongContext.getInstance().isReadReceiptConversationType(this.mConversationType)) {
            List<Message> responseMessageList = new ArrayList();
            Iterator var3 = messages.iterator();

            while(var3.hasNext()) {
                Message message = (Message)var3.next();
                ReadReceiptInfo readReceiptInfo = message.getReadReceiptInfo();
                if (readReceiptInfo != null && readReceiptInfo.isReadReceiptMessage() && !readReceiptInfo.hasRespond()) {
                    responseMessageList.add(message);
                }
            }

            if (responseMessageList.size() > 0) {
                RongIMClient.getInstance().sendReadReceiptResponse(this.mConversationType, this.mTargetId, responseMessageList, (RongIMClient.OperationCallback)null);
            }
        }

    }

    @Override
    public void onExtensionCollapsed() {
    }

    @Override
    public void onExtensionExpanded(int h) {
        if (this.indexMessageTime > 0L) {
            this.mListAdapter.clear();
            this.indexMessageTime = 0L;
            this.getHistoryMessage(this.mConversationType, this.mTargetId, 30, AutoRefreshListView.Mode.START, 1);
        } else {
            this.mList.setSelection(this.mListAdapter.getCount());
            if (this.mNewMessageCount > 0) {
                this.mNewMessageCount = 0;
                this.mNewMessageBtn.setVisibility(View.GONE);
                this.mNewMessageTextView.setVisibility(View.GONE);
            }
        }

    }



    public void onStartCustomService(String targetId) {
        this.csEnterTime = System.currentTimeMillis();
        this.mRongExtension.setExtensionBarMode(CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE);
        RongIMClient.getInstance().startCustomService(targetId, this.customServiceListener, this.mCustomUserInfo);
    }

    public void onStopCustomService(String targetId) {
        RongIMClient.getInstance().stopCustomService(targetId);
    }

    @Override
    public final void onEvaluateSubmit() {
        if (this.mEvaluateDialg != null) {
            this.mEvaluateDialg.destroy();
            this.mEvaluateDialg = null;
        }

        if (this.mCustomServiceConfig != null && this.mCustomServiceConfig.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.NONE)) {
            this.getActivity().finish();
        }

    }

    @Override
    public final void onEvaluateCanceled() {
        if (this.mEvaluateDialg != null) {
            this.mEvaluateDialg.destroy();
            this.mEvaluateDialg = null;
        }

        if (this.mCustomServiceConfig != null && this.mCustomServiceConfig.quitSuspendType.equals(CustomServiceConfig.CSQuitSuspendType.NONE)) {
            this.getActivity().finish();
        }

    }

    private void startTimer(int event, int interval) {
        this.getHandler().removeMessages(event);
        this.getHandler().sendEmptyMessageDelayed(event, (long)interval);
    }

    private void stopTimer(int event) {
        this.getHandler().removeMessages(event);
    }

    public Conversation.ConversationType getConversationType() {
        return this.mConversationType;
    }

    public String getTargetId() {
        return this.mTargetId;
    }

    protected static enum LoadMessageDirection {
        DOWN,
        UP;

        private LoadMessageDirection() {
        }
    }

}
