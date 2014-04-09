package org.szuwest.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import my.base.model.AppInfo;
import my.base.util.LogUtils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;

public class SoftWareUtils {
	private final static String TAG = "SoftWareUtils";
	
	public static void installFromIntent(File pkgFile, Context context, Handler handler){
		if( !pkgFile.exists()) return ;
		Uri uri = Uri.fromFile(pkgFile);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		try {
			context.startActivity(intent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void uninstallFromIntent(Context context, String pkgName, Handler handler){
		Uri packageURI = Uri.parse("package:" + pkgName);
		Intent intent = new Intent(Intent.ACTION_DELETE,packageURI);
		try {
			context.startActivity(intent);
		} catch (Exception e) {
			handler.sendMessage(handler.obtainMessage(0, "error"));
			e.printStackTrace();
		}
	}
	
	/**
	 * 须root权限，静默安装（貌似只限于用户软件）
	 * @param apkPath
	 * @return
	 */
	public static boolean installFromPm(String apkPath, ShellCommand shell){
		String cmd = "pm install " + apkPath;
		LogUtils.e(TAG, cmd);
		boolean flag = false;
		try {
			ShellCommand.CommandResult result = shell.su.runWaitFor(cmd);
			flag = result.success();
		} catch (RuntimeException e) {
			flag = false;
			LogUtils.e(TAG, cmd + "failed");
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 须root权限，静默卸载（貌似只限于用户软件）
	 * @param packageName
	 * @return
	 */
	public static boolean uninstallFromPm(String packageName, ShellCommand shell){
		String cmd = "pm uninstall " + packageName;
		LogUtils.e(TAG, cmd);
		boolean flag = false;
		try {
			ShellCommand.CommandResult result = shell.su.runWaitFor(cmd);
			flag = result.success();
		} catch (RuntimeException e) {
			flag = false;
			LogUtils.e(TAG, cmd + "failed");
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 须root权限，可删除任何应用程序
	 * @param apkpath
	 * @return
	 */
	public static boolean uninstallFromRm(Context context, String packageName, String apkpath, ShellCommand shell) {
		String rmApkCmd = "rm -f " + "\"" + apkpath + "\"";//加引号是因为有些文件名带有特殊符号
		String rmOdexCmd = "rm -f " + "\"" + apkpath.replace("apk", "odex") + "\"";
		String pmPkgCmd = "pm uninstall " + packageName;
		LogUtils.d(TAG, rmApkCmd);
		LogUtils.d(TAG, rmOdexCmd);
		/**删除流程
		 * 1.删除apk文件，只要把apk文件删除了，该软件就不能用了
		 * 2.删除odex文件（如果有的话）
		 * 3.前两步成功之后，就可以采用普通方式(调用uninstallFromIntent方法)
		 *    或静默方式(命令行方式)卸载该软件，清除软件的数据.这里采用静默卸载方式
		 * 4.删除数据文件(如/data/dalvik-cache/下的文件)
		 */
		boolean flag = false;
		String apk_path = apkpath;
		File file = new File(apk_path);
		if( !file.exists() ) return true;
		String fileName = file.getName();
		//先调用API删除
		try {
			//如果apk文件不是放在/system/app下，说明该包是升级包，要先把该包删掉，然后再删除/system/app下的包
			if( !apk_path.startsWith("/system/app") ){
				if(file.canWrite())
					flag = file.delete();
				else{
					shell.su.runWaitFor("chmod 0777 /data/app");
					shell.su.runWaitFor("chmod 0777 /mnt/asec");
					flag = shell.su.runWaitFor(rmApkCmd).success();
				}
				shell.su.runWaitFor(pmPkgCmd);
				apk_path = context.getPackageManager().
					getPackageInfo(packageName, 0).applicationInfo.sourceDir;
				file = new File(apk_path);
				rmApkCmd = "rm -f " + "\"" + apk_path + "\"";//加引号是因为有些文件名带有特殊符号
				rmOdexCmd = "rm -f " + "\"" + apk_path.replace("apk", "odex") + "\"";
			}
			
			flag = file.delete();
			if( !flag )//有些调用API是删除不了的,只能通过命令行删除
				flag = shell.su.runWaitFor(rmApkCmd).success();
			file = new File(apk_path.replace("apk", "odex"));
			if(file.exists())
				if(!file.delete())
					shell.su.runWaitFor(rmOdexCmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//删除成功
		if(flag){
			String rmDataPkgCmd = "rm -r /data/data/" + packageName;
			String rmDataDalvikCmd = "rm -r /data/dalvik-cache/system@app@" + fileName + "@classes.dex";
			
			shell.su.runWaitFor(pmPkgCmd);
			File pkgFile = new File("/data/data/" + packageName);
			if(pkgFile.exists()){
				shell.su.runWaitFor("chmod 0777 /data/data/" + packageName);
				rmDataPkgCmd = "rm -r " + pkgFile.getAbsolutePath();
				LogUtils.d(TAG, rmDataPkgCmd);
				shell.su.runWaitFor(rmDataPkgCmd);
			}
			else
				LogUtils.e(TAG, "/data/data/" + packageName +" 已删除");
			
			shell.su.runWaitFor(rmDataDalvikCmd);
			LogUtils.d(TAG, pmPkgCmd);
			LogUtils.d(TAG, rmDataPkgCmd);
			LogUtils.d(TAG, rmDataDalvikCmd);
		}
		return flag;
	}
	
	public static String getSysAppPath(Context context, String packageName, 
			String apkpath, ShellCommand shell){
		String rmApkCmd = "rm -f " + "\"" + apkpath + "\"";//加引号是因为有些文件名带有特殊符号
		String pmPkgCmd = "pm uninstall " + packageName;
		String apk_path = apkpath;
		File file = new File(apk_path);
		//先调用API删除
		try {
			if(file.canWrite())
				file.delete();
			else{
				shell.su.runWaitFor("chmod 0777 /data/app");
				shell.su.runWaitFor("chmod 0777 /mnt/asec");
				shell.su.runWaitFor(rmApkCmd).success();
			}
			shell.su.runWaitFor(pmPkgCmd);
			apk_path = context.getPackageManager().
				getPackageInfo(packageName, 0).applicationInfo.sourceDir;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return apk_path==null ? apkpath : apk_path ;
	}
	
	public static String getSysAppAPath(Context context, String pkgName){
		 File sysFile = new File("/system/app");
		 PackageManager pm = context.getPackageManager();
		 File[] files = sysFile.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String filename) {
				filename = filename.toLowerCase(); 
		        return filename.endsWith("apk");
			}});
//		 String[] filesName = sysFile.list(new FilenameFilter(){
//			@Override
//			public boolean accept(File dir, String filename) {
//				filename = filename.toLowerCase(); 
//				return filename.endsWith("apk");
//			}});
		 PackageInfo pkgInfo;
		 for(int i=0; i<files.length; i++){
			 pkgInfo = pm.getPackageArchiveInfo(files[i].getAbsolutePath(), 0);
			 if( pkgInfo!=null && pkgInfo.packageName.equals(pkgName))
				 return files[i].getAbsolutePath();
		 }
		return null;
	}
	
	public static AppInfo getPkgAppInfo(Context context, String pkgName){
		PackageInfo packageInfo = null;
		PackageManager pm = context.getPackageManager();
		try {
			packageInfo = pm.getPackageInfo(pkgName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		AppInfo appInfo = new AppInfo();
		String appName = pm.getApplicationLabel(packageInfo.applicationInfo).toString().trim();
        appInfo.setAppName(appName);
        appInfo.setPackageName(packageInfo.packageName);
        String sourceDir = packageInfo.applicationInfo.sourceDir;
        int index = sourceDir.lastIndexOf("/");
        if (index >= 0) {
            appInfo.setInstallPath(sourceDir.substring(0, index + 1));
            appInfo.setFileName(sourceDir.substring(index + 1));
        } else {
        	appInfo.setFileName(sourceDir);
        }
        File file = new File(sourceDir);  
        if (file.exists()) {
        	appInfo.setAppFile(file);
        	appInfo.setSize(file.length());
        }
        appInfo.setVersionName(packageInfo.versionName);
        appInfo.setVersionCode(packageInfo.versionCode);
        appInfo.setIcon(packageInfo.applicationInfo.loadIcon(pm));
		return appInfo;
	}	
	
	public static void getInstalldAppInfo(Context context, 
			List<AppInfo> tmp_userApps, List<AppInfo> tmp_sysApps){
		tmp_sysApps.clear();
		tmp_userApps.clear();
		List<PackageInfo> allPkgs = new ArrayList<PackageInfo>();
		PackageManager pm = context.getPackageManager();
		allPkgs = pm.getInstalledPackages(0);
		int size = allPkgs.size();
		for(int i=0; i< size; i++){
			PackageInfo pkgInfo = allPkgs.get(i); 
			if(pkgInfo.packageName.equalsIgnoreCase(context.getPackageName()))//去掉自己
				continue;
			AppInfo appInfo = new AppInfo();
			String sourceDir = pkgInfo.applicationInfo.sourceDir;
			File file = new File(sourceDir);  
			if (file.exists()) {
				appInfo.setAppFile(file);
				appInfo.setSize(file.length());
			}
			else
				continue;//安装文件已经不存在，已被删除。舍弃
			String appName = pm.getApplicationLabel(pkgInfo.applicationInfo).toString().trim();
            if(appName==null || appName=="")appInfo.setAppName(pkgInfo.packageName);
			appInfo.setAppName(appName);
            appInfo.setPackageName(pkgInfo.packageName);
            int index = sourceDir.lastIndexOf("/");
            if (index >= 0) {
                appInfo.setInstallPath(sourceDir.substring(0, index + 1));
                appInfo.setFileName(sourceDir.substring(index + 1));
            } else {
            	appInfo.setFileName(sourceDir);
            }
            appInfo.setVersionName(pkgInfo.versionName);
            appInfo.setVersionCode(pkgInfo.versionCode);
            appInfo.setIcon(pkgInfo.applicationInfo.loadIcon(pm));
            if((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0){
            	tmp_sysApps.add(appInfo);
			}
			else{
				tmp_userApps.add(appInfo);
			}
		}
		LogUtils.d(TAG, "sys=" + tmp_sysApps.size() + " user=" + tmp_userApps.size());
	}
	
    /**
	 * @param long型值
	 * @return 参数转为数值+MB/KB
	 */
	public static String getFileSizeReadable(long size) {

		if (size <= -1)
			return "0";
		DecimalFormat df = new DecimalFormat("###.##");
		long KB = 1024;
		long MB = 1024 * 1024;
		float f;
		if (size < MB) {
			f = (float) ((float) size / (float) KB);
			return (df.format(new Float(f).doubleValue()) + "KB");
		} else {
			f = (float) ((float) size / (float) (MB));
			return (df.format(new Float(f).doubleValue()) + "MB");
		}
	}
}
