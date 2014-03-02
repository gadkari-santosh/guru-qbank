package com.san.guru.activities;

import static com.san.guru.constant.AppContents.*;

import java.util.ArrayList;
import java.util.Stack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.san.guru.QuestionBank;
import com.san.guru.dto.IntentData;
import com.san.guru.dto.Option;
import com.san.guru.dto.Question;

import com.san.guru.R;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	final QuestionBank qBank = new QuestionBank(this);

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

		Button button = (Button) findViewById(R.id.button1);
		final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.option);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (radioGroup != null
						&& radioGroup.getCheckedRadioButtonId() != -1) {

					Question lQ = lastQuestion.pop();
					RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
					lQ.setUserAnswer(String.valueOf(radioButton.getText()));

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
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		qBank.result();
		String message = "Correct : " + qBank.getNumCorrrect() + " Wrong :" + qBank.getNumWrong();

		// set title
//		alertDialogBuilder.setTitle("Result");
//
//		// set dialog message
//		alertDialogBuilder
//				.setMessage(message)
//				.setCancelable(false)
//				.setPositiveButton("Yes",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int id) {
//								// if this button is clicked, close
//								// current activity
//								MainActivity.this.finish();
//							}
//						});
//
//		// create alert dialog
//		AlertDialog alertDialog = alertDialogBuilder.create();
//
//		// show it
//		alertDialog.show();
		
		Intent intent = new Intent(this, ResultActivity.class);
		this.startActivity(intent);
		finish();
	}

	private void loadQuestionOnScreen(Question question) {
		
		final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.option);

		radioGroup.removeAllViews();

		TextView txtQuestion = (TextView) findViewById(R.id.txtQuestion);
		txtQuestion.setText(question.getDescription());

		for (Option option : question.getOptions()) {
			RadioButton b = new RadioButton(this);
			b.setTextColor(Color.BLACK);
			b.setText(option.getText());
			radioGroup.addView(b);
		}

	}

}