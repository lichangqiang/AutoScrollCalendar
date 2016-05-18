package com.magicwork.widget.view;

import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;

/**
 * 日历控件的父控件，当在父控件进行上下滑动时会传导事件到日历控件
 * 
 * @author licq
 *
 */
public class AutoScrollCalendarParentLayout extends LinearLayout {

	public interface OnSelectedDayCalendarChangeListener {
		void onSelectedDayChanged(Calendar newCalendar);
	}

	public enum Direction {
		pre, current, next
	}

	AutoScrollCalendarView scrollCalendarView;
	SingleLineCalendarView singleLineCalendarView;
	int lastX;
	int lastY;
	boolean isIntercept;
	boolean isShowScrollCalendarView = true;// 当前是周模式还是月模式
	OnGlobalLayoutListener layoutListener = null;

	OnSelectedDayCalendarChangeListener selectedDayChangedListener;

	Calendar selectedCalendar = Calendar.getInstance();

	public AutoScrollCalendarParentLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(VERTICAL);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		// 找到日历控件
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i) instanceof AutoScrollCalendarView) {
				scrollCalendarView = (AutoScrollCalendarView) getChildAt(i);
				singleLineCalendarView = new SingleLineCalendarView(getContext(), Calendar.getInstance());
				addView(singleLineCalendarView, i + 1);
				singleLineCalendarView.setCurrentCalendar(selectedCalendar);
				scrollCalendarView.setCurrentMonth(selectedCalendar);
				break;
			}
		}
		// showScrllCalendar(isShowScrollCalendarView);
		requestLayout();
		layoutListener = new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
				showScrllCalendar(false);
			}
		};
		getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int currentX = (int) ev.getX();
		int currentY = (int) ev.getY();
		int deltaX = currentX - lastX;
		int deltaY = currentY - lastY;
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isIntercept = false;
			lastX = (int) ev.getX();
			lastY = (int) ev.getY();
			scrollCalendarView.onRequireTouchEvent(ev);
			break;
		case MotionEvent.ACTION_MOVE:
			if (Math.abs(deltaY) > Math.abs(deltaX) * 3 && !singleLineCalendarView.isIntercept) {// 判断是否是纵向滑动，若是纵向滑动则直接将控件事件拦截
				if (isShowScrollCalendarView) {
					isIntercept = true;
				} else {
					showScrllCalendar(true);
				}

			}
			break;
		case MotionEvent.ACTION_UP:

			break;
		default:
			break;
		}
		lastX = currentX;
		lastY = currentY;
		// 传导事件到子控件
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return isIntercept;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		scrollCalendarView.onRequireTouchEvent(event);
		return true;
	}

	public void showScrllCalendar(boolean isShow) {
		if (isShowScrollCalendarView == isShow)
			return;
		isShowScrollCalendarView = isShow;
		if (isShow) {
			scrollCalendarView.setVisibility(View.VISIBLE);
			singleLineCalendarView.setVisibility(View.GONE);
			scrollCalendarView.setCurrentMonth(selectedCalendar);
			scrollCalendarView.setToSingleLineState();
		} else {
			scrollCalendarView.setVisibility(View.GONE);
			singleLineCalendarView.setVisibility(View.VISIBLE);
			singleLineCalendarView.setCurrentCalendar(selectedCalendar);
		}
	}

	public void setSelectedDayCalendar(Calendar calendar) {
		selectedCalendar = (Calendar) calendar.clone();
		if (selectedDayChangedListener != null) {
			selectedDayChangedListener.onSelectedDayChanged(selectedCalendar);
		}
	}

	public void setSelectedDayChangedListener(OnSelectedDayCalendarChangeListener selectedDayChangedListener) {
		this.selectedDayChangedListener = selectedDayChangedListener;
	}

}
