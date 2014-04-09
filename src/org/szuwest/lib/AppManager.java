package org.szuwest.lib;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Process;


import java.util.Stack;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 */
public class AppManager {

	private Stack<Activity> activityStack;
	private static AppManager instance = null;
	private Handler globalHandler = new Handler();
	private Activity currentActivity;

	private AppManager() {
        activityStack = new Stack<Activity>();
    }

	/**
	 * 单一实例
	 */
	public static AppManager getInstance() {
		if (instance == null) {
			synchronized (AppManager.class) {
				instance = new AppManager();
			}
		}
		return instance;
	}

	public Handler getGlobalHandler() {
		return globalHandler;
	}

    public int size() {
        return activityStack.size();
    }

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		activityStack.add(activity);
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity() {
//		Activity activity = activityStack.lastElement();
//		return activity;
		return currentActivity;
	}
	
	public void setCurrentActivity(Activity activity) {
		currentActivity = activity;
	}

	/**
	 * 移除当前Activity（堆栈中最后一个压入的）
	 */
	public void removeActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
		}
	}
	
	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
                break;
			}
		}
	}

    public Activity getActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}
	
	/**
	 * 结束除exceptActivity之外的所有Activities
	 */
	public void finishOtherActivities(Activity exceptActivity) {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			Activity activity = activityStack.get(i);
			if (null != activity && exceptActivity != activity && !exceptActivity.equals(activity)) {
				activity.finish();
			}
		}
	}

    /**
     * 结束除Class<?> cls之外的所有Activities
     */
    public void finishOtherActivities(Class<?> cls) {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            Activity activity = activityStack.get(i);
            if (null != activity && !(activity.getClass().equals(cls)) ) {
                activity.finish();
            }
        }
    }

	/**
	 * 退出应用程序
	 */
	@SuppressWarnings("deprecation")
	public void AppExit(final Context context) {
		try {
			finishAllActivity();
//            MobclickAgent.onKillProcess(context);
            globalHandler.postDelayed(new Runnable() {//延迟强杀进程，避免界面闪退
                @Override
                public void run() {
                    // 强杀进程

                    Process.killProcess(Process.myPid());
                    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
//                        context.stopService(new Intent(context, ProtectService.class));
//                        activityManager.killBackgroundProcesses("com.xiaoenai.app:daemon");
//                        activityManager.killBackgroundProcesses("com.xiaoenai.app:tools");
                    }
                    System.exit(0);
                }
            },200);
		} catch (Exception e) {
		}
	}
}