package com.mc.parking.client.ui.admin;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.google.zxing.qrcode.decoder.Mode;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.mc.addpic.utils.Bimp;
import com.mc.park.client.R;
import com.mc.parking.client.entity.CouponBean;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkInfo_ImgEntity;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.layout.ActionBaseActivity;
import com.mc.parking.client.layout.BaseDialogFragment;
import com.mc.parking.client.layout.BaseViewPagerIndicator;
import com.mc.parking.client.layout.PullToRefreshListView;
import com.mc.parking.client.layout.PullToRefreshListView.IXListViewListener;
import com.mc.parking.client.layout.PullToRefreshListView.OnRefreshListener;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.CommFindEntity;
import com.mc.parking.client.layout.net.HttpRequest;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.utils.DBHelper;
import com.mc.parking.client.utils.SessionUtils;
import com.nostra13.universalimageloader.core.ImageLoader;


public class AddParkInfoHistoryActivity extends ActionBaseActivity implements IXListViewListener{

	ViewPager myviewpager = null;
	private List<View> views;
	private View view1, view2;
	private TextView commitbtn, uncommitbtn;
	PullToRefreshListView mlistview1;
	PullToRefreshListView mlistview2;
	private PullToRefreshListViewAdapter adapter1, adapter2;
	private String[] columns = new String[] { "提交", "未提交" };
	BaseViewPagerIndicator mIndicator;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	public final static int FROM_TYPE_REMOTE=1;
	public final static int FROM_TYPE_LOCAL=2;
	public final static int FROM_TYPE_NEW=0;
	
	private DBHelper dbHelper = null;

	private List<TParkInfoEntity> dataTParkinfo, okdata;

	SimpleDateFormat myFmt3 = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");

	String historydate;

	Dao<TParkInfo_LocEntity, Integer> dao;
	Dao<TParkInfo_ImgEntity, Integer> daoimage;
	int datacount=1;
	int totalcount=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		okdata = new ArrayList<TParkInfoEntity>();
		setContentView(R.layout.addparkinfo_history_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_topbar);
		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		titleView.setText("采集记录");
		// 查询服务器数据
		getRemoteData();
		getHistoryDate();
		initViewPager();

