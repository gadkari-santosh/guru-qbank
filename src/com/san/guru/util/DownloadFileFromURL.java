package com.san.guru.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.san.guru.model.SubjectFile;
import com.san.guru.model.Subjects;
import com.san.guru.widget.MyCustomAdapter;

/**
 * Background Async Task to download file
 * */
public class DownloadFileFromURL extends AsyncTask<String, String, String> {

	private ProgressDialog pDialog;
	
	private List<String> downloadedFiles = new ArrayList<String>();
	
	ListView listView = null;
	
	Context ctx = null;
	
	ICallback callback = null;
	
	public DownloadFileFromURL(Context ctx, ListView listView, ICallback callback) {
		this.listView = listView;
		this.pDialog = new ProgressDialog(ctx);
		
		pDialog = new ProgressDialog(ctx);
		pDialog.setCancelable(true);
		pDialog.setMessage("File downloading ...");
		pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pDialog.setProgress(0);
		pDialog.setMax(100);
		
		this.ctx = ctx;
		
		this.callback = callback;
		Log.i("Download", "starting..");
		
	}
	
    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     * */
    @Override
    protected void onPreExecute() {
    	this.pDialog.setMessage("Progress start");
        this.pDialog.show();
    }

    /**
     * Downloading file in background thread
     * */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
        	
        	for (String eachURL : f_url) {
	            URL url = new URL(eachURL);
	            URLConnection conection = url.openConnection();
	            conection.connect();
	            // this will be useful so that you can show a tipical 0-100% progress bar
	            int lenghtOfFile = conection.getContentLength();
	            
	            // download the file
	            InputStream input = new BufferedInputStream(url.openStream(), 8192);
	            
	            CharSequence destination = eachURL.subSequence(eachURL.lastIndexOf("/")+1, eachURL.length());
	            
	            OutputStream output = pDialog.getContext().openFileOutput(String.valueOf(destination), Context.MODE_PRIVATE);
	            
	            pDialog.setProgress(10);
	            
	            byte data[] = new byte[1024];
	
	            long total = 0;
	
	            while ((count = input.read(data)) != -1) {
	                total += count;
	                // publishing the progress....
	                // After this onProgressUpdate will be called
	                pDialog.setProgress((int)((total*100)/lenghtOfFile));
	
	                // writing data to file
	                output.write(data, 0, count);
	            }
	
	            // flushing output
	            output.flush();
	
	            // closing streams
	            output.close();
	            input.close();
	            
	            Log.i("Download", "finish download..");
	            
	            downloadedFiles.add(String.valueOf(destination));
        	}
            
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return "done";
    }

    /**
     * Updating progress bar
     * */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        pDialog.setProgress(Integer.parseInt(progress[0]));
   }
    
    /**
     * After completing background task
     * Dismiss the progress dialog
     * **/
    @Override
    protected void onPostExecute(String file_url) {
    	if (pDialog.isShowing()) {
    		pDialog.dismiss();
        }

        callback.call(downloadedFiles);
        
//		try {
//			str = FileUtil.convertStreamToString(pDialog.getContext().openFileInput(this.destination));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//        
//        List<SubjectFile> subjectFiles = new ArrayList<SubjectFile>();
//        
//		String[] subjectArray = str.split(",");
//		for (String subject : subjectArray) {
//			SubjectFile subjectFile = new SubjectFile();
//			
//			String title = subject.substring(0, subject.indexOf("("));
//			String attributes = subject.substring(subject.indexOf("(")+1, subject.indexOf(")"));
//			
//			subjectFile.setTitle(title);
//			
//			if (attributes != null) {
//				String[] attrs = attributes.split(":");
//				subjectFile.setDescription(attrs[0]);
//				subjectFile.setSize(attrs[1]);
//				subjectFile.setFileName(attrs[2]);
//			}
//				
//			subjectFiles.add(subjectFile);
//		}
//		
//		MyCustomAdapter adapter = new MyCustomAdapter(ctx, subjectFiles);
//		listView.setAdapter(adapter);
//		adapter.notifyDataSetChanged();
    }
}