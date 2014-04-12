package com.san.guru.model;

import static com.san.guru.constant.AppConstants.ATTEMPTED_FILE_FORMAT;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xml.sax.InputSource;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.san.guru.dto.Attempted;
import com.san.guru.dto.Question;
import com.san.guru.util.FileUtil;
import com.san.guru.util.GsonUtil;

public class QuestionBank {
	private static final String LOG_TAG = "QuestionBank";
	
	private static QuestionBank INSTANCE = null; 

	private List<Subject> subjects = new ArrayList<Subject>();
	
	private int correct = 0;
	private int wrong = 0;
	private int attempt = 0;
	
	private int count = -1;
	
	private Context ctx = null;
	
	private List<Question> listOfQuestions = new ArrayList();
	
	private Map<String, Map<Integer, Question>> qBank = new HashMap<String, Map<Integer,Question>>();
	
	private Map<String,Integer> config = new HashMap<String, Integer>();
	
	private boolean init = false;
	
	private QuestionBank(Context ctx) {
		this.ctx = ctx;
	}
	
	public static synchronized QuestionBank getNewInstance(Context context) {
		if (INSTANCE == null)
			INSTANCE = new QuestionBank(context);
		
		return INSTANCE;
	}
	
	public static QuestionBank getInstance() {
		return INSTANCE;
	}
	
	public boolean isInitialized() {
		return init;
	}
	
	public void init(Set<String> subjects) {
		init(subjects, -1);
	}
	
	public void init(Set<String> skillSet, int numQuestions) {
		init = false;
		
		subjects.clear();
		listOfQuestions.clear();
		qBank.clear();
		
		count = -1;
		correct =0;
		wrong = 0;
		
		Subjects subjects = Subjects.getInstance();
		
		AssetManager assetManager = ctx.getAssets();
		
		int select = numQuestions / skillSet.size();
		int remaining = numQuestions % skillSet.size();
		
		
		for (String subjectName : skillSet) {
			if ("All".equalsIgnoreCase(subjectName))
				continue;
			
			Subject subject = subjects.getSubject(subjectName);
			
			try {
				
				InputSource inputSource = null;
				
				switch (subject.getFileSource()) {
					case APP:
						inputSource = new InputSource(assetManager.open(subject.getQuestionSetFile()));
						break;
					case LOCAL_STORAGE:
						inputSource = new InputSource(ctx.openFileInput(subject.getQuestionSetFile()));
						break;
				}
				
				List<Question> questions = load(inputSource, subject.getName());
				
				List<Question> unAttemptedQuestions = null;
				
				if ( remaining > 0) {
					unAttemptedQuestions = getUnAttemptedQuestions(subjectName, questions, select + 1);
					remaining--;
				} else { 
					unAttemptedQuestions = getUnAttemptedQuestions(subjectName, questions, select);
				}
				
				listOfQuestions.addAll(unAttemptedQuestions);
				subject.setQuestionSet(unAttemptedQuestions);
				
				this.subjects.add(subject);
				
			} catch (IOException e) {
				Log.e(LOG_TAG, e.toString(), e);
				e.printStackTrace();
			}
		}
		
		init = true;
	}
	
	private List<Question> getUnAttemptedQuestions(String subject, List<Question> questions, int select) {
		String attemptFileName = String.format(ATTEMPTED_FILE_FORMAT, subject);
		
		Attempted attempted = null;
		
		List<Question> unAttemptedQuestions = new ArrayList<Question>();
		
		if (!FileUtil.exists(ctx, attemptFileName)) {
			attempted = makeAllQuestionsUnAttempted(attemptFileName, subject, questions);
		} else {
			String json = FileUtil.getFile(ctx, attemptFileName);
			if (json == null || "".equals(json.trim())) {
				attempted = makeAllQuestionsUnAttempted(attemptFileName, subject, questions);
			} else {
				attempted = GsonUtil.getObject(json, Attempted.class);
			}			
		}
		
		// Two possibilities. either file exists or becaise of error while creating
		// new file, attempted file may not be created.
		
		if (attempted != null) {
			Iterator<Integer> unattempedIds = attempted.getUnAttemped().iterator();
			for (int i=0; i<select; i++) {
				if (unattempedIds.hasNext()) {
					int id = unattempedIds.next();
					if (questions.size() > id) {
						Question question = questions.get(id);
						question.setSubject(subject);
						
						unAttemptedQuestions.add(question); 
					} else {
						attempted = makeAllQuestionsUnAttempted(attemptFileName, subject, questions);
						unattempedIds = attempted.getUnAttemped().iterator();
						i--;
					}
				} else {
					attempted = makeAllQuestionsUnAttempted(attemptFileName, subject, questions);
					unattempedIds = attempted.getUnAttemped().iterator();
					i--;
				}
			}
		}
		
		return unAttemptedQuestions;
	}
	
