package com.mc.parking.client.ui;

import com.mc.park.client.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class WebViewActivity extends Activity {
	private WebView myWebView;
	 Activity mActivity = WebViewActivity.this;
		private static final String SHAREDPREFERENCES_NAME = "first_pref";

	@SuppressLint("SetJavaScriptEnabled") 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
	             

		myWebView = (WebView) findViewById(R.id.myWebView);
	
		myWebView.getSettings().setJavaScriptEnabled(true);
		
		class JavaScriptinterface {
		
			@JavascriptInterface 
			/** 与js交互时用到的方法，在js里直接调用的 */
		    
			public void startActivity() {
				
				
				
				setGuide();
				geHome();
				
			}
		}
		// 与js交互，JavaScriptinterface 是个接口，与js交互时用到的，这个接口实现了从网页跳到app中的activity 的方法，特别重要
		myWebView.addJavascriptInterface(new JavaScriptinterface(), "android");
		myWebView.loadUrl("file:///android_asset/index.html");

	}
	
	protected void geHome() {
		// TODO Auto-generated method
		Intent intent = new Intent(mActivity, IndexActivity.class);  
		mActivity.startActivity(intent);  
		mActivity.finish();  
	}

	protected void setGuide() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = mActivity.getSharedPreferences(
				SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		
		Editor editor = preferences.edit();

		editor.putBoolean("isFirstIn", false);
		editor.commit();
	}
	
	
}