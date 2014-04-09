package org.szuwest.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.szuwest.lib.BaseApplication;
import org.szuwest.view.XLToast;

public class Util {
	
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dipToPixels(Context context, int dip) {
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
		return (int) px;
	}
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

    public static void showToast(String msg) {
        XLToast.showToast(BaseApplication.getInstance(), XLToast.XLToastType.XLTOAST_TYPE_NORMAL, msg);
    }

    public static void showToast(String msg, XLToast.XLToastType toastType) {
        XLToast.showToast(BaseApplication.getInstance(), toastType, msg);
    }

    public static void showAlertToast(String msg) {
        XLToast.showToast(BaseApplication.getInstance(), XLToast.XLToastType.XLTOAST_TYPE_ALARM, msg);
    }

    public static void showSuccessToast(String msg) {
        XLToast.showToast(BaseApplication.getInstance(), XLToast.XLToastType.XLTOAST_TYPE_SUC, msg);
    }

	/**
	 * 判断程序是否在前台.
	 */
	public static boolean isAppInForeground(Context context) {
		boolean result = false;
		String packageName = context.getPackageName();
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> task_list = activityManager.getRunningTasks(1);
		if (task_list.size() > 0) {
			if (task_list.get(0).topActivity.getPackageName().trim()
					.equals(packageName)) {
				result = true;
			}
		}
		return result;
	}
	