		initlistview();

	}

	private void initViewPager() {
		myviewpager = (ViewPager) findViewById(R.id.viewpager);
		mIndicator = (BaseViewPagerIndicator) findViewById(R.id.id_stickynavlayout_indicator);
		mIndicator.setMyMode(1);
		mIndicator.setnumb(dataTParkinfo.size());
		mIndicator.setTitles(columns);

		mIndicator.setViewPage(myviewpager);
		views = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater();
		view1 = inflater.inflate(R.layout.item_viewpage_listview, null);
		view2 = inflater.inflate(R.layout.item_viewpage_listview, null);

		views.add(view1);

		views.add(view2);

		// views.add(view3);
		myviewpager.setAdapter(new MyViewPagerAdapter(views));
		myviewpager.setCurrentItem(0);
		myviewpager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	// viewpager初始化
	public class MyViewPagerAdapter extends PagerAdapter {
		private List<View> mListViews;

		public MyViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

			mIndicator.scroll(arg0, arg1);

		}

		public void onPageSelected(int arg0) {

		}

	}

	private void initlistview() {
		mlistview1 = (PullToRefreshListView) view1
				.findViewById(R.id.history_pull_to_refresh_listview);
		mlistview1.setDividerHeight(10);
		mlistview2 = (PullToRefreshListView) view2
				.findViewById(R.id.history_pull_to_refresh_listview);
		mlistview2.setDividerHeight(10);
		adapter1 = new PullToRefreshListViewAdapter() {
		};
		adapter2 = new PullToRefreshListViewAdapter() {
		};
		adapter2.getdata(okdata,0);
		
		mlistview2.setXListViewListener(this);
		mlistview2.setPullLoadEnable(false);
		mlistview1.setPullLoadEnable(false);
		mlistview1.setXListViewListener(this);
		mlistview1.setShowLastUpdatedText(true);
		mlistview2.setShowLastUpdatedText(true);
		mlistview1.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				getRemoteData();
				
				mlistview1.onRefreshComplete();
				
				
			}
		});
		
		mlistview2.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				getHistoryDate();
				mIndicator.setnumb(dataTParkinfo.size());
				adapter2.getdata(dataTParkinfo,0);
				mlistview2.setAdapter(adapter2);
				mlistview2.onRefreshComplete();
			}
		});
		mlistview2.setAdapter(adapter2);
		mlistview1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Bimp.tempTParkInfo=okdata.get(position);
				Bimp.tempTParkImageList=okdata.get(position).imgUrlArray;
				Bimp.tempTParkLocList=okdata.get(position).latLngArray;
				Bimp.ADDPARK_VIEW_MODE=AddParkInfoHistoryActivity.FROM_TYPE_REMOTE;
				Intent inte = new Intent();
				inte.setClass(getApplicationContext(),
						AddParkInfoDetailActivity.class);
				startActivity(inte);
				finish();

			}
		});
		
		mlistview1.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					 int position, long id) {
				// TODO Auto-generated method stub
				//super.onBackPressed();
				TParkInfoEntity parkInfo = okdata.get(position);
				BaseDialogFragment confirmDialog = new BaseDialogFragment();
				confirmDialog.setMessage("确认刪除吗?");
				confirmDialog.setPositiveButton("确认",
						new LongClickForServer(parkInfo));
				confirmDialog.setNegativeButton("取消", null);
				confirmDialog.show(getFragmentManager(), "");
				
				return true;
			}
		});
		
		mlistview2.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int arg1, long id) {
				// TODO Auto-generated method stub
				//super.onBackPressed();
			final long currentparkid=dataTParkinfo.get(arg1).parkId;
				BaseDialogFragment confirmDialog = new BaseDialogFragment();
				confirmDialog.setMessage("确认刪除吗?");
				confirmDialog.setPositiveButton("确认",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Dao<TParkInfoEntity, Integer> dao;
								try {
							
									dao = getHelper()
											.getParkdetailDao();
									Dao<TParkInfo_LocEntity, Integer> daoloc = getHelper()
											.getParkdetail_locDao();
									Dao<TParkInfo_ImgEntity, Integer> daoima = getHelper()
											.getParkdetail_imagDao();
									
									
									List<TParkInfo_ImgEntity> deleimagelist= daoima.queryBuilder().where().eq("parkImgId", currentparkid)
											.query();

									DeleteBuilder<TParkInfoEntity, Integer> deleteBuilderparkinfo=dao.deleteBuilder();
									
									deleteBuilderparkinfo.where().eq("parkId", currentparkid);
									deleteBuilderparkinfo.delete();
									
									
									DeleteBuilder<TParkInfo_LocEntity, Integer> deleteBuilderloc = daoloc
											.deleteBuilder();
									deleteBuilderloc.where().eq("parkLocId", currentparkid);
									deleteBuilderloc.delete();
									
									DeleteBuilder<TParkInfo_ImgEntity, Integer> deleteBuilderimage = daoima
											.deleteBuilder();
									deleteBuilderimage.where().eq("parkImgId", currentparkid);
									deleteBuilderimage.delete();
									
									
									//删除本地图片文件
									 for (TParkInfo_ImgEntity tempparkimage :deleimagelist) {
				
										 File file=new File(tempparkimage.imgUrlPath.toString());
											file.delete();
										}
									
									 getHistoryDate();
									mIndicator.setnumb(dataTParkinfo.size());
									adapter2.getdata(dataTParkinfo,0);
									mlistview2.setAdapter(adapter2);
									
									
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							
							

								
								
								
							}
						});
				confirmDialog.setNegativeButton("取消", null);
				confirmDialog.show(getFragmentManager(), "");
				
				return true;
			}
		});

		mlistview2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				Long currentID = dataTParkinfo.get(position).parkId;
				Bimp.tempTParkInfo = dataTParkinfo.get(position);

				try {
					dao = getHelper().getParkdetail_locDao();

					Bimp.tempTParkLocList = dao.queryBuilder().where()
							.eq("parkLocId", currentID).query();

					daoimage = getHelper().getParkdetail_imagDao();

					Bimp.tempTParkImageList = daoimage.queryBuilder().where()
							.eq("parkImgId", currentID).query();

				} catch (SQLException e) {
					Log.e("ItemClickListener", e.getMessage(),e);
				}
				Bimp.ADDPARK_VIEW_MODE=AddParkInfoHistoryActivity.FROM_TYPE_LOCAL;
				Bimp.parkid=currentID;
				Intent inte = new Intent();
				inte.setClass(getApplicationContext(),
						AddParkInfoDetailActivity.class);
				startActivity(inte);
				finish();
			}
		});

	}

	
	class LongClickForServer implements DialogInterface.OnClickListener{

		TParkInfoEntity parkInfo;
		public LongClickForServer(TParkInfoEntity parkInfo){
			this.parkInfo = parkInfo;
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which) {

			if(parkInfo!=null){
			//不需要动画的task
			HttpRequest<ComResponse<TParkInfoEntity>> httpRequestAni = new HttpRequest<ComResponse<TParkInfoEntity>>("/a/parkinfo/delete/"+parkInfo.parkId,
					new TypeToken<ComResponse<TParkInfoEntity>>() {}.getType()) {

				@Override
				public void onSuccess(ComResponse<TParkInfoEntity> arg0) {
					if (ComResponse.STATUS_OK==arg0.getResponseStatus()) {						
						getRemoteData();
					} else {
						// 没有任何提交数据的处理
						Toast.makeText(AddParkInfoHistoryActivity.this, "删除错误\r\n"+arg0.getErrorMessage(), Toast.LENGTH_SHORT).show();
					}

				}

				@Override
				public void onFailed(String message) {
					if(message!=null)
					Toast.makeText(AddParkInfoHistoryActivity.this, message, Toast.LENGTH_SHORT).show();
				}

			};

			httpRequestAni.execute();
			}
        	
		
			
		}
		
	}
	
	public abstract class PullToRefreshListViewAdapter extends
			android.widget.BaseAdapter {

		private ArrayList<CouponBean> items = new ArrayList<CouponBean>();
		ViewHolder viewHolder;
		List<TParkInfoEntity> mydata = new ArrayList<TParkInfoEntity>();
		// 0--本地数据库的绑定1--网络数据库的绑定
		int Mode = 0;

		public void getdata(List<TParkInfoEntity> mydata, int mode) {
			this.mydata = mydata;
			this.Mode = mode;

		}

		public class ViewHolder {
			public String id;
			public TextView parkname;
			public TextView addDate;
			public ImageView image;
		}

		/**
		 * Loads the data.
		 */
		public void loadData(List<CouponBean> remoteData) {

			items.addAll(0, remoteData);

			// MANDATORY: Notify that the data has changed
			notifyDataSetChanged();
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
						.inflate(R.layout.item_addparkhistory, null);
				viewHolder.parkname = (TextView) convertView
						.findViewById(R.id.parkname_string);
				viewHolder.addDate = (TextView) convertView
						.findViewById(R.id.history_item_startdate);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();

			}

			viewHolder.image = (ImageView) convertView
					.findViewById(R.id.addhistory_img);
			// imageLoader
			// .displayImage(
			// "http://i2.sinaimg.cn/travel/ul/2008/0818/U3059P704DT20080818182043.jpg",
			// image1);
			if (Mode == 0) {
				String imagepath = getimage(mydata.get(position).parkId);
				if (imagepath != null) {
					String uristring="file://"+imagepath;				
					imageLoader.displayImage(uristring,viewHolder.image);
				}
			} else if (Mode == 1) {
				if (mydata.get(position).imgUrlArray != null
						&& mydata.get(position).imgUrlArray.size() > 0) {

					imageLoader
							.displayImage(
									mydata.get(position).imgUrlArray.get(0).imgUrlHeader
											+ mydata.get(position).imgUrlArray
													.get(0).imgUrlPath,
									viewHolder.image);

				}

			}

			viewHolder.parkname.setText(mydata.get(position).parkname);
			if (mydata.get(position).createDate != null)
				historydate = myFmt3.format(mydata.get(position).createDate);
			else
				historydate = "";
			viewHolder.addDate.setText(historydate);

			return convertView;
		}

	}

	private void getHistoryDate() {
		try {
			Dao<TParkInfoEntity, Integer> dao = getHelper().getParkdetailDao();
			
			dataTParkinfo = dao.queryBuilder().orderBy("createDate", false).query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public DBHelper getHelper() {
		if (dbHelper == null) {
			dbHelper = OpenHelperManager.getHelper(
					AddParkInfoHistoryActivity.this, DBHelper.class);
		}
		return dbHelper;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		GetDate();
//		finddata();
		mIndicator.setnumb(dataTParkinfo.size());
		adapter2.getdata(dataTParkinfo,0);
		mlistview2.setAdapter(adapter2);
		adapter1.getdata(okdata,1);
		mlistview1.setAdapter(adapter1);
	
	
		

	}

	private String getimage(Long id) {
		List<TParkInfo_ImgEntity> imagelist = new ArrayList<TParkInfo_ImgEntity>();
		try {
			daoimage = getHelper().getParkdetail_imagDao();
			imagelist = daoimage.queryBuilder().where().eq("parkImgId", id)
					.query();
			if (imagelist.size() == 0)
				return null;
			else
				return imagelist.get(0).imgUrlPath;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	private void getRemoteData() {
		HttpRequestAni<CommFindEntity<TParkInfoEntity>> httpRequestAni = new HttpRequestAni<CommFindEntity<TParkInfoEntity>>(
				AddParkInfoHistoryActivity.this, "/a/parkinfo/"+SessionUtils.loginUser.userid,
				new TypeToken<CommFindEntity<TParkInfoEntity>>() {
				}.getType()) {

			@Override
			public void callback(CommFindEntity<TParkInfoEntity> arg0) {

				if(arg0==null)
					return;
				if (arg0.getRowCount() > 0) {
					// 如果返回的不为空并且总数大于0
					// 有数据的时候处理
					datacount=1;
					totalcount=arg0.getRowCount();
					if(arg0.getPageCount()>1)
						mlistview1.setPullLoadEnable(true);
					else
						mlistview1.setPullLoadEnable(false);
					 okdata = arg0.getResult();
				} else if(arg0.getRowCount()==0) {
					okdata.clear();
				}
				adapter1.getdata(okdata,1);
				mlistview1.setAdapter(adapter1);

			}

		};

		httpRequestAni.execute();

	}



	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		if(datacount>totalcount-1)
		{
			mlistview1.stopLoadMore();
			Toast.makeText(AddParkInfoHistoryActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
			mlistview1.setPullLoadEnable(false);
			return;
			
		}
		
		
	int itemnum=myviewpager.getCurrentItem();
	   if(itemnum==0)
	{
		HttpRequestAni<CommFindEntity<TParkInfoEntity>> httpRequestAni = new HttpRequestAni<CommFindEntity<TParkInfoEntity>>(
				AddParkInfoHistoryActivity.this, "/a/parkinfo/"+SessionUtils.loginUser.userid+"?p="+datacount,
				new TypeToken<CommFindEntity<TParkInfoEntity>>() {
				}.getType()) {

			@Override
			public void callback(CommFindEntity<TParkInfoEntity> arg0) {

				if(arg0==null)
				{
					mlistview1.stopLoadMore();
					return;
				}
				if (arg0.getRowCount() > 0) {
					// 如果返回的不为空并且总数大于0
					// 有数据的时候处理
					totalcount=arg0.getPageCount();
					List<TParkInfoEntity> templistdata=arg0.getResult();
					for(TParkInfoEntity tempitem:templistdata)
					{
						
						okdata.add(tempitem);

					}
							
				} else if(arg0.getRowCount()==0) {
					okdata.clear();
			
				}
				
				
				adapter1.getdata(okdata,1);
				adapter1.notifyDataSetChanged();
				mlistview1.stopLoadMore();
				//mlistview1.setAdapter(adapter1);
				
				
				datacount++;
						

			}

		};

		httpRequestAni.execute();

	}
		
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		int itemnum=myviewpager.getCurrentItem();
		if(itemnum==0)
		{
			
			
		}
	}

}
