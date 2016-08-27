package com.hyx.android.Game351;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.base.Constants;
import com.hyx.android.Game351.login.LoginActivity;
import com.hyx.android.Game351.util.MyTools;
import com.hyx.android.Game351.util.SP;

import java.util.Timer;
import java.util.TimerTask;

public class WeclomeActivity extends BaseActivity {

    @Override
    protected void initView() {

        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setScaleType(ScaleType.CENTER_CROP);
        imageView.setBackgroundResource(R.drawable.weclome_bg);
        setContentView(imageView);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        if (SP.getSp(ctx).getBoolean(MyTools.getAppVersionName(this), true)) {   //主要是用于更新用户，在第一次使用新版本时，清除登陆信息
            SP.getEdit(ctx).putBoolean(MyTools.getAppVersionName(this), false).commit();

            app.SetFontSize(Constants.defaultFontSize);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                timer.cancel();

                startActivity(new Intent(WeclomeActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//				startActivity(new Intent(WeclomeActivity.this, PersionPosition.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//				startActivity(new Intent(WeclomeActivity.this, EditPersionIfo.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

//		        if (app.isNetworkAvailable()) {
//		            Intent intent=new Intent(Weclome.this, CheckAndUpdateService.class);  
//		            startService(intent);   
//		        }

                finish();
            }
        }, 2000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return true;
    }

}
