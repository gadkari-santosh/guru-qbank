package com.san.guru.dto;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import com.san.guru.constant.TEST_TYPE;

public class TestResult implements Serializable {

	private float percent = 0.0f;
	
	private int totalCorrect 	   = 0;
	private int totalWrong 	       = 0;
	private int totalQuestions     = 0;
	private int totalTimeinSeconds = 0;
	private int pace 			   = 0;
	
	private Date date = null;
	
	private List<SubjectResult> subjectResult = null;
	
	private int id = 0;
	
	private TEST_TYPE testType = TEST_TYPE.Practice;
	
	public TEST_TYPE getTestType() {
		return testType;
	}

	public void setTestType(TEST_TYPE testType) {
		this.testType = testType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getPace() {
		return pace;
	}

	public void setPace(int pace) {
		this.pace = pace;
	}

	public float getPercent() {
		return percent;
	}
	
	public String getPercentStr() {
		NumberFormat format = new DecimalFormat("##.##");
		return format.format(percent);
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