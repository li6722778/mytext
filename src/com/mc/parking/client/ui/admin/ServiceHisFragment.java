package com.mc.parking.client.ui.admin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.mc.parking.client.entity.TIncome;
import com.mc.parking.client.entity.TOrder_Py;
import com.mc.parking.client.entity.TParkInfo_Py;
import com.mc.parking.client.entity.TParkService;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.PullToRefreshListView;
import com.mc.parking.client.layout.PullToRefreshListView.IXListViewListener;
import com.mc.parking.client.layout.PullToRefreshListView.OnRefreshListener;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.CommFindEntity;
import com.mc.parking.client.layout.net.HttpRequest;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.OrderDetailActivity;
import com.mc.parking.client.ui.admin.ServiceHisFragment.PullToRefreshListViewAdapter.ViewHolder;
import com.mc.parking.client.ui.admin.ServiceOrderFragment.PullToRefreshListViewAdapter;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;

public class ServiceHisFragment extends Fragment implements IXListViewListener {
	private PullToRefreshListView listView;
	private PullToRefreshListViewAdapter adapter;
	private Activity activity;
	private LinearLayout progress;
	List<TOrder_Py> listalldata = new ArrayList<TOrder_Py>();
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
		String urlString = "";

		if (SessionUtils.loginUser != null && SessionUtils.loginUser.parkInfoAdm != null) {
			HttpRequestAni<CommFindEntity<TOrder_Py>> httpRequestAni = new HttpRequestAni<CommFindEntity<TOrder_Py>>(
					activity, "/a/service/done/" + SessionUtils.loginUser.parkInfoAdm.parkId + "?p=" + count,
					new TypeToken<CommFindEntity<TOrder_Py>>() {
					}.getType()) {

				@Override
				public void callback(CommFindEntity<TOrder_Py> arg0) {

					if (arg0 == null) {
						Toast.makeText(getActivity(), "加载出错，请重新加载", Toast.LENGTH_SHORT).show();
						return;
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
			public ImageView starticon;
			public TextView paytype;
			TextView servicename;
	        TextView order_status;
	        TextView service_finish;
	        TextView service_concel;
	        

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
				rowView = inflater.inflate(R.layout.item_admin_list_service_his, null);

				viewHolder.orderName = (TextView) rowView.findViewById(R.id.order_string);
				viewHolder.useid = (TextView) rowView.findViewById(R.id.order_userid);

				viewHolder.orderDate = (TextView) rowView.findViewById(R.id.order_item_date);

				
				viewHolder.paytype = (TextView) rowView.findViewById(R.id.order_item_type);
				viewHolder.servicename = (TextView) rowView.findViewById(R.id.servicename);
			viewHolder.service_finish=(TextView) rowView.findViewById(R.id.order_status);
			viewHolder.service_concel=(TextView) rowView.findViewById(R.id.order_status_concel);

				rowView.setTag(viewHolder);
			}

			final ViewHolder holder = (ViewHolder) rowView.getTag();

			TuserInfo userinfo = record.order.getUserInfo();
			holder.orderName.setText("" + record.order.getOrderId()+" 服务号:"+record.parkPyId);
			holder.useid.setText("车主：" + userinfo.userPhone);
			holder.orderDate.setText(UIUtils.formatDate(activity, record.order.getOrderDate().getTime()));
			holder.servicename.setText(record.paymentName);
			if(record.paymentStatus==Constants.SERVICE_FINISH)
			{
				holder.service_finish.setVisibility(View.VISIBLE);
				holder.service_concel.setVisibility(View.GONE);
			}
			else if(record.paymentStatus==Constants.SERVICE_REJECT)
			{
				holder.service_finish.setVisibility(View.GONE);
				holder.service_finish.setVisibility(View.VISIBLE);
			}
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

	}

}