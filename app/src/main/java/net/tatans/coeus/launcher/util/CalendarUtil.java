package net.tatans.coeus.launcher.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.tatans.coeus.audio.manager.AudioManagerUtil;
import net.tatans.coeus.audio.util.AudioManagerCallBack;
import net.tatans.coeus.launcher.activities.LauncherApp;
import net.tatans.coeus.launcher.info.DateInfo;
import net.tatans.coeus.speaker.Speaker;
import net.tatans.coeus.speaker.Speaker.onSpeechCompletionListener;
import android.text.format.Time;
import android.util.Log;

@SuppressWarnings("deprecation")
public class CalendarUtil {
	private static final String TAG = "calendar";
	private static final int IDLE = 0;
	private static final int RUNNING = 1;
	private static int currState;
	private static Speaker speaker;
	private static AudioManagerUtil manager;
	private static AudioManagerCallBack audioCallBack;
	static {
		currState = IDLE;
		audioCallBack = new AudioManagerCallBack() {
			/**
			 * 音频焦点长期失去
			 */
			@Override
			public void onFocusLoss() {
				super.onFocusLoss();
				Log.i(TAG, "onFocusLoss");
				stop();
			}

			/**
			 * 音频焦点暂时失去
			 */
			@Override
			public void onFocusLossTransient() {
				super.onFocusLossTransient();
				Log.i(TAG, "onFocusLossTransient");
				// stop();
			}

			/**
			 * 音频焦点暂时失去，降低周围声音
			 */
			@Override
			public void onFocusLossTransientDuck() {
				super.onFocusLossTransientDuck();
				Log.i(TAG, "onFocusLossTransientDuck");
				// stop();
			}

			/**
			 * 请求授权音频焦点失败
			 */
			@Override
			public void onFocusFail(int errCode) {
				super.onFocusFail(errCode);
				Log.i(TAG, "onFocusFail");
			}

			/**
			 * 音频焦点重新取到
			 */
			@Override
			public void onFocusGain() {
				super.onFocusGain();
				Log.i(TAG, "onFocusGain");
				LauncherApp.putInt(Const.FOCUS, Const.Calendar);
			}

			/**
			 * 请求授权音频焦点成功
			 */
			@Override
			public void onFocusSuccess() {
				super.onFocusSuccess();
				Log.i(TAG, "onFocusSuccess");
				LauncherApp.putInt(Const.FOCUS, Const.Calendar);
				start();
			}
		};
	}

	public static void run() {
		Log.i(TAG, "currState=" + currState);
		speaker = Speaker.getInstance(LauncherApp.getInstance());
		speaker.getSpeechController().setOnSpeechCompletionListener(
				new onSpeechCompletionListener() {
					@Override
					public void onCompletion(int arg0) {
						if (arg0 == 0) {
							stop();
						}
					};
				});
		switch (currState) {
		case IDLE:
			// manager = ManagerUtil.getAudioManager(LauncherApp.getInstance(),
			// audioCallBack, AudioManagerConst.FOCUS_GAIN_TRANSIENT);
			if (!manager.isAudioFocusSuccess()) {
				speaker.speech("日历启动失败，请稍后再试", 85);
				return;
			}
			break;
		case RUNNING:
			stop();
			break;
		default:
			break;
		}
	}

	/**
	 * 开始
	 */
	private static void start() {
		if (currState == IDLE) {
			currState = RUNNING;
			int currentYear = getCurrentYear();
			int currentMonth = getCurrentMonth();
			int lastSelected = getCurrentDay();
			String formatDate = getFormatDate(currentYear, currentMonth);
			List<DateInfo> list = null;
			try {
				list = initCalendar(formatDate, currentMonth);
				int pos = getDayFlag(list, lastSelected);
				String str = "今天" + getDateString() + getWeekString()
						+ list.get(pos).getNongliDate() + getTermString()
						+ getwFestival() + getEve();
				speaker.speech(str, 85);
			} catch (Exception e) {
			}
		}
	}

