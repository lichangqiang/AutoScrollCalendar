package com.magicwork.widget.entity;

import java.util.Calendar;

public class Day {
	int dayOfMonth;
	Month month;
	Calendar calendar;

	public Day(int dayOfMonth, Month month) {
		this.dayOfMonth = dayOfMonth;
		this.month = month;
	}

	@Override
	public String toString() {
		return dayOfMonth + "";
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public int getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

}
