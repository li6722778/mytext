package com.mc.parking.client.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Window;
import android.widget.TextView;

import com.mc.parking.client.R;
import com.mc.parking.client.layout.ActionBaseActivity;
import com.mc.parking.client.layout.BaseViewPagerIndicator;
import com.mc.parking.client.ui.fragment.TakecashFragment;
import com.mc.parking.client.ui.fragment.TakecashhistoryFragment;

public class TakecashActivity extends ActionBaseActivity {

	private ViewPager viewPager;
	private String[] columns = new String[] { "账号资产", "提现记录" };
	public android.support.v4.app.Fragment tabFragments[];
	private boolean hasCheckedView2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.ac_take_cash);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_topbar);

		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		titleView.setText("提现管理");

		

		tabFragments = new android.support.v4.app.Fragment[] {
				new TakecashFragment(), new TakecashhistoryFragment() };
		viewPager = (ViewPager) findViewById(R.id.viewpagerss);
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

				if(arg0==1&&!hasCheckedView2){//滑动过去再查下view2的东西
					if(tabFragments!=null){
						for(Fragment orderFragment:tabFragments){
							if(orderFragment instanceof TakecashhistoryFragment){
								((TakecashhistoryFragment)orderFragment).load();
								hasCheckedView2 = true;
								break;
							}
						}
					}
					
				}

			
			}
		});
		viewPager.setCurrentItem(0);
		
	
	}
	
	public void setposition(int page)
	{
		
		if(page!=0||page!=1)
		{
			return;
		}
		viewPager.setCurrentItem(page);
		
		
	}
	

}
