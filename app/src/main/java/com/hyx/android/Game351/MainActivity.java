package com.hyx.android.Game351;

import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.Toast;

import com.hyx.android.Game351.Topic.TopicActivity;
import com.hyx.android.Game351.base.MyApplication;
import com.hyx.android.Game351.favorite.WishActivity;
import com.hyx.android.Game351.history.HistoryActivity;
import com.hyx.android.Game351.home.MenuActivity;
import com.hyx.android.Game351.more.MoreActivity;
import com.hyx.android.Game351.util.ApkType;
import com.hyx.android.Game351.util.MyTools;

import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("deprecation")
//27105152@qq.com xgn111
public class MainActivity extends TabActivity implements OnCheckedChangeListener {

    private static final int Finish_App = 1;
    public int isOpenFromSearch = 0;

    public TabHost mTabHost;
    public RadioGroup radioGroup;
    private int pressTime = 0;
    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (MyApplication) getApplication();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        app.addActivity(this);


        initWidget();
    }

    /**
     * init
     */
    private void initWidget() {
        mTabHost = getTabHost();
        mTabHost.addTab(mTabHost.newTabSpec("menu").setIndicator("menu").setContent(new Intent(this, MenuActivity.class)));

        if (MyTools.getCurrentApkType(this) == ApkType.TYPE_CopyRead ||
                MyTools.getCurrentApkType(this) == ApkType.TYPE_MEIJU ||
                MyTools.getCurrentApkType(this) == ApkType.TYPE_21) {
            ((RadioButton) findViewById(R.id.favorite_check)).setText("记录");
            mTabHost.addTab(mTabHost.newTabSpec("His").setIndicator("His").setContent(new Intent(this, HistoryActivity.class)));
        } else {
            mTabHost.addTab(mTabHost.newTabSpec("wish").setIndicator("wish").setContent(new Intent(this, TopicActivity.class)));
        }

//        if (MyTools.getCurrentApkType(this) != ApkType.TYPE_21) {
            ((RadioButton) findViewById(R.id.history_check)).setText("收藏");
            mTabHost.addTab(mTabHost.newTabSpec("fastrecord").setIndicator("fastrecord").setContent(new Intent(this, WishActivity.class)));
//        } else
//            mTabHost.addTab(mTabHost.newTabSpec("history").setIndicator("history").setContent(new Intent(this, HistoryActivity.class)));

        mTabHost.addTab(mTabHost.newTabSpec("more").setIndicator("more").setContent(new Intent(this, MoreActivity.class)));

        radioGroup = (RadioGroup) findViewById(R.id.groupTab);
        radioGroup.setOnCheckedChangeListener(this);

        if (MyTools.getCurrentApkType(this) == ApkType.TYPE_CopyRead ||
                MyTools.getCurrentApkType(this) == ApkType.TYPE_MEIJU ||
                MyTools.getCurrentApkType(this) == ApkType.TYPE_21) {
            radioGroup.check(R.id.menu_check);
        } else {
            mTabHost.setCurrentTab(1);
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.menu_check:
                mTabHost.setCurrentTab(0);
                break;
            case R.id.favorite_check:
                mTabHost.setCurrentTab(1);
                break;
            case R.id.history_check:
                mTabHost.setCurrentTab(2);
                break;
            case R.id.more_check:
                mTabHost.setCurrentTab(3);
                break;
            default:
                break;
        }
    }

    /**
     * 实现点击两次退出程序
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (pressTime++) {
                case 0:
                    Toast.makeText(MainActivity.this, R.string.app_exit_atteniton, Toast.LENGTH_SHORT).show();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            pressTime = 0;
                        }
                    }, 3000);
                    return true;

                case 1:
                    isOpenFromSearch = Finish_App;
                    this.finish();
                    return true;
                default:
                    break;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isOpenFromSearch == Finish_App) {
            overridePendingTransition(0, R.anim.app_exit);
        } else {
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//    	app.StopService();
    }

}
