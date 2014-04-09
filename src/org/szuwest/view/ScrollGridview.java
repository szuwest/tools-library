package org.szuwest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 可以放在ScrollView中的GridView
 * @author Administrator
 *
 */
public class ScrollGridview extends GridView{ 		 
	
    public ScrollGridview(Context context, AttributeSet attrs, int defStyle) { 
        super(context, attrs, defStyle); 
    } 
    
    public ScrollGridview(Context context, AttributeSet attrs) { 
        super(context, attrs); 
    } 
    
    public ScrollGridview(Context context) { 
        super(context); 
    } 
    
    @Override 
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 
        int expandSpec = MeasureSpec.makeMeasureSpec(  
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);  
        super.onMeasure(widthMeasureSpec, expandSpec);  	 
    } 
}
