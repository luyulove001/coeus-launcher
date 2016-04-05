package net.tatans.coeus.launcher.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by SiLiPing on 2016/4/5.
 * Android 4.4 的代码本来就有控制移动数据的启用与关闭的方法 setMobileDataEnabled
 * 设置数据流量，获取数据流量开关状态
 * */
public class ConnectivityUtil {
    private static final String TAG = "ConnectivityUtil";
    public ConnectivityManager mConnectivityManager;
    public NetworkInfo mNetworkInfo;

    public ConnectivityUtil(Context context) {
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
    }

    public boolean isNetworkConnected() {
        if (null != mNetworkInfo) {
            return mNetworkInfo.isConnected();
        }
        return false;
    }

    public boolean isMobileConnected() {
        if ((null != mNetworkInfo) && mNetworkInfo.isConnected()) {
            return ConnectivityManager.TYPE_MOBILE == mNetworkInfo.getType();
        }
        return false;
    }

    /**
     * 不需要获取系统权限
     * 适用于android4.4版本
     * @param isMobileDataEnabled
     */
    public void setMobileDataEnabled(boolean isMobileDataEnabled) {
        try {
            Method method = mConnectivityManager.getClass().getDeclaredMethod("setMobileDataEnabled", boolean.class);
            if (null != method) {
                method.setAccessible(true);
                method.invoke(mConnectivityManager, isMobileDataEnabled);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 不需要获取系统权限
     * 适用于android4.4版本
     */
    public boolean getMobileDataEnabled() {
        try {
            Method method = mConnectivityManager.getClass().getDeclaredMethod("getMobileDataEnabled");
            if (null != method) {
                method.setAccessible(true);
                boolean isMobileDataEnabled = (Boolean) method.invoke(mConnectivityManager);
                return isMobileDataEnabled;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
