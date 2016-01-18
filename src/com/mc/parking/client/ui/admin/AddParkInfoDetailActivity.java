package com.mc.parking.client.ui.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.client.Constants;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.ui.admin.ChangeParkPriceFragment.MyTimePickerDialog;
import com.mc.parking.client.utils.DBHelper;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;

public class AddParkInfoDetailActivity extends FragmentActivity {

	Button nextbtn = null;

	RadioButton paymode1, paymode2;
	TextView pay_mode1_intime,compname, contactmen, tele, parkname, parkaddress,cityname,
			getimage_parktime, parkovertime, paynomal, daypay, daytimepay,
			startime, endtime, couponpay, locationtext,feeTypeFixedMinuteOfInActivite,feeTypeSecMinuteOfActivite;
	//Spinner spinner;
	CheckBox counpmode1, counpmode2;
	private int hourOfDay1, minute1;
	private int timemode = 0;
	private DBHelper dbHelper = null;

	public GetParkInfoMapFragment mapFragment = null;
	public AddParkImageFragment imageFragment = null;

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

	public Date discountSecEndHour;
	
	int feeactivite;
	
	int feeoutactivite;
	

	public int starthouse = 0;
	public int startminute = 0;
	LinearLayout pay_span_layout;
	String[] numbers = { "1小时以内", "2小时以内", "3小时以内", "4小时以内", "5小时以内", "6小时以内",
			"7小时以内", "8小时以内", "9小时以内", "10小时以内", "11小时以内", "12小时以内" };
	

	SimpleDateFormat format = new SimpleDateFormat("HH:mm");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.ac_getimage_addinfo1);
		pay_mode1_intime=(TextView)findViewById(R.id.pay_mode1_intime);
		feeTypeFixedMinuteOfInActivite=(EditText)findViewById(R.id.feeTypeFixedMinuteOfInActivite);
		feeTypeFixedMinuteOfInActivite.setOnFocusChangeListener(onfocll);
		feeTypeSecMinuteOfActivite=(EditText)findViewById(R.id.feeTypeSecMinuteOfActivite);
		feeTypeSecMinuteOfActivite.setOnFocusChangeListener(onfocll);
		Calendar calendar = Calendar.getInstance();
		hourOfDay1 = calendar.get(Calendar.HOUR_OF_DAY);
		minute1 = calendar.get(Calendar.MINUTE); // 获得当前的秒

		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_topbar);
		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		titleView.setText("数据采集");

		initetextview();
		pay_span_layout=(LinearLayout)findViewById(R.id.spinner_new);
		pay_span_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(AddParkInfoDetailActivity.this);
				
				
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
		paymode1 = (RadioButton) findViewById(R.id.payradioButton1);
		paymode2 = (RadioButton) findViewById(R.id.payradioButton2);

		paymode1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
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

		// 下一步按钮
		nextbtn = (Button) findViewById(R.id.add_parkinfo_nextbtn);

		nextbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent inte = new Intent();
				// inte.setClass(getActivity(),
				// AddParkInfoDetailActivity.class);

				/* validation plsu */
				if (!UIUtils.validationNotEmpty(AddParkInfoDetailActivity.this,"管理公司", compname)) {
					return;
				}
				if (!UIUtils.validationNotEmpty(AddParkInfoDetailActivity.this,"联系人", contactmen)) {
					return;
				}
				if (!UIUtils.validationNotEmpty(AddParkInfoDetailActivity.this,"联系电话", tele)) {
					return;
				}
				if (!UIUtils.validationNotEmpty(AddParkInfoDetailActivity.this,"停车场名称", parkname)) {
					return;
				}
				if (!UIUtils.validationNotEmpty(AddParkInfoDetailActivity.this,"停车场地址", parkaddress)) {
					return;
				}
				if (!UIUtils.validationNotEmpty(AddParkInfoDetailActivity.this,"停车场城市", cityname)) {
					return;
				}
				if(counpmode2.isChecked())
				{
					if(startime.getText()==null||startime.getText().toString().trim().equals(""))
					{
						 Toast.makeText(getApplicationContext(), "优惠时段开始时间不能为空",
								 Toast.LENGTH_SHORT).show();
						 return;
						
					}
					if(endtime.getText()==null||endtime.getText().toString().trim().equals(""))
					{
						 Toast.makeText(getApplicationContext(), "优惠时段结束时间不能为空",
								 Toast.LENGTH_SHORT).show();
						return;
					}
					
				}

				//如果选择了优惠时段
