package com.FsGr;



import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import Gesture.Gesture;
import Gesture.GestureList;
import android.util.Log;
import android.widget.Toast;





public class GestureRecognitionManager { // �ν� �Ŵ���
	

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
	
	
	
	
	
	//Ÿ�ӽø��� �Ⱦ�
	public Gesture getFirstRecog_NOTS(Gesture inputData){ // 1�� �Ǻ�
		
		
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
		

	











//Ÿ�ӽø��� �Ⱦ�
public List<Gesture> getFirstRecog_NOTS_SimilarList(Gesture inputData){ //���� ���� ������
	
	int resultIdx = 0;
//	double initDist = 10000;
	double shortestDist = Double.MAX_VALUE;
	
	double simiConst=1; //���� ���
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
		
		System.out.println("*******************************2�� �� �����*******************************");
		
		PartBit unionedPartBit;
		
		int maxDivDepth=3; //�ִ� ���� ����
		
		List<SimilarInfo> amList=similarInfo; // SI����Ʈ    [0]:����� ������̸� , [1]�����ϴ� ��Ʈ��Ʈ
		
		GestureList amList_SV = new GestureList ();  	//����� ����Ʈ 
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
			
			amList_PartBit.add(temp); //��Ʈ XOR�� ���� ���� ��Ʈ��Ʈ ����Ʈ �ҷ��� 
		}

			unionedPartBit = amList_PartBit.getUnion();  //��Ʈ XOR
			
			System.out.println("================Unioned Part Bit(2���� :" + ret1.getName() + ")================");
			
			for(Boolean bit : unionedPartBit){
				
				if(bit)
				System.out.print("1  ");
				
				else
				System.out.print("0  ");
			}
			
			Log.i("pppppppbbbbbbbbbbbbbbbbbbbb", "pb:" + unionedPartBit.size());
			
			//Toast.makeText(getBaseContext(), "unionPB"+unionedPartBit.size(), 0).show();
			
			System.out.println("\n==============================================================");
			
			GestureList targetll = target.getFinalSeqList(unionedPartBit, maxDivDepth); //�׽�Ʈ(�Է�) �����Ϳ� ���� 1�κи� ����
			GestureList retll = ret1.getFinalSeqList(unionedPartBit, maxDivDepth); //��� �����Ϳ� ���� 1�κи� ����
			
			List<GestureList> amlll = new ArrayList<GestureList> (); 
			
			 for(Gesture am : amList_SV)		 
			 amlll.add(am.getFinalSeqList(unionedPartBit, maxDivDepth)); //���� ����ó�鿡 ���� 1�κи� ����
			
	
				
			 double sum_ret=0; // ret - target�� ���ҵȽ�������  dtw ��
			 List<Double> sum_am_list = new ArrayList<Double>(); 
				
			for(int j=0; j<amList.size(); j++)
				sum_am_list.add(0.0); //���絿�۵�(j~) - target�� ���� �������� dtw �� 
			
			
			
			double dtwVal_vs_ret; //ret - target�� ���ҵȽ�������  dtw
			List<Double> dtwVal_vs_amList = new ArrayList<Double>(); //���� ���� - target�� ���ҵȽ�������  dtw
			
			for(int i=0; i<targetll.size(); i++){ // ��Ʈ��Ʈ�� �ҿ����� ��ŭ ����
				
			
				dtwVal_vs_ret= com.dtw.DTW.DynamicTimeWarp(retll.get(i), targetll.get(i)); // i��° ��Ʈ �� dtw
				sum_ret=sum_ret + dtwVal_vs_ret;
								
			
			//	for(GestureList amll :amlll)//�ùз� ������ŭ ����
			//		dtwVal_vs_amList.add(com.dtw.DTW.DynamicTimeWarp(amll.get(i), targetll.get(i))); //�ùз� �ϳ� �� i��° ��Ʈ dtw
				
				
				//amlll ��(k): ������ ��   ��(i): �ĺ�Ʈ �񿬼� ��
				for(int k=0; k<amlll.size(); k++){ //���� ����ŭ ����	
					
				 dtwVal_vs_amList.add(com.dtw.DTW.DynamicTimeWarp(amlll.get(k).get(i), targetll.get(i))); //�ùз� �ϳ� �� i��° ��Ʈ dtw
				 sum_am_list.set(k, sum_am_list.get(k) + dtwVal_vs_amList.get(k)); //�� ���� ��ġ�� ����
				 
				}
							
			}// for(~targetll)
			
			
			sum_am_list.add(sum_ret); //����vsŸ�� dtw�� ����Ʈ��  1��vsŸ��  dtw�� �߰�
	
			amList.add(new SimilarInfo(ret1, "")); //���� vs Ÿ�� ��� ����Ʈ��  1�� vs Ÿ�� dtw�� �߰�
			
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
		//return "�̷����� ���µ�";
	}	
	
}	
	


