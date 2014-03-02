package com.san.guru;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import com.san.guru.dto.Option;
import com.san.guru.dto.Question;

public class QuestionBank {

	Activity activity = null;
	XmlResourceParser xml = null;
	
	private List subjects = null;
	
	Map<Integer, Question> questions = new HashMap<Integer, Question>();
	
	int correct = 0;
	int wrong = 0;
	
	private int count = 0;
	
	List<Question> listOfQuestions = new ArrayList();
	
	public QuestionBank(Activity activity) {
		this.activity = activity;
	}
	
	Map<String, Map<Integer, Question>> qBank = new HashMap<String, Map<Integer,Question>>();
	
	Map<String,Integer> config = new HashMap<String, Integer>();
	
	public void init(List<String> skillSet) {
		
		loadStaticData();
		
		Resources res = activity.getResources();
		this.subjects = skillSet;
		
		for (String skill : skillSet) {
			load(new InputSource(res.openRawResource( config.get(skill.toUpperCase()) )), skill);
		}
	}
	
	private void loadStaticData() {
		config.put("SPRING", R.raw.test_spring);
		config.put("J2EE", R.raw.test_j2ee);
		config.put("XML", R.raw.test_xml);
		config.put("CORE JAVA", R.raw.test_core_java);
	}
	
	private void load(InputSource inputStream,String skill) {
		int qId = 0;
		Map<Integer, Question> subject = new HashMap<Integer, Question>();
		
		try {
			Resources res = activity.getResources();
			
			XPath xpath = XPathFactory.newInstance().newXPath();
			String expression = "//Test";
			NodeList nodes = null;
			
			nodes = (NodeList) xpath.evaluate(expression, inputStream, XPathConstants.NODESET);
			
			List<Question> questions = new ArrayList<Question>();
			
			for (int i=0; i<nodes.getLength(); i++) {
				Question question = new Question();
				
				Node node = nodes.item(i);
				
				qId =  Integer.parseInt(xpath.evaluate("@id", node));
				question.setId(qId);
				question.setDescription( xpath.evaluate("Description", node) );
				question.setAnswer( Integer.parseInt(xpath.evaluate("Answer", node)) );
				
				List<Option> options = new ArrayList<Option>();
				
				NodeList optionNodeList = (NodeList) xpath.evaluate("Options/Option", node, XPathConstants.NODESET);
				for (int j=0; j<optionNodeList.getLength(); j++) {
					
					Option option = new Option();
					
					Node optionNode = (Node) optionNodeList.item(j);
					
					option.setId( Integer.parseInt(xpath.evaluate("@id", optionNode)));
					option.setText( optionNode.getTextContent().trim() );
					
					options.add(option);
				}
				question.setOptions(options);
				subject.put(qId, question);
				listOfQuestions.add(question);
				
			}
			
			this.qBank.put(skill, subject);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
//	public void init() {
//		int qId = 0;
//		
//		try {
//			Resources res = activity.getResources();
//			
//			XPath xpath = XPathFactory.newInstance().newXPath();
//			String expression = "//Test";
//			NodeList nodes = null;
//			
//			nodes = (NodeList) xpath.evaluate(expression,new InputSource(res.openRawResource(R.raw.test)), XPathConstants.NODESET);
//			
//			List<Question> questions = new ArrayList<Question>();
//			
//			for (int i=0; i<nodes.getLength(); i++) {
//				Question question = new Question();
//				
//				Node node = nodes.item(i);
//				
//				qId =  Integer.parseInt(xpath.evaluate("@id", node));
//				question.setId(qId);
//				question.setDescription( xpath.evaluate("Description", node) );
//				question.setAnswer( Integer.parseInt(xpath.evaluate("Answer", node)) );
//				
//				List<Option> options = new ArrayList<Option>();
//				
//				NodeList optionNodeList = (NodeList) xpath.evaluate("Options/Option", node, XPathConstants.NODESET);
//				for (int j=0; j<optionNodeList.getLength(); j++) {
//					
//					Option option = new Option();
//					
//					Node optionNode = (Node) optionNodeList.item(j);
//					
//					option.setId( Integer.parseInt(xpath.evaluate("@id", optionNode)));
//					option.setText( optionNode.getTextContent().trim() );
//					
//					options.add(option);
//				}
//				question.setOptions(options);
//				this.questions.put(qId, question);
//			}
//			
//		} catch (Exception e) {
//			Log.d("QuestionBank", e.toString());
//		}
//	}
	
	public Question getQuestion(int id) {
		return questions.get(id);
	}
	
	private int subCount = 0;
	
	public Question nextQuestion() {
		
		if (listOfQuestions.size() == count)
			return null;
		
		return listOfQuestions.get(count++);
	}
	
	public int getMaxQuestions() {
		return questions.size();
	}
	
	public void result() {
		
		
		for (Question question : questions.values()) {
			if (question.isCorrect())
				correct++;
			else 
				wrong++;
		}
	}
	
	public int getNumCorrrect() {
		return correct;
	}
	
	public int getNumWrong() {
		return wrong;
	}
}
