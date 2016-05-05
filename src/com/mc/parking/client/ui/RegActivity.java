package com.mc.parking.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.android.volley.Request.Method;
import com.google.gson.reflect.TypeToken;
import com.mc.parking.client.Constants;
import com.mc.park.client.R;
import com.mc.parking.client.entity.ChebolePayOptions;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.ActionBaseActivity;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.CommFindEntity;
import com.mc.parking.client.layout.net.HttpRequest;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;
import com.mc.parking.zxing.camera.MipcaActivityCapture;

/**
 * 
 * @author woderchen
 *
 */
public class RegActivity extends ActionBaseActivity {

	private int currentParent;
	private long userphone;

	private EditText edit_verifycode, phonenumber, userpassword,pushnumber;
	private Button request_verifycode;
	private TimeCount time;
	// private SmsContent content;
	private boolean checkstaute;
	private ImageView scanView;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_reg_phone);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_topbar);
		time = new TimeCount(Constants.Verifycodetime, 1000);// 构造CountDownTimer对象
		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		titleView.setText(R.string.reg_title);
		// content = new SmsContent(new Handler());
		// 注册短信变化监听
		// this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"),
		// true, content);
		final TParkInfo_LocEntity tParkInfo_LocEntity = (TParkInfo_LocEntity) getIntent()
				.getSerializableExtra("parkinfo");
		currentParent = 0;
		Object parent = getIntent().getExtras().get("parent");
		if (parent != null) {
			currentParent = (int) parent;
		}

		String userphoneFromLogin = getIntent().getStringExtra("userphone");

		phonenumber = (EditText) findViewById(R.id.reg_g1_userid);
		edit_verifycode = (EditText) findViewById(R.id.edit_verifycode);
		userpassword = (EditText) findViewById(R.id.edit_password);
		pushnumber = (EditText) findViewById(R.id.edit_pushnumber);
		scanView = (ImageView) findViewById(R.id.scan_pushnumberview);
		

		if (userphoneFromLogin != null) {
			phonenumber.setText(userphoneFromLogin);
		}

		Button next_submit = (Button) findViewById(R.id.reg_submit);
		request_verifycode = (Button) findViewById(R.id.request_verifycode);
		request_verifycode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (!UIUtils.validationNotEmpty(RegActivity.this, "手机号码",
						phonenumber)) {
					return;
				}

				request_verifycode.setEnabled(false);
				time.start();// 开始计时

				HttpRequest<String> httpRequestAni = new HttpRequest<String>(
						Method.POST, null, "/a/reg/sendsms/"
								+ phonenumber.getText().toString(),
						new TypeToken<String>() {
						}.getType(), String.class) {
					@Override
					public void onSuccess(String infos) {
						Toast.makeText(RegActivity.this, infos,
								Toast.LENGTH_SHORT).show();
						request_verifycode.setEnabled(true);
					}

					@Override
					public void onFailed(String message) {
						Toast.makeText(RegActivity.this, "验证码发送失败." + message,
								Toast.LENGTH_SHORT).show();
						request_verifycode.setEnabled(true);
					}
				};

				httpRequestAni.execute();
			}
		});

		scanView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(),
						MipcaActivityCapture.class);
				startActivityForResult(intent, 11);
			}
		});

		next_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (!UIUtils.validationNotEmpty(RegActivity.this, "手机号码",
						phonenumber)) {
					return;
				}
				if (!UIUtils.validationNotEmpty(RegActivity.this, "验证码",
						edit_verifycode)) {
					return;
				}
				Intent intent = new Intent(RegActivity.this,
						Reg_noticeActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (resultCode) {
		case 1:
			register();
			// this.setResult(resultCode, data);
			// finish();
			break;
		case 11:
			String scanresult= data.getStringExtra("ScanResult");
			if (scanresult!= null)
			// 请求play验证 推广码
			{
				verifytwodimensioncode(scanresult);
			}
			break;
		default:
			break;
		}

	}

	private void verifytwodimensioncode(String code) {
	try {
		String	parastring = URLEncoder.encode(code, "utf-8");
		HttpRequest<ComResponse<String>> httpRequestAni = new HttpRequest<ComResponse<String>>("/a/user/scanforext?c="+parastring, new TypeToken<ComResponse<String>>() {
		}.getType()) {
			@Override
			public void onSuccess(ComResponse<String> arg0) {
				if(arg0!=null&&arg0.getResponseEntity()!=null){
					pushnumber.setEnabled(true);
					pushnumber.setText(arg0.getResponseEntity());
					pushnumber.setEnabled(false);
				}
				else {
					Toast.makeText(RegActivity.this, "请扫描正确的二维码" ,
							Toast.LENGTH_SHORT).show();
				}
			
			}

			@Override
			public void onFailed(String message) {
				Toast.makeText(RegActivity.this, "邀请码获取失败" + message,
						Toast.LENGTH_SHORT).show();
			}
		};

		httpRequestAni.execute();
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		

	}

	private Handler reghandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// 关闭ProgressDialog
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			Toast.makeText(RegActivity.this, "注册成功", Toast.LENGTH_SHORT).show();

			if (currentParent > 0) {
				switch (currentParent) {
				case LoginActivity.parent_userinfo:
					Intent intentUser = new Intent(RegActivity.this,
							UserInfoActivity.class);
					startActivity(intentUser);
					finish();
					break;
				case LoginActivity.parent_oderinfo:
					Intent intentOrder = new Intent(RegActivity.this,
							OrderActivity.class);
					startActivity(intentOrder);
					finish();
					break;
				default:
					finish();
				}
			} else {
				// Intent intent = new Intent(RegDetailActivity.this,
				// PayWayActivity.class);
				// Bundle buidle = new Bundle();
				// buidle.putSerializable("parkinfo", parkinfo);
				// intent.putExtras(buidle);
				setResult(1, getIntent());
				finish();
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// this.getContentResolver().unregisterContentObserver(content);
		// 注销短信监听广播

	}

	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			request_verifycode.setText("重新验证");
			request_verifycode.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			request_verifycode.setClickable(false);
			request_verifycode.setText("正在获取..." + "(" + millisUntilFinished
					/ 1000 + "秒)");
		}
	}

	//
	// /**
	// * 监听短信数据库
	// */
	// class SmsContent extends ContentObserver {
	//
	// private Cursor cursor = null;
	//
	// public SmsContent(Handler handler) {
	// super(handler);
	// }
	//
	// @SuppressWarnings("deprecation")
	// @Override
	// public void onChange(boolean selfChange) {
	//
	// super.onChange(selfChange);
	// //读取收件箱中指定号码的短信
	// cursor = managedQuery(Uri.parse("content://sms/inbox"), new
	// String[]{"_id", "address", "read", "body"},
	// " address=? and read=?", new String[]{"106590573205224", "0"},
	// "_id desc");//按id排序，如果按date排序的话，修改手机时间后，读取的短信就不准了
	// if (cursor != null && cursor.getCount() > 0) {
	// ContentValues values = new ContentValues();
	// values.put("read", "1"); //修改短信为已读模式
	// cursor.moveToNext();
	// int smsbodyColumn = cursor.getColumnIndex("body");
	// String smsBody = cursor.getString(smsbodyColumn);
	//
	// edit_verifycode.setText(getDynamicPassword(smsBody));
	//
	// }
	//
	// //在用managedQuery的时候，不能主动调用close()方法， 否则在Android 4.0+的系统上， 会发生崩溃
	// if(Build.VERSION.SDK_INT < 14) {
	// cursor.close();
	// }
	// }
	// }
	//
	// /**
	// * 从字符串中截取连续4位数字
	// * 用于从短信中获取动态密码
	// * @param str 短信内容
	// * @return 截取得到的4位动态密码
	// */
	// public static String getDynamicPassword(String str) {
	// Pattern continuousNumberPattern = Pattern.compile("[0-9\\.]+");
	// Matcher m = continuousNumberPattern.matcher(str);
	// String dynamicPassword = "";
	// while(m.find()){
	// if(m.group().length() == 4) {
	// System.out.print(m.group());
	// dynamicPassword = m.group();
	// }
	// }
	//
	// return dynamicPassword;
	// }

	public void register() {
		userphone = Long.parseLong(phonenumber.getText().toString());
		String verCode = edit_verifycode.getText().toString();

		String password = userpassword.getText().toString();
		StringBuilder username = new StringBuilder("cbl").append(phonenumber
				.getText().toString());

		TuserInfo userInfo = new TuserInfo();

		userInfo.userPhone = userphone;
		userInfo.passwd = password;
		if(pushnumber.getText()!=null)
		userInfo.extensionstring=pushnumber.getText().toString().trim();
		else
			userInfo.extensionstring="";
		userInfo.userName = username.toString();

		HttpRequestAni<ComResponse<TuserInfo>> httpRequestAni = new HttpRequestAni<ComResponse<TuserInfo>>(
				RegActivity.this, "/a/user/reg?c=" + verCode,
				new TypeToken<ComResponse<TuserInfo>>() {
				}.getType(), userInfo, TuserInfo.class) {

			@Override
			public void callback(ComResponse<TuserInfo> arg0) {
				if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {
					TuserInfo userInfo = arg0.getResponseEntity();
					Message msg = new Message();
					msg.arg1 = userInfo.userType;
					SessionUtils.loginUser = userInfo;
					reghandler.sendMessage(msg);
				} else {
					Toast.makeText(RegActivity.this,
							"[异常]" + arg0.getErrorMessage(), Toast.LENGTH_SHORT)
							.show();
				}

			}

		};

		httpRequestAni.execute();

	}

}
