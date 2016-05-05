package com.mc.parking.client.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.mc.park.client.R;
import com.mc.parking.client.entity.TOptions;
import com.mc.parking.client.layout.net.HttpRequest;

public class Reg_noticeActivity extends Activity {

	private TextView regnotice_context;
	private Button regbutton, cancelbutton;
	private CheckBox ischeck;
	private boolean checkstatue;

	private  int ok=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_reg_notice);
		regnotice_context = (TextView) findViewById(R.id.reg_noticetext);
		regbutton = (Button) findViewById(R.id.reg_notice_ok);
		cancelbutton = (Button) findViewById(R.id.reg_notice_cancel);
		ischeck = (CheckBox) findViewById(R.id.reg_check);

		
		ischeck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					checkstatue = true;
				} else {
					checkstatue = false;
				}
			}
		});
		
		regbutton.setOnClickListener(new  OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(checkstatue==false)
				{
					Toast.makeText(Reg_noticeActivity.this, "请先勾选同意协议", Toast.LENGTH_SHORT).show();
					return;
				}
				else {
					setResult(ok);
					finish();
				}
			}
		});
		cancelbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});

		//load protocol for reg
		getRegProtocol();
	}
	
	
	public void getRegProtocol() {
		HttpRequest<TOptions> httpRequest = new HttpRequest<TOptions>(
				"/a/reg/protocol", TOptions.class) {
			@Override
			public void onSuccess(TOptions arg0) {
			  if(arg0!=null){
				  String context = arg0.longTextObject;
				  if(context==null||context.trim().equals("")){
					  context = arg0.textObject;
				  }
				  regnotice_context.setText(context);
			  }
			}

			@Override
			public void onFailed(String message) {
			}

		};

		httpRequest.execute();

	}

}
