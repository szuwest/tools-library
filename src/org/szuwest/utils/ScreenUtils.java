package org.szuwest.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import org.szuwest.lib.AppManager;
import org.szuwest.lib.BaseApplication;


public class ScreenUtils {

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(float dpValue) {
		final float scale = BaseApplication.getInstance().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(float pxValue) {
		final float scale = BaseApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
	}

	/*
	 * 屏幕尺寸相关操作
	 * 
	 * 在第一个Activity初始化的时候必须设置高宽值，后面才能调用到
	 */
	private static int screenHeight = 0;
	private static int screenWidth = 0;

	public static int getScreenHeight() {
		if(screenHeight == 0){
            if (AppManager.getInstance().currentActivity() != null) {
			    setScreenSize(AppManager.getInstance().currentActivity());
            } else {
                setScreenSize();
            }
		}
		return screenHeight;
	}

	public static int getScreenWidth() {
		if(screenWidth == 0){
            if (AppManager.getInstance().currentActivity() != null) {
                setScreenSize(AppManager.getInstance().currentActivity());
            } else {
                setScreenSize();
            }
		}
		return screenWidth;
	}

	public static void setScreenSize(Activity activity) {
		if (screenHeight == 0 || screenWidth == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			screenWidth = dm.widthPixels;
			screenHeight = dm.heightPixels;
		}
	}

    public static void setScreenSize() {
        Display display= ((WindowManager) BaseApplication.getInstance().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
    }

	public static void hideIme(Activity context){
        if(context==null)
            return;
        final View v = context.getWindow().peekDecorView();
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /*
     *
     * 设置屏幕亮度 lp = 0 全暗 ，lp= -1,根据系统设置， lp = 1; 最亮
     */
    public static void setBrightness(Activity activity, float brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if(isAutoBrightness(activity))
            stopAutoBrightness(activity);
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
        } else if (lp.screenBrightness < 0.05) {
            lp.screenBrightness = (float) 0.05;
        }
        activity.getWindow().setAttributes(lp);
//        LogUtils.i("current screenBrightness= " + lp.screenBrightness);
    }

    //since api 8
    private static String SCREEN_BRIGHTNESS ="screen_brightness";
    private static String SCREEN_BRIGHTNESS_MODE="screen_brightness_mode";
    private static int SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1;
    private static int SCREEN_BRIGHTNESS_MODE_MANUAL = 0;

    /**
     * 判断是否开启了自动亮度调节
     *  since api 8
     */
    public static boolean isAutoBrightness(Activity activity) {
        boolean automicBrightness = false;
        try {
//	        automicBrightness = Settings.System.getInt(activity.getContentResolver(),
//	                Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
            automicBrightness = Settings.System.getInt(activity.getContentResolver(),
                    SCREEN_BRIGHTNESS_MODE) == SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }

    /**
     * 获取屏幕的亮度
     * since api 8
     */
    public static int getScreenBrightness(Activity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
//	        nowBrightnessValue = android.provider.Settings.System.getInt(
//	                resolver, Settings.System.SCREEN_BRIGHTNESS);
            nowBrightnessValue = Settings.System.getInt(
                    resolver, SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    /**
     * 停止自动亮度调节 since api 8
     */
    public static void stopAutoBrightness(Activity activity) {

//	    Settings.System.putInt(activity.getContentResolver(),
//	            Settings.System.SCREEN_BRIGHTNESS_MODE,
//	            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.putInt(activity.getContentResolver(),
                SCREEN_BRIGHTNESS_MODE,
                SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * 开启亮度自动调节
     *  since api 8
     * @param activity
     */
    public static void startAutoBrightness(Activity activity) {
//	    Settings.System.putInt(activity.getContentResolver(),
//	            Settings.System.SCREEN_BRIGHTNESS_MODE,
//	            Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        Settings.System.putInt(activity.getContentResolver(),
                SCREEN_BRIGHTNESS_MODE,
                SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    /**
     * 保存亮度设置状态
     *  since api 8
     */
    public static void saveBrightness(Activity activity, int brightness) {
        Uri uri = Settings.System
                .getUriFor(SCREEN_BRIGHTNESS);
        Settings.System.putInt(activity.getContentResolver(), SCREEN_BRIGHTNESS,
                brightness);
        // resolver.registerContentObserver(uri, true, myContentObserver);
        activity.getContentResolver().notifyChange(uri, null);
    }

    public static void setFullScreen(Activity activity){
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static void cancelFullScreen(Activity activity){
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(attrs);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static void disableWindowTouch(Activity activity){
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags |= (WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        activity.getWindow().setAttributes(attrs);
    }

    public static void setWindowTouchable(Activity activity){
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        activity.getWindow().setAttributes(attrs);
    }

}