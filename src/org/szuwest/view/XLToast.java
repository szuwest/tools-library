package org.szuwest.view;

import org.szuwest.library.R;
import org.szuwest.utils.Util;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.view.View;
import android.content.Context;

public class XLToast {
	public static enum XLToastType {
		XLTOAST_TYPE_NORMAL, // 普通无图标toast
		XLTOAST_TYPE_ALARM, // 失败or警告toast
		XLTOAST_TYPE_SUC, // 成功toast
		XLTOAST_TYPE_SMILE// 微笑toast
	};

	public static void showToast(Context context, XLToastType type, int gravity, String str, int maxLines, int timeDuration) {
		if (str != null) {
			Toast toast = new Toast(context);
			View toastView = LayoutInflater.from(context).inflate(R.layout.xl_toast_view, null);
			TextView txt = (TextView) toastView.findViewById(R.id.xl_toast_txt);
			txt.setText(str);
			if (maxLines > 0)
				txt.setMaxLines(maxLines);
			ImageView img = (ImageView) toastView.findViewById(R.id.xl_toast_img);
			switch (type) {
			case XLTOAST_TYPE_NORMAL:
				img.setVisibility(View.GONE);
				// 无图标时，字居中
				txt.setGravity(Gravity.CENTER);
                txt.setPadding(Util.dip2px(context, 15), 0, Util.dip2px(context, 3), Util.dip2px(context, 15));
				break;
			case XLTOAST_TYPE_ALARM:
				img.setVisibility(View.VISIBLE);
				img.setBackgroundResource(R.drawable.xl_dlg_error);
				break;
			case XLTOAST_TYPE_SUC:
				img.setVisibility(View.VISIBLE);
				img.setBackgroundResource(R.drawable.xl_dlg_right);
				break;
			case XLTOAST_TYPE_SMILE://图片有问题
				img.setVisibility(View.VISIBLE);
				img.setBackgroundResource(R.drawable.xl_dlg_smile);
				break;
			default:
				img.setVisibility(View.GONE);
                // 无图标时，字居中
                txt.setGravity(Gravity.CENTER);
                txt.setPadding(Util.dip2px(context, 15), 0, Util.dip2px(context, 3), Util.dip2px(context, 15));
				break;
			}
			toast.setView(toastView);
			if(gravity == Gravity.BOTTOM){
				int px = Util.dip2px(context, 80);
				toast.setGravity(Gravity.BOTTOM, 0, px);
			}else{
				toast.setGravity(gravity, 0, 0);
			}
			toast.setDuration(timeDuration);// 默认只显示2S
			toast.show();
		}
	}
	
	public static void showToast(Context context, XLToastType type, String str) {
		showToast(context, type, Gravity.BOTTOM, str, Toast.LENGTH_SHORT);
	}
	public static void showToast(Context context, XLToastType type, String str, int timeDuration) {
		showToast(context, type, Gravity.BOTTOM, str, 0, timeDuration);
	}

	public static void showToast(Context context, XLToastType type, int gravity, String str) {
		showToast(context, type, gravity, str, 0, Toast.LENGTH_SHORT);
	}
	public static void showToast(Context context, XLToastType type, int gravity, String str, int timeDuration) {
		showToast(context, type, gravity, str, 0, timeDuration);
	}
	
//	public static void showToast(Context context, XLToastType type, String str, int maxLines) {
//		showToast(context, type, Gravity.BOTTOM, str, maxLines, Toast.LENGTH_SHORT);
//	}
	public static void showToast(Context context, XLToastType type, String str, int maxLines, int timeDuration) {
		showToast(context, type, Gravity.BOTTOM, str, maxLines, timeDuration);
	}
}
