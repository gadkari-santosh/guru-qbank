package com.san.guru.model;

import static com.san.guru.constant.AppConstants.ASSET_SUBJECT;
import static com.san.guru.constant.AppConstants.DOWNLOADED_SUBJECT_FILE;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import android.util.Log;

import com.san.guru.constant.FILE_SOURCE;
import com.san.guru.util.FileUtil;

public class Subjects {

	private Map<String, Subject> subjectMap = new LinkedHashMap<String, Subject>();
	
	private List<String> downloadedSubjects = new ArrayList<String>();
	
	private static final Subjects INSTANCE = new Subjects();
	
	private Subjects() {}
	
	public static Subjects getInstance() {
		return INSTANCE;
	}
	
	public void init(Context ctx) {
		AssetManager assetManager = ctx.getAssets();
		
		try {
			InputStream inputStream = assetManager.open(ASSET_SUBJECT);
			loadXml(inputStream);
			
			FileInputStream downloadedFileStream = ctx.openFileInput(DOWNLOADED_SUBJECT_FILE);
			String content = FileUtil.convertStreamToString(downloadedFileStream);
			
			if (content != null) {
				String[] subjectArray = content.split(",");
				for (String subject : subjectArray) {
					Subject subjectFile = new Subject();
					
					String title = subject.substring(0, subject.indexOf("("));
					String attributes = subject.substring(subject.indexOf("(")+1, subject.indexOf(")"));
					
					subjectFile.setName(title);
					
					if (attributes != null) {
						String[] attrs = attributes.split(":");
						subjectFile.setFileSource(FILE_SOURCE.LOCAL_STORAGE);
						subjectFile.setQuestionSetFile(attrs[2]);
					}
						
					subjectMap.put(title, subjectFile);
					downloadedSubjects.add(title);
				}
			}
		} catch (Exception e) {
			Log.e("error", e.toString());
		}
	}
	
	@SuppressLint("NewApi")
	private void loadXml(InputStream inputStream) {
		Node node = null;
		
		Subject subject = null;
		
		try {
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
	public List getSubjectNames() {
		Object[] objArray = subjectMap.keySet().toArray();
		
		return Arrays.asList(objArray);
	}
	
	public Collection<Subject> getSubjects() {
		return subjectMap.values();
	}
	
	public void removeSubject(String subject) {
		subjectMap.remove(subject);
	}
	
	public void removeAllDownloadedSubjects() {
		for (String  subject : downloadedSubjects) {
			subjectMap.remove(subject);
		}
	}
}