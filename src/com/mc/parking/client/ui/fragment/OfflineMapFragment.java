package com.mc.parking.client.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.OffinemapEntity;
import com.mc.parking.client.entity.OfflineMapCityBean;
import com.mc.parking.client.entity.OfflineMapCityBean.Flag;
import com.mc.parking.client.layout.BaseDialogFragmentv4;
import com.mc.parking.client.ui.OffineMapActivity;
import com.mc.parking.client.utils.DBHelper;
import com.mc.parking.client.utils.SessionUtils;

public class OfflineMapFragment extends android.support.v4.app.Fragment {

	protected static final String TAG = "OfflineMapActivity";

	private ListView mListView;
	/**
	 * 离线地图的数据
	 */
	private List<OfflineMapCityBean> mDatas = new ArrayList<OfflineMapCityBean>();
	/**
	 * 适配器
	 */
	private MyOfflineCityBeanAdapter mAdapter;
	private LayoutInflater mInflater;
	private Context context;
	/**
	 * 目前加入下载队列的城市
	 */
	private List<Integer> mCityCodes = new ArrayList<Integer>();
	public static final String ARG_SECTION_NUMBER = "section_number";
	View view;
	private MKOfflineMap mOfflineMap;

	public OfflineMapFragment(MKOfflineMap mOfflineMap) {
		this.mOfflineMap = mOfflineMap;
	}

	private DBHelper dbHelper = null;

	public DBHelper getHelper() {
		if (dbHelper == null) {
			dbHelper = OpenHelperManager.getHelper(getActivity(),
					DBHelper.class);
		}
		return dbHelper;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_offlinemap_hotcity,
				container, false);

