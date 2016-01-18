package com.mc.parking.client.ui;

import java.util.List;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mc.parking.client.R;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.entity.TParkInfo_Product;
import com.mc.parking.client.ui.fragment.TakecashhistoryFragment.PullToRefreshListViewAdapter.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityHomeList extends Activity {
	@ViewInject(R.id.detail_activity_home_list1_back)
	private ImageView btn_back;

	// 4个过滤条件的按钮
	@ViewInject(R.id.detail_activity_home_list1_fujin)
	private LinearLayout fujin;
	@ViewInject(R.id.detail_activity_home_list1_meishi)
	private LinearLayout meishi;
	@ViewInject(R.id.detail_activity_home_list1_zhineng)
	private LinearLayout zhineng;
	@ViewInject(R.id.detail_activity_home_list1_shaixuan)
	private LinearLayout shaixuan;

	private ListView mylist,list_type;
	// 对应的图片和布局显示
	@ViewInject(R.id.detail_activity_home_list1_filter1)
	private LinearLayout filter_1;
	@ViewInject(R.id.detail_activity_home_list1_0_arrow_fujin)
	private ImageView iv_1;
	@ViewInject(R.id.detail_activity_home_list1_0_arrow_meishi)
	private ImageView iv_2;
	@ViewInject(R.id.detail_activity_home_list1_0_arrow_zhineng)
	private ImageView iv_3;
	@ViewInject(R.id.detail_activity_home_list1_0_arrow_shaixuan)
	private ImageView iv_4;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	Myadapter myadaper;
	TParkInfo_LocEntity tParkInfo_LocEntity;
	List<TParkInfo_Product> tpdata;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_activity_home_list1);

		tParkInfo_LocEntity = (TParkInfo_LocEntity) getIntent()
				.getSerializableExtra("parkinfo");
		if (tParkInfo_LocEntity.parkInfo.produArray != null
				&& tParkInfo_LocEntity.parkInfo.produArray.size() > 0) {
			tpdata = tParkInfo_LocEntity.parkInfo.produArray;
			myadaper = new Myadapter() {
			};
		}

		mylist = (ListView) findViewById(R.id.id_productlist);
		mylist.setAdapter(myadaper);
		ViewUtils.inject(this);
	}

	@OnClick({ R.id.detail_activity_home_list1_back,
			R.id.detail_activity_home_list1_fujin,
			R.id.detail_activity_home_list1_meishi,
			R.id.detail_activity_home_list1_zhineng,
			R.id.detail_activity_home_list1_shaixuan })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.detail_activity_home_list1_back:
			finish();
			break;
		case R.id.detail_activity_home_list1_fujin:
			if (filter_1.getVisibility() == View.GONE) {
				filter_1.setVisibility(View.VISIBLE);// 对应的布局设置显示
				iv_1.setVisibility(View.VISIBLE);// 对应上面的小箭头显示
			} else { // 设置gone不显示
				filter_1.setVisibility(View.GONE);
				setAllImageArrowGone();
			}

			break;
		case R.id.detail_activity_home_list1_meishi:
			if (filter_1.getVisibility() == View.GONE) {
				filter_1.setVisibility(View.VISIBLE);// 对应的布局设置显示
				iv_2.setVisibility(View.VISIBLE);// 对应上面的小箭头显示
			} else { // 设置gone不显示
				filter_1.setVisibility(View.GONE);
				setAllImageArrowGone();
			}
			break;
		case R.id.detail_activity_home_list1_zhineng:
			if (filter_1.getVisibility() == View.GONE) {
				filter_1.setVisibility(View.VISIBLE);// 对应的布局设置显示
				iv_3.setVisibility(View.VISIBLE);// 对应上面的小箭头显示
			} else { // 设置gone不显示
				filter_1.setVisibility(View.GONE);
				setAllImageArrowGone();
			}
			break;
		case R.id.detail_activity_home_list1_shaixuan:
			if (filter_1.getVisibility() == View.GONE) {
				filter_1.setVisibility(View.VISIBLE);// 对应的布局设置显示
				iv_4.setVisibility(View.VISIBLE);// 对应上面的小箭头显示
			} else { // 设置gone不显示
				filter_1.setVisibility(View.GONE);
				setAllImageArrowGone();
			}
			break;

		default:
			break;
		}
	}

	private void setAllImageArrowGone() {
		iv_1.setVisibility(View.GONE);
		iv_2.setVisibility(View.GONE);
		iv_3.setVisibility(View.GONE);
		iv_4.setVisibility(View.GONE);
	}

	public class Myadapter extends android.widget.BaseAdapter {

		public class ViewHolder {
			public String id;
			public TextView productname;
			public TextView productprice;
			public TextView askDate;
			public TextView handleDate;
			public TextView overDate;
			public ImageView productimg, handleimage, overimage;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return tpdata.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = new ViewHolder();
			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater
						.inflate(R.layout.item_product_view, null);
				viewHolder.productimg = (ImageView) convertView
						.findViewById(R.id.productimg);
				viewHolder.productname = (TextView) convertView
						.findViewById(R.id.productname);
				viewHolder.productprice = (TextView) convertView
						.findViewById(R.id.productprice);
				convertView.setTag(viewHolder);
			}

			viewHolder = (ViewHolder) convertView.getTag();

			viewHolder.productname.setText(tpdata.get(position).ProductName);
			viewHolder.productprice.setText(""
					+ tpdata.get(position).currentMoney);
			imageLoader.displayImage(
					tpdata.get(position).imgUrlHeader
							+ tpdata.get(position).imgUrlPath,
					viewHolder.productimg);
			viewHolder.productname.setText(tpdata.get(position).ProductName);
            
			return convertView;
		}

	}

}
