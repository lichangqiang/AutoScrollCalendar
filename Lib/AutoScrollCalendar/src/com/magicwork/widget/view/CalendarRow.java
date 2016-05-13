package com.magicwork.widget.view;

import java.util.ArrayList;
import java.util.List;

import com.magicwork.widget.entity.Day;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CalendarRow extends LinearLayout {
	List<Day> days = new ArrayList<Day>();
	LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

	public CalendarRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CalendarRow(Context context) {
		super(context);
		init();
		param.topMargin = 10;
		param.bottomMargin = 10;
		param.leftMargin = 10;
		param.rightMargin = 10;
	}

	public void init() {
		setOrientation(HORIZONTAL);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int height = 0;
		if (getChildAt(0) != null) {
			height = getChildAt(0).getMeasuredWidth();
			measureChildren(MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
		}

		setMeasuredDimension(widthSize, height);
	}

	public void addDay(Day day) {
		days.add(day);
		CalendarCell calendarCell = new CalendarCell(getContext());
		calendarCell.setDay(day);
		param.weight = 1;
		addView(calendarCell, param);
	}

}
