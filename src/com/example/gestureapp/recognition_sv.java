package com.example.gestureapp;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Gesture.Gesture;
import Gesture.SensorValue;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.FsGr.GestureRecognitionManager;
import com.FsGr.SimilarInfo;


import etc.SensorInformation;
import etc.SensorObserverThread;


//가속도 센서 변화폭  감지에 의한 자동인식
public class recognition_sv extends Service implements SensorEventListener,  Runnable {



    private int callingCount;
 
	
	private boolean btnCon=false; // true:인식중 			false:비인식중
	
	
	public SensorInformation sif =null;
	
		// 센서 관리자
			SensorManager sm = null;
				
		// 가속도 센서
			Sensor accSensor = null;
			

	private	long recog_start_time=0;
	private long recog_end_time;
	
	private GestureRecognitionManager GRM;  
	
	private PackageManager pm;
	
	
	private Gesture gesture;
	private String basicRet;
	private List<Gesture> gsList;
	private long preTime;
	private long curTime;
	
	private float x,y,z;
	private float pre_x, pre_y, pre_z;
	private float speed;
	
	private boolean isON=false; // T: START    F: END
	
	private float Thre_start_no_recog=500; //비인식중(F)에 start 기준
	
	private float Thre_end_no_recog = 300;//인식중(T)에  end기준
	
	
	private SensorObserverThread windowThread;
	
	static public boolean isPost20ms=false;

	private LinkedList<Long> Q = new LinkedList<Long>(); //기울기를 모아놓는 큐 5~10개 유지
	private LinkedList<SensorValue> Q_sv = new LinkedList<SensorValue>(); // 이전 데이터 유지
	
	
	private long digSum=0;
	private long dig;
	private long digSum_to_comp;
	private boolean is10=false;
	//private int sensorTerm=0;
	private Gesture temp;
	
	
	 BroadcastReceiver retReceiver = new BroadcastReceiver(){ //인식 실행
			

		 				//List<String[]>SI_list;
		 				
		 				Gesture ret1, final_ret;
		 				List<SimilarInfo> SI_list;
		 
				        @Override
				        public void onReceive(Context context, Intent intent){
				        	
				        	if(intent.getAction().equals("Ret"))
				            { 
				            	Log.e("vvvvvvvv:", "ooooooooo");
				            	

								//result1 =GRM.getFirstRecog_NOTS(gesture);
				            	ret1=GRM.getFirstRecog_NOTS(gesture);
				            	
				            	if(ret1==null) return;
				            	
								Toast.makeText(getBaseContext(), "1차: "+ret1.getName(), 0).show();
								
								SI_list = getSimilarInfo(ret1.getName());
							
								Toast.makeText(getBaseContext(), "유사 수 "+SI_list.size(), 0).show();
								
								if(SI_list.size()==0){ //그냥 실행
									final_ret=ret1;
								}
								
								else{//2차 판정
									final_ret=GRM.AdvancedRecognitionProcess(ret1, gesture, SI_list);
									
									Toast.makeText(getBaseContext(), "2차: "+final_ret.getName(), 0).show();
								}
								
								gesture.clear();
								 Q.clear();
								 digSum=0;
								 digSum_to_comp=0;
					    	 	  Q_sv.clear();
					    		  //sensorTerm=0;
				
								pm= getPackageManager();
								Intent i = pm.getLaunchIntentForPackage(final_ret.getName());
								i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								 startActivity(i);
								}		
				            }
				       
				          };

	  // 서비스 생성시 1번만 실행
	  public void onCreate() {
	        super.onCreate();
	
             
			sif=new SensorInformation();
		
			
			IntentFilter Filter = new IntentFilter("Ret");
			Filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
			registerReceiver(this.retReceiver, Filter);
			
			
			
			gesture=new Gesture("input");
			
			this.gsList= getAllModel();
			
			GRM= new GestureRecognitionManager(this.gsList);
			
			sm = (SensorManager) getSystemService(SENSOR_SERVICE);
			accSensor = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
			
            if(accSensor !=null)
  	        	sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_FASTEST);
	    
	        Toast.makeText(getBaseContext(), "Service is Created", 0).show();
	    }
	
	
	
	
	
	

	@Override
