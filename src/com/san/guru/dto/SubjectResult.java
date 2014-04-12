package com.san.guru.dto;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class SubjectResult implements Serializable {

	private float percent = 0.0f;
	
	private int totalCorrect   = 0;
	private int totalWrong 	   = 0;
	private int totalQuestions = 0;
	
	private String name = null;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
}