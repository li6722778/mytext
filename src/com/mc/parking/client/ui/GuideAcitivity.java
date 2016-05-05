package com.mc.parking.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mc.park.client.R;
import com.mc.parking.client.adapter.ViewPagerAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GuideAcitivity extends Activity implements OnPageChangeListener{
	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;
	private LinearLayout ll;
	private ImageView[] dots;
	private int currentIndex;	
	private Map<Integer, View> viewMap = new HashMap<Integer, View>();
	private static final int images_slide[] = 
						{R.drawable.app_start1,
                 		R.drawable.app_start2,
                 		R.drawable.app_start3,
						}; 
	 @Override  
	    protected void onCreate(Bundle savedInstanceState) {  
	        super.onCreate(savedInstanceState); 
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.ac_guide_activity);  
	      
	        initViews();  
	  
	        initDots();  
	    }

	private void initDots() {
		// TODO Auto-generated method stub
		ll = (LinearLayout) findViewById(R.id.ll);
		dots = new ImageView[views.size()];
		for(int i = 0; i<images_slide.length;i++)
		{
			dots[i] = (ImageView)ll.getChildAt(i);
     
			dots[i].setEnabled(true);

			dots[i].setTag(i);
		}
		currentIndex = 0;
		dots[currentIndex].setEnabled(false);
				
	}
	
	private void setCurrentDot(int pos)
	{
		if(pos <0 || pos>views.size()-1 || currentIndex == pos)
		{
			return;
		}
		
		 dots[pos].setEnabled(false);
	     dots[currentIndex].setEnabled(true);
	     currentIndex = pos;
	}
	private void setCurrentView(int position)
     {
         if (position < 0 || position >= images_slide.length) {
             return;
         }

         vp.setCurrentItem(position);
     }

	private void initViews() {
		// TODO Auto-generated method stub

		views = new ArrayList<View>();	
		
		for(int i=0;i< images_slide.length;i++)
		{
			View view = getView(i,images_slide[i]);
			views.add(view);		
		}
		
		vpAdapter = new ViewPagerAdapter(views,this);
		
		vp = (ViewPager) findViewById(R.id.vPager);
		vp.setAdapter(vpAdapter);
		vp.setOnPageChangeListener(this);  
		
		
	}

	public View getView(int arg0,int image)
	{
		View rowView = this.viewMap.get(arg0);
		
		if (rowView == null) {
			
			LayoutInflater inflater = this.getLayoutInflater();
			rowView = inflater.inflate(R.layout.item_guide_list, null);
			ImageView image_item = (ImageView)rowView.findViewById(R.id.image);
			image_item.setBackgroundResource(image);

			viewMap.put(arg0, rowView);
		}
		return rowView;
	}
	

    public void onPageScrollStateChanged(int arg0) {  
    }  
  
    public void onPageScrolled(int arg0, float arg1, int arg2) {  
    }  
  
    public void onPageSelected(int arg0) {  
        setCurrentDot(arg0);  
    }

}
