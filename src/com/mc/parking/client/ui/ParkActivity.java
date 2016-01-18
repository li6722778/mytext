package com.mc.parking.client.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.OperationCanceledException;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.google.gson.reflect.TypeToken;
import com.mc.parking.client.Constants;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.ParkCommentsEntity;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkInfo_ImgEntity;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.layout.ActionBaseActivity;
import com.mc.parking.client.layout.BaseViewPagerIndicator;
import com.mc.parking.client.layout.CustomProgressDialog;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.HttpRequest;
import com.mc.parking.client.ui.fragment.LoginDialogFragment.LoginInputListener;
import com.mc.parking.client.ui.fragment.ParkingCommentsFragment;
import com.mc.parking.client.ui.fragment.ParkingInfoFragment;
import com.mc.parking.client.utils.SafeAsyncTask;
import com.mc.parking.client.utils.SessionUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ParkActivity extends ActionBaseActivity  {
	private TParkInfo_LocEntity tParkInfo_LocEntity;
	private String[] imageUrls;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private ViewPager viewPager;
	private String[] columns = new String[] { "详情", "评论" };
	public android.support.v4.app.Fragment tabFragments[];
	private PopupWindow popupWindow;
	public static int PROGRESS = 0;
	public static final int STOP_PROGRESS = 0;
	private EditText edit;
	private Activity activity;
	private View popupWindow_view;
	private BaseViewPagerIndicator mIndicator ;
	

	public void gotoPage() {
		Intent intent = new Intent(this, YuyueActivity.class);
		Bundle buidle = new Bundle();
		buidle.putSerializable("parkinfoLoc", tParkInfo_LocEntity);
		intent.putExtras(buidle);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onDestroy() {
		if (popupWindow != null) {
			popupWindow.dismiss();
			popupWindow = null;
		}
		super.onDestroy();
	}

	public  void  changetotalcomments(int total) {
		
		columns[1]="评论("+total+")";
		mIndicator.setTitles(columns);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.ac_parking_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_topbar);

		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		titleView.setText(R.string.parkdetail_title);
		
		RatingBar ratingBar = (RatingBar) findViewById(R.id.parkdetail_rat);

		// added for rating
		ImageButton imageButton = (ImageButton) findViewById(R.id.topbarRightButton);
		imageButton.setVisibility(View.VISIBLE);
		imageButton.setEnabled(false);
		if (SessionUtils.isLogined() == true) {
			imageButton.setEnabled(true);
			imageButton.setBackgroundResource(R.drawable.star_selected);

			imageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showCommentsPopup();
				}
			});
		}

		tParkInfo_LocEntity = (TParkInfo_LocEntity) getIntent()
				.getSerializableExtra("parkinfo");
		
		ratingBar.setRating(tParkInfo_LocEntity.parkInfo.averagerat);
		
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_empty).cacheInMemory(true)
				.cacheOnDisc(false).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		// 停车场名
		TextView name = (TextView) findViewById(R.id.parkdetail_name);
		// 停车场地址
		TextView adress = (TextView) findViewById(R.id.parkdetail_address);

		if (tParkInfo_LocEntity.parkInfo != null) {
			name.setText(tParkInfo_LocEntity.parkInfo.parkname);
			adress.setText(tParkInfo_LocEntity.parkInfo.address);
		}

		// 距离时间
		TextView distance = (TextView) findViewById(R.id.parkdetail_distance);
		distance.setText(" 距您" + tParkInfo_LocEntity.distance + "米");

		// 预计达到时间

		ImageView pevImg = (ImageView) findViewById(R.id.packdetail_img);

		imageUrls = new String[tParkInfo_LocEntity.parkInfo.imgUrlArray.size()];
		for (int i = 0; i < imageUrls.length; i++) {
			TParkInfo_ImgEntity tParkInfo_ImgEntity = tParkInfo_LocEntity.parkInfo.imgUrlArray
					.get(i);
			imageUrls[i] = tParkInfo_ImgEntity.imgUrlHeader
					+ tParkInfo_ImgEntity.imgUrlPath;
		}
		if (imageUrls != null && imageUrls.length > 0) {
			TextView packdetail_img_number = (TextView) findViewById(R.id.packdetail_img_number);
			packdetail_img_number.setText("" + imageUrls.length);
			imageLoader.displayImage(imageUrls[0], pevImg, options);
		}

		tabFragments = new android.support.v4.app.Fragment[] {
				new ParkingInfoFragment(), new ParkingCommentsFragment() };
		viewPager = (ViewPager) findViewById(R.id.id_stickynavlayout_viewpager);
		mIndicator = (BaseViewPagerIndicator) findViewById(R.id.id_stickynavlayout_indicator);
		mIndicator.setTitles(columns);
		mIndicator.setViewPage(viewPager);
		// 创建一个FragmentPagerAdapter对象，该对象负责为ViewPager提供多个Fragment
		FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(
				getSupportFragmentManager()) {
			// 获取第position位置的Fragment
			@Override
			public android.support.v4.app.Fragment getItem(int position) {
				//
				// Bundle args = new Bundle();
				// args.putInt(OfflineMapFragment.ARG_SECTION_NUMBER, position +
				// 1);
				// fragment.setArguments(args);
				return tabFragments[position];
			}

			@Override
			public int getCount() {
				return columns.length;
			}

		};

		// 为ViewPager组件设置FragmentPagerAdapter
		viewPager.setAdapter(pagerAdapter); // ①
		// 为ViewPager组件绑定事件监听器
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			// 当ViewPager显示的Fragment发生改变时激发该方法
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				mIndicator.scroll(position, positionOffset);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		viewPager.setCurrentItem(0);
	}

	/**
	 * 弹出评论的popup
	 * 
	 */
	public void showCommentsPopup() {

		if (popupWindow == null) {
			initpopwindows();
		}
		popupWindow.showAtLocation(popupWindow_view,
				MarginLayoutParams.WRAP_CONTENT, 0, 120);
		backgroundAlpha(0.5f);
	}

	private void initpopwindows() {
		// TODO Auto-generated method stub

		popupWindow_view = getLayoutInflater().inflate(R.layout.ac_popupwindow,
				null, false);

		// 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
		popupWindow = new PopupWindow(popupWindow_view,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		popupWindow.setAnimationStyle(R.style.popwindow_style); // 设置动画

		// 设置动画效果
		// popupWindow.setAnimationStyle(R.style.AnimationFade);
		// 这里是位置显示方式,在屏幕的左侧

		ColorDrawable cd = new ColorDrawable(0x000000);
		popupWindow.setBackgroundDrawable(cd);
		backgroundAlpha(0.5f);
		popupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
			    backgroundAlpha(1f); 
				
			}
		});
		
		edit = (EditText) popupWindow_view.findViewById(R.id.comment);

		Button backButton = (Button) popupWindow_view.findViewById(R.id.back);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (popupWindow != null)
					popupWindow.dismiss();

			}
		});
		final RatingBar ratescore = (RatingBar) popupWindow_view
				.findViewById(R.id.comments_rate);

		Button arugeButton = (Button) popupWindow_view
				.findViewById(R.id.submit_comment);

		arugeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Long parkingId = tParkInfo_LocEntity.parkInfo.parkId;

				String comments = edit.getText().toString();
				if (comments.trim().equals("")) {
					Toast.makeText(ParkActivity.this, "评论不能为空" ,
							Toast.LENGTH_SHORT).show();
					return;
				}
				TParkInfoEntity parkInfo = new TParkInfoEntity();
				parkInfo.parkId = parkingId;
				float rating = (float) ratescore.getRating();

				ParkCommentsEntity parkCommentsEntity = new ParkCommentsEntity(
						parkInfo, comments, rating);
				HttpRequest<ComResponse<ParkCommentsEntity>> httpRequestAni = new HttpRequest<ComResponse<ParkCommentsEntity>>(
						Method.POST, parkCommentsEntity, "/a/comments/save",
						new TypeToken<ComResponse<ParkCommentsEntity>>() {
						}.getType(), ParkCommentsEntity.class) {

					@Override
					public void onSuccess(ComResponse<ParkCommentsEntity> arg0) {
						if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {

							Message msg = new Message();
							commentsghandler.sendMessage(msg);

						} else {
							Toast.makeText(ParkActivity.this,
									"[异常]" + arg0.getErrorMessage(),
									Toast.LENGTH_SHORT).show();

						}

					}
					@Override
					public void onFailed(String message) {
						Toast.makeText(ParkActivity.this, "[异常]"+message,
								Toast.LENGTH_SHORT).show();

					}

				};

				httpRequestAni.execute();

			}
		});

	}

	public TParkInfo_LocEntity getParkInfo() {
		return tParkInfo_LocEntity;
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 1) {

		} else if (resultCode == Constants.ResultCode.HOME) {
			setResult(resultCode);
			finish();
		} else if (resultCode == Constants.ResultCode.NAVATIGOR_START) {
			setResult(Constants.ResultCode.NAVATIGOR_START, data);
			finish();
		}if (resultCode == Constants.ResultCode.ORDER_LIST_RELOAD) {
			setResult(Constants.ResultCode.ORDER_LIST_RELOAD, data);
			finish();
		}
	}

	SafeAsyncTask<Object> calcelOrderTask = new SafeAsyncTask<Object>() {
		@Override
		public Object call() throws Exception {
			// 取消订单bean
			try {
				Thread.sleep(3 * 1000);
			} catch (Exception e) {

			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog
						.createDialog(ParkActivity.this);
				progressDialog.setMessage("正在取消订单,请等待...");
			}
			progressDialog.show();
		}

		@Override
		protected void onException(final Exception e) throws RuntimeException {
			super.onException(e);
			progressDialog.clear();
			if (e instanceof OperationCanceledException) {
				System.err.println(e);
			}
		}

		@Override
		protected void onSuccess(final Object data) throws Exception {
			super.onSuccess(data);
			Message msg = new Message();
			msg.arg1 = 2;
			progressDialog.clear();
		}
	};

	public void showphoto(View v) {
		switch (v.getId()) {
		case R.id.packdetail_img_frame:
			Intent intent = new Intent(ParkActivity.this,
					UploadPhotoActivity.class);
			Bundle buidle = new Bundle();
		    TParkInfoEntity tParkInfoEntity = tParkInfo_LocEntity.parkInfo;
			buidle.putSerializable("parkinfo", tParkInfoEntity);
			intent.putExtras(buidle);
			startActivity(intent);
			break;
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (popupWindow != null)
			popupWindow.dismiss();
		super.onBackPressed();
	}

	private Handler commentsghandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			edit.setText("");
			Toast.makeText(ParkActivity.this, "添加评论成功", Toast.LENGTH_SHORT)
					.show();
			popupWindow.dismiss();
			ParkingCommentsFragment parkingCommentsFragment = (ParkingCommentsFragment) tabFragments[1];
			parkingCommentsFragment.getfirstdata(tParkInfo_LocEntity);
		}
	};

	  public void backgroundAlpha(float bgAlpha)  
	    {  
	        WindowManager.LayoutParams lp = getWindow().getAttributes();  
	        lp.alpha = bgAlpha; //0.0-1.0  
	                getWindow().setAttributes(lp);  
	    }  
}