package com.dtw;


public class DTWPath //¸ÅÄªÁ¤º¸ (ÇÏ³ªÀÇ ½ì ¿ä¼Ò : i,j global cost) 
{
	public int[] index = new int[2];
	double cost;
	
	public DTWPath(int i, int j, double cost)
	{
		index[0]=i;
		index[1]=j;
		this.cost=cost;
	}
	
	public int getI()
	{
		return index[0];
	}
	
	public int getJ()
	{
		return index[1];
	}
	
	public double getCost()
	{
		return cost;
	}
	
}
