package com.mc.parking.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ScrollView;


public class CustomScrollView extends ScrollView {

	private View inner;// 孩子View

	private float y;// 点击时y坐标

	private Rect normal = new Rect();// 矩形(这里只是个形式，只是用于判断是否�?要动�?.)

	private boolean isCount = false;// 是否�?始计�?

	private boolean isMoveing = false;// 是否�?始移�?.

	private ImageView imageView;

	private int initTop, initbottom;// 初始高度
	private int top, bottom;// 拖动时时高度�?

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	public CustomScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/***
	 * 根据 XML 生成视图工作完成.该函数在生成视图的最后调用，在所有子视图添加完之�?. 即使子类覆盖�? onFinishInflate
	 * 方法，也应该调用父类的方法，使该方法得以执行.
	 */
	@Override
	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			inner = getChildAt(0);
		}
	}

	/** touch 事件处理 **/
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (inner != null) {
			commOnTouchEvent(ev);
		}
		return super.onTouchEvent(ev);
	}

	/***
	 * 触摸事件
	 * 
	 * @param ev
	 */
	public void commOnTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			top = initTop = imageView.getTop();
			bottom = initbottom = imageView.getBottom();
			break;

		case MotionEvent.ACTION_UP:

			isMoveing = false;
			// 手指松开.
			if (isNeedAnimation()) {

				animation();

			}
			break;
		/***
		 * 排除出第�?次移动计算，因为第一次无法得知y坐标�? 在MotionEvent.ACTION_DOWN中获取不到，
		 * 因为此时是MyScrollView的touch事件传�?�到到了LIstView的孩子item上面.�?以从第二次计算开�?.
		 * 然�?�我们也要进行初始化，就是第�?次移动的时�?�让滑动距离�?0. 之后记录准确了就正常执行.
		 */
		case MotionEvent.ACTION_MOVE:

			final float preY = y;// 按下时的y坐标

			float nowY = ev.getY();// 时时y坐标
			int deltaY = (int) (nowY - preY);// 滑动距离
			if (!isCount) {
				deltaY = 0; // 在这里要�?0.
			}

			if (deltaY < 0 && top <= initTop)
				return;

			// 当滚动到�?上或者最下时就不会再滚动，这时移动布�?
			isNeedMove();

			if (isMoveing) {
				// 初始化头部矩�?
				if (normal.isEmpty()) {
					// 保存正常的布�?位置
					normal.set(inner.getLeft(), inner.getTop(),
							inner.getRight(), inner.getBottom());
				}

				// 移动布局
				inner.layout(inner.getLeft(), inner.getTop() + deltaY / 3,
						inner.getRight(), inner.getBottom() + deltaY / 3);

				top += (deltaY / 6);
				bottom += (deltaY / 6);
				imageView.layout(imageView.getLeft(), top,
						imageView.getRight(), bottom);
			}

			isCount = true;
			y = nowY;
			break;

		default:
			break;

		}
	}

	/***
	 * 回缩动画
	 */
	public void animation() {

		TranslateAnimation taa = new TranslateAnimation(0, 0, top + 200,
				initTop + 200);
		taa.setDuration(200);
		imageView.startAnimation(taa);
		imageView.layout(imageView.getLeft(), initTop, imageView.getRight(),
				initbottom);

		// �?启移动动�?
		TranslateAnimation ta = new TranslateAnimation(0, 0, inner.getTop(),
				normal.top);
		ta.setDuration(200);
		inner.startAnimation(ta);
		// 设置回到正常的布�?位置
		inner.layout(normal.left, normal.top, normal.right, normal.bottom);
		normal.setEmpty();

		isCount = false;
		y = 0;// 手指松开要归0.

	}

	// 是否�?要开启动�?
	public boolean isNeedAnimation() {
		return !normal.isEmpty();
	}

	/***
	 * 是否�?要移动布�? inner.getMeasuredHeight():获取的是控件的�?�高�?
	 * 
	 * getHeight()：获取的是屏幕的高度
	 * 
	 * @return
	 */
	public void isNeedMove() {
		int offset = inner.getMeasuredHeight() - getHeight();
		int scrollY = getScrollY();
		// Log.e("jj", "scrolly=" + scrollY);
		// 0是顶部，后面那个是底�?
		if (scrollY == 0 || scrollY == offset) {
			isMoveing = true;
		}
	}

}
