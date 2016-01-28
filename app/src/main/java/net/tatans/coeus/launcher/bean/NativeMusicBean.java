package net.tatans.coeus.launcher.bean;

import android.graphics.Bitmap;
/**
 * @author SiLPing
 * Purpose: 本地音乐MP3类
 * Create Time: 2015-10-29 下午5:52:07
 */
public class NativeMusicBean {

	private String title;//音乐名
	private Bitmap art;//音乐图片
	private String artist;//歌手
	private long id;
	private long albumId;
	private String url;//存储地址
	
	public long getAlbumId() {
		return albumId;
	}
	public void setAlbumId(long albumId) {
		this.albumId = albumId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Bitmap getArt() {
		return art;
	}
	public void setArt(Bitmap art) {
		this.art = art;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}
