package com.mc.parking.client.ui.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request.Method;
import com.google.gson.reflect.TypeToken;
import com.mc.parking.client.Constants;
import com.mc.parking.client.PackingApplication;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.OrderEntity;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.entity.TParkInfo_Py;
import com.mc.parking.client.entity.TParkService;
import com.mc.parking.client.layout.BaseDialogFragment;
import com.mc.parking.client.layout.PullToRefreshListView;
import com.mc.parking.client.layout.PullToRefreshListView.IXListViewListener;
import com.mc.parking.client.layout.PullToRefreshListView.OnRefreshListener;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.CommFindEntity;
import com.mc.parking.client.layout.net.HttpRequest;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.OrderActivity;
import com.mc.parking.client.ui.OrderDetailActivity;
import com.mc.parking.client.ui.ParkActivity;
import com.mc.parking.client.ui.YuyueActivity;
import com.mc.parking.client.ui.fragment.UserOrderFragment.PullToRefreshListViewAdapter.ViewHolder;
import com.mc.parking.client.utils.Notice;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;

public class UserOrderFragment extends  Fragment implements IXListViewListener  {

	ViewPager myviewpager = null;
	private List<View> views;
	private View view1, view2, view3;
	// 定义标题下面的三个类别以及数字
	private TextView baseviewpage_tv1, baseviewpage_tv2, baseviewpage_tv3, baseviewpage_numb1, baseviewpage_numb2,
			baseviewpage_numb3;
	private RelativeLayout indicator_first, indicator_second, indicator_thrid;
	private PullToRefreshListView listView1;
	//未完成
	private PullToRefreshListView listView2;
	// 未付款订单listview3
	private PullToRefreshListView listView3;
	private PullToRefreshListViewAdapter adapter1;
	private PullToRefreshListViewAdapter adapter2;
	private PullToRefreshListViewAdapter adapter3;

	private static final int COLOR_TEXT_NORMAL = Color.rgb(124, 124, 124);
	private static final int COLOR_INDICATOR_COLOR = Color.rgb(74, 144, 226);

	// BaseViewPagerIndicator mIndicator;

	int currentcountView1 = 1;
	int totalcountView1 = 0;
	int indexView1 = 0;// 长按删除指定数据的索引

	int currentcountView2 = 1;
	int totalcountView2 = 0;
	int currentcountView3 = 1;
	int totalcountView3 = 0;

