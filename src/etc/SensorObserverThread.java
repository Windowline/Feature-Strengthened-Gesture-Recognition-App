package etc;



import Gesture.SensorValue;
//import com.motion.MotionRecognizer;



import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.example.gestureapp.training;
import com.example.gestureapp.recognition;

public class SensorObserverThread extends Thread{
	
	//public static ControlSocket mControlSocket			= null;
	
	public static List<SensorValue> SensorValueStream		= new ArrayList<SensorValue>(); //Raw
	public static List<SensorValue> SensorValuepost20ms		= new ArrayList<SensorValue>(); //post
	
	public static List<SensorValue> SensorValuepre20ms		= new ArrayList<SensorValue>();
	public static List<SensorValue> SensorValue50ms			= new ArrayList<SensorValue>();
	
	
	
	
	private static Handler handler = null;
	private Timer mainTimer = null;
	
	private static boolean kind; // T:트레이닝 ,  F:인식
	
	//private Boolean isAlive = true;

	/*
	public SensorObserverThread(List<SensorValue> stream, List<SensorValue> streamPost20ms){
		this.mControlSocket = GestureRecogActivity.mControlSocket;
		
		this.SensorValueStream 	= stream;	
		this.SensorValuepost20ms	= streamPost20ms;
	}
	*/
	
	public SensorObserverThread(List<SensorValue> stream, List<SensorValue> streamPost20ms
			, Handler handler, boolean kind){
		//this.mControlSocket = MainActivity.mControlSocket;
		
		this.SensorValueStream 	= stream;	
		this.SensorValuepost20ms	= streamPost20ms;
		
		this.handler= handler;
		this.kind=kind;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		mainTimer = new Timer();
		
		mainTimer.schedule(new quantize(), 0, 30); //0초후에 task를 실행하고 30초마다 반복해라.
		
	}
	
	public void kill(){
	//	mControlSocket.finalize();
		mainTimer.cancel();	
	}
	
	public  class quantize extends TimerTask {
		
		boolean isFirst = true;
	
		
		@Override
		public void run() {
			
			// 10초뒤
			Timer timer = new Timer();			  
			timer.schedule(new quantize_sub(), 10, 20); //10초후에 task를 싫행하고 20초마다 반복해라 
	  }
	}
	
	public static class quantize_sub extends TimerTask {
		
		boolean isFirst = true;		
		//boolean kind;
		
	
		
		@Override
		public void run() {			
			
			if(isFirst){
				
				if(kind)
					training.isPost20ms =true;
				
				else if(!kind)
					 recognition.isPost20ms=true;
				
				isFirst = false;
			}
			
			
			else{
				SensorValue50ms = new ArrayList<SensorValue>();
				SensorValue50ms.addAll(SensorValuepre20ms);
				SensorValuepre20ms.clear();
				
				SensorValue50ms.addAll(SensorValueStream);
				SensorValueStream.clear();
								
				
				if(kind)
					training.isPost20ms =false;
				
				else if(!kind)
					recognition.isPost20ms =false;
				
				
				
				SensorValuepre20ms.addAll(SensorValuepost20ms);
				SensorValuepost20ms.clear();
				
				
				quantize_vectors(SensorValue50ms);
				this.cancel();
			}					   
		}
		

		
		
		public void quantize_vectors(List<SensorValue> vts){
			
			SensorValue mean = null;
			
			float[] 	sum_XYZ = {0,0,0};
			float[] 	mean_XYZ = {0,0,0};
			int			nullCnt	= 0;
			
			
			if(vts.size() == 0){ //non 
				
			//	mean = new SensorValue(0,0,0);
				
				return;
				
			}else{			
				
				for(SensorValue vt : vts){
					if(vt==null){
						nullCnt++;
						continue;
					}
					sum_XYZ[0] += vt.getX();
					sum_XYZ[1] += vt.getY();
					sum_XYZ[2] += vt.getZ();
				}
				
				for(int i=0; i<nullCnt; i++){
					vts.remove(null);
				}
				
				mean_XYZ[0] = sum_XYZ[0] / vts.size();
				mean_XYZ[1] = sum_XYZ[1] / vts.size();
				mean_XYZ[2] = sum_XYZ[2] / vts.size();
				
				
				mean=new SensorValue(mean_XYZ[0], mean_XYZ[1], mean_XYZ[2]);
			
			//	if(MainActivity.gesActivity.isMuteBtnPressed){
			//		mean = new SensorValue(0, 0, 0);
			//}
			//	else{
			//		mean = new SensorValue(mean_XYZ, MainActivity.gesActivity.isCheckBtnPressed);
			//	}
			
			}	
			
			//Log.i("quan", mean.toString());
			
			//mControlSocket.send_object(mean);
			//GestureRecogActivity.mControlSocket.send_object(mean);
			
			Message retmsg = Message.obtain(handler, 0, mean);			
			handler.sendMessage(retmsg);	
			
			
			
			
			
			
			
			
			
			
			
			
			/*
			
			if(vts.size() != 0){
				
				
				for(SensorValue vt : vts){
					if(vt==null){
						nullCnt++;
						continue;
					}
					sum_XYZ[0] += vt.getX();
					sum_XYZ[1] += vt.getY();
					sum_XYZ[2] += vt.getZ();
				}
				
				for(int i=0; i<nullCnt; i++){
					vts.remove(null);
				}
				
				mean_XYZ[0] = sum_XYZ[0] / vts.size();
				mean_XYZ[1] = sum_XYZ[1] / vts.size();
				mean_XYZ[2] = sum_XYZ[2] / vts.size();
				
				
				mean=new SensorValue(mean_XYZ[0], mean_XYZ[1], mean_XYZ[2]);
			//	
			//	if(MainActivity.gesActivity.isMuteBtnPressed){
			//		mean = new SensorValue(0, 0, 0);
			//	}
			//	else{
			//		mean = new SensorValue(mean_XYZ, MainActivity.gesActivity.isCheckBtnPressed);
			//	}
			
			}	
			
			//Log.i("quan", mean.toString());
			
			//mControlSocket.send_object(mean);
			//GestureRecogActivity.mControlSocket.send_object(mean);
			
			Message retmsg = Message.obtain(handler, 0, mean);			
			handler.sendMessage(retmsg);	
			
			*/
			
		}
		
	
	}
	
}

