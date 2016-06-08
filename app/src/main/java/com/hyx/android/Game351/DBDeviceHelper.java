package com.hyx.android.Game351;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hyx.android.Game351.base.Constants;

public class DBDeviceHelper extends SQLiteOpenHelper {

    /*
     * 数据表字段成员以及数据类型
     */
    public static final String sort_id = "sort_id";
    public static final String HistoryIfo = "historyIfo";
    /*
     * 数据库名
     */
    private static final String DATABASENAME = "BBXStudyNew.db";
    private static final String sort_id_type = " TEXT,";
    private static final String HistoryIfo_type = " TEXT";
    /*
     * 设备数据库表名
     */
    public static String Table_DeviceName = "Table_";
    /*
     * 创建设备管理数据库表
     */
    String Table_HistoryRecord = "CREATE TABLE IF NOT EXISTS " + Table_DeviceName + Constants.HistoryRecord +//创建表名
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT," + // 主键，用来标识每行
            sort_id + sort_id_type + //产品ID,用来标识产品
            HistoryIfo + HistoryIfo_type +
            ")";

    /*
     * 创建设备管理数据库表
     */
    String Table_FavoriteRecord = "CREATE TABLE IF NOT EXISTS " + Table_DeviceName + Constants.FavoriteRecord +//创建表名
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT," + // 主键，用来标识每行
            sort_id + sort_id_type + //产品ID,用来标识产品
            HistoryIfo + HistoryIfo_type +
            ")";

    public DBDeviceHelper(Context context) {
        super(context, DATABASENAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(Table_HistoryRecord);
        db.execSQL(Table_FavoriteRecord);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Table_DeviceName + Constants.HistoryRecord);
        db.execSQL("DROP TABLE IF EXISTS " + Table_DeviceName + Constants.FavoriteRecord);
        onCreate(db);
    }

}
