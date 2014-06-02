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

import static com.san.guru.constant.AppConstants.DEFAULT_QUESTION_SET_SIZE;
import static com.san.guru.constant.AppConstants.INTENT_DATA;
import static com.san.guru.constant.AppConstants.INTENT_NUM_QUESTION;
import static com.san.guru.constant.AppConstants.INTENT_RESULT;
import static com.san.guru.constant.AppConstants.INTENT_SKILL_SET;
import static com.san.guru.constant.AppConstants.INTENT_TEST_TYPE;
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
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.san.guru.R;
import com.san.guru.constant.Mode;
import com.san.guru.constant.TEST_TYPE;
import com.san.guru.dto.IntentData;
import com.san.guru.dto.Option;
import com.san.guru.dto.Question;
import com.san.guru.dto.SubjectResult;
import com.san.guru.dto.TestResult;
import com.san.guru.model.QuestionBank;
import com.san.guru.model.Subject;
import com.san.guru.util.BackgroundTask;
import com.san.guru.util.Dialog;
import com.san.guru.util.Formatter;
import com.san.guru.util.ICallback;
import com.san.guru.util.ResourceUtils;
import com.san.guru.widget.SanClock;

/**
 * This Activity displays questions.
 *  
 * @author Santosh Gadkari (gadkari.santosh@gmail.com)
 */
@SuppressLint("NewApi")
public class QuestionActivity extends AbstractActivity {

	final QuestionBank qBank = QuestionBank.getNewInstance(this);
	
	SanClock sanClock = new SanClock();
	
	final Stack<Question> lastQuestion = new Stack<Question>();
	
	private Mode mode;
	
	private TEST_TYPE testType = null;
	
	private CountDownTimer countDownTimer = null; 
	
	private CountDownTimer timedTestTimer = null;

	private String WRONG_ANSWER_FORMAT = "Wrong Answer \nCorrect Answer : %s \nExplaination: %s";
	
