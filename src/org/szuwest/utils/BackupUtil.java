package org.szuwest.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import my.base.model.AppInfo;
import my.base.util.FileUtils;

public class BackupUtil {
	
	public static boolean copy(boolean isSysApp, AppInfo appInfo){
		if( !FileUtils.hasSd()) return false;
		String fileName = "";
		int ret = 0;
		if(isSysApp){
			if(appInfo.getAppFile().getName().equals("pkg.apk"))//升级过的系统程序并且升级包安装在SD卡里
				fileName = "/sdcard/uninstall/systemApps/" + appInfo.getAppName();
			else
				fileName = "/sdcard/uninstall/systemApps/" + appInfo.getAppFile().getName();
			ret = CopySdcardFile(appInfo.getAppFile().getAbsolutePath(), fileName);
			if(ret != 0) return false;
			String odexName = appInfo.getAppFile().getAbsolutePath().replace(".apk", ".odex");
			File odexFile = new File(odexName);
			if(odexFile.exists()){
				fileName = fileName.replace(".apk", ".odex");
				ret = CopySdcardFile(odexFile.getAbsolutePath(), fileName);
			}
		}else{
			fileName = "/sdcard/uninstall/userApps/" + appInfo.getAppName() + ".apk";
			ret = CopySdcardFile(appInfo.getAppFile().getAbsolutePath(), fileName);
		}
		if(ret == 0) return true;
		else return false;
	}
	
//	public static boolean restoreSysApp(File file){
//		String toDir = "/system/app/";
//		String toApkFile = toDir + file.getName();
//		int ret = CopySdcardFile(file.getAbsolutePath(), toApkFile);
//		String odexFile = file.getAbsolutePath().replace(".apk", ".odex");
//		if(new File(odexFile).exists() && ret==0)
//			ret = CopySdcardFile(odexFile, toApkFile.replace(".apk", ".odex"));
//		if(ret == 0) {
//			UninstallActivity.shellCmd.su.runWaitFor("chmod 0777 " + toApkFile);
//			UninstallActivity.shellCmd.su.runWaitFor("chmod 0777 " + toApkFile.replace(".apk", ".odex"));
//			return true;
//		}
//		else return false;
//	}
	
	public static void restoreUserApp(File pkgFile, Context context){
		SoftWareUtils.installFromIntent(pkgFile, context, null);
	}
	
	public static boolean isBacked(boolean isSysApp, AppInfo appInfo){
		String fileName ="";
		if(isSysApp){
			if(appInfo.getAppFile().getName().equals("pkg.apk"))//升级过的系统程序并且升级包安装在SD卡里
				fileName = "/sdcard/uninstall/systemApps/" + appInfo.getAppName();
			else
				fileName = "/sdcard/uninstall/systemApps/" + appInfo.getAppFile().getName();
		}
		else
			fileName = "/sdcard/uninstall/userApps/" + appInfo.getAppName() + ".apk";
		File file = new File(fileName);
		if(file.exists()) return true;
		else return false;
	}
	
	public static int copy(String fromFile, String toFile) {
		// 要复制的文件目录
		File[] currentFiles;
		File root = new File(fromFile);
		// 如同判断SD卡是否存在或者文件是否存在
		// 如果不存在则 return出去
		if (!root.exists()) {
			return -1;
		}
		// 如果存在则获取当前目录下的全部文件 填充数组
		currentFiles = root.listFiles();

		// 目标目录
		File targetDir = new File(toFile);
		// 创建目录
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		// 遍历要复制该目录下的全部文件
		for (int i = 0; i < currentFiles.length; i++) {
			if (currentFiles[i].isDirectory())// 如果当前项为子目录 进行递归
			{
				copy(currentFiles[i].getPath() + "/",
						toFile + currentFiles[i].getName() + "/");

			} else// 如果当前项为文件则进行文件拷贝
			{
				CopySdcardFile(currentFiles[i].getPath(), toFile
						+ currentFiles[i].getName());
			}
		}
		return 0;
	}
	
	//文件拷贝
	//要复制的目录下的所有非子目录(文件夹)文件拷贝
	public static int CopySdcardFile(String fromFile, String toFile) {
		try {
			InputStream fosfrom = new FileInputStream(fromFile);
			OutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
			return 0;

		} catch (Exception ex) {
			return -1;
		}
	}
	
//	public static void recordLog(Context context, boolean isSysApp,
//			AppInfo appInfo, boolean isSuccessful){
//		if(appInfo == null) return;
//		Intent intent = new Intent(context, UninstallLogService.class);
//		String apkfileName = "";
//		if(isSysApp){
//			if(appInfo.getAppFile().getName().equals("pkg.apk"))//升级过的系统程序并且升级包安装在SD卡里
//				apkfileName = appInfo.getAppName();
//			else
//				apkfileName = appInfo.getAppFile().getName();
//		}else
//			apkfileName = appInfo.getAppName() + ".apk";
//		
//		intent.putExtra(uLog.ISSYSAPP, isSysApp);
//		intent.putExtra(uLog.APKSIZE, appInfo.getSize());
//		intent.putExtra(uLog.APKFILE, apkfileName);
//		intent.putExtra(uLog.APPNAME, appInfo.getAppName());
//		intent.putExtra(uLog.PKGNAME, appInfo.getPackageName());
//		String result = isSuccessful ? 
//				context.getString(R.string.uninstall_success):
//					context.getString(R.string.uninstall_fail) ;
//		intent.putExtra(uLog.RESULT, result);
//		intent.putExtra(uLog.APPVERSION,appInfo.getVersionName());
//		context.startService(intent);
//	}
//	
//	public static void recordLog2(Context context, boolean isSysApp,
//			AppInfo appInfo, boolean isSuccessful){
//		
//		ContentValues values = new ContentValues();
//		values.clear();
//		
//		String apkfileName = "";
//		if(isSysApp){
//			if(appInfo.getAppFile().getName().equals("pkg.apk"))//升级过的系统程序并且升级包安装在SD卡里
//				apkfileName = appInfo.getAppName();
//			else
//				apkfileName = appInfo.getAppFile().getName();
//		}else
//			apkfileName = appInfo.getAppName() + ".apk";
//		
//		long now = Long.valueOf(System.currentTimeMillis());
//		values.put(uLog.APPNAME, appInfo.getAppName());
//		values.put(uLog.APKSIZE, appInfo.getSize());
//		values.put(uLog.APKFILE, apkfileName);
//		values.put(uLog.PKGNAME, appInfo.getPackageName());
//		values.put(uLog.ISSYSAPP, isSysApp?1:0);
//		values.put(uLog.REMOVETIME, now);
//		values.put(uLog.APPVERSION,appInfo.getVersionName());
//		String result = isSuccessful ? 
//				context.getString(R.string.uninstall_success):
//					context.getString(R.string.uninstall_fail) ;
//		values.put(uLog.RESULT,result);
//		
//		context.getContentResolver().insert(uLog.CONTENT_URI, values);
//	}

}
