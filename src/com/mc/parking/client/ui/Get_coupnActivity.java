package com.mc.parking.client.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;

import com.mc.park.client.R;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.utils.SessionUtils;

public class Get_coupnActivity extends Activity {

	private LinearLayout deletebutton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_getcoupn);
		deletebutton = (LinearLayout) findViewById(R.id.delete);
		deletebutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();			
			}
		});

	}
	
	
	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("userinfo", SessionUtils.loginUser);
		super.onSaveInstanceState(outState);
	}
	
	
	@Override
	public void onRestoreInstanceState(Bundle outState) {
	        super.onRestoreInstanceState(outState);
	        SessionUtils.loginUser=(TuserInfo) outState.getSerializable("userinfo");
	}



}
