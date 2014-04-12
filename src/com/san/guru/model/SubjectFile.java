package com.san.guru.model;

import android.graphics.Shader.TileMode;

public class SubjectFile {

	private String title = null;
	private String size = null;
	private String description = null;
	private String fileName = null;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String toString() {
		//Java(100 questions:83kb:test.xml),
		return String.format("%s(%s:%s:%s)", this.title, this.description, this.size, this.fileName);
	}
}