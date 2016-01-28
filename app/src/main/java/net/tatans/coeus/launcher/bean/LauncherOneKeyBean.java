package net.tatans.coeus.launcher.bean;

import net.tatans.coeus.annotation.sqlite.Table;

/**
 * Rhea [v 2.0.0]
 * classes : net.tatans.coeus.launcher.bean.LauncherBean
 * 余亮 create at 2015/9/22 10:31
 */
@Table(name="OneKeyApp")
public class LauncherOneKeyBean {
	private int id;
	private int OneKeyID;
	private String OneKeyDes;
	private String OneKeyName;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOneKeyID() {
		return OneKeyID;
	}
	public void setOneKeyID(int oneKeyID) {
		OneKeyID = oneKeyID;
	}
	public String getOneKeyDes() {
		return OneKeyDes;
	}
	public void setOneKeyDes(String oneKeyDes) {
		OneKeyDes = oneKeyDes;
	}
	public String getOneKeyName() {
		return OneKeyName;
	}
	public void setOneKeyName(String oneKeyName) {
		OneKeyName = oneKeyName;
	}
	
}
