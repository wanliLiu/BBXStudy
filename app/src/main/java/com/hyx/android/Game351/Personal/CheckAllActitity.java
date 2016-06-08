package com.hyx.android.Game351.Personal;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.HeadView.OnBackBtnListener;

import java.util.ArrayList;
import java.util.List;

public class CheckAllActitity extends BaseActivity {

    private LocalActivityManager manager = null;
    private ViewPager pager = null;
    private HeadView headView;

    private RadioGroup radiochose;
    private RadioButton ChosePost, ChoseRelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.person_topic);

        headView = (HeadView) findViewById(R.id.headView);
        headView.getBtnBack().setImageResource(R.drawable.back);

        radiochose = (RadioGroup) findViewById(R.id.radiochose);
        ChosePost = (RadioButton) findViewById(R.id.ChosePost);
        ChoseRelay = (RadioButton) findViewById(R.id.ChoseRelay);

        initPagerViewer();
    }

    @Override
    protected void initListener() {

        headView.setOnBackBtnListener(new OnBackBtnListener() {

            @Override
            public void onClick() {
                onBackPressed();
            }
        });
        radiochose.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.ChosePost) {
                    pager.setCurrentItem(0);
                } else {
                    pager.setCurrentItem(1);
                }
            }
        });

        pager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {

                if (arg0 == 0) {
                    radiochose.check(radiochose.getChildAt(0).getId());
                } else {
                    radiochose.check(radiochose.getChildAt(1).getId());
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    @Override
    protected void initData() {

        if (app.getUserID().equals(getIntent().getStringExtra("uid"))) {
            headView.setTitle("我的帖子");
            ChosePost.setText("我发表的");
            ChoseRelay.setText("我回复的");
        } else {
            headView.setTitle(getIntent().getStringExtra("UserName") + "的帖子");
            ChosePost.setText("Ta的帖子");
            ChoseRelay.setText("Ta的回复");
        }

    }

    /**
     * 初始化PageViewer
     */
    private void initPagerViewer() {

        pager = (ViewPager) findViewById(R.id.viewpage);
        final ArrayList<View> list = new ArrayList<View>();
        Intent intent = new Intent(ctx, PersonTopic.class);
        intent.putExtra("uid", getIntent().getStringExtra("uid"));
        intent.putExtra("UserName", getIntent().getStringExtra("UserName"));
        list.add(getView("topic", intent));
        Intent intent2 = new Intent(ctx, PersonRelay.class);
        intent2.putExtra("uid", getIntent().getStringExtra("uid"));
        intent2.putExtra("UserName", getIntent().getStringExtra("UserName"));
        list.add(getView("relay", intent2));

        pager.setAdapter(new MyPagerAdapter(list));
        pager.setCurrentItem(0);
    }

    /**
     * 通过activity获取视图
     *
     * @param id
     * @param intent
     * @return
     */
    private View getView(String id, Intent intent) {
        return manager.startActivity(id, intent).getDecorView();
    }

    /**
     * Pager适配器
     */
    public class MyPagerAdapter extends PagerAdapter {
        List<View> list = new ArrayList<View>();

        public MyPagerAdapter(ArrayList<View> list) {
            this.list = list;
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            ViewPager pViewPager = ((ViewPager) container);
            pViewPager.removeView(list.get(position));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ViewPager pViewPager = ((ViewPager) arg0);
            pViewPager.addView(list.get(arg1));
            return list.get(arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }

}
