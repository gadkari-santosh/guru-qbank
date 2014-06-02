package com.san.guru.activities;

import static com.san.guru.constant.AppConstants.INTENT_DATA;
import static com.san.guru.constant.AppConstants.INTENT_RESULT;
import static com.san.guru.constant.AppConstants.LINKEDIN_POST;
import static com.san.guru.constant.AppConstants.MODE;
import static com.san.guru.constant.AppConstants.SM_CAPTION;
import static com.san.guru.constant.AppConstants.SM_DESC;
import static com.san.guru.constant.AppConstants.SM_TITLE;
import static com.san.guru.constant.AppConstants.SOCIAL_MEDIA_FB;
import static com.san.guru.constant.AppConstants.SOCIAL_MEDIA_LN;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.san.guru.R;
import com.san.guru.constant.Mode;
import com.san.guru.dto.IntentData;
import com.san.guru.dto.SubjectResult;
import com.san.guru.dto.TestResult;
import com.san.guru.socialmedia.ISocialMediaHandler;
import com.san.guru.socialmedia.SocialMediaHandlerFactory;
import com.san.guru.util.DateTimeUtils;
import com.san.guru.util.Dialog;
import com.san.guru.util.FileUtil;
import com.san.guru.util.Formatter;
import com.san.guru.util.ICallback;
import com.san.guru.util.ResourceUtils;

public class ResultActivity extends AbstractActivity {

	private static final String STRING_FORMAT_PERCENT = "Result \n<b>%s%% \n %s out of %s";

	private static final String STRING_FORMAT_PACE = "Pace \n <b>%s Q/Min \n Time: %s";

	private TestResult result = new TestResult();
	
	int maxChars = 0;
	
	private Mode mode = null;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_home:
	        	Dialog.show(this, "Are you sure ?", "Home", new ICallback() {
					
					@Override
					public void call(Object obj) {
						Intent intent = new Intent(ResultActivity.this, ChooseSubjectActivity.class);
						ResultActivity.this.startActivity(intent);
						
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
	
	private void drawSubjectChart() {
		
		if (result.getSubjectResult() == null)
			return;
		
		TableLayout tableLayout = (TableLayout) findViewById(R.id.subjectTable);

		for (SubjectResult subjectResult : result.getSubjectResult()) {
			addRow(tableLayout, 
					String.format("%s (%s/%s)", 
							    subjectResult.getName(),
							    subjectResult.getTotalCorrect(),
							    subjectResult.getTotalQuestions()), 
					subjectResult.getPercent());
		}
	}
	
	@SuppressLint("NewApi")
	private void addRow(TableLayout tableLayout, String name, float percent) {
		
		android.widget.TableRow.LayoutParams textParams = new android.widget.TableRow.LayoutParams();
		textParams.width = textParams.MATCH_PARENT;
		textParams.height = textParams.WRAP_CONTENT;
		textParams.setMargins(10, 0, 0, 10);
		
		android.widget.TableRow.LayoutParams params = new android.widget.TableRow.LayoutParams();
		params.width = params.MATCH_PARENT;
		params.height = params.WRAP_CONTENT;
		params.weight = 1.0f;
		params.setMargins(10, 0, 10, 0);
		
		ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
		progressBar.setLayoutParams(params);
		progressBar.setProgress((int)percent);
		progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogressbar));

		TextView view = new TextView(this);
		view.setLayoutParams(textParams);
		view.setText(name);
		view.setTextColor(Color.parseColor("#4682B4"));
		view.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
		
		TableRow tableRow = new TableRow(this);
		tableRow.setLayoutParams(params);
		tableRow.addView(view);
		tableRow.addView(progressBar);
		
		tableLayout.addView(tableRow);
	}

	protected void setHeightOfMainArea () {
		int height = 0;
		
		DisplayMetrics dimension = new DisplayMetrics();
		
		ScrollView sView = (ScrollView) findViewById(R.id.ScrollView);
		
		View gView1 = (View) findViewById(R.id.grid_1);
		View gView2 = (View) findViewById(R.id.grid_2);
		View gView3 = (View) findViewById(R.id.grid_3);
		
		TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, getResources().getDisplayMetrics());

		getWindowManager().getDefaultDisplay().getMetrics(dimension);
		height = dimension.heightPixels;
		
		LayoutParams layoutParams = gView2.getLayoutParams();
		
		layoutParams.height = height - (gView3.getLayoutParams().height
									    + gView1.getLayoutParams().height 
									    + (int) (height * 0.15));
		
		sView.getLayoutParams().height = layoutParams.height;
		layoutParams.width = android.widget.GridLayout.LayoutParams.MATCH_PARENT;
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		IntentData intentData = null;

		String totalTime = "00:00:00";

		setContentView(R.layout.layout_result);

		final Intent intent = getIntent();
		if (intent != null && intent.getExtras() != null) {
			intentData = (IntentData) intent.getExtras().get(INTENT_DATA);

			if (intentData != null) {
				result = (TestResult) intentData.getValue(INTENT_RESULT);
				mode = (Mode) intentData.getValue(MODE);
				
				totalTime = DateTimeUtils.getTimeString(result
						.getTotalTimeinSeconds());
			}
		}
		// Set hight of main area.		
		setHeightOfMainArea();
		
		DisplayMetrics dimension = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dimension);
		int height = dimension.heightPixels;
		int width = dimension.widthPixels;

