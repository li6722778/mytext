package com.mc.parking.client.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.OperationCanceledException;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mc.parking.client.Constants;
import com.mc.parking.client.PackingApplication;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.BaseActivity;
import com.mc.parking.client.layout.BaseDialogFragment;
import com.mc.parking.client.layout.BaseToggleButton;
import com.mc.parking.client.layout.CustomProgressDialog;
import com.mc.parking.client.layout.BaseToggleButton.OnChangedListener;
import com.mc.parking.client.ui.fragment.ChangePassFragment;
import com.mc.parking.client.utils.SafeAsyncTask;
import com.mc.parking.client.utils.SessionUtils;

public class UserInfoActivity extends BaseActivity implements OnClickListener {

	private Context context;
	BaseToggleButton newmeesageButton, voiceButton, vibrationButton;

	public void backTo(View v) {
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_userinfo);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_topbar);
		context = PackingApplication.getInstance();
		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		final TuserInfo userinfo = SessionUtils.loginUser;
		if (userinfo != null) {
			String abbr = "";
			if (userinfo.userType == Constants.USER_TYPE_PADMIN) {
				abbr = "[车位管理员]";
			}
			titleView.setText(getResources().getString(
					R.string.rightmenu_userinfo_title)
					+ abbr);
		} else {
			titleView.setText(R.string.rightmenu_userinfo_title);
		}

		// final OrderEntity
		// orderinfo=(OrderEntity)getIntent().getSerializableExtra("orderinfo");
		//
		LinearLayout quitsystem = (LinearLayout) findViewById(R.id.b5);
		quitsystem.setOnClickListener(this);
		RelativeLayout updatepassword = (RelativeLayout) findViewById(R.id.update_password);
		updatepassword.setOnClickListener(this);
		LinearLayout secritylayout = (LinearLayout) findViewById(R.id.b1);
		secritylayout.setOnClickListener(this);
		TextView username = (TextView) findViewById(R.id.username);
		TextView useraccount = (TextView) findViewById(R.id.useraccount);
		TextView userphone = (TextView) findViewById(R.id.userphone);
		if (SessionUtils.loginUser != null) {
			username.setText("" + SessionUtils.loginUser.userName);
			useraccount.setText("" + SessionUtils.loginUser.userPhone);
			userphone.setText("" + SessionUtils.loginUser.userPhone);
		}

		initBaseToggleButton();

		newmeesageButton.setOnChangedListener(new OnChangedListener() {

			@Override
			public void OnChanged(boolean CheckState) {
				// TODO Auto-generated method stub
				if (CheckState == true) {

					Toast.makeText(UserInfoActivity.this, "开启新消息通知",
							Toast.LENGTH_SHORT).show();
				
					Constants.NEWMESSAGENOTICE = 1;
					return;
				}

				Toast.makeText(UserInfoActivity.this, "关闭新消息通知",
						Toast.LENGTH_SHORT).show();
	
				Constants.NEWMESSAGENOTICE = 0;
				
				return;

			}
		});

		voiceButton.setOnChangedListener(new OnChangedListener() {

			@Override
			public void OnChanged(boolean CheckState) {
				// TODO Auto-generated method stub
				if (CheckState == true) {

					Toast.makeText(UserInfoActivity.this, "开启新消息声音提示",
							Toast.LENGTH_SHORT).show();
					Constants.NEWMESSAGENOTICEVOICE = 1;
		
					return;

				}
				Toast.makeText(UserInfoActivity.this, "关闭新消息声音提示",
						Toast.LENGTH_SHORT).show();
				Constants.NEWMESSAGENOTICEVOICE = 0;
	
				return;
			}
		});

		vibrationButton.setOnChangedListener(new OnChangedListener() {

			@Override
			public void OnChanged(boolean CheckState) {
				// TODO Auto-generated method stub
				if (CheckState == true) {

					Toast.makeText(UserInfoActivity.this, "开启新消息震动提示",
							Toast.LENGTH_SHORT).show();
					Constants.NEWMESSAGENOTICEVIBRATE = 1;
					return;

				} else {
					Toast.makeText(UserInfoActivity.this, "关闭新消息震动提示",
							Toast.LENGTH_SHORT).show();
					Constants.NEWMESSAGENOTICEVIBRATE = 0;
					return;

				}
			}
		});
	}

	private void initBaseToggleButton() {

		newmeesageButton = (BaseToggleButton) findViewById(R.id.user_system_message_onoff);
		voiceButton = (BaseToggleButton) findViewById(R.id.user_system_voice_onoff);
		vibrationButton = (BaseToggleButton) findViewById(R.id.user_system_vire_onoff);

		SharedPreferences.Editor editor = getSharedPreferences("notice",
				MODE_PRIVATE).edit();
		if (Constants.NEWMESSAGENOTICE == 1) {
			newmeesageButton.setCheck(true);

		} else {
			newmeesageButton.setCheck(false);
		}
		if (Constants.NEWMESSAGENOTICEVIBRATE == 1) {
			vibrationButton.setCheck(true);

		} else {
			newmeesageButton.setCheck(false);
		}

		if (Constants.NEWMESSAGENOTICEVOICE == 1) {
			voiceButton.setCheck(true);
		} else {
			newmeesageButton.setCheck(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.b5:
			BaseDialogFragment confirmDialog = new BaseDialogFragment();
			confirmDialog.setMessage("确认退出账户吗?");
			confirmDialog.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							if (SessionUtils.loginUser.userType >= Constants.USER_TYPE_PADMIN
									&& SessionUtils.loginUser.userType < Constants.USER_TYPE_PADMIN + 10) {
								SessionUtils.cleanParkinfo(context);
							}
							SessionUtils.loginUser =null;
							SessionUtils.cleanuserinfo(context);
							finish();
						}
					});
			confirmDialog.setNegativeButton("取消", null);
			confirmDialog.show(getFragmentManager(), "");
			break;
		case R.id.update_password:
			ChangePassFragment dialog2 = new ChangePassFragment();
			dialog2.show(getFragmentManager(), "ChangePassFragment");
			break;

		}

	}

	private void fetchUpdateInfo() {
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
							.createDialog(UserInfoActivity.this);
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
			protected void onSuccess(final Object object) throws Exception {
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
			Toast.makeText(UserInfoActivity.this, "当前已经是最新版本",
					Toast.LENGTH_SHORT).show();

		}
	};
}
