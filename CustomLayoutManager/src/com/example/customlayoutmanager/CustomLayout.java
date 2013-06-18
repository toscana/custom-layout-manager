package com.example.customlayoutmanager;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class CustomLayout extends ViewGroup {
	private int mDx;
	private int mDy;

	private int COLS;
	private int ROWS;

	private View mSelectedView = null;

	private boolean mPressed = false;

	public CustomLayout(Context context) {
		super(context);
		COLS = getWidth();
		ROWS = getHeight();
	}

	/*
	 * This Method has to be overridden because we want to handle touch events
	 * ourselves in this class.
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
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

			Log.d("point", "first is " + p1.x + "," + p1.y);
			Log.d("point", "second is " + p2.x + "," + p2.y);

			/*
			 * if (p1.x < p2.x && p1.y > p2.y) { // first finger is left down,
			 * second is right up Rect r = mSelectedView.getDimensions(); int[]
			 * leftDown = getRowColPressed((int) p1.x, (int) p1.y); int[]
			 * rightUp = getRowColPressed((int) p2.x, (int) p2.y);
			 * r.set(leftDown[0], rightUp[1], rightUp[0], leftDown[1]);
			 * Log.d("bert", "chagend to" + leftDown[0] + " " + rightUp[1] + " "
			 * + rightUp[0] + " " + leftDown[1]);
			 * //((GraphicalView)mToMove).repaint(); this.requestLayout();
			 * invalidate(); } else if (p1.x < p2.x && p1.y < p2.y) { // first
			 * finger is left up, second finger is right down Rect r =
			 * mSelectedView.getDimensions(); int[] leftUp =
			 * getRowColPressed((int) p1.x, (int) p1.y); int[] rightDown =
			 * getRowColPressed((int) p2.x, (int) p2.y); r.set(leftUp[0],
			 * leftUp[1], rightDown[0], rightDown[1]); Log.d("bert",
			 * "chagned to " + leftUp[0] + " " + leftUp[1] + " " + rightDown[0]
			 * + " " + rightDown[1]); //((GraphicalView)mToMove).repaint();
			 * this.requestLayout(); invalidate(); } else if (p1.x > p2.x &&
			 * p1.y < p2.y) { // first finger is right up, second finger is left
			 * down Rect r = mSelectedView.getDimensions(); int[] rightUp =
			 * getRowColPressed((int) p1.x, (int) p1.y); int[] leftDown =
			 * getRowColPressed((int) p2.x, (int) p2.y); r.set(leftDown[0],
			 * rightUp[1], rightUp[0], leftDown[1]); //
			 * Log.d("bert","chagned to " + leftUp[0]+" " + leftUp[1]+" " + //
			 * rightDown[0]+" " + rightDown[1]);
			 * //((GraphicalView)mToMove).repaint(); this.requestLayout();
			 * invalidate(); } else { // first finger is right down, second
			 * finger is left up Rect r = mSelectedView.getDimensions(); int[]
			 * rightDown = getRowColPressed((int) p1.x, (int) p1.y); int[]
			 * leftUp = getRowColPressed((int) p2.x, (int) p2.y);
			 * r.set(leftUp[0], leftUp[1], rightDown[0], rightDown[1]);
			 * Log.d("bert", "chagned to " + leftUp[0] + " " + leftUp[1] + " " +
			 * rightDown[0] + " " + rightDown[1]);
			 * //((GraphicalView)mToMove).repaint(); this.requestLayout();
			 * invalidate(); }
			 */
			return true;

		} else if (pointerCount == 1) {

			// movement gesture
			int action = ev.getActionMasked();

			/*
			 * for(int i=0;i<getChildCount();i++){ Log.d("bert","child" + i +
			 * "is " + getChildAt(i)); }
			 */

			/*
			 * if(v != null){ //indien geen view touched moet er sowieso niets
			 * gebeuren, dus v moet != null
			 * 
			 * if(mSelectedView != null){ //er was al een view selected if(v ==
			 * mSelectedView){ //is zelfde gebleven //NOTHING TO DO } else{ //
			 * is een andere view geworden //first lets remove the
			 * relativelayout container from the old stuff RelativeLayout rl =
			 * (RelativeLayout)mSelectedView.getParent(); removeView(rl);
			 * rl.removeView(mSelectedView); Rect rec = mViewProps.remove(rl);
			 * mViewProps.put(mSelectedView,rec); addView(mSelectedView,rec);
			 * 
			 * 
			 * //now lets add the new touched view as a rellayout on the system
			 * 
			 * mSelectedView = v; RelativeLayout newrel = new
			 * RelativeLayout(getContext()); TextView overlay = new
			 * TextView(getContext()); overlay.setText("bert");
			 * removeView(mSelectedView); newrel.addView(mSelectedView);
			 * newrel.addView(overlay); addView(newrel, rec); rec =
			 * mViewProps.remove(mSelectedView); mViewProps.put(newrel,rec);
			 * 
			 * requestLayout(); invalidate(); } } else{ //mSelectedview was
			 * null, so this is the first selected view in this program //we now
			 * only have to remove this view from the layout and put it inside a
			 * rellayout
			 * 
			 * Rect rec = mViewProps.remove(v); mSelectedView = v;
			 * //removeView(mSelectedView); RelativeLayout newrel = new
			 * RelativeLayout(getContext()); TextView overlay = new
			 * TextView(getContext()); overlay.setText("bert");
			 * removeView(mSelectedView); newrel.addView(mSelectedView);
			 * newrel.addView(overlay); addView(newrel,rec);
			 * 
			 * mViewProps.put(newrel,rec); requestLayout(); invalidate(); }
			 * 
			 * }
			 */

			View v = getTouchedView((int) ev.getX(), (int) ev.getY());
			switch (action) {
			case (MotionEvent.ACTION_DOWN):

				if (v != null) {

					if (mSelectedView != null) {
						viewUnSelect(mSelectedView);
					}
					mSelectedView = v;
					viewSelect(v);

					Log.d("bert", "touched" + v);
				}
				if (v != null) {
					v.setBackgroundColor(Color.RED);

				}
				return true;
			case (MotionEvent.ACTION_MOVE):

				//
				// Log.d("bert", "Action was MOVE");
				// if (v != null) {
				//
				// //TODO what if complex views are used? view inside view
				// inside viwe... -->parent is not correct
				//
				// //Log.d("bert","GOING TO MOVE " + mSelectedView);
				// //Log.d("bert","GOING TO MOVE its parent " +
				// mSelectedView.getParent());
				//
				// int x = (int) ev.getX();
				// int y = (int) ev.getY();
				// // Log.d("position", "rowcol is " + rowcolPressed[0] + "x" +
				// // rowcolPressed[1]);
				//
				// if (!mPressed) {
				// if(x < v.getLeft() +(v.getRight()-v.getLeft())*0.33 &&
				// y<v.getTop() + (v.getBottom()-v.getTop())*0.33 ){
				// //leftupper corner
				// mSelectedView.setLeft(x);
				// mSelectedView.setTop(y);
				// this.requestLayout();
				// invalidate();
				// return true;
				//
				// }
				// else if(x > v.getLeft() +(v.getRight()-v.getLeft())*0.66 &&
				// y<v.getTop() + (v.getBottom()-v.getTop())*0.33 ){
				// //rightupper corner
				//
				// mSelectedView.setRight(x);
				// mSelectedView.setTop(y);
				// this.requestLayout();
				// invalidate();
				// return true;
				//
				// }
				// else if(x < v.getLeft() +(v.getRight()-v.getLeft())*0.33 &&
				// y>v.getTop() + (v.getBottom()-v.getTop())*0.66){
				// //leftdown corner
				// mSelectedView.setLeft(x);
				// mSelectedView.setBottom(y);
				// this.requestLayout();
				// invalidate();
				// return true;
				//
				// }
				// else if(x > v.getLeft() +(v.getRight()-v.getLeft())*0.66 &&
				// y>v.getTop() + (v.getBottom()-v.getTop())*0.66 ) {
				// //rightdown corner
				// mSelectedView.setRight(x);
				// mSelectedView.setBottom(y);
				// this.requestLayout();
				// invalidate();
				// return true;
				// }
				//
				//
				//
				// mDx = v.getLeft() - x;
				// mDy = v.getTop() - y;
				// mPressed = true;
				// }
				//
				// // Log.d("position", "dx" + mDx);
				// // Log.d("position", "dy" + mDy);
				//
				// x += mDx;
				// y += mDy;
				// // Log.d("position", "going to " + rowcolPressed[0] + "," +
				// // rowcolPressed[1]);
				//
				// /*
				// *
				// * start computing new center
				// */
				//
				// // todo doest work when 1 size width and gheight
				//
				// /*
				// Rect tomoveRect = mSelectedView.getDimensions();
				//
				// Rect tmp = new Rect(tomoveRect);
				// tmp.offsetTo(rowcolPressed[0], rowcolPressed[1]);
				//
				// // Log.d("bert","can be moved " + mCanBeMoved);
				// if (liesWithinBoundaries(tmp)) {
				// tomoveRect.offsetTo(rowcolPressed[0], rowcolPressed[1]);
				// } else {
				// if (verticalMovementPossible(tmp)) {
				// tomoveRect.offsetTo(tomoveRect.left,
				// rowcolPressed[1]);
				// } else if (horizontalMovementPossible(tmp)) {
				// tomoveRect.offsetTo(rowcolPressed[0],
				// tomoveRect.top);
				// }
				//
				// }
				// */
				//
				// //going to move
				// mSelectedView.offsetLeftAndRight(y);
				// mSelectedView.offsetTopAndBottom(x);
				//
				// this.requestLayout();
				// this.invalidate();
				//
				// }

				// Log.d("bert","moved to X" + ev.getX());
				// Log.d("bert","moved to Y" + ev.getY());
				return true;
			case (MotionEvent.ACTION_UP):
				// Log.d("bert", "Action was UP");

				mPressed = false;
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

	private void viewUnSelect(View v) {
		// TODO Auto-generated method stub
		v.setBackgroundColor(Color.BLACK);

	}

	private void viewSelect(View v) {
		// TODO Auto-generated method stub
		v.setBackgroundColor(Color.RED);
		RelativeLayout newrel = new RelativeLayout(getContext());
		TextView overlay = new TextView(getContext());
		overlay.setText("bert");
		
		removeView(v);
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
		
		newrel.addView(v,lp);
	//	newrel.addView(overlay,lp);
		
		RelativeLayout.LayoutParams lp2  = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp2.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
		lp2.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		
		ImageView bullet = new ImageView(getContext());
		bullet.setImageResource(R.drawable.bullet);
		newrel.addView(bullet);
		//bullet.bringToFront();
		
		
		
		addView(newrel, v.getLeft(),v.getTop(),v.getRight(),v.getBottom());
		
		requestLayout();
		invalidate();

	}

	private void addView(View v, Rect rec) {
		// TODO Auto-generated method stub
		addView(v, rec.top, rec.left, rec.bottom, rec.right);

	}

	/*
	 * private boolean horizontalMovementPossible(Rect tmp) { int[] rightDown =
	 * getRowColPressed(this.getWidth(), this.getHeight()); return !(tmp.left <
	 * 0 || tmp.right > rightDown[0]);
	 * 
	 * }
	 * 
	 * private boolean verticalMovementPossible(Rect tmp) { int[] rightDown =
	 * getRowColPressed(this.getWidth(), this.getHeight());
	 * 
	 * return !(tmp.top < 0 || tmp.bottom > rightDown[1]); }
	 * 
	 * private boolean liesWithinBoundaries(Rect tmp) { int[] rightDown =
	 * getRowColPressed(this.getWidth(), this.getHeight()); return !(tmp.left <
	 * 0 || tmp.top < 0 || tmp.right > rightDown[0] || tmp.bottom >
	 * rightDown[1]); }
	 */

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

	private int[] getLeftUp(int row, int col) {
		int colWidth = this.getWidth() / COLS;
		int rowHeight = this.getHeight() / ROWS;
		int[] point = new int[2];
		point[0] = col * colWidth;
		point[1] = row * rowHeight;
		return point;
	}

	private int[] getRightDown(int row, int col) {
		int colWidth = this.getWidth() / COLS;
		int rowHeight = this.getHeight() / ROWS;
		int[] point = new int[2];
		point[0] = (col + 1) * colWidth;
		point[1] = (row + 1) * rowHeight;
		return point;
	}

	// public int[] get

	/*
	 * Called when this view should assign a view and positions to all of its
	 * children.
	 * 
	 * changed This is a new size or position for this view left Left position,
	 * relative to parent top Top position, relative to parent right Right
	 * position, relative to parent bottom Bottom position, relative to parent
	 */

	public void addView(View child, int leftRow, int leftCol, int rightRow,
			int rightCol) {
		// TODO Auto-generated method stub
		child.setTop(leftRow);
		child.setLeft(leftCol);
		child.setBottom(rightRow);
		child.setRight(rightCol);

		super.addView(child);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < this.getChildCount(); i++) {
			View v = getChildAt(i);

			// Log.d("onLayout", "component " + ((TextView) v).getText()
			// + "leftup " + getCo(leftUp) + " rightdown "
			// + getCo(rightDown));
			v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
		}
	}

	private String getCo(int[] co) {
		return "(" + co[0] + "," + co[1] + ")";
	}

	/*
	 * 
	 * Measure the view and its content to determine the measured width and the
	 * measured height. This method is invoked by measure(int, int) and should
	 * be overriden by subclasses to provide accurate and efficient measurement
	 * of their contents.
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		// At this time we need to call setMeasuredDimensions(). Lets just
		// call the parent View's method
		// (see
		// https://github.com/android/platform_frameworks_base/blob/master/core/java/android/view/View.java)
		// that does:

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		setMeasuredDimension(
				getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
				getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));

		int wspec = MeasureSpec.makeMeasureSpec(getMeasuredWidth()
				/ getChildCount(), MeasureSpec.EXACTLY);
		int hspec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(),
				MeasureSpec.EXACTLY);
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			v.measure(wspec + 1, hspec + 1);
		}
	}
}
