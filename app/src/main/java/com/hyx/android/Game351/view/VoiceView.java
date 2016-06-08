package com.hyx.android.Game351.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyx.android.Game351.R;

public class VoiceView extends RelativeLayout {

    private TextView voiceTime;
    private ImageView isPlaying;
    private ImageView isLoading;

    private AnimationDrawable anDrawable;
    private onVoiceStart onStart;

    private String voiceFilePath;

    private VoiceView subview;

    private boolean playing = false;

    public VoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = mInflater.inflate(R.layout.voice_button, this, true);
        voiceTime = (TextView) view.findViewById(R.id.voiceTime);
        isPlaying = (ImageView) view.findViewById(R.id.voicePlaying);
        isLoading = (ImageView) view.findViewById(R.id.isLoading);

        isPlaying.setBackgroundResource(R.drawable.icon_volume_3);
        setVoiceTime(10);

        RelativeLayout father = (RelativeLayout) view.findViewById(R.id.voiceItem);
        father.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View vi) {

                if (onStart != null) {
                    onStart.onVoiceStartPlay(subview);
                }
            }
        });
    }

    /**
     * @param view
     */
    public void setTag(VoiceView view) {
        this.subview = view;
    }

    /**
     * 设置时间
     *
     * @param time
     */
    public void setVoiceTime(int time) {
        voiceTime.setText("" + time + "\"");
    }

    /**
     * 显示准备播放进度
     */
    public void PlayPrepare() {
        isLoading.setVisibility(View.VISIBLE);
        isPlaying.setVisibility(View.INVISIBLE);
        AnimationDrawable animationDrawable = (AnimationDrawable) isLoading.getBackground();
        animationDrawable.start();
        playing = true;
    }

    /**
     * 开始播放
     */
    public void StartPlayIng() {
        isLoading.setVisibility(View.INVISIBLE);
        isPlaying.setVisibility(View.VISIBLE);
        isPlaying.setBackgroundResource(R.anim.playing_progress_round);
        anDrawable = (AnimationDrawable) isPlaying.getBackground();
        anDrawable.start();
        playing = true;
    }

    /**
     * 停止播放
     */
    public void StopPlayIng() {
        isLoading.setVisibility(View.INVISIBLE);
        isPlaying.setVisibility(View.VISIBLE);

        if (anDrawable != null && anDrawable.isRunning()) {
            anDrawable.stop();
            anDrawable = null;
        }
        playing = false;
        isPlaying.setBackgroundResource(R.drawable.icon_volume_3);
    }

    public boolean isPlaying() {
        return playing;
    }

    /**
     * 获取音频文件路径
     *
     * @return
     */
    public String getVoiceFilePath() {
        return voiceFilePath;
    }

    /**
     * 保存音频播放文件
     *
     * @param Path
     */
    public void setVoiceFilePath(String voiceFilePath) {
        this.voiceFilePath = voiceFilePath;
    }

    /**
     * 设置监听事件
     *
     * @param onVoiceStart
     */
    public void setOnVoiceStartListener(onVoiceStart onVoiceStart) {
        onStart = onVoiceStart;
    }

    /**
     * 开始播放音乐接口
     *
     * @author acer
     */
    public interface onVoiceStart {
        public void onVoiceStartPlay(View view);
    }
}
