package com.san.guru.widget;
 
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.san.guru.R;
import com.san.guru.model.SubjectFile;
 
 
public class MyCustomAdapter extends BaseAdapter {
	
    private List<SubjectFile> mListItems;
    private LayoutInflater mLayoutInflater;
    
    private Context context = null;

	private List<CheckedTextView> cViews = new ArrayList<CheckedTextView>();
	
    public MyCustomAdapter(Context context, List<SubjectFile> arrayList){
 
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

		CheckedTextView post = null;
		
		if (view == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.listview_subitems, null);
		}

		post = (CheckedTextView) view.findViewById(R.id.check);
		TextView textView = (TextView) view.findViewById(R.id.description);
		post.setText(mListItems.get(position).getTitle());
		textView.setText(mListItems.get(position).getDescription() + "    Size:" + mListItems.get(position).getSize());
		
		cViews.add(post);

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
    
//    public void filterData(String query){
//		   
//		  query = query.toLowerCase();
//		  
//		  mListItems.add("santosh");
//		  mListItems.add("santosh1");
//		  mListItems.remove(1);
//		 
//		  notifyDataSetChanged();
//		   
//		 }
}
