package com.san.guru.util;

public class DateTimeUtils {
	
	public static String getTimeString(int seconds) {
		int hours = seconds / 3600;
	    int minutes = (seconds % 3600) / 60;
	    int sec = seconds % 60;
	    
	    String time = twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(sec);
	    
	    return time;
	}
	
	private static String twoDigitString(int number) {
	    if (number == 0) {
	        return "00";
	    }

	    if (number / 10 == 0) {
	        return "0" + number;
	    }

	    return String.valueOf(number);
	}
}
