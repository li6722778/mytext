package com.mc.parking.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushManager;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.client.Constants;
import com.mc.parking.client.PackingApplication;
import com.mc.park.client.R;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.BaseActivity;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.admin.AddParkInfoDetailActivity;
import com.mc.parking.client.ui.admin.AddParkInfoHistoryActivity;
import com.mc.parking.client.ui.admin.AdminHomeActivity;
import com.mc.parking.client.ui.fragment.FindPayPassFragment;
import com.mc.parking.client.ui.fragment.ParklistDialogFragment;
import com.mc.parking.client.utils.Log;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;

public class LoginActivity extends BaseActivity {

	public static final int parent_userinfo = 1;
	public static final int parent_oderinfo = 2;
	public static final int parent_walletinfo = 3;
	public static final int parent_couponinfo = 4;
	private int currentParent;
	Message msg;
	private Context context;

	public Handler loginhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (msg != null) {

				if (msg.arg1>=Constants.USER_TYPE_PADMIN&&msg.arg1<Constants.USER_TYPE_PADMIN+10) {// 车位管理员
					Toast.makeText(LoginActivity.this, "登录成功,您是车位管理员",
							Toast.LENGTH_SHORT).show();
					if(SessionUtils.loginUser!=null){
						if(SessionUtils.loginUser.userType>=Constants.USER_TYPE_PADMIN && SessionUtils.loginUser.userType < Constants.USER_TYPE_PADMIN + 10)
						{
						String Clientid= PushManager.getInstance().getClientid(getApplicationContext());
						UIUtils.getclientId(SessionUtils.loginUser.userid, Clientid);
						}
					}
					finish();
					return;
				}

				if (msg.arg1 >= Constants.USER_TYPE_MADMIN&&msg.arg1<Constants.USER_TYPE_MADMIN+10) {// 车位管理员
					Toast.makeText(LoginActivity.this, "登录成功,您是数据采集员",
							Toast.LENGTH_SHORT).show();
					Bimp.tempTParkInfo = new TParkInfoEntity();
					Bimp.tempTParkImageList.clear();
					Bimp.tempTParkLocList.clear();
					Bimp.ADDPARK_VIEW_MODE = AddParkInfoHistoryActivity.FROM_TYPE_NEW;
					finish();
					return;
				}
				else{
					String Clientid= PushManager.getInstance().getClientid(getApplicationContext());
					UIUtils.getclientId(SessionUtils.loginUser.userid, Clientid);
					Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT)
							.show();
					finish();
					return;
					
					}

			}
			
			switch (currentParent) {
			case parent_userinfo:
				Intent intentUser = new Intent(LoginActivity.this,
						UserInfoActivity.class);
				startActivity(intentUser);
				finish();
				break;
			case parent_oderinfo:
				Intent intentOrder = new Intent(LoginActivity.this,
						OrderActivity.class);
				startActivity(intentOrder);
				finish();
				break;

			case parent_couponinfo:
				Intent intentcoupon = new Intent(LoginActivity.this,
						CouponDetailActivity.class);
				startActivity(intentcoupon);
				finish();
				break;
			default:
				finish();
			}

		}
	};

	public void backTo(View v) {
		finish();
	}

	/**
	 * desc:保存对象
	 * 
	 * @param context
	 * @param key
	 * @param obj
	 *            要保存的对象，只能保存实现了serializable的对象 modified:
	 */

	public void forgetPasswd(View v) {
		FindPayPassFragment dialog4 = new FindPayPassFragment(
				FindPayPassFragment.TYPE_LOGIN);
		dialog4.show(getFragmentManager(), "FindPayPassFragment");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_login);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_topbar);
		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		titleView.setText(R.string.loginStr);
		currentParent = 0;
		context = PackingApplication.getInstance();

		Object parent = getIntent().getExtras().get("parent");
		if (parent != null) {
			currentParent = (int) parent;
		}
	

		final EditText userid = (EditText) findViewById(R.id.edit_userid);
		final EditText passsword = (EditText) findViewById(R.id.edit_password);

		Button login_submit = (Button) findViewById(R.id.login_submit);
		login_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (!UIUtils.validationNotEmpty(LoginActivity.this, "手机号码",
						userid)) {
					return;
				}
				if (!UIUtils.validationNotEmpty(LoginActivity.this, "密码",
						passsword)) {
					return;
				}
				// new
				// LoginTask(LoginActivity.this,loginhandler,userid.getText().toString(),passsword.getText().toString()).execute();

				HttpRequestAni<TuserInfo> httpRequestAni = new HttpRequestAni<TuserInfo>(
						LoginActivity.this, "/a/login/" + userid.getText()+"?currentVersion="+Constants.VERSION+"&os=android",
						new TypeToken<TuserInfo>() {
						}.getType()) {
					@Override
					public void callback(TuserInfo arg0) {
						msg = new Message();
						msg.arg1 = arg0.userType;
						SessionUtils.loginUser = arg0;
						SessionUtils.saveoUserinfo(context);

						if (SessionUtils.loginUser.userType >= Constants.USER_TYPE_PADMIN&& SessionUtils.loginUser.userType < Constants.USER_TYPE_PADMIN + 10) {
							HttpRequestAni<List<TParkInfoEntity>> httpRequestAni = new HttpRequestAni<List<TParkInfoEntity>>(
									LoginActivity.this, "/a/user/adm/"+ arg0.userid,
									new TypeToken<List<TParkInfoEntity>>() {
									}.getType()) {
								
								
								@Override
								public void callback(List<TParkInfoEntity> arg0) {
									if (arg0 != null && arg0.size() > 0) {
										if (arg0.size() == 1) {
											SessionUtils.loginUser.parkInfoAdm = arg0.get(0);
											SessionUtils.savechoiceparkInfoAdm(context);
											loginhandler.sendMessage(msg);
										} else {
											List<TParkInfoEntity> parkList = new ArrayList<TParkInfoEntity>();
											// 这里应该弹出一个popup，让用户来选择他想操作哪一个停车场??
											for (int i = 0; i < arg0.size(); i++) {
												parkList.add(arg0.get(i));
											}
											if (parkList != null&& parkList.size() > 0) {
												ParklistDialogFragment dialog = new ParklistDialogFragment(parkList){

													@Override
													public void setchoice(
															TParkInfoEntity parkInfoEntity) {
														if(parkInfoEntity!=null)
														{
															SessionUtils.loginUser.parkInfoAdm=parkInfoEntity;
															SessionUtils.savechoiceparkInfoAdm(context);
															loginhandler.sendMessage(msg);
														}
														
													}
													
													
												};
												dialog.show(getFragmentManager(),"parklistdialog");
												// 弹出完毕
											}
										}

									} else {
										loginhandler.sendEmptyMessage(0);
									}
								}
							};
							httpRequestAni.execute();
						} else {
							loginhandler.sendMessage(msg);
						}

					}

					@Override
					public Map<String, String> getHeaders() {
						HashMap<String, String> params = new HashMap<String, String>();
						String creds = String.format("%s:%s", userid.getText(),
								passsword.getText());
						String auth = "Basic "
								+ Base64.encodeToString(creds.getBytes(),
										Base64.DEFAULT);
						params.put("Authorization", auth);
						return params;
					}

				};

				httpRequestAni.execute();

			}
		});

		Button login_reg = (Button) findViewById(R.id.login_reg);
		login_reg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginActivity.this,
						RegActivity.class);
				if (currentParent > 0)
					intent.putExtra("parent", currentParent);
				    intent.putExtra("userphone", userid.getText());
				startActivity(intent);
				finish();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		this.setResult(resultCode, data);
		finish();
	}

	

}
