package Gesture;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GestureList extends ArrayList<Gesture>
{
	
	
	public GestureList() { }
	
	public void SaveAsModel(String saveAsPath) //C:\\model Ȥ�� C:\\model\\quant_model�� ��η� �´�.
	{
		try 
		{
			for(Gesture Gb : this)
			{
				String fileName = "\\" + Gb.getName() + ".txt"; //���̵�.txt�� ����ȴ�.
				
				StringBuffer attr = new StringBuffer();
				File savefile = new File(saveAsPath);
				FileOutputStream fos = new FileOutputStream(savefile + fileName);
				System.out.println(savefile + fileName);
				
				for(SensorValue Lsv : Gb)
					attr.append(Double.toString(Lsv.getX())+","+Double.toString(Lsv.getY())+","+Double.toString(Lsv.getZ())+"\n");
				
				fos.write(attr.toString().getBytes());
				fos.close();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public void SaveAsSamples(String saveAsPath) //C:\\model\\���̸����� �� ��η� �´�.
	{
		try 
		{
			String fileName = "temp"; //C:\\model//���̸�����//samples.txt�� ����ȴ�.
			
			StringBuffer attr = new StringBuffer();
			File savefile = new File(saveAsPath);
			FileOutputStream fos = new FileOutputStream(savefile + fileName);
			
			int idx = 0;
			
			for(Gesture Gb : this)
			{
				System.out.println(savefile + fileName);
				
				for(SensorValue Lsv : Gb)
					attr.append(Double.toString(Lsv.getX())+","+Double.toString(Lsv.getY())+","+Double.toString(Lsv.getZ())+"\r\n");
				
				attr.append("--" + idx + "th--\r\n");
				fos.write(attr.toString().getBytes());
				
				idx++;
			}
			
			fos.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	
	public Gesture getMedian(){ //�߾� ����
		
		
		List<Double> distSumList = new ArrayList<Double> ();
		int retIdx, minIdx;
		double tempSum=0.0;
		double minSum;
		
		for(int row=0; row<this.size(); row++){
			
			for(int col=0; col<this.size(); col++)
				tempSum=tempSum + com.dtw.DTW.DynamicTimeWarp(this.get(row), this.get(col));
				
			distSumList.add(tempSum);
			
		}
		
		retIdx=0; minSum=distSumList.get(0);
		
		for(int i=1; i<distSumList.size(); i++){
		
			if(minSum > distSumList.get(i)){
				retIdx=i;
				minSum=distSumList.get(i);
			}	
		}
		
		return this.get(retIdx);

	}
	
	
	

	

}
