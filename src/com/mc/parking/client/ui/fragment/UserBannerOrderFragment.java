package com.mc.parking.client.ui.fragment;

import android.support.v4.app.Fragment;
import android.text.Html;
import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import com.google.gson.reflect.TypeToken;
import com.mc.parking.client.Constants;
import com.mc.park.client.R;
import com.mc.parking.client.entity.TOrder_Py;
import com.mc.parking.client.entity.TServiceOrder;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.PullToRefreshListView;
import com.mc.parking.client.layout.PullToRefreshListView.IXListViewListener;
import com.mc.parking.client.layout.PullToRefreshListView.OnRefreshListener;
import com.mc.parking.client.layout.net.CommFindEntity;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.admin.ServiceHisFragment.PullToRefreshListViewAdapter;
import com.mc.parking.client.ui.admin.ServiceHisFragment.PullToRefreshListViewAdapter.ViewHolder;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;

public class UserBannerOrderFragment extends Fragment implements IXListViewListener {

	private PullToRefreshListView listView;
	private PullToRefreshListViewAdapter adapter;
	private Activity activity;
	private LinearLayout progress;
	List<TServiceOrder> listalldata = new ArrayList<TServiceOrder>();
	TextView totalMoney, totalNumb;

	int datacount = 0;
	int totalcount = 0;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 重新刷新
	 */
	public void reload() {
		datacount = 0;
		getremotedata(datacount);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_admin_order_listview, container, false);
		activity = getActivity();
		listView = (PullToRefreshListView) view.findViewById(R.id.pull_to_refresh_listview);
		listView.setPullLoadEnable(false);
		listView.setXListViewListener(this);

		progress = (LinearLayout) view.findViewById(R.id.progressContainer);

