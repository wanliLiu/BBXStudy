package com.hyx.android.Game351.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.hyx.android.Game351.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class MyTools {

    // 十六进制下数字到字符的映射数组
    private final static String[] hexDigits = {
            "0", "1", "2", "3", "4",
            "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"
    };

    /**
     * 接口解密
     *
     * @param buffer
     * @return
     */
    public static byte[] revProcessData(byte[] buffer) throws Exception {

        Inflater decompressor = new Inflater();
        decompressor.setInput(buffer);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(buffer.length);
        byte[] buf = new byte[1024];

        while (!decompressor.finished()) {
            try {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            } catch (DataFormatException e) {
                e.printStackTrace();
                break;
            }
        }

        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        buffer = bos.toByteArray();

        return buffer;
    }

    /**
     * 将一个字节转化成十六进制形式的字符串
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * 转换字节数组为十六进制字符串
     *
     * @param
     * @return 十六进制字符串
     */
    private static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    /**
     * 将时间戳转为代表"距现在多久之前"的字符串
     *
     * @param timeStr 时间戳
     * @return
     */
    public static String getStandardDate(String timeStr) {

        long left = System.currentTimeMillis() / 1000 - Long.parseLong(timeStr);
        double day = left * 1.0 / (3600 * 24) * 1.0;

        String temp = "";
        if (day >= 1) {
            temp = (int) day + "天前";
        } else {
            int hours = (int) (left / 3600);
            if (hours >= 1) {
                temp = hours + "小时前";
            } else {
                int minutes = (int) ((left - hours * 3600) / 60);
                if (minutes > 5) {
                    temp = minutes + "分钟前";
                } else {
                    temp = "刚刚";
                }
            }
        }

        return temp;

    }

    /**
     * MD5加密，32位
     *
     * @param str
     * @return
     */
    public static String getMD5_32(String str) {

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }

        return md5StrBuff.toString();
    }

    /**
     * 获取android设备的唯一编码(有无手机卡都可以，有无wifi都可以，wifi是否打开都可以，是否是手机都可以)
     *
     * @param context
     * @return 唯一序列号
     */
    public static String getUUID(Context context) {

        String deviceId = SP.getSp(context).getString(SP.DEVICE_ID, "");
        if (TextUtils.isEmpty(deviceId)) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm.getDeviceId();
            // If running on an emulator
            if (deviceId == null || deviceId.trim().length() == 0 || deviceId.matches("0+")) {
                deviceId = (new StringBuilder("EMU")).append((new Random(System.currentTimeMillis())).nextLong()).toString();
            }
            SP.getEdit(context).putString(SP.DEVICE_ID, deviceId).commit();
        }

        return deviceId;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 货币处理
     *
     * @param str
     * @param sepStr
     * @return
     */
    public static String currencyAdd(String str, String sepStr) {
        int valuelength = str.length();
        StringBuffer temp = new StringBuffer("");
        if (valuelength > 3) {// 上千
            String[] splitValue;
            if ((valuelength % 3) == 0) {
                splitValue = new String[valuelength / 3];
            } else {
                splitValue = new String[(valuelength / 3) + 1];
            }
            int j = 0;
            for (int i = 0; i < splitValue.length; i++) {
                if ((valuelength % 3) == 0) {
                    splitValue[i] = str.substring(j, j + 3);
                    j += 3;
                } else {
                    if (i == 0) {
                        j = valuelength % 3;
                        splitValue[i] = str.substring(0, j);
                    } else {
                        splitValue[i] = str.substring(j, j + 3);
                        j += 3;
                    }
                }
            }
            for (int i = 0; i < splitValue.length; i++) {
                temp.append(splitValue[i] + sepStr);
            }

            return temp.deleteCharAt(temp.length() - 1).toString();
        }

        return str;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter  
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高  
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度  
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度  
        // params.height最后得到整个ListView完整显示需要的高度  
        listView.setLayoutParams(params);
    }

    /**
     * 获取目前apk类型
     *
     * @param ctx
     * @return
     */
    public static ApkType getCurrentApkType(Context ctx) {
        try {
            ApplicationInfo appInfo = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
            String temp = appInfo.metaData.getString("CURRENT_APK_TYPE");
            if (temp.equals("Type21") && temp.equals(BuildConfig.FLAVOR) && BuildConfig.APK_TYPE == 0) {
                return ApkType.TYPE_21;
            } else if (temp.equals("CopyRead") && temp.equals(BuildConfig.FLAVOR) && BuildConfig.APK_TYPE == 1) {
                return ApkType.TYPE_CopyRead;
            } else if (temp.equals("FastRecord") && temp.equals(BuildConfig.FLAVOR) && BuildConfig.APK_TYPE == 2) {
                return ApkType.TYPE_FastRecord;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ApkType.TYPE_21;
    }
}
