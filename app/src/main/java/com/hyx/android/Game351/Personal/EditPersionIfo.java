package com.hyx.android.Game351.Personal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.base.BaseListAdapter;
import com.hyx.android.Game351.base.Constants;
import com.hyx.android.Game351.data.ApiCallBack;
import com.hyx.android.Game351.data.ApiHelper;
import com.hyx.android.Game351.data.Result;
import com.hyx.android.Game351.modle.ReviewsUploadPicture;
import com.hyx.android.Game351.modle.UserIfoBean;
import com.hyx.android.Game351.util.DisplayUtil;
import com.hyx.android.Game351.util.MemoryStatus;
import com.hyx.android.Game351.util.MyTools;
import com.hyx.android.Game351.util.PicutreSelect;
import com.hyx.android.Game351.util.PicutreSelect.onPictureSelectListener;
import com.hyx.android.Game351.view.CityPicker;
import com.hyx.android.Game351.view.HeadView;
import com.hyx.android.Game351.view.HeadView.OnActionBtnListener;
import com.hyx.android.Game351.view.HeadView.OnBackBtnListener;
import com.hyx.android.Game351.view.NonScrollGridView;
import com.hyx.android.Game351.view.dialog.DefaultDialog;
import com.hyx.android.Game351.view.dialog.DialogSelectListener;
import com.loopj.android.http.RequestParams;
import com.maogousoft.actionsheet.ActionSheet;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import br.com.dina.ui.model.RoundImageView;
import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.OnTableItemClickListener;

public class EditPersionIfo extends BaseActivity {

    private static final int OpenSign_code = 10;
    private static final int PicrueCrop = 12;

    // 能够添加图片的个数
    private static final int CAN_ADD_PICUTRE_NUM = 8;

    private HeadView headView;
    private NonScrollGridView pictureAdd;
    private ReviewsPictureAddAdapter adapter;

    private int imageUploadIndex = 0;
    private PicutreSelect picutreSelect = null;

    private LinearLayout Editcontainer;

    private int WhoNeedPic = -1;

    private String[] sexSelect = new String[]{"我是男生", "我是女生"};

    private UserIfoBean userIfo;

    private int mYear = 1989, mMonth = 7, mDay = 18;
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            String mm;
            String dd;
            if (monthOfYear <= 9) {
                mMonth = monthOfYear + 1;
                mm = "0" + mMonth;
            } else {
                mMonth = monthOfYear + 1;
                mm = String.valueOf(mMonth);
            }
            if (dayOfMonth <= 9) {
                mDay = dayOfMonth;
                dd = "0" + mDay;
            } else {
                mDay = dayOfMonth;
                dd = String.valueOf(mDay);
            }
            mDay = dayOfMonth;

