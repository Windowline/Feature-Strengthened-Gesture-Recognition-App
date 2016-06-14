package test2;


import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.example.gestureapp.R;




public class ACT_button extends Activity 
{
	
	BCR_button bcr;
	BCR_button bcr2;
	BCR_button bcr3;

	    @Override
	    public void onCreate(Bundle savedInstanceState)
	    {
	         
	    	
	    	super.onCreate(savedInstanceState);
			setContentView(R.layout.act_button);	
	    	
	    	
	        //전원버튼
				bcr= new BCR_button();
				bcr2 =new BCR_button();
				bcr3= new BCR_button();
			
	            IntentFilter offFilter = new IntentFilter (Intent.ACTION_SCREEN_OFF);
	            IntentFilter onFilter = new IntentFilter (Intent.ACTION_SCREEN_ON);
	            IntentFilter mediaFilter = new IntentFilter (Intent.ACTION_MEDIA_BUTTON);
	            
	            
	            registerReceiver(bcr, offFilter);
	            registerReceiver(bcr, onFilter);
	            registerReceiver(bcr, mediaFilter);
	    }
	 
	        @Override
	    public void onDestroy(){
	        unregisterBroadcast();//전원버튼
	    }
	 
	    //전원버튼
	    private void unregisterBroadcast() {
	        unregisterReceiver(bcr);
	    }
		
	
	
	/*
	 @Override
	    public void onCreate(Bundle savedInstanceState)
	    {
			super.onCreate(savedInstanceState);
			//setContentView(R.layout.act_button);	
	
		 BCR_button mMediaButtonReceiver = new BCR_button();
		 IntentFilter mediaFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
	
		 mediaFilter.setPriority(1);
		 registerReceiver(mMediaButtonReceiver, mediaFilter);
		 
	    }
 	*/
	}
   
	
	
