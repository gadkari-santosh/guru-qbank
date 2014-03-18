package com.san.guru.model;

import java.io.IOException;
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
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import com.san.guru.R;
import com.san.guru.dto.Option;
import com.san.guru.dto.Question;

public class QuestionBank {

//	Activity activity = null;
	
	XmlResourceParser xml = null;
	
	private List<Subject> subjects = new ArrayList<Subject>();
	
	Map<Integer, Question> questions = new HashMap<Integer, Question>();
	
	int correct = 0;
	int wrong = 0;
	
	private int count = 0;
	
	private Context ctx = null;
	List<Question> listOfQuestions = new ArrayList();
	
	public QuestionBank(Context ctx) {
		this.ctx = ctx;
	}
	
	Map<String, Map<Integer, Question>> qBank = new HashMap<String, Map<Integer,Question>>();
	
	Map<String,Integer> config = new HashMap<String, Integer>();
	
	public void init(List<String> skillSet) {
//		loadStaticData();
//		
//		Resources res = activity.getResources();
//		this.subjects = skillSet;
//		
//		for (String skill : skillSet) {
//			load(new InputSource(res.openRawResource( config.get(skill.toUpperCase()) )), skill);
//		}
		
		Subjects subjects = Subjects.getInstance();
		
		AssetManager assetManager = ctx.getAssets();
		
		for (String subjectName : skillSet) {
			Subject subject = subjects.getSubject(subjectName);
			
			try {
				List<Question> questions = load(new InputSource(assetManager.open(subject.getQuestionSetFile())), subject.getName());
				
				subject.setQuestionSet(questions);
				
				this.subjects.add(subject);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private List<Question> load(InputSource inputStream, String skill) {
		int qId = 0;
		Map<Integer, Question> subject = new HashMap<Integer, Question>();
		
		List<Question> questions = new ArrayList<Question>();
		
		try {

			XPath xpath = XPathFactory.newInstance().newXPath();
			String expression = "//Test";
			NodeList nodes = null;
			
			nodes = (NodeList) xpath.evaluate(expression, inputStream, XPathConstants.NODESET);
			
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
				
				questions.add(question);
			}
			
			this.qBank.put(skill, subject);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return questions;
	}
	
	public Question getQuestion(int id) {
		return questions.get(id);
	}
	
	public Question nextQuestion() {
		
		Question question = null;
		if (listOfQuestions.size() == count)
			return null;
		
		question = listOfQuestions.get(count++);
		question.setId(count);
		
		return question;
	}
	
	public int getMaxQuestions() {
		return listOfQuestions.size();
	}
	
	public void result() {
		for (Subject subject : subjects) {
			subject.result();
			
			correct = correct + subject.getNumCorrrect();
			wrong = wrong + subject.getNumWrong();
		}
	}
	
	public int getNumCorrrect() {
		return correct;
	}
	
	public int getNumWrong() {
		return wrong;
	}
	
	public List<Subject> getSubjects() {
		return subjects;
	}
}
