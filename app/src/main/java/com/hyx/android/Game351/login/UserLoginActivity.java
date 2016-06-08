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
import com.hyx.android.Game351.util.MyTools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserLoginActivity extends BaseActivity {

    private EditText accountInput, pwdInput, passwordInputAgain;
    private Button regitsAccount, btnResigter;

    @Override
    protected void initView() {
        setContentView(R.layout.register_activity);

        accountInput = (EditText) findViewById(R.id.accountInput);
        pwdInput = (EditText) findViewById(R.id.passwordInput);
        passwordInputAgain = (EditText) findViewById(R.id.passwordInputAgain);

        regitsAccount = (Button) findViewById(R.id.btnFindPwdBack);
        regitsAccount.setOnClickListener(this);
        btnResigter = (Button) findViewById(R.id.btnSignIn);
        btnResigter.setOnClickListener(this);

        if (getIntent().getBooleanExtra("fromWhere", false)) {//注册
            btnResigter.setText(R.string.LOGIN_register_1);
        } else {
            btnResigter.setText(R.string.LOGIN_register_2);
        }
    }

    /**
     * 登陆
     */
    private void register() {
        if (TextUtils.isEmpty(accountInput.getText().toString().trim()) ||
                TextUtils.isEmpty(pwdInput.getText().toString().trim()) ||
                TextUtils.isEmpty(passwordInputAgain.getText().toString().trim())) {
            MyToast("输入的数据不能为空，请输入");
            return;
        }

        if (TextUtils.isEmpty(app.getUserPhone())) {
            MyToast("你手机还没有注册");
            return;
        }

        if (!pwdInput.getText().toString().trim().equals(passwordInputAgain.getText().toString().trim())) {
            pwdInput.setText("");
            passwordInputAgain.setText("");
            MyToast("输入的密码不一致，请重新输入！！！！！");
            return;
        }
        showProgress();

        ApiParams params = new ApiParams();
        if (getIntent().getBooleanExtra("fromWhere", false)) {//注册
            params.put("action", "reg");
            params.put("checkcode", MyTools.getMD5_32(accountInput.getText().toString() + pwdInput.getText().toString() + Constants.checkCode));
        } else {
            params.put("action", "resetPwd");
            params.put("checkcode", MyTools.getMD5_32(accountInput.getText().toString() + pwdInput.getText().toString() + app.getUserPhone() + Constants.checkCode));
        }

        params.put("username", accountInput.getText().toString());
        params.put("pwd", pwdInput.getText().toString());
        params.put("tel", app.getUserPhone());

        ApiHelper.get(ctx, params, new ApiCallBack() {

            @Override
            public void receive(Result result) {
                dismissProgress();
                if (result.isSuccess()) {
                    JSONObject json = JSON.parseObject(result.getObj().toString());
                    if (json.getBooleanValue("success")) {
                        MyToast(json.getString("info"));
                        if (getIntent().getBooleanExtra("fromWhere", false)) {
                            app.exit();
                            app.setUserPassword(pwdInput.getText().toString());
                            app.setUserName(accountInput.getText().toString());
                            app.setUserLoginDate(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
                            app.setUserID(json.getString("uid"));
//							app.StartCheckService();
                            startActivity(new Intent(UserLoginActivity.this, MainActivity.class));
                        }
                        UserLoginActivity.this.finish();
                    } else {
                        MyToast(json.getString("info"));
                    }
                } else {
                    MyToast(result.getObj().toString());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFindPwdBack:
                onBackPressed();
                break;
            case R.id.btnSignIn:
                register();
                break;
            default:
                break;
        }
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.quiet_fixedly, R.anim.push_bottom_out);
    }

}
