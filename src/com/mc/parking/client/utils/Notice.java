package com.mc.parking.client.utils;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Vibrator;
import com.mc.parking.client.Constants;
import com.mc.parking.client.PackingApplication;
import com.mc.park.client.R;
public class Notice {


	public static void rightnotice() {

		if (Constants.NEWMESSAGENOTICEVIBRATE == 1) {
			Vibrator vibrator = (Vibrator) PackingApplication.getInstance()
					.getSystemService(Context.VIBRATOR_SERVICE);
			long[] pattern = { 100, 100, 100, 100 }; // ֹͣ
														// ����
														// ֹͣ
														// ����
			vibrator.vibrate(pattern, -1); // �ظ����������pattern
											// ���ֻ����һ�Σ�index��Ϊ-1
		}

		if (Constants.NEWMESSAGENOTICEVOICE == 1) {
			NotificationManager manger = (NotificationManager) PackingApplication
					.getInstance().getSystemService(
							Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification();
			// �Զ������� �����ļ�����rawĿ¼�£�û�д�Ŀ¼�Լ�����һ��
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
//			long[] pattern = { 100, 100, 100, 100 }; // ֹͣ
//														// ����
//														// ֹͣ
//														// ����
//			vibrator.vibrate(pattern, -1); // �ظ����������pattern
//											// ���ֻ����һ�Σ�index��Ϊ-1
//		}
//		if (Constants.NEWMESSAGENOTICEVOICE == 1) {
//			NotificationManager manger = (NotificationManager) PackingApplication
//					.getInstance().getSystemService(
//							Context.NOTIFICATION_SERVICE);
//			Notification notification = new Notification();
//			// �Զ������� �����ļ�����rawĿ¼�£�û�д�Ŀ¼�Լ�����һ��
//			notification.sound = Uri.parse("android.resource://"
//					+ getPackageName() + "/" + R.raw.noticemusic);
//			manger.notify(1, notification);
//		}
	}
}
