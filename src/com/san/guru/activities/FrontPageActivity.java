package com.san.guru.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.san.guru.R;

public class FrontPageActivity extends Activity {

	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_first_page);
		
		 int secondsDelayed = 3;
	        new Handler().postDelayed(new Runnable() {
	                public void run() {
	                	Intent intent = new Intent(FrontPageActivity.this, SkillSetActivity.class);
	                	FrontPageActivity.this.startActivity(intent);
	                    finish();
	                }
	        }, secondsDelayed * 1000);
	}
}