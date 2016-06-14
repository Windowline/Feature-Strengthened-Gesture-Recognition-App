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


//���ӵ� ���� ��ȭ��  ������ ���� �ڵ��ν�
public class recognition_sv extends Service implements SensorEventListener,  Runnable {



    private int callingCount;
 
	
	private boolean btnCon=false; // true:�ν��� 			false:���ν���
	
	
	public SensorInformation sif =null;
	
		// ���� ������
			SensorManager sm = null;
				
		// ���ӵ� ����
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
	
	private float Thre_start_no_recog=500; //���ν���(F)�� start ����
	
	private float Thre_end_no_recog = 300;//�ν���(T)��  end����
	
	
	private SensorObserverThread windowThread;
	
	static public boolean isPost20ms=false;

	private LinkedList<Long> Q = new LinkedList<Long>(); //���⸦ ��Ƴ��� ť 5~10�� ����
	private LinkedList<SensorValue> Q_sv = new LinkedList<SensorValue>(); // ���� ������ ����
	
	
	private long digSum=0;
	private long dig;
	private long digSum_to_comp;
	private boolean is10=false;
	//private int sensorTerm=0;
	private Gesture temp;
	
	
	 BroadcastReceiver retReceiver = new BroadcastReceiver(){ //�ν� ����
			

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
				            	
								Toast.makeText(getBaseContext(), "1��: "+ret1.getName(), 0).show();
								
								SI_list = getSimilarInfo(ret1.getName());
							
								Toast.makeText(getBaseContext(), "���� �� "+SI_list.size(), 0).show();
								
								if(SI_list.size()==0){ //�׳� ����
									final_ret=ret1;
								}
								
								else{//2�� ����
									final_ret=GRM.AdvancedRecognitionProcess(ret1, gesture, SI_list);
									
									Toast.makeText(getBaseContext(), "2��: "+final_ret.getName(), 0).show();
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

	  // ���� ������ 1���� ����
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
		 	
		 
		 //�ʹݺ� �����
		 if(Q_sv.size()<15)
			 Q_sv.offerLast(new SensorValue(x,y,z));
		 
		 else if(Q_sv.size()==15){
			 Q_sv.pollFirst();
			 Q_sv.offerLast(new SensorValue(x,y,z));
			 //sensorTerm--;
		 }
		 
		 
		 digSum=digSum+(long)speed;//������ ����	  			  
		 Q.offerLast((long)speed);
			  
		 
		if(Q.size()==10){ //10�� á����--->�˻���
				
			digSum_to_comp=digSum;
			Log.i("digSum_to_comp", ""+digSum_to_comp);
			
			for(int i=0; i<4; i++)
				digSum=digSum-Q.pollFirst();
		
			 
			 /* 11 05 
			  * �����ʿ���
			  */
			 
			 if(isON==false){ //10���� á�� ���ν� ���϶� 
				 
				   if(digSum_to_comp>=22000){ //�Ӱ�ġ �ѱ� -> �νĽ������� �ѱ�
					 
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
			 
			 
			 else if(isON==true){ //10���� á�� ���� �ν����� ��
	
		
				 		if(digSum_to_comp>=13000){ //�ν���-�ν� ����
						  Log.e("Step", "SHAKE");			
						  
						  gesture.add(new SensorValue(x, y, z));
				 		}
					   
			
				 		else if(digSum_to_comp<13000){ //�ν���-DTW���� 
				 		
				    	   recog_end_time=System.currentTimeMillis();
	    	  
				    	   isON=false;
				    	   
				    	   Vibrator vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
						   vibe.vibrate(40);		  
				    	   
				    	  
				    	   		if( (recog_end_time)-(recog_start_time)> 900){ // dtw ����
				    	   			Log.e("ret", "�������");
				    		   
				    	   			//  Toast.makeText(getBaseContext(), ""+gesture.size(), 0).show();
								 
				    	   			 	gesture.addAll(0, Q_sv); //���ݺ� ����
				    	   			 	
				    	   			 //	for(int j=gesture.size()-1; j>=gesture.size()-11; j--)
				    	   			 //		gesture.remove(j);
							 	
				    	   			Toast.makeText(getBaseContext(), "���۳�(�ڵ�)"+gesture.size(), 0).show();
				    		   
				    	   			sendBroadcast(new Intent("Ret"));
				    	   		 }
				    	
				    	  
				    	   		else {//���� ���
				    	   			Log.e("ret", "�������");
				    	   			gesture.clear();
				    	   			Q.clear();
				    	   			digSum=0;
				    	   			digSum_to_comp=0;
				    	   			Q_sv.clear();
				    		 
				    	   			}//  
				    	   		
				        }//�Ӱ�ġ==F
			}//isON==T
			 
			 
			  //pre_xyz :�ٷ� �� �� ��
			  pre_x=x;
			  pre_y=y;
			  pre_z=z;
			 
			 return;
		 }// is10==T
		
		
		
		else if(Q.size()<10) { //10���� ��á���� &&    ���� �ν����϶� : ����ó �߰�   ���ν����϶� ����..			
			
			if(isON)
				gesture.add(new SensorValue(x,y,z));
			
			else {}
			
			  //pre_xyz :�ٷ� �� �� ��
			  pre_x=x;
			  pre_y=y;
			  pre_z=z;
			  
			  return;
		}
		
		
		else
			Log.i("�̷�������", "�¾�");
		
	
	 }
 }


	
	
	
	
	// ���񽺰� ȣ��ɶ����� �Ź� ����(onResume()�� ���)
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
	
	
	
	
	//model�� ������ ���� 2���񱳸� ���� ������ ������        String ����ó�̸�/��Ʈ��Ʈ   ���ٸ� null List
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
			  if(f.getName().equals("SI_"+modelName+".txt")){ //1�� ����� ���� SI�� ����� �� ã�´�
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
			
				
			for(int i=0; i<arr_si.length-1; i++){ //������ �˼� ����.,.. �̻���..
										
			  si_str_temp=arr_si[i].split(",");
					
					Log.i("i / si / si_1 / si_2 / si_size", +i+"  "+arr_si[i]+"  " +si_str_temp[0] +"  " +si_str_temp[1]+"   "+si_str_temp.length);
					
	
				
					ges_temp=new Gesture(si_str_temp[0]);
					
				
					fis=openFileInput(si_str_temp[0]+".txt");
					
					ges_temp.setSVFromModel(fis);
					
					
					sil.add(new SimilarInfo(ges_temp, si_str_temp[1]));
					
			
				}
				
		
	}catch(Exception e){}
	
	
	// Toast.makeText(getBaseContext(), "aaa"+sil.size(), 0).show();
		
		return  sil;//����. size=0
		
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
		   
		for(File f : list)//�� ����Ʈ
		{
			
			fName=f.getName();
			modelName= fName.substring(0, fName.length()-4);
			
			// ������ �ƴ� �����̸� , txt������ ���ϸ��� �о��.
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



private Gesture getModel(String modelName){ // Ư�� �� �ҷ�����
	
	Gesture model =new Gesture(modelName);
	
	File[] list = getFilesDir().listFiles();
	
	String fName;
	
	
	try{
		  FileInputStream fis=null;

		for(File f : list)//�� ����Ʈ
		{
			
			fName=f.getName();
			modelName= fName.substring(0, fName.length()-4);
			
			// ������ �ƴ� �����̸� , txt������ ���ϸ��� �о��.
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



  // ���񽺰� ����ɶ� ����
public void onDestroy() {
	 	
    super.onDestroy();
        
	sm.unregisterListener(this);
    
    Toast.makeText(getBaseContext(), "Service is Destroied", 0).show();
}



}