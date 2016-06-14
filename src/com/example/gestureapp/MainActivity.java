package com.example.gestureapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import test.AppList;
import Gesture.Gesture;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.FsGr.GestureRecognitionManager;
import com.FsGr.PartBit;
import com.FsGr.PartBitGenerator;


public class MainActivity extends Activity { // ���� UI

	Button btnT, btnS, btnD, btnPC, btnFinishApp, btnFinishRecog, btnRecog_key, btnRecog_auto;
	
	Button btnPBdel, btnMG;
	
	Intent intent;
	
	int exe_code=0; //1��Ƽ��Ƽ,  2����
	
	boolean isService=false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnT = (Button)findViewById(R.id.training);
		btnS = (Button)findViewById(R.id.simi);
		btnD = (Button)findViewById(R.id.deleteAll);
		btnPC = (Button)findViewById(R.id.pc);
		btnFinishApp= (Button)findViewById(R.id.finish_app);
		btnFinishRecog = (Button)findViewById(R.id.finish_recog);
		btnRecog_key = (Button)findViewById(R.id.recog_key);
		btnRecog_auto= (Button)findViewById(R.id.recog_auto);
	
		btnPBdel= (Button)findViewById(R.id.pbdel);
		btnMG = (Button)findViewById(R.id.mg);
		
		//Toast.makeText(getBaseContext(), "OnCreate.", Toast.LENGTH_SHORT).show();
		
		
		
		btnT.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				intent = new Intent(MainActivity.this, AppList.class);
				
				exe_code=1;
				
				intent.putExtra("T_TYPE", "no");
				
				startActivity(intent);
			}
		});
	

		

//���� �� ���� ����
btnS.setOnClickListener(new View.OnClickListener() { //Ư¡ ���� ������ ����
	public void onClick(View v) {
		
		Toast.makeText(getBaseContext(), "��Ʈ��Ʈ ����222", Toast.LENGTH_SHORT).show();
		
		//similarGen();
		 similarGen2();
		}
	});
	
	
	
	
	btnD.setOnClickListener(new View.OnClickListener() { // ��� ���� ����
		public void onClick(View v) {
			
			int deleteNum=deleteAllModel();
			
			Toast.makeText(getBaseContext(), "������: "+deleteNum, Toast.LENGTH_SHORT).show();
			
		}
	});
	
	
	
	
	btnPC.setOnClickListener(new View.OnClickListener() { //��Ʈ��Ʈ Ȯ��
		
		public void onClick(View v) {
			
			FileInputStream fis;
			
			int cnt=0;
			byte[] data = new byte[512];
			
			try{
				File[] list=getFilesDir().listFiles();
				
				for(File f : list){//�� ����
					
					if(f.isFile() && f.getName().substring(0, 3).equals("SI_")){
						
						cnt++;
						//Toast.makeText(getBaseContext(), ""+f.getName(), Toast.LENGTH_SHORT).show();
						//FileInputStream fis=new FileInputStream
						fis=openFileInput(f.getName());
						fis.read(data);
						
						Toast.makeText(getBaseContext(), f.getName()+": "+data, Toast.LENGTH_SHORT).show();
					}
				}
					
			}catch(Exception e){}
			
			Toast.makeText(getBaseContext(), "cnt: "+cnt, Toast.LENGTH_SHORT).show();
			
		}
	});
	
	
	
	

	
	
btnFinishRecog.setOnClickListener(new View.OnClickListener() { //���� ����
		
		public void onClick(View v) {
			//if(exe_code==2)
			
			  //if(isService){
				  
				//  isService=false;
				  
				stopService(intent);
								
			 // }
		}
	});

	
	
btnFinishApp.setOnClickListener(new View.OnClickListener() {// �� ��������
	
	public void onClick(View v) {
		
		//if(isService){
		//	isService=false;
		stopService(intent);
		//}
		
		moveTaskToBack(true);
	      finish();
	      android.os.Process.killProcess(android.os.Process.myPid());
		
	}
});




btnRecog_key.setOnClickListener(new View.OnClickListener() {// �� ��������
	
	public void onClick(View v) {
		
		intent = new Intent(MainActivity.this, recognition_btn.class);
		
		exe_code=1;
		
		startActivity(intent);

		
	}
});

	

	

btnRecog_auto.setOnClickListener(new View.OnClickListener() { //�ڵ� �ν� ���
	
	public void onClick(View v) {
		
		intent = new Intent(MainActivity.this, recognition_sv.class);
		
		exe_code=1;
		
		startService(intent);

		
	}
});





btnPBdel.setOnClickListener(new View.OnClickListener() { //Ư¡ ���� �����͸� ����
	
	public void onClick(View v) {
		
		FileInputStream fis;
		
		int cnt=0;
		byte[] data = new byte[512];
		
		try{
			File[] list=getFilesDir().listFiles();
			
			for(File f : list){//�� ����
				
				if(f.isFile() && f.getName().substring(0, 3).equals("SI_")){
					deleteFile(f.getName()); 
					cnt++;
				}
				
			}
				
		}catch(Exception e){}
		
	
		Toast.makeText(getBaseContext(), "������ : "+cnt, Toast.LENGTH_SHORT).show();
		
}});






