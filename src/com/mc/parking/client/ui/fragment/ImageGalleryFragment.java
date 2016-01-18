package com.mc.parking.client.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.mc.parking.client.Constants.Extra;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.ui.ImagePagerActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressWarnings("deprecation")
public class ImageGalleryFragment extends Fragment{
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	String[] imageUrls;

	DisplayImageOptions options;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_image_gallery, container, false);
		final TParkInfo_LocEntity tParkInfo_LocEntity=(TParkInfo_LocEntity)getActivity().getIntent().getSerializableExtra("parkinfo");;//从当前Activity中获得
		imageUrls = new String [tParkInfo_LocEntity.parkInfo.imgUrlArray.size()];

		options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_stub)
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_empty)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();

		Gallery gallery = (Gallery) view.findViewById(R.id.gallery);
		
		gallery.setAdapter(new ImageGalleryAdapter());
		gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startImagePagerActivity(position);
			}
		});
		return view;
	}

	private void startImagePagerActivity(int position) {
		Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
		intent.putExtra(Extra.IMAGES, imageUrls);
		intent.putExtra(Extra.IMAGE_POSITION, position);
		startActivity(intent);
	}

	private class ImageGalleryAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = (ImageView) convertView;
			if (imageView == null) {
				imageView = (ImageView) getActivity().getLayoutInflater().inflate(R.layout.item_gallery_image, parent, false);
			}
			imageLoader.displayImage(imageUrls[position], imageView, options);
			return imageView;
		}
	}
}