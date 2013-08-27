package com.example.customlayoutmanager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.MotionEvent.PointerCoords;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyRelativeLayout extends RelativeLayout {

	private int mDx;
	private int mDy;
	
	private int mDrawableResize;
	private int mResizeHandleHeight;
	private int mResizeHandleWidth;
	
	private boolean mEditViewMode = false;
	private RelativeLayout mTempRelativeLayout;

	private View mSelectedView = null;
	private LayoutParams mSelectedViewLayout;
	private boolean mSelectFirstTime = false;
	
	//4 images used for handles in the selected view
	private View mTopHandle;
	private View mLeftHandle;
	private View mRightHandle;
	private View mBottomHandle;

	public MyRelativeLayout(Context context,int drawableResize) {
		super(context);
		mDrawableResize = drawableResize;
		//get the size of the used image to get positioning right
		Drawable d = getResources().getDrawable(mDrawableResize);
		mResizeHandleWidth = d.getIntrinsicWidth();
		mResizeHandleHeight= d.getIntrinsicHeight();	
		// TODO Auto-generated constructor stub
	}
	
	public void setLayoutEditable(boolean layoutIsEditable){
		mEditViewMode = true;		
	}

	public void addView(View v, int left, int top, int right, int bottom)
			throws IllegalArgumentException {
		if (top >= bottom)
			throw new IllegalArgumentException(
					"Top cannot be greater than or equal to bottom");
		if (right <= left)
			throw new IllegalArgumentException(
					"Left cannot be greater than or equal to right");
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				right - left, bottom - top);
		params.leftMargin = left;
		params.topMargin = top;
		v.setLayoutParams(params);
		this.addView(v);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		int pointerCount = ev.getPointerCount();
		if (pointerCount == 2 && mSelectedView != null) {
			Log.d("bert", "pointercount is " + pointerCount);

			PointerCoords p1 = new PointerCoords();
			PointerCoords p2 = new PointerCoords();

			ev.getPointerCoords(0, p1);
			ev.getPointerCoords(1, p2);

			return true;

		} else if (pointerCount == 1) {

			// movement gesture
			int action = ev.getActionMasked();

			int x = (int) ev.getX();
			int y = (int) ev.getY();
			View v = getTouchedView(x,y);
			Log.d("bert","touched view is " + v);
			
			switch (action) {
			case (MotionEvent.ACTION_DOWN):
				Log.d("bert","ACTION DOWN");
				
				//if you click on the upper relativelayout, unselect last selected
				if(v == null && mTempRelativeLayout != null){
					viewUnSelect(mTempRelativeLayout);
				}

				mSelectFirstTime = false;
				if (v != null && v!=mTempRelativeLayout) {

					if (mSelectedView != null) {
						viewUnSelect(mSelectedView);
					}
				    mSelectFirstTime = true;	
					
					mSelectedView = v;
					viewSelect(v);
					

					Log.d("bert", "touched" + v);
					
					
					// mPressed = true;
				}
			
				if (v != null) {
					mDx = v.getLeft() - x;
					mDy = v.getTop() - y;
				}
				
				
				return true;
				
			case (MotionEvent.ACTION_MOVE):
				Log.d("bert","ACTION MOVE");
				if(mSelectFirstTime == false && mTempRelativeLayout!=null){
					
					LayoutParams layout = (LayoutParams) mTempRelativeLayout.getLayoutParams();
					layout.leftMargin = x + mDx;
					layout.topMargin = y + mDy;
					
					//Start snapping part
					Log.d("bert","parent place right " + ((ViewGroup)mTempRelativeLayout.getParent()).getRight());
					
				
					if(mTempRelativeLayout.getTop() + mResizeHandleHeight/2 <=0){
						layout.topMargin = ((ViewGroup)mTempRelativeLayout.getParent()).getTop()-mResizeHandleHeight/2;
						mTopHandle.setVisibility(View.INVISIBLE);
					}
					if(mTempRelativeLayout.getLeft() + mResizeHandleWidth/2 <=0){
						layout.leftMargin = ((ViewGroup)mTempRelativeLayout.getParent()).getLeft() -mResizeHandleWidth/2;
						mLeftHandle.setVisibility(View.INVISIBLE);
					}
					Log.d("locking","right comp is " + (mTempRelativeLayout.getRight()) + " en parent right is " + ((ViewGroup)mTempRelativeLayout.getParent()).getRight());
					//Log.d("locking","top comp is " + (mTempRelativeLayout.getTop()) + " en parent top is " + ((ViewGroup)mTempRelativeLayout.getParent()).getTop());
					//Log.d("locking","bottom comp is " + (mTempRelativeLayout.getBottom()) + " en parent bottom is " + ((ViewGroup)mTempRelativeLayout.getParent()).getBottom());
					if(mTempRelativeLayout.getRight()>=((ViewGroup)mTempRelativeLayout.getParent()).getRight() +mResizeHandleWidth/2){
						//layout.rightMargin = 0;
						mRightHandle.setVisibility(View.INVISIBLE);
						layout.leftMargin = ((ViewGroup)mTempRelativeLayout.getParent()).getRight()-mTempRelativeLayout.getWidth() +mResizeHandleWidth/2;
					}
					if(mTempRelativeLayout.getBottom()>=((ViewGroup)mTempRelativeLayout.getParent()).getBottom()+mResizeHandleHeight/2){
						layout.topMargin = ((ViewGroup)mTempRelativeLayout.getParent()).getBottom()-mTempRelativeLayout.getHeight()+mResizeHandleHeight/2;
						mBottomHandle.setVisibility(View.INVISIBLE);
					}
					
					
					
					//Stop snapping part
					
					mTempRelativeLayout.setLayoutParams(layout);
					//Log.d("bert","moved and top is now" + v.getTop());
					
					
					this.requestLayout();
					this.invalidate();
					
				}
				return true;
			case (MotionEvent.ACTION_UP):
				Log.d("bert","ACTION UP");

				//mPressed = false;
				mDx = 0;
				mDy = 0;
				return true;
			case (MotionEvent.ACTION_CANCEL):
				// Log.d("bert", "Action was CANCEL");
				return true;
			case (MotionEvent.ACTION_OUTSIDE):
				// Log.d("bert", "Movement occurred outside bounds "
				// /+ "of current screen element");
				return true;

			case (MotionEvent.ACTION_POINTER_DOWN):
				// Log.d("bert",
				// "POINTER DOWN Action was POINTER DOWN + place is "
				// + ev.getX() + "," + ev.getY());
				return true;
			default:
				return super.onTouchEvent(ev);
			}

		}
		return super.onTouchEvent(ev);
		// return false;
	}
	
	/*
	 * This Method has to be overridden because we want to handle touch events
	 * ourselves in this class.
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return mEditViewMode;
	}

	/*
	 * Put the selected view in a relativelayout in order to get an overlay
	 * on top of the view with handles for moving and resizing the view
	 */
	private void viewSelect(View v) {
		// TODO Auto-generated method stub
		RelativeLayout newrel = new RelativeLayout(getContext());
		mTempRelativeLayout = newrel;
		removeView(v);
		RelativeLayout.LayoutParams l = (LayoutParams) v.getLayoutParams();
		//create a copy to save the inner view sizes until object deselected
		mSelectedViewLayout = new LayoutParams(l);
		RelativeLayout.LayoutParams innerViewLayout = new RelativeLayout.LayoutParams(l);
		
		int viewWidth = l.width;
		int viewHeight = l.height;
		
		
	
		
		//change layout params because we want the resize images centered on bound
		l.width = l.width + mResizeHandleWidth;
		l.height = l.height + mResizeHandleHeight;
		l.leftMargin -= mResizeHandleWidth/2;
		l.rightMargin -= mResizeHandleWidth/2;
		l.topMargin -= mResizeHandleHeight/2;
		l.bottomMargin -= mResizeHandleHeight/2;
	
		addView(newrel,l);
		
		//set alpha to show which view is in EDIT mode
		v.setAlpha(0.2f);
				
		
		
		//copy view into inner layout
		newrel.addView(v, innerViewLayout);
		
		
		//inner view which contains the real view
		innerViewLayout.leftMargin = mResizeHandleWidth/2;
		innerViewLayout.topMargin = mResizeHandleWidth/2;
		
		//add left Resize image
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp2.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		lp2.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
		ImageView bullet = new ImageView(getContext());
		bullet.setImageResource(R.drawable.bullet);
		newrel.addView(bullet, lp2);
		mLeftHandle = bullet;
		
		//add right Resize image
		lp2 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp2.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		lp2.leftMargin = viewWidth;
		bullet =  new ImageView(getContext());
		bullet.setImageResource(R.drawable.bullet);
		mRightHandle= bullet;
	
		newrel.addView(bullet, lp2);
		
		//add top Resize image
		lp2 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lp2.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		bullet = new ImageView(getContext());
		bullet.setImageResource(R.drawable.bullet);
		newrel.addView(bullet, lp2);
		mTopHandle = bullet;
		

		//add bottom Resize image
		lp2 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		bullet = new ImageView(getContext());
		bullet.setImageResource(R.drawable.bullet);
		lp2.topMargin = viewHeight;
		newrel.addView(bullet, lp2);
		mBottomHandle = bullet;
	
		requestLayout();
		invalidate();
		Log.d("bert","component " + v + " has been selected");

	}

	private void viewUnSelect(View v) {
		// TODO Auto-generated method stub
		
		//get the size of the used image to get positioning right
		Drawable d = getResources().getDrawable(mDrawableResize);
		int w = d.getIntrinsicWidth();
		int h = d.getIntrinsicHeight();
	
		View viewInside = mTempRelativeLayout.getChildAt(0);
		//RelativeLayout.LayoutParams layoutParams = (LayoutParams) rl.getLayoutParams();
		//viewInside.setLayoutParams(mSelectedViewLayout);
		v.setAlpha(1f);
		mTempRelativeLayout.removeAllViews();
		removeView(mTempRelativeLayout);
		mSelectedViewLayout.leftMargin = ((LayoutParams)mTempRelativeLayout.getLayoutParams()).leftMargin + w/2 ;
		mSelectedViewLayout.topMargin = ((LayoutParams)mTempRelativeLayout.getLayoutParams()).topMargin + h/2;
		addView(viewInside,mSelectedViewLayout);
		Log.d("bert","component " + viewInside + " was removed from the rellayout container");
		
		//put null in mTempRelativeLayout because 2x clicking upper relativealyout (this) otherwise crashes
		mTempRelativeLayout =null;
		mSelectedView = null;
		
		
		requestLayout();
		invalidate();
	}

	private View getTouchedView(int x, int y) {
		Log.d("point", "touched " + x + " , " + y);
		int nbChildren = getChildCount();

		for (int i = 0; i < nbChildren; i++) {
			View v = getChildAt(i);

			if (v.getLeft() <= x && x <= v.getRight())
				if (v.getTop() <= y && y <= v.getBottom()) {
					return v;
				}

		}
		return null;
	}

}
