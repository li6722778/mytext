package com.mc.parking.client.ui;

import java.util.Date;

import javax.security.auth.PrivateCredentialPermission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.igexin.sdk.PushManager;
import com.mc.parking.client.Constants;
import com.mc.parking.client.PackingApplication;
import com.mc.park.client.R;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.ActionBaseActivity;
import com.mc.parking.client.utils.DateHelper;
import com.mc.parking.client.utils.Log;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;
public class SplashActivity extends ActionBaseActivity {
	boolean isFirstIn = false;
	boolean  condition1 ,condition2;
	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	private static final long SPLASH_DELAY_MILLIS = 1000;
	private static final String SHAREDPREFERENCES_NAME = "first_pref";
	private Context context;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				goHome();
				break;
			case GO_GUIDE:
				goGuide();
				break;
			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_splashactivity);
		context = PackingApplication.getInstance();
		TextView version_text = (TextView) findViewById(R.id.version_text);
		String message = "³ÌÐò°æ±¾:"+Constants.VERSION;
		if(Constants.HTTP.startsWith("http://192.168")){
			message+="[ÄÚ²¿²âÊÔ°æ]";
		}
		version_text.setText(message);
		
		PushManager.getInstance().initialize(this.getApplicationContext());
		init();
	
	}

	private void init() {

		/* read cache */
		Log.i("SplashActivity", "::::::::::start to get cache,path:");

		try {
			SharedPreferences _cacheFile = getSharedPreferences(
					Constants.CACHE_PROP, MODE_PRIVATE);
			if (_cacheFile != null) {
				SessionUtils.city = _cacheFile.getString(
						PackingApplication.CACHE_KEY_CITY,
						Constants.CITY_DEFAULT);
				SessionUtils.address = _cacheFile.getString(
						PackingApplication.CACHE_KEY_ADDRESS, "");
				SessionUtils.cityCode = _cacheFile.getInt(
						PackingApplication.CACHE_KEY_CITYCODE,
						Constants.CITY_DEFAULT_CODE);
				String lastTimeLantitude = _cacheFile.getString(
						PackingApplication.CACHE_KEY_lastTimeLantitude, "0");
				;
				try {
					if (lastTimeLantitude != null)
						SessionUtils.lastTimeLantitude = Double
								.parseDouble(lastTimeLantitude);
				} catch (Exception e) {
					Log.e("SplashActivity", "error", e);
				}
				String lastTimeLongitude = _cacheFile.getString(
						PackingApplication.CACHE_KEY_lastTimeLongitude, "0");
				;
				try {
					if (lastTimeLongitude != null)
						SessionUtils.lastTimeLongitude = Double
								.parseDouble(lastTimeLongitude);
				} catch (Exception e) {
					Log.e("SplashActivity", "error", e);
				}
			} else {
				SessionUtils.city = Constants.CITY_DEFAULT;
				SessionUtils.cityCode = Constants.CITY_DEFAULT_CODE;
			}

			Log.d("PackingApplication", ":::::::::: city:" + SessionUtils.city);
			Log.d("PackingApplication", ":::::::::: citycode:"
					+ SessionUtils.cityCode);
			Log.d("PackingApplication", ":::::::::: address:"
					+ SessionUtils.address);
			Log.d("PackingApplication", ":::::::::: lastTimeLantitude:"
					+ SessionUtils.lastTimeLantitude);
			Log.d("PackingApplication", ":::::::::: lastTimeLongitude:"
					+ SessionUtils.lastTimeLongitude);
		} catch (Exception e) {
			Log.e("SplashActivity", "error", e);
			SessionUtils.city = Constants.CITY_DEFAULT;
			SessionUtils.cityCode = Constants.CITY_DEFAULT_CODE;
		}

		
		getversion();

		TuserInfo userInfo = (TuserInfo) SessionUtils.readUserinfo(context,
				"userinfo");

		if (userInfo != null) {
			SharedPreferences sharedata = getSharedPreferences("user",
					MODE_PRIVATE);
			Long date = sharedata.getLong("time", 0);
			Date recorddate = new Date(date);
			Date nowDate = new Date();
			int resultday = DateHelper.diffDate(nowDate, recorddate);
			if (resultday <= Constants.loginperiod) {
				SessionUtils.loginUser = userInfo;
				if (SessionUtils.loginUser.userType >= Constants.USER_TYPE_PADMIN
						&& SessionUtils.loginUser.userType < Constants.USER_TYPE_PADMIN + 10) {
					TParkInfoEntity tParkInfoEntity = (TParkInfoEntity) SessionUtils
							.readchoiceparkInfoAdm(context, "parkinfo");
					SessionUtils.loginUser.parkInfoAdm = tParkInfoEntity;
				}
			}

		}
		
		initconfig();
		
		
		if(SessionUtils.loginUser!=null){
			//Toast.makeText(context,"SessionUtils.loginUser.userType:"+SessionUtils.loginUser.userType, Toast.LENGTH_SHORT).show();
		if(SessionUtils.loginUser.userType>=Constants.USER_TYPE_PADMIN && SessionUtils.loginUser.userType < (Constants.USER_TYPE_PADMIN + 10))
		{
			String Clientid= PushManager.getInstance().getClientid(getApplicationContext());
			UIUtils.getclientId(SessionUtils.loginUser.userid, Clientid);
		}
		
	}
	}

	private void initconfig() {
		SharedPreferences config = context.getSharedPreferences("notice", 0);
		int newmessage = config.getInt("notices", 1);
		int voice = config.getInt("voice", 1);
		int vibrate = config.getInt("vibrate", 1);
		Constants.NEWMESSAGENOTICE=newmessage;
		Constants.NEWMESSAGENOTICEVOICE=voice;
		Constants.NEWMESSAGENOTICEVIBRATE=vibrate;	
	}

	private void goGuide() {
/*		// TODO Auto-generated method stub
		if(checkNetworkAvailable(context)){
			Intent intent = new Intent(SplashActivity.this, WebViewActivity.class);
			startActivity(intent);
			finish();
		}
		
		else{
		Intent intent = new Intent(SplashActivity.this, GuideAcitivity.class);
		startActivity(intent);
		finish();}*/
		Intent intent = new Intent(SplashActivity.this, GuideAcitivity.class);
		startActivity(intent);
		finish();
	}

	private void goHome() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(SplashActivity.this, IndexActivity.class);
		startActivity(intent);
		finish();
	}
	
	
	

	// ¼ì²âÍøÂç
		public static boolean checkNetworkAvailable(Context context) {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity == null) {
				return false;
			} else {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							NetworkInfo netWorkInfo = info[i];
							if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
								return true;
							} else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
								return true;
							}
						}
					}
				}
			}

			return false;

		}
	
	
	
	
	public void  getversion()
	{
		
		 
		SharedPreferences preferencess= getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);
		condition1 = preferencess.getBoolean("isFirstIn", true);
		SharedPreferences preferences = getSharedPreferences("version", MODE_PRIVATE);
		long versionid = preferences.getLong("versionid", 0);
		if(versionid!=0&&versionid!=Constants.VERSION)
		{
			condition2=true;
		}
		else {
			condition2=false;
		}

		if(condition1==true||condition2==true)
		{
			isFirstIn=true;
		}
		
		
		
		
		if (!isFirstIn) {
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		} else {
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
		}
		
		
	}

	

}
