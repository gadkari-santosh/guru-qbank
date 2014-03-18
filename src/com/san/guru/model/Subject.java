package com.san.guru.model;

import java.util.List;

import com.san.guru.dto.Question;

public class Subject {

	private String name = null;
	private String questionSetFile = null;
	
	private List<Question> questionSet = null;
	
	private int correct = 0;
	private int wrong = 0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuestionSetFile() {
		return questionSetFile;
	}

	public void setQuestionSetFile(String questionSetFile) {
		this.questionSetFile = questionSetFile;
	}

	public List<Question> getQuestionSet() {
		return questionSet;
	}

	public void setQuestionSet(List<Question> questionSet) {
		this.questionSet = questionSet;
	}
	
	public void result() {
		for (Question question : questionSet) {
			if (question.isCorrect())
				correct++;
			else 
				wrong++;
		}
	}
	
	public int getNumCorrrect() {
		return correct;
	}
	
	public int getNumWrong() {
		return wrong;
	}
	
	public int getNumQuestions() {
		return questionSet.size();
	}
}