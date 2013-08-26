package com.mi.sta7.utils;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.R;
import com.mi.sta7.bean.ResponseBean;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class HomeListAdpter extends BaseAdapter {
	private static File cacheDir = null;

	private int imh;
	private int imw;
	private Context context;
	public List<ResponseBean> beans;
	@SuppressLint("UseSparseArrays")
	public Map<Integer, View> aMap = new HashMap<Integer, View>();

	public HomeListAdpter(Context context, List<ResponseBean> Beans) {
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		view = aMap.get(position);
		if (view == null) {
			final GetView getView = new GetView();
			view = LayoutInflater.from(context).inflate(
					R.layout.home_list_item, null);
			getView.imView = (ImageView) view.findViewById(R.id.home_list_img);
			String type = beans.get(position).getType();
			getView.imageView = (ImageView) view
					.findViewById(R.id.home_icn);
			
			if (type.equals("music")) {
				getView.imageView.setBackgroundResource(R.drawable.home_music);
			} else if (type.equals("av")) {
				getView.imageView.setBackgroundResource(R.drawable.home_movie);
			} else if (type.equals("pic")) {
				getView.imageView.setBackgroundResource(R.drawable.home_photo);
			} else if (type.equals("mix")) {
				getView.imageView.setBackgroundResource(R.drawable.latest_words);
			}

			getView.textViewName = (TextView) view
					.findViewById(R.id.home_list_tx_title);
			getView.textViewTitle = (TextView) view
					.findViewById(R.id.home_list_tx_name);
			Date date = new Date(Long.valueOf(beans.get(
					position).getCreated()));
		Timestamp timestamp = new Timestamp(Long.parseLong(beans.get(
				position).getCreated()+"000"));
			getView.textViewTitle.setText(beans.get(position).getTitle());
			getView.textViewName.setText(timestamp.toString().substring(0, 11));
			ImageLoaderHelper.imageLoader.loadImage(context,beans.get(position)
					.getImage() ,new SimpleImageLoadingListener()
			{
				@Override
				public void onLoadingComplete(Bitmap loadedImage) {
					super.onLoadingComplete(loadedImage);
					float scale = ((float)(MyTool.getWidth()-6))/loadedImage.getWidth();
					imh=(int)(scale*loadedImage.getHeight());
					imw=(int)(scale*loadedImage.getWidth());
					Matrix matrix = new Matrix();
				    // RESIZE THE BIT MAP
				    matrix.postScale(imw, imh);
				   getView.imView.setImageBitmap(getResizedBitmap(loadedImage, imh, imw));	
				    //getView.imView.setImageBitmap(getResizedBitmap(loadedImage, 50, 50));
					
				}
			});
			
			
			
		}

		return view;
	}
	
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    Matrix matrix = new Matrix();
	    matrix.postScale(scaleWidth, scaleHeight);
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	    return resizedBitmap;
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
