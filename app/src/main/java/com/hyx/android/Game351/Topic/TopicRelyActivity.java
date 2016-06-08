package com.hyx.android.Game351.Topic;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hyx.android.Game351.Personal.PersionPosition;
import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.base.BaseListAdapter;
import com.hyx.android.Game351.base.Constants;
import com.hyx.android.Game351.data.ApiCallBack;
import com.hyx.android.Game351.data.ApiHelper;
import com.hyx.android.Game351.data.ApiParams;
import com.hyx.android.Game351.data.Result;
import com.hyx.android.Game351.modle.RelayBean;
import com.hyx.android.Game351.modle.TopicContentBean;
import com.hyx.android.Game351.util.DisplayUtil;
import com.hyx.android.Game351.util.MLog;
import com.hyx.android.Game351.util.MyTools;
import com.hyx.android.Game351.view.CircularImageView;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.HeadView.OnBackBtnListener;
import com.hyx.android.Game351.view.VoiceView;
import com.hyx.android.Game351.view.VoiceView.onVoiceStart;
import com.kubility.demo.MP3Recorder;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import br.com.dina.ui.model.RoundImageView;


/**
 * http://www.tgbbx.com/api/test/list.php
 * http://www.lklun.com/thread-4482-1-1.html-----获取音量的大小
 *
 * @author acer
 */
public class TopicRelyActivity extends BaseActivity {

    public static final int REQUEST_LOCAL_PRICTURE_CHOOSE = 2507;
    public static final int REQUEST_CAPTURE_IMAGE = 2506;

