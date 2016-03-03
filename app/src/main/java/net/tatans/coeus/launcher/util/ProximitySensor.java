package net.tatans.coeus.launcher.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;

import net.tatans.coeus.network.tools.TatansLog;

/**
 * @author Yuliang
 * @time 2015/3/25
 */
public class ProximitySensor implements SensorEventListener {
	// 时间间隔
	private Context mContext;
	private AudioManager audioManager;
	//public static int temp = 0;
	private Sensor mSensor;
	@SuppressLint("UseSparseArrays")
	public ProximitySensor(Context context) {
		mContext=context;
		audioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);

		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);

		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
	}

	public void setOnProximitySensorListener(OnProximitySensorListener onShakeListener) {
		mOnShakeListener = onShakeListener;
	}

	public void onResume() {
		mSensorManager.registerListener(this,mSensor,
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
		/*if (checkInterval()) {
			return;
		}*/
		int sensorType = event.sensor.getType();
		// values[0]:X轴，values[1]：Y轴，values[2]：Z轴
		float[] values = event.values;
		if (sensorType == Sensor.TYPE_PROXIMITY) {
			if (values[0] == mSensor.getMaximumRange()) {
				mOnShakeListener.onFarEar();
				audioManager.setMode(AudioManager.MODE_NORMAL);
				audioManager.setSpeakerphoneOn(true);
				TatansLog.e("Sensor.TYPE_PROXIMITY,远离耳朵。");
			}else{
				mOnShakeListener.onNearEar();
				audioManager.setMode(AudioManager.MODE_IN_CALL);
				audioManager.setSpeakerphoneOn(false);
				TatansLog.e("Sensor.TYPE_PROXIMITY,贴近耳朵。");
			}
			// 这里可以调节摇一摇的灵敏度
			/*if (temp==0){
				mOnShakeListener.onFarEar();
				TatansLog.e("Sensor.TYPE_PROXIMITY,远离耳朵。");
				temp=1;
				audioManager.setMode(AudioManager.MODE_NORMAL);
			}else if(temp==1){
				mOnShakeListener.onNearEar();
				TatansLog.e("Sensor.TYPE_PROXIMITY,贴近耳朵。");
				temp=0;
				audioManager.setMode(AudioManager.MODE_IN_CALL);
			}*/
		}
	}

	public interface OnProximitySensorListener {
		public void onNearEar();
		public void onFarEar();
	}


	private SensorManager mSensorManager = null;
	private OnProximitySensorListener mOnShakeListener = null;
}
