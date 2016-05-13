package com.magicwork.widget.engine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.magicwork.widget.entity.Day;
import com.magicwork.widget.entity.Month;
import com.magicwork.widget.entity.Week;

import android.nfc.NfcAdapter.CreateBeamUrisCallback;
import android.util.Log;

/**
 * 通过后台来缓存日历加快日历的切换速度
 * 
 * @author licq
 *
 */
public class CalendarLoader {
	boolean isLoadFinished = false;
	Calendar beginCalendar = Calendar.getInstance();
	Calendar endCalendar = Calendar.getInstance();
	Calendar currCalendar;
	ExecutorService service = Executors.newSingleThreadScheduledExecutor();
	public static String TAG = "CalendarLoader";

	/**
	 * 预加载距当前日历的前后的月数的日历
	 * 
	 * @param monthCount
	 */
	public void startLoad(int monthCount) {
		/*
		 * beginCalendar.roll(Calendar.MONTH, -monthCount);
		 * endCalendar.roll(Calendar.MONTH, monthCount);
		 */
		currCalendar = beginCalendar;
		service.execute(new Runnable() {

			@Override
			public void run() {

			}
		});
	}

	public void display(Calendar calendar) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String result = format.format(calendar.getTime());
		Log.e(TAG, result);
	}

	/**
	 * 加载多个月的月历
	 * 
	 * @param calendar
	 *            加载的目标月历
	 * @param range
	 *            月历的范围 如：3 表示加载前后3个月的月历
	 * @return
	 */
	public static List<Month> loadMonths(Calendar calendar, int range) {
		List<Month> months = new ArrayList<Month>();
		calendar.roll(Calendar.MONTH, -range);
		Calendar tempCalendar = (Calendar) calendar.clone();
		for (int i = 0; i < range * 2; i++) {
			Month month = loadMonth(tempCalendar);
			months.add(month);
			calendar.roll(Calendar.MONTH, true);
			tempCalendar = (Calendar) calendar.clone();
			Log.e(TAG, month.toString());
		}
		return months;
	}

	public static Month loadMonth(Calendar calendar) {
		Month month = new Month(calendar);

		Calendar tempCalendar = (Calendar) calendar.clone();
		tempCalendar.set(Calendar.DAY_OF_MONTH, 1);// 将日历移动到该月的第一天

		int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK);// 这个月的第一天在第一周的第几天
		// 将属于第一周的非本月的日子填充到Month
		for (int i = 1; i < firstDayOfWeek; i++) {
			Day day = new Day(-1, month);
			month.addDays(day);
		}
		// tempCalendar.getActualMaximum(field)

		tempCalendar.set(Calendar.DAY_OF_MONTH, tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));// 将日子移动到该月的最后一天
		int lastDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK);// 这个月的最后一天是周几
		int dayOfMonth = tempCalendar.get(Calendar.DAY_OF_MONTH);// 最后一天是这个月的第几天

		tempCalendar.set(Calendar.DAY_OF_MONTH, 1);// 将日历移动到该月的第一天
		// 将本月的日子全部填充
		for (int i = 1; i <= dayOfMonth; i++) {
			Calendar dayCalendar = (Calendar) tempCalendar.clone();
			dayCalendar.roll(Calendar.DAY_OF_YEAR, i - 1);

			Day day = new Day(i, month);
			day.setCalendar(dayCalendar);
			month.addDays(day);
		}

		// 将属于最后一周的非本月的日子填充到MOnth
		for (int j = lastDayOfWeek; j < 7; j++) {
			Day day = new Day(-1, month);
			month.addDays(day);
		}

		return month;
	}

	/**
	 * 加载当前日历的周数据
	 * 
	 * @param calendar
	 * @return
	 */
	public static Week loadWeek(Calendar calendar) {
		Week week = new Week();
		Calendar tempCalendar = (Calendar) calendar.clone();
		int dayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK);
		tempCalendar.roll(Calendar.DAY_OF_YEAR, -dayOfWeek);
		for (int i = 1; i <= 7; i++) {
			Calendar dayCalendar = (Calendar) tempCalendar.clone();
			dayCalendar.roll(Calendar.DAY_OF_YEAR, i);

			int dayOfMonth = dayCalendar.get(Calendar.DAY_OF_MONTH);
			Month month = new Month(dayCalendar);
			Day day = new Day(dayOfMonth, month);
			day.setCalendar(dayCalendar);
			week.getDays().add(day);
			// Log.e(TAG, "calendar "+getSimpleDateStr(dayCalendar));
		}
		return week;
	}

	/**
	 * 加载上周的日历
	 * 
	 * @param calendar
	 * @return
	 */
	public static Week loadPreWeek(Calendar calendar) {
		Calendar tempCalendar = (Calendar) calendar.clone();
		tempCalendar.roll(Calendar.DAY_OF_YEAR, -7);
		return loadWeek(tempCalendar);
	}

	/**
	 * 加载下周日历
	 * 
	 * @param calendar
	 * @return
	 */
	public static Calendar getNextWeekCalendar(Calendar calendar) {
		Calendar tempCalendar = (Calendar) calendar.clone();
		tempCalendar.roll(Calendar.DAY_OF_YEAR, 7);
		return tempCalendar;
	}

	public static Calendar getFirstDayOfWeek(Calendar calendar) {
		Calendar tempCalendar = (Calendar) calendar.clone();
		tempCalendar.set(Calendar.DAY_OF_WEEK, 1);
		return tempCalendar;
	}

	public static Calendar getFirstDayOfMonth(Calendar calendar) {
		Calendar tempCalendar = (Calendar) calendar.clone();
		tempCalendar.set(Calendar.DAY_OF_MONTH, 1);
		return tempCalendar;
	}

	/**
	 * 加载上周日历
	 * 
	 * @param calendar
	 * @return
	 */
	public static Calendar getPreWeekCalendar(Calendar calendar) {
		Calendar tempCalendar = (Calendar) calendar.clone();
		tempCalendar.roll(Calendar.DAY_OF_YEAR, -7);
		return tempCalendar;
	}

	/**
	 * 加载下周的日历
	 * 
	 * @param calendar
	 * @return
	 */
	public static Week loadNextWeek(Calendar calendar) {
		Calendar tempCalendar = (Calendar) calendar.clone();
		tempCalendar.roll(Calendar.DAY_OF_YEAR, 7);
		return loadWeek(tempCalendar);
	}

	/**
	 * 获取下个月的月历
	 * 
	 * @param calendar
	 * @return
	 */
	public static Calendar getNextMonthCalendar(Calendar calendar) {
		Calendar nextMonthCalendar = (Calendar) calendar.clone();
		int month = nextMonthCalendar.get(Calendar.MONTH);
		if (month == 11) {
			nextMonthCalendar.roll(Calendar.YEAR, true);
			nextMonthCalendar.set(Calendar.MONTH, 0);
		} else {
			nextMonthCalendar.roll(Calendar.MONTH, true);
		}
		return nextMonthCalendar;
	}

	/**
	 * 获取上一个月的月历
	 * 
	 * @param calendar
	 * @return
	 */
	public static Calendar getPreMonthCalendar(Calendar calendar) {
		Calendar preMonthCalendar = (Calendar) calendar.clone();
		int month = preMonthCalendar.get(Calendar.MONTH);
		if (month == 0) {
			preMonthCalendar.roll(Calendar.YEAR, false);
			preMonthCalendar.set(Calendar.MONTH, 11);
		} else {
			preMonthCalendar.roll(Calendar.MONTH, false);
		}
		return preMonthCalendar;
	}

	public static String getSimpleDateStr(Calendar calendar) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(calendar.getTime());
	}

	public static int getRowNumber(Calendar calendar) {
		Calendar tempCalendar = (Calendar) calendar.clone();
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		tempCalendar.set(Calendar.DAY_OF_MONTH, 1);// 将日历移动到该月的第一天

		int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK);// 这个月的第一天在第一周的第几天

		int row = (dayOfMonth + 6 + firstDayOfWeek - 1) / 7;
		return row;
	}
}
