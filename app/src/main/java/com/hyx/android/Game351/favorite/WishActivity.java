package com.hyx.android.Game351.favorite;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.base.BaseListAdapter;
import com.hyx.android.Game351.base.Constants;
import com.hyx.android.Game351.data.HttpUtil;
import com.hyx.android.Game351.home.MD5;
import com.hyx.android.Game351.home.MakeSubgectFavorite;
import com.hyx.android.Game351.modle.HistoryBean;
import com.hyx.android.Game351.modle.SubjectBean;
import com.hyx.android.Game351.util.ApkType;
import com.hyx.android.Game351.util.MyTools;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.HeadView.OnBackBtnListener;
import com.hyx.android.Game351.view.dialog.DefaultDialog;
import com.hyx.android.Game351.view.dialog.DialogSelectListener;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.File;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class WishActivity extends BaseActivity {

    private HeadView headView;
    private ListView favoriteList;
    private favoriteAdapter adapter;

    private List<HistoryBean> favorite;

    private MediaPlayer mediaPlayer = null;

    private boolean isDisplayEn, isDisplayCH = true;

    @Override
    protected void initView() {
        setContentView(R.layout.wish_acitivity);

        headView = (HeadView) findViewById(R.id.headView);
        headView.setTitle(getResources().getString(R.string.home_favorite));

        favoriteList = (ListView) findViewById(R.id.favoriteList);

        if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_FastRecord)

        {
            headView.setViewContent("收藏", "显示英文");
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
    }

    @Override
    protected void initListener() {
        if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_21) {
            headView.setOnBackBtnListener(new OnBackBtnListener() {

                @Override
                public void onClick() {
                    onBackPressed();
                }
            });
        }

        if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead) {
            favoriteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ctx, MakeSubgectFavorite.class);
                    intent.putExtra("currentPosition", position);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void initData() {
        adapter = new favoriteAdapter(ctx);
        favoriteList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        favorite = app.getDBManger(Constants.FavoriteRecord).GetAllRecords();
        app.CloseDBManger();

        if (favorite != null && favorite.size() > 0) {
            adapter.setList(favorite);
        }
    }

    public File createFilePath(String url) {

        // 扩展名位置
        int index = url.lastIndexOf('.');
        if (index == -1) {
            return null;
        }

        StringBuilder filePath = new StringBuilder();

        // 图片存取路径
        filePath.append(this.getExternalCacheDir().getAbsolutePath() + "/");
        filePath.append(MD5.Md5(url)).append(url.substring(index));

        return new File(filePath.toString());
    }

    private void play(String file) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(file);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            });
        } catch (Exception e) {
        }
    }

    /**
     * 播放音乐
     *
     * @param music
     */
    private void PlayMusic(String music) {
        File file = createFilePath(music);
        if (file.exists()) {
            play(file.getAbsolutePath());
        } else {
            downloadFile(file, music);
        }
    }

    /***
     * 使用通用库加载
     */
    private void downloadFile(final File file, String url) {

        showProgress();

        HttpUtil.getClient().get(url, new FileAsyncHttpResponseHandler(file) {

            int lastPercent = 0;

            @Override
            public void onSuccess(int arg0, Header[] arg1, File arg2) {
                dismissProgress();
                play(arg2.getAbsolutePath());
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                if (totalSize != 1) {
                    int percent = (int) (Math.round((bytesWritten * 1.0 / totalSize * 1.0) * 100));
                    if (percent != lastPercent) {
                        lastPercent = percent;
//                        MLog.e("下载进度", String.valueOf(percent) + "%");
                    }
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, Throwable arg2, File arg3) {
                dismissProgress();
            }
        });
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
     *
     */
    private class favoriteAdapter extends BaseListAdapter<HistoryBean> {

        public favoriteAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.favorite_item, null);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            SubjectBean tempBean = ((HistoryBean) getItem(position)).getFavorite();
            if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_FastRecord) {
                holder.chineseDis.setVisibility(isDisplayCH ? View.VISIBLE : View.INVISIBLE);
                holder.englishDis.setVisibility(isDisplayEn ? View.VISIBLE : View.INVISIBLE);

                holder.chineseDis.setText(tempBean.getQuestion());
                holder.englishDis.setText(tempBean.getEnglishStr());

                holder.favorite.setTag(position);
                holder.favorite.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int index = (Integer) v.getTag();
                        app.getDBManger(Constants.FavoriteRecord).deleteAHistoryRecord(favorite.get(index).getSort_id());
                        app.CloseDBManger();
                        favorite.remove(index);
                        adapter.setList(favorite);
                    }
                });

                String temp = Constants.ResourceAddress + "res" + getList().get(position).getFistId() + "/res" + getList().get(position).getSecodeId() + "/audio/" + tempBean.getMp3_addr();
                holder.item_fast.setTag(temp);
                holder.item_fast.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlayMusic((String) v.getTag());
                    }
                });
            } else {
                holder.chinese.setText(tempBean.getQuestion());
                holder.showAnswer.setTag(getString(tempBean.getAnswer().split("\\|")));
                holder.delete.setTag(position);
                String temp = Constants.ResourceAddress + "res" + getList().get(position).getFistId() + "/res" + getList().get(position).getSecodeId() + "/audio/" + tempBean.getMp3_addr();
                holder.playMusic.setTag(temp);

                holder.playMusic.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        PlayMusic((String) v.getTag());
                    }
                });

                holder.showAnswer.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        final DefaultDialog dialog = new DefaultDialog(WishActivity.this);
                        dialog.setDescription((String) v.getTag());
                        dialog.setBtnOk("确定");
                        dialog.setDialogTitle("答案");
                        dialog.setDialogListener(new DialogSelectListener() {

                            @Override
                            public void onChlidViewClick(View paramView) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                });

                holder.delete.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int index = (Integer) v.getTag();
                        app.getDBManger(Constants.FavoriteRecord).deleteAHistoryRecord(favorite.get(index).getSort_id());
                        app.CloseDBManger();
                        favorite.remove(index);
                        adapter.setList(favorite);
                    }
                });
            }

            if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead) {
                holder.takeAction.setVisibility(View.GONE);
                holder.actDelete.setVisibility(View.VISIBLE);
                holder.actDelete.setTag(position);
                holder.actDelete.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int index = (Integer) v.getTag();
                        app.getDBManger(Constants.FavoriteRecord).deleteAHistoryRecord(favorite.get(index).getSort_id());
                        app.CloseDBManger();
                        favorite.remove(index);
                        adapter.setList(favorite);
                    }
                });

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.chinese.getLayoutParams();
                params.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics());
                holder.chinese.setLayoutParams(params);
            }


            return convertView;
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

        private class ViewHolder {
            public TextView chinese;
            public TextView playMusic;
            public TextView showAnswer;
            public TextView delete;

            private View inFastRecord, item_fast, takeAction;
            private Button favorite;
            private TextView englishDis, chineseDis, actDelete;

            public ViewHolder(View view) {

                takeAction = view.findViewById(R.id.takeAction);
                chinese = (TextView) view.findViewById(R.id.chinese);
                playMusic = (TextView) view.findViewById(R.id.PlayMusic);
                showAnswer = (TextView) view.findViewById(R.id.ShowAnswer);
                delete = (TextView) view.findViewById(R.id.delete);

                inFastRecord = view.findViewById(R.id.inFastRecord);
                item_fast = view.findViewById(R.id.item_fast);

                actDelete = (TextView) view.findViewById(R.id.actDelete);
                favorite = (Button) view.findViewById(R.id.favorite);
                englishDis = (TextView) view.findViewById(R.id.englishDis);
                chineseDis = (TextView) view.findViewById(R.id.chineseDis);

                if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_FastRecord) {
                    favorite.setText("删除");
                    inFastRecord.setVisibility(View.GONE);
                    item_fast.setVisibility(View.VISIBLE);
                }

                view.setTag(this);
            }
        }
    }

}