	public static String getAllDate() {
		int currentYear = getCurrentYear();
		int currentMonth = getCurrentMonth();
		int lastSelected = getCurrentDay();
		String formatDate = getFormatDate(currentYear, currentMonth);
		List<DateInfo> list = null;
		try {
			list = initCalendar(formatDate, currentMonth);
			int pos = getDayFlag(list, lastSelected);
			String str = "今天" + getDateString() + getWeekString()
					+ list.get(pos).getNongliDate() + getTermString()
					+ getwFestival() + getEve();
			System.out.println("当前时间日历：" + str);
			return str;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 停止
	 */
	private static void stop() {
		if (speaker != null) {
			speaker.stop();
			if (manager != null) {
				manager.abandonAudioFocus();
				manager = null;
			}
			currState = IDLE;
		}
	}

	/**
	 * 暂停
	 */
	public static void pause() {
		// 实施停止操作
		stop();
	}

	private static int getCurrentYear() {
		Time t = new Time();
		t.setToNow();
		return t.year;
	}

	private static int getCurrentMonth() {
		Time t = new Time();
		t.setToNow();
		return t.month + 1;
	}

	private static int getCurrentDay() {
		Time t = new Time();
		t.setToNow();
		return t.monthDay;
	}

	private static int getWeekDay(String date) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		try {
			calendar.setTime(sdf.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 1) {
			dayOfWeek = 0;
		} else {
			dayOfWeek -= 1;
		}
		return dayOfWeek;
	}

	private static boolean isLeapYear(int year) {
		if (year % 400 == 0 || year % 100 != 0 && year % 4 == 0) {
			return true;
		}
		return false;
	}

	private static int getDaysOfMonth(int year, int month) {
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			return 31;
		case 4:
		case 6:
		case 9:
		case 11:
			return 30;
		default:
			if (isLeapYear(year)) {
				return 29;
			}
			return 28;
		}
	}

	private static String getFormatDate(int year, int month) {
		String formatYear = year + "";
		String formatMonth = "";
		if (month < 10) {
			formatMonth = "0" + month;
		} else {
			formatMonth = month + "";
		}
		return formatYear + "-" + formatMonth + "-01";
	}

	private static String getFormatDate(int year, int month, int day) {
		String formatYear = year + "";
		String formatMonth = "";
		String formatDay = "";
		if (month < 10) {
			formatMonth = "0" + month;
		} else {
			formatMonth = month + "";
		}
		if (day < 10) {
			formatDay = "0" + day;
		} else {
			formatDay = day + "";
		}
		return formatYear + "-" + formatMonth + "-" + formatDay;
	}

	private static List<DateInfo> initCalendar(String formatDate, int month)
			throws Exception {
		int dates = 1;
		int year = Integer.parseInt(formatDate.substring(0, 4));
		int[] allDates = new int[42];
		for (int i = 0; i < allDates.length; i++) {
			allDates[i] = -1;
		}
		int firstDayOfMonth = getWeekDay(formatDate);
		int totalDays = getDaysOfMonth(year, month);
		for (int i = firstDayOfMonth; i < totalDays + firstDayOfMonth; i++) {
			allDates[i] = dates;
			dates++;
		}

		List<DateInfo> list = new ArrayList<DateInfo>();
		DateInfo dateInfo;
		for (int i = 0; i < allDates.length; i++) {
			dateInfo = new DateInfo();
			dateInfo.setDate(allDates[i]);
			if (allDates[i] == -1) {
				dateInfo.setNongliDate("");
				dateInfo.setThisMonth(false);
				dateInfo.setWeekend(false);
			} else {
				String date = getFormatDate(year, month, allDates[i]);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
						Locale.getDefault());
				long time = sdf.parse(date).getTime();
				String weekDay = "";
				Lunar lunar = new Lunar(time);
				if (lunar.getDayOfWeek() == 1) {
					weekDay = "周日";
				} else {
					weekDay = "周" + (lunar.getDayOfWeek() - 1);
				}

				String nongLiMonth = "农历" + lunar.getLunarMonthString() + "月";
				if (lunar.isFestival()) {
					if (lunar.isLFestival()
							&& lunar.getLunarMonthString().substring(0, 1)
									.equals("闰") == false) {
						dateInfo.setNongliDate(nongLiMonth
								+ lunar.getLunarDayString() + ","
								+ lunar.getLFestivalName());
						dateInfo.setHoliday(true);
					} else {
						dateInfo.setNongliDate(nongLiMonth
								+ lunar.getLunarDayString() + ","
								+ lunar.getSFestivalName());
						dateInfo.setHoliday(true);
					}
				} else {
					dateInfo.setNongliDate(nongLiMonth
							+ lunar.getLunarDayString());
					dateInfo.setHoliday(false);
				}
				dateInfo.setThisMonth(true);
				int t = getWeekDay(getFormatDate(year, month, allDates[i]));
				if (t == 0 || t == 6) {
					dateInfo.setWeekend(true);
				} else {
					dateInfo.setWeekend(false);
				}
			}
			list.add(dateInfo);
		}

		int front = getFirstIndexOf(list);
		int back = getLastIndexOf(list);
		int lastMonthDays = getDaysOfMonth(year, month - 1);
		int nextMonthDays = 1;
		for (int i = front - 1; i >= 0; i--) {
			list.get(i).setDate(lastMonthDays);
			lastMonthDays--;
		}
		for (int i = back + 1; i < list.size(); i++) {
			list.get(i).setDate(nextMonthDays);
			nextMonthDays++;
		}
		return list;
	}

