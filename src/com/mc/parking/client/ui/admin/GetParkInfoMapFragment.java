package com.mc.parking.client.ui.admin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
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
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.client.Constants;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.utils.DBHelper;
import com.mc.parking.client.utils.SessionUtils;

;

public class GetParkInfoMapFragment extends Fragment {

	private BitmapDescriptor mIconMaker, IconMaker, BaiduIcon, selectIcon,
			exitIcon, entreIcon;
	
	public final static String KEY_LOCATION_ENTITY="KEY_LOCATION_ENTITY";

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	OverlayOptions overlayOptions = null;
	Marker currentmarker = null;
	Marker ClicMarker = null;
	// HashMap<String, Marker> inhashmap = new HashMap<String, Marker>();
	// HashMap<String, Marker> outhashmap = new HashMap<String, Marker>();
	// ArrayList<LatLng> inlist=new ArrayList<LatLng>();
	// ArrayList<LatLng> outlist=new ArrayList<LatLng>();
	CheckBox checkbox1, checkbox2;
	List<Marker> inmarkerlist = new ArrayList<Marker>();
	List<Marker> outmarkerlist = new ArrayList<Marker>();
	
	
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	boolean isFirstLoc = true;// 是否首次定位
	// tool
	LinearLayout toolLayout = null;
	// Button INButton,OutButton,CButton,CButton2,ImageButton;
	TextView INButton, OutButton, CButton;
	// move 工具
	TextView uptext, downtext, lefttext, righttext;
	RelativeLayout movelayout = null;
	// 标记过后点击marker的工具条
	LinearLayout markerclicLayout;
	TextView markerremove, markerclose;

	LatLng currentlatlng = null;

	List<LatLng> latlist = new ArrayList<LatLng>();

	// HashMap<Integer, Marker> Inmarkermap = new HashMap<Integer, Marker>();
	// HashMap<Integer, Marker> Outmarkermap = new HashMap<Integer, Marker>();

	List<TParkInfo_LocEntity> parkloclist = new ArrayList<TParkInfo_LocEntity>();

	TParkInfo_LocEntity tempparkloc;
	
	private ImageButton zoomOutBtn, zoomInBtn,mylocBtn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View view = inflater.inflate(R.layout.ac_getpark_map, container, false);
		view = inti(view);

