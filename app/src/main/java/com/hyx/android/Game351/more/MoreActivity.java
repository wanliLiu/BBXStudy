package com.hyx.android.Game351.more;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.favorite.WishActivity;
import com.hyx.android.Game351.history.HistoryActivity;
import com.hyx.android.Game351.login.LoginActivity;
import com.hyx.android.Game351.util.ApkType;
import com.hyx.android.Game351.util.MyTools;
import com.hyx.android.Game351.util.SP;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.UISwitchButton;
import com.hyx.android.Game351.view.dialog.DefaultDialog;
import com.hyx.android.Game351.view.dialog.DialogSelectListener;

import java.io.File;
import java.math.BigDecimal;

import br.com.dina.ui.model.BasicItem;
import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.OnTableItemClickListener;

public class MoreActivity extends BaseActivity {

    private static final int RQF_Display = 1;
    private static final String ATTR_PACKAGE_STATS = "PackageStats";
    private HeadView headView;
    private UITableView moreSetting;
    private UISwitchButton switchCopyZh, switchCopyAnswer, switchCopypic;
    private UISwitchButton toggleButton, toggleButtonAutoDisplay;

    private SeekBar seekBar, fontSeekBar, showtimeSeekBar;
    private TextView timeProgress, fontTextView, showtimeProgress;

    private LinearLayout exit;

    /**
     * 异步处理删除文件，读文件的大小
     */
    private Handler cacheHander = new Handler() {
        public void handleMessage(Message msg) {

            int index = (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_FastRecord || MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead || MyTools.getCurrentApkType(ctx) == ApkType.TYPE_MEIJU) ? 2 : 3;

            switch (msg.what) {
                case 1: {
                    showProgress();
                    File[] file = getApplicationContext().getExternalCacheDir().listFiles();

                    for (File file2 : file) {
                        file2.delete();
                    }
                    ;
                    dismissProgress();
                    MyToast("清除缓存成功！");
                    BasicItem temp = moreSetting.getBasicItem(index);
                    temp.setTitle("清除缓存(" + formatFileSize(app.getCacheFileSize()) + ")");
                    moreSetting.updateBasicItem(temp, index);
                }
                break;
                case 2: {
                    BasicItem temp = moreSetting.getBasicItem(index);
                    temp.setTitle("清除缓存(" + formatFileSize(app.getCacheFileSize()) + ")");
                    moreSetting.updateBasicItem(temp, index);
                }
                break;
                default:
                    break;
            }
        }

        ;
    };

    /**
     * @param length
     * @return
     */
    public static String formatFileSize(long length) {
        String result = null;
        int sub_string = 0;
        if (length >= 1073741824) {
            sub_string = String.valueOf((float) length / 1073741824).indexOf(
                    ".");
            result = ((float) length / 1073741824 + "000").substring(0,
                    sub_string + 3) + "GB";
        } else if (length >= 1048576) {
            sub_string = String.valueOf((float) length / 1048576).indexOf(".");
            result = ((float) length / 1048576 + "000").substring(0,
                    sub_string + 3) + "MB";
        } else if (length >= 1024) {
            sub_string = String.valueOf((float) length / 1024).indexOf(".");
            result = ((float) length / 1024 + "000").substring(0,
                    sub_string + 3) + "KB";
        } else if (length < 1024)
            result = Long.toString(length) + "B";
        return result;
    }

    @Override
    protected void initView() {
        setContentView(R.layout.more_acitivity);

        headView = (HeadView) findViewById(R.id.headView);
        headView.setTitle(getResources().getString(R.string.home_more));

        moreSetting = (UITableView) findViewById(R.id.moreSetting);

        if (MyTools.getCurrentApkType(ctx) != ApkType.TYPE_FastRecord) {
            if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead ||
                    MyTools.getCurrentApkType(ctx) == ApkType.TYPE_MEIJU) {
//                moreSetting.addBasicItem("记录");// 0
            }
            else
                moreSetting.addBasicItem("收藏");// 0
        }