	/**
	 * @author chulu
	 * @time 2015/4/22 得到当前天数的i
	 */
	private static int getDayFlag(List<DateInfo> list, int day) {
		int i;
		for (i = 0; i < list.size(); i++) {
			if (list.get(i).getDate() == day && list.get(i).isThisMonth()) {
				return i;
			}
		}
		for (i = list.size() - 1; i >= 0; i--) {
			if (list.get(i).isThisMonth()) {
				break;
			}
		}
		return i;
	}

	/**
	 * @author chulu
	 * @time 2015/4/22
	 */
	private static int getFirstIndexOf(List<DateInfo> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getDate() != -1)
				return i;
		}
		return -1;
	}

	/**
	 * @author chulu
	 * @time 2015/4/22
	 */
	private static int getLastIndexOf(List<DateInfo> list) {
		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.get(i).getDate() != -1)
				return i;
		}
		return -1;
	}

	/**
	 * 获取当前的日期（例：2015-2-1）
	 */
	public static String getDateString() {
		int currentYear = getCurrentYear();
		int currentMonth = getCurrentMonth();
		int lastSelected = getCurrentDay();
		return currentYear + "年" + currentMonth + "月" + lastSelected + "日,";
	}

	/**
	 * 获取当前的星期（例：星期一）
	 */
	public static String getWeekString() {
		Date date_lock = new Date();
		Lunar lunar_lock = new Lunar(date_lock);
		return lunar_lock.getLunarWeekDay() + ",";
	}

	/**
	 * 获取当前的节气（例:冬至）
	 */
	public static String getTermString() {
		Date date_lock = new Date();
		Lunar lunar_lock = new Lunar(date_lock);
		return lunar_lock.getTermString() + ",";
	}

	/**
	 * 获得某一年的母亲节或父亲节 （例:2015.5.10（获取2015年五月的第二个星期天））
	 * （例:2015.6.21（获取2015年六月的第三个星期天））
	 */
	public static String getwFestival() {
		Date date_lock = new Date();
		Lunar lunar_lock = new Lunar(date_lock);
		String wfestival = "";
		String mFestival = Lunar.getDate(lunar_lock.getSolarYear(), 5, 2, 0)
				.toString();
		String fFestival = Lunar.getDate(lunar_lock.getSolarYear(), 6, 3, 0)
				.toString();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		String date = df.format(new Date()).toString();// new Date()为获取当前系统时间
		if (mFestival.equals(date) || mFestival == date) {
			wfestival = "母亲节 ";
		}
		if (fFestival.equals(date) || fFestival == date) {
			wfestival = "父亲节 ";
		}
		return wfestival + ",";
	}

	/**
	 * 获得某一年的除夕 （例:除夕（获取2015年的腊月二十九））
	 */
	public static String getEve() {
		Date date_lock = new Date();
		Lunar lunar_lock = new Lunar(date_lock);
		String Ffestival = "";
		// 1(获得农历的月份)
		int month = lunar_lock.getLunarMonth();
		// 1(获得农历月份中的哪一天)
		int date = lunar_lock.getLunarDay();
		// 判断该月是否为大月
		if (lunar_lock.isBigMonth()) {
			if (month == 12 && date == 30) {
				Ffestival = "除夕 ";
			}
		} else {
			if (month == 12 && date == 29) {
				Ffestival = "除夕 ";
			}
		}
		return Ffestival;
	}

}
