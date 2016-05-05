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
import com.mc.park.client.R;
import com.mc.parking.client.entity.PushMessage;
import com.mc.parking.client.ui.MainActivity;
import com.mc.parking.client.ui.OrderActivity;
import com.mc.parking.client.ui.OrderDetailActivity;
import com.mc.parking.client.ui.YuyueActivity;
import com.mc.parking.client.ui.admin.AdminHomeActivity;

public class PushReceiver extends BroadcastReceiver {
	// ֪ͨ����
	private Uri noticeUri = Uri
			.parse("android.resource://com.mc.parking.client/raw/"+R.raw.noticemusic);
	long[] pattern = { 100, 100, 100, 100 };

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Toast.makeText(context, "push one", Toast.LENGTH_LONG).show();
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {
		case PushConsts.GET_MSG_DATA:

			byte[] payload = bundle.getByteArray("payload");
			Toast.makeText(context, "push two", Toast.LENGTH_LONG).show();
			if (payload != null) {
				String orderInfo = new String(payload);
				//Toast.makeText(context,orderInfo, Toast.LENGTH_SHORT).show();
				Log.d("onReceive", orderInfo);
				PushMessage pushmessage = new PushMessage();
				pushmessage.decodeMessage(orderInfo);
				
				//�����Ϣ��
				if(pushmessage.type==null||pushmessage.message==null||pushmessage.type.equals("")||pushmessage.message.equals("")){
					Log.d("onReceive", "the remote message is not suitable for push receiver");
					return;
				}
				
				
				Toast.makeText(context,pushmessage.message, Toast.LENGTH_LONG).show();
				
				// ����֪ͨ��չ�ֵ�������Ϣ
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context).setSmallIcon(R.drawable.push)
						.setContentTitle(pushmessage.title)
						.setContentText(pushmessage.message);

				mBuilder.setAutoCancel(true);// �Լ�ά��֪ͨ����ʧ
				mBuilder.setSound(noticeUri);
				mBuilder.setVibrate(pattern);
				// mBuilder.setLargeIcon(btm);
				
				
				//#########################���￪ʼ��ת��ָ���Ľ���###################################################
				
				if(pushmessage.type.equals("ORDER_REQUEST_PAY")||pushmessage.type.equals("ORDER_START")||pushmessage.type.equals("ORDER_DONE")){
						// ����һ��Intent
						Intent resultIntent = new Intent(context,
								AdminHomeActivity.class);
						resultIntent.putExtra("from", "notice");
						if(pushmessage.type.equals("ORDER_DONE"))
						{
							resultIntent.putExtra("selectNum", 1);
						}
						// ��װһ��Intent
						PendingIntent resultPendingIntent = PendingIntent.getActivity(
								context, 0, resultIntent,
								PendingIntent.FLAG_UPDATE_CURRENT);
						// ����֪ͨ�������ͼ
						mBuilder.setContentIntent(resultPendingIntent);
		
						NotificationManager mNotificationManager = (NotificationManager) context
								.getSystemService(Context.NOTIFICATION_SERVICE);
						mNotificationManager.notify(0, mBuilder.build());
						
						//ˢ��
						 refreshAdminPage();
						
				
				}else if(pushmessage.type.equals("ADMIN_ORDER_DONE")){
					
					// ����һ��Intent
					Intent resultIntent = new Intent(context,
							OrderActivity.class);
					resultIntent.putExtra("from", "notice");
					// ��װһ��Intent
					PendingIntent resultPendingIntent = PendingIntent.getActivity(
							context, 0, resultIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					// ����֪ͨ�������ͼ
					mBuilder.setContentIntent(resultPendingIntent);
	
					NotificationManager mNotificationManager = (NotificationManager) context
							.getSystemService(Context.NOTIFICATION_SERVICE);
					mNotificationManager.notify(0, mBuilder.build());
					
					//�����ǰ�Ƕ������棬��ˢ��
					Activity currentActivity = PackingApplication.getInstance().getCurrentActivity();
					if(currentActivity!=null){
						if(currentActivity instanceof OrderDetailActivity){//����ǵ�ǰ�����ǳ�λ����Ա�Ķ�������
						    ((OrderDetailActivity)currentActivity).notifyOrderDone();
						}else if(currentActivity instanceof OrderActivity){//����ǵ�ǰ�����ǳ�λ����Ա�Ķ�������
						    ((OrderActivity)currentActivity).getfirstdata();
						}
					}
				}else if(pushmessage.type.equals("ORDER_IN")){
					//ˢ��
					 refreshAdminPage();
				}else if(pushmessage.type.equals("ORDER_WILL_EXPIRE")){
					try{
					Activity currentActivity = PackingApplication.getInstance().getCurrentActivity();
					if(currentActivity!=null){
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(currentActivity);
						alertDialog.setTitle("��ܰ��ʾ");
						alertDialog.setMessage(pushmessage.message);
						alertDialog.setPositiveButton("ȷ��", null);
						alertDialog.create().show();
					}else
					{
						
					}
					}catch(Exception e){
						Log.e("PushReceiver", e.getMessage());
					}
				}else if(pushmessage.type.equals("MESSAGE_PUSH_COMM")){
					//��ͨ��Ϣ
					try{
						Activity currentActivity = PackingApplication.getInstance().getCurrentActivity();
						if(currentActivity!=null){
							AlertDialog.Builder alertDialog = new AlertDialog.Builder(currentActivity);
							alertDialog.setTitle(pushmessage.title);
							alertDialog.setMessage(pushmessage.message);
							alertDialog.setPositiveButton("ȷ��", null);
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
		//�����ǰ�Ƕ������棬��ˢ��
		Activity currentActivity = PackingApplication.getInstance().getCurrentActivity();
		if(currentActivity!=null){
			if(currentActivity instanceof AdminHomeActivity){//����ǵ�ǰ�����ǳ�λ����Ա�Ķ�������
			    ((AdminHomeActivity)currentActivity).reloadOrderList();
			}
			
			if(currentActivity instanceof MainActivity){//����ǵ�ǰ�����ǳ�λ����Ա����ҳ
			    ((MainActivity)currentActivity).reload();
			}
		}
	}
}
