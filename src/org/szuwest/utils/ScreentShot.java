package org.szuwest.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import my.base.util.LogUtils;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

public class ScreentShot {

	private final static String TAG = "ActivityScreentShot";
	public final static String SAVEIMGDIR = "/sdcard/screenshot/";
	
	// 获取指定Activity的截屏，保存到png文件
	public static Bitmap takeScreenShot(Activity activity) {

		// View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1;
		try {
			b1 = view.getDrawingCache();
		} catch(OutOfMemoryError e) {
			return null;
		}

		// 获取状态栏高度
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		LogUtils.d(TAG, statusBarHeight+"");

		// 获取屏幕长和高
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay().getHeight();

		// 去掉标题栏
		Bitmap b = null;
		try {
			b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
		} catch (Exception e) {
			LogUtils.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		view.destroyDrawingCache();
		return b;
	}


    /**
     * 把一个View的对象转换成bitmap
     */
	public static Bitmap getViewBitmap(View v) {
    	
        v.clearFocus();
        v.setPressed(false);

        //能画缓存就返回false
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false); 
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            LogUtils.e(TAG, "failed getViewBitmap(" + v + ")");
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }


	
	// 保存到sdcard
	/**
	 * @param strFileName /sdcard/xxx.png
	 */
	public static boolean savePic(Bitmap b, String strFileName) {
		if(b==null)return false;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(strFileName);
			if (null != fos) {
				b.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
				return true;
			}
		} catch (FileNotFoundException e) {
			LogUtils.e(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LogUtils.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	// 程序入口
	public static boolean activityShot(Activity activity) {
		String picName = getPicName(activity);
		Bitmap b1 = takeScreenShot(activity);
		if(b1 == null) return false;
		boolean flag = savePic(b1,picName);
//		if(flag){
//			if(AccelerometerListener.closeSound)
//				Toast.makeText(activity, activity.getString(R.string.shot_success)+picName, Toast.LENGTH_LONG).show();
//			LogUtils.e(TAG, "截图成功!图片保存路径为:"+picName);
//		}
		return flag;
	}
	
//	public static boolean rootScreenShot(Context context){
//		String gsnap = context.getDir("bin", Context.MODE_PRIVATE).getAbsolutePath() + "/shot";
//		String dev = "/dev/graphics/fb0";
//		String picName = getPicName(null);
//		ToolBoxApplication app = (ToolBoxApplication) ((CapScreenService)context).getApplication();
//		CommandResult result = app.getShellCmd().su.runWaitFor(gsnap + " " + picName + " " + dev);
//		boolean flag = result.success();
//		if(flag){
//			if(AccelerometerListener.closeSound)
//				Toast.makeText(context, context.getString(R.string.shot_success)+picName, Toast.LENGTH_LONG).show();
//			LogUtils.e(TAG, "截图成功!图片保存路径为:"+picName);
//		}
//		return flag;
//	}

	private static String getPicName(Activity activity){
		String picName = "";
		SimpleDateFormat formatter;
		if(activity != null){
			picName = activity.getTitle().toString();
			formatter = new SimpleDateFormat("MMdd_hhmmss");
		}
		else {
			picName = "shot";
			formatter = new SimpleDateFormat("yyyyMMdd_hhmmss");
		}
		Date currentTime_1 = new Date(); 
		String dateString = formatter.format(currentTime_1); 
		File file = new File(SAVEIMGDIR);
		boolean flag = file.exists();
		if( !flag )
			flag = file.mkdirs();
		if(activity != null)
			picName = SAVEIMGDIR + picName + dateString+".png";
		else
			picName = SAVEIMGDIR + picName + dateString+".jpg";
		return picName;
	}
}