package com.hyx.android.Game351.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hyx.android.Game351.R;
import com.hyx.android.Game351.base.BaseActivity;
import com.hyx.android.Game351.base.Constants;
import com.maogousoft.actionsheet.ActionSheet;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;

import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.OnTableViewItemClickListener;

/**
 * 用来选择图片的选择器
 *
 * @author sofia
 */
public class PicutreSelect {

    private static boolean SDCanNotUse = false;
    private Context ctx;
    private String cameraFilePath;

    private onPictureSelectListener listener;

    public PicutreSelect(Context context) {
        ctx = context;

        final ActionSheet actionSheet = new ActionSheet(ctx);

        final UITableView table = new UITableView(ctx);
        // table.setContailAll(true);
        table.setLineIsCross(true);
        table.setLastLineDisplay(true);
        View viewCamera = LayoutInflater.from(ctx).inflate(R.layout.reviews_load_img_way, null);
        ((TextView) viewCamera.findViewById(R.id.selectWay)).setText("拍照");
        table.addViewItem(new ViewItem(viewCamera));

        View viewLocalBlum = LayoutInflater.from(ctx).inflate(R.layout.reviews_load_img_way, null);
        ((TextView) viewLocalBlum.findViewById(R.id.selectWay)).setText("从相册选择");
        table.addViewItem(new ViewItem(viewLocalBlum));

        table.commit();

        table.setOnTableItemClickListener(new OnTableViewItemClickListener() {

            @Override
            public void onItemClick(int Group, int index) {
                actionSheet.dismiss();
                if (index == 0) {
                    OpenLocalCameraTakePicture();
                } else {
                    if (listener != null) {
                        listener.onPictureSelect(onPictureSelectListener.REQUEST_LOCAL_PRICTURE_CHOOSE);
                    } else {
                        OpenLocalPictureSelect();
                    }
                }
            }
        });

        actionSheet.addView(table);
        actionSheet.setCanclebtnVisibility(View.GONE);
    }

    /**
     * 打开手机本地图片选择
     */
    public void OpenLocalPictureSelect() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        // intent.addCategory(Intent.CATEGORY_OPENABLE);
        ((BaseActivity) ctx).startActivityForResult(intent, onPictureSelectListener.REQUEST_LOCAL_PRICTURE_CHOOSE);
    }

    /**
     * 打开照相机照相获取图片
     */
    public void OpenCameraTakePicture() {
        // 打开Camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(cameraFilePath)));
        ((BaseActivity) ctx).startActivityForResult(intent, onPictureSelectListener.REQUEST_CAPTURE_IMAGE);
    }

    /**
     * 打开本地照相机照相
     */
    private void OpenLocalCameraTakePicture() {
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

        File outFile = new File(BuildDir, getPictureName());
        cameraFilePath = outFile.getAbsolutePath();

        if (listener != null) {
            listener.onPictureSelect(onPictureSelectListener.REQUEST_CAPTURE_IMAGE);
        } else {
            OpenCameraTakePicture();
        }

    }

    /**
     * 根据时间来设置照片的名字
     *
     * @return
     */
    private String getPictureName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_hhmmss");
        Date date = new Date(System.currentTimeMillis());

        return "IMG_" + format.format(date) + ".jpg";
    }

    /**
     * 获取照相机照相图片路径
     *
     * @return
     */
    public String getCameraFilePath() {
        return this.cameraFilePath;
    }

    /**
     * 设置照相机的默认地址
     *
     * @param path
     */
    public void setCameraFilePath(String path) {
        cameraFilePath = path;
    }

    /**
     * 是否有Sd卡
     *
     * @return
     */
    public boolean getSDCanNotUse() {
        return this.SDCanNotUse;
    }

    public void setListener(onPictureSelectListener listener) {
        this.listener = listener;
    }

    public interface onPictureSelectListener {
        public static final int REQUEST_LOCAL_PRICTURE_CHOOSE = 2507;
        public static final int REQUEST_CAPTURE_IMAGE = 2506;

        public void onPictureSelect(int pickWhere);
    }
}
