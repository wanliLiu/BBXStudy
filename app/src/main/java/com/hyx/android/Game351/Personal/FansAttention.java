package com.hyx.android.Game351.Personal;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.base.BaseListAdapter;
import com.hyx.android.Game351.base.Constants;
import com.hyx.android.Game351.data.ApiCallBack;
import com.hyx.android.Game351.data.ApiHelper;
import com.hyx.android.Game351.data.ApiParams;
import com.hyx.android.Game351.data.Result;
import com.hyx.android.Game351.modle.fansAttentBean;
import com.hyx.android.Game351.util.MyTools;
import com.hyx.android.Game351.view.CircularImageView;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.HeadView.OnBackBtnListener;

import java.util.List;

public class FansAttention extends BaseActivity {

    private PullToRefreshListView topicList;
    private ListView topicContnet;
    private conAdapter adapter;
    private HeadView headView;
    private int page = 1;

    private String uid;
    private boolean isfans = false;
    private String userName;

    @Override
    protected void initView() {
        setContentView(R.layout.fan_attention);

        headView = (HeadView) findViewById(R.id.headView);
        headView.getBtnBack().setImageResource(R.drawable.back);

        topicList = (PullToRefreshListView) findViewById(R.id.List);

        topicContnet = topicList.getRefreshableView();
    }

    @Override
    protected void initListener() {

        headView.setOnBackBtnListener(new OnBackBtnListener() {

            @Override
            public void onClick() {
                onBackPressed();
            }
        });
        topicContnet.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                try {
                    fansAttentBean temp = (fansAttentBean) adapter.getItem(position - 1);
                    Intent intent = new Intent(ctx, PersionPosition.class);
                    intent.putExtra("uid", temp.getUser_id());
                    intent.putExtra("UserName", temp.getUser_name());
                    startActivity(intent);
                } catch (Exception e) {
                }
            }
        });

        topicList.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (adapter.getList() != null && adapter.getList().size() > 0) {
                    adapter.getList().clear();
                }
                getTopicContent();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page += 1;
                getTopicContent();
            }
        });

    }

    @Override
    protected void initData() {

        uid = getIntent().getStringExtra("uid");
        userName = getIntent().getStringExtra("UserName");
        isfans = getIntent().getBooleanExtra("isfans", false);

        adapter = new conAdapter(ctx);
        topicContnet.setAdapter(adapter);

        if (app.getUserID().equals(uid)) {
            if (isfans) {
                headView.setTitle("我的粉丝");
            } else {
                headView.setTitle("我的关注");
            }
        } else {
            if (isfans) {
                headView.setTitle("Ta的粉丝");
            } else {
                headView.setTitle("Ta的关注");
            }
        }

        getTopicContent();
    }

    /**
     * 获取栏目的内容
     */
    private void getTopicContent() {
        showProgress();

        ApiParams params = new ApiParams();
        if (isfans) {
            params.put("action", "get_fans");
        } else {
            params.put("action", "get_focus");
        }
        params.put("uid", uid);
        params.put("username", userName);//
        params.put("page", page);
        params.put("checkcode", MyTools.getMD5_32(userName + Constants.checkCode));

        ApiHelper.post(ctx, params, new ApiCallBack() {

            @Override
            public void receive(Result result) {

                try {
                    dismissProgress();
                    topicList.onRefreshComplete();

                    if (result.isSuccess()) {
                        JSONObject json = JSON.parseObject(result.getObj().toString());
                        if (json.getBooleanValue("success")) {
                            if (!TextUtils.isEmpty(json.getString("data"))) {
                                List<fansAttentBean> temp = JSON.parseArray(json.getString("data"), fansAttentBean.class);
                                if (temp != null && temp.size() > 0) {
                                    adapter.addAll(temp);
                                }
                            }
                        }
                    } else {
                        MyToast(result.getObj().toString());
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    /**
     * @author acer
     */
    private class conAdapter extends BaseListAdapter<fansAttentBean> {
        public conAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.fan_attention_title, null);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final fansAttentBean temp = getList().get(position);

            holder.name.setText(temp.getUser_name());
            holder.area.setText(temp.getArea());

            imageLoader.displayImage(Constants.ServerTopicAdd + temp.getAvatar(), holder.image, optionsHead);

            return convertView;
        }

        private class ViewHolder {
            public CircularImageView image;
            public TextView name;
            public TextView area;

            public Button btnAttention;

            public ViewHolder(View view) {

                image = (CircularImageView) view.findViewById(R.id.UserImage);
                name = (TextView) view.findViewById(R.id.name);
                area = (TextView) view.findViewById(R.id.address);

                btnAttention = (Button) view.findViewById(R.id.btnAttent);
                btnAttention.setVisibility(View.INVISIBLE);

                view.setTag(this);
            }
        }
    }
}
