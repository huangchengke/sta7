package com.mi.sta7.utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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
import com.mi.sta7.bean.ResponseBean;
public class HomeListAdpter2 extends BaseAdapter {
	private Context context;
	public List<ResponseBean> beans;
	@SuppressLint("UseSparseArrays")

	public HomeListAdpter2(Context context, List<ResponseBean> Beans) {
		this.context = context;
		this.beans = Beans;
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
/**
 * @Override  
       @Override  
        public View getView(final int position, View convertView,  
                ViewGroup parent)  
        {  
            View view = convertView;  
            final ViewHolder holder;  
            if (convertView == null)  
            {  
                view = getLayoutInflater().inflate(R.layout.item_list_image,  
                        null);  
                holder = new ViewHolder();  
                holder.text = (TextView) view.findViewById(R.id.text);  
                holder.image = (ImageView) view.findViewById(R.id.image);  
                view.setTag(holder);  
            } else  
                holder = (ViewHolder) view.getTag();  
  
            holder.text.setText("Item " + position);  
  
            imageLoader  
                    .displayImage(imageUrls[position], holder.image, options);  
  
            return view;  
        }  
 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view=convertView;
		final GetView getView;
		if (view == null) {
			getView= new GetView();
			view = LayoutInflater.from(context).inflate(
					R.layout.home_list_item, null);
			getView.imView = (ImageView) view.findViewById(R.id.home_list_img);
			getView.imageView = (ImageView) view
					.findViewById(R.id.home_icn);
			getView.textViewName = (TextView) view
					.findViewById(R.id.home_list_tx_title);
			getView.textViewTitle = (TextView) view
					.findViewById(R.id.home_list_tx_name);
			view.setTag(getView);
		}
		else {
			getView=(GetView) view.getTag();
		}
		String type = beans.get(position).getType();
		if (type.equals("music")) {
			getView.imageView.setBackgroundResource(R.drawable.home_music);
		} else if (type.equals("av")) {
			getView.imageView.setBackgroundResource(R.drawable.home_movie);
		} else if (type.equals("pic")) {
			getView.imageView.setBackgroundResource(R.drawable.home_photo);
		} else if (type.equals("mix")) {
			getView.imageView.setBackgroundResource(R.drawable.latest_words);
		}
		Date date = new Date(Long.valueOf(beans.get(
				position).getCreated()));
	Timestamp timestamp = new Timestamp(Long.parseLong(beans.get(
			position).getCreated()+"000"));
	getView.textViewTitle.setText(beans.get(position).getTitle());
	getView.textViewName.setText(timestamp.toString().substring(0, 11));
		ImageLoaderHelper.imageLoader.displayImage(beans.get(position)
				.getImage(), getView.imView);
		return view;
	}

	static class GetView {
		ImageView imView, imageView;
		TextView textViewTitle;
		TextView textViewName;
	}

	public void refreshNews(List<ResponseBean> newBean) {
		this.beans.addAll(newBean);
		this.notifyDataSetChanged();

	}

}
