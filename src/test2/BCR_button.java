package test2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;




public class BCR_button extends BroadcastReceiver  {
	
	  public static final String ScreenOff = "android.intent.action.SCREEN_OFF";
	  
	  public static final String ScreenOn = "android.intent.action.SCREEN_ON";
	  
	  public static final String Media = "android.intent.action.MEDIA_BUTTON";
      
      @Override
      public void onReceive(Context context, Intent intent){
    	  
          if(intent.getAction().equals(ScreenOff))
          	System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaa");  	
          
          
          else if (intent.getAction().equals(ScreenOn))
        	  System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"); 
          
          else if(intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON))
        	  System.out.println("cccccccccccccccccccccccccccccccccc");
          
    
          
          	return;
         // else if(intent.getAction().equals(Media))
        //	  System.out.println("cccccccccccccccccccccccc"); 
          
          }
      
      	 
 
}



//public class BCR_button extends BroadcastReceiver implements   {



