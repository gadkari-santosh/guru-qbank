package com.san.guru.widget;

import static com.san.guru.constant.AppConstants.INTENT_DATA;
import static com.san.guru.constant.AppConstants.INTENT_RESULT;
import static com.san.guru.constant.AppConstants.MODE;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.san.guru.R;
import com.san.guru.activities.RecordsActivity;
import com.san.guru.activities.ResultActivity;
import com.san.guru.constant.Mode;
import com.san.guru.dto.IntentData;
import com.san.guru.dto.TestResult;

public class SavedResultAdapter extends BaseAdapter {
	
    private List<TestResult> mListItems;
    private LayoutInflater mLayoutInflater;
    
    private Context context = null;

    public SavedResultAdapter(Context context, List<TestResult> arrayList){
 
        mListItems = arrayList;
        
        this.context = context;
        
        //get the layout inflater
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    @Override
    public int getCount() {
        //getCount() represents how many items are in the list
        return mListItems.size();
    }
 
    @Override
    //get the data of an item from a specific position
    //i represents the position of the item in the list
    public Object getItem(int i) {
        return null;
    }
 
    @Override
    //get the position id of the item from the list
    public long getItemId(int i) {
        return 0;
    }
 
    @Override
	public View getView(int position, View view, ViewGroup parent) {
    	
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    	
		if (view == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.listview_records, null);
		}

		TextView examTime = (TextView) view.findViewById(R.id.examTime);
		final Button record   = (Button) view.findViewById(R.id.record);
		
		final TestResult result = mListItems.get(position);
		
		record.setText(String.format("%s%% (%s out of %s)", result.getPercentStr(), result.getTotalCorrect(), result.getTotalQuestions()));
		Listener listener = new Listener();
		listener.setId(position);
		
		record.setOnClickListener(listener);
		
		Date date = result.getDate();
		if (date == null)
			date = new Date();
		
		examTime.setText(simpleDateFormat.format(date));
		
		return view;
	}
 
    /**
     * Static class used to avoid the calling of "findViewById" every time the getView() method is called,
     * because this can impact to your application performance when your list is too big. The class is static so it
     * cache all the things inside once it's created.
     */
    private static class ViewHolder {
        protected TextView itemName;
    }
    
    private class Listener implements OnClickListener {
    	
    	private int id = 0;
    	
		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		@Override
		public void onClick(View v) {
			TestResult result = mListItems.get(getId());
			IntentData intentData = new IntentData();
			intentData.putValue(INTENT_RESULT, result);
			intentData.putValue(MODE, Mode.RESULT_DISPLAY);
			
			Intent intent = new Intent(v.getContext(), ResultActivity.class);
			intent.putExtra(INTENT_DATA, intentData);
			
			v.getContext().startActivity(intent);
		}
    	
    }
}
