package com.san.guru.activities;

import static com.san.guru.constant.AppConstants.INTENT_DATA;
import static com.san.guru.constant.AppConstants.INTENT_RECORDS;
import static com.san.guru.constant.AppConstants.INTENT_RESULT;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.san.guru.R;
import com.san.guru.dto.IntentData;
import com.san.guru.dto.TestResult;
import com.san.guru.widget.SavedResultAdapter;

public class RecordsActivity extends AbstractActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final List<TestResult> list = new ArrayList<TestResult>();
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_show_records);
		
		ListView listView = (ListView) findViewById(R.id.listViewRecords);
		
		Intent intent = getIntent();
		if (intent != null && intent.getExtras() != null) {
			IntentData intentData = (IntentData)intent.getExtras().get(INTENT_DATA);
			
			List listResults = (List<TestResult>) intentData.getValue(INTENT_RECORDS);
			if (listResults != null) {
				list.addAll(listResults);
			}
		}
		
		listView.setAdapter(new SavedResultAdapter(this, list));
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				TestResult result = list.get(position);
				IntentData intentData = new IntentData();
				intentData.putValue(INTENT_RESULT, result);
				
				Intent intent = new Intent(RecordsActivity.this, ResultActivity.class);
				intent.putExtra(INTENT_DATA, intentData);
				
				RecordsActivity.this.startActivity(intent);
				finish();
			}
			
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
