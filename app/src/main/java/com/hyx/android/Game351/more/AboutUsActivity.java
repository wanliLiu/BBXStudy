package com.hyx.android.Game351.more;

import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.HeadView.OnBackBtnListener;

public class AboutUsActivity extends BaseActivity {

    private HeadView headView;

    @Override
    protected void initView() {
        setContentView(R.layout.about_us);

        headView = (HeadView) findViewById(R.id.headView);
        headView.setTitle("关于我们");
        headView.getBtnBack().setImageResource(R.drawable.back);
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
