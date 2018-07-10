package com.liaoliao.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;



import com.liaoliao.R;
import com.liaoliao.chat.ui.MainFragment;
import com.liaoliao.chat.utils.StatusBarUtil;


import me.yokeyword.fragmentation.SupportActivity;

public class MainActivity extends SupportActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
      /*  this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
       */

      setContentView(R.layout.activity_main_1);
       // StatusBarUtil.setTransparent(this);

        StatusBarUtil.immersive(this);
        if (savedInstanceState == null) {
            loadRootFragment(R.id.fragment_container, MainFragment.newInstance());
        }
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

}
