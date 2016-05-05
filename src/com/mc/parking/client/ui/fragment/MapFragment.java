package com.mc.parking.client.ui.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.baidu.navisdk.BNaviPoint;
import com.baidu.navisdk.BNaviPoint.CoordinateType;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BaiduNaviManager.OnStartNavigationListener;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mc.parking.client.Constants;
import com.mc.park.client.R;
import com.mc.parking.client.entity.BaiduParkInfo;
import com.mc.parking.client.entity.MapMakers;
import com.mc.parking.client.entity.OrderEntity;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.entity.TParkService;
import com.mc.parking.client.entity.TTopBannner;
import com.mc.parking.client.entity.VersionEntity;
import com.mc.parking.client.layout.BaseDialogFragment;
import com.mc.parking.client.layout.SlideShowView;
import com.mc.parking.client.layout.SlideShowView.OnClickActionListener;
import com.mc.parking.client.layout.net.HttpRequest;
import com.mc.parking.client.ui.ActivityHomeList;
import com.mc.parking.client.ui.AdsWebActivity;
import com.mc.parking.client.ui.IndexActivity;
import com.mc.parking.client.ui.LoginActivity;
import com.mc.parking.client.ui.MainActivity;
import com.mc.parking.client.ui.NavigationActivity;
import com.mc.parking.client.ui.OffineMapActivity;
import com.mc.parking.client.ui.OrderActivity;
import com.mc.parking.client.ui.OrderDetailActivity;
import com.mc.parking.client.ui.ParkActivity;
import com.mc.parking.client.ui.YuyueActivity;
import com.mc.parking.client.ui.listener.MapOrientationListener;
import com.mc.parking.client.ui.listener.MapOrientationListener.OnOrientationListener;
import com.mc.parking.client.utils.DBHelper;
import com.mc.parking.client.utils.Log;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class MapFragment extends Fragment {

	/**
	 * ��ͼ�ؼ�
	 */
	private MapView mMapView = null;
	/**
	 * ��ͼʵ��
	 */
	private BaiduMap mBaiduMap;
	/**
	 * ��λ�Ŀͻ���
	 */
	private LocationClient mLocationClient;
	/**
	 * ��λ�ļ�����
	 */
	public MyLocationListener mMyLocationListener;
	/**
	 * ��ǰ��λ��ģʽ
	 */
	private LocationMode mCurrentMode;
	/***
	 * �Ƿ��ǵ�һ�ζ�λ
	 */
	private volatile boolean isFristLocation = true;

	private volatile boolean isFristCityDownConfirm = true;

	/**
	 * ����һ�εľ�γ��
	 */
	public double mCurrentLantitude;
	public double mCurrentLongitude;
	private String locationName;

	/**
	 * ��ǰ�ľ���
	 */
	private float mCurrentAccracy;
	/**
	 * ���򴫸����ļ�����
	 */
	private MapOrientationListener myOrientationListener;
	/**
	 * ���򴫸���X�����ֵ
	 */
	private int mXDirection;

	MapParkListFragment mapparklistFragment;
	AdsMainDialogFragment addialog;
	ImageLoader imageloader = ImageLoader.getInstance();

	// ��ʼ��ȫ�� bitmap ��Ϣ������ʱ��ʱ recycle
	// private BitmapDescriptor
	// mIconMaker=BitmapDescriptorFactory.fromResource(R.drawable.maker);
	// private BitmapDescriptor
	// codingMaker=BitmapDescriptorFactory.fromResource(R.drawable.marker_coding_o);
	// private BitmapDescriptor codingMaker = BitmapDescriptorFactory
	// .fromResource(R.drawable.maker);
	private BitmapDescriptor baiduIcon = BitmapDescriptorFactory.fromResource(R.drawable.myloc);
	// private BitmapDescriptor selectIcon = BitmapDescriptorFactory
	// .fromResource(R.drawable.marker_h);

	// ��ʼ��17��marker
	public BitmapDescriptor[] bubbleMarkers = new BitmapDescriptor[20];

	/**
	 * ��ϸ��Ϣ�� ����
	 */
	private RelativeLayout mMarkerInfoLy, emMarkerInfoLy;

	private static TParkInfo_LocEntity tParkInfo_LocEntity;
	private BaiduParkInfo baiduParkInfo;
	private ImageButton zoomOutBtn, zoomInBtn, btn_mapserchpark;

	private LinearLayout linearLayout, banner_total_line;

	private LinearLayout yuyueLayout;
	public SearchListFragment listFragment;
	private EditText searchEditText;
	public TopBarFragment topBarFragment;
	public TextView marker_park_name, marker_park_info, einfo_servicename, bannertxt;
	public ImageView marker_parkimage, marker_serviceimage;
	private LinearLayout toolbarMap;
	private MainActivity mainActivity;

	private boolean isDisplaySearchView = false;

	private Button Baiduparkgo;
	private Button yuyueButton, mashangquButton;
	// ����
	private boolean mIsEngineInitSuccess = false;
	public DisplayImageOptions options;

	private View view;

	OverlayOptions locaoverlayOptions;
	OverlayOptions locaoverlayOptions1;
	Marker locamarker;
	Marker centremarker;

	View markview;

	// �����ض���
	TextView adsshow;
	ImageView adsimage;
	List<TTopBannner> mybanner;
	List<ImageView> bannerlist;
	SlideShowView slide;
	private NaviEngineInitListener mNaviEngineInitListener = new NaviEngineInitListener() {
		public void engineInitSuccess() {
			// ������ʼ�����첽�ģ���ҪһС��ʱ�䣬�������־��ʶ�������Ƿ��ʼ���ɹ���Ϊtrueʱ����ܷ��𵼺�
			mIsEngineInitSuccess = true;
		}

		public void engineInitStart() {
		}

		public void engineInitFail() {
		}
	};

	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	// poi�������
	PoiSearch mPoiSearch;
	List<PoiInfo> listpoiInfos;
	Marker currentmarker = null;

	// Marker oldmarker = null;

	public BaiduMap getMap() {
		return mBaiduMap;
	}

	public void setMapCurrentMode(LocationMode mCurrentMode) {
		this.mCurrentMode = mCurrentMode;
	}

	public void gotoPage() {
		Intent intent = new Intent(mainActivity, YuyueActivity.class);
		Bundle buidle = new Bundle();
		buidle.putSerializable("parkinfoLoc", tParkInfo_LocEntity);
		intent.putExtras(buidle);
		startActivityForResult(intent, 1);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			view = inflater.inflate(R.layout.fragment_map2, container, false);
		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			view = inflater.inflate(R.layout.fragment_map, container, false);
		}
		markview = inflater.inflate(R.layout.ac_marker_window, null);
		einfo_servicename = (TextView) view.findViewById(R.id.einfo_servicename);
		einfo_servicename.setSelected(true);
		marker_park_name = (TextView) markview.findViewById(R.id.marker_park_name);
		marker_park_info = (TextView) markview.findViewById(R.id.marker_park_info);
		marker_parkimage = (ImageView) markview.findViewById(R.id.marker_parkimage);
		marker_serviceimage = (ImageView) markview.findViewById(R.id.marker_serviceimage);
		FrameLayout viewFrame = (FrameLayout) view.findViewById(R.id.id_bmapView);
		LatLng cacheLatLng = new LatLng(SessionUtils.lastTimeLantitude, SessionUtils.lastTimeLongitude);
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_empty).cacheInMemory(true)
				.cacheOnDisc(false).displayer(new RoundedBitmapDisplayer(12)).build();

		Log.d("MapFragment cache latlng", cacheLatLng.toString());

		mainActivity = (MainActivity) getActivity();

		yuyueLayout = (LinearLayout) view.findViewById(R.id.yuyueLayout);
		linearLayout = (LinearLayout) view.findViewById(R.id.map_toolbar);

		yuyueButton = (Button) view.findViewById(R.id.yuyueButton);
		mashangquButton = (Button) view.findViewById(R.id.mashangquButton);

		// �������ĵ�
		BaiduMapOptions options = new BaiduMapOptions();
		options.zoomControlsEnabled(false);
		options.mapStatus(new MapStatus.Builder().target(cacheLatLng).build());
		mMapView = new MapView(mainActivity, options);
		viewFrame.addView(mMapView);

		tParkInfo_LocEntity = null;
		// ��һ�ζ�λ
		isFristLocation = true;
		isFristCityDownConfirm = true;
		locationName = "";

		yuyueButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				
				if (!SessionUtils.isLogined()) {
					LoginDialogFragment dialog = new LoginDialogFragment(mainActivity, tParkInfo_LocEntity);
					dialog.show(getFragmentManager(), "loginDialog");
				} else {
					
					Intent intent = new Intent(getActivity(), YuyueActivity.class);
					Bundle buidle = new Bundle();
					buidle.putSerializable("parkinfoLoc", tParkInfo_LocEntity);
					intent.putExtras(buidle);
					startActivityForResult(intent, 0);
				}
			}
		});

		mashangquButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog dialog = new AlertDialog.Builder(mainActivity).setTitle("��ܰ��ʾ")
						.setMessage("δ������ǰԤԼ,��λ���ܱ�ռ��.")
						.setNeutralButton("��ԤԼֱ��ȥ", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						OrderEntity order = new OrderEntity();
						order.settParkInfo_LocEntity(tParkInfo_LocEntity);
						Intent baiduint = new Intent();
						baiduint.putExtra("orderinfo", order);
						onActivityResult(1, Constants.ResultCode.NON_ORDER, baiduint);
					}
				}).setNegativeButton("����ԤԼ", null).show();
			}
		});
		// ��ȡ��ͼ�ؼ�����
		// mMapView = (MapView) view.findViewById(R.id.id_bmapView);

		mMarkerInfoLy = (RelativeLayout) view.findViewById(R.id.id_marker_info);
		emMarkerInfoLy = (RelativeLayout) view.findViewById(R.id.id_emarker_info);

		// ����͸����
		mMarkerInfoLy.getBackground().setAlpha(Constants.Alpha);
		emMarkerInfoLy.getBackground().setAlpha(Constants.Alpha);
		yuyueButton.getBackground().setAlpha(Constants.Alpha);
		mashangquButton.getBackground().setAlpha(Constants.Alpha);
		toolbarMap = (LinearLayout) view.findViewById(R.id.map_toolbar);
		;
		// ��õ�ͼ��ʵ��
		mBaiduMap = mMapView.getMap();
		mMapView.getMap().setTrafficEnabled(true);

		// ��ʼ��Poi
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
		Baiduparkgo = (Button) view.findViewById(R.id.BaiduButton);
		Baiduparkgo.getBackground().setAlpha(Constants.Alpha);
		// ��ʼ����λ
		initMyLocation();
		// ��ʼ��������
		initOritationListener();
		initMarkerClickEvent();
		initMapClickEvent();

		mMarkerInfoLy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tParkInfo_LocEntity != null) {
					
					Intent intent = new Intent(getActivity(), ActivityHomeList.class);
					Bundle buidle = new Bundle();
					buidle.putSerializable("parkinfo", tParkInfo_LocEntity);
					intent.putExtras(buidle);
					startActivityForResult(intent, 0);
				} else {
					Toast.makeText(getActivity(), "û�л�ȡ����ϸ��ͣ����Ϣ", Toast.LENGTH_SHORT).show();
				}

			}

		});
		Baiduparkgo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent baiduint = new Intent();
				onActivityResult(1, Constants.ResultCode.NAVATIGOR_START, baiduint);
			}
		});

		mCurrentMode = LocationMode.NORMAL;
		// startPoi();
		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

			@Override
			public void onMapStatusChange(MapStatus arg0) {

			}

			@Override
			public void onMapStatusChangeFinish(MapStatus arg0) {
				// LatLng latLng = mBaiduMap.getMapStatus().target; ������ȡ���ĵ㾭γ��
			}

			@Override
			public void onMapStatusChangeStart(MapStatus arg0) {
				// TODO Auto-generated method stub

			}

		});

		// ������С��ͼ
		btn_mapserchpark = (ImageButton) view.findViewById(R.id.btn_mapserchpark);
		btn_mapserchpark.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (UIUtils.myserchlatlng != null) {
					LatLng serchll = new LatLng(UIUtils.myserchlatlng.latitude, UIUtils.myserchlatlng.longitude);
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(serchll, 16);
					mBaiduMap.animateMapStatus(u);
				}
			}
		});
		zoomOutBtn = (ImageButton) view.findViewById(R.id.zoomin);
		zoomInBtn = (ImageButton) view.findViewById(R.id.zoomout);
		zoomInBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				float zoomLevel = mBaiduMap.getMapStatus().zoom;

				if (zoomLevel <= 18) {
					// MapStatusUpdateFactory.zoomIn();

					mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomIn());
					zoomOutBtn.setEnabled(true);
				} else {
					Toast.makeText(getActivity(), "�Ѿ��������", Toast.LENGTH_SHORT).show();
					zoomInBtn.setEnabled(false);
				}
			}
		});
		zoomOutBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				float zoomLevel = mBaiduMap.getMapStatus().zoom;
				if (zoomLevel > 4) {
					mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomOut());
					zoomInBtn.setEnabled(true);
				} else {
					zoomOutBtn.setEnabled(false);
					Toast.makeText(getActivity(), "�Ѿ�������С", Toast.LENGTH_SHORT).show();
				}
			}
		});

		ImageButton btn_maplocal = (ImageButton) view.findViewById(R.id.btn_maplocal);
		btn_maplocal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
	
				center2myLoc();
			}
		});

		ImageButton btn_mappark = (ImageButton) view.findViewById(R.id.btn_mappark);
		btn_mappark.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				featchCurrentParkForCenterOfMap();

			}
		});

		final CheckBox btton2 = (CheckBox) view.findViewById(R.id.topbarMapTraff);
		btton2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					getMap().setTrafficEnabled(true);
				} else {
					getMap().setTrafficEnabled(false);
				}
			}

		});
		FragmentManager manager = getFragmentManager();
		topBarFragment = (TopBarFragment) manager.findFragmentById(R.id.fragment_topbar);
		searchEditText = (EditText) view.findViewById(R.id.search_box2);
		listFragment = new SearchListFragment();
		mapparklistFragment = new MapParkListFragment();
		banner_total_line = (LinearLayout) view.findViewById(R.id.banner_total_line);
		banner_total_line.setVisibility(View.GONE);
		searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.hide(topBarFragment).addToBackStack(null);
				transaction.replace(R.id.fragment_map, listFragment).addToBackStack(null).commit();
				searchEditText.clearFocus();
				isDisplaySearchView = true;
				if (listFragment != null) {
					listFragment.displayHistoryView();
				}

			}
		});
		// �����ؿؼ�
		bannertxt = (TextView) view.findViewById(R.id.bannertxt);
		bannertxt.setSelected(true);
		addialog = new AdsMainDialogFragment();
		adsshow = (TextView) view.findViewById(R.id.adsshow_btn);
		slide = (SlideShowView) view.findViewById(R.id.slideshowView);
		getBanner();
		slide.setOnClickActionListener(new OnClickActionListener() {

			@Override
			public void selectnum(int i) {
				// TODO Auto-generated method stub
				if (!SessionUtils.isLogined()) {
					Intent intentLogin = new Intent(getActivity(),
							LoginActivity.class);
					intentLogin.putExtra("parent", LoginActivity.parent_userinfo);
					startActivity(intentLogin);

				} else {

					LinearLayout tx = (LinearLayout) view.findViewById(R.id.ads_line);
					if (mybanner != null && mybanner.get(i).bannerType == 1 && mybanner.get(i).parkServiceBean != null
							&& mybanner.get(i).parkServiceBean.size() > 0
							&& mybanner.get(i).parkServiceBean.get(0).supplyInfo != null) {
						Bundle bu = new Bundle();
						bu.putSerializable("data", mybanner.get(i));
						addialog.setArguments(bu);
						// addialog.setdata(mybanner.get(i));
						addialog.show(getFragmentManager(), "banner");
						tx.setVisibility(View.GONE);
						adsshow.setBackgroundResource(R.drawable.icondownarrow);
					} else if (mybanner.get(i).bannerType == 2) {
						Intent intent = new Intent();
						intent.setClass(getActivity(), AdsWebActivity.class);
						intent.putExtra("url", mybanner.get(i).clickurl);
						if (mybanner != null && mybanner.get(i).parkServiceBean != null
								&& mybanner.get(i).parkServiceBean.size() > 0) {
							intent.putExtra("title", mybanner.get(i).parkServiceBean.get(0).serviceName);
						} else {
							intent.putExtra("title", "��ҳ");
						}
						startActivity(intent);
						tx.setVisibility(View.GONE);
						adsshow.setBackgroundResource(R.drawable.icondownarrow);
					} else if (mybanner.get(i).bannerType == 3) {
						tx.setVisibility(View.GONE);
						adsshow.setBackgroundResource(R.drawable.icondownarrow);
					} else {
						Toast.makeText(getActivity(), "��ʱ�޷��򿪣����Ժ�����", Toast.LENGTH_SHORT).show();
					}

				}
			}
		});

		adsshow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LinearLayout bannerline = (LinearLayout) view.findViewById(R.id.ads_line);
				if (bannerline.isShown()) {
					bannerline.setVisibility(View.GONE);
					adsshow.setBackgroundResource(R.drawable.icondownarrow);
				} else {
					bannerline.setVisibility(View.VISIBLE);
					adsshow.setBackgroundResource(R.drawable.iconuparrow);
				}
			}
		});
		adsimage = (ImageView) view.findViewById(R.id.adsimage);
		/*
		 * slide.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {} });
		 */

		return view;
	}

	private void initMapClickEvent() {
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				clearMapClick();

			}
		});
	}

	private void clearMapClick() {
		mMarkerInfoLy.setVisibility(View.GONE);
		Baiduparkgo.setVisibility(View.GONE);
		linearLayout.setVisibility(View.VISIBLE);
		mBaiduMap.hideInfoWindow();
		emMarkerInfoLy.setVisibility(View.GONE);
		// if (oldmarker != null) {
		// oldmarker.setIcon(codingMaker);
		//
		// }
		tParkInfo_LocEntity = null;

		yuyueLayout.setVisibility(View.VISIBLE);
		yuyueButton.setEnabled(false);
		mashangquButton.setEnabled(false);

	}

	private void initMarkerClickEvent() {
		// ��Marker�ĵ��
		mBaiduMap.setOnMarkerClickListener(onmarkerclick);
	}

	/**
	 * ����infoΪ�����ϵĿؼ�������Ϣ
	 * 
	 * @param mMarkerInfo2
	 * @param info
	 */
	protected void popupInfo(RelativeLayout mMarkerLy, TParkInfo_LocEntity info) {
		ViewHolder viewHolder = null;
		tParkInfo_LocEntity = info;
		if (mMarkerLy.getTag() == null) {
			viewHolder = new ViewHolder();
			viewHolder.infoName = (TextView) mMarkerLy.findViewById(R.id.info_name);

			viewHolder.infoDistance = (TextView) mMarkerLy.findViewById(R.id.info_distance);
			viewHolder.servicename = (TextView) mMarkerLy.findViewById(R.id.einfo_servicename);
			viewHolder.park_address = (TextView) mMarkerLy.findViewById(R.id.parkchebole_address);
			viewHolder.serviceline = (LinearLayout) mMarkerLy.findViewById(R.id.service_layout);
			viewHolder.rating = (RatingBar) mMarkerLy.findViewById(R.id.ratingBar);
			mMarkerLy.setTag(viewHolder);
		}
		viewHolder = (ViewHolder) mMarkerLy.getTag();
		if (tParkInfo_LocEntity.parkInfo != null) {
			viewHolder.infoDistance.setText(" ����" + tParkInfo_LocEntity.distance + "��");
			viewHolder.infoName.setText(tParkInfo_LocEntity.parkInfo.parkname);
			viewHolder.park_address.setText(tParkInfo_LocEntity.parkInfo.address);
			viewHolder.rating.setRating(tParkInfo_LocEntity.parkInfo.averagerat);
		}
		if (tParkInfo_LocEntity.parkInfo.services != null && tParkInfo_LocEntity.parkInfo.services.size() > 0) {
			viewHolder.serviceline.setVisibility(View.VISIBLE);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < tParkInfo_LocEntity.parkInfo.services.size(); i++) {
				sb.append(tParkInfo_LocEntity.parkInfo.services.get(i).serviceDetailForApp);

			}
			viewHolder.servicename.setText(sb);
		} else
			viewHolder.serviceline.setVisibility(View.GONE);
	}

	/**
	 * ���õ������mMarkerLy�Ŀؼ�
	 * 
	 */
	private class ViewHolder {
		RatingBar rating;
		TextView infoName;
		TextView infoDistance;
		TextView park_address;
		TextView servicename;
		LinearLayout serviceline;

	}

	protected void elsepopupInfo(RelativeLayout emMarkerLy, BaiduParkInfo baiduinfo) {

		eViewHolder eviewHolder = null;
		baiduParkInfo = baiduinfo;

		if (emMarkerLy.getTag() == null) {
			eviewHolder = new eViewHolder();
			eviewHolder.einfoName = (TextView) emMarkerLy.findViewById(R.id.einfo_name);
			eviewHolder.einfoDistance = (TextView) emMarkerLy.findViewById(R.id.einfo_distance);
			eviewHolder.parkAddress = (TextView) emMarkerLy.findViewById(R.id.parkbaidu_address);
			emMarkerLy.setTag(eviewHolder);
		}

		LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
		LatLng markLatLng = new LatLng(baiduParkInfo.getLatitude(), baiduParkInfo.getLongitude());

		double distance = DistanceUtil.getDistance(ll, markLatLng);
		int distance2 = Integer.parseInt(new java.text.DecimalFormat("0").format(distance));

		eviewHolder = (eViewHolder) emMarkerLy.getTag();
		eviewHolder.einfoDistance.setText(" ����" + distance2 + "��");
		eviewHolder.parkAddress.setText(baiduinfo.getAddress());
		eviewHolder.einfoName.setText(baiduParkInfo.getName());
	}

	/**
	 * ���õ������emMarkerLy�Ŀؼ�
	 * 
	 */
	private class eViewHolder {
		TextView einfoName;
		TextView einfoDistance;
		TextView parkAddress;
	}

	/**
	 * ��ʼ�����򴫸���
	 */
	private void initOritationListener() {
		myOrientationListener = new MapOrientationListener(getActivity().getApplicationContext());
		myOrientationListener.setOnOrientationListener(new OnOrientationListener() {
			@Override
			public void onOrientationChanged(float x) {
				mXDirection = (int) x;
				// ���춨λ����
				MyLocationData locData = new MyLocationData.Builder().accuracy(mCurrentAccracy)
						// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
						.direction(mXDirection).latitude(mCurrentLantitude).longitude(mCurrentLongitude).build();
				if (mBaiduMap != null) {
					// ���ö�λ����
					mBaiduMap.setMyLocationData(locData);
					// �����Զ���ͼ��
					// BitmapDescriptor mCurrentMarker =
					// BitmapDescriptorFactory
					// .fromResource(R.drawable.navi_map_gps_locked);
					MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, null);
					mBaiduMap.setMyLocationConfigeration(config);
				}

			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			toolbarMap.setOrientation(LinearLayout.VERTICAL);
		}

	}

	/**
	 * ��ʼ����λ��ش���
	 */
	private void initMyLocation() {

		if (SessionUtils.lastTimeLantitude > 0 && SessionUtils.lastTimeLongitude > 0) {
			LatLng ll = new LatLng(SessionUtils.lastTimeLantitude, SessionUtils.lastTimeLongitude);
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 14);
			mBaiduMap.setMapStatus(u);
		}

		// ��λ��ʼ��
		mLocationClient = new LocationClient(getActivity());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		// ���ö�λ���������
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);// �߾���
		option.setCoorType("bd09ll");// ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		option.setIsNeedAddress(true);// λ�ã�һ��Ҫ���ã��������ò�����ַ
		option.setOpenGps(true);// ��GPS
		option.setScanSpan(5000);// �೤ʱ�����һ������
		option.setNeedDeviceDirect(true);
		mLocationClient.setLocOption(option);

	}

	public void featchCurrentParkForCenterOfMap() {
		if (mBaiduMap != null) {
			clearMapClick();
			LatLng la = mBaiduMap.getMapStatus().target;
			sreachNearbyParkingNoMove(la.latitude, la.longitude, 1);
		}
	}

	/**
	 * ��ͼ�ƶ����ҵ�λ��,�˴��������·���λ����Ȼ��λ�� ֱ�������һ�ξ�γ�ȣ������ʱ��û�ж�λ�ɹ������ܻ���ʾЧ������
	 */
	public void center2myLoc() {
		clearMapClick();
		LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);
		mBaiduMap.animateMapStatus(u, 900);
	}

	public void moveToNewLocation(double lantitude, double longitude) {

		if (lantitude > 0 && longitude > 0) {
			LatLng ll = new LatLng(lantitude, longitude);
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);
			mBaiduMap.animateMapStatus(u, 900);
			sreachNearbyParking(lantitude, longitude);
		}
	}

	/**
	 * ʵ��ʵλ�ص�����
	 */
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (locamarker != null) {
				locamarker.remove();
				locamarker = null;
			}
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null)
				return;

			if (location.getCity() != null && !location.getCity().trim().equals("")) {
				SessionUtils.city = location.getCity();
			}
			if (location.getCityCode() != null) {
				try {
					SessionUtils.cityCode = Integer.parseInt(location.getCityCode());
				} catch (Exception e) {
					Log.e("onReceiveLocation", e.getMessage(), e);
				}
			}
			SessionUtils.address = location.getAddrStr();

			// ���춨λ����
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(mXDirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mCurrentAccracy = location.getRadius();
			// ���ö�λ����
			mBaiduMap.setMyLocationData(locData);
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();
			locationName = location.getAddrStr();
			LatLng ll1 = new LatLng(location.getLatitude(), location.getLongitude());

			// �����Զ���ͼ��
			// BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
			// .fromResource(R.drawable.navi_map_gps_locked);
			// MyLocationConfiguration config = new MyLocationConfiguration(
			// mCurrentMode, true, mCurrentMarker);
			// mBaiduMap.setMyLocationConfigeration(config);
			// ��һ�ζ�λʱ������ͼλ���ƶ�����ǰλ��
			if (isFristLocation) {

				// ����ǹ���Ա�Ͳ���Ҫ��
				if (SessionUtils.loginUser != null && SessionUtils.loginUser.userType >= Constants.USER_TYPE_PADMIN
						&& SessionUtils.loginUser.userType < Constants.USER_TYPE_PADMIN + 10) {

				} else {
					isFristLocation = false;
					LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);
					mBaiduMap.animateMapStatus(u, 900);
					sreachNearbyParking(mCurrentLantitude, mCurrentLongitude);
				}
			}
			// ��ʾ��λ���marker
			if (locamarker == null) {
				locaoverlayOptions = new MarkerOptions().position(ll1).icon(getNumberMarker(9)).zIndex(7);
				locamarker = (Marker) (mBaiduMap.addOverlay(locaoverlayOptions));
				Bundle bundle = new Bundle();
				bundle.putString("location", "location");
				locamarker.setExtraInfo(bundle);
			} else {
				locamarker.setPosition(ll1);

			}

			if (isFristCityDownConfirm && SessionUtils.city != null) {
				isFristCityDownConfirm = false;

				// if (SessionUtils.cityCode > 0) {
				// new Thread(new Runnable() {
				// @Override
				// public void run() {
				// try {
				// Dao<OffinemapEntity, Integer> dao = getHelper()
				// .getOffineDao();
				// List<OffinemapEntity> allDownload = dao
				// .queryBuilder().where()
				// .eq("citycode", SessionUtils.cityCode)
				// .query();
				// Message msg = new Message();
				// if (allDownload != null) {
				// msg.arg1 = allDownload.size();
				//
				// } else {
				// msg.arg1 = 0;
				// }
				// offineHandler.sendMessage(msg);
				// } catch (Exception e) {
				// Log.e("getOffineDao", e.getMessage(), e);
				// isFristCityDownConfirm = true;
				//
				// }
				// }
				//
				// }).start();
				// }

			}
		}

	}

	private DBHelper dbHelper = null;

	public DBHelper getHelper() {
		if (dbHelper == null) {
			dbHelper = OpenHelperManager.getHelper(getActivity(), DBHelper.class);
		}
		return dbHelper;
	}

	private Handler offineHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (msg.arg1 == 0) {
				BaseDialogFragment confirmDialog = new BaseDialogFragment();
				confirmDialog.setMessage(SessionUtils.city + "û�����ߵ�ͼ���Ƿ�����?");
				confirmDialog.setPositiveButton("����", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent intent = new Intent(getActivity(), OffineMapActivity.class);
						startActivity(intent);
					}
				});
				confirmDialog.setNegativeButton("ȡ��", null);
				confirmDialog.show(getFragmentManager(), "");
			}
		}
	};

	/**
	 * ��ʼ��ͼ��
	 */
	public void sreachNearbyParking(final double lantitude, final double longitude) {

		mBaiduMap.clear();
		// ��ʾ��ǰλ�õ�marker
		locamarker = null;
		showlocationmarker();
		UIUtils.myserchlatlng = null;
		// �����ٶ�poi����
		// startPoi(lantitude, longitude);

		HttpRequest<List<MapMakers>> httpRequestAni = new HttpRequest<List<MapMakers>>(
				"/a/ans/simple?a=" + lantitude + "&n=" + longitude, new TypeToken<List<MapMakers>>() {
				}.getType()) {
			@Override
			public void onSuccess(List<MapMakers> infos) {

				if (infos != null) {

					if (infos.size() > 0) {

						LatLng latLng = null;
						Marker marker = null;
						OverlayOptions overlayOptions = null;
						LatLng currentLatLng = new LatLng(mCurrentLantitude, mCurrentLongitude);
						int totalOpen = 0;
						for (MapMakers info : infos) {
							// λ��
							latLng = new LatLng(info.latitude, info.longitude);
							double distance = DistanceUtil.getDistance(latLng, currentLatLng);
							info.distance = (Math.round(distance));

							if (info.isOpen == 1) {
								if (info.serviceTotal > 0) {
									overlayOptions = new MarkerOptions().position(latLng)
											.icon(BitmapDescriptorFactory.fromResource(R.drawable.servicemark))
											.zIndex(5);
								} else {
									overlayOptions = new MarkerOptions().position(latLng)
											.icon(getNumberMarker(info.parkFreeCount)).zIndex(5);
								}
								totalOpen++;
							} else if (info.isOpen == 0) {
								overlayOptions = new MarkerOptions().position(latLng).icon(getNumberMarker(-1))
										.zIndex(5);

							}
							marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
							Bundle bundle = new Bundle();
							bundle.putSerializable("info", info);
							marker.setExtraInfo(bundle);
						}

						Toast.makeText(getActivity(), "����" + infos.size() + "��ǩԼͣ����," + totalOpen + "��ͣ�����п�λ",
								Toast.LENGTH_SHORT).show();

						// ����ͼ�Ƶ������һ����γ��λ��
						MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(latLng, 16);
						mBaiduMap.animateMapStatus(u);
					} else {
						Toast.makeText(getActivity(), "��ǰ��Χ����ǩԼͣ����", Toast.LENGTH_SHORT).show();
					}

				}
				// Message message = new Message();
				// if(arg0!=null&&arg0.version!=null){
				// long version = arg0.version;
				//
				// if(Constants.VERSION<version&&arg0.forceUpdate!=0){
				// message.arg1=2;
				// }else if(Constants.VERSION<version){
				// message.arg1=1;
				// }
				// message.obj = arg0;
				// }
				// versionHandler.sendMessage(message);
			}

			@Override
			public void onFailed(String message) {
				Log.e("checkVersion", message);
				// Message messageObj = new Message();
				// versionHandler.sendMessage(messageObj);
			}

		};

		httpRequestAni.execute();

	}

	@Override
	public void onStart() {
		if (mCurrentLantitude >= 0 && mCurrentLongitude > 0) {
			// center2myLoc();
		}

		// ����ͼ�㶨λ
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		// �������򴫸���
		myOrientationListener.start();

		System.out.println("===========>map onStart");

		super.onStart();

	}

	@Override
	public void onStop() {
		// �ر�ͼ�㶨λ
		mBaiduMap.setMyLocationEnabled(false);
		UIUtils.currentlatlng = mMapView.getMap().getMapStatus().target;
		UIUtils.currentZoom = mMapView.getMap().getMapStatus().zoom;
		mLocationClient.stop();

		// �رշ��򴫸���
		myOrientationListener.stop();

		System.out.println("===========>map onStop");
		slide.stopPlay();
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (dbHelper != null) {
			OpenHelperManager.releaseHelper();
			dbHelper = null;
		}
		// ��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
		if (mMapView != null)
			mMapView.onDestroy();
		// if (mIconMaker != null)
		// mIconMaker.recycle();

		for (BitmapDescriptor bmap : bubbleMarkers) {
			if (bmap != null) {
				bmap.recycle();
			}
		}

		// if (codingMaker != null)
		// codingMaker.recycle();
		if (baiduIcon != null)
			baiduIcon.recycle();
		// if (selectIcon != null)
		// selectIcon.recycle();

		mMapView = null;
		mBaiduMap = null;
		tParkInfo_LocEntity = null;

		System.out.println("===========>map onDestroy");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		clearMapClick();

		// ���Ը��ݶ���������������Ӧ�Ĳ���
		if (Constants.ResultCode.NAVATIGOR_START == resultCode) {
			Double navigateToLantitude = 0.0d;
			Double navigateToLongitude = 0.0d;
			String address = "";
			final OrderEntity order = (OrderEntity) data.getSerializableExtra("orderinfo");
			BaiduParkInfo baiduinfo = currentmarker == null ? null
					: (BaiduParkInfo) currentmarker.getExtraInfo().get("baidu");
			if (baiduinfo != null) {
				navigateToLantitude = baiduinfo.getLatitude();
				navigateToLongitude = baiduinfo.getLongitude();
				address = baiduinfo.getAddname();

			} else {

				TParkInfo_LocEntity selectedParkinfo = order.gettParkInfo_LocEntity();
				if (selectedParkinfo != null) {
					navigateToLantitude = selectedParkinfo.latitude;
					navigateToLongitude = selectedParkinfo.longitude;
					address = selectedParkinfo.parkInfo.address;
				} else {
					TParkInfoEntity infoEntity = order.getParkInfo();
					List<TParkInfo_LocEntity> locEntitys = infoEntity.latLngArray;
					boolean hasfound = false;
					if (locEntitys != null) {
						for (TParkInfo_LocEntity loc : locEntitys) {
							if (loc.isOpen == 1 && loc.type == 1) {
								navigateToLantitude = loc.latitude;
								navigateToLongitude = loc.longitude;
								address = infoEntity.address;
								hasfound = true;
								break;
							}
						}
					}

					if (!hasfound) {
						Toast.makeText(getActivity(), "û�дӸö����ҵ����ʵ�ͣ�������.", Toast.LENGTH_SHORT).show();
						return;
					}
				}

			}

			try {

				BNaviPoint startPoint = new BNaviPoint(mCurrentLongitude, mCurrentLantitude, locationName,
						CoordinateType.BD09_MC);
				BNaviPoint endPoint = new BNaviPoint(navigateToLongitude, navigateToLantitude,
						(address == null || address.trim().equals("")) ? SessionUtils.city : address,
						CoordinateType.BD09_MC);

				BaiduNaviManager.getInstance().launchNavigator(getActivity(), startPoint, endPoint,
						NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, // ��·��ʽ
						true, // ��ʵ����
						BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, // �����߲���
						new OnStartNavigationListener() { // ��ת����

							@Override
							public void onJumpToNavigator(Bundle configParams) {
								Intent intent = new Intent(getActivity(), NavigationActivity.class);
								if (order != null) {
									configParams.putSerializable("orderinfo", order);

								}
								UIUtils.backState = true;
								// Toast.makeText(getActivity(),
								// ""+UIUtils.backState,
								// Toast.LENGTH_SHORT).show();
								intent.putExtras(configParams);
								startActivityForResult(intent, 3);

							}

							@Override
							public void onJumpToDownloader() {
							}
						});
			} catch (Exception e) {
				Toast.makeText(getActivity(), "�쳣����,��ȷ����������͵�ͼ�ͻ����Ƿ�����.", Toast.LENGTH_SHORT).show();
			}

		} else if (Constants.ResultCode.ORDER_AUTH == resultCode) {

			final OrderEntity order = (OrderEntity) data.getSerializableExtra("orderinfo");
			if (order != null) {
				Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
				Bundle buidle = new Bundle();
				buidle.putBoolean("isRefresh", true);
				buidle.putSerializable("orderinfo", order);
				intent.putExtras(buidle);
				startActivityForResult(intent, 1);
			}
		} else if (Constants.ResultCode.NAVATIGOR_END == resultCode) { // ������������
			center2myLoc();
		} else if (resultCode == Constants.ResultCode.ORDER_LIST_RELOAD) {
			Intent intent = new Intent(getActivity(), OrderActivity.class);
			intent.putExtras(data.getExtras());
			startActivityForResult(intent, 0);
		} else if (Constants.ResultCode.NON_ORDER == resultCode) {
			Double navigateToLantitude = 0.0d;
			Double navigateToLongitude = 0.0d;
			String address = "";
			final OrderEntity order = (OrderEntity) data.getSerializableExtra("orderinfo");
			TParkInfo_LocEntity selectedParkinfo = order.gettParkInfo_LocEntity();
			if (selectedParkinfo != null) {
				navigateToLantitude = selectedParkinfo.latitude;
				navigateToLongitude = selectedParkinfo.longitude;
				address = selectedParkinfo.parkInfo.address;
			} else {
				TParkInfoEntity infoEntity = order.getParkInfo();
				List<TParkInfo_LocEntity> locEntitys = infoEntity.latLngArray;
				boolean hasfound = false;
				if (locEntitys != null) {
					for (TParkInfo_LocEntity loc : locEntitys) {
						if (loc.isOpen == 1 && loc.type == 1) {
							navigateToLantitude = loc.latitude;
							navigateToLongitude = loc.longitude;
							address = infoEntity.address;
							hasfound = true;
							break;
						}
					}
				}

				if (!hasfound) {
					Toast.makeText(getActivity(), "û�дӸö����ҵ����ʵ�ͣ�������.", Toast.LENGTH_SHORT).show();
					return;
				}
			}

			try {

				BNaviPoint startPoint = new BNaviPoint(mCurrentLongitude, mCurrentLantitude, locationName,
						CoordinateType.BD09_MC);
				BNaviPoint endPoint = new BNaviPoint(navigateToLongitude, navigateToLantitude,
						(address == null || address.trim().equals("")) ? SessionUtils.city : address,
						CoordinateType.BD09_MC);
				BaiduNaviManager.getInstance().launchNavigator(getActivity(), startPoint, endPoint,
						NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, // ��·��ʽ
						true, // ��ʵ����
						BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, // �����߲���
						new OnStartNavigationListener() { // ��ת����

							@Override
							public void onJumpToNavigator(Bundle configParams) {
								Intent intent = new Intent(getActivity(), NavigationActivity.class);
								if (order != null) {
									configParams.putSerializable("orderinfo", order);

								}
								UIUtils.backState = true;
								intent.setFlags(1);
								intent.putExtras(configParams);
								startActivityForResult(intent, 4);

							}

							@Override
							public void onJumpToDownloader() {
							}
						});
			} catch (Exception e) {
				Toast.makeText(getActivity(), "�쳣����,��ȷ����������͵�ͼ�ͻ����Ƿ�����.", Toast.LENGTH_SHORT).show();
			}

		} else if (Constants.ResultCode.NO_ORDER == requestCode) {

			center2myLoc();
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		// ��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onResume();
		if (Constants.NETFLAG) {
			if (UIUtils.currentlatlng != null && !UIUtils.backState) {

				LatLng ll = new LatLng(UIUtils.currentlatlng.latitude, UIUtils.currentlatlng.longitude);
				float zoom = 0;
				if (UIUtils.currentZoom >= 1 && UIUtils.currentZoom <= 19) {
					zoom = UIUtils.currentZoom;

				} else
					zoom = 15;
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, zoom);
				mBaiduMap.animateMapStatus(u, 900);
				// if (UIUtils.myserchlatlng != null) {
				// sreachNearbyParkingNoMove(UIUtils.myserchlatlng.latitude,
				// UIUtils.myserchlatlng.longitude, 1);
				// } else
				sreachNearbyParkingNoMove(UIUtils.currentlatlng.latitude, UIUtils.currentlatlng.longitude, 1);

			} else if (UIUtils.backState) {

				UIUtils.backState = false;

			}
			// ��ȡ�����Ϣ
			getBanner();
		}

		MainActivity activity = (MainActivity) getActivity();
		// activity.topBarFragment.resetStatus();

	}

	@Override
	public void onPause() {
		super.onPause();
		// ��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onPause();
	}

	private void InitiPoi() {
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

	}

	private void startPoi(double lantitude, double longitude) {

		LatLng currentlatlng = new LatLng(lantitude, longitude);
		mPoiSearch.searchNearby((new PoiNearbySearchOption()).keyword("ͣ����").radius(Constants.BAIDUPOIDISTANCE)
				.location(currentlatlng).pageCapacity(10));

	}

	// Poi�������
	OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
		public void onGetPoiResult(com.baidu.mapapi.search.poi.PoiResult result) {
			if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {

				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {

				// ��Ӱٶ�ͣ����������
				bindBaiduPark(result);
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

				// // ������ؼ����ڱ���û���ҵ����������������ҵ�ʱ�����ذ����ùؼ�����Ϣ�ĳ����б�
				// String strInfo = "��";
				// for (CityInfo cityInfo : result.getSuggestCityList()) {
				// strInfo += cityInfo.city;
				// strInfo += ",";
				// }
				// strInfo += "�ҵ����";
				// Toast.makeText(getActivity(), strInfo, Toast.LENGTH_LONG)
				// .show();
			}

		};

		public void onGetPoiDetailResult(com.baidu.mapapi.search.poi.PoiDetailResult arg0) {

		};
	};

	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			Toast.makeText(getActivity(), "���", Toast.LENGTH_SHORT).show();
			return true;
		}
	}

	OnMarkerClickListener onmarkerclick = new OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(final Marker marker) {
			// �����֮ǰ��mark
			clearMapClick();
			// ���marker�е�����
			currentmarker = marker;
			MapMakers info = null;
			if (marker.getExtraInfo() != null && marker.getExtraInfo().get("info") != null) {
				info = (MapMakers) marker.getExtraInfo().get("info");
			}
			if (info == null) {
				// if (oldmarker != null) {
				// oldmarker.setIcon(codingMaker);
				//
				// }
				String mymakerstring = marker.getExtraInfo().getString("location");
				if (mymakerstring != null && mymakerstring.equals("location")) {
					sreachNearbyParkingNoMove(mCurrentLantitude, mCurrentLongitude, 1);
					return true;
				} else if (mymakerstring != null && mymakerstring.equals("location1")) {
					if (UIUtils.myserchlatlng == null) {
						// UIUtils.myserchlatlng =
						// mMapView.getMap().getMapStatus().target;
						return true;
					}
					sreachNearbyParkingNoMove(UIUtils.myserchlatlng.latitude, UIUtils.myserchlatlng.longitude, 1);
					centremarker = null;
					showlocationmarker(UIUtils.myserchlatlng.latitude, UIUtils.myserchlatlng.longitude);
					return true;

				}
				// �ٶ�ͣ��������¼�
				BaiduParkInfo baiduinfo = (BaiduParkInfo) marker.getExtraInfo().get("baidu");
				Baiduparkgo.setVisibility(View.VISIBLE);
				linearLayout.setVisibility(View.GONE);
				yuyueLayout.setVisibility(View.GONE);
				mMarkerInfoLy.setVisibility(View.GONE);
				// ����һ��TextView�û��ڵ�ͼ����ʾInfoWindow
				TextView location = new TextView(getActivity().getApplicationContext());

				location.setBackgroundResource(R.drawable.location_tips);
				location.setPadding(20, 20, 20, 30);
				location.setText(baiduinfo.getName());

				// �¼�

				// ��marker���ڵľ�γ�ȵ���Ϣת������Ļ�ϵ�����
				final LatLng ll = marker.getPosition();
				Point p = mBaiduMap.getProjection().toScreenLocation(ll);
				p.y -= 47;
				LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
				// Ϊ������InfoWindow��ӵ���¼�
				InfoWindow mInfoWindow = new InfoWindow(markview, llInfo, -50);
				// ��ʾInfoWindow
				// mBaiduMap.showInfoWindow(mInfoWindow);
				emMarkerInfoLy.setVisibility(View.VISIBLE);

				elsepopupInfo(emMarkerInfoLy, baiduinfo);
				return true;

			}
			// marker.setIcon(selectIcon);
			// if (oldmarker != null && oldmarker != marker) {
			// oldmarker.setIcon(codingMaker);
			//
			// }
			// oldmarker = marker;
			Baiduparkgo.setVisibility(View.GONE);
			// ����һ��TextView�û��ڵ�ͼ����ʾInfoWindow
			// TextView location = new TextView(getActivity()
			// .getApplicationContext());
			// location.setBackgroundResource(R.drawable.location_tips);
			// location.setPadding(20, 20, 20, 30);
			// location.setGravity(Gravity.CENTER);
			// location.setText("������...");

			// TextView marker_park_name=(TextView)
			// markview.findViewById(R.id.marker_park_name);
			// TextView marker_park_info=(TextView)
			// markview.findViewById(R.id.marker_park_info);
			// ImageView marker_parkimage=(ImageView)
			// markview.findViewById(R.id.marker_parkimage);
			// imageloader.displayImage("drawable://" + R.drawable.push,
			// marker_parkimage);
			// marker_parkimage.setImageDrawable(getResources().getDrawable(R.drawable.push));
			// marker_parkimage.setImageBitmap();
			marker_park_name.setText("������...");
			marker_park_info.setText("");
			// ��marker���ڵľ�γ�ȵ���Ϣת������Ļ�ϵ�����
			final LatLng ll = marker.getPosition();
			Point p = mBaiduMap.getProjection().toScreenLocation(ll);
			p.y -= 47;
			LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
			// Ϊ������InfoWindow��ӵ���¼�
			InfoWindow mInfoWindow = new InfoWindow(markview, llInfo, -50);
			// ��ʾInfoWindow
			mBaiduMap.showInfoWindow(mInfoWindow);
			yuyueLayout.setVisibility(View.VISIBLE);
			yuyueButton.setEnabled(false);
			mashangquButton.setEnabled(true);

			// ִ��һ������ȥ��ȡ�����ͣ����
			httpTaskForFetchParkInfo(mInfoWindow, marker_park_name, info, marker_park_info, marker_parkimage);

			return true;
		}

	};

	private void httpTaskForFetchParkInfo(final InfoWindow mInfoWindow, final TextView parkname,
			final MapMakers mapMaker, final TextView pricetext, final ImageView imageview) {

		// ��ͼƬ���ÿ�
		// imageloader.displayImage("", imageview);

		// pricetext.setText("");
		HttpRequest<TParkInfo_LocEntity> httpRequestAni = new HttpRequest<TParkInfo_LocEntity>(
				"/a/ans/" + mapMaker.parkLocId, new TypeToken<TParkInfo_LocEntity>() {
				}.getType()) {
			@Override
			public void onSuccess(TParkInfo_LocEntity info) {

				if (info != null) {
					
					// text
					/*
					 * List<TParkService> list=new ArrayList<TParkService>();
					 * for(int i=0;i<4;i++) { TParkService item=new
					 * TParkService(); item.serviceName="��ѩ"; item.serviceId=i;
					 * list.add(item); } info.parkInfo.services=list;
					 */
					// ������ϸ��Ϣ����Ϊ�ɼ�
					parkname.setText(info.parkInfo.parkname);
					info.distance = mapMaker.distance;
					pricetext.setText(info.parkInfo.detail);
					if (info != null && info.parkInfo != null && info.parkInfo.imgUrlArray.size() > 0) {

						/*
						 * imageloader.loadImage(info.parkInfo.imgUrlArray.get(0
						 * ).imgUrlHeader +
						 * info.parkInfo.imgUrlArray.get(0).imgUrlPath, new
						 * SimpleImageLoadingListener() { public void
						 * onLoadingComplete(String imageUri, android.view.View
						 * view, android.graphics.Bitmap loadedImage) {
						 * imageview.setImageBitmap(loadedImage);
						 * //imageView����Ҫ��ʾ��imageview�ؼ����󣬲����ļ�����//���õ� }; } );
						 */

						imageloader.displayImage(
								info.parkInfo.imgUrlArray.get(0).imgUrlHeader
										+ info.parkInfo.imgUrlArray.get(0).imgUrlPath,
								marker_parkimage, options, new SimpleImageLoadingListener() {
							public void onLoadingComplete(String imageUri, android.view.View view,
									android.graphics.Bitmap loadedImage) {
								marker_parkimage.setImageBitmap(loadedImage); // imageView����Ҫ��ʾ��imageview�ؼ����󣬲����ļ�����//���õ�
							};
						});
						if (info.parkInfo.services != null && info.parkInfo.services.size() > 0) {
							marker_serviceimage.setVisibility(View.VISIBLE);
						} else {
							marker_serviceimage.setVisibility(View.GONE);
						}
					}

					// �������Ϊ0��֤��û���˴����ǻ��Ǹ�һ��Ĭ�ϸ߷ְ�
					if (info.parkInfo.averagerat <= 0) {
						info.parkInfo.averagerat = 4;
					}

					if (info.isOpen == 1) {
						Drawable drawable = getResources().getDrawable(R.drawable.cashsucess);
						drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); // ���ñ߽�
						parkname.setCompoundDrawables(null, null, drawable, null);
						if (yuyueLayout != null) {
							yuyueButton.setEnabled(true);
						}
					} else {
						Drawable drawable = getResources().getDrawable(R.drawable.parkstop);
						drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); // ���ñ߽�
						parkname.setCompoundDrawables(null, null, drawable, null);
					}

					mMarkerInfoLy.setVisibility(View.VISIBLE);
					// �����̼���ϢΪ��ϸ��Ϣ����������Ϣ
					popupInfo(mMarkerInfoLy, info);
					if (linearLayout != null) {
						linearLayout.setVisibility(View.GONE);
					}
				} else {
					parkname.setText("�޷���ѯ����ͣ������Ϣ");
				}

				mBaiduMap.hideInfoWindow();
				mBaiduMap.showInfoWindow(mInfoWindow);
			}

			@Override
			public void onFailed(String message) {
				Log.e("checkVersion", message);
				parkname.setText("��ȡͣ������Ϣʧ��");
			}

		};

		httpRequestAni.execute();
	}

	// �󶨰ٶȵ�ͼ���
	private void bindBaiduPark(com.baidu.mapapi.search.poi.PoiResult result) {
		List<PoiInfo> listpoi = result.getAllPoi();
		LatLng BaidulatLng = null;
		Marker marker = null;
		OverlayOptions overlayOptions = null;
		BaiduParkInfo baiduparkinfo = null;
		for (PoiInfo poiinfo : listpoi) {

			// λ��
			BaidulatLng = poiinfo.location;

			// ͼ��
			overlayOptions = new MarkerOptions().position(BaidulatLng).icon(baiduIcon).zIndex(5);

			baiduparkinfo = new BaiduParkInfo(poiinfo.uid, BaidulatLng.latitude, BaidulatLng.longitude, poiinfo.name,
					poiinfo.address);
			marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
			Bundle bundle = new Bundle();
			bundle.putSerializable("baidu", baiduparkinfo);
			marker.setExtraInfo(bundle);

		}
	}

	/**
	 * �ҵ���λʣ��������Ӧ��icon
	 * 
	 * @param number
	 * @return
	 */
	public BitmapDescriptor getNumberMarker(int number) {
		if (number > 0 && number < 10) {
			if (bubbleMarkers[number - 1] == null) {
				switch (number) {
				case 1:
					bubbleMarkers[number - 1] = BitmapDescriptorFactory.fromResource(R.drawable.bubble_1);
					break;
				case 2:
					bubbleMarkers[number - 1] = BitmapDescriptorFactory.fromResource(R.drawable.bubble_2);
					break;
				case 3:
					bubbleMarkers[number - 1] = BitmapDescriptorFactory.fromResource(R.drawable.bubble_3);
					break;
				case 4:
					bubbleMarkers[number - 1] = BitmapDescriptorFactory.fromResource(R.drawable.bubble_4);
					break;
				case 5:
					bubbleMarkers[number - 1] = BitmapDescriptorFactory.fromResource(R.drawable.bubble_5);
					break;
				case 6:
					bubbleMarkers[number - 1] = BitmapDescriptorFactory.fromResource(R.drawable.bubble_6);
					break;
				case 7:
					bubbleMarkers[number - 1] = BitmapDescriptorFactory.fromResource(R.drawable.bubble_7);
					break;
				case 8:
					bubbleMarkers[number - 1] = BitmapDescriptorFactory.fromResource(R.drawable.bubble_8);
					break;
				case 9:
					bubbleMarkers[number - 1] = BitmapDescriptorFactory.fromResource(R.drawable.locatedmap);
					break;
				}

			}

			return bubbleMarkers[number - 1];
		} else if (number >= 10 && number < 100) {
			int numTem = number / 10;
			if (bubbleMarkers[numTem - 1] == null) {
				switch (numTem) {
				case 1:
					bubbleMarkers[9] = BitmapDescriptorFactory.fromResource(R.drawable.bubble_10);
					break;
				case 2:
					bubbleMarkers[10] = BitmapDescriptorFactory.fromResource(R.drawable.bubble_20);
					break;
				case 3:
					bubbleMarkers[11] = BitmapDescriptorFactory.fromResource(R.drawable.bubble_30);
					break;
				case 4:
					bubbleMarkers[12] = BitmapDescriptorFactory.fromResource(R.drawable.bubble_40);
					break;
				case 5:
					bubbleMarkers[13] = BitmapDescriptorFactory.fromResource(R.drawable.bubble_50);
					break;
				case 6:
					bubbleMarkers[14] = BitmapDescriptorFactory.fromResource(R.drawable.bubble_60);
					break;
				case 7:
					bubbleMarkers[15] = BitmapDescriptorFactory.fromResource(R.drawable.bubble_70);
					break;
				case 8:
					bubbleMarkers[16] = BitmapDescriptorFactory.fromResource(R.drawable.bubble_80);
					break;
				case 9:
					bubbleMarkers[17] = BitmapDescriptorFactory.fromResource(R.drawable.bubble_90);
					break;
				}
			}

			return bubbleMarkers[numTem + 8];

		} else if (number >= 100) {
			if (bubbleMarkers[17] == null) {
				bubbleMarkers[17] = BitmapDescriptorFactory.fromResource(R.drawable.bubble_90);
			}
			return bubbleMarkers[17];
		} else if (number < 0) {

			if (bubbleMarkers[19] == null) {
				bubbleMarkers[19] = BitmapDescriptorFactory.fromResource(R.drawable.maker_noopen);
			}
			return bubbleMarkers[19];

		}

		else {
			if (bubbleMarkers[18] == null) {
				bubbleMarkers[18] = BitmapDescriptorFactory.fromResource(R.drawable.maker);
			}
			return bubbleMarkers[18];
		}
	}

	public void getMapListData() {
		LatLng la = mBaiduMap.getMapStatus().target;
		if (la != null)
			getParkListDate(la.latitude, la.longitude);
	}

	public void getParkListDate(final double lantitude, final double longitude) {

		// TODO Auto-generated method stub
		UIUtils.currentlatlng = new LatLng(lantitude, longitude);
		UIUtils.mylocallatlng = new LatLng(mCurrentLantitude, mCurrentLongitude);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.hide(topBarFragment).addToBackStack(null);
		transaction.replace(R.id.fragment_map, mapparklistFragment).addToBackStack(null).commit();

		// UIUtils.currentlatlng=mMapView.getMap().getMapStatus().target;
		// Intent intent=new Intent();
		// intent.setClass(getActivity(), MapParkListviewActivity.class);
		// startActivity(intent);
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
		// HttpRequestAni<CommFindEntity<TParkInfo_LocEntity>> httpRequestAni =
		// new HttpRequestAni<CommFindEntity<TParkInfo_LocEntity>>(
		// getActivity(),"/a/ans/pagelist?a=" + lantitude + "&n=" + longitude,
		// new TypeToken<CommFindEntity<TParkInfo_LocEntity>>() {
		// }.getType()){
		//
		// @Override
		// public void callback(CommFindEntity<TParkInfo_LocEntity> arg0) {
		//
		// List<TParkInfo_LocEntity> mydata;
		// if (arg0 != null&&arg0.getRowCount()>0) {
		//
		// mydata=arg0.getResult();
		//
		//
		//
		//
		// LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
		// for(TParkInfo_LocEntity info:mydata)
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
		// UIUtils.tempParkinfo=mydata;
		// Intent intent=new Intent();
		// intent.setClass(getActivity(), MapParkListviewActivity.class);
		// startActivity(intent);
		//
		// }else
		// {
		// Toast.makeText(getActivity(), "��������ʧ��", Toast.LENGTH_SHORT).show();
		//
		// }
		//
		// }
		//
		//
		// };
		//
		// httpRequestAni.execute();
	}

	/**
	 * ��ʼ��ͼ�� ֻ�����㣬���ƶ������ a=0��ʾ����ʾ��ǵ�marker 1��ʾ��ʾ
	 */
	public void sreachNearbyParkingNoMove(final double lantitude, final double longitude, int a) {

		mBaiduMap.clear();
		// ��ʾ��ǰλ�õ�marker
		locamarker = null;
		showlocationmarker();
		if (a == 1 && UIUtils.myserchlatlng != null) {
			// UIUtils.myserchlatlng = new LatLng(lantitude, longitude);
			centremarker = null;
			showlocationmarker(UIUtils.myserchlatlng.latitude, UIUtils.myserchlatlng.longitude);
		}
		// �����ٶ�poi����
		// startPoi(lantitude, longitude);
		HttpRequest<List<MapMakers>> httpRequestAni = new HttpRequest<List<MapMakers>>(
				"/a/ans/simple?a=" + lantitude + "&n=" + longitude, new TypeToken<List<MapMakers>>() {
				}.getType()) {
			@Override
			public void onSuccess(List<MapMakers> infos) {

				if (infos != null) {

					if (infos.size() > 0) {

						LatLng latLng = null;
						Marker marker = null;
						OverlayOptions overlayOptions = null;
						LatLng currentLatLng = new LatLng(mCurrentLantitude, mCurrentLongitude);

						int totalOpen = 0;
						for (MapMakers info : infos) {
							// λ��
							latLng = new LatLng(info.latitude, info.longitude);
							double distance = DistanceUtil.getDistance(latLng, currentLatLng);
							info.distance = (Math.round(distance));

							if (info.isOpen == 1) {
								// �ж��Ƿ��з���
								if (info.serviceTotal > 0) {
									overlayOptions = new MarkerOptions().position(latLng)
											.icon(BitmapDescriptorFactory.fromResource(R.drawable.servicemark))
											.zIndex(5);
								} else {
									overlayOptions = new MarkerOptions().position(latLng)
											.icon(getNumberMarker(info.parkFreeCount)).zIndex(5);
								}
								totalOpen++;
							} else if (info.isOpen == 0) {
								overlayOptions = new MarkerOptions().position(latLng).icon(getNumberMarker(-1))
										.zIndex(5);

							}
							marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
							Bundle bundle = new Bundle();
							bundle.putSerializable("info", info);
							marker.setExtraInfo(bundle);
						}

						Toast.makeText(getActivity(), "����" + infos.size() + "��ǩԼͣ����," + totalOpen + "��ͣ�����п�λ",
								Toast.LENGTH_SHORT).show();

					} else {
						Toast.makeText(getActivity(), "��ǰ��Χ����ǩԼͣ����", Toast.LENGTH_SHORT).show();
					}
					// ����ͼ�Ƶ���ǰ������λ��
					LatLng llserch = new LatLng(lantitude, longitude);
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llserch, 16);
					mBaiduMap.animateMapStatus(u);

				}
				// Message message = new Message();
				// if(arg0!=null&&arg0.version!=null){
				// long version = arg0.version;
				//
				// if(Constants.VERSION<version&&arg0.forceUpdate!=0){
				// message.arg1=2;
				// }else if(Constants.VERSION<version){
				// message.arg1=1;
				// }
				// message.obj = arg0;
				// }
				// versionHandler.sendMessage(message);
			}

			@Override
			public void onFailed(String message) {
				Log.e("checkVersion", message);
				// Message messageObj = new Message();
				// versionHandler.sendMessage(messageObj);
			}

		};

		httpRequestAni.execute();

	}

	// ��ʾ��λ���marker
	private void showlocationmarker(final double lantitude, final double longitude) {
		if (centremarker != null)
			centremarker.remove();
		centremarker = null;
		LatLng ll2 = new LatLng(lantitude, longitude);
		if (centremarker == null) {
			locaoverlayOptions1 = new MarkerOptions().position(ll2).icon(getNumberMarker(9)).zIndex(7);
			centremarker = (Marker) (mBaiduMap.addOverlay(locaoverlayOptions1));
			Bundle bundle = new Bundle();
			bundle.putString("location", "location1");
			centremarker.setExtraInfo(bundle);

		} else {
			centremarker.setPosition(ll2);

		}

	}

	// ��ʾ��λ���marker
	private void showlocationmarker() {
		if (locamarker != null)
			locamarker.remove();
		locamarker = null;
		LatLng ll1 = new LatLng(mCurrentLantitude, mCurrentLongitude);
		if (locamarker == null) {
			locaoverlayOptions = new MarkerOptions().position(ll1).icon(getNumberMarker(9)).zIndex(7);
			locamarker = (Marker) (mBaiduMap.addOverlay(locaoverlayOptions));
			Bundle bundle = new Bundle();
			bundle.putString("location", "location");
			locamarker.setExtraInfo(bundle);

		} else {
			locamarker.setPosition(ll1);

		}

	}

	public void showbackserch() {
		btn_mapserchpark.setVisibility(View.VISIBLE);

	}

	public void getBanner() {
		String userid = "";
		if (SessionUtils.isLogined()) {
			userid = "?userId=" + SessionUtils.loginUser.userid;
		} else
			userid = "?userId=0";
		HttpRequest<List<TTopBannner>> httpRequestAni = new HttpRequest<List<TTopBannner>>("/a/topbanner/map" + userid,
				new TypeToken<List<TTopBannner>>() {
				}.getType()) {
			@Override
			public void onSuccess(List<TTopBannner> arg0) {
				if (arg0 != null)
					mybanner = arg0;
				banner_total_line.setVisibility(View.GONE);
				if (arg0.size() > 0)
					Bindbanner(arg0);
			}

			@Override
			public void onFailed(String message) {
				Log.e("getBanner", message);

			}

		};

		httpRequestAni.execute();

	}

	public void Bindbanner(List<TTopBannner> arg0) {
		LinearLayout tx = (LinearLayout) view.findViewById(R.id.ads_line);
		banner_total_line.setVisibility(View.VISIBLE);
		List<String> imageUris = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arg0.size(); i++) {
			imageUris.add(arg0.get(i).imgUrlHeader + arg0.get(i).imgUrlPath);
			sb.append(arg0.get(i).detail);
			if (arg0.get(i).autoappear == 1) {
				if (tx.getVisibility() == View.GONE) {
					tx.setVisibility(View.VISIBLE);
					adsshow.setBackgroundResource(R.drawable.iconuparrow);
				}
			}
		}
		if (imageUris.size() < 1) {
		} else {
			// Toast.makeText(getActivity(), "a"+imageUris.size(),
			// Toast.LENGTH_SHORT).show();
			slide.setImageUris(imageUris);
			slide.startPlay();
		}
		bannertxt.setText(sb.toString());

	}

}
