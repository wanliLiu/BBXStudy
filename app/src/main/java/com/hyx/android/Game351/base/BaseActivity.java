package com.hyx.android.Game351.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.hyx.android.Game351.R;
import com.hyx.android.Game351.alipay.Keys;
import com.hyx.android.Game351.alipay.Rsa;
import com.hyx.android.Game351.data.ApiCallBack;
import com.hyx.android.Game351.data.ApiHelper;
import com.hyx.android.Game351.data.ApiParams;
import com.hyx.android.Game351.data.HttpUtil;
import com.hyx.android.Game351.data.Result;
import com.hyx.android.Game351.login.LoginActivity;
import com.hyx.android.Game351.util.MLog;
import com.hyx.android.Game351.util.MyTools;
import com.hyx.android.Game351.view.LoadingDialog;
import com.hyx.android.Game351.view.dialog.DefaultDialog;
import com.hyx.android.Game351.view.dialog.DialogSelectListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseActivity extends Activity implements OnClickListener {

    /**
     * context
     */
    protected Context ctx;
    /**
     * 自定义application
     */
    protected MyApplication app;
    protected InputMethodManager imm;
    protected ImageLoader imageLoader;
    protected DisplayImageOptions options, optionsHead, optionBack;
    /**
     * 加载进度条
     */
    private LoadingDialog loadDialog;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    MLog.e(msg.obj.toString());
                    String temp = msg.obj.toString();
                    String src = temp.replace("{", "");
                    src = src.replace("}", "");
                    if (src.indexOf("resultStatus=9000") != -1) {
                        UpToVIP();
                        MyToast("支付成功");
                    } else if (src.indexOf("resultStatus=4000") != -1) {
                        MyToast("系统异常");
                    } else if (src.indexOf("resultStatus=4001") != -1) {
                        MyToast("订单参数错误");
                    } else if (src.indexOf("resultStatus=6001") != -1) {
                        MyToast("用户取消支付");
                    } else if (src.indexOf("resultStatus=6002") != -1) {
                        MyToast("网络连接异常");
                    }
                }
                break;
                default:
                    break;
            }
        }
    };

    /**
     * 初始化界面
     */
    protected abstract void initView();

    /**
     * 初始化事件监听
     */
    protected abstract void initListener();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    protected boolean isScreenLandsape = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(isScreenLandsape ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);

        ctx = this;
        app = (MyApplication) getApplication();
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        app.addActivity(this);

        ImageInit();

        initView();
        initListener();
        initData();

    }

    /**
     *
     */
    private void ImageInit() {
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_320212)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(250, true, true, false))//是否图片加载好后渐入的动画时间
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();


        optionsHead = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(250, true, true, false))//是否图片加载好后渐入的动画时间
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        optionBack = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.bg_dashboard_default)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(250, true, true, false))//是否图片加载好后渐入的动画时间
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 取消当前所有的request
        HttpUtil.getClient().cancelAllRequests(true);
        HttpUtil.getClient().cancelRequests(ctx, true);

        dismissProgress();

    }

    @Override
    protected void onDestroy() {

        // 关闭加载进度条
        dismissProgress();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        super.onRestart();
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 显示Toast
     *
     * @param text 文本内容
     */
    protected void MyToast(String text, int time) {
        Toast.makeText(ctx, text, time).show();
    }

    /**
     * 显示加载进度条
     */
    public void showProgress() {

        if (!isFinishing()) {
            if (loadDialog == null) {
                loadDialog = new LoadingDialog(ctx);
            }
            loadDialog.show();
        }
    }

    /**
     * 停止显示加载进度条
     */
    public void dismissProgress() {

        if (!isFinishing()) {
            if (loadDialog != null && loadDialog.isShowing()) {
                loadDialog.dismiss();
                loadDialog = null;
            }
        }
    }

    /**
     * 显示Toast
     *
     * @param text 文本内容
     */
    protected void MyToast(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示Toast
     *
     * @param resId string资源id
     */
    protected void MyToast(int resId) {
        Toast.makeText(ctx, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 关闭软键盘
     */
    public void HideKeyboard() {
        try {
            // 隐藏键盘
            if (imm.isActive()) {
                // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                imm.hideSoftInputFromWindow(BaseActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 关闭软键盘
     */
    public void HideKeyboard(View view) {
        try {
            IBinder windowToken = view.getWindowToken();
            if (windowToken != null) {
                imm.hideSoftInputFromWindow(windowToken, 0);
            }

            HideKeyboard();
        } catch (Exception e) {
        }
    }

    /**
     * 打开软键盘
     */
    public void ShowKeyboard(View view) {
        try {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        } catch (Exception e) {
        }
    }

    /**
     * 提示给出界面
     */
    public void showNeedToAiPlay() {
        final DefaultDialog dialog = new DefaultDialog(BaseActivity.this);
        dialog.setDescription("确定要升级到VIP吗?");
        dialog.setBtnCancle("暂不升级");
        dialog.setBtnOk("立即升级");
        dialog.setDialogTitle("升级到VIP后可无限制使用所有功能");
        dialog.setDialogListener(new DialogSelectListener() {

            @Override
            public void onChlidViewClick(View paramView) {
                dialog.dismiss();
                if (paramView.getId() == R.id.btn_ok) {
                    Aipay();
                }
            }
        });

        dialog.show();
    }

    /**
     * 支付宝接口
     */
    private void Aipay() {
        try {
            String info = getNewOrderInfo();
            String sign = Rsa.sign(info, Keys.PRIVATE);
            sign = URLEncoder.encode(sign);
            info += "&sign=\"" + sign + "\"&" + getSignType();
            // start the pay.

            final String orderInfo = info;
            Runnable payRunnable = new Runnable() {

                @Override
                public void run() {
                    // 构造PayTask 对象
                    PayTask alipay = new PayTask(BaseActivity.this);
                    // 调用支付接口，获取支付结果
                    String result = alipay.pay(orderInfo);

                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            };

            // 必须异步调用
            Thread payThread = new Thread(payRunnable);
            payThread.start();

        } catch (Exception ex) {
            ex.printStackTrace();
            MyToast("操作失败，请重试");
        }
    }

    /**
     * 获取订单信息
     *
     * @return
     */
    private String getNewOrderInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("partner=\"");
        sb.append(Keys.DEFAULT_PARTNER);
        sb.append("\"&out_trade_no=\"");
        sb.append(getOutTradeNo());
        sb.append("\"&subject=\"");
        sb.append("铁杆英语升级VIP");
        sb.append("\"&body=\"");
        sb.append("升级成VIP后可无限制使用铁杆英语，而且会获得更多的帮助！");
        sb.append("\"&total_fee=\"");
        sb.append(Constants.BuyMoney);
        sb.append("\"&notify_url=\"");

        // 网址需要做URL编码
        sb.append(URLEncoder.encode("http://notify.java.jpxx.org/index.jsp"));
        sb.append("\"&service=\"mobile.securitypay.pay");
        sb.append("\"&_input_charset=\"UTF-8");
        sb.append("\"&return_url=\"");
        sb.append(URLEncoder.encode("http://m.alipay.com"));
        sb.append("\"&payment_type=\"1");
        sb.append("\"&seller_id=\"");
        sb.append(Keys.DEFAULT_SELLER);

        // 如果show_url值为空，可不传
        // sb.append("\"&show_url=\"");
        sb.append("\"&it_b_pay=\"1m");
        sb.append("\"");

        return new String(sb);
    }

    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
        Date date = new Date();
        String key = format.format(date);

        java.util.Random r = new java.util.Random();
        key += r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    /**
     * 升级到VIP
     */
    private void UpToVIP() {
        showProgress();

        ApiParams params = new ApiParams();
        params.put("action", "buy");
        params.put("username", app.getUserName());
        params.put("checkcode", MyTools.getMD5_32(app.getUserName() + Constants.checkCode));

        ApiHelper.get(ctx, params, new ApiCallBack() {

            @Override
            public void receive(Result result) {
                dismissProgress();

                if (result.isSuccess()) {
                    JSONObject json = JSON.parseObject(result.getObj().toString());
                    MyToast(json.getString("info"));
                    if (json.getBooleanValue("success")) {
                        app.exit();
                        Intent intent = new Intent(ctx, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ctx.startActivity(intent);
                        BaseActivity.this.finish();
                    }
                } else {
                    MyToast(result.getObj().toString());
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.quiet_fixedly, R.anim.push_right_out);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.quiet_fixedly);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.push_left_in, R.anim.quiet_fixedly);
    }
}
