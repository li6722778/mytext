package com.mc.parking.client.ui.fragment;

import java.io.File;
import java.sql.SQLException;
import java.util.Date;

import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.mc.parking.client.Constants;
import com.mc.park.client.R;
import com.mc.parking.client.entity.TIncome;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkInfo_ImgEntity;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.entity.TUseCouponEntity;
import com.mc.parking.client.entity.TakeCashEntity;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.CommFindEntity;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.CouponDetailActivity;
import com.mc.parking.client.ui.ParkActivity;
import com.mc.parking.client.ui.TakecashActivity;
import com.mc.parking.client.ui.YuyueActivity;
import com.mc.parking.client.ui.admin.AddParkInfoDetailActivity;
import com.mc.parking.client.ui.admin.AdminGetParkInfoActivity;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TakecashFragment extends android.support.v4.app.Fragment {
	TakecashActivity TakecashActivity;
	TParkInfoEntity tparkinfo;
	TextView bankcode, totalIncome, dayIncome,cash_total,web_total,counpon_total,takscash_total,takeableIncome,counpon_allowance;
	EditText use_zfb;
	Double totalmoney, datamoney;
	Button askButton,refreshcash_btn;
	TakeCashEntity takecashbean = new TakeCashEntity();

	TIncome incomebean = new TIncome();
	Activity activity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = getActivity();
		View view = inflater.inflate(R.layout.fragment_takecash, container,
				false);
		bankcode = (TextView) view.findViewById(R.id.bankcode);
		cash_total = (TextView) view.findViewById(R.id.cash_total);
		counpon_total = (TextView) view.findViewById(R.id.counpon_total);
		web_total = (TextView) view.findViewById(R.id.web_total);
		bankcode.setFocusableInTouchMode(true);
		bankcode.requestFocus();
		totalIncome = (TextView) view.findViewById(R.id.totalIncome);
		dayIncome = (TextView) view.findViewById(R.id.dayIncome);
		use_zfb = (EditText) view.findViewById(R.id.use_zfb);
		TakecashActivity = (com.mc.parking.client.ui.TakecashActivity) getActivity();
		takscash_total= (TextView) view.findViewById(R.id.takscash_total);
		takeableIncome=(TextView) view.findViewById(R.id.takeableIncome);
		counpon_allowance=(TextView) view.findViewById(R.id.counpon_allowance);

		tparkinfo = SessionUtils.loginUser.parkInfoAdm;
		if (tparkinfo != null) {
			if (tparkinfo.venderBankNumber == null) {
				tparkinfo.venderBankNumber = "";
			}
			if (tparkinfo.venderBankName == null) {
				tparkinfo.venderBankName = "";

			}
			if (tparkinfo.venderBankNumber.trim().equals("")
					|| tparkinfo.venderBankName.trim().equals("")
					|| tparkinfo.venderBankNumber.length() < 8) {
				bankcode.setText("[无绑定银行卡]");

			} else {
				int codelength = tparkinfo.venderBankNumber.length();
				if(tparkinfo.venderBankNumber.length()>4){
				String endcode = tparkinfo.venderBankNumber.substring(
						codelength - 4, codelength);
				bankcode.setText("" + tparkinfo.venderBankName + "(尾号"
						+ endcode + ")");
				}
			}
		}
		askButton = (Button) view.findViewById(R.id.askcash_btn);
		refreshcash_btn=(Button) view.findViewById(R.id.refreshcash_btn);
		refreshcash_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getallmoneyDate();
			}
		});
		askButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				

				
				// TODO Auto-generated method stub
				if (use_zfb==null||use_zfb.getText()==null||use_zfb.getText().toString().trim().equals("")) {
					Toast.makeText(getActivity(), "请输入提现金额", Toast.LENGTH_SHORT)
							.show();
					return;

				}
				
				Double webmoney = Double.valueOf(web_total.getText()
						.toString().trim());
				Double counponmoney = Double.valueOf(counpon_total.getText()
						.toString().trim());
				
				Double hasTakeCash = Double.valueOf(takscash_total.getText()
						.toString().trim());
				
				Double currenttake = Double.valueOf(use_zfb.getText()
						.toString().trim());
				
				Double allowance = Double.valueOf(counpon_allowance.getText()
						.toString().trim());
				
				
				Double totalmoney = UIUtils.decimalPrice(webmoney+counponmoney-hasTakeCash+allowance);
				if (currenttake > totalmoney||currenttake<=0) {
					Toast.makeText(getActivity(), "请输入有效金额", Toast.LENGTH_SHORT)
							.show();
					return;

				}
				
				if(bankcode!=null&&
						bankcode.getText()!=null&&
						bankcode.getText().toString().indexOf("无绑定银行卡")>=0){
					Toast.makeText(getActivity(), "请联系客服绑定银行卡", Toast.LENGTH_SHORT)
					.show();
					return;
				}
				
				takecashbean.cardname = tparkinfo.venderBankName;
				takecashbean.cardnumber = tparkinfo.venderBankNumber;
				takecashbean.cardownername = tparkinfo.owner;
				takecashbean.status = 1;
				takecashbean.takemoney = currenttake;
				takecashbean.parkid = SessionUtils.loginUser.parkInfoAdm.parkId;

			
				HttpRequestAni<ComResponse<TakeCashEntity>> httpRequestAni = new HttpRequestAni<ComResponse<TakeCashEntity>>(
						getActivity(), "/a/takecash/save",
						new TypeToken<ComResponse<TakeCashEntity>>() {
						}.getType(), takecashbean, TakeCashEntity.class) {

					@Override
					public void callback(ComResponse<TakeCashEntity> arg0) {
						if(arg0!=null)
						{
						if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {
							use_zfb.setText("");
							getallmoneyDate();
							Toast.makeText(getActivity(), "提现申请成功，请等待后台处理",
									Toast.LENGTH_SHORT).show();
						}else
						{
							
							Toast.makeText(getActivity(), arg0.getErrorMessage(),
									Toast.LENGTH_SHORT).show();
						}
						}else
						{
							Toast.makeText(getActivity(), "提现失败",
									Toast.LENGTH_SHORT).show();
							
						}
					}
					@Override
							public void onFailed(String message) {
								// TODO Auto-generated method stub
								
							}

				};

				httpRequestAni.execute();
			}
		});

		getallmoneyDate();
		bankcode.setFocusableInTouchMode(true);
		bankcode.requestFocus();
		return view;
	}

	void getallmoneyDate() {

		totalmoney = 1000d;
		datamoney = 100d;
		totalIncome.setText("·······");
		dayIncome.setText("·······");
		web_total.setText("·······");
		cash_total.setText("·······");
		takeableIncome.setText("·······");
		
		if(SessionUtils.loginUser!=null&&SessionUtils.loginUser.parkInfoAdm!=null&&SessionUtils.loginUser.parkInfoAdm.parkId!=null){
		HttpRequestAni<TIncome> httpRequestgetmoney = new HttpRequestAni<TIncome>(
				getActivity(), "/a/income/"
						+ SessionUtils.loginUser.parkInfoAdm.parkId,
				new TypeToken<TIncome>() {
				}.getType()) {

			@Override
			public void callback(TIncome arg0) {
				if (arg0 != null) {
					totalIncome.setText(""
							+ UIUtils.decimalPrice(arg0.incometotal));
					dayIncome.setText(""
							+ UIUtils.decimalPrice(arg0.incometoday));
					web_total.setText(""
							+ UIUtils.decimalPrice(arg0.incometotal-arg0.cashtotal-arg0.counpontotal-arg0.allowance));
					cash_total.setText(""
							+ UIUtils.decimalPrice(arg0.cashtotal));
					counpon_total.setText(""+ UIUtils.decimalPrice(arg0.counpontotal));
					
					takeableIncome.setText(""
							+ UIUtils.decimalPrice(arg0.incometotal-arg0.cashtotal-arg0.takeCashTotal));
					takscash_total.setText(""
							+ UIUtils.decimalPrice(arg0.takeCashTotal));
					counpon_allowance.setText(""+ UIUtils.decimalPrice(arg0.allowance));
					
				}

			}

		};

		httpRequestgetmoney.execute();
		}
	}
}
