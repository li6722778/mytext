package com.mc.parking.client.layout;

import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * for actionbar common activity
 * @author woderchen
 *
 */
public class ActionBaseActivity extends FragmentActivity {
	protected CustomProgressDialog progressDialog;

	public void backTo(View v) {
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// switch (item.getItemId()) {
		// case R.id.item_clear_memory_cache:
		// imageLoader.clearMemoryCache();
		// return true;
		// case R.id.item_clear_disc_cache:
		// imageLoader.clearDiscCache();
		// return true;
		// default:
		// return false;
		// }
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

	}
}
