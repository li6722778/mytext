package com.mc.parking.client.ui.fragment;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.mc.parking.client.Constants;
import com.mc.park.client.R;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.utils.SessionUtils;

public abstract class ParklistDialogFragment extends DialogFragment {

	String[] parkname;
	List<TParkInfoEntity> parkList;
	public  int postion=0;
	public static int mark;
	

	public  abstract void setchoice(TParkInfoEntity parkInfoEntity);
		
	

	public ParklistDialogFragment(List<TParkInfoEntity> parkList) {
		this.parkList = parkList;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		//View view = inflater.inflate(R.layout.fragment_parklistdialog, null);	
		final String[] parkname =new String[parkList.size()];;
		if(parkList!=null){
		for (int i = 0; i < parkname.length; i++) {
			parkname[i] = parkList.get(i).parkname;
		}
		}
		
		builder.setTitle(getResources().getString(R.string.admin_park_selection));
		
		builder.setSingleChoiceItems(parkname, 0,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								postion = which;
								
							}
						})
				// Add action buttons
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Toast.makeText(getActivity(),"选择的停车场为:" + parkname[postion],Toast.LENGTH_SHORT).show();
						setchoice(parkList.get(postion));
					}
				}).setNegativeButton("取消", new  DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (SessionUtils.loginUser.userType >= Constants.USER_TYPE_PADMIN
								&& SessionUtils.loginUser.userType < Constants.USER_TYPE_PADMIN + 10) {
							
							SessionUtils.cleanParkinfo(getActivity());
						}
						SessionUtils.loginUser =null;
						SessionUtils.cleanuserinfo(getActivity());
					}
				});
		return builder.create();
	}

}
