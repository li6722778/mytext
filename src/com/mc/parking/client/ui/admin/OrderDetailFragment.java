package com.mc.parking.client.ui.admin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mc.parking.client.Constants;
import com.mc.park.client.R;
import com.mc.parking.client.entity.OrderEntity;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;

public class OrderDetailFragment extends Fragment {
	
	private OrderEntity record;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_admin_order_detail, container, false);
		
		
		TextView admin_order_detail_ordernumber = (TextView)view.findViewById(R.id.admin_order_detail_ordernumber);
		TextView admin_order_detail_from = (TextView)view.findViewById(R.id.admin_order_detail_from);
		TextView admin_order_detail_orderdate = (TextView)view.findViewById(R.id.admin_order_detail_orderdate);
		TextView admin_order_detail_startdate = (TextView)view.findViewById(R.id.admin_order_detail_startdate);
		TextView admin_order_detail_enddate = (TextView)view.findViewById(R.id.admin_order_detail_enddate);
		
		if(record!=null){
			admin_order_detail_ordernumber.setText(record.getOrderId()+"");
			admin_order_detail_from.setText(record.getUserInfo()==null?"δ֪":record.getUserInfo().userPhone+"");
			admin_order_detail_orderdate.setText(UIUtils.formatDate(getActivity(),record.getOrderDate().getTime()));
			admin_order_detail_startdate.setText(record.getStartDate()!=null?UIUtils.formatDate(getActivity(),record.getStartDate().getTime()):"");
			admin_order_detail_enddate.setText(record.getEndDate()!=null?UIUtils.formatDate(getActivity(),record.getEndDate().getTime()):"");
		}
		
		Button admin_order_detail_back = (Button)view.findViewById(R.id.admin_order_detail_back);

		admin_order_detail_back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
			
		});
		
		
		
		return view;
	}
	
	public void setEntity(OrderEntity record){
		this.record = record;
	}
}