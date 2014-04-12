package com.san.guru.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class FileUtil {
	
	private static final String LOG_TAG = "FileUtil";

	public static String convertStreamToString(InputStream is)  {
		if (is == null)
			return null;
		
		try {
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    StringBuilder sb = new StringBuilder();
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		      sb.append(line).append("\n");
		    }
		    reader.close();
		    return sb.toString();
		} catch (Exception exp) {
			Log.e("FileUtil", exp.toString());
			return null;
		}
	}

	public static String getStringFromFile (String filePath) throws Exception {
	    File fl = new File(filePath);
	    FileInputStream fin = new FileInputStream(fl);
	    String ret = convertStreamToString(fin);
	    //Make sure you close all streams.
	    fin.close();        
	    return ret;
	}
	
	public static String getFileFromAsset(Context ctx, String path) {
		try {
			return convertStreamToString( ctx.getAssets().open(path) );
		} catch (IOException e) {
			return null;
		}
	}
	
	public static void saveFileInPhone(Context ctx, String path, String content) {
		try {
			OutputStream output = ctx.openFileOutput(path, Context.MODE_PRIVATE);
			PrintWriter writer = new PrintWriter(output);
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean existsInAsset(Context ctx, String fileName) {
		try {
			AssetManager assets = ctx.getAssets();
			assets.open(fileName);
			return true;
		} catch (Exception exp) {
			return false;
		}
	}
	
	public static boolean exists(Context ctx, String fileName) {
		try {
			ctx.openFileInput(fileName);
			return true;
		} catch (Exception exp) {
			return false;
		}
	}
	
	public static String getFile(Context ctx, String fileName) {
		try {
			return convertStreamToString( ctx.openFileInput(fileName) );
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, e.toString(), e);
			return null;
		}
	}
}
