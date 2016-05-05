package com.mc.parking.client.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.mc.park.client.R;
import com.mc.parking.client.utils.SessionUtils;

public class OffineMapMyCityFragment extends android.support.v4.app.Fragment {
	private MKOfflineMap mOfflineMap;
	TextView detail;
	private boolean downloading = false;
	ProgressBar progressBar;
	public OffineMapMyCityFragment(MKOfflineMap mOfflineMap) {
		this.mOfflineMap = mOfflineMap;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_offlinemap_mycity,
				container, false);

		TextView titleView = (TextView) view.findViewById(R.id.offine_city);
		if(SessionUtils.cityCode<=0){
			titleView.setText("");
		}else{
		   titleView.setText(SessionUtils.city);
		}

		detail = (TextView) view.findViewById(R.id.download_detail);
		Button download = (Button) view.findViewById(R.id.offineDownload);
	    progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		
		downloading = false;

		if(SessionUtils.cityCode==0){
			detail.setText("系统无法定位获取您的城市信息");
		}else{
			detail.setText("无离线地图包");
		}
		
		

		download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!downloading) {
					downloading = true;
					mOfflineMap.start(SessionUtils.cityCode);
					Toast.makeText(getActivity(), "开始下载离线地图",
							Toast.LENGTH_SHORT).show();
				} else {
					mOfflineMap.pause(SessionUtils.cityCode);
					Toast.makeText(getActivity(), "暂停下载离线地图",
							Toast.LENGTH_SHORT).show();
					downloading = false;
				}
			}
		});

		return view;
	}

	public void setDownloadMessage(String message,int ratio) {
		if (detail != null) {
			if(ratio>0&&ratio<100){
				progressBar.setProgress(ratio);
				progressBar.setVisibility(View.VISIBLE);
			}
			detail.setText(message);
			if(ratio>=100){
				downloading = false;
				progressBar.setVisibility(View.GONE);
			}
		}
	}

	public String formatDataSize(int size) {
		String ret = "";
		if (size < (1024 * 1024)) {
			ret = String.format("%dK", size / 1024);
		} else {
			ret = String.format("%.1fM", size / (1024 * 1024.0));
		}
		return ret;
	}

}