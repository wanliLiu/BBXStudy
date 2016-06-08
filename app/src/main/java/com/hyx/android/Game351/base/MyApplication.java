package com.hyx.android.Game351.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hyx.android.Game351.DBDevicesManger;
import com.hyx.android.Game351.login.CheckService;
import com.hyx.android.Game351.util.ACache;
import com.hyx.android.Game351.util.FileUtil;
import com.hyx.android.Game351.util.SP;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;

public class MyApplication extends Application {

    private static DBDevicesManger dataManger;
    private Intent checkService;
    private List<Activity> mList = new LinkedList<Activity>();

    private ACache cache;

    @Override
    public void onCreate() {
        super.onCreate();

        initImageLoader(getApplicationContext());
        checkService = new Intent(this, CheckService.class);

        //创建缓存目录
        cache = ACache.get(FileUtil.getDir(getApplicationContext(), "Cache"));

        if (IsFistTimeUse()) {
            MarketIsFirstTime();
            SetFontSize(5);
        }
    }

    public ACache getCache() {
        return cache;
    }

    public void setCache(ACache cache) {
        this.cache = cache;
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    //关闭每一个list内的activity  
    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            System.exit(0);   
//        	SP.getEdit(this).clear().commit();
        }
    }

    //杀进程  
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    /**
     * @param context
     */
    private void initImageLoader(Context context) {

        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        /*
         * //显示本地图片信息 String imageUri = "http://site.com/image.png"; // from Web
         * String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
         * String imageUri = "content://media/external/audio/albumart/13"; //
         * from content provider String imageUri = "assets://image.png"; // from
         * assets String imageUri = "drawable://" + R.drawable.image; // from
         * drawables (only images, non-9patch)
         */
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 3)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(Constants.CacheSize * 1024 * 1024) // 50
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    /*
     * 检查网络是否可用
     */
    public boolean isNetworkAvailable() {
        NetworkInfo ifo = ((ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (ifo != null) {
            if (ifo.isAvailable()) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    /**
     * @param key
     * @param yes
     */
    public void setSwitch(String key, boolean yes) {
        SP.getEdit(this).putBoolean(key, yes).commit();
    }

    public boolean getSwitch(String key) {
        return SP.getSp(this).getBoolean(key, true);
    }

    /**
     * 是否连续播放
     *
     * @param yesOrno
     */
    public void PlayContinue(Boolean yesOrno) {
        SP.getEdit(this).putBoolean(SP.isPlayCouninue, yesOrno).commit();
    }


    /**
     * 默认连续播放
     *
     * @return
     */
    public boolean isPlayContinue() {
        return SP.getSp(this).getBoolean(SP.isPlayCouninue, false);
    }

    /**
     * 是否自动播放
     *
     * @param yesOrno
     */
    public void AutoDisplay(Boolean yesOrno) {
        SP.getEdit(this).putBoolean(SP.isAutoDisplay, yesOrno).commit();
    }

    /**
     * 是否是VIP用户
     *
     * @return
     */
    public boolean isVIPuser() {
        return SP.getSp(this).getBoolean(SP.IsVIPuser, false);
    }

    /**
     * 保存当前用户的状态
     *
     * @param flag
     */
    public void setVIPuserStatus(int flag) {
        SP.getEdit(this).putBoolean(SP.IsVIPuser, flag == 1 ? true : false).commit();
    }

    /**
     * 是否自动播放
     *
     * @return
     */
    public boolean isAutoDisplay() {
        return SP.getSp(this).getBoolean(SP.isAutoDisplay, false);
    }

    /**
     * 获取连续播放的时间
     *
     * @return
     */
    public int getShowtime() {
        return SP.getSp(this).getInt(SP.SHOWTIME, 500);
    }

    /**
     * 自动播放的时间
     *
     * @param time
     */
    public void setShowtime(int time) {
        SP.getEdit(this).putInt(SP.SHOWTIME, time).commit();
    }

    public boolean IsFistTimeUse() {
        return SP.getSp(this).getBoolean(SP.IsFistTime, true);
    }

    public void MarketIsFirstTime() {
        SP.getEdit(this).putBoolean(SP.IsFistTime, false).commit();
    }

    /**
     * 连续播放的时间
     *
     * @param time
     */
    public void SetPlayTime(int time) {
        SP.getEdit(this).putInt(SP.isPlayTime, time).commit();
    }

    /**
     * 获取连续播放的时间
     *
     * @return
     */
    public int getPlayTime() {
        return SP.getSp(this).getInt(SP.isPlayTime, 0) + 200;
    }

    /**
     * 连续播放的时间
     *
     * @param time
     */
    public void SetFontSize(int size) {
        SP.getEdit(this).putInt(SP.SUBJECTFONT, size).commit();
    }

    public int getFontSize() {
        return SP.getSp(this).getInt(SP.SUBJECTFONT, 0) + 20;
    }

    public void setUserPhonew(String phone) {
        SP.getEdit(this).putString(SP.UserPhone, phone).commit();
    }

    public String getUserPhone() {
        return SP.getSp(this).getString(SP.UserPhone, "");
    }

    public String getUserID() {
        return SP.getSp(this).getString(SP.UserID, "");
    }

    public void setUserID(String ID) {
        SP.getEdit(this).putString(SP.UserID, ID).commit();
    }

    public String getUserName() {
        return SP.getSp(this).getString(SP.UserName, "");
    }

    public void setUserName(String name) {
        SP.getEdit(this).putString(SP.UserName, name).commit();
    }

    public String getUserPassword() {
        return SP.getSp(this).getString(SP.UserPassword, "");
    }

    public void setUserPassword(String name) {
        SP.getEdit(this).putString(SP.UserPassword, name).commit();
    }

    public void setUserAvatar(String Avatar) {
        SP.getEdit(this).putString(SP.UserAvatar, Avatar).commit();
    }

    public String getUserAvatard() {
        return SP.getSp(this).getString(SP.UserAvatar, "");
    }

    public String getUserLoginDate() {
        return SP.getSp(this).getString(SP.logindate, "");
    }

    public void setUserLoginDate(String logindate) {
        SP.getEdit(this).putString(SP.logindate, logindate).commit();
    }

    public void StartCheckService() {
        startService(checkService);
    }

    public void StopService() {
        stopService(checkService);
    }

    /**
     * 删除所有缓存文件
     */
    public void deleteAllCacheFile() {
        File[] file = getApplicationContext().getExternalCacheDir().listFiles();

        for (File file2 : file) {
            file2.delete();
        }
        ;
    }

    /**
     * 获取文件的大小
     *
     * @param f
     * @return
     * @throws Exception
     */
    public long getFileSizes(File f) throws Exception {
        long s = 0;
        if (f.exists()) {
            FileInputStream fis = new FileInputStream(f);
            s = fis.available();
        }
        return s;
    }

    /**
     * 获取缓存目录的总大小
     *
     * @return
     */
    public long getCacheFileSize() {
        long totailSize = 0;
        File[] file = getApplicationContext().getExternalCacheDir().listFiles();

        for (File file2 : file) {
            try {
                totailSize += getFileSizes(file2);
            } catch (Exception e) {
            }
        }

        return totailSize;
    }

    /**
     * 获取缓存文件的个数
     *
     * @return
     */
    public int getCacheFileCount() {

        return this.getExternalCacheDir().listFiles().length;
    }

    /**
     * 获取数据库操作
     *
     * @return
     */
    public DBDevicesManger getDBManger(String tableName) {
        if (dataManger == null) {
            dataManger = new DBDevicesManger(getApplicationContext(), tableName);
        }
        if (!dataManger.isDbOpen()) {

            try {
                dataManger.openDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return dataManger;
    }

    /**
     * 关闭操作的数据库
     */
    public void CloseDBManger() {
        if (dataManger.isDbOpen() && dataManger != null) {
            dataManger.closeDB();
            dataManger = null;
        }
    }

}
