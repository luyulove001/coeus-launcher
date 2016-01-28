package net.tatans.coeus.launcher.tools;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by chulu on 2015/3/25.
 */
public class TimeTool {
	public static String getCurrentTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * @param hourTime
	 * @return 将小时转换成毫秒数
	 */
	public static long getHourMillisTime(int hourTime) {
		long millisTime = 0;
		millisTime = hourTime * 60 * 60 * 1000;
		return millisTime;
	}

	/**
	 * @return 得到闹钟开始时间的毫秒数
	 */
	public static long getStartMillisTime() {
		int startTime = 0;
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		if (minute == 0) {
			startTime = hour;
		} else {
			if (hour == 0) {
				startTime = 1;
			} else {
				startTime = hour + 1;
			}
		}

		return getHourMillisTime(startTime);
	}
}
