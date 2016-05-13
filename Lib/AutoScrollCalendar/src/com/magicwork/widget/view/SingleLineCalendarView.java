package com.magicwork.widget.view;

import java.util.Calendar;

import com.magicwork.widget.engine.CalendarLoader;
import com.magicwork.widget.entity.Day;
import com.magicwork.widget.entity.Week;
import com.magicwork.widget.view.AutoScrollCalendarParentLayout.Direction;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * 表示当AutoScrlloCalendarView收缩时的状态
 * 
 * @author licq
 *
 */
public class SingleLineCalendarView extends ViewGroup implements ICalendarView {
	CalendarRow currentRow;// 表示当前正在实现的Row
	Calendar currentCalendar;// 表示当前的日历
	Direction direction;
	boolean isIntercept;
	int lastX;
	int lastY;
	int touchSlop;
	private Scroller scroller;
	boolean isScrolling;
	private Calendar selectedDayCalendar = Calendar.getInstance();
	LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

	public SingleLineCalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SingleLineCalendarView(Context context, Calendar currentCalendar) {
		super(context);
		this.currentCalendar = currentCalendar;
		init();
	}

	private void init() {

		touchSlop = 8;
		scroller = new Scroller(getContext());
		param.topMargin = 10;
		param.bottomMargin = 10;
	}

	public void setCurrentCalendar(Calendar calendar) {
		currentCalendar = calendar;
		selectedDayCalendar = (Calendar) calendar.clone();
		removeAllViews();
		addView(loadWeekRow(CalendarLoader.loadPreWeek(currentCalendar)), param);
		addView(loadWeekRow(CalendarLoader.loadWeek(currentCalendar)), param);
		addView(loadWeekRow(CalendarLoader.loadNextWeek(currentCalendar)), param);
		// currentRow = (CalendarRow) getChildAt(1);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		int heightSize = getChildAt(1).getMeasuredHeight() + param.topMargin;

		widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		View leftView = getChildAt(0);
		View rightView = getChildAt(2);
		currentRow = (CalendarRow) getChildAt(1);
		int width = currentRow.getMeasuredWidth();
		int height = currentRow.getMeasuredHeight();
		int left = l + getPaddingLeft();
		int top = t + getPaddingTop();
		int right = left + width + getPaddingRight();
		int bottom = top + height + getPaddingBottom();

		leftView.layout(left - width, top, left, bottom);
		currentRow.layout(left, top, right, bottom);
		rightView.layout(right, top, right + width, bottom);
		int scrollX = getScrollX();
		scrollTo(0, getScrollY());

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int currentX = (int) ev.getX();
		int currentY = (int) ev.getY();

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isIntercept = false;
			lastX = currentX;
			lastY = currentY;
			break;
		case MotionEvent.ACTION_MOVE:
			if (Math.abs(lastX - currentX) > Math.abs(lastY - currentY) * 2 && Math.abs(lastX - currentX) > touchSlop) {
				isIntercept = true;
				super.dispatchTouchEvent(ev);
			}
			break;

		default:
			break;
		}

		lastX = currentX;
		lastY = currentY;
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		lastX = (int) ev.getX();
		lastY = (int) ev.getY();
		return isIntercept;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int currentX = (int) event.getX();
		int currentY = (int) event.getY();

		int deltaX = currentX - lastX;
		int deltaY = currentY - lastY;

		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			scrollBy(-deltaX, 0);
			break;
		case MotionEvent.ACTION_UP:
			if (getScrollX() > getMeasuredWidth() / 4) {
				scrollToNext();
			} else if (getScrollX() < -getMeasuredWidth() / 4) {
				scrollToPre();
			} else {
				scrollToCurrent();
			}
			isIntercept = false;
			break;

		default:
			break;
		}
		lastX = currentX;
		lastY = currentY;
		return super.onTouchEvent(event);
	}

	private void scrollToNext() {
		direction = Direction.next;
		smoothScroollTo(getMeasuredWidth(), 0);
	}

	private void scrollToPre() {
		direction = Direction.pre;
		smoothScroollTo(-getMeasuredWidth(), 0);
	}

	private void scrollToCurrent() {
		direction = Direction.current;
		smoothScroollTo(0, 0);
	}

	private void smoothScroollTo(int destX, int destY) {
		isScrolling = true;
		scroller.startScroll(getScrollX(), getScrollY(), destX - getScrollX(), destY - getScrollY());
		postInvalidate();
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			invalidate();
		} else {
			if (isScrolling) {
				if (direction == Direction.next) {
					currentCalendar = CalendarLoader.getNextWeekCalendar(currentCalendar);

					Calendar calendar = CalendarLoader.getFirstDayOfWeek(currentCalendar);
					setSelectedDayCalendar(calendar);

					CalendarRow row = loadWeekRow(CalendarLoader.loadNextWeek(currentCalendar));
					addView(row, param);
					removeViewAt(0);
				} else if (direction == Direction.pre) {
					currentCalendar = CalendarLoader.getPreWeekCalendar(currentCalendar);
					Calendar calendar = CalendarLoader.getFirstDayOfWeek(currentCalendar);
					setSelectedDayCalendar(calendar);

					CalendarRow row = loadWeekRow(CalendarLoader.loadPreWeek(currentCalendar));
					addView(row, 0, param);
					removeViewAt(3);
				}
			}
			isScrolling = false;
		}
	}

	/**
	 * 加载一周的视图
	 * 
	 * @param week
	 * @return
	 */
	private CalendarRow loadWeekRow(Week week) {
		CalendarRow row = new CalendarRow(getContext());
		for (int i = 0; i < 7; i++) {
			Day day = week.getDays().get(i);
			row.addDay(day);
		}
		return row;
	}

	@Override
	public void setSelectedDayCalendar(Calendar calendar) {
		selectedDayCalendar = calendar;
		((AutoScrollCalendarParentLayout) getParent()).setSelectedDayCalendar(selectedDayCalendar);
		for (int i = 0; i < getChildCount(); i++) {
			ViewGroup row = (ViewGroup) getChildAt(i);
			for (int j = 0; j < row.getChildCount(); j++) {
				row.getChildAt(j).invalidate();
			}
		}
	}

	@Override
	public Calendar getSelectedDayCalendar() {
		return selectedDayCalendar;
	}

}
