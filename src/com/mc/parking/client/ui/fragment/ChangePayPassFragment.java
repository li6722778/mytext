package com.mc.parking.client.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.OperationCanceledException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mc.parking.client.R;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.CustomProgressDialog;
import com.mc.parking.client.ui.ParkActivity;
import com.mc.parking.client.utils.SafeAsyncTask;
import com.mc.parking.client.utils.SessionUtils;

public class ChangePayPassFragment extends DialogFragment
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
		View view = inflater.inflate(R.layout.fragment_changepaypass, null);
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
								
								if(change_password.getText().toString().equals(currentPass.getText().toString())){
									 Toast.makeText(getActivity(), "密码和当前一致,请重新输入",Toast.LENGTH_SHORT).show();
									 return;
								}
								
								if(!change_password.getText().toString().equals(change_password2.getText().toString())){
									 Toast.makeText(getActivity(), "两次密码输入不一致,请重新输入",Toast.LENGTH_SHORT).show();
									 return;
								}
								
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
										if (progressDialog == null){
								            progressDialog = CustomProgressDialog.createDialog(getActivity());
								            progressDialog.setMessage("正在修改支付密码,请等待...");
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
									protected void onSuccess(final Object userInfo)
											throws Exception {
										super.onSuccess(userInfo);
							           
							            loginhandler.sendEmptyMessage(0); 
							            progressDialog.clear();
									}
								}.execute();
								
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
           
            Toast.makeText(activity, "支付密码修改成功",Toast.LENGTH_SHORT).show();
           
        }};  
}
