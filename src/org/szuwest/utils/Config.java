package org.szuwest.utils;

import java.util.List;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class Config {
//	private static final String TAG = "Config";
	
	public static int getVerCode(Context context,String packageName) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					packageName, 0).versionCode;
		} catch (NameNotFoundException e) {
//			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		return verCode;
	}
	
	public static String getVerName(Context context,String packageName) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					packageName, 0).versionName;
		} catch (NameNotFoundException e) {
//			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		return verName;	

	}
	
	public static String getAppName(Context context,String packageName) {
		return context.getPackageManager().getApplicationLabel(context.getApplicationInfo()).toString();
	}
	
	public static Drawable getAppIcon(Context context,String packageName) {
		return context.getPackageManager().getApplicationIcon(context.getApplicationInfo());
	}
	
	public static boolean isPackageAvilible(Context context, String packageName){ 
		//获取所有已安装程序的包信息 
        List<PackageInfo> pinfo = context.getPackageManager().getInstalledPackages(0);
       //从pinfo中将包名字逐一取出比较
            if(pinfo != null){ 
            for(int i = 0; i < pinfo.size(); i++){ 
                if(packageName.equals(pinfo.get(i).packageName))
                	return true; 
            } 
        } 
        return false;
	}
	
	public static boolean isIntentAvailable(Context context, String action) { 
        final PackageManager packageManager = context.getPackageManager(); 
        final Intent intent = new Intent(action); 
        List<ResolveInfo> list = 
                packageManager.queryIntentActivities(intent, 
                        PackageManager.MATCH_DEFAULT_ONLY); 
       
        return list.size() > 0; 
    } 

	public static String getPhoneNum(Context context){
		  TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		  return tm.getLine1Number();
	}
	
	public static String getIMEI(Context context){
		  TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		  return tm.getDeviceId();
	}
	
	//获取device的os version 
	public static String getOSVersion() { 
        String version = android.os.Build.VERSION.RELEASE; 
        return version; 
    } 
	
	public static String getModel() { 
        return android.os.Build.MODEL; 
    } 
	
	// 取得device的IP address 
	public static String getIp(Context context) { 
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE); 
        WifiInfo wifiInfo = wifiManager.getConnectionInfo(); 
        int ipAddress = wifiInfo.getIpAddress(); 
         
        // 格式化IP address，例如：格式化前：1828825280，格式化后：192.168.1.109 
        String ip = String.format("%d.%d.%d.%d", 
                (ipAddress & 0xff), 
                (ipAddress >> 8 & 0xff), 
                (ipAddress >> 16 & 0xff), 
                (ipAddress >> 24 & 0xff)); 
        return ip; 
    } 
	
	public static int getDefaultDisplayWidth(Activity activity){
		return activity.getWindowManager().getDefaultDisplay().getWidth();
	}
	
	public static int getDefaultDisplayHeight(Activity activity){
		return activity.getWindowManager().getDefaultDisplay().getHeight();
	}

	public static int getDisplayMetricsHeight(Activity activity){
		 DisplayMetrics dm = new DisplayMetrics();  
		 activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}
	
	public static int getDisplayMetricsWidth(Activity activity){
		DisplayMetrics dm = new DisplayMetrics();  
		 activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	
	public static void vibrate(Activity activity, long pattern[]){
	    	Vibrator vibrator;
	    	vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
//			long[] pattern = { 10, 200 }; // OFF/ON/OFF/ON...
			vibrator.vibrate(pattern, -1);
	}
	
	public static void setVolume(Activity activity, int volume){
			AudioManager mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
			int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			long[] pattern = { 10, 80 }; // OFF/ON/OFF/ON...
			int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			current +=volume;
			if(current==0) vibrate(activity,pattern);
			else if(current==max) vibrate(activity,pattern);
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
//			LogUtils.i("setVolume", "max : " + max + " current : " + current);
	}
	
	public static int getVolume(Activity activity){
		AudioManager mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
//		int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//		if(current==0) return activity.getString(R.string.silent)+":0%";
//		else return activity.getString(R.string.volume)+":"+(int)current*100 / max + "%";
		return current;
	}
	
	/* 
     *  
     * 设置屏幕亮度 lp = 0 全暗 ，lp= -1,根据系统设置， lp = 1; 最亮 
     */  
	public static void setBrightness(Activity activity, float brightness) {  
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();  
        if(isAutoBrightness(activity))
        	stopAutoBrightness(activity);
        long[] pattern = { 10, 80 }; // OFF/ON/OFF/ON...
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;  
        if (lp.screenBrightness > 1) {  
            lp.screenBrightness = 1;  
            vibrate(activity,pattern); 
        } else if (lp.screenBrightness < 0.05) {  
            lp.screenBrightness = (float) 0.05;  
            vibrate(activity,pattern);  
        }  
        activity.getWindow().setAttributes(lp);  
//        LogUtils.i(TAG, "current screenBrightness= " + lp.screenBrightness);  
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
	    } catch (SettingNotFoundException e) {
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
	        nowBrightnessValue = android.provider.Settings.System.getInt(
	                resolver, SCREEN_BRIGHTNESS);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return nowBrightnessValue;
	}
	
//	public static String getBrightnessPercent(Activity activity){
//		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
//		float percent = lp.screenBrightness*100;
//		if(percent==-100)
//			return activity.getString(R.string.brightness)+":"+activity.getString(R.string.default_value);
//		else return activity.getString(R.string.brightness)+":"+(int)percent + "%";
//	}
	
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
	    Uri uri = android.provider.Settings.System
	            .getUriFor(SCREEN_BRIGHTNESS);
	    android.provider.Settings.System.putInt(activity.getContentResolver(), SCREEN_BRIGHTNESS,
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
//		Toast.makeText(activity, activity.getString(R.string.screen_lock), Toast.LENGTH_SHORT).show();
	}
	
	public static void setWindowTouchable(Activity activity){
		WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
		attrs.flags &= (~WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
		activity.getWindow().setAttributes(attrs);
//		Toast.makeText(activity, activity.getString(R.string.screen_unlock), Toast.LENGTH_SHORT).show();
	}

}