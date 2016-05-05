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
 * 这个是用于进行图片浏览时的界�?
 *
 * @author king
 * @QQ:595163260
 * @version 2014�?10�?18�?  下午11:47:53
 */
public class GalleryActivity extends Activity {
	private Intent intent;
    // 返回按钮
    private Button back_bt;
	// 发�?�按�?
	private Button send_bt;
	//删除按钮
	private Button del_bt;
	//顶部显示预览图片位置的textview
	private TextView positionTextView;
	//获取前一个activity传过来的position
	private int position;
	//当前的位�?
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
		// 为发送按钮设置文�?gallery01
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
	
	// 返回按钮添加的监听器
	private class BackListener implements OnClickListener {

		public void onClick(View v) {
			finish();
		}
	}
	
	// 删除按钮添加的监听器
	private class DelListener implements OnClickListener {

		public void onClick(View v) {
			
		
			
			
			BaseDialogFragment confirmDialog = new BaseDialogFragment();
			confirmDialog.setMessage("ȷ�τh����?");
			confirmDialog.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							deleitem();
							
						}
					});
			confirmDialog.setNegativeButton("ȡ��", null);
			confirmDialog.show(getFragmentManager(), "");
			
			

			
			
		}
	}

	// 完成按钮的监�?
	private class GallerySendListener implements OnClickListener {
		public void onClick(View v) {
			finish();
//			intent.setClass(mContext,MainActivity.class);
//			startActivity(intent);
		}

	}

	private void deleitem()
	{

		
			//����ͼƬ
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
							// û���κ��ύ���ݵĴ���
							Toast.makeText(getApplicationContext(), "ɾ������", Toast.LENGTH_SHORT).show();
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
						Toast.makeText(getApplicationContext(), "ɾ���ɹ�", Toast.LENGTH_SHORT).show();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "ɾ��ʧ��", Toast.LENGTH_SHORT).show();
				}
				} 
				else
				{
					//����ͼƬû��ID
					File file=new File(Bimp.tempTParkImageList.get(location).imgUrlPath.toString());
					file.delete();
			     	Bimp.tempTParkImageList.remove(location);
			     	finish();
			     	Toast.makeText(getApplicationContext(), "ɾ���ɹ�", Toast.LENGTH_SHORT).show();
					
				}
				
			    
			}
			
		
	}

	/**
	 * 监听返回按钮
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