	boolean hasCheckedView2;
	boolean hasCheckedView1;
	boolean hasCheckedView3;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ac_order_listview,
				container, false);
		baseviewpage_tv1 = (TextView) view.findViewById(R.id.baseviewpage_tv1);
		baseviewpage_tv2 = (TextView) view.findViewById(R.id.baseviewpage_tv2);
		baseviewpage_tv3 = (TextView) view.findViewById(R.id.baseviewpage_tv3);

		baseviewpage_numb1 = (TextView) view.findViewById(R.id.baseviewpage_numb1);
		baseviewpage_numb2 = (TextView) view.findViewById(R.id.baseviewpage_numb2);
		baseviewpage_numb3 = (TextView) view.findViewById(R.id.baseviewpage_numb3);

		indicator_first = (RelativeLayout) view.findViewById(R.id.indicator_first);
		indicator_second = (RelativeLayout) view.findViewById(R.id.indicator_second);
		indicator_thrid = (RelativeLayout) view.findViewById(R.id.indicator_thrid);

		indicator_first.setOnClickListener(ll1);
		indicator_second.setOnClickListener(ll1);
		indicator_thrid.setOnClickListener(ll1);

		indicator_first.setOnClickListener(ll1);

		Integer selectTab = getActivity().getIntent().getIntExtra("selectedTab", 0);
				

		hasCheckedView2 = false;
		hasCheckedView3 = false;
		initViewPager(selectTab,view);
		if (selectTab == 0) {
			initlistview();
			hasCheckedView1 = true;
		} else if (selectTab == 2) {
			getfirstdataForView2();
			hasCheckedView2 = true;
		}

		
		return view;
	}
	OnClickListener ll1 = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.indicator_first:
				if (myviewpager.getCurrentItem() == 0)
					return;
				else {
					myviewpager.setCurrentItem(0);
					changestatus(0);
				}

				break;
			case R.id.indicator_second:
				if (myviewpager.getCurrentItem() == 1)
					return;
				else {
					myviewpager.setCurrentItem(1);
					changestatus(1);
				}

				break;
			case R.id.indicator_thrid:
				if (myviewpager.getCurrentItem() == 2)
					return;
				else {
					myviewpager.setCurrentItem(2);
					changestatus(2);
				}

				break;

			default:
				break;
			}

		}
	};

	private void changestatus(int i) {

		switch (i) {
		case 0:
			baseviewpage_tv1.setTextColor(COLOR_INDICATOR_COLOR);
			baseviewpage_tv2.setTextColor(COLOR_TEXT_NORMAL);
			baseviewpage_tv3.setTextColor(COLOR_TEXT_NORMAL);

			indicator_first.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_tab_blue));
			indicator_second.setBackgroundDrawable(getResources().getDrawable(R.color.white));
			indicator_thrid.setBackgroundDrawable(getResources().getDrawable(R.color.white));

			break;
		case 1:
			baseviewpage_tv2.setTextColor(COLOR_INDICATOR_COLOR);
			baseviewpage_tv1.setTextColor(COLOR_TEXT_NORMAL);
			baseviewpage_tv3.setTextColor(COLOR_TEXT_NORMAL);

			indicator_second.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_tab_blue));
			indicator_first.setBackgroundDrawable(getResources().getDrawable(R.color.white));
			indicator_thrid.setBackgroundDrawable(getResources().getDrawable(R.color.white));
			break;
		case 2:
			baseviewpage_tv3.setTextColor(COLOR_INDICATOR_COLOR);
			baseviewpage_tv2.setTextColor(COLOR_TEXT_NORMAL);
			baseviewpage_tv1.setTextColor(COLOR_TEXT_NORMAL);

			indicator_thrid.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_tab_blue));
			indicator_second.setBackgroundDrawable(getResources().getDrawable(R.color.white));
			indicator_first.setBackgroundDrawable(getResources().getDrawable(R.color.white));

			break;

		default:
			break;
		}

	}

	private void initlistview() {
		listView1 = (PullToRefreshListView) view1.findViewById(R.id.history_pull_to_refresh_listview);
		listView1.setDividerHeight(10);
		adapter1 = new PullToRefreshListViewAdapter() {
		};

		listView1.setPullLoadEnable(false);
		listView1.setXListViewListener(this);
		listView1.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				getfirstdata();
				listView1.onRefreshComplete();
			}
		});
		getfirstdata();
		// click listener
		listView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				OrderEntity record = (OrderEntity) adapter1.getItem(arg2);
				if (record == null) {
					Toast.makeText(getActivity(), "加载出错，请刷新后重试", Toast.LENGTH_SHORT).show();
					return;
				}
				getorderEntryByID(record.getOrderId());
				/*
				 * if(record.getOrderStatus()==Constants.ORDER_TYPE_OVERDUE){
				 * Toast.makeText(getActivity(), "当前订单已经过期,请激活使用",
				 * Toast.LENGTH_LONG) .show(); return; }
				 * 
				 * if(record.getOrderStatus()==Constants.ORDER_TYPE_EXCPTION){
				 * Toast.makeText(getActivity(), "当前订单异常,请联系管理员",
				 * Toast.LENGTH_LONG) .show(); return; }
				 * 
				 * Intent intent = new Intent(getActivity(),
				 * OrderDetailActivity.class); Bundle buidle = new Bundle();
				 * buidle.putBoolean("isFromFinshed", false);
				 * buidle.putSerializable("orderinfo", record);
				 * intent.putExtras(buidle); startActivityForResult(intent, 1);
				 */
			}
		});

		listView2 = (PullToRefreshListView) view2.findViewById(R.id.history_pull_to_refresh_listview);
		listView2.setDividerHeight(10);
		adapter2 = new PullToRefreshListViewAdapter() {
		};

		listView2.setPullLoadEnable(false);
		listView2.setXListViewListener(this);

		listView2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				OrderEntity record = (OrderEntity) adapter2.getItem(arg2);

				Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
				Bundle buidle = new Bundle();
				buidle.putSerializable("orderinfo", record);
				buidle.putBoolean("isFromFinshed", true);
				intent.putExtras(buidle);
				startActivityForResult(intent, 1);
			}
		});

		listView2.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				getfirstdataForView2();

				listView2.onRefreshComplete();
			}
		});

		// 未付款订单listview3
		listView3 = (PullToRefreshListView) view3.findViewById(R.id.history_pull_to_refresh_listview);
		listView3.setDividerHeight(10);
		adapter3 = new PullToRefreshListViewAdapter() {

		};

		listView3.setPullLoadEnable(false);
		listView3.setXListViewListener(this);
		getfirstdataforUnPay();
		listView3.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				OrderEntity record = (OrderEntity) adapter3.getItem(arg2);

				Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
				Bundle buidle = new Bundle();
				buidle.putSerializable("orderinfo", record);
				buidle.putBoolean("isFromFinshed", false);
				intent.putExtras(buidle);
				startActivityForResult(intent, 1);
			}
		});

		listView3.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				getfirstdataforUnPay();
				listView3.onRefreshComplete();
			}
		});
		listView3.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ViewHolder viewHolder = (ViewHolder) arg1.getTag();
				if (viewHolder.orderName != null && adapter3 != null) {
					final OrderEntity orderEntity = (OrderEntity) adapter3.getItem(arg2);
					if (orderEntity.getOrderStatus() == Constants.ORDER_TYPE_START) {

						TParkInfoEntity infoEntity = orderEntity.getParkInfo();
						if (infoEntity != null) {

							List<TParkInfo_Py> pys = orderEntity.getPay();
							boolean foundHasPayment = false;
							if (pys != null) {
								for (TParkInfo_Py py : pys) {
									if (py.getAckStatus() == Constants.PAYMENT_STATUS_FINISH
											|| py.getAckStatus() == Constants.PAYMENT_STATUS_PENDING) {
										foundHasPayment = true;
										break;
									}
								}
							}

							if (!foundHasPayment) {
								BaseDialogFragment confirmDialog = new BaseDialogFragment();
								confirmDialog.setMessage("是否取消订单");
								confirmDialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										cancelorder(orderEntity);
									}

								});
								confirmDialog.setNegativeButton("否", null);
								confirmDialog.show(getActivity().getFragmentManager(), "");

							} else {
								Toast.makeText(getActivity(), "当前订单已经付费,您不能取消", Toast.LENGTH_LONG).show();

							}
						}
					}
				}
				return true;
			}
		});

	}

	private void initViewPager(int selectTab,View view) {
		myviewpager = (ViewPager) view.findViewById(R.id.viewpager);

		views = new ArrayList<View>();
		LayoutInflater inflater = getActivity().getLayoutInflater();
		view1 = inflater.inflate(R.layout.item_viewpage_listview, null);
		view2 = inflater.inflate(R.layout.item_viewpage_listview, null);
		view3 = inflater.inflate(R.layout.item_viewpage_listview, null);

		views.add(view3);// 未付费
		views.add(view1);// 已付费
		views.add(view2);// 已完成

		// views.add(view3);
		myviewpager.setAdapter(new MyViewPagerAdapter(views));
		myviewpager.setCurrentItem(selectTab);
		myviewpager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	// viewpager初始化
	public class MyViewPagerAdapter extends PagerAdapter {
		private List<View> mListViews;

		public MyViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

			// mIndicator.scroll(arg0, arg1);

		}

		public void onPageSelected(int arg0) {

			if (arg0 == 2 && !hasCheckedView2) {// 滑动过去再查下view2的东西
				getfirstdataForView2();
				hasCheckedView2 = true;
			} else if (arg0 == 0 && !hasCheckedView1) {
				initlistview();
				hasCheckedView1 = true;
			}
			changestatus(arg0);

		}

	}

	/**
	 * The adapter used to display the results in the list
	 * 
	 */

	public abstract class PullToRefreshListViewAdapter extends android.widget.BaseAdapter {

		List<OrderEntity> data = new ArrayList<OrderEntity>();

		public class ViewHolder {
			public String id;
			public ImageView imageView;
			public TextView orderName;
			public TextView orderDate;
			public TextView parkname;
			public TextView parktype;
			public ImageButton deletButton;
			public ImageView starticon;
			private ImageButton wancheng, weiwangcheng, yiguoqi;
			private ImageButton jihuobutton;
		}

		/**
		 * Loads the data.
		 */
		public void loadData(List<OrderEntity> remoteData) {

			data.clear();
			data.addAll(0, remoteData);
			// MANDATORY: Notify that the data has changed
			notifyDataSetChanged();
		}

		public void loadMore(List<OrderEntity> remoteData) {
			data.addAll(remoteData);
			// MANDATORY: Notify that the data has changed
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;

			final OrderEntity record = (OrderEntity) getItem(position);

			LayoutInflater inflater = getActivity().getLayoutInflater();

			ViewHolder viewHolder = new ViewHolder();

			if (convertView == null) {
				rowView = inflater.inflate(R.layout.item_list_order, null);
				viewHolder.parkname = (TextView) rowView.findViewById(R.id.order_item_parkname);

				viewHolder.orderName = (TextView) rowView.findViewById(R.id.order_string);

				viewHolder.orderDate = (TextView) rowView.findViewById(R.id.order_item_date);

				viewHolder.deletButton = (ImageButton) rowView.findViewById(R.id.order_delete);
				viewHolder.wancheng = (ImageButton) rowView.findViewById(R.id.order_wancheng);

				viewHolder.weiwangcheng = (ImageButton) rowView.findViewById(R.id.order_weiwancheng);

				viewHolder.yiguoqi = (ImageButton) rowView.findViewById(R.id.order_yiguoqi);
				viewHolder.parktype = (TextView) rowView.findViewById(R.id.order_item_parktype);
				viewHolder.starticon = (ImageView) rowView.findViewById(R.id.order_start_icon);
				viewHolder.jihuobutton = (ImageButton) rowView.findViewById(R.id.jihuo);
				rowView.setTag(viewHolder);

			}

			ViewHolder holder = (ViewHolder) rowView.getTag();
			TParkInfoEntity infoEntity = record.getParkInfo();

			holder.parktype.setText(record.getOrderDetail());

			holder.parkname.setText(infoEntity.parkname);

			if (record.orderFeeType == 1 && record.getStartDate() != null && record.getEndDate() == null) {
				holder.starticon.setVisibility(View.VISIBLE);
			} else {
				holder.starticon.setVisibility(View.GONE);
			}

			String orderId = "" + record.getOrderId();
			if (record.getPay() != null) {
				double pay = 0.0d;
				double payPending = 0.0d;
				for (TParkInfo_Py py : record.getPay()) {
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
				}
				if (payPending > 0) {
					orderId += "[正在付款" + UIUtils.decimalPrice(pay) + "元]";
				}
			}

			holder.orderName.setText("订单号:" + orderId);
			holder.orderDate.setText(UIUtils.formatDate(getActivity(), record.getOrderDate().getTime()));

			if (record.getOrderStatus() == Constants.ORDER_TYPE_FINISH) {
				holder.wancheng.setVisibility(View.VISIBLE);
				holder.yiguoqi.setVisibility(View.GONE);
				holder.jihuobutton.setVisibility(View.GONE);
				holder.wancheng.setEnabled(false);
				holder.weiwangcheng.setEnabled(false);
				holder.weiwangcheng.setVisibility(View.GONE);
			}

			else if (record.getOrderStatus() == Constants.ORDER_TYPE_START) {

				holder.wancheng.setVisibility(View.GONE);
				holder.yiguoqi.setVisibility(View.GONE);
				holder.jihuobutton.setVisibility(View.GONE);
				holder.weiwangcheng.setVisibility(View.VISIBLE);
				holder.wancheng.setEnabled(false);
				holder.weiwangcheng.setEnabled(false);
			} else if (record.getOrderStatus() == Constants.ORDER_TYPE_OVERDUE) {

				holder.wancheng.setVisibility(View.GONE);
				holder.yiguoqi.setVisibility(View.VISIBLE);
				holder.jihuobutton.setVisibility(View.VISIBLE);
				holder.wancheng.setEnabled(false);
				holder.weiwangcheng.setEnabled(false);
				holder.yiguoqi.setEnabled(false);
				holder.weiwangcheng.setVisibility(View.GONE);
				holder.jihuobutton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						BaseDialogFragment confirmDialog = new BaseDialogFragment();
						confirmDialog.setMessage("请确认车场管理员已在场，是否激活订单?");
						confirmDialog.setPositiveButton("激活", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								restartorder(record);
							}
						});
						confirmDialog.setNegativeButton("取消", null);
						confirmDialog.show(getActivity().getFragmentManager(), "");

					}
				});
			}

			return rowView;
		}
	}

	/*@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Constants.ResultCode.HOME) {
			setResult(resultCode);
			finish();
		} else if (resultCode == Constants.ResultCode.NAVATIGOR_START) {
			setResult(Constants.ResultCode.NAVATIGOR_START, data);
			finish();
		} else if (resultCode == Constants.ResultCode.ORDER_LIST_RELOAD) {
			onRefresh();
		}
	}*/
	
	public void getfirstdata() {
		HttpRequest<CommFindEntity<OrderEntity>> httpRequestAni = new HttpRequest<CommFindEntity<OrderEntity>>(
				"/a/order/mepay/2", new TypeToken<CommFindEntity<OrderEntity>>() {
				}.getType()) {

			@Override
			public void onSuccess(CommFindEntity<OrderEntity> arg0) {
				if (arg0 == null)
					return;
				if (arg0.getRowCount() > 0) {
					// 如果返回的不为空并且总数大于0
					// 有数据的时候处理
					baseviewpage_numb2.setText("" + arg0.getRowCount());
					currentcountView1 = 1;
					totalcountView1 = arg0.getPageCount();
					// mIndicator.setnumb(arg0.getRowCount());
					if (arg0.getPageCount() > 1)
						listView1.setPullLoadEnable(true);
					else
						listView1.setPullLoadEnable(false);

					adapter1.loadData(arg0.getResult());
				} else if (arg0.getRowCount() == 0) {
					adapter1.loadData(new ArrayList<OrderEntity>());
				}
				listView1.setAdapter(adapter1);
			}

			@Override
			public void onFailed(String message) {

			}

		};

		httpRequestAni.execute();
	}

	/*public void getfirstdata() {
		HttpRequestAni<CommFindEntity<OrderEntity>> httpRequestAni = new HttpRequestAni<CommFindEntity<OrderEntity>>(
				getActivity(), "/a/order/mepay/2", new TypeToken<CommFindEntity<OrderEntity>>() {
				}.getType()) {

			@Override
			public void callback(CommFindEntity<OrderEntity> arg0) {
				if (arg0 == null)
					return;
				if (arg0.getRowCount() > 0) {
					// 如果返回的不为空并且总数大于0
					// 有数据的时候处理
					baseviewpage_numb2.setText("" + arg0.getRowCount());
					currentcountView1 = 1;
					totalcountView1 = arg0.getPageCount();
					// mIndicator.setnumb(arg0.getRowCount());
					if (arg0.getPageCount() > 1)
						listView1.setPullLoadEnable(true);
					else
						listView1.setPullLoadEnable(false);

					adapter1.loadData(arg0.getResult());
				} else if (arg0.getRowCount() == 0) {
					adapter1.loadData(new ArrayList<OrderEntity>());
				}
				listView1.setAdapter(adapter1);
			}

		};

		httpRequestAni.execute();
	}*/
	
	

	public void getfirstdataforUnPay() {
		HttpRequestAni<CommFindEntity<OrderEntity>> httpRequestAni = new HttpRequestAni<CommFindEntity<OrderEntity>>(
				getActivity(), "/a/order/mepay/1", new TypeToken<CommFindEntity<OrderEntity>>() {
				}.getType()) {

			@Override
			public void callback(CommFindEntity<OrderEntity> arg0) {

				if (arg0 == null)
					return;
				if (arg0.getRowCount() > 0) {
					// 如果返回的不为空并且总数大于0
					// 有数据的时候处理
					baseviewpage_numb1.setText("" + arg0.getRowCount());
					currentcountView3 = 1;
					totalcountView3 = arg0.getPageCount();
					// mIndicator.setnumb(arg0.getRowCount());
					if (arg0.getPageCount() > 1)
						listView3.setPullLoadEnable(true);
					else
						listView3.setPullLoadEnable(false);

					adapter3.loadData(arg0.getResult());
				} else if (arg0.getRowCount() == 0) {
					adapter3.loadData(new ArrayList<OrderEntity>());
				}
				listView3.setAdapter(adapter3);

			}

		};

		httpRequestAni.execute();
	}
	

	/*private void getfirstdataforUnPay() {
		HttpRequest<CommFindEntity<OrderEntity>> httpRequestAni = new HttpRequest<CommFindEntity<OrderEntity>>(
				"/a/order/mepay/1", new TypeToken<CommFindEntity<OrderEntity>>() {
				}.getType()) {

			@Override
			public void onSuccess(CommFindEntity<OrderEntity> arg0) {

				if (arg0 == null)
					return;
				if (arg0.getRowCount() > 0) {
					// 如果返回的不为空并且总数大于0
					// 有数据的时候处理
					baseviewpage_numb1.setText("" + arg0.getRowCount());
					currentcountView3 = 1;
					totalcountView3 = arg0.getPageCount();
					// mIndicator.setnumb(arg0.getRowCount());
					if (arg0.getPageCount() > 1)
						listView3.setPullLoadEnable(true);
					else
						listView3.setPullLoadEnable(false);

					adapter3.loadData(arg0.getResult());
				} else if (arg0.getRowCount() == 0) {
					adapter3.loadData(new ArrayList<OrderEntity>());
				}
				listView3.setAdapter(adapter3);

			}

			@Override
			public void onFailed(String message) {

			}

		};

		httpRequestAni.execute();
	}*/

	private void getfirstdataForView2() {
		HttpRequestAni<CommFindEntity<OrderEntity>> httpRequestAni = new HttpRequestAni<CommFindEntity<OrderEntity>>(
				getActivity(), "/a/orderhis/me", new TypeToken<CommFindEntity<OrderEntity>>() {
				}.getType()) {

			@Override
			public void callback(CommFindEntity<OrderEntity> arg0) {
				if (arg0 == null)
					return;
				if (arg0.getRowCount() > 0) {
					// 如果返回的不为空并且总数大于0
					// 有数据的时候处理
					currentcountView2 = 1;
					totalcountView2 = arg0.getPageCount();
					if (arg0.getPageCount() > 1)
						listView2.setPullLoadEnable(true);
					else
						listView2.setPullLoadEnable(false);
					baseviewpage_numb3.setText("" + arg0.getRowCount());
					adapter2.loadData(arg0.getResult());
				} else if (arg0.getRowCount() == 0) {
					adapter2.loadData(new ArrayList<OrderEntity>());
				}
				listView2.setAdapter(adapter2);

			}

		};

		httpRequestAni.execute();
	}

	public void onloadMoreForView2() {

		if (currentcountView2 > totalcountView2 - 1) {
			listView2.stopLoadMore();
			Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
			listView2.setPullLoadEnable(false);
			return;

		}

		HttpRequest<CommFindEntity<OrderEntity>> httpRequestAni = new HttpRequest<CommFindEntity<OrderEntity>>(
				"/a/orderhis/me" + "?p=" + currentcountView2, new TypeToken<CommFindEntity<OrderEntity>>() {
				}.getType()) {

			@Override
			public void onSuccess(CommFindEntity<OrderEntity> arg0) {

				if (arg0 == null) {
					listView2.stopLoadMore();
					return;
				}
				if (arg0.getRowCount() > 0) {
					// 如果返回的不为空并且总数大于0
					// 有数据的时候处理
					totalcountView2 = arg0.getPageCount();
					List<OrderEntity> templistdata = arg0.getResult();
					adapter2.loadMore(templistdata);
					adapter2.notifyDataSetChanged();

				} else if (arg0.getRowCount() == 0) {
					listView2.stopLoadMore();
					return;

				}

				listView2.stopLoadMore();
				currentcountView2++;
			}

			@Override
			public void onFailed(String message) {
				Toast.makeText(getActivity(), "加载列表失败", Toast.LENGTH_SHORT).show();
			}

		};
		httpRequestAni.execute();

	}

	public void onloadMoreforpay() {

		if (currentcountView1 > totalcountView1 - 1) {
			listView1.stopLoadMore();
			Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
			listView1.setPullLoadEnable(false);
			listView1.stopLoadMore();
			return;

		}

		HttpRequest<CommFindEntity<OrderEntity>> httpRequestAni = new HttpRequest<CommFindEntity<OrderEntity>>(
				"/a/order/mepay/" + 2 + "?p=" + currentcountView1, new TypeToken<CommFindEntity<OrderEntity>>() {
				}.getType()) {

			@Override
			public void onSuccess(CommFindEntity<OrderEntity> arg0) {

				if (arg0 == null) {
					listView1.stopLoadMore();
					return;
				}
				if (arg0.getRowCount() > 0) {
					// 如果返回的不为空并且总数大于0
					// 有数据的时候处理
					totalcountView1 = arg0.getPageCount();
					// mIndicator.setnumb(arg0.getRowCount());
					List<OrderEntity> templistdata = arg0.getResult();
					adapter1.loadMore(templistdata);
					adapter1.notifyDataSetChanged();

				} else if (arg0.getRowCount() == 0) {
					listView1.stopLoadMore();
					return;

				}

				listView1.stopLoadMore();
				currentcountView1++;
			}

			@Override
			public void onFailed(String message) {
				Toast.makeText(getActivity(), "加载列表失败", Toast.LENGTH_SHORT).show();
			}

		};
		httpRequestAni.execute();

	}

	

	public void onloadMoreforUnpay() {

		if (currentcountView3 > totalcountView3 - 1) {
			listView3.stopLoadMore();
			Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
			listView3.setPullLoadEnable(false);
			listView3.stopLoadMore();
			return;

		}

		HttpRequest<CommFindEntity<OrderEntity>> httpRequestAni = new HttpRequest<CommFindEntity<OrderEntity>>(
				"/a/order/mepay/" + 1 + "?p=" + currentcountView3, new TypeToken<CommFindEntity<OrderEntity>>() {
				}.getType()) {

			@Override
			public void onSuccess(CommFindEntity<OrderEntity> arg0) {

				if (arg0 == null) {
					listView3.stopLoadMore();
					return;
				}
				if (arg0.getRowCount() > 0) {
					// 如果返回的不为空并且总数大于0
					// 有数据的时候处理
					totalcountView3 = arg0.getPageCount();
					// mIndicator.setnumb(arg0.getRowCount());
					List<OrderEntity> templistdata = arg0.getResult();
					adapter3.loadMore(templistdata);
					adapter3.notifyDataSetChanged();

				} else if (arg0.getRowCount() == 0) {
					listView3.stopLoadMore();
					return;

				}

				listView3.stopLoadMore();
				currentcountView3++;
			}

			@Override
			public void onFailed(String message) {
				Toast.makeText(getActivity(), "加载列表失败", Toast.LENGTH_SHORT).show();
			}

		};
		httpRequestAni.execute();

	}

	/**
	 * 
	 * @param orderEntity
	 */
	private void cancelorder(OrderEntity orderEntity) {
		Long id = orderEntity.getOrderId();
		HttpRequest<ComResponse<OrderEntity>> httpRequestAni = new HttpRequest<ComResponse<OrderEntity>>(
				"/a/order/delete/" + id, new TypeToken<ComResponse<OrderEntity>>() {
				}.getType()) {
			@Override
			public void onSuccess(ComResponse<OrderEntity> arg0) {
				if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {
					Toast.makeText(getActivity(), "取消订单成功", Toast.LENGTH_SHORT).show();
					getfirstdataforUnPay();
					adapter3.notifyDataSetChanged();
				} else {
					Toast.makeText(getActivity(), "取消订单失败", Toast.LENGTH_SHORT).show();
				}

			}

			@Override
			public void onFailed(String message) {
				Toast.makeText(getActivity(), "删除订单失败", Toast.LENGTH_SHORT).show();
				Notice.errorotice();
			}

		};

		httpRequestAni.execute();

	}

	private void restartorder(OrderEntity orderinfo) {
		HttpRequest<ComResponse<OrderEntity>> httpRequestAni = new HttpRequest<ComResponse<OrderEntity>>(Method.POST,
				orderinfo, "/a/order/restartorder", new TypeToken<ComResponse<OrderEntity>>() {
				}.getType(), OrderEntity.class) {

			@Override
			public void onSuccess(ComResponse<OrderEntity> arg0) {
				if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {
					getfirstdata();
					Notice.rightnotice();
				} else {
					Toast.makeText(getActivity(), "[异常]" + arg0.getErrorMessage(), Toast.LENGTH_SHORT).show();
					Notice.errorotice();
				}

			}

			@Override
			public void onFailed(String message) {
				Toast.makeText(getActivity(), "[异常]", Toast.LENGTH_SHORT).show();
				Notice.errorotice();
			}

		};

		httpRequestAni.execute();

	}

	@Override
	public void onRefresh() {
		int itemnum = myviewpager.getCurrentItem();
		if (itemnum == 1) {
			getfirstdata();
			listView1.onRefreshComplete();
		} else if (itemnum == 2) {
			getfirstdataForView2();
			listView2.onRefreshComplete();
		}

	}

	@Override
	public void onLoadMore() {
		int itemnum = myviewpager.getCurrentItem();
		if (itemnum == 1) {
			onloadMoreforpay();
		} else if (itemnum == 2) {
			onloadMoreForView2();
		} else if (itemnum == 0) {

			onloadMoreforUnpay();
		}
	}

	void getorderEntryByID(long id) {
		HttpRequestAni<OrderEntity> httpRequestAni = new HttpRequestAni<OrderEntity>(getActivity(),
				"/a/order/find/" + id, new TypeToken<OrderEntity>() {
				}.getType()) {

			@Override
			public void callback(OrderEntity arg0) {
				if (arg0 == null) {
					Toast.makeText(getActivity(), "加载出错，请刷新后重试", Toast.LENGTH_SHORT).show();
					return;
				} else {
					OrderEntity record = arg0;
					if (record.getOrderStatus() == Constants.ORDER_TYPE_OVERDUE) {
						Toast.makeText(getActivity(), "当前订单已经过期,请激活使用", Toast.LENGTH_LONG).show();
						getfirstdata();
						return;
					}

					if (record.getOrderStatus() == Constants.ORDER_TYPE_EXCPTION) {
						Toast.makeText(getActivity(), "当前订单异常,请联系管理员", Toast.LENGTH_LONG).show();
						return;
					}

					Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
					Bundle buidle = new Bundle();
					buidle.putBoolean("isFromFinshed", false);
					buidle.putSerializable("orderinfo", record);
					intent.putExtras(buidle);
					startActivityForResult(intent, 1);

				}

			}

			@Override
			public void onFailed(String message) {
				// TODO Auto-generated method stub
				super.onFailed(message);
				Toast.makeText(getActivity(), "访问网络数据失败", Toast.LENGTH_SHORT).show();

			}

		};

		httpRequestAni.execute();

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (Constants.NETFLAG)
			getfirstdata();
	}
	
}