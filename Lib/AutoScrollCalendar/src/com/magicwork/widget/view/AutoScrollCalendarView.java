package com.magicwork.widget.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.magicwork.widget.engine.CalendarLoader;
import com.magicwork.widget.entity.Month;
import com.magicwork.widget.view.AutoScrollCalendarParentLayout.Direction;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.provider.Settings.Global;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class AutoScrollCalendarView extends ViewGroup implements ICalendarView {
	public List<Month> months = new ArrayList<Month>();
	private MarginLayoutParams param = new MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.WRAP_CONTENT);
	private Scroller scroller;
	private int touchSlop;

	private int lastRawX;
	private int lastRawY;
	private int touchRawX;
	private int touchRawY;
	private boolean isIntercept;
	private boolean isSCrolltoNext;
	private Calendar currentCalendar;
	private int startScrollX;
	private boolean isHoriztonalScrolling;
	private boolean isVerticalScrolling;
	private int verticalScrollHeight;// 上下滑动时高度的改变
	private int index;
	private CalendarView currentCalendarView;
	private SingleLineCalendarView singLineView;
	private boolean isMoveUp;
	private Calendar selectedDayCalendar = Calendar.getInstance();
	private ViewTreeObserver.OnGlobalLayoutListener layoutListener;

	public interface OnCalendarChangeListener {
		public void onMonthChanged(Calendar currentCalendar);
	}

	private OnCalendarChangeListener calendarChangeListener;

	Direction direction;
	private int lastY;
	private int lastX;

	public AutoScrollCalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Calendar getSelectedDayCalendar() {
		return selectedDayCalendar;
	}

	public void setSelectedDayCalendar(Calendar selectedDayCalendar) {
		this.selectedDayCalendar = selectedDayCalendar;
		((AutoScrollCalendarParentLayout) getParent()).setSelectedDayCalendar(selectedDayCalendar);
		String clendar = CalendarLoader.getSimpleDateStr(selectedDayCalendar);
		// 更新所有CalendarCell的状态
		for (int i = 0; i < currentCalendarView.getChildCount(); i++) {
			ViewGroup row = (ViewGroup) currentCalendarView.getChildAt(i);
			for (int j = 0; j < row.getChildCount(); j++) {
				row.getChildAt(j).invalidate();
			}
		}

	}

	public AutoScrollCalendarView(Context context) {
		super(context);
		init();
	}

	public void init() {
		touchSlop = 8;
		scroller = new Scroller(getContext());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int heightSize = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
		int widthSize = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();

		heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST);
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);

		measureChildren(widthMeasureSpec, heightMeasureSpec);
		heightSize = getChildAt(1).getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - getScrollY() - verticalScrollHeight;
		setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		View currentView = getChildAt(1);
		View leftView = getChildAt(0);
		View rightView = getChildAt(2);
		currentCalendarView = (CalendarView) getChildAt(1);

		int left = getPaddingLeft();
		int top = getPaddingTop();
		int right = left + currentView.getMeasuredWidth() - getPaddingRight();
		int bottom = top + currentView.getMeasuredHeight() - getPaddingBottom();

		if (currentView != null) {
			currentView.layout(left, top, right, bottom);
		}

		int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

		if (leftView != null) {
			leftView.layout(left - width, top, left, bottom);
		}
		if (rightView != null) {
			rightView.layout(right, top, right + width, bottom);
		}
		scrollTo(0, getScrollY());// 更新当前View状态
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int currenRawY = (int) ev.getRawY();
		int curentRawX = (int) ev.getRawX();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isIntercept = false;
			lastRawX = (int) ev.getRawX();
			lastRawY = (int) ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			// 判断是否是横向滚动，若是则拦截事件
			if (Math.abs(lastRawX - curentRawX) > touchSlop && Math.abs(lastRawX - curentRawX) > 2 * Math.abs(lastRawY - currenRawY)) {
				// return true;
				isIntercept = true;
				return super.dispatchTouchEvent(ev);
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		default:
			break;
		}
		lastRawX = curentRawX;
		lastRawY = currenRawY;
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		touchRawX = (int) ev.getRawX();
		touchRawY = (int) ev.getRawY();
		startScrollX = getScrollX();
		return isIntercept;
	}

	/**
	 * 负责Calendar的左右滑动
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Log.e(VIEW_LOG_TAG,
		// "current"+CalendarLoader.getSimpleDateStr(currentCalendar));
		int currentRawX = (int) event.getRawX();
		int currentRawY = (int) event.getRawY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			scrollBy(-(currentRawX - touchRawX), 0);
			break;
		case MotionEvent.ACTION_UP:
			int scrollX = getScrollX() - startScrollX;// 计算每次移动的真实长度
			if (scrollX > getMeasuredWidth() / 4) {
				scrollToNext();
			} else if (scrollX < -getMeasuredWidth() / 4) {
				scrollToPre();
			} else {
				scrollToCurrent();
			}
			break;
		default:
			break;
		}
		touchRawY = currentRawY;
		touchRawX = currentRawX;
		return true;
	}

	/**
	 * 接受从父控件手动传来的事件
	 * 
	 * @param event
	 */
	public void onRequireTouchEvent(MotionEvent event) {
		int currentX = (int) event.getRawX();
		int currentY = (int) event.getRawY();
		int deltaY = currentY - lastY;
		int deltaX = currentX - lastX;
		Log.e(VIEW_LOG_TAG, "MotionEvent*****" + event.getAction());
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isMoveUp = false;
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (isVerticalScrollable(deltaX, deltaY)) {
				if (isMoveUp(deltaY)) {
					if (getScrollY() >= currentCalendarView.getRowViewTopAt(selectedDayCalendar)) {
						if (getMeasuredHeight() - Math.abs(deltaY) <= currentCalendarView.getRowHeight()) {
							deltaY = -(getMeasuredHeight() - currentCalendarView.getRowHeight());
						}
						verticalScrollHeight -= deltaY;
					} else {
						if (getScrollY() + Math.abs(deltaY) >= currentCalendarView.getRowViewTopAt(selectedDayCalendar)) {
							scrollTo(0, currentCalendarView.getRowViewTopAt(selectedDayCalendar));
						} else {
							scrollBy(0, -deltaY);
						}
					}
					requestLayout();
				} else {
					if (getScrollY() <= 0) {
						if (getMeasuredHeight() + Math.abs(deltaY) >= currentCalendarView.getMeasuredHeight()) {
							deltaY = -(getMeasuredHeight() - currentCalendarView.getMeasuredHeight());
						}
						verticalScrollHeight -= deltaY;
					} else {
						if (getScrollY() - deltaY < 0) {
							scrollTo(0, 0);
						} else {
							scrollBy(0, -deltaY);
						}
					}
					requestLayout();
				}

			}

			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:

			if (isMoveUp(0)) {
				collapseScrollView();
			} else {
				expandScrollView();
			}

			break;

		default:
			break;
		}
		lastX = currentX;
		lastY = currentY;
	}

	/**
	 * 收缩View
	 */
	public void collapseScrollView() {
		if (getScrollY() >= currentCalendarView.getRowViewTopAt(selectedDayCalendar)) {
			reduceViewHeight();
		} else {
			isVerticalScrolling = true;
			smoothScroollTo(0, currentCalendarView.getRowViewTopAt(selectedDayCalendar));
		}
		requestLayout();
	}

	public void expandScrollView() {
		if (getScrollY() <= 0) {
			addViewHeight();
		} else {
			isVerticalScrolling = true;
			smoothScroollTo(0, 0);
		}
		requestLayout();
	}

	/**
	 * 转换到单行模式
	 */
	public void setToSingleLineState() {
		requestLayout();
		scrollTo(0, currentCalendarView.getRowViewTopAt(selectedDayCalendar));
		verticalScrollHeight = currentCalendarView.getMeasuredHeight() - currentCalendarView.getRowHeight() - getPaddingTop() - getPaddingBottom() - getScrollY();
		requestLayout();
	}

	/**
	 * 不断增加View的高度到达动画效果
	 */
	public void addViewHeight() {
		post(new Runnable() {

			@Override
			public void run() {
				int deltaY = 40;
				if (getMeasuredHeight() >= currentCalendarView.getMeasuredHeight()) {
					return;
				}

				if (getMeasuredHeight() + deltaY >= currentCalendarView.getMeasuredHeight()) {
					deltaY = currentCalendarView.getMeasuredHeight() - getMeasuredHeight();
				}
				verticalScrollHeight -= deltaY;
				requestLayout();
				addViewHeight();
			}
		});
	}

	/**
	 * 不断的减少View的高度达到动画效果
	 */
	public void reduceViewHeight() {
		post(new Runnable() {

			@Override
			public void run() {
				int deltaX = 40;
				if (getMeasuredHeight() == currentCalendarView.getRowHeight()) {
					((AutoScrollCalendarParentLayout) getParent()).showScrllCalendar(false);
					return;
				}

				if (getMeasuredHeight() - (deltaX) <= currentCalendarView.getRowHeight()) {
					verticalScrollHeight += (getMeasuredHeight() - currentCalendarView.getRowHeight());
				} else {
					verticalScrollHeight += deltaX;
				}
				requestLayout();
				reduceViewHeight();
			}
		});
	}

	/**
	 * 判断当前是否有条件竖向滚动
	 * 
	 * @param deltaX
	 * @param deltaY
	 * @return
	 */
	public boolean isVerticalScrollable(int deltaX, int deltaY) {
		boolean flag = false;
		if (Math.abs(deltaY) > 2 * Math.abs(deltaX)) {
			if (getMeasuredHeight() >= currentCalendarView.getRowHeight() && getMeasuredHeight() <= currentCalendarView.getMeasuredHeight()) {
				flag = true;
			}

		}

		return flag;
	}

	public void updateHeight(int height) {
		int rowHeight = currentCalendarView.getRowHeight();
		int viewHeight = currentCalendarView.getMeasuredHeight();
		if (height > currentCalendarView.getRowHeight() && height < currentCalendarView.getMeasuredHeight()) {
			getLayoutParams().height = height;
			requestLayout();
		}
	}

	public boolean isMoveUp(int deltaY) {
		if (deltaY < 0) {
			isMoveUp = true;
			return true;
		} else if (deltaY > 0) {
			isMoveUp = false;
			return false;
		} else {
			return isMoveUp;
		}
	}

	public void scrollToNext() {
		direction = Direction.next;
		isHoriztonalScrolling = true;
		smoothScroollTo(getMeasuredWidth(), 0);
	}

	public void scrollToPre() {
		direction = Direction.pre;
		isHoriztonalScrolling = true;
		smoothScroollTo(-getMeasuredWidth(), 0);
	}

	private void scrollToCurrent() {
		direction = Direction.current;
		isHoriztonalScrolling = true;
		smoothScroollTo(0, 0);
	}

	private void smoothScroollTo(int destX, int destY) {
		scroller.startScroll(getScrollX(), getScrollY(), destX - getScrollX(), destY - getScrollY());
		postInvalidate();
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			invalidate();
			if (isVerticalScrolling) {
				requestLayout();
			}
		} else {
			if (isHoriztonalScrolling) {
				if (direction == Direction.next) {// 当动画滚动完成后，则增加月历并删除第一月历
					currentCalendar = CalendarLoader.getNextMonthCalendar(currentCalendar);
					setSelectedDayCalendar(CalendarLoader.getFirstDayOfMonth(currentCalendar));
					addMonthView(CalendarLoader.getNextMonthCalendar(currentCalendar));
					removeViewAt(0);
					updateViewAfterLayout();
				} else if (direction == Direction.pre) {// 当动画滚动完成后，则删除月历并删除最后一个月历
					currentCalendar = CalendarLoader.getPreMonthCalendar(currentCalendar);
					setSelectedDayCalendar(CalendarLoader.getFirstDayOfMonth(currentCalendar));
					addMonthView(CalendarLoader.getPreMonthCalendar(currentCalendar), 0);
					removeViewAt(3);
					updateViewAfterLayout();
				}
				isHoriztonalScrolling = false;
				if (calendarChangeListener != null) {
					calendarChangeListener.onMonthChanged(currentCalendar);
				}
			} else if (isVerticalScrolling) {
				isVerticalScrolling = false;
				if (isMoveUp(0)) {
					reduceViewHeight();
				} else {
					addViewHeight();
				}
			}
		}
	}

	public void setCurrentMonth(Calendar startCalendar) {
		this.currentCalendar = startCalendar;
		this.selectedDayCalendar = startCalendar;
		scrollTo(0, 0);
		verticalScrollHeight = 0;
		removeAllViews();
		addMonthView(CalendarLoader.getPreMonthCalendar(currentCalendar));
		addMonthView(startCalendar);
		addMonthView(CalendarLoader.getNextMonthCalendar(currentCalendar));

	}

	public void updateViewAfterLayout() {
		layoutListener = new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				setSelectedDayCalendar(selectedDayCalendar);
				getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
			}
		};
		getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
	}

	public void addMonthView(Calendar calendar) {
		addMonthView(calendar, -1);
	}

	public void addMonthView(Calendar calendar, int pos) {
		try {
			pos = pos < 0 ? getChildCount() : pos;
			Month month = CalendarLoader.loadMonth(calendar);
			if (month != null) {
				CalendarView calendarView = new CalendarView(getContext());
				calendarView.setMonth(month);
				addView(calendarView, pos, param);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCalendarChangeListener(OnCalendarChangeListener calendarChangeListener) {
		this.calendarChangeListener = calendarChangeListener;
	}

}
