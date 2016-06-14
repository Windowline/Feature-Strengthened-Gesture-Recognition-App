package com.FsGr;


import java.util.ArrayList;
import java.util.List;

import Gesture.Gesture;
import android.util.Log;

import com.dtw.DTWPath;
import com.util.EuclideanDistance;
import com.util.ShowPath;


public class PartBitGenerator
{	

	private Gesture SeqI = new Gesture(); // 모델 시퀀스
	private Gesture SeqJ = new Gesture(); // 타겟 시퀀스
	
	private double totalCost; // 모델 - 타켓간 총 비용

	private List<DTWPath> normalPath; // 모델 -타겟간 비누적 경로


	private ShowPath warpInfo; 
	
	private int maxDivDepth;
	private double Beta=1.2; //defualt
	
	private double totalAvgSum;
	
	private List<Double> divCostList;
	
	private PartBit partBit;
	
	
	
	
	public static PartBit getPartBitBetween(Gesture target1, Gesture target2, int maxDivDepth)
	{
		return new PartBitGenerator(target1, target2, maxDivDepth).getPartBit();
		
	}
	


	private PartBitGenerator(Gesture target1, Gesture target2, int maxDivDepth)
	{	
		//System.out.println(modelName);
		//System.out.println(targetName);

		this.SeqI=target1;
		this.SeqJ=target2;
		
	
		this.warpInfo = com.dtw.DTW.DynamicTimeWarp_RetPath(SeqI, SeqJ); // I-J 간 매핑정보  ( getPath()는 누적 경로)
		
		this.totalCost=warpInfo.getPath().get(warpInfo.getPath().size()-1).getCost(); //총 코스트합
		
		this.maxDivDepth=maxDivDepth;
	
		this.normalPath=new ArrayList<DTWPath>(); //느적 아닌 코스트 경로 생성
		
		for(DTWPath p : this.warpInfo.getPath())
		    this.normalPath.add(new DTWPath(p.index[0], p.index[1], EuclideanDistance.calcDistance(SeqI.get(p.index[0]), SeqJ.get(p.index[1]))));
			//구간별 비누적 경로 생성(i,j 유클리디안),  쓰이지 않음
		
		
		this.partBit = findHighCostInterval_Init(); //특징 강조 부분 추출
	}
	
	
	
	
	private PartBit getPartBit()
	{
		return this.partBit;
	}

	
	
	//*파트비트를 구하기 위해쓰임.
	// * 분할된 구간의 평균치를 저장해두는 리스트 반환
	// * 균등하게 나누기 위해 2로 나눠가며 생성
	private List<Double> setDivCost(Gesture subSeqI, Gesture subSeqJ, int divDepth, List<Double> divCostList) //여기가문제..?
	{
		
		int iSize = subSeqI.size();
		int jSize = subSeqJ.size();
		
		List<Double> avg = new ArrayList<Double>();
		
		if(divDepth==this.maxDivDepth)
		{
			
			ShowPath tempWarpInfo = com.dtw.DTW.DynamicTimeWarp_RetPath(subSeqI, subSeqJ);
			
			avg.add(tempWarpInfo.getPath().get(tempWarpInfo.getPath().size()-1).getCost());
			
		
			return avg;
		}
		
		List<Double> frontPart = setDivCost(subSeqI.subList(0, iSize/2), subSeqJ.subList(0, jSize/2), divDepth+1, divCostList);
		List<Double> rearPart = setDivCost(subSeqI.subList(iSize/2, iSize), subSeqJ.subList(jSize/2, jSize), divDepth+1, divCostList);
		
		avg.addAll(frontPart);
		avg.addAll(rearPart);
	
		return avg;
	}
	
	
	//모델과 타겟간 파트비트 구함.
	private PartBit findHighCostInterval_Init()
	{	
		this.divCostList= new ArrayList<Double>();
			
		this.divCostList.add(0.0);
		
		//미리 구간 별로 나누어진 코스트 리스트 받음
		this.divCostList.addAll(setDivCost(this.SeqI, this.SeqJ, 0, new ArrayList<Double>()));
		
			
		//구간별 코스트의 평균을 구하기 위해 미리 전체 코스트를 구하고 2^분할 레벨로  나눠주면 중복 계산 피함 (안자르고 했을 떄와 차이??  ---> 안잘랐을 때의 총 코스트와  자르고 모두 더한 코스트는 달라...-->왜곡 때문에)
		this.totalAvgSum=0.0;
		for(double val : this.divCostList)
			this.totalAvgSum=this.totalAvgSum+val;
	
	
		PartBit frontPartBit =  findHighCostInterval(1, divCostList.size()/2, 1);
		PartBit rearPartBit = findHighCostInterval(divCostList.size()/2+1, divCostList.size()-1, 1);
		
		frontPartBit.addAll(rearPartBit);
		
	//	frontPartBit.add(0, false); //dummy
		
		return frontPartBit;
	}
	


	//파트비트 구하는 부분함수 재귀적 실행
	private PartBit findHighCostInterval(int s, int e,  int divDepth)
	{
		PartBit subPartBit = new PartBit();
		
		if(maxDivDepth<divDepth) //최대 분할 레벨을 넘어가기 전까지 1(특징강조)이 나오지 않는다면 0(비슺한 부분)으로 취급
			return subPartBit;
		
		double val=0;
		double avg=this.totalAvgSum/Math.pow(2, divDepth); //분할 레벨에 따른 평균 계산
		
		for(int i=s; i<=e; i++)
			val=val+this.divCostList.get(i); // 미리 저장해둔 구간별 코스트를 이용
		
		
		System.out.println("실행범위  : " + s + " ~ " + e + " / 실행크기 "  + (e-s+1) + " /  divDepth: " + divDepth + " / 평균:  " + avg + " / val:  " + val + " / totalsum: " + this.totalAvgSum);
		
		if(avg * this.Beta > val) // 추출 조건 비만족
		{
			PartBit frontPartBit = findHighCostInterval(s, s+(e-s)/2, divDepth+1);
			PartBit rearPartBit = findHighCostInterval(s+(e-s)/2+1, e, divDepth+1);
			
			if(frontPartBit.size()!=0 && rearPartBit.size()!=0) // 아직 리프가 아니라면 
				frontPartBit.addAll(rearPartBit); 
			
			else frontPartBit.add(false); //리프라면 0
			
			return frontPartBit;
		}
		
		
		else //추출 조건 만족
		{
			
		System.out.println("발견!");
			
			if(this.maxDivDepth==divDepth)
				subPartBit.add(true);
			
			else
			{
				for(int i=1; i<=Math.pow(2, this.maxDivDepth-divDepth); i++) //할당 구간만큼 1로 채운다
					subPartBit.add(true);
			}
					
			return subPartBit;
		}
	
	}


}