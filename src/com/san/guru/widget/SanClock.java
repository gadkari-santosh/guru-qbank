package com.san.guru.widget;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import com.san.guru.util.DateTimeUtils;

import android.os.Handler;
import android.widget.TextView;

public class SanClock {

	private TextView textView = null;

	private Handler myHandler = new Handler();

	private AtomicInteger seconds = new AtomicInteger(0);
	
	private Runnable myRunnable = null;
	
	private Timer myTimer = null;
	
	private boolean isCountDown = false;
	
	public SanClock(TextView textView) {
		this.textView = textView;
	}
	
	public SanClock() {
		// NOP
	}
	
	public SanClock(int countDown,TextView textView) {
		seconds = new AtomicInteger(countDown);
		this.textView = textView;
		
		isCountDown = true;
	}
	
	public void setTextView(TextView textView) {
		this.textView = textView;
	}

	public void start() {
		myRunnable = new Runnable() {
			public void run() {
				int secs = 0;
				
				if (isCountDown)
					secs = seconds.decrementAndGet();
				else
					secs = seconds.incrementAndGet();
				
			    String time = DateTimeUtils.getTimeString(secs);

				textView.setText(time);
			}
		};

		myTimer = new Timer();
		myTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				UpdateGUI();
			}
		}, 0, 1000);
	}

	private void UpdateGUI() {
		myHandler.post(myRunnable);
	}
	
	public void stopStart() {
		stop();
		start();
	}

	public void stop() {
		if (myTimer != null)
			myTimer.cancel();
	}
	
	public int getElaspedTime() {
		return seconds.get();
	}
}
