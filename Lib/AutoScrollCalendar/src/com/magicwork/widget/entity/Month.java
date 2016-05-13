package com.magicwork.widget.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Month {
	private List<Day> days = new ArrayList<Day>();

	private Calendar calendar;

	public Month(Calendar calendar) {
		this.calendar = (Calendar) calendar.clone();
	}

	public void init() {
		int count1 = calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);
	}

	public void addDays(Day day) {
		days.add(day);
	}

	public List<Day> getDays() {
		return days;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	@Override
	public String toString() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");

		return formatter.format(calendar.getTime());
	}

	public boolean isSameMonth(Calendar startCalendar) {
		if (startCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && startCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
			return true;
		} else {
			return false;
		}
	}
}
