package br.com.dina.ui.model;

import android.view.View;

public class BasicItem implements IListItem {

	private boolean mClickable = true;
	private int mDrawable = -1;// 左边图标
	private int mDrawableRight = -1;// 右边图标
	private int mDrawableChevron = -1;// 右边箭头图标
	private int mDrawableRightWaring = -1;//右边红色提示图标
	private String mTitle;// 左边标题
	private String mSubtitle;// 左边子标题
	private String mCount;// 右边箭头旁边文字
	private String match;//匹配相同的字符串显示不同的颜色
	private int matchColor = -1;//相同字符串显示的颜色
	private int mColor = -1;// 左边标题文字颜色
	
	private boolean differentColor = false;//标题是否显示不同颜色

	private View mView;// 当前view
	
	public BasicItem(String _title) {
		this.mTitle = _title;
	}

	/**
	 * title显示不同的颜色
	 * @param title
	 * 显示的内容
	 * @param matchString
	 * 需要匹配的内容
	 * @param DeaultColor
	 * title默认显示的颜色
	 * @param matchColor
	 * 匹配的显示元素
	 */
	public BasicItem(String title,String matchString,int DeaultColor,int matchColor,int _drawableChevron) {
		this.mTitle = title;
		this.match = matchString;
		this.mColor = DeaultColor;
		this.matchColor = matchColor;
		differentColor = true;
		this.mDrawableChevron = _drawableChevron;
	}
	// public BasicItem(String _title, String _subtitle) {
	// this.mTitle = _title;
	// this.mSubtitle = _subtitle;
	// }
	//
	// public BasicItem(String _title, String _subtitle, int _color) {
	// this.mTitle = _title;
	// this.mSubtitle = _subtitle;
	// this.mColor = _color;
	// }
	//
	// public BasicItem(String _title, String _subtitle, boolean _clickable) {
	// this.mTitle = _title;
	// this.mSubtitle = _subtitle;
	// this.mClickable = _clickable;
	// }
	//
	// public BasicItem(int _drawable, String _title, String _subtitle) {
	// this.mDrawable = _drawable;
	// this.mTitle = _title;
	// this.mSubtitle = _subtitle;
	// }
	//
	// public BasicItem(int _drawable, String _title, String _subtitle, int
	// _color) {
	// this.mDrawable = _drawable;
	// this.mTitle = _title;
	// this.mSubtitle = _subtitle;
	// this.mColor = _color;
	// }
	//
	// public BasicItem(int _drawable, String _title, String _subtitle,
	// int _color, String count) {
	// this.mDrawable = _drawable;
	// this.mTitle = _title;
	// this.mSubtitle = _subtitle;
	// this.mColor = _color;
	// this.mCount = count;
	// }

	public View getView() {
		return mView;
	}

	public void setView(View mView) {
		this.mView = mView;
	}

	/**
	 * 
	 * @param _title
	 * @param _count
	 */
	public BasicItem(String _title, String _count) {
		this.mTitle = _title;
		this.mCount = _count;
	}
	
	/**
     * 初始化是否可以点击
     * @param _title
     * @param _count
     * @param clicable
     */
    public BasicItem(String _title, String _count,int _drawableChevron, boolean clicable) {
        this.mTitle = _title;
        this.mCount = _count;
        this.mClickable = clicable;
        this.mDrawableChevron = _drawableChevron;
    }

	/**
	 * 
	 * @param _title
	 * @param _count
	 * @param _drawableChevron
	 */
	public BasicItem(String _title, String _count, int _drawableChevron) {
		this.mTitle = _title;
		this.mCount = _count;
		this.mDrawableChevron = _drawableChevron;
	}

