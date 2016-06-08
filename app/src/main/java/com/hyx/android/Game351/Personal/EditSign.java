package com.hyx.android.Game351.Personal;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.HeadView.OnActionBtnListener;
import com.hyx.android.Game351.view.HeadView.OnBackBtnListener;

public class EditSign extends BaseActivity {

    private HeadView headView;

    private int CanInputLength = 140;

    private EditText input;
    private TextView inputAttention;

    @Override
    protected void initView() {
        setContentView(R.layout.edit_sign);

        headView = (HeadView) findViewById(R.id.headView);
        headView.getBtnBack().setImageResource(R.drawable.back);
        headView.setTitle("修改签名");

        headView.getBtnAction().setText("确定");

        input = (EditText) findViewById(R.id.input);
        inputAttention = (TextView) findViewById(R.id.inputAttention);
    }

    @Override
    protected void initListener() {

        headView.setOnBackBtnListener(new OnBackBtnListener() {

            @Override
            public void onClick() {
                onBackPressed();
            }
        });
        headView.setOnActionBtnListener(new OnActionBtnListener() {

            @Override
            public void onClick() {

                if (!TextUtils.isEmpty(input.getText().toString())) {
                    Intent intent = new Intent();
                    intent.putExtra("Content", input.getText().toString());
                    setResult(RESULT_OK, intent);
                    EditSign.this.finish();
                } else {
                    MyToast("你还没有输入，不能提交！");
                }

            }
        });

        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                if (!TextUtils.isEmpty(s.toString().trim())) {
                    int length = s.toString().trim().length();

                    if (length > CanInputLength) {
                        inputAttention.setText("不能在输入了，亲");
                        input.setText(input.getText().toString()
                                .substring(0, CanInputLength));
                    } else {
                        inputAttention.setText("你还能输入"
                                + (CanInputLength - length) + "字");
                    }
                } else {
                    inputAttention.setText("你还能输入" + CanInputLength + "字");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void initData() {

        if (!TextUtils.isEmpty(getIntent().getStringExtra("Content"))) {
            input.setText(getIntent().getStringExtra("Content"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        HideKeyboard();
    }
}
