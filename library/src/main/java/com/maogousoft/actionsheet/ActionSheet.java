package com.maogousoft.actionsheet;

import com.soli.lib.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * 仿ios ActionSheet
 * 
 * @author:wst
 * @time:2014-5-16
 */
public class ActionSheet {

	private Context ctx;
	private View rootView;
	private View view;// 用户传入view
	private LinearLayout viewContainer;// 用户传入view的容器

	private Dialog dialog;

	private Button btnCancel;
	private OnCancelClickListener onCancelClickListener;

	public interface OnCancelClickListener {
		void onClick();
	}

	public void setOnCancelClick(OnCancelClickListener onCancelClickListener) {
		this.onCancelClickListener = onCancelClickListener;
	}

	public ActionSheet(Context ctx) {
		this.ctx = ctx;
		init();
	}

	public ActionSheet(Context ctx, View view) {
		this.ctx = ctx;
		this.view = view;
		init();
	}

	public void addView(View view) {
		this.view = view;
		if (view != null) {
			viewContainer.addView(view);
			rootView.invalidate();
		}

	}

	public void init() {
		dialog = new Dialog(ctx, R.style.ActionSheet);

		rootView = LayoutInflater.from(ctx).inflate(R.layout.actionsheet, null);
		final int cFullFillWidth = 10000;

		rootView.setMinimumWidth(cFullFillWidth);

		// 用户传入view的容器
		viewContainer = (LinearLayout) rootView.findViewById(R.id.layoutContainer);
		btnCancel = (Button) rootView.findViewById(R.id.btnCancel);

		if (view != null) {
			viewContainer.addView(view);
		}

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onCancelClickListener != null) {
					onCancelClickListener.onClick();
				}
				dialog.dismiss();
			}
		});

		Window w = dialog.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dialog.onWindowAttributesChanged(lp);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {

				if (onCancelClickListener != null) {
					onCancelClickListener.onClick();
				}
				dialog.dismiss();
			}
		});

		dialog.setContentView(rootView);
		dialog.show();

	}

	public void dismiss() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}
	/**
	 * 根据需要显示与否取消按钮
	 * @param visibility
	 */
	public void setCanclebtnVisibility(int visibility)
	{
		btnCancel.setVisibility(visibility);
	}
	
	/**
	 * 
	 * @param cancel
	 */
	public void setOutSideTouch(boolean cancel)
	{
		dialog.setCanceledOnTouchOutside(cancel);
	}
}
