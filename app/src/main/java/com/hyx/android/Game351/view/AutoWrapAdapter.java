package com.hyx.android.Game351.view;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.hyx.android.Game351.base.BaseListAdapter;


public class AutoWrapAdapter<T> extends BaseListAdapter<T> {

    private static final String TAG = AutoWrapAdapter.class.getSimpleName();
    private View myView;
    private ViewGroup myViewGroup;
    private AutoWrapListView myCustomListView;
    private OnItemClickListener singlelistener;
    private OnItemLongClickListener longListener;

    public AutoWrapAdapter(Context context) {
        super(context);
    }

    /**
     * Add all the View controls to the custom SexangleViewList
     * When you use this SexangleViewList should be instantiated first and then call
     * Because here is not intercept and throw such as null pointer exception
     * The name is called mySexangleView View passed in must be empty
     * Of course the ViewGroup transfer time must also be empty
     */
    private final void getAllViewAdd() {
        this.myCustomListView.removeAllViews();
        for (int i = 0; i < getCount(); i++) {
            View viewItem = getView(i, this.myView, this.myViewGroup);
            this.myCustomListView.addView(viewItem, i);
        }
    }

    /**
     * The refresh AutoWrapView interface
     * Here is set to True representative will execute reset CustomListView twice
     * This method is called before, please first instantiation mySexangleListView
     * Otherwise, this method in redraw CustomListView abnormal happens
     */
    public void notifyDataSetChanged() {
        AutoWrapListView.setAddChildType(true);
        notifyCustomListView(this.myCustomListView);
    }

    /**
     * Redraw the Custom controls for the first time, you should invoke this method
     * In order to ensure that each load data do not repeat to get rid of the
     * custom of the ListView all View objects
     * The following will be set up to monitor events as controls
     * First load regardless whether OnItemClickListener and OnItemLongClickListener is NULL,
     * they do not influence events Settings
     *
     * @param formateList
     */
    public void notifyCustomListView(AutoWrapListView formateList) {
        this.myCustomListView = formateList;
        myCustomListView.removeAllViews();
        getAllViewAdd();
        setOnItemClickListener(singlelistener);
        setOnItemLongClickListener(longListener);
    }

    /**
     * Set the click event of each View, external can realize the interface for your call
     *
     * @param listener
     */
    public void setOnItemClickListener(final OnItemClickListener listener) {
        this.singlelistener = listener;
        if (singlelistener == null) {
            return;
        }
        for (int i = 0; i < myCustomListView.getChildCount(); i++) {
            final int parame = i;
            View view = myCustomListView.getChildAt(i);
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (singlelistener != null) {
                        singlelistener.onItemClick(null, v, parame, getCount());
                    }
                }
            });
        }
    }

    /**
     * Set each long press event, the View outside can realize the interface for your call
     *
     * @param listener
     */
    public void setOnItemLongClickListener(final OnItemLongClickListener listener) {
        this.longListener = listener;
        if (longListener == null) {
            return;
        }
        for (int i = 0; i < myCustomListView.getChildCount(); i++) {
            final int parame = i;
            View view = myCustomListView.getChildAt(i);
            view.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if (longListener != null) {
                        longListener.onItemLongClick(null, v, parame, getCount());
                    }
                    return true;
                }
            });
        }
    }
}
