package com.hyx.android.Game351.home;

import android.widget.TextView;

import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.HeadView.OnBackBtnListener;

public class ReadAllActivity extends BaseActivity {

    private HeadView headView;
    private TextView allContent;

    @Override
    protected void initView() {
        setContentView(R.layout.read_all_activity);

        headView = (HeadView) findViewById(R.id.headView);
        headView.setTitle("全文阅读");

        allContent = (TextView) findViewById(R.id.allContent);

        allContent.setText(getIntent().getStringExtra("content"));
    }

    @Override
    protected void initListener() {

        headView.setOnBackBtnListener(new OnBackBtnListener() {

            @Override
            public void onClick() {
                onBackPressed();

            }
        });
    }

    @Override
    protected void initData() {

    }


}
