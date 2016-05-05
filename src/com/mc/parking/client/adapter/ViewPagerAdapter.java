package com.mc.parking.client.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;

import com.mc.park.client.R;
import com.mc.parking.client.ui.MainActivity;

public class ViewPagerAdapter extends PagerAdapter{

	private List<View> views;
	private Activity activity;
	private static final String SHAREDPREFERENCES_NAME = "first_pref";
	
	public ViewPagerAdapter(List<View> vs, Activity ac) {
		// TODO Auto-generated constructor stub
			this.views = vs;
			this.activity = ac;
	}
	
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(views != null)
		{
			return views.size();
		}
		return 0;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		// TODO Auto-generated method stub
		((ViewPager) container).removeView(views.get(position)); 
	}

	@Override
	public void finishUpdate(View container) {
		// TODO Auto-generated method stub
		super.finishUpdate(container);
	}

	@Override
	public Object instantiateItem(View container, int position) {
		// TODO Auto-generated method stub

		((ViewPager) container).addView(views.get(position), 0);  
		if(position == views.size()-1)
		{
			
			ImageButton go_home = (ImageButton) container.findViewById(R.id.go_home);
			go_home.setVisibility(View.VISIBLE);
			go_home.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					setGuide();
					geHome();
					
				}
			});
		}
		return views.get(position);
		
	}

	protected void geHome() {
		// TODO Auto-generated method
		Intent intent = new Intent(activity, MainActivity.class);  
        activity.startActivity(intent);  
        activity.finish();  
	}


	protected void setGuide() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = activity.getSharedPreferences(
				SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		
		Editor editor = preferences.edit();

		editor.putBoolean("isFirstIn", false);
		editor.commit();
	}

    @Override  
    public boolean isViewFromObject(View arg0, Object arg1) {  
        return (arg0 == arg1);  
    }  
  
    @Override  
    public void restoreState(Parcelable arg0, ClassLoader arg1) {  
    }  
  
    @Override  
    public Parcelable saveState() {  
        return null;  
    }  
  
    @Override  
    public void startUpdate(View arg0) {  
    } 

}
