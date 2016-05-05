package com.mc.parking.client.ui.admin;

import java.io.File;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.client.Constants;
import com.mc.park.client.R;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkInfo_ImgEntity;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.layout.BaseDialogFragment;
import com.mc.parking.client.layout.ViewPagerFixed;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.CommFindEntity;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.utils.DBHelper;
import com.mc.parking.client.utils.SessionUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



/**
 * 杩欎釜鏄敤浜庤繘琛屽浘鐗囨祻瑙堟椂鐨勭晫闈?
 *
 * @author king
 * @QQ:595163260
 * @version 2014骞?10鏈?18鏃?  涓嬪崍11:47:53
 */
public class GalleryActivity extends Activity {
	private Intent intent;
    // 杩斿洖鎸夐挳
    private Button back_bt;
	// 鍙戦?佹寜閽?
	private Button send_bt;
	//鍒犻櫎鎸夐挳
	private Button del_bt;
	//椤堕儴鏄剧ず棰勮鍥剧墖浣嶇疆鐨則extview
	private TextView positionTextView;
	//鑾峰彇鍓嶄竴涓猘ctivity浼犺繃鏉ョ殑position
	private int position;
	//褰撳墠鐨勪綅缃?
	private int location = 0;
	
	private ArrayList<View> listViews = null;
	private ViewPagerFixed pager;
	private MyPageAdapter adapter;

	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	private Context mContext;
	
	private DBHelper dbHelper = null;

	RelativeLayout photo_relativeLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ac_addpark_imageview);
		//PublicWay.activityList.add(this);
		mContext = this;
		back_bt = (Button) findViewById(R.id.gallery_back);
		
		del_bt = (Button)findViewById(R.id.gallery_del);
		back_bt.setOnClickListener(new BackListener());
		
		del_bt.setOnClickListener(new DelListener());
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		position =bundle.getInt("position");
			
    
		//isShowOkBt();
		// 涓哄彂閫佹寜閽缃枃瀛?gallery01
		pager = (ViewPagerFixed) findViewById(R.id.gallery01);
		pager.setOnPageChangeListener(pageChangeListener);
		updatedata();
		//pager.setPageMargin((int)getResources().getDimensionPixelOffset(Res.getDimenID("ui_10_dip")));
		
		pager.setCurrentItem(position);
	}
	
	
	private void updatedata()
	{
		for (int i = 0; i < Bimp.tempTParkImageList.size(); i++) {
			initListViews(i);
		}
		
		adapter = new MyPageAdapter(listViews);
		pager.setAdapter(adapter);
		
	}
	
	public DBHelper getHelper() {
		if (dbHelper == null) {
			dbHelper = OpenHelperManager.getHelper(getApplicationContext(),
					DBHelper.class);
		}
		return dbHelper;
	}
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {
			location = arg0;
			
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageScrollStateChanged(int arg0) {

		}
	};
	
	private void initListViews(int i) {
		if (listViews == null)
			listViews = new ArrayList<View>();
		ImageView img = new ImageView(this);
		img.setBackgroundColor(0xff000000);
		
		if(Bimp.tempTParkImageList.get(i).imgUrlHeader!=null&&!Bimp.tempTParkImageList.get(i).imgUrlHeader.toString().trim().equals(""))
		{
		
			imageLoader.displayImage(Bimp.tempTParkImageList.get(i).imgUrlHeader+Bimp.tempTParkImageList.get(i).imgUrlPath, img);
		
		}
			else
		{
			String uristring="file://"+Bimp.tempTParkImageList.get(i).imgUrlPath;
		
			imageLoader.displayImage(uristring.toString(),img);
//		img.setImageURI(Uri.parse("file://mnt"
//						+ Bimp.tempTParkImageList.get(i).imgUrlPath));		
		}
		
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		listViews.add(img);
	}
	
	// 杩斿洖鎸夐挳娣诲姞鐨勭洃鍚櫒
	private class BackListener implements OnClickListener {

		public void onClick(View v) {
			finish();
		}
	}
	
	// 鍒犻櫎鎸夐挳娣诲姞鐨勭洃鍚櫒
	private class DelListener implements OnClickListener {

		public void onClick(View v) {
			
		
			
			
			BaseDialogFragment confirmDialog = new BaseDialogFragment();
			confirmDialog.setMessage("确认刪除吗?");
			confirmDialog.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							deleitem();
							
						}
					});
			confirmDialog.setNegativeButton("取消", null);
			confirmDialog.show(getFragmentManager(), "");
			
			

			
			
		}
	}

	// 瀹屾垚鎸夐挳鐨勭洃鍚?
	private class GallerySendListener implements OnClickListener {
		public void onClick(View v) {
			finish();
//			intent.setClass(mContext,MainActivity.class);
//			startActivity(intent);
		}

	}

	private void deleitem()
	{

		
			//网络图片
			if(Bimp.tempTParkImageList.get(location).imgUrlHeader!=null&&!Bimp.tempTParkImageList.get(location).imgUrlHeader.trim().equals(""))
			{
		
				HttpRequestAni<ComResponse<TParkInfo_ImgEntity>> httpRequestAni = new HttpRequestAni<ComResponse<TParkInfo_ImgEntity>>(
						GalleryActivity.this, "/a/image/delete/"+Bimp.tempTParkImageList.get(location).parkImgId,
						new TypeToken<ComResponse<TParkInfo_ImgEntity>>() {
						}.getType()) {

					@Override
					public void callback(ComResponse<TParkInfo_ImgEntity> arg0) {

						if (ComResponse.STATUS_OK==arg0.getResponseStatus()) {						
							Bimp.tempTParkImageList.remove(location);
							finish();
						} else {
							// 没有任何提交数据的处理
							Toast.makeText(getApplicationContext(), "删除错误", Toast.LENGTH_SHORT).show();
						}

					}

				};

				httpRequestAni.execute();
				
				
			}else
			{
		
				if(Bimp.tempTParkImageList.get(location).ID!=null&&Bimp.tempTParkImageList.get(location).ID!=0)
				{
				long id=Bimp.tempTParkImageList.get(location).ID;
			
				try {
					Dao<TParkInfo_ImgEntity, Integer> daoima = getHelper()
							.getParkdetail_imagDao();
					
					DeleteBuilder<TParkInfo_ImgEntity, Integer> deleteBuilderloc = daoima
							.deleteBuilder();
					deleteBuilderloc.where().eq("ID", id);
					 deleteBuilderloc.delete();
					 File file=new File(Bimp.tempTParkImageList.get(location).imgUrlPath.toString());
						file.delete();
				     	Bimp.tempTParkImageList.remove(location);
				     	finish();
						Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "删除失败", Toast.LENGTH_SHORT).show();
				}
				} 
				else
				{
					//新增图片没有ID
					File file=new File(Bimp.tempTParkImageList.get(location).imgUrlPath.toString());
					file.delete();
			     	Bimp.tempTParkImageList.remove(location);
			     	finish();
			     	Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
					
				}
				
			    
			}
			
		
	}

	/**
	 * 鐩戝惉杩斿洖鎸夐挳
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return true;
	}
	
	
	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;

		private int size;
		public MyPageAdapter(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {
			try {
				((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
}