	private Attempted makeAllQuestionsUnAttempted(String attemptFileName, 
											 String subject,
											 List<Question> questions) {
		Attempted attempted = null;
		
		try {
			OutputStream output = ctx.openFileOutput(attemptFileName, Context.MODE_PRIVATE);
			
			attempted = new Attempted();
			attempted.setSubject(subject);
			attempted.setNumQuestions(questions.size());
			
			Set<Integer> unatttempted = new HashSet();
			for (Question question : questions) {
				unatttempted.add(question.getId());
			}
			attempted.setUnAttemped(unatttempted);
			
			String fileContent = GsonUtil.getJSon(attempted, Attempted.class);
			
			PrintWriter writer = new PrintWriter(output);
			writer.write(fileContent);
			writer.flush();
			writer.close();
			
		} catch (FileNotFoundException e1) {
			Log.e(LOG_TAG, e1.toString(), e1);
			Toast.makeText(ctx, "Unable to create File. Please check settings or space on your mobile.", 100);
		}
		
		return attempted;
	}
	
	private List<Question> load(InputSource inputStream, String skill) throws IOException {
		InputStream reader = inputStream.getByteStream();
        
		StringBuilder b = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(reader));
        
        for(String line; (line = r.readLine()) != null; ) {
            b.append(line).append("\n");
        }

        Type listOfTestObject = new TypeToken<List<Question>>(){}.getType();
        Gson gson = new Gson();
        List<Question> obj = gson.fromJson(b.toString(), listOfTestObject);
        
        return obj;
	}
	
	public Question nextQuestion() {
		count++;
		
		Question question = null;
		if (listOfQuestions.size() == count)
			return null;
		
		question = listOfQuestions.get(count);
		//question.setId(count+1);
		
		return question;
	}
	
	public int getSequence() {
		return count;
	}
	
	public Question backQuestion() {
		
		Question question = null;
		if (count <= 0)
			return null;
		
		question = listOfQuestions.get(--count);
		question.setId(count+1);
		
		return question;
	}
	
	public void resetCounter() {
		count = -1;
	}
	
	public int getMaxQuestions() {
		return listOfQuestions.size();
	}
	
	public void result() {
		correct = 0;
		wrong   = 0;
		attempt = 0;
		
		for (Subject subject : subjects) {
			subject.result();
			
			correct = correct + subject.getNumCorrrect();
			wrong = wrong + subject.getNumWrong();
			attempt = attempt + subject.getNumAttempted();
		}
	}
	
	public int getNumCorrrect() {
		return correct;
	}
	
	public int getNumWrong() {
		return wrong;
	}
	
	public int getNumAttempted() {
		return attempt;
	}
	
	public List<Subject> getSubjects() {
		return subjects;
	}
	
	public void markAttempted(Question question) {
		String attemptFileName = String.format(ATTEMPTED_FILE_FORMAT, question.getSubject());
		
		String content = FileUtil.getFile(ctx, attemptFileName);
		Attempted attempted = GsonUtil.getObject(content, Attempted.class);
		attempted.getUnAttemped().remove(question.getId());
		
		String json = GsonUtil.getJSon(attempted, Attempted.class);
		FileUtil.saveFileInPhone(ctx, attemptFileName, json);
	}
}
