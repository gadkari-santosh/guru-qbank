package com.san.guru.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.san.guru.R;
import com.san.guru.constant.FILE_SOURCE;
import com.san.guru.model.Subject;
import com.san.guru.model.Subjects;
import com.san.guru.util.Dialog;
import com.san.guru.util.FileUtil;
import com.san.guru.util.ICallback;

public class SettingsActivity extends PreferenceActivity {

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_home:
	        	
				Intent intent = new Intent(this, ChooseSubjectActivity.class);
				this.startActivity(intent);
				
				finish();
	            return true;
	        case R.id.action_quit:
	        	finish();
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
		
		ActionBar ab = getActionBar(); 
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#203864"));     
        ab.setBackgroundDrawable(colorDrawable);
        
		PreferenceCategory appStorage = (PreferenceCategory)findPreference("appStorage");
		
		final CheckBoxPreference deleteAll = (CheckBoxPreference) findPreference("deleteAllData");
		deleteAll.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				
				if (deleteAll.isChecked()) {
					Dialog.show(SettingsActivity.this, 
							"You will lose all question banks. Are you sure?", 
							"Delete Data", new ICallback() {
								
								@Override
								public void call(Object object) {
									FileUtil.deleteAppDirFiles(SettingsActivity.this);
									Toast.makeText(SettingsActivity.this, "Deleted All..", 100).show();
									
									Subjects.getInstance().removeAllDownloadedSubjects();
								}
							}, new ICallback() {
								
								@Override
								public void call(Object object) {
									deleteAll.setChecked(false);
								}
							},  false);
				}
				
				return false;
			}
		});
		
		for (final Subject subject : Subjects.getInstance().getSubjects()) {
			if (subject.getFileSource() == FILE_SOURCE.LOCAL_STORAGE) {
				
				long size = FileUtil.getFileSize(this, subject.getQuestionSetFile());
				
				if (size > 0) {
					final CheckBoxPreference checkBoxPref = new CheckBoxPreference(this);
					checkBoxPref.setTitle(subject.getName());
					
					checkBoxPref.setSummary(String.format("~Size:%s kb.", size));
					checkBoxPref.setChecked(false);
					
					checkBoxPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
						
						@Override
						public boolean onPreferenceClick(Preference preference) {
							
							if (checkBoxPref.isChecked()) {
								Dialog.show(SettingsActivity.this, 
										"You will lose your subject question bank. Are you sure?", 
										"Delete "+subject.getName(), new ICallback() {
											
											@Override
											public void call(Object object) {
												
												FileUtil.deleteFile(SettingsActivity.this, subject.getQuestionSetFile());
												Toast.makeText(SettingsActivity.this, "Deleted " +subject.getName(), 100).show();
												
												Subjects.getInstance().removeSubject(subject.getName());
											}
										},new ICallback() {
											
											@Override
											public void call(Object object) {
												checkBoxPref.setChecked(false);
											}
										}, false);
							}
							return false;
						}
					});
					
					appStorage.addPreference(checkBoxPref);
				}
			}
		}
	}

}
