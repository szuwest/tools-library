package org.szuwest.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.szuwest.library.R;
import org.szuwest.utils.ScreenUtils;

public class CommonDialog extends Dialog {

	public static final int COLOR_GREY = 0;
	public static final int COLOR_WHITE = 1;
	public static final int COLOR_RED = 2;

	private LinearLayout container;
	private LinearLayout dialogLayout;
	private TextView titleTextView;
	private RelativeLayout mainLayout;
	private Button cancelButton;

	private int layoutHeight = 0;

	public CommonDialog(Context context) {
		super(context, R.style.CommonDialog);
		mainLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.common_dialog_commondialg, null);
		container = (LinearLayout) mainLayout.findViewById(R.id.CommonDialogCustomContainer);
		titleTextView = (TextView) mainLayout.findViewById(R.id.CommonDialogTitle);
		dialogLayout = (LinearLayout) mainLayout.findViewById(R.id.CommonDialogLayout);
		cancelButton = (Button) mainLayout.findViewById(R.id.CommonDialogCancel);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(mainLayout);
	}

	@Override
	protected void onStart() {
		super.onStart();
//		dialogLayout.measure(0, 0);
//		layoutHeight = dialogLayout.getMeasuredHeight();
//		ObjectAnimator.ofFloat(dialogLayout, "translationY", layoutHeight, 0).setDuration(300).start();
		dialogLayout.setVisibility(View.VISIBLE);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	public void addView(View view) {
		container.addView(view);
	}

	public void setTitle(int textResId) {
		titleTextView.setText(textResId);
	}

	public void setTitle(String text) {
		titleTextView.setText(text);
	}

	public void addButton(int textResId, int color, View.OnClickListener listener) {
		addButton(getContext().getString(textResId), color, listener);
	}

	public void addButton(String text, int color, View.OnClickListener listener) {
		Button button = new Button(getContext());
		button.setText(text);
		button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		@SuppressWarnings("deprecation")
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.topMargin = ScreenUtils.dip2px(5);
		params.bottomMargin = ScreenUtils.dip2px(5);
		button.setLayoutParams(params);
		switch (color) {
			case COLOR_GREY:
				button.setTextColor(0xFFFFFFFF);
				button.setShadowLayer(0, 0, -2, 0x88000000);
				button.setBackgroundResource(R.drawable.common_dialog_button_grey);
				break;
			case COLOR_RED:
				button.setTextColor(0xFFFFFFFF);
				button.setShadowLayer(0, 0, -2, 0x88000000);
				button.setBackgroundResource(R.drawable.common_dialog_button_red);
				break;
			case COLOR_WHITE:
				button.setTextColor(0xFF4B4B4B);
				button.setShadowLayer(0, 0, 2, 0x88FFFFFF);
				button.setBackgroundResource(R.drawable.common_dialog_button_white);
				break;
			default:
				break;
		}
		if (listener != null) {
//			button.setOnTouchListener(UiUtils.addPressedEffect);
			button.setOnClickListener(listener);
		}
		container.addView(button);
	}

//	@Override
//	public void dismiss() {
//		ObjectAnimator animator = ObjectAnimator.ofFloat(dialogLayout, "translationY", 0, layoutHeight);
//		animator.setDuration(300);
//		animator.addListener(new Animator.AnimatorListener() {
//			@Override
//			public void onAnimationStart(Animator animation) {
//			}
//
//			@Override
//			public void onAnimationEnd(Animator animation) {
//				CommonDialog.super.dismiss();
//			}
//
//			@Override
//			public void onAnimationCancel(Animator animation) {
//			}
//
//			@Override
//			public void onAnimationRepeat(Animator animation) {
//			}
//		});
//		animator.start();
//	}
}
