package com.mc.parking.client.ui.fragment;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.OperationCanceledException;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.mc.park.client.R;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.CustomProgressDialog;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.ParkActivity;
import com.mc.parking.client.ui.admin.AddParkInfoDetailActivity;
import com.mc.parking.client.utils.SafeAsyncTask;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;

public class ChangePassFragment extends DialogFragment
{
	private EditText currentPass;
	private EditText change_password;
	private EditText change_password2;
	
	private CustomProgressDialog progressDialog;  
	Activity activity;

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.fragment_changeloginpass, null);
		currentPass = (EditText) view.findViewById(R.id.currpass);
		change_password = (EditText) view.findViewById(R.id.change_password);
		change_password2 = (EditText) view.findViewById(R.id.change_password2);
		activity = getActivity();
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
				// Add action buttons
				.setPositiveButton("提交",
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int id)
							{
								final TuserInfo userInfo = SessionUtils.loginUser;
								final String oldPass = userInfo.getPasswd();
								if (!UIUtils.validationNotEmpty(activity,"新密码", change_password)) {
									return;
								}
								
//								if(!currentPass.getText().toString().equals(oldPass)){
//									 Toast.makeText(getActivity(), "当前密码输入错误,请重新输入",Toast.LENGTH_SHORT).show();
//									 currentPass.requestFocus();
//									 return;
//								}
								
//								if(change_password.getText().toString().equals(currentPass.getText().toString())){
//									 Toast.makeText(getActivity(), "密码和当前一致,请重新输入",Toast.LENGTH_SHORT).show();
//									 change_password.requestFocus();
//									 return;
//								}
								
								if(!change_password.getText().toString().equals(change_password2.getText().toString())){
									 Toast.makeText(getActivity(), "两次密码输入不一致,请重新输入",Toast.LENGTH_SHORT).show();
									 change_password.requestFocus();
									 return;
								}
								
								
								if(userInfo!=null){
									userInfo.setPasswd(change_password.getText().toString());
									userInfo.setUpdatePerson(currentPass.getText().toString());
									HttpRequestAni<ComResponse<TuserInfo>> httpRequestAni = new HttpRequestAni<ComResponse<TuserInfo>>(
											activity, "/a/user/changepw", new TypeToken<ComResponse<TuserInfo>>() {}.getType(),userInfo,TuserInfo.class) {
	
										@Override
										public void callback(ComResponse<TuserInfo> arg0) {
											if(arg0.getResponseStatus()==ComResponse.STATUS_OK){
												TuserInfo userInfo=arg0.getResponseEntity();  
											    loginhandler.sendEmptyMessage(0); 
											    Toast.makeText(activity, "密码修改成功",Toast.LENGTH_SHORT).show();
											}else{
												Toast.makeText(activity, "[错误]"+arg0.getErrorMessage(), Toast.LENGTH_SHORT)
												.show();
											}
	
										}
										
										@Override
										public Map<String, String> getHeaders() {
											HashMap<String, String> params = new HashMap<String, String>();
											String creds = String.format("%s:%s", userInfo.getUserPhone(),
													oldPass);
											String auth = "Basic "
													+ Base64.encodeToString(creds.getBytes(),
															Base64.DEFAULT);
											params.put("Authorization", auth);
											return params;
										}
										
									};
	
									httpRequestAni.execute();
								}

							}
						}).setNegativeButton("取消", null);
		return builder.create();
	}

	private Handler loginhandler = new Handler(){  
		  
        @Override  
        public void handleMessage(Message msg) {  
            //关闭ProgressDialog  
        	if (progressDialog != null){
                progressDialog.dismiss();
                progressDialog = null;
            }
           
            Toast.makeText(activity, "登录密码修改成功",Toast.LENGTH_SHORT).show();
           
        }};  
}
