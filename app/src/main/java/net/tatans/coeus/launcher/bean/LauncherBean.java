package net.tatans.coeus.launcher.bean;

import net.tatans.coeus.annotation.sqlite.Table;

/**
 * Rhea [v 2.0.0]
 * classes : net.tatans.coeus.launcher.bean.LauncherBean
 * 余亮 create at 2015/9/22 10:31
 */

@Table(name="launcher")
public class LauncherBean {
	private int id;
	private int launcherID;
	private int launcherIco;
	private String launcherName;
	private String launcherSort;
	private String launcherPackage;
	private String launcherMainClass;
	public int getLauncherID() {
		return launcherID;
	}
	public void setLauncherID(int launcherID) {
		this.launcherID = launcherID;
	}
	public int getLauncherIco() {
		return launcherIco;
	}
	public void setLauncherIco(int launcherIco) {
		this.launcherIco = launcherIco;
	}
	public String getLauncherName() {
		return launcherName;
	}
	public void setLauncherName(String launcherName) {
		this.launcherName = launcherName;
	}
	public String getLauncherSort() {
		return launcherSort;
	}
	public void setLauncherSort(String launcherSort) {
		this.launcherSort = launcherSort;
	}
	public String getLauncherPackage() {
		return launcherPackage;
	}
	public void setLauncherPackage(String launcherPackage) {
		this.launcherPackage = launcherPackage;
	}
	public String getLauncherMainClass() {
		return launcherMainClass;
	}
	public void setLauncherMainClass(String launcherMainClass) {
		this.launcherMainClass = launcherMainClass;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
