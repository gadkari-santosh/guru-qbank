package com.san.guru.activities;

import static com.san.guru.constant.AppConstants.INTENT_DATA;
import static com.san.guru.constant.AppConstants.INTENT_NUM_QUESTION;
import static com.san.guru.constant.AppConstants.MODE;
import static com.san.guru.constant.AppConstants.INTENT_TEST_TYPE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.san.guru.R;
import com.san.guru.constant.Mode;
import com.san.guru.constant.TEST_TYPE;
import com.san.guru.dto.IntentData;
import com.san.guru.model.QuestionBank;
import com.san.guru.util.Dialog;
import com.san.guru.util.ICallback;
import com.san.guru.util.ResourceUtils;

public class TestTypeActivity extends AbstractActivity {

	private int numQuestions = 20;
	private TEST_TYPE testType = TEST_TYPE.Practice;
	
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
	
	public void setTestType(View view) {
		RadioButton radioButton = (RadioButton)view;
		this.testType = TEST_TYPE.valueOf(String.valueOf( radioButton.getText()).replace(" ", "") );
		
		TextView helpText = (TextView) findViewById(R.id.txtViewHelp);
		
		switch (this.testType) {
			case Practice:
				helpText.setText(ResourceUtils.getString(this, R.string.help_practice));
				break;
			case RapidFire:
				helpText.setText(ResourceUtils.getString(this, R.string.help_rapid_fire));
				break;
			case Timed:
				helpText.setText(ResourceUtils.getString(this, R.string.help_Timed));
				break;
		}
	}
	
	public void setNumberOfQuestions(View view) {
		RadioButton radioButton = (RadioButton)view;
		this.numQuestions = Integer.parseInt(String.valueOf( radioButton.getText()) );
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_choose_test_type);
		
		TextView marqueeText=(TextView)findViewById(R.id.marquee_text);
		marqueeText.requestFocus();
		
		final Button nextButton = (Button) findViewById(R.id.butTrdTypNext);
		final Button prevButton = (Button) findViewById(R.id.butTrdTypPrev);
		
		TextView text = (TextView) findViewById(R.id.textNumQuestion);
		text.setTextSize(15);
		
		TextView helpText = (TextView) findViewById(R.id.txtViewHelp);
		helpText.setText(ResourceUtils.getString(this, R.string.help_practice));
		
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				nextButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				prevButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				
				QuestionBank.getNewInstance(v.getContext());
				
				Intent intent = new Intent(TestTypeActivity.this, QuestionActivity.class);
				IntentData intentData = restoreIntent(intent, TestTypeActivity.this.getIntent());
				
				//Remove after work
				intentData.putValue(INTENT_NUM_QUESTION, TestTypeActivity.this.numQuestions);
				intentData.putValue(INTENT_TEST_TYPE, TestTypeActivity.this.testType);
				
		        TestTypeActivity.this.startActivity(intent);
		        
		        finish();
			}
		});
		
		prevButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				prevButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				nextButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				
				Intent intent = new Intent(TestTypeActivity.this, ChooseSubjectActivity.class);
				
		        TestTypeActivity.this.startActivity(intent);
		        
		        finish();
			}
		});
		
		setNotice();
		
		setHeightOfMainArea();
	}
	
	protected void setHeightOfMainArea () {
		int height = 0;
		
		DisplayMetrics dimension = new DisplayMetrics();
		
		View gView2 = (View) findViewById(R.id.grid_2);
		View gView1 = (View) findViewById(R.id.grid_1);
		
		TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, getResources().getDisplayMetrics());

		getWindowManager().getDefaultDisplay().getMetrics(dimension);
		height = dimension.heightPixels;
		
		
		gView1.getLayoutParams().height = height - (gView2.getLayoutParams().height
									    + (int) (height * 0.20));
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