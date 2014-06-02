package com.san.guru.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.san.guru.R;
import com.san.guru.util.FileUtil;
import com.san.guru.util.ResourceUtils;

@SuppressLint("NewApi")
public class AbstractActivity extends Activity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar ab = getActionBar(); 
        ColorDrawable colorDrawable = new ColorDrawable(ResourceUtils.getColor(this, R.color.AppColor));     
        ab.setBackgroundDrawable(colorDrawable);
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
       }
	}
	
	protected void setAd() {
		AdView adView = (AdView) findViewById(R.id.adView);
		
		// Create an ad request. Check logcat output for the hashed device ID to
	    // get test ads on a physical device.
	    AdRequest adRequest = new AdRequest.Builder()
	        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	        .addTestDevice("INSERT_YOUR_HASHED_DEVICE_ID_HERE")
	        .build();
	    
	    adView.loadAd(adRequest);
	}
	
	protected void setHeightOfMainArea () {
		int height = 0;
		
		DisplayMetrics dimension = new DisplayMetrics();
		
		View gView1 = (View) findViewById(R.id.grid_1);
		View gView2 = (View) findViewById(R.id.grid_2);
		View gView3 = (View) findViewById(R.id.grid_3);
		
		TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, getResources().getDisplayMetrics());

		getWindowManager().getDefaultDisplay().getMetrics(dimension);
		height = dimension.heightPixels;
		
		LayoutParams layoutParams = gView2.getLayoutParams();
		
		layoutParams.height = height - (gView3.getLayoutParams().height
									    + gView1.getLayoutParams().height 
									    + (int) (height * 0.17));
		
		layoutParams.width = android.widget.GridLayout.LayoutParams.MATCH_PARENT;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ActionBar bar = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(ResourceUtils.getColor(this, R.color.AppColor));     
        bar.setBackgroundDrawable(colorDrawable);

	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_home:
	        	
				Intent intent = new Intent(this, ChooseSubjectActivity.class);
				this.startActivity(intent);
				
				finish();
	            return true;
	        case R.id.action_quit:
	        	finish();
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	protected void setNotice() {
		String onlineNotice = "";
		try {
			onlineNotice = FileUtil.readFromUrl(ResourceUtils.getString(this, R.string.notice_url));
		} catch (Exception e) {
			e.printStackTrace();
			;
		}
		
		TextView marqueeText=(TextView)findViewById(R.id.marquee_text);
		marqueeText.setText("Use download button to checkout latest question set." + " |  " +onlineNotice);
		marqueeText.setSelected(true);
		marqueeText.setTypeface(null, Typeface.BOLD);
		marqueeText.setSingleLine();
		marqueeText.setEllipsize(TruncateAt.MARQUEE);
		marqueeText.setHorizontallyScrolling(true);
		
		marqueeText.requestFocus();
	}
}
