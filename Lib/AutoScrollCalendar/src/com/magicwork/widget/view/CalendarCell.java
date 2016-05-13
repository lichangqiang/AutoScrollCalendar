package com.magicwork.widget.view;

import java.util.Calendar;

import com.magicwork.widget.engine.CalendarLoader;
import com.magicwork.widget.entity.Day;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.PointF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CalendarCell extends View {

	Paint paint = new Paint();
	TextPaint textPaint = new TextPaint();
	Day day;
	FontMetrics fontMetrics;
	ICalendarView scrollCalendarView;

	// SingleLineCalendarView singlineCalendarView;
	public CalendarCell(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CalendarCell(Context context) {
		super(context);
		initPaint();
		init();
	}

	public void init() {
		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isInThisMonth(day)) {
					if (scrollCalendarView != null) {
						scrollCalendarView.setSelectedDayCalendar(day.getCalendar());
						requestLayout();
					}

				}
			}
		});
		setWillNotDraw(false);
		setWillNotCacheDrawing(false);
	}

	public void initPaint() {
		textPaint.setColor(Color.RED);
		textPaint.setTextSize(40);
		// textPaint.setTextAlign(Align.CENTER);
		fontMetrics = textPaint.getFontMetrics();
	}

	public void setDay(Day day) {
		this.day = day;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		updateCalendarView();
		// 判断是否在这个月内
		if (!isInThisMonth(day) || (scrollCalendarView).getSelectedDayCalendar() == null) {
			drawNonMonthDay(canvas);
			return;
		}

		// 判断是否被选中的那天
		if (!isSameDay(scrollCalendarView.getSelectedDayCalendar())) {
			drawNoneSelecedDay(canvas);
		} else {
			drawSelecteDay(canvas);
		}
	}

	private void drawNonMonthDay(Canvas canvas) {
		// 让文字居中显示
		float x = getWidth() / 2 - textPaint.measureText(day.toString()) / 2;
		float y = (getHeight() + (fontMetrics.descent - fontMetrics.ascent)) * 0.5f - fontMetrics.bottom;
		canvas.drawColor(Color.rgb(140, 140, 140));
		// canvas.drawText(day.toString(), x, y, textPaint);
	}

	private void drawNoneSelecedDay(Canvas canvas) {
		// 让文字居中显示
		float x = getWidth() / 2 - textPaint.measureText(day.toString()) / 2;
		float y = (getHeight() + (fontMetrics.descent - fontMetrics.ascent)) * 0.5f - fontMetrics.bottom;
		canvas.drawColor(Color.rgb(140, 140, 140));
		canvas.drawText(day.toString(), x, y, textPaint);
	}

	private void drawSelecteDay(Canvas canvas) {
		// 让文字居中显示
		float x = getWidth() / 2 - textPaint.measureText(day.toString()) / 2;
		float y = (getHeight() + (fontMetrics.descent - fontMetrics.ascent)) * 0.5f - fontMetrics.bottom;
		canvas.drawColor(Color.rgb(210, 210, 210));
		canvas.drawText(day.toString(), x, y, textPaint);
	}

	private void updateCalendarView() {

		try {
			scrollCalendarView = (AutoScrollCalendarView) getParent().getParent().getParent();
		} catch (Exception e) {
			scrollCalendarView = (SingleLineCalendarView) getParent().getParent();
		}
	}

	private boolean isInThisMonth(Day day) {
		if (day.getDayOfMonth() == -1) {
			return false;
		}
		return true;
	}

	private boolean isSameDay(Calendar selectedCalendar) {
		Calendar calendar = day.getCalendar();
		if (calendar == null) {
			return false;
		}
		Log.e(VIEW_LOG_TAG, "current" + CalendarLoader.getSimpleDateStr(calendar) + "  selected:" + CalendarLoader.getSimpleDateStr(selectedCalendar));
		if (calendar.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == selectedCalendar.get(Calendar.DAY_OF_YEAR)) {
			return true;
		}
		return false;
	}
}
