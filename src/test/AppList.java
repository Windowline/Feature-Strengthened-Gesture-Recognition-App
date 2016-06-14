package test;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gestureapp.Tarining2;
import com.example.gestureapp.training;

public class AppList extends ListActivity  {
	
	ArrayList<String> arGeneral;
	 List<ApplicationInfo> list;
	 ArrayList<String> pacageNm;
	 
	 String t_type="no";
	 
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        arGeneral = new ArrayList<String>();
	        pacageNm = new ArrayList<String>();
	        
	        
	        Intent intent = getIntent();
	         t_type=intent.getExtras().getString("T_TYPE");

	        
	        final PackageManager pm = getPackageManager();
	        List<ApplicationInfo> list = pm.getInstalledApplications(0);
	        for (ApplicationInfo applicationInfo : list) {
	         String name = String.valueOf(applicationInfo.loadLabel(pm));    // æ€ ¿Ã∏ß
	         String pName = applicationInfo.packageName;   // æ€ ∆–≈∞¡ˆ
//	         Drawable iconDrawable = applicationInfo.loadIcon(pm);   // æ€ æ∆¿Ãƒ‹
	         arGeneral.add(name + " [ " + pName + " ] ");
	         pacageNm.add(pName);
	        }
	        ArrayAdapter<String> adapter;
	        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arGeneral);
	        
	        setListAdapter(adapter);
	    }

	 @Override
	 protected void onListItemClick(ListView l, View v, int position, long id) {
	  String mes;
	  mes = "Select Item = " + arGeneral.get(position);
	 // Toast.makeText(MainActivity.this, mes, Toast.LENGTH_SHORT).show();
	  PackageManager pm = getPackageManager();
	  
	  Intent toTraining = null;
	  
	  if(t_type.equals("no"))
		  toTraining = new Intent(AppList.this, training.class);
	  
	  else if(t_type.equals("yes"))
		  toTraining =  new Intent(AppList.this, Tarining2.class);
	  
	  
	  toTraining.putExtra("appName", pacageNm.get(position));
	  
	  startActivity(toTraining);
	  
	  //Intent i = pm.getLaunchIntentForPackage(pacageNm.get(position));
	  //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	  //startActivity(i);
	 }
	  
}
