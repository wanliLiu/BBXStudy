
package br.com.dina.ui.model;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

public class MarqueeTextView extends TextView {
    
    private boolean isCanMarquee = true;
    
    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    @Override
    @ExportedProperty(category = "focus")
    public boolean isFocused() {
        
        if (isCanMarquee) {
            return true;
        }
          
        return super.isFocused();
    }
    /**
     * 通过代码动态设置是否获取焦点
     * @param isFocus
     */
    public void SetCanMarquee(boolean isFocus) {
        isCanMarquee = isFocus;
    }
}
