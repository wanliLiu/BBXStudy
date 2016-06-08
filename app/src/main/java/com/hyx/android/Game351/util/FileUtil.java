package com.hyx.android.Game351.util;

import android.content.Context;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {

    /**
     * 获取目录
     *
     * @param context
     * @return
     */
    public static File getRootDir(Context context) {

        File targetDir = null;

        try {
            if (MemoryStatus.isExternalMemoryAvailable()) {
//		        targetDir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getApplicationInfo().packageName + "/Milanoo");
                targetDir = new File(Environment.getExternalStorageDirectory(), "TieGan");
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }
            }
        } catch (Exception e) {
        }

        if (targetDir == null || !targetDir.exists()) {
            targetDir = context.getFilesDir();
        }

        return targetDir;
    }

    /**
     * 获取目录
     *
     * @param context
     * @param name
     * @return
     */
    public static File getDir(Context context, String name) {
        File file = new File(getRootDir(context), name);
        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }

    /**
     * 获取文件
     *
     * @param context
     * @param dir
     * @param fileName
     * @return
     */
    public static File getFile(Context context, String dir, String fileName) {
        return new File(getDir(context, dir), fileName);
    }

    /**
     * Creates a constant cache file path given a target cache directory and an
     * image key.
     *
     * @param cacheDir
     * @param key
     * @return
     */
    public static File createFilePath(File dir, String url) {
        // 扩展名位置
        int index = url.lastIndexOf('.');
        if (index == -1) {
            return null;
        }

        StringBuilder filePath = new StringBuilder();

        // 图片存取路径
        filePath.append(dir.getAbsolutePath() + "/");
        filePath.append(MyTools.getMD5_32(url)).append(url.substring(index));

        return new File(filePath.toString());
    }

    /**
     * 把assets里的文件copy到指定文件目录下，同名拷贝
     *
     * @param context
     * @param dir
     * @param assetFileName assets文件的名称
     * @return
     */
    public static File CopyAssetsToXXX(Context context, String dir, String assetFileName) {
        File file = getFile(context, dir, assetFileName);
        try {
            OutputStream myOutput = new FileOutputStream(file);
            InputStream myInput = context.getAssets().open(assetFileName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }

            myOutput.flush();
            myInput.close();
            myOutput.close();
        } catch (Exception e) {
            file = null;
        }
        return file;
    }

    /**
     * 读取文本数据
     *
     * @param context  程序上下文
     * @param fileName 文件名
     * @return String, 读取到的文本内容，失败返回null
     */
    public static String readAssets(Context context, String fileName) {
        InputStream is = null;
        String content = null;
        try {
            is = context.getAssets().open(fileName);
            if (is != null) {

                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int readLength = is.read(buffer);
                    if (readLength == -1) break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                is.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            content = null;
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return content;
    }
}