		listView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				reload();

			}
		});
		listView.setPullRefreshEnable(true);

		adapter = new PullToRefreshListViewAdapter() {
		};
		listView.setAdapter(adapter);
		getremotedata(0);
		// click listener
		// Register the context menu for actions
		registerForContextMenu(listView);

		return view;
	}

	public void getremotedata(int count) {
		if (SessionUtils.loginUser != null) {
			HttpRequestAni<CommFindEntity<TServiceOrder>> httpRequestAni = new HttpRequestAni<CommFindEntity<TServiceOrder>>(
					activity, "/a/serviceorder/me" + "?p=" + count, new TypeToken<CommFindEntity<TServiceOrder>>() {
					}.getType()) {

				@Override
				public void callback(CommFindEntity<TServiceOrder> arg0) {

					if (arg0 == null) {
						Toast.makeText(getActivity(), "加载出错，请重新加载", Toast.LENGTH_SHORT).show();
						return;
					}

					if (arg0.getRowCount() > 0) {

						if (datacount == 0 && listalldata != null)
							listalldata.clear();
						List<TServiceOrder> templistdata = arg0.getResult();
						for (TServiceOrder tempitem : templistdata) {

							listalldata.add(tempitem);

						}
						totalcount = arg0.getPageCount();
						if (arg0.getPageCount() > 1)
							listView.setPullLoadEnable(true);
						else
							listView.setPullLoadEnable(false);

						// 显示分页信息
						UIUtils.displayPaginationInfo(activity, datacount, Constants.PAGINATION_PAGESIZE,
								arg0.getRowCount());

					} else if (arg0.getRowCount() == 0) {
						listalldata.clear();
					}

					adapter.getdata(listalldata);
					adapter.notifyDataSetChanged();
					listView.stopLoadMore();
					datacount++;
					if (listView.isRefreshing()) {

						listView.onRefreshComplete();
					}

				}

				@Override
				public void onFailed(String message) {

					if (message != null)
						Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
				}

			};

			httpRequestAni.execute();

		}
	}

	public abstract class PullToRefreshListViewAdapter extends android.widget.BaseAdapter {

		private ArrayList<TServiceOrder> items = new ArrayList<TServiceOrder>();;

		public class ViewHolder {
			public String id;
			public TextView estimate;
			public TextView orderfee;
			public TextView orderDate;
			public TextView address;
			TextView servicename;
			TextView order_status;
			TextView servicedetail;
			TextView orderSupplyComments;
			TimeCount time;

			LinearLayout orderSupplyComments_line;
		}

		public void getdata(List<TServiceOrder> mydata) {
			this.items = (ArrayList<TServiceOrder>) mydata;
		}

		/**
		 * Loads the data.
		 */
		public void loadData(List<TServiceOrder> remoteData) {

			items.clear();

			items.addAll(0, remoteData);

			// MANDATORY: Notify that the data has changed
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			TServiceOrder record = (TServiceOrder) getItem(position);

			ViewHolder viewHolder;

			if (convertView == null) {
				LayoutInflater inflater = activity.getLayoutInflater();
				convertView = inflater.inflate(R.layout.item_admin_list_banner, null);

				viewHolder = new ViewHolder();
				viewHolder.address = (TextView) convertView.findViewById(R.id.address);
				viewHolder.servicedetail = (TextView) convertView.findViewById(R.id.servicedetail);
				viewHolder.servicename = (TextView) convertView.findViewById(R.id.servicename);
				viewHolder.orderfee = (TextView) convertView.findViewById(R.id.order_fee);
				viewHolder.estimate = (TextView) convertView.findViewById(R.id.estimate);
				viewHolder.order_status = (TextView) convertView.findViewById(R.id.order_status);
				viewHolder.orderSupplyComments = (TextView) convertView.findViewById(R.id.orderSupplyComments);
				viewHolder.orderSupplyComments_line=(LinearLayout) convertView.findViewById(R.id.orderSupplyComments_line);
				
				viewHolder.time = null;
				convertView.setTag(viewHolder);
			} else
				viewHolder = (ViewHolder) convertView.getTag();
			if (record.orderDetail != null) {
				viewHolder.servicedetail.setText(record.orderDetail);
			} else
				viewHolder.servicedetail.setText("");
			
			if(record.orderSupplyComments!=null&&!record.orderSupplyComments.equals(""))
			{
				viewHolder.orderSupplyComments_line.setVisibility(View.VISIBLE);
				viewHolder.orderSupplyComments.setText(Html.fromHtml(record.orderSupplyComments));
				
			}else
			{
				viewHolder.orderSupplyComments_line.setVisibility(View.GONE);
			}
			viewHolder.orderfee.setText("费用￥" + record.payTotal);
			if(record.contactAddress!=null)
			{
			viewHolder.address.setText(record.contactAddress);
			}else
				viewHolder.address.setText("");
			viewHolder.servicename.setText(record.orderName);

			viewHolder.id = "" + position;
			viewHolder.estimate.setText("加载中。。。");

			// 将线程清除
			if (viewHolder.time != null) {
				viewHolder.time.oncancel();
				viewHolder.time = null;

			}

			if (record.orderStatus == Constants.ORDER_TYPE_FINISH) {
				viewHolder.order_status.setText("已完成");
				viewHolder.estimate.setText(sdf.format(record.orderStartDate) + "到" + sdf.format(record.orderEndDate));
			} else if (record.orderStatus == Constants.ORDER_TYPE_DOING) {
				// 进行中
				viewHolder.order_status.setText("进行中");
				if (record.estimate > 0) {
					int timeflag = getcountdown(record.orderStartDate, record.estimate);

					if (timeflag > 0) {
						// holder.time = new TimeCount(timeflag * 60 * 1000,
						// 1000, holder.estimate);
						viewHolder.time = new TimeCount(timeflag * 1000, 1000, viewHolder.estimate, 0);
						viewHolder.time.start();
					} else
						// if()
						viewHolder.estimate.setText("预计时间已到");
				} else
					viewHolder.estimate.setText("预计时间有误，请联系管理员");

			} else if (record.orderStatus == Constants.ORDER_TYPE_START) {
				// 已提交
				viewHolder.order_status.setText("已提交");
				if (record.orderStartDate != null)
					viewHolder.estimate.setText("" + sdf.format(record.orderStartDate));
				else
					viewHolder.estimate.setText("时间获取失败");
			} else if (record.orderStatus == Constants.ORDER_TYPE_OVERDUE) {
				// 已超期
				viewHolder.order_status.setText("已超时");
				int timeflag = getovertime(record.orderStartDate, record.estimate);

				if (timeflag < 0) {
					timeflag = Math.abs(timeflag);
					viewHolder.time = new TimeCount(timeflag * 60 * 1000, 1000, viewHolder.estimate, 1);
					viewHolder.time.start();
				}
			} else {
				viewHolder.order_status.setText("异常");
				viewHolder.estimate.setText("订单异常！");
			}

			return convertView;
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		if (datacount > totalcount - 1) {
			listView.stopLoadMore();
			Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
			listView.setPullLoadEnable(false);
			return;

		} else
			getremotedata(datacount);
	}

	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {
		TextView tx;
		long addnum = 0;
		int s = 0;
		boolean isstop = false;
		int i = 0;
		Thread thd = null;
		Handler handler = new Handler() { // handle
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					addnum = addnum + 1000;
					if (tx != null && !isstop)
						tx.setText(Html.fromHtml(
								"服务已经超时 <font size=\"3\" color=\"red\">" + changetime(addnum).toString() + "</font>"));
				}
				if (thd == null) {
					thd = new Thread(new MyThread());
					thd.start();
				}
				super.handleMessage(msg);
			}
		};

		public TimeCount(long millisInFuture, long countDownInterval, TextView tx) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
			this.tx = tx;
		}

		public TimeCount(long millisInFuture, long countDownInterval, TextView tx, int s) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
			this.tx = tx;
			this.s = s;
			if (s == 1) {
				this.addnum = millisInFuture;
				isstop = false;
				thd = null;
				thd = new Thread(new MyThread());
				thd.start();

			} else
				thd = null;
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			if (s == 0) {
				addnum = 0;
				thd = new Thread(new MyThread());
				thd.start();
			}
			/*
			 * int i = 10; while (i > 0) { try { Thread.sleep(1000); // sleep
			 * 1000ms addnum++; Thread thd = new Thread(new MyThread(addnum));
			 * thd.start(); i--; } catch (Exception e) { e.printStackTrace(); }
			 * }
			 */

		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			if (tx == null) {
				super.cancel();
			} else {
				if (s == 1) {
					this.onFinish();
				} else {

					tx.setText("预计服务约在 " + changetime(millisUntilFinished) + "完成");
				}
			}
		}

		public void oncancel() {
			if (thd != null) {
				// thd.stop();
				isstop = true;
				thd = null;
			}
			super.cancel();

		}

		public class MyThread implements Runnable {
			long addnum;

			// thread
			@Override
			public void run() {
				try {
					while (!isstop) {
						Thread.sleep(1000);
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// tx.setText("服务已经超时" + changetime(addnum));
			}
		}
	}

	private int getcountdown(Date starttime, int limttime) {
		int hasmint = 0;
		int stillmint = 0;
		Date end;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date start;
		try {
			// start = sdf.parse(starttime.toString());
			// end = sdf.parse(new Date(System.currentTimeMillis()).toString());
			hasmint = (int) ((System.currentTimeMillis() - starttime.getTime()));
			if (hasmint > limttime * 1000 * 60)
				stillmint = 0;
			else
				stillmint = limttime * 1000 * 60 - hasmint;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stillmint / 1000;
	}

	private int getovertime(Date starttime, int limttime) {
		int hasmint = 0;
		int stillmint = 0;
		Date end;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date start;
		try {
			// start = sdf.parse(starttime.toString());
			// end = sdf.parse(new Date(System.currentTimeMillis()).toString());
			hasmint = (int) ((System.currentTimeMillis() - starttime.getTime()) / (1000 * 60));
			stillmint = limttime - hasmint;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return stillmint;
	}

	private String changetime(long millisUntilFinished) {
		StringBuffer sb = new StringBuffer();
		int second = (int) ((millisUntilFinished % (60 * 1000)) / 1000);
		int minute = (int) (millisUntilFinished % (60 * 60 * 1000)) / (60 * 1000);
		int hour = (int) (millisUntilFinished % (60 * 60 * 60 * 1000)) / (60 * 60 * 1000);
		if (hour != 0)
			sb.append("" + hour + "小时");
		if (minute != 0)
			sb.append(minute + "分钟");
		else if (hour != 0)
			sb.append("0分钟");
		sb.append(second + "秒");
		return sb.toString();

	}

}
