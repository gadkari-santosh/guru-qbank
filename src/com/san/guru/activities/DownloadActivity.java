package com.san.guru.activities;

import static com.san.guru.constant.AppConstants.DOWNLOADED_SUBJECT_FILE;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.san.guru.R;
import com.san.guru.model.SubjectFile;
import com.san.guru.model.Subjects;
import com.san.guru.util.Dialog;
import com.san.guru.util.DownloadFileFromURL;
import com.san.guru.util.FileUtil;
import com.san.guru.util.ICallback;
import com.san.guru.util.ResourceUtils;
import com.san.guru.widget.MyCustomAdapter;

public class DownloadActivity extends AbstractActivity  {

	HashSet<CharSequence> checkedItems = new HashSet<CharSequence>();
	
	final Map<String,SubjectFile> subjectFiles = new HashMap<String, SubjectFile>();
	
	final List<SubjectFile> subjects = new ArrayList<SubjectFile>();
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_home:
	        	Dialog.show(this, "Are you sure ?", "Home", new ICallback() {
					
					@Override
					public void call(Object obj) {
						Intent intent = new Intent(DownloadActivity.this, ChooseSubjectActivity.class);
						DownloadActivity.this.startActivity(intent);
						
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_download);
		
		if (!ResourceUtils.isNetworkAvailable(DownloadActivity.this)) {
			Toast.makeText(DownloadActivity.this, "No network.Check your Internet connection.", 100).show();
		}
		
		ProgressDialog progressBar = new ProgressDialog(this);
		progressBar.setCancelable(true);
		progressBar.setMessage("File downloading ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.setMax(100);
		
		final ListView listViewSubjects = (ListView) findViewById(R.id.listViewDownload);
		
		DownloadFileFromURL d = new DownloadFileFromURL(this, listViewSubjects, new ICallback() {
			
			@Override
			public void call(Object object) {
				
				List<String> downloadedFiles = (List) object;
				
				String content = null;
				for (String downloadedFile : downloadedFiles) {
					
					try {
						content = FileUtil.convertStreamToString(DownloadActivity.this.openFileInput(downloadedFile));
						
						String[] subjectArray = content.split(",");
						for (String subject : subjectArray) {
							SubjectFile subjectFile = new SubjectFile();
							
							String title = subject.substring(0, subject.indexOf("("));
							String attributes = subject.substring(subject.indexOf("(")+1, subject.indexOf(")"));
							
							subjectFile.setTitle(title);
							
							if (attributes != null) {
								String[] attrs = attributes.split(":");
								subjectFile.setDescription(attrs[0]);
								subjectFile.setSize(attrs[1]);
								subjectFile.setFileName(attrs[2]);
							}
							subjects.add(subjectFile);
								
							subjectFiles.put(title, subjectFile);
						}
						
						MyCustomAdapter adapter = new MyCustomAdapter(DownloadActivity.this, subjects);
						listViewSubjects.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					} catch (Exception exp) {
						Log.e("DownloadActivity", exp.toString());
					}
				}
			}
	        
		});
		
		d.execute(ResourceUtils.getString(this, R.string.subject_url));
		
		Log.i("Download", "waiting..");
		
		setListViewHight();
		
		listViewSubjects.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, 
								    View arg1, 
								    int arg2,
								    long arg3) {
				
				LinearLayout childAt = (LinearLayout) arg1;
				CheckedTextView txtView = (CheckedTextView) childAt.getChildAt(1);
				
				if (!txtView.isChecked()) {
					checkedItems.add(txtView.getText());
					txtView.setChecked(true);
				} else {
					checkedItems.remove(txtView.getText());
					txtView.setChecked(false);
				}
			}
		});
		
		Button downloadButton = (Button) findViewById(R.id.buttonDownload);
		downloadButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				DownloadFileFromURL d = new DownloadFileFromURL(v.getContext(), listViewSubjects, new ICallback() {
					
					@Override
					public void call(Object object) {
						StringBuilder subject = new StringBuilder();
						try {
							OutputStream output = v.getContext().openFileOutput(DOWNLOADED_SUBJECT_FILE, Context.MODE_PRIVATE);
							PrintWriter writer = new PrintWriter(output);
							
							for (CharSequence item : checkedItems) {
								SubjectFile subjectFile2 = subjectFiles.get(item);
								subject.append(subjectFile2.toString()).append(",");
							}
							writer.write(subject.toString());
							writer.flush();
							writer.close();
							
							Subjects.getInstance().init(v.getContext());
							
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				});
				
				String partialURL = ResourceUtils.getString(DownloadActivity.this, R.string.question_bank_url);
				
				String[] urls = new String[checkedItems.size()];
				
				int i = 0;
				for (CharSequence item : checkedItems) {
					SubjectFile subjectFile2 = subjectFiles.get(item);
					urls[i++] = partialURL+subjectFile2.getFileName();
				}
				
				d.execute(urls);
			}
		});
		
		setAd();
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
}