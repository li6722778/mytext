package com.mc.parking.client.utils;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Vibrator;
import com.mc.parking.client.Constants;
import com.mc.parking.client.PackingApplication;
import com.mc.parking.client.R;
public class Notice {


	public static void rightnotice() {

		if (Constants.NEWMESSAGENOTICEVIBRATE == 1) {
			Vibrator vibrator = (Vibrator) PackingApplication.getInstance()
					.getSystemService(Context.VIBRATOR_SERVICE);
			long[] pattern = { 100, 100, 100, 100 }; // 停止
														// 开启
														// 停止
														// 开启
			vibrator.vibrate(pattern, -1); // 重复两次上面的pattern
											// 如果只想震动一次，index设为-1
		}

		if (Constants.NEWMESSAGENOTICEVOICE == 1) {
			NotificationManager manger = (NotificationManager) PackingApplication
					.getInstance().getSystemService(
							Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification();
			// 自定义声音 声音文件放在raw目录下，没有此目录自己创建一个
			notification.sound = Uri.parse("android.resource://"
					+ getPackageName() + "/" + R.raw.noticemusic);
			manger.notify(1, notification);
		}
	}

	private static String getPackageName() {
		String packname = "com.mc.parking.client";
		return packname;
	}

	public static void errorotice() {
//
//		
//		if (Constants.NEWMESSAGENOTICEVIBRATE ==1) {
//			Vibrator vibrator = (Vibrator) PackingApplication.getInstance()
//					.getSystemService(Context.VIBRATOR_SERVICE);
//			long[] pattern = { 100, 100, 100, 100 }; // 停止
//														// 开启
//														// 停止
//														// 开启
//			vibrator.vibrate(pattern, -1); // 重复两次上面的pattern
//											// 如果只想震动一次，index设为-1
//		}
//		if (Constants.NEWMESSAGENOTICEVOICE == 1) {
//			NotificationManager manger = (NotificationManager) PackingApplication
//					.getInstance().getSystemService(
//							Context.NOTIFICATION_SERVICE);
//			Notification notification = new Notification();
//			// 自定义声音 声音文件放在raw目录下，没有此目录自己创建一个
//			notification.sound = Uri.parse("android.resource://"
//					+ getPackageName() + "/" + R.raw.noticemusic);
//			manger.notify(1, notification);
//		}
	}
}
