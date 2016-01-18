package com.mc.parking.client.ui.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.admin.adrm.PhasedListener;
import com.mc.parking.admin.adrm.PhasedSeekBar;
import com.mc.parking.admin.adrm.SimplePhasedAdapter;
import com.mc.parking.client.Constants;
import com.mc.parking.client.PackingApplication;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkInfo_ImgEntity;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.HttpRequest;
import com.mc.parking.client.layout.net.HttpRequestAni;
import com.mc.parking.client.ui.ParkActivity;
import com.mc.parking.client.ui.UploadPhotoActivity;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.zxing.camera.MipcaActivityCapture;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CollectMainFragment extends Fragment {

	TextView collectBtn,collectHisBtn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.collect_main_fragment,
				container, false);
		
		collectBtn=(TextView) view.findViewById(R.id.collectBtn);
		collectHisBtn=(TextView) view.findViewById(R.id.collectHisBtn);
		
		collectBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(SessionUtils.loginUser!=null&&SessionUtils.loginUser.userType>=Constants.USER_TYPE_MADMIN&&SessionUtils.loginUser.userType<Constants.USER_TYPE_MADMIN+10)
				{
					
					Bimp.tempTParkInfo = new TParkInfoEntity();
					Bimp.tempTParkImageList.clear();
					Bimp.tempTParkLocList.clear();
					Bimp.ADDPARK_VIEW_MODE = AddParkInfoHistoryActivity.FROM_TYPE_NEW;
				Intent intent=new Intent();
				
				intent.setClass(getActivity(), AddParkInfoDetailActivity.class);
				startActivity(intent);
				}
				
			}
		});
		
		collectHisBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(SessionUtils.loginUser!=null&&SessionUtils.loginUser.userType>=Constants.USER_TYPE_MADMIN&&SessionUtils.loginUser.userType<Constants.USER_TYPE_MADMIN+10)
				{
				Intent intent=new Intent();
				intent.setClass(getActivity(), AddParkInfoHistoryActivity.class);
				startActivity(intent);
				}
				
			}
		});
		
		
		
		
		
		return view;
	}

	
	
	
}
