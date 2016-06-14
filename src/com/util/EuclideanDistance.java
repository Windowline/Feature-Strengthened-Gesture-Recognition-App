
package com.util;

import Gesture.SensorValue;


public class EuclideanDistance
{
   public static double calcDistance(double[] vector1, double[] vector2) // 
   {
      if (vector1.length != vector2.length)
         throw new InternalError("ERROR:  cannot calculate the distance between vectors of different sizes.");

      double sqSum = 0.0;
      
      for (int x=0; x<vector1.length; x++)
      {
          sqSum += Math.pow(vector1[x]-vector2[x], 2.0);
      }
      return Math.sqrt(sqSum);
   }  
   
   
   
  
   public static double calcDistance(SensorValue vector1, SensorValue vector2) // 
   {
     
       double sqSum = 0.0;
      
  
    	  sqSum=sqSum+ Math.pow(vector1.getX()-vector2.getX(), 2.0);
    	  sqSum=sqSum+ Math.pow(vector1.getY()-vector2.getY(), 2.0);
    	  sqSum=sqSum+ Math.pow(vector1.getZ()-vector2.getZ(), 2.0);
    	  
      
      return Math.sqrt(sqSum);
   }  
}