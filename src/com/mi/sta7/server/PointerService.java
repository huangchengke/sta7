package com.mi.sta7.server;
import com.mi.sta7.ui.CenterActivity2;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class PointerService {

	private static float degrees=-135;
	public static boolean isStart;
	private Tasks tasks;
	public  void start()
	{
		isStart=true;
		tasks=new Tasks();
		new Thread(tasks).start();
	}
	public static void stop()
	{
		isStart=false;
	}
	
	class Tasks  implements Runnable
	{
        
		@Override
		public void run() {
		
			Message message = Message.obtain();
			if (CenterActivity2.getInstance().getDegrees()>degrees) {
				CenterActivity2.getInstance().setDegrees(CenterActivity2.getInstance().getDegrees() - 0.5f);
			} else if (CenterActivity2.getInstance().getDegrees()<degrees) {
				CenterActivity2.getInstance().setDegrees(CenterActivity2.getInstance().getDegrees() + 0.5f);
			}
			else {
		          isStart=false;     
			}
			handler.sendMessage(message);
			
		}
		
	}
	 Handler handler = new Handler(){
		@Override
		public void handleMessage(Message message) {
			CenterActivity2.getInstance().refresh();
			if (isStart) {
				handler.postDelayed(tasks, 10);
			}
		
		}
	};
}
