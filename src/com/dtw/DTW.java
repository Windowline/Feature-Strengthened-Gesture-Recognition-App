package com.dtw;

import java.util.ArrayList;
import java.util.List;
import Gesture.Gesture;
import com.util.EuclideanDistance;
import com.util.ShowPath;


public class DTW{
	
   //No TimeSeries  kind:1 경로연산 X
   public static double DynamicTimeWarp(Gesture tsI, Gesture tsJ)
   {
     
	   
      final double[][] costMatrix = new double[tsI.size()][tsJ.size()];
      final int maxI = tsI.size()-1;
      final int maxJ = tsJ.size()-1;
      
      int minSeq=Math.abs(maxI-maxJ);

     // List<DTWPath> pathList = new ArrayList<DTWPath>();
      
      costMatrix[0][0] = EuclideanDistance.calcDistance(tsI.get(0),  tsJ.get(0)); // 초기의  유클리드 거리를 Matrix에 입력한다.
      
      for (int j=1; j<=maxJ; j++) // 가장 좌측  i == 0 인 Matrix를 입력
         costMatrix[0][j] = costMatrix[0][j-1] + EuclideanDistance.calcDistance(tsI.get(0), tsJ.get(j));
      

      //동적 계획법에 기반한 global cost matrix 생성
      for (int i=1; i<=maxI; i++)   // i = columns
      {
      
         costMatrix[i][0] = costMatrix[i-1][0] + EuclideanDistance.calcDistance(tsI.get(i), tsJ.get(0));

         for (int j=1; j<=maxJ; j++)  // j = rows
         {
        	 
        	 
            // (i,j) = LocalCost(i,j) + minGlobalCost{(i-1,j),(i-1,j-1),(i,j-1)}
            final double minGlobalCost = Math.min(costMatrix[i-1][j],  Math.min(costMatrix[i-1][j-1], costMatrix[i][j-1])); 
            costMatrix[i][j] = minGlobalCost + EuclideanDistance.calcDistance(tsI.get(i), tsJ.get(j));
             
         }  
      }  
      
      
  
      //경로 탐색
      /*
      final double minimumCost = costMatrix[maxI][maxJ];
     
      final WarpPath minCostPath = new WarpPath(maxI+maxJ-1);
      int i = maxI;
      int j = maxJ;
      minCostPath.addFirst(i, j);
      pathList.add(new DTWPath(i,j,costMatrix[i][j]));
      while ((i>0) || (j>0))
      {
        
         final double diagCost;
         final double leftCost;
         final double downCost;

         if ((i>0) && (j>0))
            diagCost = costMatrix[i-1][j-1];
         else
            diagCost = Double.POSITIVE_INFINITY;

         if (i > 0)
            leftCost = costMatrix[i-1][j];
         else
            leftCost = Double.POSITIVE_INFINITY;

         if (j > 0)
            downCost = costMatrix[i][j-1];
         else
            downCost = Double.POSITIVE_INFINITY;


         if ((diagCost<=leftCost) && (diagCost<=downCost))
         {
            i--;
            j--;
         }
         else if ((leftCost<diagCost) && (leftCost<downCost))
            i--;
         else if ((downCost<diagCost) && (downCost<leftCost))
            j--;
         else if (i <= j)  // leftCost==rightCost > diagCost
            j--;
         else   // leftCost==rightCost > diagCost
            i--;
         
     
         minCostPath.addFirst(i, j);
         pathList.add(new DTWPath(i,j,costMatrix[i][j]));
   
      }  // while
	*/

     
      
       return costMatrix[maxI][maxJ];
   }  
   
   
   
   
   //No TimeSeries kind2: 경로반환
   public static ShowPath DynamicTimeWarp_RetPath(Gesture tsI, Gesture tsJ)
   {

      final double[][] costMatrix = new double[tsI.size()][tsJ.size()];
      final int maxI = tsI.size()-1;
      final int maxJ = tsJ.size()-1;
      
 
      
      int minSeq=Math.abs(maxI-maxJ);
      
      int diagonal_rate=0;
     
      
      List<DTWPath> pathList = new ArrayList<DTWPath>();
      
     
      costMatrix[0][0] = EuclideanDistance.calcDistance(tsI.get(0), tsJ.get(0)); // 초기의  유클리드 거리를 Matrix에 입력한다.
      
      for (int j=1; j<=maxJ; j++) // 가장 좌측  i == 0 인 Matrix를 입력
         costMatrix[0][j] = costMatrix[0][j-1] + EuclideanDistance.calcDistance(tsI.get(0), tsJ.get(j));

      
      
      
      //경로 탐색
      for (int i=1; i<=maxI; i++)   // i = columns
      {
         
         costMatrix[i][0] = costMatrix[i-1][0] + EuclideanDistance.calcDistance(tsI.get(i), tsJ.get(0));

         for (int j=1; j<=maxJ; j++)  // j = rows
         {
  
            final double minGlobalCost = Math.min(costMatrix[i-1][j],
                                                  Math.min(costMatrix[i-1][j-1],
                                                           costMatrix[i][j-1]));
            // 두 좌표값중 작은 값을 minGlobalCost로
            costMatrix[i][j] = minGlobalCost + EuclideanDistance.calcDistance(tsI.get(i), tsJ.get(j));
  
         }  
      }  
      
    
      final double minimumCost = costMatrix[maxI][maxJ];
      

 
      int i = maxI;
      int j = maxJ;
  
      pathList.add(new DTWPath(i,j,costMatrix[i][j]));
      while ((i>0) || (j>0))
      {
        
         final double diagCost;
         final double leftCost;
         final double downCost;

         if ((i>0) && (j>0))
            diagCost = costMatrix[i-1][j-1];
         else
            diagCost = Double.POSITIVE_INFINITY;

         if (i > 0)
            leftCost = costMatrix[i-1][j];
         else
            leftCost = Double.POSITIVE_INFINITY;

         if (j > 0)
            downCost = costMatrix[i][j-1];
         else
            downCost = Double.POSITIVE_INFINITY;

       
         if ((diagCost<=leftCost) && (diagCost<=downCost))
         {
            i--;
            j--;
         }
         else if ((leftCost<diagCost) && (leftCost<downCost))
            i--;
         else if ((downCost<diagCost) && (downCost<leftCost))
            j--;
         else if (i <= j)  // leftCost==rightCost > diagCost
            j--;
         else   // leftCost==rightCost > diagCost
            i--;
         
        
        
         diagonal_rate=diagonal_rate+Math.abs(i-j); 
        
         pathList.add(new DTWPath(i,j,costMatrix[i][j]));
      
      }  //while

      
      diagonal_rate=diagonal_rate-minSeq;
 
     return new ShowPath("temp", pathList, maxI, maxJ, diagonal_rate, (int)costMatrix[maxI][maxJ] );
      
    
   }  
   
}  
