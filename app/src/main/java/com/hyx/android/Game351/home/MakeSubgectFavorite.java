package com.hyx.android.Game351.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyx.android.Game351.DBDevicesManger;
import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.base.Constants;
import com.hyx.android.Game351.data.HttpUtil;
import com.hyx.android.Game351.modle.HistoryBean;
import com.hyx.android.Game351.util.ApkType;
import com.hyx.android.Game351.util.MLog;
import com.hyx.android.Game351.util.MyTools;
import com.hyx.android.Game351.util.SP;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.HeadView.OnActionBtnListener;
import com.hyx.android.Game351.view.HeadView.OnBackBtnListener;
import com.hyx.android.Game351.view.MarqueeView;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MakeSubgectFavorite extends BaseActivity {

    /**
     * 后台下载文件
     */
    private static final int PictureDownload = 1;
    private static final int MP3Download = 2;
    private static final int NextOnew = 3;

    private HeadView headView;
    private List<HistoryBean> dataBeans;
    private ImageView headImag;
    private RelativeLayout haveMp3, noMp3;
    private ImageButton startPlay;
    private TextView useTime, noMp3chinesase;
    private TextView textEnDis, textChinestDis;
    private MarqueeView enContainer, chContainer;
    //copy read
    private TextView copyAnswerCh, copyAnswerEn;
    private LinearLayout playIng;
    private View voiceRecod;
    private TextView recordTime;
    private ImageView voiceLevel;
    private Button btnLeft, btnRight, answerDisplay, addFavorite, chinesDis;
    private boolean isDisplayEn, isDisplayCH = true;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private int subjectNum = 0;
    private String[] answer;
    private List<OneDisplay> answerDis = new ArrayList<MakeSubgectFavorite.OneDisplay>();
    private int answerIndex = 0;
    // 记录时间
    private int seconds = 0;
    private int downloadIndex = 0;
    private int currentDownload = 0;
    private MediaPlayer mediaPlayer = null;
    private boolean isNextButtonClick = false;
    private int currentIndex = 0;
    private boolean isRunning = true;
    private TextView OnimageTextDislay;
    private boolean isFromButton = false;


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
                                temp = dataBeans.get(downloadIndex).getFavorite().getPic_addr();
                                if (TextUtils.isEmpty(temp)) {
                                    downLoadHandler.sendEmptyMessage(MP3Download);
                                } else {
                                    temp = Constants.ResourceAddress + "res" + dataBeans.get(subjectNum).getFistId() + "/res" + dataBeans.get(subjectNum).getSecodeId() + "/pic/" + dataBeans.get(downloadIndex).getFavorite().getPic_addr();
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
                            temp = dataBeans.get(downloadIndex).getFavorite().getMp3_addr();
                            if (TextUtils.isEmpty(temp) || dataBeans.get(subjectNum).getFavorite().getIs_select() == 2) {
                                downLoadHandler.sendEmptyMessage(NextOnew);
                            } else {
                                temp = Constants.ResourceAddress + "res" + dataBeans.get(subjectNum).getFistId() + "/res" + dataBeans.get(subjectNum).getSecodeId() + "/audio/" + dataBeans.get(downloadIndex).getFavorite().getMp3_addr();
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
                            if (downloadIndex == currentIndex) {
                                dismissProgress();
                                subjectNum = currentIndex;
                                showImageAndMp3();
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


        haveMp3 = (RelativeLayout) findViewById(R.id.haveMp3);
        haveMp3.setVisibility(View.GONE);

        noMp3 = (RelativeLayout) findViewById(R.id.haveNoMp3);
        noMp3.setVisibility(View.GONE);

        startPlay = (ImageButton) findViewById(R.id.startPlay);
        startPlay.setOnClickListener(this);
        headImag = (ImageView) findViewById(R.id.showimage);

        useTime = (TextView) findViewById(R.id.useTime);
        useTime.setText("00:00");
        noMp3chinesase = (TextView) findViewById(R.id.chinesase);

        playIng = (LinearLayout) findViewById(R.id.chose);
        voiceRecod = findViewById(R.id.voiceRecodAtte);
        recordTime = (TextView) findViewById(R.id.recordTime);
        voiceLevel = (ImageView) findViewById(R.id.voiceLevel);

        textEnDis = (TextView) findViewById(R.id.textEnDis);
        textChinestDis = (TextView) findViewById(R.id.textChinestDis);

        enContainer = (MarqueeView) findViewById(R.id.enContainer);
        chContainer = (MarqueeView) findViewById(R.id.chContainer);
        enContainer.setVisibility(View.INVISIBLE);
        chContainer.setVisibility(View.INVISIBLE);

        btnLeft = (Button) findViewById(R.id.btn1);
        btnLeft.setOnClickListener(this);
        btnRight = (Button) findViewById(R.id.btn2);
        btnRight.setOnClickListener(this);
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
        if (subjectNum >= 0) {
            showImageAndMp3();
        } else {
            onBackPressed();
        }
    }


    @Override
    protected void initListener() {

        if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_MEIJU) {
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

    }

    @Override
    protected void initData() {

        dataBeans = app.getDBManger(Constants.FavoriteRecord).GetAllRecords();
        app.CloseDBManger();
        if (dataBeans == null) {
            finish();
            return;
        }

        currentIndex = getIntent().getIntExtra("currentPosition", 0);

        if (MyTools.getCurrentApkType(ctx) != ApkType.TYPE_FastRecord)
            headView.getBtnAction().setText("下一题");

        subjectNum = 0;
        downloadIndex = 0;
        downLoadHandler.sendEmptyMessage(PictureDownload);
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
                    enContainer.setVisibility(View.VISIBLE);
                    textEnDis.setText(getString());
                    enContainer.startMarquee();
                }
                break;
            case R.id.favorite:
            case R.id.addFavorite:
                if (dataBeans.get(subjectNum).getFavorite().getIs_select() == 1) {
                    FavoriteRecord(subjectNum, addFavorite);
                } else {
                    MyToast("亲，翻译题不能收藏！！！");
                }

                break;
            case R.id.chinesDis:
                if (subjectNum < dataBeans.size()) {
                    if (dataBeans.get(subjectNum).getFavorite().getIs_select() == 1) {
                        if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead ||
                                MyTools.getCurrentApkType(this) == ApkType.TYPE_MEIJU) {
                            copyAnswerCh.setText(dataBeans.get(subjectNum).getFavorite().getQuestion());
                        } else {
                            chContainer.setVisibility(View.VISIBLE);
                            textChinestDis.setText(dataBeans.get(subjectNum).getFavorite().getQuestion());
                            chContainer.startMarquee();
                        }
                    }
                }
                break;
            case R.id.btnRepet: {
                isNextButtonClick = true;
                subjectNum--;
                getIntoNext();
            }
            break;
            case R.id.btn1:
                if (answerIndex < answerDis.size()) {
                    if (answerDis.get(answerIndex).getLeftDisplay().rightOrError) {// 选择正确
                        String exist = OnimageTextDislay.getText().toString();
                        if (TextUtils.isEmpty(exist)) {
                            exist = answerDis.get(answerIndex).getLeftDisplay().answer
                                    + " ";
                        } else {
                            exist += answerDis.get(answerIndex).getLeftDisplay().answer
                                    + " ";
                        }
                        OnimageTextDislay.setText(exist);
                        OnimageTextDislay.setVisibility(View.VISIBLE);
                        answerIndex++;
                        if (answerIndex == answerDis.size()) {
                            OnimageTextDislay.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    getIntoNext();
                                }
                            }, 300);

                        } else {
                            ChangeAnswerDisplay();
                        }

                    } else {
                        isNextButtonClick = true;
                        // // 选择错误，从新来
                        // answerIndex = 0;
                        // ChangeAnswerDisplay();
                        // 给出提示音
                        playErrorSound();
                    }
                }
                break;
            case R.id.btnNext: {
                isNextButtonClick = true;
                getIntoNext();
            }
            break;
            case R.id.btn2:
                if (answerIndex < answerDis.size()) {
                    if (answerDis.get(answerIndex).getRightDisplay().rightOrError) {// 选择正确
                        String exist = OnimageTextDislay.getText().toString();
                        if (TextUtils.isEmpty(exist)) {
                            exist = answerDis.get(answerIndex).getRightDisplay().answer
                                    + " ";
                        } else {
                            exist += answerDis.get(answerIndex).getRightDisplay().answer
                                    + " ";
                        }
                        OnimageTextDislay.setText(exist);
                        OnimageTextDislay.setVisibility(View.VISIBLE);
                        answerIndex++;
                        if (answerIndex == answerDis.size()) {
                            OnimageTextDislay.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    getIntoNext();
                                }
                            }, 300);
                        } else {
                            ChangeAnswerDisplay();
                        }
                    } else {
                        isNextButtonClick = true;
                        // 选择错误，从新来
                        // answerIndex = 0;
                        // ChangeAnswerDisplay();
                        // 给出提示音
                        playErrorSound();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 进行下一题目的播放
     */
    public void getIntoNext() {

        isFromButton = false;
        subjectNum++;
        if (subjectNum < dataBeans.size()) {
            showImageAndMp3();
        } else {
            subjectNum = dataBeans.size() - 1;
            MyToast("全部播放完毕");
        }
    }

    /**
     * 保存历史记录
     */
    private void FavoriteRecord(int index, Button btn) {
        DBDevicesManger dataManager = app.getDBManger(Constants.FavoriteRecord);
//		String soritString = dataBeans.get(index).getId();
        if (dataManager.IsHaveTheRecord(dataBeans.get(index).getFavorite().getId())) {
            if (dataManager.deleteAHistoryRecord(dataBeans.get(index).getFavorite().getId())) {
                btn.setTextColor(getResources().getColor(R.color.color_black));
            }
        } else {
            HistoryBean save = new HistoryBean();
            save.setFistId(dataBeans.get(index).getFistId());
            save.setSecodeId(dataBeans.get(index).getSecodeId());
            save.setSort_id(dataBeans.get(index).getFavorite().getId());
            // 保存该商品的浏览历史记录
            // 打包数据，然后放到数据库中去
            save.setFavorite(dataBeans.get(index).getFavorite());
            app.getDBManger(Constants.FavoriteRecord).AddHistory(save);
            if (dataManager.IsHaveTheRecord(dataBeans.get(index).getFavorite().getId())) {
                btn.setTextColor(getResources().getColor(R.color.color_silde_menu_diviver_2));
            }
        }

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
        }, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

    /**
     * 打乱数据
     */
    private void putDataToDisplay() {
        answerDis.clear();
        if (answer.length == 1) {
            OneDisplay tepDisplay = new OneDisplay();
            tepDisplay.setRightDisplay(new anArray(answer[0], true));
            tepDisplay.setLeftDisplay(new anArray("", false));
            answerDis.add(tepDisplay);
        } else {
            for (int i = 0; i < answer.length; i += 2) {
                OneDisplay tepDisplay = new OneDisplay();
                int rd = Math.random() > 0.5 ? 1 : 0;
                if (rd == 1) {
                    tepDisplay.setRightDisplay(new anArray(answer[i], true));
                    tepDisplay.setLeftDisplay(new anArray(answer[i + 1], false));
                } else {
                    tepDisplay.setRightDisplay(new anArray(answer[i + 1], false));
                    tepDisplay.setLeftDisplay(new anArray(answer[i], true));
                }

                answerDis.add(tepDisplay);
            }
        }
    }

    /**
     * 显示
     */
    private void ChangeAnswerDisplay() {
        btnRight.setText(answerDis.get(answerIndex).getRightDisplay().answer);
        btnLeft.setText(answerDis.get(answerIndex).getLeftDisplay().answer);
    }

    /********************************** 进入一个题目吧当前所有的音频文件和图片都下载到缓存目录中 *************************************/

    /**
     * 根据是否有收藏显示收藏按钮的状态
     */
    private boolean favoriteState(int index) {
        boolean ishave = false;
        DBDevicesManger dataManager = app.getDBManger(Constants.FavoriteRecord);
        if (dataManager.IsHaveTheRecord(dataBeans.get(index).getFavorite().getId())) {
            ishave = true;
        } else {
            ishave = false;
        }

        app.CloseDBManger();

        return ishave;
    }

    /**
     * 播放音频
     */
    private void showImageAndMp3() {

        headView.setTitle(dataBeans.get(subjectNum).getTitle());

        textEnDis.setText("");
        textChinestDis.setText("");
        enContainer.setVisibility(View.INVISIBLE);
        chContainer.setVisibility(View.INVISIBLE);

        if (!isFromButton) {
            answerIndex = 0;
            OnimageTextDislay.setText("");
            OnimageTextDislay.setVisibility(View.GONE);
        }

        if (dataBeans.get(subjectNum).getFavorite().getIs_select() == 1 ||
                dataBeans.get(subjectNum).getFavorite().getIs_select() == 2) {
            if (!isFromButton) {
                answer = dataBeans.get(subjectNum).getFavorite().getAnswer().split("\\|");
                putDataToDisplay();
                ChangeAnswerDisplay();
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
                copyAnswerCh.setText(dataBeans.get(subjectNum).getFavorite().getQuestion());
            } else {
                copyAnswerCh.setText("");
            }
        }

        if (!TextUtils.isEmpty(dataBeans.get(subjectNum).getFavorite().getPic_addr())) {
            if ((MyTools.getCurrentApkType(this) == ApkType.TYPE_MEIJU || MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead) && !app.getSwitch(SP.CopySwitchpic)) {
                headImag.setImageResource(R.drawable.no_image);
            } else {
                String tempString = Constants.ResourceAddress + "res" + dataBeans.get(subjectNum).getFistId() + "/res" + dataBeans.get(subjectNum).getSecodeId() + "/pic/" + dataBeans.get(subjectNum).getFavorite().getPic_addr();
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

        if (!(TextUtils.isEmpty(dataBeans.get(subjectNum).getFavorite().getMp3_addr())) && (dataBeans.get(subjectNum).getFavorite().getIs_select() == 1 || dataBeans.get(subjectNum).getFavorite().getIs_select() == 3)) {
            haveMp3.setVisibility(View.VISIBLE);
            noMp3.setVisibility(View.GONE);
            chContainer.setVisibility(View.VISIBLE);
            String tempString = Constants.ResourceAddress + "res" + dataBeans.get(subjectNum).getFistId() + "/res" + dataBeans.get(subjectNum).getSecodeId() + "/audio/" + dataBeans.get(subjectNum).getFavorite().getMp3_addr();
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
                    if (dataBeans.get(subjectNum).getFavorite().getIs_select() == 3) {
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
                            if (dataBeans.get(subjectNum).getFavorite().getIs_select() == 3) {
                                playIng.setVisibility(View.GONE);
                                voiceRecod.setVisibility(View.VISIBLE);
                            } else {
                                startPlay.setEnabled(true);
                                playIng.setVisibility(View.VISIBLE);
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


                                autoDisplay();

                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (dataBeans.get(subjectNum).getFavorite().getIs_select() == 4) {
                playIng.setVisibility(View.GONE);
                voiceRecod.setVisibility(View.VISIBLE);
            } else {
                playIng.setVisibility(View.VISIBLE);
                voiceRecod.setVisibility(View.GONE);

                if (dataBeans.get(subjectNum).getFavorite().getIs_select() == 2) {
                    autoDisplay();
                }
            }
            haveMp3.setVisibility(View.GONE);
            noMp3.setVisibility(View.VISIBLE);
            chContainer.setVisibility(View.INVISIBLE);
            noMp3chinesase.setText(dataBeans.get(subjectNum).getFavorite().getQuestion());
        }
    }

    /**
     * @return
     */
    private String getString() {
        String temp = "";

        if (dataBeans.get(subjectNum).getFavorite().getIs_select() == 3 ||
                dataBeans.get(subjectNum).getFavorite().getIs_select() == 4) {
            temp = dataBeans.get(subjectNum).getFavorite().getAnswer();
        } else {
            for (int i = 0; i < answer.length; i++) {

                if (i % 2 == 0) {
                    temp += answer[i] + " ";
                }
            }
        }

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
                    int percent = (int) (Math.round((bytesWritten * 1.0 / totalSize * 1.0) * 100));
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
            copyAnswerCh.setTextSize(app.getFontSize());
            copyAnswerEn.setTextSize(app.getFontSize());
        } else {
            btnLeft.setTextSize(app.getFontSize());
            btnRight.setTextSize(app.getFontSize());
        }
        OnimageTextDislay.setTextSize(app.getFontSize());
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


    private void autoDisplay() {
        if (app.isAutoDisplay() && app.getShowtime() > 0) {
            if (answerIndex < answerDis.size()) {

                answerIndex++;
                if (answerIndex == answerDis.size()) {
                    OnimageTextDislay.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            getIntoNext();
                        }
                    }, app.getShowtime());

                } else {
                    OnimageTextDislay.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            ChangeAnswerDisplay();
                            autoDisplay();
                        }
                    }, app.getShowtime());

                }
            }
        }
    }

    /**
     * @author acer
     */
    private class OneDisplay {
        public anArray leftDisplay;
        public anArray rightDisplay;

        public anArray getLeftDisplay() {
            return leftDisplay;
        }

        public void setLeftDisplay(anArray leftDisplay) {
            this.leftDisplay = leftDisplay;
        }

        public anArray getRightDisplay() {
            return rightDisplay;
        }

        public void setRightDisplay(anArray rightDisplay) {
            this.rightDisplay = rightDisplay;
        }

    }

    /**
     * 选择答案的组合
     *
     * @author acer
     */
    private class anArray {
        public String answer;
        public boolean rightOrError;

        public anArray(String answer, boolean isRight) {
            this.answer = answer;
            rightOrError = isRight;
        }
    }
}
