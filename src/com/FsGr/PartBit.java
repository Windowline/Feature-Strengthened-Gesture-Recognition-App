package com.FsGr;

import java.util.ArrayList;
import java.util.List;


public class PartBit extends ArrayList<Boolean>
{
	
	
	public PartBit subList(int fromIndex, int toIndex) //바로 반환x
	{
		
		
		List<Boolean> sublist = new ArrayList<Boolean>(); //임시로 담아둘 객체
		PartBit subpartbit = new PartBit(); //반환할 객체

		sublist = super.subList(fromIndex, toIndex);

		for(int i = 0; i < sublist.size(); i++)
		subpartbit.add(sublist.get(i));

		return subpartbit;
		
		
	}
}