btnMG.setOnClickListener(new View.OnClickListener() { // �߾� ���� ���ϴ� ���
	
	public void onClick(View v) {
		
		intent = new Intent(MainActivity.this, AppList.class);
		
		exe_code=1;
		
		intent.putExtra("T_TYPE", "yes");
		
		startActivity(intent);
		
	}
});






	
	
}// OnCreate	
	
	
	

	
	@Override
	public void onPause() {
		super.onPause();
		
		//if(exe_code==1)
			//stopActivity(intent);
		
	//	else if(exe_code==2)
		//	stopService(intent);

	}
	
	
	@Override
	public void onResume() {
		super.onResume();
	
	}
	
	
	

	
	private int deleteAllModel(){
		
		int i=0;
		
		try{
			File[] list=getFilesDir().listFiles();
			
			for(File f : list)//�� ����Ʈ
				if(f.isFile() && f.getName().substring(f.getName().length()-3).equals("txt"))
				{	deleteFile(f.getName());   i++;  }
				
		}catch(Exception e){}
		
		return i;
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
			
					fis=openFileInput(f.getName());

					model = new Gesture(modelName);
					model.setSVFromModel(fis);
					
					
					gsList.add(model);
				}
				
			}

		}catch(Exception e){}
		
		
		return gsList;
	}
	
	
	
	
	public void similarGen(){
		
		List<Gesture> allModel = getAllModel();
		
	    System.out.println("����׷��������");
			  
		int maxDivDepth=3;
			
		List<Gesture> similarList_temp;
			
			
		GestureRecognitionManager grm = new GestureRecognitionManager(allModel);
				
		Toast.makeText(getBaseContext(), "����:"+allModel.size(), Toast.LENGTH_SHORT).show();
		
		
		
		try{
			
			int i=0;
			
		for(Gesture inputData : allModel){
			
			i++;
			
		 similarList_temp = grm.getFirstRecog_NOTS_SimilarList(inputData); // inputData �� ���� Ȯ���ص� ���ÿ��� �����´�.
	
		 
			if(similarList_temp.size()>0){
				
				
				//Toast.makeText(getBaseContext(), "ggggggggg", Toast.LENGTH_SHORT).show();
				
			

				FileOutputStream fos = openFileOutput("SI_"+inputData.getName()+".txt", Context.MODE_WORLD_READABLE);
			
				for(Gesture sg : similarList_temp){
					
					//fos.write( (sg.getName()+",").getBytes() );
					
					fos.write( (sg.getName()+",").getBytes() );
					
					Log.i("sg_getName()", sg.getName());
					
					
					PartBit partBit = PartBitGenerator.getPartBitBetween(inputData, sg, maxDivDepth);
					
		
					
					for(Boolean bit : partBit)
				  	{
						Log.i("bit: ", ""+bit);
						
				  		if(bit) fos.write('1');
				  		
				  		else fos.write('0');
				  	}
					  	fos.write("/".getBytes());
				 
				}
				
				  fos.close();
			}
			
			
			else
				Toast.makeText(getBaseContext(), "��ùз�", Toast.LENGTH_SHORT).show();
			
		}
		
		Toast.makeText(getBaseContext(), "..............."+i, Toast.LENGTH_SHORT).show();
		
		
	 }catch(Exception e){}

}
	
	
	
	
	
public void similarGen2(){ // �������
	
	List<Gesture> allModel = getAllModel();
	
	  System.out.println("����׷��������");
	  
	  int maxDivDepth=3;
	
		List<Gesture> similarList_temp;
	
	
		GestureRecognitionManager grm = new GestureRecognitionManager(allModel);
		
		
		
		for(Gesture inputData : allModel)
		{
			similarList_temp = grm.getFirstRecog_NOTS_SimilarList(inputData); // inputData �� ���� Ȯ���ص� ���ÿ��� �����´�.
			
			FileOutputStream fos; 
			
			if(similarList_temp.size()>0){				
				try{
						
						for(Gesture simi : similarList_temp){
							
							 fos = openFileOutput("SI_"+simi.getName()+".txt", MODE_APPEND); //���� �������� �Ͱ� ������ ����Ʈ���� ���ϻ���
							 fos.write( (inputData.getName()+",").getBytes());  //���� �������ΰ� �̸� �Է�
							
							 PartBit partBit = PartBitGenerator.getPartBitBetween(inputData, simi, maxDivDepth);
							
							
							 for(Boolean bit : partBit)
						  	 {
							 	Log.i("bit: ", ""+bit);
								
						  		if(bit) fos.write('1');
						  		
						  		else fos.write('0');
						  	 }
						    fos.write("/".getBytes());
							  	
						   
						    fos.close();
						 }	
				}	 
				  catch(Exception e){}
			}
         }
		
}



}