		context = getActivity();
		mInflater = LayoutInflater.from(context);
		/**
		 * 初始化离线地图
		 */
		initOfflineMap();
		/**
		 * 初始化ListView数据
		 */
		initData();
		/**
		 * 初始化ListView
		 */
		initListView();
		return view;
	}

	private void initListView() {
		mListView = (ListView) view.findViewById(R.id.offlinemap_list);
		if (mListView != null) {
			mAdapter = new MyOfflineCityBeanAdapter();
			mListView.setAdapter(mAdapter);

			mListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					int cityId = mDatas.get(position).getCityCode();
					if (mCityCodes.contains(cityId)) {
						removeTaskFromQueue(position, cityId);
					} else {
						addToDownloadQueue(position, cityId);
					}

				}
			});
		}
	}

	/**
	 * 将任务移除下载队列
	 * 
	 * @param pos
	 * @param cityId
	 */
	public void removeTaskFromQueue(int pos, int cityId) {
		mOfflineMap.pause(cityId);
		mDatas.get(pos).setFlag(Flag.NO_STATUS);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 删除离线地图
	 * 
	 * @param pos
	 * @param cityId
	 */
	public void deleteMap(int pos, final int cityId) {
		mOfflineMap.remove(cityId);
		mDatas.get(pos).setFlag(Flag.DELETE);
		mDatas.get(pos).setProgress(0);
		mAdapter.notifyDataSetChanged();

		if (cityId == SessionUtils.cityCode) {
			FragmentActivity activity = getActivity();
			if (activity != null) {
				OffineMapActivity myactivity = (OffineMapActivity) activity;
				myactivity.setMyCityDownloadMessage("无离线地图包", 0);
			}
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Dao<OffinemapEntity, Integer> dao = getHelper()
							.getOffineDao();
					DeleteBuilder<OffinemapEntity, Integer> deleteBuilder = dao
							.deleteBuilder();
					deleteBuilder.where().eq("citycode", cityId);
					deleteBuilder.delete();
				} catch (Exception e) {
					Log.e("getOffineDao", e.getMessage(), e);
				}
			}

		}).start();
	}

	/**
	 * 将下载任务添加至下载队列
	 * 
	 * @param pos
	 * @param cityId
	 */
	public void addToDownloadQueue(int pos, int cityId) {
		mCityCodes.add(cityId);
		mOfflineMap.start(cityId);
		mDatas.get(pos).setFlag(Flag.PAUSE);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (dbHelper != null) {
			OpenHelperManager.releaseHelper();
			dbHelper = null;
		}
	}

	private void initData() {

		// 获得所有热门城市
		ArrayList<MKOLSearchRecord> offlineCityList = mOfflineMap
				.getHotCityList();
		MKOLSearchRecord shenyang = new MKOLSearchRecord();
		shenyang.cityID = 58;
		shenyang.cityName = "沈阳市";
		offlineCityList.add(0, shenyang);

		// 所有城市
		ArrayList<MKOLSearchRecord> allCityList = mOfflineMap
				.getOfflineCityList();
		offlineCityList.addAll(allCityList);

		// 获得所有已经下载的城市列表
		ArrayList<MKOLUpdateElement> allUpdateInfo = mOfflineMap
				.getAllUpdateInfo();
		List<Integer> doneCity = new ArrayList<Integer>();
		if (allUpdateInfo != null)
			for (final MKOLUpdateElement ele : allUpdateInfo) {
				OfflineMapCityBean cityBean = new OfflineMapCityBean();
				cityBean.setCityName(ele.cityName);
				cityBean.setCityCode(ele.cityID);
				cityBean.setProgress(ele.ratio);
				mDatas.add(cityBean);
				doneCity.add(ele.cityID);
				if (ele.cityID == SessionUtils.cityCode) {
					String message = "";
					if (ele.status == MKOLUpdateElement.FINISHED) {
						message = "离线地图包已经下载完成\n";
						message += "所占容量:" + formatDataSize(ele.size);
					} else if (ele.status == MKOLUpdateElement.WAITING) {
						message = "离线地图包等待下载\n";
					} else if (ele.status == MKOLUpdateElement.DOWNLOADING) {
						message = "更新进度:" + ele.ratio + "%";
					} else if (ele.status == MKOLUpdateElement.SUSPENDED) {
						message = "更新进度:" + ele.ratio + "%,当前暂停下载";
					}
					FragmentActivity activity = getActivity();
					if (activity != null) {
						OffineMapActivity myactivity = (OffineMapActivity) activity;
						myactivity.setMyCityDownloadMessage(message, ele.ratio);
					}
				}
			}

		// 设置所有数据的状态
		for (MKOLSearchRecord record : offlineCityList) {
			if (doneCity.contains(record.cityID)) {
				continue;
			}
			OfflineMapCityBean cityBean = new OfflineMapCityBean();
			cityBean.setCityName(record.cityName);
			cityBean.setCityCode(record.cityID);

			mDatas.add(cityBean);
		}

	}

	/**
	 * 初始化离线地图
	 */
	private void initOfflineMap() {

		// 设置监听
		mOfflineMap.init(new MKOfflineMapListener() {
			@Override
			public void onGetOfflineMapState(int type, int state) {
				switch (type) {
				case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
					// 离线地图下载更新事件类型
					MKOLUpdateElement update = mOfflineMap.getUpdateInfo(state);
					Log.d("OffineMapMyCityFragment", update.cityName + " ,"
							+ update.ratio + ", city:" + state);
					for (OfflineMapCityBean bean : mDatas) {
						if (bean.getCityCode() == state) {
							bean.setProgress(update.ratio);
							bean.setFlag(Flag.DOWNLOADING);
							break;
						}
					}

					String message = "";
					if (SessionUtils.cityCode == state) {
						if (update != null) {
							if (update.ratio == 100) {
								message = "离线地图包已经下载\n";
								message += "所占容量:"
										+ formatDataSize(update.size);
							}
							if (update.status == MKOLUpdateElement.WAITING) {
								message = "离线地图包等待下载\n";
							} else if (update.status == MKOLUpdateElement.DOWNLOADING) {
								message = "更新进度:" + update.ratio + "%";
							} else if (update.status == MKOLUpdateElement.SUSPENDED) {
								message = "更新进度:" + update.ratio + "%,当前暂停下载";
							}
						}
						FragmentActivity activity = getActivity();
						if (activity != null) {
							OffineMapActivity myactivity = (OffineMapActivity) activity;
							myactivity.setMyCityDownloadMessage(message,
									update.ratio);
						}
					}

					mAdapter.notifyDataSetChanged();
					Log.d(TAG, "TYPE_DOWNLOAD_UPDATE");
					break;
				case MKOfflineMap.TYPE_NEW_OFFLINE:
					// 有新离线地图安装
					Log.d(TAG, "TYPE_NEW_OFFLINE");
					break;
				case MKOfflineMap.TYPE_VER_UPDATE:
					// 版本更新提示
					break;
				}

			}
		});
	}

	public String formatDataSize(int size) {
		String ret = "";
		if (size < (1024 * 1024)) {
			ret = String.format("%dK", size / 1024);
		} else {
			ret = String.format("%.1fM", size / (1024 * 1024.0));
		}
		return ret;
	}

	/**
	 * 热门城市地图列表的Adapter
	 * 
	 * @author zhy
	 * 
	 */
	class MyOfflineCityBeanAdapter extends BaseAdapter {

		@Override
		public boolean isEnabled(int position) {
			if (mDatas.get(position).getProgress() == 100) {
				return false;
			}
			return super.isEnabled(position);
		}

		@Override
		public int getCount() {
			return mDatas.size();
		}

		@Override
		public Object getItem(int position) {
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final OfflineMapCityBean bean = mDatas.get(position);
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.offlinemap_item,
						parent, false);
				holder.cityName = (TextView) convertView
						.findViewById(R.id.id_cityname);
				holder.progress = (TextView) convertView
						.findViewById(R.id.id_progress);
				holder.removebutton = (ImageButton) convertView
						.findViewById(R.id.city_delete);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.cityName.setText(bean.getCityName());
			int progress = bean.getProgress();
			String progressMsg = "";
			// 根据进度情况，设置显示
			if (progress == 0) {
				progressMsg = "未下载";
			} else if (progress == 100) {
				bean.setFlag(Flag.NO_STATUS);
				progressMsg = "已下载";
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Dao<OffinemapEntity, Integer> dao = getHelper()
									.getOffineDao();
							List<OffinemapEntity> allDownload = dao
									.queryBuilder().where()
									.eq("citycode", bean.getCityCode()).query();
							if (allDownload == null || allDownload.size() <= 0) {
								OffinemapEntity entity = new OffinemapEntity();
								entity.setCitycode(bean.getCityCode());
								entity.setCityName(bean.getCityName());
								entity.setProgress(bean.getProgress());
								dao.createIfNotExists(entity);
							}

						} catch (Exception e) {
							Log.e("getOffineDao", e.getMessage(), e);
						}
					}

				}).start();
			} else {
				progressMsg = progress + "%";
			}
			// 根据当前状态，设置显示
			switch (bean.getFlag()) {
			case PAUSE:
				progressMsg += "【等待下载】";
				break;
			case DOWNLOADING:
				progressMsg += "【正在下载】";
				break;
			case DELETE:
				progressMsg += "【离线包已删除】";
				break;
			default:
				break;
			}
			holder.progress.setText(progressMsg);

			if (bean.getProgress() == 100) {
				holder.removebutton.setVisibility(View.VISIBLE);
				holder.removebutton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						BaseDialogFragmentv4 confirmDialog = new BaseDialogFragmentv4();
						confirmDialog.setMessage("确定删除[" + bean.getCityName()
								+ "]本地离线地图数据吗?");
						confirmDialog.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										deleteMap(position, bean.getCityCode());
									}
								});
						confirmDialog.setNegativeButton("取消", null);
						confirmDialog.show(getFragmentManager(), "");

					}

				});
			} else {
				holder.removebutton.setVisibility(View.GONE);
			}

			return convertView;
		}

		private class ViewHolder {
			TextView cityName;
			TextView progress;
			ImageButton removebutton;

		}
	}
}
