package io.rong.imkit.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.rong.common.RLog;
import io.rong.imkit.InputBar;
import io.rong.imkit.InputMenu;
import io.rong.imkit.R;
import io.rong.imkit.actions.IClickActions;
import io.rong.imkit.actions.IMoreClickAdapter;
import io.rong.imkit.actions.MoreClickAdapter;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.emoticon.EmoticonTabAdapter;
import io.rong.imkit.emoticon.IEmoticonClickListener;
import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imkit.menu.ISubMenuItemClickListener;
import io.rong.imkit.menu.InputSubMenu;


import io.rong.imkit.utilities.ExtensionHistoryUtil;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.CustomServiceMode;

/**
 * Created by lv on 2018/7/6 for EasyApp-master
 */
public class RongExtension extends LinearLayout {
    private static final String TAG = "RongExtension";
    private ImageView mPSMenu;
    private View mPSDivider;
    private List<InputMenu> mInputMenuList;
    private LinearLayout mMainBar;
    private ViewGroup mExtensionBar;
    private ViewGroup mSwitchLayout;
    private ViewGroup mContainerLayout;
    private ViewGroup mPluginLayout;
    private ViewGroup mMenuContainer;
    private View mEditTextLayout;
    private EditText mEditText;
    private View mVoiceInputToggle;
    private PluginAdapter mPluginAdapter;
    private EmoticonTabAdapter mEmotionTabAdapter;
    private IMoreClickAdapter moreClickAdapter;
    private FrameLayout mSendToggle;
    private ImageView mEmoticonToggle;
    private ImageView mPluginToggle;
    private ImageView mVoiceToggle;
    private OnClickListener mVoiceToggleClickListener;
    private Fragment mFragment;
    private IExtensionClickListener mExtensionClickListener;
    private Conversation.ConversationType mConversationType;
    private String mTargetId;
    private List<IExtensionModule> mExtensionModuleList;
    private InputBar.Style mStyle;
    private RongExtension.VisibilityState lastState;
    private boolean hasEverDrawn;
    private String mUserId;
    boolean isKeyBoardActive;
    boolean collapsed;
    int originalTop;
    int originalBottom;

    public RongExtension(Context context) {
        super(context);
        this.lastState = RongExtension.VisibilityState.EXTENSION_VISIBLE;
        this.hasEverDrawn = false;
        this.isKeyBoardActive = false;
        this.collapsed = true;
        this.originalTop = 0;
        this.originalBottom = 0;
        this.initView();
        this.initData();
    }

    public RongExtension(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.lastState = VisibilityState.EXTENSION_VISIBLE;
        this.hasEverDrawn = false;
        this.isKeyBoardActive = false;
        this.collapsed = true;
        this.originalTop = 0;
        this.originalBottom = 0;
        TypedArray a = context.obtainStyledAttributes(attrs, io.rong.imkit.R.styleable.RongExtension);
        int attr = a.getInt(io.rong.imkit.R.styleable.RongExtension_RCStyle, 291);
        a.recycle();
        this.initView();
        this.initData();
        this.mStyle = InputBar.Style.getStyle(attr);
        if (this.mStyle != null) {
            this.setInputBarStyle(this.mStyle);
        }

    }

    public void onDestroy() {
        RLog.d("RongExtension", "onDestroy");
        Iterator var1 = this.mExtensionModuleList.iterator();

        while (var1.hasNext()) {
            IExtensionModule module = (IExtensionModule) var1.next();
            module.onDetachedFromExtension();
        }

        this.mExtensionClickListener = null;
        this.hideInputKeyBoard();
    }

    public void collapseExtension() {
        this.hidePluginBoard();
        this.hideEmoticonBoard();
        this.hideInputKeyBoard();
    }

    public void showSoftInput() {
        this.showInputKeyBoard();
        this.mContainerLayout.setSelected(true);
    }

    public boolean isExtensionExpanded() {
        return this.mPluginAdapter != null && this.mPluginAdapter.getVisibility() == 0 || this.mEmotionTabAdapter != null && this.mEmotionTabAdapter.getVisibility() == 0;
    }

    public void setInputBarStyle(InputBar.Style style) {
        switch (style) {
            case STYLE_SWITCH_CONTAINER_EXTENSION:
                this.setSCE();
                break;
            case STYLE_CONTAINER:
                this.setC();
                break;
            case STYLE_CONTAINER_EXTENSION:
                this.setCE();
                break;
            case STYLE_EXTENSION_CONTAINER:
                this.setEC();
                break;
            case STYLE_SWITCH_CONTAINER:
                this.setSC();
        }

    }

