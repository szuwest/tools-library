package org.szuwest.utils;

import com.mobclick.android.MobclickAgent;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

public class Setting {
	public final static String SETTING = "setting";
	public static final String ISFIRSTIMEUSE = "isFirsTimeUse";
	public final static String ROWS = "rows";
	public final static String COLUMNS = "columns";
	public final static String NOTIFY_TYPE = "notify_type";
	public final static String WEEKTH = "weekth";
	public final static String WEEK_START_TIME = "weekth_time";
	public final static String MAIN_THEME_PATH = "main_theme_path";
	public final static String MAIN_THEME_LANDSCAPE = "main_theme_landscape";
	public final static String DETAIL_THEME_PATH = "detail_theme_path";

	public final static String ITEM_BG_COLOR = "item_bg_color";
	
	public static boolean isSpecialMode(){
		return true;
	}
	
	public static void saveWeekth(Context context, int weekth){
		SharedPreferences pref = context.getSharedPreferences(SETTING, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(WEEKTH, weekth);
		if(editor.commit())
			saveWeekthTime(context, System.currentTimeMillis());
	}
	public static int getWeekth(Context context){
		SharedPreferences pref = context.getSharedPreferences(SETTING, 0);
		return pref.getInt(WEEKTH, 0);
	}
	
	public static void saveWeekthTime(Context context, long weekthTime){
		SharedPreferences pref = context.getSharedPreferences(SETTING, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putLong(WEEK_START_TIME, weekthTime);
		editor.commit();
	}
	public static long getWeekthTime(Context context){
		SharedPreferences pref = context.getSharedPreferences(SETTING, 0);
		return pref.getLong(WEEK_START_TIME, 0);
	}
	
	
	public static void saveMainThemePath(Context context, String imgPath){
		SharedPreferences pref = context.getSharedPreferences(SETTING, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(MAIN_THEME_PATH, imgPath);
		editor.commit();
	}
	public static String getMainThemePath(Context context){
		SharedPreferences pref = context.getSharedPreferences(SETTING, 0);
		return pref.getString(MAIN_THEME_PATH, null);
	}
	
	public static void saveMainThemeLandscape(Context context, String imgPath){
		SharedPreferences pref = context.getSharedPreferences(SETTING, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(MAIN_THEME_LANDSCAPE, imgPath);
		editor.commit();
	}
	public static String getMainThemeLandscape(Context context){
		SharedPreferences pref = context.getSharedPreferences(SETTING, 0);
		return pref.getString(MAIN_THEME_LANDSCAPE, null);
	}
	
	public static void saveDetailThemePath(Context context, String imgPath){
		SharedPreferences pref = context.getSharedPreferences(SETTING, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(DETAIL_THEME_PATH, imgPath);
		editor.commit();
	}
	public static String getDetailThemePath(Context context){
		SharedPreferences pref = context.getSharedPreferences(SETTING, 0);
		return pref.getString(DETAIL_THEME_PATH, null);
	}

    public static boolean removeSetting(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(SETTING, 0);
        return pref.edit().remove(key).commit();
    }
	
	public static void saveBgColor(Context context, int row, int column, int color){
		SharedPreferences pref = context.getSharedPreferences(ITEM_BG_COLOR, 0);
		SharedPreferences.Editor editor = pref.edit();
		String key = "color_" + row + "_" + column;
		editor.putInt(key, color);
		editor.commit();
	}
	public static int getBgColor(Context context, int row, int column){
		SharedPreferences pref = context.getSharedPreferences(ITEM_BG_COLOR, 0);
		String key = "color_" + row + "_" + column;
		return pref.getInt(key, -1);
	}

    public static boolean removeBgColor(Context context, int row, int column) {
        SharedPreferences pref = context.getSharedPreferences(ITEM_BG_COLOR, 0);
        String key = "color_" + row + "_" + column;
        return pref.edit().remove(key).commit();
    }

	
	///////////////////
    public static boolean isTime2Open(Context context){
    	String channel = getUmengChannel(context);
    	if(channel == null || channel.equals("")) return false;
    	String openParams = MobclickAgent.getConfigParams(context, channel);
    	if(openParams != null && openParams.equalsIgnoreCase("on")){
    		return true;
    	}
    	else
    		return false;
    }
	
	public static String getUmengChannel(Context context){
		PackageManager pm = context.getPackageManager();
		Bundle bundle = null;
		ApplicationInfo appInfo;
		String channel = null;
		try {
			appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (appInfo != null && appInfo.metaData != null) {
				bundle = appInfo.metaData;
				if (bundle != null) {
					channel = bundle.getString("UMENG_CHANNEL");
					return channel;
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return channel;
	}

	
	/**
	 * hide the view with up to down animation
	 * @param view
	 */
	public void hideViewAnim(View view){
		TranslateAnimation up2Down = new TranslateAnimation(
				TranslateAnimation.ABSOLUTE, 0,
				TranslateAnimation.ABSOLUTE, 0, 
				TranslateAnimation.ABSOLUTE, 0,
				TranslateAnimation.RELATIVE_TO_PARENT, 1.0f
				);
		up2Down.setInterpolator(new AccelerateInterpolator());
		up2Down.setDuration(800);
		AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
		alpha.setDuration(700);
		AnimationSet anim = new AnimationSet(true);
		anim.addAnimation(up2Down);
		anim.addAnimation(alpha);
		view.startAnimation(anim);
		view.setVisibility(View.GONE);
	}

	/**
	 * 
	 * @param context
	 * @param titleName
	 * @param drawableId
	 * @param componentName must be ".XXXXActivity"
	 * @param isLancher
	 */
	public void createShortCut(Context context, String titleName, int drawableId, String componentName, boolean isLancher) {
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,titleName);
		Bitmap icon = BitmapFactory.decodeResource(context.getResources(), drawableId);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
		
		Intent respondIntent = new Intent(Intent.ACTION_MAIN);
		if(isLancher){
			respondIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		}
		else{
			respondIntent.addCategory(Intent.CATEGORY_DEFAULT);
		}
		respondIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		respondIntent.setComponent(new ComponentName(context.getPackageName(),
				context.getPackageName() + componentName));
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, respondIntent);
		
		shortcut.putExtra("duplicate", false);
		context.sendBroadcast(shortcut);
	}
	
//	public void createShortCut(Context context) {
//		Intent shortcut = new Intent(
//				"com.android.launcher.action.INSTALL_SHORTCUT");
//		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
//				context.getString(R.string.app_name));
//		Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
//				R.drawable.icon);
//		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
//
//		Intent respondIntent = new Intent(Intent.ACTION_MAIN);
//		respondIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//		respondIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//		respondIntent.setComponent(new ComponentName(context.getPackageName(),
//				context.getPackageName() + ".MainActivity"));
//		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, respondIntent);
//
//		shortcut.putExtra("duplicate", true);
//		context.sendBroadcast(shortcut);
//	}
}