        moreSetting.addBasicItem("使用说明");// 1
//        if (app.isVIPuser())
//            moreSetting.addBasicItem("你已经是VIP 欢迎你", R.drawable.transparent);// 2
//        else
        moreSetting.addBasicItem("升级到VIP", R.drawable.transparent);// 2
        moreSetting.addBasicItem("清除缓存（0.0kb)", R.drawable.transparent);// 3
        moreSetting.addBasicItem("关于我们");// 4
        moreSetting.addBasicItem("好评", R.drawable.transparent);// 5
//        moreSetting.addBasicItem("退出登录", R.drawable.transparent);// 6
//        moreSetting.addBasicItem("个人空间", R.drawable.transparent);// 7

        moreSetting.commit();

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        timeProgress = (TextView) findViewById(R.id.timeProgress);

        fontSeekBar = (SeekBar) findViewById(R.id.Fontseekbar);
        fontTextView = (TextView) findViewById(R.id.fontProgress);

        toggleButton = (UISwitchButton) findViewById(R.id.toggleButton);


        if (app.isPlayContinue()) {
            toggleButton.setChecked(true);
        }

        exit = (LinearLayout) findViewById(R.id.exit);

        showtimeSeekBar = (SeekBar) findViewById(R.id.showtimeSeekBar);
        showtimeProgress = (TextView) findViewById(R.id.showtimeProgress);
        toggleButtonAutoDisplay = (UISwitchButton) findViewById(R.id.toggleButtonAutoDisplay);
        if (app.isAutoDisplay()) {
            toggleButtonAutoDisplay.setChecked(true);
        }

