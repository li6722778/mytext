package com.mc.parking.client.ui.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.reflect.TypeToken;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.CouponBean;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.layout.PullToRefreshListView;
import com.mc.parking.client.layout.PullToRefreshListView.IXListViewListener;
import com.mc.parking.client.layout.PullToRefreshListView.OnRefreshListener;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.ParkActivity;
import com.mc.parking.client.utils.UIUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MapParkListFragment extends Fragment
		implements LoaderManager.LoaderCallbacks<List<PoiInfo>>, IXListViewListener {

	PullToRefreshListView listview;
	private PullToRefreshListViewAdapter adapter;
	List<TParkInfo_LocEntity> mydata = new ArrayList<TParkInfo_LocEntity>();
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	ImageButton topbarBack;

	int CURRENT_PAGE = 0;
	int TOTALE_PAGE = 0;
	Comparator<TParkInfo_LocEntity> comparator = new Comparator<TParkInfo_LocEntity>() {

		@Override
		public int compare(TParkInfo_LocEntity lhs, TParkInfo_LocEntity rhs) {
			// TODO Auto-generated method stub
			if (lhs.distance > rhs.distance)
				return 1;
			else if (lhs.distance == rhs.distance)
				return 0;
			else
				return -1;
		}

	};

	// -----------------------------------------------------------------------------------------
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.ac_mapparklist, container, false);

		view = init(view);
		return view;

	}

	private View init(View view) {

		listview = (PullToRefreshListView) view.findViewById(R.id.history_pull_to_refresh_listview);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(true);
		topbarBack = (ImageButton) view.findViewById(R.id.topbarBack);
		topbarBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				FragmentManager manager = getFragmentManager();
				manager.popBackStackImmediate();
				manager.popBackStackImmediate();

			}
		});
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
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				if (adapter != null && adapter.getItem(position) != null) {
					try {
						Intent intent = new Intent(getActivity(), ParkActivity.class);
						Bundle buidle = new Bundle();
						if (adapter.getItem(position) == null) {
							return;
						} else {
							TParkInfo_LocEntity temp = (TParkInfo_LocEntity) adapter.getItem(position);
							if (temp.parkInfo.averagerat <= 0) {
								temp.parkInfo.averagerat = 4;
							}
							buidle.putSerializable("parkinfo", temp);
							intent.putExtras(buidle);
							startActivity(intent);
						}
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();

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

					Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
					listview.setPullLoadEnable(false);
					return;

				} else {
					CURRENT_PAGE++;
					load();
				}
			}
		});
		// Toast.makeText(getApplicationContext(),
		// ""+UIUtils.currentlatlng.latitude, Toast.LENGTH_SHORT).show();
		if (UIUtils.currentlatlng != null)
			getParkListDate(UIUtils.currentlatlng.latitude, UIUtils.currentlatlng.longitude);
		else {
			Toast.makeText(getActivity(), "查询失败", Toast.LENGTH_SHORT).show();

		}

		return view;
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
			Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
			listview.setPullLoadEnable(false);
			return;

		} else {
			CURRENT_PAGE++;
			load();
		}
	}

	public abstract class PullToRefreshListViewAdapter extends android.widget.BaseAdapter {

		private ArrayList<CouponBean> items = new ArrayList<CouponBean>();
		ViewHolder viewHolder;
		List<TParkInfo_LocEntity> mydata = new ArrayList<TParkInfo_LocEntity>();

		// 0--本地数据库的绑定1--网络数据库的绑定

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
			public TextView serviceicon;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mydata.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return this.mydata.get(position);
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
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_map_parklist, null);
				viewHolder.image = (ImageView) convertView.findViewById(R.id.addhistory_img);
				viewHolder.parkname = (TextView) convertView.findViewById(R.id.parkname_string);
				viewHolder.serviceicon = (TextView) convertView.findViewById(R.id.einfo_service);
				viewHolder.paymode = (TextView) convertView.findViewById(R.id.txt_pay_mode);
				viewHolder.distance = (TextView) convertView.findViewById(R.id.txt_distance);
				viewHolder.detail = (TextView) convertView.findViewById(R.id.txt_detail);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();

			}

			if (mydata.get(position).parkInfo != null) {
				if (mydata.get(position).parkInfo.imgUrlArray != null
						&& mydata.get(position).parkInfo.imgUrlArray.size() > 0) {

					imageLoader.displayImage(mydata.get(position).parkInfo.imgUrlArray.get(0).imgUrlHeader
							+ mydata.get(position).parkInfo.imgUrlArray.get(0).imgUrlPath, viewHolder.image);

				}

				viewHolder.parkname.setText(mydata.get(position).parkInfo.parkname);

				if (mydata.get(position).parkInfo.feeType == 2) {
					viewHolder.paymode.setText("计次收费");
				} else {
					viewHolder.paymode.setText("分段收费");
				}
				viewHolder.detail.setText(mydata.get(position).parkInfo.detail);
				viewHolder.distance.setText(" 距离" + mydata.get(position).distance + "米");

			}

			// viewHolder.serviceicon
			if (mydata.get(position).parkInfo != null && mydata.get(position).parkInfo.services != null
					&& mydata.get(position).parkInfo.services.size() > 0) {
				viewHolder.serviceicon.setVisibility(View.VISIBLE);

			} else {
				viewHolder.serviceicon.setVisibility(View.GONE);
			}
			return convertView;

		}
	}

	@Override
	public Loader<List<PoiInfo>> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<List<PoiInfo>> loader, List<PoiInfo> data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoaderReset(Loader<List<PoiInfo>> loader) {
		// TODO Auto-generated method stub

	}

	public void getParkListDate(final double lantitude, final double longitude) {
		// HttpRequest<List<TParkInfo_LocEntity>> httpRequestAni = new
		// HttpRequest<List<TParkInfo_LocEntity>>(
		// "/a/ans?a=" + lantitude + "&n=" + longitude,
		// new TypeToken<List<TParkInfo_LocEntity>>() {
		// }.getType()) {
		// @Override
		// public void onSuccess(List<TParkInfo_LocEntity> infos) {
		//
		// if (infos != null) {
		// LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
		// for(TParkInfo_LocEntity info:infos)
		// {
		// LatLng markLatLng = new LatLng(info.latitude,
		// info.longitude);
		//
		// double distance = DistanceUtil.getDistance(ll, markLatLng);
		// int distance2 = Integer.parseInt(new java.text.DecimalFormat("0")
		// .format(distance));
		// info.distance=distance2;
		// }
		//
		// UIUtils.tempParkinfo=infos;
		// Intent intent=new Intent();
		// intent.setClass(getActivity(), MapParkListviewActivity.class);
		// startActivity(intent);
		//
		// }
		//
		// }
		//
		// @Override
		// public void onFailed(String message) {
		// Log.e("checkVersion", message);
		//
		// }
		//
		// };
		//
		// httpRequestAni.execute();
		HttpRequestAni<List<TParkInfo_LocEntity>> httpRequestAni = new HttpRequestAni<List<TParkInfo_LocEntity>>(
				getActivity(), "/a/ans/nearby/all?a=" + lantitude + "&n=" + longitude,
				new TypeToken<List<TParkInfo_LocEntity>>() {
				}.getType()) {

			@Override
			public void callback(List<TParkInfo_LocEntity> arg0) {
				// adapter.loadData(arg0);
				if (arg0 != null) {
					for (TParkInfo_LocEntity info : arg0) {
						LatLng markLatLng = new LatLng(info.latitude, info.longitude);
						if (UIUtils.mylocallatlng != null) {
							LatLng ll = new LatLng(UIUtils.currentlatlng.latitude, UIUtils.currentlatlng.longitude);
							double distance = DistanceUtil.getDistance(ll, markLatLng);

							int distance2 = Integer.parseInt(new java.text.DecimalFormat("0").format(distance));
							info.distance = distance2;
						} else {
							info.distance = 0;
						}
					}
					Collections.sort(arg0, comparator);
				}

				adapter.loadData(arg0);
			}
			/*
			 * 含分页的list返回接口
			 * 
			 * listview.stopLoadMore(); if (arg0 != null&&arg0.getRowCount()>0)
			 * { TOTALE_PAGE = arg0.getPageCount();
			 * 
			 * if (arg0.getPageCount() <= 1) {
			 * listview.setPullLoadEnable(false); } else {
			 * listview.setPullLoadEnable(true); } mydata=arg0.getResult();
			 * 
			 * 
			 * 
			 * 
			 * 
			 * for(TParkInfo_LocEntity info:mydata) { LatLng markLatLng = new
			 * LatLng(info.latitude, info.longitude);
			 * if(UIUtils.mylocallatlng!=null){ LatLng ll = new
			 * LatLng(UIUtils.mylocallatlng.latitude,
			 * UIUtils.mylocallatlng.longitude); double distance =
			 * DistanceUtil.getDistance(ll, markLatLng);
			 * 
			 * int distance2 = Integer.parseInt(new java.text.DecimalFormat("0")
			 * .format(distance)); info.distance=distance2; }else {
			 * info.distance=0; } }
			 * 
			 * Collections.sort(mydata, comparator);
			 * 
			 * } else if (arg0.getRowCount() == 0) { if (mydata != null)
			 * mydata.clear(); } if (mydata == null || mydata.size() <= 0) {
			 * Toast.makeText(getActivity(), "未查询到结果",
			 * Toast.LENGTH_LONG).show();
			 * 
			 * listview.onRefreshComplete();
			 * 
			 * return;
			 * 
			 * } if (mydata.size() > 0) { if (CURRENT_PAGE == 0)
			 * adapter.loadData(mydata); else adapter.loadMore(mydata); }
			 * //显示分页信息 UIUtils.displayPaginationInfo(getActivity(),
			 * CURRENT_PAGE, Constants.PAGINATION_PAGESIZE, arg0.getRowCount()
			 * ); if (listview.isRefreshing()) { listview.onRefreshComplete();
			 * 
			 * }
			 * 
			 * 
			 */

		};

		httpRequestAni.execute();
	}

	public void load() {

		if (UIUtils.currentlatlng != null)
			getParkListDate(UIUtils.currentlatlng.latitude, UIUtils.currentlatlng.longitude);
		else {
			Toast.makeText(getActivity(), "查询失败", Toast.LENGTH_SHORT).show();

		}

	}

}
