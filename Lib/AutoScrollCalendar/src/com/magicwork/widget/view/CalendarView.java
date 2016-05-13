package com.magicwork.widget.view;

import java.util.Calendar;

import com.magicwork.widget.engine.CalendarLoader;
import com.magicwork.widget.entity.Month;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class CalendarView extends LinearLayout {
	private Month month;
	LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	private int lastY;
	private int lastX;
	private int touchSlop;

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();

	}

	public CalendarView(Context context) {
		super(context);
		init();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int totalHeight = 0;
		for (int i = 0; i < getChildCount(); i++) {

			totalHeight += getChildAt(i).getMeasuredHeight() + param.topMargin + param.bottomMargin;
		}
		setMeasuredDimension(widthSize, totalHeight);
	}

	public void init() {
		setOrientation(VERTICAL);
		param.topMargin = 10;
		param.bottomMargin = 10;
		touchSlop = 8;
	}

	public void setMonth(Month month) {
		removeAllViews();
		this.month = month;

		CalendarRow row = null;
		for (int i = 0; i < month.getDays().size(); i++) {
			if (i % 7 == 0) {// 若为7的倍数则新增一行
				row = new CalendarRow(getContext());
				addView(row, param);
			}
			row.addDay(month.getDays().get(i));

		}
	}

	public boolean isMoveUp(int deltaY) {
		if (deltaY < 0) {
			return true;
		} else {
			return false;
		}
	}

	public int getRowHeight() {
		return getChildAt(0).getMeasuredHeight() + param.topMargin + param.bottomMargin;
	}

	public View getRowAt(Calendar calendar) {
		int index = CalendarLoader.getRowNumber(calendar) - 1;
		return getChildAt(index);
	}

	public int getRowViewTopAt(Calendar calendar) {
		return getRowAt(calendar).getTop() - param.topMargin;
	}
}
