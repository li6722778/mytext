package com.mc.parking.client.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.mc.parking.client.Constants;
import com.mc.parking.client.PackingApplication;
import com.mc.parking.client.R;
import com.mc.parking.client.adapter.IndexGalleryAdapter;
import com.mc.parking.client.entity.IndexGalleryItemData;
import com.mc.parking.client.layout.BaseActivity;
import com.mc.parking.client.layout.BaseDialogFragment;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.widgets.HomeSearchBarPopupWindow;
import com.mc.parking.widgets.HomeSearchBarPopupWindow.onSearchBarItemClickListener;
import com.mc.parking.widgets.JazzyViewPager;
import com.mc.parking.widgets.JazzyViewPager.TransitionEffect;
import com.mc.parking.widgets.OutlineContainer;
import com.mc.parking.zxing.camera.MipcaActivityCapture;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources.NotFoundException;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class IndexActivity extends BaseActivity implements OnClickListener,
		onSearchBarItemClickListener {
	public static final String TAG = IndexActivity.class.getSimpleName();

	private ImageView mMiaoShaImage = null;
	private TextView mIndexHour = null;
	private TextView mIndexMin = null;
	private TextView mIndexSeconds = null;
	private TextView mIndexPrice = null;
	private TextView mIndexRawPrice = null;

	// =============‰∏≠ÈÉ®ÂØºËà™Ê†èÊ®°Âù?=====
	private ImageButton shake, baidumapbnt, shake1;
	private Intent mIntent;

	// ============== ÂπøÂëäÂàáÊç¢ ===================
	private JazzyViewPager mViewPager = null;
	/**
	 * Ë£ÖÊåáÂºïÁöÑImageViewÊï∞ÁªÑ
	 */
	private ImageView[] mIndicators;

	/**
	 * Ë£ÖViewPager‰∏≠ImageViewÁöÑÊï∞Áª?
	 */
	private ImageView[] mImageViews;
	private List<String> mImageUrls = new ArrayList<String>();
	private LinearLayout mIndicator = null;
	private String mImageUrl = null;
	private static final int MSG_CHANGE_PHOTO = 1;
	/** ÂõæÁâáËá™Âä®ÂàáÊç¢Êó∂Èó¥ */
	private static final int PHOTO_CHANGE_TIME = 3000;
	// ============== ÂπøÂëäÂàáÊç¢ ===================

	private Gallery mStormGallery = null;
	private Gallery mPromotionGallery = null;
	private IndexGalleryAdapter mStormAdapter = null;
	private IndexGalleryAdapter mPromotionAdapter = null;
	private List<IndexGalleryItemData> mStormListData = new ArrayList<IndexGalleryItemData>();
	private List<IndexGalleryItemData> mPromotionListData = new ArrayList<IndexGalleryItemData>();
	private IndexGalleryItemData mItemData = null;
	private HomeSearchBarPopupWindow mBarPopupWindow = null;
	private EditText mSearchBox = null;
	private ImageButton mCamerButton = null;
	private ImageButton mCamerButton1 = null;
	private ImageButton ShowProductBut = null;
	private ImageButton LogeInButton = null;
	private ImageButton MyproductBn = null;
	private ImageButton LoginButton = null;
	private LinearLayout mTopLayout = null;
	private LinearLayout mimagebutn = null;
	private RelativeLayout hotestview = null;

	String[] bbbb;
	private Handler mHandler;

	private ArrayList<String> arrayList = new ArrayList<String>();
	private ArrayList<String> brrayList = new ArrayList<String>();
	private ArrayList<String> crrayList = new ArrayList<String>();

	int morebool = 0;
	int mastnumb = 20;
	JSONArray arraya = null;
	JSONArray resultarraya = null;
	JSONArray currentarraya = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
		findViewById();

		mHandler = new Handler(getMainLooper()) {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_CHANGE_PHOTO:
					int index = mViewPager.getCurrentItem();
					if (index == mImageUrls.size() - 1) {
						index = -1;
					}
					mViewPager.setCurrentItem(index + 1);
					mHandler.sendEmptyMessageDelayed(MSG_CHANGE_PHOTO,
							PHOTO_CHANGE_TIME);
				}
			}

		};

		initData();
		initView();

	}

	protected void findViewById() {
		// TODO Auto-generated method stub
		mIndexHour = (TextView) findViewById(R.id.index_miaosha_hour);
		// mIndexMin = (TextView) findViewById(R.id.index_miaosha_min);
		// mIndexSeconds = (TextView) findViewById(R.id.index_miaosha_seconds);
		mIndexPrice = (TextView) findViewById(R.id.index_miaosha_price);
		mIndexRawPrice = (TextView) findViewById(R.id.index_miaosha_raw_price);

		mMiaoShaImage = (ImageView) findViewById(R.id.index_miaosha_image);
		mViewPager = (JazzyViewPager) findViewById(R.id.index_product_images_container);
		mIndicator = (LinearLayout) findViewById(R.id.index_product_images_indicator);

		mStormGallery = (Gallery) findViewById(R.id.index_jingqiu_gallery);
		mPromotionGallery = (Gallery) findViewById(R.id.index_tehui_gallery);

		mSearchBox = (EditText) findViewById(R.id.index_search_edit);
		mCamerButton = (ImageButton) findViewById(R.id.index_camer_button);
		mCamerButton1 = (ImageButton) findViewById(R.id.index_promotion_btn);
		baidumapbnt = (ImageButton) findViewById(R.id.index_nearpark_btn);

		LogeInButton = (ImageButton) findViewById(R.id.index_order_btn);
		MyproductBn = (ImageButton) findViewById(R.id.index_collect_btn);

		ShowProductBut = (ImageButton) findViewById(R.id.index_mainactivity_btn);
		mTopLayout = (LinearLayout) findViewById(R.id.index_top_layout);

		LoginButton = (ImageButton) findViewById(R.id.index_personinfo_btn);
		shake = (ImageButton) findViewById(R.id.index_shake);
		shake1 = (ImageButton) findViewById(R.id.index_groupbuy_btn);
		hotestview = (RelativeLayout) findViewById(R.id.main_hot_view);

		// Ê∑ªÂä†‰∫ã‰ª∂
		hotestview.setOnClickListener(indexClickListener);
		shake.setOnClickListener(indexClickListener);
		shake1.setOnClickListener(indexClickListener);
		ShowProductBut.setOnClickListener(indexClickListener);
		baidumapbnt.setOnClickListener(indexClickListener);
		LogeInButton.setOnClickListener(indexClickListener);
		MyproductBn.setOnClickListener(indexClickListener);
		LoginButton.setOnClickListener(indexClickListener);

	}

	private OnClickListener indexClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			
			case R.id.index_shake:

				  mIntent = new Intent(IndexActivity.this,
				  CouponDetailActivity.class);
				startActivity(mIntent);
				break;
			case R.id.index_mainactivity_btn:
				if (SessionUtils.isLogined()) {
					mIntent = new Intent(IndexActivity.this, MainActivity.class);
					startActivity(mIntent);
				} else {
					Intent intentLogin = new Intent(IndexActivity.this,
							LoginActivity.class);
					intentLogin.putExtra("parent",
							LoginActivity.parent_oderinfo);
					startActivity(intentLogin);
				}
				break;
			case R.id.index_nearpark_btn:
				if (SessionUtils.isLogined()) {
					mIntent = new Intent(IndexActivity.this,
							MapParkListviewActivity.class);
					startActivity(mIntent);
				} else {
					Intent intentLogin = new Intent(IndexActivity.this,
							LoginActivity.class);
					intentLogin.putExtra("parent",
							LoginActivity.parent_oderinfo);
					startActivity(intentLogin);
				}

				break;
			case R.id.index_groupbuy_btn:
				
				 mIntent = new Intent(IndexActivity.this, ActivityHomeList.class);
				  
				  startActivity(mIntent);
				 
				break;
			case R.id.index_order_btn:
				// mIntent = new Intent(IndexActivity.this,
				// CircleActivity.class);
				// startActivity(mIntent);
				Toast.makeText(getApplicationContext(), "ÊöÇÊú™Âº?ÊîæÔºÅ",
						Toast.LENGTH_SHORT).show();
				break;
			case R.id.main_hot_view:
				Intent inte = new Intent();

				/*
				 * String[] datainfo = {
				 * currentarraya.getJSONObject(0).getString( "BRANDBRAND"),
				 * currentarraya.getJSONObject(0).getString(
				 * "BRANDPRODUCINGUNITS"), currentarraya.getJSONObject(0)
				 * .getString("BRANDTEL"),
				 * currentarraya.getJSONObject(0).getString( "BRANDHOMEIMAGE"),
				 * currentarraya.getJSONObject(0).getString( "ProductType"),
				 * currentarraya.getJSONObject(0).getString(
				 * "BRANDPRODUCTCONTENT"),
				 * currentarraya.getJSONObject(0).getString( "BRANDBRAND") };
				 * inte.setClass(getApplicationContext(),
				 * ProductItemInfo.class); inte.putExtra("datainfo", datainfo);
				 * startActivity(inte);
				 */

				break;
			case R.id.index_collect_btn:

				/*
				 * mIntent = new Intent(IndexActivity.this,
				 * PirvateListingActivity.class); startActivity(mIntent);
				 */

				break;

			case R.id.index_personinfo_btn:

				if (SessionUtils.isLogined()) {

					mIntent = new Intent(IndexActivity.this,
							UserInfoActivity.class);

					startActivity(mIntent);
				} else {
					Intent intentLogin = new Intent(IndexActivity.this,
							LoginActivity.class);
					intentLogin.putExtra("parent",
							LoginActivity.parent_oderinfo);
					startActivity(intentLogin);
				}

				break;

			default:
				break;
			}

		}
	};

	protected void initView() {

		// TODO Auto-generated method stub

		// mIndexRawPrice.getPaint().setFlags(
		// Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

		// ======= top viewPager ========
		mIndicators = new ImageView[mImageUrls.size()];
		if (mImageUrls.size() <= 1) {
			mIndicator.setVisibility(View.GONE);
		}

		for (int i = 0; i < mIndicators.length; i++) {
			ImageView imageView = new ImageView(this);
			LayoutParams params = new LayoutParams(0,
					LayoutParams.WRAP_CONTENT, 1.0f);
			if (i != 0) {
				params.leftMargin = 5;
			}
			imageView.setLayoutParams(params);
			mIndicators[i] = imageView;
			if (i == 0) {
				mIndicators[i]
						.setBackgroundResource(R.drawable.android_activities_cur);
			} else {
				mIndicators[i]
						.setBackgroundResource(R.drawable.android_activities_bg);
			}

			mIndicator.addView(imageView);
		}
		Toast.makeText(getApplicationContext(), "" + mIndicators.length,
				Toast.LENGTH_SHORT).show();

		mImageViews = new ImageView[mImageUrls.size()];

		for (int i = 0; i < mImageViews.length; i++) {

			ImageView imageView = new ImageView(this);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			mImageViews[i] = imageView;
		}
		mViewPager.setTransitionEffect(TransitionEffect.CubeOut);
		mViewPager.setCurrentItem(0);
		mHandler.sendEmptyMessageDelayed(MSG_CHANGE_PHOTO, PHOTO_CHANGE_TIME);

		mViewPager.setAdapter(new MyAdapter());
		mViewPager.setOnPageChangeListener(new MyPageChangeListener());
		mViewPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (mImageUrls.size() == 0 || mImageUrls.size() == 1)
					return true;
				else
					return false;
			}
		});

		// =======≥ı ºªØ viewPager ========

		mStormAdapter = new IndexGalleryAdapter(this,
				R.layout.activity_index_gallery_item, mStormListData,
				new int[] { R.id.index_gallery_item_image,
						R.id.index_gallery_item_text });

		mStormGallery.setAdapter(mStormAdapter);

		mPromotionAdapter = new IndexGalleryAdapter(this,
				R.layout.activity_index_gallery_item, mPromotionListData,
				new int[] { R.id.index_gallery_item_image,
						R.id.index_gallery_item_text });

		mPromotionGallery.setAdapter(mPromotionAdapter);

		mStormGallery.setSelection(3);
		mPromotionGallery.setSelection(3);

		mBarPopupWindow = new HomeSearchBarPopupWindow(this,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mBarPopupWindow.setOnSearchBarItemClickListener(this);

		mCamerButton.setOnClickListener(this);
		mCamerButton1.setOnClickListener(this);
		mSearchBox.setOnClickListener(this);
		mStormGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Ëá™Âä®ÁîüÊàêÁöÑÊñπÊ≥ïÂ≠òÊ†?
				Intent inte = new Intent();

				/*
				 * String[] datainfo = {
				 * currentarraya.getJSONObject(arg2).getString( "BRANDBRAND"),
				 * currentarraya.getJSONObject(arg2).getString(
				 * "BRANDPRODUCINGUNITS"),
				 * currentarraya.getJSONObject(arg2).getString( "BRANDTEL"),
				 * currentarraya.getJSONObject(arg2).getString(
				 * "BRANDHOMEIMAGE"),
				 * currentarraya.getJSONObject(arg2).getString( "ProductType"),
				 * currentarraya.getJSONObject(arg2).getString(
				 * "BRANDPRODUCTCONTENT"),
				 * currentarraya.getJSONObject(arg2).getString( "BRANDBRAND") };
				 * inte.setClass(getApplicationContext(),
				 * ProductItemInfo.class); inte.putExtra("datainfo", datainfo);
				 * startActivity(inte);
				 */

			}

		});

		mPromotionGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				Intent inte = new Intent();

			}
		});

		mSearchBox.setInputType(InputType.TYPE_NULL);
	}

	private void initanimotion() {

		YoYo.with(Techniques.FadeIn).duration(700)
				.playOn(findViewById(R.id.id_imagebutton));
	}

	private void initData() {
		mImageUrl = "drawable://" + R.drawable.wlwwlwaaafirstimage1;
		mImageUrls.add(mImageUrl);

		mImageUrl = "drawable://" + R.drawable.wlwwlwaaafirstimage1;
		mImageUrls.add(mImageUrl);

		mImageUrl = "drawable://" + R.drawable.wlwwlwaaafirstimage1;
		mImageUrls.add(mImageUrl);

		// mImageUrl = "drawable://" + R.drawable.image04;
		// mImageUrls.add(mImageUrl);
		//
		// mImageUrl = "drawable://" + R.drawable.image05;
		// mImageUrls.add(mImageUrl);
		//
		// mImageUrl = "drawable://" + R.drawable.image06;
		// mImageUrls.add(mImageUrl);
		//
		// mImageUrl = "drawable://" + R.drawable.image07;
		// mImageUrls.add(mImageUrl);
		//
		// mImageUrl = "drawable://" + R.drawable.image08;

		/*
		 * SharedPreferences sharedPreferences1 =
		 * getSharedPreferences("MainData", 0); String jsonarraystring =
		 * sharedPreferences1.getString("hotdata", ""); try { currentarraya =
		 * new JSONArray(jsonarraystring); } catch (JSONException e) { // TODO
		 * Ëá™Âä®ÁîüÊàêÁö? catch Âù? e.printStackTrace(); }
		 */
		// for (int i = 0; i <bbbb.length; i++) {
		//
		//
		// mItemData = new IndexGalleryItemData();
		// mItemData.setId(i + 1);
		// mItemData.setImageUrl(bbbb[i]);
		// mItemData.setPrice("");
		// mStormListData.add(mItemData);
		// mPromotionListData.add(mItemData);
		//
		// }
		/*
		 * for (int i = 0; i < mImageUrls.size(); i++) {
		 * 
		 * try {
		 * 
		 * mItemData = new IndexGalleryItemData(); mItemData.setId(i + 1);
		 * mItemData.setImageUrl(getResources().getString(
		 * R.string.imagebaseurl) + currentarraya.getJSONObject(i).getString(
		 * "BRANDHOMEIMAGE"));
		 * mItemData.setPrice(currentarraya.getJSONObject(i).getString(
		 * "BRANDBRAND")); if (i < currentarraya.length() / 2) {
		 * mStormListData.add(mItemData); } else {
		 * mPromotionListData.add(mItemData); } } catch (JSONException e) { //
		 * TODO Ëá™Âä®ÁîüÊàêÁö? catch Âù? e.printStackTrace(); }
		 * 
		 * }
		 */

		mItemData = new IndexGalleryItemData();
		mItemData.setId(1);
		mItemData
				.setImageUrl("http://192.168.0.2:126/Fujian/083962820020150113032647.jpg");
		mItemData.setPrice("Ôø?79.00");
		mStormListData.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(2);
		mItemData
				.setImageUrl("http://192.168.0.2:126/Fujian/083962801820150113043051.jpg");
		mItemData.setPrice("Ôø?89.00");
		mStormListData.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(3);
		mItemData
				.setImageUrl("http://192.168.0.2:126/Fujian/083962820020150113033216.jpg");
		mItemData.setPrice("Ôø?99.00");
		mStormListData.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(4);
		mItemData
				.setImageUrl("http://192.168.0.2:126/Fujian/083962820020150113024429.jpg");
		mItemData.setPrice("Ôø?109.00");
		mStormListData.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(5);
		mItemData
				.setImageUrl("http://192.168.0.2:126/Fujian/083962801820150113042950.JPG");
		mItemData.setPrice("Ôø?119.00");
		mStormListData.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(6);
		mItemData
				.setImageUrl("http://192.168.0.2:126/Fujian/083962801820150113043123.jpg");
		mItemData.setPrice("Ôø?129.00");
		mStormListData.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(7);
		mItemData
				.setImageUrl("http://192.168.0.2:126/Fujian/083962820020150113033102.jpg");
		mItemData.setPrice("Ôø?139.00");
		mStormListData.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(8);
		mItemData
				.setImageUrl("http://192.168.0.2:126/Fujian/083962820020150113015902.jpg");
		mItemData.setPrice("Ôø?69.00");
		mPromotionListData.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(9);
		mItemData
				.setImageUrl("http://192.168.0.2:126/Fujian/083962820020150113015526.jpg");
		mItemData.setPrice("Ôø?99.00");
		mPromotionListData.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(10);
		mItemData
				.setImageUrl("http://192.168.0.2:126/Fujian/083962820020150113031428.jpg");
		mItemData.setPrice("Ôø?109.00");
		mPromotionListData.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(11);
		mItemData
				.setImageUrl("http://192.168.0.2:126/Fujian/083962820020150113032757.jpg");
		mItemData.setPrice("Ôø?119.00");
		mPromotionListData.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(12);
		mItemData.setImageUrl("drawable://"
				+ R.drawable.abc_ab_share_pack_holo_dark);
		mItemData.setPrice("129.00");
		mPromotionListData.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(13);
		mItemData.setImageUrl("drawable://"
				+ R.drawable.abc_ab_share_pack_holo_dark);
		mItemData.setPrice("139.00");
		mPromotionListData.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(14);
		mItemData.setImageUrl("drawable://"
				+ R.drawable.abc_ab_share_pack_holo_dark);
		mItemData.setPrice("149.00");
		mPromotionListData.add(mItemData);
	}

	/**
	 * top  ≈‰∆˜
	 * 
	 * @author Administrator
	 *
	 */
	public class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {

			return mImageViews.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			if (view instanceof OutlineContainer) {
				return ((OutlineContainer) view).getChildAt(0) == obj;
			} else {
				return view == obj;
			}
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(mViewPager
					.findViewFromObject(position));
		}

		@Override
		// ≥ı º∂Øª≠
		public Object instantiateItem(View container, int position) {
			String a = "drawable://" + R.drawable.wlwwlwaaafirstimage1;
			ImageLoader.getInstance().displayImage(mImageUrls.get(position),
					mImageViews[position]);
			((ViewPager) container).addView(mImageViews[position], 0);
			mViewPager.setObjectForPosition(mImageViews[position], position);
			return mImageViews[position];
		}

	}

	/**
	 * º‡Ã˝topµƒ±‰ªØ
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			setImageBackground(position);
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/**
	 * …Ë÷√
	 * 
	 * @param selectItemsIndex
	 */
	private void setImageBackground(int selectItemsIndex) {
		for (int i = 0; i < mIndicators.length; i++) {
			if (i == selectItemsIndex) {
				mIndicators[i]
						.setBackgroundResource(R.drawable.android_activities_cur);
			} else {
				mIndicators[i]
						.setBackgroundResource(R.drawable.android_activities_bg);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.index_camer_button:
			// int height = mTopLayout.getHeight()
			// + CommonTools.getStatusBarHeight(this);
			// mBarPopupWindow.showAtLocation(mTopLayout, Gravity.TOP, 0,
			/*
			 * // height); mIntent = new Intent(IndexActivity.this,
			 * CaptureActivity.class); startActivity(mIntent);
			 */
			break;

		case R.id.index_search_edit:
			/*
			 * openActivity(XListViewActivity.class); break;
			 */
		case R.id.index_promotion_btn:
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(),
					MipcaActivityCapture.class);
			startActivityForResult(intent, 11);
			
			break;

		default:
			break;
		}
	}

	@Override
	public void onBarCodeButtonClick() {/*
										 * // TODO Auto-generated method stub //
										 * CommonTools.showShortToast(this,
										 * "Êù°Á†ÅË¥?");
										 * 
										 * mIntent = new
										 * Intent(IndexActivity.this,
										 * CaptureActivity.class);
										 * startActivity(mIntent);
										 */
	}

	@Override
	public void onCameraButtonClick() {/*
										 * // TODO Auto-generated method stub
										 * CommonTools.showShortToast(this,
										 * "ÊãçÁÖßË¥?");
										 */
	}

	@Override
	public void onColorButtonClick() {/*
									 * // TODO Auto-generated method stub
									 * CommonTools.showShortToast(this,
									 * "È¢úËâ≤Ë¥?");
									 */
	}

	@Override
	protected void onResume() {
		super.onResume();
		initanimotion();
	}

	public String getFromAssets(String fileName) {
		try {
			InputStreamReader inputReader = new InputStreamReader(
					getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			String Result = "";
			while ((line = bufReader.readLine()) != null)
				Result += line;
			return Result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/*
	 * private class MyThread implements Runnable {
	 * 
	 * public void run() { // TODO Auto-generated method stub try {
	 * 
	 * arrayList.clear(); brrayList.clear();
	 * 
	 * arrayList.add("brandid"); arrayList.add("BRANDBRAND");
	 * arrayList.add("m"); arrayList.add("n"); brrayList.add("");
	 * brrayList.add(""); brrayList.add("1"); brrayList.add("10"); resultarraya
	 * = Httptool.getAllInfo("PH_SearchProdcut1", arrayList, brrayList,
	 * "PH_SearchProdcut1Result", "Table"); currentarraya = resultarraya;
	 * 
	 * // if(morebool==0) // { // brrayList.add("1"); // brrayList.add("6"); //
	 * resultarraya // =Httptool.getAllInfo("PH_selectShopInfoByBarcode",
	 * arrayList, // brrayList,"PH_selectShopInfoByBarcodeResult","Table"); //
	 * currentarraya=resultarraya; // morebool=1; // }else // { // int
	 * aaaa=6+(morebool-1)*3+1; // int aaaa1=6+morebool*3; //
	 * brrayList.add(String.valueOf(aaaa)); //
	 * brrayList.add(String.valueOf(aaaa1)); // resultarraya //
	 * =Httptool.getAllInfo("PH_selectShopInfoByBarcode", arrayList, //
	 * brrayList,"PH_selectShopInfoByBarcodeResult","Table"); //
	 * currentarraya=Httptool.joinJSONArray(currentarraya, // resultarraya); //
	 * morebool++; // } // brrayList.add(String.valueOf(page*5+1)); //
	 * brrayList.add(String.valueOf((page+1)*5)); // int m = 1; // int n = 10;
	 * // brrayList.add(String.valueOf(m)); // brrayList.add(String.valueOf(n));
	 * // httpstring = // NewSoap.GetWebServre("PH_selectShopInfoByBarcode",
	 * arrayList, // brrayList);
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } Message msgMessage = new
	 * Message(); if (resultarraya == null) { msgMessage.arg1 = 1;
	 * handler.sendMessage(msgMessage); } else {
	 * 
	 * msgMessage.arg1 = 2; handler.sendMessage(msgMessage); }
	 * Log.e("ThreadName", Thread.currentThread().getName()); }
	 * 
	 * }
	 */

	/*
	 * private Handler handler = new Handler() { public void
	 * handleMessage(Message msg) { switch (msg.arg1) { case 1: try {
	 * 
	 * Toast.makeText(getApplicationContext(), "aaaaaaa",
	 * Toast.LENGTH_SHORT).show(); } catch (NumberFormatException e) { // TODO
	 * Ëá™Âä®ÁîüÊàêÁö? catch Âù? e.printStackTrace(); } initData(); findViewById();
	 * initView();
	 * 
	 * break; case 2:
	 * 
	 * SharedPreferences sharedPreferences = getSharedPreferences( "MainData",
	 * 0); SharedPreferences.Editor editor = sharedPreferences.edit();
	 * editor.putString("hotdata", resultarraya.toString()); editor.commit();
	 * initData(); findViewById(); initView(); break; case 3:
	 * 
	 * default: break; } } };
	 */
	@Override
	public void onBackPressed() {

		
		FragmentManager manager = getFragmentManager();
		while (manager.getBackStackEntryCount() > 0) {
			manager.popBackStackImmediate();
			manager.popBackStackImmediate();

			return;
		}

		// super.onBackPressed();
		BaseDialogFragment confirmDialog = new BaseDialogFragment();
		confirmDialog.setMessage("»∑»œÕÀ≥ˆœµÕ≥¬?");
		confirmDialog.setPositiveButton("»∑»œ",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					
						Intent intent = new Intent(Intent.ACTION_MAIN);
						intent.addCategory(Intent.CATEGORY_HOME);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						android.os.Process.killProcess(android.os.Process
								.myPid());
					}
				});
		confirmDialog.setNegativeButton("»°œ˚", null);
		confirmDialog.show(getFragmentManager(), "");
	}
	
	@Override
	protected void onActivityResult(int requestcode, int resultcode, Intent data) {
		// TODO Auto-generated method stub

		if (resultcode == 11) {
			String scanresultstring = data.getStringExtra("ScanResult");
			if(scanresultstring!=null&&!scanresultstring.toString().trim().equals(""))
			{
				Toast.makeText(getApplicationContext(), scanresultstring, Toast.LENGTH_SHORT).show();
			}else
				Toast.makeText(getApplicationContext(), "«Î…®√Ë’˝»∑µƒ”≈ª›Ñª",
						Toast.LENGTH_LONG).show();

		}

	}
}
