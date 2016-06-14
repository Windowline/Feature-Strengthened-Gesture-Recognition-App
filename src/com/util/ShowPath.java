package com.util;


import java.util.ArrayList;
import java.util.List;

import com.dtw.DTWPath;

public class ShowPath  { // 누적경로 저장
	
		/**
	 * 
	 */

	List<DTWPath> mpList;
	int numOfWarping;
	int mSize;
	int tSize;
	
	
	public List<DTWPath> getPath(){
		
		List<DTWPath> retPath=new ArrayList<DTWPath>();
		
		for(int i=mpList.size()-1; i>=0; i--)
			retPath.add(mpList.get(i));
		
		return retPath;
	}
	
	

	
	
	
	
	public ShowPath(String mName,  List<DTWPath> mpList, int mSize, int tSize, int dia, int pathVal)
	{
      
          
          this.mpList=new ArrayList<DTWPath>();
   
          this.mpList=mpList;
          this.numOfWarping=mpList.size();
          this.mSize=mSize;
          this.tSize=tSize;
          
	}



     
     
     public boolean isPath(int i, int j)
     {    	 
    	 for(DTWPath mp : mpList)
    	 {
    		if((i==mp.getI() && j==mp.getJ()))
    		 return true;
    	 }
    	
    	 return false;
     }
}
