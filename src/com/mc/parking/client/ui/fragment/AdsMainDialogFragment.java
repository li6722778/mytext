package com.mc.parking.client.ui.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Request.Method;
import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushManager;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.client.Constants;
import com.mc.park.client.R;
import com.mc.parking.client.entity.ChebolePayOptions;
import com.mc.parking.client.entity.TParkService;
import com.mc.parking.client.entity.TTopBannner;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.HttpRequest;
import com.mc.parking.client.utils.PayResult;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;
import com.mc.wxutils.Util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AdsMainDialogFragment extends DialogFragment {

	String[] parkname, ss;
	List<TParkService> servicelist;
	EditText editText;
	int size = 0, payway;
	List<TParkService> selects;
	TTopBannner banner;
	ListView list;
	String carlisen, serviceId = "", supplyId = "", orderid;
	TextView pay, carnum, service_name, service_detail, money, bannername, contactPhone, address, gopay,explain;
	RelativeLayout banner_relay,payline;
	ImageLoader imageLoader = ImageLoader.getInstance();
	ImageView payimage;
	Boolean sIsWXAppInstalledAndSupported;
	static final int SDK_PAY_FLAG = 1;

	public void setdata(TTopBannner banner) {
		// TODO Auto-generated constructor stub
		this.banner = banner;
		serviceId = "" + banner.serviceId;
		// BindData();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater factory = LayoutInflater.from(getActivity());// ��ʾ��
		final View view = factory.inflate(R.layout.ac_admaindialog, null);// ���������final��
		pay = (TextView) view.findViewById(R.id.payway);
		service_name = (TextView) view.findViewById(R.id.service_name);
		service_detail = (TextView) view.findViewById(R.id.service_detail);
		payimage = (ImageView) view.findViewById(R.id.paywayimage);
		carnum = (TextView) view.findViewById(R.id.carnum);
		money = (TextView) view.findViewById(R.id.money);
		banner_relay = (RelativeLayout) view.findViewById(R.id.banner_relay);
		bannername = (TextView) view.findViewById(R.id.bannername);
		address = (TextView) view.findViewById(R.id.address);
		contactPhone = (TextView) view.findViewById(R.id.contactPhone);
		gopay = (TextView) view.findViewById(R.id.gopaybtn);
		payline=(RelativeLayout) view.findViewById(R.id.payline);
		TextView cancel = (TextView) view.findViewById(R.id.cancel_btn);
		explain=(TextView) view.findViewById(R.id.explain);
		msgApi = WXAPIFactory.createWXAPI(getActivity(), null);
		msgApi.registerApp(Constants.APP_ID);
		banner_relay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSupply();
			}
		});
		imageLoader.displayImage("drawable://" + R.drawable.iconzhifubao, payimage);

		// ������ť
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});

		// ֧����ť
		gopay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotopay();
			}
		});
		payline.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("��ѡ��֧����ʽ");
				builder.setSingleChoiceItems(Constants.PAY_WAY, getSelectedIndex(pay.getText().toString()),
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						// ����֧����ʽ
						pay.setText(Constants.PAY_WAY[which]);
						payway = which;
						if (which == 0) {
							imageLoader.displayImage("drawable://" + R.drawable.iconzhifubao, payimage);

						} else {
							imageLoader.displayImage("drawable://" + R.drawable.iconweixin, payimage);

						}
					}
				})
						// Add action buttons
						.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {

					}
				}).show();

			
			}
		});
		
		// ѡ��֧��
		/*payimage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {}
		});*/
		if (getArguments().get("data") != null) {
			this.banner = (TTopBannner) getArguments().get("data");
			serviceId = "" + banner.serviceId;
			bindData();
		} else {
			Toast.makeText(getActivity(), "�쳣", Toast.LENGTH_SHORT).show();
			dismiss();
		}

		LinearLayout ll_account = (LinearLayout) view.findViewById(R.id.ll_account);
		ll_account.requestFocus();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view);
		// BindData();
		return builder.create();
	}

	private void bindData() {

		if (SessionUtils.loginUser != null && SessionUtils.loginUser.email != null
				&& !SessionUtils.loginUser.email.trim().equals(""))
			carnum.setText(SessionUtils.loginUser.email);
		pay.setText(Constants.PAY_WAY[0]);

		if (banner.parkServiceBean != null&&banner.parkServiceBean.size()>0) {

			int size = banner.parkServiceBean.size();
			if (banner.cacheAddress != null && !banner.cacheAddress.trim().equals(""))
				address.setText(banner.cacheAddress);
			else
				address.setText(SessionUtils.address);

			if (SessionUtils.loginUser != null)
				contactPhone.setText("" + SessionUtils.loginUser.userPhone);
			service_name.setText(banner.parkServiceBean.get(0).serviceName);
			service_detail.setText(banner.parkServiceBean.get(0).serviceDetail);
			money.setText("��" + banner.parkServiceBean.get(0).serviceFee + "Ԫ");
			if(banner.formComments!=null)
			{
			explain.setText(Html.fromHtml(banner.formComments));
			}
			else
				explain.setText("");
			if (banner.parkServiceBean.size() <= 1) {
				banner_relay.setVisibility(View.GONE);
				if(banner.parkServiceBean.size()==1)
					supplyId = "" + banner.parkServiceBean.get(0).supplyInfo.id;
			} else {
				// ѡ��Ӧ��
				banner_relay.setVisibility(View.VISIBLE);
				supplyId = "" + banner.parkServiceBean.get(0).supplyInfo.id;
				bannername.setText(banner.parkServiceBean.get(0).supplyInfo.supplyName);

			}
		}

	}

	// ���ֹ�Ӧ��ѡ��
	private void showSupply() {
		// TODO Auto-generated method stub
		int size = banner.parkServiceBean.size();
		if(size<2)
		{
			Toast.makeText(getActivity(), "û�пɹ�ѡ��ķ����̣�", Toast.LENGTH_SHORT).show();
			return;
		}
		ss = new String[size];
		for (int i = 0; i < size; i++) {
			ss[i] = banner.parkServiceBean.get(i).supplyInfo.supplyName;

		}
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// View view =
		// inflater.inflate(R.layout.fragment_parklistdialog, null);
		builder.setTitle("��ѡ�������");
		builder.setSingleChoiceItems(ss, getSelectedIndexforSupply(bannername.getText().toString()),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						bannername.setText(ss[which]);
						supplyId = "" + banner.parkServiceBean.get(which).supplyInfo.id;
					}
				})
				// Add action buttons
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {

					}
				}).show();

	}

	// ���֧����index
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

	// ��ù�Ӧ�̵�index
	public int getSelectedIndexforSupply(String payway) {
		if (ss != null) {
			for (int i = 0; i < ss.length; i++) {
				String p = ss[i];
				if (p.trim().equals(payway.trim())) {
					return i;
				}
			}
		}
		return 0;
	}

	private void gotopay() {

		enablePayConfirmButton(false);
		StringBuffer sb = new StringBuffer();
		try {
			String city = URLEncoder.encode(SessionUtils.city.toString(), "utf-8");
			if (city != null && !city.trim().equals(""))
				sb.append("&city=" + city);
			if (address != null && !address.getText().toString().trim().equals(""))
				sb.append("&contactAddress=" + URLEncoder.encode(address.getText().toString().trim(), "utf-8"));
			if (carnum != null && !carnum.getText().toString().trim().equals(""))
				sb.append("&carLisence=" + URLEncoder.encode(carnum.getText().toString().trim(), "utf-8"));

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String phone = contactPhone.getText().toString();
		String clientid = PushManager.getInstance().getClientid(getActivity());
		sb.append("&payWay=" + payway);

		// �ж��Ƿ�Ϊ�� Ϊ���ˢ�����
		clientid = UIUtils.checkCurrentClientID(clientid);
		sb.append("&clientId=" + clientid);
		ChebolePayOptions op = new ChebolePayOptions();
		HttpRequest<ComResponse<String>> httpRequestAni = new HttpRequest<ComResponse<String>>(Method.POST, op,
				"/a/serviceorder/pay/" + serviceId + "?supplyId=" + supplyId + "&contactPhone=" + phone + sb.toString(),
				new TypeToken<ComResponse<String>>() {
				}.getType(), ChebolePayOptions.class) {

			@Override
			public void onSuccess(final ComResponse<String> arg0) {
				// TODO Auto-generated method stub
				if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {
					// ���orderid
					orderid = arg0.getExtendResponseContext().trim();
					// ����Ҫ֧���Ķ���
					if (arg0.getResponseEntity().toString().trim().equals("nofee")) {
						paychangeStatus(arg0.getExtendResponseContext().trim(), Constants.PAYMENT_STATUS_FINISH);

					} else if (payway == 0) {
						// ֧����֧��
						Runnable payRunnable = new Runnable() {
							@Override
							public void run() {
								// ����PayTask ����
								PayTask alipay = new PayTask(getActivity());
								// ����֧���ӿڣ���ȡ֧�����
								String result = alipay.pay(arg0.getResponseEntity());

								Message msg = new Message();
								msg.what = SDK_PAY_FLAG;
								msg.obj = result;
								mHandler.sendMessage(msg);
							}
						};
						Thread payThread = new Thread(payRunnable);
						payThread.start();
						//���ز����µ�ͼ
						UIUtils.backState=true;
					} else if (payway == 1) {
						resultunifiedorder = Util.decodeXml(arg0.getResponseEntity());
						if (resultunifiedorder != null) {
							genPayReq(resultunifiedorder);
							//���ز����µ�ͼ
							UIUtils.backState=true;
						} else {

							Toast.makeText(getActivity(), "֧��ʧ��", Toast.LENGTH_SHORT).show();

						}

					}
				} else if (arg0.getResponseStatus() == ComResponse.STATUS_FAIL) {

					Toast.makeText(getActivity(), arg0.getErrorMessage(), Toast.LENGTH_SHORT).show();
					enablePayConfirmButton(true);
				}
			}

			@Override
			public void onFailed(String message) {
				enablePayConfirmButton(true);
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "����" + message, Toast.LENGTH_LONG).show();
			}
		};

		httpRequestAni.execute();

	}

	private void paychangeStatus(String orderid, final int paystatus) {

		// �����Ǹ��¶���״̬
		HttpRequest<ComResponse<String>> httpRequestAni = new HttpRequest<ComResponse<String>>(Method.POST, null,
				"/a/serviceorder/payment/update/" + orderid + "?s=" + paystatus, new TypeToken<ComResponse<String>>() {
				}.getType(), String.class) {

			@Override
			public void onSuccess(ComResponse<String> arg0) {
				if (arg0 != null) {
					if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {
						if (paystatus == Constants.PAYMENT_STATUS_FINISH) {
							dismiss();
						}
						Toast.makeText(getActivity(), arg0.getExtendResponseContext(), Toast.LENGTH_SHORT).show();
					} else if (arg0.getResponseStatus() == ComResponse.STATUS_FAIL) {
						Toast.makeText(getActivity(), arg0.getErrorMessage(), Toast.LENGTH_SHORT).show();
					}

				}
			}

			@Override
			public void onFailed(String message) {
				Toast.makeText(getActivity(), "�쳣��" + message, Toast.LENGTH_LONG).show();
			}
		};

		httpRequestAni.execute();

	}

	// ΢��֧����ش���
	/**
	 * 
	 * 
	 */
	PayReq req;
	Map<String, String> resultunifiedorder;
	 IWXAPI msgApi;

	private void genPayReq(Map<String, String> result) {
		if (isWXAppInstalledAndSupported(getActivity(), msgApi)) {

			Bimp.WXback=0;
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
               
		} else {
			enablePayConfirmButton(true);
		}

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(orderid!=null&&!orderid.trim().equals(""))
		if (Bimp.WXback == Constants.PAYMENT_STATUS_FINISH) {
			paychangeStatus(orderid,Constants.PAYMENT_STATUS_FINISH);
			Bimp.WXback = 0;
		} else if (Bimp.WXback == Constants.PAYMENT_STATUS_PENDING) {
			paychangeStatus(orderid,Constants.PAYMENT_STATUS_PENDING);
			Bimp.WXback = 0;

		} else if (Bimp.WXback == Constants.PAYMENT_STATUS_EXCPTION) {
			Bimp.WXback = 0;
			enablePayConfirmButton(true);
			paychangeStatus(orderid,Constants.PAYMENT_STATUS_EXCPTION);
		} else {
			enablePayConfirmButton(true);

		}
	}

	private boolean isWXAppInstalledAndSupported(Context context, IWXAPI api) {
		// LogOutput.d(TAG, "isWXAppInstalledAndSupported");
		sIsWXAppInstalledAndSupported = api.isWXAppInstalled() && api.isWXAppSupportAPI();
		if (!sIsWXAppInstalledAndSupported) {
			// Log.w(TAG, "~~~~~~~~~~~~~~΢�ſͻ���δ��װ����ȷ��");
			// Toast.showToast(context, "΢�ſͻ���δ��װ����ȷ��");
			Toast.makeText(getActivity(), "΢�ſͻ���δ��װ����ȷ��", Toast.LENGTH_SHORT).show();
		}

		return sIsWXAppInstalledAndSupported;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			enablePayConfirmButton(true);
			switch (msg.what) {
			case SDK_PAY_FLAG: {

				PayResult payResult = new PayResult((String) msg.obj);

				// ֧�������ش˴�֧���������ǩ�������֧����ǩ����Ϣ��ǩԼʱ֧�����ṩ�Ĺ�Կ����ǩ
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// �ж�resultStatus Ϊ��9000�������֧���ɹ�������״̬�������ɲο��ӿ��ĵ�
				if (TextUtils.equals(resultStatus, "9000")) {
					paychangeStatus(orderid, Constants.PAYMENT_STATUS_FINISH);
				} else {
					// �ж�resultStatus Ϊ�ǡ�9000����������֧��ʧ��
					// ��8000������֧�������Ϊ֧������ԭ�����ϵͳԭ���ڵȴ�֧�����ȷ�ϣ����ս����Ƿ�ɹ��Է�����첽֪ͨΪ׼��С����״̬��
					if (TextUtils.equals(resultStatus, "8000")) {
						paychangeStatus(orderid, Constants.PAYMENT_STATUS_PENDING);

					} else {
						// ����ֵ�Ϳ����ж�Ϊ֧��ʧ�ܣ������û�����ȡ��֧��������ϵͳ���صĴ���
						paychangeStatus(orderid, Constants.PAYMENT_STATUS_EXCPTION);
					}
				}
				break;
			}

			default:
				break;
			}
		};
	};

	public void enablePayConfirmButton(boolean enable) {
		gopay.setEnabled(enable);
		// �û�ѡ�����ֽ�֧��
		if (enable) {
			gopay.setText("ȷ�ϣ�ȥ֧��");
		} else {
			gopay.setText("����֧���ӿ�");
		}

	}
}
