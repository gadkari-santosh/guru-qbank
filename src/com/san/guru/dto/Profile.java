package com.san.guru.dto;

import java.util.Date;

public class Profile {

	private String name = "My Name";
	
	private Date joinDate = new Date();
	
	private Date lastUpdated = new Date();
	
	private String bitmapPath = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getBitmapPath() {
		return bitmapPath;
	}

	public void setBitmapPath(String bitmapPath) {
		this.bitmapPath = bitmapPath;
	}
}
