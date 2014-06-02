package com.san.guru.util;

import java.util.ArrayList;
import java.util.List;

import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;

public class Formatter {

	public static SpannableString formatDescription (String q) {
		SpannableString text = null;
		
		String sTag = "<code>";
		String eTag = "</code>";
		
		try {
			
			StringBuilder textB = new StringBuilder(q);
			
			List<Integer> sTagIndx = new ArrayList<Integer>();
			List<Integer> eTagIndx = new ArrayList<Integer>();
			
			getStartAndEndTagIndex(textB, sTagIndx, eTagIndx, sTag, eTag);
			
			if (sTagIndx.size() == eTagIndx.size() && sTagIndx.size() > 0) {
				
				text = new SpannableString(textB.toString());
				
				for (int i=0; i<sTagIndx.size(); i++) {
					text.setSpan(new TypefaceSpan("monospace"), sTagIndx.get(i), eTagIndx.get(i), 0);
				}
			} else {
				text = new SpannableString(q);
			}
			
		} catch (Throwable th) {
			Log.e("Exception - Unable to format:", q, th);
			text = new SpannableString(q);
		}
		return text;
	}
	
	private static void getStartAndEndTagIndex(StringBuilder text, List<Integer> sTagIndx, List<Integer> eTagIndx, String sTag, String eTag) {
		int sTagSize = sTag.length();
		int eTagSize = eTag.length();
		
		int sIdx = 0;
		int eIdx = 0;
		
		while (text.indexOf(sTag) >= 0) {
			sIdx = text.indexOf(sTag);
			text.delete(sIdx, sIdx+sTagSize);
			
			eIdx = text.indexOf(eTag);
			text.delete(eIdx, eIdx+eTagSize);
			
			sTagIndx.add(sIdx);
			eTagIndx.add((eIdx-eTagSize)+1);
		}
	}
	
	public static SpannableString formatPercent(String result) {
		System.out.println(result);
		String sTag = "<b>";
		String eTag = "%";
		int startIdx = result.indexOf(sTag);
		int endIdx = result.indexOf(eTag);
		
		result = result.replace("<b>", "");
		
		SpannableString text = new SpannableString(result);
		text.setSpan(new RelativeSizeSpan(2f), startIdx, endIdx-sTag.length(), 0);
		text.setSpan(new RelativeSizeSpan(0.8f), endIdx, result.length(), 0);

		return text;
	}
	
	public static SpannableString formatPace(String result) {
		System.out.println(result);
		String sTag = "<b>";
		String eTag = "Q";
		int startIdx = result.indexOf(sTag);
		int endIdx = result.indexOf(eTag);
		
		result = result.replace("<b>", "");
		
		SpannableString text = new SpannableString(result);
		text.setSpan(new RelativeSizeSpan(2f), startIdx, endIdx-sTag.length(), 0);
		text.setSpan(new RelativeSizeSpan(0.8f), endIdx+3, result.length(), 0);

		return text;
	}
}