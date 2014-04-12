/*
 * Copyright (C) 2014 - 2025
 *
 * This software is copyrighted. The software may not be copied, reproduced, 
 * translated or reduced to any electronic medium or machine-readable form without 
 * the prior written consent of "Santosh Gadkari", except that you may make one copy 
 * of the program disks solely for back-up purposes.
 * 
 * I have worked very hard to create a quality product and wish to realize 
 * the fair fruits of our labor. We therefore insist that you honor our copyright.
 * However,we want to encourage the use of our product in all possible circumstances and 
 * will work very hard to meet your needs.
 * 
 */
package com.san.guru.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.san.guru.R;
import com.san.guru.model.QuestionBank;
import com.san.guru.model.Subjects;

/**
 * This Activity shows splash screen.
 * 
 * @author Santosh Gadkari (gadkari.santosh@gmail.com)
 */
public class SplashScreenActivity extends Activity {

	/** Splash screen delay in seconds. */
	private static final int DELAY = 3000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_first_page);
		
		// Lets initialize while user enjoy slash screen :)
		init();
		
	    new Handler().postDelayed(new Runnable() {
	           public void run() {
	                Intent intent = new Intent(SplashScreenActivity.this, ChooseSubjectActivity.class);
	                SplashScreenActivity.this.startActivity(intent);
	                finish();
	           }
	    }, DELAY);
	}
	
	// Santosh, it may happen that the init may take little longer than splash screen.
	// handle this scenario.
	private void init() {
		
		// 1. Load all subjects data.
		Subjects.getInstance().init(this);
		
		// 2. Load question bank.
		QuestionBank.getNewInstance(this);
	}
}