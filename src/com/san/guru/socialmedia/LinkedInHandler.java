package com.san.guru.socialmedia;

import static com.san.guru.constant.AppConstants.*;
import static com.san.guru.constant.AppConstants.LINKEDIN_POST;
import static com.san.guru.constant.AppConstants.LINKEDIN_TITLE;

import java.util.EnumSet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.schema.Person;
import com.google.code.linkedinapi.schema.VisibilityType;
import com.san.guru.R;
import com.san.guru.socialmedia.LinkedinDialog.OnVerifyListener;
import com.san.guru.util.ResourceUtils;

public class LinkedInHandler implements ISocialMediaHandler {

	@Override
	public void post(final Activity activity,final Bundle bundle) {
		
		
		
		ProgressDialog progressDialog = new ProgressDialog(activity);//.show(LinkedInSampleActivity.this, null, "Loadong...");
		
		LinkedinDialog linkedInDialog = new LinkedinDialog(activity, progressDialog);
		linkedInDialog.show();
		
		//set call back listener to get oauth_verifier value
		linkedInDialog.setVerifierListener(new OnVerifyListener()
		{
			Toast toast = null;
			
			@Override
			public void onVerify(String verifier)
			{
				try
				{
					Log.i("LinkedinSample", "verifier: " + verifier);
					
					if (!ResourceUtils.isNetworkAvailable(activity.getApplicationContext())) {
						Toast.makeText(activity.getApplicationContext(), "Check your Internet connection.", 100).show();
						return;
					}

					LinkedInAccessToken accessToken = LinkedinDialog.oAuthService.getOAuthAccessToken(LinkedinDialog.liToken, verifier); 
					LinkedInApiClient client = LinkedinDialog.factory.createLinkedInApiClient(accessToken);

					Log.i("LinkedinSample", "ln_access_token: " + accessToken.getToken());
					Log.i("LinkedinSample", "ln_access_token: " + accessToken.getTokenSecret());
					
					Person profile = client.getProfileForCurrentUser(EnumSet.of(ProfileField.FIRST_NAME, ProfileField.LAST_NAME, ProfileField.HEADLINE));
					toast = Toast.makeText(activity, "Logged In. Welcome " +profile.getFirstName(), 190);
					toast.show();
					
					client.postShare(bundle.getString(LINKEDIN_POST), 
							bundle.getString(SM_TITLE),
							ResourceUtils.getString(activity, R.string.fb_desc), 
							bundle.getString(SM_CAPTION),
							ResourceUtils.getString(activity, R.string.ln_link), 
							VisibilityType.ANYONE);
					
					toast = Toast.makeText(activity, profile.getFirstName() + ", Your score Posted.", 190);
					toast.show();
				}
				catch (Exception e) 
				{
					Toast.makeText(activity.getApplicationContext(), "Err#Unable to open LinkedIn.", 100).show();
					Log.i("LinkedinSample", "error to get verifier" + e);
					e.printStackTrace();
				}
			}
		});
		
		//set progress dialog 
		progressDialog.setMessage("Loading...");
		progressDialog.setCancelable(true);
		progressDialog.show();
	}
	
	@Override
	public void init(Context context) {
		// NOP
	}
}
