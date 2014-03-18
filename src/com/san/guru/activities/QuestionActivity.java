package com.san.guru.activities;

import static com.san.guru.constant.AppContents.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.san.guru.R;
import com.san.guru.dto.IntentData;
import com.san.guru.dto.Option;
import com.san.guru.dto.Question;
import com.san.guru.dto.SubjectResult;
import com.san.guru.dto.TestResult;
import com.san.guru.model.QuestionBank;
import com.san.guru.model.Subject;
import com.san.guru.util.Dialog;
import com.san.guru.util.ICallback;
import com.san.guru.widget.SanClock;

@SuppressLint("NewApi")
public class QuestionActivity extends Activity {

	final QuestionBank qBank = new QuestionBank(this);
	final SanClock sanClock = new SanClock();
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_home:
	        	Dialog.show(this, "Are you sure ?", "Home", new ICallback() {
					
					@Override
					public void call() {
						Intent intent = new Intent(QuestionActivity.this, ChooseSubjectActivity.class);
						QuestionActivity.this.startActivity(intent);
						
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
	
	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_question);

		Intent intent = getIntent();
		IntentData data = (IntentData) intent.getExtras().get(INTENT_DATA);
		
		ArrayList skillSet = (ArrayList) data.getValue(INTENT_SKILL_SET);

		// Inialize Question Bank.
		qBank.init(skillSet);

		final Stack<Question> lastQuestion = new Stack<Question>();

		final Question question = qBank.nextQuestion();
		lastQuestion.push(question);

		loadQuestionOnScreen(question);
		
		final TextView txtTimer = (TextView) findViewById(R.id.txtTimer);
		
		sanClock.setTextView(txtTimer);
		sanClock.start();
		
		final Button buttonNext = (Button) findViewById(R.id.NextQuestion);
		final Button buttonBack = (Button) findViewById(R.id.PrevQuestion);
		final Button buttonPause = (Button) findViewById(R.id.PauseTest);
		final Button buttonEnd = (Button) findViewById(R.id.EndTest);
		
		final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.option);
		
		radioGroup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
				buttonBack.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				buttonPause.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				buttonEnd.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
			}
		});
		
		buttonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
				buttonBack.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				buttonPause.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				buttonEnd.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
			}
		});
		
		buttonPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
				buttonBack.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				buttonPause.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				buttonEnd.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
			}
		});
		
		buttonEnd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
				buttonBack.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				buttonPause.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				buttonEnd.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				
				Toast.makeText(v.getContext(), "End Test", 100);
				Dialog.show(v.getContext(), "Are you sure?", "End Test", new ICallback() {
					
					@Override
					public void call() {
						Intent intent = new Intent(v.getContext(), ResultActivity.class);
						QuestionActivity.this.startActivity(intent);
						
						finish();
					}
				}, false);
			}
		});
		
		buttonNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				
				buttonBack.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				buttonPause.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				buttonEnd.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
				if (radioGroup != null
						&& radioGroup.getCheckedRadioButtonId() != -1) {

					Question lQ = lastQuestion.pop();
					RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
					lQ.setUserAnswer(String.valueOf(radioButton.getText()));
					lQ.isCorrect();
					
					Question question = qBank.nextQuestion();
					lastQuestion.push(question);

					if (question == null)
						loadEndTest();
					else
						loadQuestionOnScreen(question);
				} 
			}
		});
	}

	private void loadEndTest() {
		qBank.result();
		
		sanClock.stop();
		
		TestResult result = new TestResult();
		result.setTotalTimeinSeconds(sanClock.getElaspedTime());
		result.setTotalCorrect(qBank.getNumCorrrect());
		result.setTotalWrong(qBank.getNumWrong());
		result.setTotalQuestions(qBank.getMaxQuestions());
		result.setPercent( ((float)qBank.getNumCorrrect() / (float)qBank.getMaxQuestions()) * 100.0f);
		result.setPace( (int) ((((double)qBank.getMaxQuestions() / (double)sanClock.getElaspedTime())) * 60) );
		
		List<Subject> subjects = qBank.getSubjects();
		List<SubjectResult> subjectResults = new ArrayList<SubjectResult>();
		
		for (Subject subject : subjects) {
			SubjectResult subjectResult = new SubjectResult();
			subjectResult.setTotalCorrect(subject.getNumCorrrect());
			subjectResult.setTotalWrong(subject.getNumWrong());
			subjectResult.setTotalQuestions(subject.getNumQuestions());
			
			subjectResult.setPercent( ((float)subject.getNumCorrrect() / (float)subject.getNumQuestions()) * 100.0f );
			subjectResults.add(subjectResult);
			subjectResult.setName(subject.getName());
		}
		
		result.setSubjectResult(subjectResults);
		
		IntentData intentData = new IntentData();
		intentData.putValue(INTENT_RESULT, result);
		
		Intent intent = new Intent(this, ResultActivity.class);
		intent.putExtra(INTENT_DATA, intentData);
		
		this.startActivity(intent);
		finish();
	}

	private void loadQuestionOnScreen(Question question) {
		
		final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.option);

		radioGroup.removeAllViews();
		
		TextView txtQCount = (TextView) findViewById(R.id.txtQCount);
		txtQCount.setText("Questions : " + question.getId() +"/" + qBank.getMaxQuestions());
		
		final Button buttonNext = (Button) findViewById(R.id.NextQuestion);
		TextView txtQuestion = (TextView) findViewById(R.id.txtQuestion);
		txtQuestion.setText(question.getDescription());
		
		for (Option option : question.getOptions()) {
			RadioButton b = new RadioButton(this);
			b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
			}
		});
			b.setTextColor(Color.BLACK);
			b.setText(option.getText());
			radioGroup.addView(b);
		}
		
		
		
		radioGroup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
			}
		});
		
	}
}