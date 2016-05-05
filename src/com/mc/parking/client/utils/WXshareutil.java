package com.mc.parking.client.utils;

import java.io.ByteArrayOutputStream;

import com.mc.parking.client.PackingApplication;
import com.mc.park.client.R;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author mxs E-mail:308348194@qq.com
 * @version ����ʱ�䣺2015��8��19�� ����5:31:03
 */
public class WXshareutil {

	// Ϊ��������һ��Ψһ�ı�ʶ
	public static String buildTransaction(final String type) {
		return (type == null ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis());
	}

	// ��bitmapת����byte����
	public static byte[] bitmaptoByteArray(final Bitmap bitmap,
			final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bitmap.recycle();
		}
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static SendMessageToWX.Req sharetofriend(String url,String title,String description) {
		SendMessageToWX.Req req = getsharecontext(url,title,description);
		req.scene = SendMessageToWX.Req.WXSceneSession;
		return req;

	}

	public static SendMessageToWX.Req sharetofriendcircle(String url,String title,String description) {
		SendMessageToWX.Req req = getsharecontext(url,title,description);
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		return req;

	}

	public static SendMessageToWX.Req getsharecontext(String url,String title,String description) {
		// �����������ڷ�װҪ���͵�URL
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = url;
		// ����message����
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title =title;
		msg.description =description;
		// ��������ͼ
		Bitmap thumb = BitmapFactory.decodeResource(PackingApplication
				.getInstance().getResources(), R.drawable.sharebutton);
		msg.thumbData = bitmaptoByteArray(thumb, true);
		// ����SendMessageTowx.Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		return req;

	}
}
