package com.san.guru.socialmedia;

import static com.san.guru.constant.AppConstants.*;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.san.guru.R;
import com.san.guru.util.ResourceUtils;

public class FacebookHandler implements ISocialMediaHandler {
	
	private static final String LOG_TAG = "FacebookHandler";
	
	private static final String TOKEN = "access_token";
    
	private static final String EXPIRES = "expires_in";
    
	private static final String KEY = "facebook-credentials";
    
	private Facebook facebook = null;
	
	private Context context = null;
	
	@Override
	public void post(Activity activity, Bundle params) {
		params.putString("name", params.getString(SM_TITLE));
		params.putString("caption", params.getString(SM_CAPTION) + " Test Round");
		params.putString("description", params.getString(SM_DESC));
		params.putString("link", ResourceUtils.getString(activity, R.string.fb_link));
		params.putString("picture", ResourceUtils.getString(activity, R.string.fb_picture));
		
		if (!ResourceUtils.isNetworkAvailable(context)) {
			Toast.makeText(context, "Check your Internet connection.", 100).show();
			return;
		}
		
		facebook.dialog(activity, "feed", params, new DialogListener() {
			
			@Override
			public void onFacebookError(FacebookError e) {
				Toast.makeText(context, "Facebook Err# Unable to post.", 100).show();
			}
			
			@Override
			public void onError(DialogError e) {
				Toast.makeText(context, "Facebook Err# Unable to post.", 100).show();
			}
			
			@Override
			public void onComplete(Bundle values) {
				Toast.makeText(context, "Your score posted on Facebook.", 100).show();
			}
			
			@Override
			public void onCancel() {
				Toast.makeText(context, "Facebook Cancel# Unable to post", 100).show();
			}
		});
		
	}

	@Override
	public void init(Context context) {
		try {
			this.context = context;
			facebook = new Facebook(ResourceUtils.getString(context, R.string.fb_app_id));
			restoreCredentials(facebook);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			Log.e(LOG_TAG, throwable.toString());
			
			Toast.makeText(context, "Err#Unable to open Facebook. ", 100).show();
		}
	}
	
	private boolean restoreCredentials(Facebook facebook) {
    	SharedPreferences sharedPreferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
    	facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
    	facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
    	return facebook.isSessionValid();
	}
}