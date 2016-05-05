package com.mc.parking.client.ui.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.admin.adrm.PhasedListener;
import com.mc.parking.admin.adrm.PhasedSeekBar;
import com.mc.parking.admin.adrm.SimplePhasedAdapter;
import com.mc.parking.client.Constants;
import com.mc.parking.client.PackingApplication;
import com.mc.park.client.R;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkInfo_ImgEntity;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.HttpRequest;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.ParkActivity;
import com.mc.parking.client.ui.UploadPhotoActivity;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.zxing.camera.MipcaActivityCapture;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CheweiFragment extends Fragment {

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
	private LinearLayout progress, pay_info;
	TextView parkmode;
	int PAY_VIEW = 0;
	// 对于控件，0代表开放，1代表关闭
	int PARK_OPEN = 0;
	int PARK_CLOSE = 1;
	private TextView ordernotcomecount;
	private TextView ordernotoutcount;

	SimpleDateFormat format = new SimpleDateFormat("HH:mm");

	RadioButton paymode1, paymode2;
	TextView compname, contactmen, tele, parkname_pay, parkaddress, getimage_parktime, parkovertime, paynomal, daypay,
			daytimepay, startime, endtime, couponpay, locationtext, show_pay_info, feeTypeFixedMinuteOfInActivite,
			feeTypeSecMinuteOfActivite;

	RelativeLayout myrelate;
	Spinner spinner;
	CheckBox counpmode1, counpmode2;
	private int hourOfDay1, minute1;
	private int timemode = 0;

	public int starthouse = 0;
	public int startminute = 0;

	PhasedSeekBar psbHorizontal;

	Button modefybtn;

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

	int feeactivity, feeoutactivity;

	Activity activity;
	public Date discountSecEndHour;
	String[] numbers = { "1小时以内", "2小时以内", "3小时以内", "4小时以内", "5小时以内", "6小时以内", "7小时以内", "8小时以内", "9小时以内", "10小时以内",
			"11小时以内", "12小时以内" };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_admin_chewei_new, container, false);
		activity = getActivity();
		realy_all = (RelativeLayout) view.findViewById(R.id.chewei_all_relay);
		myrelate = (RelativeLayout) view.findViewById(R.id.myrelate);
		myrelate.setOnClickListener(ll1);
		progress = (LinearLayout) view.findViewById(R.id.youhui_progressContainer);
		feeTypeFixedMinuteOfInActivite = (EditText) view.findViewById(R.id.feeTypeFixedMinuteOfInActivite);
		feeTypeSecMinuteOfActivite = (EditText) view.findViewById(R.id.feeTypeSecMinuteOfActivite);
		// realy_all.setVisibility(View.INVISIBLE);
		// progress.setVisibility(View.VISIBLE);
		Calendar calendar = Calendar.getInstance();
		hourOfDay1 = calendar.get(Calendar.HOUR_OF_DAY);
		minute1 = calendar.get(Calendar.MINUTE); // 获得当前的秒
		view = initivaule(view);

		view = initetextview(view);
		paysetmode1();
		paymode1.setFocusableInTouchMode(true);
		paymode1.requestFocus();

		if (SessionUtils.loginUser != null) {
			TParkInfoEntity sessoinparkinfo = SessionUtils.loginUser.parkInfoAdm;
			if (sessoinparkinfo != null) {

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
				parkimagenum.setText("" + (imageUrls == null ? 0 : imageUrls.length));
				parkinfo = sessoinparkinfo;

			}
			Bimp.tempTParkInfo = sessoinparkinfo;
			Binddata();
		}

		return view;
	}

	private View initivaule(View view) {

		parkmode = (TextView) view.findViewById(R.id.offine_name);
		CheweiTopImag = (ImageView) view.findViewById(R.id.chewei_packdetail_img);
		CheweiTopImag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (imageUrls == null) {
					Toast.makeText(getActivity(), "未找到图片", Toast.LENGTH_SHORT).show();

				} else {
					Intent intent = new Intent(getActivity(), UploadPhotoActivity.class);
					Bundle buidle = new Bundle();
					buidle.putSerializable("parkinfo", parkinfo);
					intent.putExtras(buidle);
					startActivity(intent);
				}

			}
		});

		// 指示灯
		LightImage = (ImageView) view.findViewById(R.id.chewei_light_img);
		LightImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (CHECK_LIGHT == 0) {
					imageLoader.displayImage("drawable://" + R.drawable.start, LightImage);
					// chewei_info.setVisibility(View.VISIBLE);
					parkmode.setText("开始接单");
					CHECK_LIGHT = 1;
					psbHorizontal.setPosition(0);
					updateparkstatus(Constants.PARK_STATE_OPEN);

				} else {

					imageLoader.displayImage("drawable://" + R.drawable.stop, LightImage);
					// chewei_info.setVisibility(View.INVISIBLE);
					parkmode.setText("停止接单");
					CHECK_LIGHT = 0;
					psbHorizontal.setPosition(1);
					updateparkstatus(Constants.PARK_STATE_CLOSE);
				}

			}
		});
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
		chewei_scan = (RelativeLayout) view.findViewById(R.id.chewei_scan_Relat);

		chewei_pay_info = (RelativeLayout) view.findViewById(R.id.pay_info_mode);
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
		TextView fabuChewei = (TextView) view.findViewById(R.id.chewei_fabuChewei);
		final TextView cheweiView = (TextView) view.findViewById(R.id.chewei_cheweinumber);

		TextView parkdetail_name = (TextView) view.findViewById(R.id.chewei_parkname);
		TextView parkdetail_address = (TextView) view.findViewById(R.id.chewei_parkdistance);

		TuserInfo userInfo = SessionUtils.loginUser;
		if (userInfo != null && (userInfo.userType == Constants.USER_TYPE_PADMIN
				|| userInfo.userType == Constants.USER_TYPE_PSADMIN)) {
			String cheweiName = userInfo.parkInfoAdm == null ? "[empty]" : userInfo.parkInfoAdm.parkname;
			parkdetail_name.setText(cheweiName);
			parkdetail_address.setText(userInfo.parkInfoAdm == null ? "[empty]" : userInfo.parkInfoAdm.address);
		} else {
			parkdetail_name.setText(Html.fromHtml("<html><font color=red>您不是车位管理员,请重新登录.</font></html>"));
		}

		TextView reduceChewei = (TextView) view.findViewById(R.id.chewei_reduceChewei);
		parkname = (TextView) view.findViewById(R.id.chewei_parkname);
		parkaddinfo = (TextView) view.findViewById(R.id.chewei_parkdistance);
		parkimagenum = (TextView) view.findViewById(R.id.packdetail_img_number);

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_empty).cacheInMemory(true)
				.cacheOnDisc(false).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

		fabuChewei.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CheweiFabuDialogFragment dialog2 = new CheweiFabuDialogFragment(cheweiView);
				dialog2.show(getFragmentManager(), "FabuDialogFragment");
			}

		});

		reduceChewei.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CheweiReduceDialogFragment dialog2 = new CheweiReduceDialogFragment(cheweiView);
				dialog2.show(getFragmentManager(), "CheweiReduceDialogFragment");
			}

		});

		// new ParkingDetailTask() {
		//
		// protected void onSuccess(TParkInfo_LocEntity data) throws Exception {
		// super.onSuccess(data);
		// progress.setVisibility(View.GONE);
		// realy_all.setVisibility(View.VISIBLE);
		// if (data != null)
		//
		// InitiParkInfo(data.parkInfo);
		//
		// };
		// }.execute();

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

		if (SessionUtils.loginUser != null && (SessionUtils.loginUser.userType == Constants.USER_TYPE_PSADMIN
				|| SessionUtils.loginUser.userType == Constants.USER_TYPE_SADMIN)) {
			// chewei_pay_info.setVisibility(View.VISIBLE);
			// pay_info.setVisibility(View.VISIBLE);

		} else {
			chewei_pay_info.setVisibility(View.GONE);
			pay_info.setVisibility(View.GONE);

		}

		return view;
	}

	private void InitiParkInfo(TParkInfoEntity data) {

		if (data != null) {
			if (data.imgUrlArray != null) {
				imageUrls = new String[data.imgUrlArray.size()];
				if (imageUrls.length > 0) {
					imageUrls[0] = data.imgUrlArray.get(0).imgUrlHeader + data.imgUrlArray.get(0).imgUrlPath;
					imageLoader.displayImage(imageUrls[0], CheweiTopImag);
				}
				// parkaddinfo.setText(data.getMapAddress());
				parkimagenum.setText("" + imageUrls.length);
			}
			parkname.setText(data.parkname);
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

	private View initetextview(View view) {
		ordernotcomecount = (TextView) view.findViewById(R.id.ordernotcome);
		ordernotoutcount = (TextView) view.findViewById(R.id.ordernotout);
		// getnotcomeincount();
		getimage_parktime = (TextView) view.findViewById(R.id.id_getimage_parktime);
		paynomal = (TextView) view.findViewById(R.id.id_getimage_paynomal);
		daypay = (TextView) view.findViewById(R.id.id_getimage_daypay);
		daytimepay = (TextView) view.findViewById(R.id.id_getimage_daytimepay);
		startime = (TextView) view.findViewById(R.id.id_getimage_startime);
		endtime = (TextView) view.findViewById(R.id.id_getimage_endtime);
		parkovertime = (TextView) view.findViewById(R.id.id_getimage_parkovertime);
		spinner = (Spinner) view.findViewById(R.id.spinner1);
		SpinnerAdapter adapter = new SpinnerAdapter(activity, android.R.layout.simple_spinner_item, numbers);
		spinner.setAdapter(adapter);
		couponpay = (TextView) view.findViewById(R.id.id_getimage_daypay);
		show_pay_info = (TextView) view.findViewById(R.id.show_pay_info);

		counpmode1 = (CheckBox) view.findViewById(R.id.DaypayradioButton);
		counpmode2 = (CheckBox) view.findViewById(R.id.DaytimeradioButton);

		startime.setOnClickListener(ll1);
		endtime.setOnClickListener(ll1);

		parkovertime.setOnClickListener(ll1);
		parkovertime.setOnFocusChangeListener(onfocll);
		getimage_parktime.setOnFocusChangeListener(onfocll);
		daypay.setOnFocusChangeListener(onfocll);
		daytimepay.setOnFocusChangeListener(onfocll);
		counpmode1.setOnClickListener(ll1);

		counpmode2.setOnClickListener(ll1);

		paymode1 = (RadioButton) view.findViewById(R.id.payradioButton1);
		paymode2 = (RadioButton) view.findViewById(R.id.payradioButton2);
		modefybtn = (Button) view.findViewById(R.id.add_parkinfo_modefybtn);
		psbHorizontal = (PhasedSeekBar) view.findViewById(R.id.psb_hora);

		final Resources resources = getResources();
		psbHorizontal.setAdapter(new SimplePhasedAdapter(resources,
				new int[] { R.drawable.btn_square_selector, R.drawable.btn_xis_selector }));

		psbHorizontal.setPosition(0);

		psbHorizontal.setListener(new PhasedListener() {
			@Override
			public void onPositionSelected(int position) {
				if (position == 0) {
					imageLoader.displayImage("drawable://" + R.drawable.start, LightImage);
					// chewei_info.setVisibility(View.VISIBLE);
					parkmode.setText("开始接单");
					CHECK_LIGHT = 1;
					updateparkstatus(1);

				} else if (position == 1) {
					imageLoader.displayImage("drawable://" + R.drawable.stop, LightImage);
					// chewei_info.setVisibility(View.INVISIBLE);
					parkmode.setText("停止接单");
					CHECK_LIGHT = 0;
					updateparkstatus(0);

				}
			}
		});
		modefybtn.setOnClickListener(ll1);
		paymode1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					paysetmode1();

				} else {

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
				TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new MyTimePickerDialog(),
						hourOfDay1, minute1, true);
				timePickerDialog.show();
				timemode = 0;
				break;
			case R.id.id_getimage_endtime:
				timePickerDialog = new TimePickerDialog(getActivity(), new MyTimePickerDialog(), hourOfDay1, minute1,
						true);
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
					daypay.setText("0.0");
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
					daytimepay.setText("0.0");
					startime.setClickable(false);
					endtime.setClickable(false);
					startime.setText("");
					endtime.setText("");
				}
				break;
			case R.id.add_parkinfo_modefybtn:
				modifyRemoteData();

				break;
			case R.id.myrelate:
				if (activity != null && SessionUtils.loginUser != null && SessionUtils.loginUser.parkInfoAdm != null) {
					Intent intent = new Intent();
					intent.setClass(activity, AdminHomeActivity.class);
					startActivity(intent);
				}
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
					if (parkovertime.getText().toString().trim().equals("0.0") && paymode1.isChecked()) {
						parkovertime.setText("");

					}

				} else if (parkovertime.getText().toString().trim().equals("")) {
					parkovertime.setText("0.0");

				}
				break;
			case R.id.id_getimage_parktime:

				if (hasFocus) {
					if (getimage_parktime.getText().toString().trim().equals("0.0") && paymode1.isChecked()) {
						getimage_parktime.setText("");

					}

				} else if (getimage_parktime.getText().toString().trim().equals("")) {
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

			}

		}
	};

	public class MyTimePickerDialog implements TimePickerDialog.OnTimeSetListener {

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
			} else {

				if (minute < 10)
					endtime.setText("" + hourOfDay + ":" + "0" + minute);
				else
					endtime.setText("" + hourOfDay + ":" + minute);
			}
		}

	}

	void paysetmode1() {

		spinner.setFocusableInTouchMode(true);
		getimage_parktime.setFocusableInTouchMode(true);
		getimage_parktime.requestFocus();

		parkovertime.setFocusableInTouchMode(true);
		// parkovertime.requestFocus();

		paynomal.setFocusableInTouchMode(false);
		paynomal.clearFocus();
		paynomal.setText("0.0");

	}

	void setstate() {

		startime.setClickable(false);
		endtime.setClickable(false);
		daypay.setFocusableInTouchMode(false);

	}

	void paysetmode2() {

		spinner.setFocusableInTouchMode(false);
		spinner.clearFocus();

		getimage_parktime.setFocusableInTouchMode(false);
		getimage_parktime.clearFocus();
		parkovertime.setFocusableInTouchMode(false);
		parkovertime.clearFocus();

		paynomal.setFocusableInTouchMode(true);
		paynomal.requestFocus();

		parkovertime.setText("0.0");
		getimage_parktime.setText("0.0");
		paynomal.setText("");
	}

	private void Binddata() {

		if (Bimp.tempTParkInfo != null) {

			getimage_parktime.setText("" + Bimp.tempTParkInfo.feeTypeSecInScopeHourMoney);

			parkovertime.setText("" + Bimp.tempTParkInfo.feeTypeSecOutScopeHourMoney);

			daypay.setText("" + Bimp.tempTParkInfo.discountHourAlldayMoney);

			daytimepay.setText("" + Bimp.tempTParkInfo.discountSecHourMoney);

			if (Bimp.tempTParkInfo.feeTypeSecInScopeHours < 12)
				spinner.setSelection(Bimp.tempTParkInfo.feeTypeSecInScopeHours);

			if (Bimp.tempTParkInfo.isDiscountAllday == 1) {
				counpmode1.setChecked(true);
				daytimepay.setFocusableInTouchMode(true);

			} else {
				counpmode1.setChecked(false);
				daytimepay.setFocusableInTouchMode(false);
			}

			if (Bimp.tempTParkInfo.isDiscountSec == 1) {
				counpmode2.setChecked(true);
				daypay.setFocusableInTouchMode(true);
				if (Bimp.tempTParkInfo.discountSecStartHour != null)
					startime.setText(format.format(Bimp.tempTParkInfo.discountSecStartHour));

				if (Bimp.tempTParkInfo.discountSecEndHour != null)
					endtime.setText(format.format(Bimp.tempTParkInfo.discountSecEndHour));
			} else {
				counpmode2.setChecked(false);
				daypay.setFocusableInTouchMode(false);
				startime.setText("");
				endtime.setText("");
			}

			if (Bimp.tempTParkInfo.feeType == 1) {
				paymode1.setChecked(true);
				paymode2.setChecked(false);
				paynomal.setText("0.0");
				feeTypeFixedMinuteOfInActivite.setText("0");
				feeTypeSecMinuteOfActivite.setText("" + Bimp.tempTParkInfo.feeTypeSecMinuteOfActivite);

				// paynomal.setFocusableInTouchMode(false);
				// spinner.setFocusableInTouchMode(true);
				// getimage_parktime.setFocusableInTouchMode(true);
				// parkovertime.setFocusableInTouchMode(true);

			} else if (Bimp.tempTParkInfo.feeType == 2) {

				paymode2.setChecked(true);
				paymode1.setChecked(false);

				paynomal.setFocusableInTouchMode(true);

				feeTypeFixedMinuteOfInActivite.setText("" + Bimp.tempTParkInfo.feeTypeFixedMinuteOfInActivite);
				feeTypeSecMinuteOfActivite.setText("0");

				// spinner.setFocusableInTouchMode(false);
				// getimage_parktime.setFocusableInTouchMode(false);
				// parkovertime.setFocusableInTouchMode(false);

				paynomal.setText("" + Bimp.tempTParkInfo.feeTypefixedHourMoney);
			}

			if (Bimp.tempTParkInfo.latLngArray == null || Bimp.tempTParkInfo.latLngArray.size() == 0) {

			} else {

				if (Bimp.tempTParkInfo.latLngArray.get(0).isOpen == 1) {
					psbHorizontal.setPosition(PARK_OPEN);
					imageLoader.displayImage("drawable://" + R.drawable.start, LightImage);

				} else {
					psbHorizontal.setPosition(PARK_CLOSE);
					imageLoader.displayImage("drawable://" + R.drawable.stop, LightImage);

				}

			}

			chewei_pay_info.setFocusableInTouchMode(false);
			chewei_pay_info.clearFocus();
			chewei_info.setFocusableInTouchMode(true);
			chewei_info.requestFocus();

		} else {

		}

	}

	private void modifyRemoteData() {
		if (counpmode2.isChecked()) {
			if (startime.getText() == null || startime.getText().toString().trim().equals("")) {
				Toast.makeText(getActivity(), "优惠时段开始时间不能为空", Toast.LENGTH_SHORT).show();
				return;

			}
			if (endtime.getText() == null || endtime.getText().toString().trim().equals("")) {
				Toast.makeText(getActivity(), "优惠时段结束时间不能为空", Toast.LENGTH_SHORT).show();
				return;
			}

		}

		if (paymode1.isChecked()) {
			feeType = 1;
			feeTypeSecInScopeHours = spinner.getSelectedItemPosition();
			if (getimage_parktime.getText().toString().trim().equals(""))
				feeTypeSecInScopeHourMoney = 0;
			else
				feeTypeSecInScopeHourMoney = Double.valueOf(getimage_parktime.getText().toString().trim());
			if (parkovertime.getText().toString().trim().equals(""))
				feeTypeSecOutScopeHourMoney = 0;
			else
				feeTypeSecOutScopeHourMoney = Double.valueOf(parkovertime.getText().toString().trim());
			feeTypefixedHourMoney = 0.0;

			if (feeTypeSecMinuteOfActivite.getText().toString().trim() != "")
				feeactivity = Integer.valueOf(feeTypeSecMinuteOfActivite.getText().toString().trim());
			else
				feeactivity = 0;

			feeoutactivity = 0;

		} else {
			feeType = 2;
			feeTypeSecInScopeHours = 0;
			feeTypeSecInScopeHourMoney = 0.0;
			feeTypeSecOutScopeHourMoney = 0.0;
			if (paynomal.getText().toString().trim().equals(""))
				feeTypefixedHourMoney = 0;
			else
				feeTypefixedHourMoney = Double.valueOf(paynomal.getText().toString().trim());

			if (feeTypeFixedMinuteOfInActivite.getText().toString().trim() != "")
				feeoutactivity = Integer.valueOf(feeTypeFixedMinuteOfInActivite.getText().toString().trim());
			else
				feeoutactivity = 0;

			feeactivity = 0;

		}

		if (counpmode1.isChecked()) {
			isDiscountAllday = 1;
			discountHourAlldayMoney = Double.valueOf(daypay.getText().toString().trim());

		} else {
			isDiscountAllday = 2;
			discountHourAlldayMoney = 0.0;

		}

		if (counpmode2.isChecked()) {
			isDiscountSec = 1;
			if (daytimepay.getText().toString().trim().equals("")) {
				discountSecHourMoney = 0.0d;
			} else {
				discountSecHourMoney = Double.valueOf(daytimepay.getText().toString().trim());
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
			Bimp.tempTParkInfo.feeTypeFixedMinuteOfInActivite = feeoutactivity;
			Bimp.tempTParkInfo.feeTypeSecMinuteOfActivite = feeactivity;
			Bimp.tempTParkInfo.createDate = new Date();
			if (counpmode2.isChecked()) {
				if (startime.getText() != null && startime.getText() != "")
					Bimp.tempTParkInfo.discountSecStartHour = format.parse(startime.getText().toString());

				if (endtime.getText() != null && endtime.getText() != "")
					Bimp.tempTParkInfo.discountSecEndHour = format.parse(endtime.getText().toString());

			} else {
				Bimp.tempTParkInfo.discountSecStartHour = null;

				Bimp.tempTParkInfo.discountSecEndHour = null;
			}

			Bimp.tempTParkImageList = null;
			Bimp.tempTParkLocList = null;

			HttpRequestAni<ComResponse<TParkInfoEntity>> httpRequestAni = new HttpRequestAni<ComResponse<TParkInfoEntity>>(
					getActivity(), "/a/parkinfoprod/save", new TypeToken<ComResponse<TParkInfoEntity>>() {
					}.getType(), Bimp.tempTParkInfo, TParkInfoEntity.class) {

				@Override
				public void callback(ComResponse<TParkInfoEntity> arg0) {
					// TODO Auto-generated method stub

					if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {
						Bimp.tempTParkInfo = arg0.getResponseEntity();
						SessionUtils.loginUser.parkInfoAdm = arg0.getResponseEntity();
						Toast.makeText(getActivity(), "保存成功\r\n", Toast.LENGTH_SHORT).show();
						Binddata();

					} else {

						Toast.makeText(getActivity(), "数据采集失败\r\n" + arg0.getErrorMessage(), Toast.LENGTH_SHORT).show();
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

		public SpinnerAdapter(final Context context, final int textViewResourceId, final String[] objects) {
			super(context, textViewResourceId, objects);
			this.items = objects;
			this.context = context;
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = activity.getLayoutInflater();
			if (convertView == null) {

				convertView = inflater.inflate(android.R.layout.simple_gallery_item, parent, false);
			}

			TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
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

				convertView = inflater.inflate(android.R.layout.simple_gallery_item, parent, false);
			}

			// android.R.id.text1 is default text view in resource of the
			// android.
			// android.R.layout.simple_spinner_item is default layout in
			// resources of android.

			TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
			tv.setText(items[position]);

			tv.setTextColor(Color.BLACK);
			tv.setTextSize(12);
			return convertView;
		}
	}

	void updateparkstatus(int status)

	{
		final int newstate = status;
		if (SessionUtils.loginUser == null || SessionUtils.loginUser.parkInfoAdm == null) {
			Toast.makeText(getActivity(), "账号出错，请重新登陆", Toast.LENGTH_LONG).show();
			return;

		}
		HttpRequestAni<ComResponse<TParkInfoEntity>> httpRequestAni = new HttpRequestAni<ComResponse<TParkInfoEntity>>(
				getActivity(), "/a/parkingprod/open/" + SessionUtils.loginUser.parkInfoAdm.parkId + "/" + status,
				new TypeToken<ComResponse<TParkInfoEntity>>() {
				}.getType()) {

			@Override
			public void callback(ComResponse<TParkInfoEntity> arg0) {
				// TODO Auto-generated method stub

				if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {
					Bimp.tempTParkInfo = arg0.getResponseEntity();
					Toast.makeText(getActivity(), "状态更改成功\r\n", Toast.LENGTH_SHORT).show();

					// 改变session中的值
					int latsize = SessionUtils.loginUser.parkInfoAdm.latLngArray.size();
					for (int i = 0; i < latsize; i++)
						SessionUtils.loginUser.parkInfoAdm.latLngArray.get(i).isOpen = newstate;

				} else {

					Bimp.tempTParkInfo = SessionUtils.loginUser.parkInfoAdm;
					Toast.makeText(getActivity(), "状态更改失败\r\n" + arg0.getErrorMessage(), Toast.LENGTH_SHORT).show();
				}
				Binddata();
			}

		};

		httpRequestAni.execute();

	}

	public void getnotcomeincount() {

		if (SessionUtils.loginUser != null && SessionUtils.loginUser.parkInfoAdm != null
				&& SessionUtils.loginUser.parkInfoAdm.parkId != null && SessionUtils.loginUser.parkInfoAdm.parkId > 0) {

			HttpRequest<String> httpRequest = new HttpRequest<String>(
					"/a/order/getnotcome/" + SessionUtils.loginUser.parkInfoAdm.parkId, new TypeToken<String>() {
					}.getType()) {

				@Override
				public void onSuccess(String arg0) {
					// TODO Auto-generated method stub
					if (arg0 != null) {
						String[] counts = arg0.split("\\#");
						ordernotcomecount.setText("" + counts[0]);
						ordernotoutcount.setText("" + (counts.length > 1 ? counts[1] : "--"));
					}

				}

				@Override
				public void onFailed(String message) {
					// TODO Auto-generated method stub

				}

			};

			httpRequest.execute();

		}
	}

	@Override
	public void onResume() {
		PackingApplication.getInstance().setCurrentActivity(getActivity());
		getnotcomeincount();
		super.onResume();
	}

	@Override
	public void onStart() {
		PackingApplication.getInstance().setCurrentActivity(getActivity());
		super.onStart();
	}

}
