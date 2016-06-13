package com.hyx.android.Game351.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.base.BaseListAdapter;
import com.hyx.android.Game351.base.Constants;
import com.hyx.android.Game351.data.ApiCallBack;
import com.hyx.android.Game351.data.ApiHelper;
import com.hyx.android.Game351.data.ApiParams;
import com.hyx.android.Game351.data.Result;
import com.hyx.android.Game351.modle.MenuDataBean;
import com.hyx.android.Game351.modle.ScoreBean;
import com.hyx.android.Game351.modle.SubjectBean;
import com.hyx.android.Game351.util.ApkType;
import com.hyx.android.Game351.util.MyTools;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.HeadView.OnBackBtnListener;

import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class ScoreActivity extends BaseActivity {

    private HeadView headView;

    private TextView scorePaixu;
    private ListView scoreList;

    private Button ChannelFast, NewChannel, ReadAll, ShareFrieds;
    private scoreAdaper adaper;

    private String titleString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (MyTools.getCurrentApkType(this) == ApkType.TYPE_MEIJU) {
            isScreenLandsape = true;
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.score_activity);

        headView = (HeadView) findViewById(R.id.headView);
        headView.setTitle(getIntent().getStringExtra("title"));

        scorePaixu = (TextView) findViewById(R.id.scorePaixu);
        scoreList = (ListView) findViewById(R.id.scoreList);

        ChannelFast = (Button) findViewById(R.id.ChannelFast);
        NewChannel = (Button) findViewById(R.id.NewChannel);
        ReadAll = (Button) findViewById(R.id.ReadAll);
        ShareFrieds = (Button) findViewById(R.id.ShareFrieds);

        ChannelFast.setOnClickListener(this);
        NewChannel.setOnClickListener(this);
        ReadAll.setOnClickListener(this);
        ShareFrieds.setOnClickListener(this);

        LinearLayout layout = (LinearLayout) findViewById(R.id.bottomAction);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead ||
                MyTools.getCurrentApkType(this) == ApkType.TYPE_MEIJU) {
            ChannelFast.setText("再来一遍");
            NewChannel.setText("新的复读");

            findViewById(R.id.incopyRead).setVisibility(View.GONE);
            params.height = MyTools.dip2px(ctx, 100f);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        layout.setLayoutParams(params);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ChannelFast: {
                Intent intent = new Intent(ctx, MakeSubgect.class);
                intent.putExtra("FistId", getIntent().getStringExtra("FistId"));
                intent.putExtra("secodeId", getIntent().getStringExtra("secodeId"));
                intent.putExtra("data", getIntent().getStringExtra("data"));
                intent.putExtra("title", getIntent().getStringExtra("title"));
                intent.putExtra("AllSubjects", getIntent().getStringExtra("AllSubjects"));
                intent.putExtra("currentPosition", getIntent().getIntExtra("currentPosition", -1));
                startActivity(intent);
                this.finish();
            }
            break;
            case R.id.NewChannel: {
                int currentPosition = getIntent().getIntExtra("currentPosition", -1);
                if (currentPosition != -1) {
                    MenuDataBean temp = JSON.parseObject(getIntent().getStringExtra("AllSubjects"), MenuDataBean.class);
                    if (temp != null && temp.getData() != null && temp.getData().size() > 0 && currentPosition + 1 < temp.getData().size()) {
                        titleString = temp.getData().get(currentPosition + 1).getSort_name();
                        GetdataSubject(temp.getData().get(currentPosition + 1).getSort_id(), currentPosition + 1);
                    } else {
                        MyToast("你已经做完当前所有的题目了，请换一下个！！！");
                        this.finish();
                    }
                }
            }
            break;
            case R.id.ReadAll: {
                List<SubjectBean> temp = JSON.parseArray(getIntent().getStringExtra("data"), SubjectBean.class);
                if (temp != null && temp.size() > 0) {
                    int index = 1;
                    StringBuffer buffer = new StringBuffer("");
                    for (SubjectBean subjectBean : temp) {
                        buffer.append(index + "." + "\n");
                        buffer.append("问题：" + subjectBean.getQuestion() + "\n");
                        buffer.append("答案：" + getString(subjectBean.getAnswer().split("\\|")) + "\n\n");
                        ++index;
                    }
                    if (buffer.length() > 0) {
                        Intent intent = new Intent(ctx, ReadAllActivity.class);
                        intent.putExtra("content", buffer.toString());
                        startActivity(intent);
                    }
                }
            }
            break;
            case R.id.ShareFrieds: {
                ShareSDK.initSDK(this);
                OnekeyShare oks = new OnekeyShare();
                //关闭sso授权
                oks.disableSSOWhenAuthorize();
                // 分享时Notification的图标和文字
                oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
                // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                oks.setTitle("分享");
                // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                oks.setTitleUrl("http://www.tgbbx.com");
                // text是分享文本，所有平台都需要这个字段
                oks.setText("全中国最简单,最快学好英语的方法,10句60秒搞定。我用过了非常靠谱,强烈推荐。详见官网：http://www.tgbbx.com");
                // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//	        oks.setImagePath("/sdcard/test.jpg");
                oks.setImageUrl("http://www.tgbbx.com/xici/star.png");
                // url仅在微信（包括好友和朋友圈）中使用
                oks.setUrl("http://www.tgbbx.com");
                // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//	        oks.setComment("我是测试评论文本");
                // site是分享此内容的网站名称，仅在QQ空间使用
                oks.setSite(getString(R.string.app_name));
                // siteUrl是分享此内容的网站地址，仅在QQ空间使用
                oks.setSiteUrl("http://www.tgbbx.com");
                oks.setSilent(true);
                // 启动分享GUI
                oks.setDialogMode();
                oks.show(this);
            }
            break;
            default:
                break;
        }
    }

    /**
     * @return
     */
    private String getString(String[] answer) {
        String temp = "";

        for (int i = 0; i < answer.length; i++) {

            if (i % 2 == 0) {
                temp += answer[i] + " ";
            }
        }

        return temp;
    }

    /**
     * 获取题目列表
     */
    private void GetdataSubject(String sid, final int currentPosition) {
        showProgress();

        ApiParams params = new ApiParams();
        params.put("action", "getSubject");
        params.put("username", app.getUserName());
        params.put("sid", sid);
        params.put("logindate", app.getUserLoginDate());
        params.put("checkcode", MyTools.getMD5_32(app.getUserName() + Constants.checkCode));

        ApiHelper.get(ctx, params, new ApiCallBack() {

            @Override
            public void receive(Result result) {
                dismissProgress();

                if (result.isSuccess()) {
                    JSONObject json = JSON.parseObject(result.getObj().toString());
                    if (json.getBooleanValue("success")) {
                        List<SubjectBean> temp = JSON.parseArray(json.getString("data"), SubjectBean.class);
                        if (temp != null && temp.size() > 0) {
                            Intent intent = new Intent(ScoreActivity.this, MakeSubgect.class);
                            intent.putExtra("data", json.getString("data"));
                            intent.putExtra("title", titleString);
                            intent.putExtra("FistId", getIntent().getStringExtra("FistId"));
                            intent.putExtra("secodeId", getIntent().getStringExtra("secodeId"));
                            intent.putExtra("AllSubjects", getIntent().getStringExtra("AllSubjects"));
                            intent.putExtra("currentPosition", currentPosition);
                            startActivity(intent);
                            finish();
                        }
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

        if (getIntent().getBooleanExtra("way", false)) {
            // 没有按下一题
            getScore();
        }
    }

    /**
     * 获取排名
     */
    private void getScore() {

        showProgress();

        ApiParams params = new ApiParams();
        params.put("action", "getScore");
        params.put("username", app.getUserName());
        params.put("sid", getIntent().getStringExtra("sid"));
        params.put("score", getIntent().getStringExtra("useTime"));
        params.put(
                "checkcode",
                MyTools.getMD5_32(app.getUserName()
                        + getIntent().getStringExtra("sid")
                        + getIntent().getStringExtra("useTime")
                        + Constants.checkCode));

        ApiHelper.get(ctx, params, new ApiCallBack() {

            @Override
            public void receive(Result result) {

                dismissProgress();

                if (result.isSuccess()) {
                    JSONObject json = JSON.parseObject(result.getObj().toString());
                    if (json.getBooleanValue("success")) {
                        List<ScoreBean> temp = JSON.parseArray(json.getString("scores"), ScoreBean.class);
                        if (temp != null && temp.size() > 0) {
                            int ruank = 0, score = 0;
                            for (int i = 0; i < temp.size(); i++) {
                                if (temp.get(i).getUser_name().equalsIgnoreCase(app.getUserName())) {
                                    ruank = temp.get(i).getNum();
                                    score = temp.get(i).getScore();
                                    break;
                                }
                            }
                            scorePaixu.setText("你的排名" + ruank + ",用时" + score + "秒");
                            adaper = new scoreAdaper(ctx);
                            adaper.setList(temp);
                            scoreList.setAdapter(adaper);
                        }
                    } else {
                        MyToast(json.getString("info"));
                    }
                } else {
                    MyToast(result.getObj().toString());
                }
            }
        });
    }

    /**
     * 显示分数的适配器
     *
     * @author acer
     */
    private class scoreAdaper extends BaseListAdapter<ScoreBean> {
        public scoreAdaper(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.score_item, null);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.paiMing.setText(getList().get(position).getNum() + "");
            holder.UserName.setText(getList().get(position).getUser_name());
            holder.useTime.setText(getList().get(position).getScore() + "");

            return convertView;
        }

        private class ViewHolder {
            public TextView paiMing;
            public TextView UserName;
            public TextView useTime;

            public ViewHolder(View view) {

                paiMing = (TextView) view.findViewById(R.id.paiMing);
                UserName = (TextView) view.findViewById(R.id.UserName);
                useTime = (TextView) view.findViewById(R.id.useTime);
                view.setTag(this);
            }
        }
    }
}
