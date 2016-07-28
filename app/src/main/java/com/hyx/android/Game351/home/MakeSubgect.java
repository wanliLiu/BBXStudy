package com.hyx.android.Game351.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chivox.AIEngine;
import com.chivox.AIEngineHelper;
import com.hyx.android.Game351.DBDevicesManger;
import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.base.BaseListAdapter;
import com.hyx.android.Game351.base.Constants;
import com.hyx.android.Game351.data.ApiCallBack;
import com.hyx.android.Game351.data.ApiHelper;
import com.hyx.android.Game351.data.ApiParams;
import com.hyx.android.Game351.data.HttpUtil;
import com.hyx.android.Game351.data.Result;
import com.hyx.android.Game351.modle.HistoryBean;
import com.hyx.android.Game351.modle.MenuDataBean;
import com.hyx.android.Game351.modle.PositionBean;
import com.hyx.android.Game351.modle.SubjectBean;
import com.hyx.android.Game351.util.ApkType;
import com.hyx.android.Game351.util.MLog;
import com.hyx.android.Game351.util.MyTools;
import com.hyx.android.Game351.util.SP;
import com.hyx.android.Game351.view.AutoWrapListView;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.HeadView.OnActionBtnListener;
import com.hyx.android.Game351.view.HeadView.OnBackBtnListener;
import com.hyx.android.Game351.view.MarqueeView;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MakeSubgect extends BaseActivity {

    /**
     * 后台下载文件
     */
    private static final int PictureDownload = 1;
    private static final int MP3Download = 2;
    private static final int NextOnew = 3;
    private String TAG = "MakeSubgect";
    private HeadView headView;
    private List<SubjectBean> dataBeans;
    private ImageView headImag;
    private RelativeLayout haveMp3, noMp3;
    private ImageButton startPlay;
    private TextView useTime, noMp3chinesase;
    private TextView textEnDis, textChinestDis;
    private MarqueeView enContainer, chContainer;
    //copy read
    private TextView copyAnswerCh, copyAnswerEn;
    private View voiceRecod, playIng;
    private TextView recordTime;
    private ImageView voiceLevel;
    private Button answerDisplay, addFavorite, chinesDis;
    private menuAdapter adapter;
    private GridView faslist;
    private boolean isDisplayEn, isDisplayCH = true;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private String FistId, secodeId;

    private int subjectNum = 0;
    private int answerIndex = 0;

    private PosAdapter posAdapter;
    private AutoWrapListView answerList;


    // 记录时间
    private int seconds = 0;
    private int downloadIndex = 0;
    private int currentDownload = 0;
    private MediaPlayer mediaPlayer = null;
    private boolean isNextButtonClick = false;
    private int currentIndex = 0;
    private MenuDataBean menu;
    private String title;
    private boolean isRunning = true;
    private TextView OnimageTextDislay;
    /**
     * 第三方测试
     */
    private AIRecorder recorder = null;
    private long engine = 0;
    private String appKey = "141639166500000d";
    private String secretKey = "1283dc28d41c7db4519902b647120112";
    private String userId = "bbx_user_001";
    //需要测评的文字
    private String text = "";
    private int errorTime = 0;
    private boolean isFromButton = false;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            String tempMunites = "", tempSecond = "";

            seconds++;

            int mseconds = seconds % 60;
            int munites = seconds / 60;

            if (mseconds < 10) {
                tempSecond = "0" + mseconds;
            } else {
                tempSecond = "" + mseconds;
            }

            if (munites < 10) {
                tempMunites = "0" + munites;
            } else {
                tempMunites = "" + munites;
            }

            useTime.setText(tempMunites + ":" + tempSecond);

            handler.sendEmptyMessageDelayed(0, 1000);
        }

        ;
    };

    private Handler downLoadHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (!isRunning) {
                return;
            }
            if (dataBeans != null && dataBeans.size() > 0) {

                if (downloadIndex < dataBeans.size()) {
                    String temp = "";
                    if (downloadIndex == 0) {
                        showProgress();
                    }
                    switch (msg.what) {
                        case PictureDownload:
                            if ((MyTools.getCurrentApkType(ctx) == ApkType.TYPE_MEIJU || MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead) && !app.getSwitch(SP.CopySwitchpic)) {
                                downLoadHandler.sendEmptyMessage(MP3Download);
                            } else {
                                temp = dataBeans.get(downloadIndex).getPic_addr();
                                if (TextUtils.isEmpty(temp)) {
                                    downLoadHandler.sendEmptyMessage(MP3Download);
                                } else {
                                    temp = Constants.ResourceAddress + "res" + FistId + "/res" + secodeId + "/pic/" + dataBeans.get(downloadIndex).getPic_addr();
                                    File file = createFilePath(temp);
                                    if (!file.exists()) {// 没有，那么就开始下载
                                        currentDownload = PictureDownload;
                                        downloadFile(file, temp);
                                    } else {
                                        // 存在就不下载了
                                        downLoadHandler.sendEmptyMessage(MP3Download);
                                    }
                                }
                            }
                            break;
                        case MP3Download:
                            temp = dataBeans.get(downloadIndex).getMp3_addr();
                            if (TextUtils.isEmpty(temp)) {
                                downLoadHandler.sendEmptyMessage(NextOnew);
                            } else {
                                temp = Constants.ResourceAddress + "res" + FistId + "/res" + secodeId + "/audio/" + dataBeans.get(downloadIndex).getMp3_addr();
                                File file = createFilePath(temp);
                                if (!file.exists()) {// 没有，那么就开始下载
                                    currentDownload = MP3Download;
                                    downloadFile(file, temp);
                                } else {
                                    // 存在就不下载了
                                    downLoadHandler.sendEmptyMessage(NextOnew);
                                }
                            }
                            break;
                        case NextOnew:
                            if (downloadIndex == 0) {
                                dismissProgress();
                                if (MyTools.getCurrentApkType(ctx) != ApkType.TYPE_FastRecord) {
                                    MLog.e("开始播放第一个");
                                    showImageAndMp3();
                                    if (!app.isPlayContinue()) {
                                        handler.sendEmptyMessageDelayed(0, 1000);
                                    }
                                } else {
                                    adapter.setList(dataBeans);
                                }
                            }
                            downloadIndex++;
                            if (downloadIndex < dataBeans.size()) {
                                downLoadHandler.sendEmptyMessage(PictureDownload);
                            } else {
                                // MyToast("下载完了");
                                MLog.e("所有的都下载完了");
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        ;
    };
    private AIEngine.aiengine_callback callback = new AIEngine.aiengine_callback() {

        @Override
        public int run(byte[] id, int type, byte[] data, int size) {

            if (type == AIEngine.AIENGINE_MESSAGE_TYPE_JSON) {

                final String result = new String(data, 0, size).trim(); /* must trim the end '\0' */
                try {
                    JSONObject json = JSON.parseObject(result);
                    if (json.containsKey("vad_status") || json.containsKey("volume")) {
                        int status = json.getIntValue("vad_status");
                        final int volume = json.getIntValue("volume");
                        if (status == 2) {
                            //表示录音结束
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    recorder.stop();
                                }
                            }).start();
                        } else if (status == 1) {
                            //录音正在进行
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    updateMicStatus(volume / 10);
//									recordTime.setText("" + volume);
                                }
                            });
                        }
                    } else {
                        if (recorder.isRunning()) {
                            recorder.stop();
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                dismissProgress();
                                MLog.e(TAG, result);
                                takeCareOfResult(result);
                            }
                        });
                    }

                } catch (Exception e) {
                }
            }
            return 0;
        }
    };
    /**
     * ***********************************************************************************************************
     */
    AIRecorder.Callback recorderCallback = new AIRecorder.Callback() {
        public void onStarted() {
            byte[] id = new byte[64];
            String param = "{\"vadEnable\": 1, \"volumeEnable\": 0, \"coreProvideType\": \"cloud\", \"app\": {\"userId\": \"" + userId + "\"}, \"audio\": {\"audioType\": \"wav\", \"channel\": 1, \"sampleBytes\": 2, \"sampleRate\": 16000}, \"request\": {\"coreType\": \"en.sent.score\", \"res\": \"eng.snt.g4\", \"refText\":\"" + text + "\", \"rank\": 100}}";
            int rv = AIEngine.aiengine_start(engine, param, id, callback);

            Log.d(TAG, "engine start: " + rv);
            Log.d(TAG, "engine param: " + param);

            runOnUiThread(new Runnable() {
                public void run() {
                    recordTime.setText("录音开始");
                }
            });
        }

        public void onStopped() {
            AIEngine.aiengine_stop(engine);
            Log.d(TAG, "engine stopped");
            runOnUiThread(new Runnable() {
                public void run() {
                    recordTime.setText("录音结束");
                    showProgress();
                }
            });
        }

        public void onData(byte[] data, int size) {
            AIEngine.aiengine_feed(engine, data, size);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (MyTools.getCurrentApkType(this) == ApkType.TYPE_MEIJU) {
            isScreenLandsape = true;
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.make_subject);


        headView = (HeadView) findViewById(R.id.headView);

        answerList = (AutoWrapListView) findViewById(R.id.answerList);
        posAdapter = new PosAdapter(ctx);
        answerList.setAdapter(posAdapter);
        answerList.setDividerHeight(0);
        answerList.setDividerWidth(0);

        haveMp3 = (RelativeLayout) findViewById(R.id.haveMp3);
        haveMp3.setVisibility(View.GONE);

        noMp3 = (RelativeLayout) findViewById(R.id.haveNoMp3);
        noMp3.setVisibility(View.GONE);

        startPlay = (ImageButton) findViewById(R.id.startPlay);
        startPlay.setOnClickListener(this);
        headImag = (ImageView) findViewById(R.id.showimage);

        useTime = (TextView) findViewById(R.id.useTime);
        if (MyTools.getCurrentApkType(ctx) != ApkType.TYPE_21)
            useTime.setVisibility(View.INVISIBLE);

        useTime.setText("00:00");
        noMp3chinesase = (TextView) findViewById(R.id.chinesase);

        playIng = findViewById(R.id.chose);
        voiceRecod = findViewById(R.id.voiceRecodAtte);
        recordTime = (TextView) findViewById(R.id.recordTime);
        voiceLevel = (ImageView) findViewById(R.id.voiceLevel);

        textEnDis = (TextView) findViewById(R.id.textEnDis);
        textChinestDis = (TextView) findViewById(R.id.textChinestDis);

        enContainer = (MarqueeView) findViewById(R.id.enContainer);
        chContainer = (MarqueeView) findViewById(R.id.chContainer);
        enContainer.setVisibility(View.INVISIBLE);
        chContainer.setVisibility(View.INVISIBLE);

        answerDisplay = (Button) findViewById(R.id.answerDisplay);
        answerDisplay.setOnClickListener(this);
        addFavorite = (Button) findViewById(R.id.addFavorite);
        addFavorite.setOnClickListener(this);
        chinesDis = (Button) findViewById(R.id.chinesDis);
        chinesDis.setOnClickListener(this);

        findViewById(R.id.favorite).setOnClickListener(this);

        OnimageTextDislay = (TextView) findViewById(R.id.OnimageTextDislay);
        OnimageTextDislay.setVisibility(View.GONE);
        imageInit();

        if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead ||
                MyTools.getCurrentApkType(this) == ApkType.TYPE_MEIJU) {
            findViewById(R.id.inCopy).setVisibility(View.GONE);
            findViewById(R.id.inCopy1).setVisibility(View.GONE);
            findViewById(R.id.inCopy2).setVisibility(View.GONE);
            findViewById(R.id.copyReadAnswerDis).setVisibility(View.VISIBLE);

            copyAnswerCh = (TextView) findViewById(R.id.copyCh);
            copyAnswerEn = (TextView) findViewById(R.id.copyEn);
            findViewById(R.id.btnRepet).setOnClickListener(this);
            findViewById(R.id.btnNext).setOnClickListener(this);

        }

        if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_FastRecord) {
            findViewById(R.id.fastrecord).setVisibility(View.GONE);
            findViewById(R.id.fastlist).setVisibility(View.VISIBLE);

            faslist = (GridView) findViewById(R.id.fastlist);
            adapter = new menuAdapter(ctx);
            faslist.setAdapter(adapter);
        }

    }

    private void imageInit() {
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(250, true, true, false))// 是否图片加载好后渐入的动画时间
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    /**
     *
     */
    private void onback() {
        if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_FastRecord) {
            onBackPressed();
            return;
        }

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        --subjectNum;
        isFromButton = false;
        if (subjectNum >= 0) {
            showImageAndMp3();
        } else {
            onBackPressed();
        }
    }


    @Override
    protected void initListener() {

        if (MyTools.getCurrentApkType(this) == ApkType.TYPE_MEIJU) {
            headView.setVisibility(View.GONE);
            findViewById(R.id.btnbackCopyRead).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onback();
                }
            });
        }

        headView.setOnBackBtnListener(new OnBackBtnListener() {

            @Override
            public void onClick() {
                onback();
            }
        });

        if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_FastRecord)
            return;

        headView.setOnActionBtnListener(new OnActionBtnListener() {

            @Override
            public void onClick() {
                isNextButtonClick = true;

                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                getIntoNext();
            }
        });

        answerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (dealAnswerDisplay(posAdapter.getItem(position))) {
                    posAdapter.getItem(position).setShow(false);
                    posAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * @param str
     * @return
     */
    private boolean isEqualTheSame(String str) {
        String[] temp = dataBeans.get(subjectNum).getAnswer().split("\\|");

        return str.equalsIgnoreCase(temp[answerIndex]);
    }

    /**
     *
     */
    private boolean dealAnswerDisplay(PositionBean bean) {
        boolean isOk = false;
        //能对上号，或是相等
        if (bean.getPosition() == answerIndex || isEqualTheSame(bean.getStr())) {
            String exist = OnimageTextDislay.getText().toString();
            if (TextUtils.isEmpty(exist)) {
                exist = bean.getStr() + " ";
            } else {
                exist += bean.getStr() + " ";
            }

            if (isWorld())
                exist = exist.trim();

            OnimageTextDislay.setText(exist);
            OnimageTextDislay.setVisibility(View.VISIBLE);
            answerIndex++;
            if (answerIndex == posAdapter.getCount()) {
                OnimageTextDislay.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        getIntoNext();
                    }
                }, 300);
            }

            isOk = true;
        } else {
//            isNextButtonClick = true;
            if (bean.isShow())
                playErrorSound();
        }

        return isOk;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.startPlay:
                if (subjectNum < dataBeans.size()) {
                    isFromButton = true;
                    showImageAndMp3();
                }
                break;
            case R.id.answerDisplay:
                if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead ||
                        MyTools.getCurrentApkType(this) == ApkType.TYPE_MEIJU) {
                    copyAnswerEn.setText(getString());
                } else {
                    isNextButtonClick = true;
                    enContainer.setVisibility(View.VISIBLE);
                    textEnDis.setText(getString());
                    enContainer.startMarquee();
                }
                break;
            case R.id.favorite:
            case R.id.addFavorite:
                if (dataBeans.get(subjectNum).getIs_select() == 1 || dataBeans.get(subjectNum).getIs_select() == 2) {
                    FavoriteRecord(subjectNum, addFavorite);
                } else {
                    MyToast("不能收藏！！！");
                }

                break;
            case R.id.chinesDis:
                if (subjectNum < dataBeans.size()) {
                    if (dataBeans.get(subjectNum).getIs_select() == 1 || dataBeans.get(subjectNum).getIs_select() == 2) {
                        if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead ||
                                MyTools.getCurrentApkType(this) == ApkType.TYPE_MEIJU) {
                            copyAnswerCh.setText(dataBeans.get(subjectNum).getQuestion());
                        } else {
                            chContainer.setVisibility(View.VISIBLE);
                            textChinestDis.setText(dataBeans.get(subjectNum).getQuestion());
                            chContainer.startMarquee();
                        }
                    }
                }
                break;
            case R.id.btnRepet: {
//                isNextButtonClick = true;
                subjectNum--;
                getIntoNext();
            }
            break;
            case R.id.btnNext: {
                isNextButtonClick = true;
                getIntoNext();
            }
            break;
            default:
                break;
        }
    }

    /**
     * 上传答题时间
     */
    private void upLoadScore() {
        showProgress();

        ApiParams params = new ApiParams();
        params.put("action", "uploadScore");
        params.put("username", app.getUserName());
        params.put("sid", dataBeans.get(0).getSubject_sort_id());
        params.put("score", seconds + "");
        params.put("checkcode", MyTools.getMD5_32(app.getUserName() + dataBeans.get(0).getSubject_sort_id() + seconds + "" + Constants.checkCode));

        ApiHelper.get(ctx, params, new ApiCallBack() {

            @Override
            public void receive(Result result) {
                dismissProgress();

                if (result.isSuccess()) {
                    JSONObject json = JSON.parseObject(result.getObj().toString());
                    if (json.getBooleanValue("success")) {
                        StartScoreActivity(true);
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
     * 进行下一题目的播放
     */
    public void getIntoNext() {

        errorTime = 0;

        isFromButton = false;

        subjectNum++;
        if (subjectNum < dataBeans.size()) {
            showImageAndMp3();
        } else {
            subjectNum = dataBeans.size() - 1;
            // 当前题目都昨晚了，进行下一个题目的播放
            if (app.isPlayContinue() ||
                    MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead ||
                    MyTools.getCurrentApkType(this) == ApkType.TYPE_MEIJU) {
                //||MyTools.getCurrentApkType(this) == ApkType.TYPE_21
                isPlayContinue();
            } else {
                handler.removeMessages(0);
                //!isNextButtonClick &&
                if (!isNextButtonClick && !app.isAutoDisplay()) {
                    upLoadScore();
                } else {
                    // 不能参加排名
                    StartScoreActivity(false);
                }
            }
        }
    }

    /**
     * 获取题目列表
     */
    private void GetdataSubject(String sid) {
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

                boolean isOkay = false;
                if (result.isSuccess()) {
                    JSONObject json = JSON.parseObject(result.getObj().toString());
                    if (json.getBooleanValue("success")) {
                        List<SubjectBean> temp = JSON.parseArray(json.getString("data"), SubjectBean.class);
                        if (temp != null && temp.size() > 0) {

                            headView.setTitle(title);

                            subjectNum = 0;
                            dataBeans = temp;

                            downloadIndex = 0;
                            downLoadHandler.sendEmptyMessage(PictureDownload);

                            RecordHappen();

                            isOkay = true;
                        }
                    } else {
                        MyToast(json.getString("info"));
                    }
                } else {
                    MyToast(result.getObj().toString());
                }

                if (!isOkay) {
                    MyToast("获取数据失败，请重试！！！");
                    finish();
                }
            }
        });
    }

    /**
     * 是否继续播放，下一个题目的播放
     */
    private void isPlayContinue() {
        if (menu != null && menu.getData() != null & menu.getData().size() > 0) {
            currentIndex++;
            if (currentIndex < menu.getData().size()) {
                title = menu.getData().get(currentIndex).getSort_name();
                GetdataSubject(menu.getData().get(currentIndex).getSort_id());
            } else {
                MyToast("当前分类题目已经全部做完了！");
                this.finish();
            }
        }
    }

    /**
     * @param from 为true，可以参加排名 为false 不可以参加排名
     */
    private void StartScoreActivity(boolean from) {
        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra("way", from);
        intent.putExtra("title", title);
        intent.putExtra("data", getIntent().getStringExtra("data"));
        intent.putExtra("sid", dataBeans.get(0).getSubject_sort_id());
        intent.putExtra("useTime", seconds + "");
        intent.putExtra("FistId", FistId);
        intent.putExtra("secodeId", secodeId);
        intent.putExtra("AllSubjects", JSON.toJSONString(menu));
        intent.putExtra("currentPosition", currentIndex);
        startActivity(intent);
        this.finish();
    }

    /**
     * 保存历史记录
     */
    private void FavoriteRecord(int index, Button btn) {
        DBDevicesManger dataManager = app.getDBManger(Constants.FavoriteRecord);
//		String soritString = dataBeans.get(index).getId();
        if (dataManager.IsHaveTheRecord(dataBeans.get(index).getId())) {
            if (dataManager.deleteAHistoryRecord(dataBeans.get(index).getId())) {
                if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead ||
                        MyTools.getCurrentApkType(this) == ApkType.TYPE_MEIJU) {
                    ((ImageView) findViewById(R.id.favorite)).setImageResource(R.drawable.icon_favorite);
                } else
                    btn.setTextColor(getResources().getColor(R.color.color_black));
            }
        } else {
            HistoryBean save = new HistoryBean();
            save.setTitle(title);
            save.setFistId(FistId);
            save.setSecodeId(secodeId);
            save.setSort_id(dataBeans.get(index).getId());
            // 保存该商品的浏览历史记录
            // 打包数据，然后放到数据库中去
            save.setFavorite(dataBeans.get(index));
            app.getDBManger(Constants.FavoriteRecord).AddHistory(save);
            if (dataManager.IsHaveTheRecord(dataBeans.get(index).getId())) {
                if (MyTools.getCurrentApkType(this) == ApkType.TYPE_MEIJU) {
                    ((ImageView) findViewById(R.id.favorite)).setImageResource(R.drawable.icon_favorite_have);
                } else
                    btn.setTextColor(getResources().getColor(R.color.color_silde_menu_diviver_2));
            }
        }

        app.CloseDBManger();
    }

    /**
     * 保存历史记录
     */
    private void RecordHappen() {
        HistoryBean save = new HistoryBean();
        save.setTitle(title);
        save.setAllSubjects(menu);
        save.setCurrentPosition(currentIndex);
        save.setFistId(FistId);
        save.setSecodeId(secodeId);
        save.setData(dataBeans);
        save.setSort_id(dataBeans.get(0).getSubject_sort_id());
        save.setRecordTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        // 保存该商品的浏览历史记录
        // 打包数据，然后放到数据库中去
        app.getDBManger(Constants.HistoryRecord).AddHistory(save);
        app.CloseDBManger();
    }

    /**
     * 选题选错，给出提示
     */
    private void playErrorSound() {
        final SoundPool sp = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        final int recouid = sp.load(ctx, R.raw.error, 1);
        headView.postDelayed(new Runnable() {

            @Override
            public void run() {

                sp.play(recouid, 1f, 1f, 0, 0, 1);
            }
        }, 350);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (engine != 0) {

            AIEngine.aiengine_delete(engine);
            engine = 0;
            Log.d(TAG, "engine deleted: " + engine);
        }

        if (recorder != null) {
            recorder.stop();
            recorder = null;
        }


    }

    @Override
    protected void initData() {

        dataBeans = JSON.parseArray(getIntent().getStringExtra("data"), SubjectBean.class);

        if (dataBeans == null) {
            finish();
            return;
        }

        title = getIntent().getStringExtra("title");
        if (MyTools.getCurrentApkType(ctx) != ApkType.TYPE_FastRecord)
            headView.setViewContent(title, "下一题");
        else {
            headView.setViewContent(title, "显示英文");
            findViewById(R.id.layouAction).setPadding(0, 0, 0, 0);
            findViewById(R.id.btn_action1).setVisibility(View.VISIBLE);
            findViewById(R.id.fram).setVisibility(View.VISIBLE);
            findViewById(R.id.layouAction).setClickable(false);
            int piex = MyTools.dip2px(ctx, 15f);
            int piexp = MyTools.dip2px(ctx, 5f);
            ((Button) findViewById(R.id.btn_action1)).setPadding(piexp, piex, 0, piex);
            ((Button) findViewById(R.id.btn_action)).setPadding(piexp, piex, piexp, piex);
            ((Button) findViewById(R.id.btn_action1)).setDuplicateParentStateEnabled(false);
            ((Button) findViewById(R.id.btn_action)).setDuplicateParentStateEnabled(false);
            ((Button) findViewById(R.id.btn_action1)).setText("显示中文");
            ((Button) findViewById(R.id.btn_action1)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isDisplayCH = !isDisplayCH;
                    adapter.notifyDataSetChanged();
                }
            });

            ((Button) findViewById(R.id.btn_action)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isDisplayEn = !isDisplayEn;
                    adapter.notifyDataSetChanged();
                }
            });

        }

        menu = JSON.parseObject(getIntent().getStringExtra("AllSubjects"), MenuDataBean.class);
        currentIndex = getIntent().getIntExtra("currentPosition", -1);

        FistId = getIntent().getStringExtra("FistId");
        secodeId = getIntent().getStringExtra("secodeId");

        downloadIndex = 0;
        downLoadHandler.sendEmptyMessage(PictureDownload);

        RecordHappen();

        if (dataBeans.get(subjectNum).getIs_select() == 3 ||
                dataBeans.get(subjectNum).getIs_select() == 4) {
            /* init aiengine and airecorder */
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        /* create aiengine instance */
                        if (engine == 0) {

                            String provisionPath = AIEngineHelper.extractResourceOnce(getApplicationContext(), "aiengine.provision", false);
                            String vadResPath = AIEngineHelper.extractResourceOnce(getApplicationContext(), "vad.0.10.bin", false);

                            Log.d(TAG, "provisionPath:" + provisionPath);

                            String cfg = String.format("{\"appKey\": \"%s\", \"secretKey\": \"%s\", \"provision\": \"%s\", \"vad\": {\"enable\": 1, \"res\": \"%s\", \"speechLowSeek\": 40, \"sampleRate\": 16000, \"strip\": 0}, \"cloud\": {\"server\": \"http://s-edu.api.chivox.com/api/v3.0/score\"}}",
                                    appKey, secretKey,
                                    provisionPath,
                                    vadResPath);
                            engine = AIEngine.aiengine_new(cfg, ctx);
                            Log.d(TAG, "aiengine: " + engine);
                        }

						/* create airecorder instance  */
                        if (recorder == null) {
                            recorder = new AIRecorder();
                            Log.d(TAG, "airecorder: " + recorder);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

    /********************************** 进入一个题目吧当前所有的音频文件和图片都下载到缓存目录中 *************************************/

    /**
     * 根据是否有收藏显示收藏按钮的状态
     */
    private boolean favoriteState(int index) {
        boolean ishave = false;
        DBDevicesManger dataManager = app.getDBManger(Constants.FavoriteRecord);
        if (dataManager.IsHaveTheRecord(dataBeans.get(index).getId())) {
            ishave = true;
        } else {
            ishave = false;
        }

        app.CloseDBManger();

        return ishave;
    }

    /**
     * 看是否是单词
     */
    private boolean isWorld() {

        boolean isworld = false;

        try {
            if (dataBeans.get(subjectNum).getIs_select() == 2) {
                isworld = true;
            }
        } catch (Exception e) {
        }

        if (!isworld) {
            String[] temp = dataBeans.get(subjectNum).getAnswer().split("\\|");
            isworld = true;
            for (int i = temp.length - 1; i >= 0; i--) {
                if (temp[i].length() > 1) {
                    isworld = false;
                    break;
                }
            }
        }


        return isworld;
    }

    /**
     * @param str
     */
    private void dealPosition(String str) {
        answerIndex = 0;

        String[] answer = str.split("\\|");

        List<PositionBean> WordsPos = new ArrayList<>();
        for (int i = 0; i < answer.length; i++) {
            WordsPos.add(new PositionBean(i, answer[i]));
        }

        //打乱排序
        Collections.shuffle(WordsPos);

        posAdapter.setIsWorld(isWorld());
        posAdapter.setList(WordsPos);

        OnimageTextDislay.setTextSize(TypedValue.COMPLEX_UNIT_SP, isWorld() ? app.getFontSize() + 20 : app.getFontSize());
    }

    /**
     * 播放音频
     */
    private void showImageAndMp3() {
        textEnDis.setText("");
        textChinestDis.setText("");
        enContainer.setVisibility(View.INVISIBLE);
        chContainer.setVisibility(View.INVISIBLE);

        if (!isFromButton) {
            answerIndex = 0;
            OnimageTextDislay.setText("");
            OnimageTextDislay.setVisibility(View.GONE);
        }

        if (dataBeans.get(subjectNum).getIs_select() == 1 ||
                dataBeans.get(subjectNum).getIs_select() == 2) {
            if (!isFromButton) {
                dealPosition(dataBeans.get(subjectNum).getAnswer());
            }
        } else {
            startPlay.setEnabled(false);
        }

        if (favoriteState(subjectNum)) {
            if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_MEIJU) {
                ((ImageView) findViewById(R.id.favorite)).setImageResource(R.drawable.icon_favorite_have);
            } else {
                addFavorite.setTextColor(getResources().getColor(R.color.color_silde_menu_diviver_2));
            }
        } else {
            if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_MEIJU) {
                ((ImageView) findViewById(R.id.favorite)).setImageResource(R.drawable.icon_favorite);
            } else {
                addFavorite.setTextColor(getResources().getColor(R.color.color_black));
            }
        }

        if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead ||
                MyTools.getCurrentApkType(this) == ApkType.TYPE_MEIJU) {
            if (app.getSwitch(SP.CopySwitchAnswer)) {
                copyAnswerEn.setText(getString());
            } else {
                copyAnswerEn.setText("");
            }

            if (app.getSwitch(SP.CopySwitchZh)) {
                copyAnswerCh.setText(dataBeans.get(subjectNum).getQuestion());
            } else {
                copyAnswerCh.setText("");
            }
        }

        if (!TextUtils.isEmpty(dataBeans.get(subjectNum).getPic_addr())) {
            if ((MyTools.getCurrentApkType(this) == ApkType.TYPE_MEIJU || MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead) && !app.getSwitch(SP.CopySwitchpic)) {
                headImag.setImageResource(R.drawable.no_image);
            } else {
                String tempString = Constants.ResourceAddress + "res" + FistId + "/res" + secodeId + "/pic/" + dataBeans.get(subjectNum).getPic_addr();
                MLog.e(tempString);
                File file = createFilePath(tempString);
                if (file.exists()) {
                    // imageLoader.displayImage(tempString, headImag, options);
                    BitmapFactory.Options options1 = new BitmapFactory.Options();
                    options1.inSampleSize = 2;
                    Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), options1);
                    if (bm != null) {
                        headImag.setImageBitmap(bm);
                    } else {
                        imageLoader.displayImage(tempString, headImag, options);
                    }
                }
            }
        } else {
            headImag.setImageResource(R.drawable.no_image);
        }

        if (!(TextUtils.isEmpty(dataBeans.get(subjectNum).getMp3_addr())) && (dataBeans.get(subjectNum).getIs_select() == 1 || dataBeans.get(subjectNum).getIs_select() == 2 || dataBeans.get(subjectNum).getIs_select() == 3)) {
            haveMp3.setVisibility(View.VISIBLE);
            noMp3.setVisibility(View.GONE);
            chContainer.setVisibility(View.VISIBLE);
            String tempString = Constants.ResourceAddress + "res" + FistId + "/res" + secodeId + "/audio/" + dataBeans.get(subjectNum).getMp3_addr();
            MLog.e(tempString);
            File file = createFilePath(tempString);
            if (file.exists()) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                try {
                    startPlay.setEnabled(false);
                    if (dataBeans.get(subjectNum).getIs_select() == 3) {
                        voiceRecod.setVisibility(View.INVISIBLE);
                        playIng.setVisibility(View.GONE);
                    } else {
                        voiceRecod.setVisibility(View.GONE);
                        playIng.setVisibility(View.INVISIBLE);
                    }
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(file.getAbsolutePath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                            if (dataBeans.get(subjectNum).getIs_select() == 3) {
                                playIng.setVisibility(View.GONE);
                                voiceRecod.setVisibility(View.VISIBLE);
                                text = getString();
                                StartRecord();
                            } else {
                                startPlay.setEnabled(true);
                                playIng.setVisibility(View.VISIBLE);
                                answerList.requestLayout();
                                voiceRecod.setVisibility(View.GONE);
                                // 连续播放
                                if (app.isPlayContinue() && isRunning) {
                                    playIng.postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            getIntoNext();
                                        }
                                    }, app.getPlayTime());
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (dataBeans.get(subjectNum).getIs_select() == 4) {
                playIng.setVisibility(View.GONE);
                voiceRecod.setVisibility(View.VISIBLE);
                //这里也显示录音界面
                text = getString();
                StartRecord();
            } else {
                playIng.setVisibility(View.VISIBLE);
                answerList.requestLayout();
                voiceRecod.setVisibility(View.GONE);
            }
            haveMp3.setVisibility(View.GONE);
            noMp3.setVisibility(View.VISIBLE);
            chContainer.setVisibility(View.INVISIBLE);
            noMp3chinesase.setText(dataBeans.get(subjectNum).getQuestion());
        }


    }

    /**
     * 开始录音
     */
    private void StartRecord() {
        String wavPath = AIEngineHelper.getFilesDir(getApplicationContext()).getPath() + "/record/" + "cache_record.wav";
        MLog.e(text);

        if (recorder.isRunning()) {
            recorder.stop();
        }
        recorder.start(wavPath, recorderCallback);
    }

    /**
     * @return
     */
    private String getString() {
        String temp = "";

        if (dataBeans.get(subjectNum).getIs_select() == 3 ||
                dataBeans.get(subjectNum).getIs_select() == 4) {
            temp = dataBeans.get(subjectNum).getAnswer();
        } else {
            String[] answer = dataBeans.get(subjectNum).getAnswer().split("\\|");
            for (int i = 0; i < answer.length; i++) {

//                if (i % 2 == 0) {
                temp += answer[i] + " ";
//                }
            }
        }

        if (isWorld())
            temp = temp.replaceAll(" ", "");

        return temp;
    }

    /**
     * Creates a constant cache file path given a target cache directory and an
     * image key.
     *
     * @param key
     * @return
     */
    public File createFilePath(String key) {
        // 扩展名位置
        int index = key.lastIndexOf('.');
        if (index == -1) {
            return null;
        }

        StringBuilder filePath = new StringBuilder();

        // 图片存取路径
        filePath.append(this.getExternalCacheDir().getAbsolutePath() + "/");
        filePath.append(MD5.Md5(key)).append(key.substring(index));

        return new File(filePath.toString());
    }

    /***
     * 使用通用库加载
     */
    private void downloadFile(final File file, String url) {

        HttpUtil.getClient().get(url, new FileAsyncHttpResponseHandler(file) {

            int lastPercent = 0;

            @Override
            public void onSuccess(int arg0, Header[] arg1, File arg2) {

                if (currentDownload == PictureDownload) {
                    // 图片下载完成，进行mp3的下载
                    downLoadHandler.sendEmptyMessage(MP3Download);
                } else if (currentDownload == MP3Download) {
                    downLoadHandler.sendEmptyMessage(NextOnew);
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                if (totalSize != 1) {
                    int percent = (int) (Math.round((bytesWritten * 1.0
                            / totalSize * 1.0) * 100));
                    if (percent != lastPercent) {
                        lastPercent = percent;
//						MLog.e("下载进度", String.valueOf(percent) + "%");
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                if (currentDownload == PictureDownload) {
                    // 图片下载完成，进行mp3的下载
                    downLoadHandler.sendEmptyMessage(MP3Download);
                } else if (currentDownload == MP3Download) {
                    downLoadHandler.sendEmptyMessage(NextOnew);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;

        if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead ||
                MyTools.getCurrentApkType(this) == ApkType.TYPE_MEIJU) {
            copyAnswerCh.setTextSize(TypedValue.COMPLEX_UNIT_SP, app.getFontSize());
            copyAnswerEn.setTextSize(TypedValue.COMPLEX_UNIT_SP, app.getFontSize());
        }

        OnimageTextDislay.setTextSize(TypedValue.COMPLEX_UNIT_SP, isWorld() ? app.getFontSize() + 20 : app.getFontSize());
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * 更新mic状态
     */
    private void updateMicStatus(int db) {
        if (db <= 1) {
            voiceLevel.setImageResource(R.drawable.tt_sound_volume_01);
        } else if (db == 2) {
            voiceLevel.setImageResource(R.drawable.tt_sound_volume_02);
        } else if (db == 3) {
            voiceLevel.setImageResource(R.drawable.tt_sound_volume_03);
        } else if (db == 4) {
            voiceLevel.setImageResource(R.drawable.tt_sound_volume_04);
        } else if (db == 5) {
            voiceLevel.setImageResource(R.drawable.tt_sound_volume_05);
        } else if (db == 6) {
            voiceLevel.setImageResource(R.drawable.tt_sound_volume_06);
        } else {
            voiceLevel.setImageResource(R.drawable.tt_sound_volume_07);
        }
    }

    /**
     * 获取发音得分
     *
     * @param score
     */
    private void CheckResult(String score) {
        JSONArray json = JSON.parseArray(score);
        if (json != null && json.size() > 0 && !TextUtils.isEmpty(text)) {
            SpannableString ss = new SpannableString(text);
            boolean isAllPass = true;
            for (int i = 0; i < json.size(); i++) {
                JSONObject temp = json.getJSONObject(i);
                String mchar = temp.getString("char");
                int index = text.indexOf(mchar);
                if (index != -1) {
                    if (temp.getIntValue("score") < 60) {
                        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_red)), index, index + mchar.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        isAllPass = false;
                    }
//		    		else {
//						ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorb)), index, index + mchar.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//					}
                }
            }

            OnimageTextDislay.setText(ss);
            OnimageTextDislay.setVisibility(View.VISIBLE);

            if (!isAllPass) {
                errorTime++;
                if (errorTime == 3) {//只有三次机会
                    MyToast("继续努力，下次再来！");
                    voiceDelayNext();
                } else {
                    OnimageTextDislay.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            StartRecord();
                        }
                    }, 1000);
                    MyToast("发音不标准，再试一次");
                }
            } else {
                //进入下一题
                voiceDelayNext();
            }
        }
    }

    /**
     * 延迟执行
     */
    private void voiceDelayNext() {
        OnimageTextDislay.postDelayed(new Runnable() {

            @Override
            public void run() {
                getIntoNext();
            }
        }, 1000);
    }

    /**
     * 分析处理结果
     *
     * @param result
     */
    private void takeCareOfResult(String result) {

        JSONObject json = JSON.parseObject(result);
        if (json.containsKey("result") && !TextUtils.isEmpty(json.getString("result"))) {
            JSONObject jsonResult = JSON.parseObject(json.getString("result"));
            if (jsonResult.containsKey("info") && !TextUtils.isEmpty(jsonResult.getString("info"))) {
                JSONObject jsoninfo = JSON.parseObject(jsonResult.getString("info"));
                if (jsoninfo.containsKey("tipId")) {
                    switch (jsoninfo.getIntValue("tipId")) {
                        //本次录音没有异常
                        case 0:
                            break;
                        //音频数据为0，可提示未录音
                        case 10000:
                            MyToast("没有任何录音");
                            break;
                        //用户发音不完整如"i want an apple"，可能只发了"i want an"，可提示发音不完整
                        case 10001:
                            MyToast("亲，发音不完整");
                            break;
                        //识别不完整，出现这种情况大部分是静音，及音频偏短，可提示发音不完整
                        case 10002:
                        case 10003:
                            MyToast("亲，发音不完整");
                            break;
                        //音量偏低，可能位置太远，可建议用户调整麦克风位置
                        case 10004:
                            MyToast("亲，离麦克风近一点");
                            break;
                        //音频截幅，可能位置太近，可建议用户调整麦克风位置
                        case 10005:
                            MyToast("亲，离麦克风远一点");
                            break;
                        //音频质量偏差（由录音环境嘈杂或语音不明显引起的信噪比低，或麦克风质量偏差), 可提示语音不明显。
                        case 10006:
                            break;
                        default:
                            break;
                    }
                    MLog.e("overall=" + jsonResult.getIntValue("overall"));
                    if (jsonResult.containsKey("details") && !TextUtils.isEmpty(jsonResult.getString("details"))) {
                        CheckResult(jsonResult.getString("details"));
                    }
                }
            }
        } else {
            OnimageTextDislay.postDelayed(new Runnable() {

                @Override
                public void run() {
                    StartRecord();
                }
            }, 500);
//			MyToast("网络连接出错！");
        }
    }

    /**
     * 速记
     */
    private class menuAdapter extends BaseListAdapter<SubjectBean> {

        public menuAdapter(Context context) {
            super(context);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.menu_item, null);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            SubjectBean temp = getList().get(position);

            holder.chineseDis.setVisibility(isDisplayCH ? View.VISIBLE : View.INVISIBLE);
            holder.englishDis.setVisibility(isDisplayEn ? View.VISIBLE : View.INVISIBLE);

            holder.chineseDis.setText(temp.getQuestion());
            holder.englishDis.setText(temp.getEnglishStr());

            if (favoriteState(position)) {
                holder.favorite.setTextColor(getResources().getColor(R.color.color_silde_menu_diviver_2));
            } else {
                holder.favorite.setTextColor(getResources().getColor(R.color.color_black));
            }

            holder.favorite.setTag(position);
            holder.favorite.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (getList().get((Integer) v.getTag()).getIs_select() == 1) {
                        FavoriteRecord((Integer) v.getTag(), (Button) v);
                    } else {
                        MyToast("亲，翻译题不能收藏！！！");
                    }
                }
            });

            holder.item_fast.setTag(position);
            holder.item_fast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subjectNum = (Integer) v.getTag() - 1;
                    getIntoNext();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private View menuTit, item_fast;
            private Button favorite;
            private TextView englishDis, chineseDis;

            public ViewHolder(View view) {
                menuTit = view.findViewById(R.id.menuTit);
                item_fast = view.findViewById(R.id.item_fast);

                favorite = (Button) view.findViewById(R.id.favorite);
                englishDis = (TextView) view.findViewById(R.id.englishDis);
                chineseDis = (TextView) view.findViewById(R.id.chineseDis);

                menuTit.setVisibility(View.GONE);
                item_fast.setVisibility(View.VISIBLE);


                view.setTag(this);
            }
        }
    }
}
