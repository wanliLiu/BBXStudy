package com.hyx.android.Game351.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.hyx.android.Game351.modle.PositionBean;


public class AutoWrapListView extends ViewGroup {

    private static final String TAG = AutoWrapListView.class.getSimpleName();
    private AutoWrapAdapter<PositionBean> myCustomAdapter;

    private static boolean addChildType;
    private int dividerWidth = 20;
    private int dividerHeight = 20;
    private int viewWidth = 0;

    public AutoWrapListView(Context context) {
        super(context);
    }

    public AutoWrapListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoWrapListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean arg0, int Left, int Top, int Right, int Bottom) {
        final int count = getChildCount();
        int lengthX = getPaddingLeft();
        int lengthY = getPaddingTop();
        //布局所有子View
        for (int i = 0; i < count; i++) {

            final View child = getChildAt(i);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();

            if (i == 0) {
                lengthX += width;
            } else {
                lengthX += width + getDividerWidth();
            }

            if ((i == 0 && lengthX <= viewWidth)) {
                lengthY += height;
            }

            if (lengthX > viewWidth) {
                lengthX = width + getPaddingLeft();
                lengthY += height + getDividerHeight();
            }
            child.layout(lengthX - width, lengthY - height, lengthX, lengthY);
        }

        LayoutParams lp = AutoWrapListView.this.getLayoutParams();
        lp.height = lengthY + getPaddingBottom();
        AutoWrapListView.this.setLayoutParams(lp);
        if (isAddChildType()) {
            new Thread(new RefreshCustomThread()).start();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量父容器
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        viewWidth = width - getPaddingLeft() - getPaddingRight();

        setMeasuredDimension(width, height);
        //测量子View
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    private final static boolean isAddChildType() {
        return addChildType;
    }

    public static void setAddChildType(boolean addChildType) {
        AutoWrapListView.addChildType = addChildType;
    }

    private final int getDividerHeight() {
        return dividerHeight;
    }

    public void setDividerHeight(int dividerHeight) {
        this.dividerHeight = dividerHeight;
    }

    private final int getDividerWidth() {
        return dividerWidth;
    }

    public void setDividerWidth(int dividerWidth) {
        this.dividerWidth = dividerWidth;
    }

    public void setAdapter(AutoWrapAdapter<PositionBean> adapter) {
        this.myCustomAdapter = adapter;
        setAddChildType(true);
        adapter.notifyCustomListView(this);
    }

    /**
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        myCustomAdapter.setOnItemClickListener(listener);
    }

    /**
     * Corresponding Item long click event
     *
     * @param listener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        myCustomAdapter.setOnItemLongClickListener(listener);
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (msg.getData().containsKey("getRefreshThreadHandler")) {
                    setAddChildType(false);
                    myCustomAdapter.notifyCustomListView(AutoWrapListView.this);
                }
            } catch (Exception e) {
                Log.w(TAG, e);
            }
        }

    };

    private final class RefreshCustomThread implements Runnable {

        @Override
        public void run() {
            Bundle b = new Bundle();
            try {
                Thread.sleep(50);
            } catch (Exception e) {

            } finally {
                b.putBoolean("getRefreshThreadHandler", true);
                sendMsgHanlder(handler, b);
            }
        }
    }

    private final void sendMsgHanlder(Handler handler, Bundle data) {
        Message msg = handler.obtainMessage();
        msg.setData(data);
        handler.sendMessage(msg);
    }
}
