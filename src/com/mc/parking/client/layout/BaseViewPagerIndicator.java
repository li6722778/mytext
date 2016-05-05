package com.mc.parking.client.layout;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mc.park.client.R;

public class BaseViewPagerIndicator extends LinearLayout {
	private static final int COLOR_TEXT_NORMAL = Color.rgb(124, 124, 124);
	private static final int COLOR_INDICATOR_COLOR = Color.rgb(74, 144, 226);

	private String[] mTitles;
	private int mTabCount;
	private int mIndicatorColor = COLOR_INDICATOR_COLOR;
	private float mTranslationX;
	private Paint mPaint = new Paint();
	private int mTabWidth;
	private ViewPager viewPager;
	private int MyMode = 0;
	private int displayHighlightIndex=0;
	TextView numbtxt,firstnumbtxt;
	private int txtnumb = 0;
	HashMap<Integer, TextView> textMap;

	public BaseViewPagerIndicator(Context context) {
		this(context, null);
	}

	public BaseViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint.setColor(mIndicatorColor);
		mPaint.setStrokeWidth(9.0F);
		textMap = new HashMap<Integer, TextView>();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mTabCount > 0)
			mTabWidth = w / mTabCount;
	}

	public void setMyMode(int mode) {
		setMyMode(mode,1);
	}
	public void setMyMode(int mode,int displayHighlightIndex) {
		this.MyMode = mode;
		this.displayHighlightIndex = displayHighlightIndex;

	}

	public void setnumb(int numb) {

		this.txtnumb = numb;
		if (numbtxt != null)
			numbtxt.setText("" + numb);
		if(firstnumbtxt!=null){
			firstnumbtxt.setText("" + numb);
		}

	}

	public void setViewPage(ViewPager viewPager) {
		this.viewPager = viewPager;
	}

	public void setTitles(String[] titles) {
		mTitles = titles;
		mTabCount = titles.length;
		if (MyMode == 1) {
			generateTitleView_newmode();

		} else
			generateTitleView();

	}
	
	
	

	public void setIndicatorColor(int indicatorColor) {
		this.mIndicatorColor = indicatorColor;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		canvas.save();
		canvas.translate(mTranslationX, getHeight() - 2);
		canvas.drawLine(0, 0, mTabWidth, 0, mPaint);
		canvas.restore();
	}

	public void scroll(int position, float offset) {
		/**
		 * <pre>
		 *  0-1:position=0 ;1-0:postion=0;
		 * </pre>
		 */
		if (viewPager != null) {
			if (textMap != null && textMap.size() > 0) {
				for (Integer indexView : textMap.keySet()) {
					if (indexView == position) {
						TextView tv = textMap.get(position);
						if (tv != null) {
							tv.setTextColor(COLOR_INDICATOR_COLOR);
						}
					} else {
						TextView tv = textMap.get(indexView);
						if (tv != null) {
							tv.setTextColor(COLOR_TEXT_NORMAL);
						}
					}
				}
			}
		}
		mTranslationX = getWidth() / mTabCount * (position + offset);
	
		invalidate();
		
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}


	private void generateTitleView() {
		if (getChildCount() > 0)
			this.removeAllViews();
		int count = mTitles.length;

		setWeightSum(count);

		View view = LayoutInflater.from(getContext()).inflate(
				R.layout.item_viewpager_total, null);

		TextView tv = (TextView) view.findViewById(R.id.baseviewpage_tv1);
		RelativeLayout relay = (RelativeLayout) view.findViewById(R.id.indicator_relay);	
		RelativeLayout firstrelay = (RelativeLayout) view.findViewById(R.id.indicator_first);		
		TextView tv2 = (TextView) view.findViewById(R.id.baseviewpage_tv2);

		if (mTitles != null && mTitles.length > 0) {
			tv.setText(mTitles[0]);

			if (mTitles.length > 1)
				tv2.setText(mTitles[1]);
		}
		


		addView(view);
		
		// addView(relay);
		tv.setOnClickListener(new PageClickListener(0));
		relay.setOnClickListener(new PageClickListener(1));
		textMap.put(0, tv);
		textMap.put(1, tv2);
		tv.setTextColor(COLOR_INDICATOR_COLOR);
	}

	private void generateTitleView_newmode() {
		if (getChildCount() > 0)
			this.removeAllViews();
		int count = mTitles.length;

		setWeightSum(count);

		View view = LayoutInflater.from(getContext()).inflate(
				R.layout.item_viewpager_total, null);

		TextView tv = (TextView) view.findViewById(R.id.baseviewpage_tv1);
		RelativeLayout relay = (RelativeLayout) view.findViewById(R.id.indicator_relay);
		
		RelativeLayout firstrelay = (RelativeLayout) view.findViewById(R.id.indicator_first);
		numbtxt = (TextView) relay.findViewById(R.id.baseviewpage_numb);
		
		firstnumbtxt = (TextView) firstrelay.findViewById(R.id.baseviewpage_firstnumb);
		
		TextView tv2 = (TextView) view.findViewById(R.id.baseviewpage_tv2);

		if (mTitles != null && mTitles.length > 0) {
			tv.setText(mTitles[0]);

			if (mTitles.length > 1)
				tv2.setText(mTitles[1]);
		}
		
		if(displayHighlightIndex==0){
			firstnumbtxt.setVisibility(View.VISIBLE);
			numbtxt.setVisibility(View.GONE);
			firstnumbtxt.setText("" + txtnumb);
		}else{
			firstnumbtxt.setVisibility(View.GONE);
			numbtxt.setVisibility(View.VISIBLE);
			numbtxt.setText("" + txtnumb);
		}

		addView(view);
		
		// addView(relay);
		tv.setOnClickListener(new PageClickListener(0));
		relay.setOnClickListener(new PageClickListener(1));
		textMap.put(0, tv);
		textMap.put(1, tv2);
		tv.setTextColor(COLOR_INDICATOR_COLOR);

	}

	class PageClickListener implements OnClickListener {
		int index;

		public PageClickListener(int index) {
			this.index = index;
		}

		@Override
		public void onClick(View v) {

			// System.out.println("click me:"+index);
			//
			if (viewPager != null) {
				viewPager.setCurrentItem(index);
				if (textMap != null && textMap.size() > 0) {
					for (Integer indexView : textMap.keySet()) {
						if (indexView == index) {
							TextView tv = textMap.get(indexView);
							if (tv != null) {
								tv.setTextColor(COLOR_INDICATOR_COLOR);
							}
						} else {
							TextView tv = textMap.get(indexView);
							if (tv != null) {
								tv.setTextColor(COLOR_TEXT_NORMAL);
							}
						}
					}
				}
			}

		}
	}
}
