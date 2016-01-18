package com.mc.parking.client.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.mc.parking.client.R;
import com.mc.parking.client.layout.ActionBaseActivity;
import com.mc.parking.client.layout.CircleImageView;
import com.mc.parking.client.layout.RoundImageView;

public class UseraccountAcitivity extends ActionBaseActivity implements OnClickListener{
	
	private LinearLayout personlayout;
	private ImageView touxiangview;
	private  static  final int CHOOSE_PICTURE=1;
	private  static  final int CROP_PICTURE=2;
	private static String currentImagefile = "";
	CircleImageView roundimage;
	final String dir = Environment.getExternalStorageDirectory() + "/cbl_image/";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_useraccount);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_topbar);
		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		titleView.setText(R.string.accountitle);	
		roundimage=(CircleImageView) findViewById(R.id.roundimage);
		initview();
	}

	private void initview() {
		personlayout =(LinearLayout) findViewById(R.id.personally);
		personlayout.setOnClickListener(this);
		touxiangview = (ImageView) findViewById(R.id.touxiang);
		touxiangview.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.personally:
			Intent personinfo = new Intent(UseraccountAcitivity.this,
					UserInfoActivity.class);
			startActivity(personinfo);
			break;
		case R.id.touxiang:
			new com.mc.parking.client.layout.ActionSheetDialog(UseraccountAcitivity.this)
			.builder()
			.setCancelable(false)
			.setCanceledOnTouchOutside(false)
			.addSheetItem("拍照", com.mc.parking.client.layout.ActionSheetDialog.SheetItemColor.Blue,
					new com.mc.parking.client.layout.ActionSheetDialog.OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {

						}
					})
			.addSheetItem("从相册选择", com.mc.parking.client.layout.ActionSheetDialog.SheetItemColor.Blue,
					new com.mc.parking.client.layout.ActionSheetDialog.OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							/*Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);  
		                    openAlbumIntent.setType("image/*");  
		                    startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);*/  
		                    
		                    Intent intent;
		                    intent = new Intent(
		                                        Intent.ACTION_PICK,
		                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		                    startActivityForResult(intent, CHOOSE_PICTURE);
//							Intent intent = new Intent();  
//			                intent.setType("image/*");  
//			                intent.setAction(Intent.ACTION_GET_CONTENT);  
//			                startActivityForResult(intent, 2);  

						}
					}).show();
	break;
			
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CHOOSE_PICTURE:
				//照片的原始资源地址
//				Uri originalUri = data.getData(); 
//	            Toast.makeText(UseraccountAcitivity.this, ""+originalUri, Toast.LENGTH_SHORT).show();
//	            Intent cropintent =new Intent(UseraccountAcitivity.this,CroppictureActivity.class);
//	            cropintent.putExtra("path", data.getData().toString());
//	            startActivityForResult(cropintent, CHOOSE_PICTURE);
//				break;
				 Intent intent = new Intent();  
	             
	             intent.setAction("com.android.camera.action.CROP");  
	             intent.setDataAndType(data.getData(), "image/*");// mUri是已经选择的图片Uri  
	             intent.putExtra("crop", "true");  
	             intent.putExtra("aspectX", 1);// 裁剪框比例  
	             intent.putExtra("aspectY", 1);  
	             intent.putExtra("outputX", 150);// 输出图片大小  
	             intent.putExtra("outputY", 150);  
	             intent.putExtra("return-data", true);  
	            
	          startActivityForResult(intent, 33);  
			break;
			case 33:
				 Bitmap bmap = data.getParcelableExtra("data");  
				// touxiangview.setImageResource(bmap);
				 //touxiangview.set
				 saveimage(bmap);
				 roundimage.setImageBitmap(bmap);
				
			default:
				break;
			}
		}
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
		
		public void  saveimage(Bitmap bm)
		{
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
			 currentImagefile = dir + String.valueOf(System.currentTimeMillis()) + ".jpg";
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
					 
					 Toast.makeText(this, "图片获取错误,请重试\r\n"+currentImagefile, Toast.LENGTH_SHORT)
						.show();
				 }else
				baos.writeTo(out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}

}
