
package br.com.dina.ui.widget;

import java.util.ArrayList;
import java.util.List;

import com.soli.lib.R;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.dina.ui.model.BasicItem;
import br.com.dina.ui.model.IListItem;
import br.com.dina.ui.model.MarqueeTextView;
import br.com.dina.ui.model.RoundImageView;
import br.com.dina.ui.model.ViewItem;

public class UITableView extends LinearLayout {

    private int mIndexController = 0;
    private LayoutInflater mInflater;
    private LinearLayout mMainContainer;
    private LinearLayout mListContainer;
    private List<IListItem> mItemList;
    private OnTableItemClickListener onTableItemClickListener;
    private OnTableViewItemClickListener onTableViewItemClickListener;
    private boolean isLineCross;// 设置分割线条是否拉通
    private boolean lastLineDisplay = false;// 最后一条线是否显示
    private boolean imageInResource = true;// 设置图片显示的来源
    private Context ctx;
    
    private boolean isCanMarqueen = true;//决定改title上文字是否可以滚动显示，如果不可以滚动，你那么就隔断显示
    
    public UITableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        ctx = context;
        
        mItemList = new ArrayList<IListItem>();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mMainContainer = (LinearLayout) mInflater.inflate(R.layout.list_container, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT);
        addView(mMainContainer, params);
        mListContainer = (LinearLayout) mMainContainer.findViewById(R.id.buttonsContainer);

    }

    public UITableView(Context context) {
        super(context);
        mItemList = new ArrayList<IListItem>();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMainContainer = (LinearLayout) mInflater.inflate(R.layout.list_container, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        addView(mMainContainer, params);
        mListContainer = (LinearLayout) mMainContainer.findViewById(R.id.buttonsContainer);

    }

    /**
     * 显示颜色不同的title
     * 
     * @param title
     * @param matchString
     * @param defaultColor
     * @param matchColor
     */
    public void addBasicItem(String title, String matchString, int defaultColor, int matchColor, int _drawableChevron) {
        mItemList.add(new BasicItem(title, matchString, defaultColor, matchColor, _drawableChevron));
    }

    /**
     * @param title 标题
     */
    public void addBasicItem(String title) {
        mItemList.add(new BasicItem(title));
    }

    /**
     * @param _title 标题
     * @param _count 右边文字
     */
    public void addBasicItem(String _title, String _count) {
        mItemList.add(new BasicItem(_title, _count));
    }

    /**
     * @param _title 标题
     * @param _count 右边文字
     */
    public void addBasicItem(String _title, String _count, int _drawableChevron, boolean clicable) {
        mItemList.add(new BasicItem(_title, _count, _drawableChevron, clicable));
    }

    /**
     * @param viewItem
     */
    public void addViewItem(ViewItem viewItem) {
        mItemList.add(viewItem);
    }

    /**
     * @param _title 标题
     * @param _count 右边文字
     * @param _drawableChevron 右边箭头图标
     */
    public void addBasicItem(String _title, String _count, int _drawableChevron) {
        mItemList.add(new BasicItem(_title, _count, _drawableChevron));
    }

    /**
     * @param _title 标题
     * @param _drawableChevron 右边箭头图标
     */
    public void addBasicItem(String _title, int _drawableChevron) {
        mItemList.add(new BasicItem(_title, _drawableChevron));
    }

    /**
     * @param _title 标题
     * @param _drawableChevron 右边箭头图标
     */
    public void addBasicItem(int _drawableWaring, String _title) {
        mItemList.add(new BasicItem(_drawableWaring, _title));
    }

    /**
     * 初始默认字体的原色
     * 
     * @param _title 标题
     * @param _drawableChevron 右边箭头图标
     */
    public void addBasicItem(String _title, int color, int _drawableChevron) {
        mItemList.add(new BasicItem(_title, color, _drawableChevron));
    }

    /**
     * 初始默认字体的原色,加左边显示的图标
     * 
     * @param _title 标题
     * @param _drawableChevron 右边箭头图标
     */
    public void addBasicItem(int colorleft, String _title, int color, int _drawableChevron) {
        mItemList.add(new BasicItem(colorleft, _title, color, _drawableChevron));
    }

    /**
     * 添加默认点击方式
     * 
     * @param _title
     * @param color
     * @param _drawableChevron
     */
    public void addBasicItem(int colorleft, String _title, int color, int _drawableChevron, boolean isClickable) {
        mItemList.add(new BasicItem(colorleft, _title, color, _drawableChevron, isClickable));
    }

    /**
     * 添加默认点击方式
     * 
     * @param _title
     * @param color
     * @param _drawableChevron
     */
    public void addBasicItem(String _title, int color, int _drawableChevron, boolean isClickable) {
        mItemList.add(new BasicItem(_title, color, _drawableChevron, isClickable));
    }

    // /**
    // *
    // * @param drawable
    // * 左边图标
    // * @param _title
    // * 标题
    // * @param _drawableChevron
    // * 右边箭头图标
    // */
    // public void addBasicItem(int drawable, String _title, int
    // _drawableChevron) {
    // mItemList.add(new BasicItem(drawable, _title, _drawableChevron));
    // }

    /**
     * @param item
     */
    public void addBasicItem(BasicItem item) {
        mItemList.add(item);
    }

    public void commit() {
        mIndexController = 0;

        if (mItemList.size() > 1) {
            // when the list has more than one item
            for (int i = 0; i < mItemList.size(); i++) {
                IListItem obj = mItemList.get(i);

                View tempItemView = mInflater.inflate(R.layout.list_item, null);

                View line = tempItemView.findViewById(R.id.line);
                View lineCross = tempItemView.findViewById(R.id.lineCross);

                if (isLineCross) {
                    line.setVisibility(View.GONE);
                    lineCross.setVisibility(View.VISIBLE);
                } else {
                    line.setVisibility(View.VISIBLE);
                    lineCross.setVisibility(View.GONE);
                }

                if (i == (mItemList.size() - 1) && !lastLineDisplay) {
                    // 是最后一个,那么就不要一条线
                    line.setVisibility(View.GONE);
                    lineCross.setVisibility(View.GONE);
                }
                if (i == (mItemList.size() - 1) && lastLineDisplay) {
                    // 是最后一个,那么就不要一条线
                    line.setVisibility(View.GONE);
                    lineCross.setVisibility(View.VISIBLE);
                }

                setupItem(tempItemView, obj, mIndexController);

                // tempItemView.setClickable(obj.isClickable());
                mListContainer.addView(tempItemView);

                mIndexController++;
            }
        } else if (mItemList.size() == 1) {
            // when the list has only one item
            View tempItemView = mInflater.inflate(R.layout.list_item, null);
            IListItem obj = mItemList.get(0);

            View line = tempItemView.findViewById(R.id.line);
            View lineCross = tempItemView.findViewById(R.id.lineCross);

            if (!lastLineDisplay) {
                // 是最后一个,那么就不要一条线
                line.setVisibility(View.GONE);
                lineCross.setVisibility(View.GONE);
            }
            if (lastLineDisplay) {
                line.setVisibility(View.GONE);
                lineCross.setVisibility(View.VISIBLE);
            }

            setupItem(tempItemView, obj, mIndexController);
            // tempItemView.setClickable(obj.isClickable());
            mListContainer.addView(tempItemView);
        }
    }

    private void setupItem(View view, IListItem item, int index) {
        if (item instanceof BasicItem) {
            BasicItem tempItem = (BasicItem) item;
            setupBasicItem(view, tempItem, mIndexController);
        } else if (item instanceof ViewItem) {
            ViewItem tempItem = (ViewItem) item;
            setupViewItem(view, tempItem, mIndexController);
        }
    }
    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     * 
     * @param dipValue
     * @param scale
     *            （DisplayMetrics类中属性density；Context.getResources().
     *            getDisplayMetrics().density）
     * @return
     */
    private  int dip2px(float dipValue, float scale) {
        return (int) (dipValue * scale + 0.5f);
    }
    
    /**
     * @param view
     * @param item
     * @param index
     */
    private void setupBasicItem(View view, BasicItem item, int index) {

        item.setView(view);

        if (item.getDrawable() != -1) {
            ((RoundImageView) view.findViewById(R.id.image)).setVisibility(View.VISIBLE);
            if (imageInResource) {
                ((RoundImageView) view.findViewById(R.id.image)).setBackgroundResource(item.getDrawable());
            } else {
                ((RoundImageView) view.findViewById(R.id.image)).setBackgroundColor(item.getDrawable());
            }
        } else {
            ((RoundImageView) view.findViewById(R.id.image)).setVisibility(View.GONE);
        }

        if (item.getSubtitle() != null) {
            ((TextView) view.findViewById(R.id.subtitle)).setText(item.getSubtitle());
        } else {
            ((TextView) view.findViewById(R.id.subtitle)).setVisibility(View.GONE);
        }
        
        //不滚动显示的时候
        if (!isCanMarqueen) {
            ((MarqueeTextView) view.findViewById(R.id.title)).SetCanMarquee(isCanMarqueen);
            ((MarqueeTextView) view.findViewById(R.id.title)).setEllipsize(TruncateAt.END);
        }
        
        // 一个title显示不同的颜色
        if (item.getDifferentColor()) {

            if (item.getColor() != -1) {
                ((MarqueeTextView) view.findViewById(R.id.title)).setTextColor(item.getColor());
            }

            SpannableString ss = new SpannableString(item.getTitle());
            String lowerLower = item.getTitle().toLowerCase();
            String[] Searh = item.getMatch().toLowerCase().split(" ");
            for (int i = 0; i < Searh.length; i++) {
                int temp = lowerLower.indexOf(Searh[i]);
                if (temp != -1) {
                    // 表示存在
                    ss.setSpan(new ForegroundColorSpan(item.getMatchColor()), temp, temp + Searh[i].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new StyleSpan(Typeface.BOLD), temp, temp + Searh[i].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            // 有相同的字符串
            ((MarqueeTextView) view.findViewById(R.id.title)).setText(ss);

        } else {
            ((MarqueeTextView) view.findViewById(R.id.title)).setText(item.getTitle());
            if (item.getColor() != -1) {
                ((MarqueeTextView) view.findViewById(R.id.title)).setTextColor(item.getColor());
            }
        }

        // 需要显示其他的图片
        if (item.getDrawableChevron() != -1) {
            ((ImageView) view.findViewById(R.id.chevron)).setImageResource(item.getDrawableChevron());
        }

        // 显示右边红色的提示小东西
        if (item.getmDrawableRightWaring() != -1) {
            ((RoundImageView) view.findViewById(R.id.waring)).setVisibility(View.VISIBLE);
            RoundImageView imageView = (RoundImageView) view.findViewById(R.id.waring);
            if (imageInResource) {
                imageView.AllowRound(false);
                imageView.setImageResource(item.getmDrawableRightWaring());
            } else {
                //特用
                imageView.setMinimumHeight(dip2px(35, ctx.getResources().getDisplayMetrics().density));
                imageView.setMinimumWidth(dip2px(35, ctx.getResources().getDisplayMetrics().density));
                imageView.setBackgroundColor(item.getmDrawableRightWaring());
            }
            
        } else {
            ((ImageView) view.findViewById(R.id.waring)).setVisibility(View.GONE);
        }
        
        //右边图标
        if (item.getmDrawableRight() != -1) {
            ((RoundImageView) view.findViewById(R.id.imageRight)).setVisibility(View.VISIBLE);
            if (imageInResource) {
                ((RoundImageView) view.findViewById(R.id.imageRight)).setBackgroundResource(item.getmDrawableRight());
            } else {
                ((RoundImageView) view.findViewById(R.id.imageRight)).setBackgroundColor(item.getmDrawableRight());
            }
        } else {
            ((RoundImageView) view.findViewById(R.id.imageRight)).setVisibility(View.GONE);
        }
        
        //右边没有什么需要显示，就隐藏掉
        if (TextUtils.isEmpty(item.getCount())) {
            ((LinearLayout) view.findViewById(R.id.RightSide)).setVisibility(View.GONE); 
        }else {
            ((LinearLayout) view.findViewById(R.id.RightSide)).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.itemCount)).setText(item.getCount());
        }
        
        view.setTag(index);

        if (item.isClickable()) {
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (onTableItemClickListener != null)
                        onTableItemClickListener.onItemClick((Integer) view.getTag());
                }

            });
        } else {

            if (item.getDrawableChevron() == -1) {
                ((ImageView) view.findViewById(R.id.chevron)).setVisibility(View.GONE);
            }
        }
    }

    /**
     * @param view 父视图，也就是itemContainer视图
     * @param itemView 要显示的内容视图
     * @param index 自定义视图需要注意，需要加入android:duplicateParentState="true"
     */
    private void setupViewItem(View view, ViewItem itemView, int index) {

        if (itemView.getView() != null) {// 必须有需要显示的视图，也就是自定义的视图

            itemView.setFatherView(view);// 保存父视图，后面动态修改的时候需要用
            RelativeLayout father = (RelativeLayout) view.findViewById(R.id.itemContainer);

            // 自定义背景色
            if (itemView.getBackground() != -1) {
                father.setBackgroundResource(itemView.getBackground());
            }

            // 添加自定义显示的视图
            LinearLayout itemContainer = (LinearLayout) view.findViewById(R.id.layout);
            itemContainer.removeAllViews();
            itemContainer.setOrientation(LinearLayout.VERTICAL);
            itemContainer.addView(itemView.getView());

            // 添加点击事件
            if (itemView.isClickable()) {
                father.setTag(index);
                father.setContentDescription(String.valueOf(itemView.getGroup()));
                father.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onTableViewItemClickListener != null)
                            onTableViewItemClickListener.onItemClick(Integer.valueOf(view.getContentDescription().toString()), (Integer) view.getTag());
                    }
                });
            } else {
                father.setOnClickListener(null);
            }
        }
    }

    public interface OnTableItemClickListener {

        void onItemClick(int index);

    }

    public interface OnTableViewItemClickListener {

        void onItemClick(int Group, int index);
    }

    /**
     * @return
     */
    public int getCount() {
        return mItemList.size();
    }

    /**
	 * 
	 */
    public void clear() {
        mItemList.clear();
        mListContainer.removeAllViews();
    }

    /**
     * @param listener
     */
    public void setOnTableItemClickListener(OnTableItemClickListener listener) {
        this.onTableItemClickListener = listener;
    }

    /**
	 * 
	 */
    public void removeClickListener() {
        this.onTableItemClickListener = null;
    }
    
    /**
     * 设置是否可以marqueen
     * @param isCanMarquee
     */
    public void SetTitleCanMarquee(boolean isCanMarquee)
    {
        this.isCanMarqueen = isCanMarquee;
    }
    /**
     * @param listener
     */
    public void setOnTableItemClickListener(OnTableViewItemClickListener listener) {
        this.onTableViewItemClickListener = listener;
    }

    /**
	 * 
	 */
    public void removeViewItemClickListener() {
        this.onTableViewItemClickListener = null;
    }

    /**
     * 设置分割线条，是否拉通
     */
    public void setLineIsCross(boolean isCross) {
        isLineCross = isCross;
    }
    /**
     * 如果是最后一个，是否显示横线
     * 
     * @param lastLineDisplay
     */
    public void setLastLineDisplay(boolean lastLineDisplay) {
        this.lastLineDisplay = lastLineDisplay;
    }

    /**
     * 设置图片显示来源
     * 
     * @param fromResource
     */
    public void setImageDisplayFromResource(boolean fromResource)
    {
        this.imageInResource = fromResource;
    }

    /**
     * 获取BasicItem
     * 
     * @param index
     * @return
     */
    public BasicItem getBasicItem(int index) {

        if (mItemList == null) {
            return null;
        }

        if (index > (mItemList.size() - 1)) {
            return null;
        }

        return (BasicItem) mItemList.get(index);
    }

    /**
     * 获取BasicItem
     * 
     * @param index
     * @return
     */
    public ViewItem getViewItem(int index) {

        if (mItemList == null) {
            return null;
        }

        if (index > (mItemList.size() - 1)) {
            return null;
        }

        return (ViewItem) mItemList.get(index);
    }

    public void updateBasicItem(BasicItem basicItem, int index) {
        setupBasicItem(basicItem.getView(), basicItem, index);
    }

    public void updateViewItem(ViewItem viewItem, int index) {
        setupViewItem(viewItem.getFatherView(), viewItem, index);
    }
}
