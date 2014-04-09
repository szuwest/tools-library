package org.szuwest.lib;

import android.app.Application;
import android.os.Handler;

import com.mobclick.android.MobclickAgent;

import my.base.util.LogUtils;

/**
 * 全局应用程序基类
 * Created by szuwest on 13-10-1.
 */
public class BaseApplication extends Application {

    private static BaseApplication instance;
    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MobclickAgent.onError(this);
        LogUtils.enableLogging(true);
        mHandler = new Handler();
    }

    public void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    public void postDelay(Runnable runnable, long delay) {
        mHandler.postDelayed(runnable, delay);
    }

    public Handler getHandler() {
       return mHandler;
    }

    public static BaseApplication getInstance() {
        return instance;
    }
}
