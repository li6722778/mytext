package com.mc.parking.client.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushManager;
import com.mc.parking.client.Constants;
import com.mc.parking.client.PackingApplication;
import com.mc.park.client.R;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.CustomProgressDialog;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.MainActivity;
import com.mc.parking.client.ui.ParkActivity;
import com.mc.parking.client.ui.RegActivity;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;

public class LoginDialogFragmentv4 extends DialogFragment {
	private EditText mUsername;
	private EditText mPassword;

	private CustomProgressDialog progressDialog;
	private ParkActivity parkActivity;
	private TParkInfo_LocEntity tParkInfo_LocEntity;
	private Activity activity;
	Message msg;
	private Context context;
	android.support.v4.app.FragmentManager fManager;

	public interface LoginInputListener {
		void onLoginInputComplete();

	}

	public LoginDialogFragmentv4(Activity activity,
			TParkInfo_LocEntity tParkInfo_LocEntity) {
		this.tParkInfo_LocEntity = tParkInfo_LocEntity;
		this.activity = activity;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.fragment_login_dialog, null);
		mUsername = (EditText) view.findViewById(R.id.edit_userid);
		mPassword = (EditText) view.findViewById(R.id.edit_password);
		fManager = getFragmentManager();
		parkActivity = (ParkActivity) getActivity();
		context = PackingApplication.getInstance();
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
				// Add action buttons
				.setPositiveButton("登录", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						if (!UIUtils.validationNotEmpty(activity, "手机号码",
								mUsername)) {
							return;
						}
						if (!UIUtils.validationNotEmpty(activity, "密码",
								mPassword)) {
							return;
						}
						HttpRequestAni<TuserInfo> httpRequestAni = new HttpRequestAni<TuserInfo>(
								activity, "/a/login/"
										+ mUsername.getText().toString()+"?currentVersion="+Constants.VERSION+"&os=android",
								new TypeToken<TuserInfo>() {
								}.getType()) {
							@Override
							public void callback(TuserInfo arg0) {
								msg = new Message();
								msg.arg1 = arg0.userType;
								SessionUtils.loginUser = arg0;
								SessionUtils.saveoUserinfo(context);

								if (SessionUtils.loginUser.userType >= Constants.USER_TYPE_PADMIN
										&& SessionUtils.loginUser.userType < Constants.USER_TYPE_PADMIN + 10) {
									HttpRequestAni<List<TParkInfoEntity>> httpRequestAni = new HttpRequestAni<List<TParkInfoEntity>>(
											parkActivity,
											"/a/user/adm/" + arg0.userid,
											new TypeToken<List<TParkInfoEntity>>() {
											}.getType()) {
										@Override
										public void callback(
												List<TParkInfoEntity> arg0) {
											if (arg0 != null && arg0.size() > 0) {
												if (arg0.size() == 1) {
													SessionUtils.loginUser.parkInfoAdm = arg0
															.get(0);
													SessionUtils
															.savechoiceparkInfoAdm(context);
													loginhandler
															.sendMessage(msg);
												} else {
													List<TParkInfoEntity> parkList = new ArrayList<TParkInfoEntity>();
													// 这里应该弹出一个popup，让用户来选择他想操作哪一个停车场??
													for (int i = 0; i < arg0
															.size(); i++) {
														parkList.add(arg0
																.get(i));
													}
													if (parkList != null
															&& parkList.size() > 0) {
														ParklistDialogFragmentv4 dialog = new ParklistDialogFragmentv4(
																parkList) {

															@Override
															public void setchoice(
																	TParkInfoEntity parkInfoEntity) {

																if (parkInfoEntity != null) {
																	SessionUtils.loginUser.parkInfoAdm = parkInfoEntity;
																	SessionUtils
																			.savechoiceparkInfoAdm(context);
																	loginhandler.sendMessage(msg);
																}
															}
														};
														dialog.show(fManager,
																"parklistdialog");
														// 弹出完毕
													}
												}

											} else {
												loginhandler
														.sendEmptyMessage(0);
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
								String creds = String.format("%s:%s", mUsername
										.getText().toString(), mPassword
										.getText().toString());
								String auth = "Basic "
										+ Base64.encodeToString(
												creds.getBytes(),
												Base64.DEFAULT);
								params.put("Authorization", auth);
								return params;
							}

						};

						httpRequestAni.execute();

					}
				}).setNegativeButton("取消", null)
				.setNeutralButton("注册", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Intent intent = new Intent(activity, RegActivity.class);
						Bundle buidle = new Bundle();
						buidle.putSerializable("parkinfo", tParkInfo_LocEntity);
						intent.putExtras(buidle);
						intent.putExtra("userphone", mUsername.getText()
								.toString());
						startActivityForResult(intent, 1);
					}
				});
		return builder.create();
	}

	private Handler loginhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// 关闭ProgressDialog
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (parkActivity != null) {
				Toast.makeText(parkActivity, "登录成功", Toast.LENGTH_SHORT).show();
				if(SessionUtils.loginUser!=null){
					if(SessionUtils.loginUser.userType>=Constants.USER_TYPE_PADMIN&& SessionUtils.loginUser.userType < Constants.USER_TYPE_PADMIN + 10)
					{
					String Clientid= PushManager.getInstance().getClientid(PackingApplication.getInstance());
					UIUtils.getclientId(SessionUtils.loginUser.userid, Clientid);
					}
				}

				if (activity instanceof MainActivity) {
					MainActivity mainActivity = (MainActivity) activity;
					mainActivity.mapFragment.gotoPage();
				} else if (activity instanceof ParkActivity) {
					ParkActivity mainActivity = (ParkActivity) activity;
					((ParkActivity) activity).gotoPage();
				}

			}
		}
	};

}
