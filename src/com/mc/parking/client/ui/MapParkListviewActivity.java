package com.mc.parking.client.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.reflect.TypeToken;
import com.mc.parking.client.Constants;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.CouponBean;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.layout.ActionBaseActivity;
import com.mc.parking.client.layout.PullToRefreshListView;
import com.mc.parking.client.layout.PullToRefreshListView.IXListViewListener;
import com.mc.parking.client.layout.PullToRefreshListView.OnRefreshListener;
import com.mc.parking.client.layout.net.CommFindEntity;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.utils.UIUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MapParkListviewActivity extends ActionBaseActivity implements
		IXListViewListener {

	PullToRefreshListView listview;
	private PullToRefreshListViewAdapter adapter;
	List<TParkInfo_LocEntity> mydata = new ArrayList<TParkInfo_LocEntity>();
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	int CURRENT_PAGE = 0;
	int TOTALE_PAGE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_mapparklist);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_topbar);
		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		titleView.setText("附近门店");
		listview = (PullToRefreshListView) findViewById(R.id.history_pull_to_refresh_listview);
		listview.setPullLoadEnable(false);
	listview.setPullRefreshEnable(true);
		adapter = new PullToRefreshListViewAdapter() {
		};
		listview.setAdapter(adapter);
		listview.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				CURRENT_PAGE = 0;
				load();
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(adapter!=null&&adapter.getItem(position)!=null)
				{
					try {
						Intent intent = new Intent(getApplicationContext(),
								ParkActivity.class);
						Bundle buidle = new Bundle();
						if(adapter.getItem(position)==null)
						{
							return;
						}else
						{
						TParkInfo_LocEntity temp=(TParkInfo_LocEntity)adapter.getItem(position);
						buidle.putSerializable("parkinfo",temp);
						intent.putExtras(buidle);
						startActivity(intent);
						}
					} catch (Exception e) {
						// TODO: handle exception
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
					
					}
				}
				
			}
		});
		listview.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub

				// TODO Auto-generated method stub
				if (CURRENT_PAGE > TOTALE_PAGE - 2) {
				
					Toast.makeText(getApplicationContext(), "没有更多数据", Toast.LENGTH_SHORT).show();
					listview.setPullLoadEnable(false);
					return;

				} else
				{
					CURRENT_PAGE++;
					load();
				} 
			}
		});
    // Toast.makeText(getApplicationContext(), ""+UIUtils.currentlatlng.latitude, Toast.LENGTH_SHORT).show();
		if(UIUtils.currentlatlng!=null)
		getParkListDate(UIUtils.currentlatlng.latitude,UIUtils.currentlatlng.longitude);
		else
		{
			Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		if (CURRENT_PAGE > TOTALE_PAGE - 1) {
			listview.stopLoadMore();
			Toast.makeText(getApplicationContext(), "没有更多数据", Toast.LENGTH_SHORT).show();
			listview.setPullLoadEnable(false);
			return;

		} else
		{
			CURRENT_PAGE++;
			load();
		} 
	}

	public abstract class PullToRefreshListViewAdapter extends
			android.widget.BaseAdapter {

		private ArrayList<CouponBean> items = new ArrayList<CouponBean>();
		ViewHolder viewHolder;
		List<TParkInfo_LocEntity> mydata = new ArrayList<TParkInfo_LocEntity>();

		// 0--鏈湴鏁版嵁搴撶殑缁戝畾1--缃戠粶鏁版嵁搴撶殑缁戝畾

		public void getdata(List<TParkInfo_LocEntity> mydata) {
			this.mydata = mydata;

		}

		/**
		 * Loads the data.
		 */
		public void loadMore(List<TParkInfo_LocEntity> mydata) {
			this.mydata.addAll(mydata);
			// MANDATORY: Notify that the data has changed
			notifyDataSetChanged();
		}

		public void loadData(List<TParkInfo_LocEntity> mydata) {

			this.mydata.clear();
			this.mydata.addAll(0, mydata);

			// MANDATORY: Notify that the data has changed
			notifyDataSetChanged();
		}
		
		
		public class ViewHolder {
			public String id;
			public TextView parkname;
			public TextView paymode;
			public TextView distance;
			public TextView detail;
			public ImageView image;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mydata.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			View rowView = convertView;

			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.item_map_parklist, null);
				viewHolder.image=(ImageView) convertView
						.findViewById(R.id.addhistory_img);
				viewHolder.parkname = (TextView) convertView
						.findViewById(R.id.parkname_string);
				
				viewHolder.paymode = (TextView) convertView
						.findViewById(R.id.txt_pay_mode);
				viewHolder.distance = (TextView) convertView
						.findViewById(R.id.txt_distance);
				viewHolder.detail = (TextView) convertView
						.findViewById(R.id.txt_detail);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();

			}

			if (mydata.get(position).parkInfo != null) {
				if (mydata.get(position).parkInfo.imgUrlArray != null
						&& mydata.get(position).parkInfo.imgUrlArray.size() > 0) {

					imageLoader
							.displayImage(
									mydata.get(position).parkInfo.imgUrlArray
											.get(0).imgUrlHeader
											+ mydata.get(position).parkInfo.imgUrlArray
													.get(0).imgUrlPath,
									viewHolder.image);
					

				}

				viewHolder.parkname
						.setText(mydata.get(position).parkInfo.parkname);

				if (mydata.get(position).parkInfo.feeType == 2) {
					viewHolder.paymode.setText("计时");
				} else {
					viewHolder.paymode.setText("计次");
				}
				viewHolder.detail.setText(mydata.get(position).parkInfo.detail);
				viewHolder.distance.setText(" 距离"
						+ mydata.get(position).distance + "米");
				
			}

			return convertView;

		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		UIUtils.tempParkinfo=null;
		
	}
	
	public void getParkListDate(final double lantitude,
			final double longitude)
	{
//		HttpRequest<List<TParkInfo_LocEntity>> httpRequestAni = new HttpRequest<List<TParkInfo_LocEntity>>(
//				"/a/ans?a=" + lantitude + "&n=" + longitude,
//				new TypeToken<List<TParkInfo_LocEntity>>() {
//				}.getType()) {
//			@Override
//			public void onSuccess(List<TParkInfo_LocEntity> infos) {
//
//				if (infos != null) {
//					LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
//					for(TParkInfo_LocEntity info:infos)
//					{		
//					LatLng markLatLng = new LatLng(info.latitude,
//							info.longitude);
//
//					double distance = DistanceUtil.getDistance(ll, markLatLng);
//					int distance2 = Integer.parseInt(new java.text.DecimalFormat("0")
//							.format(distance));
//					info.distance=distance2;
//					}
//
//				UIUtils.tempParkinfo=infos;
//				Intent intent=new Intent();
//				 intent.setClass(getActivity(), MapParkListviewActivity.class);
//				 startActivity(intent);
//					
//				}
//				
//			}
//
//			@Override
//			public void onFailed(String message) {
//				Log.e("checkVersion", message);
//				
//			}
//
//		};
//
//		httpRequestAni.execute();
		HttpRequestAni<CommFindEntity<TParkInfo_LocEntity>> httpRequestAni = new HttpRequestAni<CommFindEntity<TParkInfo_LocEntity>>(
				MapParkListviewActivity.this,"/a/ans/nearby/pagelist?a=" + lantitude + "&n=" + longitude+"&p="+CURRENT_PAGE,
				new TypeToken<CommFindEntity<TParkInfo_LocEntity>>() {
				}.getType()){
		
				@Override
				public void callback(CommFindEntity<TParkInfo_LocEntity> arg0) {
					
					listview.stopLoadMore();
					if (arg0 != null&&arg0.getRowCount()>0) {
						TOTALE_PAGE = arg0.getPageCount();
						
						if (arg0.getPageCount() <= 1) {
							listview.setPullLoadEnable(false);
						} else {
							listview.setPullLoadEnable(true);
						}
						mydata=arg0.getResult();
						
						
						
						
					LatLng ll = new LatLng(UIUtils.currentlatlng.latitude, UIUtils.currentlatlng.longitude);
					for(TParkInfo_LocEntity info:mydata)
					{		
					LatLng markLatLng = new LatLng(info.latitude,
							info.longitude);

					double distance = DistanceUtil.getDistance(ll, markLatLng);
					int distance2 = Integer.parseInt(new java.text.DecimalFormat("0")
							.format(distance));
					info.distance=distance2;
					}

				
					
				} else if (arg0.getRowCount() == 0) {
					if (mydata != null)
						mydata.clear();
				}
				if (mydata == null || mydata.size() <= 0) {
					Toast.makeText(MapParkListviewActivity.this,
							"没有更多数据",
							Toast.LENGTH_LONG).show();
					
					listview.onRefreshComplete();

					return;

				}
				if (mydata.size() > 0) {
					if (CURRENT_PAGE == 0)
						adapter.loadData(mydata);
					else
						adapter.loadMore(mydata);
				}
				//鏄剧ず鍒嗛〉淇℃伅
				UIUtils.displayPaginationInfo(MapParkListviewActivity.this, CURRENT_PAGE, Constants.PAGINATION_PAGESIZE, arg0.getRowCount() );
				if (listview.isRefreshing()) {
					listview.onRefreshComplete();

				}

				
			}
			
			
	};
	
	httpRequestAni.execute();
	}
	
	public void load()
	{
	
		if(UIUtils.currentlatlng!=null)
			getParkListDate(UIUtils.currentlatlng.latitude,UIUtils.currentlatlng.longitude);
			else
			{
				Toast.makeText(getApplicationContext(), "加载出错", Toast.LENGTH_SHORT).show();
				finish();
			}
		
	}
}
