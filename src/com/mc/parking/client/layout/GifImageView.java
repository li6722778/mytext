package com.mc.parking.client.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import com.mc.park.client.R;

public class GifImageView extends View {
	private long movieStart;
	private Movie movie;

	// �˴�������д�ù��췽��
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
		// ��һ�β���
		if (movieStart == 0) {
			movieStart = curTime;
		}
		if (movie != null) {
			int duraction = movie.duration();
			int relTime = (int) ((curTime - movieStart) % duraction);
			movie.setTime(relTime);
			movie.draw(canvas, 0, 0);
			// ǿ���ػ�
			invalidate();
		}
		super.onDraw(canvas);
	}
}
