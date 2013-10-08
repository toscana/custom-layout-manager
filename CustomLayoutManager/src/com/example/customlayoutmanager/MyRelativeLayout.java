package com.example.customlayoutmanager;

import android.content.Context;
import android.graphics.Rect;
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

	private int mSnapDistance;
	private int mUnsnapDistance;
	
	private int mDx;
	private int mDy;
	
	private int mDrawableResize;
	private int mResizeHandleHeight;
	private int mResizeHandleWidth;
	
	private int mViewToResizeTop;
	private int mViewToResizeLeft;
	private int mViewToResizeRight;
	private int mViewToResizeBottom;
	

	private boolean mEditViewMode = false;
	private RelativeLayout mTempRelativeLayout;

	private View mSelectedView = null;
	private LayoutParams mSelectedInnerViewLayoutParams;

	private int mXSnapPosition;
	private int mYSnapPosition;

	// 4 images used for handles in the selected view
	private View mTopHandle;
	private View mLeftHandle;
	private View mRightHandle;
	private View mBottomHandle;

	// check if resizing
	private boolean mResizing;
	private View mSelectedResizeHandle;

	public MyRelativeLayout(Context context, int drawableResize) {
		super(context);
		mDrawableResize = drawableResize;
		// get the size of the used image to get positioning right
		Drawable d = getResources().getDrawable(mDrawableResize);
		mResizeHandleWidth = d.getIntrinsicWidth();
		mResizeHandleHeight = d.getIntrinsicHeight();
		// TODO Auto-generated constructor stub

		mResizing = false;
		mSnapDistance = 25;
		mUnsnapDistance = 40;
	}

	public void setLayoutEditable(boolean layoutIsEditable) {
		mEditViewMode = layoutIsEditable;
	}

	public void addView(View v, int left, int top, int right, int bottom) throws IllegalArgumentException {
		if (top >= bottom)
			throw new IllegalArgumentException("Top cannot be greater than or equal to bottom");
		if (right <= left)
			throw new IllegalArgumentException("Left cannot be greater than or equal to right");
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(right - left, bottom - top);
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
			View v = getTouchedView(x, y);
			// Log.d("bert", "touched point  is " + x + "," + y);

			switch (action) {
			case (MotionEvent.ACTION_DOWN):
				// Log.d("bert", "touched i" + v);
				if (v == null) {
					// if you click on the upper relativelayout, unselect last
					// selected
					// check if something was selected, if so remove handles
					if (mTempRelativeLayout != null)
						viewUnSelect(mTempRelativeLayout);
				} else if (v != mLeftHandle && v != mRightHandle && v != mTopHandle && v != mBottomHandle) {
					// user clicked on normal view inside relativelayout
				
					mDx = v.getLeft() - x - mResizeHandleWidth / 2;
					mDy = v.getTop() - y - mResizeHandleHeight / 2;

					// check if the view in focus has to change
					if (v != mTempRelativeLayout) {
						if (mSelectedView != null) {
							viewUnSelect(mSelectedView);
						}
						viewSelect(v);
					}
				}
				return true;

			case (MotionEvent.ACTION_MOVE):
				// Log.d("bert","move move move");
				if (mTempRelativeLayout != null) {

					if (mResizing) {
						// Log.d("bert", "handles pressed");
						resizeView(x, y, mSelectedResizeHandle);
						mResizing = true;

					} else if (v == mLeftHandle || v == mRightHandle || v == mTopHandle || v == mBottomHandle) {
						// Log.d("bert", "handles pressed");
						mViewToResizeBottom = mTempRelativeLayout.getBottom();
						mViewToResizeTop = mTempRelativeLayout.getTop();
						mViewToResizeRight = mTempRelativeLayout.getRight();
						mViewToResizeLeft = mTempRelativeLayout.getLeft();
						resizeView(x, y, v);
						mResizing = true;
						mSelectedResizeHandle = v;
					}
					// no resize action
					else {
						Log.d("bert", "MOVING");
						moveView(x, y);
					}

					this.requestLayout();
					this.invalidate();

				}
				return true;
			case (MotionEvent.ACTION_UP):
				Log.d("bert", "ACTION UP");
				mResizing = false;
			
				// mPressed = false;
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

	private void moveView(int x, int y) {
		LayoutParams layout = (LayoutParams) mTempRelativeLayout.getLayoutParams();
		layout.leftMargin = x + mDx;
		layout.topMargin = y + mDy;
		// requestLayout();
		/*
		 Log.d("bert","leftmargi" + layout.leftMargin); Log.d("bert","topmarg"
		 * + layout.topMargin);
		 */
		
		Log.d("bert","leftmargi" + layout.leftMargin);
		Log.d("bert","topmarg" + layout.topMargin);
		requestLayout();
		invalidate();
		snapOnMove(x, y, layout);
		
	

		// stop snapping part for all other elements on the
		// screen

		// mTempRelativeLayout.setLayoutParams(layout);
		// Log.d("bert","moved and top is now" + v.getTop());

	}

	private void snapOnMove(int x, int y, LayoutParams layout) {
		Log.d("bert","start of Move: LEFT " + layout.leftMargin + "TOP " + layout.topMargin);
		// Start snapping part in surrounding rectangle
		// (surrounding
		// relativelayout)
		if (layout.topMargin - mSnapDistance <= 0) {
			layout.topMargin = ((ViewGroup) mTempRelativeLayout.getParent()).getTop() - mResizeHandleHeight / 2;
			Log.d("bert","snap1");
		}
		
		if (layout.topMargin + layout.height >= ((ViewGroup) mTempRelativeLayout.getParent()).getBottom() - mSnapDistance){
			//int height = layout.height;
			layout.topMargin = ((ViewGroup) mTempRelativeLayout.getParent()).getBottom() - layout.height
					+ mResizeHandleHeight / 2;
			
			Log.d("bert","snap2");
			
			
		}

		if (layout.leftMargin - mSnapDistance <= 0){ 
			layout.leftMargin = ((ViewGroup) mTempRelativeLayout.getParent()).getLeft() - mResizeHandleWidth / 2;
			Log.d("bert","snap3");
		}
		
		if (layout.leftMargin+layout.width >= ((ViewGroup) mTempRelativeLayout.getParent()).getRight() - mSnapDistance){ 
			int width = layout.width;
			layout.leftMargin = ((ViewGroup) mTempRelativeLayout.getParent()).getRight() - width
					+ mResizeHandleWidth / 2;
			
			
			Log.d("bert","snap4");
		}
		
		// Stop snapping part for surrounding relativelayout

		// start snapping part for all other elements on the
		// screen
		View other;
		for (int i = 0; i < getChildCount(); i++) {
			other = getChildAt(i);
			// only snap if views are adjacent
			if (other != mTempRelativeLayout && viewsAdjacent(mTempRelativeLayout, other)) {
				if (Math.abs(layout.topMargin - other.getBottom()) <= mResizeHandleHeight / 2) {
					layout.topMargin = other.getBottom() - mResizeHandleHeight / 2;
					Log.d("bert","snap 5");
				}
				if (Math.abs(layout.topMargin + mTempRelativeLayout.getHeight() - other.getTop()) <= mSnapDistance) {
					layout.topMargin = other.getTop() - mTempRelativeLayout.getHeight() + mResizeHandleHeight / 2;
					Log.d("bert","snap 6");
				}
				if (Math.abs(layout.leftMargin - other.getRight()) <= mSnapDistance) {
					layout.leftMargin = other.getRight() - mResizeHandleWidth / 2;
					Log.d("bert","snap 7");
				}
				if (Math.abs(layout.leftMargin + mTempRelativeLayout.getWidth() - other.getLeft()) <= mSnapDistance) {
					layout.leftMargin = other.getLeft() - mTempRelativeLayout.getWidth() + mResizeHandleWidth / 2;
					Log.d("bert","snap 8");
				}
			}
		}
		Log.d("bert","end of Move: LEFT " + layout.leftMargin + "TOP " + layout.topMargin);
	}

	private void resizeView(int x, int y, View v) {
		//v is the resize handle which was clicked
		if (v == mBottomHandle) {
			RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams) mTempRelativeLayout.getLayoutParams());
			((RelativeLayout.LayoutParams) mBottomHandle.getLayoutParams()).topMargin += y - (params.topMargin + params.height);
			params.height += y - (params.topMargin + params.height);
			
			if(params.topMargin + params.height >= ((ViewGroup) mTempRelativeLayout.getParent()).getBottom() - mSnapDistance){
				params.height = ((ViewGroup) mTempRelativeLayout.getParent()).getBottom() - mViewToResizeTop + mResizeHandleHeight/2;
			}
			
			// start snapping part for all other elements on the
			// screen
			View other;
			for (int i = 0; i < getChildCount(); i++) {
				other = getChildAt(i);
				// only snap if views are adjacent
				if (other != mTempRelativeLayout && viewsAdjacent(mTempRelativeLayout, other)) {
						if (Math.abs(y - other.getTop()) <= mSnapDistance) {
							params.height = other.getTop() - params.topMargin + mResizeHandleHeight/2;
						}
						else if(Math.abs(y - other.getBottom()) <= mSnapDistance){
							params.height = other.getBottom() - params.topMargin + mResizeHandleHeight/2;
						}
				}
			}
			
		} else if (v == mTopHandle) {
			RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams) mTempRelativeLayout.getLayoutParams());
			params.height += params.topMargin - y;
			params.topMargin = y;
			Log.d("bert","resize 2");
			
			if(params.topMargin < mSnapDistance){
				Log.d("bert","SNAP TOP ON RESIZE");
				params.topMargin = -mResizeHandleHeight/2;
				params.height = mViewToResizeBottom + mResizeHandleHeight/2;
			}
			
			// start snapping part for all other elements on the
						// screen
			View other;
			for (int i = 0; i < getChildCount(); i++) {
				other = getChildAt(i);
				// only snap if views are adjacent
				if (other != mTempRelativeLayout && viewsAdjacent(mTempRelativeLayout, other)) {
					if (Math.abs(y - other.getBottom()) <= mSnapDistance) {
						params.topMargin = other.getBottom() - mResizeHandleHeight / 2;
						params.height = mViewToResizeBottom - other.getBottom() + mResizeHandleHeight/2; 
					} else if (Math.abs(y - other.getTop()) <= mSnapDistance) {
						params.topMargin = other.getTop() - mResizeHandleHeight / 2;
						params.height = mViewToResizeBottom - other.getTop() + mResizeHandleHeight/2;
					}
				}
			}
			
			
		} else if (v == mLeftHandle) {
			RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams) mTempRelativeLayout.getLayoutParams());
			params.width += params.leftMargin - x;
			params.leftMargin = x;
			
			if(params.leftMargin < mSnapDistance){
				Log.d("bert","SNAP TOP ON RESIZE");
				params.leftMargin = -mResizeHandleWidth/2;
				params.width = mViewToResizeRight + mResizeHandleHeight/2;
			}
			View other;
			for (int i = 0; i < getChildCount(); i++) {
				other = getChildAt(i);
				// only snap if views are adjacent
				if (other != mTempRelativeLayout && viewsAdjacent(mTempRelativeLayout, other)) {
					if (Math.abs(x - other.getRight()) <= mSnapDistance) {
						params.leftMargin = other.getRight() - mResizeHandleHeight / 2;
						params.width = mViewToResizeRight - other.getRight() + mResizeHandleHeight/2; 
					} else if (Math.abs(x - other.getLeft()) <= mSnapDistance) {
						params.leftMargin = other.getLeft() - mResizeHandleHeight / 2;
						params.width = mViewToResizeRight - other.getLeft() + mResizeHandleHeight/2;
					}
				}
			}
		
			
		} else if (v == mRightHandle ) {
			Log.d("bert","resize 4");
			RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams) mTempRelativeLayout.getLayoutParams());
			((RelativeLayout.LayoutParams) mRightHandle.getLayoutParams()).leftMargin += x - (params.leftMargin + params.width);
			params.width += x - (params.leftMargin + params.width);
			
			if(params.leftMargin + params.width >= ((ViewGroup) mTempRelativeLayout.getParent()).getRight() - mSnapDistance){
				Log.d("bert","SNAP bottom ON RESIZE");
				params.width = ((ViewGroup)mTempRelativeLayout.getParent()).getRight() - mViewToResizeLeft + mResizeHandleWidth/2;
			}
			
			View other;
			for (int i = 0; i < getChildCount(); i++) {
				other = getChildAt(i);
				// only snap if views are adjacent
				if (other != mTempRelativeLayout && viewsAdjacent(mTempRelativeLayout, other)) {
					if (Math.abs(x - other.getLeft()) <= mSnapDistance) {
						params.width = other.getLeft() - mViewToResizeLeft  + mResizeHandleHeight/2; 
					} else if (Math.abs(x - other.getRight()) <= mSnapDistance) {
						params.width = other.getRight() - params.leftMargin + mResizeHandleHeight/2;
					}
				}
			}
		}

	}

	

	private boolean viewsAdjacent(RelativeLayout toSnap, View other) {
		Rect rec1 = new Rect(toSnap.getLeft() - mResizeHandleWidth, toSnap.getTop() - mResizeHandleHeight, toSnap.getRight()
				+ mResizeHandleWidth, toSnap.getBottom() + mResizeHandleHeight);
		Rect rec2 = new Rect(other.getLeft(), other.getTop(), other.getRight(), other.getBottom());

		// TODO Auto-generated method stub
		return Rect.intersects(rec1, rec2);
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
	 * Put the selected view in a relativelayout in order to get an overlay on
	 * top of the view with handles for moving and resizing the view
	 */
	private void viewSelect(View v) {
		// TODO Auto-generated method stub
		// save clicked view as selected view
		mSelectedView = v;
		mTempRelativeLayout = new RelativeLayout(getContext());
		mSelectedInnerViewLayoutParams = (LayoutParams) v.getLayoutParams();

		removeView(v);
		// Log.d("bert","lyoaut params are leftMargin" +
		// mSelectedInnerViewLayoutParams.leftMargin);
		// create a copy to save the inner view sizes until object deselected

		// RelativeLayout.LayoutParams innerViewLayout = new
		// RelativeLayout.LayoutParams(mSelectedInnerViewLayoutParams);

		int viewWidth = mSelectedInnerViewLayoutParams.width;
		int viewHeight = mSelectedInnerViewLayoutParams.height;

		// change layout params because we want the resize images centered on
		// bound
		// Log.d("bert","to begin with it is " +
		// mSelectedInnerViewLayoutParams.leftMargin);
		/*
		 * innerViewLayout.height += mResizeHandleHeight; innerViewLayout.width
		 * += mResizeHandleWidth; innerViewLayout.leftMargin =
		 * mResizeHandleWidth / 2; innerViewLayout.rightMargin =
		 * mResizeHandleWidth / 2; innerViewLayout.topMargin =
		 * mResizeHandleHeight / 2; innerViewLayout.bottomMargin =
		 * mResizeHandleHeight / 2;
		 */

		// set alpha to show which view is in EDIT mode
		v.setAlpha(0.2f);

		// copy view into inner layout

		RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		lp3.leftMargin = mResizeHandleWidth / 2;
		lp3.rightMargin = mResizeHandleWidth / 2;
		lp3.topMargin = mResizeHandleHeight / 2;
		lp3.bottomMargin = mResizeHandleHeight / 2;
		lp3.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		lp3.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

		// maybe possible to do this more elegantly
		mTempRelativeLayout.addView(v, lp3);

		// add left Resize image
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp2.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		lp2.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		ImageView bullet = new ImageView(getContext());
		bullet.setImageResource(R.drawable.bullet);
		mTempRelativeLayout.addView(bullet, lp2);
		mLeftHandle = bullet;

		// add right Resize image
		lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp2.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		// lp2.leftMargin = viewWidth;
		bullet = new ImageView(getContext());
		bullet.setImageResource(R.drawable.bullet);
		mRightHandle = bullet;
		mTempRelativeLayout.addView(bullet, lp2);

		// add top Resize image
		lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lp2.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		bullet = new ImageView(getContext());
		bullet.setImageResource(R.drawable.bullet);
		mTempRelativeLayout.addView(bullet, lp2);
		mTopHandle = bullet;

		// add bottom Resize image
		lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		bullet = new ImageView(getContext());
		bullet.setImageResource(R.drawable.bullet);
		// lp2.topMargin = viewHeight;
		mTempRelativeLayout.addView(bullet, lp2);
		mBottomHandle = bullet;

		RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(mSelectedInnerViewLayoutParams);
		l.width += mResizeHandleWidth;
		l.height += mResizeHandleHeight;
		l.rightMargin = -mResizeHandleWidth / 2;
		l.topMargin -= mResizeHandleWidth / 2;
		l.bottomMargin = -mResizeHandleHeight / 2;
		l.leftMargin -= mResizeHandleWidth / 2;

		addView(mTempRelativeLayout, l);
		requestLayout();
		invalidate();
		
		  Log.d("bert", "component " + v + " has been selected");
		  Log.d("bert","IVE PUTTED IT IN TO A RELLATYOUT " +
		  mTempRelativeLayout);
		  Log.d("bert","and the real layout param is leftMargin " +
				  ((RelativeLayout.LayoutParams)mTempRelativeLayout.getLayoutParams()).leftMargin);
		 
	}

	private void viewUnSelect(View v) {

		View viewInside = mTempRelativeLayout.getChildAt(0);
		// RelativeLayout.LayoutParams layoutParams = (LayoutParams)
		// rl.getLayoutParams();
		// viewInside.setLayoutParams(mSelectedViewLayout);
		viewInside.setAlpha(1f);
		mTempRelativeLayout.removeAllViews();
		removeView(mTempRelativeLayout);

		mSelectedInnerViewLayoutParams.leftMargin = ((LayoutParams) mTempRelativeLayout.getLayoutParams()).leftMargin
				+ mResizeHandleWidth / 2;
		mSelectedInnerViewLayoutParams.topMargin = ((LayoutParams) mTempRelativeLayout.getLayoutParams()).topMargin
				+ mResizeHandleHeight / 2;
		mSelectedInnerViewLayoutParams.height = mTempRelativeLayout.getLayoutParams().height - mResizeHandleHeight;
		mSelectedInnerViewLayoutParams.width = mTempRelativeLayout.getLayoutParams().width - mResizeHandleWidth;

		// viewInside.setLayoutParams(mSelectedInnerViewLayoutParams);
		addView(viewInside, mSelectedInnerViewLayoutParams);

		Log.d("bert","inner view which was unselected has left " + viewInside.getLeft() + " and top " + viewInside.getTop());

		// put null in mTempRelativeLayout because 2x clicking upper
		// relativealyout (this) otherwise crashes
		mTempRelativeLayout = null;
		mSelectedView = null;
		mSelectedInnerViewLayoutParams = null;
		// Log.d("bert", "component " + viewInside +
		// " was removed from the rellayout container");

		requestLayout();
		invalidate();
	}

	private View getTouchedView(int x, int y) {
		// Log.d("bert", "touched " + x + " , " + y);
		int nbChildren = getChildCount();

		if (mTempRelativeLayout != null)
			if (mTempRelativeLayout.getLeft() <= x && x <= mTempRelativeLayout.getRight())
				if ((mTempRelativeLayout.getTop() <= y && y <= mTempRelativeLayout.getBottom())) {
					// Log.d("bert", "selected clicked");
					if (checkHandlePress(mLeftHandle, x, y))
						return mLeftHandle;
					else if (checkHandlePress(mRightHandle, x, y))
						return mRightHandle;
					else if (checkHandlePress(mTopHandle, x, y))
						return mTopHandle;
					else if (checkHandlePress(mBottomHandle, x, y))
						return mBottomHandle;

				}

		for (int i = 0; i < nbChildren; i++) {
			View v = getChildAt(i);

			if (v.getLeft() <= x && x <= v.getRight())
				if (v.getTop() <= y && y <= v.getBottom()) {
					return v;
				}

		}
		return null;
	}

	private boolean checkHandlePress(View handle, int x, int y) {
		// Log.d("bert", "left handle is on posisiont " + handle.getLeft() +
		// " en y " + handle.getTop());
		if (handle.getLeft() <= (x - mTempRelativeLayout.getLeft()) && (x - mTempRelativeLayout.getLeft()) <= handle.getRight()) {
			if ((handle.getTop() <= (y - mTempRelativeLayout.getTop()) && (y - mTempRelativeLayout.getTop()) <= handle
					.getBottom())) {
				// Log.d("bert", "BONSU BOSNUS");
				return true;
			}
		}

		return false;
	}

}
