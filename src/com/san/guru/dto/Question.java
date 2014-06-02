package com.san.guru.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Question {

	private int id = 0;
	
	private String description = null;
	private String explaination = null;
	private String subject = null;
	private int level = 0;
	
	private List<Option> options = null;
	
	private Set<Integer> answer = new HashSet<Integer>();
	
	private Set<Integer> userAnswer = new HashSet<Integer>();
	
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

	public Set<Integer> getAnswers() {
		return answer;
	}

	public void setAnswer(int answer) {
		this.answer.add(answer);
	}

	public Set<Integer> getUserAnswers() {
		return userAnswer;
	}

	public void setUserAnswer(int userAnswer) {
		this.userAnswer.add(userAnswer);
	}
	
	public void cancelUserAnswer(int userAnswer) {
		this.userAnswer.remove(userAnswer);
	}
	
	public void clearUserAnswer() {
		this.userAnswer.clear();
	}
	
	public boolean isCorrect() {
		if (getAnswers().size() == getUserAnswers().size()
			&& getUserAnswers().containsAll(getAnswers()))
			return true;
		return false;
	}
	
	public boolean isAttempted() {
		if (userAnswer.size() > 0)
			return true;
		
		return false;
	}
	
	public boolean isMultipleChoice() {
		if (answer.size() > 1 )
			return true;
		
		return false;
	}
}