		return view;
	}

	private View inti(View view) {

		// 百度地图定义

		FrameLayout viewFrame = (FrameLayout) view
				.findViewById(R.id.id_getpark_bmapView);
		LatLng p = new LatLng(SessionUtils.lastTimeLantitude,
				SessionUtils.lastTimeLongitude);

		BaiduMapOptions options = new BaiduMapOptions();
		options.zoomControlsEnabled(false);
		options.mapStatus(new MapStatus.Builder().target(p).zoom(20).build());

		mMapView = new MapView(getActivity(), options);
		viewFrame.addView(mMapView);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		mIconMaker = BitmapDescriptorFactory.fromResource(R.drawable.newmyloc);
		exitIcon = BitmapDescriptorFactory
				.fromResource(R.drawable.newmaker_exit);
		entreIcon = BitmapDescriptorFactory
				.fromResource(R.drawable.newmarker_entrance);
		selectIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_h);
		BaiduIcon = BitmapDescriptorFactory.fromResource(R.drawable.myloc);
		// 定位初始化
		mLocClient = new LocationClient(getActivity());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型gcj02 bd09ll
		option.setScanSpan(500);
		option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);
		mLocClient.setLocOption(option);
		mLocClient.start();

		mCurrentMarker = null;
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, null));
		mBaiduMap.setOnMapClickListener(ll1);
		mBaiduMap.setOnMarkerClickListener(ll2);

		// 控件定义

		// tool
		INButton = (TextView) view.findViewById(R.id.id_getpark_in);
		OutButton = (TextView) view.findViewById(R.id.id_getpark_out);
		CButton = (TextView) view.findViewById(R.id.id_getpark_cancel);
		checkbox1 = (CheckBox) view.findViewById(R.id.add_map_mode);
		// ImageButton=(Button)view.findViewById(R.id.id_getpark_ok);
		checkbox2 = (CheckBox) view.findViewById(R.id.add_map_mode1);
		// move tool
		uptext = (TextView) view.findViewById(R.id.map_move_up);
		downtext = (TextView) view.findViewById(R.id.map_move_down);
		lefttext = (TextView) view.findViewById(R.id.map_move_left);
		righttext = (TextView) view.findViewById(R.id.map_move_right);
		movelayout = (RelativeLayout) view.findViewById(R.id.map_toolbar_move);
		markerclicLayout = (LinearLayout) view
				.findViewById(R.id.getpark_marker_clic);

		markerremove = (TextView) view
				.findViewById(R.id.id_getpark_mark_remove);
		markerclose = (TextView) view
				.findViewById(R.id.id_getpark_marker_close);
		zoomOutBtn = (ImageButton) view.findViewById(R.id.zoomout_park);
		zoomInBtn = (ImageButton) view.findViewById(R.id.zoomin_park);
		mylocBtn=(ImageButton)view.findViewById(R.id.btn_maplocal_park);
		
		
		zoomOutBtn.setOnClickListener(ll3);
		zoomInBtn.setOnClickListener(ll3);
		mylocBtn.setOnClickListener(ll3);
		

		markerremove.setOnClickListener(ll3);
		markerclose.setOnClickListener(ll3);

		uptext.setOnClickListener(ll3);
		downtext.setOnClickListener(ll3);
		righttext.setOnClickListener(ll3);
		lefttext.setOnClickListener(ll3);

		INButton.setOnClickListener(ll3);
		OutButton.setOnClickListener(ll3);
		CButton.setOnClickListener(ll3);

		checkbox1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
					toolLayout.setVisibility(View.GONE);
					markerclicLayout.setVisibility(View.GONE);
					mBaiduMap.hideInfoWindow();
					Toast.makeText(getActivity(), "已关闭选点功能", Toast.LENGTH_SHORT)
							.show();

				} else {

					Toast.makeText(getActivity(), "已开启选点功能", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		// ImageButton.setOnClickListener(ll3);

		checkbox2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
					Toast.makeText(getActivity(), "已切换成正常模式",
							Toast.LENGTH_SHORT).show();

				} else {
					mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
					Toast.makeText(getActivity(), "已切换成卫星模式",
							Toast.LENGTH_SHORT).show();

				}

			}
		});

		toolLayout = (LinearLayout) view.findViewById(R.id.getpark_tool);
		binddata();
		
		return view;

	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			currentlatlng = new LatLng(location.getLatitude(),
					location.getLongitude());

			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);				
				centertoLoc();

			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	public void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	public void onResume() {
		mMapView.onResume();
		super.onResume();

	}

	@Override
	public void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	private OnMapClickListener ll1 = new OnMapClickListener() {

		@Override
		public boolean onMapPoiClick(MapPoi arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onMapClick(LatLng latLng) {
			// TODO Auto-generated method stub

			markerclicLayout.setVisibility(View.GONE);

			toolLayout.setVisibility(View.VISIBLE);
			mBaiduMap.hideInfoWindow();
			movelayout.setVisibility(View.GONE);

			// if(checkbox1.isChecked())
			// {
			// overlayOptions = new MarkerOptions().position(latLng)
			// .icon(mIconMaker).zIndex(5);
			// Marker marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
			// Bundle bundle = new Bundle();
			// currentmarker=marker;
			// toolLayout.setVisibility(View.VISIBLE);
			// movelayout.setVisibility(View.VISIBLE);
			//
			// }else
			// {
			// movelayout.setVisibility(View.GONE);
			// }

		}

	};

	private OnMarkerClickListener ll2 = new OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker arg0) {
			// TODO Auto-generated method stub
			String parkmode = arg0.getExtraInfo().get("mode").toString();
			String parknumber = arg0.getExtraInfo().get("number").toString();

			movelayout.setVisibility(View.VISIBLE);
			markerclicLayout.setVisibility(View.VISIBLE);

			toolLayout.setVisibility(View.GONE);
			currentmarker = arg0;
			// 生成一个TextView用户在地图中显示InfoWindow
			// 生成一个TextView用户在地图中显示InfoWindow
			TextView location = new TextView(getActivity()
					.getApplicationContext());
			location.setBackgroundResource(R.drawable.location_tips);
			location.setPadding(30, 20, 30, 50);
			location.setText(parkmode + parknumber);
			// 将marker所在的经纬度的信息转化成屏幕上的坐标
			final LatLng ll = arg0.getPosition();
			Point p = mBaiduMap.getProjection().toScreenLocation(ll);
			p.y -= 47;
			LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
			// 为弹出的InfoWindow添加点击事件
			InfoWindow mInfoWindow = new InfoWindow(location, llInfo, -30);
			// 显示InfoWindow
			mBaiduMap.showInfoWindow(mInfoWindow);

			// if(arg0.getExtraInfo()==null||arg0.getExtraInfo().get("mode")==null||arg0.getExtraInfo().get("number")==null)
			// {
			// if(checkbox1.isChecked())
			// toolLayout.setVisibility(View.VISIBLE);
			// movelayout.setVisibility(View.VISIBLE);
			// markerclicLayout.setVisibility(View.GONE);
			// }else
			// {
			// if(checkbox1.isChecked())
			// markerclicLayout.setVisibility(View.VISIBLE);
			// String parkmode=arg0.getExtraInfo().get("mode").toString();
			// String parknumber=arg0.getExtraInfo().get("number").toString();
			//
			// movelayout.setVisibility(View.GONE);
			//
			// // 生成一个TextView用户在地图中显示InfoWindow
			// // 生成一个TextView用户在地图中显示InfoWindow
			// TextView location = new TextView(getActivity()
			// .getApplicationContext());
			// location.setBackgroundResource(R.drawable.location_tips);
			// location.setPadding(30, 20, 30, 50);
			// location.setText(Bimp.templistbean.get(0).getCompanyName()+parkmode+parknumber);
			// // 将marker所在的经纬度的信息转化成屏幕上的坐标
			// final LatLng ll = arg0.getPosition();
			// Point p = mBaiduMap.getProjection().toScreenLocation(ll);
			// p.y -= 47;
			// LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
			// // 为弹出的InfoWindow添加点击事件
			// InfoWindow mInfoWindow = new InfoWindow(location, llInfo, -30);
			// // 显示InfoWindow
			// mBaiduMap.showInfoWindow(mInfoWindow);
			// }

			return true;
		}
	};

	OnClickListener ll3 = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.id_getpark_out:

				// movelayout.setVisibility(View.GONE);
				// outmarkerlist.add(currentmarker);
				// Bundle bundle = new Bundle();
				// bundle.putString("mode", "出口");
				//
				// bundle.putInt("number",outmarkerlist.size());
				//
				//
				// currentmarker.setExtraInfo(bundle);
				// toolLayout.setVisibility(View.GONE);
				// currentmarker.setIcon(exitIcon);

				if (Isaddloca()) {
					overlayOptions = new MarkerOptions()
							.position(currentlatlng).icon(exitIcon).zIndex(3);
					Marker outmarker = (Marker) (mBaiduMap
							.addOverlay(overlayOptions));
					int numbersize = outmarkerlist.size() + 1;
					Bundle bundle = new Bundle();
					bundle.putString("mode", "出口");

					//added cxp
					TParkInfo_LocEntity newdata = new TParkInfo_LocEntity();
					newdata.type=2;
					bundle.putSerializable(KEY_LOCATION_ENTITY, newdata);
					
					bundle.putInt("number", numbersize);
					outmarker.setExtraInfo(bundle);
					outmarkerlist.add(outmarker);

				} else {
					Toast.makeText(getActivity(), "请勿重复选点！", Toast.LENGTH_LONG)
							.show();

				}

				break;

			case R.id.id_getpark_in:
				// movelayout.setVisibility(View.GONE);
				// inmarkerlist.add(currentmarker);
				// Bundle bundle1 = new Bundle();
				// bundle1.putString("mode", "入口");
				//
				//
				// bundle1.putInt("number",inmarkerlist.size());
				// currentmarker.setExtraInfo(bundle1);
				// toolLayout.setVisibility(View.GONE);
				// currentmarker.setIcon(entreIcon);
				if (Isaddloca()) {
					int numbersize = inmarkerlist.size() + 1;
					overlayOptions = new MarkerOptions()
							.position(currentlatlng).icon(entreIcon).zIndex(3);
					Marker inmarker = (Marker) (mBaiduMap
							.addOverlay(overlayOptions));

		
					Bundle bundle = new Bundle();
					
					TParkInfo_LocEntity newdata = new TParkInfo_LocEntity();
					newdata.type=1;
					bundle.putSerializable(KEY_LOCATION_ENTITY, newdata);
					
					bundle.putString("mode", "入口");
					bundle.putInt("number", numbersize);
					inmarker.setExtraInfo(bundle);
					inmarkerlist.add(inmarker);

				} else {
					Toast.makeText(getActivity(), "请勿重复选点！", Toast.LENGTH_LONG)
							.show();

				}

				break;
			case R.id.id_getpark_cancel:
				// 占不用
				movelayout.setVisibility(View.GONE);
				if (currentmarker.getExtraInfo() == null
						|| currentmarker.getExtraInfo().getString("mode") == null) {
					toolLayout.setVisibility(View.GONE);
					currentmarker.remove();
					mBaiduMap.hideInfoWindow();

				} else {
					// if (currentmarker.getExtraInfo().getString("mode")
					// .equals("出口")) {
					// outmarkerlist = setnewnumb(outmarkerlist);
					// } else {
					// inmarkerlist = setnewnumb(inmarkerlist);
					//
					// }

				}

				break;

			case R.id.map_move_up:

				if (currentmarker != null) {

					LatLng newlat = new LatLng(
							currentmarker.getPosition().latitude
									+ Constants.MOVE_UNIT,
							currentmarker.getPosition().longitude);
					currentmarker.setPosition(newlat);
					addWindows(currentmarker);
				}
				break;

			case R.id.map_move_down:
				if (currentmarker != null) {
					LatLng newlat = new LatLng(
							currentmarker.getPosition().latitude
									- Constants.MOVE_UNIT,
							currentmarker.getPosition().longitude);
					currentmarker.setPosition(newlat);
					addWindows(currentmarker);
				}
				break;
			case R.id.map_move_left:
				if (currentmarker != null) {
					LatLng newlat = new LatLng(
							currentmarker.getPosition().latitude,
							currentmarker.getPosition().longitude
									- Constants.MOVE_UNIT);
					currentmarker.setPosition(newlat);
					addWindows(currentmarker);
				}
				break;

			case R.id.map_move_right:
				if (currentmarker != null) {
					LatLng newlat = new LatLng(
							currentmarker.getPosition().latitude,
							currentmarker.getPosition().longitude
									+ Constants.MOVE_UNIT);
					currentmarker.setPosition(newlat);
					addWindows(currentmarker);
				}
				break;

			case R.id.id_getpark_mark_remove:

				markerclicLayout.setVisibility(View.GONE);
				movelayout.setVisibility(View.GONE);

				toolLayout.setVisibility(View.VISIBLE);
				mBaiduMap.hideInfoWindow();
				
