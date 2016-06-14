package com.example.gestureapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

public class SVReceiver extends BroadcastReceiver {

	PackageManager pm;
	
	@Override
    public void onReceive(Context context, Intent intent){
	
		if(intent.getAction().equals("auto_recog_start")){
			
			Toast.makeText(context, "START", 0).show();
		}
		
		
		else if(intent.getAction().equals("auto_recog_end")){
			
			Toast.makeText(context, "END", 0).show();
		}
		
		
		else if (intent.getAction().equals("dtw_and_exe_app")){
			
			//String ret=intent.getExtras().toString();
		
			//Log.e("result: ", ret);
		//	pm= context.getPackageManager();
		//	Intent i = pm.getLaunchIntentForPackage(ret);
		//	  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//	  context.startActivity(i);
		}
		
	}	
	
}
