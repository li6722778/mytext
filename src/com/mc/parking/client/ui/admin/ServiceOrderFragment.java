package com.mc.parking.client.ui.admin;

import java.util.ArrayList;
import java.util.Date;
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

import com.android.volley.Request.Method;
import com.google.gson.reflect.TypeToken;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.client.Constants;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.ChebolePayOptions;
import com.mc.parking.client.entity.OrderEntity;
import com.mc.parking.client.entity.TOrder_Py;
import com.mc.parking.client.entity.TParkInfo_Py;
import com.mc.parking.client.entity.TParkService;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.BaseDialogFragment;
import com.mc.parking.client.layout.PullToRefreshListView;
import com.mc.parking.client.layout.PullToRefreshListView.IXListViewListener;
import com.mc.parking.client.layout.PullToRefreshListView.OnRefreshListener;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.CommFindEntity;
import com.mc.parking.client.layout.net.HttpRequest;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.MainActivity;
import com.mc.parking.client.ui.OrderActivity;
import com.mc.parking.client.ui.OrderDetailActivity;
import com.mc.parking.client.ui.admin.ServiceOrderFragment.PullToRefreshListViewAdapter.ViewHolder;
import com.mc.parking.client.utils.Notice;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;

public class ServiceOrderFragment extends Fragment implements IXListViewListener {
	private PullToRefreshListView listView;
	private PullToRefreshListViewAdapter adapter;
	private Activity activity;
	private LinearLayout progress;
	private OrderDetailFragment orderDetailFragment;
	List<TOrder_Py> listalldata = new ArrayList<TOrder_Py>();
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
		if (Constants.NETFLAG) {
			reload();
		}
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_admin_order_listview, container, false);
		activity = getActivity();
		listView = (PullToRefreshListView) view.findViewById(R.id.pull_to_refresh_listview);
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
		// Register the context menu for actions
		registerForContextMenu(listView);

		return view;
	}

	public void getremotedata(int count) {
		String urlString = "";

		if (SessionUtils.loginUser != null && SessionUtils.loginUser.parkInfoAdm != null) {
			HttpRequestAni<CommFindEntity<TOrder_Py>> httpRequestAni = new HttpRequestAni<CommFindEntity<TOrder_Py>>(
					activity, "/a/service/pending/" + SessionUtils.loginUser.parkInfoAdm.parkId + "?p=" + count,
					new TypeToken<CommFindEntity<TOrder_Py>>() {
					}.getType()) {

				@Override
				public void callback(CommFindEntity<TOrder_Py> arg0) {

					if (arg0 == null) {
						Toast.makeText(getActivity(), "加载出错，请重新加载", Toast.LENGTH_SHORT).show();
						return;
					}

					if (getActivity() != null && getActivity() instanceof AdminServiceActivity) {
						((AdminServiceActivity) getActivity()).setDisplayTotal(arg0.getRowCount());
					}

					if (arg0.getRowCount() > 0) {

						if (datacount == 0 && listalldata != null)
							listalldata.clear();
						List<TOrder_Py> templistdata = arg0.getResult();
						for (TOrder_Py tempitem : templistdata) {

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

		private ArrayList<TOrder_Py> items = new ArrayList<TOrder_Py>();;

		public class ViewHolder {
			public String id;
			public TextView orderName;
			public TextView useid;;
			public TextView orderDate;
			public Button okbtn;
			public Button rejectbtn;
			public ImageView starticon;
			public TextView paytype;
			TextView servicename;
	

		}

		public void getdata(List<TOrder_Py> mydata) {
			this.items = (ArrayList<TOrder_Py>) mydata;
		}

		/**
		 * Loads the data.
		 */
		public void loadData(List<TOrder_Py> remoteData) {

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

			final TOrder_Py record = (TOrder_Py) getItem(position);

			LayoutInflater inflater = activity.getLayoutInflater();

			final ViewHolder viewHolder = new ViewHolder();

			if (convertView == null) {
				rowView = inflater.inflate(R.layout.item_admin_list_service, null);

				viewHolder.orderName = (TextView) rowView.findViewById(R.id.order_string);
				viewHolder.useid = (TextView) rowView.findViewById(R.id.order_userid);

				viewHolder.orderDate = (TextView) rowView.findViewById(R.id.order_item_date);

				viewHolder.okbtn = (Button) rowView.findViewById(R.id.order_accept);
				viewHolder.rejectbtn = (Button) rowView.findViewById(R.id.order_reject);
				viewHolder.paytype = (TextView) rowView.findViewById(R.id.order_item_type);
				viewHolder.servicename = (TextView) rowView.findViewById(R.id.servicename);
			

				rowView.setTag(viewHolder);
			}

			final ViewHolder holder = (ViewHolder) rowView.getTag();

			TuserInfo userinfo = record.order.getUserInfo();
			holder.orderName.setText("" + record.order.getOrderId()+" 服务号:"+record.parkPyId);
			holder.useid.setText("车主：" + userinfo.userPhone);
			holder.orderDate.setText(UIUtils.formatDate(activity, record.order.getOrderDate().getTime()));
			holder.servicename.setText(record.paymentName);

			if (record.paymentStatus == 2) {
				holder.okbtn.setVisibility(View.VISIBLE);
				holder.rejectbtn.setVisibility(View.VISIBLE);
			} else {
				holder.okbtn.setVisibility(View.GONE);
				holder.rejectbtn.setVisibility(View.GONE);
			}

			/*
			 * if(activity!=null&&activity instanceof AdminHomeActivity){
			 * holder.paytype.setText(((AdminHomeActivity)activity).getpaymoney(
			 * record)); }
			 */

			holder.rejectbtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setTitle("");
					builder.setMessage("是否放弃该服务");
					builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							updateservice(record.parkPyId, Constants.SERVICE_REJECT);
						}
					});
					builder.setNegativeButton("取消",null);
					builder.show();
					//updateservice(record.parkPyId, Constants.SERVICE_REJECT);
				}
			});

			holder.okbtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					

					// TODO Auto-generated method stub
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setTitle("");
					builder.setMessage("是否确认完成");
					builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							updateservice(record.parkPyId, Constants.SERVICE_FINISH);
						}
					});
					builder.setNegativeButton("取消",null);
					builder.show();
					//updateservice(record.parkPyId, Constants.SERVICE_REJECT);
				
					
					

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
				getActivity(), "/a/pay/findpay/" + orderid + "/" + parkId,
				new TypeToken<ComResponse<ChebolePayOptions>>() {
				}.getType()) {

			@Override
			public void callback(ComResponse<ChebolePayOptions> arg0) {
				ChebolePayOptions priceEntity = new ChebolePayOptions();
				if (arg0 != null) {
					priceEntity = arg0.getResponseEntity();
					if (priceEntity != null && priceEntity.payActualPrice > 0) {
						CheckoutDialogFragment dialog2 = new CheckoutDialogFragment(Bimp.temporder.getParkInfo().parkId,
								Bimp.temporder.getOrderId(), priceEntity.payActualPrice);
						dialog2.show(getFragmentManager(), "FabuDialogFragment");

					}
				} else {
					Toast.makeText(getActivity(), "没有获取到任何信息", Toast.LENGTH_LONG).show();
					return;
				}

				if (arg0.getResponseStatus() == ComResponse.STATUS_FAIL) {

					String flag = arg0.getExtendResponseContext();
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

					if (flag != null && flag.equals("pass")) {

						alertDialog.setTitle("完成订单");
						alertDialog.setNegativeButton("未产生费用，可以放行", null);
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

	void updateservice(Long payid, final int mode) {

		// 下面是更新订单状态mode 1完成3.拒绝
		HttpRequest<ComResponse<TParkService>> httpRequestAni = new HttpRequest<ComResponse<TParkService>>(Method.POST,
				null, "/a/service/status/update/" + payid + "?s=" + mode, new TypeToken<ComResponse<TParkService>>() {
				}.getType(), TParkService.class) {

			@Override
			public void onSuccess(ComResponse<TParkService> arg0) {
				if (arg0 != null) {
					String resultstring="";
					if(mode==Constants.SERVICE_FINISH)
					{
						resultstring="完成服务";
					}else
					{
						resultstring="取消服务";
					}
					reload();
					Toast.makeText(getActivity(), resultstring, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailed(String message) {
				Toast.makeText(getActivity(), "异常：" + message, Toast.LENGTH_LONG).show();
			}
		};

		httpRequestAni.execute();

		// if (paystatus == Constants.PAYMENT_STATUS_FINISH
		// || paystatus == Constants.PAYMENT_STATUS_PENDING) {
		//
		// AlertDialog.Builder alertDialog = new AlertDialog.Builder(
		// OrderDetailActivity.this);
		// alertDialog
		// .setTitle(paystatus == Constants.PAYMENT_STATUS_FINISH ? "支付成功"
		// : "支付确认中");
		// alertDialog
		// .setMessage(paystatus == Constants.PAYMENT_STATUS_FINISH ?
		// "谢谢您的光临,请离场"
		// : "当前支付接口正在处理,请等待...");
		//
		// if (paystatus == Constants.PAYMENT_STATUS_FINISH) {
		// orderinfo.setOrderStatus(Constants.ORDER_TYPE_FINISH);
		// orderinfo.setEndDate(new Date());// 这里应该取服务端时间。。。
		// //setProgressStatus();
		// fetchLatestOrder();
		// alertDialog.setNegativeButton("返回订单列表",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// showPopwindow();
		// }
		// });
		// } else {
		// alertDialog.setNegativeButton("刷新",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// fetchLatestOrder();
		// }
		// });
		// }
		//
		// alertDialog.create().show();
		// } else {
		// Toast.makeText(getApplicationContext(), "支付未进行,您可以重新为此订单进行支付",
		// Toast.LENGTH_LONG).show();
		// }

	}

}