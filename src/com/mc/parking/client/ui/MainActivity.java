package com.mc.parking.client.ui;

import java.net.URLEncoder;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.baidu.navisdk.BaiduNaviManager;
import com.google.gson.reflect.TypeToken;
import com.mc.parking.client.Constants;
import com.mc.parking.client.PackingApplication;
import com.mc.park.client.R;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.entity.VersionEntity;
import com.mc.parking.client.layout.BaseDialogFragment;
import com.mc.parking.client.layout.net.HttpRequest;
import com.mc.parking.client.ui.admin.CheweiFragment;
import com.mc.parking.client.ui.admin.CollectMainFragment;
import com.mc.parking.client.ui.fragment.MapFragment;
import com.mc.parking.client.ui.fragment.TopBarFragment;
import com.mc.parking.client.utils.Log;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;
import com.mc.parking.client.utils.WXshareutil;
import com.mc.parking.receiver.NetState;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MainActivity extends FragmentActivity {
	protected static final String TAG = "MainActivity";
	/**
	 * 地图控件
	 */
	public MapFragment mapFragment = null;
	public TopBarFragment topBarFragment = null;
	public Object communicationData;
	public CheweiFragment cheweiFragment = null;
	public CollectMainFragment collectmain = null;
	public IWXAPI api;
	// 广播监听网络
	NetState mReceiver = new NetState();
	IntentFilter mFilter = new IntentFilter();

	public void moveToNewLocation(double lantitude, double longitude) {
		if (mapFragment != null) {
			Log.d("MainActivity", "move to lantitude:" + lantitude
					+ ",longitude:" + longitude);
			UIUtils.myserchlatlng=new LatLng(lantitude, longitude);
			mapFragment.sreachNearbyParkingNoMove(lantitude, longitude,1);
			mapFragment.showbackserch();
		}
	}

	public static boolean mIsEngineInitSuccess = false;
	private NaviEngineInitListener mNaviEngineInitListener = new NaviEngineInitListener() {
		public void engineInitSuccess() {
			// 导航初始化是异步的，需要一小段时间，以这个标志来识别引擎是否初始化成功，为true时候才能发起导航
			mIsEngineInitSuccess = true;
		}

		public void engineInitStart() {

		}

		public void engineInitFail() {
			mIsEngineInitSuccess = false;
		}
	};

	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(
				Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 导航初始化
		BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(),
				mNaviEngineInitListener, null);
		setContentView(R.layout.activity_main);
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
		api.registerApp(Constants.APP_ID);
		FragmentManager manager = getFragmentManager();
		topBarFragment = (TopBarFragment) manager
				.findFragmentById(R.id.fragment_topbar);
		mapFragment = (MapFragment) manager.findFragmentById(R.id.fragment_map);
		topBarFragment.initSlidingMenu();
		checkVersion();
		PackingApplication.getInstance().setMainActivity(this);

	}

	public void openmaplist() {
		if (mapFragment != null) {
			mapFragment.getMapListData();

		}

	}

	private void checkVersion() {
		
		long userid = 0;
		int userType = Constants.USER_TYPE_NORMAL;
		String userCity = "";
		long clientVersion = Constants.VERSION;
		String os = "android";
		
		if(SessionUtils.loginUser!=null){
			
			userid = SessionUtils.loginUser.userid;
			userType = SessionUtils.loginUser.userType;
			try{
			    userCity = URLEncoder.encode(SessionUtils.city, "UTF-8");
			}catch(Exception e){
				userCity =  ""+SessionUtils.cityCode;
			}
			
		}
		
		HttpRequest<VersionEntity> httpRequestAni = new HttpRequest<VersionEntity>(
				"/a/version?userid="+userid+"&userType="+userType+"&userCity="+userCity+"&currentVersion="+clientVersion+"&os="+os+"", new TypeToken<VersionEntity>() {
				}.getType()) {
			@Override
			public void onSuccess(VersionEntity arg0) {
				Message message = new Message();
				if (arg0 != null && arg0.version != null) {
					long version = arg0.version;

					if (Constants.VERSION < version && arg0.forceUpdate == 1) {
						message.arg1 = 2;
					} else if (Constants.VERSION < version
							&& arg0.forceUpdate == 0) {
						message.arg1 = 1;
					} else if (Constants.VERSION < version&&arg0.forceUpdate == 3) {
						message.arg1 = 3;
					}
					message.obj = arg0;
				}
				versionHandler.sendMessage(message);
			}

			@Override
			public void onFailed(String message) {
				Log.e("checkVersion", message);
				Message messageObj = new Message();
				versionHandler.sendMessage(messageObj);
			}

		};

		httpRequestAni.execute();
	}

	private Handler versionHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (msg.arg1 == 1) {
				VersionEntity version = (VersionEntity) msg.obj;
				String message = version.updatesContent;
				final String url = version.updateUrl;

				BaseDialogFragment confirmDialog = new BaseDialogFragment();
				confirmDialog
						.setMessage((message != null && message.length() > 0) ? message
								: "当前有新版本，是否立即更新？");
				confirmDialog.setPositiveButton("更新版本",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								com.mc.parking.client.UpdateManager manager = new com.mc.parking.client.UpdateManager(
										MainActivity.this);
								// 检查软件更新
								manager.showDownloadDialog();

							}
						});
				confirmDialog.setNegativeButton("下次再说", null);
				confirmDialog.show(getFragmentManager(), "");
			} else if (msg.arg1 == 2) {
				VersionEntity version = (VersionEntity) msg.obj;
				String message = version.updatesContent;
				final String url = version.updateUrl;

				BaseDialogFragment confirmDialog = new BaseDialogFragment();
				confirmDialog
						.setMessage((message != null && message.length() > 0) ? message
								: "亲爱的用户，您需要更新为最新版本才能继续使用。");
				confirmDialog.setPositiveButton("更新版本",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								com.mc.parking.client.UpdateManager manager = new com.mc.parking.client.UpdateManager(
										MainActivity.this);
								// 检查软件更新
								manager.showDownloadDialog();
							}
						});
				confirmDialog.show(getFragmentManager(), "");
			} else if (msg.arg1 == 3) {
				VersionEntity version = (VersionEntity) msg.obj;
				String message = version.updatesContent;
				BaseDialogFragment confirmDialog = new BaseDialogFragment();
				confirmDialog
						.setMessage((message != null && message.length() > 0) ? message
								: "收到远程服务器通知消息");
				confirmDialog.setPositiveButton("我知道了", null);
				confirmDialog.show(getFragmentManager(), "");
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 写cache文件
	 */
	public void writeCache() {

		try {
			Log.d("MainActivity", "::::::::::start to write cache:"
					+ SessionUtils.city);

			SharedPreferences _cacheFile = getSharedPreferences(
					Constants.CACHE_PROP, MODE_PRIVATE);
			Editor edit = _cacheFile.edit();
			edit.putString(PackingApplication.CACHE_KEY_CITY, SessionUtils.city);
			edit.putInt(PackingApplication.CACHE_KEY_CITYCODE,
					SessionUtils.cityCode);
			edit.putString(PackingApplication.CACHE_KEY_ADDRESS,
					SessionUtils.address);
			edit.putString(PackingApplication.CACHE_KEY_lastTimeLantitude, ""
					+ mapFragment.mCurrentLantitude);
			edit.putString(PackingApplication.CACHE_KEY_lastTimeLongitude, ""
					+ mapFragment.mCurrentLongitude);
			edit.commit();
			SessionUtils.savechoiceparkInfoAdm(getApplicationContext());
		} catch (Exception e) {
			Log.e("writeCache", e.getMessage(), e);
		}

		try {
			SharedPreferences.Editor editor = getSharedPreferences("notice",
					MODE_PRIVATE).edit();
			editor.putInt("notices", Constants.NEWMESSAGENOTICE);
			editor.putInt("vibrate", Constants.NEWMESSAGENOTICEVIBRATE);
			editor.putInt("voice", Constants.NEWMESSAGENOTICEVOICE);
			editor.commit();

		} catch (Exception e) {

		}
		try {
			// 存版本号状态
			SharedPreferences SharedPreferences = getSharedPreferences(
					"version", MODE_PRIVATE);
			SharedPreferences.Editor editor = SharedPreferences.edit();
			editor.putLong("versionid", Constants.VERSION);
			editor.commit();
		} catch (Exception e) {

		}

	}

	/******************************** only for test *****************************************/
	/**
	 * 默认点击menu菜单，菜单项不现实图标，反射强制其显示
	 */
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {

		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {

		if (topBarFragment.slidingMenu != null
				&& topBarFragment.slidingMenu.isMenuShowing()) {
			topBarFragment.slidingMenu.toggle(true);

			return;
		}

		FragmentManager manager = getFragmentManager();
		while (manager.getBackStackEntryCount() > 0) {
			manager.popBackStackImmediate();
			manager.popBackStackImmediate();

			return;
		}

		finish();
		// super.onBackPressed();
	/*	BaseDialogFragment confirmDialog = new BaseDialogFragment();
		confirmDialog.setMessage("确认退出系统吗?");
		confirmDialog.setPositiveButton("确认",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//writeCache();
						Intent intent = new Intent(Intent.ACTION_MAIN);
						intent.addCategory(Intent.CATEGORY_HOME);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						android.os.Process.killProcess(android.os.Process
								.myPid());
					}
				});
		confirmDialog.setNegativeButton("取消", null);
		confirmDialog.show(getFragmentManager(), "");*/
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {

		super.onStop();
		writeCache();
	}

	@Override
	protected void onDestroy() {
		//关闭广播
/*		PackingApplication.getInstance().setCurrentActivity(null);
		unregisterReceiver(mReceiver);*/
		super.onDestroy();

	}

	@Override
	protected void onResume() {

		super.onResume();
		//为推送获取activity
		PackingApplication.getInstance().setCurrentActivity(this);
		if (SessionUtils.loginUser != null) {
			if (SessionUtils.loginUser.userType >= Constants.USER_TYPE_PADMIN
					&& SessionUtils.loginUser.userType < Constants.USER_TYPE_MADMIN) {
				android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
				if (manager.getBackStackEntryCount() == 0) {
					cheweiFragment = new CheweiFragment();
					android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager()
							.beginTransaction();
					// Replace whatever is in the fragment_container view with
					// this fragment,
					// and add the transaction to the back stack so the user can
					// navigate back
					transaction.replace(R.id.fragment_map, cheweiFragment);
					transaction.addToBackStack(null);
					// Commit the transaction
					transaction.commit();

				
				} else {
					return;
				}
			}

			if (SessionUtils.loginUser.userType >= Constants.USER_TYPE_MADMIN
					&& SessionUtils.loginUser.userType < Constants.USER_TYPE_MADMIN + 10) {
				android.support.v4.app.FragmentManager manager = getSupportFragmentManager();

				if (manager.getBackStackEntryCount() == 0) {
					collectmain = new CollectMainFragment();
					android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager()
							.beginTransaction();
					// Replace whatever is in the fragment_container view with
					// this fragment,
					// and add the transaction to the back stack so the user can
					// navigate back
					transaction.replace(R.id.fragment_map, collectmain);
					transaction.addToBackStack(null);
					// Commit the transaction
					transaction.commit();

					
				} else {
					
					return;
				}
			}

		}
		if (SessionUtils.loginUser == null) {

			android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
			if (manager.getBackStackEntryCount() >= 1) {
				manager.popBackStack();
			} else {
				return;
			}
		}

	}

	public void reload() {
		Log.d("onReceive", "reload:" + cheweiFragment);
		if (cheweiFragment != null) {
			cheweiFragment.getnotcomeincount();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		writeCache();
		outState.putSerializable("userinfo", SessionUtils.loginUser);
		super.onSaveInstanceState(outState);
	}
	
	
	@Override
	public void onRestoreInstanceState(Bundle outState) {
	        super.onRestoreInstanceState(outState);
	        SessionUtils.loginUser=(TuserInfo) outState.getSerializable("userinfo");
	}
	
	

}
