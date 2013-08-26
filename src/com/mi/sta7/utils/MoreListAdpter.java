package com.mi.sta7.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.string;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.R;
import com.mi.sta7.bean.ShareBean;

public class MoreListAdpter extends BaseAdapter {
	private static final String LOG_TAG = "MORELISTADAPTER";
	private int id;
	private List<ShareBean> shareBeans;
	private Context context;
	public Map<Integer, View> aMap = new HashMap<Integer, View>();
	
	public List<ShareBean> getShareBeans() {
		return shareBeans;
	}

	public void setShareBeans(List<ShareBean> shareBeans) {
		this.shareBeans = shareBeans;
	}

	public MoreListAdpter(List<ShareBean> shareBeans,Context context) {
		this.shareBeans=shareBeans;
		this.context=context;
	}
  
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getCount() {
		return shareBeans.size();
	}

	@Override
	public Object getItem(int position) {
		return shareBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view=aMap.get(position);
		if (view==null) {
			
			GetView geView=new GetView();
			view=LayoutInflater.from(context).inflate(R.layout.more_item, null);
			geView.imageView=(ImageView) view.findViewById(R.id.more_image);
			geView.userNameTextView=(TextView) view.findViewById(R.id.more_user_name);
			geView.dateTextView=(TextView) view.findViewById(R.id.date);
			geView.contenTextView=(TextView) view.findViewById(R.id.more_content);
			geView.descriptionTextView=(TextView) view.findViewById(R.id.description);
			geView.userNameTextView.setText(shareBeans.get(position).getName());
			geView.dateTextView.setText(shareBeans.get(position).getTime());
			geView.contenTextView.setText(shareBeans.get(position).getContent());
			if(shareBeans.get(position).getVerified_reason()!="" && shareBeans.get(position).getVerified_reason().length()!=0) {
				geView.descriptionTextView.setText("来自"+shareBeans.get(position).getVerified_reason());
			} else {
				geView.descriptionTextView.setText("");
			}
			ImageLoaderHelper.imageLoader.displayImage(shareBeans.get(position).getImg(), geView.imageView);
			aMap.put(position,view);
		}
		return view;
	}
	
	static class GetView {
		ImageView imageView;
		TextView userNameTextView,dateTextView,contenTextView,descriptionTextView;
	}
	
	public void refreshNews(List<ShareBean> nList) {
		if (null != shareBeans && null != nList) {
			this.shareBeans.addAll(nList);
			this.notifyDataSetChanged();
		}
	}
}
