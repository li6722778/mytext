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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushManager;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.client.Constants;
import com.mc.parking.client.PackingApplication;
import com.mc.parking.client.R;
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

public class AdsWebActivity extends BaseActivity {

	public static final int parent_userinfo = 1;
	public static final int parent_oderinfo = 2;
	public static final int parent_walletinfo = 3;
	public static final int parent_couponinfo = 4;
	private int currentParent;
	Message msg;
	private Context context;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adsmainactivity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_topbar);
		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		if(getIntent().getStringExtra("title")!=null)
		titleView.setText(getIntent().getStringExtra("title").toString());
		else
			titleView.setText("车伯乐");
		WebView webview=(WebView) findViewById(R.id.webview);
		
		 webview.setWebViewClient(new WebViewClient(){
             @Override
             public boolean shouldOverrideUrlLoading(WebView view, String url){
                      
                     return false;
                      
             }
     });
		webview.loadUrl(getIntent().getStringExtra("url"));
	}


}
