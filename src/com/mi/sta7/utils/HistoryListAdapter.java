package com.mi.sta7.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.R;
import com.mi.sta7.bean.HistoryBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HistoryListAdapter extends BaseAdapter {
	
	private List<HistoryBean> historyBeans;
	private Context context;
	private Map<Integer, View> historyViewMap = new HashMap<Integer, View>();
	
	public List<HistoryBean> getHistoryBeans() {
		return historyBeans;
	}

	public void setHistoryBeans(List<HistoryBean> historyBeans) {
		this.historyBeans = historyBeans;
	}

	public HistoryListAdapter(Context context, List<HistoryBean> historyBeans) {
		this.context = context;
		this.historyBeans = historyBeans;
	}
	
	@Override
	public int getCount() {
		return historyBeans.size();
	}

	@Override
	public Object getItem(int position) {
		return historyBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = historyViewMap.get(position);
		if(view==null) {
			view = LayoutInflater.from(context).inflate(R.layout.history_item, null);
			TextView historyNameTextView = (TextView) view.findViewById(R.id.history_name);
			historyNameTextView.setText(historyBeans.get(position).getHistory_name());
			ImageView historyUrliImageView = (ImageView) view.findViewById(R.id.history_img);
			ImageLoaderHelper.imageLoader.displayImage(historyBeans.get(position).getHistory_url(), historyUrliImageView);
			historyViewMap.put(position, view);
		}
		return view;
	}

	public void refresh(List<HistoryBean> historyBeans) {
		if(historyBeans != null && this.historyBeans != null) {
			this.historyBeans.addAll(historyBeans);
			this.notifyDataSetChanged();
		}
	}
	
}
