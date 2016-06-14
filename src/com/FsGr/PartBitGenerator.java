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

	private Gesture SeqI = new Gesture(); // �� ������
	private Gesture SeqJ = new Gesture(); // Ÿ�� ������
	
	private double totalCost; // �� - Ÿ�ϰ� �� ���

	private List<DTWPath> normalPath; // �� -Ÿ�ٰ� ���� ���


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
		
	
		this.warpInfo = com.dtw.DTW.DynamicTimeWarp_RetPath(SeqI, SeqJ); // I-J �� ��������  ( getPath()�� ���� ���)
		
		this.totalCost=warpInfo.getPath().get(warpInfo.getPath().size()-1).getCost(); //�� �ڽ�Ʈ��
		
		this.maxDivDepth=maxDivDepth;
	
		this.normalPath=new ArrayList<DTWPath>(); //���� �ƴ� �ڽ�Ʈ ��� ����
		
		for(DTWPath p : this.warpInfo.getPath())
		    this.normalPath.add(new DTWPath(p.index[0], p.index[1], EuclideanDistance.calcDistance(SeqI.get(p.index[0]), SeqJ.get(p.index[1]))));
			//������ ���� ��� ����(i,j ��Ŭ�����),  ������ ����
		
		
		this.partBit = findHighCostInterval_Init(); //Ư¡ ���� �κ� ����
	}
	
	
	
	
	private PartBit getPartBit()
	{
		return this.partBit;
	}

	
	
	//*��Ʈ��Ʈ�� ���ϱ� ���ؾ���.
	// * ���ҵ� ������ ���ġ�� �����صδ� ����Ʈ ��ȯ
	// * �յ��ϰ� ������ ���� 2�� �������� ����
	private List<Double> setDivCost(Gesture subSeqI, Gesture subSeqJ, int divDepth, List<Double> divCostList) //���Ⱑ����..?
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
	
	
	//�𵨰� Ÿ�ٰ� ��Ʈ��Ʈ ����.
	private PartBit findHighCostInterval_Init()
	{	
		this.divCostList= new ArrayList<Double>();
			
		this.divCostList.add(0.0);
		
		//�̸� ���� ���� �������� �ڽ�Ʈ ����Ʈ ����
		this.divCostList.addAll(setDivCost(this.SeqI, this.SeqJ, 0, new ArrayList<Double>()));
		
			
		//������ �ڽ�Ʈ�� ����� ���ϱ� ���� �̸� ��ü �ڽ�Ʈ�� ���ϰ� 2^���� ������  �����ָ� �ߺ� ��� ���� (���ڸ��� ���� ���� ����??  ---> ���߶��� ���� �� �ڽ�Ʈ��  �ڸ��� ��� ���� �ڽ�Ʈ�� �޶�...-->�ְ� ������)
		this.totalAvgSum=0.0;
		for(double val : this.divCostList)
			this.totalAvgSum=this.totalAvgSum+val;
	
	
		PartBit frontPartBit =  findHighCostInterval(1, divCostList.size()/2, 1);
		PartBit rearPartBit = findHighCostInterval(divCostList.size()/2+1, divCostList.size()-1, 1);
		
		frontPartBit.addAll(rearPartBit);
		
	//	frontPartBit.add(0, false); //dummy
		
		return frontPartBit;
	}
	


	//��Ʈ��Ʈ ���ϴ� �κ��Լ� ����� ����
	private PartBit findHighCostInterval(int s, int e,  int divDepth)
	{
		PartBit subPartBit = new PartBit();
		
		if(maxDivDepth<divDepth) //�ִ� ���� ������ �Ѿ�� ������ 1(Ư¡����)�� ������ �ʴ´ٸ� 0(���� �κ�)���� ���
			return subPartBit;
		
		double val=0;
		double avg=this.totalAvgSum/Math.pow(2, divDepth); //���� ������ ���� ��� ���
		
		for(int i=s; i<=e; i++)
			val=val+this.divCostList.get(i); // �̸� �����ص� ������ �ڽ�Ʈ�� �̿�
		
		
		System.out.println("�������  : " + s + " ~ " + e + " / ����ũ�� "  + (e-s+1) + " /  divDepth: " + divDepth + " / ���:  " + avg + " / val:  " + val + " / totalsum: " + this.totalAvgSum);
		
		if(avg * this.Beta > val) // ���� ���� ����
		{
			PartBit frontPartBit = findHighCostInterval(s, s+(e-s)/2, divDepth+1);
			PartBit rearPartBit = findHighCostInterval(s+(e-s)/2+1, e, divDepth+1);
			
			if(frontPartBit.size()!=0 && rearPartBit.size()!=0) // ���� ������ �ƴ϶�� 
				frontPartBit.addAll(rearPartBit); 
			
			else frontPartBit.add(false); //������� 0
			
			return frontPartBit;
		}
		
		
		else //���� ���� ����
		{
			
		System.out.println("�߰�!");
			
			if(this.maxDivDepth==divDepth)
				subPartBit.add(true);
			
			else
			{
				for(int i=1; i<=Math.pow(2, this.maxDivDepth-divDepth); i++) //�Ҵ� ������ŭ 1�� ä���
					subPartBit.add(true);
			}
					
			return subPartBit;
		}
	
	}


}