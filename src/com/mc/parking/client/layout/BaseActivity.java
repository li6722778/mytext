package com.mc.parking.client.layout;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class BaseActivity extends Activity{
	protected CustomProgressDialog progressDialog;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//			case R.id.item_clear_memory_cache:
//				imageLoader.clearMemoryCache();
//				return true;
//			case R.id.item_clear_disc_cache:
//				imageLoader.clearDiscCache();
//				return true;
//			default:
//				return false;
//		}
		return true;
	}
	
	@Override 
	public void onBackPressed() { 
	    super.onBackPressed(); 
	
	} 
}