        if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead ||
                MyTools.getCurrentApkType(ctx) == ApkType.TYPE_MEIJU) {
//            findViewById(R.id.inCopy).setVisibility(View.GONE);
//            findViewById(R.id.inCopy1).setVisibility(View.GONE);
            findViewById(R.id.inCopy2).setVisibility(View.GONE);
            findViewById(R.id.inCopy3).setVisibility(View.GONE);

            findViewById(R.id.switchzh).setVisibility(View.VISIBLE);
            findViewById(R.id.switchAnswer).setVisibility(View.VISIBLE);
            findViewById(R.id.switchPic).setVisibility(View.VISIBLE);

            switchCopyZh = (UISwitchButton) findViewById(R.id.switchCopyZh);
            switchCopyZh.setTag(SP.CopySwitchZh);
            switchCopyAnswer = (UISwitchButton) findViewById(R.id.switchCopyAnswer);
            switchCopyAnswer.setTag(SP.CopySwitchAnswer);
            switchCopypic = (UISwitchButton) findViewById(R.id.switchCopypic);
            switchCopypic.setTag(SP.CopySwitchpic);

            switchCopyZh.setChecked(app.getSwitch(SP.CopySwitchZh));
            switchCopyAnswer.setChecked(app.getSwitch(SP.CopySwitchAnswer));
            switchCopypic.setChecked(app.getSwitch(SP.CopySwitchpic));

            switchCopyZh.setOnCheckedChangeListener(new oncopyCheck());
            switchCopyAnswer.setOnCheckedChangeListener(new oncopyCheck());
            switchCopypic.setOnCheckedChangeListener(new oncopyCheck());
        }

        if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_FastRecord) {
            findViewById(R.id.inCopy).setVisibility(View.GONE);
            findViewById(R.id.inCopy1).setVisibility(View.GONE);
            findViewById(R.id.inCopy2).setVisibility(View.GONE);
            findViewById(R.id.inCopy3).setVisibility(View.GONE);
            findViewById(R.id.inCopy4).setVisibility(View.GONE);
        }
    }

    @Override
    protected void initListener() {

        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                app.SetPlayTime(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                timeProgress.setText("连续播放时间间隔（"
                        + String.valueOf(new BigDecimal((progress + 200) * 0.1 / 100.0)
                        .setScale(1, BigDecimal.ROUND_HALF_UP)) + "秒）");
            }
        });

        fontSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                app.SetFontSize(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                fontTextView.setText("做题字体的大小（" + (progress + 14) + "）");
            }
        });


        showtimeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                app.setShowtime(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                showtimeProgress.setText("自动播放时间间隔（"
                        + String.valueOf(new BigDecimal((progress + 100) * 0.1 / 100.0)
                        .setScale(1, BigDecimal.ROUND_HALF_UP)) + "秒）");
            }
        });
        toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                app.PlayContinue(isChecked);
            }
        });

        toggleButtonAutoDisplay.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                app.AutoDisplay(isChecked);
            }
        });

        moreSetting.setOnTableItemClickListener(new OnTableItemClickListener() {

            @Override
            public void onItemClick(int index) {

                if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_FastRecord ||
                        MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead ||
                        MyTools.getCurrentApkType(ctx) == ApkType.TYPE_MEIJU)
                    index += 1;


                switch (index) {
                    case 0:
                        if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead ||
                                MyTools.getCurrentApkType(ctx) == ApkType.TYPE_MEIJU) {
                            startActivity(new Intent(MoreActivity.this, HistoryActivity.class));
                        } else {
                            startActivity(new Intent(MoreActivity.this, WishActivity.class));
                        }

                        break;
                    case 1:
                        startActivity(new Intent(MoreActivity.this, InstructionActivity.class));
                        break;
                    case 2: {
                        //弹出支付界面
//                            if (!app.isVIPuser())
//                            {
                        showNeedToAiPlay();
//                            }
                    }
                    break;
                    case 3: {
                        final DefaultDialog dialog = new DefaultDialog(MoreActivity.this);
                        dialog.setDescription("你确定清除缓存？");
                        dialog.setBtnCancle("暂不清除");
                        dialog.setBtnOk("清除");
                        dialog.setDialogTitle("提示");
                        dialog.setDialogListener(new DialogSelectListener() {

                            @Override
                            public void onChlidViewClick(View paramView) {
                                dialog.dismiss();
                                if (paramView.getId() == R.id.btn_ok) {
                                    cacheHander.sendEmptyMessage(1);
                                }
                            }
                        });

                        dialog.show();
                    }
                    break;
                    case 4:
                        startActivity(new Intent(MoreActivity.this, AboutUsActivity.class));
                        break;
                    case 5: {
                        Uri uri = Uri.parse("http://m.anzhi.com/info_1807031.html");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
//                          e.printStackTrace();
//                          Toast.makeText(context, "Couldn't launch the market !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;
                    case 6:

                        break;
                    case 7:
//                    	startActivity(new Intent(MoreActivity.this, PersionPosition.class));
                        break;
                    default:
                        break;
                }
            }
        });

        exit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                app.exit();
                Intent intent = new Intent(ctx, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
        });
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // getpkginfo("com.hyx.android.Game351");

        seekBar.setProgress(app.getPlayTime() - 200);
        fontSeekBar.setProgress(app.getFontSize() - 14);
        fontTextView.setText("做题字体的大小（" + app.getFontSize() + "）");

        timeProgress
                .setText("连续播放时间间隔（"
                        + String.valueOf(new BigDecimal(
                        app.getPlayTime() * 0.1 / 100.0).setScale(1,
                        BigDecimal.ROUND_HALF_UP)) + "秒）");


        showtimeSeekBar.setProgress(app.getShowtime() - 200);
        showtimeProgress
                .setText("自动播放时间间隔（"
                        + String.valueOf(new BigDecimal(
                        app.getShowtime() * 0.1 / 100.0).setScale(1,
                        BigDecimal.ROUND_HALF_UP)) + "秒）");


        cacheHander.sendEmptyMessage(2);
    }

    private class oncopyCheck implements OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            app.setSwitch(buttonView.getTag().toString(), isChecked);
        }
    }
}
