package com.mc.parking.client.ui.admin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.client.Constants;
import com.mc.park.client.R;
import com.mc.parking.client.entity.OrderEntity;
import com.mc.parking.client.entity.TIncome;
import com.mc.parking.client.entity.TOrder_Py;
import com.mc.parking.client.entity.TParkInfo_Py;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.PullToRefreshListView;
import com.mc.parking.client.layout.PullToRefreshListView.IXListViewListener;
import com.mc.parking.client.layout.PullToRefreshListView.OnRefreshListener;
import com.mc.parking.client.layout.net.CommFindEntity;
import com.mc.parking.client.layout.net.HttpRequest;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.OrderDetailActivity;
import com.mc.parking.client.ui.admin.OrderHisFragment.PullToRefreshListViewAdapter.ViewHolder;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;

public class OrderHisFragment extends Fragment implements IXListViewListener {
	private PullToRefreshListView listView;
	private PullToRefreshListViewAdapter adapter;
	private Activity activity;
	private LinearLayout progress;
	private OrderDetailFragment orderDetailFragment;
	List<OrderEntity> listalldata = new ArrayList<OrderEntity>();
	TextView totalMoney, totalNumb;

	int datacount = 0;
	int totalcount = 0;

	/**
	 * 重新刷新
	 */
	public void reload() {
		datacount = 0;
		getremotedata(datacount);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_admin_hisorder_listview,
				container, false);
		activity = getActivity();
		listView = (PullToRefreshListView) view
				.findViewById(R.id.pull_to_refresh_listview);
		listView.setPullLoadEnable(false);
		listView.setXListViewListener(this);
		totalMoney = (TextView) view.findViewById(R.id.total_money);
		totalNumb = (TextView) view.findViewById(R.id.total_numb);
		totalMoney.setText("......");
		totalNumb.setText("......");

		progress = (LinearLayout) view.findViewById(R.id.progressContainer);

		orderDetailFragment = new OrderDetailFragment();

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

