package com.mc.parking.client.ui.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.OperationCanceledException;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mc.park.client.R;
import com.mc.parking.client.layout.CustomProgressDialog;
import com.mc.parking.client.utils.SafeAsyncTask;

public class CheweiReduceDialogFragment extends DialogFragment
{
	private EditText cheweiNumber;
	
	private CustomProgressDialog progressDialog;  
	private Activity activity;
	private TextView cheweiView;
	
	public CheweiReduceDialogFragment(TextView cheweiView){
		this.cheweiView = cheweiView;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.fragment_admin_chewei_reduce, null);
		cheweiNumber = (EditText) view.findViewById(R.id.edit_cheweinumber);
		activity =getActivity();
	
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
				// Add action buttons
				.setPositiveButton("减少",
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int id)
							{
								
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
								            progressDialog.setMessage("正在调整,请等待...");
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
									protected void onSuccess(final Object obj)
											throws Exception {
										super.onSuccess(obj);
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
        	Toast.makeText(activity, "车位调整成功",Toast.LENGTH_SHORT).show();
        	String cheweiObj = cheweiView.getText().toString();
        	try{
        		int cheweiInt = Integer.parseInt(cheweiObj);
        		int total = cheweiInt - Integer.parseInt(cheweiNumber.getText().toString());
        		total = total<0?0:total;
        		cheweiView.setText(total+"");
        	}catch(Exception e){
        		Log.e("", e.getMessage(),e);
        	}
        }};  
}
