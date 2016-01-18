package com.mc.parking.receiver;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.mc.addpic.utils.NetWorkActivity;
import com.mc.parking.client.Constants;
import com.mc.parking.client.utils.NetWorkUtil;


public class NetState extends BroadcastReceiver {

	public static boolean IS_ENABLE = true;
	private NetworkInfo gprs;
	private NetworkInfo wifi;
	Dialog dialog;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isConnected = NetWorkUtil.isNetworkConnected(context);
		gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		//startActivity(context);
		if (isConnected) {
        	Constants.NETFLAG=true;
        } else {
        	Constants.NETFLAG=false;     
			Intent intent1 = new Intent().setClass(context,
			NetWorkActivity.class);
	((Activity) context).startActivityForResult(intent1, 1);
        	Toast.makeText(context, "ÍøÂçÁ¬½ÓÊ§°Ü£¬Çë¼ì²éÍøÂç", Toast.LENGTH_LONG).show();
        }
		
	}

	/**
	 * è·³è½¬
	 * 
	 * @param context
	 */
//	private void startActivity(Context context) {
//		if (!gprs.isConnected() && !wifi.isConnected() && IS_ENABLE) {
//			System.out.println("start");
//			IS_ENABLE = false;
//			((MainActivity) context).showTips(R.drawable.tips_error,
//					"ç½‘ç»œæœªè¿žæŽ¥ï¼Œè¯·å…ˆè¿žæŽ¥ç½‘ç»œ...");
//			Intent intent = new Intent().setClass(context,
//					NetWorkActivity.class);
//			((Activity) context).startActivityForResult(intent, 1);
//		}
//	}

}