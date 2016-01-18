package com.mc.parking.client.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.admin.upload.MultiPartStack;
import com.mc.parking.admin.upload.MultiPartStringRequest;
import com.mc.parking.client.Constants;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkInfo_ImgEntity;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.layout.ActionBaseActivity;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.admin.AddParkInfoDetailActivity;
import com.mc.parking.client.ui.admin.AdminGetParkInfoActivity;

public class CroppictureActivity extends ActionBaseActivity{


	  private int mGuidelines = 1;
	  Button button;
	  com.mc.parking.client.layout.TouchImageView img;
	  String path;
	  ImageView image;
	  
	  private static RequestQueue mSingleQueue;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*		setContentView(R.layout.ac_crop_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_topbar);
		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		titleView.setText(R.string.accountitle);	*/
        
        setContentView(R.layout.ac_cutimageview);
        mSingleQueue = Volley.newRequestQueue(this, new MultiPartStack());
         path=  getIntent().getStringExtra("path");
         image=(ImageView) findViewById(R.id.image);
//       img = new com.mc.parking.client.layout.TouchImageView(this,path);
//      LinearLayout view=(LinearLayout)findViewById(R.id.myview);
//      img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
//      view.addView(img);
         image.setImageURI(Uri.parse(path));
      button=(Button) findViewById(R.id.save);
      button.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			 Intent intent = new Intent();  
             
             intent.setAction("com.android.camera.action.CROP");  
             intent.setDataAndType(Uri.parse(path), "image/*");// mUri是已经选择的图片Uri  
             intent.putExtra("crop", "true");  
             intent.putExtra("aspectX", 1);// 裁剪框比例  
             intent.putExtra("aspectY", 1);  
             intent.putExtra("outputX", 150);// 输出图片大小  
             intent.putExtra("outputY", 150);  
             intent.putExtra("return-data", true);  
            
          startActivityForResult(intent, 200);  
			
		}
	});
      
      
//      mCropOverlayView = (CropOverlayView) v.findViewById(R.id.CropOverlayView);
//      mCropOverlayView.setInitialAttributeValues(mGuidelines, mFixAspectRatio, mAspectRatioX, mAspectRatioY);
       // setContentView(img);
    }
    @Override
    protected void onActivityResult(int arg0, int arg1, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(arg0, arg1, data);
    	 
             // 拿到剪切数据  
             Bitmap bmap = data.getParcelableExtra("data");  
             if(bmap==null)
               {
            	 return;
               }
             final String dir = Environment.getExternalStorageDirectory() + "/cbl_image/";
             String currentImagefile = dir + String.valueOf(System.currentTimeMillis()) + ".jpg";
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             bmap.compress(Bitmap.CompressFormat.JPEG, 100,
			 baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
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
					 
					 Toast.makeText(getApplicationContext(), "图片获取错误,请重试\r\n"+currentImagefile, Toast.LENGTH_SHORT)
						.show();
				 }else
				baos.writeTo(out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
             String uri = Constants.HTTP + "/a/parkinfo/savepic";
             image.setImageBitmap(bmap);
             Map<String, String> params = new HashMap<String, String>();
     		// params.put("token", "DJrlPbpJQs21rv1lP41yiA==");

             Map<String, File> files = new HashMap<String, File>();
             files.put("1", new File(
            		 currentImagefile));
     		String uri1 = Constants.HTTP + "/a/image/uploadtouxiang";
     		addPutUploadFileRequest(uri1, files, params, mResonseListenerString,
     				mErrorListener, null);
         

    	
    	
    }
    
    
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

		Log.i("sddad", " volley put : uploadFile " + url);

		mSingleQueue.add(multiPartRequest);
	}
    
    
    Listener<String> mResonseListenerString = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			Log.i("aa", " on imageload String" + response);
			
			if(response==null||response.length()<=0){
				Toast.makeText(CroppictureActivity.this,
						"获取返回的图片路径为空",Toast.LENGTH_SHORT).show();
				
				return;
			}
			

		}
	};
	
	ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			if (error != null) {
				if (error.networkResponse != null)
					Log.e("TOUXIANG", " error "
							+ new String(error.networkResponse.data));
			}
		}
	};
}
