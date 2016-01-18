package com.mc.parking.client.ui.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.gson.reflect.TypeToken;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.admin.adrm.PhasedListener;
import com.mc.parking.admin.adrm.PhasedSeekBar;
import com.mc.parking.admin.adrm.SimplePhasedAdapter;
import com.mc.parking.client.Constants;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkInfo_ImgEntity;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.ParkActivity;
import com.mc.parking.client.ui.UploadPhotoActivity;
import com.mc.parking.client.ui.admin.CheweiFragment.MyTimePickerDialog;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.zxing.camera.MipcaActivityCapture;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ChangeParkPriceFragment  extends Fragment {

	private TParkInfoEntity parkinfo;
	private String[] imageUrls;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private RelativeLayout chewei_scan, realy_all, chewei_pay_info;
	private int CHECK_LIGHT = 1;
	private ImageView LightImage, CheweiTopImag;
	private LinearLayout chewei_info;
	ParkActivity parkActivity;
	TextView parkname, parkaddinfo, parkimagenum;
	private LinearLayout progress, pay_info,pay_span_layout;
	TextView parkmode;
	int PAY_VIEW = 0;
	//对于控件，0代表开放，1代表关闭
	int PARK_OPEN=0;
	int PARK_CLOSE=1;

	SimpleDateFormat format = new SimpleDateFormat("HH:mm");

	RadioButton paymode1, paymode2;
	TextView pay_mode1_intime,compname, contactmen, tele, parkname_pay, parkaddress,
			getimage_parktime, parkovertime, paynomal, daypay, daytimepay,
			startime, endtime, couponpay, locationtext, show_pay_info,otherActivite,feeTypeSecMinuteOfActivite,daypay_name,parktele;
	Spinner spinner;
	
	CheckBox counpmode1, counpmode2,telecheckbox;
	private int hourOfDay1, minute1;
	private int timemode = 0;

	public int starthouse = 0;
	public int startminute = 0;



	Button modefybtn,add_parkinfo_clear;

	public int feeType;

	public int feeTypeSecInScopeHours;

	public double feeTypeSecInScopeHourMoney;

	public double feeTypeSecOutScopeHourMoney;

	public double feeTypefixedHourMoney;

	public int isDiscountAllday;

	public int isDiscountSec;

	public double discountHourAlldayMoney;

	public double discountSecHourMoney;

	public Date discountSecStartHour;
	
	int feeactivity,feeoutactivity;
	

	Activity activity;
	public Date discountSecEndHour;
	String[] numbers = { "1小时以内", "2小时以内", "3小时以内", "4小时以内", "5小时以内", "6小时以内",
			"7小时以内", "8小时以内", "9小时以内", "10小时以内", "11小时以内", "12小时以内" };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_admin__change_price,
				container, false);
		activity = getActivity();
		realy_all = (RelativeLayout) view.findViewById(R.id.chewei_all_relay);
		progress = (LinearLayout) view
				.findViewById(R.id.youhui_progressContainer);
		otherActivite=(EditText)view.findViewById(R.id.feeTypeFixedMinuteOfInActivite);
		otherActivite.setOnFocusChangeListener(onfocll);
		feeTypeSecMinuteOfActivite=(EditText)view.findViewById(R.id.feeTypeSecMinuteOfActivite);
		feeTypeSecMinuteOfActivite.setOnFocusChangeListener(onfocll);
		pay_mode1_intime=(TextView)view.findViewById(R.id.pay_mode1_intime);
		add_parkinfo_clear=(Button)view.findViewById(R.id.add_parkinfo_clear);
		//当为计次时，单位设置成元/天，计时为元/小时。
		daypay_name=(TextView)view.findViewById(R.id.daypay_name);
		add_parkinfo_clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Binddata();
			}
		});
		//old spinner 点击选择时间
		pay_span_layout=(LinearLayout)view.findViewById(R.id.spinner_new);
		pay_span_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			
				LayoutInflater inflater = getActivity().getLayoutInflater();
				//View view = inflater.inflate(R.layout.fragment_parklistdialog, null);	
				final String[] parkname =numbers;;
				if(numbers!=null){
				for (int i = 0; i < parkname.length; i++) {
					parkname[i] = numbers[i].toString();
				}
				}
				
			
				builder.setTitle(getResources().getString(R.string.admin_parktime_selection));
				
				builder.setSingleChoiceItems(parkname, 0,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										which=which+1;
										pay_mode1_intime.setText(""+which);
										
									}
								})
						// Add action buttons
						.setPositiveButton("确认", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								
							}
						}).setNegativeButton("取消", null).show();
				
			}
		});
