package com.mc.parking.client.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.R.integer;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.baidu.navi.location.u;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.admin.upload.MultiPartStack;
import com.mc.parking.admin.upload.MultiPartStringRequest;
import com.mc.parking.client.Constants;
import com.mc.parking.client.Constants.Extra;
import com.mc.park.client.R;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkInfo_ImgEntity;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.AbsListViewBaseActivity;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.admin.AddParkInfoDetailActivity;
import com.mc.parking.client.ui.admin.AddParkInfoHistoryActivity;
import com.mc.parking.client.ui.admin.AdminGetParkInfoActivity;
import com.mc.parking.client.utils.SessionUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class UploadPhotoActivity extends AbsListViewBaseActivity {

	String[] imageUrls; // 图片Url
	DisplayImageOptions options; // 显示图片的设置
	List<TParkInfo_ImgEntity> parkimglist = new ArrayList<TParkInfo_ImgEntity>();
	private static String currentImagefile = "";
	private static String TAG = "upload";
	private static RequestQueue mSingleQueue;
	Long currentparkid;
	private String imgpath;
	final String dir = Environment.getExternalStorageDirectory()
			+ "/cbl_image/";
	private TParkInfoEntity parkInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_park_photo);
		mSingleQueue = Volley.newRequestQueue(this, new MultiPartStack());
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_topbar);
		TextView titleView = (TextView) findViewById(R.id.topbar_title);
		titleView.setText(R.string.uploadphoto_titile);

		TextView update_text = (TextView) findViewById(R.id.update_text);
		parkInfo = (TParkInfoEntity) getIntent()
				.getSerializableExtra("parkinfo");

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.update_photo);
		if (SessionUtils.loginUser != null) {
			if (SessionUtils.loginUser.userType == Constants.USER_TYPE_MADMIN
					|| SessionUtils.loginUser.userType == Constants.USER_TYPE_PADMIN
					|| SessionUtils.loginUser.userType == Constants.USER_TYPE_PADMIN) {
				linearLayout.setVisibility(View.VISIBLE);
			}
		}

		imageUrls = new String[parkInfo.imgUrlArray.size()];
		for (int i = 0; i < imageUrls.length; i++) {
			TParkInfo_ImgEntity tParkInfo_ImgEntity = parkInfo.imgUrlArray
					.get(i);
			imageUrls[i] = tParkInfo_ImgEntity.imgUrlHeader
					+ tParkInfo_ImgEntity.imgUrlPath;
		}

		update_text.setText("全部图片("
				+ (imageUrls == null ? 0 : imageUrls.length + "张)"));
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_empty).cacheInMemory(true)
				.cacheOnDisc(false).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		listView = (GridView) findViewById(R.id.gridview);
		((GridView) listView).setAdapter(new ImageAdapter()); // 填充数据
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startImagePagerActivity(position);
			}
		});

	}

	public void opencamera(View v) {
		switch (v.getId()) {
		case R.id.update_photo:

			if (!Constants.NETFLAG) {
				Toast.makeText(getApplicationContext(), "网络连接失败，请检查网络",
						Toast.LENGTH_LONG).show();
				return;

			}
			currentImagefile = dir + String.valueOf(System.currentTimeMillis())
					+ ".jpg";
			String uristring = "file://" + currentImagefile;
			Uri outputFileUri = Uri.parse(uristring);
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			startActivityForResult(cameraIntent, 1);
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {

			if (data != null) {

				Bitmap bm = resizePho();
				if (bm == null) {
					Toast.makeText(UploadPhotoActivity.this,
							"图片加载错误,请重试\r\n" + currentImagefile,
							Toast.LENGTH_SHORT).show();
					return;
				}

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
				int options = 90;
				int i = 0;
				while (baos.toByteArray().length / 1024 > 100) {
					// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
					baos.reset();// 重置baos即清空baos
					bm.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
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
					if (out == null) {

						Toast.makeText(UploadPhotoActivity.this,
								"图片获取错误,请重试\r\n" + currentImagefile,
								Toast.LENGTH_SHORT).show();
					} else
						baos.writeTo(out);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				TParkInfo_ImgEntity tempimage = new TParkInfo_ImgEntity();
				tempimage.imgUrlPath = currentImagefile;
				parkimglist.add(tempimage);
				uploadImage();

			}
		}
	}

	private void uploadImage() {
		Map<String, File> files = new HashMap<String, File>();
		if (parkimglist != null) {
			files.put("" + 0, new File(parkimglist.get(0).imgUrlPath));

		}
		Map<String, String> params = new HashMap<String, String>();

		String url = Constants.HTTP + "/a/image/upload/"
				+ parkInfo.parkId;
		addPutUploadFileRequest(url, files, params, mResonseListener,
				mErrorListener, null);

	}

	private Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
			.create();;
	Listener<String> mResonseListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {

			List<String> imgArray = mGson.fromJson(response.toString(),
					new TypeToken<List<String>>() {
					}.getType());
			if (imgArray != null && imgArray.size() > 0) {
				Message message = new Message();
				message.what = 1;
				uploadhandler.sendMessage(message);
			}

			else {
				Message message = new Message();
				message.what = 0;
				uploadhandler.sendMessage(message);
			}

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

	private void startImagePagerActivity(int position) {
		Intent intent = new Intent(this, ImagePagerActivity.class);
		intent.putExtra(Extra.IMAGES, imageUrls);
		intent.putExtra(Extra.IMAGE_POSITION, position);
		startActivity(intent);
	}

	public class ImageAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ImageView imageView;
			if (convertView == null) {
				imageView = (ImageView) getLayoutInflater().inflate(
						R.layout.item_grid_image, parent, false);
			} else {
				imageView = (ImageView) convertView;
			}

			// 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
			imageLoader.displayImage(imageUrls[position], imageView, options);

			return imageView;
		}
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

		Log.i(TAG, " volley put : uploadFile " + url);

		mSingleQueue.add(multiPartRequest);
	}

	private Handler uploadhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// 关闭ProgressDialog
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}

			switch (msg.what) {
			case 0:
				Toast.makeText(UploadPhotoActivity.this, "上传图片失败",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:

				Toast.makeText(UploadPhotoActivity.this, "上传图片成功",
						Toast.LENGTH_SHORT).show();

				break;

			default:
				break;
			}

		}
	};

	private Bitmap resizePho() {
		// TODO Auto-generated method stub

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(currentImagefile, options);
		double ratio = Math.max((options.outWidth / 1024f),
				(options.outHeight / 1024f));
		ratio = ratio * 2;

		options.inSampleSize = (int) Math.ceil(ratio);

		options.inJustDecodeBounds = false;
		Bitmap bt = BitmapFactory.decodeFile(currentImagefile, options);

		return bt;

	}
}
