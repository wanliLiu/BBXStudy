package com.hyx.android.Game351.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.hyx.android.Game351.R;


/**
 * 头部view
 *
 * @author wst
 */
public class HeadView extends RelativeLayout {

    // 返回 标题 功能按钮 底部一条边线
    // 1.左边默认返回按钮，默认点击返回
    // 2.提供方法，获取返回按钮
    // 3.提供方法，获取右边按钮
    // 4.提供方法，获取中间按钮
    // 5.提供方法设置中间名称
    // 6.提供方法设置中间和右边名称
    // 7.提供方法，设置右边按钮点击事件
    // 8.提供方法，获取底部边线

    private ImageButton ibtnBack;
    private Button btnTitle, btnAction, btnAction1;
    private View layouAction;
    private View frem;

    private View line;

    private OnBackBtnListener onBackBtnListener;
    private OnActionBtnListener onActionBtnListener;

    private boolean isHaveShoppingBag = false;// 是否有购物车

    public HeadView(final Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = mInflater.inflate(R.layout.view_headview, this, true);
        ibtnBack = (ImageButton) view.findViewById(R.id.ibtn_back);
        ibtnBack.setVisibility(View.INVISIBLE);
        btnTitle = (Button) view.findViewById(R.id.btn_title);

        layouAction = view.findViewById(R.id.layouAction);
        frem = view.findViewById(R.id.fram);
        frem.setVisibility(View.INVISIBLE);

        btnAction = (Button) view.findViewById(R.id.btn_action);
        btnAction1 = (Button) view.findViewById(R.id.btn_action1);
        line = view.findViewById(R.id.line);

        ibtnBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (onBackBtnListener != null) {
                    onBackBtnListener.onClick();
                }
            }
        });

        layouAction.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onActionBtnListener != null) {
                    onActionBtnListener.onClick();
                }

            }
        });
//        btnAction.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (onActionBtnListener != null) {
//                    onActionBtnListener.onClick();
//                }
//
//            }
//        });
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        if (title != null) {
            btnTitle.setText(title);
        }
    }

    /**
     * 是否有购物车
     *
     * @return
     */
    public boolean isHaveShoppingBag() {
        return isHaveShoppingBag;
    }

    /**
     * 设置界面内容
     */
    public void setViewContent(String title, String actionText) {

        if (title != null) {
            btnTitle.setText(title);
        }

        if (actionText != null) {
//            btnAction.setBackgroundResource(R.drawable.textbtn_bg_selector);
//            btnAction.setPadding(10, 10, 10, 10);
//            layouAction.setPadding(0, 0, 0, 0);
//            layouAction.setVisibility(View.VISIBLE);
            btnAction.setText(actionText);
        }
    }

    public ImageButton getBtnBack() {
        return ibtnBack;
    }

    public Button getBtnTitle() {
        return btnTitle;
    }

    public Button getBtnAction() {
        return btnAction;
    }

    public RelativeLayout getBtnActionLayout() {
        return (RelativeLayout) layouAction;

    }

    public FrameLayout getFrem() {
        return (FrameLayout) frem;
    }

    public View getViewLine() {
        return line;
    }

    public void setOnBackBtnListener(OnBackBtnListener onBackBtnListener) {
        ibtnBack.setVisibility(View.VISIBLE);
        this.onBackBtnListener = onBackBtnListener;
    }

    public void setOnActionBtnListener(OnActionBtnListener onActionBtnListener) {
        frem.setVisibility(View.VISIBLE);
        this.onActionBtnListener = onActionBtnListener;
    }

    public interface OnBackBtnListener {

        void onClick();

    }

    public interface OnActionBtnListener {

        void onClick();
    }
}
