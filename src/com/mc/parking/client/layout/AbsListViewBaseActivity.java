package com.mc.parking.client.layout;

import android.os.Bundle;
import android.widget.AbsListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class AbsListViewBaseActivity extends ActionBaseActivity {  
	  
    protected static final String STATE_PAUSE_ON_SCROLL = "STATE_PAUSE_ON_SCROLL";  
    protected static final String STATE_PAUSE_ON_FLING = "STATE_PAUSE_ON_FLING";  
  
    protected AbsListView listView;  
    protected ImageLoader imageLoader = ImageLoader.getInstance(); 
    protected boolean pauseOnScroll = false;  
    protected boolean pauseOnFling = true;  
  
    @Override  
    public void onRestoreInstanceState(Bundle savedInstanceState) {  
        pauseOnScroll = savedInstanceState.getBoolean(STATE_PAUSE_ON_SCROLL, false);  
        pauseOnFling = savedInstanceState.getBoolean(STATE_PAUSE_ON_FLING, true);  
    }  
  
    @Override  
    public void onResume() {  
        super.onResume();  
        applyScrollListener();  
    }  
  
    private void applyScrollListener() {  
        listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling));  
    }  
  
    @Override  
    public void onSaveInstanceState(Bundle outState) {  
        outState.putBoolean(STATE_PAUSE_ON_SCROLL, pauseOnScroll);  
        outState.putBoolean(STATE_PAUSE_ON_FLING, pauseOnFling);  
    }  
  
}  