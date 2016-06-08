package com.hyx.android.Game351.history;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.alibaba.fastjson.JSON;
import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.base.BaseListAdapter;
import com.hyx.android.Game351.base.Constants;
import com.hyx.android.Game351.data.ApiCallBack;
import com.hyx.android.Game351.data.ApiHelper;
import com.hyx.android.Game351.data.ApiParams;
import com.hyx.android.Game351.data.Result;
import com.hyx.android.Game351.home.MakeSubgect;
import com.hyx.android.Game351.login.LoginActivity;
import com.hyx.android.Game351.modle.HistoryBean;
import com.hyx.android.Game351.modle.MenuDataBean;
import com.hyx.android.Game351.util.ApkType;
import com.hyx.android.Game351.util.MyTools;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.HeadView.OnActionBtnListener;
import com.hyx.android.Game351.view.dialog.DefaultDialog;
import com.hyx.android.Game351.view.dialog.DialogSelectListener;

import java.util.List;

public class HistoryActivity extends BaseActivity {

    private HeadView headView;
    private menuAdapter adapter;
    private GridView gridView;

    private List<HistoryBean> history;

    private int index = 0;

    @Override
    protected void initView() {
        setContentView(R.layout.history_acitivity);

        headView = (HeadView) findViewById(R.id.headView);
        headView.setViewContent(getResources().getString(R.string.home_record), getResources().getString(R.string.home_favorite_clear));

        gridView = (GridView) findViewById(R.id.menuDisplay);

    }

    @Override
    protected void initListener() {

        if (MyTools.getCurrentApkType(ctx) == ApkType.TYPE_CopyRead) {
            headView.setOnBackBtnListener(new HeadView.OnBackBtnListener() {

                @Override
                public void onClick() {
                    onBackPressed();
                }
            });
        }

        headView.setOnActionBtnListener(new OnActionBtnListener() {

            @Override
            public void onClick() {
                final DefaultDialog dialog = new DefaultDialog(HistoryActivity.this);
                dialog.setDescription("你确定清除所有历史记录吗？");
                dialog.setBtnCancle("暂不清除");
                dialog.setBtnOk("立即清除");
                dialog.setDialogTitle("提示");
                dialog.setDialogListener(new DialogSelectListener() {

                    @Override
                    public void onChlidViewClick(View paramView) {
                        dialog.dismiss();
                        if (paramView.getId() == R.id.btn_ok) {
                            app.getDBManger(Constants.HistoryRecord).DeleteAllHistory();
                            app.CloseDBManger();
                            history.clear();
                            adapter.setList(null);
                        }
                    }
                });

                dialog.show();

            }
        });
    }

    @Override
    protected void initData() {

        adapter = new menuAdapter(ctx);
        gridView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        history = app.getDBManger(Constants.HistoryRecord).GetAllRecords();
        app.CloseDBManger();

        if (history != null && history.size() > 0) {
            adapter.setList(history);
        }
    }

    /**
     * 获取分类
     */
    private void GetdataCategory(String fid) {

        showProgress();

        ApiParams params = new ApiParams();
        params.put("action", "getSort");
        params.put("username", app.getUserName());
        params.put("fid", fid);
        params.put("logindate", app.getUserLoginDate());
        params.put("checkcode", MyTools.getMD5_32(app.getUserName() + Constants.checkCode));

        ApiHelper.get(ctx, params, new ApiCallBack() {

            @Override
            public void receive(Result result) {
                dismissProgress();

                if (result.isSuccess()) {
                    MenuDataBean temp = JSON.parseObject(result.getObj().toString(), MenuDataBean.class);
                    if (temp.isSuccess()) {
                        HistoryBean data = history.get(index);
                        Intent intent = new Intent(HistoryActivity.this, MakeSubgect.class);
                        intent.putExtra("data", JSON.toJSONString(data.getData()));
                        intent.putExtra("title", data.getTitle());
                        intent.putExtra("FistId", data.getFistId());
                        intent.putExtra("secodeId", data.getSecodeId());
                        intent.putExtra("AllSubjects", JSON.toJSONString(data.getAllSubjects()));
                        intent.putExtra("currentPosition", data.getCurrentPosition());
                        startActivity(intent);
                    } else {
                        MyToast(temp.getInfo());
                        app.setVIPuserStatus(temp.getFlag());
                        if (temp.getFlag() == 0) {
                            app.exit();
                            Intent intent = new Intent(ctx, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ctx.startActivity(intent);
                        } else {
                            //弹出支付界面
                            showNeedToAiPlay();
                        }
                    }
                } else {
                    MyToast(result.getObj().toString());
                }
            }
        });
    }

    private class menuAdapter extends BaseListAdapter<HistoryBean> {

        public menuAdapter(Context context) {
            super(context);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Button item = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.menu_item, null);
                item = (Button) convertView.findViewById(R.id.menuTit);
                convertView.setTag(item);
            } else {
                item = (Button) convertView.getTag();
            }

            item.setTag(position);
            item.setText(getList().get(position).getTitle() + "\n" + getList().get(position).getRecordTime());

            item.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    index = (Integer) v.getTag();
                    GetdataCategory(history.get(index).getSecodeId());
                }
            });

            return convertView;
        }
    }

}
