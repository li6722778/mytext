package com.mc.parking.client.layout;

import com.mc.park.client.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class timepickDialog extends Dialog{
	 private Context context = null;
	
	public timepickDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		 this.context = context;
	}
	
	 public timepickDialog(Context context, int theme){  
	        super(context, theme);  
	        this.context = context;  
	    }  
	    @Override  
	    protected void onCreate(Bundle savedInstanceState) {  
	        // TODO Auto-generated method stub  
	        super.onCreate(savedInstanceState);  
	        this.setContentView(R.layout.dialog_timepick);  
	    }  
	   

}
