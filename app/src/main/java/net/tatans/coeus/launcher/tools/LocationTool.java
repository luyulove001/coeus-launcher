package net.tatans.coeus.launcher.tools;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;


/**
 * Purpose:低功耗、单次定位工具
 * @author SiLiPing
 * Create Time: 2016-1-23 下午1:57:55
 */
public class LocationTool implements AMapLocationListener {

	private AMapLocationClient locationClient = null;
	private AMapLocationClientOption locationOption = null;
	private OnLocationListener mOnLocationListener;

	public void setOnLocationListener(OnLocationListener Listener){
		this.mOnLocationListener = Listener;
	}

	public interface OnLocationListener{
		void onSuccess(AMapLocation location);
		void onFailure(AMapLocation location);
		void onErr();
	}

	public LocationTool(Context ctx){
		locationClient = new AMapLocationClient(ctx);
		locationOption = new AMapLocationClientOption();
		// 设置定位模式为低功耗模式
		locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
		locationClient.setLocationListener(this);
		// 设置为单次定位
		locationOption.setOnceLocation(true);
		// 设置是否需要显示地址信息
		locationOption.setNeedAddress(true);
		// 设置定位参数
		locationClient.setLocationOption(locationOption);
		// 启动定位
		locationClient.startLocation();
	}

	/**
	 * Purpose:定位返回值
	 */
	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
			if(location.getErrorCode() == 0){
				//定位成功
				mOnLocationListener.onSuccess(location);
			} else {
				//定位失败
				mOnLocationListener.onFailure(location);
			}
		}else{
			mOnLocationListener.onErr();
		}
	}

	/**
	 * Purpose:停止定位
	 */
	public void onStopLocation(){
		locationClient.stopLocation();
	}

}
