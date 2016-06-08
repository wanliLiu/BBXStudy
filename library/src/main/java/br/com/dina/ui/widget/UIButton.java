package br.com.dina.ui.widget;

import com.soli.lib.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UIButton extends LinearLayout {

	private LayoutInflater mInflater;
	private RelativeLayout mButtonContainer;
	private ClickListener mClickListener;

	private TextView tvTitle;
	private TextView subTitle;
	private ImageView imageView;
	private View lineCross;
	private View line;

	/**
	 * 设置分割线条，是否拉通
	 */
	public void setLineIsCross(boolean isCross) {
		if (isCross) {
			line.setVisibility(View.GONE);
			lineCross.setVisibility(View.VISIBLE);
		} else {
			line.setVisibility(View.VISIBLE);
			lineCross.setVisibility(View.GONE);
		}
	}

	public TextView getTvTitle() {
		return tvTitle;
	}

	public TextView getSubTitle() {
		return subTitle;
	}

	public ImageView getImageView() {
		return imageView;
	}

	public UIButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context, attrs);
	}

	public void init(Context context, AttributeSet attrs) {
		this.setClickable(true);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mButtonContainer = (RelativeLayout) mInflater.inflate(
				R.layout.list_item, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.UIButton, 0, 0);

		CharSequence mTitle = a.getString(R.styleable.UIButton_title1);
		CharSequence mSubtitle = a.getString(R.styleable.UIButton_subtitle1);
		int mImage = a.getResourceId(R.styleable.UIButton_image, -1);

		tvTitle = (TextView) mButtonContainer.findViewById(R.id.title);
		subTitle = (TextView) mButtonContainer.findViewById(R.id.subtitle);
		imageView = (ImageView) mButtonContainer.findViewById(R.id.image);
		lineCross = mButtonContainer.findViewById(R.id.lineCross);
		line = mButtonContainer.findViewById(R.id.line);

		if (mTitle != null) {
			tvTitle.setText(mTitle.toString());
		} else {
			tvTitle.setText("subtitle");
		}

		if (mSubtitle != null) {
			subTitle.setText(mSubtitle.toString());
		} else {
			subTitle.setVisibility(View.GONE);
		}

		if (mImage != -1) {
			imageView.setImageResource(mImage);
		}

		mButtonContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				if (mClickListener != null)
					mClickListener.onClick(UIButton.this);
			}

		});

		addView(mButtonContainer, params);

		a.recycle();
	}

	public interface ClickListener {
		void onClick(View view);
	}

	/**
	 * 
	 * @param listener
	 */
	public void addClickListener(ClickListener listener) {
		this.mClickListener = listener;
	}

	/**
	 * 
	 */
	public void removeClickListener() {
		this.mClickListener = null;
	}

}