//		realy_all.setVisibility(View.INVISIBLE);
		// progress.setVisibility(View.VISIBLE);
		Calendar calendar = Calendar.getInstance();
		hourOfDay1 = calendar.get(Calendar.HOUR_OF_DAY);
		minute1 = calendar.get(Calendar.MINUTE); // 获得当前的秒
		view = initivaule(view);

		view = initetextview(view);
		paysetmode1();
		paymode1.setFocusableInTouchMode(true);
		paymode1.requestFocus();
		
		TParkInfoEntity sessoinparkinfo=SessionUtils.loginUser.parkInfoAdm;
		if(sessoinparkinfo!=null)
		{	
		List<TParkInfo_ImgEntity> imgUrlArray = sessoinparkinfo.imgUrlArray;
		if (imgUrlArray != null) {
			imageUrls = new String[imgUrlArray.size()];
			if (imageUrls.length > 0) {
				imageUrls[0] = sessoinparkinfo.imgUrlArray.get(0).imgUrlHeader
						+ sessoinparkinfo.imgUrlArray.get(0).imgUrlPath;
				imageLoader.displayImage(imageUrls[0], CheweiTopImag);
			}
		}
		// parkaddinfo.setText(data.getMapAddress());
		parkimagenum.setText("" + (imageUrls == null ? 0: imageUrls.length));
		parkinfo = sessoinparkinfo;

	
		}
		Bimp.tempTParkInfo=sessoinparkinfo;
		Binddata();
		
		return view;
	}

	private View initivaule(View view) {

		parkmode = (TextView) view.findViewById(R.id.offine_name);
		CheweiTopImag = (ImageView) view
				.findViewById(R.id.chewei_packdetail_img);
		CheweiTopImag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (imageUrls == null) {
					Toast.makeText(getActivity(), "未找到图片", Toast.LENGTH_SHORT)
							.show();

				} else {
					Intent intent = new Intent(getActivity(),
							UploadPhotoActivity.class);
					Bundle buidle = new Bundle();
					buidle.putSerializable("parkinfo", parkinfo);
					intent.putExtras(buidle);
					startActivity(intent);
				}

			}
		});

		// 指示灯
		LightImage = (ImageView) view.findViewById(R.id.chewei_light_img);
		
		// 车位信息
		chewei_info = (LinearLayout) view.findViewById(R.id.id_cheweinumb_line);
		chewei_info.setClickable(false);
		// chewei_info.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if (CHECK_LIGHT == 0) {
		// imageLoader.displayImage("drawable://" + R.drawable.start,
		// LightImage);
		// // chewei_info.setVisibility(View.VISIBLE);
		// parkmode.setText("开始接单");
		// CHECK_LIGHT = 1;
		//
		// } else {
		//
		// imageLoader.displayImage("drawable://" + R.drawable.stop,
		// LightImage);
		// // chewei_info.setVisibility(View.INVISIBLE);
		// parkmode.setText("停止接单");
		// CHECK_LIGHT = 0;
		// }
		//
		// }
		// });

		pay_info = (LinearLayout) view.findViewById(R.id.pay_info_view);
		// 扫描按钮+开启扫描pay_info_mode
		chewei_scan = (RelativeLayout) view
				.findViewById(R.id.chewei_scan_Relat);

		chewei_pay_info = (RelativeLayout) view
				.findViewById(R.id.pay_info_mode);
		chewei_pay_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (PAY_VIEW == 0) {
					pay_info.setVisibility(View.GONE);
					show_pay_info.setText("+");
					PAY_VIEW = 1;

				} else {

					pay_info.setVisibility(View.VISIBLE);
					show_pay_info.setText("-");
					PAY_VIEW = 0;
				}
			}
		});

		chewei_scan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				// intent.setClass(getActivity(), MipcaActivityCapture.class);
				intent.setClass(getActivity(), MipcaActivityCapture.class);
				startActivityForResult(intent, 11);

			}
		});
		TextView fabuChewei = (TextView) view
				.findViewById(R.id.chewei_fabuChewei);
		final TextView cheweiView = (TextView) view
				.findViewById(R.id.chewei_cheweinumber);

		TextView parkdetail_name = (TextView) view
				.findViewById(R.id.chewei_parkname);
		TextView parkdetail_address = (TextView) view
				.findViewById(R.id.chewei_parkdistance);

		TuserInfo userInfo = SessionUtils.loginUser;
		if (userInfo != null
				&& (userInfo.userType == Constants.USER_TYPE_PADMIN || userInfo.userType == Constants.USER_TYPE_PSADMIN)) {
			String cheweiName = userInfo.parkInfoAdm == null ? "[empty]"
					: userInfo.parkInfoAdm.parkname;
			parkdetail_name.setText(cheweiName);
			parkdetail_address.setText(userInfo.parkInfoAdm == null ? "[empty]"
					: userInfo.parkInfoAdm.address);
		} else {
			parkdetail_name
					.setText(Html
							.fromHtml("<html><font color=red>您不是车位管理员,请重新登录.</font></html>"));
		}

		TextView reduceChewei = (TextView) view
				.findViewById(R.id.chewei_reduceChewei);
		parkname = (TextView) view.findViewById(R.id.chewei_parkname);
		parkaddinfo = (TextView) view.findViewById(R.id.chewei_parkdistance);
		parkimagenum = (TextView) view.findViewById(R.id.packdetail_img_number);
		parktele=(TextView)view.findViewById(R.id.id_tele);

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_empty).cacheInMemory(true)
				.cacheOnDisc(false).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

	

	
		
		
