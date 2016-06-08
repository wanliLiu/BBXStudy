package com.hyx.android.Game351.login;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyx.android.Game351.base.Constants;
import com.hyx.android.Game351.base.MyApplication;
import com.hyx.android.Game351.data.ApiCallBack;
import com.hyx.android.Game351.data.ApiHelper;
import com.hyx.android.Game351.data.ApiParams;
import com.hyx.android.Game351.data.Result;
import com.hyx.android.Game351.util.MLog;
import com.hyx.android.Game351.util.MyTools;

public class CheckService extends Service {

    private final int checkPeroid = 1000 * 30;
    private final int checkPeroid_1 = 1000 * 60 * 5;
    private MyApplication app;
    private Context ctx;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                checkVaild();
                handler.sendEmptyMessageDelayed(1, checkPeroid_1);
            } else {
                if (!TextUtils.isEmpty(app.getUserName())) {
                    /**
                     * 30秒检测是否有登陆 终端定时检测是否登录
                     */
                    ApiParams params = new ApiParams();
                    params.put("action", "checkReLogin");
                    params.put("username", app.getUserName());
                    params.put("logindate", app.getUserLoginDate());
                    params.put("checkcode", MyTools.getMD5_32(app.getUserName() + Constants.checkCode));

                    ApiHelper.get(ctx, params, new ApiCallBack() {

                        @Override
                        public void receive(Result result) {
                            if (result.isSuccess()) {
                                JSONObject json = JSON.parseObject(result.getObj().toString());
                                if (!json.getBooleanValue("success")) {
                                    app.exit();
                                    Toast.makeText(ctx, json.getString("info"), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ctx, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    ctx.startActivity(intent);
                                    handler.removeMessages(0);
                                    CheckService.this.stopSelf();
                                }
                            } else {
                                MLog.e(result.getObj().toString());
                            }
                        }
                    });
                }
                handler.sendEmptyMessageDelayed(0, checkPeroid);
            }

        }

        ;
    };

    @Override
    public void onCreate() {
        super.onCreate();

        ctx = getApplicationContext();
        app = (MyApplication) getApplication();

        handler.sendEmptyMessageDelayed(0, checkPeroid);
        handler.sendEmptyMessageDelayed(1, checkPeroid_1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        handler.removeMessages(0);
        CheckService.this.stopSelf();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 终端每5分钟定时请求验证 验证终端用户是否有效
     */
    private void checkVaild() {
        if (!TextUtils.isEmpty(app.getUserName())) {
            ApiParams params = new ApiParams();
            params.put("action", "checkValid");
            params.put("username", app.getUserName());
            params.put("checkcode", MyTools.getMD5_32(app.getUserName() + Constants.checkCode));

            ApiHelper.get(ctx, params, new ApiCallBack() {

                @Override
                public void receive(Result result) {
                    if (result.isSuccess()) {
                        JSONObject json = JSON.parseObject(result.getObj().toString());
                        if (!json.getBooleanValue("success")) {
                            app.exit();
                            Intent intent = new Intent(ctx, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ctx.startActivity(intent);
                            handler.removeMessages(0);
                            CheckService.this.stopSelf();
                        } else {
                            MLog.e(json.getString("info"));
                        }
                    } else {
                        MLog.e(result.getObj().toString());
                    }
                }
            });
        }
    }
}
