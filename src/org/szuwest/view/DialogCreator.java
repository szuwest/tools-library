package org.szuwest.view;


import android.content.Context;
import android.content.DialogInterface;

public class DialogCreator {
	
	public static CustomDialog createCustomDialog(Context context, String title, String msg) {
		return createCustomDialog(CustomDialog.WHITE_ALERT_STYLE, context, title, msg);
	}
	
	public static CustomDialog createCustomDialog(int customStyle, Context context, String title, String msg) {
		CustomDialog dialog = new CustomDialog(context, customStyle);
		dialog.setSureBtnListener(null);
		dialog.setTitle(title);
		dialog.setMessage(msg);
		return dialog;
	}
	
	public static CustomDialog createCustomDialog(Context context, String title,
			String msg, DialogInterface.OnClickListener posClick,
			DialogInterface.OnClickListener ngClick) {

		return createCustomDialog(CustomDialog.WHITE_NORMAL_STYLE, context, title, msg, posClick, ngClick);
	}
	
	public static CustomDialog createCustomDialog(int style, Context context, String title,
			String msg, DialogInterface.OnClickListener posClick,
			DialogInterface.OnClickListener ngClick) {
		CustomDialog dialog = new CustomDialog(context, style);
		dialog.setTitle(title);
		dialog.setMessage(msg);
		dialog.setSureBtnListener(posClick);
		dialog.setCancelBtnListener(ngClick);
		return dialog;
	}
}