//				if (counpmode2.isChecked()) {
//					String _starttime = startime.getText().toString();
//					String _endtime = endtime.getText().toString();
//
//					try {
//						if (!UIUtils.validationNotEmpty(AddParkInfoDetailActivity.this,"开始时间", startime)) {
//							return;
//						}
//						if (!UIUtils.validationNotEmpty(AddParkInfoDetailActivity.this,"结束时间", endtime)) {
//							return;
//						}
//						
//						int sint = Integer.parseInt(_starttime.replace(":", ""));
//						int eint = Integer.parseInt(_endtime.replace(":", ""));
//
//						if ((eint - sint) < 0) {
//							Toast.makeText(getApplicationContext(),
//									"开始时间不能大于结束时间", Toast.LENGTH_SHORT)
//									.show();
//							return;
//						}
//
//					} catch (Exception e) {
//						Log.e("time compare", e.getMessage(), e);
//					}
//				}
				// if (getimage_parktime.equals("") && paynomal.equals("")) {
				// Toast.makeText(getApplicationContext(), "请完成所以信息填写",
				// Toast.LENGTH_SHORT).show();
				// return;
				//
				// }

				// if(paymode1.isChecked()&&(getimage_parktime.getText().toString().trim().equals("0.0")
				// ||parkovertime.getText().toString().trim().equals("0.0")
				// ||getimage_parktime.getText().toString().trim().equals("")
				// ||parkovertime.getText().toString().trim().equals("")))
				// {
				// Toast.makeText(getApplicationContext(), "请完成所以信息填写",
				// Toast.LENGTH_SHORT).show();
				// return;
				//
				// }
				// if(paymode2.isChecked()&&(paynomal.getText().toString().trim().equals("0.0")||paynomal.getText().toString().trim().equals("")))
				// {
				// Toast.makeText(getApplicationContext(), "请完成所以信息填写",
				// Toast.LENGTH_SHORT).show();
				// return;
				// }
				//
				// if(counpmode1.isChecked()&&(daypay.getText().toString().trim().equals("0.0")||daypay.getText().toString().trim().equals("")))
				// {
				//
				// Toast.makeText(getApplicationContext(), "请完成所以信息填写",
				// Toast.LENGTH_SHORT).show();
				// return;
				//
				// }
				//
				// if(counpmode2.isChecked()&&(daytimepay.getText().toString().trim().equals("0.0")||daytimepay.getText().toString().trim().equals("")))
				// {
				//
				// Toast.makeText(getApplicationContext(), "请完成所以信息填写",
				// Toast.LENGTH_SHORT).show();
				// return;
				//
				// }

				
				
				
				
				if (paymode1.isChecked()) {
					feeType = 1;
					int currenthous=1;
					if(pay_mode1_intime==null||pay_mode1_intime.getText()==null||pay_mode1_intime.getText().toString().trim().equals(""))
						currenthous=1;
					else
						currenthous=Integer.valueOf(pay_mode1_intime.getText().toString().trim());
					feeTypeSecInScopeHours=currenthous;
					if (getimage_parktime.getText().toString().trim()
							.equals(""))
						feeTypeSecInScopeHourMoney = 0;
					else
						feeTypeSecInScopeHourMoney = Double
								.valueOf(getimage_parktime.getText().toString()
										.trim());
					if (parkovertime.getText().toString().trim().equals(""))
						feeTypeSecOutScopeHourMoney = 0;
					else
						feeTypeSecOutScopeHourMoney = Double
								.valueOf(parkovertime.getText().toString()
										.trim());
					feeTypefixedHourMoney = 0.0;
					
					
					if(!feeTypeSecMinuteOfActivite.getText().toString().trim().equals(""))
					{
						feeactivite=Integer.valueOf(feeTypeSecMinuteOfActivite.getText().toString().trim());
						
					}else
					{
						feeactivite=0;
					}

					feeoutactivite=0;
				} else {
					feeType = 2;
					feeTypeSecInScopeHours = 0;
					feeTypeSecInScopeHourMoney = 0.0;
					feeTypeSecOutScopeHourMoney = 0.0;
					feeactivite=0;
					if (paynomal.getText().toString().trim().equals(""))
						feeTypefixedHourMoney = 0;
					else
						feeTypefixedHourMoney = Double.valueOf(paynomal
								.getText().toString().trim());
					
					if(!feeTypeFixedMinuteOfInActivite.getText().toString().trim().equals(""))
					{
						feeoutactivite=Integer.valueOf(feeTypeFixedMinuteOfInActivite.getText().toString().trim());
						
					}else
					{
						feeoutactivite=0;
					}
			

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
					if(daytimepay.getText().toString().trim().equals("")){
						discountSecHourMoney = 0.0d;
					}else{
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
					if (Bimp.tempTParkInfo == null)
						Bimp.tempTParkInfo = new TParkInfoEntity();
					Bimp.tempTParkInfo.parkname = parkname.getText().toString();
					Bimp.tempTParkInfo.address = parkaddress.getText()
							.toString();
					Bimp.tempTParkInfo.cityname=cityname.getText().toString();
					Bimp.tempTParkInfo.vender = compname.getText().toString();
					Bimp.tempTParkInfo.owner = contactmen.getText().toString();
					Bimp.tempTParkInfo.ownerPhone = Long.valueOf(tele.getText()
							.toString());

					Bimp.tempTParkInfo.feeType = feeType;
					Bimp.tempTParkInfo.feeTypefixedHourMoney = feeTypefixedHourMoney;
					Bimp.tempTParkInfo.feeTypeSecInScopeHourMoney = feeTypeSecInScopeHourMoney;
					Bimp.tempTParkInfo.feeTypeSecOutScopeHourMoney = feeTypeSecOutScopeHourMoney;
					Bimp.tempTParkInfo.feeTypeSecInScopeHours = feeTypeSecInScopeHours;
					Bimp.tempTParkInfo.discountHourAlldayMoney = discountHourAlldayMoney;
					Bimp.tempTParkInfo.discountSecHourMoney = discountSecHourMoney;
					Bimp.tempTParkInfo.isDiscountAllday = isDiscountAllday;
					Bimp.tempTParkInfo.isDiscountSec = isDiscountSec;
					Bimp.tempTParkInfo.feeTypeSecMinuteOfActivite=feeactivite;
					Bimp.tempTParkInfo.feeTypeFixedMinuteOfInActivite=feeoutactivite;
					
					
					Bimp.tempTParkInfo.createDate = new Date();
					if (startime.getText() != null && startime.getText() != "")
						Bimp.tempTParkInfo.discountSecStartHour = format
								.parse(startime.getText().toString());
					if (endtime.getText() != null && endtime.getText() != "")
						Bimp.tempTParkInfo.discountSecEndHour = format
								.parse(endtime.getText().toString());

				} catch (ParseException e) {
					// TODO Auto-generated catch block

					e.printStackTrace();
				}

				// try {
				// Dao<TParkInfo_ImgEntity, Integer> dao = getHelper()
				// .getParkdetail_imagDao();
				// TParkInfo_ImgEntity aaaa=new TParkInfo_ImgEntity();
				// aaaa.createDate=null;
				// aaaa.createPerson="111";
				// aaaa.detail="dad";
				// aaaa.imgUrlHeader="bbbb";
				// aaaa.imgUrlPath="";
				// aaaa.parkImgId=111l;
				// aaaa.updatePerson="";
				// aaaa.updateDate=null;
				//
				//
				// dao.createIfNotExists(aaaa);
				// dao = getHelper()
				// .getParkdetail_imagDao();
				// int ii=dao.queryForAll().size();
				// Toast.makeText(getApplicationContext(), "bbb"+ii,
				// Toast.LENGTH_LONG).show();
				// } catch (SQLException e) {
				// // TODO Auto-generated catch block
				// Toast.makeText(getApplicationContext(), e.toString(),
				// Toast.LENGTH_LONG).show();
				// e.printStackTrace();
				// }c

				// 存储数据到全局变量中，方便后面界面赋值
				// GetParkinfoBean currentparkbean = new GetParkinfoBean(11111,
				// compname.getText().toString(), parkaddress.getText()
				// .toString(), parkname.getText().toString(),
				// tele.getText().toString(), 1, 1, getimage_parktime
				// .getText().toString(), getimage_parktime
				// .getText().toString(), parkovertime.getText()
				// .toString(), paynomal.getText().toString(),
				// daypay.getText().toString(), couponpay.getText()
				// .toString(), startime.getText().toString(),
				// endtime.getText().toString());
				// Bimp.templistbean.clear();
				// Bimp.templistbean.add(currentparkbean);
				// Bimp.tempTParkInfo.address= parkaddress.getText().toString();
				// Bimp.tempTParkInfo.createDate=null;
				// Bimp.tempTParkInfo.createPerson=SessionUtils.loginUser.getUserName();
				// Bimp.tempTParkInfo.detail=null;
				// Bimp.tempTParkInfo.discountHourAlldayMoney=Double.valueOf(daypay.getText().toString());
				// Bimp.tempTParkInfo.discountSecEndHour=null;
				// Bimp.tempTParkInfo.discountSecHourMoney=Double.valueOf(daytimepay.getText().toString());
				// Bimp.tempTParkInfo.discountSecStartHour=null;
				// Bimp.tempTParkInfo.feeType=0;
				// Bimp.tempTParkInfo.feeTypefixedHourMoney=Double.valueOf(paynomal.getText().toString());
				// Bimp.tempTParkInfo.feeTypeSecInScopeHourMoney=Double.valueOf(getimage_parktime.getText().toString());
				// Bimp.tempTParkInfo.feeTypeSecInScopeHours=0;
				// Bimp.tempTParkInfo.feeTypeSecOutScopeHourMoney=Double.valueOf(parkovertime.getText().toString());
				// Bimp.tempTParkInfo.isDiscountAllday=1;
				// Bimp.tempTParkInfo.isDiscountSec=1;
				// Bimp.tempTParkInfo.owner=null;
				// Bimp.tempTParkInfo.ownerPhone=Long.valueOf(contactmen.getText().toString());
				// Bimp.tempTParkInfo.parkId=null;
				// Bimp.tempTParkInfo.parkname=parkname.getText().toString();
				// Bimp.tempTParkInfo.updateDate=null;
				// Bimp.tempTParkInfo.updatePerson=null;
				// Bimp.tempTParkInfo.vender=null;
				// Bimp.tempTParkInfo.venderBankName=null;
				// Bimp.tempTParkInfo.venderBankNumber=null;

				inte.setClass(getApplicationContext(),
						AdminGetParkInfoActivity.class);
				startActivityForResult(inte, 1);
				// startActivity(inte);
			}
		});

		paysetmode1();
		paymode1.setFocusableInTouchMode(true);
		paymode1.requestFocus();
		Binddata();
		paymode1.requestFocus();
	}

	/**
	 * 验证是否为空
	 * 
	 * @param editViewTitle
	 * @param editView
	 * @return
	 */
	

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg1 == Constants.ResultCode.ADDPARK_END) {

			Intent inte=new Intent();
			inte.setClass(getApplicationContext(), AddParkInfoHistoryActivity.class);
			startActivity(inte);
			
			
			finish();

		}

	}

	public DBHelper getHelper() {
		if (dbHelper == null) {
			dbHelper = OpenHelperManager.getHelper(
					AddParkInfoDetailActivity.this, DBHelper.class);
		}
		return dbHelper;
	}

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
					if (feeTypeFixedMinuteOfInActivite.getText().toString().trim().equals("0")) {
						feeTypeFixedMinuteOfInActivite.setText("");

					}

				} else if (feeTypeFixedMinuteOfInActivite.getText().toString().trim().equals("")) {
					feeTypeFixedMinuteOfInActivite.setText("0");

				}
				break;

			}

		}
	};

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
						AddParkInfoDetailActivity.this,
						new MyTimePickerDialog(), hourOfDay1, minute1, true);
				timePickerDialog.show();
				timemode = 0;
				break;
			case R.id.id_getimage_endtime:
				timePickerDialog = new TimePickerDialog(
						AddParkInfoDetailActivity.this,
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
			case R.id.id_getimage_parkovertime:

				break;
			}

		}
	};

	// 选择支付模式后让其它控件失去聚焦
	void paysetmode1() {

		pay_span_layout.setFocusableInTouchMode(true);
		pay_span_layout.setClickable(true);
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
		feeTypeFixedMinuteOfInActivite.setText("");
		feeTypeFixedMinuteOfInActivite.setFocusableInTouchMode(false);
		feeTypeFixedMinuteOfInActivite.clearFocus();

	}

	void setstate() {

		startime.setClickable(false);
		endtime.setClickable(false);
		daypay.setFocusableInTouchMode(false);

	}

	void paysetmode2() {

		pay_span_layout.setFocusableInTouchMode(false);
		pay_span_layout.setClickable(false);
		

		getimage_parktime.setFocusableInTouchMode(false);
		getimage_parktime.clearFocus();
		parkovertime.setFocusableInTouchMode(false);
		parkovertime.clearFocus();
		
		feeTypeSecMinuteOfActivite.setFocusableInTouchMode(false);
		feeTypeSecMinuteOfActivite.clearFocus();

		paynomal.setFocusableInTouchMode(true);
		paynomal.requestFocus();
		
		feeTypeFixedMinuteOfInActivite.setFocusableInTouchMode(true);
		

		paynomal.setText("0.0");
		feeTypeFixedMinuteOfInActivite.setText("0");
		
		
		parkovertime.setText("");
		getimage_parktime.setText("");
		paynomal.setText("");
		feeTypeSecMinuteOfActivite.setText("");
		pay_mode1_intime.setText("");
	}

	void initetextview() {
		compname = (TextView) findViewById(R.id.id_getimage_compname);
		contactmen = (TextView) findViewById(R.id.id_getimage_contactmen);
		tele = (TextView) findViewById(R.id.id_getimage_tele);
		parkname = (TextView) findViewById(R.id.id_getimage_parkname);
		parkaddress = (TextView) findViewById(R.id.id_getimage_parkaddress);
        cityname=(TextView) findViewById(R.id.id_getimage_city);
		getimage_parktime = (TextView) findViewById(R.id.id_getimage_parktime);
		paynomal = (TextView) findViewById(R.id.id_getimage_paynomal);
		daypay = (TextView) findViewById(R.id.id_getimage_daypay);
		daytimepay = (TextView) findViewById(R.id.id_getimage_daytimepay);
		startime = (TextView) findViewById(R.id.id_getimage_startime);
		endtime = (TextView) findViewById(R.id.id_getimage_endtime);
		parkovertime = (TextView) findViewById(R.id.id_getimage_parkovertime);
		
		couponpay = (TextView) findViewById(R.id.id_getimage_daypay);

		counpmode1 = (CheckBox) findViewById(R.id.DaypayradioButton);
		counpmode2 = (CheckBox) findViewById(R.id.DaytimeradioButton);
		
//		feeTypeFixedMinuteOfInActivite=(EditText)findViewById(R.id.feeTypeFixedMinuteOfInActivite);
//		feeTypeSecMinuteOfActivite=(EditText)findViewById(R.id.feeTypeSecMinuteOfActivite);
		startime.setOnClickListener(ll1);
		endtime.setOnClickListener(ll1);

		parkovertime.setOnClickListener(ll1);
		parkovertime.setOnFocusChangeListener(onfocll);
		getimage_parktime.setOnFocusChangeListener(onfocll);
		daypay.setOnFocusChangeListener(onfocll);
		daytimepay.setOnFocusChangeListener(onfocll);
		counpmode1.setOnClickListener(ll1);

		counpmode2.setOnClickListener(ll1);

		setstate();

	}

	public void backTo(View v) {
		finish();
	}

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
			}  else if(timemode==1){

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

	private void Binddata() {
		if (Bimp.tempTParkInfo != null) {

			if (Bimp.tempTParkInfo.address == null
					|| Bimp.tempTParkInfo.address.length() == 0
					|| Bimp.tempTParkInfo.address.trim().equals(""))
				parkaddress.setText(SessionUtils.address);
			else
				parkaddress.setText(Bimp.tempTParkInfo.address);
			
			if(Bimp.tempTParkInfo.cityname==null||Bimp.tempTParkInfo.cityname.trim().equals(""))
				cityname.setText(SessionUtils.city);
			else
				cityname.setText(Bimp.tempTParkInfo.cityname);
			
			parkname.setText(Bimp.tempTParkInfo.parkname);

			compname.setText(Bimp.tempTParkInfo.vender);

			contactmen.setText(Bimp.tempTParkInfo.owner);
			
			if (Bimp.tempTParkInfo.feeTypeSecInScopeHours ==0)
				pay_mode1_intime.setText("1");
			else
				pay_mode1_intime.setText(""+Bimp.tempTParkInfo.feeTypeSecInScopeHours);
				
			
			feeTypeFixedMinuteOfInActivite.setText(""+Bimp.tempTParkInfo.feeTypeFixedMinuteOfInActivite);
			feeTypeSecMinuteOfActivite.setText(""+Bimp.tempTParkInfo.feeTypeSecMinuteOfActivite);
			
			

			if (Bimp.tempTParkInfo.ownerPhone == 0)
				tele.setText("");
			else
				tele.setText("" + Bimp.tempTParkInfo.ownerPhone);
			getimage_parktime.setText(""
					+ Bimp.tempTParkInfo.feeTypeSecInScopeHourMoney);

			parkovertime.setText(""
					+ Bimp.tempTParkInfo.feeTypeSecOutScopeHourMoney);

			daypay.setText("" + Bimp.tempTParkInfo.discountHourAlldayMoney);

			daytimepay.setText("" + Bimp.tempTParkInfo.discountSecHourMoney);
			

			

			if (Bimp.tempTParkInfo.isDiscountAllday == 1) {
				counpmode1.setChecked(true);
				daypay.setFocusableInTouchMode(true);

			} else {
				counpmode1.setChecked(false);
				daypay.setFocusableInTouchMode(false);
			}

			if (Bimp.tempTParkInfo.isDiscountSec == 1) {
				counpmode2.setChecked(true);
				daytimepay.setFocusableInTouchMode(true);
			} else {
				counpmode2.setChecked(false);
				daytimepay.setFocusableInTouchMode(false);
			}
			if (Bimp.tempTParkInfo.discountSecStartHour != null)
				startime.setText(format
						.format(Bimp.tempTParkInfo.discountSecStartHour));

			if (Bimp.tempTParkInfo.discountSecEndHour != null)
				endtime.setText(format
						.format(Bimp.tempTParkInfo.discountSecEndHour));

			if (Bimp.tempTParkInfo.feeType == 1) {
				paymode1.setChecked(true);
				paymode2.setChecked(false);
				// paynomal.setFocusableInTouchMode(false);
				// spinner.setFocusableInTouchMode(true);
				// getimage_parktime.setFocusableInTouchMode(true);
				// parkovertime.setFocusableInTouchMode(true);

			} else {
				if (Bimp.tempTParkInfo.feeType == 2)
					paymode2.setChecked(true);
				paymode1.setChecked(false);

				paynomal.setFocusableInTouchMode(true);
				paymode2.requestFocus();
				// spinner.setFocusableInTouchMode(false);
				// getimage_parktime.setFocusableInTouchMode(false);
				// parkovertime.setFocusableInTouchMode(false);

				paynomal.setText("" + Bimp.tempTParkInfo.feeTypefixedHourMoney);
			}

		}else
		{
			
			
			
		}

	}

}
