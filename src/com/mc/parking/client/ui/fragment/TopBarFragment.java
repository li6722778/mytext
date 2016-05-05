package com.mc.parking.client.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.client.Constants;
import com.mc.park.client.R;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.BaseDialogFragment;
import com.mc.parking.client.ui.CouponDetailActivity;
import com.mc.parking.client.ui.LoginActivity;
import com.mc.parking.client.ui.MainActivity;
import com.mc.parking.client.ui.OffineMapActivity;
import com.mc.parking.client.ui.OrderActivity;
import com.mc.parking.client.ui.OrderDetailActivity;
import com.mc.parking.client.ui.TakecashActivity;
import com.mc.parking.client.ui.UserInfoActivity;
import com.mc.parking.client.ui.UseraccountAcitivity;
import com.mc.parking.client.ui.admin.AddParkInfoDetailActivity;
import com.mc.parking.client.ui.admin.AddParkInfoHistoryActivity;
import com.mc.parking.client.ui.admin.AdminHomeActivity;
import com.mc.parking.client.ui.admin.AdminServiceActivity;
import com.mc.parking.client.ui.admin.ChangeParkPriceActivity;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.WXshareutil;

public class TopBarFragment extends Fragment implements OnClickListener {

	public SearchListFragment listFragment;
	public SlidingMenu slidingMenu;
	private TextView menu_nologin;
	private TextView menu_logined_username;
	private TextView menu_logined_phone;
	private LinearLayout menu_logined;
	private Activity currentActivity;
	private RelativeLayout adminLayout;
	private LinearLayout personalInfoLayout;
	private RelativeLayout packinghistoryLayout;
	private RelativeLayout packingadminLayout;
	private RelativeLayout couponLayout;
	private RelativeLayout offineMapLayout;
	private RelativeLayout shareLayout;
	private RelativeLayout exitappLayout,serviceLayout;
	