	/**
	 * 
	 * @param _title
	 * @param _drawableChevron
	 */
	public BasicItem(String _title, int _drawableChevron) {
		this.mTitle = _title;
		this.mDrawableChevron = _drawableChevron;
	}
   /**
     * 添加红色标记
     * @param _title
     * @param _drawableChevron
     */
    public BasicItem(int _drawableWaring,String _title) {
        this.mTitle = _title;
        this.mDrawableRightWaring = _drawableWaring;
    }
//    /**
//     * 
//     * @param _title
//     * @param _drawableWaring
//     * @param _drawableChevron
//     */
//    public BasicItem(String _title,int _drawableWaring, int _drawableChevron) {
//        this.mTitle = _title;
//        this.mDrawableRightWaring = _drawableWaring;
//        this.mDrawableChevron = _drawableChevron;
//    }
	/**
	 * 默认字体原色
	 * @param _title
	 * @param _drawableChevron
	 */
	public BasicItem(String _title, int color,int _drawableChevron) {
		this.mTitle = _title;
		this.mDrawableChevron = _drawableChevron;
		this.mColor = color;
	}
	
	/**
	 * 默认字体原色
	 * @param _title
	 * @param _drawableChevron
	 */
	public BasicItem(int colorleft,String _title, int color,int _drawableChevron) {
		this.mDrawable = colorleft;
		this.mTitle = _title;
		this.mDrawableChevron = _drawableChevron;
		this.mColor = color;
	}
	/**
	 * 默认字体原色
	 * @param _title
	 * @param _drawableChevron
	 */
	public BasicItem(String _title, int color,int _drawableChevron,boolean clicable) {
		this.mTitle = _title;
		this.mDrawableChevron = _drawableChevron;
		this.mColor = color;
		this.mClickable = clicable;
	}	
	/**
	 * 默认字体原色
	 * @param _title
	 * @param _drawableChevron
	 */
	public BasicItem(int colorleft,String _title, int color,int _drawableChevron,boolean clicable) {
		this.mDrawable = colorleft;
		this.mTitle = _title;
		this.mDrawableChevron = _drawableChevron;
		this.mColor = color;
		this.mClickable = clicable;
	}
//	/**
//	 * 
//	 * @param mDrawable
//	 * @param _title
//	 * @param _drawableChevron
//	 */
//	public BasicItem(int mDrawable, String _title, int _drawableChevron) {
//		this.mDrawable = mDrawable;
//		this.mTitle = _title;
//		this.mDrawableChevron = _drawableChevron;
//	}

	public int getmDrawableRightWaring() {
        return mDrawableRightWaring;
    }
	public void setmDrawableRightWaring(int mDrawableRightWaring) {
        this.mDrawableRightWaring = mDrawableRightWaring;
    }
	public int getDrawable() {
		return mDrawable;
	}

	public void setDrawable(int drawable) {
		this.mDrawable = drawable;
	}

	public int getmDrawableRight() {
        return mDrawableRight;
    }
	public void setmDrawableRight(int mDrawableRight) {
        this.mDrawableRight = mDrawableRight;
    }
	public int getDrawableChevron() {
		return mDrawableChevron;
	}

	public void setDrawableChevron(int drawableChevron) {
		this.mDrawableChevron = drawableChevron;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public String getSubtitle() {
		return mSubtitle;
	}

	public void setSubtitle(String summary) {
		this.mSubtitle = summary;
	}

	public String getCount() {
		return mCount;
	}

	public void setCount(String count) {
		this.mCount = count;
	}

	public int getColor() {
		return mColor;
	}

	public void setColor(int mColor) {
		this.mColor = mColor;
	}

	@Override
	public boolean isClickable() {
		return mClickable;
	}

	@Override
	public void setClickable(boolean clickable) {
		mClickable = clickable;
	}

	public void setDifferentColor(boolean differentColor) {
		this.differentColor = differentColor;
	}
	
	public boolean getDifferentColor() {
		return this.differentColor;
	}

	public void setMatch(String match) {
		this.match = match;
	}
	public String getMatch() {
		return match;
	}
	
	public void setMatchColor(int matchColor) {
		this.matchColor = matchColor;
	}
	public int getMatchColor() {
		return matchColor;
	}
}