    public void setConversation(Conversation.ConversationType conversationType, String targetId) {
        if (this.mConversationType == null && this.mTargetId == null) {
            this.mConversationType = conversationType;
            this.mTargetId = targetId;
            Iterator var3 = this.mExtensionModuleList.iterator();

            while (var3.hasNext()) {
                IExtensionModule module = (IExtensionModule) var3.next();
                module.onAttachedToExtension(this);
            }

            this.initPlugins();
            this.initEmoticonTabs();
            this.initPanelStyle();
        }

        this.mConversationType = conversationType;
        this.mTargetId = targetId;
    }

    private void initPlugins() {
        Iterator var1 = this.mExtensionModuleList.iterator();

        while (var1.hasNext()) {
            IExtensionModule module = (IExtensionModule) var1.next();
            List<IPluginModule> pluginModules = module.getPluginModules(this.mConversationType);
            if (pluginModules != null && this.mPluginAdapter != null) {
                this.mPluginAdapter.addPlugins(pluginModules);
            }
        }

    }

    private void initEmoticonTabs() {
        Iterator var1 = this.mExtensionModuleList.iterator();

        while (var1.hasNext()) {
            IExtensionModule module = (IExtensionModule) var1.next();
            List<IEmoticonTab> tabs = module.getEmoticonTabs();
            this.mEmotionTabAdapter.initTabs(tabs, module.getClass().getCanonicalName());
        }

    }

    public void setInputMenu(List<InputMenu> inputMenuList, boolean showFirst) {
        if (inputMenuList != null && inputMenuList.size() > 0) {
            this.mPSMenu.setVisibility(View.VISIBLE);
            this.mPSDivider.setVisibility(View.VISIBLE);
            this.mInputMenuList = inputMenuList;
            if (showFirst) {
                this.setExtensionBarVisibility(8);
                this.setMenuVisibility(0, inputMenuList);
            }

        } else {
            RLog.e("RongExtension", "setInputMenu no item");
        }
    }

    private void setExtensionBarVisibility(int visibility) {
        if (visibility == View.GONE) {
            this.hideEmoticonBoard();
            this.hidePluginBoard();
            this.hideInputKeyBoard();
        }

        this.mExtensionBar.setVisibility(visibility);
    }

