package com.mc.parking.client.ui.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.OperationCanceledException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.mc.addpic.utils.Bimp;
import com.mc.park.client.R;
import com.mc.parking.client.entity.OrderEntity;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.layout.CustomProgressDialog;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.utils.SafeAsyncTask;

public class AlterDialogFragment extends DialogFragment
{
	private EditText cheweiNumber;
	
	private CustomProgressDialog progressDialog;  
	private Activity activity;
	

	

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.fragment_order_checkout, null);
		cheweiNumber = (EditText) view.findViewById(R.id.edit_cheweinumber);
		cheweiNumber.setFocusableInTouchMode(true);
		cheweiNumber.requestFocus();
		
		activity =getActivity();
	
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
				// Add action buttons
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int id)
							{
								
								if(Bimp.temporder==null)
								{
//									Toast.makeText(getActivity(),
//											"放行失败，请重试\r\n",
//											Toast.LENGTH_SHORT).show();
									return;
									
								}
								if(cheweiNumber.getText().toString().trim().equals(""))
								{
									Toast.makeText(activity, "放行失败,请输入正确金额", Toast.LENGTH_LONG).show();
									return;
								}
								
								Bimp.temporder.setOrderStatus(2);
								
								
								
								HttpRequestAni<ComResponse<OrderEntity>> httpRequestAni = new HttpRequestAni<ComResponse<OrderEntity>>(
										getActivity(), "/a/order/save",
										new TypeToken<ComResponse<OrderEntity>>() {
										}.getType(), Bimp.temporder, OrderEntity.class) {

											@Override
											public void callback(ComResponse<OrderEntity> arg0) {
												// TODO Auto-generated method stub
												
												

												if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {
													Bimp.temporder = arg0.getResponseEntity();								
//													Toast.makeText(getActivity(),
//															"放行成功\r\n",
//															Toast.LENGTH_SHORT).show();
													if(activity instanceof AdminHomeActivity)
													{
														AdminHomeActivity curractity=(AdminHomeActivity) activity;
														curractity.refreshfragment();
														
														
													}
													Toast.makeText(activity, "放行成功", Toast.LENGTH_LONG).show();

												} else {

//													Toast.makeText(getActivity(),
//															"放行失败，请重试\r\n" + arg0.getErrorMessage(),
//															Toast.LENGTH_SHORT).show();
												}
											
												Bimp.temporder=null;
											}};

								httpRequestAni.execute();
								
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
        	Toast.makeText(activity, "车位发布成功",Toast.LENGTH_SHORT).show();
        	
        
        }};  
}
