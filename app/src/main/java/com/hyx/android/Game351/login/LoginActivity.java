package com.hyx.android.Game351.login;


import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyx.android.Game351.MainActivity;
import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.base.Constants;
import com.hyx.android.Game351.data.ApiCallBack;
import com.hyx.android.Game351.data.ApiHelper;
import com.hyx.android.Game351.data.ApiParams;
import com.hyx.android.Game351.data.Result;
import com.hyx.android.Game351.util.ApkType;
import com.hyx.android.Game351.util.MyTools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

public class LoginActivity extends BaseActivity {

    // 填写从短信SDK应用后台注册得到的APPKEY
    private static String APPKEY = "3eab0d7bdb18";
    // 填写从短信SDK应用后台注册得到的APPSECRET
    private static String APPSECRET = "875a8daee336a19df515c5b0ed975f24";
    private Button login, findPwdBack, regitsAccount, btnGuest;
    private EditText accountInput, pwdInput;

    @Override
    protected void initView() {
        setContentView(R.layout.login_activity);

        login = (Button) findViewById(R.id.btnSignIn);
        findPwdBack = (Button) findViewById(R.id.btnFindPwdBack);
        regitsAccount = (Button) findViewById(R.id.btnRegist);
        btnGuest = (Button) findViewById(R.id.btnGuest);

        btnGuest.setOnClickListener(this);
        login.setOnClickListener(this);
        findPwdBack.setOnClickListener(this);
        regitsAccount.setOnClickListener(this);

        accountInput = (EditText) findViewById(R.id.accountInput);
        pwdInput = (EditText) findViewById(R.id.passwordInput);

        if (!TextUtils.isEmpty(app.getUserName()) && !app.getUserName().equals("test")) {
            accountInput.setText(app.getUserName());

            if (!TextUtils.isEmpty(app.getUserPassword())) {
                pwdInput.setText(app.getUserPassword());
            }
        }

        if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead) {
            findViewById(R.id.actionBar).setVisibility(View.GONE);
        }

        SMSSDK.initSDK(this, APPKEY, APPSECRET);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnGuest:
                Constants.setServerAddress(Constants.GuestAddress);
                login("test", "xgnnj025");
                break;
            case R.id.btnSignIn:
                Constants.setServerAddress(Constants.UserAddress);
                login(accountInput.getText().toString().trim(), pwdInput.getText().toString().trim());
                break;
            case R.id.btnFindPwdBack:
                takeAnother(false);
                break;
            case R.id.btnRegist:
                takeAnother(true);
                break;
            default:
                break;
        }
    }

    /**
     * 登陆
     */
    private void login(String account, String pwd) {
        if (TextUtils.isEmpty(account) ||
                TextUtils.isEmpty(pwd)) {
            MyToast("输入的数据不能为空，请输入");
            return;
        }

//		if (TextUtils.isEmpty(app.getUserPhone())) {
//			MyToast("你手机还没有注册");
//			return;
//		}

        showProgress();

        ApiParams params = new ApiParams();
        params.put("action", "login");
        params.put("username", account);
        params.put("pwd", pwd);
        app.setUserLoginDate(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        params.put("logindate", app.getUserLoginDate());
        params.put("checkcode", MyTools.getMD5_32(account + pwd + Constants.checkCode));

        ApiHelper.get(ctx, params, new ApiCallBack() {

            @Override
            public void receive(Result result) {
                dismissProgress();
                if (result.isSuccess()) {
                    JSONObject json = JSON.parseObject(result.getObj().toString());
                    if (json.getBooleanValue("success") && json.containsKey("uid")) {
                        app.setUserPassword(pwdInput.getText().toString());
                        app.setUserName(json.getString("username"));//accountInput.getText().toString()
                        app.setUserID(json.getString("uid"));
//						app.StartCheckService();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        LoginActivity.this.finish();
                    } else {
                        MyToast(json.getString("info"));
                    }
                } else {
                    app.setUserLoginDate("");
                    MyToast(result.getObj().toString());
                }
            }
        });
    }

    /**
     * @param isRegister
     */
    private void takeAnother(final boolean isRegister) {
//		Intent intent = new Intent();
//		intent.setClass(LoginActivity.this, UserLoginActivity.class);
//		if (isRegister) {//启动注册界面
//			intent.putExtra("fromWhere", true);
//		}else {//启动找回密码界面
//			intent.putExtra("fromWhere", false);
//		}
//		intent.putExtra("phone", "15882098180");
//		startActivity(intent);
//		overridePendingTransition(R.anim.push_up_in, R.anim.quiet_fixedly);

        // 打开注册页面
        RegisterPage registerPage = new RegisterPage();
        registerPage.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                // 解析注册结果
                if (result == SMSSDK.RESULT_COMPLETE) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country");
                    String phone = (String) phoneMap.get("phone");

                    app.setUserPhonew(phone);

                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, UserLoginActivity.class);
                    intent.putExtra("phone", phone);
                    if (isRegister) {//启动注册界面
                        intent.putExtra("fromWhere", true);
                    } else {//启动找回密码界面
                        intent.putExtra("fromWhere", false);
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_up_in, R.anim.quiet_fixedly);
                }
            }
        });
        registerPage.show(this);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }
}
