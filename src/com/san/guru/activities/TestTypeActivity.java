package com.san.guru.activities;

import static com.san.guru.constant.AppContents.INTENT_DATA;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.san.guru.R;

public class TestTypeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_choose_test_type);
		
		Button button1 = (Button) findViewById(R.id.butSubmitSkillSet);
		
		Button nextButton = (Button) findViewById(R.id.butTrdTypNext);
		Button prevButton = (Button) findViewById(R.id.butTrdTypPrev);
		
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TestTypeActivity.this, MainActivity.class);
				restoreIntent(intent, TestTypeActivity.this.getIntent());
				
		        TestTypeActivity.this.startActivity(intent);
		        
		        finish();
			}
		});
		
		prevButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TestTypeActivity.this, SkillSetActivity.class);
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