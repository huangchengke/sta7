package com.mi.sta7.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.R;
import com.mi.sta7.bean.OneImageBean;

public class GridViewAdpter extends BaseAdapter {
	public List<OneImageBean> beans;
	private Context context;

	public Map<Integer, View> aMap = new HashMap<Integer, View>();

	public GridViewAdpter(List<OneImageBean> beans, Context context) {
		this.beans = beans;
		this.context = context;
	}

	@Override
	public int getCount() {
		return beans.size();
	}

	@Override
	public Object getItem(int position) {
		return beans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = aMap.get(position);
		if (null == view) {
			GetView getView = new GetView();
			view = LayoutInflater.from(context).inflate(R.layout.gridview_item,
					null);
			getView.imageView = (ImageView) view.findViewById(R.id.item_image);
			getView.titleTextView = (TextView) view
					.findViewById(R.id.item_text);
			getView.titleTextView.setText(beans.get(position).getTitle());
			ImageLoaderHelper.imageLoader.displayImage(beans.get(position)
					.getImage(), getView.imageView);
			aMap.put(position, view);
		}
		return view;
	}

	static class GetView {
		ImageView imageView;
		TextView titleTextView;
	}

	public void refsh(List<OneImageBean> beans) {
		this.beans.addAll(beans);
		this.notifyDataSetChanged();

	}

}
