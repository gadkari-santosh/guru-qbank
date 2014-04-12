package com.san.guru.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class BackgroundTask extends AsyncTask<ICallback, String, String> {

	private Context ctx = null;
	private ProgressDialog progressBar = null;
	private ICallback postExecution = null;
	
	public BackgroundTask(Context ctx,ProgressDialog bar, ICallback postExecution) {
		this.ctx = ctx;
		this.progressBar = bar;
		this.postExecution = postExecution;
	}
	
	@Override
	protected String doInBackground(ICallback... params) {
		params[0].call(null);
		return null;
	}
	
	 protected void onPostExecute(String file_url) {
	    	if (progressBar.isShowing()) 
	    		progressBar.dismiss();
	    	
	    	postExecution.call(null);
	 }
}
