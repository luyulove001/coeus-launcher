package net.tatans.coeus.launcher.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import net.tatans.coeus.launcher.activities.LauncherApp;

public class NetworkUtil {

	public String TYPE_NETWORK = "";
	private Context context;
	
	public NetworkUtil(Context context){
		this.context = context;
	}
	
	public static boolean isNetworkOK(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public static String getTextByURL(String path) throws Exception {
		URL url = new URL(path); 
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		String text = "";
		//获取输入流  
		InputStream is = conn.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, "utf-8");  
		BufferedReader bufferReader = new BufferedReader(isr);  
		String inputLine  = "";
		while((inputLine = bufferReader.readLine()) != null){  
			text += inputLine + "\n";  
		}
		return text;
	}
	
	public String networkType(){
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
			TYPE_NETWORK = "网络不可用，请检查联网后再试";
			
		}else if(mobNetInfo.isConnected() && !wifiNetInfo.isConnected()){
			TYPE_NETWORK = "您正在使用数据流量";
			
		}else if(!mobNetInfo.isConnected() && wifiNetInfo.isConnected()){
			TYPE_NETWORK = "WiFi已连接";
		}
		return TYPE_NETWORK;
	}
	
	public static boolean isMobile(){
		ConnectivityManager cm = (ConnectivityManager) LauncherApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() !=null && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE){
			return true;
		}
		return false;
	}
	public static boolean isWiFi(){
		ConnectivityManager cm = (ConnectivityManager) LauncherApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() !=null && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI){
			return true;
		}
		return false;
	}
	
	/**
	 * @return 放回当前数据流量开关
	 */
	public static boolean isMobileDataEnabledFromLollipop(){
		TelephonyManager telephoneManager = (TelephonyManager) LauncherApp.getInstance().getSystemService(Service.TELEPHONY_SERVICE);
		if(telephoneManager.getSimState() != TelephonyManager.SIM_STATE_READY){
			return false;
		}
		boolean state = false;
//		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
			state = Settings.Global.getInt(LauncherApp.getInstance().getContentResolver(), "mobile_data",0) == 1;
//		}
		return state;
	}
}
