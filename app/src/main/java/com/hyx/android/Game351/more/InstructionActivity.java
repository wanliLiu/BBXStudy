package com.hyx.android.Game351.more;

import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.HeadView.OnBackBtnListener;

public class InstructionActivity extends BaseActivity {

    private HeadView headView;

    @Override
    protected void initView() {
        setContentView(R.layout.instruction_detail);

        headView = (HeadView) findViewById(R.id.headView);
        headView.getBtnBack().setImageResource(R.drawable.back);
        headView.setTitle("使用说明");
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
