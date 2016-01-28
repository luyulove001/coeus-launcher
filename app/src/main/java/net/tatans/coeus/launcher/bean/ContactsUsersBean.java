package net.tatans.coeus.launcher.bean;

import java.io.Serializable;
/**
 * @author SiLPing
 * Purpose: 本地音乐手机号码类
 * Create Time: 2015-11-4 下午4:17:59
 */
public class ContactsUsersBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String DISPLAY_NAME;
	private String NUMBER;
	private long CONTACT_ID;
	private long PHOTO_ID;
	public String getDISPLAY_NAME() {
		return DISPLAY_NAME;
	}
	public void setDISPLAY_NAME(String dISPLAY_NAME) {
		DISPLAY_NAME = dISPLAY_NAME;
	}
	public String getNUMBER() {
		return NUMBER;
	}
	public void setNUMBER(String nUMBER) {
		NUMBER = nUMBER;
	}
	public long getCONTACT_ID() {
		return CONTACT_ID;
	}
	public void setCONTACT_ID(long cONTACT_ID) {
		CONTACT_ID = cONTACT_ID;
	}
	public long getPHOTO_ID() {
		return PHOTO_ID;
	}
	public void setPHOTO_ID(long pHOTO_ID) {
		PHOTO_ID = pHOTO_ID;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
