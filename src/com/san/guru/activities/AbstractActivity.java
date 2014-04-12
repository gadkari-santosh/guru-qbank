package com.san.guru.activities;

import com.san.guru.R;

import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class AbstractActivity extends Activity {

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
									    + (int) (height * 0.165));
		
		layoutParams.width = android.widget.GridLayout.LayoutParams.MATCH_PARENT;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
	
}
