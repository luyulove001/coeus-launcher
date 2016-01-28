package net.tatans.coeus.launcher.bean;

/**
 * Rhea [v 2.0.0]
 * classes : net.tatans.coeus.launcher.bean.LauncherBean
 * 余亮 create at 2015/9/22 10:31
 */

public class LauncherAppBean {
	private int id;
	private int launcherAppId;
	private int appIco;
	private String appName;
	private String appPackage;
	private String appMainClass;
	public int getLauncherAppId() {
		return launcherAppId;
	}
	public void setLauncherAppId(int launcherAppId) {
		this.launcherAppId = launcherAppId;
	}
	public int getAppIco() {
		return appIco;
	}
	public void setAppIco(int appIco) {
		this.appIco = appIco;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppPackage() {
		return appPackage;
	}
	public void setAppPackage(String appPackage) {
		this.appPackage = appPackage;
	}
	public String getAppMainClass() {
		return appMainClass;
	}
	public void setAppMainClass(String appMainClass) {
		this.appMainClass = appMainClass;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

}
