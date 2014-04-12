package com.san.guru.socialmedia;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public interface ISocialMediaHandler {

	public void post(Activity activity, Bundle params);
	
	
	public void init(Context context);
}
