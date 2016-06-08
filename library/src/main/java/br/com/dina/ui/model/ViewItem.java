package br.com.dina.ui.model;

import android.view.View;

public class ViewItem implements IListItem {
	
	private boolean mClickable = true;//该视图是否可以点击
	private View mView;//自定义的view，用来放到容器layout中的试图
	private int group = -1;
	private View fatherView;//item视图，包括mView
	private int background = -1;//自定义Item的背景色

	public void setFatherView(View fatherView) {
		this.fatherView = fatherView;
	}
	public View getFatherView() {
		return fatherView;
	}
	public ViewItem(View view) {
		this.mView = view;
	}
	
	public View getView() {
		return this.mView;
	}

	@Override
	public boolean isClickable() {
		return mClickable;
	}

	@Override
	public void setClickable(boolean clickable) {
		mClickable = clickable;		
	}
	
	public int getGroup() {
		return group;
	}
	
	public void setGroup(int group) {
		this.group = group;
	}
	
	public void setBackground(int background) {
        this.background = background;
    }
	
	public int getBackground() {
        return background;
    }
}
