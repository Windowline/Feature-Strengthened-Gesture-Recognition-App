package Gesture;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.FsGr.PartBit;


public class Gesture extends ArrayList<SensorValue>
{
	
	private String Name = "defalut"; //A, B, C, D ��
	
	public Gesture(Gesture gesture, String name) 
	{
		this.Name = name;
		super.addAll(gesture);
	}
	
	public Gesture(String name){
		this.Name=name;
	}
	
	
	public Gesture(Gesture gesture)
	{
		super.addAll(gesture);
	}
	
	
	public Gesture()
	{
		
	}
	

	public Gesture subList(int fromIndex, int toIndex) //super.subList�� �״�� ��ȯ �Ұ���
	{ 
		List<SensorValue> sublist = new ArrayList<SensorValue>(); //�ӽ÷� ��Ƶ� ��ü
		Gesture subgesture = new Gesture(); //��ȯ�� ��ü

		sublist = super.subList(fromIndex, toIndex);

		for(int i = 0; i < sublist.size(); i++)
		subgesture.add(sublist.get(i));

		return subgesture;
	}
	
	
	public String getName()
	{
		return Name;
	}
	
	public void setName(String Name)
	{
		this.Name = Name;
	}
	
	
	
	public GestureList getFinalSeqList(PartBit unionedPartBit, int maxDivDepth) //2���񱳸� ���� �������� ��������Ʈ���� �����. 1�� �ι� ����)
	{
		GestureList dividedGestures = getDividedSeq(maxDivDepth);
		
		System.out.println("ds: "+dividedGestures.size());
		
		GestureList gestureList =new GestureList ();
		
		Gesture temp;
		
		int j;
		
		for(int i=0; i<Math.pow(2, maxDivDepth); )
		{
			temp = new Gesture();
			
			if(unionedPartBit.get(i))
			{				
				temp.addAll(dividedGestures.get(i));
				//System.out.println("i :" +i);
				
				for(j=i+1; j<Math.pow(2, maxDivDepth);  j++)
				{
					if(!unionedPartBit.get(j))
						break;
					
					temp.addAll(dividedGestures.get(j));
				}			
				
				gestureList.add(temp);
				
				i=j+1;
			}
			
			else i++;
		}
		
		return gestureList;
	}
	
	
	private GestureList getDividedSeq(int maxDivDepth) //2^maxDivDepth ��ŭ ����Ʈ ������
	{
		LinkedList<Gesture> Q = new LinkedList<Gesture>();
		GestureList gestureList = new GestureList();
		
		Q.add(this);
		
		Gesture temp;
		
		int tSize;
		
		for(int i=0; i<maxDivDepth; i++)
		{
			for(int j=1; j<=Math.pow(2, i); j++)
			{
				temp = Q.pollFirst(); 
		
				tSize= temp.size();
						
				//System.out.println("tsize: " + tSize);
				
				Q.offerLast(temp.subList(0, tSize/2)); //offerLast : ���� ����
				Q.offerLast(temp.subList(tSize/2, tSize));
			}
		}
		
		while(!Q.isEmpty())
		{
			temp=Q.pollFirst();
			//System.out.println("aa:" + temp.size());
		
			gestureList.add(temp);
		}
		
		return gestureList;
	}
	
	
	
	
	
	
	public int saveAsModel(FileOutputStream fos){ //������ ��ȯ
		
		System.out.println("this size: " +this.size());
		
		String toInput=null;
		
		int i=0;
		
		try{
			
			for(SensorValue sv : this){
				i++;
				toInput=Double.toString(sv.getX())+","+Double.toString(sv.getY())+","+Double.toString(sv.getZ())+"/";
				fos.write(toInput.getBytes());
			}
			
			fos.close();
			
		}	 
		  catch(Exception e){}
		
		
		return i;
	}
	
	
	
	public int setSVFromModel(FileInputStream fis){
		
		System.out.println("set SV");
		
		int i=0;
		
		byte[] data = new byte[1024*50];
		
		
		try
		{	
			fis.read(data);
			String svSet = new String(data);
			String aline_sv[] = svSet.split("/");
			
			//System.out.println("bcnt: "+ aline_sv.length);
			
			String aline_xyz[];
			
			
			
			for(String sv : aline_sv){
				aline_xyz=sv.split(",");
				SensorValue svInput = new SensorValue(Double.parseDouble(aline_xyz[0]),Double.parseDouble(aline_xyz[1]),Double.parseDouble(aline_xyz[2]));
				this.add(svInput);
			}
			
			
		}
		catch(Exception e){}
		
		return this.size();
		
	}
	
	
	
	
	
	
	
	
	
	
}
