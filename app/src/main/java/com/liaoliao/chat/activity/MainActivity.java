package com.liaoliao.chat.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.liaoliao.App;
import com.liaoliao.R;
import com.liaoliao.chat.ui.MainFragment;
import com.liaoliao.chat.utils.StatusBarUtil;
import com.liaoliao.chat.utils.StatusBarUtils;


import me.yokeyword.fragmentation.SupportActivity;

public class MainActivity extends SupportActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_1);

        com.jaeger.library.StatusBarUtil.setTranslucentForCoordinatorLayout(this,0);

        if (savedInstanceState == null) {
            loadRootFragment(R.id.fragment_container, MainFragment.newInstance());
        }
        //changStatusIconCollor(true);
        StatusBarUtils.StatusBarLightMode(this);
    }
    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
        /**
         * 防止华为机型未加入白名单时按返回键回到桌面再锁屏后几秒钟进程被杀
         */
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(launcherIntent);
    }


    public void changStatusIconCollor(boolean setDark) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            View decorView = getWindow().getDecorView();
            if(decorView != null){
                int vis = decorView.getSystemUiVisibility();
                if(setDark){
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else{
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
        }
    }
}
