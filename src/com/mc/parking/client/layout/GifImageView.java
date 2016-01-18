package com.mc.parking.client.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import com.mc.parking.client.R;

public class GifImageView extends View {
	private long movieStart;
	private Movie movie;

	// 此处必须重写该构造方法
	public GifImageView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		TypedArray a = context.obtainStyledAttributes(attributeSet,R.styleable.Gif); 
		int gitsrc = a.getInt(R.styleable.Gif_src,0); 
		movie = Movie.decodeStream(getResources().openRawResource(gitsrc));
		a.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		long curTime = android.os.SystemClock.uptimeMillis();
		// 第一次播放
		if (movieStart == 0) {
			movieStart = curTime;
		}
		if (movie != null) {
			int duraction = movie.duration();
			int relTime = (int) ((curTime - movieStart) % duraction);
			movie.setTime(relTime);
			movie.draw(canvas, 0, 0);
			// 强制重绘
			invalidate();
		}
		super.onDraw(canvas);
	}
}