	private ImageView admin_flagImg;
	private RelativeLayout getparkinfoLayout, adminPriceLayout, getcashLayout,
			historyLayout, personlLayout;
	ImageButton maplistbutton;
	Activity activity;
	private PopupWindow window;
	private com.mc.parking.receiver.ShareBroadcastReceiver ShareBroadcastReceiver;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.fragment_topbar, container,
				false);
		activity = getActivity();
		callback();
		maplistbutton = (ImageButton) view.findViewById(R.id.maplistbutton);
		if (SessionUtils.loginUser == null
				|| !(SessionUtils.loginUser.userType >= 20)) {

			maplistbutton.setVisibility(View.VISIBLE);
		} else
			maplistbutton.setVisibility(View.GONE);
		// 地图列表按钮
		maplistbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (activity != null && activity instanceof MainActivity) {
					MainActivity mainactivity = (MainActivity) activity;
					mainactivity.openmaplist();
				}

			}
		});

		ImageButton configButton = (ImageButton) view
				.findViewById(R.id.systemconfig);

		currentActivity = getActivity();

		configButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (slidingMenu == null) {
					initSlidingMenu();
				}
				// PackingApplication.getInstance().getMKOfflineMap()
				packinghistoryLayout.setVisibility(View.VISIBLE);
				couponLayout.setVisibility(View.VISIBLE);
				historyLayout.setVisibility(View.GONE);
				offineMapLayout.setVisibility(View.VISIBLE);

				if (SessionUtils.isLogined()) {
					menu_nologin.setVisibility(View.GONE);
					menu_logined.setVisibility(View.VISIBLE);

					TuserInfo userInfo = SessionUtils.loginUser;
					if (userInfo != null) {
						personlLayout.setVisibility(View.VISIBLE);
						if ((userInfo.userType >= Constants.USER_TYPE_PADMIN && userInfo.userType < Constants.USER_TYPE_PADMIN + 10)
								|| userInfo.userType == Constants.USER_TYPE_SADMIN) {
							// 如果没有车位，不让用户进入
							if ((userInfo.userType >= Constants.USER_TYPE_PADMIN && userInfo.userType < Constants.USER_TYPE_PADMIN + 10)) {
								offineMapLayout.setVisibility(View.GONE);

							} else {
								offineMapLayout.setVisibility(View.VISIBLE);
							}
							adminLayout.setVisibility(View.VISIBLE);
							menu_logined_username.setText(""
									+ userInfo.userName);
							menu_logined_phone.setText("" + userInfo.userPhone);
							if (userInfo.userType == Constants.USER_TYPE_SADMIN)
								admin_flagImg
										.setImageDrawable(getResources()
												.getDrawable(
														R.drawable.superadminbadge));
							else
								admin_flagImg.setImageDrawable(getResources()
										.getDrawable(R.drawable.adminbadge));
							admin_flagImg.setVisibility(View.VISIBLE);
							getparkinfoLayout.setVisibility(View.GONE);
							// remove unwanted menu
							packinghistoryLayout.setVisibility(View.GONE);
							historyLayout.setVisibility(View.GONE);
							// walletLayout.setVisibility(View.GONE);
							couponLayout.setVisibility(View.GONE);
							shareLayout.setVisibility(View.GONE);
							getparkinfoLayout.setVisibility(View.GONE);
							adminPriceLayout.setVisibility(View.GONE);
							serviceLayout.setVisibility(View.GONE);
							if (userInfo.userType == Constants.USER_TYPE_PADMIN) {
								getcashLayout.setVisibility(View.GONE);
								adminPriceLayout.setVisibility(View.GONE);
								serviceLayout.setVisibility(View.GONE);
							} else {
								getcashLayout.setVisibility(View.VISIBLE);
								adminPriceLayout.setVisibility(View.VISIBLE);
								serviceLayout.setVisibility(View.VISIBLE);
							}

						
						} else if ((userInfo.userType >= Constants.USER_TYPE_MADMIN && userInfo.userType < Constants.USER_TYPE_MADMIN + 10)
								|| userInfo.userType == Constants.USER_TYPE_SADMIN) {

							adminLayout.setVisibility(View.GONE);
							menu_logined_username.setText(""
									+ userInfo.userName);
							menu_logined_phone.setText("" + userInfo.userPhone);
							if (userInfo.userType == Constants.USER_TYPE_SADMIN)
								admin_flagImg
										.setImageDrawable(getResources()
												.getDrawable(
														R.drawable.superadminbadge));
							else
								admin_flagImg.setImageDrawable(getResources()
										.getDrawable(R.drawable.datacollector));
							admin_flagImg.setVisibility(View.VISIBLE);
							packingadminLayout.setVisibility(View.GONE);
							packinghistoryLayout.setVisibility(View.GONE);
							// admin_flagImg.setBackgroundDrawable(getResources().getDrawable(R.drawable.datacollector));
							// remove unwanted menu
							// packinghistoryLayout.setVisibility(View.VISIBLE);
							// walletLayout.setVisibility(View.GONE);
							couponLayout.setVisibility(View.GONE);
							shareLayout.setVisibility(View.GONE);
							getparkinfoLayout.setVisibility(View.VISIBLE);
							// getparkinfoLayout.setVisibility(View.VISIBLE);
							adminPriceLayout.setVisibility(View.GONE);
							serviceLayout.setVisibility(View.GONE);
							getcashLayout.setVisibility(View.GONE);
							historyLayout.setVisibility(View.VISIBLE);
							

						}
						// 普通用户
						else {
							adminLayout.setVisibility(View.GONE);
							menu_logined_username.setText(""
									+ userInfo.userName);
							menu_logined_phone.setText("" + userInfo.userPhone);
							admin_flagImg.setVisibility(View.GONE);
							getparkinfoLayout.setVisibility(View.GONE);
							adminPriceLayout.setVisibility(View.GONE);
							serviceLayout.setVisibility(View.GONE);
							getcashLayout.setVisibility(View.GONE);
							couponLayout.setVisibility(View.VISIBLE);
							shareLayout.setVisibility(View.VISIBLE);
							packinghistoryLayout.setVisibility(View.VISIBLE);

						}
					} else {
						adminLayout.setVisibility(View.GONE);
						menu_logined_username.setText("");
						menu_logined_phone.setText("");
						admin_flagImg.setVisibility(View.GONE);
						getparkinfoLayout.setVisibility(View.GONE);
						adminPriceLayout.setVisibility(View.GONE);
						serviceLayout.setVisibility(View.GONE);
						getcashLayout.setVisibility(View.GONE);
						couponLayout.setVisibility(View.GONE);
						shareLayout.setVisibility(View.GONE);
						packinghistoryLayout.setVisibility(View.GONE);
						
					}
				} else {
					menu_nologin.setVisibility(View.VISIBLE);
					menu_logined.setVisibility(View.GONE);
					adminLayout.setVisibility(View.GONE);
					admin_flagImg.setVisibility(View.GONE);
					menu_logined_username.setText("");
					menu_logined_phone.setText("");
					getparkinfoLayout.setVisibility(View.GONE);
					adminPriceLayout.setVisibility(View.GONE);
					serviceLayout.setVisibility(View.GONE);
					getcashLayout.setVisibility(View.GONE);
					couponLayout.setVisibility(View.GONE);
					shareLayout.setVisibility(View.GONE);
					packinghistoryLayout.setVisibility(View.GONE);
					personlLayout.setVisibility(View.GONE);
				}

				slidingMenu.toggle();
			}
		});

		return view;
	}

	public void initSlidingMenu() {
		if (slidingMenu == null) {
			slidingMenu = new SlidingMenu(getActivity());
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			slidingMenu.setShadowDrawable(R.drawable.shadow_right); // 阴影
			slidingMenu.setShadowWidth(15); // 阴影宽度
			slidingMenu.setBehindOffset(100); // 前面的视图剩下多少
			slidingMenu.setMode(SlidingMenu.LEFT); // 左滑出不是右滑出
			slidingMenu.setMenu(R.layout.fragment_menu); // 设置menu容器
			slidingMenu.attachToActivity(getActivity(),SlidingMenu.SLIDING_CONTENT);
			personalInfoLayout = (LinearLayout) getActivity().findViewById(
					R.id.personalInfo);
			serviceLayout=(RelativeLayout) getActivity().findViewById(
					R.id.service);
			packinghistoryLayout = (RelativeLayout) getActivity().findViewById(
					R.id.packinghistory);
			packingadminLayout = (RelativeLayout) getActivity().findViewById(
					R.id.packingadmin);

			couponLayout = (RelativeLayout) getActivity().findViewById(
					R.id.coupon);
			offineMapLayout = (RelativeLayout) getActivity().findViewById(
					R.id.offineMap);
			exitappLayout = (RelativeLayout) getActivity().findViewById(
					R.id.exitapp);
			getparkinfoLayout = (RelativeLayout) getActivity().findViewById(
					R.id.getparkinfo);
			getcashLayout = (RelativeLayout) getActivity().findViewById(
					R.id.getcash);
			adminPriceLayout = (RelativeLayout) getActivity().findViewById(
					R.id.admin_change_price);
			historyLayout = (RelativeLayout) getActivity().findViewById(
					R.id.AddParkHistory);
			personlLayout = (RelativeLayout) getActivity().findViewById(
					R.id.persondetail);
			shareLayout =  (RelativeLayout) getActivity().findViewById(R.id.share);
			
			
            shareLayout.setOnClickListener(this);
			getcashLayout.setOnClickListener(this);
			adminPriceLayout.setOnClickListener(this);
			getparkinfoLayout.setOnClickListener(this);
			personalInfoLayout.setOnClickListener(this);
			serviceLayout.setOnClickListener(this);
			packinghistoryLayout.setOnClickListener(this);
			packingadminLayout.setOnClickListener(this);
			couponLayout.setOnClickListener(this);
			offineMapLayout.setOnClickListener(this);
			exitappLayout.setOnClickListener(this);
			historyLayout.setOnClickListener(this);
			personlLayout.setOnClickListener(this);
			menu_nologin = (TextView) getActivity().findViewById(
					R.id.menu_nologin);
			menu_logined = (LinearLayout) getActivity().findViewById(
					R.id.menu_logined);
			menu_logined_username = (TextView) getActivity().findViewById(
					R.id.menu_logined_username);
			menu_logined_phone = (TextView) getActivity().findViewById(
					R.id.menu_logined_phone);
			adminLayout = (RelativeLayout) getActivity().findViewById(
					R.id.packingadmin);
			admin_flagImg = (ImageView) getActivity().findViewById(
					R.id.admin_flagImg);

		}
	}

	@Override
	public void onDestroy() {

		if (slidingMenu != null) {
			slidingMenu.removeAllViews();
			slidingMenu = null;
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.packingadmin:
			if (SessionUtils.loginUser.parkInfoAdm == null) {
				Toast.makeText(getActivity(), "当前无停车场资源", Toast.LENGTH_LONG)
						.show();
				return;
			}
			Intent intentAdmin = new Intent(getActivity(),
					AdminHomeActivity.class);
			startActivity(intentAdmin);
			slidingMenu.toggle(false);
			break;
		case R.id.personalInfo:
			if (SessionUtils.isLogined()) {
				break;
			} else {
				Intent intentLogin = new Intent(getActivity(),
						LoginActivity.class);
				intentLogin.putExtra("parent", LoginActivity.parent_userinfo);
				startActivity(intentLogin);
			}
			slidingMenu.toggle(false);
			break;
		case R.id.packinghistory:
			if (SessionUtils.isLogined()) {
				Intent packinghis = new Intent(getActivity(),
						OrderActivity.class);
				startActivityForResult(packinghis, 0);
			} else {
				Intent intentLogin = new Intent(getActivity(),
						LoginActivity.class);
				intentLogin.putExtra("parent", LoginActivity.parent_oderinfo);
				startActivity(intentLogin);
			}
			slidingMenu.toggle(false);
			break;

		case R.id.offineMap:
			Intent intent = new Intent(getActivity(), OffineMapActivity.class);
			startActivity(intent);
			slidingMenu.toggle(false);
			break;
		case R.id.exitapp:
			BaseDialogFragment confirmDialog = new BaseDialogFragment();
			confirmDialog.setMessage("确认退出系统吗?");
			confirmDialog.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							if (currentActivity instanceof MainActivity) {
								((MainActivity) currentActivity).writeCache();
							}

							Intent intent = new Intent(Intent.ACTION_MAIN);
							intent.addCategory(Intent.CATEGORY_HOME);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							android.os.Process.killProcess(android.os.Process
									.myPid());
						}
					});
			confirmDialog.setNegativeButton("取消", null);
			confirmDialog.show(getFragmentManager(), "");
			break;

		case R.id.coupon:
			if (SessionUtils.isLogined()) {
				Intent intentcoupon = new Intent(getActivity(),
						CouponDetailActivity.class);
				startActivity(intentcoupon);
			} else {
				Intent intentLogin = new Intent(getActivity(),
						LoginActivity.class);
				intentLogin.putExtra("parent", LoginActivity.parent_couponinfo);
				startActivity(intentLogin);
			}
			slidingMenu.toggle(false);
			break;

		case R.id.getparkinfo:

			if (SessionUtils.isLogined()) {
				// Intent intentcoupon = new Intent(getActivity(),
				// AdminGetParkInfoActivity.class);
				Bimp.tempTParkInfo = new TParkInfoEntity();
				Bimp.tempTParkImageList.clear();
				Bimp.tempTParkLocList.clear();
				Bimp.ADDPARK_VIEW_MODE = AddParkInfoHistoryActivity.FROM_TYPE_NEW;
				Intent intentcoupon = new Intent(getActivity(),
						AddParkInfoDetailActivity.class);
				startActivity(intentcoupon);
			} else {
				Intent intentLogin = new Intent(getActivity(),
						LoginActivity.class);
				intentLogin.putExtra("parent", LoginActivity.parent_couponinfo);
				startActivity(intentLogin);
			}
			slidingMenu.toggle(false);
			break;

		case R.id.getcash:
			if (SessionUtils.isLogined()) {
				//
				Intent intentcoupon = new Intent(getActivity(),
						TakecashActivity.class);
				startActivity(intentcoupon);
			} else {
				Intent intentLogin = new Intent(getActivity(),
						LoginActivity.class);
				intentLogin.putExtra("parent", LoginActivity.parent_couponinfo);
				startActivity(intentLogin);
			}
			slidingMenu.toggle(false);
			break;

		case R.id.AddParkHistory:
			if (SessionUtils.isLogined()) {
				// Intent intentcoupon = new Intent(getActivity(),
				// AdminGetParkInfoActivity.class);
				Intent intentcoupon = new Intent(getActivity(),
						AddParkInfoHistoryActivity.class);
				startActivity(intentcoupon);
			} else {
				Intent intentLogin = new Intent(getActivity(),
						LoginActivity.class);
				intentLogin.putExtra("parent", LoginActivity.parent_couponinfo);
				startActivity(intentLogin);
			}
			slidingMenu.toggle(false);
			break;

		case R.id.admin_change_price:

			if (SessionUtils.isLogined()) {
				Intent intentUser = new Intent(getActivity(),
						ChangeParkPriceActivity.class);
				startActivity(intentUser);
			} else {
				Intent intentLogin = new Intent(getActivity(),
						LoginActivity.class);
				intentLogin.putExtra("parent", LoginActivity.parent_userinfo);
				startActivity(intentLogin);
			}
			slidingMenu.toggle(false);
			break;

		case R.id.persondetail:
			if (SessionUtils.isLogined()) {
				Intent intentUser = new Intent(getActivity(),
						UserInfoActivity.class);
				startActivity(intentUser);
			} else {
				Intent intentLogin = new Intent(getActivity(),
						LoginActivity.class);
				intentLogin.putExtra("parent", LoginActivity.parent_userinfo);
				startActivity(intentLogin);
			}
			slidingMenu.toggle(false);
			break;
			
			
		case R.id.share:
			showPopwindow();
			break;
			
		case R.id.service:
			if (SessionUtils.isLogined()) {
				Intent intentUser = new Intent(getActivity(),
						AdminServiceActivity.class);
				startActivity(intentUser);
			} else {
				Intent intentLogin = new Intent(getActivity(),
						LoginActivity.class);
				intentLogin.putExtra("parent", LoginActivity.parent_userinfo);
				startActivity(intentLogin);
			}
			slidingMenu.toggle(false);
			break;
			
		default:
			break;
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (currentActivity instanceof MainActivity) {
			MainActivity main = ((MainActivity) currentActivity);
			if (main.mapFragment != null) {
				main.mapFragment
						.onActivityResult(requestCode, resultCode, data);
			}
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (SessionUtils.loginUser == null
				|| !(SessionUtils.loginUser.userType >= 20)) {

			maplistbutton.setVisibility(View.VISIBLE);
		} else
			maplistbutton.setVisibility(View.GONE);
	}

	
	
	private void showPopwindow() {

		if (window == null) {
			intitpopwindow();
			return;
		}

		// 在底部显示
		window.showAtLocation(
				activity.findViewById(R.id.start),
				Gravity.BOTTOM, 0, 0);
		backgroundAlpha(0.5f);

	}
	
	
	private void intitpopwindow() {

		// 利用layoutInflater获得View
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		window.showAtLocation(
				activity.findViewById(R.id.start),
				Gravity.BOTTOM, 0, 0);

		backgroundAlpha(0.5f);

		LinearLayout shareLayout = (LinearLayout) view.findViewById(R.id.share);
		LinearLayout sharetocLayout = (LinearLayout) view
				.findViewById(R.id.sharetoc);
		shareLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (activity instanceof MainActivity) {
					MainActivity currentactivity = (MainActivity) activity;
					currentactivity.api.sendReq(WXshareutil.sharetofriend(Constants.MENUSHAREPAGE,"你停车，我买单","你停车 我买单！首次下载APP即送5元停车券"));
					
				}
				window.dismiss();
			}
		});

		sharetocLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (activity instanceof MainActivity) {
				MainActivity currentactivity = (MainActivity) activity;
				currentactivity.api.sendReq(WXshareutil.sharetofriendcircle(Constants.MENUSHAREPAGE,"你停车，我买单","你停车 我买单！首次下载APP即送5元停车券"));
				}
				window.dismiss();
			}
		});

		LinearLayout canceLayout = (LinearLayout) view
				.findViewById(R.id.cancel);

		canceLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				window.dismiss();
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
	
	// 设置背景透明度
	public void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
		activity.getWindow().setAttributes(lp);
	}
	
	private void callback() {
		// TODO Auto-generated method stub
		ShareBroadcastReceiver = new com.mc.parking.receiver.ShareBroadcastReceiver();
		ShareBroadcastReceiver
				.setOnReceivedMessageListener(new com.mc.parking.receiver.ShareBroadcastReceiver.MessageListener() {

					@Override
					public void OnReceived(String message) {
						if (message == "ok" || message.equals("ok")) {
                        Toast.makeText(activity, "分享成功", Toast.LENGTH_SHORT).show();
						}

					}
				});
	}
}
