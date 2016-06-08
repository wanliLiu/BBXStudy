package com.hyx.android.Game351.Topic;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;

import com.hyx.android.Game351.data.HttpUtil;
import com.hyx.android.Game351.view.VoiceView;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;

/**
 * 帖子音频播放工具
 *
 * @author acer
 */
public class VoiceTool {

    private static final int StartPlay = 1;
    private static final int StopPlay = 2;
    private static final int PlayParpare = 3;

    private Handler handler;
    private MediaPlayer mPlayer = null;

    private String recordFilePath = "";
    private String radioPath = "";
    private String oldradioPath = "";

    private VoiceView tempVoice;
    private VoiceView lastVoice;

    private Context context;

    public VoiceTool(Handler mHandler, Context ctx) {
        handler = mHandler;
        context = ctx;
    }

    public VoiceView getLastVoice() {
        return lastVoice;
    }

    public void setLastVoice(VoiceView lastVoice) {
        this.lastVoice = lastVoice;
    }

    public VoiceView getTempVoice() {
        return tempVoice;
    }

    public void setTempVoice(VoiceView tempVoice) {
        this.tempVoice = tempVoice;
    }

    public String getRecordFilePath() {
        return recordFilePath;
    }

    public void setRecordFilePath(String recordFilePath) {
        this.recordFilePath = recordFilePath;
    }

    public String getRadioPath() {
        return radioPath;
    }

    public void setRadioPath(String radioPath) {
        this.radioPath = radioPath;
    }

    public String getOldradioPath() {
        return oldradioPath;
    }

    public void setOldradioPath(String oldradioPath) {
        this.oldradioPath = oldradioPath;
    }

    /***
     * 使用通用库加载
     */
    public void downloadFile() {
        if (!oldradioPath.equals(radioPath)) {
            oldradioPath = radioPath;
            recordFilePath = context.getExternalCacheDir() + "/temp_voice_record.mp3";
            File file = new File(recordFilePath);
            if (file.exists()) {
                file.delete();
            }
            tempVoice.PlayPrepare();
            HttpUtil.getClient().get(radioPath, new FileAsyncHttpResponseHandler(file) {

                int lastPercent = 0;

                @Override
                public void onSuccess(int arg0, Header[] arg1, File arg2) {
                    handler.sendEmptyMessage(StartPlay);
                }

                @Override
                public void onProgress(int bytesWritten, int totalSize) {
                    if (totalSize != 1) {
                        int percent = (int) (Math.round((bytesWritten * 1.0 / totalSize * 1.0) * 100));
                        if (percent != lastPercent) {
                            lastPercent = percent;
//	                        MLog.e("下载进度", String.valueOf(percent) + "%");
                        }
                    }
                }

                @Override
                public void onFailure(int arg0, Header[] arg1, Throwable arg2, File arg3) {
                    handler.sendEmptyMessage(StopPlay);
                }
            });
        } else {
            handler.sendEmptyMessage(StartPlay);
        }

    }

    public void stopPlay() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

        if (tempVoice != null) {
            tempVoice.StopPlayIng();
        }
    }

    /**
     * 开始放音
     */
    public void StartPlay() {
        try {
            tempVoice.StartPlayIng();
            lastVoice = tempVoice;
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(recordFilePath);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    tempVoice.StopPlayIng();
                    mPlayer.release();
                    mPlayer = null;
                    oldradioPath = "";
                    File file = new File(recordFilePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            });
        } catch (Exception e) {
        }
    }

}
