package com.hyx.android.Game351.base;

import android.os.Environment;

import com.hyx.android.Game351.BuildConfig;

import java.io.File;

/**
 * 常量
 *
 * @author sofia
 */
public class Constants {

    // 本地文件夹目录
    public static final String RootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "TieGan";
    // 使用产生的图片位置
    public static final String PICTURE = RootDir + File.separator + "Pictures";

    public static final String BuyMoney = "600";

    // 缓存的容量MB
    public static final int CacheSize = 100;// 100MB
    // 默认保存历史搜索记录的条数
    public static final int SaveHistoryCount = 20;
    // 浏览历史记录
    public static final int ReviewsMaxCount = 100;

    public static final String HistoryRecord = "HistoryRecord";
    public static final String FavoriteRecord = "FavoriteRecord";

    public static final String checkCode = "defabc";
    public static final String UserAddress = BuildConfig.Address;
    public static final String GuestAddress = BuildConfig.GuestAddress;//体验接口地址
    public static final String ResourceAddress = "http://www.tgbbx.com/resources/";
    public static final String ServerTopidAddress = "http://www.tgbbx.com/apinew/api6_3.php";
    public static final String ServerTopicAdd = "http://www.tgbbx.com/";
    public static String ServerDataAddress = "";

    static {
        ServerDataAddress = UserAddress;
    }

    /**
     *
     */
    public static void setServerAddress(String address) {
        ServerDataAddress = address;
    }
}
