package org.szuwest.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * 实现跑马灯效果的文本视图
 * @author Dengjy
 *
 */
public class MarqueeTextView extends TextView{

	public MarqueeTextView(Context context) {
		super(context);
	}
	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
	    if(focused)
	        super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}

	@Override
	public void onWindowFocusChanged(boolean focused) {
	    if(focused)
	        super.onWindowFocusChanged(focused);
	}


	@Override
	public boolean isFocused() {
	    return true;
	}
}
