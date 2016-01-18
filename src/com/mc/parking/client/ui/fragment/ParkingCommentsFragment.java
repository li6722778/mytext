package com.mc.parking.client.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.navisdk.model.params.MsgDefine;
import com.google.gson.reflect.TypeToken;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.OrderEntity;
import com.mc.parking.client.entity.ParkCommentsEntity;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.layout.PullToRefreshListView;
import com.mc.parking.client.layout.PullToRefreshListView.IXListViewListener;
import com.mc.parking.client.layout.PullToRefreshListView.OnRefreshListener;
import com.mc.parking.client.layout.net.CommFindEntity;
import com.mc.parking.client.layout.net.HttpRequest;
import com.mc.parking.client.ui.ParkActivity;
import com.mc.parking.client.ui.UploadPhotoActivity;
import com.mc.parking.client.utils.Notice;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;


public class ParkingCommentsFragment extends android.support.v4.app.Fragment
		implements IXListViewListener {

	private PullToRefreshListView listView;
	private PullToRefreshListViewAdapter adapter;
	private LinearLayout progress;
	private ParkActivity parkActivity;
	int currentcount = 1;
	int totalcount = 0;
	TParkInfo_LocEntity tParkInfo_LocEntity;
	public View view;
	private ImageButton nocommentsbutton;
	private RelativeLayout shownocommentslaLayout;

	private int total;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_packdetail_comments,
				container, false);
		parkActivity = (ParkActivity) getActivity();
		tParkInfo_LocEntity = parkActivity.getParkInfo();
		initlistview(view, tParkInfo_LocEntity);
		progress = (LinearLayout) view.findViewById(R.id.progressContainer);
		nocommentsbutton = (ImageButton) view
				.findViewById(R.id.nocommentsbutton);
		shownocommentslaLayout = (RelativeLayout) view
				.findViewById(R.id.shownocommentsbutton);
		nocommentsbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				parkActivity.showCommentsPopup();
			}
		});

		// Register the context menu for actions
		registerForContextMenu(listView);

		return view;
	}

	private void initlistview(View view,
			final TParkInfo_LocEntity tParkInfo_LocEntity) {

		listView = (PullToRefreshListView) view
				.findViewById(R.id.pull_to_refresh_listview);
	    listView.setPullRefreshEnable(true);
		adapter = new PullToRefreshListViewAdapter() {};
		getfirstdata(tParkInfo_LocEntity);
		listView.setXListViewListener(this);
		listView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				getfirstdata(tParkInfo_LocEntity);
				listView.onRefreshComplete();
			}
		});

	}

	@Override
	public void onLoadMore() {
		onloadMore(tParkInfo_LocEntity);

	}

	public abstract class PullToRefreshListViewAdapter extends
			android.widget.BaseAdapter {
		List<ParkCommentsEntity> data = new ArrayList<ParkCommentsEntity>();

		public class ViewHolder {
			public String id;
			public TextView park_owner;
			public RatingBar ratingBar;
			public TextView comments;
			public TextView logDate;
		}

		/**
		 * Loads the data.
		 */
		public void loadData(List<ParkCommentsEntity> remoteData) {

			data.clear();
			data.addAll(0, remoteData);
			// MANDATORY: Notify that the data has changed
			notifyDataSetChanged();
		}

		public void loadMore(List<ParkCommentsEntity> remoteData) {
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

			ParkCommentsEntity record = (ParkCommentsEntity) getItem(position);

			LayoutInflater inflater = parkActivity.getLayoutInflater();

			ViewHolder viewHolder = new ViewHolder();

			if (convertView == null) {
				rowView = inflater.inflate(R.layout.item_list_parkcomments,
						null);

				viewHolder.park_owner = (TextView) rowView
						.findViewById(R.id.park_owner);

				viewHolder.ratingBar = (RatingBar) rowView
						.findViewById(R.id.ratingBar);

				viewHolder.comments = (TextView) rowView
						.findViewById(R.id.park_comments_text);

				viewHolder.logDate = (TextView) rowView
						.findViewById(R.id.park_comments_date);

				rowView.setTag(viewHolder);
			}

			final ViewHolder holder = (ViewHolder) rowView.getTag();

			if (record != null) {
				holder.comments.setText(record.getComments());
				holder.logDate.setText(record.getCreateDate()==null?"":UIUtils.formatDate(parkActivity, record
						.getCreateDate().getTime()));
				holder.park_owner.setText(getAnonymous(record.getCreatePerson()));
				holder.ratingBar.setRating(record.getRating());
			}
			return rowView;
		}
	}
	
	
	public String getAnonymous(String owner){
		StringBuilder username = new StringBuilder(owner);
		if (username.length() > 10) {
			char ch = '*';
			username.setCharAt(9, ch);
			username.setCharAt(10, ch);
			username.setCharAt(11, ch);
	   }
		return username.toString();
	}
	


	public void getfirstdata(TParkInfo_LocEntity tParkInfo_LocEntity) {
		HttpRequest<CommFindEntity<ParkCommentsEntity>> httpRequestAni = new HttpRequest<CommFindEntity<ParkCommentsEntity>>(
				"/a/comments/" + tParkInfo_LocEntity.parkInfo.parkId,
				new TypeToken<CommFindEntity<ParkCommentsEntity>>() {
				}.getType()) {

			@Override
			public void onSuccess(CommFindEntity<ParkCommentsEntity> arg0) {
				if (arg0 == null) {
					Message msg = new Message();
					msg.what = 0;
					showhandler.sendMessage(msg);

				}
				if (arg0.getRowCount() > 0) {
					// 如果返回的不为空并且总数大于0
					// 有数据的时候处理
					Message msg = new Message();
					msg.what = 1;
					showhandler.sendMessage(msg);
					currentcount = 1;
					total = arg0.getRowCount();
					parkActivity.changetotalcomments(total);
					totalcount = arg0.getPageCount();
					if (arg0.getPageCount() >1){
					 listView.setPullLoadEnable(true);
					}
					else 
						listView.setPullLoadEnable(false);

					adapter.loadData(arg0.getResult());
				} else if (arg0.getRowCount() == 0) {
					Message msg = new Message();
					msg.what = 0;
					showhandler.sendMessage(msg);
					adapter.loadData(new ArrayList<ParkCommentsEntity>());
				}

				listView.setAdapter(adapter);
			}

			@Override
			public void onFailed(String message) {
				Message msg = new Message();
				msg.what = 1;
				showhandler.sendMessage(msg);
				Toast.makeText(getActivity(), "[异常]", Toast.LENGTH_SHORT)
						.show();
			}

		};

		httpRequestAni.execute();

	}

	public void onloadMore(TParkInfo_LocEntity tParkInfo_LocEntity) {

		if (currentcount > totalcount - 1) {
			listView.stopLoadMore();
			Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
			listView.setPullLoadEnable(false);
			return;

		}

		HttpRequest<CommFindEntity<ParkCommentsEntity>> httpRequestAni = new HttpRequest<CommFindEntity<ParkCommentsEntity>>(
				"/a/comments/" + tParkInfo_LocEntity.parkInfo.parkId + "?p="
						+ currentcount,
				new TypeToken<CommFindEntity<ParkCommentsEntity>>() {
				}.getType()) {

			@Override
			public void onSuccess(CommFindEntity<ParkCommentsEntity> arg0) {

				if (arg0 == null) {
					listView.stopLoadMore();
					return;
				}
				if (arg0.getRowCount() > 0) {
					// 如果返回的不为空并且总数大于0
					// 有数据的时候处理
					totalcount = arg0.getPageCount();
					List<ParkCommentsEntity> templistdata = arg0.getResult();

					adapter.loadMore(templistdata);
					adapter.notifyDataSetChanged();

				} else if (arg0.getRowCount() == 0) {
					listView.stopLoadMore();
					return;

				}

				listView.stopLoadMore();
				currentcount++;
			}

			@Override
			public void onFailed(String message) {

				Toast.makeText(getActivity(), "[异常]", Toast.LENGTH_SHORT)
						.show();

			}

		};
		httpRequestAni.execute();

	}

	@Override
	public void onRefresh() {
//		getfirstdata(tParkInfo_LocEntity);
		listView.onRefreshComplete();

	}

	private Handler showhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				if(SessionUtils.isLogined()){
					nocommentsbutton.setEnabled(true);
				}
				else{
					nocommentsbutton.setEnabled(false);
				}
				nocommentsbutton.setVisibility(View.VISIBLE);
				shownocommentslaLayout.setVisibility(View.VISIBLE);
				
				break;
			case 1:
				nocommentsbutton.setVisibility(View.GONE);
				shownocommentslaLayout.setVisibility(View.GONE);
				break;
			default:

				break;
			}
		}
	};
}