public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub

	if(arg0.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){
			
		  sif.setAccelerometer(arg0.values);
		  sif.setIsAccelerometer(true);
		  
		  x=sif.getAccelerometer_X();	y=sif.getAccelerometer_Y();		z=sif.getAccelerometer_Z();
			 
		 speed = Math.abs( (Math.abs(x) + Math.abs(y) + Math.abs(z)) - ( Math.abs(pre_x) - Math.abs(pre_y) - Math.abs(pre_z)) ) / 17 *10000;
		 	
		 
		 //초반부 보충용
		 if(Q_sv.size()<15)
			 Q_sv.offerLast(new SensorValue(x,y,z));
		 
		 else if(Q_sv.size()==15){
			 Q_sv.pollFirst();
			 Q_sv.offerLast(new SensorValue(x,y,z));
			 //sensorTerm--;
		 }
		 
		 
		 digSum=digSum+(long)speed;//기울기합 갱신	  			  
		 Q.offerLast((long)speed);
			  
		 
		if(Q.size()==10){ //10개 찼을때--->검사함
				
			digSum_to_comp=digSum;
			Log.i("digSum_to_comp", ""+digSum_to_comp);
			
			for(int i=0; i<4; i++)
				digSum=digSum-Q.pollFirst();
		
			 
			 /* 11 05 
			  * 수정필요함
			  */
			 
			 if(isON==false){ //10개가 찼고 비인식 중일때 
				 
				   if(digSum_to_comp>=22000){ //임계치 넘김 -> 인식시작으로 넘김
					 
					  Vibrator vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
					   vibe.vibrate(40);		  
					  Log.e("Step", "SHAKE");
 
					  recog_start_time=System.currentTimeMillis();
					  
					  gesture.add(new SensorValue(x, y, z));
					  
					  isON=true;
					  		  
				  }
				  
				   else if(digSum_to_comp<22000){  					    	  
			      }
		     } 
			 
			 
			 else if(isON==true){ //10개가 찼고 현재 인식중일 때
	
		
				 		if(digSum_to_comp>=13000){ //인식중-인식 유지
						  Log.e("Step", "SHAKE");			
						  
						  gesture.add(new SensorValue(x, y, z));
				 		}
					   
			
				 		else if(digSum_to_comp<13000){ //인식중-DTW시작 
				 		
				    	   recog_end_time=System.currentTimeMillis();
	    	  
				    	   isON=false;
				    	   
				    	   Vibrator vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
						   vibe.vibrate(40);		  
				    	   
				    	  
				    	   		if( (recog_end_time)-(recog_start_time)> 900){ // dtw 연산
				    	   			Log.e("ret", "연산시작");
				    		   
				    	   			//  Toast.makeText(getBaseContext(), ""+gesture.size(), 0).show();
								 
				    	   			 	gesture.addAll(0, Q_sv); //전반부 보충
				    	   			 	
				    	   			 //	for(int j=gesture.size()-1; j>=gesture.size()-11; j--)
				    	   			 //		gesture.remove(j);
							 	
				    	   			Toast.makeText(getBaseContext(), "동작끝(자동)"+gesture.size(), 0).show();
				    		   
				    	   			sendBroadcast(new Intent("Ret"));
				    	   		 }
				    	
				    	  
				    	   		else {//연산 취소
				    	   			Log.e("ret", "연산취소");
				    	   			gesture.clear();
				    	   			Q.clear();
				    	   			digSum=0;
				    	   			digSum_to_comp=0;
				    	   			Q_sv.clear();
				    		 
				    	   			}//  
				    	   		
				        }//임계치==F
			}//isON==T
			 
			 
			  //pre_xyz :바로 전 텀 꺼
			  pre_x=x;
			  pre_y=y;
			  pre_z=z;
			 
			 return;
		 }// is10==T
		
		
		
		else if(Q.size()<10) { //10개가 안찼을때 &&    현재 인식중일때 : 제스처 추가   비인식중일때 안함..			
			
			if(isON)
				gesture.add(new SensorValue(x,y,z));
			
			else {}
			
			  //pre_xyz :바로 전 텀 꺼
			  pre_x=x;
			  pre_y=y;
			  pre_z=z;
			  
			  return;
		}
		
		
		else
			Log.i("이럴순없어", "맞아");
		
	
	 }
 }


	
	
	
	
	// 서비스가 호출될때마다 매번 실행(onResume()과 비슷)
    public int onStartCommand(Intent intent, int flags, int startId) {
    	

        int i = super.onStartCommand(intent, flags, startId);
              
        Toast.makeText(getBaseContext(), "Service is Started", 0).show();
         
        Toast.makeText(getBaseContext(),
                "Called Count is :" + String.valueOf(callingCount), 0).show();
        
        	callingCount++;
        	
        	
        	gesture.clear();
			 Q.clear();
			 digSum=0;
			 digSum_to_comp=0;
			 Q_sv.clear();
        		
        	
        	 if(accSensor !=null)
   	        	sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_FASTEST);
        	
        return Service.START_NOT_STICKY;
    }

	
    
    
   
    
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1){}
	
	private void init() {}
	
	
	
	
	//model과 유사한 모델의 2차비교를 위한 정보를 가져옴        String 제스처이름/파트비트   없다면 null List
	public List<SimilarInfo> getSimilarInfo(String modelName){
		
		List<SimilarInfo> sil = new ArrayList<SimilarInfo>();
		SimilarInfo si_temp;
		Gesture ges_temp;
		String[] si_str_temp;
	//	List<String[]> sil = new ArrayList<String[]>();
		
		
		byte[] data = new byte[1024*3];
		
		FileInputStream fis=null;
		
		String si_bunch;
		String arr_si[];
		
	try{
				
		File[] list = getFilesDir().listFiles();
			
		for(File f : list){
			  if(f.getName().equals("SI_"+modelName+".txt")){ //1차 결과에 대한 SI가 저장된 곳 찾는다
				fis=openFileInput(f.getName());
				fis.read(data);
				
				Log.i("which file: ", f.getName());
				break;
			  }
		}
		
			si_bunch = new String(data);

			Log.i("si_bunch: ", si_bunch);
				
			arr_si = si_bunch.split("/");
			
			Log.i("arr_si_size: ", ""+arr_si.length);
			
				
			for(int i=0; i<arr_si.length-1; i++){ //마지막 알수 없는.,.. 이상함..
										
			  si_str_temp=arr_si[i].split(",");
					
					Log.i("i / si / si_1 / si_2 / si_size", +i+"  "+arr_si[i]+"  " +si_str_temp[0] +"  " +si_str_temp[1]+"   "+si_str_temp.length);
					
	
				
					ges_temp=new Gesture(si_str_temp[0]);
					
				
					fis=openFileInput(si_str_temp[0]+".txt");
					
					ges_temp.setSVFromModel(fis);
					
					
					sil.add(new SimilarInfo(ges_temp, si_str_temp[1]));
					
			
				}
				
		
	}catch(Exception e){}
	
	
	// Toast.makeText(getBaseContext(), "aaa"+sil.size(), 0).show();
		
		return  sil;//없음. size=0
		
	}
	
