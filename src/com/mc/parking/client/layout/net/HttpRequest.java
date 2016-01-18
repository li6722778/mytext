package com.mc.parking.client.layout.net;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.sax.StartElementListener;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mc.addpic.utils.NetWorkActivity;
import com.mc.parking.client.Constants;
import com.mc.parking.client.PackingApplication;
import com.mc.parking.client.utils.SessionUtils;

/**
 * 
 * @author woderchen
 *
 * @param <T>
 */
public abstract class HttpRequest<T> extends BaseListener<T>{

	public final static String TAG = "HttpRequest";
	public static Gson gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	//uri not include http://****:****/
	private String uri = "";
	private Type classType;
	private Map<String, Object> params;
	private int method;
	private Object bean;
	private Class<?> postClass;

	public HttpRequest(String uri, Type classType) {
		this.uri = uri;
		this.classType = classType;
		this.method = Method.GET;
		this.params = null;
		bean = null;
	}
	
	public HttpRequest(int method, Map<String, Object> params,String uri, Type classType) {
		this.uri = uri;
		this.classType = classType;
		this.method = method;
		this.params = params;
		bean = null;
	}
	
	public HttpRequest(int method, Object bean,String uri, Type classType,Class<?> postClass) {
		this.uri = uri;
		this.classType = classType;
		this.method = method;
		this.bean = bean;
		this.params = null;
		this.postClass = postClass;
	}

	/**
	 * you can re-write here
	 */
	public void onPreExecute(){
		
	};
	
	/**
	 * fetch userid and passwd from memory cache
	 * @return
	 */
	public Map<String, String> getHeaders(){
		return SessionUtils.buildAuthHeaders();
	}
	
	public  Map<String, Object> jsonToMap(Object transferBean) {
		
		String jsonString = gsonBuilder.toJson(transferBean,postClass);
		Log.d("HttpRequest","transfer to map from bean:"+jsonString);
	    Type type = new TypeToken<Map<String, Object>>(){}.getType();
	    return gsonBuilder.fromJson(jsonString, type);
	}

	
	
	private void sendRequest() {
		
		//判断是否有网络,没有着弹出提示窗体
		if(Constants.NETFLAG==false)  
		{
			Activity actvity=PackingApplication.getInstance().getMainActivity();
			
			if(actvity!=null)
			{
	        	Intent intent1 = new Intent().setClass(actvity,NetWorkActivity.class);
	        	actvity.startActivityForResult(intent1, 1);
	       	  
			}
		}
		//(PackingApplication.getInstance()).startActivityForResult(intent1, 1);
		if(bean!=null){
			
			params = jsonToMap(bean);
			Log.d("HttpRequest","transfer done:"+params);
		}
		
		if(uri.indexOf("#")>=0){
			uri = uri.replace("#", "%23");
		}
		
		String transferUrl = Constants.HTTP + uri;

/*		try{
			String[] usls = uri.split("\\?");
			String tempUrl = Constants.HTTP+usls[0];
			if(usls.length>1){
				String abbr = uri.substring(uri.indexOf("?"));
				abbr = URLEncoder.encode(abbr, "utf-8");
				tempUrl =tempUrl+"?"+abbr;
			}
		    transferUrl = tempUrl;
		}catch(Exception e){
			Log.w("sendRequest", e);
		}
		Log.w("@@@@@@@transferUrl", transferUrl);*/
		BaseRequest<T> gsonRequest = new BaseRequest<T>(method,params,transferUrl, classType, this){
			
			/*if not needful,pls remove below code, any question, pls check with chenxp*/
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
			    return  HttpRequest.this.getHeaders();//from sessionUtils
			}
		};

		// send the request
		PackingApplication.getInstance().addToRequestQueue(gsonRequest, TAG);
	}

	
	public void execute() {
		try {
			onPreExecute();
			sendRequest();
		} catch (Exception e) {
			onFailed(e.getMessage());
			Log.e(TAG, e.getMessage(), e);
		}
	}
}
