package com.san.guru.dto;

import java.util.List;

public class Question {

	private int id = 0;
	private String description = null;
	private List<Option> options = null;
	private int answer = 0;
	
	private String userAnswer = null;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	public int getAnswer() {
		return answer;
	}

	public void setAnswer(int answer) {
		this.answer = answer;
	}

	public String getUserAnswer() {
		return userAnswer;
	}

	public void setUserAnswer(String userAnswer) {
		this.userAnswer = userAnswer;
	}
	
	public boolean isCorrect() {
		for (Option option : getOptions()) {
			if (option.getText().endsWith(userAnswer)
				&& option.getId() == answer) {
				
				return true;
			}
		}
		
		return false;
	}
}