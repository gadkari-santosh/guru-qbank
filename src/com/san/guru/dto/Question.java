package com.san.guru.dto;

import java.util.List;

public class Question {

	private int id = 0;
	
	private String description = null;
	private String explaination = null;
	private String subject = null;
	private int level = 0;
	
	private List<Option> options = null;
	
	private int answer = 0;
	
	private String userAnswer = null;

	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getExplaination() {
		return explaination;
	}

	public void setExplaination(String explaination) {
		this.explaination = explaination;
	}

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
			if (option.getText().equalsIgnoreCase(userAnswer)
				&& option.getId() == answer) {
				
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isAttempted() {
		if (userAnswer != null && !"".equals(userAnswer.trim()))
			return true;
		
		return false;
	}
}