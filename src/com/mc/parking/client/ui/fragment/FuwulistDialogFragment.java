package com.mc.parking.client.ui.fragment;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Request.Method;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.mc.addpic.utils.Bimp;
import com.mc.parking.client.Constants;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.ChebolePayOptions;
import com.mc.parking.client.entity.OrderEntity;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkService;
import com.mc.parking.client.entity.TuserInfo;
import com.mc.parking.client.layout.BaseDialogFragment;
import com.mc.parking.client.layout.PullToRefreshListView;
import com.mc.parking.client.layout.net.ComResponse;
import com.mc.parking.client.layout.net.HttpRequest;
import com.mc.parking.client.ui.YuyueActivity;
import com.mc.parking.client.ui.admin.AdminHomeActivity;
import com.mc.parking.client.ui.admin.OrderHisFragment.PullToRefreshListViewAdapter.ViewHolder;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;
import com.mc.parking.zxing.decoding.Intents.Encode;
import com.mc.wxutils.Util;

public abstract class FuwulistDialogFragment extends DialogFragment {

	String[] parkname;
	List<TParkService> servicelist;
	EditText editText;
	myListViewAdapter adapter;
	public int size = 0;
	List<TParkService> selects;
	ListView list;
	String carlisen;

	public abstract void setchoice(List<TParkService> servicelist);

	public FuwulistDialogFragment(List<TParkService> servicelist) {
		this.servicelist = servicelist;
		this.size = servicelist.size();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater factory = LayoutInflater.from(getActivity());// 提示框
		final View view = factory.inflate(R.layout.ac_serverdialog, null);// 这里必须是final的
		list = (ListView) view.findViewById(R.id.listview);
		LinearLayout ll_account = (LinearLayout) view.findViewById(R.id.ll_account);
		ll_account.requestFocus();

		adapter = new myListViewAdapter() {
		};
		selects = new ArrayList<TParkService>();

		adapter.getdata(servicelist);
		list.setAdapter(adapter);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		// View view = inflater.inflate(R.layout.fragment_parklistdialog, null);
		final String[] parkname = new String[size];

		builder.setTitle("请选择服务");

		builder.setView(view);

		// Add action buttons
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				String newcarlisen = "";
				try {
					newcarlisen = URLEncoder.encode(editText.getText().toString().trim(), "utf-8");

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (newcarlisen.toString().trim().equals("")) {
					// 控制不让关闭弹出框
					Field field;
					try {
						field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, false);
						Toast.makeText(getActivity(), "车牌号不能为空！", Toast.LENGTH_SHORT).show();
						return;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(getActivity(), "出错！请重新确认！", Toast.LENGTH_SHORT).show();
						return;
					}

				} else {
					// 允许关闭弹出框
					try {
						Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, true);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				if (!newcarlisen.equals(carlisen)) {

					HttpRequest<ComResponse<TuserInfo>> httpRequestAni = new HttpRequest<ComResponse<TuserInfo>>(
							Method.POST, SessionUtils.loginUser, "/a/user/changelisence?l=" + newcarlisen,
							new TypeToken<ComResponse<TuserInfo>>() {
					}.getType(), TuserInfo.class) {

						@Override
						public void onSuccess(ComResponse<TuserInfo> arg0) {
							if (arg0.getResponseStatus() == ComResponse.STATUS_OK) {
								setchoice(servicelist);
								SessionUtils.loginUser.email = editText.getText().toString().trim();

							} else if (arg0.getResponseStatus() == ComResponse.STATUS_FAIL)
								Toast.makeText(getActivity(), arg0.getErrorMessage(), Toast.LENGTH_SHORT).show();

						}

						@Override
						public void onFailed(String message) {
							Toast.makeText(getActivity(), "提交失败！", Toast.LENGTH_SHORT).show();
							return;
						}
					};

					httpRequestAni.execute();

				} else
					setchoice(servicelist);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Field field;
				try {
					
					
					for(int i=0;i<servicelist.size();i++)
					{
						servicelist.get(i).forceSelection=0;
						
					}
					field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, true);
					setchoice(servicelist);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		editText = (EditText) view.findViewById(R.id.editText);
		if (SessionUtils.loginUser != null) {
			carlisen = SessionUtils.loginUser.email;
			editText.setText(carlisen);
		}
		return builder.create();
	}

	public abstract class myListViewAdapter extends BaseAdapter {
		private List<TParkService> items = new ArrayList<TParkService>();;

		public class ViewHolder {

			public TextView orderName;
			public TextView detail;;
			public CheckBox select;
		}

		public void getdata(List<TParkService> mydata) {
			this.items = mydata;
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View rowView = convertView;

			LayoutInflater inflater = getActivity().getLayoutInflater();

			final ViewHolder viewHolder = new ViewHolder();

			if (convertView == null) {
				rowView = inflater.inflate(R.layout.item_list_service, null);

				viewHolder.orderName = (TextView) rowView.findViewById(R.id.service_string);
				viewHolder.detail = (TextView) rowView.findViewById(R.id.service_detail);

				viewHolder.select = (CheckBox) rowView.findViewById(R.id.select);
				rowView.setTag(viewHolder);
			}

			final ViewHolder holder = (ViewHolder) rowView.getTag();
			holder.orderName.setText(servicelist.get(position).serviceName);
			holder.detail.setText(servicelist.get(position).serviceDetailForApp);
			if (servicelist.get(position).forceSelection == 1)
				holder.select.setChecked(true);
			else
				holder.select.setChecked(false);
			holder.select.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (holder.select.isChecked()) {
						// selects.add();
						servicelist.get(position).forceSelection = 1;

					} else
						servicelist.get(position).forceSelection = 0;

				}
			});

			return rowView;
		}

	}
}