    private static final int StartRecordVoice = 11;
    private static final int StopRecordVoice = 22;
    private static final int VoiceRecordTime = 42;
    /**
     *
     */
    private static final int StartPlay = 1;
    private static final int StopPlay = 2;
    private static final int PlayParpare = 3;
    private String recordFilePath = "";
    private String pictureSelctePath = "";
    private HeadView headView;
    private ImageButton relay_voice, relay_media, btnPictureSelect, btnCameraSelect, imageDelete;
    private ImageView voiceWarning, mediaWarning;
    private Button btnRelay, btnRecord, btnReset;
    private EditText content;
    private FrameLayout ChoseLayout;
    private MediaPlayer mPlayer = null;
    private VoiceView btnVoice;
    private RelativeLayout recordShow, RecordStartShow, RecordDoneShow, MediaOpenShow, PicturedoneShow;
    private TextView recordTimeShow;
    private int recordTime = 0;
    private RoundImageView addImage;
    private int action = 0;
    private int[] who = new int[2];
    private TopicContentBean topic;
    private VoiceTool tool;
    /**
     *
     */
    private TextView userName;
    private TextView time;
    private TextView topicTitle;
    private VoiceView voice;
    private TextView topicContent;
    private RoundImageView image;
    private RelativeLayout pariseBac;
    private TextView PariseDisplay;
    private Button btnRelayUser;
    private TextView relayNum;
    private ImageView voiceLevel;
    private PullToRefreshListView topicListRelayFather;
    private ListView topicListRelay;
    private CircularImageView UserImage;
    private relayAdapter adapter;
    private LinearLayout containter;
    private boolean isFirst = true;
    private int page = 1;
    private boolean isFromRelayRefresh = false;
    private MP3Recorder recorder = null;
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
    /**
     * 录音处理流程
     */
    private Handler voiceRecordHander = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case MP3Recorder.MSG_REC_STOPPED:
                    break;
                case MP3Recorder.MSG_REC_VOLEM:
                    updateMicStatus((Integer) msg.obj);
                    break;
                case StartRecordVoice:
                    VoiceRecordStart();
                    break;
                case StopRecordVoice:
                    VoiceRecordStop();
                    break;
                case MP3Recorder.MSG_REC_STARTED:
                case MP3Recorder.MSG_REC_RESTORE:
                case VoiceRecordTime:
                    recordTime++;
                    recordTimeShow.setText(recordTime + "\"");
                    voiceRecordHander.sendEmptyMessageDelayed(VoiceRecordTime, 1000);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void initView() {
        setContentView(R.layout.topic_relay_activity);

        headView = (HeadView) findViewById(R.id.headView);
        headView.getBtnBack().setImageResource(R.drawable.back);
        headView.setTitle("详情");

        btnPictureSelect = (ImageButton) findViewById(R.id.btnPictureSelect);
        btnCameraSelect = (ImageButton) findViewById(R.id.btnCameraSelect);
        relay_voice = (ImageButton) findViewById(R.id.btnRelyVoice);
        relay_media = (ImageButton) findViewById(R.id.btnRelyMedia);
        imageDelete = (ImageButton) findViewById(R.id.imageDelete);

        addImage = (RoundImageView) findViewById(R.id.addImage);

        voiceWarning = (ImageView) findViewById(R.id.voiceWarning);
        mediaWarning = (ImageView) findViewById(R.id.mediaWaring);

        btnRelay = (Button) findViewById(R.id.btnSend);
        btnRecord = (Button) findViewById(R.id.btnRecord);
        btnReset = (Button) findViewById(R.id.btnReset);

        ChoseLayout = (FrameLayout) findViewById(R.id.addAnother);
        content = (EditText) findViewById(R.id.RelyContent);

        recordShow = (RelativeLayout) findViewById(R.id.voiceRecodAtte);
        recordTimeShow = (TextView) findViewById(R.id.recordTime);

        RecordStartShow = (RelativeLayout) findViewById(R.id.btnRecordStart);
        RecordDoneShow = (RelativeLayout) findViewById(R.id.btnRecordDone);
        MediaOpenShow = (RelativeLayout) findViewById(R.id.btnMediaOpen);
        PicturedoneShow = (RelativeLayout) findViewById(R.id.btnPicturedone);

        voiceLevel = (ImageView) findViewById(R.id.voiceLevel);
        btnVoice = (VoiceView) findViewById(R.id.btnVoice);
        /**
         *
         */
        View view = LayoutInflater.from(ctx).inflate(R.layout.topic_relay_header, null);
        UserImage = (CircularImageView) view.findViewById(R.id.UserImage);
        userName = (TextView) view.findViewById(R.id.userName);
        time = (TextView) view.findViewById(R.id.time);
        topicTitle = (TextView) view.findViewById(R.id.topicTitle);
        voice = (VoiceView) view.findViewById(R.id.voicePlay);
        containter = (LinearLayout) view.findViewById(R.id.containter);
        topicContent = (TextView) view.findViewById(R.id.topicContent);
        image = (RoundImageView) view.findViewById(R.id.image);
        image.setRectAdius(4);
        pariseBac = (RelativeLayout) view.findViewById(R.id.pariseBac);
        PariseDisplay = (TextView) view.findViewById(R.id.PariseDisplay);
        btnRelayUser = (Button) view.findViewById(R.id.btnRelay);
        relayNum = (TextView) view.findViewById(R.id.relayNum);

        topicListRelayFather = (PullToRefreshListView) findViewById(R.id.topicListRelay);
        topicListRelayFather.setBackgroundColor(getResources().getColor(R.color.backgound));
        topicListRelay = topicListRelayFather.getRefreshableView();

        topicListRelay.addHeaderView(view);

        tool = new VoiceTool(voicHandler, ctx);
    }

