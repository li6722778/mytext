package com.mc.parking.client.ui.admin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.client.Constants;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.ChebolePayOptions;
import com.mc.parking.client.entity.OrderEntity;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.PullToRefreshListView;
import com.mc.parking.client.layout.PullToRefreshListView.IXListViewListener;
import com.mc.parking.client.layout.PullToRefreshListView.OnRefreshListener;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.CommFindEntity;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.OrderActivity;
import com.mc.parking.client.ui.OrderDetailActivity;
import com.mc.parking.client.ui.admin.OrderFragment.PullToRefreshListViewAdapter.ViewHolder;
import com.mc.parking.client.utils.Notice;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;

public class OrderFragment extends Fragment implements IXListViewListener {
	private PullToRefreshListView listView;
	private PullToRefreshListViewAdapter adapter;
	private Activity activity;
	private LinearLayout progress;
	private OrderDetailFragment orderDetailFragment;
	List<OrderEntity> listalldata = new ArrayList<OrderEntity>();
	private int datacount = 0;
	private int totalcount = 0;


	/**
	 * 重新刷新
	 */
	public void reload() {
		datacount = 0;
		getremotedata(datacount);
	}

	@Override
	public void onResume() {
		if(Constants.NETFLAG)
		{
		    reload();
		}
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_admin_order_listview,
				container, false);
		activity = getActivity();
		listView = (PullToRefreshListView) view
				.findViewById(R.id.pull_to_refresh_listview);
		listView.setPullLoadEnable(false);
		listView.setXListViewListener(this);

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
		//getremotedata(datacount);

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
		String urlString = "";

		if (SessionUtils.loginUser != null
				&& SessionUtils.loginUser.parkInfoAdm != null) {
			HttpRequestAni<CommFindEntity<OrderEntity>> httpRequestAni = new HttpRequestAni<CommFindEntity<OrderEntity>>(
					activity,"/a/order/" + SessionUtils.loginUser.parkInfoAdm.parkId
							+ "?p=" + count,
					new TypeToken<CommFindEntity<OrderEntity>>() {
					}.getType()) {

				@Override
				public void callback(CommFindEntity<OrderEntity> arg0) {

					if (arg0 == null) {
						Toast.makeText(getActivity(), "加载出错，请重新加载",
								Toast.LENGTH_SHORT).show();
						return;
					}
					
					if(activity!=null&&activity instanceof AdminHomeActivity){
						((AdminHomeActivity)activity).setDisplayTotal(arg0.getRowCount() );
					}
					
					if (arg0.getRowCount() > 0) {

						if (datacount == 0 && listalldata != null)
							listalldata.clear();
						List<OrderEntity> templistdata = arg0.getResult();
						for (OrderEntity tempitem : templistdata) {

							listalldata.add(tempitem);

						}
						totalcount = arg0.getPageCount();
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
			public ImageView starticon;
			public TextView paytype;

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
				rowView = inflater
						.inflate(R.layout.item_admin_list_order, null);

				viewHolder.orderName = (TextView) rowView
						.findViewById(R.id.order_string);
				viewHolder.useid = (TextView) rowView
						.findViewById(R.id.order_userid);

				viewHolder.orderDate = (TextView) rowView
						.findViewById(R.id.order_item_date);

				viewHolder.goButton = (Button) rowView
						.findViewById(R.id.order_reject);
				viewHolder.paytype = (TextView) rowView
						.findViewById(R.id.order_item_type);

				viewHolder.starticon=(ImageView)rowView.findViewById(R.id.order_admin_start_icon);
				
				rowView.setTag(viewHolder);
			}

			final ViewHolder holder = (ViewHolder) rowView.getTag();

			TuserInfo userinfo = record.getUserInfo();
			holder.orderName.setText("" + record.getOrderId());
			holder.useid.setText("" + userinfo.userPhone);
			holder.orderDate.setText(UIUtils.formatDate(activity, record
					.getOrderDate().getTime()));
			

			
			if(record.orderFeeType==0){
				if (record.getParkInfo().feeType == 1) {
					holder.goButton.setVisibility(View.VISIBLE);
				} else {
					holder.goButton.setVisibility(View.GONE);
				}
			}else {
				if(record.orderFeeType==1){
					holder.goButton.setVisibility(View.VISIBLE);
					if(record.getStartDate()!=null&&record.getEndDate()==null){
						holder.starticon.setVisibility(View.VISIBLE);
					}else{
						holder.starticon.setVisibility(View.GONE);
					}
				}else{
					holder.goButton.setVisibility(View.GONE);
				}
			}

			
			if(activity!=null&&activity instanceof AdminHomeActivity){
				holder.paytype.setText(((AdminHomeActivity)activity).getpaymoney(record));
			}
			
			holder.goButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Bimp.temporder = record;
					if (SessionUtils.loginUser != null
							&& SessionUtils.loginUser.parkInfoAdm != null&&Bimp.temporder!=null)
					{
						//查询价格
					scanForOut(record.getOrderId(),SessionUtils.loginUser.parkInfoAdm.parkId);
//					CheckoutDialogFragment dialog2 = new CheckoutDialogFragment(
//							record.getParkInfo().parkId, record.getOrderId());
//					dialog2.show(getFragmentManager(), "FabuDialogFragment");
					}

				}

			});

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

	
	/**
	 * 结账放行确认
	 * 
	 * @param orderid
	 * @param scanResult
	 */
	private void scanForOut(long orderid, long parkId) {

		HttpRequestAni<ComResponse<ChebolePayOptions>> httpRequestAni = new HttpRequestAni<ComResponse<ChebolePayOptions>>(
				getActivity(), "/a/pay/findpay/" + orderid + "/"
						+ parkId,
				new TypeToken<ComResponse<ChebolePayOptions>>() {
				}.getType()) {

			@Override
			public void callback(ComResponse<ChebolePayOptions> arg0) {
				 ChebolePayOptions priceEntity = new ChebolePayOptions();
				if (arg0 != null) {
					priceEntity = arg0.getResponseEntity();
					if(priceEntity!=null&&priceEntity.payActualPrice>0)
					{
						CheckoutDialogFragment dialog2 = new CheckoutDialogFragment(
								Bimp.temporder.getParkInfo().parkId, Bimp.temporder.getOrderId(),priceEntity.payActualPrice);
				        dialog2.show(getFragmentManager(), "FabuDialogFragment");
						
					}
				} else {
					Toast.makeText(getActivity(),
							"没有获取到任何信息", Toast.LENGTH_LONG).show();
					return;
				}

				if (arg0.getResponseStatus() == ComResponse.STATUS_FAIL) {

					String flag = arg0.getExtendResponseContext();
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							getActivity());

					if (flag != null && flag.equals("pass")) {

						alertDialog.setTitle("完成订单");
						alertDialog.setNegativeButton("未产生费用，可以放行",
							null);
					} else {
						alertDialog.setTitle("提示");
						alertDialog.setNegativeButton("关闭", null);
					}
					alertDialog.setMessage(arg0.getErrorMessage());

					alertDialog.create().show();
				}
			}
		};

		httpRequestAni.execute();

	}
	
	
}