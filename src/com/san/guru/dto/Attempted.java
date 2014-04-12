package com.san.guru.dto;

import java.util.List;
import java.util.Set;

import com.google.gson.annotations.SerializedName;

public class Attempted {
	
	@SerializedName("subject")
	String subject;
	
	@SerializedName("numQuestions")
	Integer numQuestions;
	
	@SerializedName("unAttemped")
	Set<Integer> unAttemped;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Integer getNumQuestions() {
		return numQuestions;
	}

	public void setNumQuestions(Integer numQuestions) {
		this.numQuestions = numQuestions;
	}

	public Set<Integer> getUnAttemped() {
		return unAttemped;
	}

	public void setUnAttemped(Set<Integer> unAttemped) {
		this.unAttemped = unAttemped;
	}
}