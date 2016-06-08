package com.hyx.android.Game351.util;

import android.os.Environment;
import android.os.StatFs;

import java.io.IOException;

public class MemoryStatus {

    static final int ERROR = -1;

    /*
     * 外部储存器是否可用
     */
    public static boolean isExternalMemoryAvailable() {
        return Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED);
    }

    /*
     * 格式化编码
     */
    public static String formatSize(long paramLong) {
        String str = null;
        if (paramLong >= 1024L) {
            str = "KiB";
            paramLong /= 1024L;
            if (paramLong >= 1024L) {
                str = "MiB";
                paramLong /= 1024L;
            }
        }
        StringBuilder localStringBuilder = new StringBuilder(Long.toString(paramLong));
        for (int i = localStringBuilder.length() - 3; ; i -= 3) {
            if (i <= 0) {
                if (str != null)
                    localStringBuilder.append(str);
                return localStringBuilder.toString();
            }
            localStringBuilder.insert(i, ',');
        }
    }

    /*
     * 获取有用的外部储存器空间
     */
    public static long getAvailableExternalMemorySize() {
        long Size;
        if (isExternalMemoryAvailable()) {
            StatFs localStatFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
            Size = localStatFs.getBlockSize() * localStatFs.getAvailableBlocks() / 1024L / 1024L;
        } else {
            Size = -1L;
        }
        return Size;
    }

    /*
     * 获得有效可用的内存空间
     */
    public static long getAvailableInternalMemorySize() {
        StatFs localStatFs = new StatFs(Environment.getDataDirectory().getPath());
        return localStatFs.getBlockSize() * localStatFs.getAvailableBlocks() / 1024L / 1024L;
    }

    /*
     * 外部存储器总空间
     */
    public static long getTotalExternalMemorySize() {
        long Size;
        if (isExternalMemoryAvailable()) {
            StatFs localStatFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
            Size = localStatFs.getBlockSize() * localStatFs.getBlockCount() / 1024L / 1024L;
        } else {
            Size = -1L;
        }
        return Size;
    }

    /*
     * 内部存储器总空间
     */
    public static long getTotalInternalMemorySize() {
        StatFs localStatFs = new StatFs(Environment.getDataDirectory().getPath());
        return localStatFs.getBlockSize() * localStatFs.getBlockCount() / 1024L / 1024L;
    }

    /**
     * 修改文件属性
     *
     * @param paramString1
     * @param paramString2
     * @throws IOException
     */
    public static void chMod(String paramString1, String paramString2) throws IOException {
        Runtime.getRuntime().exec("chmod " + paramString1 + " " + paramString2);
    }
}
