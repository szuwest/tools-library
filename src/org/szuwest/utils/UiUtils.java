package org.szuwest.utils;

import android.annotation.SuppressLint;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

public class UiUtils {

	// 给按钮加上按下效果
	public static final OnTouchListener addPressedEffect = new OnTouchListener() {

		@SuppressLint("NewApi")
		@SuppressWarnings("deprecation")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
            if (v.getBackground() == null) return false;
			/**
			 * 按下这个按钮进行的颜色过滤
			 */
			final float[] BT_SELECTED = new float[] { 1, 0, 0, 0, -50, 0, 1, 0, 0, -50, 0, 0, 1, 0, -50, 0, 0, 0, 1, 0 };

			/**
			 * 按钮恢复原状的颜色过滤
			 */
			final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
				if (android.os.Build.VERSION.SDK_INT < 16) {
					v.setBackgroundDrawable(v.getBackground());
				} else {
					v.setBackground(v.getBackground());
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));
				if (android.os.Build.VERSION.SDK_INT < 16) {
					v.setBackgroundDrawable(v.getBackground());
				} else {
					v.setBackground(v.getBackground());
				}
			}
			return false;
		}
	};

	public static class PressEffectTouchListener implements OnTouchListener {
		OnTouchListener custemListener = null;

		public PressEffectTouchListener(OnTouchListener onTouchListener) {
			custemListener = onTouchListener;
		}

		@SuppressLint("NewApi")
		@SuppressWarnings("deprecation")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			/**
			 * 按下这个按钮进行的颜色过滤
			 */
			final float[] BT_SELECTED = new float[] { 1, 0, 0, 0, -50, 0, 1, 0, 0, -50, 0, 0, 1, 0, -50, 0, 0, 0, 1, 0 };

			/**
			 * 按钮恢复原状的颜色过滤
			 */
			final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };
			if (custemListener != null)
				custemListener.onTouch(v, event);
            if (v.getBackground() == null) return false;
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
				if (android.os.Build.VERSION.SDK_INT < 16) {
					v.setBackgroundDrawable(v.getBackground());
				} else {
					v.setBackground(v.getBackground());
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));
				if (android.os.Build.VERSION.SDK_INT < 16) {
					v.setBackgroundDrawable(v.getBackground());
				} else {
					v.setBackground(v.getBackground());
				}
			}
			return false;
		}
	};

    public static final OnTouchListener addImgBtnPressedEffect = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if ( !(v instanceof ImageButton) ) return false;
            ImageButton imageButton = (ImageButton) v;
            if (imageButton.getDrawable() == null) return false;
            /**
             * 按下这个按钮进行的颜色过滤
             */
            final float[] BT_SELECTED = new float[] { 1, 0, 0, 0, -50, 0, 1, 0, 0, -50, 0, 0, 1, 0, -50, 0, 0, 0, 1, 0 };

            /**
             * 按钮恢复原状的颜色过滤
             */
            final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Drawable drawable = imageButton.getDrawable();
                drawable.setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
                imageButton.setImageDrawable(drawable);
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                Drawable drawable = imageButton.getDrawable();
                drawable.setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));
                imageButton.setImageDrawable(drawable);
            }
            return false;
        }
    };
}