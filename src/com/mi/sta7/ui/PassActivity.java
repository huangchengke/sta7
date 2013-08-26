package com.mi.sta7.ui;

import com.mi.sta7.R;
import com.mi.sta7.mangerdate.activityManagers;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class PassActivity extends Activity{
	private View pView;
	private ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityManagers.addActivity(this);
	}
private void init()
{
	setContentView(R.layout.pass);
	pView=findViewById(R.id.pass_pb);
	
}
}
