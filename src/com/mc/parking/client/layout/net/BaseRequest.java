package com.mc.parking.client.layout.net;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mc.parking.client.utils.Log;

/**
 * volley Gson request
 * 
 * @author woderchen
 *
 * @param <T>
 */
public class BaseRequest<T> extends Request<T> {
	private final Listener<T> mListener;
	private Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
			.create();;

	private Type mClass;

	private Map<String, Object> params;

	public BaseRequest(int method, Map<String, Object> params, String url,
			Type clazz, BaseListener<T> listener) {
		super(method, url, listener);
		this.params = params;
		mClass = clazz;
		mListener = listener;
		
		setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}

	public BaseRequest(int method, String url, Type clazz,
			BaseListener<T> listener) {
		this(method, null, url, clazz, listener);
	}

	public BaseRequest(String url, Type clazz, BaseListener<T> listener) {
		this(Method.GET, null, url, clazz, listener);
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {

		try {

			Log.d("BaseRequest", "HTTP CODE:" + response.statusCode);

			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			
			
			if(mClass == String.class){
				return Response.success((T)jsonString,
						HttpHeaderParser.parseCacheHeaders(response));
			}
			
			return Response.success((T)mGson.fromJson(jsonString, mClass),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected void deliverResponse(T response) {
		Log.d("BaseRequest", "deliver response:"
				+ (response==null?null:response.getClass().getName()));
		mListener.onResponse(response);
	}

	@Override
	protected Map getParams() throws AuthFailureError {
		if (params != null) {

			for (String key : params.keySet()) {
				Log.d("BaseRequest", "http param key-value:" + key + "-"
						+ params.get(key));
			}

			return params;
		}
		return new HashMap();
	}
}
