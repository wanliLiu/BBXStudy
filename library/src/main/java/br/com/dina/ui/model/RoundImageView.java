package br.com.dina.ui.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 圆角ImageView
 * 
 * @author skg
 * 
 */
public class RoundImageView extends ImageView {

    private boolean isAllowRound = true;
    
	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RoundImageView(Context context) {
		super(context);
		init();
	}

	private final RectF roundRect = new RectF();
	private float rect_adius = 8;
	private final Paint maskPaint = new Paint();
	private final Paint zonePaint = new Paint();

	private void init() {
		maskPaint.setAntiAlias(true);
		maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		//
		zonePaint.setAntiAlias(true);
		zonePaint.setColor(Color.WHITE);
		//
		float density = getResources().getDisplayMetrics().density;
		rect_adius = rect_adius * density;
	}

	public void setRectAdius(float adius) {
		rect_adius = adius;
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int w = getWidth();
		int h = getHeight();
		roundRect.set(0, 0, w, h);
	}

	@Override
	public void draw(Canvas canvas) {
	    try {
	        if (isAllowRound) {
	            canvas.saveLayer(roundRect, zonePaint, Canvas.ALL_SAVE_FLAG);
	            canvas.drawRoundRect(roundRect, rect_adius, rect_adius, zonePaint);
	            //
	            canvas.saveLayer(roundRect, maskPaint, Canvas.ALL_SAVE_FLAG);
	            super.draw(canvas);
	            canvas.restore();
            }else {
                super.draw(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * 是否允许圆角
	 * @param allow
	 */
	public void AllowRound(boolean allow)
	{
	    isAllowRound = allow;
	}
}