package com.hyx.android.Game351.Topic;

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

import com.hyx.android.Game351.Personal.PersonRelay;
import com.hyx.android.Game351.Personal.PersonTopic;
import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.util.DisplayUtil;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.HeadView.OnActionBtnListener;
import com.hyx.android.Game351.view.HeadView.OnBackBtnListener;

import java.util.ArrayList;
import java.util.List;

public class TopicCategoryActivity extends BaseActivity {

    private LocalActivityManager manager = null;
    private ViewPager pager = null;
    private HeadView headView;

    private RadioGroup radiochose;
    private RadioButton radioRecently, radioHot;

    private String catid;

    private onNewTopicPost topicPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.category_content);

        headView = (HeadView) findViewById(R.id.headView);
        headView.getBtnBack().setImageResource(R.drawable.back);
        headView.setTitle(getIntent().getStringExtra("title"));

        headView.getBtnAction().setText("发帖");

        radiochose = (RadioGroup) findViewById(R.id.categoryCheck);
        radioRecently = (RadioButton) findViewById(R.id.radioRecently);
        radioHot = (RadioButton) findViewById(R.id.radioHot);

        radioRecently.setTextColor(getResources().getColor(
                R.color.system_title_color));
        radioHot.setTextColor(getResources()
                .getColor(R.color.color_light_black));

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

        headView.setOnActionBtnListener(new OnActionBtnListener() {

            @Override
            public void onClick() {
                Intent intent = new Intent(TopicCategoryActivity.this,
                        PostTopicActivity.class);
                intent.putExtra("catid", catid);
                startActivityForResult(intent, 200);
            }
        });

        radiochose.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioRecently) {
                    pager.setCurrentItem(0);
                    radioRecently.setTextColor(getResources().getColor(
                            R.color.system_title_color));
                    radioRecently.setBackgroundResource(R.drawable.check);
                    radioHot.setTextColor(getResources().getColor(
                            R.color.color_light_black));
                    radioHot.setBackgroundResource(R.drawable.uncheck);
                } else {
                    pager.setCurrentItem(1);
                    radioHot.setTextColor(getResources().getColor(
                            R.color.system_title_color));
                    radioHot.setBackgroundResource(R.drawable.check);
                    radioRecently.setTextColor(getResources().getColor(
                            R.color.color_light_black));
                    radioRecently.setBackgroundResource(R.drawable.uncheck);
                }
                int padding = DisplayUtil.dip2px(10, getResources().getDisplayMetrics().density);
                radioRecently.setPadding(padding, padding, padding, padding);
                radioHot.setPadding(padding, padding, padding, padding);
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

        catid = getIntent().getStringExtra("catid");

    }

    /**
     * 初始化PageViewer
     */
    private void initPagerViewer() {

        pager = (ViewPager) findViewById(R.id.viewpage);
        final ArrayList<View> list = new ArrayList<View>();
        Intent intent = new Intent(ctx, PersonTopic.class);
        intent.putExtra("catid", getIntent().getStringExtra("catid"));
        intent.putExtra("FromPostPosition", true);
        list.add(getView("topic", intent));
        Intent intent2 = new Intent(ctx, PersonRelay.class);
        intent2.putExtra("catid", getIntent().getStringExtra("catid"));
        intent2.putExtra("FromPostPosition", true);
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

    public void setOnlistener(onNewTopicPost newTopic) {
        topicPost = newTopic;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 200) {
                if (topicPost != null) {
                    radiochose.check(radiochose.getChildAt(0).getId());
                    topicPost.onNew();
                }
            }
        }
    }

    public interface onNewTopicPost {
        public void onNew();
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
        public void destroyItem(ViewGroup container, int position, Object object) {
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
