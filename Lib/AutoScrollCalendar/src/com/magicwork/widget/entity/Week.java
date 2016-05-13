package com.magicwork.widget.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Week {
	private List<Day> days = new ArrayList<Day>(7);

	public List<Day> getDays() {
		return days;
	}

	public void setDays(List<Day> days) {
		this.days = days;
	}

}
