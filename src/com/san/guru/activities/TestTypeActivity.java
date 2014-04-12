package com.san.guru.activities;

import static com.san.guru.constant.AppConstants.*;
import static com.san.guru.constant.AppConstants.INTENT_NUM_QUESTION;
import static com.san.guru.constant.AppConstants.MODE;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.san.guru.R;
import com.san.guru.constant.Mode;
import com.san.guru.dto.IntentData;
import com.san.guru.model.QuestionBank;
import com.san.guru.util.Dialog;
import com.san.guru.util.ICallback;

public class TestTypeActivity extends AbstractActivity {

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_home:
	        	Dialog.show(this, "Are you sure ?", "Home", new ICallback() {
					
					@Override
					public void call(Object obj) {
						Intent intent = new Intent(TestTypeActivity.this, ChooseSubjectActivity.class);
						TestTypeActivity.this.startActivity(intent);
						
						finish();
					}
				}, false);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_choose_test_type);
		
		final Button nextButton = (Button) findViewById(R.id.butTrdTypNext);
		final Button prevButton = (Button) findViewById(R.id.butTrdTypPrev);
		
		TextView text = (TextView) findViewById(R.id.textNumQuestion);
		text.setTextSize(15);
		
		NumberPicker numPicker = (NumberPicker) findViewById(R.id.numberPicker1);
		numPicker.setMaxValue(50);
		numPicker.setMinValue(20);
		
		// Used atomic interger just to make integer as final.
		final AtomicInteger numQuestion = new AtomicInteger(numPicker.getValue());
		
		numPicker.setOnValueChangedListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker numberpicker, int oldValue, int newValue) {
				numQuestion.set(newValue);
			}
		});
		
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				nextButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				prevButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
				QuestionBank.getNewInstance(v.getContext());
				
				Intent intent = new Intent(TestTypeActivity.this, QuestionActivity.class);
				IntentData intentData = restoreIntent(intent, TestTypeActivity.this.getIntent());
				
				// Assuming intentdata will always present.
				intentData.putValue(INTENT_NUM_QUESTION, numQuestion.get());
				
		        TestTypeActivity.this.startActivity(intent);
		        
		        finish();
			}
		});
		
		prevButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				prevButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				nextButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
				Intent intent = new Intent(TestTypeActivity.this, ChooseSubjectActivity.class);
				IntentData intentData = restoreIntent(intent, TestTypeActivity.this.getIntent());
				
		        TestTypeActivity.this.startActivity(intent);
		        
		        finish();
			}
		});
		
		setHeightOfMainArea();
	}
	
	private IntentData restoreIntent(Intent to, Intent from) {
		IntentData intentData = null;
		if (from.getExtras() != null) {
			intentData = (IntentData) from.getExtras().get(INTENT_DATA);
			intentData.putValue(MODE, Mode.TEST);
			to.putExtra(INTENT_DATA, intentData);
		} 
		
		return intentData;
	}
}