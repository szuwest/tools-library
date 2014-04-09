package org.szuwest.view;

import org.szuwest.library.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialog extends Dialog{
	
	public static final int BLACK_ALERT_STYLE = 1;
	public static final int BLACK_NORMAL_STYLE = 2;
	public static final int BLACK_LOADING_STYLE = 3;
	public static final int WHITE_ALERT_STYLE = 4;
	public static final int WHITE_NORMAL_STYLE = 5;

	private int mStyle = BLACK_ALERT_STYLE;
	
	public CustomDialog(Context context) {
		this(context, BLACK_ALERT_STYLE);
	}
	
	public CustomDialog(Context context, int myStyle) {
		super(context, R.style.bt_dialog);
		mStyle = myStyle;
		initUI(context);
	}
	
	private TextView titleText;
	private TextView msgText;
	private Button sureBtn;
	private Button cancelBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		Context ctxt = getContext();
//		initUI(ctxt);
		super.onCreate(savedInstanceState);
	}
	
	private void initUI(Context context){
		int resLayoutId = 0;
		switch(mStyle){
			case BLACK_ALERT_STYLE:
				resLayoutId = R.layout.dialog_black_alert;
				break;
			case BLACK_NORMAL_STYLE:
				resLayoutId = R.layout.dialog_black_style;
				break;
			case BLACK_LOADING_STYLE:
				resLayoutId = R.layout.dialog_black_loading;
				break;
			case WHITE_ALERT_STYLE:
				resLayoutId = R.layout.dialog_white_alert;
				break;
			case WHITE_NORMAL_STYLE:
				resLayoutId = R.layout.dialog_white_style;
				break;
			default:
				resLayoutId = R.layout.dialog_black_alert;	
				break;
		}
		
		View dlgView = LayoutInflater.from(context).inflate(resLayoutId, null);
		
		titleText = (TextView) dlgView.findViewById(R.id.dialog_title);
		
		msgText = (TextView) dlgView.findViewById(R.id.dialog_message);
		
		sureBtn = (Button) dlgView.findViewById(R.id.dialog_sure_btn);
		
		cancelBtn = (Button) dlgView.findViewById(R.id.dialog_cancel_btn);
		
		setContentView(dlgView);
	}


	public void setTitle(CharSequence title) {
		if(titleText != null) {
            if (title != null && !title.equals("")) {
			    titleText.setText(title);
            } else {
//                titleText.setVisibility(View.GONE);
            }
        }
	}

    public void setTitle(int resId) {
        if (titleText != null) {
            if (resId > 0) titleText.setText(resId);
        }
    }

	public void setMessage(CharSequence message) {
		if(msgText != null)
			msgText.setText(message);
	}
	
	
	public void setSureBtnText(CharSequence text) {
		if (text != null) {
			sureBtn.setText(text);
		}
	}

	public void setCancelBtnText(CharSequence text) {
		if (text != null && cancelBtn != null) {
			cancelBtn.setText(text);
		}
	}

	public void setSureBtnListener(DialogInterface.OnClickListener lListener) {
		if (lListener != null && sureBtn != null) {
			sureBtn.setTag(lListener);
			sureBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					((DialogInterface.OnClickListener) v.getTag()).onClick(CustomDialog.this, 0);
				}
			});
		} else if ( sureBtn != null ){
			sureBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		}
	}

	public void setCancelBtnListener(DialogInterface.OnClickListener rListener) {
		if (rListener != null && cancelBtn != null) {
			cancelBtn.setTag(rListener);
			cancelBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					((DialogInterface.OnClickListener) v.getTag()).onClick(CustomDialog.this, 0);
				}
			});
		} else if( cancelBtn != null ){
			cancelBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		}
	}
}
