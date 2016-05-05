package com.mc.parking.client.ui.admin;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.mc.park.client.R;

public class ChangeParkPriceActivity extends FragmentActivity{
	public ChangeParkPriceFragment myFragment=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.ac_change_parkprice);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_topbar);
		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		titleView.setText("价格调整");
		FragmentManager manager = getFragmentManager();

		myFragment = (ChangeParkPriceFragment) manager
				.findFragmentById(R.id.change_price_fragment);
		


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public void backTo(View v) {
		// mapFragment.setList();
		finish();
	}
	
	
	
	
}
