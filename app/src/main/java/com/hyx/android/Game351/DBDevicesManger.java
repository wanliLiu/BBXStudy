package com.hyx.android.Game351;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSON;
import com.hyx.android.Game351.base.Constants;
import com.hyx.android.Game351.modle.HistoryBean;

import java.util.ArrayList;
import java.util.List;

/*
 * 设备数据管理类，用来管理设备在本地数据库里的数据信息；
 */
public class DBDevicesManger {

    private final Context context;
    private DBDeviceHelper helper;
    private SQLiteDatabase db;
    private String tableName = "";

    private String name = "";

    public DBDevicesManger(Context mcontext, String dbName) {
        context = mcontext;
        name = dbName;

    }

    /**
     * 打开数据库
     *
     * @throws SQLException
     */
    public void openDB() throws SQLException {

        helper = new DBDeviceHelper(context);
        // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
        // mFactory);
        // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();

        tableName = DBDeviceHelper.Table_DeviceName + name;
    }

    /**
     * 判断数据库是否打开
     *
     * @return
     */
    public boolean isDbOpen() {
        if (db != null && helper != null) {
            return db.isOpen() ? true : false;
        }

        return false;
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {

        helper.close();
        db.close();
    }

    /**
     * 增加单个设备到数据库,添加设备前需要查询数据库中是否有该设备
     *
     * @param device
     */
    private void AddAHistoryRecord(HistoryBean device) {
        if (db.isOpen()) {
            db.beginTransaction();
            try {

                ContentValues values = new ContentValues();
                values.put(DBDeviceHelper.sort_id, device.getSort_id());
                values.put(DBDeviceHelper.HistoryIfo, JSON.toJSONString(device));

                db.insert(tableName, null, values);

                db.setTransactionSuccessful();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }

    /**
     * 在添加之前需要判断该商品ID是否存在数据库中
     *
     * @param productID
     * @param device
     */
    public void AddHistory(HistoryBean ifo) {
        if (IsHaveTheRecord(ifo.getSort_id())) {
            //如果存在就删除，删除了之后在添加
            deleteAHistoryRecord(ifo.getSort_id());
        }
        //如果数量超过限制，那么就删除最后一条
        List<HistoryBean> list = GetAllRecords();
        if (list.size() >= Constants.ReviewsMaxCount) {
            deleteAHistoryRecord(list.get(list.size() - 1).getSort_id());
        }

        //最后增加数据到数据库
        AddAHistoryRecord(ifo);
    }

    /**
     * 删除所有的历史数据
     */
    public void DeleteAllHistory() {
        db.delete(tableName, null, null);
    }

    /**
     * 根据产品ID删除一条信息
     *
     * @param productID
     * @return
     */
    public boolean deleteAHistoryRecord(String sort_id) {
        return db.delete(tableName, DBDeviceHelper.sort_id + "=?", new String[]{sort_id}) > 0;
    }

    /**
     * 获取数据库中所有设备列表
     *
     * @return
     */
    public List<HistoryBean> GetAllRecords() {
        List<HistoryBean> devices = new ArrayList<HistoryBean>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName, null);
            if (tableName.contains(Constants.HistoryRecord)) {
                //倒序拿数据
                int count = cursor.getCount();
                if (cursor.moveToLast()) {
                    do {
                        HistoryBean ifo = JSON.parseObject(cursor.getString(cursor.getColumnIndex(DBDeviceHelper.HistoryIfo)), HistoryBean.class);
                        devices.add(ifo);
                        cursor.moveToPrevious();
                    } while ((--count) > 0);
                }
            } else {
                //顺序那数据
                while (cursor.moveToNext()) {
                    HistoryBean ifo = JSON.parseObject(cursor.getString(cursor.getColumnIndex(DBDeviceHelper.HistoryIfo)), HistoryBean.class);
                    devices.add(ifo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return devices;
    }

    /**
     * 判断这个产品的ID是否存在数据库中
     *
     * @param productID
     * @return
     */
    public boolean IsHaveTheRecord(String sort_id) {
        boolean isExist = false;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + DBDeviceHelper.sort_id + "=?", new String[]{sort_id});
            if (cursor.getCount() > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return isExist;
    }
}
