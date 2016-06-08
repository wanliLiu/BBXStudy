package com.hyx.android.Game351.home;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyx.android.Game351.Personal.PersionPosition;
import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.base.BaseListAdapter;
import com.hyx.android.Game351.base.Constants;
import com.hyx.android.Game351.data.ApiCallBack;
import com.hyx.android.Game351.data.ApiHelper;
import com.hyx.android.Game351.data.ApiParams;
import com.hyx.android.Game351.data.Result;
import com.hyx.android.Game351.login.LoginActivity;
import com.hyx.android.Game351.modle.MenuData;
import com.hyx.android.Game351.modle.MenuDataBean;
import com.hyx.android.Game351.modle.SubjectBean;
import com.hyx.android.Game351.util.MyTools;
import com.hyx.android.Game351.view.CircularImageView;

import java.util.List;

public class MenuActivity extends BaseActivity {

    private GridView gridView;
    private menuAdapter adapter;

    private MenuDataBean menuData_1, menuData_2, menuData_3;

    private int category = 1;
    private String titleString = "";
    private String FistId, secodeId;

    private int CurrentPosition = 0;

    private ImageButton ibtn_back;
    private CircularImageView image;

    @Override
    protected void initView() {
        setContentView(R.layout.menu_acitivity);

        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        // headView.setTitle(getResources().getString(R.string.home_menu));

        image = (CircularImageView) findViewById(R.id.image);

        gridView = (GridView) findViewById(R.id.menuDisplay);
    }

    @Override
    protected void initListener() {

        ibtn_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (category) {
                    case 4:
                    case 3:
                        category = 2;
                        adapter.setList(menuData_2.getData());
                        break;
                    case 2:
                        category = 1;
                        ibtn_back.setVisibility(View.INVISIBLE);
                        adapter.setList(menuData_1.getData());
                        break;
                    default:
                        break;
                }
            }
        });
        ibtn_back.setVisibility(View.INVISIBLE);

        image.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, PersionPosition.class);
                intent.putExtra("uid", app.getUserID());
                intent.putExtra("UserName", app.getUserName());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {

        adapter = new menuAdapter(ctx);
        gridView.setAdapter(adapter);

        GetdataCategory("0");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!TextUtils.isEmpty(app.getUserAvatard())) {
            imageLoader.displayImage(app.getUserAvatard(), image, optionsHead);
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
                        if (temp.getData() != null && temp.getData().size() > 0) {
                            switch (category) {
                                case 1:
                                    menuData_1 = temp;
                                    break;
                                case 2:
                                    menuData_2 = temp;
                                    ibtn_back.setVisibility(View.VISIBLE);
                                    break;
                                case 3:
                                    category = 4;
                                    menuData_3 = temp;
                                    ibtn_back.setVisibility(View.VISIBLE);
                                    break;
                                default:
                                    break;
                            }
                            adapter.setList(temp.getData());
                            gridView.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    gridView.setSelection(0);
                                }
                            }, 20);
                        } else {
                            if (category == 2 || category == 3) {
                                category--;
                            }
                        }
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
                            // 弹出支付界面
                            showNeedToAiPlay();
                        }
                    }
                } else {
                    MyToast(result.getObj().toString());
                }
            }
        });
    }

    /**
     * 升级到VIP
     */
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

                if (result.isSuccess()) {
                    JSONObject json = JSON.parseObject(result.getObj().toString());
                    if (json.getBooleanValue("success")) {
                        List<SubjectBean> temp = JSON.parseArray(json.getString("data"), SubjectBean.class);
                        if (temp != null && temp.size() > 0) {
                            Intent intent = new Intent(MenuActivity.this, MakeSubgect.class);
                            intent.putExtra("data", json.getString("data"));
                            intent.putExtra("title", titleString);
                            intent.putExtra("FistId", FistId);
                            intent.putExtra("secodeId", secodeId);
                            intent.putExtra("AllSubjects", JSON.toJSONString(menuData_3));
                            intent.putExtra("currentPosition", CurrentPosition);
                            startActivity(intent);
                        }
                    } else {
                        MyToast(json.getString("info"));
                        app.exit();
                        Intent intent = new Intent(ctx, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ctx.startActivity(intent);
                    }
                } else {
                    MyToast(result.getObj().toString());
                }
            }
        });
    }

    private class menuAdapter extends BaseListAdapter<MenuData> {

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
            item.setText(getList().get(position).getSort_name());

            item.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    switch (category) {
                        case 1:
                            category = 2;
                            FistId = getList().get((Integer) v.getTag()).getSort_id();
                            GetdataCategory(FistId);
                            break;
                        case 2:
                            category = 3;
                            secodeId = getList().get((Integer) v.getTag()).getSort_id();
                            GetdataCategory(secodeId);
                            break;
                        case 3:
                            GetdataCategory(getList().get((Integer) v.getTag()).getSort_id());
                            break;
                        default:
                            titleString = getList().get((Integer) v.getTag()).getSort_name();
                            CurrentPosition = (Integer) v.getTag();
                            GetdataSubject(getList().get((Integer) v.getTag()).getSort_id());
                            break;
                    }

                }
            });

            return convertView;
        }
    }
}
