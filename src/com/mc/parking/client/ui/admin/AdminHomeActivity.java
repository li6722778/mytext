package com.mc.parking.client.ui.admin;

import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.mc.parking.client.Constants;
import com.mc.parking.client.PackingApplication;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.OrderEntity;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkInfo_Py;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.BaseViewPagerIndicator;
import com.mc.parking.client.ui.MainActivity;
import com.mc.parking.client.utils.DateHelper;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;

public class AdminHomeActivity extends FragmentActivity {

	OrderFragment orderFrag;
	private BaseViewPagerIndicator mIndicator = null;
	private ViewPager viewPager;
	private String[] columns = new String[] { "未完成订单", "已完成订单" };
	public android.support.v4.app.Fragment tabFragments[];

	// 是否点击tab2
	private boolean hasCheckedView2;

	public void backTo(View v) {
		onBackPressed();
	}

	/**
	 * 设置TAB上的总数
	 * 
	 * @param numb
	 */
	public void setDisplayTotal(int numb) {
		if (mIndicator != null) {
			mIndicator.setnumb(numb);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_admin);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.actionbar_topbar);
		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		titleView.setText(R.string.admin_title);
		initview();

		// 保存当前实例
		PackingApplication.getInstance().setCurrentActivity(AdminHomeActivity.this);

	}

	/**
	 * 重新刷新订单页面
	 */
	public void reloadOrderList() {
		if (tabFragments != null) {
			for (Fragment orderFragment : tabFragments) {
				if (orderFragment instanceof OrderFragment) {
					((OrderFragment) orderFragment).reload();
					break;
				}
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		// 判断来自哪里
		String fromString = intent.getStringExtra("from");
		if (fromString != null && fromString.trim().equals("notice")) {// 来自通知消息
			reloadOrderList();
		}

		// 保存当前实例
		PackingApplication.getInstance().setCurrentActivity(AdminHomeActivity.this);
	}

	@Override
	protected void onResume() {

		// 如果是空，就再次读取
		if (SessionUtils.loginUser == null) {
			TuserInfo userInfo = (TuserInfo) SessionUtils.readUserinfo(AdminHomeActivity.this, "userinfo");

			if (userInfo != null) {
				SharedPreferences sharedata = getSharedPreferences("user", MODE_PRIVATE);
				Long date = sharedata.getLong("time", 0);
				Date recorddate = new Date(date);
				Date nowDate = new Date();
				int resultday = DateHelper.diffDate(nowDate, recorddate);
				if (resultday <= Constants.loginperiod) {
					SessionUtils.loginUser = userInfo;
					if (SessionUtils.loginUser.userType >= Constants.USER_TYPE_PADMIN
							&& SessionUtils.loginUser.userType < Constants.USER_TYPE_PADMIN + 10) {
						TParkInfoEntity tParkInfoEntity = (TParkInfoEntity) SessionUtils
								.readchoiceparkInfoAdm(AdminHomeActivity.this, "parkinfo");
						SessionUtils.loginUser.parkInfoAdm = tParkInfoEntity;
					}
				}

			}
		}

		// 保存当前实例
		PackingApplication.getInstance().setCurrentActivity(this);

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// PackingApplication.getInstance().setCurrentActivity(null);
		super.onDestroy();
	}

	private void initview() {
		tabFragments = new android.support.v4.app.Fragment[] { new OrderFragment(), new OrderHisFragment() };
		viewPager = (ViewPager) findViewById(R.id.viewpagerss);
		mIndicator = (BaseViewPagerIndicator) findViewById(R.id.id_stickynavlayout_indicator);
		mIndicator.setMyMode(1, 0);
		mIndicator.setTitles(columns);
		mIndicator.setViewPage(viewPager);
		// 创建一个FragmentPagerAdapter对象，该对象负责为ViewPager提供多个Fragment
		FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			// 获取第position位置的Fragment
			@Override
			public android.support.v4.app.Fragment getItem(int position) {
				//
				// Bundle args = new Bundle();
				// args.putInt(OfflineMapFragment.ARG_SECTION_NUMBER, position +
				// 1);
				// fragment.setArguments(args);
				return tabFragments[position];
			}

			@Override
			public int getCount() {
				return columns.length;
			}

		};

		// 为ViewPager组件设置FragmentPagerAdapter

		viewPager.setAdapter(pagerAdapter); // ①
		// 为ViewPager组件绑定事件监听器
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			// 当ViewPager显示的Fragment发生改变时激发该方法
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				mIndicator.scroll(position, positionOffset);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == 1 && !hasCheckedView2) {// 滑动过去再查下view2的东西
					if (tabFragments != null) {
						for (Fragment orderFragment : tabFragments) {
							if (orderFragment instanceof OrderHisFragment) {
								((OrderHisFragment) orderFragment).reload();
								hasCheckedView2 = true;
								break;
							}
						}
					}

				}

			}
		});
		viewPager.setCurrentItem(0);

		int selectnum = getIntent().getIntExtra("selectNum", 0);
		if (selectnum == 1) {
			hasCheckedView2 = true;
			viewPager.setCurrentItem(selectnum);
			UIUtils.okorder = true;
		}

	}

	/*
	 * private void InitView() { // TODO Auto-generated method stub mTabHost =
	 * (FragmentTabHost) findViewById(android.R.id.tabhost);
	 * mTabHost.setup(this, getSupportFragmentManager(),
	 * android.R.id.tabcontent);
	 * mTabHost.getTabWidget().setVisibility(View.GONE); //隐藏系统的TabWidget
	 * 
	 * // mTabHost.addTab(mTabHost.newTabSpec("chewei").setIndicator("Home"), //
	 * CheweiFragment.class, null);
	 * mTabHost.addTab(mTabHost.newTabSpec("chewei").setIndicator("Home"),
	 * CheweiFragment.class, null);
	 * 
	 * //
	 * mTabHost.addTab(mTabHost.newTabSpec("chooser").setIndicator("Message"),
	 * // CaptureFragment.class, null); //
	 * mTabHost.addTab(mTabHost.newTabSpec("order").setIndicator("Profile"),
	 * OrderFragment.class, null);
	 * 
	 * // mTabHost.addTab(mTabHost.newTabSpec("user").setIndicator("userinfo"),
	 * // UserInfoFragment.class, null);
	 * 
	 * 
	 * // mTabHost.setOnTabChangedListener(this);
	 * mTabHost.setCurrentTabByTag("chewei"); ((RadioButton)
	 * findViewById(R.id.radio_tab1)).setChecked(true);
	 * 
	 * radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
	 * radioGroup.setOnCheckedChangeListener(this); }
	 */

	// public void onCheckedChanged(RadioGroup group, int checkedId) {
	// FragmentManager fm = getSupportFragmentManager();
	// CheweiFragment cheweiFrag = (CheweiFragment)
	// fm.findFragmentByTag("chewei");
	// //CaptureFragment chooserFrag = (CaptureFragment)
	// fm.findFragmentByTag("chooser");
	// orderFrag = (OrderFragment) fm.findFragmentByTag("order");
	// UserInfoFragment userFrag = (UserInfoFragment)
	// fm.findFragmentByTag("user");
	// FragmentTransaction ft = fm.beginTransaction();
	//
	// //** Detaches the androidfragment if exists */
	// if (cheweiFrag != null)
	// ft.detach(cheweiFrag);
	// // if (chooserFrag != null)
	// // ft.detach(chooserFrag);
	// if (orderFrag != null)
	// ft.detach(orderFrag);
	// if (userFrag != null)
	// ft.detach(userFrag);
	//
	// switch (checkedId) {
	// case R.id.radio_tab1:
	// if (cheweiFrag == null) {
	// ft.add(android.R.id.tabcontent, new CheweiFragment(), "chewei");
	// } else {
	// ft.attach(cheweiFrag);
	// }
	// mTabHost.setCurrentTabByTag("chewei");
	// break;
	// // case R.id.radio_tab2:
	// // if (chooserFrag == null) {
	// // ft.add(android.R.id.tabcontent, new CaptureFragment(), "chooser");
	// // } else {
	// // ft.attach(chooserFrag);
	// // }
	// // mTabHost.setCurrentTabByTag("chooser");
	// // break;
	// case R.id.radio_tab2:
	// if (orderFrag == null) {
	// ft.add(android.R.id.tabcontent, new OrderFragment(), "order");
	// } else {
	// ft.attach(orderFrag);
	// }
	// mTabHost.setCurrentTabByTag("order");
	// break;
	// // case R.id.radio_tab2:
	// // if (userFrag == null) {
	// // ft.add(android.R.id.tabcontent, new OrderFragment(), "user");
	// // } else {
	// // ft.attach(userFrag);
	// // }
	// // mTabHost.setCurrentTabByTag("user");
	// // break;
	// }
	// }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	public void refreshfragment() {
		FragmentManager fm = getSupportFragmentManager();
		CheweiFragment cheweiFrag = (CheweiFragment) fm.findFragmentByTag("chewei");
		// CaptureFragment chooserFrag = (CaptureFragment)
		// fm.findFragmentByTag("chooser");
		orderFrag = (OrderFragment) fm.findFragmentByTag("order");
		// 刷新
		// if(orderFrag==null)
		// Toast.makeText(getApplicationContext(), "aaa",
		// Toast.LENGTH_LONG).show();
		// else
		// orderFrag.getremotedata(0);

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminHomeActivity.this);
		alertDialog.setTitle("订单完成");
		alertDialog.setMessage("订单已经完成,请通知车主出场");
		alertDialog.setNegativeButton("关闭", null);
		alertDialog.create().show();

	}

	/**
	 * 得到支付方式和价格
	 * 
	 * @param orderinfo
	 * @return
	 */
	public String getpaymoney(OrderEntity orderinfo) {
		List<TParkInfo_Py> pys = orderinfo.getPay();
		String orderId = "";
		if (pys != null) {
			double pay = 0.0d;
			double payPending = 0.0d;

			String payment = "";
			for (TParkInfo_Py py : pys) {
				if (py.getPayMethod() != Constants.PAYMENT_SERVICE) {
					if (py.getAckStatus() == Constants.PAYMENT_STATUS_FINISH) {
						pay += (py.getPayActu() + py.getCouponUsed());
					} else if (py.getAckStatus() == Constants.PAYMENT_STATUS_PENDING) {
						payPending += py.getPayActu();
					}

					if (py.getPayMethod() == 1) {
						payment += "[支付宝]";
					}
					if (py.getPayMethod() == 2) {
						payment += "[微信支付]";
					}
					if (py.getPayMethod() == 3) {
						payment += "[银联支付]";
					}
					if (py.getPayMethod() == 4) {
						payment += "[停车券]";
					}
					if (py.getPayMethod() == 8) {
						payment += "[优惠活动]";
					}
					if (py.getPayMethod() == 5) {
						payment += "[支付宝+停车券]";
					}
					if (py.getPayMethod() == 13) {
						payment += "[现金+停车券]";
					}
					if (py.getPayMethod() == 9) {
						payment += "[现金]";
					}
					if (py.getPayMethod() == Constants.PAYMENT_LIJIAN) {
						payment += "[立减]";
					}
				}
			}
			if (pay > 0) {
				orderId += "[已付款" + UIUtils.decimalPrice(pay) + "元],支付方式:" + payment;
			}
			if (payPending > 0) {
				orderId += "[正在付款" + UIUtils.decimalPrice(pay) + "元]" + payment;
			}
		}
		return orderId;

	}

}
