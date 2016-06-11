package com.hyx.android.Game351.base;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wst
 * @version V1.0
 * @Title: BaseListAdapter.java
 * @Package com.milanoo.store.base
 * @Description: Adapter基类
 * @date 2013-12-29 下午5:26:46
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {

    protected List<T> mList;

    protected Context ctx;

    protected ListView mListView;

    protected LayoutInflater mInflater;

    protected MyApplication app;

    public BaseListAdapter(Context context) {
        this.ctx = context;
        mInflater = LayoutInflater.from(ctx);
        app = (MyApplication) ((Activity) context).getApplication();
    }

    public BaseListAdapter(Context context, List<T> list) {
        this.ctx = context;
        this.mList = list;
        mInflater = LayoutInflater.from(ctx);
        app = (MyApplication) ((Activity) context).getApplication();
    }

    /**
     * 显示Toast
     *
     * @param text 文本内容
     */
    protected void MyToast(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示Toast
     *
     * @param resId string资源id
     */
    protected void MyToast(int resId) {
        Toast.makeText(ctx, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mList == null ? 0 : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public List<T> getList() {
        return this.mList;
    }

    public void setList(List<T> list) {
        mList = new ArrayList<T>();
        this.mList = list;
        notifyDataSetChanged();
    }

    public void add(T t) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.add(t);
        notifyDataSetChanged();
    }

    public void set(int location, T t) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.set(location, t);
        notifyDataSetChanged();
    }

    public void add(int location, T t) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.add(location, t);
        notifyDataSetChanged();
    }

    public void addAll(List<T> list) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addAllToFirst(List<T> list) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.addAll(0, list);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        if (mList != null) {
            mList.remove(position);
            notifyDataSetChanged();
        }
    }

    public void removeAll() {
        if (mList != null) {
            mList.removeAll(mList);
            notifyDataSetChanged();
        }
    }

    public ListView getListView() {
        return this.mListView;
    }

    public void setListView(ListView listView) {
        this.mListView = listView;
    }
}