    private void setMenuVisibility(int visibility, List<InputMenu> inputMenuList) {
        if (this.mMenuContainer == null) {
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            this.mMenuContainer = (ViewGroup) inflater.inflate(io.rong.imkit.R.layout.rc_ext_menu_container, (ViewGroup) null);
            this.mMenuContainer.findViewById(io.rong.imkit.R.id.rc_switch_to_keyboard).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setExtensionBarVisibility(View.VISIBLE);
                    mMenuContainer.setVisibility(View.GONE);
                }
            });

            for (int i = 0; i < inputMenuList.size(); ++i) {
                final InputMenu menu = (InputMenu) inputMenuList.get(i);
                LinearLayout rootMenu = (LinearLayout) inflater.inflate(R.layout.rc_ext_root_menu_item, (ViewGroup) null);
                LayoutParams lp = new LayoutParams(-1, -1, 1.0F);
                rootMenu.setLayoutParams(lp);
                TextView title = (TextView) rootMenu.findViewById(io.rong.imkit.R.id.rc_menu_title);
                title.setText(menu.title);
                ImageView iv = (ImageView) rootMenu.findViewById(io.rong.imkit.R.id.rc_menu_icon);
                if (menu.subMenuList != null && menu.subMenuList.size() > 0) {
                    iv.setVisibility(View.VISIBLE);
                    iv.setImageResource(io.rong.imkit.R.drawable.rc_menu_trangle);
                }

                final int finalI = i;
                rootMenu.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<String> subMenuList = menu.subMenuList;
                        if (subMenuList != null && subMenuList.size() > 0) {
                            InputSubMenu subMenu = new InputSubMenu(RongExtension.this.getContext(), subMenuList);
                            subMenu.setOnItemClickListener(new ISubMenuItemClickListener() {
                                @Override
                                public void onClick(int index) {
                                    if (RongExtension.this.mExtensionClickListener != null) {
                                        RongExtension.this.mExtensionClickListener.onMenuClick(finalI, index);
                                    }

                                }
                            });
                            subMenu.showAtLocation(v);
                        } else if (RongExtension.this.mExtensionClickListener != null) {
                            RongExtension.this.mExtensionClickListener.onMenuClick(finalI, -1);
                        }

                    }
                });
                ViewGroup menuBar = (ViewGroup) this.mMenuContainer.findViewById(R.id.rc_menu_bar);
                menuBar.addView(rootMenu);
            }

            this.addView(this.mMenuContainer);
        }

        if (visibility == View.GONE) {
            this.mMenuContainer.setVisibility(View.GONE);
        } else {
            this.mMenuContainer.setVisibility(View.VISIBLE);
        }

    }

    public void setMenuVisibility(int visibility) {
        if (this.mMenuContainer != null) {
            this.mMenuContainer.setVisibility(visibility);
        }

    }

    public int getMenuVisibility() {
        return this.mMenuContainer != null ? this.mMenuContainer.getVisibility() : 8;
    }

    public void setExtensionBarMode(CustomServiceMode mode) {
        switch (mode) {
            case CUSTOM_SERVICE_MODE_NO_SERVICE:
                this.setC();
                break;
            case CUSTOM_SERVICE_MODE_HUMAN:
            case CUSTOM_SERVICE_MODE_HUMAN_FIRST:
                if (this.mStyle != null) {
                    this.setInputBarStyle(this.mStyle);
                }

                this.mVoiceToggle.setImageResource(R.drawable.rc_voice_toggle_selector);
                this.mVoiceToggle.setOnClickListener(this.mVoiceToggleClickListener);
                break;
            case CUSTOM_SERVICE_MODE_ROBOT:
                this.setC();
                break;
            case CUSTOM_SERVICE_MODE_ROBOT_FIRST:
                this.mVoiceToggle.setImageResource(R.drawable.rc_cs_admin_selector);
                this.mVoiceToggle.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (RongExtension.this.mExtensionClickListener != null) {
                            RongExtension.this.mExtensionClickListener.onSwitchToggleClick(v, RongExtension.this.mContainerLayout);
                        }

                    }
                });
                this.setSC();
        }

    }

    public EditText getInputEditText() {
        return this.mEditText;
    }

    public void refreshEmoticonTabIcon(IEmoticonTab tab, Drawable icon) {
        if (icon != null && this.mEmotionTabAdapter != null && tab != null) {
            this.mEmotionTabAdapter.refreshTabIcon(tab, icon);
        }

    }

    public void addPlugin(IPluginModule pluginModule) {
        if (pluginModule != null) {
            this.mPluginAdapter.addPlugin(pluginModule);
        }

    }

    public void removePlugin(IPluginModule pluginModule) {
        if (pluginModule != null) {
            this.mPluginAdapter.removePlugin(pluginModule);
        }

    }

    public List<IPluginModule> getPluginModules() {
        return this.mPluginAdapter.getPluginModules();
    }

    public void addPluginPager(View v) {
        if (null != this.mPluginAdapter) {
            this.mPluginAdapter.addPager(v);
        }

    }

    public void removePluginPager(View v) {
        if (this.mPluginAdapter != null && v != null) {
            this.mPluginAdapter.removePager(v);
        }

    }

    public boolean addEmoticonTab(int index, IEmoticonTab tab, String tag) {
        if (this.mEmotionTabAdapter != null && tab != null && !TextUtils.isEmpty(tag)) {
            return this.mEmotionTabAdapter.addTab(index, tab, tag);
        } else {
            RLog.e("RongExtension", "addEmoticonTab Failure");
            return false;
        }
    }

    public void addEmoticonTab(IEmoticonTab tab, String tag) {
        if (this.mEmotionTabAdapter != null && tab != null && !TextUtils.isEmpty(tag)) {
            this.mEmotionTabAdapter.addTab(tab, tag);
        }

    }

    public List<IEmoticonTab> getEmoticonTabs(String tag) {
        return this.mEmotionTabAdapter != null && !TextUtils.isEmpty(tag) ? this.mEmotionTabAdapter.getTagTabs(tag) : null;
    }

    public int getEmoticonTabIndex(String tag) {
        return this.mEmotionTabAdapter != null && !TextUtils.isEmpty(tag) ? this.mEmotionTabAdapter.getTagTabIndex(tag) : -1;
    }

    public boolean removeEmoticonTab(IEmoticonTab tab, String tag) {
        boolean result = false;
        if (this.mEmotionTabAdapter != null && tab != null && !TextUtils.isEmpty(tag)) {
            result = this.mEmotionTabAdapter.removeTab(tab, tag);
        }

        return result;
    }

    public void setCurrentEmoticonTab(IEmoticonTab tab, String tag) {
        if (this.mEmotionTabAdapter != null && tab != null && !TextUtils.isEmpty(tag)) {
            this.mEmotionTabAdapter.setCurrentTab(tab, tag);
        }

    }

    public void setEmoticonTabBarEnable(boolean enable) {
        if (this.mEmotionTabAdapter != null) {
            this.mEmotionTabAdapter.setTabViewEnable(enable);
        }

    }

    public void setEmoticonTabBarAddEnable(boolean enable) {
        if (this.mEmotionTabAdapter != null) {
            this.mEmotionTabAdapter.setAddEnable(enable);
        }

    }

    public void setEmoticonTabBarAddClickListener(IEmoticonClickListener listener) {
        if (this.mEmotionTabAdapter != null) {
            this.mEmotionTabAdapter.setOnEmoticonClickListener(listener);
        }

    }

    public void addEmoticonExtraTab(Context context, Drawable drawable, OnClickListener clickListener) {
        if (this.mEmotionTabAdapter != null) {
            this.mEmotionTabAdapter.addExtraTab(context, drawable, clickListener);
        }

    }

    public void setFragment(Fragment fragment) {
        this.mFragment = fragment;
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public Conversation.ConversationType getConversationType() {
        return this.mConversationType;
    }

    public String getTargetId() {
        return this.mTargetId;
    }

    public void setExtensionClickListener(IExtensionClickListener clickListener) {
        this.mExtensionClickListener = clickListener;
    }

    public void onActivityPluginResult(int requestCode, int resultCode, Intent data) {
        int position = (requestCode >> 8) - 1;
        int reqCode = requestCode & 255;
        IPluginModule pluginModule = this.mPluginAdapter.getPluginModule(position);
        if (pluginModule != null) {
            if (this.mExtensionClickListener != null && resultCode == -1) {
                if (pluginModule instanceof ImagePlugin) {
                    boolean sendOrigin = data.getBooleanExtra("sendOrigin", false);
                    ArrayList<Uri> list = data.getParcelableArrayListExtra("android.intent.extra.RETURN_RESULT");
                    this.mExtensionClickListener.onImageResult(list, sendOrigin);
                } else if (pluginModule instanceof DefaultLocationPlugin || pluginModule instanceof CombineLocationPlugin) {
                    double lat = data.getDoubleExtra("lat", 0.0D);
                    double lng = data.getDoubleExtra("lng", 0.0D);
                    String poi = data.getStringExtra("poi");
                    String thumb = data.getStringExtra("thumb");
                    this.mExtensionClickListener.onLocationResult(lat, lng, poi, Uri.parse(thumb));
                }
            }

            pluginModule.onActivityResult(reqCode, resultCode, data);
        }

    }

    public boolean onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int position = (requestCode >> 8) - 1;
        int reqCode = requestCode & 255;
        IPluginModule pluginModule = this.mPluginAdapter.getPluginModule(position);
        return pluginModule instanceof IPluginRequestPermissionResultCallback ? ((IPluginRequestPermissionResultCallback) pluginModule).onRequestPermissionResult(this.mFragment, this, reqCode, permissions, grantResults) : false;
    }

    public void startActivityForPluginResult(Intent intent, int requestCode, IPluginModule pluginModule) {
        if ((requestCode & -256) != 0) {
            throw new IllegalArgumentException("requestCode must less than 256.");
        } else {
            int position = this.mPluginAdapter.getPluginPosition(pluginModule);
            this.mFragment.startActivityForResult(intent, (position + 1 << 8) + (requestCode & 255));
        }
    }

    public void requestPermissionForPluginResult(String[] permissions, int requestCode, IPluginModule pluginModule) {
        if ((requestCode & -256) != 0) {
            throw new IllegalArgumentException("requestCode must less than 256");
        } else {
            int position = this.mPluginAdapter.getPluginPosition(pluginModule);
            int req = (position + 1 << 8) + (requestCode & 255);
            PermissionCheckUtil.requestPermissions(this.mFragment, permissions, req);
        }
    }

    private void initData() {
        this.mExtensionModuleList = RongExtensionManager.getInstance().getExtensionModules();
        this.mPluginAdapter = new PluginAdapter();
        this.mPluginAdapter.setOnPluginClickListener(new IPluginClickListener() {
            @Override
            public void onClick(IPluginModule pluginModule, int position) {
                if (RongExtension.this.mExtensionClickListener != null) {
                    RongExtension.this.mExtensionClickListener.onPluginClicked(pluginModule, position);
                }

                pluginModule.onClick(RongExtension.this.mFragment, RongExtension.this);
            }
        });
        this.mEmotionTabAdapter = new EmoticonTabAdapter();
        this.moreClickAdapter = new MoreClickAdapter();
        this.mUserId = RongIMClient.getInstance().getCurrentUserId();

        try {
            boolean enable = this.getResources().getBoolean(this.getResources().getIdentifier("rc_extension_history", "bool", this.getContext().getPackageName()));
            ExtensionHistoryUtil.setEnableHistory(enable);
            ExtensionHistoryUtil.addExceptConversationType(Conversation.ConversationType.CUSTOMER_SERVICE);
        } catch (Resources.NotFoundException var2) {
            RLog.e("RongExtension", "rc_extension_history not configure in rc_configuration.xml");
            var2.printStackTrace();
        }

    }

    private void initView() {
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(this.getContext().getResources().getColor(R.color.rc_extension_normal));
        this.mExtensionBar = (ViewGroup) LayoutInflater.from(this.getContext()).inflate(R.layout.rc_ext_extension_bar, (ViewGroup) null);
        this.mMainBar = (LinearLayout) this.mExtensionBar.findViewById(R.id.ext_main_bar);
        this.mSwitchLayout = (ViewGroup) this.mExtensionBar.findViewById(R.id.rc_switch_layout);
        this.mContainerLayout = (ViewGroup) this.mExtensionBar.findViewById(R.id.rc_container_layout);
        this.mPluginLayout = (ViewGroup) this.mExtensionBar.findViewById(R.id.rc_plugin_layout);
        this.mEditTextLayout = LayoutInflater.from(this.getContext()).inflate(R.layout.rc_ext_input_edit_text, (ViewGroup) null);
        this.mEditTextLayout.setVisibility(View.VISIBLE);
        this.mContainerLayout.addView(this.mEditTextLayout);
        LayoutInflater.from(this.getContext()).inflate(R.layout.rc_ext_voice_input, this.mContainerLayout, true);
        this.mVoiceInputToggle = this.mContainerLayout.findViewById(R.id.rc_audio_input_toggle);
        this.mVoiceInputToggle.setVisibility(View.GONE);
        this.mEditText = (EditText) this.mExtensionBar.findViewById(R.id.rc_edit_text);
        this.mSendToggle = (FrameLayout) this.mExtensionBar.findViewById(R.id.rc_send_toggle);
        this.mPluginToggle = (ImageView) this.mExtensionBar.findViewById(R.id.rc_plugin_toggle);
        this.mEditText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (0 == event.getAction()) {
                    if (RongExtension.this.mExtensionClickListener != null) {
                        RongExtension.this.mExtensionClickListener.onEditTextClick(RongExtension.this.mEditText);
                    }

                    if (Build.BRAND.toLowerCase().contains("meizu")) {
                        RongExtension.this.mEditText.requestFocus();
                        RongExtension.this.mEmoticonToggle.setSelected(false);
                        RongExtension.this.isKeyBoardActive = true;
                    } else {
                        RongExtension.this.showInputKeyBoard();
                    }

                    RongExtension.this.mContainerLayout.setSelected(true);
                    RongExtension.this.hidePluginBoard();
                    RongExtension.this.hideEmoticonBoard();
                }

                return false;
            }
        });
        this.mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !TextUtils.isEmpty(RongExtension.this.mEditText.getText())) {
                    RongExtension.this.mSendToggle.setVisibility(View.VISIBLE);
                    RongExtension.this.mPluginLayout.setVisibility(View.GONE);
                }

            }
        });
        this.mEditText.addTextChangedListener(new TextWatcher() {
            private int start;
            private int count;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (RongExtension.this.mExtensionClickListener != null) {
                    RongExtension.this.mExtensionClickListener.beforeTextChanged(s, start, count, after);
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                this.start = start;
                this.count = count;
                if (RongExtension.this.mExtensionClickListener != null) {
                    RongExtension.this.mExtensionClickListener.onTextChanged(s, start, before, count);
                }

                if (RongExtension.this.mVoiceInputToggle.getVisibility() == View.VISIBLE) {
                    RongExtension.this.mSendToggle.setVisibility(View.GONE);
                    RongExtension.this.mPluginLayout.setVisibility(View.VISIBLE);
                } else if (s != null && s.length() != View.VISIBLE) {
                    RongExtension.this.mSendToggle.setVisibility(View.VISIBLE);
                    RongExtension.this.mPluginLayout.setVisibility(View.GONE);
                } else {
                    RongExtension.this.mSendToggle.setVisibility(View.GONE);
                    RongExtension.this.mPluginLayout.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (AndroidEmoji.isEmoji(s.subSequence(this.start, this.start + this.count).toString())) {
                    RongExtension.this.mEditText.removeTextChangedListener(this);
                    RongExtension.this.mEditText.setText(AndroidEmoji.ensure(s.toString()), TextView.BufferType.SPANNABLE);
                    RongExtension.this.mEditText.setSelection(this.start + this.count);
                    RongExtension.this.mEditText.addTextChangedListener(this);
                }

                if (RongExtension.this.mExtensionClickListener != null) {
                    RongExtension.this.mExtensionClickListener.afterTextChanged(s);
                }

            }
        });
        this.mEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return RongExtension.this.mExtensionClickListener != null && RongExtension.this.mExtensionClickListener.onKey(RongExtension.this.mEditText, keyCode, event);
            }
        });
        this.mEditText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (RongExtension.this.mEditText.getText().length() > 0 && RongExtension.this.mEditText.isFocused() && !RongExtension.this.hasEverDrawn) {
                    Rect rect = new Rect();
                    RongExtension.this.mEditText.getWindowVisibleDisplayFrame(rect);
                    int keypadHeight = RongExtension.this.mEditText.getRootView().getHeight() - rect.bottom;
                    int inputbarHeight = (int) RongExtension.this.mEditText.getContext().getResources().getDimension(R.dimen.rc_extension_bar_min_height);
                    if (keypadHeight > inputbarHeight * 2) {
                        RongExtension.this.hasEverDrawn = true;
                    }

                    if (RongExtension.this.mExtensionClickListener != null) {
                        RongExtension.this.mExtensionClickListener.onEditTextClick(RongExtension.this.mEditText);
                    }

                    RongExtension.this.showInputKeyBoard();
                    RongExtension.this.mContainerLayout.setSelected(true);
                    RongExtension.this.hidePluginBoard();
                    RongExtension.this.hideEmoticonBoard();
                }

            }
        });
        this.mVoiceToggle = (ImageView) this.mExtensionBar.findViewById(R.id.rc_voice_toggle);
        this.mVoiceToggleClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RongExtension.this.mExtensionClickListener != null) {
                    RongExtension.this.mExtensionClickListener.onSwitchToggleClick(v, RongExtension.this.mContainerLayout);
                }

                if (RongExtension.this.mVoiceInputToggle.getVisibility() == View.GONE) {
                    RongExtension.this.mEditTextLayout.setVisibility(View.GONE);
                    RongExtension.this.mSendToggle.setVisibility(View.GONE);
                    RongExtension.this.mPluginLayout.setVisibility(View.VISIBLE);
                    RongExtension.this.hideInputKeyBoard();
                    RongExtension.this.showVoiceInputToggle();
                    RongExtension.this.mContainerLayout.setClickable(true);
                    RongExtension.this.mContainerLayout.setSelected(false);
                } else {
                    RongExtension.this.mEditTextLayout.setVisibility(View.VISIBLE);
                    RongExtension.this.hideVoiceInputToggle();
                    RongExtension.this.mEmoticonToggle.setImageResource(R.drawable.rc_emotion_toggle_selector);
                    if (RongExtension.this.mEditText.getText().length() > 0) {
                        RongExtension.this.mSendToggle.setVisibility(View.VISIBLE);
                        RongExtension.this.mPluginLayout.setVisibility(View.GONE);
                    } else {
                        RongExtension.this.mSendToggle.setVisibility(View.GONE);
                        RongExtension.this.mPluginLayout.setVisibility(View.VISIBLE);
                    }

                    RongExtension.this.showInputKeyBoard();
                    RongExtension.this.mContainerLayout.setSelected(true);
                }

                RongExtension.this.hidePluginBoard();
                RongExtension.this.hideEmoticonBoard();
            }
        };
        this.mVoiceToggle.setOnClickListener(this.mVoiceToggleClickListener);
        this.mVoiceInputToggle.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (RongExtension.this.mExtensionClickListener != null) {
                    RongExtension.this.mExtensionClickListener.onVoiceInputToggleTouch(v, event);
                }

                return false;
            }
        });
        this.mSendToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = RongExtension.this.mEditText.getText().toString();
                RongExtension.this.mEditText.getText().clear();
                RongExtension.this.mEditText.setText("");
                if (RongExtension.this.mExtensionClickListener != null) {
                    RongExtension.this.mExtensionClickListener.onSendToggleClick(v, text);
                }

            }
        });
        this.mPluginToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RongExtension.this.mExtensionClickListener != null) {
                    RongExtension.this.mExtensionClickListener.onPluginToggleClick(v, RongExtension.this);
                }

                RongExtension.this.setPluginBoard();
            }
        });
        this.mEmoticonToggle = (ImageView) this.mExtensionBar.findViewById(R.id.rc_emoticon_toggle);
        this.mEmoticonToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RongExtension.this.mExtensionClickListener != null) {
                    RongExtension.this.mExtensionClickListener.onEmoticonToggleClick(v, RongExtension.this);
                }

                if (RongExtension.this.isKeyBoardActive()) {
                    RongExtension.this.hideInputKeyBoard();
                    RongExtension.this.getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RongExtension.this.setEmoticonBoard();
                        }
                    }, 200L);
                } else {
                    RongExtension.this.setEmoticonBoard();
                }

                RongExtension.this.hidePluginBoard();
            }
        });
        this.mPSMenu = (ImageView) this.mExtensionBar.findViewById(R.id.rc_switch_to_menu);
        this.mPSMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RongExtension.this.setExtensionBarVisibility(View.GONE);
                RongExtension.this.setMenuVisibility(View.VISIBLE, RongExtension.this.mInputMenuList);
            }
        });
        this.mPSDivider = this.mExtensionBar.findViewById(R.id.rc_switch_divider);
        this.addView(this.mExtensionBar);
    }

    public void showMoreActionLayout(List<IClickActions> actions) {
        this.lastState = this.getMenuVisibility() == View.VISIBLE ? RongExtension.VisibilityState.MENUCONTAINER_VISIBLE : RongExtension.VisibilityState.EXTENSION_VISIBLE;
        this.setExtensionBarVisibility(View.GONE);
        this.setMenuVisibility(View.GONE);
        this.moreClickAdapter.bindView(this, this.mFragment, actions);
    }

    public void hideMoreActionLayout() {
        if (!this.getConversationType().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE) && !this.getConversationType().equals(Conversation.ConversationType.PUBLIC_SERVICE)) {
            this.setExtensionBarVisibility(View.VISIBLE);
        } else if (this.mInputMenuList != null) {
            if (this.lastState == RongExtension.VisibilityState.MENUCONTAINER_VISIBLE) {
                this.setExtensionBarVisibility(View.GONE);
                this.setMenuVisibility(View.VISIBLE);
            } else {
                this.setExtensionBarVisibility(View.VISIBLE);
                this.mPSMenu.setVisibility(View.VISIBLE);
                this.mPSDivider.setVisibility(View.VISIBLE);
            }
        } else {
            this.setExtensionBarVisibility(View.VISIBLE);
        }

        this.moreClickAdapter.hideMoreActionLayout();
    }

    public void setMoreActionEnable(boolean enable) {
        this.moreClickAdapter.setMoreActionEnable(enable);
    }

    public boolean isMoreActionShown() {
        return this.moreClickAdapter.isMoreActionShown();
    }

    private void hideVoiceInputToggle() {
        this.mVoiceToggle.setImageResource(R.drawable.rc_voice_toggle_selector);
        this.mVoiceInputToggle.setVisibility(View.GONE);
        ExtensionHistoryUtil.setExtensionBarState(this.getContext(), this.mUserId, this.mConversationType, ExtensionHistoryUtil.ExtensionBarState.NORMAL);
    }

    private void showVoiceInputToggle() {
        this.mVoiceInputToggle.setVisibility(View.VISIBLE);
        this.mVoiceToggle.setImageResource(R.drawable.rc_keyboard_selector);
        ExtensionHistoryUtil.setExtensionBarState(this.getContext(), this.mUserId, this.mConversationType, ExtensionHistoryUtil.ExtensionBarState.VOICE);
    }

    private void hideEmoticonBoard() {
        this.mEmotionTabAdapter.setVisibility(View.GONE);
        this.mEmoticonToggle.setImageResource(R.drawable.rc_emotion_toggle_selector);
    }

    private void setEmoticonBoard() {
        if (this.mEmotionTabAdapter.isInitialized()) {
            if (this.mEmotionTabAdapter.getVisibility() == View.VISIBLE) {
                this.mEmotionTabAdapter.setVisibility(View.GONE);
                this.mEmoticonToggle.setSelected(false);
                this.mEmoticonToggle.setImageResource(R.drawable.rc_emotion_toggle_selector);
                this.showInputKeyBoard();
            } else {
                this.mEmotionTabAdapter.setVisibility(View.VISIBLE);
                this.mContainerLayout.setSelected(true);
                this.mEmoticonToggle.setSelected(true);
                this.mEmoticonToggle.setImageResource(R.drawable.rc_keyboard_selector);
            }
        } else {
            this.mEmotionTabAdapter.bindView(this);
            this.mEmotionTabAdapter.setVisibility(View.VISIBLE);
            this.mContainerLayout.setSelected(true);
            this.mEmoticonToggle.setSelected(true);
            this.mEmoticonToggle.setImageResource(R.drawable.rc_keyboard_selector);
        }

        if (!TextUtils.isEmpty(this.mEditText.getText())) {
            this.mSendToggle.setVisibility(View.VISIBLE);
            this.mPluginLayout.setVisibility(View.GONE);
        }

    }

    private void hidePluginBoard() {
        if (this.mPluginAdapter != null) {
            this.mPluginAdapter.setVisibility(View.GONE);
            View pager = this.mPluginAdapter.getPager();
            this.mPluginAdapter.removePager(pager);
        }

    }

    private void setPluginBoard() {
        if (this.mPluginAdapter.isInitialized()) {
            if (this.mPluginAdapter.getVisibility() == View.VISIBLE) {
                View pager = this.mPluginAdapter.getPager();
                if (pager != null) {
                    pager.setVisibility(pager.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                } else {
                    this.mPluginAdapter.setVisibility(View.GONE);
                    this.mContainerLayout.setSelected(true);
                    this.showInputKeyBoard();
                }
            } else {
                this.mEmoticonToggle.setImageResource(R.drawable.rc_emotion_toggle_selector);
                if (this.isKeyBoardActive()) {
                    this.getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RongExtension.this.mPluginAdapter.setVisibility(View.VISIBLE);
                        }
                    }, 200L);
                } else {
                    this.mPluginAdapter.setVisibility(View.VISIBLE);
                }

                this.hideInputKeyBoard();
                this.hideEmoticonBoard();
                this.mContainerLayout.setSelected(false);
            }
        } else {
            this.mEmoticonToggle.setImageResource(R.drawable.rc_emotion_toggle_selector);
            this.mPluginAdapter.bindView(this);
            this.mPluginAdapter.setVisibility(View.VISIBLE);
            this.mContainerLayout.setSelected(false);
            this.hideInputKeyBoard();
            this.hideEmoticonBoard();
        }

        this.hideVoiceInputToggle();
        this.mEditTextLayout.setVisibility(View.VISIBLE);
    }

    private boolean isKeyBoardActive() {
        return this.isKeyBoardActive;
    }

    private void hideInputKeyBoard() {
        @SuppressLint("WrongConstant") InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService("input_method");
        imm.hideSoftInputFromWindow(this.mEditText.getWindowToken(), 0);
        this.mEditText.clearFocus();
        this.isKeyBoardActive = false;
    }

    private void showInputKeyBoard() {
        this.mEditText.requestFocus();
        @SuppressLint("WrongConstant") InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService("input_method");
        imm.showSoftInput(this.mEditText, 0);
        this.mEmoticonToggle.setSelected(false);
        this.isKeyBoardActive = true;
    }

    private void setSCE() {
        this.mSwitchLayout.setVisibility(View.VISIBLE);
        if (this.mSendToggle.getVisibility() == View.VISIBLE) {
            this.mPluginLayout.setVisibility(View.GONE);
        } else {
            this.mPluginLayout.setVisibility(View.VISIBLE);
        }

        this.mMainBar.removeAllViews();
        this.mMainBar.addView(this.mSwitchLayout);
        this.mMainBar.addView(this.mContainerLayout);
        this.mMainBar.addView(this.mPluginLayout);
    }

    private void setSC() {
        this.mSwitchLayout.setVisibility(View.VISIBLE);
        this.mMainBar.removeAllViews();
        this.mMainBar.addView(this.mSwitchLayout);
        this.mMainBar.addView(this.mContainerLayout);
    }

    private void setCE() {
        if (this.mSendToggle.getVisibility() == View.VISIBLE) {
            this.mPluginLayout.setVisibility(View.GONE);
        } else {
            this.mPluginLayout.setVisibility(View.VISIBLE);
        }

        this.mMainBar.removeAllViews();
        this.mMainBar.addView(this.mContainerLayout);
        this.mMainBar.addView(this.mPluginLayout);
    }

    private void setEC() {
        if (this.mSendToggle.getVisibility() == View.VISIBLE) {
            this.mPluginLayout.setVisibility(View.GONE);
        } else {
            this.mPluginLayout.setVisibility(View.VISIBLE);
        }

        this.mMainBar.removeAllViews();
        this.mMainBar.addView(this.mPluginLayout);
        this.mMainBar.addView(this.mContainerLayout);
    }

    private void setC() {
        this.mMainBar.removeAllViews();
        this.mMainBar.addView(this.mContainerLayout);
    }

    private void initPanelStyle() {
        ExtensionHistoryUtil.ExtensionBarState state = ExtensionHistoryUtil.getExtensionBarState(this.getContext(), this.mUserId, this.mConversationType);
        if (state == ExtensionHistoryUtil.ExtensionBarState.NORMAL) {
            this.mVoiceToggle.setImageResource(R.drawable.rc_voice_toggle_selector);
            this.mEditTextLayout.setVisibility(View.VISIBLE);
            this.mVoiceInputToggle.setVisibility(View.GONE);
        } else {
            this.mVoiceToggle.setImageResource(R.drawable.rc_keyboard_selector);
            this.mEditTextLayout.setVisibility(View.GONE);
            this.mVoiceInputToggle.setVisibility(View.VISIBLE);
            this.mSendToggle.setVisibility(View.GONE);
            this.mPluginLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.originalTop != 0) {
            if (this.originalTop > t) {
                if (this.originalBottom > b && this.mExtensionClickListener != null && this.collapsed) {
                    this.collapsed = false;
                    this.mExtensionClickListener.onExtensionExpanded(this.originalBottom - t);
                } else if (this.collapsed && this.mExtensionClickListener != null) {
                    this.collapsed = false;
                    this.mExtensionClickListener.onExtensionExpanded(b - t);
                }
            } else if (!this.collapsed && this.mExtensionClickListener != null) {
                this.collapsed = true;
                this.mExtensionClickListener.onExtensionCollapsed();
            }
        }

        if (this.originalTop == 0) {
            this.originalTop = t;
            this.originalBottom = b;
        }

    }

    public void resetEditTextLayoutDrawnStatus() {
        this.hasEverDrawn = false;
    }

    public void showRequestPermissionFailedAlter(String content) {
        Context context = this.mFragment.getActivity();
        PermissionCheckUtil.showRequestPermissionFailedAlter(context, content);
    }

    static enum VisibilityState {
        EXTENSION_VISIBLE,
        MENUCONTAINER_VISIBLE;

        private VisibilityState() {
        }
    }

}