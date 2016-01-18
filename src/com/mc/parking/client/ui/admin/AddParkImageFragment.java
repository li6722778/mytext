package com.mc.parking.client.ui.admin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mc.addpic.utils.Bimp;
import com.mc.addpic.utils.ImageItem;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.TParkInfo_ImgEntity;
import com.mc.parking.client.utils.DBHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AddParkImageFragment extends Fragment {

	private GridView noScrollgridview;
	private GridAdapter adapter;
	private View parentView;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	public static Bitmap bimap;
	public static ArrayList<ImageItem> currentSelectBitmap = new ArrayList<ImageItem>();
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	List<TParkInfo_ImgEntity> parkimaglist = new ArrayList<TParkInfo_ImgEntity>();
	private static String currentImagefile = "";
	final String dir = Environment.getExternalStorageDirectory() + "/cbl_image/";
	DisplayImageOptions options;
	
	private Activity currentActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bimp.tempSelectBitmap.clear();

		View view = inflater.inflate(R.layout.fragment_admin_addimage,
				container, false);
		
		currentActivity = getActivity();
		
		createfile();
		bimap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_addpic_unfocused);
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_empty).cacheInMemory(true)
		.cacheOnDisc(false).considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565).build();
		view = Init(view);
		Binddata();

		return view;
	}

	public View Init(View view) {

		noScrollgridview = (GridView) view.findViewById(R.id.noScrollgridview);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(currentActivity);
		adapter.update();
		
		noScrollgridview.setAdapter(adapter);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == 0) {
					Log.i("ddddddd", "----------");
					photo();
				} else {
					
					Bimp.tempTParkImageList = parkimaglist;
					Intent intent = new Intent(currentActivity,
							GalleryActivity.class);
					intent.putExtra("position", arg2-1);
					intent.putExtra("ID", arg2-1);
					startActivity(intent);
				}
			}
		});

		return view;
	}
