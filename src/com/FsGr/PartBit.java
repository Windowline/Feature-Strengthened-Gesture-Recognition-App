package com.FsGr;

import java.util.ArrayList;
import java.util.List;


public class PartBit extends ArrayList<Boolean>
{
	
	
	public PartBit subList(int fromIndex, int toIndex) //�ٷ� ��ȯx
	{
		
		
		List<Boolean> sublist = new ArrayList<Boolean>(); //�ӽ÷� ��Ƶ� ��ü
		PartBit subpartbit = new PartBit(); //��ȯ�� ��ü

		sublist = super.subList(fromIndex, toIndex);

		for(int i = 0; i < sublist.size(); i++)
		subpartbit.add(sublist.get(i));

		return subpartbit;
		
		
	}
}