//		new ParkingDetailTask() {
//
//			protected void onSuccess(TParkInfo_LocEntity data) throws Exception {
//				super.onSuccess(data);
//				progress.setVisibility(View.GONE);
//				realy_all.setVisibility(View.VISIBLE);
//				if (data != null)
//      
//					InitiParkInfo(data.parkInfo);
//
//			};
//		}.execute();

		// new ParkingDetailTask(){
		//
		// protected void onSuccess(ParkInfo data) throws Exception {
		// super.onSuccess(data);
		// progress.setVisibility(View.GONE);
		// realy_all.setVisibility(View.VISIBLE);
		// if(data!=null)
		//
		// InitiParkInfo(data);
		//
		//
		// };
		// }.execute();

		if (SessionUtils.loginUser.userType == Constants.USER_TYPE_PSADMIN
				|| SessionUtils.loginUser.userType == Constants.USER_TYPE_SADMIN) {
			
			pay_info.setVisibility(View.VISIBLE);

		} else {
			
			pay_info.setVisibility(View.GONE);

		}

		return view;
	}

	private void InitiParkInfo(TParkInfoEntity data) {

		if (data != null ) {
			imageUrls = new String[data.imgUrlArray.size()];
			if(imageUrls.length>0)
			imageLoader.displayImage(imageUrls[0], CheweiTopImag);
			parkname.setText(data.parkname);
			// parkaddinfo.setText(data.getMapAddress());
			parkimagenum.setText("" + imageUrls.length);
			parkinfo = data;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 11) {
			Toast.makeText(getActivity(), "确认成功！", Toast.LENGTH_SHORT).show();

		}
	}

	View initetextview(View view) {

		getimage_parktime = (TextView) view
				.findViewById(R.id.id_getimage_parktime);
		paynomal = (TextView) view.findViewById(R.id.id_getimage_paynomal);
		daypay = (TextView) view.findViewById(R.id.id_getimage_daypay);
		daytimepay = (TextView) view.findViewById(R.id.id_getimage_daytimepay);
		startime = (TextView) view.findViewById(R.id.id_getimage_startime);
		endtime = (TextView) view.findViewById(R.id.id_getimage_endtime);
		parkovertime = (TextView) view
				.findViewById(R.id.id_getimage_parkovertime);
		spinner = (Spinner) view.findViewById(R.id.spinner1);
		SpinnerAdapter adapter = new SpinnerAdapter(activity,
				android.R.layout.simple_spinner_item, numbers);
		spinner.setAdapter(adapter);
		couponpay = (TextView) view.findViewById(R.id.id_getimage_daypay);
		show_pay_info = (TextView) view.findViewById(R.id.show_pay_info);

		counpmode1 = (CheckBox) view.findViewById(R.id.DaypayradioButton);
		counpmode2 = (CheckBox) view.findViewById(R.id.DaytimeradioButton);
		telecheckbox=(CheckBox)view.findViewById(R.id.telecheckbox);
		startime.setOnClickListener(ll1);
		endtime.setOnClickListener(ll1);

		parkovertime.setOnClickListener(ll1);
		parkovertime.setOnFocusChangeListener(onfocll);
		getimage_parktime.setOnFocusChangeListener(onfocll);
		daypay.setOnFocusChangeListener(onfocll);
		daytimepay.setOnFocusChangeListener(onfocll);
		counpmode1.setOnClickListener(ll1);

		counpmode2.setOnClickListener(ll1);
  telecheckbox.setOnClickListener(ll1);
		paymode1 = (RadioButton) view.findViewById(R.id.payradioButton1);
		paymode2 = (RadioButton) view.findViewById(R.id.payradioButton2);
		modefybtn = (Button) view.findViewById(R.id.add_parkinfo_modefybtn);
		
		modefybtn.setOnClickListener(ll1);
		paymode1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {			
					
					paysetmode1();
					

				} else {
				
					daypay_name.setText("元/天");
					paysetmode2();

				}

			}
		});

		paymode1.setOnClickListener(ll1);
		paymode2.setOnClickListener(ll1);

		setstate();

		return view;
	}

	OnClickListener ll1 = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch (v.getId()) {

			case R.id.payradioButton1:
				// paymode1.setChecked(!paymode1.isChecked());
				// TimePickerDialog timePickerDialog = new
				// TimePickerDialog(getApplicationContext(),
				// new MyTimePickerDialog(),
				// hourOfDay, minute, true);
				// timePickerDialog.show();
				paymode1.setChecked(true);
				paymode2.setChecked(false);

				break;

			case R.id.payradioButton2:
				// paymode2.setChecked(!paymode2.isChecked());
				// paysetmode2();

				paymode1.setChecked(false);
				paymode2.setChecked(true);
				break;

			case R.id.id_getimage_startime:
				TimePickerDialog timePickerDialog = new TimePickerDialog(
						getActivity(), new MyTimePickerDialog(), hourOfDay1,
						minute1, true);
				timePickerDialog.show();
				timemode = 0;
				break;
			case R.id.id_getimage_endtime:
				timePickerDialog = new TimePickerDialog(getActivity(),
						new MyTimePickerDialog(), hourOfDay1, minute1, true);
				timePickerDialog.show();
				timemode = 1;
				break;

			case R.id.DaypayradioButton:
				if (counpmode1.isChecked()) {
					daypay.setFocusableInTouchMode(true);
					daypay.requestFocus();
					daypay.setText("");

				} else {
					daypay.setFocusableInTouchMode(false);
					daypay.clearFocus();
					daypay.setText("");
				}
				break;
			case R.id.DaytimeradioButton:
				
			
				if (counpmode2.isChecked()) {
					daytimepay.setFocusableInTouchMode(true);
					daytimepay.requestFocus();
					daytimepay.setText("");
					startime.setClickable(true);
					endtime.setClickable(true);

				} else {
					daytimepay.setFocusableInTouchMode(false);
					daytimepay.clearFocus();
					daytimepay.setText("");
					startime.setClickable(false);
					endtime.setClickable(false);
					startime.setText("");
					endtime.setText("");
				}
			
				break;
			case R.id.telecheckbox:
				
				break;
			case R.id.add_parkinfo_modefybtn:
				modifyRemoteData();

				break;

			}

		}
	};

	OnFocusChangeListener onfocll = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.id_getimage_parkovertime:
				if (hasFocus) {
					if (parkovertime.getText().toString().trim().equals("0.0")
							&& paymode1.isChecked()) {
						parkovertime.setText("");

					}

				} else if (parkovertime.getText().toString().trim().equals("")) {
					parkovertime.setText("0.0");

				}
				break;
			case R.id.id_getimage_parktime:

				if (hasFocus) {
					if (getimage_parktime.getText().toString().trim()
							.equals("0.0")
							&& paymode1.isChecked()) {
						getimage_parktime.setText("");

					}

				} else if (getimage_parktime.getText().toString().trim()
						.equals("")) {
					getimage_parktime.setText("0.0");

				}
				break;
			case R.id.id_getimage_daypay:
				if (hasFocus) {
					if (daypay.getText().toString().trim().equals("0.0")) {
						daypay.setText("");

					}

				} else if (daypay.getText().toString().trim().equals("")) {
					daypay.setText("0.0");

				}
				break;
			case R.id.id_getimage_daytimepay:

				if (hasFocus) {
					if (daytimepay.getText().toString().trim().equals("0.0")) {
						daytimepay.setText("");

					}

				} else if (daytimepay.getText().toString().trim().equals("")) {
					daytimepay.setText("0.0");

				}
				break;
				
			case R.id.feeTypeSecMinuteOfActivite:

				if (hasFocus) {
					if (feeTypeSecMinuteOfActivite.getText().toString().trim().equals("0")) {
						feeTypeSecMinuteOfActivite.setText("");

					}

				} else if (feeTypeSecMinuteOfActivite.getText().toString().trim().equals("")) {
					feeTypeSecMinuteOfActivite.setText("0");

				}
				break;

			case R.id.feeTypeFixedMinuteOfInActivite:

				if (hasFocus) {
					if (otherActivite.getText().toString().trim().equals("0")) {
						otherActivite.setText("");

					}

				} else if (otherActivite.getText().toString().trim().equals("")) {
					otherActivite.setText("0");

				}
				break;

				
			}

		}
	};

	
	
	public class MyTimePickerDialog implements
			TimePickerDialog.OnTimeSetListener {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub

			if (timemode == 0) {

				if (minute < 10)
					startime.setText("" + hourOfDay + ":" + "0" + minute);
				else
					startime.setText("" + hourOfDay + ":" + minute);
				starthouse = hourOfDay;
				startminute = minute;
				// endtime.setText("");
			} else if(timemode==1){

				if (minute < 10)
					endtime.setText("" + hourOfDay + ":" + "0" + minute);
				else
					endtime.setText("" + hourOfDay + ":" + minute);
			}else if(timemode==2)
			{
				if(hourOfDay==0)
					pay_mode1_intime.setText("1");
				else
					pay_mode1_intime.setText(""+hourOfDay);
				
				
			}
		}

	}

	void paysetmode1() {

		daypay_name.setText("元/小时");
		pay_span_layout.setFocusableInTouchMode(true);
		
		getimage_parktime.setFocusableInTouchMode(true);
		getimage_parktime.requestFocus();
		
		feeTypeSecMinuteOfActivite.setFocusableInTouchMode(true);
		

		parkovertime.setFocusableInTouchMode(true);
		// parkovertime.requestFocus();
		
		feeTypeSecMinuteOfActivite.setText("0");

		pay_mode1_intime.setText("1");
		parkovertime.setText("0.0");

		paynomal.setFocusableInTouchMode(false);
		paynomal.clearFocus();
		paynomal.setText("");
		otherActivite.setText("");
		otherActivite.setFocusableInTouchMode(false);
		otherActivite.clearFocus();
		
		//分段计费可以使用优惠时段
		

		daytimepay.setFocusableInTouchMode(true);
		counpmode2.setClickable(true);
		
		startime.setClickable(true);
		endtime.setClickable(true);
	}

	void setstate() {

		startime.setClickable(false);
		endtime.setClickable(false);
		daypay.setFocusableInTouchMode(false);

	}

	void paysetmode2() {

		
		pay_span_layout.setFocusableInTouchMode(false);
		
		

		getimage_parktime.setFocusableInTouchMode(false);
		getimage_parktime.clearFocus();
		parkovertime.setFocusableInTouchMode(false);
		parkovertime.clearFocus();
		
		feeTypeSecMinuteOfActivite.setFocusableInTouchMode(false);
		feeTypeSecMinuteOfActivite.clearFocus();

		paynomal.setFocusableInTouchMode(true);
		paynomal.requestFocus();
		
		otherActivite.setFocusableInTouchMode(true);
		

		paynomal.setText("0.0");
		otherActivite.setText("0");
		
		
		parkovertime.setText("");
		getimage_parktime.setText("");
		paynomal.setText("");
		feeTypeSecMinuteOfActivite.setText("");
		pay_mode1_intime.setText("");
		
		


		counpmode2.setClickable(false);
		daytimepay.setFocusableInTouchMode(false);
		daytimepay.setText("");
		startime.setText("");
		endtime.setText("");
		startime.setClickable(false);
		endtime.setClickable(false);
		if(counpmode2.isChecked())
		{
			counpmode2.setChecked(false);
			
		}
		
	
	
	}

	private void Binddata() {

		if (Bimp.tempTParkInfo != null) {

			getimage_parktime.setText(""
					+ Bimp.tempTParkInfo.feeTypeSecInScopeHourMoney);

			parkovertime.setText(""
					+ Bimp.tempTParkInfo.feeTypeSecOutScopeHourMoney);

			daypay.setText("" + Bimp.tempTParkInfo.discountHourAlldayMoney);

			daytimepay.setText("" + Bimp.tempTParkInfo.discountSecHourMoney);
			
			parktele.setText(""+Bimp.tempTParkInfo.parkPhone);
			if(Bimp.tempTParkInfo.teleable==1)
			{
				telecheckbox.setChecked(true);
				
			}else
				telecheckbox.setChecked(false);
			if (Bimp.tempTParkInfo.feeTypeSecInScopeHours ==0)
				pay_mode1_intime.setText("1");
			else
				pay_mode1_intime.setText(""+Bimp.tempTParkInfo.feeTypeSecInScopeHours);
				

			if (Bimp.tempTParkInfo.isDiscountAllday == 1) {
				counpmode1.setChecked(true);
				daypay.setFocusableInTouchMode(true);

			} else {
				counpmode1.setChecked(false);
				daypay.setText("");
				daypay.setFocusableInTouchMode(false);
			}

			if (Bimp.tempTParkInfo.isDiscountSec == 1) {
				counpmode2.setChecked(true);
				daytimepay.setFocusableInTouchMode(true);
				if (Bimp.tempTParkInfo.discountSecStartHour != null)
					startime.setText(format
							.format(Bimp.tempTParkInfo.discountSecStartHour));
				   startime.setClickable(true);
				   endtime.setClickable(true);
				   
				if (Bimp.tempTParkInfo.discountSecEndHour != null)
					endtime.setText(format
							.format(Bimp.tempTParkInfo.discountSecEndHour));
			} else {
				counpmode2.setChecked(false);
				daytimepay.setFocusableInTouchMode(false);
				daytimepay.setText("");
				startime.setText("");
				endtime.setText("");
				startime.setClickable(false);
				endtime.setClickable(false);
			}

			if (Bimp.tempTParkInfo.feeType == 1) {
				paymode1.setChecked(true);
				paymode2.setChecked(false);
				paynomal.setText("");
				otherActivite.setText("");
			 	feeTypeSecMinuteOfActivite.setText(""+Bimp.tempTParkInfo.feeTypeSecMinuteOfActivite);

				// paynomal.setFocusableInTouchMode(false);
				// spinner.setFocusableInTouchMode(true);
				// getimage_parktime.setFocusableInTouchMode(true);
				// parkovertime.setFocusableInTouchMode(true);

			} else if (Bimp.tempTParkInfo.feeType == 2) {

				paymode2.setChecked(true);
				paymode1.setChecked(false);
				getimage_parktime.setText("");
				parkovertime.setText("");
				paynomal.setFocusableInTouchMode(true);
				
				otherActivite.setText(""+Bimp.tempTParkInfo.feeTypeFixedMinuteOfInActivite);
			 	feeTypeSecMinuteOfActivite.setText("");

				// spinner.setFocusableInTouchMode(false);
				// getimage_parktime.setFocusableInTouchMode(false);
				// parkovertime.setFocusableInTouchMode(false);

				paynomal.setText("" + Bimp.tempTParkInfo.feeTypefixedHourMoney);
			}

		
			
			
			if(Bimp.tempTParkInfo.latLngArray==null||Bimp.tempTParkInfo.latLngArray.size()==0)
			{
				
				
			}else
			{
				
				if(Bimp.tempTParkInfo.latLngArray.get(0).isOpen==1)
				{
					 imageLoader.displayImage("drawable://" + R.drawable.start,
					 LightImage);
				}else
				{
					 imageLoader.displayImage("drawable://" + R.drawable.stop,
					 LightImage);
					
				}
				
				
				
			}
			
			
			chewei_pay_info.setFocusableInTouchMode(false);
			chewei_pay_info.clearFocus();
			chewei_info.setFocusableInTouchMode(true);
			chewei_info.requestFocus();

		}else
		{
		
			
			
		}

	}

	

	private void modifyRemoteData() {
		if (counpmode2.isChecked()) {
			if (startime.getText() == null
					|| startime.getText().toString().trim().equals("")) {
				Toast.makeText(getActivity(), "优惠时段开始时间不能为空",
						Toast.LENGTH_SHORT).show();
				return;

			}
			if (endtime.getText() == null
					|| endtime.getText().toString().trim().equals("")) {
				Toast.makeText(getActivity(), "优惠时段结束时间不能为空",
						Toast.LENGTH_SHORT).show();
				return;
			}

		}

		if (paymode1.isChecked()) {
			feeType = 1;
			
			int currenthous=1;
			if(pay_mode1_intime==null||pay_mode1_intime.getText()==null||pay_mode1_intime.getText().toString().trim().equals(""))
				currenthous=1;
			else
				currenthous=Integer.valueOf(pay_mode1_intime.getText().toString().trim());
			feeTypeSecInScopeHours=currenthous;
			if (getimage_parktime.getText().toString().trim().equals(""))
				feeTypeSecInScopeHourMoney = 0;
			else
				feeTypeSecInScopeHourMoney = Double.valueOf(getimage_parktime
						.getText().toString().trim());
			if (parkovertime.getText().toString().trim().equals(""))
				feeTypeSecOutScopeHourMoney = 0;
			else
				feeTypeSecOutScopeHourMoney = Double.valueOf(parkovertime
						.getText().toString().trim());
			feeTypefixedHourMoney = 0.0;
			
			if(feeTypeSecMinuteOfActivite.getText().toString().trim()!="")
			feeactivity=Integer.valueOf(feeTypeSecMinuteOfActivite.getText().toString().trim());
			else
			feeactivity=0;
			
			feeoutactivity=0;

		} else {
			feeType = 2;
			feeTypeSecInScopeHours = 0;
			feeTypeSecInScopeHourMoney = 0.0;
			feeTypeSecOutScopeHourMoney = 0.0;
			if (paynomal.getText().toString().trim().equals(""))
				feeTypefixedHourMoney = 0;
			else
				feeTypefixedHourMoney = Double.valueOf(paynomal.getText()
						.toString().trim());
			
			if(otherActivite.getText().toString().trim()!="")
				feeoutactivity=Integer.valueOf(otherActivite.getText().toString().trim());
				else
					feeoutactivity=0;
				
				feeactivity=0;

		}

		if (counpmode1.isChecked()) {
			isDiscountAllday = 1;
			discountHourAlldayMoney = Double.valueOf(daypay.getText()
					.toString().trim());

		} else {
			isDiscountAllday = 2;
			discountHourAlldayMoney = 0.0;

		}

		if (counpmode2.isChecked()) {
			isDiscountSec = 1;
			if (daytimepay.getText().toString().trim().equals("")) {
				discountSecHourMoney = 0.0d;
			} else {
				discountSecHourMoney = Double.valueOf(daytimepay.getText()
						.toString().trim());
			}

		} else {
			isDiscountSec = 2;
			discountSecHourMoney = 0.0;

		}

		// Dao<TParkInfoEntity, Integer> dao = getHelper()
		// .getParkdetailDao();

		// Calendar currentcalendar=Calendar.getInstance();
		// String seconds=
		// String.valueOf(currentcalendar.get(Calendar.SECOND));
		// String
		// minute=String.valueOf(currentcalendar.get(Calendar.MINUTE));

		try {

			Bimp.tempTParkInfo.feeType = feeType;
			Bimp.tempTParkInfo.feeTypefixedHourMoney = feeTypefixedHourMoney;
			Bimp.tempTParkInfo.feeTypeSecInScopeHourMoney = feeTypeSecInScopeHourMoney;
			Bimp.tempTParkInfo.feeTypeSecOutScopeHourMoney = feeTypeSecOutScopeHourMoney;
			Bimp.tempTParkInfo.feeTypeSecInScopeHours = feeTypeSecInScopeHours;
			Bimp.tempTParkInfo.discountHourAlldayMoney = discountHourAlldayMoney;
			Bimp.tempTParkInfo.discountSecHourMoney = discountSecHourMoney;
			Bimp.tempTParkInfo.isDiscountAllday = isDiscountAllday;
			Bimp.tempTParkInfo.isDiscountSec = isDiscountSec;
			Bimp.tempTParkInfo.feeTypeFixedMinuteOfInActivite=feeoutactivity;
			Bimp.tempTParkInfo.feeTypeSecMinuteOfActivite=feeactivity;
			Bimp.tempTParkInfo.createDate = new Date();
			if (counpmode2.isChecked()) {
				if (startime.getText() != null && startime.getText() != "")
					Bimp.tempTParkInfo.discountSecStartHour = format
							.parse(startime.getText().toString());

				if (endtime.getText() != null && endtime.getText() != "")
					Bimp.tempTParkInfo.discountSecEndHour = format
							.parse(endtime.getText().toString());

			} else {
				Bimp.tempTParkInfo.discountSecStartHour = null;

				Bimp.tempTParkInfo.discountSecEndHour = null;
			}
if(telecheckbox.isChecked())
	Bimp.tempTParkInfo.teleable=1;
else
	Bimp.tempTParkInfo.teleable=0;
if(!parktele.getText().toString().trim().equals(""))
{
	try {
		  Bimp.tempTParkInfo.parkPhone=Long.valueOf(parktele.getText().toString().trim());	
	} catch (Exception e) {
		 Bimp.tempTParkInfo.parkPhone=0;
	}
    

}
			Bimp.tempTParkImageList = null;
			Bimp.tempTParkLocList = null;

			HttpRequestAni<ComResponse<TParkInfoEntity>> httpRequestAni = new HttpRequestAni<ComResponse<TParkInfoEntity>>(
					getActivity(), "/a/parkinfoprod/save",
					new TypeToken<ComResponse<TParkInfoEntity>>() {
					}.getType(), Bimp.tempTParkInfo, TParkInfoEntity.class) {

				@Override
				public void callback(ComResponse<TParkInfoEntity> arg0) {
					// TODO Auto-generated method stub

					if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {
						Bimp.tempTParkInfo = arg0.getResponseEntity();
						Toast.makeText(getActivity(), "保存成功\r\n",
								Toast.LENGTH_SHORT).show();
						Binddata();

					} else {

						Toast.makeText(getActivity(),
								"数据采集失败\r\n" + arg0.getErrorMessage(),
								Toast.LENGTH_SHORT).show();
					}

				}
			};

			httpRequestAni.execute();

		} catch (ParseException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
	}

	private class SpinnerAdapter extends ArrayAdapter<String> {
		Context context;
		String[] items = new String[] {};

		public SpinnerAdapter(final Context context,
				final int textViewResourceId, final String[] objects) {
			super(context, textViewResourceId, objects);
			this.items = objects;
			this.context = context;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = activity.getLayoutInflater();
			if (convertView == null) {

				convertView = inflater.inflate(
						android.R.layout.simple_gallery_item, parent, false);
			}

			TextView tv = (TextView) convertView
					.findViewById(android.R.id.text1);
			tv.setText(items[position]);
			tv.setGravity(Gravity.CENTER);
			tv.setTextColor(Color.BLUE);
			tv.setTextSize(15);
			return convertView;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = activity.getLayoutInflater();
			if (convertView == null) {

				convertView = inflater.inflate(
						android.R.layout.simple_gallery_item, parent, false);
			}

			// android.R.id.text1 is default text view in resource of the
			// android.
			// android.R.layout.simple_spinner_item is default layout in
			// resources of android.

			TextView tv = (TextView) convertView
					.findViewById(android.R.id.text1);
			tv.setText(items[position]);

			tv.setTextColor(Color.BLACK);
			tv.setTextSize(12);
			return convertView;
		}
	}
	
	void updateparkstatus(int status)
	
	{
		final int newstate=status;
		if(SessionUtils.loginUser==null||SessionUtils.loginUser.parkInfoAdm==null)
		{
			Toast.makeText(getActivity(), "账号出错，请重新登陆", Toast.LENGTH_LONG).show();
			return;
			
		}
		HttpRequestAni<ComResponse<TParkInfoEntity>> httpRequestAni = new HttpRequestAni<ComResponse<TParkInfoEntity>>(
				getActivity(), "/a/parkingprod/open/"+SessionUtils.loginUser.parkInfoAdm.parkId+"/"+status,
				new TypeToken<ComResponse<TParkInfoEntity>>() {
				}.getType()) {

			@Override
			public void callback(ComResponse<TParkInfoEntity> arg0) {
				// TODO Auto-generated method stub

				if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {
					Bimp.tempTParkInfo = arg0.getResponseEntity();
					Toast.makeText(getActivity(), "状态更改成功\r\n",
							Toast.LENGTH_SHORT).show();
				
					//改变session中的值
				int latsize=SessionUtils.loginUser.parkInfoAdm.latLngArray.size();
					for(int i=0;i<latsize;i++)
					SessionUtils.loginUser.parkInfoAdm.latLngArray.get(i).isOpen=newstate;
					
					

				} else {

					Bimp.tempTParkInfo=SessionUtils.loginUser.parkInfoAdm;
					Toast.makeText(getActivity(),
							"状态更改失败\r\n" + arg0.getErrorMessage(),
							Toast.LENGTH_SHORT).show();
				}
				Binddata();
			}
			
		};

		httpRequestAni.execute();
		
	}
}