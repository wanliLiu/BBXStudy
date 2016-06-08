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
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.base.Constants;
import com.hyx.android.Game351.data.ApiCallBack;
import com.hyx.android.Game351.data.ApiHelper;
import com.hyx.android.Game351.data.Result;
import com.hyx.android.Game351.util.DisplayUtil;
import com.hyx.android.Game351.util.MLog;
import com.hyx.android.Game351.util.MyTools;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.HeadView.OnActionBtnListener;
import com.hyx.android.Game351.view.HeadView.OnBackBtnListener;
import com.hyx.android.Game351.view.VoiceView;
import com.hyx.android.Game351.view.VoiceView.onVoiceStart;
import com.kubility.demo.MP3Recorder;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

import br.com.dina.ui.model.RoundImageView;

public class PostTopicActivity extends BaseActivity {

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
    private ImageButton relay_voice, relay_media, btnPictureSelect, btnCameraSelect, imageDelete;
    private ImageView voiceWarning, mediaWarning;
    private Button btnRelay, btnRecord, btnReset;
    private EditText content;
    private FrameLayout ChoseLayout;
    private ImageView voiceLevel;
    private MediaPlayer mPlayer = null;
    private VoiceView btnVoice;
    private RelativeLayout recordShow, RecordStartShow, RecordDoneShow, MediaOpenShow, PicturedoneShow;
    private TextView recordTimeShow;
    private int recordTime = 0;
    private RoundImageView addImage;
    private int action = 0;
    private int[] who = new int[2];
    private VoiceTool tool;
    private HeadView headView;
    private EditText topicTitle;
    private EditText topicContent;
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
                    tool.downloadFile();
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
        setContentView(R.layout.topic_relay_content);

        headView = (HeadView) findViewById(R.id.headView);
        headView.getBtnBack().setImageResource(R.drawable.back);
        headView.setTitle("发帖");

        headView.getBtnAction().setText("发表");

        btnPictureSelect = (ImageButton) findViewById(R.id.btnPictureSelect);
        btnCameraSelect = (ImageButton) findViewById(R.id.btnCameraSelect);
        relay_voice = (ImageButton) findViewById(R.id.btnRelyVoice);
        relay_media = (ImageButton) findViewById(R.id.btnRelyMedia);
        imageDelete = (ImageButton) findViewById(R.id.imageDelete);

        addImage = (RoundImageView) findViewById(R.id.addImage);

        voiceWarning = (ImageView) findViewById(R.id.voiceWarning);
        mediaWarning = (ImageView) findViewById(R.id.mediaWaring);

        btnRelay = (Button) findViewById(R.id.btnSend);
        btnRelay.setVisibility(View.INVISIBLE);
        btnRecord = (Button) findViewById(R.id.btnRecord);
        btnReset = (Button) findViewById(R.id.btnReset);

        ChoseLayout = (FrameLayout) findViewById(R.id.addAnother);
        content = (EditText) findViewById(R.id.RelyContent);
        content.setVisibility(View.INVISIBLE);

        recordShow = (RelativeLayout) findViewById(R.id.voiceRecodAtte);
        recordTimeShow = (TextView) findViewById(R.id.recordTime);

        RecordStartShow = (RelativeLayout) findViewById(R.id.btnRecordStart);
        RecordDoneShow = (RelativeLayout) findViewById(R.id.btnRecordDone);
        MediaOpenShow = (RelativeLayout) findViewById(R.id.btnMediaOpen);
        PicturedoneShow = (RelativeLayout) findViewById(R.id.btnPicturedone);

        btnVoice = (VoiceView) findViewById(R.id.btnVoice);

        tool = new VoiceTool(voicHandler, ctx);

        topicTitle = (EditText) findViewById(R.id.topicTitle);
        topicContent = (EditText) findViewById(R.id.topicContent);

        voiceLevel = (ImageView) findViewById(R.id.voiceLevel);
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
                if (!TextUtils.isEmpty(topicTitle.getText().toString().trim())) {
                    postTopic();
                } else {
                    MyToast("标题为必填内容");
                }
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

        btnRelay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //提交评论
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
    }

    /**
     * 回帖
     *
     * @param idtype
     * @param id
     */
    private void postTopic() {
        try {

            who[0] = who[1] = 0;
            DisplayAction();
            HideKeyboard();

            showProgress();
            RequestParams params = new RequestParams();
            params.put("action", "post_topic");
            params.put("uid", app.getUserID());
            params.put("username", app.getUserName());
            params.put("catid", getIntent().getStringExtra("catid"));
            params.put("flag", "0");
            params.put("displayorder", "0");

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

            params.put("title", topicTitle.getText().toString());

            if (!TextUtils.isEmpty(topicContent.getText().toString())) {
                params.put("content", topicContent.getText().toString());
                params.put("topictype", "0");
            } else {
                params.put("topictype", "1");
            }
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
                                setResult(RESULT_OK);
                                finish();
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

    @Override
    protected void initData() {

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
}
