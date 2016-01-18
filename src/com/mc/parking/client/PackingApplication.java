package com.mc.parking.client;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.mc.parking.client.ui.SplashActivity;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * 
 * @author woderchen
 *
 */
public class PackingApplication extends Application {
	public static final String TAG = PackingApplication.class.getSimpleName();
	private static boolean isReadCache = false;

	public final static String CACHE_KEY_CITY = "CACHE_KEY_CITY";
	public final static String CACHE_KEY_CITYCODE = "CACHE_KEY_CITYCODE";
	public final static String CACHE_KEY_ADDRESS = "CACHE_KEY_ADDRESS";
	public final static String CACHE_KEY_lastTimeLantitude = "CACHE_KEY_lastTimeLantitude";
	public final static String CACHE_KEY_lastTimeLongitude = "CACHE_KEY_lastTimeLongitude";

	private MKOfflineMap mOfflineMap;
	private RequestQueue mRequestQueue;
	
	
	/**ss
	 * 保存当前运行的activity
	 */
	private Activity currentActivity;

	public MKOfflineMap getMKOfflineMap() {
		if (mOfflineMap == null) {
			mOfflineMap = new MKOfflineMap();
		}
		return mOfflineMap;
	}
	
	/**
	 * 
	 * @return
	 */
	public Activity getCurrentActivity(){
		return currentActivity;
	}
	
	/**
	 * 
	 * @param currentActivity
	 */
	public void setCurrentActivity(Activity currentActivity){
		Log.d("setCurrentActivity", "#####set current#####:"+currentActivity);
		this.currentActivity = currentActivity;
	}

	private static PackingApplication mInstance;

	public static synchronized PackingApplication getInstance() {
		if (mInstance == null) {
			mInstance = new PackingApplication();
		}

		return mInstance;
	}
	
	public Activity mainActivity;
	
	
	public Activity getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(Activity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public void onCreate() {
		super.onCreate();
		mInstance = this;
		// load Map sdk
		SDKInitializer.initialize(this);

		mOfflineMap = new MKOfflineMap();

		initImageLoader(getApplicationContext());
		 
	    Thread.setDefaultUncaughtExceptionHandler(restartHandler); // 程序崩溃时触发线程  以下用来捕获程序崩溃异常 
	}
	
	
	
    // 创建服务用于捕获崩溃异常    
    private UncaughtExceptionHandler restartHandler = new UncaughtExceptionHandler() {    
        public void uncaughtException(Thread thread, Throwable ex) {    
            restartApp();//发生崩溃异常时,重启应用    
        }    
    };

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				//.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
	
	   public void restartApp(){  
	        Intent intent = new Intent(mInstance,SplashActivity.class);  
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
	        mInstance.startActivity(intent);  
	        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前  
	    }  
	
}
