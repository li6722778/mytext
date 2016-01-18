package com.mc.parking.client.ui.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.mc.parking.client.Constants;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.TakeCashEntity;
import com.mc.parking.client.layout.PullToRefreshListView;
import com.mc.parking.client.layout.PullToRefreshListView.IXListViewListener;
import com.mc.parking.client.layout.PullToRefreshListView.OnRefreshListener;
import com.mc.parking.client.layout.net.CommFindEntity;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.TakecashActivity;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TakecashhistoryFragment extends android.support.v4.app.Fragment
		implements IXListViewListener {

	private PullToRefreshListView listView;
	private PullToRefreshListViewAdapter adapter;
	private LinearLayout progress;
	private TakecashActivity takecashActivity;
	List<TakeCashEntity> okdata = null;
	SimpleDateFormat myFmt3 = new SimpleDateFormat("yyyy-MM-dd");
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	int CURRENT_PAGE = 0;
	int TOTALE_PAGE = 0;
	private ArrayList<TakeCashEntity> olderitem = new ArrayList<TakeCashEntity>();;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_takecashhistroy,
				container, false);
		takecashActivity = (TakecashActivity) getActivity();
		/* final ParkInfo parkInfo= new ParkInfo(); */
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_empty).cacheInMemory(true)
				.cacheOnDisc(false).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		listView = (PullToRefreshListView) view
				.findViewById(R.id.pull_to_refresh_listviews);
		listView.setPullLoadEnable(false);
		listView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				if (TOTALE_PAGE - 1 > CURRENT_PAGE) {
					CURRENT_PAGE++;
					getremoteData();

				} else {
					listView.stopLoadMore();
					listView.setPullLoadEnable(false);
					Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT)
							.show();

				}
			}
		});

		progress = (LinearLayout) view.findViewById(R.id.progressContainers);

		listView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				load();
			}
		});

		adapter = new PullToRefreshListViewAdapter() {
		};
		listView.setAdapter(adapter);

		// Register the context menu for actions
		registerForContextMenu(listView);
		//getremoteData();

		return view;
	}
	
	public void load()
	{
		CURRENT_PAGE = 0;
		getremoteData();
		
	}

	public abstract class PullToRefreshListViewAdapter extends
			android.widget.BaseAdapter {

		private ArrayList<TakeCashEntity> items = new ArrayList<TakeCashEntity>();;

		public class ViewHolder {
			public String id;
			public TextView orderid;
			public TextView cash;
			public TextView askDate;
			public TextView handleDate;
			public TextView overDate;
			public ImageView askimage, handleimage, overimage;

		}

		/**
		 * Loads the data.
		 */
		public void loadMore(List<TakeCashEntity> remoteData) {
			items.addAll(remoteData);
			// MANDATORY: Notify that the data has changed
			notifyDataSetChanged();
		}

		public void loadData(List<TakeCashEntity> remoteData) {

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

			TakeCashEntity record = (TakeCashEntity) getItem(position);

			LayoutInflater inflater = takecashActivity.getLayoutInflater();

			ViewHolder viewHolder = new ViewHolder();

			if (convertView == null) {
				rowView = inflater.inflate(R.layout.item_list_takecashhistroy,
						null);

				viewHolder.orderid = (TextView) rowView
						.findViewById(R.id.order_item_number);
				viewHolder.cash = (TextView) rowView
						.findViewById(R.id.order_cash);

				viewHolder.askDate = (TextView) rowView
						.findViewById(R.id.ask_date);
				viewHolder.handleDate = (TextView) rowView
						.findViewById(R.id.handle_date);
				viewHolder.overDate = (TextView) rowView
						.findViewById(R.id.over_date);

				viewHolder.askimage = (ImageView) rowView
						.findViewById(R.id.ask_image);
				viewHolder.handleimage = (ImageView) rowView
						.findViewById(R.id.handle_image);
				viewHolder.overimage = (ImageView) rowView
						.findViewById(R.id.over_image);

				rowView.setTag(viewHolder);
			}

			ViewHolder holder = (ViewHolder) rowView.getTag();

			holder.orderid.setText("" + record.takecashid);

			holder.cash.setText("" + record.takemoney);
			if (record.status == Constants.TAKE_MONEY_ASK) {
				if (record.askdata != null)
					holder.askDate.setText(myFmt3.format(record.askdata));
				else
					holder.askDate.setText("");
				holder.handleDate.setText("");
				holder.overDate.setText("");

				imageLoader.displayImage("drawable://"
						+ R.drawable.cashprocessgrey, holder.handleimage,
						options);
				imageLoader.displayImage("drawable://"
						+ R.drawable.cashsucessgrey, holder.overimage, options);
			} else if (record.status == Constants.TAKE_MONEY_HANDLE) {
				if (record.askdata != null && record.bankHandleData != null) {
					holder.askDate.setText(myFmt3.format(record.askdata));
					holder.handleDate.setText(myFmt3
							.format(record.bankHandleData));
				} else {
					holder.askDate.setText("");
					holder.handleDate.setText("");

				}
				holder.overDate.setText("");

				imageLoader.displayImage(
						"drawable://" + R.drawable.cashprocess,
						holder.handleimage, options);
				imageLoader.displayImage("drawable://"
						+ R.drawable.cashsucessgrey, holder.overimage, options);

			} else if (record.status == Constants.TAKE_MONEY_COMPLETE) {
				if (record.askdata != null && record.bankHandleData != null
						&& record.okData != null) {
					holder.askDate.setText(myFmt3.format(record.askdata));
					holder.handleDate.setText(myFmt3
							.format(record.bankHandleData));
					holder.handleDate.setText(myFmt3.format(record.okData));

				} else {
					holder.askDate.setText("");
					holder.handleDate.setText("");
					holder.overDate.setText("");

				}

				imageLoader.displayImage(
						"drawable://" + R.drawable.cashprocess,
						holder.handleimage, options);
				imageLoader.displayImage("drawable://" + R.drawable.cashsucess,
						holder.overimage, options);

			}
			return rowView;
		}
	}

	void getremoteData() {
		String pagestring = "";
		if (CURRENT_PAGE > 0) {
			pagestring = "?p=" + CURRENT_PAGE;

		}

		if(SessionUtils.loginUser!=null&&SessionUtils.loginUser.parkInfoAdm!=null&&SessionUtils.loginUser.parkInfoAdm.parkId!=null){
		HttpRequestAni<CommFindEntity<TakeCashEntity>> httpRequestAni = new HttpRequestAni<CommFindEntity<TakeCashEntity>>(
				getActivity(), "/a/takecash/"
						+ SessionUtils.loginUser.parkInfoAdm.parkId
						+ pagestring,
				new TypeToken<CommFindEntity<TakeCashEntity>>() {
				}.getType()) {

			@Override
			public void callback(CommFindEntity<TakeCashEntity> arg0) {

				if (arg0 == null)
					return;
				if (arg0.getRowCount() > 0) {
					TOTALE_PAGE = arg0.getPageCount();

					if (arg0.getPageCount() <= 1) {
						listView.setPullLoadEnable(false);
					} else {
						listView.setPullLoadEnable(true);
					}
					okdata = arg0.getResult();

				} else if (arg0.getRowCount() == 0) {
					if (okdata != null)
						okdata.clear();
				}
				if (okdata == null || okdata.size() <= 0) {
					Toast.makeText(getActivity(),
							"没有记录",
							Toast.LENGTH_LONG).show();
					
					listView.onRefreshComplete();

					return;

				}
				if (okdata.size() > 0) {
					if (CURRENT_PAGE == 0)
						adapter.loadData(okdata);
					else
						adapter.loadMore(okdata);
				}
				//显示分页信息
				UIUtils.displayPaginationInfo(getActivity(), CURRENT_PAGE, Constants.PAGINATION_PAGESIZE, arg0.getRowCount() );
				if (listView.isRefreshing()) {
					listView.onRefreshComplete();

				}
			}

		};

		httpRequestAni.execute();
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

}
