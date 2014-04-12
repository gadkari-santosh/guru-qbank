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

import static com.san.guru.constant.AppConstants.*;
import static com.san.guru.constant.AppConstants.INTENT_RESULT;
import static com.san.guru.constant.AppConstants.INTENT_SKILL_SET;
import static com.san.guru.constant.AppConstants.MODE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.san.guru.R;
import com.san.guru.constant.Mode;
import com.san.guru.dto.IntentData;
import com.san.guru.dto.Option;
import com.san.guru.dto.Question;
import com.san.guru.dto.SubjectResult;
import com.san.guru.dto.TestResult;
import com.san.guru.model.QuestionBank;
import com.san.guru.model.Subject;
import com.san.guru.util.BackgroundTask;
import com.san.guru.util.Dialog;
import com.san.guru.util.ICallback;
import com.san.guru.widget.SanClock;

/**
 * This Activity displays questions.
 *  
 * @author Santosh Gadkari (gadkari.santosh@gmail.com)
 */
@SuppressLint("NewApi")
public class QuestionActivity extends AbstractActivity {

	final QuestionBank qBank = QuestionBank.getNewInstance(this);
	final SanClock sanClock = new SanClock();
	
	final Stack<Question> lastQuestion = new Stack<Question>();
	
	private Mode mode;
	
	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_question);

		// initialize question bank with subjects selected from
		// previous screen and display first question.
		initQuestionBankAndDisplayFirstQuestion();
		
		// Load bottom buttons.
		loadButtons();
		
		// Adjust height of middle main area.
		setHeightOfMainArea();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	private void setAndStartTimer() {
		TextView txtTimer = (TextView) findViewById(R.id.txtTimer);
		
		sanClock.setTextView(txtTimer);
		
		if (mode == Mode.TEST)
			sanClock.start();
	}
	
	private void initQuestionBankAndDisplayFirstQuestion() {
		
		Intent intent = getIntent();
		IntentData data = (IntentData) intent.getExtras().get(INTENT_DATA);
		
		final Set skillSet = (Set) data.getValue(INTENT_SKILL_SET);
		
		mode = (Mode) data.getValue(MODE);
		
		final int numQuestions = (Integer) data.getValue(INTENT_NUM_QUESTION, 0);
		
		final ProgressDialog progressBar = new ProgressDialog(this);
		progressBar.setCancelable(true);
		progressBar.setMessage("Generating Question Set...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
		progressBar.show();
		BackgroundTask task = new BackgroundTask(this, progressBar, new ICallback() {
			
			@Override
			public void call(Object object) {
				final Question question = qBank.nextQuestion();
				lastQuestion.push(question);

				loadQuestionOnScreen(question);
				
				setAndStartTimer();
			}
		});
		
		task.execute(new ICallback() {
			
			@Override
			public void call(Object object) {
				if (mode == Mode.TEST) {
					qBank.init(skillSet, numQuestions);
				} else if (mode == Mode.REVIEW) {
					sanClock.stop();
					qBank.resetCounter();
				}
			}
		});
	}
	
	private void loadButtons() {
		
		final Button buttonNext  = (Button) findViewById(R.id.NextQuestion);
		final Button buttonBack  = (Button) findViewById(R.id.PrevQuestion);
		final Button buttonPause = (Button) findViewById(R.id.PauseTest);
		final Button buttonEnd   = (Button) findViewById(R.id.EndTest);
		
		//showAnswer
		buttonPause.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_action_accept), null, null);
		buttonPause.setText("ANSWER");
		
		final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.option);
		
		// Load next question button.
		buttonNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				buttonBack.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				buttonPause.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				buttonEnd.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
				if (mode == Mode.REVIEW) {
					Question question = qBank.nextQuestion();
					lastQuestion.push(question);

					if (question == null)
						loadEndTest();
					else
						loadQuestionOnScreen(question);
					
					return;
				}
				
				if (radioGroup != null && mode == Mode.TEST) {

					RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
					
					if (radioButton == null) {
						Toast toast = Toast.makeText(v.getContext(), "Select Option", 50);
						toast.show();
						return;
					}
					Question lQ = lastQuestion.pop();
					lQ.setUserAnswer(String.valueOf(radioButton.getText()));
					
					Question question = qBank.nextQuestion();
					if (question != null) {
						lastQuestion.push(question);
					} else {
						lastQuestion.push(lQ);
					}

					if (question == null)
						loadEndTest();
					else
						loadQuestionOnScreen(question);
				} 
			}
		});
		
		// Load back button.
		buttonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
				buttonBack.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				buttonPause.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				buttonEnd.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
				if (mode == Mode.REVIEW) {
					Question question = qBank.backQuestion();
					lastQuestion.push(question);

					if (question == null)
						loadEndTest();
					else
						loadQuestionOnScreen(question);
					
					return;
				}
				
				if (radioGroup != null && mode == Mode.TEST) {
					RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
					
					Question lastQ = null;
					if (!lastQuestion.isEmpty()) {
						lastQ = lastQuestion.pop();
						if (radioButton != null)
							lastQ.setUserAnswer(String.valueOf(radioButton.getText()));
					}
					
					Question question = qBank.backQuestion();
					if (question == null) {
						Toast.makeText(v.getContext(), "You are at begening", 100).show();
						lastQuestion.push(lastQ);
						return;
					}
					
					lastQuestion.push(question);
					loadQuestionOnScreen(question);
				} 
			}
		});
		
		// Load pause button.
		buttonPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mode == Mode.REVIEW) {
					Toast toast = Toast.makeText(v.getContext(), "Use Next button", 100);
					toast.show();
					
					return;
				}
				
				buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
				buttonBack.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				buttonPause.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				buttonEnd.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
				Question lQ = lastQuestion.pop();
				lQ.setUserAnswer(null);
				
				Question question = qBank.nextQuestion();
				lastQuestion.push(question);

				if (question == null)
					loadEndTest();
				else
					loadQuestionOnScreen(question);
			}
		});
		
		// Load End Test button.
		buttonEnd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
				buttonBack.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				buttonPause.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				buttonEnd.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				
				Dialog.show(v.getContext(), "Are you sure?", "End Test", new ICallback() {
					
					@Override
					public void call(Object obj) {
						loadEndTest();
					}
				}, false);
			}
		});
		
		// Load radio button.
		radioGroup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
				buttonBack.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				buttonPause.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				buttonEnd.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				
			}
		});
	}

	private void loadEndTest() {
		if (mode == Mode.REVIEW) {
			Intent intent = new Intent(this, ResultActivity.class);
			restoreIntent(intent, this.getIntent());
			
			this.startActivity(intent);
			finish();
			
			return;
		}
		
		qBank.result();
		
		sanClock.stop();
		
		TestResult result = new TestResult();
		result.setTotalTimeinSeconds(sanClock.getElaspedTime());
		result.setTotalCorrect(qBank.getNumCorrrect());
		result.setTotalWrong(qBank.getNumWrong());
		result.setTotalQuestions(qBank.getMaxQuestions());
		result.setPercent( ((float)qBank.getNumCorrrect() / (float)qBank.getMaxQuestions()) * 100.0f);
		result.setPace( (int) ((((double)qBank.getNumAttempted() / (double)sanClock.getElaspedTime())) * 60) );
		
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

	private void restoreIntent(Intent to, Intent from) {
		if (from.getExtras() != null) {
			IntentData intentData = (IntentData) from.getExtras().get(INTENT_DATA);
			intentData.putValue(MODE, Mode.TEST);
			to.putExtra(INTENT_DATA, intentData);
		}
	}
	
	private void loadQuestionOnScreen(Question question) {
		
		final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.option);

		radioGroup.removeAllViews();
		
		TextView txtQCount = (TextView) findViewById(R.id.txtQCount);
		txtQCount.setText("Questions : " + (qBank.getSequence()+1) +"/" + qBank.getMaxQuestions());
		
		final Button buttonNext = (Button) findViewById(R.id.NextQuestion);
		TextView txtQuestion = (TextView) findViewById(R.id.txtQuestion);
		
		String q = question.getDescription();
		q = question.getId() + ":" + q; //TODO - remove after work.
		int startIdx = q.indexOf("<code>");
		int endIdx = q.indexOf("</code>");
		
		q = q.replace("<code>", "");
		q = q.replace("</code>", "");
		
		SpannableString text = new SpannableString(q);
		text.setSpan(new TypefaceSpan("monospace"), startIdx, endIdx, 0);
		
		txtQuestion.setText(text);
		
		for (Option option : question.getOptions()) {
			RadioButton b = new RadioButton(this);
			b.setTextColor(Color.BLACK);
			b.setText(option.getText());
			
			if (mode == Mode.REVIEW) {
				b.setEnabled(false);
			} else if (mode == Mode.TEST) {
				b.setEnabled(true);
			}
			
			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.GRAY));
				}
			});
			
			radioGroup.addView(b);
			
			if (b.getText().equals(question.getUserAnswer())) {
				radioGroup.check(b.getId());
			}
		}
		TextView txtExplain = (TextView) findViewById(R.id.txtExplain);
		
		if (mode == Mode.REVIEW) {
			txtExplain.setVisibility(TextView.VISIBLE);
			
			if (question.isCorrect()) {
				txtExplain.setText("Correct");
				txtExplain.setTextColor(Color.parseColor("#007A00"));
			} else {
				txtExplain.setText(question.getExplaination());
				txtExplain.setTextColor(Color.parseColor("#FD2E2E"));
			}
		} else {
			txtExplain.setVisibility(TextView.INVISIBLE);
			qBank.markAttempted(question);
		}
	}
}