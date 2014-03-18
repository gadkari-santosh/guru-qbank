package com.san.guru.dto;

import java.io.Serializable;
import java.util.List;

public class TestResult implements Serializable {

	private float percent = 0.0f;
	
	private int totalCorrect 	   = 0;
	private int totalWrong 	       = 0;
	private int totalQuestions     = 0;
	private int totalTimeinSeconds = 0;
	private int pace 			   = 0;
	
	private List<SubjectResult> subjectResult = null;

	public int getPace() {
		return pace;
	}

	public void setPace(int pace) {
		this.pace = pace;
	}

	public float getPercent() {
		return percent;
	}

	public void setPercent(float percent) {
		this.percent = percent;
	}

	public int getTotalCorrect() {
		return totalCorrect;
	}

	public void setTotalCorrect(int totalCorrect) {
		this.totalCorrect = totalCorrect;
	}

	public int getTotalWrong() {
		return totalWrong;
	}

	public void setTotalWrong(int totalWrong) {
		this.totalWrong = totalWrong;
	}

	public int getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(int totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public int getTotalTimeinSeconds() {
		return totalTimeinSeconds;
	}

	public void setTotalTimeinSeconds(int totalTimeinSeconds) {
		this.totalTimeinSeconds = totalTimeinSeconds;
	}

	public List<SubjectResult> getSubjectResult() {
		return subjectResult;
	}

	public void setSubjectResult(List<SubjectResult> subjectResult) {
		this.subjectResult = subjectResult;
	}
}