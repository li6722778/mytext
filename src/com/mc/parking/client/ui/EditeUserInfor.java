package com.mc.parking.client.ui;

import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;
import com.mc.addpic.utils.Bimp;
import com.mc.park.client.R;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.BaseActivity;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.HttpRequestAni;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EditeUserInfor extends BaseActivity{
	
	
	TextView myname,mytele,mycarnum;
	Button save_submit;
	TuserInfo userinfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.ac_userinfodetail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_topbar);
		 userinfo=(TuserInfo) getIntent().getSerializableExtra("info");
		
		mycarnum=(TextView) findViewById(R.id.mycarnum);
		mytele=(TextView) findViewById(R.id.mytele);
		myname=(TextView) findViewById(R.id.myname);
		save_submit=(Button) findViewById(R.id.save_submit);
		if(userinfo!=null)
		{
			
		}
		save_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				submitData();
				//Toast.makeText(getApplicationContext(), "ºÇºÇ", Toast.LENGTH_SHORT).show();
			}
		});
		if(userinfo!=null)
		BindData(userinfo);
	}
	
	public void backTo(View v) {
		finish();
	}
	
	private void BindData(TuserInfo userinfo)
	{
		mycarnum.setText(userinfo.email);
		mytele.setText(""+userinfo.userPhone);
		myname.setText(userinfo.userName);
		
		
		
	}
	
	private void submitData()
	{
		if(userinfo==null)
		{
			Toast.makeText(getApplicationContext(), "ºÇºÇ", Toast.LENGTH_SHORT).show();
			return;
		}
		userinfo.setEmail(mycarnum.getText().toString());
		userinfo.setUserName(myname.getText().toString());
		
		HttpRequestAni<ComResponse<TuserInfo>> httprequest=new HttpRequestAni<ComResponse<TuserInfo>>(
				this, "/a/user/save",
				new TypeToken<ComResponse<TuserInfo>>() {
				}.getType(), userinfo, TuserInfo.class){
			
			@Override
			public void callback(ComResponse<TuserInfo> arg0) {
				// TODO Auto-generated method stub
				if(arg0!=null)
				{
					
					Toast.makeText(getApplicationContext(), "³É¹¦", Toast.LENGTH_SHORT).show();
				}
			}
		};
		httprequest.execute();
		
	}
	
	
}
