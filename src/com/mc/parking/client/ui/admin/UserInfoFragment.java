package com.mc.parking.client.ui.admin;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.OperationCanceledException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mc.parking.client.R;
import com.mc.parking.client.layout.BaseDialogFragmentv4;
import com.mc.parking.client.layout.CustomProgressDialog;
import com.mc.parking.client.utils.SafeAsyncTask;
import com.mc.parking.client.utils.SessionUtils;

public class UserInfoFragment extends Fragment implements OnClickListener{
	protected CustomProgressDialog progressDialog;
	private Activity currentActivity;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ac_userinfo, container, false);
		
		currentActivity = getActivity();
		
		LinearLayout quitsystem = (LinearLayout) view.findViewById(R.id.b5);
		quitsystem.setOnClickListener(this);
		
		
		LinearLayout systemlayout = (LinearLayout) view.findViewById(R.id.b2);
		systemlayout.setOnClickListener(this);
		LinearLayout secritylayout = (LinearLayout) view.findViewById(R.id.b1);
		secritylayout.setOnClickListener(this);
		
	   return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.b5:
			BaseDialogFragmentv4 confirmDialog = new BaseDialogFragmentv4();
			confirmDialog.setMessage("确认退出账户吗?");
			confirmDialog.setPositiveButton("确认",
					new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							SessionUtils.loginUser = null;  
							currentActivity.finish();
						}
					});
			confirmDialog.setNegativeButton("取消",null);
			confirmDialog.show(getFragmentManager(), "");
			break;		
		}
		
	}
	
	private void fetchUpdateInfo(){
		new SafeAsyncTask<Object>() {
			@Override
			public Object call() throws Exception {
				
				try {
					Thread.sleep(3 * 1000);
				} catch (Exception e) {

				}
				return null;
			}

			@Override
			protected void onPreExecute() {
				if (progressDialog == null) {
					progressDialog = CustomProgressDialog
							.createDialog(currentActivity);
					progressDialog.setMessage("正在检查最新版本,请等待...");
				}
				progressDialog.show();
			}

			@Override
			protected void onException(final Exception e)
					throws RuntimeException {
				super.onException(e);
				progressDialog.clear();
				if (e instanceof OperationCanceledException) {
					System.err.println(e);
				}
			}

			@Override
			protected void onSuccess(final Object object)
					throws Exception {
				super.onSuccess(object);
				loginhandler.sendEmptyMessage(0);
				progressDialog.clear();
			}
		}.execute();
	}
	
	private Handler loginhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// 关闭ProgressDialog
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			Toast.makeText(currentActivity, "当前已经是最新版本", Toast.LENGTH_SHORT)
					.show();

			
		}
	};
}
