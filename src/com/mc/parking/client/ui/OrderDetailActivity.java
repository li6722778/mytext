package com.mc.parking.client.ui;

import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Request.Method;
import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushManager;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.client.Constants;
import com.mc.parking.client.PackingApplication;
import com.mc.park.client.R;
import com.mc.parking.client.entity.ChebolePayOptions;
import com.mc.parking.client.entity.OrderEntity;
import com.mc.parking.client.entity.TOptions;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkInfo_Py;
import com.mc.parking.client.entity.TShare;
import com.mc.parking.client.entity.Tredirecturl;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.BaseActivity;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.HttpRequest;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.utils.DateHelper;
import com.mc.parking.client.utils.Notice;
import com.mc.parking.client.utils.PayResult;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;
import com.mc.parking.client.utils.WXshareutil;
import com.mc.parking.zxing.camera.MipcaActivityCapture;
import com.mc.wxutils.Util;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class OrderDetailActivity extends BaseActivity {

	int SCAN_SUCCESS = 11;
	private OrderEntity orderinfo;

	private TextView messageView;
	private RadioButton progress1, progress2, progress3, progress4;

	private OrderActivity orderActivity;

	private RelativeLayout scanbutton;

	private int fromyuyue;

	private double hasPay = 0;
	private ChebolePayOptions priceEntity = new ChebolePayOptions();
	private LinearLayout order_orderpaydate_layout, order_pay_info, order_orderstartdate_layout,
			order_orderenddate_layout;
	private TextView ordernumber, order_orderdate, order_orderpaydate, order_orderstartdate, order_orderenddate;
	private Button yudingButton, goNavi;
	private RelativeLayout haixufuLayout, update_couponlayout, lijianRelativeLayout;
	private View orderdetail_last_view, orderdetail_coupon_view, lijian_view;
	private TextView order_detail_hour, order_detail_hour_value, order_detail_coupon, order_detail_total,
			order_detail_hour_hasvalue, order_detail_hour_lijian, service_detail;
	private static final int SDK_PAY_FLAG = 1;
	private static final int SHARE_SUCCESS = 2;
	private static final int SEND = 3;
	private com.mc.parking.receiver.ShareBroadcastReceiver ShareBroadcastReceiver;
	private boolean isFromFinshed;
	private double f = 0.01;
	private PopupWindow window;
	private static final String[] PAY_WAY = new String[] { "支付宝", "现金支付", "微信支付" };
	private int selectedIndex = 0;
	private ImageButton shareButton;
	// 打电话图标
	ImageView teleimage;
	// 微信添加相关代码
	private static String myresultcode;
	private static int currentPayway;

	private String getuniuqeurl;
	// 判断是否安装微信
	private Boolean sIsWXAppInstalledAndSupported;

	public IWXAPI api;

	public void backTo(View v) {

		if (fromyuyue == 2) {
			Intent intent = new Intent();
			setResult(Constants.ResultCode.HOME, intent);
		}
		finish();
	}

	@Override
	public void onBackPressed() {
		if (fromyuyue == 2) {
			Intent intent = new Intent();
			setResult(Constants.ResultCode.HOME, intent);
		}
		finish();
	}

	public void gotoNavi(View v) {
		Intent intent = new Intent();
		Bundle buidle = new Bundle();
		buidle.putSerializable("orderinfo", orderinfo);
		intent.putExtras(buidle);
		setResult(Constants.ResultCode.NAVATIGOR_START, intent);
		finish();
	}

	/**
	 * 支付handler
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			yudingButton.setClickable(true);
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

	/**
	 * 分享成功回调handler
	 */
	private Handler sHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHARE_SUCCESS:
				// 查询是否分享过
				if (SessionUtils.loginUser != null && SessionUtils.loginUser.userid != null && orderinfo != null
						&& getuniuqeurl != null) {
					HttpRequest<ComResponse<TShare>> httpRequest = new HttpRequest<ComResponse<TShare>>(
							"/a/isshare/find?id=" + SessionUtils.loginUser.userid + "&url=" + getuniuqeurl,
							new TypeToken<ComResponse<TShare>>() {
					}.getType()) {
						@Override
						public void onFailed(String message) {

						}

						@Override
						public void onSuccess(ComResponse<TShare> arg0) {
							if (arg0 != null) {
								if (arg0.getResponseStatus() == 0) {
									Toast.makeText(OrderDetailActivity.this, "获得分享优惠劵成功", Toast.LENGTH_SHORT).show();
									Intent intent = new Intent(OrderDetailActivity.this, Get_coupnActivity.class);
									startActivity(intent);

								}

								setResult(Constants.ResultCode.HOME);
								finish();
							}

						}

					};

					httpRequest.execute();
				}

				break;

			default:
				break;
			}
		};
	};

	/**
	 * 获取唯一url
	 */

	public String getuniqueurl() {
		String uniqueurl = null;
		if (orderinfo != null) {
			String orederid = Long.toString(orderinfo.getOrderId()) + "0";
			String userid = Long.toString(SessionUtils.loginUser.userid);
			uniqueurl = orederid + userid;
		}
		return uniqueurl;

	}

	@Override
	protected void onResume() {
		// 保存当前实例
		PackingApplication.getInstance().setCurrentActivity(this);
		super.onResume();
		if (Bimp.WXback == Constants.PAYMENT_STATUS_FINISH) {
			paychangeStatus(Constants.PAYMENT_STATUS_FINISH);
			Bimp.WXback = 0;
		} else if (Bimp.WXback == Constants.PAYMENT_STATUS_PENDING) {
			paychangeStatus(Constants.PAYMENT_STATUS_PENDING);
			Bimp.WXback = 0;

		} else if (Bimp.WXback == Constants.PAYMENT_STATUS_EXCPTION) {
			Bimp.WXback = 0;
			paychangeStatus(Constants.PAYMENT_STATUS_EXCPTION);
		}

	}

	/**
	 * 更新支付接口调用后的状态
	 * 
	 * @param paystatus
	 */
	private void paychangeStatus(final int paystatus) {

		// 下面是更新订单状态
		HttpRequest<ComResponse<TParkInfo_Py>> httpRequestAni = new HttpRequest<ComResponse<TParkInfo_Py>>(Method.POST,
				priceEntity,
				"/a/pay/update/" + (priceEntity.order == null ? 0 : priceEntity.order.getOrderId()) + "/"
						+ +priceEntity.paymentId + "/" + paystatus + "?&u=true",
				new TypeToken<ComResponse<TParkInfo_Py>>() {
				}.getType(), ChebolePayOptions.class) {

			@Override
			public void onSuccess(ComResponse<TParkInfo_Py> arg0) {
				if (arg0 != null) {
					if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {

						if (paystatus == Constants.PAYMENT_STATUS_FINISH
								|| paystatus == Constants.PAYMENT_STATUS_PENDING) {

							AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderDetailActivity.this);
							alertDialog.setTitle(paystatus == Constants.PAYMENT_STATUS_FINISH ? "支付成功" : "支付确认中");
							alertDialog.setMessage(
									paystatus == Constants.PAYMENT_STATUS_FINISH ? "谢谢您的光临,请离场" : "当前支付接口正在处理,请等待...");

							if (paystatus == Constants.PAYMENT_STATUS_FINISH) {
								orderinfo.setOrderStatus(Constants.ORDER_TYPE_FINISH);
								orderinfo.setEndDate(new Date());// 这里应该取服务端时间。。。
								// setProgressStatus();
								fetchLatestOrder();
								alertDialog.setNegativeButton("返回订单列表", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										showPopwindow();
									}
								});
							} else {
								alertDialog.setNegativeButton("刷新", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										fetchLatestOrder();
									}
								});
							}

							alertDialog.create().show();
						} else {
							Toast.makeText(getApplicationContext(), "支付未进行,您可以重新为此订单进行支付", Toast.LENGTH_LONG).show();
						}

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

		// if (paystatus == Constants.PAYMENT_STATUS_FINISH
		// || paystatus == Constants.PAYMENT_STATUS_PENDING) {
		//
		// AlertDialog.Builder alertDialog = new AlertDialog.Builder(
		// OrderDetailActivity.this);
		// alertDialog
		// .setTitle(paystatus == Constants.PAYMENT_STATUS_FINISH ? "支付成功"
		// : "支付确认中");
		// alertDialog
		// .setMessage(paystatus == Constants.PAYMENT_STATUS_FINISH ?
		// "谢谢您的光临,请离场"
		// : "当前支付接口正在处理,请等待...");
		//
		// if (paystatus == Constants.PAYMENT_STATUS_FINISH) {
		// orderinfo.setOrderStatus(Constants.ORDER_TYPE_FINISH);
		// orderinfo.setEndDate(new Date());// 这里应该取服务端时间。。。
		// //setProgressStatus();
		// fetchLatestOrder();
		// alertDialog.setNegativeButton("返回订单列表",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// showPopwindow();
		// }
		// });
		// } else {
		// alertDialog.setNegativeButton("刷新",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// fetchLatestOrder();
		// }
		// });
		// }
		//
		// alertDialog.create().show();
		// } else {
		// Toast.makeText(getApplicationContext(), "支付未进行,您可以重新为此订单进行支付",
		// Toast.LENGTH_LONG).show();
		// }

	}

	/**
	 * 时间颜色跟着当前进程变化
	 * 
	 * @param indexDate
	 */
	private void setHighLightDate(int indexDate) {

		goNavi.setVisibility(View.GONE);
		order_orderdate.setTextColor(getResources().getColor(R.color.gray));
		order_orderpaydate.setTextColor(getResources().getColor(R.color.gray));
		order_orderstartdate.setTextColor(getResources().getColor(R.color.gray));
		order_orderenddate.setTextColor(getResources().getColor(R.color.gray));

		switch (indexDate) {
		case 0:
			order_orderdate.setTextColor(getResources().getColor(R.color.green_dark));
			yudingButton.setVisibility(View.VISIBLE);
			scanbutton.setVisibility(View.GONE);
			teleimage.setVisibility(View.VISIBLE);
			break;
		case 1:
			order_orderpaydate.setTextColor(getResources().getColor(R.color.green_dark));
			yudingButton.setVisibility(View.GONE);
			scanbutton.setVisibility(View.VISIBLE);
			teleimage.setVisibility(View.VISIBLE);
			goNavi.setVisibility(View.VISIBLE);
			break;
		case 2:

			order_orderstartdate.setTextColor(getResources().getColor(R.color.green_dark));
			yudingButton.setVisibility(View.GONE);
			scanbutton.setVisibility(View.VISIBLE);
			teleimage.setVisibility(View.VISIBLE);
			break;
		case 3:
			order_orderenddate.setTextColor(getResources().getColor(R.color.green_dark));
			yudingButton.setVisibility(View.GONE);
			scanbutton.setVisibility(View.GONE);
			teleimage.setVisibility(View.GONE);
			break;
		}

	}

	/**
	 * 设置当前进程状态
	 */
	private void setProgressStatus() {

		List<TParkInfo_Py> pys = orderinfo.getPay();
		String orderId = "" + orderinfo.getOrderId();
		if (pys != null) {
			double pay = 0.0d;
			double payPending = 0.0d;
			for (TParkInfo_Py py : pys) {
				if (py.getPayMethod() != Constants.PAYMENT_SERVICE) {
					if (py.getAckStatus() == Constants.PAYMENT_STATUS_FINISH) {
						pay += (py.getPayActu() + py.getCouponUsed());
					} else if (py.getAckStatus() == Constants.PAYMENT_STATUS_PENDING) {
						payPending += py.getPayActu();
					}
				}
			}
			if (pay > 0) {
				orderId += "[已付款" + UIUtils.decimalPrice(pay) + "元]";
				hasPay = UIUtils.decimalPrice(pay);
			}
			if (payPending > 0) {
				orderId += "[正在付款" + UIUtils.decimalPrice(pay) + "元]";
			}
		}

		ordernumber.setText(orderId);

		if (orderinfo.getOrderDate() != null) {
			order_orderdate.setText(UIUtils.formatDate(OrderDetailActivity.this, orderinfo.getOrderDate().getTime()));
		}

		int indexHighLight = 0;
		if (progress1 != null && orderinfo != null) {
			if (orderinfo.getOrderStatus() == Constants.ORDER_TYPE_FINISH) {
				progress4.setChecked(true);
				if (orderinfo.orderFeeType == 0) {
					if (orderinfo.getParkInfo().feeType == 1) { // 分段收费才需要高亮出场时间
						setHighLightDate(3);
						indexHighLight = 3;
					}
				} else if (orderinfo.orderFeeType == 1) {
					setHighLightDate(3);
					indexHighLight = 3;
				}
			}

			if (orderinfo.getStartDate() != null) {
				progress3.setChecked(true);
				setHighLightDate(2);
				if (indexHighLight < 2) {
					indexHighLight = 2;
				}
			}

			TParkInfoEntity parkInfo = orderinfo.getParkInfo();
			if (pys != null) {
				for (TParkInfo_Py ps : pys) {
					if (ps.getAckStatus() == Constants.PAYMENT_STATUS_FINISH) {
						progress2.setChecked(true);
						setHighLightDate(1);
						if (indexHighLight < 1) {
							indexHighLight = 1;
						}
						break;
					}
				}
			}
			setHighLightDate(indexHighLight);
			// 支付时间获取
			String payDateString = "";
			if (pys != null) {
				for (int i = (pys.size() - 1); i >= 0; i--) {
					TParkInfo_Py ps = pys.get(i);// 找到最近的一次支付
					if (ps.getAckStatus() == Constants.PAYMENT_STATUS_FINISH
							|| ps.getAckStatus() == Constants.PAYMENT_STATUS_PENDING) {
						Date payDate = ps.getPayDate();
						payDateString = UIUtils.formatDate(OrderDetailActivity.this, payDate.getTime());
						if (ps.getAckStatus() == Constants.PAYMENT_STATUS_PENDING) {
							payDateString += "[支付中]";
						}
						break;
					}
				}
			}
			if (!payDateString.trim().equals("")) {
				order_orderpaydate_layout.setVisibility(View.VISIBLE);
				order_orderpaydate.setText(payDateString);
			}
			if (orderinfo.getStartDate() != null) {
				order_orderstartdate_layout.setVisibility(View.VISIBLE);
				order_orderstartdate
						.setText(UIUtils.formatDate(OrderDetailActivity.this, orderinfo.getStartDate().getTime()));
			}
			if (orderinfo.getEndDate() != null) {
				order_orderenddate_layout.setVisibility(View.VISIBLE);
				order_orderenddate
						.setText(UIUtils.formatDate(OrderDetailActivity.this, orderinfo.getEndDate().getTime()));
			}

		}

		// 以下针对车位管理员查看，需要隐藏支付等按钮
		if (SessionUtils.loginUser != null) {
			if (!(SessionUtils.loginUser.userType >= Constants.USER_TYPE_NORMAL
					&& SessionUtils.loginUser.userType < Constants.USER_TYPE_NORMAL + 10) || isFromFinshed) {
				adminViewStatus();

			}
		}

		if (orderinfo != null && (orderinfo.getOrderStatus() == Constants.ORDER_TYPE_FINISH
				|| orderinfo.getOrderStatus() == Constants.ORDER_TYPE_PENDING)) {

			if (pys != null) {
				double counponUsedMoney = 0.0;
				double parkSpentHour = 0.0;
				double payActualPrice = 0.0;
				double hasAlreadyPay = 0.0;
				double yingfu = 0.0;
				double userallow = 0.0;
				// 服务总价格
				double servicetotalfee = 0.0;

				Date startDate = orderinfo.getStartDate();
				Date endDate = orderinfo.getEndDate();

				if (startDate != null && endDate != null) {
					int mins = DateHelper.diffDateForMin(endDate, startDate);
					double mhour = mins / 60.0;
					parkSpentHour = Math.ceil(mhour); // 总共停车这么多小时
				}

				for (int i = (pys.size() - 1); i >= 0; i--) {
					TParkInfo_Py ps = pys.get(i);// 找到最近的一次支付
					if (ps.getAckStatus() == Constants.PAYMENT_STATUS_FINISH
							|| ps.getAckStatus() == Constants.PAYMENT_STATUS_PENDING) {

						if (SessionUtils.loginUser.userType >= Constants.USER_TYPE_NORMAL
								&& SessionUtils.loginUser.userType < Constants.USER_TYPE_NORMAL + 10) {
							if (ps.getPayMethod() == Constants.PAYMENT_LIJIAN) {
								userallow = ps.getCouponUsed();
							} else {
								counponUsedMoney += ps.getCouponUsed();
								//计算服务价格
								if (ps.getPayMethod() == Constants.PAYMENT_SERVICE)
									servicetotalfee = +ps.getPayActu();
							}

						} else {
							counponUsedMoney += ps.getCouponUsed();
						}
						if (ps.getPayMethod() != Constants.PAYMENT_SERVICE) {
							yingfu += (ps.getPayActu() + ps.getCouponUsed());
						}

					}
				}

				// 管理员不管立减
				if (SessionUtils.loginUser != null) {
					if ((SessionUtils.loginUser.userType >= Constants.USER_TYPE_NORMAL
							&& SessionUtils.loginUser.userType < Constants.USER_TYPE_NORMAL + 10)) {

						displayPayInfo(UIUtils.decimalPrice(counponUsedMoney), parkSpentHour, 0.0d,
								UIUtils.decimalPrice(hasPay), UIUtils.decimalPrice(yingfu), true,
								orderinfo.orderFeeType, userallow, servicetotalfee);
					} else {

						displayPayInfo(UIUtils.decimalPrice(counponUsedMoney), parkSpentHour, 0.0d,
								UIUtils.decimalPrice(hasPay), UIUtils.decimalPrice(yingfu), true,
								orderinfo.orderFeeType, 0, servicetotalfee);

					}
				}

			}

		}

	}

	/**
	 * 以下针对车位管理员查看，需要隐藏支付等按钮
	 * 
	 * @param v
	 */
	// scanbutton yudingButton goNavi
	void adminViewStatus() {
		scanbutton.setVisibility(View.GONE);

		yudingButton.setVisibility(View.GONE);
		teleimage.setVisibility(View.GONE);
		goNavi.setVisibility(View.GONE);
	}

	/**
	 * 跳转到收银台
	 * 
	 * @param v
	 */

	public void gotoPay(View v) {

		selectedIndex = 0;
		if (orderinfo.getStartDate() != null) { // 这样肯定是出场支付

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// View view =
			// inflater.inflate(R.layout.fragment_parklistdialog, null);
			builder.setTitle("请选择支付方式");
			builder.setSingleChoiceItems(PAY_WAY, 0, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					selectedIndex = which;
				}
			})
					// Add action buttons
					.setPositiveButton("确认", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {

							if (selectedIndex == 0) {// 网上支付
								Runnable payRunnable = new Runnable() {

									@Override
									public void run() {
										// 构造PayTask 对象
										PayTask alipay = new PayTask(OrderDetailActivity.this);
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
							} else if (selectedIndex == 1) {
								TParkInfoEntity parkInfo = orderinfo.getParkInfo();
								if (parkInfo != null) {
									String clientid = PushManager.getInstance().getClientid(getApplicationContext());
									//判断是否为空 为解决刷单情况
									clientid=UIUtils.checkCurrentClientID(clientid);
									String scanResult = "http://localhost#" + parkInfo.parkId + "&clientId=" + clientid;
									scanForOut(orderinfo.getOrderId(), scanResult, Constants.PAYMENT_TYPE_CASH);
								}
							} else if (selectedIndex == 2) {
								TParkInfoEntity parkInfo = orderinfo.getParkInfo();

								scanForOut(orderinfo.getOrderId(), myresultcode, Constants.PAYMENT_TYPE_WEIXIN);

							}

						}
					}).show();

		} else {
			Intent intent = new Intent(OrderDetailActivity.this, YuyueActivity.class);
			Bundle buidle = new Bundle();
			buidle.putSerializable("parkinfo", orderinfo.getParkInfo());
			buidle.putSerializable("orderInfo", orderinfo);
			intent.putExtras(buidle);
			startActivityForResult(intent, 0);
		}

	}

	public void fetchLatestOrder() {
		// 下面是更新订单状态
		HttpRequestAni<OrderEntity> httpRequestAni = new HttpRequestAni<OrderEntity>(OrderDetailActivity.this,
				"/a/order/find/" + orderinfo.getOrderId(), new TypeToken<OrderEntity>() {
				}.getType()) {

			@Override
			public void callback(OrderEntity arg0) {
				if (arg0 != null) {
					orderinfo = arg0;
					setProgressStatus();
				}
			}

		};
		httpRequestAni.execute();
	}

	private boolean showsharebutton() {
		boolean timeresult = timeout();
		// 如果登录用户不为空 ，并且为未超时
		if (SessionUtils.loginUser != null && timeresult == false) {

			if (SessionUtils.loginUser.userType == Constants.USER_TYPE_NORMAL) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_order_detail);
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
		api.registerApp(Constants.APP_ID);
		callback();
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.actionbar_topbar);
		Button startButton = (Button) findViewById(R.id.start);
		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopwindow();

			}
		});

		shareButton = (ImageButton) findViewById(R.id.sharebutton);
		shareButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopwindow();
			}
		});
		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		titleView.setText(R.string.order_detail_title);
		orderActivity = new OrderActivity();
		orderinfo = (OrderEntity) getIntent().getSerializableExtra("orderinfo");
		boolean isRefresh = (Boolean) getIntent().getBooleanExtra("isRefresh", false);
		fromyuyue = getIntent().getIntExtra("fromyuyue", 0);
		isFromFinshed = (Boolean) getIntent().getBooleanExtra("isFromFinshed", false);
		ordernumber = (TextView) findViewById(R.id.order_ordernumber);

		TextView parkingname = (TextView) findViewById(R.id.parkdetail_name);
		TextView parkdetail_address = (TextView) findViewById(R.id.parkdetail_address);
		order_orderdate = (TextView) findViewById(R.id.order_orderdate);
		TextView order_feedetail = (TextView) findViewById(R.id.order_feedetail);

		progress1 = (RadioButton) findViewById(R.id.radio0);
		progress2 = (RadioButton) findViewById(R.id.radio1);
		progress3 = (RadioButton) findViewById(R.id.radio2);
		progress4 = (RadioButton) findViewById(R.id.radio3);
		order_orderpaydate_layout = (LinearLayout) findViewById(R.id.order_orderpaydate_layout);
		order_orderstartdate_layout = (LinearLayout) findViewById(R.id.order_orderstartdate_layout);
		order_orderenddate_layout = (LinearLayout) findViewById(R.id.order_orderenddate_layout);
		order_orderpaydate = (TextView) findViewById(R.id.order_orderpaydate);
		order_orderstartdate = (TextView) findViewById(R.id.order_orderstartdate);
		order_orderenddate = (TextView) findViewById(R.id.order_orderenddate);
		yudingButton = (Button) findViewById(R.id.yudingButton);
		lijianRelativeLayout = (RelativeLayout) findViewById(R.id.lijianRelativeLayout);
		order_detail_hour_lijian = (TextView) findViewById(R.id.order_detail_hour_lijian);
		order_pay_info = (LinearLayout) findViewById(R.id.order_detail);
		order_detail_hour = (TextView) findViewById(R.id.order_detail_hour);
		order_detail_hour_value = (TextView) findViewById(R.id.order_detail_hour_value);
		order_detail_total = (TextView) findViewById(R.id.order_detail_total);
		order_detail_hour_hasvalue = (TextView) findViewById(R.id.order_detail_hour_hasvalue);
		order_detail_coupon = (TextView) findViewById(R.id.order_detail_coupon);

		haixufuLayout = (RelativeLayout) findViewById(R.id.haixufuLayout);
		update_couponlayout = (RelativeLayout) findViewById(R.id.update_couponlayout);
		orderdetail_last_view = (View) findViewById(R.id.orderdetail_last_view);
		orderdetail_coupon_view = (View) findViewById(R.id.orderdetail_coupon_view);
		service_detail = (TextView) findViewById(R.id.service_detail);

		lijian_view = findViewById(R.id.lijian_view);
		teleimage = (ImageView) findViewById(R.id.teleimage);
		teleimage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (orderinfo != null && orderinfo.getParkInfo() != null && orderinfo.getParkInfo().teleable == 1) {
					Intent phoneitend = new Intent(Intent.ACTION_DIAL,
							Uri.parse("tel:" + orderinfo.getParkInfo().parkPhone));
					startActivity(phoneitend);
				} else {
					Toast.makeText(getApplicationContext(), "该停车场暂不支持", Toast.LENGTH_SHORT).show();
				}
			}
		});

		goNavi = (Button) findViewById(R.id.goNavi);
		TParkInfoEntity parkInfo = null;
		if (orderinfo.getParkInfo() != null) {
			parkInfo = orderinfo.getParkInfo();
		}
		order_feedetail.setText(orderinfo.getOrderDetail() == null ? "" : orderinfo.getOrderDetail());
		parkingname.setText(parkInfo == null ? "" : parkInfo.parkname);
		parkdetail_address.setText(parkInfo == null ? "" : parkInfo.address);
		LinearLayout serviceli = (LinearLayout) findViewById(R.id.service_line);
		if (orderinfo.selectedServicesDetail == null || orderinfo.selectedServicesDetail.toString().trim().equals("")) {

			serviceli.setVisibility(View.GONE);

		} else {
			service_detail.setText(orderinfo.selectedServicesDetail);
			serviceli.setVisibility(View.VISIBLE);
		}
		// 计次收费不需要显示出场图标
		View view3 = (View) findViewById(R.id.view3);
		RadioButton radio3 = (RadioButton) findViewById(R.id.radio3);
		if (orderinfo.orderFeeType == 0) {
			if (parkInfo.feeType != 1) {
				progress3.setText("已确认");
				view3.setVisibility(View.GONE);
				radio3.setVisibility(View.GONE);
			}
		} else if (orderinfo.orderFeeType != 1) {
			progress3.setText("已确认");
			view3.setVisibility(View.GONE);
			radio3.setVisibility(View.GONE);
		}
		messageView = (TextView) findViewById(R.id.order_ordermessage);

		scanbutton = (RelativeLayout) findViewById(R.id.gotocheckButton);

		scanbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (orderinfo != null && orderinfo.getParkInfo() != null) {
					if (orderinfo.getParkInfo().feeType != 1 && orderinfo.getStartDate() == null) {
						getorderEntryByID(orderinfo.getOrderId());
					} else {
						Intent intent = new Intent();
						intent.setClass(getApplicationContext(), MipcaActivityCapture.class);
						intent.putExtra("mode", Constants.USER_SCAN_BACK);
						startActivityForResult(intent, 11);
					}

				}

			}
		});

		setProgressStatus();
		if (isRefresh) {
			fetchLatestOrder();
		}

		// 保存当前实例
		PackingApplication.getInstance().setCurrentActivity(OrderDetailActivity.this);
	}

	private void callback() {
		// TODO Auto-generated method stub
		ShareBroadcastReceiver = new com.mc.parking.receiver.ShareBroadcastReceiver();
		ShareBroadcastReceiver
				.setOnReceivedMessageListener(new com.mc.parking.receiver.ShareBroadcastReceiver.MessageListener() {

					@Override
					public void OnReceived(String message) {
						if (message == "ok" || message.equals("ok")) {
							Message msMessage = new Message();
							msMessage.what = SHARE_SUCCESS;
							sHandler.sendMessage(msMessage);
						}

					}
				});
	}

	@Override
	protected void onDestroy() {
		// PackingApplication.getInstance().setCurrentActivity(null);
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// 保存当前实例
		PackingApplication.getInstance().setCurrentActivity(this);
		super.onNewIntent(intent);
	}

	@Override
	protected void onActivityResult(int requestcode, int resultcode, Intent data) {
		super.onActivityResult(requestcode, resultcode, data);
		if (resultcode == Constants.ResultCode.NAVATIGOR_START) {
			setResult(Constants.ResultCode.NAVATIGOR_START, data);
			finish();
		} else if (resultcode == SCAN_SUCCESS) {
			String scanResult = data.getStringExtra("ScanResult");
			if (scanResult != null && !scanResult.equals("")) {
				if (orderinfo.getOrderStatus() == Constants.ORDER_TYPE_FINISH) {
					Toast.makeText(OrderDetailActivity.this, "订单已经完成", Toast.LENGTH_SHORT).show();
					return;
				}
				if (orderinfo.getStartDate() != null) {// 这里应该是出场扫码
					myresultcode = scanResult;
					scanForOut(orderinfo.getOrderId(), scanResult, Constants.PAYMENT_TYPE_ZFB);

				} else {// 进场扫码
					scanForIn(orderinfo.getOrderId(), scanResult);
				}
			}
		}

	}

	/**
	 * 显示出当前的付款详细信息
	 * 
	 * @param counponUsedMoney
	 * @param parkSpentHour
	 * @param payActualPrice
	 * @param hasAlreadyPay
	 * @param yingfu
	 */
	private void displayPayInfo(double counponUsedMoney, double parkSpentHour, double payActualPrice,
			double hasAlreadyPay, double yingfu, boolean finishOrder, int orderFeeType, double lijian, double service) {
		order_pay_info.setVisibility(View.VISIBLE);
		LinearLayout line = (LinearLayout) findViewById(R.id.showservice_line);
		// 服务的显示隐藏判断
		if (orderinfo != null && orderinfo.selectedServicesDetail != null
				&& !orderinfo.selectedServicesDetail.toString().trim().equals("")) {

			if (service < 0.001) {
				line.setVisibility(View.GONE);
			} else {
				line.setVisibility(View.VISIBLE);
				TextView order_detail_service = (TextView) findViewById(R.id.service_fee);
				order_detail_service.setText("￥" + service);
			}
		} else {
			line.setVisibility(View.GONE);
		}
		if (lijian < 0.001) {
			lijianRelativeLayout.setVisibility(View.GONE);
			lijian_view.setVisibility(View.GONE);
		} else {

			hasAlreadyPay = UIUtils.decimalPrice(hasAlreadyPay - lijian);
			lijianRelativeLayout.setVisibility(View.VISIBLE);
			lijian_view.setVisibility(View.VISIBLE);

			order_detail_hour_lijian.setText("￥" + lijian);
		}
		order_detail_coupon.setText("￥" + counponUsedMoney);
		order_detail_hour.setText((orderFeeType == 1
				? (Html.fromHtml(
						"停车<font color=red>" + (parkSpentHour <= 0 ? "1" : ("" + parkSpentHour)) + "</font>小时"))
				: "按次收费"));
		order_detail_total.setText("￥" + payActualPrice);

		if (finishOrder) {
			haixufuLayout.setVisibility(View.GONE);
			orderdetail_last_view.setVisibility(View.GONE);
			// 已经付
			order_detail_hour_hasvalue.setText("￥" + UIUtils.decimalPrice(hasAlreadyPay - counponUsedMoney));

			if (showsharebutton() == true) {
				shareButton.setVisibility(View.VISIBLE);
			}
			if (showsharebutton() == false) {
				shareButton.setVisibility(View.GONE);
			}
		} else {
			haixufuLayout.setVisibility(View.VISIBLE);
			orderdetail_last_view.setVisibility(View.VISIBLE);
			// 已经付
			order_detail_hour_hasvalue.setText("￥" + hasAlreadyPay);
		}

		// 应付
		order_detail_hour_value.setText("￥" + UIUtils.decimalPrice(yingfu));

		// 将服务费隐藏（暂时不显示）
		
	}

	/**
	 * 出场扫码
	 * 
	 * @param orderid
	 * @param scanResult
	 */
	private void scanForOut(long orderid, String scanResult, int payway) {
		currentPayway = payway;
		HttpRequestAni<ComResponse<ChebolePayOptions>> httpRequestAni = new HttpRequestAni<ComResponse<ChebolePayOptions>>(
				OrderDetailActivity.this, "/a/pay/outnew/" + orderid + "?payway=" + payway + "&o=" + scanResult,
				new TypeToken<ComResponse<ChebolePayOptions>>() {
				}.getType(), priceEntity, ChebolePayOptions.class) {

			@Override
			public void callback(ComResponse<ChebolePayOptions> arg0) {
				if (arg0 != null) {
					priceEntity = arg0.getResponseEntity();
					// 如果是微信支付 直接调用微信支付相关代码
					if (currentPayway == Constants.PAYMENT_TYPE_WEIXIN) {

						resultunifiedorder = Util.decodeXml(priceEntity.payInfo);
						if (resultunifiedorder != null) {
							genPayReq(resultunifiedorder);

						} else {
							Toast.makeText(getApplicationContext(), "支付失败", Toast.LENGTH_SHORT).show();

						}

					}
				} else {
					Toast.makeText(getApplicationContext(), "没有获取到任何信息，请联系车场管理员", Toast.LENGTH_LONG).show();
					return;
				}

				if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {
					yudingButton.setVisibility(View.VISIBLE);
					scanbutton.setVisibility(View.VISIBLE);
					displayPayInfo(priceEntity.counponUsedMoneyForOut, priceEntity.parkSpentHour,
							priceEntity.payActualPrice, hasPay,
							UIUtils.decimalPrice(priceEntity.payActualPriceForTotal), false, 1,
							priceEntity.userAllowance, priceEntity.serviceTotalFee);

					Toast.makeText(getApplicationContext(), "您需要支付" + priceEntity.payActualPrice + "元",
							Toast.LENGTH_LONG).show();
					// 这里用户需要调用支付界面去支付

				} else if (arg0.getResponseStatus() == ComResponse.STATUS_FAIL) {

					String flag = arg0.getExtendResponseContext();
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderDetailActivity.this);

					if (flag != null && flag.equals("pass")) {
						Notice.rightnotice();
						ChebolePayOptions temp = arg0.getResponseEntity();
						orderinfo = temp.order;
						setProgressStatus();
						// fetchLatestOrder();
						alertDialog.setTitle("完成订单");
						alertDialog.setNegativeButton("返回", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								showPopwindow();
							}
						});
					} else if (flag != null && flag.equals("wait")) {
						ChebolePayOptions temp = arg0.getResponseEntity();
						orderinfo = temp.order;
						setProgressStatus();

						alertDialog.setTitle("等待结账放行");
						alertDialog.setNegativeButton("返回订单列表", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								showPopwindow();
							}
						});
					} else {
						alertDialog.setTitle("提示");
						alertDialog.setNegativeButton("关闭", null);
					}
					alertDialog.setMessage(arg0.getErrorMessage());

					alertDialog.create().show();
				}
			}
		};

		httpRequestAni.execute();

	}

	/**
	 * 给消息推送使用
	 */
	public void notifyOrderDone() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderDetailActivity.this);
		alertDialog.setTitle("完成订单");
		alertDialog.setMessage("谢谢您的光临,请离场");
		alertDialog.setNegativeButton("返回订单列表", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				showPopwindow();

			}
		});
		alertDialog.create().show();

	}

	/**
	 * 显示popupWindow
	 */
	private void showPopwindow() {
		shareButton.setVisibility(View.VISIBLE);
		if (window == null) {
			intitpopwindow();
			return;
		}

		// 在底部显示
		window.showAtLocation(OrderDetailActivity.this.findViewById(R.id.start), Gravity.BOTTOM, 0, 0);
		backgroundAlpha(0.5f);

	}

	private void intitpopwindow() {

		// 利用layoutInflater获得View
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.sharepopwindow, null);
		// 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()

		window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		// 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
		window.setFocusable(true);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		window.setBackgroundDrawable(dw);

		// 设置popWindow的显示和消失动画
		window.setAnimationStyle(R.style.sharepopwindow);
		// 在底部显示
		window.showAtLocation(OrderDetailActivity.this.findViewById(R.id.start), Gravity.BOTTOM, 0, 0);

		backgroundAlpha(0.5f);

		LinearLayout shareLayout = (LinearLayout) view.findViewById(R.id.share);
		LinearLayout sharetocLayout = (LinearLayout) view.findViewById(R.id.sharetoc);
		shareLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				window.dismiss();
				Toast.makeText(OrderDetailActivity.this, "分享中，请稍等...", Toast.LENGTH_SHORT).show();
				getuniuqeurl = getuniqueurl();
				if (getuniuqeurl != null) {
					getsharecontent(1, getuniuqeurl);
				}
			}
		});

		sharetocLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				window.dismiss();
				Toast.makeText(OrderDetailActivity.this, "分享中，请稍等...", Toast.LENGTH_SHORT).show();
				getuniuqeurl = getuniqueurl();
				if (getuniuqeurl != null) {
					getsharecontent(2, getuniuqeurl);
				}
			}
		});

		LinearLayout canceLayout = (LinearLayout) view.findViewById(R.id.cancel);

		canceLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				window.dismiss();
				shareButton.setVisibility(View.VISIBLE);
				if (!isFromFinshed) {
					setResult(Constants.ResultCode.HOME);
					finish();
				}
			}
		});

		// popWindow消失监听方法
		window.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {

				backgroundAlpha(1f);
			}
		});

	}

	protected void getsharecontent() {
		HttpRequest<TOptions> httpRequest = new HttpRequest<TOptions>("/a/getoption/9", TOptions.class) {
			@Override
			public void onSuccess(TOptions arg0) {
				if (arg0 != null) {
					String context = arg0.longTextObject;
					if (context == null || context.trim().equals("")) {
						context = arg0.textObject;
						context = context.replace("，", ",");
						String[] contexts = context.split(",");
						if (contexts.length >= 3) {
							Constants.WXSHARE_TITLE = contexts[0];
							Constants.WXSHARE_CONTEXT = contexts[1] + "," + contexts[2];
						}
					}

				}
			}

			@Override
			public void onFailed(String message) {
			}

		};

		httpRequest.execute();

	}

	// 设置背景透明度
	public void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
		getWindow().setAttributes(lp);
	}

	/**
	 * 进场扫码
	 * 
	 * @param orderId
	 * @param scanResult
	 */
	private void scanForIn(long orderId, String scanResult) {

		HttpRequestAni<ComResponse<OrderEntity>> httpRequestAni = new HttpRequestAni<ComResponse<OrderEntity>>(
				OrderDetailActivity.this, "/a/scan/entrance/" + orderId + "?o=" + scanResult,
				new TypeToken<ComResponse<OrderEntity>>() {
				}.getType()) {

			@Override
			public void callback(ComResponse<OrderEntity> arg0) {
				Message msg = new Message();
				if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {

					String feedback = arg0.getExtendResponseContext();
					if (feedback != null && feedback.equals("pass")) {
						msg.what = 3; // 计次
					} else {
						msg.what = 1;
					}

					OrderEntity orderEntity = arg0.getResponseEntity();
					msg.obj = orderEntity;

				} else {
					msg.what = 2;
					msg.obj = arg0.getErrorMessage();
				}
				updatehandler.sendMessage(msg);

			}
		};

		httpRequestAni.execute();

	}

	private Handler updatehandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case 3:
				OrderEntity orderEntity1 = (OrderEntity) msg.obj;
				orderinfo = orderEntity1;

				// fetchLatestOrder();
				setProgressStatus();
				Notice.rightnotice();
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderDetailActivity.this);
				alertDialog.setTitle("完成订单");
				alertDialog.setNegativeButton("返回", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						showPopwindow();
					}
				});
				alertDialog.setMessage("车辆进场扫码成功");
				alertDialog.create().show();

				adminViewStatus();
				break;
			case 1:
				OrderEntity orderEntity2 = (OrderEntity) msg.obj;
				orderinfo = orderEntity2;
				setProgressStatus();
				Toast.makeText(OrderDetailActivity.this, "车辆进场扫码成功", Toast.LENGTH_SHORT).show();
				Notice.rightnotice();
				break;

			default:
				Notice.errorotice();
				String message = (String) msg.obj;
				AlertDialog.Builder alertDialogError = new AlertDialog.Builder(OrderDetailActivity.this);
				alertDialogError.setTitle("错误");
				alertDialogError.setMessage(message);
				alertDialogError.setNegativeButton("关闭", null);
				alertDialogError.create().show();
				break;
			}
		}
	};

	void getorderEntryByID(long id) {
		HttpRequestAni<OrderEntity> httpRequestAni = new HttpRequestAni<OrderEntity>(OrderDetailActivity.this,
				"/a/order/find/" + id, new TypeToken<OrderEntity>() {
				}.getType()) {

			@Override
			public void callback(OrderEntity arg0) {
				if (arg0 == null) {
					Toast.makeText(getApplicationContext(), "加载出错，请刷新后重试", Toast.LENGTH_SHORT).show();
					finish();
					return;
				} else {
					OrderEntity record = arg0;
					if (record.getOrderStatus() == Constants.ORDER_TYPE_OVERDUE) {
						Toast.makeText(OrderDetailActivity.this, "当前订单已经过期,无法扫描", Toast.LENGTH_LONG).show();
						finish();
						return;
					}

					if (record.getOrderStatus() == Constants.ORDER_TYPE_EXCPTION) {
						Toast.makeText(OrderDetailActivity.this, "当前订单异常,请联系管理员", Toast.LENGTH_LONG).show();
						finish();
						return;
					}

					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), MipcaActivityCapture.class);
					intent.putExtra("mode", Constants.USER_SCAN_BACK);
					startActivityForResult(intent, 11);

				}

			}

			@Override
			public void onFailed(String message) {
				// TODO Auto-generated method stub
				super.onFailed(message);
				Toast.makeText(getApplicationContext(), "访问网络数据失败", Toast.LENGTH_SHORT).show();

			}

		};

		httpRequestAni.execute();

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

			msgApi.registerApp(Constants.APP_ID);
			msgApi.sendReq(req);

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

	public boolean timeout() {
		// 说明该订单已经完成
		if (orderinfo.getEndDate() != null) {
			Date date = new Date();
			Date endate = orderinfo.getEndDate();
			// 获取完成订单和现在时间的时间差
			long days = (date.getTime() - endate.getTime()) / (1000 * 60 * 60 * 24);
			// 如果天数大于=1天 不显示，返回真，不显示sharebutton
			if (days >= 1) {
				return true;
			}
			// 如果天数小于一天，返回假 显示sharebutton
			if (days < 1) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("userinfo", SessionUtils.loginUser);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onRestoreInstanceState(Bundle outState) {
		super.onRestoreInstanceState(outState);
		SessionUtils.loginUser = (TuserInfo) outState.getSerializable("userinfo");
	}

	private void getsharecontent(final int type, final String getuniuqeurl) {
		HttpRequest<TOptions> httpRequest = new HttpRequest<TOptions>("/a/getoption/9", TOptions.class) {
			@Override
			public void onSuccess(TOptions arg0) {
				if (arg0 != null) {
					String context = arg0.longTextObject;
					if (context == null || context.trim().equals("")) {
						context = arg0.textObject;
						context = context.replace("，", ",");
						String[] contexts = context.split(",");
						Constants.WXSHARE_TITLE = contexts[0];
						Constants.WXSHARE_CONTEXT = contexts[1] + "," + contexts[2];
						saveurl(getuniuqeurl, type);
					}

				}
			}

			@Override
			public void onFailed(String message) {
			}

		};

		httpRequest.execute();

	}

	// 点击分享后保存链接有效
	private void saveurl(final String getuniuqeurl, final int type) {

		HttpRequest<String> httpRequest = new HttpRequest<String>("/a/saveurl?url=" + getuniuqeurl, String.class) {
			@Override
			public void onSuccess(String arg0) {
				if (arg0 != null) {
					getneturl(type, getuniuqeurl);
				}

			}

			@Override
			public void onFailed(String message) {
				// TODO Auto-generated method stub

			}
		};

		httpRequest.execute();

	}

	private void getneturl(final int type, final String getuniuqeurl) {
		HttpRequest<TOptions> httpRequest = new HttpRequest<TOptions>("/a/getoption/11", TOptions.class) {
			@Override
			public void onSuccess(TOptions arg0) {
				if (arg0 != null) {
					String context = arg0.longTextObject;
					if (context == null || context.trim().equals("")) {
						context = arg0.textObject;
						String url = context.replaceAll("state=1", "state=" + getuniuqeurl);
						saveredicturl(getuniuqeurl, url, type);
					}

				}
			}

			@Override
			public void onFailed(String message) {
			}

		};

		httpRequest.execute();
	}

	private void saveredicturl(final String uniqueurl, final String redicturl, final int type) {
		Tredirecturl redirecturl = new Tredirecturl();
		redirecturl.redicturl = redicturl;
		redirecturl.uniqueurl = uniqueurl;
		HttpRequest<Tredirecturl> httpRequest = new HttpRequest<Tredirecturl>(Method.POST, redirecturl,
				"/a/redicturl/save", new TypeToken<Tredirecturl>() {
				}.getType(), Tredirecturl.class) {
			@Override
			public void onSuccess(Tredirecturl infos) {

				if (infos != null) {
					if (type == 1) {
						api.sendReq(WXshareutil.sharetofriend(redicturl, Constants.WXSHARE_TITLE,
								Constants.WXSHARE_CONTEXT));
					} else if (type == 2) {
						api.sendReq(WXshareutil.sharetofriendcircle(redicturl, Constants.WXSHARE_TITLE,
								Constants.WXSHARE_CONTEXT));
					}

				}
			}

			@Override
			public void onFailed(String message) {

			}
		};

		httpRequest.execute();
	}

}
