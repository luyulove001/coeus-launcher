package net.tatans.coeus.launcher.util;

import java.util.HashMap;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.network.tools.TatansLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;

/**
 * @author Yuliang
 * @time 2015/3/25
 */
public class ShakeUtils implements SensorEventListener {
	// 两次检测的相隔时间
	private static final int UPDATE_INTERVAL_TIME = 500;
	// 上次检测时间
	private long lastUpdateTime;
	// 时间间隔
	private Context mContext;
	private long timeInterval;
	@SuppressLint("UseSparseArrays")
	public ShakeUtils(Context context) {
		mContext=context;
		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
	}

	public void setOnShakeListener(OnShakeListener onShakeListener) {
		mOnShakeListener = onShakeListener;
	}

	public void onResume() {
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void onPause() {
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// 检查两次调用时间间隔
		if (checkInterval()) {
			return;
		}
		int sensorType = event.sensor.getType();
		// values[0]:X轴，values[1]：Y轴，values[2]：Z轴
		float[] values = event.values;
		if (sensorType == Sensor.TYPE_ACCELEROMETER) {
			// 这里可以调节摇一摇的灵敏度
			if ((Math.abs(values[0]) > SENSOR_VALUE
					|| Math.abs(values[1]) > SENSOR_VALUE || Math
					.abs(values[2]) > SENSOR_VALUE*2)) {
				if (null != mOnShakeListener) {
					mOnShakeListener.onShake();
				}
			}
		}
	}

	public interface OnShakeListener {
		public void onShake();
	}

	/**
	 * 检测两次调用onShake方法时间间隔
	 * 
	 * @return
	 */
	public boolean checkInterval() {
		// 现在检测时间
		long currentUpdateTime = System.currentTimeMillis();
		// 两次时间间隔
		timeInterval = currentUpdateTime - lastUpdateTime;
		// 判断是否到了时间间隔
		if (timeInterval < UPDATE_INTERVAL_TIME) {
			return true;
		}
		// 现在的时间变成上次时间
		lastUpdateTime = currentUpdateTime;
		return false;
	}


	private SensorManager mSensorManager = null;
	private OnShakeListener mOnShakeListener = null;
	private static final int SENSOR_VALUE = 9;
}
