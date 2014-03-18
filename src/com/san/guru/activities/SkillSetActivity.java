package com.san.guru.activities;

import static com.san.guru.constant.AppContents.INTENT_DATA;
import static com.san.guru.constant.AppContents.INTENT_SKILL_SET;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.san.guru.R;
import com.san.guru.dto.IntentData;
import com.san.guru.model.Subjects;
import com.san.guru.util.UIUtils;

public class SkillSetActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		final ArrayList checkedItems = new ArrayList();
		
		Subjects subjects = Subjects.getInstance();
		subjects.init(this);
		
		String[] GENRES = subjects.getSubjects();
		
//		String[] GENRES = new String[] {
//			        "Core Java", "Servlet", "JSP", "XML", "Hibernate", "Spring", "SQL",
//			        "EJB 3.0", "Design Patterns", "Junit", "Java Script", "Application Servers", "JavaFX", "SoftwareEngg."
//			    	};
//		   
		setContentView(R.layout.layout_choose_subject);
		   
		DisplayMetrics dimension = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dimension);
        int height = dimension.heightPixels;
        
		final ListView  lView = (ListView) findViewById(R.id.listView1);
		lView.setAdapter(new ItemsAdapter(this, GENRES));
		lView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		lView.setOnItemClickListener(new OnItemClickListener() {

			   @Override
			   public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			     long arg3) {
				   
				   CheckedTextView view =  ((CheckedTextView)arg1);
				   if (view.isChecked()) {
					   checkedItems.add(view.getText());
				   } else {
					   checkedItems.remove(view.getText());
				   }
			   	}
			}
		);
		
		View gView  = (View) findViewById(R.id.grid_2);
		View gView3 = (View) findViewById(R.id.grid_3);
		View gView1 = (View) findViewById(R.id.grid_1);
		
		Button button = (Button) findViewById(R.id.butSubmitSkillSet);
		button.setOnClickListener(new OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SkillSetActivity.this, TestTypeActivity.class);
				
				SparseBooleanArray sba = lView.getCheckedItemPositions();
					
				checkedItems.clear();
				
				for ( int cnt=0; cnt<sba.size(); cnt++) {
					int boo = sba.keyAt(cnt);
						
					CheckedTextView ctv = (CheckedTextView)lView.getItemAtPosition(boo);
					if (ctv.isChecked())
						checkedItems.add(ctv.getText());
				}					
				
				IntentData data = new IntentData();
				data.putValue(INTENT_SKILL_SET, checkedItems);
				
				intent.putExtra(INTENT_DATA, data);
				SkillSetActivity.this.startActivity(intent);
				
				finish();
			}
		});
		
		TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, getResources().getDisplayMetrics());
				
		LayoutParams layoutParams = gView.getLayoutParams();
		int h = layoutParams.height;
		int k = UIUtils.dpToPx(372, this);
		layoutParams.height = height - (gView3.getLayoutParams().height+gView1.getLayoutParams().height + (int)(height * 0.165));
		layoutParams.width = android.widget.GridLayout.LayoutParams.MATCH_PARENT;
	}

	private class ItemsAdapter extends BaseAdapter {

			private String[] items;
			private Context context = null;
			
			private List<String> listOfSkillSet = new ArrayList<String>();
			
			ArrayList<CheckedTextView> cViews = new ArrayList<CheckedTextView>();
			
			public ItemsAdapter(Context context, String[] item) {
				this.items = item;
				
				Intent intent = ((Activity)context).getIntent();
				if (intent != null && intent.getExtras() != null) {
					IntentData data = (IntentData) intent.getExtras().get(INTENT_DATA);
					
					if (data != null) {
						listOfSkillSet = (List) data.getValue(INTENT_SKILL_SET);
					}
				}
			}

			// @Override

			public View getView(int position, View convertView, ViewGroup parent) {

				View v = convertView;

				if (v == null) {
					LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = vi.inflate(R.layout.items, null);
				}
				
				CheckedTextView post = (CheckedTextView) v.findViewById(R.id.checkList);
				post.setText(items[position]);
				
				if (listOfSkillSet.contains(items[position])) {
					((ListView)parent).setItemChecked(position, true);
				} else {
					((ListView)parent).setItemChecked(position, false);
				}
				
				cViews.add(post);

				return v;
			}

			public int getCount() {

				return items.length;

			}

			public Object getItem(int position) {
				if (cViews.size() < position)
					return null;
				return cViews.get(position);
			}

			public long getItemId(int position) {
				return position;
			}
		}
}
