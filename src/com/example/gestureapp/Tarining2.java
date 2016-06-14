package com.example.gestureapp;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import Gesture.Gesture;
import Gesture.GestureList;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import etc.SensorInformation;

public class Tarining2 extends Activity implements SensorEventListener { 
	
	Button btnControl, btnClear, btnAppSelect, btnCompl;
	EditText editName;
	
	TextView tvAX = null;
	TextView tvAY = null;
	TextView tvAZ = null;
	
	
	
	public SensorInformation sif =null;
	
	Gesture gesture;
	GestureList gestureList;
	Gesture gesture_to_save;
	
	int numOfGes;
	
	boolean isOn=false;
	
	// 센서 관리자
		SensorManager sm = null;
			
	// 가속도 센서
		Sensor accSensor = null;
		

		public static long start;
		public static long end;
		
		static public boolean isPost20ms=false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.training);
		
		btnControl=(Button)findViewById(R.id.btnControl);
		btnClear = (Button)findViewById(R.id.btnClear);
		btnAppSelect = (Button)findViewById(R.id.btnAppSelect);
		btnCompl= (Button)findViewById(R.id.btnComplete);
		
		
		editName= (EditText)findViewById(R.id.motionNm);
		
		tvAX = (TextView) findViewById(R.id.tvAX);
		tvAY = (TextView) findViewById(R.id.tvAY);
		tvAZ = (TextView) findViewById(R.id.tvAZ);
		
	
		
		
		sif=new SensorInformation();
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		accSensor = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);


		
		
		
		//gesture=new Gesture();
		
		
		
		Intent intent = getIntent();
		String appName = intent.getExtras().getString("appName");
		Toast.makeText(getBaseContext(), appName, Toast.LENGTH_SHORT).show();
		
		editName.setText(appName);
		
		
		numOfGes=0;
		
		
		btnControl.setOnClickListener(new OnClickListener(){ //트레이닝버튼
			
			
			@Override
			public void onClick(View arg0) {
				
	
				long time;
				
				if(isOn){ //두번째
					
					gesture.setName(editName.getText().toString());
					
					gestureList.add(gesture);
			
					
					isOn = false;
				}
				else if(!isOn){ //처음
							
					gesture=new Gesture();
					
					//gesture.clear();
		
					isOn=true;
				
					Toast.makeText(getBaseContext(), gesture.size()+" ", Toast.LENGTH_SHORT).show();
					
				
				}
			
		}}
		);
		
		

		btnAppSelect.setOnClickListener(new OnClickListener(){
			

			@Override
			public void onClick(View arg0) {
				finish();
		}});
	

	
		
		btnClear.setOnClickListener(new OnClickListener(){
		
			@Override
			public void onClick(View arg0) {
				finish();
			}});
		
		
		
	
		
	btnCompl.setOnClickListener(new OnClickListener(){ //완료 버튼 행렬로 표준값 저장해야함
			
			
			@Override
			public void onClick(View arg0) {
				
				gesture_to_save=gestureList.getMedian();
				
				
				try{
					
					FileOutputStream fos = openFileOutput(gesture_to_save.getName()+".txt", Context.MODE_WORLD_READABLE);
					
					int size1=gesture.saveAsModel(fos);
				
					Toast.makeText(getBaseContext(), gesture.size()+" / "+size1, Toast.LENGTH_SHORT).show();
	
					}
					
					catch(Exception e){}
					
				
				
			}
			
		});
		
		
	
		
	
	}
	
	
	
	
	
	
	
	
	@Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		PackageManager pm = getPackageManager();
	
		if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
			
			if(!isOn)
			{
				
				//gesture.clear();
				
				gesture=new Gesture();
			
				isOn=true;
				//start=System.currentTimeMillis();
				Toast.makeText(getBaseContext(), gesture.size()+" ", Toast.LENGTH_SHORT).show();
			}
			
			
			else if(isOn)
			{
				
				gesture.setName(editName.getText().toString());
				gestureList.add(gesture);
				
				
				
				/*
				try{
					
				FileOutputStream fos = openFileOutput(gesture.getName()+".txt", Context.MODE_WORLD_READABLE);
				
				int size1=gesture.saveAsModel(fos);
				
				FileInputStream fis = openFileInput(gesture.getName()+".txt");
			
				Toast.makeText(getBaseContext(), gesture.size()+" / "+size1, Toast.LENGTH_SHORT).show();

				}
				
				catch(Exception e){}
				
				
			//	GestureInfo gi = new GestureInfo();
				
				*/
				
				isOn = false;
				  
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
		
		switch (arg0.sensor.getType()) {
		
			case Sensor.TYPE_LINEAR_ACCELERATION:
				
				if(isOn){
				sif.setAccelerometer(arg0.values);
				sif.setIsAccelerometer(true);
				gesture.add(new SensorValue(sif.getAccelerometer_X(), sif.getAccelerometer_Y(), sif.getAccelerometer_Z()));
				
				}
				else{}
				
				break;
		}
	}
	
	

	
	
	
	

	
	
	
	

	
	
	
	@Override
	public void onResume() {
		super.onResume();
		// 가속도 센서 리스너 오브젝트를 등록
		sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_FASTEST);
		
		gesture.clear();
		//svList_raw.clear();
		
		//windowThread.kill();
	}

	
	@Override
	public void onPause() {
		super.onPause();
		// 센서에서 이벤트 리스너 분리
		sm.unregisterListener(this);
		
		gesture.clear();

	//	svList_raw.clear();
		
		//sm.unregisterListener(this);
		//windowThread.kill();
	}
	
	
	public void onDistroy() {
		super.onDestroy();
		sm.unregisterListener(this);
	}

	
	
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1){}
	
	private void init() {}

}
