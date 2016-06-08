package com.hyx.android.Game351.Personal;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyx.android.Game351.R;
import com.hyx.android.Game351.Topic.TopicRelyActivity;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.base.BaseListAdapter;
import com.hyx.android.Game351.base.Constants;
import com.hyx.android.Game351.data.ApiCallBack;
import com.hyx.android.Game351.data.ApiHelper;
import com.hyx.android.Game351.data.ApiParams;
import com.hyx.android.Game351.data.Result;
import com.hyx.android.Game351.modle.TopicContentBean;
import com.hyx.android.Game351.modle.UserIfoBean;
import com.hyx.android.Game351.util.MyTools;
import com.hyx.android.Game351.view.CircularImageView;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.HeadView.OnActionBtnListener;
import com.hyx.android.Game351.view.HeadView.OnBackBtnListener;
import com.hyx.android.Game351.view.NonScrollGridView;
import com.hyx.android.Game351.view.NonScrollListView;

import java.util.ArrayList;
import java.util.List;

import br.com.dina.ui.model.RoundImageView;

public class PersionPosition extends BaseActivity {

    private static final int EditResult = 32;

    private HeadView headView;
    private UserIfoBean userIfo;

    private ImageView Imageback;
    private CircularImageView persPic;
    private TextView name, other, btnfan, btnattention, AllCorrectNum, FistNum, NumPost, NumRelay;

    private NonScrollGridView picWall;
    private List<String> pic = new ArrayList<String>();
    private PicAdapter picadapter;

    private NonScrollListView menList;
    private personAdapter perAdapter;

    private TextView Checkall, whoPost;
    private Button btnAttent;

    private String uid;
    private String userName;

    private RelativeLayout container;

    private View buttomShow;


