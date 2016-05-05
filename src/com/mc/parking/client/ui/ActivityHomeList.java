package com.mc.parking.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mc.park.client.R;
import com.mc.parking.client.entity.ChebolePayOptions;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.entity.TParkInfo_Product;
import com.mc.parking.client.ui.fragment.TakecashhistoryFragment.PullToRefreshListViewAdapter.ViewHolder;
import com.mc.parking.client.utils.UIUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.R.integer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
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
	@ViewInject(R.id.cart)
	private ImageView cart;
	@ViewInject(R.id.gotopay)
	private TextView gotopay;
	@ViewInject(R.id.cart_price)
	private TextView cartprice;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	Myadapter myadaper;
	typeAdapter typeadapter;
	TParkInfo_LocEntity tParkInfo_LocEntity;
	double totalprice;
	List<TParkInfo_Product> tpdata;
	List<String> typedata;
	HashMap<String, List<TParkInfo_Product>> map=new HashMap<String, List<TParkInfo_Product>>();
	HashMap<TParkInfo_Product,Integer> hasselect=new HashMap<TParkInfo_Product, Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_activity_home_list1);
		typedata=new ArrayList<String>();
		tParkInfo_LocEntity = (TParkInfo_LocEntity) getIntent()
				.getSerializableExtra("parkinfo");
		mylist = (ListView) findViewById(R.id.id_productlist);
		list_type=(ListView) findViewById(R.id.list_type);
		
		List<TParkInfo_Product> myproduce=new ArrayList<TParkInfo_Product>();
		  
			   TParkInfo_Product mm=new TParkInfo_Product();
			   mm.ProductName="金龙鱼大米";
			   mm.imgUrlHeader="drawable://" + R.drawable.product2;
			   mm.currentMoney=53;
			   myproduce.add(mm);
			   
			   
			    mm=new TParkInfo_Product();
			   mm.ProductName="五常稻花香";
			   mm.imgUrlHeader="drawable://" + R.drawable.product4;
			   mm.currentMoney=53;
			   myproduce.add(mm);
			   
		   
			    mm=new TParkInfo_Product();
				   mm.ProductName="汉中大米";
				   mm.imgUrlHeader="drawable://" + R.drawable.product5;
				   mm.currentMoney=53;
				   myproduce.add(mm);
			   
				   
				    mm=new TParkInfo_Product();
					   mm.ProductName="珍珠米";
					   mm.imgUrlHeader="drawable://" + R.drawable.product6;
					   mm.currentMoney=53;
					   myproduce.add(mm);
			   
					    mm=new TParkInfo_Product();
						   mm.ProductName="僡玉圣金";
						   mm.imgUrlHeader="drawable://" + R.drawable.product7;
						   mm.currentMoney=53;
						   myproduce.add(mm);
			   
		
		   tParkInfo_LocEntity.parkInfo.produArray=myproduce;
		if (tParkInfo_LocEntity.parkInfo.produArray != null
				&& tParkInfo_LocEntity.parkInfo.produArray.size() > 0) {
			tpdata = tParkInfo_LocEntity.parkInfo.produArray;
			myadaper = new Myadapter() {
			};
			myadaper.setdata(tpdata);
			gettype();
			typeadapter=new typeAdapter(){};
			mylist.setAdapter(myadaper);
			list_type.setAdapter(typeadapter);
		}
		//gettype();
		
		ViewUtils.inject(this);
	}

	@OnClick({ R.id.detail_activity_home_list1_back,
			R.id.detail_activity_home_list1_fujin,
			R.id.detail_activity_home_list1_meishi,
			R.id.detail_activity_home_list1_zhineng,
			R.id.detail_activity_home_list1_shaixuan,
			R.id.gotopay
			})
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
		case R.id.gotopay:
			Toast.makeText(getApplicationContext(), "aaaaa", Toast.LENGTH_SHORT).show();
			ChebolePayOptions opp=new ChebolePayOptions();
			opp.allProducts=getpostdata();
			Intent intent = new Intent(getApplicationContext(), ProYuyueActivity.class);
			Bundle buidle = new Bundle();
			buidle.putSerializable("parkinfoLoc", tParkInfo_LocEntity);
			buidle.putSerializable("price", opp);
			intent.putExtras(buidle);
			startActivityForResult(intent, 1);
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

	
	private void setgopaybutton()
	{
		if(totalprice>100)
		{
			gotopay.setText("去结算");
			gotopay.setClickable(true);
			gotopay.setBackgroundResource(R.color.orange);
		}else
		{
			gotopay.setClickable(false);
			gotopay.setText("还差"+totalprice+"元");
			gotopay.setBackgroundResource(R.color.gray_white);
		}
	}
	
	public class Myadapter extends android.widget.BaseAdapter {
		List<TParkInfo_Product> mydata=new ArrayList<TParkInfo_Product>();
		
		public void setdata(List<TParkInfo_Product> tpdata){
			
			this.mydata=tpdata;
		}
		public class ViewHolder {
			public String id;
			public TextView productname;
			public TextView productprice;
			public TextView askDate,add,reduce,num;
			public TextView handleDate;
			public TextView overDate;
			public ImageView productimg, handleimage, overimage;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.mydata.size();
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			if (convertView == null) {
				ViewHolder viewHolder = new ViewHolder();
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater
						.inflate(R.layout.item_product_view, null);
				viewHolder.productimg = (ImageView) convertView
						.findViewById(R.id.productimg);
				viewHolder.productname = (TextView) convertView
						.findViewById(R.id.productname);
				viewHolder.productprice = (TextView) convertView
						.findViewById(R.id.productprice);
				viewHolder.add = (TextView) convertView
						.findViewById(R.id.add);
				viewHolder.reduce = (TextView) convertView
						.findViewById(R.id.reduce);
				viewHolder.num = (TextView) convertView
						.findViewById(R.id.num);
				convertView.setTag(viewHolder);
			}

			final ViewHolder	viewHolder = (ViewHolder) convertView.getTag();
			//增加数量同时保存选择的数量
			viewHolder.add.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(viewHolder.reduce.getVisibility()==View.INVISIBLE)
					{
						viewHolder.reduce.setVisibility(View.VISIBLE);
						viewHolder.num.setVisibility(View.VISIBLE);
						
					}
					
					Integer hasnum=hasselect.get(mydata.get(position));
					if(hasnum==null)
					{
						hasselect.put(mydata.get(position), 1);
					}else
						hasselect.put(mydata.get(position), hasnum+1);
					viewHolder.num.setText(""+(Integer.valueOf(viewHolder.num.getText().toString())+1));
					totalprice= UIUtils.decimalPrice(totalprice+mydata.get(position).currentMoney);
					cartprice.setText(""+totalprice);
					viewHolder.productname.setText(mydata.get(position).ProductName);
			
					
					setgopaybutton();
				}
			});
			//减少数量同时保存选择的数量
			viewHolder.reduce.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int numb=Integer.valueOf(viewHolder.num.getText().toString())-1;
					viewHolder.num.setText(""+numb);
					//删除记录下的数据
					Integer hasnum=hasselect.get(mydata.get(position));
				
						hasselect.put(mydata.get(position), hasnum-1);
						totalprice= UIUtils.decimalPrice(totalprice-mydata.get(position).currentMoney);
						if(totalprice==0.00)
						{
							cartprice.setText("购物车是空的");
						}else
							cartprice.setText(""+totalprice);
				     if(numb==0)
				     {
				    	 viewHolder.reduce.setVisibility(View.INVISIBLE);
							viewHolder.num.setVisibility(View.INVISIBLE);
				     }
				     setgopaybutton();
				}
			});
			
			//绑定当前数量
			Integer hasnum=hasselect.get(mydata.get(position));
			if(hasnum!=null&&hasnum!=0)
			{
				viewHolder.reduce.setVisibility(View.VISIBLE);
				viewHolder.num.setVisibility(View.VISIBLE);
				viewHolder.num.setText(""+hasnum);
			}
			
			viewHolder.productname.setText(mydata.get(position).ProductName);
			viewHolder.productprice.setText(""
					+ mydata.get(position).currentMoney);
			imageLoader.displayImage(
					mydata.get(position).imgUrlHeader
							,
					viewHolder.productimg);
			viewHolder.productname.setText(mydata.get(position).ProductName);
            
			return convertView;
		}

	}
	
	private void gettype()
	{
		if(tpdata!=null)
		{
			typedata.add("大米");
			typedata.add("油");
			
			/*if(typedata==null)
			{
				typedata=new ArrayList<String>();
			}
			typedata.clear();
			for(TParkInfo_Product item:tpdata)
			{
				if(typedata.contains(""+item.type))
				{
					
				}else
				{
					typedata.add(""+item.type);
				}
				 List<TParkInfo_Product> curritem=map.get(item.type);
				 if(curritem==null)
				 {
					 curritem=new ArrayList<TParkInfo_Product>();
				 }
				 curritem.add(item);
				 map.put(""+item.type, curritem);
				
			}*/
			
		}
		
	}
	
	
	
	public class typeAdapter extends android.widget.BaseAdapter{
		
		public class ViewHolder {
	
			public TextView type;
		

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(typedata==null)
				return 0;
			else
			return typedata.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = new ViewHolder();
			if(convertView==null)
			{
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater
						.inflate(R.layout.item_list_text, null);
				viewHolder.type=(TextView) convertView.findViewById(R.id.type);
				convertView.setTag(viewHolder);
			}
			
				viewHolder=(ViewHolder) convertView.getTag();
				
				
				
				viewHolder.type.setText(typedata.get(position).toString());
				
				viewHolder.type.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {/*
						// TODO Auto-generated method stub
						
						myadaper=new Myadapter();
						myadaper.setdata(map.get(typedata.get(position).toString()));
						mylist.setAdapter(myadaper);
					*/}
				});
			
			return convertView;
		}
		
		
	}

	private List<TParkInfo_Product> getpostdata()
	{
	       Iterator iter = hasselect.keySet().iterator();
		List<TParkInfo_Product> data=new ArrayList<TParkInfo_Product>();
		while(iter.hasNext())
		{
			TParkInfo_Product item=(TParkInfo_Product) iter.next();
			if(item!=null)
			{
			item.selectnum=hasselect.get(item);
			data.add(item);
			}
				
		}
		Toast.makeText(getApplicationContext(), ""+data.size(), Toast.LENGTH_SHORT).show();
		return data;
	}
}
