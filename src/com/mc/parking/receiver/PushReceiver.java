package com.mc.parking.receiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.igexin.sdk.PushConsts;
import com.mc.parking.client.PackingApplication;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.PushMessage;
import com.mc.parking.client.ui.MainActivity;
import com.mc.parking.client.ui.OrderActivity;
import com.mc.parking.client.ui.OrderDetailActivity;
import com.mc.parking.client.ui.YuyueActivity;
import com.mc.parking.client.ui.admin.AdminHomeActivity;

public class PushReceiver extends BroadcastReceiver {
	// 通知声音
	private Uri noticeUri = Uri
			.parse("android.resource://com.mc.parking.client/raw/"+R.raw.noticemusic);
	long[] pattern = { 100, 100, 100, 100 };

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		switch (bundle.getInt(PushConsts.CMD_ACTION)) {
		case PushConsts.GET_MSG_DATA:

			byte[] payload = bundle.getByteArray("payload");

			if (payload != null) {
				String orderInfo = new String(payload);
				//Toast.makeText(context,orderInfo, Toast.LENGTH_SHORT).show();
				Log.d("onReceive", orderInfo);
				PushMessage pushmessage = new PushMessage();
				pushmessage.decodeMessage(orderInfo);
				
				//检查消息体
				if(pushmessage.type==null||pushmessage.message==null||pushmessage.type.equals("")||pushmessage.message.equals("")){
					Log.d("onReceive", "the remote message is not suitable for push receiver");
					return;
				}
				
				
				Toast.makeText(context,pushmessage.message, Toast.LENGTH_LONG).show();
				
				// 定义通知栏展现的内容信息
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context).setSmallIcon(R.drawable.push)
						.setContentTitle(pushmessage.title)
						.setContentText(pushmessage.message);

				mBuilder.setAutoCancel(true);// 自己维护通知的消失
				mBuilder.setSound(noticeUri);
				mBuilder.setVibrate(pattern);
				// mBuilder.setLargeIcon(btm);
				
				
				//#########################这里开始跳转到指定的界面###################################################
				
				if(pushmessage.type.equals("ORDER_REQUEST_PAY")||pushmessage.type.equals("ORDER_START")||pushmessage.type.equals("ORDER_DONE")){
						// 构建一个Intent
						Intent resultIntent = new Intent(context,
								AdminHomeActivity.class);
						resultIntent.putExtra("from", "notice");
						if(pushmessage.type.equals("ORDER_DONE"))
						{
							resultIntent.putExtra("selectNum", 1);
						}
						
						// 封装一个Intent
						PendingIntent resultPendingIntent = PendingIntent.getActivity(
								context, 0, resultIntent,
								PendingIntent.FLAG_UPDATE_CURRENT);
						// 设置通知主题的意图
						mBuilder.setContentIntent(resultPendingIntent);
		
						NotificationManager mNotificationManager = (NotificationManager) context
								.getSystemService(Context.NOTIFICATION_SERVICE);
						mNotificationManager.notify(0, mBuilder.build());
						
						//刷新
						 refreshAdminPage();
						
				
				}else if(pushmessage.type.equals("ADMIN_ORDER_DONE")){
					
					// 构建一个Intent
					Intent resultIntent = new Intent(context,
							OrderActivity.class);
					resultIntent.putExtra("from", "notice");
					// 封装一个Intent
					PendingIntent resultPendingIntent = PendingIntent.getActivity(
							context, 0, resultIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					// 设置通知主题的意图
					mBuilder.setContentIntent(resultPendingIntent);
	
					NotificationManager mNotificationManager = (NotificationManager) context
							.getSystemService(Context.NOTIFICATION_SERVICE);
					mNotificationManager.notify(0, mBuilder.build());
					
					//如果当前是订单界面，就刷新
					Activity currentActivity = PackingApplication.getInstance().getCurrentActivity();
					if(currentActivity!=null){
						if(currentActivity instanceof OrderDetailActivity){//如果是当前正好是车位管理员的订单界面
						    ((OrderDetailActivity)currentActivity).notifyOrderDone();
						}else if(currentActivity instanceof OrderActivity){//如果是当前正好是车位管理员的订单界面
						    ((OrderActivity)currentActivity).getfirstdata();
						}
					}
				}else if(pushmessage.type.equals("ORDER_IN")){
					//刷新
					 refreshAdminPage();
				}else if(pushmessage.type.equals("ORDER_WILL_EXPIRE")){
					try{
					Activity currentActivity = PackingApplication.getInstance().getCurrentActivity();
					if(currentActivity!=null){
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(currentActivity);
						alertDialog.setTitle("温馨提示");
						alertDialog.setMessage(pushmessage.message);
						alertDialog.setPositiveButton("确认", null);
						alertDialog.create().show();
					}else
					{
						
					}
					}catch(Exception e){
						Log.e("PushReceiver", e.getMessage());
					}
				}else if(pushmessage.type.equals("MESSAGE_PUSH_COMM")){
					//普通消息
					try{
						Activity currentActivity = PackingApplication.getInstance().getCurrentActivity();
						if(currentActivity!=null){
							AlertDialog.Builder alertDialog = new AlertDialog.Builder(currentActivity);
							alertDialog.setTitle(pushmessage.title);
							alertDialog.setMessage(pushmessage.message);
							alertDialog.setPositiveButton("确认", null);
							alertDialog.create().show();
						}
						}catch(Exception e){
							Log.e("PushReceiver", e.getMessage());
						}
				}
				
			}
			break;
		default:
			break;
		}

	}
	
	
	private void refreshAdminPage(){
		//如果当前是订单界面，就刷新
		Activity currentActivity = PackingApplication.getInstance().getCurrentActivity();
		if(currentActivity!=null){
			if(currentActivity instanceof AdminHomeActivity){//如果是当前正好是车位管理员的订单界面
			    ((AdminHomeActivity)currentActivity).reloadOrderList();
			}
			
			if(currentActivity instanceof MainActivity){//如果是当前正好是车位管理员的首页
			    ((MainActivity)currentActivity).reload();
			}
		}
	}
}
