package com.mc.parking.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;


public class AbScrollView extends ScrollView {
	private View inner; // 孩子View
	private static final int DEFAULT_POSITION = -1;
	private float y = DEFAULT_POSITION;// 点击时y的坐�?
	private Rect normal = new Rect();

	// 滑动距离及坐�?
	private float xDistance, yDistance, xLast, yLast;

	public AbScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 根据XML生成视图工作完成，该函数在生成视图的�?后调用，在所有子视图添加完成之后�? 即使子类覆盖了onFinishInflate
	 * 方法，也应该调用父类的方法， 使得该方法得以执�?
	 */
	@Override
	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			inner = getChildAt(0);

		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (inner == null) {
			return super.onTouchEvent(ev);
		} else {
			commOnTouchEvent(ev);
		}

		return super.onTouchEvent(ev);
	}

	public void commOnTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			y = ev.getY();
			break;
		case MotionEvent.ACTION_UP:

			if (isNeedAnimation()) {
				animation();
			}

			y = DEFAULT_POSITION;
			break;

		/**
		 * 排除第一次移动计算，因为第一次无法得知y左边，在MotionEvent.ACTION_DOWN中获取不到，
		 * 因为此时是MyScrollView的Tocuh时间传�?�到了ListView的孩子item上面。所以从第二次开始计�?
		 * 然�?�我们也要进行初始化，就是第�?次移动的时�?�让滑动距离归零，之后记录准确了就正常执�?
		 */
		case MotionEvent.ACTION_MOVE:
			float preY = y;
			float nowY = ev.getY();
			if (isDefaultPosition(y)) {
				preY = nowY;
			}
			int deltaY = (int) (preY - nowY);
			scrollBy(0, deltaY);
			y = nowY;
			// 当滚动到�?上或者最下时就不会再滚动，这时移动布�?
			if (isNeedMove()) {
				if (normal.isEmpty()) {
					// 保存正常的布�?位置
					normal.set(inner.getLeft(), inner.getTop(),
							inner.getRight(), inner.getBottom());

				}
				// 移动布局
				inner.layout(inner.getLeft(), inner.getTop() - deltaY,
						inner.getRight(), inner.getBottom() - deltaY);
			}
			break;

		default:
			break;
		}
	}

	// �?启动画移�?

	public void animation() {
		// �?启移动动�?
		TranslateAnimation ta = new TranslateAnimation(0, 0, inner.getTop(),
				normal.top);
		ta.setDuration(200);
		inner.startAnimation(ta);
		// 设置回到正常的布�?位置
		inner.layout(normal.left, normal.top, normal.right, normal.bottom);

		normal.setEmpty();

	}

	// 是否�?要开启动�?
	public boolean isNeedAnimation() {
		return !normal.isEmpty();
	}

	// 是否�?要移动布�?
	public boolean isNeedMove() {

		int offset = inner.getMeasuredHeight() - getHeight();
		int scrollY = getScrollY();
		if (scrollY == 0 || scrollY == offset) {
			return true;
		}
		return false;
	}

	// �?查是否处于默认位�?
	private boolean isDefaultPosition(float position) {
		return position == DEFAULT_POSITION;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();

			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;

			if (xDistance > yDistance) {
				return false;
			}
		}

		return super.onInterceptTouchEvent(ev);
	}

}