	public static void hiddenInput(Context ctx, View v) {
		InputMethodManager inputMethodManager = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	public static void showInput(Context ctx) {
		InputMethodManager inputMethodManager = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
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
	    public static boolean isImeShow(Context context){
	        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
	        return imm.isActive();
	    }
	
	/**
	 * wuwenhua 2012-11-30
	 * 通过读取vold.fstab，获取外置SD卡实际的路径
	 * 说明：
	 * Environment.getExternalStorageDirectory()获取到的SD卡路径为/mnt/sdcard，
	 * 但是对于外置SD卡，实际的系统路径可能为其他路径，如/mnt/extSdCard，
	 * 写文件的时候可以使用虚拟路径/mnt/sdcard，但是通过StatFs获取磁盘空间的时候，必须用实际路径/mnt/extSdCard
	 */	
    public static String GetExternalSdCardRealPath(){
    	
    	/***************************** 常量 *********************************/
    	final File VOLD_FSTAB = new File(Environment.getRootDirectory().getAbsoluteFile()
				+ File.separator
				+ "etc"
				+ File.separator
				+ "vold.fstab");
		final String HEAD        = "dev_mount";
//		final String LABEL       = "<label>";
//		final String MOUNT_POINT = "<mount_point>";
//		final String PATH 	     = "<part>";
//		final String SYSFS_PATH  = "<sysfs_path1...>";
		
		final int NLABEL       = 1; // Label for the volume	
		final int NPATH 	   = 2; // Partition
//		final int NMOUNT_POINT = 3; // Where the volume will be mounted
//		final int NSYSFS_PATH  = 4;
		/*******************************************************************/
		
		// 磁盘信息列表 
		ArrayList<String> voldList = new ArrayList<String>();

		// 外置SD卡的实际路径
    	String sdCardPath = "";
    	
    	// 获取磁盘列表信息，存到mVoldList
    	try {
    			voldList.clear();
	    		BufferedReader br = new BufferedReader(new FileReader(VOLD_FSTAB));
	    		String tmp = null;
	    		while ((tmp = br.readLine()) != null) {
	    			// the words startsWith "dev_mount" are the SD info
	    			if (tmp.startsWith(HEAD)) {
	    				voldList.add(tmp);
	    			}
	    		}
	    		br.close();
	    		voldList.trimToSize();    		
    		
    	} catch (IOException e){    	
    		;
    	}
    	
    	// 截取列表部分
    	for (int i = 0; i < voldList.size(); i++)
    	{
    		String[] sinfo = voldList.get(i).split(" ");
    		String label 		= sinfo[NLABEL];
//    		String mount_point  = sinfo[NMOUNT_POINT];
    		String path 		= sinfo[NPATH];
//    		String sysfs_path 	= sinfo[NSYSFS_PATH];

    		if (label.toLowerCase().contains("sdcard")) // 这个比较山寨
    		{
    			sdCardPath = path;
    			break;
    		}
    	}
   	  	
    	return sdCardPath;
    }
    
    public static long getAvailableExternalMemorySize() {

        //String path = GetExternalSdCardRealPath(); // 优选拉取SD卡的实际目录
		//if (path.equals(""))
        //{
        //    path = Environment.getExternalStorageDirectory().getPath();
        //}
        String path = Environment.getExternalStorageDirectory().getPath();
        
		StatFs stat = new StatFs(path);
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	public static long getTotalExternalMemorySize() {

        //String path = GetExternalSdCardRealPath(); // 优选拉取SD卡的实际目录
		//if (path.equals(""))
        //{
        //    path = Environment.getExternalStorageDirectory().getPath();
        //}
        String path = Environment.getExternalStorageDirectory().getPath();
        
		Environment.getExternalStorageState();
		StatFs stat = new StatFs(path);
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	public static long getFreeExternalMemorySize() {

        //String path = GetExternalSdCardRealPath(); // 优选拉取SD卡的实际目录
		//if (path.equals(""))
        //{
        //    path = Environment.getExternalStorageDirectory().getPath();
        //}
        String path = Environment.getExternalStorageDirectory().getPath();
        
		Environment.getExternalStorageState();
		StatFs stat = new StatFs(path);
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getFreeBlocks();
		return totalBlocks * blockSize;
	}
	
	public static void saveBitmap(Bitmap bitmap, String path, String name) {
		if (!path.endsWith("/")) {
			path += "/";
		}
		File file = new File(path + name);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();

			FileOutputStream out = null;
			out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
		}
		return null;
	}
	
	// 将 BASE64 编码的字符串 s 进行解码
	public static String getFromBase64(String s) {
		if (s == null)
			return null;
		byte[] bs = null;
		String str = null;
		try {
			bs = android.util.Base64.decode(s, android.util.Base64.DEFAULT);
			if (bs != null) {
				str = new String(bs);
			}
		} catch (Exception e) {

		}
		return str;
	}
	
	public static String md5(String key) {
		try {
			char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
					'a', 'b', 'c', 'd', 'e', 'f' };
			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			byte[] buf = key.getBytes();
			md.update(buf, 0, buf.length);
			byte[] bytes = md.digest();
			StringBuilder sb = new StringBuilder(32);
			for (byte b : bytes) {
				sb.append(hex[((b >> 4) & 0xF)]).append(hex[((b >> 0) & 0xF)]);
			}
			key = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return key;
	}
	
	 /**
	    * 将图片圆角化
	    * @param bitmap 需要圆角化的图片
	    * @param roundPx 圆角的像素大小， 值越大角度越明显
	    * @return 圆角化后的图片
	    */
		public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundPx) {
			if ( null == bitmap )
				return null;

			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
			// 得到画布
			Canvas canvas = new Canvas(output);

			// 将画布的四角圆化
			final int color = Color.RED;
			final Paint paint = new Paint();
			// 得到与图像相同大小的区域 由构造的四个值决定区域的位置以及大小
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			// drawRoundRect的第2,3个参数一样则画的是正圆的一角，如果数值不同则是椭圆的一角
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);

			return output;
		}
		
		public static boolean isNetWorkOn(Context context){
		     // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理） 
		    try { 
		        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		        if (connectivity != null) { 
		            // 获取网络连接管理的对象 
		            NetworkInfo info = connectivity.getActiveNetworkInfo(); 
		            if (info != null&& info.isConnected()) { 
		                // 判断当前网络是否已经连接 
		                if (info.getState() == NetworkInfo.State.CONNECTED) { 
		                    return true; 
		                } 
		            } 
		        } 
		    } catch (Exception e) {
		    } 
		    return false; 
		}
		
		public static boolean isChineseEnv(Context context){
			Configuration config = context.getResources().getConfiguration();
//			String country = config.locale.getCountry().toLowerCase();
			String language = config.locale.getLanguage();
//			LogUtils.d("env", "country=" + country + " language=" + language);
			if( language.equalsIgnoreCase(Locale.CHINESE.getLanguage())
					|| language.equalsIgnoreCase(Locale.CHINA.getLanguage())
					|| language.equalsIgnoreCase(Locale.SIMPLIFIED_CHINESE.getLanguage())
					|| language.equalsIgnoreCase(Locale.TRADITIONAL_CHINESE.getLanguage()) )
				return true;
//			if( country.contains("cn")|| country.contains("tw") )
//					return true;
			return false;
		}
}
