package com.FsGr;



import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import Gesture.Gesture;
import Gesture.GestureList;
import android.util.Log;
import android.widget.Toast;





public class GestureRecognitionManager { // 인식 매니저
	

	private String modelFolderLoc = "";
	private FileInputStream fis;
	

	private List<Gesture> gsList;
		
	
	
	public GestureRecognitionManager(String modelFolderLoc) 
	{
		this.modelFolderLoc = modelFolderLoc; 
	}
	
	
	//public GestureRecognitionManager(List<TimeSeries> tsList){
	//	this.tsList=tsList;
	//}
	
	
	public GestureRecognitionManager(List<Gesture> gsList){
		this.gsList=gsList;
	}
	
	
	
	
	
	//타임시리즈 안씀
	public Gesture getFirstRecog_NOTS(Gesture inputData){ // 1차 판별
		
		
		int resultIdx = 0;
		double initDist = 10000;
		double shortestDist = Double.MAX_VALUE;
		
		
		Log.i("input size: ", ""+inputData.size());
		
		for(int i = 0 ; i < this.gsList.size() ; i++){
			
			Log.i("gs size: ", ""+this.gsList.size());
			
			double dist = com.dtw.DTW.DynamicTimeWarp(this.gsList.get(i), inputData);
		
			//double dist=5;
			
		//	Log.i("dtw: ",  this.gsList.get(i).getName() + " / " +dist);


			if(shortestDist > dist){
	
					shortestDist = dist;
					resultIdx = i;
				
			}
		}
		
		Log.i("DTW1_VALUE", ""+shortestDist);
		
		if(shortestDist>1500) return null;
		
		
		
		return this.gsList.get(resultIdx);
		//return "a";
	}
		

	











//타임시리즈 안씀
public List<Gesture> getFirstRecog_NOTS_SimilarList(Gesture inputData){ //유사 동작 가려냄
	
	int resultIdx = 0;
//	double initDist = 10000;
	double shortestDist = Double.MAX_VALUE;
	
	double simiConst=1; //유사 계수
	double abs_cha;
	
	List<Gesture> similarList = new ArrayList<Gesture> ();
	List<Double> distList = new ArrayList<Double>();
	
	
	for(int i = 0 ; i < this.gsList.size() ; i++){
		double dist = com.dtw.DTW.DynamicTimeWarp(this.gsList.get(i), inputData);
		
		distList.add(dist);
		
		Log.i("dtw: ",  this.gsList.get(i).getName() + " / " +dist);


		if(shortestDist > dist){

				shortestDist = dist;
				resultIdx = i;
			
		}
	}
	
	
	for(int i=0; i<distList.size(); i++){
		
		
		if(i!=resultIdx && distList.get(i)*0.85 < shortestDist	)
			similarList.add(this.gsList.get(i));
		
	}
	
	
	return similarList;
	
}
	


		


	
	
	
public Gesture AdvancedRecognitionProcess(Gesture ret1, Gesture target, List<SimilarInfo> similarInfo ){
		
		System.out.println("*******************************2차 비교 실행됨*******************************");
		
		PartBit unionedPartBit;
		
		int maxDivDepth=3; //최대 분할 깊이
		
		List<SimilarInfo> amList=similarInfo; // SI리스트    [0]:결과의 유사모델이름 , [1]대응하는 파트비트
		
		GestureList amList_SV = new GestureList ();  	//유사모델 리스트 
		PartBitList amList_PartBit = new PartBitList ();
		
		for(SimilarInfo am : amList){
			amList_SV.add(am.getGesture());
			
			PartBit temp = new PartBit();
			
			Log.i("pb: ", am.getPartBit());
			
			for(char bit : am.getPartBit().toCharArray()){
				
				Log.i("bit: ", ""+bit);
				
				if(bit=='1')
					temp.add(false);
				
				else if(bit=='0')
					temp.add(false);
				
			}
			
			amList_PartBit.add(temp); //비트 XOR을 위해 실제 파트비트 리스트 불러옴 
		}

			unionedPartBit = amList_PartBit.getUnion();  //비트 XOR
			
			System.out.println("================Unioned Part Bit(2차비교 :" + ret1.getName() + ")================");
			
			for(Boolean bit : unionedPartBit){
				
				if(bit)
				System.out.print("1  ");
				
				else
				System.out.print("0  ");
			}
			
			Log.i("pppppppbbbbbbbbbbbbbbbbbbbb", "pb:" + unionedPartBit.size());
			
			//Toast.makeText(getBaseContext(), "unionPB"+unionedPartBit.size(), 0).show();
			
			System.out.println("\n==============================================================");
			
			GestureList targetll = target.getFinalSeqList(unionedPartBit, maxDivDepth); //테스트(입력) 데이터에 관해 1부분만 추출
			GestureList retll = ret1.getFinalSeqList(unionedPartBit, maxDivDepth); //결과 데이터에 대해 1부분만 추출
			
			List<GestureList> amlll = new ArrayList<GestureList> (); 
			
			 for(Gesture am : amList_SV)		 
			 amlll.add(am.getFinalSeqList(unionedPartBit, maxDivDepth)); //유사 제스처들에 대해 1부분만 추출
			
	
				
			 double sum_ret=0; // ret - target간 분할된시퀀스의  dtw 합
			 List<Double> sum_am_list = new ArrayList<Double>(); 
				
			for(int j=0; j<amList.size(); j++)
				sum_am_list.add(0.0); //유사동작들(j~) - target간 불할 시퀀스의 dtw 합 
			
			
			
			double dtwVal_vs_ret; //ret - target간 분할된시퀀스의  dtw
			List<Double> dtwVal_vs_amList = new ArrayList<Double>(); //유사 동작 - target간 분할된시퀀스의  dtw
			
			for(int i=0; i<targetll.size(); i++){ // 파트비트가 불연속인 만큼 루프
				
			
				dtwVal_vs_ret= com.dtw.DTW.DynamicTimeWarp(retll.get(i), targetll.get(i)); // i번째 파트 값 dtw
				sum_ret=sum_ret + dtwVal_vs_ret;
								
			
			//	for(GestureList amll :amlll)//시밀러 갯수만큼 루프
			//		dtwVal_vs_amList.add(com.dtw.DTW.DynamicTimeWarp(amll.get(i), targetll.get(i))); //시밀러 하나 당 i번째 파트 dtw
				
				
				//amlll 행(k): 유사한 수   열(i): 파비트 비연속 수
				for(int k=0; k<amlll.size(); k++){ //유사 수만큼 루프	
					
				 dtwVal_vs_amList.add(com.dtw.DTW.DynamicTimeWarp(amlll.get(k).get(i), targetll.get(i))); //시밀러 하나 당 i번째 파트 dtw
				 sum_am_list.set(k, sum_am_list.get(k) + dtwVal_vs_amList.get(k)); //각 유사 위치에 누적
				 
				}
							
			}// for(~targetll)
			
			
			sum_am_list.add(sum_ret); //유사vs타겟 dtw값 리스트에  1차vs타겟  dtw값 추가
	
			amList.add(new SimilarInfo(ret1, "")); //유사 vs 타겟 결과 리스트에  1차 vs 타겟 dtw값 추가
			
			double minimum =10000;
			int minimumIdx=0;
			int idx=0;

			for(double dtwVal : sum_am_list){
				if(dtwVal<minimum){
					minimum=dtwVal;	
					minimumIdx=idx;
				}
				idx++;
			}
		
		  return amList.get(minimumIdx).getGesture();
		//return "이럴리가 없는데";
	}	
	
}	
	


