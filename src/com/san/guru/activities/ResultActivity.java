package com.san.guru.activities;

import static com.san.guru.constant.AppConstants.INTENT_DATA;
import static com.san.guru.constant.AppConstants.INTENT_RESULT;
import static com.san.guru.constant.AppConstants.LINKEDIN_POST;
import static com.san.guru.constant.AppConstants.MODE;
import static com.san.guru.constant.AppConstants.SM_CAPTION;
import static com.san.guru.constant.AppConstants.SM_TITLE;
import static com.san.guru.constant.AppConstants.SOCIAL_MEDIA_FB;
import static com.san.guru.constant.AppConstants.SOCIAL_MEDIA_LN;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.san.guru.R;
import com.san.guru.constant.Mode;
import com.san.guru.dto.IntentData;
import com.san.guru.dto.SubjectResult;
import com.san.guru.dto.TestResult;
import com.san.guru.socialmedia.ISocialMediaHandler;
import com.san.guru.socialmedia.SocialMediaHandlerFactory;
import com.san.guru.util.DateTimeUtils;
import com.san.guru.util.Dialog;
import com.san.guru.util.ICallback;
import com.san.guru.util.ResourceUtils;

public class ResultActivity extends AbstractActivity {

	private static final String STRING_FORMAT_PERCENT = "<b>Result</b><br><br> <font color='#254117'><b>%s %%</b> <br><small>%s out of %s</small>";

	private static final String STRING_FORMAT_PACE = "<b>Pace</b><br><br> <font color='#254117' ><b>%s Q/Min</b> <br><small>Time:%s</small>";

	private TestResult result = new TestResult();
	
	int maxChars = 0;
	
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

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		IntentData intentData = null;

		if (android.os.Build.VERSION.SDK_INT > 9) {
		      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
		}
		
		String totalTime = "00:00:00";

		setContentView(R.layout.layout_result);

		final Intent intent = getIntent();
		if (intent != null && intent.getExtras() != null) {
			intentData = (IntentData) intent.getExtras().get(INTENT_DATA);

			if (intentData != null) {
				result = (TestResult) intentData.getValue(INTENT_RESULT);
				totalTime = DateTimeUtils.getTimeString(result
						.getTotalTimeinSeconds());
			}
		}

		DisplayMetrics dimension = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dimension);
		int height = dimension.heightPixels;
		int width = dimension.widthPixels;

		TextView view = (TextView) findViewById(R.id.txtVwScore);
		TextView view1 = (TextView) findViewById(R.id.txtVwPace);

		LayoutParams layoutParams = view.getLayoutParams();
		LayoutParams layoutParams1 = view1.getLayoutParams();

		layoutParams.height = height - (int) (height * 0.85);
		layoutParams.width = width - (int) (width * 0.60);

		layoutParams1.height = height - (int) (height * 0.85);
		layoutParams1.width = width - (int) (width * 0.60);

		view.setText(Html.fromHtml(String.format(STRING_FORMAT_PERCENT,
				result.getPercentStr(), result.getTotalCorrect(),
				result.getTotalQuestions())));
		view1.setText(Html.fromHtml(String.format(STRING_FORMAT_PACE,
				result.getPace(), totalTime)));
		
		ImageButton facebook = (ImageButton) findViewById(R.id.imgBFacebook);
		ImageButton linkedIn = (ImageButton) findViewById(R.id.imgBLinkedIn);
		
		LinearLayout smLinearLayout = (LinearLayout) findViewById(R.id.smLinearLayout);
		View grid2 = (View) findViewById(R.id.grid_2);
		
		int smLayoutHeight = smLinearLayout.getLayoutParams().height;
		
		
		TableLayout tableLayout = (TableLayout) findViewById(R.id.subjectTable);
		LayoutParams tableLayoutParams = tableLayout.getLayoutParams();
		tableLayoutParams.height = height - (smLayoutHeight+ grid2.getLayoutParams().height+layoutParams.height + (int) (height * 0.35));
		
		drawSubjectChart();

		final Button quitTestButton = (Button) findViewById(R.id.butQuitTest);
		final Button reviewTestButton = (Button) findViewById(R.id.butReview);
		final Button feedbackButton = (Button) findViewById(R.id.butFeedback);
		
		feedbackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				feedbackButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				reviewTestButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				quitTestButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
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
				
				bundle.putString(SM_CAPTION, getSubjects(result));
				
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
				Bundle bundle = new Bundle();
				bundle.putString(SM_TITLE, String.format("I Got %s %%(%s Out Of %s)", 
											result.getPercentStr(),
											result.getTotalCorrect(),
											result.getTotalQuestions()));
				
				bundle.putString(SM_CAPTION, getSubjects(result));
				bundle.putString(LINKEDIN_POST, "Tested my skills with JavaQ");
				
				if (!ResourceUtils.isNetworkAvailable(ResultActivity.this)) {
					Toast.makeText(ResultActivity.this, "Check your Internet connection.", 100).show();
					return;
				}
				
				//get selected items
				Toast.makeText(ResultActivity.this, SOCIAL_MEDIA_LN, Toast.LENGTH_SHORT).show();
				
				ISocialMediaHandler socialMediaHandler = SocialMediaHandlerFactory.getInstance().getSocialMediaHandler(SOCIAL_MEDIA_LN);
				socialMediaHandler.init(ResultActivity.this);
				socialMediaHandler.post(ResultActivity.this, bundle);
			}
		});
		        
		reviewTestButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				feedbackButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				reviewTestButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				quitTestButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
				Intent backIntent = new Intent(ResultActivity.this, QuestionActivity.class);
				IntentData intentData = (IntentData) intent.getExtras().get(INTENT_DATA);
				intentData.putValue(MODE, Mode.REVIEW);
				
				backIntent.putExtra(INTENT_DATA, intentData);
				ResultActivity.this.startActivity(backIntent);
				
				finish();
			}
		});
		
		quitTestButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				feedbackButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				reviewTestButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				quitTestButton.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						v.getContext());

				// set title
				alertDialogBuilder.setTitle("Exit");

				// set dialog message
				alertDialogBuilder
						.setMessage("Do you really want to exit?")
						.setCancelable(false)
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// Do nothing.
									}
								})
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										// current activity
										finish();
									}
								});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		});
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

}