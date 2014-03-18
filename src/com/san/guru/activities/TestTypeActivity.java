package com.san.guru.activities;

import static com.san.guru.constant.AppContents.INTENT_DATA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.Spinner;

import com.san.guru.R;
import com.san.guru.util.Dialog;
import com.san.guru.util.ICallback;

public class TestTypeActivity extends Activity {

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_home:
	        	Dialog.show(this, "Are you sure ?", "Home", new ICallback() {
					
					@Override
					public void call() {
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
		
//		ActionBar ab = getActionBar();
//	    ab.setHomeButtonEnabled(true);
//	    ab.setDisplayHomeAsUpEnabled(true);
//	    ab.setNavigationMode(ActionBar.DISPLAY_SHOW_HOME);
//	    ab.show();

		setContentView(R.layout.layout_choose_test_type);
		
		final Button nextButton = (Button) findViewById(R.id.butTrdTypNext);
		final Button prevButton = (Button) findViewById(R.id.butTrdTypPrev);
		
		Spinner spinner2 = (Spinner) findViewById(R.id.spinner1);
		NumberPicker findViewById = (NumberPicker) findViewById(R.id.numberPicker1);
		findViewById.setMaxValue(50);
		findViewById.setMinValue(20);
		List<String> list = new ArrayList<String>();
		list.add("Practice Test");
		list.add("Timed Test");
		list.add("Rapid Fire");
		spinner2.setPrompt("Select test type");
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_dropdown_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spinner2.setAdapter(dataAdapter);

		
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				nextButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				prevButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
				Intent intent = new Intent(TestTypeActivity.this, QuestionActivity.class);
				restoreIntent(intent, TestTypeActivity.this.getIntent());
				
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
				restoreIntent(intent, TestTypeActivity.this.getIntent());
				
		        TestTypeActivity.this.startActivity(intent);
		        
		        finish();
			}
		});
	}
	
	private void restoreIntent(Intent to, Intent from) {
		if (from.getExtras() != null)
			to.putExtra(INTENT_DATA, (Serializable) from.getExtras().get(INTENT_DATA));
	}
}