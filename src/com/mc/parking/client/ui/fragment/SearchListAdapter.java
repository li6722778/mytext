package com.mc.parking.client.ui.fragment;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.mc.parking.client.R;
import com.mc.parking.client.R.color;

public class SearchListAdapter  extends BaseAdapter {
	private List<PoiInfo>        _data;
	  private LayoutInflater      _inflater;
	  private String              _query;
	  private ForegroundColorSpan _querySpan;
	  
	  public SearchListAdapter(Context context, List<PoiInfo> data){
	    super();
	    _data = data;
	    _inflater = LayoutInflater.from(context);
	    _query = null;
	    _querySpan = new ForegroundColorSpan(Color.rgb(74, 144, 226));
	  }
	  
	  @Override
	  public int getCount() {
	    return _data==null?0:_data.size();
	  }

	  @Override
	  public Object getItem(int position) {
	    return _data.get(position);
	  }

	  @Override
	  public long getItemId(int position) {
	    return position;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    ViewHolder holder;
	    
	    if (convertView == null){
	      convertView = _inflater.inflate(R.layout.item_list_search, null);
	      holder = new ViewHolder();
	      holder.nameTextView = (TextView) convertView .findViewById(R.id.name_string);
	      convertView.setTag(holder);
	    }else{
	      holder = (ViewHolder) convertView.getTag();
	    }
	    
	    PoiInfo poiInfo = _data.get(position);
	    String name =poiInfo.name+","+poiInfo.address+","+poiInfo.city;
	    int index = TextUtils.isEmpty(_query) ? -1 : name.toLowerCase().indexOf(_query.toLowerCase());
	    if( index == -1 ){
	      holder.nameTextView.setText(name);
	    }else{
	      SpannableStringBuilder ssb = new SpannableStringBuilder(name);
	      ssb.setSpan(_querySpan, index, index + _query.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	      holder.nameTextView.setText(ssb);
	    }
//	    
	    return convertView;
	  }
	  
	  @Override
	  public void notifyDataSetChanged() {
	    super.notifyDataSetChanged();
	  }
	  
	  // Public methods
	  // -----------------------------------------------------------------------------------------
	  public void setData(List<PoiInfo> data){
	    _data = data;
	    notifyDataSetChanged();
	  }
	  
	  public String getQuery(){
	    return _query;
	  }
	  
	  public void setQuery(String query){
	    _query = query;
	  }
	  
	  // ViewHolder class
	  // -----------------------------------------------------------------------------------------
	  static class ViewHolder {
	    TextView nameTextView;
	  }
}
