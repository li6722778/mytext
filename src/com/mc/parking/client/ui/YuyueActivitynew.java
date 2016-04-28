package com.mc.parking.client.ui;

import java.io.StringReader;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Request.Method;
import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushManager;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.client.Constants;
import com.mc.parking.client.PageConstant;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.ChebolePayOptions;
import com.mc.parking.client.entity.CouponBean;
import com.mc.parking.client.entity.OrderEntity;
import com.mc.parking.client.entity.TCouponEntity;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkInfo_ImgEntity;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.entity.TParkInfo_Py;
import com.mc.parking.client.entity.TParkService;
import com.mc.parking.client.layout.ActionBaseActivity;
import com.mc.parking.client.layout.BaseDialogFragment;
import com.mc.parking.client.layout.CustomProgressDialog;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.HttpRequest;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.fragment.FuwulistDialogFragment;
import com.mc.parking.client.ui.fragment.ParklistDialogFragment;
import com.mc.parking.client.utils.DateHelper;
import com.mc.parking.client.utils.PayResult;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;
import com.mc.wxutils.MD5;
import com.mc.wxutils.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class YuyueActivitynew extends ActionBaseActivity {

	private TParkInfoEntity parkInfo;
	private TextView parkname, youhui;
	private String[] imageUrls;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private List<CouponBean> youhuilist;
	private ChebolePayOptions priceEntity;
	private OrderEntity orderInfoFromDetailPage;
	private RelativeLayout payview, pay_info_mode, lijianReala, fuwulayout;
	private SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	int PAY_VIEW = 1;
	private RadioButton paymode1, paymode2;
	private TextView compname, contactmen, tele, parkname_pay, parkaddress, paywayString, lijian_price, service_pay;

	private int hourOfDay1, minute1;
	private int timemode = 0;
	// 选中的服务
	TextView selecttext;
	public int starthouse = 0;
	public int startminute = 0;

	public double mCurrentLantitude;
	public double mCurrentLongitude;

	private Button modefybtn, yudingButton;

	private TextView lijiandetal,paytypenane, orgin_pay, yuyue_detail, coupon_price, total_price, parksava_text;

	private Activity activity;

	private Long couponid;
	public Date discountSecEndHour;
	private String[] numbers = { "1小时以内", "2小时以内", "3小时以内", "4小时以内", "5小时以内", "6小时以内", "7小时以内", "8小时以内", "9小时以内",
			"10小时以内", "11小时以内", "12小时以内" };

	private DecimalFormat df = new DecimalFormat("######0.00");

	// 商户PID
	public static final String PARTNER = "";
	// 商户收款账号
	public static final String SELLER = "";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "";

	private static final int SDK_PAY_FLAG = 1;

	private int COUPON_CHECK_FLAG = 0;

	TCouponEntity couponBean = null;

	private int selectedPayWay = 0;
	public View line2;

	StringBuffer selectServiceID = new StringBuffer("");

	// 判断是否安装微信
	private Boolean sIsWXAppInstalledAndSupported;
	private Boolean firstshow = true;

	Context context;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);

		
		setContentView(R.layout.ac_order_detailnew);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_topbar);
		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		titleView.setText("订单详情");
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.actionbar_topbar);
		context = getApplicationContext();
		
	}

	
}
