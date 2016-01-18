package com.mc.parking.client.layout.net;

import java.lang.reflect.Type;
import java.util.Map;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.mc.parking.client.layout.CustomProgressDialog;

/**
 * 
 * @author woderchen
 *
 * @param <T>
 */
public abstract class HttpRequestAni<T> extends HttpRequest<T>{
	public static final String TAG = HttpRequestAni.class.getSimpleName();

	private CustomProgressDialog progressDialog;
	private Activity activity;
	
	public HttpRequestAni(final Activity activity, String uri, Type classType,int method, Map<String, Object> params) {
		super(method,params,uri, classType);
		this.activity = activity;
	}
	
	public HttpRequestAni(final Activity activity, String uri, Type classType, Object bean, Class<?> postClass) {
		super(Method.POST,bean,uri, classType,postClass);
		this.activity = activity;
	}
	
	public HttpRequestAni(final Activity activity, String uri, Type classType) {
		super(Method.GET,null,uri, classType);
		this.activity = activity;
	}
	
	@Override
	public void onPreExecute() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(activity);
			progressDialog.setMessage("ÇëµÈ´ý...");
		}
		progressDialog.show();
		
	}

	public abstract void callback(T arg0);
	
	@Override
	public void onSuccess(T arg0) {
		Log.i(TAG, "-----------http request successfully.----------");
		Log.i(TAG, arg0==null?"empty value":arg0.toString());
		progressDialog.clear();
		progressDialog.dismiss();
		progressDialog = null;
		callback(arg0);
		
	}

	@Override
	public void onFailed(String message) {
		if (progressDialog!=null) {
			try{
			progressDialog.clear();
			progressDialog.dismiss();
			}catch(Exception e){
				Log.e(TAG, e.getMessage(),e);
			}
			progressDialog = null;
		}
		Log.e(TAG, message);
		
		Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
		
		
	}

}