		// click listener
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				ViewHolder viewHolder = (ViewHolder) arg1.getTag();
				if (viewHolder.orderName != null && adapter != null) {
					OrderEntity record = (OrderEntity) adapter.getItem(arg2);
					if (record != null) {
						Intent intent = new Intent();
						// orderinfo
						intent.setClass(getActivity(),
								OrderDetailActivity.class);
						intent.putExtra("isRefresh", true);
						intent.putExtra("orderinfo", record);
						startActivity(intent);

					}

					// orderDetailFragment.setEntity(record);
					// FragmentTransaction transaction = getFragmentManager()
					// .beginTransaction();
					// transaction.replace(android.R.id.tabcontent,
					// orderDetailFragment,"orderdetail")
					// .addToBackStack(null).commit();
				}
			}
		});

		// Register the context menu for actions
		registerForContextMenu(listView);
		// new OrderAdmListTask(){
		// @Override
		// protected void onPreExecute() {
		// if(progress!=null){
		// progress.setVisibility(View.VISIBLE);
		// }
		// }
		//
		// @Override
		// protected void onException(final Exception e) throws RuntimeException
		// {
		// super.onException(e);
		// progress.setVisibility(View.GONE);
		// if (e instanceof OperationCanceledException) {
		// System.err.println(e);
		// }
		// }
		//
		// @Override
		// protected void onSuccess(final List<OrderEntity> data) throws
		// Exception {
		// super.onSuccess(data);
		// progress.setVisibility(View.GONE);
		// adapter.loadData(data);
		// }
		// }.execute();;

		return view;
	}

	public void getremotedata(int count) {
		if (count == 0) {
			totalNumb.setText("0");
			totalMoney.setText("￥0.00");
		}
		if (SessionUtils.loginUser != null
				&& SessionUtils.loginUser.parkInfoAdm != null) {
			HttpRequest<CommFindEntity<OrderEntity>> httpRequestAni = new HttpRequest<CommFindEntity<OrderEntity>>(
					"/a/orderhis/" + SessionUtils.loginUser.parkInfoAdm.parkId
							+ "?p=" + count,
					new TypeToken<CommFindEntity<OrderEntity>>() {
					}.getType()) {

				@Override
				public void onSuccess(CommFindEntity<OrderEntity> arg0) {

					if (arg0 == null) {
						Toast.makeText(getActivity(), "加载出错，请重新加载",
								Toast.LENGTH_SHORT).show();
						return;
					}
					if (arg0.getRowCount() > 0) {

						if (datacount == 0 && listalldata != null)
							listalldata.clear();
						List<OrderEntity> templistdata = arg0.getResult();
						for (OrderEntity tempitem : templistdata) {

							listalldata.add(tempitem);

						}
						totalcount = arg0.getPageCount();
						totalNumb.setText("" + arg0.getRowCount());
						getallmoneyDate();
						if (arg0.getPageCount() > 1)
							listView.setPullLoadEnable(true);
						else
							listView.setPullLoadEnable(false);
						
						//显示分页信息
						UIUtils.displayPaginationInfo(activity, datacount, Constants.PAGINATION_PAGESIZE, arg0.getRowCount() );
						
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
						Toast.makeText(getActivity(), message,
								Toast.LENGTH_SHORT).show();

				}

			};

			httpRequestAni.execute();
		}

	}

	public abstract class PullToRefreshListViewAdapter extends
			android.widget.BaseAdapter {

		private ArrayList<OrderEntity> items = new ArrayList<OrderEntity>();;

		public class ViewHolder {
			public String id;
			public TextView orderName;
			public TextView useid;;
			public TextView orderDate;
			public Button goButton;
			public TextView paytype;
			public TextView payMoney;

		}

		public void getdata(List<OrderEntity> mydata) {
			this.items = (ArrayList<OrderEntity>) mydata;
		}

		/**
		 * Loads the data.
		 */
		public void loadData(List<OrderEntity> remoteData) {

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
			View rowView = convertView;

			final OrderEntity record = (OrderEntity) getItem(position);

			LayoutInflater inflater = activity.getLayoutInflater();

			final ViewHolder viewHolder = new ViewHolder();

			if (convertView == null) {
				rowView = inflater.inflate(R.layout.item_admin_list_order_his,
						null);

				viewHolder.orderName = (TextView) rowView
						.findViewById(R.id.order_string);
				viewHolder.useid = (TextView) rowView
						.findViewById(R.id.order_userid);

				viewHolder.orderDate = (TextView) rowView
						.findViewById(R.id.order_item_date);

				viewHolder.goButton = (Button) rowView
						.findViewById(R.id.order_reject);
				viewHolder.paytype = (TextView) rowView
						.findViewById(R.id.order_item_money);

				rowView.setTag(viewHolder);
			}

			final ViewHolder holder = (ViewHolder) rowView.getTag();

			TuserInfo userinfo = record.getUserInfo();
			StringBuilder orderState = new StringBuilder("<html>");
			orderState.append("" + record.getOrderId());
			if (record.getOrderStatus() == Constants.PARKORDER_STATE_COMPLETE)
				orderState.append("<font color=#7c7c7c size=3>[已完成]</font>");
			else if (record.getOrderStatus() == Constants.PARKORDER_STATE_ERROR)
				orderState.append("<font color=#7c7c7c size=3>[异常]</font>");
			else
				orderState.append("<font color=#7c7c7c size=3>[其它]</font>");

			orderState.append("</html>");

			holder.orderName.setText(Html.fromHtml(orderState.toString()));
			holder.useid.setText("" + userinfo.userPhone);
			holder.orderDate.setText(UIUtils.formatDate(activity, record
					.getEndDate().getTime()));

			if (activity != null && activity instanceof AdminHomeActivity) {
				holder.paytype.setText(((AdminHomeActivity) activity)
						.getpaymoney(record));
			}

			// if(record.getParkInfo().feeType==1)
			// {
			// holder.paytype.setText("分段付费");
			// }else
			// {
			// holder.paytype.setText("计次付费");
			//
			// }

			// if (record.getOrderStatus() == Constants.ORDER_TYPE_START) {
			// holder.goButton.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			//
			//
			// // CheweiFabuDialogFragment dialog2 = new
			// CheweiFabuDialogFragment(
			// // cheweiView);
			// // dialog2.show(getFragmentManager(),
			// "FabuDialogFragment");CheckoutDialogFragment
			// //
			//
			// CheckoutDialogFragment dialog2 = new CheckoutDialogFragment();
			// dialog2.show(getFragmentManager(), "FabuDialogFragment");
			//
			// }
			//
			// });
			//
			// }
			return rowView;
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(UIUtils.okorder)
		{
			UIUtils.okorder=false;
			
			reload();
		}
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

	void getallmoneyDate() {

		HttpRequestAni<TIncome> httpRequestgetmoney = new HttpRequestAni<TIncome>(
				getActivity(), "/a/income/"
						+ SessionUtils.loginUser.parkInfoAdm.parkId,
				new TypeToken<TIncome>() {
				}.getType()) {

			@Override
			public void callback(TIncome arg0) {

				totalMoney.setText("￥" + arg0.incometotal);

			}

		};

		httpRequestgetmoney.execute();

	}

}