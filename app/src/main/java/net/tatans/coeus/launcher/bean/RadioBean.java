package net.tatans.coeus.launcher.bean;

/**
 * 
 * @author luojianqin
 * 
 * Purpose:频道类
 * 
 * Create Time: 2015-4-21 上午8:52:28
 * 
 * Version: 1.0
 */
public class RadioBean {

	private String channelID; // 频道Id
	private String channelName; //频道Name
	private String channelICON; //频道图标
	private String channelURL; //频道URL
	private int level;  //频道级别

	public RadioBean() {

	}

	public RadioBean(String channelID, String channelName,
			String channelICON, String channelURL, int level) {
		super();
		this.channelID = channelID;
		this.channelName = channelName;
		this.channelICON = channelICON;
		this.channelURL = channelURL;
		this.level = level;
	}

	public String getChannelID() {
		return channelID;
	}

	public void setChannelID(String channelID) {
		this.channelID = channelID;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelICON() {
		return channelICON;
	}

	public void setChannelICON(String channelICON) {
		this.channelICON = channelICON;
	}

	public String getChannelURL() {
		return channelURL;
	}

	public void setChannelURL(String channelURL) {
		this.channelURL = channelURL;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "RadioChannelData [channelID=" + channelID + ", channelName="
				+ channelName + ", channelICON=" + channelICON
				+ ", channelURL=" + channelURL + ", level=" + level + "]";
	}
}