	private String CORRECT_ANSWER_FORMAT = "Correct \nExplaination:%s";
	
	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_question);
		
		// initialize question bank with subjects selected from
		// previous screen and display first question.
		initQuestionBankAndDisplayFirstQuestion();
		
		startTimers();
		
		// Load bottom buttons.
		loadButtons();
		
		// Adjust height of middle main area.
		setHeightOfMainArea();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (countDownTimer != null)
			countDownTimer.cancel();
		
		if (timedTestTimer != null)
			timedTestTimer.cancel();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		if (countDownTimer != null)
			countDownTimer.cancel();
		
		if (timedTestTimer != null)
			timedTestTimer.cancel();
	}
	
	
	private void startTimers() {

		if (testType != null) {
			switch (testType) {
			
				case Timed:
					
					Intent intent = getIntent();
					IntentData data = (IntentData) intent.getExtras().get(INTENT_DATA);
					final int numQuestions = (Integer) data.getValue(INTENT_NUM_QUESTION, DEFAULT_QUESTION_SET_SIZE);
					
					int timeout = numQuestions * 10;
					
					TextView txtTimer = (TextView) findViewById(R.id.txtTimer);
					sanClock = new SanClock(timeout, txtTimer);
					sanClock.start();
					
					timedTestTimer = new CountDownTimer(timeout*1000, (timeout*1000)/5) {
						
						@Override
						public void onTick(long millisUntilFinished) {
							Toast.makeText(QuestionActivity.this, "Minutes remaining: " + millisUntilFinished / 60000, 100).show();
						}
						
						@Override
						public void onFinish() {
							timedTestTimer.cancel();
							sanClock.stop();
							
							Toast.makeText(QuestionActivity.this, "Time Expire", 100).show();
							
							loadEndTest();
						}
					};
					timedTestTimer.start();
			}
		}
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
		
		if (mode == Mode.TEST && testType == TEST_TYPE.Practice)
			sanClock.start();
	}
	
	private void initQuestionBankAndDisplayFirstQuestion() {
		
		Intent intent = getIntent();
		IntentData data = (IntentData) intent.getExtras().get(INTENT_DATA);
		
		final Set skillSet = (Set) data.getValue(INTENT_SKILL_SET);
		this.testType = (TEST_TYPE) data.getValue(INTENT_TEST_TYPE);
		
		mode = (Mode) data.getValue(MODE);
		
		final int numQuestions = (Integer) data.getValue(INTENT_NUM_QUESTION, DEFAULT_QUESTION_SET_SIZE);
		
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
		final Button buttonSkipOrAnswer  = (Button) findViewById(R.id.PauseTest);
		final Button buttonEnd   = (Button) findViewById(R.id.EndTest);
		
		if (testType == TEST_TYPE.Practice) {
			//showAnswer
			buttonSkipOrAnswer.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_action_accept), null, null);
			buttonSkipOrAnswer.setText("ANSWER");
		}
		
		final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.option);
		
		// Load next question button.
		buttonNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				buttonBack.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				buttonSkipOrAnswer.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				buttonEnd.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				
				if (countDownTimer != null)
					countDownTimer.cancel();
				
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

					Question lQ = lastQuestion.pop();
					
					if (lQ.isMultipleChoice()) {
						final LinearLayout cbLayout = (LinearLayout) findViewById(R.id.layoutCheckBox);
						for (int i=0; i<cbLayout.getChildCount(); i++) {
							CheckBox checkBox = (CheckBox) cbLayout.getChildAt(i);
							
							if (checkBox.isChecked()) {
								lQ.setUserAnswer(checkBox.getId());
							}
						}
					} else {
						RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
						
						if (radioButton == null && testType == TEST_TYPE.Timed) {
							Toast toast = Toast.makeText(v.getContext(), "Select Option", 50);
							toast.show();
							return;
						}
						
						if (radioButton != null)
							lQ.setUserAnswer(radioButton.getId());
					}
					
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
				
				if (countDownTimer != null)
					countDownTimer.cancel();
				
				buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				
				buttonBack.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				buttonSkipOrAnswer.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				buttonEnd.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				
				if (mode == Mode.REVIEW) {
					Question question = qBank.backQuestion();
					lastQuestion.push(question);

					if (question == null)
						loadEndTest();
					else
						loadQuestionOnScreen(question);
					
					return;
				}
				
				if (mode == Mode.TEST) {
					
					Question lastQ = null;
					if (!lastQuestion.isEmpty()) {
						lastQ = lastQuestion.pop();
						
						if (!lastQ.isMultipleChoice()) {
							RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
							lastQ.setUserAnswer(radioButton.getId());
						} else {
							final LinearLayout cbLayout = (LinearLayout) findViewById(R.id.layoutCheckBox);
							for (int i=0; i<cbLayout.getChildCount(); i++) {
								CheckBox checkBox = (CheckBox) cbLayout.getChildAt(i);
								
								if (checkBox.isChecked()) {
									lastQ.setUserAnswer(checkBox.getId());
								}
							}
						}
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
		buttonSkipOrAnswer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mode == Mode.REVIEW) {
					Toast toast = Toast.makeText(v.getContext(), "Use Next button", 100);
					toast.show();
					
					return;
				}
				
				if (countDownTimer != null)
					countDownTimer.cancel();
				
				TextView txtExplain = (TextView) findViewById(R.id.txtExplain);
				if (testType == TEST_TYPE.Practice) {
					Question lQ = lastQuestion.peek();
					
					if (!lQ.isMultipleChoice()) {
						RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
						
						if (radioButton == null && testType == TEST_TYPE.Timed) {
							Toast toast = Toast.makeText(v.getContext(), "Select Option", 50);
							toast.show();
							return;
						}
						
						if (radioButton != null)
							lQ.setUserAnswer(radioButton.getId());
					}
					
					if (lQ.isCorrect()) {
						txtExplain.setText(String.format(CORRECT_ANSWER_FORMAT, 
								Formatter.formatDescription(lQ.getExplaination())));
					} else {
						txtExplain.setText(String.format(WRONG_ANSWER_FORMAT, 
								lQ.getAnswers().toString(), 
								Formatter.formatDescription(lQ.getExplaination())));	
					}
					
					txtExplain.setEnabled(true);
					txtExplain.setVisibility(TextView.VISIBLE);
					
					final ScrollView sv = (ScrollView) findViewById(R.id.qScrollView);
					sv.post(new Runnable() { 
				        public void run() { 
				        	sv.scrollTo(0, sv.getBottom());
				        } 
					});
					
					return;
				}
				
				buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				
				buttonBack.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				buttonSkipOrAnswer.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.STEEL_BLUE));
				buttonEnd.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				
				Question lQ = lastQuestion.pop();