public List<SimilarInfo> getSimilarInfo2(String modelName){
	
	List<SimilarInfo> siList = new ArrayList<SimilarInfo> ();	
	
	return siList;
	
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
			if(f.isFile() && fName.substring(f.getName().length()-3).equals("txt") && !fName.substring(0,3).equals("SI_")){
		
				fis=openFileInput(fName);

				model = new Gesture(modelName);
				model.setSVFromModel(fis);
				
				
				gsList.add(model);
			}
		}

	}catch(Exception e){}
	
	
	return gsList;
}



private Gesture getModel(String modelName){ // 특정 모델 불러오기
	
	Gesture model =new Gesture(modelName);
	
	File[] list = getFilesDir().listFiles();
	
	String fName;
	
	
	try{
		  FileInputStream fis=null;

		for(File f : list)//모델 리스트
		{
			
			fName=f.getName();
			modelName= fName.substring(0, fName.length()-4);
			
			// 폴더가 아닌 파일이며 , txt형식의 파일만을 읽어옴.
			if( f.isFile() && fName.equals(modelName) ){
		
				fis=openFileInput(f.getName());
				model.setSVFromModel(fis);
				
				return model;
			}		
		}
		
	   }catch(Exception e){}

	   return model;
	
}




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
        
	sm.unregisterListener(this);
    
    Toast.makeText(getBaseContext(), "Service is Destroied", 0).show();
}



}