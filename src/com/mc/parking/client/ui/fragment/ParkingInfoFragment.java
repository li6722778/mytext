package com.mc.parking.client.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.mc.park.client.R;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.entity.TParkService;
import com.mc.parking.client.ui.ParkActivity;
import com.mc.parking.client.ui.YuyueActivity;
import com.mc.parking.client.utils.SessionUtils;

public class ParkingInfoFragment extends android.support.v4.app.Fragment {

	TextView detail,service_detail;
	ParkActivity parkActivity;
	private Button gotoPayButton;
	LinearLayout service_line;
	RelativeLayout service_lay;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_packdetail_info,
				container, false);

		parkActivity = (ParkActivity) getActivity();
		detail = (TextView) view.findViewById(R.id.parkinfo_detail);
		 service_line=(LinearLayout) view.findViewById(R.id.service_line);
		 service_lay=(RelativeLayout) view.findViewById(R.id.service_lay);
		
		RelativeLayout section_info = (RelativeLayout) view
				.findViewById(R.id.packdetail_info_s1);
		final TParkInfo_LocEntity tParkInfo_LocEntity = parkActivity
				.getParkInfo();
		 gotoPayButton = (Button) view.findViewById(R.id.gotoPayButton);

		if (tParkInfo_LocEntity.parkInfo!= null) {

			String detailString = tParkInfo_LocEntity.parkInfo.detail;
			detail.setText(detailString);
			if(tParkInfo_LocEntity.isOpen==1)
			{
				gotoPayButton.setVisibility(View.VISIBLE);
			}
			else if (tParkInfo_LocEntity.isOpen==0) {
				gotoPayButton.setVisibility(View.GONE);
			}
			
			//绑定服务信息
			if(tParkInfo_LocEntity.parkInfo.services!=null&&tParkInfo_LocEntity.parkInfo.services.size()>0)
			{
				service_lay.setVisibility(View.VISIBLE);
				
				addServiceDetail(view,tParkInfo_LocEntity.parkInfo.services);
			}else
			{ 
				service_lay.setVisibility(View.GONE);
			}
		}

	
		gotoPayButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!SessionUtils.isLogined()) {

					LoginDialogFragmentv4 dialog = new LoginDialogFragmentv4(
							parkActivity, tParkInfo_LocEntity);
					dialog.show(getFragmentManager(), "loginDialog");

				} else {// 已经登录过了
					Intent intent = new Intent(parkActivity,
							YuyueActivity.class);
					Bundle buidle = new Bundle();
					buidle.putSerializable("parkinfoLoc", tParkInfo_LocEntity);
					intent.putExtras(buidle);
					startActivityForResult(intent,0);
				}
			}
		});

		return view;
	}
	
	
	private void addServiceDetail(View view,List<TParkService> list)
	{
		

		int i=0;
		while(i<list.size())
		{
			TextView servicetext=new TextView(getActivity());
			servicetext.setText(list.get(i).serviceDetailForApp);
			servicetext.setTextSize(16);
			service_line.addView(servicetext);
			i++;
		}
		
	
	}

}