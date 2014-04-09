package org.szuwest.view;


import org.szuwest.library.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class XLToastDialog extends Dialog {
	
	private ImageView mImageView;
	private TextView hintText;
	private String hint;

	public void setHint(String hint) {
		this.hint = hint;
		if(hintText != null)
			hintText.setText(hint);
	}

	public XLToastDialog(Context context, String hint) {
		super(context, R.style.bt_dialog);
		
		Window win = getWindow();
		win.requestFeature(Window.FEATURE_NO_TITLE);
		WindowManager.LayoutParams wAttrs = win.getAttributes();
		configWindow(win, wAttrs);
//		setCanceledOnTouchOutside(true);
		this.hint = hint;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Context ctxt = getContext();
		View dlgView = LayoutInflater.from(ctxt).inflate(R.layout.xl_toast_loading_dialog, null);
		setContentView(dlgView);
		mImageView = (ImageView) findViewById(R.id.xl_loadingImg);
		hintText = (TextView) findViewById(R.id.xl_loading_hint);
		if(hint != null)
			hintText.setText(hint);
		mAnimHandler.sendEmptyMessageDelayed(0, 100);
	}
	
	private void configWindow(Window win, WindowManager.LayoutParams wAttrs) {
		// 
		wAttrs.gravity = Gravity.BOTTOM;
		wAttrs.verticalMargin = 0.14F; 
//		win.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, 
//				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE); 
//		Display d = win.getWindowManager().getDefaultDisplay(); 
//		wAttrs.width = (int) (d.getWidth() * 0.9F); 
//		wAttrs.height = (int) (d.getHeight() * 0.6F);  
	}
	
	// 
		private Handler mAnimHandler = new Handler() {

			@Override
			public void dispatchMessage(Message msg) {
				int curLevel = mImageView.getDrawable().getLevel();
				mImageView.getDrawable().setLevel(curLevel == 14 ? 0 : curLevel + 1);
				mAnimHandler.sendEmptyMessageDelayed(0, 100);
			}

		};
}

