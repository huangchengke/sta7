package com.mi.sta7.utils;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.mi.sta7.ImageLoaderHelper;
import com.mi.sta7.R;
import com.mi.sta7.bean.SingerBean;

public class SingerListAdpter extends BaseAdapter {

	private List<SingerBean> singerBeans; // 绑定的数据
	private Context context;
	private int width; // 屏幕宽度
	private ImageView singerPic; // 歌手头像
	private TextView NametextView; // 歌手名字
	private TextView number; // 歌手票数
	private LinearLayout layout; // 显示的底图
	private int max_num = 1;

	public SingerListAdpter(List<SingerBean> singerBeans, Context context, int width) {
		if(this.singerBeans!=null) this.singerBeans.clear();
		this.singerBeans = singerBeans;
		this.context = context;
		this.width = (int) (width * 0.9); // 标准宽度,以总宽度的0.9倍为基数
		for(int i=0; i<this.singerBeans.size(); i++) { // 选出最大值作为基数
			if(this.singerBeans!=null&&this.singerBeans.size()>i&&this.singerBeans.get(i)
					!=null&this.singerBeans.get(i).getSingerVotes()!=0&&this.singerBeans.get(i).getSingerVotes()>max_num) {
				max_num = this.singerBeans.get(i).getSingerVotes();
			}
		}
	}
	@Override
	public int getCount() {
		return singerBeans.size();
	}
	@Override
	public Object getItem(int position) {
		return singerBeans.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = new TextView(context);
		textView.setId(position);
		int item_width = singerBeans.get(position).getSingerVotes() * width / max_num;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(item_width, LayoutParams.FILL_PARENT);
		params.leftMargin = 2;
		params.rightMargin = 2;
		params.topMargin = 2;
		params.bottomMargin = 2;
		textView.setLayoutParams(params);
		textView.setBackgroundResource(R.drawable.singer_jindu_shap);
		View view = LayoutInflater.from(context).inflate(R.layout.show_singer_item, null);
		singerPic = (ImageView) view.findViewById(R.id.singer_img);
		layout = (LinearLayout) view.findViewById(R.id.lin);
		layout.addView(textView);
		NametextView = (TextView) view.findViewById(R.id.singer_name);
		number = (TextView) view.findViewById(R.id.piao_numer);
		number.setTag(position);
		if (singerBeans!=null && !singerBeans.isEmpty() && singerBeans.get(position).getImage()!=null) {
			ImageLoaderHelper.imageLoader.displayImage(singerBeans.get(position).getImage(), singerPic);
			NametextView.setText(singerBeans.get(position).getSingerName());
			number.setText(singerBeans.get(position).getSingerVotes()+" 票");
		}
		return view;
	}
}
