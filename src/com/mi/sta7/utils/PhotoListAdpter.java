package com.mi.sta7.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.R;
import com.mi.sta7.bean.ImageBean;

public class PhotoListAdpter extends BaseAdapter {
	private Context context;
	public List<ImageBean> Beans;
	@SuppressLint("UseSparseArrays")
	public Map<Integer, View> aMap = new HashMap<Integer, View>();

	public PhotoListAdpter(Context context, List<ImageBean> ImageBean) {
		this.context = context;
		this.Beans = ImageBean;
	}


	@Override
	public int getCount() {
		return Beans.size();
	}

	@Override
	public Object getItem(int position) {
		return Beans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		view = aMap.get(position);

		if (view == null) {
			GetView getView=new GetView();
			view = LayoutInflater.from(context).inflate(
					R.layout.home_list_item, null);
			getView.imView = (ImageView) view.findViewById(R.id.home_list_img);
			getView.icoImageView=(ImageView) view.findViewById(R.id.home_icn);
			getView.icoImageView.setImageResource(R.drawable.latest_photo_icon);
			getView.textViewName = (TextView) view
					.findViewById(R.id.home_list_tx_title);
			getView.textViewTitle = (TextView) view
					.findViewById(R.id.home_list_tx_name);
			getView.textViewTitle.setText(Beans.get(position).getTitle());
			ImageLoaderHelper.imageLoader.displayImage(Beans.get(position).getImgUrl(), getView.imView);
			aMap.put(position, view);
		}

		return view;
	}

	static class GetView {
		ImageView imView,icoImageView;
		TextView textViewTitle;
		TextView textViewName;
	}

	public void refreshNews(List<ImageBean> nList) {
		this.Beans.addAll(nList);
		this.notifyDataSetChanged();

	}
}
