package com.san.guru.dto;

public class Option {

	private int id = 0;
	private String text = null;
	
	public Option() {
	}
	
	public Option(int id, String text) {
		this.id = id;
		this.text = text;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}