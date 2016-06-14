package com.FsGr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import Gesture.Gesture;
import Gesture.SensorValue;


public class GestureTrainingManager {

	
	/*
	private List<modelInfo> miList =new ArrayList<modelInfo>(); 
	private modelInfo[] miList2;
	private int[][] matrix;
	private String name;
	private File file;
	private GestureList sampleList = new GestureList();
	
	public GestureTrainingManager(String inputData) //parsing
	{
		this.name=inputData;
		this.file = new File(consts.modelPath +inputData + consts.sampleTextFileName);
		
		try
		{
		BufferedReader in = new BufferedReader(new FileReader(this.file));
	    // 실제 센서값이 있는 파일내용을 읽는다.
		String s;
		Gesture ls = new Gesture();
		
		
			if(file.isFile())
			{
				System.out.println(file.getAbsolutePath());
				
				while( (s = in.readLine()) != null){
					if(s.startsWith("--")){
						sampleList.add(ls);
						ls = new Gesture();
					}else{
						String aline[] = s.split(",");
						SensorValue sv = new SensorValue(Double.parseDouble(aline[0]),Double.parseDouble(aline[1]),Double.parseDouble(aline[2]));
						ls.add(sv);
					}
				}
				
				
			}
			
		}catch(Exception e){}
	
		createMatrix();
		makeModelInfo();
	}
	
	
	

	
	
	private void createMatrix()
	{
		int i,j=0;
		int maxIdx=sampleList.size()-1;
		matrix=new int[maxIdx+1][maxIdx+1];
		
		TimeSeries tsCurSample;
		TimeSeries tsCompSample;
		
		TimeWarpInfo temp;
		
		for(i=0; i<=maxIdx; i++)
		{
			tsCurSample=new TimeSeries(sampleList.get(i), false, false, ',',2);
			
			for(j=0; j<maxIdx; j++)
			{
				tsCompSample=new TimeSeries(sampleList.get(j), false, false, ',',2);
				
				temp= com.dtw.DTW.getWarpInfoBetween(tsCurSample, tsCompSample);
				
				matrix[i][j]=(int)temp.getDistance();
			}
		}
	 }
	
	
	private void makeModelInfo()
	{
		int i, j;
		int maxIdx=sampleList.size()-1;
		int tempSum=0;
		
		for(i=0; i<=maxIdx; i++){
			tempSum=0;
			
			for(j=0; j<=maxIdx; j++)
				tempSum=tempSum+matrix[i][j];
			
			modelInfo temp=new modelInfo(tempSum, i, sampleList.get(i));

				
				miList.add(temp);}
		
	}
	
	private modelInfo getOptimalModel(){
		 
		 int maxIdx=sampleList.size()-1;
		 int shortestDist=miList.get(0).getDist();
		 int retIdx=0;
		 
		 
		 
		 for(int i=1; i<=maxIdx; i++){
			if(shortestDist>miList.get(i).getDist()){
				shortestDist=miList.get(i).getDist();
				retIdx=i;
			}
		 }
			
		 return miList.get(retIdx);
	}
	
	
	
	
	
	public void BasicTrainingProcess()
	{
		modelInfo optimalModel=getOptimalModel();
		
		String file_path = consts.modelPath + getTargetName() + ".txt";
		
		try
		{
			File f=new File(file_path);
			StringBuffer attr = new StringBuffer();
			FileOutputStream fos = new FileOutputStream(file_path);
			
			for(SensorValue sv : optimalModel.getSeq())
				attr.append(Double.toString(sv.getX())+ "," + Double.toString(sv.getY()) + "," +Double.toString(sv.getZ())+ "\n");
				
			fos.write(attr.toString().getBytes());
			fos.close();
		}
		catch(Exception e){}
	}
	
	
	
	public int[][] getMatrix(){
		return matrix;
	}
	
	public String getTargetName(){
		return name;
	}
	
	public File getFile(){
		return file;
	}
	

	public static void SimilarGestureGen(List<Gesture> allModel, FileOutputStream fos)
	{
		
		
		  System.out.println("유사그룹생성실행");
		  
		  int maxDivLevel=3;
		
			List<Gesture> similarList_temp;
		
		
			GestureRecognitionManager grm = new GestureRecognitionManager(allModel);
			
			BufferedWriter file;
			
			for(Gesture inputData : allModel)
			{
				similarList_temp = grm.getFirstRecog_NOTS_SimilarList(inputData); // inputData 는 기존 확보해둔 샘플에서 가져온다.

				if(similarList_temp.size()>0){
					
					try{
		
							fos.write(toInput.getBytes());
						}
						
						fos.close();
						
					}	 
					  catch(Exception e){}
					
				}
			}
		
					
				try{
					
				for(String errModel : errModelList)
				{
					File f = new File(consts.similarModelPath + errModel);
						
					if(f.isFile()) 
						file = new BufferedWriter(new FileWriter(f,true));	

					else file = new BufferedWriter(new FileWriter(f));

				  	file.write(ChkModelName);
				  	file.write("/");
				  
				  	//PartBitGenerator pbg = new PartBitGenerator("ChkModelName", errModel, maxDivDepth);
				  
				  	PartBit partBit = PartBitGenerator.getPartBitBetween(ChkModelName+".txt", errModel, maxDivDepth);
				  	//PartBit partBit = PartBitGenerator(ChkModelName,)
				  
				  	for(Boolean bit : partBit)
				  	{
				  		if(bit) file.write('1');
				  		else file.write('0');
				  	}
					  	file.newLine();
					  	file.close();
					}
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}
		}
		else System.out.println("모델이 존재하지 않습니다.");
	}

	*/
	
	
	
	
	
	
	
	
	
	

}