//				if(delemaker())
//				{
//					break;
//				}
				if(delemaker())
				{
					Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
					
				}else
				{
					Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
				}
				
                 
//				if (currentmarker.getExtraInfo().getString("mode").equals("出口")) {
//					outmarkerlist = setnewnumb(outmarkerlist);
//
//				} else {
//					inmarkerlist = setnewnumb(inmarkerlist);
//
//				}
				break;
			case R.id.id_getpark_marker_close:
				markerclicLayout.setVisibility(View.GONE);

				toolLayout.setVisibility(View.VISIBLE);
				movelayout.setVisibility(View.GONE);
				mBaiduMap.hideInfoWindow();

				break;
			case R.id.zoomin_park:
				
				float zoomLevel1 = mBaiduMap.getMapStatus().zoom;
				if (zoomLevel1 > 4) {
					mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomOut());
					zoomOutBtn.setEnabled(true);
				} else {
					zoomInBtn.setEnabled(false);
					Toast.makeText(getActivity(), "已经缩至最小", Toast.LENGTH_SHORT)
							.show();
				}

			
			
				
				break;
			case R.id.zoomout_park:

			
				

				float zoomLevel = mBaiduMap.getMapStatus().zoom;

				if (zoomLevel <= 18) {
					// MapStatusUpdateFactory.zoomIn();

					mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomIn());
					zoomInBtn.setEnabled(true);
				} else {
					Toast.makeText(getActivity(), "已经放至最大", Toast.LENGTH_SHORT)
							.show();
					zoomOutBtn.setEnabled(false);
				}
				
				break;
			case R.id.btn_maplocal_park:
				LatLng ll = new LatLng(currentlatlng.latitude	,
						currentlatlng.longitude);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);		
				
				break;
			default:
				break;
			}

		}
	};
	private DBHelper dbHelper = null;

	public DBHelper getHelper() {
		if (dbHelper == null) {
			dbHelper = OpenHelperManager.getHelper(getActivity(),
					DBHelper.class);
		}
		return dbHelper;
	}

	public void setdata() {
		try {
			Dao<TParkInfo_LocEntity, Integer> dao = getHelper()
					.getParkdetail_locDao();
			List<TParkInfo_LocEntity> newlist = new ArrayList<TParkInfo_LocEntity>();

//			/*add chenxp*/
//			if (Bimp.tempTParkLocList == null) {
//				Bimp.tempTParkLocList = new ArrayList<TParkInfo_LocEntity>();
//			} else {
//				for (int k = 0; k < inmarkerlist.size(); k++) {
//					TParkInfo_LocEntity locanEntity = Bimp.tempTParkLocList.get(k);
//					locanEntity.latitude = inmarkerlist.get(k).getPosition().latitude;
//					locanEntity.longitude = inmarkerlist.get(k).getPosition().longitude;
//					locanEntity.type=1;
//				}
//			}
			if(Bimp.tempTParkLocList==null){
				Bimp.tempTParkLocList = new ArrayList<TParkInfo_LocEntity>();
			}else{
			    Bimp.tempTParkLocList.clear();
			}
			
			for (int i = 0; i < inmarkerlist.size(); i++) {
	
				Marker marker = inmarkerlist.get(i);
				TParkInfo_LocEntity locEntity = (TParkInfo_LocEntity)marker.getExtraInfo().getSerializable(KEY_LOCATION_ENTITY);
				locEntity.latitude = inmarkerlist.get(i).getPosition().latitude;
				locEntity.longitude = inmarkerlist.get(i).getPosition().longitude;
				locEntity.updatePerson = SessionUtils.loginUser.userName;
				marker.getExtraInfo().putSerializable(KEY_LOCATION_ENTITY, locEntity);

				Bimp.tempTParkLocList.add(locEntity);
			}

			for (int i = 0; i < outmarkerlist.size(); i++) {
				Marker marker = outmarkerlist.get(i);
				TParkInfo_LocEntity locEntity = (TParkInfo_LocEntity)marker.getExtraInfo().getSerializable(KEY_LOCATION_ENTITY);
				locEntity.latitude = outmarkerlist.get(i).getPosition().latitude;
				locEntity.longitude = outmarkerlist.get(i).getPosition().longitude;
				locEntity.updatePerson = SessionUtils.loginUser.userName;
				marker.getExtraInfo().putSerializable(KEY_LOCATION_ENTITY, locEntity);
				Bimp.tempTParkLocList.add(locEntity);
			}

			// Bimp.tempTParkLocList = newlist;
			// newlist=dao.queryBuilder().where().eq("parkLocId",
			// Bimp.parkid).query();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT)
					.show();

		}

	}

	private List<Marker> setnewnumb(List<Marker> oldmarkerlist) {
		// int j = currentmarker.getExtraInfo().getInt("number");
		// if (j <= 0)
		// return aa;
		// int listsize = aa.size();
		// Toast.makeText(getActivity(), "aa" + listsize + "   " + j,
		// Toast.LENGTH_SHORT).show();
		// if (j <listsize) {
		// aa.remove(j);
		//
		// Marker maxmarker = aa.get(listsize);
		// Bundle newbundle = new Bundle();
		// Bundle oldbundle = maxmarker.getExtraInfo();
		//
		// newbundle.putString("mode", oldbundle.getString("mode"));
		//
		// newbundle.putInt("number", j);
		// maxmarker.setExtraInfo(newbundle);
		// currentmarker.remove();
		// mBaiduMap.hideInfoWindow();
		// return aa;
		//
		// } else if (j == listsize) {
		// aa.remove(j);
		// currentmarker.remove();
		// Toast.makeText(getActivity(), "bb"+j+":"+aa.size(),
		// Toast.LENGTH_LONG).show();
		//
		// }

		// return aa;

		int j = currentmarker.getExtraInfo().getInt("number");
		int listsize = oldmarkerlist.size();
		if (j < listsize) {
			oldmarkerlist.remove(currentmarker);
			currentmarker.remove();
			Marker marker;
			for (int i = 0; i < listsize; i++) {
				marker = oldmarkerlist.get(i);
				int numb = Integer.valueOf(marker.getExtraInfo().getInt(
						"number"));
				if (numb == listsize) {
					Bundle newbundle = new Bundle();
					Bundle oldbundle = marker.getExtraInfo();

					newbundle.putString("mode", oldbundle.getString("mode"));

					newbundle.putInt("number", j);
					marker.setExtraInfo(newbundle);

					return oldmarkerlist;
				}
			}

		} else {
			oldmarkerlist.remove(currentmarker);
			currentmarker.remove();

		}

		return oldmarkerlist;
	}

	private Boolean Isaddloca() {

		Double elatitude = 0.00;
		Double elongitude = 0.00;
		for (int i = 0; i < inmarkerlist.size(); i++) {
			elatitude = Math.abs(inmarkerlist.get(i).getPosition().latitude
					- currentlatlng.latitude);

			elongitude = Math.abs(inmarkerlist.get(i).getPosition().longitude
					- currentlatlng.longitude);
			if (elatitude < Constants.EX_UNIT && elongitude < Constants.EX_UNIT) {
				return false;

			}

		}

		for (int i = 0; i < outmarkerlist.size(); i++) {
			elatitude = Math.abs(outmarkerlist.get(i).getPosition().latitude
					- currentlatlng.latitude);

			elongitude = Math.abs(outmarkerlist.get(i).getPosition().longitude
					- currentlatlng.longitude);
			if (elatitude < Constants.EX_UNIT && elongitude < Constants.EX_UNIT) {
				return false;

			}

		}
		return true;

	}

	private void addWindows(Marker arg0) {

		mBaiduMap.hideInfoWindow();

		TextView location = new TextView(getActivity().getApplicationContext());
		location.setBackgroundResource(R.drawable.location_tips);
		location.setPadding(30, 20, 30, 50);
		String parkmode = arg0.getExtraInfo().get("mode").toString();
		String parknumber = arg0.getExtraInfo().get("number").toString();
		location.setText(parkmode + parknumber);
		// 将marker所在的经纬度的信息转化成屏幕上的坐标
		final LatLng ll = arg0.getPosition();
		Point p = mBaiduMap.getProjection().toScreenLocation(ll);
		p.y -= 47;
		LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
		// 为弹出的InfoWindow添加点击事件
		InfoWindow mInfoWindow = new InfoWindow(location, llInfo, -30);
		// 显示InfoWindow
		mBaiduMap.showInfoWindow(mInfoWindow);

	}

	private void binddata() {
		// if(Bimp.ADDPARK_VIEW_MODE==1)
		// {
		// movelayout.setVisibility(View.GONE);
		//
		//
		// markerclicLayout.setVisibility(View.GONE);
		// toolLayout.setVisibility(View.GONE);
		//
		// }

		if (Bimp.tempTParkLocList != null) {
			for (int i = 0; i < Bimp.tempTParkLocList.size(); i++) {
				TParkInfo_LocEntity newdata = Bimp.tempTParkLocList.get(i);
				if(Bimp.ADDPARK_VIEW_MODE==2)
					newdata.parkLocId=null;
				LatLng newlat = new LatLng(newdata.latitude, newdata.longitude);
				if (newdata.type == 1) {
					int numbersize = inmarkerlist.size() + 1;
					overlayOptions = new MarkerOptions().position(newlat)
							.icon(entreIcon).zIndex(3);
					Marker inmarker = (Marker) (mBaiduMap
							.addOverlay(overlayOptions));

					Bundle bundle = new Bundle();
					bundle.putString("mode", "入口");
					
					//added cxp
					bundle.putSerializable(KEY_LOCATION_ENTITY, newdata);
					
					bundle.putInt("number", numbersize);
					inmarker.setExtraInfo(bundle);
					inmarkerlist.add(inmarker);

				} else {
					int numbersize = outmarkerlist.size() + 1;
					overlayOptions = new MarkerOptions().position(newlat)
							.icon(exitIcon).zIndex(3);
					Marker outmarker = (Marker) (mBaiduMap
							.addOverlay(overlayOptions));

					Bundle bundle = new Bundle();
					bundle.putString("mode", "出口");
					
					//added cxp
					bundle.putSerializable(KEY_LOCATION_ENTITY, newdata);
		   			
					bundle.putInt("number", numbersize);
					outmarker.setExtraInfo(bundle);
					outmarkerlist.add(outmarker);

				}

			}

		}

	}

	Boolean delemaker()
	{
		TParkInfo_LocEntity curLocEntity=(TParkInfo_LocEntity) currentmarker.getExtraInfo().getSerializable("KEY_LOCATION_ENTITY");
		if(curLocEntity==null)
		{
			return false;
			
		}
		
	        if(	curLocEntity.parkLocId==null||!(Bimp.ADDPARK_VIEW_MODE==AddParkInfoHistoryActivity.FROM_TYPE_REMOTE))
	        {
	        	if (currentmarker.getExtraInfo().getString("mode").equals("出口")) {
					outmarkerlist = setnewnumb(outmarkerlist);

				} else {
					inmarkerlist = setnewnumb(inmarkerlist);

				}
	        	if(curLocEntity.ID!=null)
	        	{
	        		try {
	        			Toast.makeText(getActivity(), "bb"+curLocEntity.ID, Toast.LENGTH_SHORT).show();
						Dao<TParkInfo_LocEntity, Integer> daoloc= getHelper().getParkdetail_locDao();
						
						DeleteBuilder<TParkInfo_LocEntity, Integer> deleteBuilderloc = daoloc
								.deleteBuilder();
						deleteBuilderloc.where().eq("ID",curLocEntity.ID
								);
						deleteBuilderloc.delete();
						 if(Bimp.tempTParkLocList.remove(curLocEntity))
							 return true;
						
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
	        		
	        		
	        	}else
	        	{
	        		return true;
	        	}
	        	
	        	
	        	
	        	 
	        	
	        }else
	        {
	        	HttpRequestAni<ComResponse<TParkInfo_LocEntity>> httpRequestAni = new HttpRequestAni<ComResponse<TParkInfo_LocEntity>>(
						getActivity(), "/a/parkinfo/deleteLocdata/"+curLocEntity.parkLocId,
						new TypeToken<ComResponse<TParkInfo_LocEntity>>() {
						}.getType()) {

					@Override
					public void callback(ComResponse<TParkInfo_LocEntity> arg0) {
						if (ComResponse.STATUS_OK==arg0.getResponseStatus()) {						
							if (currentmarker.getExtraInfo().getString("mode").equals("出口")) {
								outmarkerlist = setnewnumb(outmarkerlist);

							} else {
								inmarkerlist = setnewnumb(inmarkerlist);

							}
							
						} else {
							// 没有任何提交数据的处理
							Toast.makeText(getActivity(), "删除错误", Toast.LENGTH_SHORT).show();
						}

						

					}

				};

				httpRequestAni.execute();
				 if(Bimp.tempTParkLocList.remove(curLocEntity))
	        	return true;
	        	
	        }
	      
		return false;
		
	}
	

	
	public void centertoLoc() {
		//clearMapClick();
		if(Bimp.ADDPARK_VIEW_MODE!=0)
		{
			if(Bimp.tempTParkLocList.size()<=0)
			{
				return;
			}else
			{
		LatLng ll=new LatLng(Bimp.tempTParkLocList.get(0).latitude, Bimp.tempTParkLocList.get(0).longitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 17);
		mBaiduMap.animateMapStatus(u, 600);
		
			}
		}
	}
}

