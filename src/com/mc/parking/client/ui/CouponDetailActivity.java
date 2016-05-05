package com.mc.parking.client.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.mc.parking.client.Constants;
import com.mc.parking.client.PageConstant;
import com.mc.park.client.R;
import com.mc.parking.client.entity.CouponBean;
import com.mc.parking.client.entity.TCouponEntity;
import com.mc.parking.client.entity.TUseCouponEntity;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.ActionBaseActivity;
import com.mc.parking.client.layout.PullToRefreshListView;
import com.mc.parking.client.layout.PullToRefreshListView.IXListViewListener;
import com.mc.parking.client.layout.PullToRefreshListView.OnRefreshListener;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.CommFindEntity;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;
import com.mc.parking.zxing.camera.MipcaActivityCapture;

public class CouponDetailActivity extends ActionBaseActivity implements IXListViewListener{

	private PullToRefreshListView listView;
	private PullToRefreshListViewAdapter adapter;
	ArrayList<CouponBean> data1 = new ArrayList<CouponBean>();;
	private List<TUseCouponEntity> okdata;
	private LinearLayout progress;
	Button findCopBtn;
	ImageView scanimage;
	EditText edittext;// 搜索框
	int SCAN_SUCCESS = 11;
	int SCAN_FAIL = 12;
	int START_MODE = 0;
	SimpleDateFormat myFmt3 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	int CURRENT_PAGE=0;
	int TOTALE_PAGE=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_admin_youhui);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_topbar);
		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		titleView.setText(R.string.youhui_title);
		Intent intent = getIntent();
		if (intent.getStringExtra("YUYUE") != null) {
			START_MODE = 1;

		}

		okdata = new ArrayList<TUseCouponEntity>();
		progress = (LinearLayout) findViewById(R.id.youhui_progressContainer);
		listView = (PullToRefreshListView) findViewById(R.id.youhui_pull_to_refresh_listview);
		listView.setPullLoadEnable(false);
		listView.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				if(TOTALE_PAGE-1>CURRENT_PAGE)
				{
					CURRENT_PAGE++;
					getRemoteData();
					
				}else
				{
					listView.stopLoadMore();
					listView.setPullLoadEnable(false);
					Toast.makeText(getApplicationContext(), "没有更多数据", Toast.LENGTH_SHORT).show();
					
				}
			}
		});
		edittext = (EditText) findViewById(R.id.youhui_search_box);
		scanimage = (ImageView) findViewById(R.id.youhui_scan_image);
		scanimage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(),
						MipcaActivityCapture.class);
				startActivityForResult(intent, 11);
			}
		});
		findCopBtn = (Button) findViewById(R.id.addYouhuiButton);
		findCopBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {/*
										 * // TODO Auto-generated method stub
										 * progress.setVisibility(View.VISIBLE);
										 * 
										 * new CouponTask(){
										 * 
										 * @Override protected void
										 * onSuccess(List<CouponBean> data)
										 * throws Exception { // TODO
										 * Auto-generated method stub
										 * super.onSuccess(data);
										 * adapter.loadData(data); }
										 * 
										 * }.execute();
										 * 
										 * listView.postDelayed(new Runnable() {
										 * 
										 * @Override public void run() {
										 * progress.setVisibility(View.GONE);
										 * listView.onRefreshComplete(); } },
										 * 2000);
										 */

				findcoupon();

			}
		});
		listView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				CURRENT_PAGE=0;
				getRemoteData();
				listView.onRefreshComplete();

			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (START_MODE == 0) {
					return;

				}
				TUseCouponEntity currentbean = (TUseCouponEntity) adapter
						.getItem(position);
				TCouponEntity youhui = currentbean.counponentity;
				Intent intent = new Intent();
				intent.putExtra("couponBean", youhui);
				setResult(PageConstant.YOUHUI_AC, intent);
				finish();

			}
		});

		adapter = new PullToRefreshListViewAdapter() {
		};
         listView.setAdapter(adapter);
		getRemoteData();
		// adapter.loadData(data1);
		// listView.setAdapter(adapter);

		/*
		 * new CouponTask() {
		 * 
		 * @Override protected void onPreExecute() { if (progress != null) {
		 * progress.setVisibility(View.VISIBLE); } }
		 * 
		 * @Override protected void onException(final Exception e) throws
		 * RuntimeException { super.onException(e);
		 * progress.setVisibility(View.GONE); if (e instanceof
		 * OperationCanceledException) { System.err.println(e); } }
		 * 
		 * @Override protected void onSuccess(final List<CouponBean> data)
		 * throws Exception { super.onSuccess(data);
		 * progress.setVisibility(View.GONE); adapter.loadData(data);
		 * listView.setAdapter(adapter);
		 * 
		 * } }.execute();
		 */

	}

	public abstract class PullToRefreshListViewAdapter extends
			android.widget.BaseAdapter {

		private ArrayList<TUseCouponEntity> items = new ArrayList<TUseCouponEntity>();;
		ViewHolder viewHolder;
		List<TUseCouponEntity> mydata = new ArrayList<TUseCouponEntity>();

		public class ViewHolder {
			public String id;
			public TextView youhui_info;
			public TextView endDate;
			public TextView startDate;
		}

		public void getdata(List<TUseCouponEntity> mydata) {
			this.mydata = mydata;

		}

		/**
		 * Loads the data.
		 */
		public void loadData(List<TUseCouponEntity> remoteData) {

			mydata.clear();
			mydata.addAll(0, remoteData);

			// MANDATORY: Notify that the data has changed
			notifyDataSetChanged();
		}
		
		public void loadMore(List<TUseCouponEntity> remoteData) {
			mydata.addAll(remoteData);
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
			return mydata.get(position);
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
						.inflate(R.layout.item_list_youhui, null);
				viewHolder.youhui_info = (TextView) convertView
						.findViewById(R.id.youhui_money);

				viewHolder.endDate = (TextView) convertView
						.findViewById(R.id.youhui_enddate);
				viewHolder.startDate = (TextView) convertView
						.findViewById(R.id.youhui_stardate);
				convertView.setTag(viewHolder);
			} else {

				viewHolder = (ViewHolder) convertView.getTag();
			}
  if(mydata.get(position)==null||mydata.get(position).counponentity==null)
	         return null;
			double couponmoney = mydata.get(position).counponentity.money;
			viewHolder.youhui_info.setText("" + UIUtils.decimalPrice(couponmoney));

			Date startDateTime =null;
			Date endDateTime =null;
			if(mydata.get(position).type==0)
			{
				//正常优惠劵
			startDateTime = mydata.get(position).counponentity.startDate;
			endDateTime = mydata.get(position).counponentity.endDate;
			}else
			{
				//分享优惠劵
				startDateTime = mydata.get(position).scanDate;
				endDateTime = mydata.get(position).useDate;
			}
			if (startDateTime != null) {
				String startdate = myFmt3.format(startDateTime);
				viewHolder.startDate.setText("" + startdate);
			}else
			{
				viewHolder.startDate.setText(Constants.TIME_NULL);
				
			}
			if (endDateTime != null) {
				String enddate = myFmt3.format(endDateTime);
				viewHolder.endDate.setText("" + enddate);
			}else
			{
				viewHolder.endDate.setText(Constants.TIME_NULL);
				
			}

			return convertView;
		}

	}

	@Override
	protected void onActivityResult(int requestcode, int resultcode, Intent data) {
		// TODO Auto-generated method stub

		if (resultcode == SCAN_SUCCESS) {
			String scanresultstring = data.getStringExtra("ScanResult");
			if(scanresultstring!=null&&!scanresultstring.toString().trim().equals(""))
			{
				edittext.setText(scanresultstring);
				findcoupon();
			}else
				Toast.makeText(getApplicationContext(), "请扫描正确的优惠劵",
						Toast.LENGTH_LONG).show();

		}

	}

	private void getRemoteData() {
		String pagestring="";
		if(CURRENT_PAGE>0)
		{
			pagestring="?p="+CURRENT_PAGE;
			
		}
		
		HttpRequestAni<CommFindEntity<TUseCouponEntity>> httpRequestAni = new HttpRequestAni<CommFindEntity<TUseCouponEntity>>(
				CouponDetailActivity.this, "/a/counpon/findcounponbyuserid/"
						+ SessionUtils.loginUser.userid+pagestring,
				new TypeToken<CommFindEntity<TUseCouponEntity>>() {
				}.getType()) {

			@Override
			public void callback(CommFindEntity<TUseCouponEntity> arg0) {

				
				if (arg0 == null)
					return;
				if (arg0.getRowCount() > 0) {
					// 如果返回的不为空并且总数大于0
					// 有数据的时候处理
					/*
					 * datacount=1; totalcount=arg0.getRowCount();
					 */
					/*
					 * if(arg0.getPageCount()>1)
					 * listView.setPullLoadEnable(true); else
					 * listView.setPullLoadEnable(false);
					 */
					TOTALE_PAGE=arg0.getPageCount();
					if(arg0.getPageCount()<=1)
					{
						listView.setPullLoadEnable(false);
					}else
					{
						listView.setPullLoadEnable(true);
					}
					okdata = arg0.getResult();

				} else if (arg0.getRowCount() == 0) {

					okdata.clear();
				}
				if (okdata == null || okdata.size() <= 0) {
					Toast.makeText(getApplicationContext(), "当前无有效停车券",
							Toast.LENGTH_LONG).show();
					listView.onRefreshComplete();

				}
				
				if(CURRENT_PAGE==0)
				{
				
					adapter.loadData(okdata);
					
				}
					else
						adapter.loadMore(okdata);

					
					if(listView.isRefreshing())
					{
						listView.onRefreshComplete();
						
					}
//				adapter.getdata(okdata);
//				listView.setAdapter(adapter);

			}

		};

		httpRequestAni.execute();

	}

	private void findcoupon() {

		String couponcode = edittext.getText().toString().trim();

		// 清空停车券edit field
		edittext.setText("");

		HttpRequestAni<ComResponse<TCouponEntity>> httpRequestAni = new HttpRequestAni<ComResponse<TCouponEntity>>(
				CouponDetailActivity.this, "/a/counpon/getcounpon?p="
						+ couponcode + "&s=" + SessionUtils.loginUser.userid,
				new TypeToken<ComResponse<TCouponEntity>>() {
				}.getType()) {

			@Override
			public void callback(ComResponse<TCouponEntity> arg0) {
				List<TCouponEntity> findcouponlist = new ArrayList<TCouponEntity>();
				if (arg0 == null) {

					Toast.makeText(getApplicationContext(), "未查询到停车券",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {
					Toast.makeText(getApplicationContext(), "添加停车券成功",
							Toast.LENGTH_LONG).show();

					getRemoteData();
				} else if(arg0.getResponseStatus() == ComResponse.STATUS_FAIL)
					Toast.makeText(getApplicationContext(),
							arg0.getErrorMessage(), Toast.LENGTH_LONG).show();
			}

		};

		httpRequestAni.execute();

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("userinfo", SessionUtils.loginUser);
		super.onSaveInstanceState(outState);
	}
	
	
	@Override
	public void onRestoreInstanceState(Bundle outState) {
	        super.onRestoreInstanceState(outState);
	        SessionUtils.loginUser=(TuserInfo) outState.getSerializable("userinfo");
	}

}