//				lQ.setUserAnswer(null);
				
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
				
				if (countDownTimer != null)
					countDownTimer.cancel();
				
				if (timedTestTimer != null)
					timedTestTimer.cancel();
				
				buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				
				buttonBack.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				buttonSkipOrAnswer.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
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
				buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				
				buttonBack.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				buttonSkipOrAnswer.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
				buttonEnd.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
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
		if (testType != null)
			result.setTestType(testType);
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
		final LinearLayout layout = (LinearLayout) findViewById(R.id.layoutQuestion);
		final LinearLayout cbLayout = (LinearLayout) findViewById(R.id.layoutCheckBox);
		
		radioGroup.removeAllViews();
		
		cbLayout.removeAllViews();
		
		TextView txtQCount = (TextView) findViewById(R.id.txtQCount);
		txtQCount.setText("Questions : " + (qBank.getSequence()+1) +"/" + qBank.getMaxQuestions());
		
		final Button buttonNext = (Button) findViewById(R.id.NextQuestion);
		TextView txtQuestion = (TextView) findViewById(R.id.txtQuestion);
		
		String q = question.getDescription();
		txtQuestion.setText(Formatter.formatDescription(q));
		
		for (Option option : question.getOptions()) {
			
			if (question.isMultipleChoice()) {
				
				CheckBox b = new CheckBox(this);
				
				b.setText(option.getId()+") "+option.getText()+"\n");
				b.setId(option.getId());
				b.setGravity(Gravity.TOP);
				b.setTextColor(ResourceUtils.getColor(this, R.color.AppColor));
				b.setTextSize(14);
				
				if (mode == Mode.REVIEW) {
					b.setEnabled(false);
				} else if (mode == Mode.TEST) {
					b.setEnabled(true);
				}
				
				b.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Question lQ = lastQuestion.peek();
						
						if (lQ != null) {
							CheckBox checkBox = (CheckBox) v;
							if (checkBox.isChecked()) {
								lQ.setUserAnswer(checkBox.getId());
							} else {
								lQ.cancelUserAnswer(checkBox.getId());
							}
						}
						buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
					}
				});
				
				if (question.getUserAnswers().contains(b.getId())) {
					b.setChecked(true);
				}
				cbLayout.addView(b);
				
			} else {
				
				final RadioButton b = new RadioButton(this);
				b.setTextColor(Color.BLACK);
				b.setText(option.getId()+") "+option.getText()+"\n");
				b.setId(option.getId());
				b.setGravity(Gravity.TOP);
				b.setTextColor(ResourceUtils.getColor(this, R.color.AppColor));
				b.setTextSize(14);
				
				if (mode == Mode.REVIEW) {
					b.setEnabled(false);
				} else if (mode == Mode.TEST) {
					b.setEnabled(true);
				}
				
				b.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						buttonNext.setBackgroundColor(Color.parseColor(com.san.guru.constant.Color.BLACK));
					}
				});
			
				
				if (question.getUserAnswers().contains(b.getId())) {
					b.setChecked(true);
					b.setSelected(true);
				}
				
				radioGroup.addView(b);
			}
		}
		TextView txtExplain = (TextView) findViewById(R.id.txtExplain);
		
		if (mode == Mode.REVIEW) {
			txtExplain.setVisibility(TextView.VISIBLE);
			
			if (question.isCorrect()) {
				txtExplain.setText(String.format(CORRECT_ANSWER_FORMAT, 
						Formatter.formatDescription(question.getExplaination())));
			} else {
				txtExplain.setText(Formatter.formatDescription(String.format(WRONG_ANSWER_FORMAT, 
						question.getAnswers().toString(), 
						question.getExplaination())));
			}
		} else {
			txtExplain.setVisibility(TextView.INVISIBLE);
			qBank.markAttempted(question);
		}
		
		if (testType == TEST_TYPE.RapidFire) {
			sanClock.stopStart();
			
			int timeout = ResourceUtils.getInt(QuestionActivity.this, R.string.rapid_fire_timeout);
			countDownTimer = new CountDownTimer(timeout, timeout/3) {
				
				@Override
				public void onTick(long millisUntilFinished) {
					Toast.makeText(QuestionActivity.this, "seconds remaining: " + millisUntilFinished / 1000, 100).show();
				}
				
				@Override
				public void onFinish() {
					sanClock.stop();
					
					Toast.makeText(QuestionActivity.this, "Time Expire", 100).show();
					
					 Button buttonNext  = (Button) findViewById(R.id.NextQuestion);
					 buttonNext.performClick();
				}
			};
			countDownTimer.start();
		}
	}
}