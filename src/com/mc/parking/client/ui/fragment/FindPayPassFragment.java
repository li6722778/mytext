package com.mc.parking.client.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

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
import com.mc.parking.client.Constants;
import com.mc.park.client.R;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.CustomProgressDialog;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.LoginActivity;
import com.mc.parking.client.utils.SafeAsyncTask;
import com.mc.parking.client.utils.SessionUtils;

public class FindPayPassFragment extends DialogFragment {
	private EditText email;

	private CustomProgressDialog progressDialog;
	Activity activity;

	public static final int TYPE_PAY = 1;
	public static final int TYPE_LOGIN = 2;

	private int currentType;

	public FindPayPassFragment(int type) {
		currentType = type;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.fragment_findpasswd, null);
		email = (EditText) view.findViewById(R.id.email);
		activity = getActivity();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
		// Add action buttons
				.setPositiveButton("找回", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {

						if (email.getText().toString().equals("")) {
							Toast.makeText(getActivity(), "手机号不能为空",
									Toast.LENGTH_SHORT).show();
							return;
						}

						HttpRequestAni<String> httpRequestAni = new HttpRequestAni<String>(
								activity,"/a/reg/sendreset/"+email.getText().toString().trim()+"?t=1",
								new TypeToken<String>() {
								}.getType(),null,String.class) {
							@Override
							public void callback(String arg0) {
								Toast.makeText(activity, ""+arg0, Toast.LENGTH_SHORT).show();
							}
						};
						httpRequestAni.execute();
						
					}
				}).setNegativeButton("取消", null);
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

			Toast.makeText(activity, "密码找回成功,请注意查收", Toast.LENGTH_SHORT).show();

		}
	};
}
