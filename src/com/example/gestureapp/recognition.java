package com.example.gestureapp;

	import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import Gesture.Gesture;
import Gesture.SensorValue;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;
import com.FsGr.GestureRecognitionManager;
import etc.SensorInformation;
import etc.SensorObserverThread;

import test2.BCR_button;

/**
 * 서비스 순서
 * onCreate() → onStartCommand() → Service Running → onDestroy()
 * 
 * 
 * 
 *   IntentFilter intentFilter = new IntentFilter();
	intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
	getApplicationContext().registerReceiver(headSetConnectReceiver, intentFilter);
 * 
 * 
 * 
 * 
 * 
 */

	public class recognition extends Service implements SensorEventListener,  Runnable {

		
		
		double x=10;

	    private int callingCount;
	 
		
		private boolean btnCon=false; // true:인식중 			false:비인식중
		
		
		public SensorInformation sif =null;
		
			// 센서 관리자
				SensorManager sm = null;
					
			// 가속도 센서
				Sensor accSensor = null;
				
				
		TextView tvAX = null;
		TextView tvAY = null;
		TextView tvAZ = null;
				
		
				

		public static long start;
		public static long end;
		
		Gesture inputGesture = new Gesture();
		
		GestureRecognitionManager GRM;  
		
		PackageManager pm;
		
		
	
		
		public boolean isON=false;
		Gesture gesture;
		private Gesture basicRet;
		private List<Gesture> gsList;

		SensorObserverThread windowThread;
		
		static public boolean isPost20ms=false;
		
		
		
		private AudioManager mAudioManager;
        private ComponentName mRemoteControlReceiver;
		
		
		//버튼 받기용
  BroadcastReceiver btnReceiver = new BroadcastReceiver(){
	
    public static final String ScreenOff = "android.intent.action.SCREEN_OFF";
    public static final String ScreenOn = "android.intent.action.SCREEN_ON";
	//  public static final String Media = "android.intent.action.MEDIA_BUTTON";
		        
		        @Override
		        public void onReceive(Context context, Intent intent){
		        	
		        	
		        	
		      	  
		            if(intent.getAction().equals(ScreenOff))
		            { 
		            	if(!isON){
		            		 System.out.println("XXXXXXXXXXXXXXXXXXXXx:  "+isON+"  :" +gesture.size());  	 
		            	gesture.clear();	
		               //System.out.println("XXXXXXXXXXXXXXXXXXXXx:  "+isON+"  :" +gesture.size());  	  
		               isON=true; 
		            	}
		            }//센서 모으기 시작 }
		            
		            
		            else if (intent.getAction().equals(ScreenOn))
		            { 
		            	
		            	if(isON)
		            	{
		            	
   	
		                System.out.println("OOOOOOOOOOOOOOOOOOOOOO:  "+isON+"  :" +gesture.size());      
		            	
		            	basicRet=GRM.getFirstRecog_NOTS(gesture);
		            	
		            	gesture.clear();	
		            	
		            	isON=false;
		            	
		            	 pm= getPackageManager();
		            	 Intent i = pm.getLaunchIntentForPackage(basicRet.getName());
						  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						  startActivity(i);
		            	}
		       
		            	
		          
		            } //그만
		            
		            else if(intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON))
		            	 Toast.makeText(getBaseContext(), "WTFWTFWTF", 0).show();
		            
		            return;
		       
		          }
    };
		
	
		
		
		  // 서비스 생성시 1번만 실행
		  public void onCreate() {
		        super.onCreate();
		        
		        
		       // mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		        //mRemoteControlReceiver = new ComponentName(this, btnReceiver.class);
		    //    mAudioManager.registerMediaButtonEventReceiver(mRemoteControlReceiver);
				
				
				
				//버튼 브로드캐스
	            IntentFilter offFilter = new IntentFilter (Intent.ACTION_SCREEN_OFF);
	            IntentFilter onFilter = new IntentFilter (Intent.ACTION_SCREEN_ON);
	            IntentFilter mediaFilter = new IntentFilter (Intent.ACTION_MEDIA_BUTTON);
	            
	            
	            
	            mediaFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
	            
	            registerReceiver(this.btnReceiver, offFilter);
	            registerReceiver(this.btnReceiver, onFilter);
	            registerReceiver(this.btnReceiver, mediaFilter);
	            
	            
	         
				sif=new SensorInformation();
			
				
				
				gesture=new Gesture("input");
				
				
				this.gsList= getAllModel();
				
				GRM= new GestureRecognitionManager(gsList);
				
				
				
				sm = (SensorManager) getSystemService(SENSOR_SERVICE);
				accSensor = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
				
	            if(accSensor !=null)
	  	        	sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_FASTEST); //누르면 센서 등록
		    
		        Toast.makeText(getBaseContext(), "Service is Created", 0).show();
		    }
		
		
		
		
		
		

		@Override
	public void onSensorChanged(SensorEvent arg0) {
			// TODO Auto-generated method stub

			switch (arg0.sensor.getType()) 
			{
		
			case Sensor.TYPE_LINEAR_ACCELERATION:
			
			if(isON){
			sif.setAccelerometer(arg0.values);
			sif.setIsAccelerometer(true);
			gesture.add(new SensorValue(sif.getAccelerometer_X(), sif.getAccelerometer_Y(), sif.getAccelerometer_Z()));
			
			}
			else{}
			
			break;
			
		}
	 }
	
	
		
		
		
		
		// 서비스가 호출될때마다 매번 실행(onResume()과 비슷)
	    public int onStartCommand(Intent intent, int flags, int startId) {
	    	
	
	        int i = super.onStartCommand(intent, flags, startId);
	              
	        Toast.makeText(getBaseContext(), "Service is Started", 0).show();
	         
	        Toast.makeText(getBaseContext(),
	                "Called Count is :" + String.valueOf(callingCount), 0).show();
	        
	        	callingCount++;
	        	
	        //	while(true){if(isON==true) break;} //버튼 누를때까지 대기 
	        	
	        	
	        	
	         //   while(true){if(isON==false) break;} //다시 누를때까지 대기
	            
	            
	        	
	          //  System.out.println("ggggg: "+this.gesture.size());
	         
	        return Service.START_STICKY;
	    }

		
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1){}
		
		private void init() {}
		

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}





	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	  // 서비스가 종료될때 실행
    public void onDestroy() {
    	 	
        super.onDestroy();
        
        unregisterBroadcast();//전원버튼
        
    	sm.unregisterListener(this);
        
        Toast.makeText(getBaseContext(), "Service is Destroied", 0).show();
    }
    
    
    private void unregisterBroadcast() {
        unregisterReceiver(btnReceiver);
    }
    
    

    
    
    private List<Gesture> getAllModel(){
    	List<Gesture> gsList = new ArrayList<Gesture>();
    	
    	File[] list = getFilesDir().listFiles();
    	
    	String fName, modelName;
    	
    	Gesture model;
    	
    	try{
    		  FileInputStream fis=null;

    		   
    		for(File f : list)//모델 리스트
    		{
    			
    			fName=f.getName();
    			modelName= fName.substring(0, fName.length()-4);
    			
    			// 폴더가 아닌 파일이며 , txt형식의 파일만을 읽어옴.
    			if(f.isFile() && fName.substring(f.getName().length()-3).equals("txt")){
    		
    				fis=openFileInput(f.getName());

    				model = new Gesture(modelName);
    				model.setSVFromModel(fis);
    				
    				
    				gsList.add(model);
    			}
    		}

    	}catch(Exception e){}
    	
    	
    	return gsList;
    }

}
	
	
		




