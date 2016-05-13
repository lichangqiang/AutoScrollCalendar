package com.magicwork.widget;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.magicwork.autoscrollcalendar.R;
import com.magicwork.widget.engine.CalendarLoader;
import com.magicwork.widget.entity.Month;
import com.magicwork.widget.view.AutoScrollCalendarView;
import com.magicwork.widget.view.AutoScrollCalendarView.OnCalendarChangeListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	AutoScrollCalendarView calendarView;
	TextView tvCalendar;
	TextView tvPreMonth;
	TextView tvNextMonth;
	TextView tvContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		calendarView = (AutoScrollCalendarView) findViewById(R.id.calendar_auto);
		tvCalendar = (TextView) findViewById(R.id.tv_calendar);
		tvPreMonth = (TextView) findViewById(R.id.tv_prev_month);
		tvNextMonth = (TextView) findViewById(R.id.tv_next_month);
		tvContent=(TextView) findViewById(R.id.tv_content);
		
		tvNextMonth.setOnClickListener(this);
		tvPreMonth.setOnClickListener(this);
		//tvContent.setOnClickListener(this);
		init();
	}

	public void init() {
		calendarView.setCurrentMonth(Calendar.getInstance());
		calendarView.setCalendarChangeListener(new OnCalendarChangeListener() {

			@Override
			public void onMonthChanged(Calendar currentCalendar) {
				showDate(currentCalendar);
			}
		});
		showDate(Calendar.getInstance());
	}
	
	
	public void showDate(Calendar calendar) {
		SimpleDateFormat formmater = new SimpleDateFormat("yyyy-MM-dd");
		tvCalendar.setText(formmater.format(calendar.getTime()));
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.tv_next_month) {
			calendarView.scrollToNext();
		} else if (id == R.id.tv_prev_month) {
			calendarView.scrollToPre();
		} else if (id == R.id.tv_calendar) {
			
		}
	}
}
