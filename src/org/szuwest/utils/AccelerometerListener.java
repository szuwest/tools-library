package org.szuwest.utils;

import my.base.util.LogUtils;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;

public class AccelerometerListener implements SensorEventListener {
	/**
	 * 摇晃检测阈值，决定了对摇晃的敏感程度，越小越敏感。
	 */
	public int shakeThreshold = 800;
	// private static final int FORCE_THRESHOLD = 350;
	/**
	 * 摇晃超时时间,必须在此时间内完成一次摇晃截图
	 */
	private static final int SHAKE_TIMEOUT = 500;
	/**
	 * 连续两次截图时间间隔
	 */
	private static final int SHAKE_DURATION = 1000;
	/**
	 * 检测的时间间隔常数
	 */
	private static final int TIME_THRESHOLD = 100;
	/**
	 * 上次检测时间
	 */
	private long mLastTime;
	/**
	 * 符合要求的摇晃的次数
	 */
	private int mShakeCount = 0;
	private static final int SHAKE_COUNT = 3;
	/**
	 * 上次一截图时间，符合要求的摇晃必须发生足够次数才会进行截图
	 */
	private long mLastShake;
	/**
	 * 上一次符合要求的摇晃的发生时间
	 */
	private long mLastForce;

	private float lastXValue = 0;
	private float lastYValue = 0;
	private float lastZValue = 0;

	private Context context;
	private SensorManager sensorManager;
	protected boolean isRegisterListener = false;
	private SensorNotifier sensorNotifier;

	public SensorNotifier getSensorNotifier() {
		return sensorNotifier;
	}

	public void setSensorNotifier(SensorNotifier sensorNotifier) {
		this.sensorNotifier = sensorNotifier;
	}

	public AccelerometerListener(Context context) {
		this.context = context;
		sensorManager = (SensorManager) this.context
				.getSystemService(Context.SENSOR_SERVICE);
	}

	
	@Override
	public void onSensorChanged(SensorEvent event) {
		long currentTime = System.currentTimeMillis();
		if ((currentTime - mLastForce) > SHAKE_TIMEOUT) {
			mShakeCount = 0;
		}
		if (currentTime - mLastTime < TIME_THRESHOLD)
			return;
		long diff = currentTime - mLastTime;

		float deltaX = event.values[0] - lastXValue;
		float deltaY = event.values[1] - lastYValue;
		float deltaZ = event.values[2] - lastZValue;
		// 另一种版本是 delta = Math.abs(deltaX+deltaY+deltaZ) / updateTime * 10000;
		// 下面这种比较精确
		float delta = FloatMath.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
				* deltaZ)
				/ diff * 10000;
		if (delta > shakeThreshold) {
			if ((++mShakeCount >= SHAKE_COUNT)
					&& (currentTime - mLastShake > SHAKE_DURATION)) {
				mLastShake = currentTime;
				mShakeCount = 0;
				
				if(sensorNotifier != null)
					sensorNotifier.doWhenSensorChg();
			}
			mLastForce = currentTime;
		}
//		String accelerometer = " lastXValue=" + lastXValue + " X="
//				+ event.values[0] + "\n" + "lastYValue=" + lastYValue + " Y="
//				+ event.values[1] + "\n" + "lastZValue=" + lastZValue + " Z="
//				+ event.values[2] + "\n";
//		LogUtils.v("AccelerometerListener", "delta=" + delta + accelerometer);
		mLastTime = currentTime;
		lastXValue = event.values[0];
		lastYValue = event.values[1];
		lastZValue = event.values[2];

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	public boolean registerListener() {
		if (!isRegisterListener)
			isRegisterListener = sensorManager.registerListener(this,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_GAME);
		LogUtils.v("AccelerometerListener", "registerListener="
				+ isRegisterListener);
		return isRegisterListener;
	}

	public void unregisterListener() {
		if (isRegisterListener) {
			sensorManager.unregisterListener(this,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
			isRegisterListener = false;
		}
		LogUtils.v("AccelerometerListener", "unregisterListener");
	}

	public interface SensorNotifier{
		public void doWhenSensorChg();
	}
}
