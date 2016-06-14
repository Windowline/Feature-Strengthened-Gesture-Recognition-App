package com.FsGr;

import java.util.ArrayList;

public class PartBitList extends ArrayList<PartBit> //파트비트 리스트
{
	
	
	//비트 OR
	public PartBit getUnion()
	{ 
		PartBit retBit = new PartBit();
		
		int num = this.size();
		
		if(num==1) 
			return this.get(0);
		
		int partBitSize=this.get(0).size();
	
		boolean temp;
		
		for(int i=0; i<partBitSize; i++)
		{
			temp = this.get(0).get(i) || this.get(1).get(i);
			
			for(int j=2; j<num; j++)
			temp= temp || this.get(j).get(i);
			
			retBit.add(temp);
		}
		return retBit;
	}
}
