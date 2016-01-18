package com.mc.parking.client.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.mc.parking.client.Constants;
import com.mc.parking.client.PackingApplication;
import com.mc.parking.client.R;
import com.mc.parking.client.layout.ActionBaseActivity;
import com.mc.parking.client.layout.BaseViewPagerIndicator;
import com.mc.parking.client.ui.admin.AddParkInfoHistoryActivity.MyOnPageChangeListener;
import com.mc.parking.client.ui.admin.AddParkInfoHistoryActivity.MyViewPagerAdapter;
import com.mc.parking.client.ui.fragment.OffineMapMyCityFragment;
import com.mc.parking.client.ui.fragment.OfflineMapFragment;
import com.mc.parking.client.ui.fragment.ParkingCommentsFragment;
import com.mc.parking.client.ui.fragment.ParkingInfoFragment;
import com.mc.parking.client.utils.SessionUtils;

public class OffineMapActivity extends ActionBaseActivity {
	private ViewPager viewPager;
	android.support.v4.app.Fragment mycityfragment = null;
	android.support.v4.app.Fragment hotcityfragment = null;

	private String[] columns = new String[] { "我的城市", "热门城市" };
	public android.support.v4.app.Fragment tabFragments[];
	/**
	 * 离线地图功能
	 */
	private MKOfflineMap mOfflineMap;

	public void backTo(View v) {
		finish();
	}

	public void setMyCityDownloadMessage(String message, int ratio) {
		if (mycityfragment != null) {
			((OffineMapMyCityFragment) mycityfragment).setDownloadMessage(
					message, ratio);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_offinemap);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_topbar);
		
		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		titleView.setText(R.string.rightmenu_offinemap_title);

		mOfflineMap = ((PackingApplication) getApplication()).getMKOfflineMap();
		mycityfragment = new OffineMapMyCityFragment(mOfflineMap);
		hotcityfragment = new OfflineMapFragment(mOfflineMap);

		tabFragments = new android.support.v4.app.Fragment[] { mycityfragment,
				hotcityfragment };
		viewPager = (ViewPager) findViewById(R.id.viewpagers);
		final BaseViewPagerIndicator mIndicator = (BaseViewPagerIndicator) findViewById(R.id.id_stickynavlayout_indicator);
		mIndicator.setTitles(columns);
		mIndicator.setViewPage(viewPager);
		// 创建一个FragmentPagerAdapter对象，该对象负责为ViewPager提供多个Fragment
		FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(
				getSupportFragmentManager()) {
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
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				mIndicator.scroll(position, positionOffset);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		viewPager.setCurrentItem(0);
		
	
	}
	

	

	@Override
	public void onDestroy() {
		if (mOfflineMap != null) {
			mOfflineMap.destroy();
		}
		super.onDestroy();
	}

	@Override
	public void onPause() {
		int cityid = SessionUtils.cityCode;
		MKOLUpdateElement temp = mOfflineMap.getUpdateInfo(cityid);
		if (temp != null && temp.status == MKOLUpdateElement.DOWNLOADING) {
			if (mOfflineMap != null)
				mOfflineMap.pause(cityid);
		}
		super.onPause();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Constants.ResultCode.HOME) {
			setResult(resultCode);
			finish();
		}
	}
}