		TextView view = (TextView) findViewById(R.id.txtVwScore);
		TextView view1 = (TextView) findViewById(R.id.txtVwPace);

		LayoutParams layoutParams = view.getLayoutParams();
		LayoutParams layoutParams1 = view1.getLayoutParams();

		view.setText(Formatter.formatPercent(String.format(STRING_FORMAT_PERCENT,
				result.getPercentStr(), result.getTotalCorrect(),
				result.getTotalQuestions())));
		view1.setText(Formatter.formatPace(String.format(STRING_FORMAT_PACE,
				result.getPace(), totalTime)));
		
		layoutParams.width = width - (int) (width * 0.60);
		layoutParams1.width = width - (int) (width * 0.60);
		layoutParams1.height = layoutParams.height;

		ImageButton facebook = (ImageButton) findViewById(R.id.imgBFacebook);
		ImageButton linkedIn = (ImageButton) findViewById(R.id.imgBLinkedIn);
		
		View grid3 = (View) findViewById(R.id.grid_3);
		View grid1 = (View) findViewById(R.id.grid_1);
		
		ScrollView scrollView = (ScrollView) findViewById(R.id.qScrollView);
		View txtViews = (View) findViewById(R.id.subjectTable);
		
		drawSubjectChart();
		
		txtViews.getLayoutParams().height = height - (grid1.getLayoutParams().height
				+ grid3.getLayoutParams().height
				+ layoutParams.height
				+ facebook.getLayoutParams().height
				+ (int) (height * 0.45));
		
		scrollView.getLayoutParams().height = height - (grid1.getLayoutParams().height
				+ grid3.getLayoutParams().height
				+ layoutParams.height
				+ facebook.getLayoutParams().height
				+ (int) (height * 0.45));
		
		final Button saveTestButton   = (Button) findViewById(R.id.butSaveTest);
		final Button reviewTestButton = (Button) findViewById(R.id.butReview);
		final Button feedbackButton   = (Button) findViewById(R.id.butFeedback);
		
		feedbackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				feedbackButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				reviewTestButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				saveTestButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				
				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL, new String[]{"gadkari.santosh@gmail.com"});		  
				email.putExtra(Intent.EXTRA_SUBJECT, "subject");
				email.putExtra(Intent.EXTRA_TEXT, "message");
				email.setType("message/rfc822");
				startActivity(Intent.createChooser(email, "Choose an Email client :"));
			}
		});
		
		facebook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString(SM_TITLE, String.format("I Got %s %%(%s Out Of %s)", 
											result.getPercentStr(),
											result.getTotalCorrect(),
											result.getTotalQuestions()));
				
				bundle.putString(SM_CAPTION, result.getTestType().name());
				bundle.putString(SM_DESC, getSubjects(result));
				
				if (!ResourceUtils.isNetworkAvailable(ResultActivity.this)) {
					Toast.makeText(ResultActivity.this, "Check your Internet connection.", 100).show();
					return;
				}
				
				//get selected items
				Toast.makeText(ResultActivity.this, SOCIAL_MEDIA_FB, Toast.LENGTH_SHORT).show();
				
				ISocialMediaHandler socialMediaHandler = SocialMediaHandlerFactory.getInstance().getSocialMediaHandler(SOCIAL_MEDIA_FB);
				socialMediaHandler.init(ResultActivity.this);
				socialMediaHandler.post(ResultActivity.this, bundle);
			}
		});
		
		linkedIn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Bundle bundle = new Bundle();
				bundle.putString(SM_TITLE, String.format("I Got %s %%(%s Out Of %s)", 
											result.getPercentStr(),
											result.getTotalCorrect(),
											result.getTotalQuestions()));
				
				bundle.putString(SM_CAPTION, getSubjects(result));
				bundle.putString(SM_DESC, getSubjects(result));
				
				if (!ResourceUtils.isNetworkAvailable(ResultActivity.this)) {
					Toast.makeText(ResultActivity.this, "Check your Internet connection.", 100).show();
					return;
				}
				
				AlertDialog.Builder alert = new AlertDialog.Builder(ResultActivity.this);  

		        alert.setTitle("LinkedIn");  
		        alert.setMessage("Post");  
		        alert.setIcon(getResources().getDrawable(R.drawable.ic_linkedin));

		        // Set an EditText view to get user input   
		        final EditText inputName = new EditText(ResultActivity.this);  
		        alert.setView(inputName);  

		        alert.setPositiveButton("Post Comments.", new DialogInterface.OnClickListener() {  
		        public void onClick(DialogInterface dialog, int whichButton) {  
		        	bundle.putString(LINKEDIN_POST, inputName.getText().toString());
		        	
		        	//get selected items
					Toast.makeText(ResultActivity.this, SOCIAL_MEDIA_LN, Toast.LENGTH_SHORT).show();
					
					ISocialMediaHandler socialMediaHandler = SocialMediaHandlerFactory.getInstance().getSocialMediaHandler(SOCIAL_MEDIA_LN);
					socialMediaHandler.init(ResultActivity.this);
					socialMediaHandler.post(ResultActivity.this, bundle);
		          }  
		        }); 
		        
		        alert.show();
		        
				//bundle.putString(LINKEDIN_POST, linkedinpost);
			}
		});
		        
		reviewTestButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				feedbackButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				reviewTestButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				saveTestButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				
				Intent backIntent = new Intent(ResultActivity.this, QuestionActivity.class);
				IntentData intentData = (IntentData) intent.getExtras().get(INTENT_DATA);
				intentData.putValue(MODE, Mode.REVIEW);
				
				backIntent.putExtra(INTENT_DATA, intentData);
				ResultActivity.this.startActivity(backIntent);
				
				finish();
			}
		});
		
		saveTestButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				feedbackButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				reviewTestButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				saveTestButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				
				if (!FileUtil.exists(ResultActivity.this, ResourceUtils.getString(ResultActivity.this, R.string.result_file))) {
			        Gson gson = new Gson();
					List<TestResult> results = new ArrayList<TestResult>();
					results.add(ResultActivity.this.result);
					FileUtil.saveFileInPhone(ResultActivity.this, 
							ResourceUtils.getString(ResultActivity.this, R.string.result_file), 
							gson.toJson(results));
				} else {
					String content = FileUtil.getFile(ResultActivity.this, ResourceUtils.getString(ResultActivity.this, R.string.result_file));
					Type listOfTestObject = new TypeToken<List<TestResult>>(){}.getType();
			        Gson gson = new Gson();
					List<TestResult> results = gson.fromJson(content, listOfTestObject);
					results.add(ResultActivity.this.result);
					ResultActivity.this.result.setDate(new Date());
					
					FileUtil.saveFileInPhone(ResultActivity.this, 
							ResourceUtils.getString(ResultActivity.this, R.string.result_file), 
							gson.toJson(results));
				}
				
				Toast.makeText(ResultActivity.this, "Saved Result", 100).show();
			}
		});
		
		if (mode == Mode.RESULT_DISPLAY) {
			grid3.setEnabled(false);
			grid3.setVisibility(GridLayout.INVISIBLE);
		} else {
			grid3.setEnabled(true);
			grid3.setVisibility(GridLayout.VISIBLE);
		}
		
		
		ActionBar ab = getActionBar(); 
		ab.setTitle(String.format("%s [%s]","Result", result.getTestType().name()));
		
		setAd();
	}
	
	private String getSubjects(TestResult result) {
		StringBuffer subjectNames = new StringBuffer();
		
		for (SubjectResult sr : result.getSubjectResult()) {
			if (sr.getName().startsWith("Domain"))
				subjectNames.append( sr.getName().replace("Domain", "") ).append(".");
			else
				subjectNames.append( sr.getName() ).append(".");
		}
		
		return subjectNames.toString();
	}
	
	private String getLinkedInSubjects(TestResult result) {
		StringBuffer subjectNames = new StringBuffer();
		
		for (SubjectResult sr : result.getSubjectResult()) {
			if (sr.getName().startsWith("Domain"))
				subjectNames.append( sr.getName().replace("Domain", "") ).append(".");
			else
				subjectNames.append( sr.getName() ).append(".");
		}
		
		return subjectNames.toString();
	}

}