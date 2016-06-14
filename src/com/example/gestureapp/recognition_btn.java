package com.example.gestureapp;

	import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import Gesture.Gesture;
import Gesture.SensorValue;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.FsGr.GestureRecognitionManager;


import etc.SensorInformation;

	/*
	* 버튼을 이용한 테스트 백그라운드 아님
	*/
	public class recognition_btn extends Activity implements SensorEventListener {
		
		Button btnEnd;

		
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
		
		private String basicRet;
		
	
		private List<Gesture> gsList;
		
		
		
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recognition);
		
		
		tvAX = (TextView) findViewById(R.id.tvAX);
		tvAY = (TextView) findViewById(R.id.tvAY);
		tvAZ = (TextView) findViewById(R.id.tvAZ);
		
		btnEnd=(Button)findViewById(R.id.btnEnd);
		
		
		sif=new SensorInformation();
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		accSensor = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		
		 if(accSensor !=null)
	        	sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_FASTEST);
		 
		this.gsList= getAllModel();
		GRM= new GestureRecognitionManager(gsList);
	
		
		
		
		btnEnd.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View arg0) {
				finish();
			}});
		 
	
		//Toast.makeText(getBaseContext(), getFilesDir().getAbsolutePath(), Toast.LENGTH_SHORT).show();
		}
		

		
		
	
		
		
		
		@Override
		 public boolean onKeyDown(int keyCode, KeyEvent event) {
		
			PackageManager pm = getPackageManager();
		
			if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
				
			 //   Vibrator vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
			  //  vibe.vibrate(40);
				
				
				
				if(!btnCon)
				{
					inputGesture.clear();
				  Toast.makeText(getBaseContext(), "동작시작:" + inputGesture.size(), Toast.LENGTH_SHORT).show();
				  
				  btnCon=true;
				}
				
				else if(btnCon)
				{
						String result;
					
					
						
						result =GRM.getFirstRecog_NOTS(inputGesture).getName();
						
						  Intent i = pm.getLaunchIntentForPackage(result);
						  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						  startActivity(i);
						
			
						
				  //Toast.makeText(getBaseContext(), "동작종료:" + result, Toast.LENGTH_SHORT).show();
					  btnCon=false;
					  
					  
					  Toast.makeText(getBaseContext(), "동작끝(버튼):" + inputGesture.size(), Toast.LENGTH_SHORT).show();
					  Toast.makeText(getBaseContext(), "결과 : "+result, Toast.LENGTH_SHORT).show();
					  
					  
					  
				}
				
				
				
				return true;
				
				
				
			}
			
			return false;
		}
		
		
		
		
		

		@Override
	public void onSensorChanged(SensorEvent arg0) {
			// TODO Auto-generated method stub
			
			
			tvAX.setText(String.valueOf(arg0.values[0]));
			tvAY.setText(String.valueOf(arg0.values[1]));
			tvAZ.setText(String.valueOf(arg0.values[2]));
			
	switch (arg0.sensor.getType()) 
		{
		
		case Sensor.TYPE_LINEAR_ACCELERATION:
				
				if(btnCon)
				{
				sif.setAccelerometer(arg0.values);
				sif.setIsAccelerometer(true);
				inputGesture.add(new SensorValue(sif.getAccelerometer_X(), sif.getAccelerometer_Y(), sif.getAccelerometer_Z()));
				}
				else{}
				
				break;
		 }
			
		}
		
		
		@Override
		public void onResume() {
			super.onResume();
			// 가속도 센서 리스너 오브젝트를 등록
			 if(accSensor !=null)
		        	sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_FASTEST);
			 
			 
			inputGesture.clear();
		
		}

		
		@Override
		public void onPause() {
			super.onPause();
			// 센서에서 이벤트 리스너 분리
			sm.unregisterListener(this);
			inputGesture.clear();

		}
		
		
	
		
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1){}
		
		private void init() {}
		

	
	private List<Gesture> getAllModel(){ //모든 모델 
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
	
	