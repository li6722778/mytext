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
import com.mc.park.client.R;
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

public class ProYuyueActivity extends ActionBaseActivity {

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

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			enablePayConfirmButton(true);
			switch (msg.what) {
			case SDK_PAY_FLAG: {

				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					paychangeStatus(Constants.PAYMENT_STATUS_FINISH);
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						paychangeStatus(Constants.PAYMENT_STATUS_PENDING);

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						paychangeStatus(Constants.PAYMENT_STATUS_EXCPTION);
					}
				}
				break;
			}

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_yuding);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.actionbar_topbar);
		context = getApplicationContext();
		RatingBar ratingBar = (RatingBar) findViewById(R.id.yuding_rat);

		msgApi.registerApp(Constants.APP_ID);
		paytypenane = (TextView) findViewById(R.id.paytypenane);
		orgin_pay = (TextView) findViewById(R.id.orgin_pay);
		yuyue_detail = (TextView) findViewById(R.id.yuyue_detail);
		coupon_price = (TextView) findViewById(R.id.coupon_price);
		total_price = (TextView) findViewById(R.id.total_price);
		parksava_text = (TextView) findViewById(R.id.parksava_text);
		lijian_price = (TextView) findViewById(R.id.lijian_price);
		lijiandetal = (TextView) findViewById(R.id.lijiandetal);
		line2 = (View) findViewById(R.id.line2);
		service_pay = (TextView) findViewById(R.id.fuwu_pay);
		TextView remindertext = (TextView) findViewById(R.id.remindertext);
		RelativeLayout zhifubaolayout = (RelativeLayout) findViewById(R.id.zhifubaolayout);

		ImageButton imageButton = (ImageButton) findViewById(R.id.topbarRightButton);
		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		titleView.setText(R.string.order_pay_title);
		imageButton.setVisibility(View.GONE);
		youhui = (TextView) findViewById(R.id.youhui);
		RelativeLayout youhuilLayout = (RelativeLayout) findViewById(R.id.youhuilayout);

		payview = (RelativeLayout) findViewById(R.id.tingchefeilayout);
		pay_info_mode = (RelativeLayout) findViewById(R.id.pay_info_mode);
		paywayString = (TextView) findViewById(R.id.paywayString);
		lijianReala = (RelativeLayout) findViewById(R.id.lijian);
		selecttext = (TextView) findViewById(R.id.selectservice);
		// 增值服务布局
		if(getIntent().getExtras().get("price")!=null)
		priceEntity=(ChebolePayOptions) getIntent().getExtras().get("price");
		else
			priceEntity=new ChebolePayOptions();
		

		final TextView zfbtextView = (TextView) findViewById(R.id.use_zfb);
		TextView name = (TextView) findViewById(R.id.parkdetail_name);

		TParkInfo_LocEntity locEntity = (TParkInfo_LocEntity) getIntent().getSerializableExtra("parkinfoLoc");
		if (locEntity == null) {
			parkInfo = (TParkInfoEntity) getIntent().getSerializableExtra("parkinfo");
		} else {
			parkInfo = locEntity.parkInfo;
			mCurrentLantitude = locEntity.latitude;
			mCurrentLongitude = locEntity.longitude;
		}
		// 判断service
		fuwulayout = (RelativeLayout) findViewById(R.id.fuwulayout);
		fuwulayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showservicedialog();
			}
		});

		// 检查是否不是已经有订单，一般来说，是订单详情过来的
		orderInfoFromDetailPage = (OrderEntity) getIntent().getSerializableExtra("orderInfo");

		if (mCurrentLantitude == 0 && mCurrentLongitude == 0) {
			mCurrentLantitude = orderInfoFromDetailPage.getLatitude();
			mCurrentLongitude = orderInfoFromDetailPage.getLongitude();
		}

		if (parkInfo.averagerat < 0.01)
			ratingBar.setRating(4);
		else
			ratingBar.setRating(parkInfo.averagerat);

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_empty).cacheInMemory(true)
				.cacheOnDisc(false).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		if (parkInfo != null)

		{
			// 计次分段的显示内容设置
			int feetype = parkInfo.feeType;
			if (orderInfoFromDetailPage != null) {
				feetype = orderInfoFromDetailPage.orderFeeType;
			}

			if (feetype == 1) {
				remindertext.setText("计费开始时间：");
				if (parkInfo.feeTypeSecMinuteOfActivite > 0) {
					parksava_text.setText(DateHelper.format(
							DateHelper.currentDateAddWithType(Calendar.MINUTE, parkInfo.feeTypeSecMinuteOfActivite),
							"yyyy-MM-dd HH:mm:ss"));

				}
			} else {
				if (parkInfo.feeTypeFixedMinuteOfInActivite > 0) {
					parksava_text.setText(DateHelper.format(
							DateHelper.currentDateAddWithType(Calendar.MINUTE, parkInfo.feeTypeFixedMinuteOfInActivite),
							"yyyy-MM-dd HH:mm:ss"));
				}

				// ImageView payment_selection_arrow = (ImageView)
				// findViewById(R.id.payment_selection_arrow);
				// zhifubaolayout.setEnabled(false);
				// payment_selection_arrow.setVisibility(View.GONE);
			}

			name.setText(parkInfo.parkname);
			// 距离时间
			// TextView distance = (TextView)
			// findViewById(R.id.parkdetail_distance);
			// distance.setText(" 距您" + tParkInfo_LocEntity.distance + "米");

			// 预计达到时间
			TextView adress = (TextView) findViewById(R.id.parkdetail_address);
			adress.setText(parkInfo.address);
			ImageView pevImg = (ImageView) findViewById(R.id.packdetail_img);

			imageUrls = new String[parkInfo.imgUrlArray.size()];
			if (imageUrls != null && imageUrls.length > 0) {
				TextView packdetail_img_number = (TextView) findViewById(R.id.packdetail_img_number);
				packdetail_img_number.setText("" + imageUrls.length);
				for (int i = 0; i < imageUrls.length; i++) {
					TParkInfo_ImgEntity tParkInfo_ImgEntity = parkInfo.imgUrlArray.get(i);
					imageUrls[i] = tParkInfo_ImgEntity.imgUrlHeader + tParkInfo_ImgEntity.imgUrlPath;
				}
				imageLoader.displayImage(imageUrls[0], pevImg, options);
			}
		}

		zhifubaolayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ProYuyueActivity.this);
				// View view =
				// inflater.inflate(R.layout.fragment_parklistdialog, null);
				builder.setTitle("请选择支付方式");
				builder.setSingleChoiceItems(Constants.PAY_WAY, getSelectedIndex(paywayString.getText().toString()),
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						selectedPayWay = which;
						paywayString.setText(Constants.PAY_WAY[which]);

					}
				})
						// Add action buttons
						.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {

					}
				}).show();
			}
		});

		youhuilist = new ArrayList<>();
		// youhui.setText("优惠： "+youhuistring);
		youhuilLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				/*
				 * if (COUPON_CHECK_FLAG == 1) {
				 * Toast.makeText(YuyueActivity.this, "停车券只能使用一次",
				 * Toast.LENGTH_LONG).show(); return; }
				 */
				// 当使用了停车券时，允许它在重新选择停车券
				if (priceEntity.payActualPrice <= 0.001 && COUPON_CHECK_FLAG == 0) {
					Toast.makeText(ProYuyueActivity.this, "已不需要支付", Toast.LENGTH_LONG).show();
					return;
				}

				Intent intent = new Intent(ProYuyueActivity.this, CouponDetailActivity.class);
				intent.putExtra("YUYUE", "1");

				startActivityForResult(intent, 1);

			}
		});

		youhuilLayout.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (COUPON_CHECK_FLAG == 0) {

				} else {

					BaseDialogFragment confirmDialog = new BaseDialogFragment();
					confirmDialog.setMessage("确认刪除吗?");
					confirmDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							COUPON_CHECK_FLAG = 0;
							getCalculatedPrice(orderInfoFromDetailPage == null
									? (priceEntity != null ? priceEntity.order : null) : orderInfoFromDetailPage);

						}
					});
					confirmDialog.setNegativeButton("取消", null);
					confirmDialog.show(getFragmentManager(), "");
				}
				return true;
			}
		});

		paymode1 = (RadioButton) findViewById(R.id.payradioButton1);
		paymode2 = (RadioButton) findViewById(R.id.payradioButton2);
		modefybtn = (Button) findViewById(R.id.add_parkinfo_modefybtn);
		yudingButton = (Button) findViewById(R.id.yudingButton);
		yuyue_detail.setText("" + parkInfo.detail);
		//getCalculatedPrice(orderInfoFromDetailPage);
		BindData();
	}

	public int getSelectedIndex(String payway) {
		if (Constants.PAY_WAY != null) {
			for (int i = 0; i < Constants.PAY_WAY.length; i++) {
				String p = Constants.PAY_WAY[i];
				if (p.trim().equals(payway.trim())) {
					return i;
				}
			}
		}
		return 0;
	}

	@Override
	protected void onActivityResult(int requestcode, int resultcode, Intent data) {
		// TODO Auto-generated method stub

		if (resultcode == PageConstant.YOUHUI_AC)

		{
			couponBean = (TCouponEntity) data.getSerializableExtra("couponBean");

			if (couponBean != null && couponBean.counponId != 0) {
				COUPON_CHECK_FLAG = 1;
				Double currmoney = couponBean.money.doubleValue();
				couponid = couponBean.counponId;
				/*getCalculatedPrice(orderInfoFromDetailPage == null ? (priceEntity != null ? priceEntity.order : null)
						: orderInfoFromDetailPage);*/
			} else {
				Toast.makeText(ProYuyueActivity.this, "停车券使用过程中出错，请重试", Toast.LENGTH_LONG).show();

			}

		} else if (resultcode == Constants.ResultCode.ORDER_LIST_RELOAD) {
			setResult(Constants.ResultCode.ORDER_LIST_RELOAD, data);
			finish();
		} else if (resultcode == Constants.ResultCode.NAVATIGOR_START) {
			setResult(Constants.ResultCode.NAVATIGOR_START, data);
			finish();
		} else if (resultcode == Constants.ResultCode.HOME) {

			finish();
		}

	}

	public void showphoto(View v) {
		switch (v.getId()) {
		case R.id.packdetail_img_frame:
			Intent intent = new Intent(ProYuyueActivity.this, UploadPhotoActivity.class);
			Bundle buidle = new Bundle();
			buidle.putSerializable("parkinfo", parkInfo);
			intent.putExtras(buidle);
			startActivity(intent);
			break;

		}

	}

	//获得价格
	private void getCalculatedPrice(final OrderEntity orderInfo) {

		StringBuffer parastring = new StringBuffer("?s=1");
		if (COUPON_CHECK_FLAG == 1 && couponid != null)
			parastring.append("&c=" + couponid);

		if (parkInfo == null) {
			Toast.makeText(ProYuyueActivity.this, "加载出错，请重新选", Toast.LENGTH_LONG).show();
			return;

		}
		if (selectServiceID != null && !selectServiceID.toString().trim().equals("")) {
			parastring.append("&selectedservice=" + selectServiceID.toString().trim());
		}

		/*没有加载进度条访问
		 * HttpRequest<ComResponse<ChebolePayOptions>> httpRequestAni = new
		 * HttpRequest<ComResponse<ChebolePayOptions>>( "/a/pay/price/" +
		 * parkInfo.parkId + parastring, new
		 * TypeToken<ComResponse<ChebolePayOptions>>() { }.getType()) {
		 * 
		 * @Override public void onSuccess(ComResponse<ChebolePayOptions> arg0)
		 * { // TODO Auto-generated method stub if (arg0 != null) { priceEntity
		 * = arg0.getResponseEntity(); if (orderInfo != null) {
		 * priceEntity.order = orderInfo; } } BindData(); }
		 * 
		 * @Override public void onFailed(String message) { if (priceEntity ==
		 * null) { priceEntity = new ChebolePayOptions(); } if (orderInfo !=
		 * null) { priceEntity.order = orderInfo; } // TODO Auto-generated
		 * method stub Toast.makeText(getApplicationContext(), "出错：" + message,
		 * Toast.LENGTH_LONG).show(); } };
		 * 
		 * httpRequestAni.execute();
		 */

		HttpRequestAni<ComResponse<ChebolePayOptions>> httpRequestAni = new HttpRequestAni<ComResponse<ChebolePayOptions>>(
				this, "/a/pay/price/" + parkInfo.parkId + parastring, new TypeToken<ComResponse<ChebolePayOptions>>() {
				}.getType()) {

			@Override
			public void callback(ComResponse<ChebolePayOptions> arg0) {
				// TODO Auto-generated method stub

				if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {
					if (arg0 != null) {
						priceEntity = arg0.getResponseEntity();
						if (orderInfo != null) {
							priceEntity.order = orderInfo;
						}
					}
					BindData();
				} else {
					if (priceEntity == null) {
						priceEntity = new ChebolePayOptions();
					}
					if (orderInfo != null) {
						priceEntity.order = orderInfo;
					}
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "出错：" + arg0.getErrorMessage(), Toast.LENGTH_LONG).show();
				}

			}

		};

		httpRequestAni.execute();
	}

	private void BindData() {
		// paytypenane,orgin_pay,yuyue_detail,coupon_price,total_price;

		if (priceEntity != null) {
			// text
			/*
			 * List<TParkService> list=new ArrayList<TParkService>(); for(int
			 * i=0;i<4;i++) { TParkService item=new TParkService();
			 * item.serviceName="除雪"; item.serviceId=i; list.add(item); }
			 * priceEntity.selectedServices=list;
			 */
			// parksava_text.setText(priceEntity.keepToDate);

			orgin_pay.setText("￥" + priceEntity.payOrginalPrice);

			total_price.setText("￥" + priceEntity.payActualPrice);
			if (COUPON_CHECK_FLAG == 1 && couponBean != null)
				coupon_price.setText("-￥" + couponBean.money);
			else
				coupon_price.setText("-￥0");

			if (priceEntity.userAllowance >= 0.001) {
				lijianReala.setVisibility(View.VISIBLE);
				//显示立减detail
				if (priceEntity.userAllowanceDescription != null && !priceEntity.userAllowanceDescription.equals("")) {
					lijiandetal.setVisibility(View.VISIBLE);
					lijiandetal.setText(priceEntity.userAllowanceDescription);
				} else
					lijiandetal.setVisibility(View.GONE);

				line2.setVisibility(View.VISIBLE);
				lijian_price.setText("-￥" + df.format(priceEntity.userAllowance));
			} else {

				lijiandetal.setVisibility(View.GONE);
				lijianReala.setVisibility(View.GONE);
				line2.setVisibility(View.GONE);
			}
			/*// 判断service
			if (priceEntity.selectedServices != null && priceEntity.selectedServices.size() > 0) {
				fuwulayout.setVisibility(View.VISIBLE);
				View view1 = findViewById(R.id.line33);
				view1.setVisibility(View.VISIBLE);
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < priceEntity.selectedServices.size(); i++) {
					if (priceEntity.selectedServices.get(i).forceSelection == 1) {
						sb.append("[" + priceEntity.selectedServices.get(i).serviceName + "] ");
					}
				}
				selecttext.setText(sb);
				service_pay.setText("￥" + priceEntity.serviceTotalFee);
			} else {
				fuwulayout.setVisibility(View.GONE);
				View view1 = findViewById(R.id.line33);
				view1.setVisibility(View.GONE);

			}*/
			// 显示服务选择界面
			if (firstshow) {
				showservicedialog();
				firstshow = false;
			}

		}

	}

	public void enablePayConfirmButton(boolean enable) {
		yudingButton.setEnabled(enable);
		// 用户选择了现金支付

		if (enable) {
			yudingButton.setText("确认，去支付");
		} else {
			yudingButton.setText("连接支付接口");
		}

	}

	/**
	 * 开始调用支付接口支付
	 */
	private void startToPay() {

		enablePayConfirmButton(false);

		String parastring = SessionUtils.city;
		if (parkInfo == null) {
			Toast.makeText(ProYuyueActivity.this, "加载出错，请重新选", Toast.LENGTH_LONG).show();
			return;
		}
		try {
			parastring = URLEncoder.encode(SessionUtils.city, "utf-8");
		} catch (Exception e) {
			Log.d("Pay", e.getMessage().toString());
		}
		String clientid = PushManager.getInstance().getClientid(getApplicationContext());
		//判断是否为空 为解决刷单情况
		clientid=UIUtils.checkCurrentClientID(clientid);
		HttpRequest<ComResponse<ChebolePayOptions>> httpRequestAni = new HttpRequest<ComResponse<ChebolePayOptions>>(
				Method.POST, priceEntity,
				"/a/pay/in/" + parkInfo.parkId + "?c=" + parastring + "&lt=" + mCurrentLantitude + "&ln="
						+ mCurrentLongitude + "&payway=" + selectedPayWay + "&clientId=" + clientid ,
				new TypeToken<ComResponse<ChebolePayOptions>>() {
				}.getType(), ChebolePayOptions.class) {

			@Override
			public void onSuccess(ComResponse<ChebolePayOptions> arg0) {

				// TODO Auto-generated method stub

				if (arg0 != null)
					priceEntity = arg0.getResponseEntity();

				if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {

					// 用户选择了现金支付
					// text选择微信

					if (selectedPayWay == 1 && priceEntity.payInfo != null) {
						resultunifiedorder = Util.decodeXml(priceEntity.payInfo);
						if (resultunifiedorder != null) {
							genPayReq(resultunifiedorder);

						} else {
							Toast.makeText(getApplicationContext(), "支付失败", Toast.LENGTH_SHORT).show();
							enablePayConfirmButton(true);
						}

					} else if (selectedPayWay == 0) {

						Runnable payRunnable = new Runnable() {
							@Override
							public void run() {
								// 构造PayTask 对象
								PayTask alipay = new PayTask(ProYuyueActivity.this);
								// 调用支付接口，获取支付结果
								String result = alipay.pay(priceEntity.payInfo);

								Message msg = new Message();
								msg.what = SDK_PAY_FLAG;
								msg.obj = result;
								mHandler.sendMessage(msg);
							}
						};
						Thread payThread = new Thread(payRunnable);
						payThread.start();
					} else if (selectedPayWay == 2) {

					}

				} else if (arg0.getResponseStatus() == ComResponse.STATUS_FAIL) {
					enablePayConfirmButton(true);
					if (arg0.getExtendResponseContext() != null && arg0.getExtendResponseContext().equals("pass")) {
						paychangeStatus(Constants.PAYMENT_STATUS_FINISH, arg0.getErrorMessage());
					} else {
						BaseDialogFragment confirmDialog = new BaseDialogFragment();
						confirmDialog.setMessage(arg0.getErrorMessage());
						confirmDialog.setPositiveButton("确认", null);
						confirmDialog.show(getFragmentManager(), "");
					}
				}
			}

			@Override
			public void onFailed(String message) {
				enablePayConfirmButton(true);
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "出错：" + message, Toast.LENGTH_LONG).show();
			}
		};

		httpRequestAni.execute();

	}

	public void pay(View v) {

		// 计次分段的显示内容设置
		int feetype = parkInfo.feeType;
		if (orderInfoFromDetailPage != null) {
			feetype = orderInfoFromDetailPage.orderFeeType;
		}
		String message = "";
		if (feetype == 1) {
			if (parkInfo.feeTypeSecMinuteOfActivite > 0) {
				message = "付款成功后，" + parkInfo.feeTypeSecMinuteOfActivite + "分钟后开始计费，确认继续支付?";

			}
		} else {
			if (parkInfo.feeTypeFixedMinuteOfInActivite > 0) {
				message = "付款成功后，" + parkInfo.feeTypeFixedMinuteOfInActivite + "分钟内到达才能保证车位，确认继续支付?";
			}
		}

		if (!message.trim().equals("")) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProYuyueActivity.this);
			alertDialog.setTitle("温馨提示");
			alertDialog.setMessage(message);
			alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					startToPay();
				}
			});
			alertDialog.setNegativeButton("取消", null);
			alertDialog.create().show();
		} else {
			startToPay();
		}

	}

	private void paychangeStatus(int paystatus, String message) {

		// 下面是更新订单状态
		HttpRequest<ComResponse<TParkInfo_Py>> httpRequestAni = new HttpRequest<ComResponse<TParkInfo_Py>>(Method.POST,
				priceEntity, "/a/pay/update/" + (priceEntity.order == null ? 0 : priceEntity.order.getOrderId()) + "/"
						+ +priceEntity.paymentId + "/" + paystatus,
				new TypeToken<ComResponse<TParkInfo_Py>>() {
				}.getType(), ChebolePayOptions.class) {

			@Override
			public void onSuccess(ComResponse<TParkInfo_Py> arg0) {
				if (arg0 != null) {
					if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {

					} else if (arg0.getResponseStatus() == ComResponse.STATUS_FAIL) {

					}
				}
			}

			@Override
			public void onFailed(String message) {
				Toast.makeText(getApplicationContext(), "异常：" + message, Toast.LENGTH_LONG).show();
			}
		};

		httpRequestAni.execute();

		if (paystatus == Constants.PAYMENT_STATUS_FINISH || paystatus == Constants.PAYMENT_STATUS_PENDING) {

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProYuyueActivity.this);
			alertDialog.setTitle("温馨提示");
			if (lijianReala.getVisibility() == View.GONE) {
				alertDialog.setMessage(
						paystatus == Constants.PAYMENT_STATUS_FINISH ? "预约成功,请在抵达停车场后扫码确认" : "当前支付接口正在处理,请等待...");
			} else {
				alertDialog.setMessage(paystatus == Constants.PAYMENT_STATUS_FINISH
						? Html.fromHtml(
								"<font size=\"3\" color=\"black\">预约成功,已为你减去</font><font size=\"3\" color=\"red\">"
										+ lijian_price.getText().toString() + "</font>"
										+ "<font size=\"3\" color=\"black\">,请在抵达停车场后扫码确认</font>")
						// "预约成功,"+"已为你减去"++",请在抵达停车场后扫码确认"
						: "当前支付接口正在处理,请等待...");
			}
			// Html.fromHtml("<font
			// color=\"red\">"+lijian_price.getText().toString()+"</font>")
			if (paystatus == Constants.PAYMENT_STATUS_FINISH) {
				alertDialog.setPositiveButton("现在就去", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent intent = getIntent();
						Bundle buidle = new Bundle();
						buidle.putSerializable("orderinfo", priceEntity.order);
						intent.putExtras(buidle);
						// startActivityForResult(intent,
						// Constants.ResultCode.NAVATIGOR_START);
						setResult(Constants.ResultCode.NAVATIGOR_START, intent);
						finish();
					}
				});
			}
			alertDialog.setNegativeButton("查看订单", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Intent intent = new Intent(ProYuyueActivity.this, OrderDetailActivity.class);
					Bundle buidle = new Bundle();
					buidle.putSerializable("orderinfo", priceEntity.order);
					buidle.putBoolean("isRefresh", true);
					buidle.putInt("fromyuyue", 2);
					intent.putExtras(buidle);
					startActivityForResult(intent, Constants.ResultCode.NAVATIGOR_START);
				}
			});

			alertDialog.create().show();
		} else {
			Toast.makeText(getApplicationContext(), "支付未进行,您可以继续支付或在[泊车记录]里找到订单并继续支付", Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * 更新支付接口调用后的状态
	 * 
	 * @param paystatus
	 */
	private void paychangeStatus(int paystatus) {
		paychangeStatus(paystatus, paystatus == Constants.PAYMENT_STATUS_FINISH ? "请在抵达停车场后扫码确认" : "当前支付接口正在处理,请等待...");
	}

	// 微信支付相关代码
	/**
	 * 
	 * 
	 */
	PayReq req;
	Map<String, String> resultunifiedorder;
	final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);

	private void genPayReq(Map<String, String> result) {
		if (isWXAppInstalledAndSupported(getApplicationContext(), msgApi)) {

			req = new PayReq();
			req.appId = result.get("appid");
			req.partnerId = result.get("partnerid");
			req.prepayId = result.get("prepayid");
			req.packageValue = "Sign=WXPay";
			req.nonceStr = result.get("noncestr");
			req.timeStamp = result.get("timestamp");
			req.sign = result.get("sign");

			// +
			// "req.packageValue"+req.packageValue.toString()+"req.nonceStr"+req.nonceStr.toString()
			// +"req.timeStamp"+req.timeStamp.toString()+"req.sign"+req.sign.toString()
			// Toast.makeText(getApplicationContext(),
			// "req.partnerId"+req.partnerId.toString(),
			// Toast.LENGTH_SHORT).show();
			// Toast.makeText(getApplicationContext(),
			// "req.prepayId"+req.prepayId.toString(),
			// Toast.LENGTH_SHORT).show();
			// Toast.makeText(getApplicationContext(),
			// "req.packageValue"+req.packageValue.toString(),
			// Toast.LENGTH_SHORT).show();
			// Toast.makeText(getApplicationContext(),
			// "req.nonceStr"+req.nonceStr.toString(),
			// Toast.LENGTH_SHORT).show();
			// Toast.makeText(getApplicationContext(),
			// "req.timeStamp"+req.timeStamp.toString(),
			// Toast.LENGTH_SHORT).show();
			// Toast.makeText(getApplicationContext(),
			// "req.sign"+req.sign.toString(), Toast.LENGTH_SHORT).show();
			msgApi.registerApp(Constants.APP_ID);
			msgApi.sendReq(req);

			// Toast.makeText(getApplicationContext(),
			// "req.appId"+req.appId.toString(), Toast.LENGTH_SHORT).show();
			// Toast.makeText(getApplicationContext(),
			// "req.partnerId"+req.partnerId.toString(),
			// Toast.LENGTH_SHORT).show();

			// List<NameValuePair> signParams = new LinkedList<NameValuePair>();
			// signParams.add(new BasicNameValuePair("appid", req.appId));
			// signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
			// signParams.add(new BasicNameValuePair("package",
			// req.packageValue));
			// signParams.add(new BasicNameValuePair("partnerid",
			// req.partnerId));
			// signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
			// signParams.add(new BasicNameValuePair("timestamp",
			// req.timeStamp));
			// //signParams.add(new BasicNameValuePair("key", appkey));
			//
			// req.sign = Util.genAppSign(signParams,appkey);

			// msgApi.registerApp(Constants.APP_ID);
			// msgApi.sendReq(req);

		} else {
			enablePayConfirmButton(true);
		}

	}

	private boolean isWXAppInstalledAndSupported(Context context, IWXAPI api) {
		// LogOutput.d(TAG, "isWXAppInstalledAndSupported");
		sIsWXAppInstalledAndSupported = api.isWXAppInstalled() && api.isWXAppSupportAPI();
		if (!sIsWXAppInstalledAndSupported) {
			// Log.w(TAG, "~~~~~~~~~~~~~~微信客户端未安装，请确认");
			// Toast.showToast(context, "微信客户端未安装，请确认");
			Toast.makeText(getApplicationContext(), "微信客户端未安装，请确认", Toast.LENGTH_SHORT).show();
		}

		return sIsWXAppInstalledAndSupported;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (Bimp.WXback == Constants.PAYMENT_STATUS_FINISH) {
			paychangeStatus(Constants.PAYMENT_STATUS_FINISH);
			Bimp.WXback = 0;
		} else if (Bimp.WXback == Constants.PAYMENT_STATUS_PENDING) {
			paychangeStatus(Constants.PAYMENT_STATUS_PENDING);
			Bimp.WXback = 0;

		} else if (Bimp.WXback == Constants.PAYMENT_STATUS_EXCPTION) {
			Bimp.WXback = 0;
			enablePayConfirmButton(true);
			paychangeStatus(Constants.PAYMENT_STATUS_EXCPTION);
		} else {
			enablePayConfirmButton(true);

		}

	}

	private void showservicedialog() {/*

		// TODO Auto-generated method stub
		if (priceEntity.selectedServices != null && priceEntity.selectedServices.size() > 0) {

			FuwulistDialogFragment dialog = new FuwulistDialogFragment(priceEntity.selectedServices) {

				@Override
				public void setchoice(List<TParkService> selects) {
					selectServiceID = new StringBuffer("");
					priceEntity.selectedServices = selects;
					StringBuffer selectstring = new StringBuffer();
					// priceEntity.selectedServices=selectservice;

					for (int i = 0; i < selects.size(); i++) {

						if (selects.get(i).forceSelection == 1) {
							selectServiceID.append(selects.get(i).id + ",");
							// selectstring.append("["+selects.get(i).serviceName+"]
							// ");
						}
					}
					if (selectServiceID.toString().trim().equals("")) {
						// 后台服务判断标识
						selectServiceID.append("c");

					}
					// selecttext.setText(selectstring);
					getCalculatedPrice(orderInfoFromDetailPage == null
							? (priceEntity != null ? priceEntity.order : null) : orderInfoFromDetailPage);
				}

			};
			dialog.show(getFragmentManager(), "parklistdialog");
			// 弹出完毕
		}

	*/}
}
