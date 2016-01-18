package com.mc.parking.client.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class VerticalScrollView  extends ScrollView{
	
	private float xDistance, yDistance, lastX, lastY;

	public VerticalScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
public	VerticalScrollView(Context context, AttributeSet attrs)
	{
		super( context, attrs );
		
		
	}
	
	public VerticalScrollView(Context context, AttributeSet attrs, int defStyle) {

		super( context, attrs, defStyle );
		}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            xDistance = yDistance = 0f;
            lastX = ev.getX();
            lastY = ev.getY();
            break;
        case MotionEvent.ACTION_MOVE:
            final float curX = ev.getX();
            final float curY = ev.getY();
            xDistance += Math.abs(curX - lastX);
            yDistance += Math.abs(curY - lastY);
            lastX = curX;
            lastY = curY;
            if(xDistance > yDistance)
                return false;
    }

    return super.onInterceptTouchEvent(ev);
	}
	
	
	
	@Override
	public void setOnTouchListener(OnTouchListener l) {
		// TODO Auto-generated method stub
		
		
		
		
		super.setOnTouchListener(l);
	}

	
}
