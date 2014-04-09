package org.szuwest.utils;


import android.os.Handler;
import android.os.Looper;

public class LooperThread extends Thread {
	
	public static class LooperThreadListener {
		
		public void onFinished() {}
		
		public void onFinished(Object obj) {}
		
		public void onFinished(Object[] obj) {}
		
		public void postFinishedOnMainThread() {
//			Xiaoenai.getInstance().postMainHandler(new Runnable() {
//
//				@Override
//				public void run() {
//					onFinished();
//				}
//			});
//		}
//
//		public void postFinishedOnMainThread(final Object obj) {
//			Xiaoenai.getInstance().postMainHandler(new Runnable() {
//
//				@Override
//				public void run() {
//					onFinished(obj);
//				}
//			});
//		}
//
//		public void postFinishedOnMainThread(final Object[] obj) {
//			Xiaoenai.getInstance().postMainHandler(new Runnable() {
//
//				@Override
//				public void run() {
//					onFinished(obj);
//				}
//			});
		}
	}
	
	private Handler handler = null;

	public Handler getHandler() {
		return handler;
	}

	@Override
	public void run() {
		Looper.prepare();
		handler = new Handler();
		Looper.loop();
	}
	
}