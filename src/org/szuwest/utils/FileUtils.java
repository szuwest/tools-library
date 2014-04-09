package org.szuwest.utils;

import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * SDcard文件操作
 */
public class FileUtils {

	public static final String CACHE_BASE_PATH = Environment.getExternalStorageDirectory() + File.separator + ".com.szuwest";
	public static String PHOTOALBUM_PATH;

	public static final String IMAGE_BASE_PATH = CACHE_BASE_PATH + File.separator + ".image";
	public static final String VOICE_BASE_PATH = CACHE_BASE_PATH + File.separator + ".voice";
	
	public final static String HTTP_CACHE_DIR 		 = "HttpCache";
	
	static {
		try {
            if (Build.VERSION.SDK_INT >= 8) {
                PHOTOALBUM_PATH = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "szuwest" + File.separator;
            } else {
                PHOTOALBUM_PATH = Environment.getExternalStorageDirectory() + File.separator + "DCIM/szuwest" + File.separator;
            }
			File path = new File(CACHE_BASE_PATH);
			if (!path.exists()) {
				path.mkdirs();
			}
            path = new File(PHOTOALBUM_PATH);
            if (!path.exists()) {
                path.mkdirs();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getPathForTempImage(String fileName) {
		return IMAGE_BASE_PATH + File.separator + fileName;
	}

	public static String creatFileForTempImage(String fileName) {
		try {

			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File imageCacheDir = new File(IMAGE_BASE_PATH);
                if (!imageCacheDir.exists()) imageCacheDir.mkdirs();
				File file = new File(getPathForTempImage(fileName));
				if (file.exists()) {
					file.delete();
				}
				return file.getPath();
			} else {
//				File file = new File(Xiaoenai.getInstance().getCacheDir(), fileName);
//				if (file.exists()) {
//					file.delete();
//				}
//				return file.getPath();
            return "";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param ASSETS_NAME
	 *            要复制的文件名
	 * @param savePath
	 *            要保存的路径
	 * @param saveName
	 *            复制后的文件名
	 */

	public static String copyFromAssets(String ASSETS_NAME, String savePath, String saveName) {
		String filename = savePath + "/" + saveName;
		File dir = new File(savePath);
		// 如果目录不中存在，创建这个目录
		if (!dir.exists())
			dir.mkdir();
		try {
			if (!(new File(filename)).exists()) {
//				InputStream is = Xiaoenai.getInstance().getResources().getAssets().open(ASSETS_NAME);
//				FileOutputStream fos = new FileOutputStream(filename);
//				byte[] buffer = new byte[7168];
//				int count = 0;
//				while ((count = is.read(buffer)) > 0) {
//					fos.write(buffer, 0, count);
//				}
//				fos.close();
//				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filename;
	}
	
	public static String getHttpCacheDir(){
		String cacheRootDir = "";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			cacheRootDir = CACHE_BASE_PATH ;
		}else{
//			cacheRootDir = Xiaoenai.getInstance().getCacheDir().getAbsolutePath();
		}
		String cacheDir = cacheRootDir + File.separator + HTTP_CACHE_DIR;
		ensureDir(cacheDir);
		return cacheDir;
	}
	
	public static boolean ensureDir(String path) {
		if (null == path) {
			return false;
		}

		boolean ret = false;

		File file = new File(path);
		if (!file.exists() || !file.isDirectory()) {
			try {
				ret = file.mkdirs();
			} catch (SecurityException se) {
				se.printStackTrace();
			}
		}

		return ret;
	}
	
	/**
	 * 递归删除文件夹
	 */		
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success){
					return false;
				}
			}
		}
		return dir.delete(); // The directory is now empty so now it can be smoked
	}
	
	/**
	 * 注销登陆的时候清空缓存目录下的所有文件
	 */
	public static void clearCacheData() {
		
		File imgDir = new File(IMAGE_BASE_PATH);
		if (imgDir.exists() && imgDir.isDirectory()) {
			File[] files = imgDir.listFiles();
			for (File file : files) {
				file.delete();
			}
		}
		File voiceDir = new File(VOICE_BASE_PATH);
		if (voiceDir.exists() && voiceDir.isDirectory()) {
			File[] files = voiceDir.listFiles();
			for (File file : files) {
				file.delete();
			}
		}
	}

    public static int copy(String fromFile, String toFile) {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if (!root.exists()) {
            return -1;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();

        //目标目录
        File targetDir = new File(toFile);
        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        //遍历要复制该目录下的全部文件
        for (int i = 0; i < currentFiles.length; i++) {
            if (currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
            {
                copy(currentFiles[i].getPath() + "/", toFile + currentFiles[i].getName() + "/");

            } else//如果当前项为文件则进行文件拷贝
            {
                copySdcardFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
            }
        }
        return 0;
    }


    //文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    public static int copySdcardFile(String fromFile, String toFile) {
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
}