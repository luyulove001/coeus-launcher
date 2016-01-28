package net.tatans.coeus.launcher.util;

import java.util.ArrayList;
import java.util.List;

import net.tatans.coeus.launcher.bean.NativeMusicBean;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
/**
 * @author SiLiPing
 * Purpose: 扫描本地音乐
 * Create Time: 2015-10-29 下午5:59:09
 */
public class NativeMediaUtils {

	public static List<NativeMusicBean> getNativeMusicList(Context ctx) {

		List<NativeMusicBean> list = new ArrayList<NativeMusicBean>();
		ContentResolver cr = ctx.getContentResolver();
		Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if (cursor != null && cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
				String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaColumns.TITLE));
				String singer = cursor.getString(cursor.getColumnIndexOrThrow(AudioColumns.ARTIST));
				int time = cursor.getInt(cursor.getColumnIndexOrThrow(AudioColumns.DURATION));
				time = time / 60000;
				String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaColumns.DISPLAY_NAME));
				String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaColumns.DATA));
				String album = cursor.getString(cursor.getColumnIndexOrThrow(AudioColumns.ALBUM));
				long albumid = cursor.getLong(cursor.getColumnIndex(AudioColumns.ALBUM_ID));
				if (url.endsWith(".mp3") || url.endsWith(".MP3") || url.endsWith(".Mp3") || url.endsWith(".mP3") || 
						url.endsWith(".WAV") || url.endsWith(".Wav") || url.endsWith(".wav") ||
						url.endsWith(".WMA") || url.endsWith(".Wma") || url.endsWith(".wma")) {
					NativeMusicBean Native = new NativeMusicBean();
					Native.setTitle(title);
					Native.setArtist(singer);
					Native.setId(id);
					Native.setUrl(url);
					Native.setAlbumId(albumid);
					list.add(Native);
				}
			}
		}
		try {
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
		return list;
	}

}