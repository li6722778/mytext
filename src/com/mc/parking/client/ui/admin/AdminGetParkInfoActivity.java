package com.mc.parking.client.ui.admin;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.baidu.navisdk.util.common.BaseUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.admin.upload.MultiPartStack;
import com.mc.parking.admin.upload.MultiPartStringRequest;
import com.mc.parking.client.Constants;
import com.mc.park.client.R;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkInfo_ImgEntity;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.CouponDetailActivity;
import com.mc.parking.client.ui.fragment.TopBarFragment;
import com.mc.parking.client.utils.DBHelper;
import com.mc.parking.client.utils.SessionUtils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AdminGetParkInfoActivity extends FragmentActivity {

	public GetParkInfoMapFragment mapFragment = null;
	public AddParkImageFragment imageFragment = null;
	public TopBarFragment topBarFragment = null;
	Button OKbtn = null;
	Button savebtn = null;
	Button closebtn = null;
	private DBHelper dbHelper = null;
	int SCAN_SUCCESS = 11;
	private static String TAG = "upload";
	private static RequestQueue mSingleQueue;
	Long parkid = null;
	Dao<TParkInfoEntity, Integer> dao;
	Dao<TParkInfo_LocEntity, Integer> daoloc;

	Dao<TParkInfo_ImgEntity, Integer> daoima;
	Long currentparkid;
	LinearLayout lay1, lay2;
	List<String> deleimagepath=new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		mSingleQueue = Volley.newRequestQueue(this, new MultiPartStack());
		setContentView(R.layout.ac_getparkinfo_main);

		try {
			dao = getHelper().getParkdetailDao();
			daoloc = getHelper().getParkdetail_locDao();
			daoima = getHelper().getParkdetail_imagDao();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_topbar);
		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		titleView.setText("数据采集");
		FragmentManager manager = getFragmentManager();

		mapFragment = (GetParkInfoMapFragment) manager
				.findFragmentById(R.id.getpark_fragment_map);

		imageFragment = (AddParkImageFragment) manager
				.findFragmentById(R.id.addimage_fragment);

		lay1 = (LinearLayout) findViewById(R.id.id_getpark_admin_lay1);
		lay2 = (LinearLayout) findViewById(R.id.id_getpark_admin_lay2);

		OKbtn = (Button) findViewById(R.id.id_getpark_ok);
		
		OKbtn.setEnabled(true);

		savebtn = (Button) findViewById(R.id.id_getpark_save);

		closebtn = (Button) findViewById(R.id.id_getpark_close);

		// 提交按钮

		lay1.setVisibility(View.VISIBLE);
		lay2.setVisibility(View.GONE);

		closebtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = getIntent();

				intent.setClass(getApplicationContext(),
						AddParkInfoDetailActivity.class);
				setResult(Constants.ResultCode.ADDPARK_END, intent);

				finish();
			}
		});

		OKbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(!Constants.NETFLAG)
				{
					Toast.makeText(getApplicationContext(), "网络连接失败，请检查网络", Toast.LENGTH_LONG).show();
					return;
					
				}				
				
				parkid = Bimp.tempTParkInfo.parkId;
				mapFragment.setdata();
				imageFragment.setdate();
				OKbtn.setEnabled(false);
				uploadImageArray();

			}
		});
		savebtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// mapFragment.setList();

				if (Bimp.ADDPARK_VIEW_MODE == AddParkInfoHistoryActivity.FROM_TYPE_REMOTE) {
					Toast.makeText(getApplicationContext(), "已提交数据无法保存本地",
							Toast.LENGTH_SHORT).show();
					return;

				}

				mapFragment.setdata();
				imageFragment.setdate();
				try {
					Dao<TParkInfoEntity, Integer> dao = getHelper()
							.getParkdetailDao();
					Dao<TParkInfo_LocEntity, Integer> daoloc = getHelper()
							.getParkdetail_locDao();
					Dao<TParkInfo_ImgEntity, Integer> daoima = getHelper()
							.getParkdetail_imagDao();

					Long newparkid;
					
					if (Bimp.tempTParkInfo.parkId != null) {
						dao.update(Bimp.tempTParkInfo);

						newparkid = Bimp.tempTParkInfo.parkId;

					} else {
						TParkInfoEntity newEntity = dao
								.createIfNotExists(Bimp.tempTParkInfo);
						newparkid = newEntity.parkId;

					}

					DeleteBuilder<TParkInfo_LocEntity, Integer> deleteBuilderloc = daoloc
							.deleteBuilder();
					deleteBuilderloc.where().eq("parkLocId", newparkid);
					deleteBuilderloc.delete();

					// tempparkloc.parkLocId = Bimp.parkid;

					for (TParkInfo_LocEntity tempparkloc : Bimp.tempTParkLocList) {

						tempparkloc.parkLocId = newparkid;

						daoloc.createIfNotExists(tempparkloc);

					}
					
					//先删除本地图片
					
//				 List<TParkInfo_ImgEntity>	imagelist = daoima.queryBuilder().where().eq("parkImgId", newparkid)
//							.query();
				

					DeleteBuilder<TParkInfo_ImgEntity, Integer> deleteBuilderimage = daoima
							.deleteBuilder();
					deleteBuilderimage.where().eq("parkImgId", newparkid);
					deleteBuilderimage.delete();

					for (TParkInfo_ImgEntity tempparkimage : Bimp.tempTParkImageList) {

						tempparkimage.parkImgId = newparkid;

						daoima.createIfNotExists(tempparkimage);

					}
//					//删除本地图片文件
//					 for (TParkInfo_ImgEntity tempparkimage :imagelist) {
//
//						 File file=new File(tempparkimage.imgUrlPath.toString());
//							file.delete();
//
//
//						}

					Toast.makeText(AdminGetParkInfoActivity.this, "数据保存成功",
							Toast.LENGTH_SHORT).show();
					
					Bimp.tempTParkInfo = null;
					Bimp.tempTParkLocList.clear();
					Bimp.tempTParkImageList.clear();

					Intent intent = getIntent();

					intent.setClass(getApplicationContext(),
							AddParkInfoDetailActivity.class);
					setResult(Constants.ResultCode.ADDPARK_END, intent);

					finish();
					// Dao<TParkInfo_ImgEntity, Integer> dao = getHelper()
					// .getParkdetail_imagDao();
					// TParkInfo_ImgEntity aaaa=new TParkInfo_ImgEntity();
					// aaaa.createDate=null;
					// aaaa.createPerson="111";
					// aaaa.detail="dad";
					// aaaa.imgUrlHeader="bbbb";
					// aaaa.imgUrlPath="";
					// aaaa.parkImgId=111l;
					// aaaa.updatePerson="";
					// aaaa.updateDate=null;
					//
					//
					// dao.createIfNotExists(aaaa);

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}

				// Toast.makeText(getApplicationContext(), ""+a,
				// Toast.LENGTH_LONG).show();

			}
		});

	}

	public DBHelper getHelper() {
		if (dbHelper == null) {
			dbHelper = OpenHelperManager.getHelper(getApplicationContext(),
					DBHelper.class);
		}
		return dbHelper;
	}

	public void backTo(View v) {
		// mapFragment.setList();
		finish();
	}

	private void uploadImageArray() {
		Map<String, File> files = new HashMap<String, File>();
		for (int i = 0; i < Bimp.tempTParkImageList.size(); i++) {
			if (Bimp.tempTParkImageList.get(i).imgUrlHeader == null
					|| Bimp.tempTParkImageList.get(i).imgUrlHeader.equals("")) {
				files.put("" + i, new File(
						Bimp.tempTParkImageList.get(i).imgUrlPath));
				deleimagepath.add(Bimp.tempTParkImageList.get(i).imgUrlPath);
			}
		}

		// files.put("pic1", new File(
		// "/storage/sdcard0/Photo_LJ/f1.jpg"));
		//
		//
		// files.put("pic2", new File(
		// "/storage/sdcard0/Photo_LJ/f2.jpg"));
		Map<String, String> params = new HashMap<String, String>();
		// params.put("token", "DJrlPbpJQs21rv1lP41yiA==");

		String uri = Constants.HTTP + "/a/parkinfo/savepic";
		addPutUploadFileRequest(uri, files, params, mResonseListenerString,
				mErrorListener, null);

	}

	Listener<JSONObject> mResonseListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject response) {
			Log.i(TAG, " on response json" + response.toString());

		}
	};
	private Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();;
	Listener<String> mResonseListenerString = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			Log.i(TAG, " on imageload String" + response);
			
			if(response==null||response.length()<=0){
				Toast.makeText(AdminGetParkInfoActivity.this,
						"获取返回的图片路径为空",Toast.LENGTH_SHORT).show();
				OKbtn.setEnabled(true);
				return;
			}
			
			
			List<String> imgArray = mGson.fromJson(response.toString(), new TypeToken<List<String>>() {}.getType());
			
			Bimp.tempTParkImageList.clear();
	
			for (int i = 0; i < imgArray.size(); i++) {
				TParkInfo_ImgEntity imagebean = new TParkInfo_ImgEntity();
				imagebean.imgUrlPath = imgArray.get(i).replace("\"", "");
				Bimp.tempTParkImageList.add(imagebean);
			}

			Bimp.tempTParkInfo.imgUrlArray = Bimp.tempTParkImageList;
			Bimp.tempTParkInfo.latLngArray = Bimp.tempTParkLocList;
			currentparkid = Bimp.tempTParkInfo.parkId;
			TParkInfoEntity postdata = changedata(Bimp.tempTParkInfo);
			

			HttpRequestAni<ComResponse<TParkInfoEntity>> httpRequestAni = new HttpRequestAni<ComResponse<TParkInfoEntity>>(
					AdminGetParkInfoActivity.this, "/a/parkinfo/save",
					new TypeToken<ComResponse<TParkInfoEntity>>() {
					}.getType(), postdata, TParkInfoEntity.class) {

				@Override
				public void callback(ComResponse<TParkInfoEntity> arg0) {
					OKbtn.setEnabled(true);
					if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {
						TParkInfoEntity info = arg0.getResponseEntity();
						Message msg = new Message();

						try {
							// Dao<TParkInfoEntity, Integer> dao = getHelper()
							// .getParkdetailDao();

							// 刪除本地數據
							if (currentparkid != null) {
								DeleteBuilder<TParkInfoEntity, Integer> deleteBuilder = dao
										.deleteBuilder();
								deleteBuilder.where().eq("parkId",
										currentparkid);
								deleteBuilder.delete();

								DeleteBuilder<TParkInfo_ImgEntity, Integer> deleteBuilderimage = daoima
										.deleteBuilder();
								deleteBuilderimage.where().eq("parkImgId",
										currentparkid);
								deleteBuilderimage.delete();

								DeleteBuilder<TParkInfo_LocEntity, Integer> deleteBuilderloc = daoloc
										.deleteBuilder();
								deleteBuilderloc.where().eq("parkLocId",
										currentparkid);
								deleteBuilderloc.delete();
								
								
							}

							//删除图片
							for(int i=0;i<deleimagepath.size();i++)
							{
								File delefile=new File(deleimagepath.get(i));
								delefile.delete();		
								
							}
							
							Toast.makeText(AdminGetParkInfoActivity.this, "数据采集成功",
									Toast.LENGTH_SHORT).show();
							
							Intent intent = getIntent();

							intent.setClass(getApplicationContext(),
									AddParkInfoDetailActivity.class);
							setResult(Constants.ResultCode.ADDPARK_END, intent);

							finish();
						
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							Log.e("HttpRequestAni", e.getMessage(), e);
							Toast.makeText(AdminGetParkInfoActivity.this,
									"数据采集失败\r\n" + e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}

						

					} else {

						Toast.makeText(AdminGetParkInfoActivity.this,
								"数据采集失败\r\n" + arg0.getErrorMessage(),
								Toast.LENGTH_SHORT).show();
					}
				}

			};

			httpRequestAni.execute();

		}
	};

	ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			if (error != null) {
				if (error.networkResponse != null)
					Log.e(TAG, " error "
							+ new String(error.networkResponse.data));
			}
		}
	};

	public static void addPutUploadFileRequest(final String url,
			final Map<String, File> files, final Map<String, String> params,
			final Listener<String> responseListener,
			final ErrorListener errorListener, final Object tag) {
		if (null == url || null == responseListener) {
			return;
		}

		MultiPartStringRequest multiPartRequest = new MultiPartStringRequest(
				Request.Method.POST, url, responseListener, errorListener) {

			@Override
			public Map<String, File> getFileUploads() {
				return files;
			}

			@Override
			public Map<String, String> getStringUploads() {
				return params;
			}

		};

		Log.i(TAG, " volley put : uploadFile " + url);

		mSingleQueue.add(multiPartRequest);
	}

	// 本地或新增提交
	private TParkInfoEntity changedata(TParkInfoEntity info) {
		
		//克隆
		TParkInfoEntity infoNew = info.clone();
		
		if(Bimp.ADDPARK_VIEW_MODE==AddParkInfoHistoryActivity.FROM_TYPE_REMOTE)
		{
			infoNew.parkId=info.parkId;
		}else
		{
			infoNew.parkId=null;
			
		}
		infoNew.createPerson = SessionUtils.loginUser==null?"":SessionUtils.loginUser.userName;
		infoNew.updatePerson = SessionUtils.loginUser==null?"":SessionUtils.loginUser.userName;
//		info1.address = info.address;
//		info1.detail = info.detail;
//		info1.discountHourAlldayMoney = info.discountHourAlldayMoney;
//		info1.discountSecHourMoney = info.discountSecHourMoney;
//		info1.feeTypefixedHourMoney = info.feeTypefixedHourMoney;
//		info1.feeTypeSecInScopeHours = info.feeTypeSecInScopeHours;
//		info1.feeTypeSecInScopeHourMoney = info.feeTypeSecInScopeHourMoney;
//		info1.feeTypeSecOutScopeHourMoney = info.feeTypeSecOutScopeHourMoney;
//		info1.isDiscountAllday = info.isDiscountAllday;
//		info1.isDiscountSec = info.isDiscountSec;
//		info1.owner = info.owner;
//		info1.ownerPhone = info.ownerPhone;
//		info1.parkname = info.parkname;
//		info1.vender = info.vender;
//		info1.venderBankName = info.venderBankName;
//		info1.venderBankNumber = info.venderBankNumber;
//		info1.feeType = info.feeType;
//
//		List<TParkInfo_ImgEntity> imgArray = new ArrayList<>();
//		for (TParkInfo_ImgEntity imagebean : info.imgUrlArray) {
//			
//			TParkInfo_ImgEntity img = new TParkInfo_ImgEntity();
//			img.createPerson = SessionUtils.loginUser.userName;
//			img.updatePerson = SessionUtils.loginUser.userName;
//			img.imgUrlPath = imagebean.imgUrlPath;
//			img.detail = imagebean.detail;
//			img.imgUrlHeader=imagebean.imgUrlHeader;
//			img.imgUrlPath = imagebean.imgUrlPath;
//			
//			imgArray.add(img);
//
//		}
//		infoNew.imgUrlArray = imgArray;
//
//		List<TParkInfo_LocEntity> locArray = new ArrayList<>();
//
//		for (TParkInfo_LocEntity imagebean : info.latLngArray) {
//
//			TParkInfo_LocEntity loc = new TParkInfo_LocEntity();
//			loc.createPerson = SessionUtils.loginUser.userName;
//			loc.updatePerson = SessionUtils.loginUser.userName;
//			loc.latitude = imagebean.latitude;
//			loc.longitude = imagebean.longitude;
//			loc.type = imagebean.type;
//			locArray.add(loc);
//		}
//
//		/* 坐标上传 */
//
//		info1.latLngArray = locArray;

		return infoNew;

	}

}
