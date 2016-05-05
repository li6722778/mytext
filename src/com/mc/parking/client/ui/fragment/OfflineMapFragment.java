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
import com.mc.park.client.R;
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
	 * ���ߵ�ͼ������
	 */
	private List<OfflineMapCityBean> mDatas = new ArrayList<OfflineMapCityBean>();
	/**
	 * ������
	 */
	private MyOfflineCityBeanAdapter mAdapter;
	private LayoutInflater mInflater;
	private Context context;
	/**
	 * Ŀǰ�������ض��еĳ���
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
		 * ��ʼ�����ߵ�ͼ
		 */
		initOfflineMap();
		/**
		 * ��ʼ��ListView����
		 */
		initData();
		/**
		 * ��ʼ��ListView
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
	 * �������Ƴ����ض���
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
	 * ɾ�����ߵ�ͼ
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
				myactivity.setMyCityDownloadMessage("�����ߵ�ͼ��", 0);
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
	 * ������������������ض���
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

		// ����������ų���
		ArrayList<MKOLSearchRecord> offlineCityList = mOfflineMap
				.getHotCityList();
		MKOLSearchRecord shenyang = new MKOLSearchRecord();
		shenyang.cityID = 58;
		shenyang.cityName = "������";
		offlineCityList.add(0, shenyang);

		// ���г���
		ArrayList<MKOLSearchRecord> allCityList = mOfflineMap
				.getOfflineCityList();
		offlineCityList.addAll(allCityList);

		// ��������Ѿ����صĳ����б�
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
						message = "���ߵ�ͼ���Ѿ��������\n";
						message += "��ռ����:" + formatDataSize(ele.size);
					} else if (ele.status == MKOLUpdateElement.WAITING) {
						message = "���ߵ�ͼ���ȴ�����\n";
					} else if (ele.status == MKOLUpdateElement.DOWNLOADING) {
						message = "���½���:" + ele.ratio + "%";
					} else if (ele.status == MKOLUpdateElement.SUSPENDED) {
						message = "���½���:" + ele.ratio + "%,��ǰ��ͣ����";
					}
					FragmentActivity activity = getActivity();
					if (activity != null) {
						OffineMapActivity myactivity = (OffineMapActivity) activity;
						myactivity.setMyCityDownloadMessage(message, ele.ratio);
					}
				}
			}

		// �����������ݵ�״̬
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
	 * ��ʼ�����ߵ�ͼ
	 */
	private void initOfflineMap() {

		// ���ü���
		mOfflineMap.init(new MKOfflineMapListener() {
			@Override
			public void onGetOfflineMapState(int type, int state) {
				switch (type) {
				case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
					// ���ߵ�ͼ���ظ����¼�����
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
								message = "���ߵ�ͼ���Ѿ�����\n";
								message += "��ռ����:"
										+ formatDataSize(update.size);
							}
							if (update.status == MKOLUpdateElement.WAITING) {
								message = "���ߵ�ͼ���ȴ�����\n";
							} else if (update.status == MKOLUpdateElement.DOWNLOADING) {
								message = "���½���:" + update.ratio + "%";
							} else if (update.status == MKOLUpdateElement.SUSPENDED) {
								message = "���½���:" + update.ratio + "%,��ǰ��ͣ����";
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
					// �������ߵ�ͼ��װ
					Log.d(TAG, "TYPE_NEW_OFFLINE");
					break;
				case MKOfflineMap.TYPE_VER_UPDATE:
					// �汾������ʾ
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
	 * ���ų��е�ͼ�б��Adapter
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
			// ���ݽ��������������ʾ
			if (progress == 0) {
				progressMsg = "δ����";
			} else if (progress == 100) {
				bean.setFlag(Flag.NO_STATUS);
				progressMsg = "������";
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
			// ���ݵ�ǰ״̬��������ʾ
			switch (bean.getFlag()) {
			case PAUSE:
				progressMsg += "���ȴ����ء�";
				break;
			case DOWNLOADING:
				progressMsg += "���������ء�";
				break;
			case DELETE:
				progressMsg += "�����߰���ɾ����";
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
						confirmDialog.setMessage("ȷ��ɾ��[" + bean.getCityName()
								+ "]�������ߵ�ͼ������?");
						confirmDialog.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										deleteMap(position, bean.getCityCode());
									}
								});
						confirmDialog.setNegativeButton("ȡ��", null);
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
