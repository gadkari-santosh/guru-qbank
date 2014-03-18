package com.san.guru.model;

import static com.san.guru.constant.AppContents.ASSET_SUBJECT;

import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;

public class Subjects {

	private Map<String, Subject> subjectMap = new LinkedHashMap<String, Subject>();
	
	private static final Subjects INSTANCE = new Subjects();
	
	private Subjects() {}
	
	public static Subjects getInstance() {
		return INSTANCE;
	}
	
	public void init(Context ctx) {
		
		AssetManager assetManager = ctx.getAssets();
		
		Node node = null;
		
		Subject subject = null;
		
		try {
			InputStream inputStream = assetManager.open(ASSET_SUBJECT);
			
			XPath xpath = XPathFactory.newInstance().newXPath();
			String expression = "//Subject";
			NodeList nodes = null;
			
			nodes = (NodeList) xpath.evaluate(expression, new InputSource(inputStream), XPathConstants.NODESET);
			
			for (int i=0; i<nodes.getLength(); i++) {
				node = nodes.item(i);
				
				subject = new Subject();
				
				subject.setName(  xpath.evaluate("@name", node) );
				subject.setQuestionSetFile(  xpath.evaluate("@file", node) );
				
				subjectMap.put(subject.getName(), subject);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public Subject getSubject(String name) {
		return subjectMap.get(name);
	}
	
	@SuppressLint("NewApi")
	public List getSubjects() {
		Object[] objArray = subjectMap.keySet().toArray();
		
		return Arrays.asList(objArray);
	}
}
