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

import static com.san.guru.constant.AppConstants.INTENT_DATA;
import static com.san.guru.constant.AppConstants.INTENT_SKILL_SET;
import static com.san.guru.constant.Color.GRAY;
import static com.san.guru.constant.Color.STEEL_BLUE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.san.guru.R;
import com.san.guru.dto.IntentData;
import com.san.guru.model.Subjects;

/**
 * 
 * This android activity shows various subjects.
 * 
 * Subject data provided inside /assets/subjects.xml
 * 
 * @author Santosh Gadkari (gadkari.santosh@gmail.com)
 * 
 */
public class ChooseSubjectActivity extends AbstractActivity {

	HashSet<CharSequence> checkedItems = new HashSet<CharSequence>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_choose_subject);
		
		handleDownloadButton();
		
		// Populate subjects on list view.
		final ListView listViewSubjects = handleListViewSubjects(Subjects.getInstance().getSubjects());
		
		// handle button events.
		handleMeButton();
		handleNextButton(listViewSubjects);
		
		// After pulling hairs finally, decided to set Listview hight at runtime.
		// Issue was, ListView used to occupy whole screen.
		setHeightOfMainArea();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_quit:
	        	finish();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void handleMeButton() {
		final Button buttonNext = (Button) findViewById(R.id.buttonNext);
		final Button buttonMe   = (Button) findViewById(R.id.buttonMe);

		buttonMe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				view.setBackgroundColor(Color.parseColor(STEEL_BLUE));
				buttonNext.setBackgroundColor(Color.parseColor(GRAY));
				
				Toast toast = Toast.makeText(view.getContext(), "Your Profile", 121);
				toast.show();
				//TODO - Show history of the user
			}
		});
	}
	
	private void handleDownloadButton() {
		final Button buttonDownload = (Button) findViewById(R.id.buttonDownload);
		
		final Button buttonNext = (Button) findViewById(R.id.buttonNext);
		final Button buttonMe   = (Button) findViewById(R.id.buttonMe);

		buttonDownload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				view.setBackgroundColor(Color.parseColor(STEEL_BLUE));
				buttonNext.setBackgroundColor(Color.parseColor(GRAY));
				
				
				Intent intent = new Intent(ChooseSubjectActivity.this, DownloadActivity.class);
				ChooseSubjectActivity.this.startActivity(intent);
				
				finish();
			}
		});
	}
	
	private void handleNextButton(final ListView listViewSubjects) {
		
		final Button buttonNext = (Button) findViewById(R.id.buttonNext);
		final Button buttonMe   = (Button) findViewById(R.id.buttonMe);

		buttonNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				view.setBackgroundColor(Color.parseColor(STEEL_BLUE));
				buttonMe.setBackgroundColor(Color.parseColor(GRAY));

				IntentData data        = new IntentData();
				SparseBooleanArray sba = null;
				Intent intent 		   = null;
				
				sba = listViewSubjects.getCheckedItemPositions();

				if (checkedItems != null && checkedItems.size() == 0) {
					Toast toast = Toast.makeText(view.getContext(), "Please select subjects.", 121);
					toast.show();
					
					return;
				}
				
				data.putValue(INTENT_SKILL_SET, checkedItems);

				intent = new Intent(ChooseSubjectActivity.this, TestTypeActivity.class);
				intent.putExtra(INTENT_DATA, data);
				ChooseSubjectActivity.this.startActivity(intent);

				finish();
			}
		});
	}
	
	private ListView handleListViewSubjects(List<String> subjectNames) {
		
		final ListView listViewSubjects = (ListView) findViewById(R.id.listViewSubjects);
		
		final List<String> fullList = new ArrayList();
		fullList.add("All");
		fullList.addAll(subjectNames);
		
		
		listViewSubjects.setAdapter(new ItemsAdapter(this, fullList));
		
		listViewSubjects.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		listViewSubjects.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, 
								    View arg1, 
								    int arg2,
								    long arg3) {
				
				Button buttonNext = (Button) findViewById(R.id.buttonNext);
				buttonNext.setBackgroundColor(Color.parseColor(GRAY));

				CheckedTextView view = ((CheckedTextView) arg1);
				if (view.isChecked() && view.getText().equals("All") && !checkedItems.contains("All")) {
					checkedItems.addAll(fullList);
					
					checkedItems.add("All");
					for (int i=0; i<listViewSubjects.getChildCount(); i++) {
						CheckedTextView childAt = (CheckedTextView) listViewSubjects.getItemAtPosition(i);
						childAt.setChecked(true);
						listViewSubjects.setItemChecked(i, true);
						childAt.setSelected(true);
					}
				} else if (!view.isChecked() && view.getText().equals("All")){
					checkedItems.clear();
					for (int i=0; i<listViewSubjects.getChildCount(); i++) {
						CheckedTextView childAt = (CheckedTextView) listViewSubjects.getItemAtPosition(i);
						childAt.setChecked(false);
						listViewSubjects.setItemChecked(i, false);
						childAt.setSelected(false);
					}
				} else if (view.isChecked() && !view.getText().equals("All")) {
					checkedItems.add(view.getText());
					CheckedTextView childAt = (CheckedTextView) listViewSubjects.getItemAtPosition(arg2);
					
					listViewSubjects.setItemChecked(arg2, true);
					
					childAt.getText();
					childAt.setChecked(true);
					childAt.setSelected(true);
				} else {
					checkedItems.remove(view.getText());
					CheckedTextView childAt = (CheckedTextView) listViewSubjects.getItemAtPosition(arg2);
					childAt.setChecked(false);
					childAt.setSelected(false);
					childAt.getText();
					listViewSubjects.setItemChecked(arg2, false);
				}
			}
		});
		
		return listViewSubjects;
	}
	
	
	/*
	 * Adapter for List TextView items.
	 */
	private class ItemsAdapter extends BaseAdapter {

		private List<String> items  = null;
		
		private List<CheckedTextView> cViews = new ArrayList<CheckedTextView>();

		public ItemsAdapter(Context context, List<String> item) {
			this.items   = item;

			Intent intent = ((Activity) context).getIntent();
			if (intent != null && intent.getExtras() != null) {
				IntentData data = (IntentData) intent.getExtras().get(INTENT_DATA);

				if (data != null) {
					checkedItems = (HashSet) data.getValue(INTENT_SKILL_SET);
				}
			}
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {

			CheckedTextView post = null;
			
			if (view == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.items, null);
			}

			post = (CheckedTextView) view.findViewById(R.id.checkList);
			post.setText(items.get(position));

			if (checkedItems.contains(items.get(position))) {
				((ListView) parent).setItemChecked(position, true);
			} else {
				((ListView) parent).setItemChecked(position, false);
			}

			cViews.add(post);

			return view;
		}

		public int getCount() {
			return items.size();
		}

		public Object getItem(int position) {
			if (cViews.size() < position)
				return null;
			return cViews.get(position);
		}

		public long getItemId(int position) {
			return position;
		}
	}
}