    @Override
    protected void initListener() {

        headView.setOnBackBtnListener(new OnBackBtnListener() {

            @Override
            public void onClick() {
                onBackPressed();
            }
        });

        btnReset.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                VoiceDelete();
            }
        });

        btnVoice.setOnVoiceStartListener(new onVoiceStart() {

            @Override
            public void onVoiceStartPlay(View view) {
                StartPlayOrStop();
            }
        });

        relay_voice.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                who[action] = 23;
                DisplayAction();

                MediaOpenShow.setVisibility(View.GONE);
                PicturedoneShow.setVisibility(View.GONE);

                if (voiceWarning.getVisibility() == View.VISIBLE) {
                    RecordDoneShow.setVisibility(View.VISIBLE);
                    RecordStartShow.setVisibility(View.GONE);
                } else {
                    RecordDoneShow.setVisibility(View.GONE);
                    RecordStartShow.setVisibility(View.VISIBLE);
                }
            }
        });

        relay_media.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                who[action] = 12;
                DisplayAction();

                RecordStartShow.setVisibility(View.GONE);
                RecordDoneShow.setVisibility(View.GONE);

                if (mediaWarning.getVisibility() == View.VISIBLE) {
                    PicturedoneShow.setVisibility(View.VISIBLE);
                    MediaOpenShow.setVisibility(View.GONE);
                } else {
                    PicturedoneShow.setVisibility(View.GONE);
                    MediaOpenShow.setVisibility(View.VISIBLE);
                }
            }
        });

        content.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (TextUtils.isEmpty(s.toString().trim())) {
                    if (voiceWarning.getVisibility() == View.GONE && mediaWarning.getVisibility() == View.GONE) {
                        btnSendOption(false);
                    }
                } else {
                    btnSendOption(true);
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

        btnRelay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //提交评论
                relayTopic();
            }
        });

        btnRecord.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btnRecord.setText("松开结束");
                    voiceRecordHander.sendEmptyMessage(StartRecordVoice);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btnRecord.setText("请按住我说话");
                    voiceRecordHander.sendEmptyMessage(StopRecordVoice);
                }
                return false;
            }
        });

        btnCameraSelect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pictureSelctePath = ctx.getExternalCacheDir() + "/relay_picture_select.jpg";
                // 打开Camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(pictureSelctePath)));
                startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
            }
        });

        btnPictureSelect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_LOCAL_PRICTURE_CHOOSE);
            }
        });

        imageDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ImageDelete();
            }
        });

        voice.setOnVoiceStartListener(new onVoiceStart() {

            @Override
            public void onVoiceStartPlay(View view) {
                actionVoice((VoiceView) view);
            }
        });

        pariseBac.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pariseNum("topicid", topic.getTopicid(), pariseBac, PariseDisplay, null);
            }
        });

        btnRelayUser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                btnRelay.setTag("topicid");
                btnRelay.setContentDescription(topic.getTopicid());
                content.setContentDescription("@" + topic.getUsername() + ":");
                content.setHint("@" + topic.getUsername());
                ShowKeyboard(content);
            }
        });

        topicListRelayFather.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (adapter.getList() != null && adapter.getList().size() > 0) {
                    adapter.getList().clear();
                }

                if (isFirst) {
                    adapter.add(new RelayBean());
                }

                page = 1;
                getRelayList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page += 1;
                getRelayList();
            }
        });

        UserImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, PersionPosition.class);
                intent.putExtra("uid", topic.getUid());
                intent.putExtra("UserName", topic.getUsername());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {

        topic = (TopicContentBean) getIntent().getSerializableExtra("topicContent");
        if (topic == null) {
            finish();
        }

        takeCareOfContent();

        adapter = new relayAdapter(ctx);
        adapter.add(new RelayBean());
        topicListRelay.setAdapter(adapter);

        getRelayList();
    }

    /**
     * 删除选中的图片
     */
    private void ImageDelete() {
        mediaWarning.setVisibility(View.GONE);
        MediaOpenShow.setVisibility(View.VISIBLE);
        PicturedoneShow.setVisibility(View.GONE);
        pictureSelctePath = "";
        if (voiceWarning.getVisibility() == View.GONE) {
            btnSendOption(false);
        }
    }

    /**
     * 删除声音文件
     */
    private void VoiceDelete() {
        voiceWarning.setVisibility(View.GONE);
        RecordStartShow.setVisibility(View.VISIBLE);
        RecordDoneShow.setVisibility(View.GONE);

        if (mediaWarning.getVisibility() == View.GONE) {
            btnSendOption(false);
        }

        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

        if (tool != null) {
            tool.stopPlay();

            if (tool.getTempVoice() != null) {
                tool.getTempVoice().StopPlayIng();
            }
        }
    }

    /**
     * 回到初始位置
     */
    private void backToBegain() {
        ImageDelete();

        VoiceDelete();

        content.setText("");

        btnRelay.setTag("topicid");
        btnRelay.setContentDescription(topic.getTopicid());
        content.setContentDescription("@" + topic.getUsername() + ":");
        content.setHint("@" + topic.getUsername());

        HideKeyboard(content);

        isFromRelayRefresh = true;

        topicListRelayFather.postDelayed(new Runnable() {

            @Override
            public void run() {
                topicListRelayFather.setRefreshing();
            }
        }, 500);
    }

    /**
     * 回帖
     *
     * @param idtype
     * @param id
     */
    private void relayTopic() {
        try {

            who[0] = who[1] = 0;
            DisplayAction();
            HideKeyboard();

            showProgress();
            RequestParams params = new RequestParams();

            params.put("action", "post_comment");
            params.put("uid", app.getUserID());
            params.put("username", app.getUserName());//
            params.put("idtype", btnRelay.getTag().toString());
            params.put("id", btnRelay.getContentDescription().toString());
            params.put("msgtype", "0");

            if (mediaWarning.getVisibility() == View.VISIBLE && !TextUtils.isEmpty(pictureSelctePath)) {
                File file = new File(pictureSelctePath);
                if (file.exists()) {
                    params.put("pic[]", file);
                }
            }

            if (voiceWarning.getVisibility() == View.VISIBLE && !TextUtils.isEmpty(recordFilePath)) {
                File file = new File(recordFilePath);
                if (file.exists()) {
                    params.put("user_upload_file[]", file);
                }
            }

            params.put("usedtime", recordTime);

            params.put("message", content.getContentDescription().toString() + content.getText().toString());

            params.put("checkcode", MyTools.getMD5_32(app.getUserName() + Constants.checkCode));

            ApiHelper.uploadFile(ctx, params, new ApiCallBack() {

                @Override
                public void receive(Result result) {
                    try {
                        dismissProgress();
                        if (result.isSuccess()) {
                            JSONObject json = JSON.parseObject(result.getObj().toString());
                            MyToast(json.getString("info"));
                            if (json.getBooleanValue("success")) {
                                backToBegain();
                            }
                        } else {
                            MyToast(result.getObj().toString());
                        }
                    } catch (Exception e) {
                        MyToast("上传图片出错");
                    }
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
    private void pariseNum(final String idtype, String id, final RelativeLayout back, final TextView dispaly, final RelayBean temp) {
        showProgress();
        ApiParams params = new ApiParams();
        params.put("action", "praise_topic");
        params.put("username", app.getUserName());//
        params.put("idtype", idtype);
        params.put("id", id);
        params.put("checkcode", MyTools.getMD5_32(app.getUserName() + Constants.checkCode));

        ApiHelper.post(ctx, params, new ApiCallBack() {

            @Override
            public void receive(Result result) {
                dismissProgress();
                if (result.isSuccess()) {
                    JSONObject json = JSON.parseObject(result.getObj().toString());

                    MyToast(json.getString("info"));

                    if (json.getBooleanValue("success")) {
                        back.setBackgroundResource(R.drawable.btn_like_hit);
                        dispaly.setTextColor(getResources().getColor(R.color.color_white));
                        if (idtype.equals("cid")) {
                            temp.setParise(true);
                            temp.setPraisenum(json.getIntValue("praise") + 1 + "");
                            dispaly.setText(json.getIntValue("praise") + 1 + "");
                        } else {
                            dispaly.setText("赞(" + (json.getIntValue("praise") + 1) + ")");
                        }

                        back.setEnabled(false);
                    } else if (json.getString("info").equals("已经点赞")) {
                        if (idtype.equals("cid")) {
                            temp.setParise(true);
                        }
                        back.setBackgroundResource(R.drawable.btn_like_hit);
                        dispaly.setTextColor(getResources().getColor(R.color.color_white));
                        back.setEnabled(false);
                    }

                } else {
                    MyToast(result.getObj().toString());
                }
            }
        });
    }

    /**
     *
     */
    private void getRelayList() {
        if (isFirst) {
            showProgress();
        }

        ApiParams params = new ApiParams();
        params.put("action", "get_comment");
        params.put("by", "topicid");
        params.put("username", app.getUserName());
        params.put("id", topic.getTopicid());
        params.put("page", page);
        params.put("checkcode", MyTools.getMD5_32(app.getUserName() + Constants.checkCode));

        ApiHelper.post(ctx, params, new ApiCallBack() {

            @Override
            public void receive(Result result) {

                if (isFirst) {
                    dismissProgress();
                }

                topicListRelayFather.onRefreshComplete();

                if (result.isSuccess()) {
                    JSONObject json = JSON.parseObject(result.getObj().toString());
                    if (json.getBooleanValue("success")) {
                        if (!TextUtils.isEmpty(json.getString("data"))) {
                            List<RelayBean> temp = JSON.parseArray(json.getString("data"), RelayBean.class);
                            if (temp != null && temp.size() > 0) {
                                if (isFirst) {
                                    isFirst = false;
                                    adapter.setIsStart(false);
                                    adapter.removeAll();
                                }

                                if (isFromRelayRefresh) {
                                    if (adapter.getList() != null && adapter.getList().size() > 0) {
                                        adapter.getList().clear();
                                    }
                                    isFromRelayRefresh = false;
                                    topicListRelay.postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            topicListRelay.setSelection(adapter.getList().size() - 1);
                                        }
                                    }, 500);
                                }

                                adapter.addAll(temp);
                                relayNum.setText("跟帖(" + temp.size() + ")");
                            }
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
     * @param voice
     * @return
     */
    private void actionVoice(final VoiceView voice) {
        boolean isshould = false;
        if (tool.getTempVoice() != null && tool.getTempVoice().isPlaying()) {
            voicHandler.sendEmptyMessage(StopPlay);
            if (tool.getLastVoice() != voice) {
                isshould = true;
            }
        } else {
            isshould = true;
        }
        if (isshould) {
            topicListRelay.postDelayed(new Runnable() {

                @Override
                public void run() {
                    tool.setTempVoice(voice);
                    if (tool.getTempVoice() != null) {
                        tool.setRadioPath(tool.getTempVoice().getContentDescription().toString());
                        voicHandler.sendEmptyMessage(PlayParpare);
                    }
                }
            }, 10);

        }
    }

    /**
     * 处理内容显示
     */
    private void takeCareOfContent() {
        if (TextUtils.isEmpty(topic.getUsername())) {
            userName.setText("Doney");
        } else {
            userName.setText(topic.getUsername());
        }

        time.setText(MyTools.getStandardDate(topic.getDateline()));

        topicTitle.setText(topic.getTitle());

        if (!TextUtils.isEmpty(topic.getAudio())) {
            voice.setContentDescription(Constants.ServerTopicAdd + topic.getAudio());
            containter.setVisibility(View.VISIBLE);
            voice.setVoiceTime(Integer.parseInt(topic.getUsedtime()));
            DisplayUtil.AutoSetVoiceViewLayout(containter, voice, Integer.parseInt(topic.getUsedtime()));
            voice.setTag(voice);
        } else {
            containter.setVisibility(View.GONE);
        }

        topicContent.setText(topic.getContent());

        if (!TextUtils.isEmpty(topic.getPic())) {
            image.setVisibility(View.VISIBLE);
//			DisplayUtil.AutoSetPictureLayout(image);
            imageLoader.displayImage(Constants.ServerTopicAdd + topic.getPic(), image, options);
        } else {
            image.setVisibility(View.GONE);
        }

        if (Integer.valueOf(topic.getPraisenum()) == 0) {
            PariseDisplay.setText("赞楼主");
        } else {
            PariseDisplay.setText("赞(" + topic.getPraisenum() + ")");
        }

        if (!TextUtils.isEmpty(topic.getAvatar())) {
            imageLoader.displayImage(Constants.ServerTopicAdd + topic.getAvatar(), UserImage, optionsHead);
        }

        btnRelay.setTag("topicid");
        btnRelay.setContentDescription(topic.getTopicid());
        content.setContentDescription("@" + topic.getUsername() + ":");
        content.setHint("@" + topic.getUsername());
    }

    /**
     * 是否显示的相应操作
     */
    private void DisplayAction() {
        HideKeyboard();
        action = (action + 1) % 2;
        if (who[0] == who[1]) {
            ChoseLayout.setVisibility(View.GONE);
            who[0] = who[1] = 0;
        } else {
            ChoseLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 把Uri转化成文件路径
     */
    private String uri2filePath(Uri uri) {
        try {
            String[] proj = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = managedQuery(uri, proj, null, null, null);
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(index);
            return path;
        } catch (Exception e) {
            return uri.getPath();
        }
    }

    /**
     * 显示图片
     *
     * @param filePath
     */
    private void setImage(String filePath) {
        try {
            mediaWarning.setVisibility(View.VISIBLE);
            MediaOpenShow.setVisibility(View.GONE);
            PicturedoneShow.setVisibility(View.VISIBLE);

            pictureSelctePath = filePath;
            int width = DisplayUtil.dip2px(70, ctx.getResources().getDisplayMetrics().density);
            addImage.setImageBitmap(DisplayUtil.getBitmapFromFile(new File(pictureSelctePath), width, width));
            btnSendOption(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_CAPTURE_IMAGE) {
                    setImage(pictureSelctePath);
                } else if (requestCode == REQUEST_LOCAL_PRICTURE_CHOOSE) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        setImage(uri2filePath(uri));
                    }
                }
            }

        } catch (Exception e) {

        }
    }

    ;

    /**
     * 开始录音的振动一小下
     */
    private void startShake() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {25, 50};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1);
    }

    /**
     * 更新mic状态
     */
    private void updateMicStatus(int voiceValue) {
        if (voiceValue < 200.0) {
            voiceLevel.setImageResource(R.drawable.tt_sound_volume_01);
        } else if (voiceValue > 200.0 && voiceValue < 600) {
            voiceLevel.setImageResource(R.drawable.tt_sound_volume_02);
        } else if (voiceValue > 600.0 && voiceValue < 1200) {
            voiceLevel.setImageResource(R.drawable.tt_sound_volume_03);
        } else if (voiceValue > 1200.0 && voiceValue < 2400) {
            voiceLevel.setImageResource(R.drawable.tt_sound_volume_04);
        } else if (voiceValue > 2400.0 && voiceValue < 10000) {
            voiceLevel.setImageResource(R.drawable.tt_sound_volume_05);
        } else if (voiceValue > 10000.0 && voiceValue < 28000.0) {
            voiceLevel.setImageResource(R.drawable.tt_sound_volume_06);
        } else if (voiceValue > 28000.0) {
            voiceLevel.setImageResource(R.drawable.tt_sound_volume_07);
        }
    }

    /**
     * 开始录音
     */
    private void VoiceRecordStart() {
        recordTime = 0;
        recordShow.setVisibility(View.VISIBLE);
        relay_voice.setEnabled(false);
        relay_media.setEnabled(false);
        try {
            recordFilePath = this.getExternalCacheDir() + "/temp_voice_record.mp3";
            File file = new File(recordFilePath);
            if (file.exists()) {
                file.delete();
            }
            if (recorder == null) {
                recorder = new MP3Recorder(recordFilePath, 8000);
                recorder.setHandle(voiceRecordHander);
            }
            startShake();
            recorder.start();
            MLog.e("开始录音");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束录音
     */
    private void VoiceRecordStop() {
        if (recorder != null) {
            recorder.stop();
            MLog.e("结束录音");
        }
        voiceRecordHander.removeMessages(VoiceRecordTime);
        recordShow.setVisibility(View.GONE);

        relay_voice.setEnabled(true);
        relay_media.setEnabled(true);

        if (recordTime < 2) {
            MyToast("录制的时间很短，请重新录制！");
            return;
        }

        btnVoice.setVoiceTime(recordTime);
        voiceWarning.setVisibility(View.VISIBLE);
        RecordDoneShow.setVisibility(View.VISIBLE);
        RecordStartShow.setVisibility(View.GONE);
        btnSendOption(true);
    }

    /**
     * 开始放音
     */
    private void StartPlayOrStop() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        } else {
            try {
                btnVoice.StartPlayIng();
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(recordFilePath);
                mPlayer.prepare();
                mPlayer.start();
                mPlayer.setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        btnVoice.StopPlayIng();
                        mPlayer.release();
                        mPlayer = null;
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    /**
     * 发送功能按钮是否可用
     *
     * @param enable
     */
    private void btnSendOption(boolean enable) {
        btnRelay.setEnabled(enable);
        if (enable) {
            btnRelay.setBackgroundResource(R.drawable.btn_send_enable_selector);
            btnRelay.setTextColor(getResources().getColor(R.color.color_white));
        } else {
            btnRelay.setBackgroundResource(R.drawable.btn_send_disable);
            btnRelay.setTextColor(getResources().getColor(R.color.color_black));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

        File file = new File(recordFilePath);
        if (file.exists()) {
            file.delete();
        }
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
     * @author acer
     */
    private class relayAdapter extends BaseListAdapter<RelayBean> {

        private boolean isStart = true;

        public relayAdapter(Context context) {
            super(context);
            isStart = true;
        }

        /**
         *
         */
        public void setIsStart(boolean flag) {
            isStart = flag;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (isStart) {
                convertView = mInflater.inflate(R.layout.norelay_list_hear, null);
            } else {
                ViewHolder holder = null;
                if (convertView == null || !(convertView instanceof LinearLayout)) {
                    convertView = mInflater.inflate(R.layout.topic_relay_title, null);
                    holder = new ViewHolder(convertView);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                RelayBean temp = getList().get(position);

                holder.userName.setText(temp.getUsername());
                holder.time.setText(MyTools.getStandardDate(temp.getDateline()));
                holder.upstaris.setText(position + 1 + "楼");
                if (!TextUtils.isEmpty(temp.getMessage())) {
                    holder.Message.setVisibility(View.VISIBLE);
                    holder.Message.setText(temp.getMessage());
                } else {
                    holder.Message.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(temp.getAudio())) {
                    holder.voiceContainer.setVisibility(View.VISIBLE);
                    DisplayUtil.AutoSetVoiceViewLayout(holder.voiceContainer, holder.voicePlay, Integer.parseInt(temp.getUsedtime()));
                    holder.voicePlay.setVoiceTime(Integer.parseInt(temp.getUsedtime()));
                    holder.voicePlay.setContentDescription(Constants.ServerTopicAdd + temp.getAudio());
                    holder.voicePlay.setTag(holder.voicePlay);
                    holder.voicePlay.setOnVoiceStartListener(new onVoiceStart() {

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
                                topicListRelay.postDelayed(new Runnable() {

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
                    holder.pic.setVisibility(View.VISIBLE);
                    imageLoader.displayImage(Constants.ServerTopicAdd + temp.getPic(), holder.pic, options);
                } else {
                    holder.pic.setVisibility(View.GONE);
                }

                if (temp.isParise()) {
                    holder.pareBack.setBackgroundResource(R.drawable.btn_like_hit);
                    holder.btnParise.setTextColor(getResources().getColor(R.color.color_white));
                    holder.pareBack.setEnabled(false);
                } else {
                    holder.pareBack.setBackgroundResource(R.drawable.btn_like);
                    holder.btnParise.setTextColor(getResources().getColor(R.color.color_light_black));
                    holder.pareBack.setEnabled(true);
                }

                if (Integer.valueOf(temp.getPraisenum()) == 0) {
                    holder.btnParise.setText("赞");
                } else {
                    holder.btnParise.setText(temp.getPraisenum());
                }

                holder.pareBack.setContentDescription(position + "");
                holder.pareBack.setTag(holder);
                holder.pareBack.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ViewHolder mHolder = (ViewHolder) v.getTag();
                        RelayBean temp = getList().get(Integer.valueOf(v.getContentDescription().toString()));
                        pariseNum("cid", temp.getCid(), mHolder.pareBack, mHolder.btnParise, temp);
                    }
                });

                holder.btnRelay.setTag(position);
                holder.btnRelay.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        btnRelay.setTag("topicid");
                        btnRelay.setContentDescription(topic.getTopicid());
                        content.setContentDescription("@" + getList().get((Integer) (v.getTag())).getUsername() + ":");
                        content.setHint("@" + getList().get((Integer) (v.getTag())).getUsername());
                        ShowKeyboard(content);
                    }
                });

                if (!TextUtils.isEmpty(temp.getAvatar())) {
                    imageLoader.displayImage(Constants.ServerTopicAdd + temp.getAvatar(), holder.image, optionsHead);
                }

                holder.image.setTag(position);
                holder.image.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        RelayBean temp = getList().get((Integer) v.getTag());
                        Intent intent = new Intent(ctx, PersionPosition.class);
                        intent.putExtra("uid", temp.getUid());
                        intent.putExtra("UserName", temp.getUsername());
                        startActivity(intent);
                    }
                });
            }
            return convertView;
        }

        private class ViewHolder {
            public CircularImageView image;
            public TextView userName;
            public TextView time;
            public TextView upstaris;
            public VoiceView voicePlay;
            public TextView Message;
            public RoundImageView pic;

            public Button btnRelay;

            public RelativeLayout pareBack;
            public TextView btnParise;

            public LinearLayout voiceContainer;

            public ViewHolder(View view) {

                image = (CircularImageView) view.findViewById(R.id.image);

                userName = (TextView) view.findViewById(R.id.userName);
                time = (TextView) view.findViewById(R.id.time);
                upstaris = (TextView) view.findViewById(R.id.upstaris);
                Message = (TextView) view.findViewById(R.id.Message);
                btnParise = (TextView) view.findViewById(R.id.btnParise);

                voicePlay = (VoiceView) view.findViewById(R.id.voicePlay);

                pic = (RoundImageView) view.findViewById(R.id.pic);
                pic.setRectAdius(4);

                voiceContainer = (LinearLayout) view.findViewById(R.id.voiceContainer);

                btnRelay = (Button) view.findViewById(R.id.btnRelay);
                pareBack = (RelativeLayout) view.findViewById(R.id.pareBack);
                view.setTag(this);
            }
        }
    }
}
