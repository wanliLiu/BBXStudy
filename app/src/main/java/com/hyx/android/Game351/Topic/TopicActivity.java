package com.hyx.android.Game351.Topic;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
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
import com.hyx.android.Game351.modle.TopicBean;
import com.hyx.android.Game351.modle.TopicContentBean;
import com.hyx.android.Game351.util.DisplayUtil;
import com.hyx.android.Game351.util.MyTools;
import com.hyx.android.Game351.view.CircularImageView;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.NonScrollGridView;
import com.hyx.android.Game351.view.VoiceView;
import com.hyx.android.Game351.view.VoiceView.onVoiceStart;

import java.io.File;
import java.util.List;

import br.com.dina.ui.model.RoundImageView;

public class TopicActivity extends BaseActivity {

    /**
     *
     */
    private static final int StartPlay = 1;
    private static final int StopPlay = 2;
    private static final int PlayParpare = 3;
    private HeadView headView;
    private PullToRefreshListView topicListFather;
    private NonScrollGridView topicCategory;
    private ListView topicContnet;
    private catAdapter catadapter;
    private conAdapter adapter;
    private VoiceTool tool;
    private int page = 1;
    private boolean isShowProgressBar = false;
    private boolean isHaveData = false;
    /**
     * 第一次启动的时候加载数据
     */
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            //如果缓存有数据的话，那么就先显示，然后在更新
            String mtopAdvert = app.getCache().getAsString("TopicBean");
            String mButtomAdvert = app.getCache().getAsString("TopicContentBean");
            if (TextUtils.isEmpty(mtopAdvert) || TextUtils.isEmpty(mButtomAdvert)) {
                isShowProgressBar = true;
                getTopic();
            } else {
                if (!TextUtils.isEmpty(mtopAdvert)) {

                    List<TopicBean> topAdvert = JSON.parseArray(mtopAdvert, TopicBean.class);
                    if (topAdvert != null && topAdvert.size() > 0) {
                        catadapter.setList(topAdvert);
                        topicListFather.setVisibility(View.VISIBLE);
                    }
                }
                if (!TextUtils.isEmpty(mButtomAdvert)) {
                    List<TopicContentBean> buttomAdvert = JSON.parseArray(mButtomAdvert, TopicContentBean.class);
                    if (buttomAdvert != null && buttomAdvert.size() > 0) {
                        adapter.addAll(buttomAdvert);
                        topicListFather.setVisibility(View.VISIBLE);
                    }
                }
                isHaveData = true;
                //更新数据
                topicListFather.setRefreshing();
            }
        }

        ;
    };
    private Handler voicHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case StartPlay:
                    tool.StartPlay();
                    break;
                case StopPlay:
                    tool.stopPlay();
                    break;
                case PlayParpare:
                    if (app.isNetworkAvailable()) {
                        tool.downloadFile();
                    } else {
                        MyToast("网络异常，请尝试");
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void initView() {

        setContentView(R.layout.topic_activity);

        headView = (HeadView) findViewById(R.id.headView);
        headView.setTitle("贴吧");

        topicListFather = (PullToRefreshListView) findViewById(R.id.topicList);
        topicListFather.setBackgroundColor(getResources().getColor(R.color.backgound));
        topicContnet = topicListFather.getRefreshableView();
        topicListFather.setVisibility(View.INVISIBLE);

        View view = LayoutInflater.from(ctx).inflate(R.layout.topic_head_view, null);
        topicCategory = (NonScrollGridView) view.findViewById(R.id.TopicCategory);

        topicContnet.addHeaderView(view);

        tool = new VoiceTool(voicHandler, ctx);
    }

    @Override
    protected void initListener() {

        topicCategory.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(ctx, TopicCategoryActivity.class);
                intent.putExtra("title", catadapter.getList().get(position).getCatname());
                intent.putExtra("catid", catadapter.getList().get(position).getCatid());
                startActivity(intent);
            }
        });

        topicContnet.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(ctx, TopicRelyActivity.class);
                intent.putExtra("topicContent", adapter.getList().get(position - 2));
                startActivity(intent);
            }
        });

        topicListFather.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (!isHaveData) {
                    if (catadapter.getList() != null && catadapter.getList().size() > 0) {
                        catadapter.getList().clear();
                    }
                    if (adapter.getList() != null && adapter.getList().size() > 0) {
                        adapter.getList().clear();
                    }
                }
                page = 1;
                getTopic();
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

        adapter = new conAdapter(ctx);
        topicContnet.setAdapter(adapter);

        catadapter = new catAdapter(ctx);
        topicCategory.setAdapter(catadapter);

        // 启动加载相关数据
        handler.sendEmptyMessageDelayed(0, 500);
    }

    /**
     * 获取栏目的内容
     */
    private void getTopicContent() {
        ApiParams params = new ApiParams();
        params.put("action", "get_topic");
        params.put("by", "catid");
        params.put("username", app.getUserName());//
        params.put("catid", "0");
        params.put("page", page);
        params.put("flag", "1");
        params.put("checkcode", MyTools.getMD5_32(app.getUserName() + Constants.checkCode));

        ApiHelper.post(ctx, params, new ApiCallBack() {

            @Override
            public void receive(Result result) {

                if (isShowProgressBar) {
                    dismissProgress();
                    isShowProgressBar = false;
                }

                topicListFather.onRefreshComplete();

                if (result.isSuccess()) {
                    JSONObject json = JSON.parseObject(result.getObj().toString());
                    if (json.getBooleanValue("success")) {
                        if (!TextUtils.isEmpty(json.getString("data"))) {
                            List<TopicContentBean> temp = JSON.parseArray(json.getString("data"), TopicContentBean.class);
                            if (temp != null && temp.size() > 0) {
                                if (isHaveData) {
                                    isHaveData = false;
                                    if (adapter.getList() != null && adapter.getList().size() > 0) {
                                        adapter.getList().clear();
                                    }
                                }
                                adapter.addAll(temp);
                                if (page == 1) {
                                    app.getCache().put("TopicContentBean", JSON.toJSONString(temp));
                                }
                                topicListFather.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    topicListFather.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            topicListFather.onRefreshComplete();
                        }
                    }, 100);
                    MyToast(result.getObj().toString());
                }
            }
        });
    }

    /**
     * 获取栏目的内容
     */
    private void getTopic() {
        if (isShowProgressBar) {
            showProgress();
        }

        ApiParams params = new ApiParams();
        params.put("action", "get_cat");
        params.put("by", "upid");
        params.put("username", app.getUserName());//
        params.put("upid", "0");
        params.put("checkcode", MyTools.getMD5_32(app.getUserName() + Constants.checkCode));

        ApiHelper.post(ctx, params, new ApiCallBack() {

            @Override
            public void receive(Result result) {

                try {
                    if (result.isSuccess()) {
                        JSONObject json = JSON.parseObject(result.getObj().toString());
                        if (json.getBooleanValue("success")) {
                            List<TopicBean> temp = JSON.parseArray(json.getString("data"), TopicBean.class);
                            if (temp != null && temp.size() > 0) {
                                catadapter.setList(temp);
                                if (page == 1) {
                                    app.getCache().put("TopicBean", JSON.toJSONString(temp));
                                }
                                topicListFather.setVisibility(View.VISIBLE);
                            }
                        } else {
                            MyToast(json.getString("info"));
                        }

                        getTopicContent();

                    } else {
                        dismissProgress();
                        topicListFather.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                topicListFather.onRefreshComplete();
                            }
                        }, 100);
                        MyToast(result.getObj().toString());
                    }

                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        tool.stopPlay();

        if (tool.getTempVoice() != null) {
            tool.getTempVoice().StopPlayIng();
        }

        File file = new File(tool.getRecordFilePath());
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 分类栏目
     */
    private class catAdapter extends BaseListAdapter<TopicBean> {

        public catAdapter(Context context) {
            super(context);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.topic_category_item, null);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            TopicBean temp = getList().get(position);
            holder.topicName.setText(temp.getCatname());
//			temp.setArticles("29169");
            int num = Integer.valueOf(temp.getArticles());
            if (num > 0) {
                holder.actionNum.setVisibility(View.VISIBLE);
                holder.actionNum.setText(temp.getArticles());
            } else {
                holder.actionNum.setVisibility(View.GONE);
            }

//			imageLoader.displayImage(Constants.ServerTopicAdd+temp.getPic(), holder.image,options);
            imageLoader.displayImage(temp.getPic(), holder.image, options);

            return convertView;
        }

        private class ViewHolder {
            public CircularImageView image;
            public TextView topicName;
            public TextView actionNum;

            public ViewHolder(View view) {
                image = (CircularImageView) view.findViewById(R.id.topicImage);
                topicName = (TextView) view.findViewById(R.id.topicName);
                actionNum = (TextView) view.findViewById(R.id.actionNum);

                view.setTag(this);
            }
        }
    }

    /**
     * @author acer
     */
    private class conAdapter extends BaseListAdapter<TopicContentBean> {
        private String hot = "<img src='" + R.drawable.icon_hot + "'/>";
        private CharSequence charSequence;

        public conAdapter(Context context) {
            super(context);
            charSequence = Html.fromHtml(hot, new ImageGetter() {

                @Override
                public Drawable getDrawable(String source) {
                    // TODO Auto-generated method stub
                    int id = Integer.parseInt(source);
                    Drawable d = getResources().getDrawable(id);
                    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                    return d;
                }
            }, null);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.topic_item, null);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            TopicContentBean temp = getList().get(position);

            if (temp.getFlag() == 1) {
                holder.topicTitle.setText(charSequence);
                holder.topicTitle.append(temp.getTitle());
            } else {
                holder.topicTitle.setText(temp.getTitle());
            }

            holder.topicContent.setText(temp.getContent());

            if (!TextUtils.isEmpty(temp.getAudio())) {
                holder.voiceContainer.setVisibility(View.VISIBLE);
                DisplayUtil.AutoSetVoiceViewLayout(holder.voiceContainer, holder.voice, Integer.parseInt(temp.getUsedtime()));
                holder.voice.setVoiceTime(Integer.parseInt(temp.getUsedtime()));
                holder.voice.setContentDescription(Constants.ServerTopicAdd + temp.getAudio());
                holder.voice.setTag(holder.voice);
                holder.voice.setOnVoiceStartListener(new onVoiceStart() {

                    @Override
                    public void onVoiceStartPlay(final View view) {
                        boolean isshould = false;
                        if (tool.getTempVoice() != null && tool.getTempVoice().isPlaying()) {
                            voicHandler.sendEmptyMessage(StopPlay);
                            VoiceView temp = (VoiceView) view;
                            if (tool.getLastVoice() != temp) {
                                isshould = true;
                            }
                        } else {
                            isshould = true;
                        }
                        if (isshould) {
                            topicContnet.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    tool.setTempVoice((VoiceView) view);
                                    if (tool.getTempVoice() != null) {
                                        tool.setRadioPath(tool.getTempVoice().getContentDescription().toString());
                                        voicHandler.sendEmptyMessage(PlayParpare);
                                    }
                                }
                            }, 10);

                        }
                    }
                });
            } else {
                holder.voiceContainer.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(temp.getPic())) {
                holder.image.setVisibility(View.VISIBLE);
                imageLoader.displayImage(Constants.ServerTopicAdd + temp.getPic(), holder.image, options);
            } else {
                holder.image.setVisibility(View.GONE);
            }
            holder.name.setText(temp.getUsername());
            holder.time.setText(MyTools.getStandardDate(temp.getDateline()));

            holder.goodNum.setText(temp.getPraisenum() + "赞");
            if (Integer.valueOf(temp.getPraisenum()) == 0) {
                holder.goodNum.setTextColor(getResources().getColor(R.color.color_line1));
            } else {
                holder.goodNum.setTextColor(getResources().getColor(R.color.color_yellow));
            }
            holder.relyNum.setText(temp.getCommentnum() + "回复");
            if (Integer.valueOf(temp.getCommentnum()) == 0) {
                holder.relyNum.setTextColor(getResources().getColor(R.color.color_line1));
            } else {
                holder.relyNum.setTextColor(getResources().getColor(R.color.color_yellow));
            }
            return convertView;
        }

        private class ViewHolder {
            public RoundImageView image;
            public TextView topicTitle;
            public TextView topicContent;
            public TextView name;
            public TextView time;
            public TextView relyNum;
            public TextView goodNum;

            public VoiceView voice;

            public LinearLayout voiceContainer;

            public ViewHolder(View view) {

                image = (RoundImageView) view.findViewById(R.id.topicPicture);
                topicTitle = (TextView) view.findViewById(R.id.topicTitle);
                topicContent = (TextView) view.findViewById(R.id.topicContent);
                name = (TextView) view.findViewById(R.id.name);
                time = (TextView) view.findViewById(R.id.time);
                relyNum = (TextView) view.findViewById(R.id.relyNum);
                goodNum = (TextView) view.findViewById(R.id.goodNum);
                voice = (VoiceView) view.findViewById(R.id.voicePlay);

                voiceContainer = (LinearLayout) view.findViewById(R.id.voiceContainer);

                view.setTag(this);
            }
        }
    }
}