//创建图片文件
	void createfile() {
		File file = new File(dir);
		if (!file.exists()) {
			try {
				Log.d("", "create file path:" + file.getCanonicalPath());
				file.mkdirs();
			} catch (Exception e) {
				Log.e("AddParkImageFramment", e.getMessage(), e);
			}
		}

	}

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private int selectedPosition = -1;
		private boolean shape;

		public class ViewHolder {
			public ImageView image;
		}

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {

			return (parkimaglist.size() + 1);
		}

		public Object getItem(int arg0) {
			return arg0;
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_published_grida,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position > 0) {

				if (parkimaglist.get(position - 1).imgUrlHeader != null&&!parkimaglist.get(position - 1).imgUrlHeader.trim().equals("")) {
					imageLoader
							.displayImage(
									parkimaglist.get(position - 1).imgUrlHeader+parkimaglist.get(position - 1).imgUrlPath,
									holder.image);
				} else {
					imageLoader.displayImage(Uri.parse("file://"+ parkimaglist.get(position - 1).imgUrlPath).toString(), holder.image);
				}

			} else if (position == 0) {
				String mImageUrl = "drawable://"
						+ R.drawable.icon_addpic_unfocused;
				imageLoader.displayImage(mImageUrl, holder.image);

			}

			return convertView;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.tempSelectBitmap.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							Bimp.max += 1;
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
						}
					}
				}
			}).start();
		}
	}

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	public void photo() {
		// MediaStore.ACTION_IMAGE_CAPTURE
		// Intent openCameraIntent = new
		// Intent("android.media.action.IMAGE_CAPTURE");
		// startActivityForResult(openCameraIntent, 1);

		// File newdir = new File(dir);
		// if (!newdir.exists())
		// newdir.mkdirs();

		// here,counter will be incremented each time,and the picture taken by
		// camera will be stored as 1.jpg,2.jpg and likewise.

		currentImagefile = dir + String.valueOf(System.currentTimeMillis()) + ".jpg";

		String uristring = "file://" + currentImagefile;
		Uri outputFileUri = Uri.parse(uristring);
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(cameraIntent, 1);
	}
	
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		switch (requestCode) {
		//压缩图片后保存
		case 1:
			Log.d("onActivityResult", "######picPath:"+currentImagefile);
			if (Bimp.tempSelectBitmap.size() < 15 && resultCode == -1) {
				
				//String uristring = "file://" + currentImagefile;
//				String uristring = "file://" + dir+"111.jpg";
//				Uri outputFileUri = Uri.parse(uristring);
				
				//Bitmap bm = BitmapFactory.decodeFile(currentImagefile);
				Bitmap bm=resizePho();
				if(bm==null)
				{
					Toast.makeText(currentActivity, "图片加载错误,请重试\r\n"+currentImagefile, Toast.LENGTH_SHORT)
					.show();
					return;
				}
				
				 ByteArrayOutputStream baos = new ByteArrayOutputStream();
				 bm.compress(Bitmap.CompressFormat.JPEG, 100,
				 baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
				 int options = 90;
				 int i=0;
				 while ( baos.toByteArray().length / 1024>260) {
				 //循环判断如果压缩后图片是否大于100kb,大于继续压缩
				 baos.reset();//重置baos即清空baos
				 bm.compress(Bitmap.CompressFormat.JPEG, options,
				 baos);//这里压缩options%，把压缩后的数据存放到baos中
				 if(options>30)
					 options=options-10;
				 
					 
				 
				i++;
				 }
				 File picFile = new File(currentImagefile);
				 FileOutputStream out = null;
				try {
					out = new FileOutputStream(picFile);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				 try {
					 if(out==null)
					 {
						 
						 Toast.makeText(currentActivity, "图片获取错误,请重试\r\n"+currentImagefile, Toast.LENGTH_SHORT)
							.show();
					 }else
					baos.writeTo(out);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				
//old
//				Bitmap bm = BitmapFactory.decodeFile(currentImagefile);
				
				
				
				

				
//			old	
	/*			FileOutputStream out = null;
				try {
					
					File picFile = new File(currentImagefile);
					if(picFile.exists()){
						out = new FileOutputStream(picFile);
						bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
						out.flush();
					}else{
						Toast.makeText(currentActivity, "图片获取错误,请重试\r\n"+currentImagefile, Toast.LENGTH_SHORT)
						.show();
					}
				} catch (Exception e) {
					Log.e("image compress", e.getMessage(), e);
					Toast.makeText(currentActivity, "图片压缩错误,请重试", Toast.LENGTH_SHORT)
					.show();
					return;
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							Log.e("image compress", e.getMessage(), e);
							return;
						}
					}
				}*/
				TParkInfo_ImgEntity tempimage = new TParkInfo_ImgEntity();
				tempimage.imgUrlHeader = "";
				tempimage.imgUrlPath = currentImagefile;
				parkimaglist.add(tempimage);
				
			}
			break;
		}
	}
	
//	 public void saveBitmap() {
//		  
//		 Bitmap bm =resizePho();
//		  File f = new File(currentImagefile);
//		  if (f.exists()) {
//		   f.delete();
//		  }
//		  try {
//		   FileOutputStream out = new FileOutputStream(f);
//		   bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
//		   out.flush();
//		   out.close();
//		  
//		  } catch (FileNotFoundException e) {
//		   // TODO Auto-generated catch block
//		   e.printStackTrace();
//		   Toast.makeText(currentActivity, "图片获取错误,请重试\r\n"+currentImagefile, Toast.LENGTH_SHORT)
//			.show();
//		  } catch (IOException e) {
//			  Toast.makeText(currentActivity, "图片获取错误,请重试\r\n"+currentImagefile, Toast.LENGTH_SHORT)
//				.show();
//		   // TODO Auto-generated catch block
//		   e.printStackTrace();
//		  }
//
//		 }

	private Bitmap resizePho() {
		// TODO Auto-generated method stub
		
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeFile(currentImagefile, options);
		double ratio=Math.max((options.outWidth/1024f), (options.outHeight/1024f));
		ratio=ratio*2;
		
		options.inSampleSize=(int)Math.ceil(ratio);		
		
		options.inJustDecodeBounds=false;
		Bitmap bt=BitmapFactory.decodeFile(currentImagefile, options);
	
		return bt;
		
		
	}

	private DBHelper dbHelper = null;

	public DBHelper getHelper() {
		if (dbHelper == null) {
			dbHelper = OpenHelperManager.getHelper(currentActivity,
					DBHelper.class);
		}
		return dbHelper;
	}

	// public void setimagdata() {
	// try {
	// Dao<TParkInfo_ImgEntity, Integer> dao =getHelper()
	// .getParkdetail_imagDao();
	//
	// for( TParkInfo_ImgEntity tempdata : parkimaglist)
	// {
	//
	// dao.createIfNotExists(tempdata);
	//
	// }
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

	public void setdate() {
		Bimp.tempTParkImageList = parkimaglist;

	}

	private void Binddata() {
		if (Bimp.tempTParkImageList != null) {
			parkimaglist = Bimp.tempTParkImageList;

			adapter.notifyDataSetChanged();

		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
		// Binddata();
		adapter.notifyDataSetChanged();
	}

}