    @Override
    protected void initView() {
        setContentView(R.layout.persion_main);

        headView = (HeadView) findViewById(R.id.headView);
        headView.getBtnBack().setImageResource(R.drawable.back);


        Imageback = (ImageView) findViewById(R.id.Imageback);
        persPic = (CircularImageView) findViewById(R.id.persPic);
        name = (TextView) findViewById(R.id.name);
        other = (TextView) findViewById(R.id.other);

        btnfan = (TextView) findViewById(R.id.btnfan);
        btnattention = (TextView) findViewById(R.id.btnattention);
        AllCorrectNum = (TextView) findViewById(R.id.AllCorrectNum);
        FistNum = (TextView) findViewById(R.id.FistNum);
        NumPost = (TextView) findViewById(R.id.NumPost);
        NumRelay = (TextView) findViewById(R.id.NumRelay);

        picWall = (NonScrollGridView) findViewById(R.id.picWal);
        menList = (NonScrollListView) findViewById(R.id.menList);
        menList.setVisibility(View.GONE);
        Checkall = (TextView) findViewById(R.id.Checkall);
        Checkall.setVisibility(View.GONE);

        btnAttent = (Button) findViewById(R.id.btnAttent);
        buttomShow = (View) findViewById(R.id.dlskd);

        whoPost = (TextView) findViewById(R.id.whoPost);
        container = (RelativeLayout) findViewById(R.id.container);
        container.setVisibility(View.INVISIBLE);
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
                Intent intent = new Intent(PersionPosition.this, EditPersionIfo.class);
                intent.putExtra("uid", getIntent().getStringExtra("uid"));
                intent.putExtra("UserName", getIntent().getStringExtra("UserName"));
                intent.putExtra("UserIfo", userIfo);
                startActivityForResult(intent, EditResult);
            }
        });

        menList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent(ctx, TopicRelyActivity.class);
                intent.putExtra("topicContent", perAdapter.getList().get(arg2));
                startActivity(intent);
            }
        });
        Checkall.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, CheckAllActitity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("UserName", userName);
                startActivity(intent);
            }
        });
        btnAttent.setTag(0);
        btnAttent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if ((Integer) v.getTag() == 0) {
                    AttentionPerson();
                } else {
                    CancleAttentionPerson();
                }

            }
        });

        btnfan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, FansAttention.class);
                intent.putExtra("uid", uid);
                intent.putExtra("UserName", userName);
                intent.putExtra("isfans", true);
                startActivity(intent);
            }
        });

        btnattention.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, FansAttention.class);
                intent.putExtra("uid", uid);
                intent.putExtra("UserName", userName);
                intent.putExtra("isfans", false);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {

//		uid = app.getUserID();
//		userName = app.getUserName();

        uid = getIntent().getStringExtra("uid");
        userName = getIntent().getStringExtra("UserName");

        if (app.getUserID().equals(uid)) {
            headView.setTitle("我的主页");
            headView.getBtnAction().setText("编辑");
            whoPost.setText("我的帖子");
            buttomShow.setVisibility(View.GONE);
        } else {
            headView.setTitle("个人主页");
            headView.getFrem().setVisibility(View.INVISIBLE);
            whoPost.setText("他的帖子");
        }

        getPersonInfo();
    }

    /**
     * 根据获取的结果处理数据
     */
    private void dealdata() {
        name.setText(userIfo.getUser_name());
        btnfan.setText("粉丝  " + userIfo.getFansnum());
        btnattention.setText("关注  " + userIfo.getFocusnum());
        AllCorrectNum.setText(userIfo.getFinishnum() + "");
        FistNum.setText(userIfo.getFirstnum() + "");
        NumPost.setText("发表(" + userIfo.getTopicnum() + ")");
        NumRelay.setText("回复(" + userIfo.getCommentnum() + ")");

        other.setText(userIfo.getBirthday() + "  " + userIfo.getArea());

        if (!TextUtils.isEmpty(userIfo.getAvatar())) {
            if (app.getUserID().equals(uid)) {
                app.setUserAvatar(Constants.ServerTopicAdd + userIfo.getAvatar());
            }
            imageLoader.displayImage(Constants.ServerTopicAdd + userIfo.getAvatar(), persPic, optionsHead);
        }

        if (!TextUtils.isEmpty(userIfo.getFace())) {
            imageLoader.displayImage(Constants.ServerTopicAdd + userIfo.getFace(), Imageback, optionBack);
        }

        AddPicture();

        if (pic != null && pic.size() > 0) {
            picadapter = new PicAdapter(ctx);
            picadapter.setList(pic);
            picWall.setAdapter(picadapter);
            picWall.setVisibility(View.VISIBLE);
        } else {
            picWall.setVisibility(View.GONE);
        }

        btnfan.setTag(userIfo.getFansnum());
        if (userIfo.getIsfocus() == 1) {
            btnAttent.setTag(1);
            btnAttent.setBackgroundResource(R.drawable.btn_attent_di_selector);
            btnAttent.setTextColor(getResources().getColor(R.color.color_white));
            btnAttent.setText("取消关注");
        } else {
            btnAttent.setTag(0);
            btnAttent.setBackgroundResource(R.drawable.btn_send_enable_selector);
            btnAttent.setTextColor(getResources().getColor(R.color.color_white));
            btnAttent.setText("关注");
        }

        getPersonTopic();

        container.setVisibility(View.VISIBLE);
    }

    private void AddPicture() {
        if (pic != null && pic.size() > 0) {
            pic.clear();
        }
        if (!TextUtils.isEmpty(userIfo.getBg1())) {
            pic.add(userIfo.getBg1());
        }
        if (!TextUtils.isEmpty(userIfo.getBg2())) {
            pic.add(userIfo.getBg2());
        }
        if (!TextUtils.isEmpty(userIfo.getBg3())) {
            pic.add(userIfo.getBg3());
        }
        if (!TextUtils.isEmpty(userIfo.getBg4())) {
            pic.add(userIfo.getBg4());
        }
        if (!TextUtils.isEmpty(userIfo.getBg5())) {
            pic.add(userIfo.getBg5());
        }
        if (!TextUtils.isEmpty(userIfo.getBg5())) {
            pic.add(userIfo.getBg5());
        }
        if (!TextUtils.isEmpty(userIfo.getBg6())) {
            pic.add(userIfo.getBg6());
        }
        if (!TextUtils.isEmpty(userIfo.getBg8())) {
            pic.add(userIfo.getBg8());
        }
    }

    /**
     * 帖子topicid，回帖cid，视频翻译vid，个人空间uid
     *
     * @param idtype
     * @param id
     * @param back
     * @param dispaly
     */
    private void CancleAttentionPerson() {
        showProgress();
        ApiParams params = new ApiParams();
        params.put("action", "cancel_focus");
        params.put("uid", app.getUserID());//
        params.put("username", app.getUserName());
        params.put("focus_uid", uid);
        params.put("checkcode", MyTools.getMD5_32(app.getUserName() + Constants.checkCode));
        ApiHelper.post(ctx, params, new ApiCallBack() {

            @Override
            public void receive(Result result) {
                try {
                    dismissProgress();
                    if (result.isSuccess()) {
                        JSONObject json = JSON.parseObject(result.getObj().toString());

                        MyToast(json.getString("info"));

                        if (json.getBooleanValue("success") || json.getString("info").equals("已经取消关注")) {
                            btnfan.setText("粉丝  " + ((Integer) btnfan.getTag() - 1));
                            btnfan.setTag((Integer) btnfan.getTag() - 1);
                            btnAttent.setTag(0);
                            btnAttent.setBackgroundResource(R.drawable.btn_send_enable_selector);
                            btnAttent.setTextColor(getResources().getColor(R.color.color_white));
                            btnAttent.setText("关注");
                        }

                    } else {
                        MyToast(result.getObj().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 帖子topicid，回帖cid，视频翻译vid，个人空间uid
     *
     * @param idtype
     * @param id
     * @param back
     * @param dispaly
     */
    private void AttentionPerson() {
        showProgress();
        ApiParams params = new ApiParams();
        params.put("action", "post_focus");
        params.put("uid", app.getUserID());//
        params.put("username", app.getUserName());
        params.put("focus_uid", uid);
        params.put("checkcode", MyTools.getMD5_32(app.getUserName() + Constants.checkCode));

        ApiHelper.post(ctx, params, new ApiCallBack() {

            @Override
            public void receive(Result result) {
                dismissProgress();
                if (result.isSuccess()) {
                    JSONObject json = JSON.parseObject(result.getObj().toString());

                    MyToast(json.getString("info"));

                    if (json.getBooleanValue("success")) {
                        btnAttent.setTag(1);
                        btnAttent.setBackgroundResource(R.drawable.btn_attent_di_selector);
                        btnAttent.setTextColor(getResources().getColor(R.color.color_white));
                        btnfan.setText("粉丝  " + ((Integer) btnfan.getTag() + 1));
                        btnfan.setTag((json.getIntValue("praise") + 1));
                        btnAttent.setText("取消关注");
                    } else if (json.getString("info").equals("已经关注")) {
                        btnAttent.setTag(1);
                        btnfan.setTag(userIfo.getFansnum());
                        btnAttent.setBackgroundResource(R.drawable.btn_attent_di_selector);
                        btnAttent.setTextColor(getResources().getColor(R.color.color_white));
                        btnAttent.setText("取消关注");
                    }

                } else {
                    MyToast(result.getObj().toString());
                }
            }
        });
    }

    /**
     * 获取栏目的内容
     */
    private void getPersonTopic() {
        showProgress();
        ApiParams params = new ApiParams();
        params.put("action", "get_topic");
        if (userIfo.getTopicnum() == 0) {
            params.put("by", "comment_username");
        } else {
            params.put("by", "username");
        }
        params.put("username", userName);
        params.put("page", 1);
        params.put("checkcode", MyTools.getMD5_32(userName + Constants.checkCode));

        ApiHelper.post(ctx, params, new ApiCallBack() {

            @Override
            public void receive(Result result) {

                dismissProgress();

                if (result.isSuccess()) {
                    JSONObject json = JSON.parseObject(result.getObj().toString());
                    if (json.getBooleanValue("success")) {
                        if (!TextUtils.isEmpty(json.getString("data"))) {
                            List<TopicContentBean> temp = JSON.parseArray(json.getString("data"), TopicContentBean.class);
                            if (temp != null && temp.size() > 0) {
                                perAdapter = new personAdapter(ctx);
                                perAdapter.setList(temp);
                                menList.setAdapter(perAdapter);
                                menList.setVisibility(View.VISIBLE);
                                Checkall.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                }
            }
        });
    }

    /**
     * 获取数据
     */
    private void getPersonInfo() {
        showProgress();
        ApiParams params = new ApiParams();
        params.put("action", "get_myspace");
        params.put("by", "uid");
        params.put("uid", uid);
        params.put("username", app.getUserName());
        params.put("checkcode", MyTools.getMD5_32(app.getUserName() + Constants.checkCode));

        ApiHelper.post(ctx, params, new ApiCallBack() {

            @Override
            public void receive(Result result) {
                dismissProgress();
                try {
                    if (result.isSuccess()) {
                        JSONObject json = JSON.parseObject(result.getObj().toString());
                        if (json.getBooleanValue("success")) {
                            if (!TextUtils.isEmpty(json.getString("data"))) {
                                userIfo = JSON.parseObject(json.getString("data"), UserIfoBean.class);
                                if (userIfo != null) {
                                    dealdata();
                                }
                            }
                        } else {
                            MyToast(json.getString("info"));
                        }
                    } else {
                        MyToast(result.getObj().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == EditResult) {
                getPersonInfo();
            }
        }
    }

    /***
     * 评论图片
     *
     * @author milanoouser
     */
    public class PicAdapter extends BaseListAdapter<String> {

        public PicAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            RoundImageView upLoadImage = null;
            if (convertView == null) {
                convertView = (LayoutInflater.from(ctx).inflate(R.layout.picture_add, null));
                upLoadImage = (RoundImageView) convertView.findViewById(R.id.addImage);
                convertView.setTag(upLoadImage);
            } else {
                upLoadImage = (RoundImageView) convertView.getTag();
            }

            upLoadImage.setRectAdius(4);
            imageLoader.displayImage(Constants.ServerTopicAdd + getList().get(position), upLoadImage, options);

            upLoadImage.setTag(position);
            upLoadImage.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(PersionPosition.this, FullScreenTakePic.class);
                        intent.putExtra("pictureArr", JSON.toJSONString(pic));
                        intent.putExtra("position", (Integer) v.getTag());
                        startActivity(intent);
                    } catch (Exception e) {
                    }
                }
            });

            return convertView;
        }
    }

    /**
     * @author Administrator
     */
    private class personAdapter extends BaseListAdapter<TopicContentBean> {
        public personAdapter(Context context) {
            super(context);
        }

        @Override
        public int getCount() {

            if (getList().size() > 3) {
                return 3;
            }
            return super.getCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.mytopic_title, null);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            TopicContentBean temp = getList().get(position);

            holder.title.setText(temp.getTitle());
            holder.postTime.setText(MyTools.getStandardDate(temp.getDateline()));

            holder.NumPost.setText(temp.getPraisenum() + "赞");
            if (Integer.valueOf(temp.getPraisenum()) == 0) {
                holder.NumPost.setTextColor(getResources().getColor(R.color.color_line1));
            } else {
                holder.NumPost.setTextColor(getResources().getColor(R.color.color_yellow));
            }
            holder.NumRelay.setText(temp.getCommentnum() + "回复");
            if (Integer.valueOf(temp.getCommentnum()) == 0) {
                holder.NumRelay.setTextColor(getResources().getColor(R.color.color_line1));
            } else {
                holder.NumRelay.setTextColor(getResources().getColor(R.color.color_yellow));
            }


            return convertView;
        }


        private class ViewHolder {
            private TextView title;
            private TextView postTime;
            private TextView NumPost;
            private TextView NumRelay;

            public ViewHolder(View view) {
                title = (TextView) view.findViewById(R.id.title);
                postTime = (TextView) view.findViewById(R.id.postTime);
                NumPost = (TextView) view.findViewById(R.id.NumPost);
                NumRelay = (TextView) view.findViewById(R.id.NumRelay);
                view.setTag(this);
            }
        }
    }
}