            String temp = String.valueOf(mYear) + "-" + mm + "-" + dd;
            ViewHoler holer = (ViewHoler) Editcontainer.getChildAt(4).getTag();
            holer.setEdit(true);
            holer.setNeedUp(temp);
            holer.content.setText(temp);
            holer.content.setTextColor(getResources().getColor(R.color.color_black));
        }
    };

    @Override
    protected void initView() {
        setContentView(R.layout.edit_person_ifo);

        headView = (HeadView) findViewById(R.id.headView);
        headView.getBtnBack().setImageResource(R.drawable.back);
        headView.setTitle("修改资料");

        headView.getBtnAction().setText("保存");

        pictureAdd = (NonScrollGridView) findViewById(R.id.PictureLoad);
        adapter = new ReviewsPictureAddAdapter(ctx);
        pictureAdd.setAdapter(adapter);
        addImageDisplay();

        Editcontainer = (LinearLayout) findViewById(R.id.Editcontainer);

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

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
                IfoCommit();
            }
        });
    }

    @Override
    protected void initData() {
        userIfo = (UserIfoBean) getIntent().getSerializableExtra("UserIfo");

        InitEditView();
    }

    /**
     * 代码生成
     */
    private void InitEditView() {
        {
            // 头像1
            View view1 = getEditView();
            ViewHoler holer = new ViewHoler(view1);
            holer.title.setText("头像");
            holer.content.setText("点击更换");
            holer.imageRight.setImageResource(R.drawable.default_avatar);
            holer.setIndex(0);
            if (userIfo != null) {
                imageLoader.displayImage(
                        Constants.ServerTopicAdd + userIfo.getAvatar(),
                        holer.imageRight, optionsHead);
            }
            view1.setOnClickListener(this);
            addEditView(view1);
        }
        {
            // 封面2
            View view2 = getEditView();
            ViewHoler holer = new ViewHoler(view2);
            holer.title.setText("封面");
            holer.content.setText("点击更换");
            holer.setIndex(1);
            holer.imageRight.setImageResource(R.drawable.default_image_320320);
            if (userIfo != null) {
                imageLoader.displayImage(
                        Constants.ServerTopicAdd + userIfo.getFace(),
                        holer.imageRight, optionBack);
            }
            view2.setOnClickListener(this);
            addEditView(view2);
        }
        {
            // 昵称
            View view3 = getEditView();
            ViewHoler holer = new ViewHoler(view3);
            holer.title.setText("昵称");
            holer.content.setText("点击输入");
            if (userIfo != null) {
                holer.content.setText(userIfo.getUser_nick_name());
                holer.setEdit(true);
                holer.setNeedUp(userIfo.getUser_nick_name());
                holer.content.setTextColor(getResources().getColor(
                        R.color.color_black));
            }
            holer.setIndex(9);
            view3.setOnClickListener(this);
            holer.RightSide.setVisibility(View.GONE);
            addEditView(view3);
        }
        {
            // 性别3
            View view3 = getEditView();
            ViewHoler holer = new ViewHoler(view3);
            holer.title.setText("性别");
            holer.content.setText("请选择...");
            holer.setIndex(2);
            view3.setOnClickListener(this);
            holer.RightSide.setVisibility(View.GONE);
            addEditView(view3);
            if (userIfo != null) {
                if (userIfo.getSex() == 1) {
                    holer.content.setText("我是男生");
                } else {
                    holer.content.setText("我是女生");
                }
                holer.content.setTextColor(getResources().getColor(
                        R.color.color_black));
            }
        }
        {
            // 年龄4
            View view4 = getEditView();
            ViewHoler holer = new ViewHoler(view4);
            holer.title.setText("出生日期");
            holer.content.setText("点击输入");
            if (userIfo != null) {
                holer.content.setText(userIfo.getBirthday());
                holer.setEdit(true);
                holer.setNeedUp(userIfo.getBirthday());
                holer.content.setTextColor(getResources().getColor(
                        R.color.color_black));
                try {
                    String[] ddStrings = userIfo.getBirthday().split("-");
                    mYear = Integer.parseInt(ddStrings[0]);
                    mMonth = Integer.parseInt(ddStrings[1]);
                    mDay = Integer.parseInt(ddStrings[2]);
                } catch (Exception e) {
                }

            }
            holer.setIndex(3);
            view4.setOnClickListener(this);
            holer.RightSide.setVisibility(View.GONE);
            addEditView(view4);
        }
        {
            // 地区5
            View view5 = getEditView();
            ViewHoler holer = new ViewHoler(view5);
            holer.title.setText("地区");
            holer.content.setText("请选择...");
            if (userIfo != null) {
                holer.content.setText(userIfo.getArea());
                holer.setEdit(true);
                holer.setNeedUp(userIfo.getArea());
                holer.content.setTextColor(getResources().getColor(
                        R.color.color_black));
            }
            holer.setIndex(4);
            view5.setOnClickListener(this);
            holer.RightSide.setVisibility(View.GONE);
            addEditView(view5);
        }
        {
            // 学校6
            View view6 = getEditView();
            ViewHoler holer = new ViewHoler(view6);
            holer.title.setText("学校");
            holer.content.setText("你在哪里读书");
            if (userIfo != null) {
                holer.content.setText(userIfo.getSchool());
                holer.setEdit(true);
                holer.setNeedUp(userIfo.getSchool());
                holer.content.setTextColor(getResources().getColor(
                        R.color.color_black));
            }
            holer.setIndex(5);
            view6.setOnClickListener(this);
            holer.RightSide.setVisibility(View.GONE);
            addEditView(view6);
        }

        {
            // 职业7
            View view7 = getEditView();
            ViewHoler holer = new ViewHoler(view7);
            holer.title.setText("职业");
            holer.setIndex(6);
            holer.content.setText("你的职业是？");
            if (userIfo != null) {
                holer.content.setText(userIfo.getVocation());
                holer.setEdit(true);
                holer.setNeedUp(userIfo.getVocation());
                holer.content.setTextColor(getResources().getColor(
                        R.color.color_black));
            }
            view7.setOnClickListener(this);
            holer.RightSide.setVisibility(View.GONE);
            addEditView(view7);
        }
        {
            // 邮箱8
            View view8 = getEditView();
            ViewHoler holer = new ViewHoler(view8);
            holer.title.setText("邮箱");
            holer.content.setText("点击输入");
            if (userIfo != null) {
                holer.content.setText(userIfo.getEmail());
                holer.setEdit(true);
                holer.setNeedUp(userIfo.getEmail());
                holer.content.setTextColor(getResources().getColor(
                        R.color.color_black));
            }
            view8.setOnClickListener(this);
            holer.setIndex(7);
            holer.RightSide.setVisibility(View.GONE);
            addEditView(view8);
        }
        {
            // 签名9
            View view9 = getEditView();
            ViewHoler holer = new ViewHoler(view9);
            holer.setIndex(8);
            holer.title.setText("签名");
            view9.setOnClickListener(this);
            holer.content.setText("一句话表达你的个性吧...");
            if (userIfo != null) {
                holer.content.setText(userIfo.getSign());
                holer.setEdit(true);
                holer.setNeedUp(userIfo.getSign());
                holer.content.setTextColor(getResources().getColor(
                        R.color.color_black));
            }
            holer.RightSide.setVisibility(View.GONE);
            addEditView(view9);
        }
    }

    /**
     * 提交信息
     */
    private void IfoCommit() {

        try {
            showProgress();
            RequestParams params = new RequestParams();

            params.put("action", "post_myspace");
            params.put("uid", app.getUserID());
            params.put("username", app.getUserName());

            // 添加背景强图片
            for (int i = 0; i < adapter.getList().size(); i++) {
                if (!adapter.getList().get(i).isAddPicure) {
                    String temp = "bg" + (i + 1) + "[]";
                    params.put(temp, new File(
                            adapter.getList().get(i).picureFilePath));
                }
            }
            // 添加的数据打包
            for (int i = 0; i < Editcontainer.getChildCount(); i++) {
                ViewHoler holer = (ViewHoler) Editcontainer.getChildAt(i)
                        .getTag();
                if (holer.isEdit()) {
                    switch (i) {
                        case 0:
                            params.put("pic[]", new File(holer.getNeedUp()));
                            break;
                        case 1:
                            params.put("user_upload_file[]",
                                    new File(holer.getNeedUp()));
                            break;
                        case 2:
                            params.put("user_nick_name", holer.getNeedUp());
                            break;
                        case 3:
                            if (holer.getNeedUp().equals("我是男生")) {
                                params.put("sex", "1");
                            } else {
                                params.put("sex", "2");
                            }
                            break;
                        case 4:
                            params.put("birthday", holer.getNeedUp());
                            break;
                        case 5:
                            params.put("area", holer.getNeedUp());
                            break;
                        case 6:
                            params.put("school", holer.getNeedUp());
                            break;
                        case 7:
                            params.put("vocation", holer.getNeedUp());
                            break;
                        case 8:
                            params.put("email", holer.getNeedUp());
                            break;
                        case 9:
                            params.put("sign", holer.getNeedUp());
                            break;
                        default:
                            break;
                    }
                }
            }
            params.put("password", app.getUserPassword());
            params.put("checkcode",
                    MyTools.getMD5_32(app.getUserName() + Constants.checkCode));

            ApiHelper.uploadFile(ctx, params, new ApiCallBack() {

                @Override
                public void receive(Result result) {
                    try {
                        dismissProgress();
                        if (result.isSuccess()) {
                            JSONObject json = JSON.parseObject(result.getObj()
                                    .toString());
                            MyToast(json.getString("info"));
                            if (json.getBooleanValue("success")) {
                                // backToBegain();
                                setResult(RESULT_OK);
                                EditPersionIfo.this.finish();
                            }
                        } else {
                            MyToast(result.getObj().toString());
                        }
                    } catch (Exception e) {
                        MyToast("上传图片出错");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            dismissProgress();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        ViewHoler holer = (ViewHoler) v.getTag();
        switch (holer.getIndex()) {
            case 0:
                WhoNeedPic = 1;
                picutreSelect = new PicutreSelect(ctx);
                break;
            case 1:
                WhoNeedPic = 2;
                picutreSelect = new PicutreSelect(ctx);
                break;
            case 2:
                ShowChoseDialog(3, sexSelect);
                break;
            case 3:
//			dialogInput(4, "输入你的出生日期");
                showDialog(0);
                // ShowChoseDialog(4,ageSelect);
                break;
            case 4:
                ShowCitySelectIfo();
                break;
            case 5:
                dialogInput(6, "请输入学校名称");
                break;
            case 6:
                dialogInput(7, "你的职业");
                break;
            case 7:
                dialogInput(8, "邮箱地址");
                break;
            case 8: {
                Intent intent = new Intent(this, EditSign.class);
                if (!TextUtils.isEmpty(holer.content.getText().toString())
                        && holer.isEdit()) {
                    intent.putExtra("Content", holer.content.getText().toString());
                }
                startActivityForResult(intent, OpenSign_code);
            }
            break;
            case 9:
                dialogInput(2, "输入昵称");
                break;
            default:
                break;
        }
    }

    protected Dialog onCreateDialog(int id) {

        return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
    }

    /**
     *
     */
    private void ShowCitySelectIfo() {
        final ActionSheet actionSheet = new ActionSheet(ctx);
        actionSheet.setOutSideTouch(false);
        View view = LayoutInflater.from(ctx)
                .inflate(R.layout.city_select, null);

        final CityPicker picker = (CityPicker) view
                .findViewById(R.id.cityPicker);
        final Button btnSure = (Button) view.findViewById(R.id.btnSure);
        final Button btnCancle = (Button) view.findViewById(R.id.btnCancle);
        btnSure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                actionSheet.dismiss();
                ViewHoler holer = (ViewHoler) Editcontainer.getChildAt(5)
                        .getTag();
                holer.setEdit(true);
                holer.setNeedUp(picker.getCity_string());
                holer.content.setText(picker.getCity_string());
                holer.content.setTextColor(getResources().getColor(
                        R.color.color_black));
            }
        });

        btnCancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                actionSheet.dismiss();
            }
        });

        actionSheet.addView(view);
        actionSheet.setCanclebtnVisibility(View.GONE);
    }

    /**
     *
     */
    private void ShowChoseDialog(final int index, final String[] content) {
        final ActionSheet actionSheet = new ActionSheet(ctx);

        final UITableView table = new UITableView(ctx);

        for (int i = 0; i < content.length; i++) {
            table.addBasicItem(content[i], R.drawable.transparent);
        }
        table.commit();

        table.setOnTableItemClickListener(new OnTableItemClickListener() {

            @Override
            public void onItemClick(int mindex) {
                actionSheet.dismiss();
                ViewHoler holer = (ViewHoler) Editcontainer.getChildAt(index)
                        .getTag();
                holer.setEdit(true);
                holer.setNeedUp(content[mindex]);
                holer.content.setText(content[mindex]);
                holer.content.setTextColor(getResources().getColor(
                        R.color.color_black));
            }
        });

        actionSheet.addView(table);
        actionSheet.setCanclebtnVisibility(View.GONE);
    }

    /**
     * @return
     */
    private View getInputView() {
        return LayoutInflater.from(ctx).inflate(
                R.layout.custom_input_dialog_content, null);
    }

    /*
     * 弹出输入对话框
     */
    private void dialogInput(final int index, String title) {
        // 获取签名内容
        final ViewHoler holer = (ViewHoler) Editcontainer.getChildAt(index).getTag();

        View view = getInputView();
        ((TextView) view.findViewById(R.id.intputTitle)).setText(title);
        final EditText input = (EditText) view.findViewById(R.id.input);

        if (holer.isEdit()
                && !TextUtils.isEmpty(holer.content.getText().toString())) {
            input.setText(holer.content.getText().toString());
        } else {
            input.setText("");
        }

        final DefaultDialog dialog = new DefaultDialog(EditPersionIfo.this);
        dialog.setBtnCancle("取消");
        dialog.setBtnOk("确定");
        dialog.AddView(view);
        dialog.setDialogListener(new DialogSelectListener() {

            @Override
            public void onChlidViewClick(View paramView) {

                if (paramView.getId() == R.id.btn_ok) {
                    String temp = input.getText().toString();
                    if (!TextUtils.isEmpty(temp)) {
                        holer.setEdit(true);
                        holer.setNeedUp(temp);
                        holer.content.setText(temp);
                        holer.content.setTextColor(getResources().getColor(R.color.color_black));
                    } else {
                        MyToast("输入的内容不能为空");
                    }
                }
            }
        });

        dialog.show();
    }

    /**
     * @param view
     */
    private void addEditView(View view) {
        Editcontainer.addView(view);
    }

    /**
     * 获取自定义view
     *
     * @return
     */
    private View getEditView() {
        return LayoutInflater.from(ctx).inflate(R.layout.edit_person_title,
                null);
    }

    /**
     * 增加图片选择按钮
     */
    private void addImageDisplay() {
        if (adapter.getCount() < CAN_ADD_PICUTRE_NUM) {
            adapter.add(new ReviewsUploadPicture(true, ""));
        }
    }

    /**
     * 显示图片
     *
     * @param filePath
     */
    private void setImage(String filePath) {
        try {
            switch (WhoNeedPic) {
                case -1:
                    if (adapter.getCount() <= CAN_ADD_PICUTRE_NUM) {
                        ReviewsUploadPicture pictureAddtemp = adapter.getList()
                                .get(imageUploadIndex);
                        pictureAddtemp.isAddPicure = false;
                        pictureAddtemp.picureFilePath = filePath;
                        adapter.set(imageUploadIndex, pictureAddtemp);
                        imageUploadIndex = 0;
                        addImageDisplay();
                    }
                    break;
                case 2: {
                    // 获取签名内容
                    int width = DisplayUtil.dip2px(35, ctx.getResources()
                            .getDisplayMetrics().density);
                    View view = Editcontainer.getChildAt(1);
                    ViewHoler holer = (ViewHoler) view.getTag();
                    holer.setEdit(true);
                    holer.setNeedUp(filePath);
                    holer.imageRight.setImageBitmap(DisplayUtil.getBitmapFromFile(
                            new File(filePath), width, width));
                }
                break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PicrueCrop);
    }

    /**
     * 把Uri转化成文件路径
     */
    private String uri2filePath(Uri uri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(uri, proj, null, null, null);
            int index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(index);
            return path;
        } catch (Exception e) {
            return uri.getPath();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == onPictureSelectListener.REQUEST_CAPTURE_IMAGE) {
                    if (WhoNeedPic == 1) {
                        startPhotoZoom(Uri.fromFile(new File(picutreSelect
                                .getCameraFilePath())));
                    } else {
                        setImage(picutreSelect.getCameraFilePath());
                        picutreSelect.setCameraFilePath(null);
                    }
                } else if (requestCode == onPictureSelectListener.REQUEST_LOCAL_PRICTURE_CHOOSE) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        if (WhoNeedPic == 1) {
                            startPhotoZoom(uri);
                        } else {
                            setImage(uri2filePath(uri));
                        }
                    }
                } else if (requestCode == OpenSign_code) {
                    // 获取签名内容
                    View view = Editcontainer.getChildAt(9);
                    ViewHoler holer = (ViewHoler) view.getTag();
                    String content = data.getStringExtra("Content");
                    if (!TextUtils.isEmpty(content)) {
                        holer.setEdit(true);
                        holer.setNeedUp(content);
                        holer.content.setText(content);
                        holer.content.setTextColor(getResources().getColor(
                                R.color.color_black));
                    }
                } else if (requestCode == PicrueCrop) {
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Bitmap photo = extras.getParcelable("data");
                            SavePicInLocal(photo);
                        }
                    }
                }
            }

        } catch (Exception e) {

        }
    }

    private File getCropFile() {
        boolean SDCanNotUse = false;
        File BuildDir = null;

        // 检查外部SD卡是否可以用
        if (MemoryStatus.isExternalMemoryAvailable()) {
            BuildDir = new File(Constants.PICTURE);
        } else {
            BuildDir = new File(ctx.getFilesDir().toString());
            SDCanNotUse = true;
        }

        if (!BuildDir.exists()) {
            BuildDir.mkdirs();
        }

        if (SDCanNotUse) {
            try {
                MemoryStatus.chMod("771", BuildDir.toString());
            } catch (Exception e) {
            }

        }

        return new File(BuildDir, "crop_pic.jpg");
    }

    /**
     * 保存裁剪后的图片
     *
     * @param bitmap
     */
    private void SavePicInLocal(Bitmap bitmap) {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        ByteArrayOutputStream baos = null; // 字节数组输出流
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] byteArray = baos.toByteArray();// 字节数组输出流转换成字节数组
            // 将字节数组写入到刚创建的图片文件中
            fos = new FileOutputStream(getCropFile());
            bos = new BufferedOutputStream(fos);
            bos.write(byteArray);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 获取签名内容
        int width = DisplayUtil.dip2px(35, ctx.getResources()
                .getDisplayMetrics().density);
        View view = Editcontainer.getChildAt(0);
        ViewHoler holer = (ViewHoler) view.getTag();
        holer.setEdit(true);
        holer.setNeedUp(getCropFile().getAbsolutePath());
        holer.imageRight.setImageBitmap(DisplayUtil.getBitmapFromFile(
                getCropFile(), width, width));
    }

    /**
     * @author acer
     */
    private class ViewHoler {
        private TextView title;
        private TextView content;
        private LinearLayout RightSide;
        private RoundImageView imageRight;
        private int index = -1;
        private boolean misEdit = false;

        private String needUp = "";

        public ViewHoler(View view) {
            title = (TextView) view.findViewById(R.id.title);
            content = (TextView) view.findViewById(R.id.content);
            RightSide = (LinearLayout) view.findViewById(R.id.RightSide);
            imageRight = (RoundImageView) view.findViewById(R.id.imageRight);
            view.setTag(this);
            index = -1;
        }

        public String getNeedUp() {
            return needUp;
        }

        public void setNeedUp(String needUp) {
            this.needUp = needUp;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public boolean isEdit() {
            return misEdit;
        }

        public void setEdit(boolean isEdit) {
            misEdit = isEdit;
        }
    }

    /***
     * 评论图片
     *
     * @author milanoouser
     */
    public class ReviewsPictureAddAdapter extends
            BaseListAdapter<ReviewsUploadPicture> {

        private int width;

        public ReviewsPictureAddAdapter(Context context) {
            super(context);
            width = DisplayUtil.dip2px(65, ctx.getResources()
                    .getDisplayMetrics().density);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            RoundImageView upLoadImage = null;
            if (convertView == null) {
                convertView = (LayoutInflater.from(ctx).inflate(
                        R.layout.picture_add, null));
                upLoadImage = (RoundImageView) convertView
                        .findViewById(R.id.addImage);
                convertView.setTag(upLoadImage);
            } else {
                upLoadImage = (RoundImageView) convertView.getTag();
            }

            ReviewsUploadPicture picture = getList().get(position);

            upLoadImage.setRectAdius(4);
            if (picture.isAddPicure) {// 显示增加图片按钮
                upLoadImage
                        .setImageResource(R.drawable.reviews_picture_add_selector);
            } else {
                upLoadImage.setImageBitmap(DisplayUtil.getBitmapFromFile(
                        new File(picture.picureFilePath), width, width));
            }

            upLoadImage.setTag(position);
            upLoadImage.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        int position = (Integer) v.getTag();
                        ReviewsUploadPicture pictureAdd = getList().get(
                                position);
                        // 添加图片
                        if (pictureAdd.isAddPicure) {
                            imageUploadIndex = position;
                            WhoNeedPic = -1;
                            picutreSelect = new PicutreSelect(ctx);
                        } else {
                            remove(position);
                            // 如果图片已经添加，那么就删除他
                            if (getCount() < CAN_ADD_PICUTRE_NUM
                                    && !getList().get(getCount() - 1).isAddPicure) {
                                addImageDisplay();
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            });

            return convertView;
        }
    }

}
