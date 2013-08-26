package com.mi.sta7.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.R;
import com.mi.sta7.bean.commentsBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentsListAdapter extends BaseAdapter {

	private Context context;
	private List<commentsBean> commentsBeans;
	private Map<Integer, View> commentsViewMap = new HashMap<Integer, View>();
	
	public CommentsListAdapter(Context context, List<commentsBean> commentsBeans) {
		this.context = context;
		this.commentsBeans = commentsBeans;
	}
	
	@Override
	public int getCount() {
		return commentsBeans.size();
	}

	@Override
	public Object getItem(int position) {
		return commentsBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = commentsViewMap.get(position);
		if(view==null) {
			view = LayoutInflater.from(context).inflate(R.layout.comments_item, null);
			ImageView comment_usr_icon = (ImageView) view.findViewById(R.id.comments_usr_icon);
			ImageLoaderHelper.imageLoader.displayImage(commentsBeans.get(position).getPic(), comment_usr_icon);
			TextView comments_usr_name = (TextView) view.findViewById(R.id.comments_usr_name);
			comments_usr_name.setText(commentsBeans.get(position).getUsr_name());
			TextView comment_content = (TextView) view.findViewById(R.id.comments_content);
			comment_content.setText(commentsBeans.get(position).getText());
			TextView comment_time = (TextView) view.findViewById(R.id.comments_time);
			comment_time.setText(commentsBeans.get(position).getTime());
			commentsViewMap.put(position, view);
		}
		return view;
	}
	
	public void refresh(List<commentsBean> commentsBeans) {
		if(commentsBeans != null && this.commentsBeans != null) {
			this.commentsBeans.addAll(commentsBeans);
			this.notifyDataSetChanged();
		}
	}
}
