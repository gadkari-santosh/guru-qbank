package com.san.guru.activities;

import static com.san.guru.constant.AppConstants.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.internal.in;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.san.guru.R;
import com.san.guru.dto.IntentData;
import com.san.guru.dto.Profile;
import com.san.guru.dto.TestResult;
import com.san.guru.util.FileUtil;
import com.san.guru.util.GsonUtil;
import com.san.guru.util.ResourceUtils;
import com.san.guru.widget.SavedResultAdapter;

public class ProfileActivity extends AbstractActivity {

	private static int RESULT_LOAD_IMAGE = 1;
	
	private Profile profile = new Profile();
	
	private String displayImagePath = null;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_home:
	        	
	        	saveProfile();
				Intent intent = new Intent(this, ChooseSubjectActivity.class);
				this.startActivity(intent);
				
				finish();
	            return true;
	        case R.id.action_quit:
	        	
	        	saveProfile();
	        	finish();
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		saveProfile();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		saveProfile();
	}
	
	private void saveProfile() {
		String file =  ResourceUtils.getString(ProfileActivity.this, R.string.profile_file);
		
		EditText name = (EditText) findViewById(R.id.editTextName);
		
		if (FileUtil.exists(this, file)) {
			this.profile = GsonUtil.getObject(this, file, Profile.class);
			if (profile == null) {
				profile = new Profile();
			}
			profile.setName(name.getText().toString());
		} else {
			this.profile.setName(name.getText().toString());
		}
		
		if (displayImagePath != null) 
			this.profile.setBitmapPath(displayImagePath);
		
		Gson gson = new Gson();
		String profileContent = gson.toJson(this.profile);
		FileUtil.saveFileInPhone(this, file, profileContent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_profile);
		
		ImageView image = (ImageView) findViewById(R.id.profileImg);
		EditText name = (EditText) findViewById(R.id.editTextName);
		
		final ArrayList<TestResult> results = new ArrayList<TestResult>();
				
		Button record = (Button) findViewById(R.id.showRecords);
		
		record.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ProfileActivity.this, RecordsActivity.class);
				IntentData data = new IntentData();
				data.putValue(INTENT_RECORDS, results);
				
				intent.putExtra(INTENT_DATA, data);
				
				ProfileActivity.this.startActivity(intent);
				
				finish();
			}
		});
		
		name.clearFocus();
		image.requestFocus();
		image.setFocusable(true);
		
		image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(
						Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						 
				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});
		
		String file =  ResourceUtils.getString(ProfileActivity.this, R.string.result_file);
		String profileFile =  ResourceUtils.getString(ProfileActivity.this, R.string.profile_file);
		
		if (FileUtil.exists(this, file)) {
			String content = FileUtil.getFile(ProfileActivity.this, file);
			
			Type listOfTestObject = new TypeToken<List<TestResult>>(){}.getType();
			Gson gson = new Gson();
			List<TestResult> resultFromJson = gson.fromJson(content, listOfTestObject);
			results.addAll(resultFromJson);
			
			record.setText(String.format("Records (%s)", results.size()));
		}
		
		if (FileUtil.exists(this, profileFile)) {
			profile = GsonUtil.getObject(this, profileFile, Profile.class);
			if ( profile == null)
				profile = new Profile();
		}
		
		if (profile.getBitmapPath() != null) {
	        Bitmap imageFile = BitmapFactory.decodeFile(profile.getBitmapPath());
	        
	        if (imageFile == null)
	       	 Toast.makeText(ProfileActivity.this, "Unable to load image", 100).show();
	        else
	       	 image.setImageBitmap(getRoundedCornerBitmap(imageFile,60));
		}
		
		Toast.makeText(this, "Name : " + profile.getName(), 100);
		
		if (profile.getName() != null && !"My Name".equalsIgnoreCase(profile.getName())) {
			name.setText(profile.getName());
		}
		
		setAd();
	}
	
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	     super.onActivityResult(requestCode, resultCode, data);
	      
	     if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
	         Uri selectedImage = data.getData();
	         String[] filePathColumn = { MediaStore.Images.Media.DATA };
	 
	         Cursor cursor = getContentResolver().query(selectedImage,
	                 filePathColumn, null, null, null);
	         cursor.moveToFirst();
	 
	         int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	         String picturePath = cursor.getString(columnIndex);
	         cursor.close();
	                      
	         ImageView image = (ImageView) findViewById(R.id.profileImg);
	         
	         Bitmap imageFile = BitmapFactory.decodeFile(picturePath);
	         
	         if (imageFile == null)
	        	 Toast.makeText(ProfileActivity.this, "Unable to load image", 100).show();
	         else
	        	 image.setImageBitmap(getRoundedCornerBitmap(imageFile,60));
	         
	         profile.setBitmapPath(picturePath);
	         
	         this.displayImagePath = picturePath;
	         // String picturePath contains the path of selected Image
	     }
	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
