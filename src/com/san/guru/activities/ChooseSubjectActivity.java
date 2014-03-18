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

import static com.san.guru.constant.AppContents.INTENT_DATA;
import static com.san.guru.constant.AppContents.INTENT_SKILL_SET;
import static com.san.guru.constant.Color.GRAY;
import static com.san.guru.constant.Color.STEEL_BLUE;

import java.util.ArrayList;
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
public class ChooseSubjectActivity extends Activity {

	final ArrayList<CharSequence> checkedItems = new ArrayList<CharSequence>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_choose_subject);
		
		// Populate subjects on list view.
		final ListView listViewSubjects = handleListViewSubjects(Subjects.getInstance().getSubjects());
		
		// handle button events.
		handleMeButton();
		handleNextButton(listViewSubjects);
		
		
		// After pulling hairs finally, decided to set Listview hight at runtime.
		// Issue was, ListView used to occupy whole screen.
		setListViewHight();
		
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

				checkedItems.clear();

				for (int cnt = 0; cnt < sba.size(); cnt++) {
					int keyAt = sba.keyAt(cnt);

					CheckedTextView ctv = (CheckedTextView) listViewSubjects.getItemAtPosition(keyAt);
					if (ctv.isChecked()) {
						
						if (ctv.getText().equals("All"))
							continue;
						
						checkedItems.add(ctv.getText());
					}
				}
				
				if (checkedItems.size() == 0) {
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
	
	private void setListViewHight () {
		int height = 0;
		
		DisplayMetrics dimension = new DisplayMetrics();
		
		View gView1 = (View) findViewById(R.id.grid_1);
		View gView2 = (View) findViewById(R.id.grid_2);
		View gView3 = (View) findViewById(R.id.grid_3);
		
		TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, getResources().getDisplayMetrics());

		getWindowManager().getDefaultDisplay().getMetrics(dimension);
		height = dimension.heightPixels;
		
		LayoutParams layoutParams = gView2.getLayoutParams();
		
		layoutParams.height = height - (gView3.getLayoutParams().height
									    + gView1.getLayoutParams().height 
									    + (int) (height * 0.165));
		
		layoutParams.width = android.widget.GridLayout.LayoutParams.MATCH_PARENT;
	}
	
	private ListView handleListViewSubjects(List<String> subjectNames) {
		
		final ListView listViewSubjects = (ListView) findViewById(R.id.listViewSubjects);
		
		List<String> fullList = new ArrayList();
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
				
				if (view.isChecked() && view.getText().equals("All")) {
					for (int i=1; i<listViewSubjects.getChildCount(); i++) {
						CheckedTextView childAt = (CheckedTextView) listViewSubjects.getChildAt(i);
						checkedItems.add(childAt.getText());
						
						childAt.setChecked(true);
						
					}
				} else if (!view.isChecked() && view.getText().equals("All")){
					checkedItems.clear();
				} else if (view.isChecked() && !view.getText().equals("All")) {
					checkedItems.add(view.getText());
				} else {
					checkedItems.remove(view.getText());
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
		
		private List<String> listOfSkillSet  = new ArrayList<String>();
		private List<CheckedTextView> cViews = new ArrayList<CheckedTextView>();

		public ItemsAdapter(Context context, List<String> item) {
			this.items   = item;

			Intent intent = ((Activity) context).getIntent();
			if (intent != null && intent.getExtras() != null) {
				IntentData data = (IntentData) intent.getExtras().get(INTENT_DATA);

				if (data != null) {
					listOfSkillSet = (List) data.getValue(INTENT_SKILL_SET);
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

			if (listOfSkillSet.contains(items.get(position))) {
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
