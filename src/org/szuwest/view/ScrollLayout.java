package org.szuwest.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class ScrollLayout extends ViewGroup {
	public static final int TOUCH_STATE_REST = 0;
	public static final int TOUCH_STATE_SCROLLING = 1;
	public static final String TAG = "MyViewPager";

	private Scroller mScroller;
	private int mCurScreen;
	private int mTouchSlop;
	private float mLastMotionX;
	private float mActionDownX;
	private int mTouchState = TOUCH_STATE_REST;
	
	private float mLastMotionY;
	
	private OnScrollPageChangeListener mOnScrollPageChangeListener;
	
	private boolean isScrollEnable = true;
	
	public boolean isScrollEnable() {
		return isScrollEnable;
	}

	public void setScrollEnable(boolean isScrollEnable) {
		this.isScrollEnable = isScrollEnable;
	}

	/**
	 * ����ʱ�����Сֵ
	 */
	private int mMinTime;
	
	public interface OnScrollPageChangeListener {
		public void OnScrollPageChanged(int curIndex);
	}
	
	public void setOnScrollPageChangeListener(OnScrollPageChangeListener listener) {
		mOnScrollPageChangeListener = listener;
	}

	public ScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScroller = new Scroller(context);
		mCurScreen = 0;
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		
//		mMinTime = Util.getScreenWidth(context);

		// ????
		setChildrenDrawingCacheEnabled(true);
		setChildrenDrawnWithCacheEnabled(true);
	}

	public ScrollLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int count = getChildCount();

		if (MeasureSpec.EXACTLY == MeasureSpec.getMode(widthMeasureSpec)) {
			for (int i = 0; i < count; i++) {
				getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
			}

			scrollTo(mCurScreen * width, 0);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		int childLeft = 0;
		final int childCount = getChildCount();

		for (int i = 0; i < childCount; i++) {
			final View childView = getChildAt(i);

			if (childView.getVisibility() != View.GONE) {
				final int childWidth = childView.getMeasuredWidth();
				childView.layout(childLeft, 0, childLeft + childWidth,
						childView.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	@Override
	public void computeScroll() {

		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		if(PageIndexActivity.sIsGridDrag) {
//			return false;
//		}
		
		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			if ((int) Math.abs(x - mLastMotionX) > mTouchSlop && Math.abs(y-mLastMotionY)<Math.abs(x-mLastMotionX))
				mTouchState = TOUCH_STATE_SCROLLING;
			break;
		case MotionEvent.ACTION_DOWN:
			mActionDownX = x;

			mLastMotionX = x;
			mLastMotionY = y;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(!isScrollEnable) return super.onTouchEvent(event);
		
		final int action = event.getAction();
		final float x = event.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}

			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaX = (int) (mLastMotionX - x);

			// ????
			mLastMotionX = x;
			// scrollBy(deltaX, 0);

			// diff
			if (deltaX < 0) {
				if (this.getScrollX() > 0) {
					scrollBy(Math.max(-this.getScrollX(), deltaX), 0);

				}
			} else if (deltaX > 0) {
				if (getChildCount() > 0) {
					final int availableToScroll = getChildAt(getChildCount() - 1).getRight() - this.getScrollX() - getWidth();
					if (availableToScroll > 0) {
						scrollBy(Math.min(availableToScroll, deltaX), 0);
					}
				}
			}

			break;
		case MotionEvent.ACTION_UP:

			if (x - mActionDownX > 4 * mTouchSlop && mCurScreen > 0) {
				snapToScreen(mCurScreen - 1);
			} else if (mActionDownX - x > 4 * mTouchSlop && mCurScreen < getChildCount() - 1) {
				snapToScreen(mCurScreen + 1);
			} else {
				if (getChildCount() > 0)
					snapToDestination();
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
		}

		return true;
	}

	public int getCurScreen() {
		return mCurScreen;
	}

	public void snapToDestination() {
		final int screenWidth = getWidth();
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	public boolean snapToScreen(int whichScreen) {
		boolean snap = false;
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));

		if (getScrollX() != (whichScreen * getWidth())) {

			final int delta = whichScreen * getWidth() - getScrollX();
			
			int minTime = Math.abs(delta)>mMinTime ? mMinTime : Math.abs(delta);
			
			mScroller.startScroll(getScrollX(), 0, delta, 0, minTime);

			mCurScreen = whichScreen;
			snap = true;
			invalidate();
		}
		
		if(mOnScrollPageChangeListener != null) {
			mOnScrollPageChangeListener.OnScrollPageChanged(whichScreen);
		}
		return snap;
	}
	
	public void snapToScreenRightNow(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));

		mCurScreen = whichScreen;

		invalidate();
		
		if(mOnScrollPageChangeListener != null) {
			mOnScrollPageChangeListener.OnScrollPageChanged(whichScreen);
		}
	}

}
