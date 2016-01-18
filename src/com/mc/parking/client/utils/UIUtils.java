package com.mc.parking.client.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import com.baidu.mapapi.model.LatLng;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mc.parking.client.PackingApplication;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.HttpRequest;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.RegActivity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Message;
import android.provider.SyncStateContract.Constants;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UIUtils {

	/**
	 * Helps determine if the app is running in a Tablet context.
	 *
	 * @param context
	 * @return
	 */
	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public static String readProperties(String CONFIG_FILE, String key) {
		Properties props = new Properties();
		InputStream in = null;
		Log.d("UIUtils", "read from:" + CONFIG_FILE);
		try {
			in = new BufferedInputStream(new FileInputStream(CONFIG_FILE));
			props.load(in);
			String value = props.getProperty(key);
			Log.d("UIUtils", "read " + key + ":" + value);
			return value;
		} catch (Exception e) {
			Log.e("UIUtils", e.getMessage());
			return "";
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Log.e("UIUtils", e.getMessage());
				}
			}
		}
	}

	public static HashMap<String, String> readProperties(String CONFIG_FILE, String... keys) {
		Properties props = new Properties();
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(CONFIG_FILE));
			props.load(in);
			HashMap<String, String> hashMap = new HashMap<String, String>();
			for (String key : keys) {
				String value = props.getProperty(key);
				Log.d("UIUtils", "read " + key + ":" + value);
				hashMap.put(key, value);
			}

			return hashMap;
		} catch (Exception e) {
			Log.e("UIUtils", e.getMessage());
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Log.e("UIUtils", e.getMessage());
				}
			}
		}
	}

	public static void writeProperties(String CONFIG_FILE, String parameterName, String parameterValue) {
		Properties prop = new Properties();
		try {
			File file = new File(CONFIG_FILE);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
			} else {
				InputStream fis = new FileInputStream(CONFIG_FILE);
				prop.load(fis);
				fis.close();
			}

			OutputStream fos = new FileOutputStream(CONFIG_FILE);
			prop.setProperty(parameterName, parameterValue);
			prop.store(fos, "Update '" + parameterName + "' value");
			Log.d("UIUtils", "updated " + CONFIG_FILE);
			fos.close();
		} catch (IOException e) {
			Log.e("UIUtils", "Visit " + CONFIG_FILE + " for updating " + parameterName + " value error");
		}
	}

	public static void writeProperties(String CONFIG_FILE, HashMap<String, String> options) {
		Properties prop = new Properties();
		try {
			File file = new File(CONFIG_FILE);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
			} else {
				InputStream fis = new FileInputStream(CONFIG_FILE);
				prop.load(fis);
				fis.close();
			}

			OutputStream fos = new FileOutputStream(CONFIG_FILE);
			for (String key : options.keySet()) {
				prop.setProperty(key, options.get(key));
			}

			prop.store(fos, "Update '" + options + "' value");
			Log.i("UIUtils", "updated " + CONFIG_FILE);
			fos.close();
		} catch (IOException e) {
			Log.e("UIUtils", "Visit " + CONFIG_FILE + " for updating " + options + " value error");
		}
	}

	/**
	 * 获取yyyyMMdd格式日期
	 * 
	 * @param time
	 * @return
	 */
	public static Date getDate(String time) {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		try {
			date = format.parse(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String formatDate(Context context, long date) {
		@SuppressWarnings("deprecation")
		int format_flags = android.text.format.DateUtils.FORMAT_NO_NOON_MIDNIGHT
				| android.text.format.DateUtils.FORMAT_ABBREV_ALL | android.text.format.DateUtils.FORMAT_CAP_AMPM
				| android.text.format.DateUtils.FORMAT_SHOW_DATE | android.text.format.DateUtils.FORMAT_SHOW_DATE
				| android.text.format.DateUtils.FORMAT_SHOW_TIME;
		return android.text.format.DateUtils.formatDateTime(context, date, format_flags);
	}

	// 生成QR图
	public static void createQRImage(final ImageView qr_image, final String text, final int QR_WIDTH,
			final int QR_HEIGHT) {

		new SafeAsyncTask<Bitmap>() {
			@Override
			public Bitmap call() throws Exception {
				// 需要引入core包
				QRCodeWriter writer = new QRCodeWriter();

				// 把输入的文本转为二维码
				BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);

				Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
				hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
				hints.put(EncodeHintType.ERROR_CORRECTION, 1);
				BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT,
						hints);
				int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
				for (int y = 0; y < QR_HEIGHT; y++) {
					for (int x = 0; x < QR_WIDTH; x++) {
						if (bitMatrix.get(x, y)) {
							pixels[y * QR_WIDTH + x] = 0xff000000;
						} else {
							pixels[y * QR_WIDTH + x] = 0xffffffff;
						}
					}
				}

				Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
				bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
				return bitmap;
			}

			@Override
			protected void onPreExecute() {

			}

			@Override
			protected void onException(final Exception e) throws RuntimeException {
				super.onException(e);
				Log.e("createQRImage", e.getMessage(), e);
			}

			@Override
			protected void onSuccess(final Bitmap bitmap) throws Exception {
				qr_image.setImageBitmap(bitmap);
			}
		}.execute();

	}

	/**
	 * 验证输入框不为空
	 * 
	 * @param editViewTitle
	 * @param editView
	 * @return
	 */
	public static boolean validationNotEmpty(Activity activity, String editViewTitle, TextView editView) {
		String text = editView.getText().toString();
		if (text.trim().equals("")) {
			Toast.makeText(activity.getApplicationContext(), "[" + editViewTitle + "]" + "不能为空", Toast.LENGTH_SHORT)
					.show();
			editView.requestFocus();
			return false;
		}

		return true;
	}

	public static double decimalPrice(Double v) {
		return new BigDecimal(Double.toString(v)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	// 检查clientid 如果为空获得手机唯一标识
	public static String checkCurrentClientID(String clientid) {

		if (clientid == null || clientid.toString().trim().equals("") || clientid.trim().equals("null")
				|| clientid.trim().equals("NULL")) {
			Activity activity = PackingApplication.getInstance().getMainActivity();
			if (activity != null) {
				TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
				clientid = tm.getDeviceId();
			} else
				clientid = "";
		}
		return clientid;
	}

	public static void getclientId(Long userid, String clientid) {
		//判断clientid满足条件否  不满足获得手机id
		clientid=checkCurrentClientID(clientid);
		HttpRequest<String> httpRequest = new HttpRequest<String>(
				"/a/push/register/" + userid + "?clientId=" + clientid, String.class) {

			@Override
			public void onSuccess(String arg0) {
				/*
				 * Toast.makeText(PackingApplication.getInstance(), arg0,
				 * Toast.LENGTH_SHORT).show();
				 */
			}

			@Override
			public void onFailed(String message) {
			}

		};

		httpRequest.execute();

	}

	/**
	 * 显示当前分页数据信息
	 * 
	 * @param content
	 * @param currentPage
	 * @param totalPage
	 * @param pageSize
	 * @param rowCount
	 */
	public static void displayPaginationInfo(Context content, int currentPageIndex, int pageSize, int rowCount) {
		int currenTotal = (currentPageIndex + 1) * pageSize;
		if (currenTotal > rowCount) {
			currenTotal = rowCount;
		}

		Toast.makeText(content, "当前显示" + currenTotal + "/" + rowCount + "条数据", Toast.LENGTH_SHORT).show();
	}

	// 实现地图返回连续变量
	// 记录跳转时地图坐标
	public static LatLng currentlatlng = null;
	// 定位坐标
	public static LatLng mylocallatlng = null;
	// 搜索时的坐标
	public static LatLng myserchlatlng = null;

	// 记录跳转时地图放大级别
	public static float currentZoom;
	// 判断是否从导航返回，若是着为false
	public static boolean backState = false;

	public static List<TParkInfo_LocEntity> tempParkinfo = null;

	// 判断是否跳到完成订单界面
	public static boolean okorder = false;

}
