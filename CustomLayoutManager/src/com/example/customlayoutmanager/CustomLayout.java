package com.example.customlayoutmanager;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnLongClickListener;

public class CustomLayout extends ViewGroup implements OnLongClickListener {

	private static final int ROWS = 50;
	private static final int COLS = 100;
	private int dx;
	private int dy;

	private ScaleGestureDetector mScaleDetector;
	
	private View toMove = null;
	private View toScale = null;

	private HashMap<View, CustomDim> viewProps;
	private boolean pressed = false;;

	public CustomLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		viewProps = new HashMap<View, CustomDim>();
		this.setOnLongClickListener(this);

		// scale code
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float mScaleFactor = 1.f;mScaleFactor *= detector.getScaleFactor();

			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

			Log.d("bert", "SCALE SCALE SCALE XXX factor " + mScaleFactor);
			if(mScaleFactor >= 1){
				//enlarge
				viewProps.get(toScale).enlarge();
				requestLayout();
				//invalidate();
			}
			else{
				//make smaller
				viewProps.get(toScale).smallen();
				requestLayout();
			}
			
			invalidate();
			return true;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		Log.d("bert", "touched");
		return true;

	}

	private int[] getRowColPressed(int x, int y) {
		int xPressed = x / (getWidth() / COLS);
		int yPressed = y / (getHeight() / ROWS);

		return new int[] { xPressed, yPressed };
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		//movement gesture
		int index = ev.getActionIndex();
        int action = ev.getActionMasked();
        int pointerId = ev.getPointerId(index);

		// scaling code
		mScaleDetector.onTouchEvent(ev);

		//int action = MotionEventCompat.getActionMasked(ev); efkes weggzet
		int[] pos = getRowColPressed((int) ev.getX(), (int) ev.getY());
		View v = getTouchedView((int) ev.getX(), (int) ev.getY());

		switch (action) {
		case (MotionEvent.ACTION_DOWN):
			
				Log.d("bert", "Action was DOWN");
			Log.d("position", "pressed position was " + getCo(pos));

			// Log.d("bert","ontouchevent x is " + ev.getX() + " y is " +
			// ev.getY());

			if (v != null) {
				v.setBackgroundColor(Color.RED);
				toMove = v;
			}

			return true;
		case (MotionEvent.ACTION_MOVE):
			
			
			Log.d("bert", "Action was MOVE");
			if (toMove != null) {

				CustomDim dims = viewProps.get(toMove);
				int[] rowcol = getRowColPressed((int) ev.getX(), (int) ev.getY());
				Log.d("position","rowcol is " + rowcol[0] + "x" +rowcol[1]);
				if(!pressed){
					dx = dims.getCenterX() - rowcol[0];
					dy = dims.getCenterY() - rowcol[1];
					pressed = true;
				}
				
				Log.d("position","dx" + dx);
				Log.d("position","dy" + dy);
				
				rowcol[0] += dx;
				rowcol[1] += dy;
				Log.d("position","going to " + rowcol[0] + ","+rowcol[1]);				
				viewProps.get(toMove).changeCenterPosition(rowcol);

				
				this.requestLayout();
				this.invalidate();

			}
			// Log.d("bert","moved to X" + ev.getX());
			// Log.d("bert","moved to Y" + ev.getY());
			return true;
		case (MotionEvent.ACTION_UP):
			Log.d("bert", "Action was UP");
			if (v != null)
				v.setBackgroundColor(Color.BLACK);
			toMove = null;
			pressed  = false;
			dx = 0;
			dy = 0;
			return true;
		case (MotionEvent.ACTION_CANCEL):
			Log.d("bert", "Action was CANCEL");
			return true;
		case (MotionEvent.ACTION_OUTSIDE):
			Log.d("bert", "Movement occurred outside bounds "
					+ "of current screen element");
			return true;

		case (MotionEvent.ACTION_POINTER_DOWN):
			Log.d("bert", "POINTER DOWN Action was POINTER DOWN + place is "
					+ ev.getX() + "," + ev.getY());
			if (toMove != null) {
				if (v != null) {
					toScale = v;
					Log.d("bert", "toscale HIT HIT HIT HIT");
				}
			}
			return true;
		default:
			return super.onTouchEvent(ev);
		}

		// return false;
	}

	private View getTouchedView(int x, int y) {
		int nbChildren = getChildCount();
		int[] rowcolPressed = getRowColPressed(x, y);
		for (int i = 0; i < nbChildren; i++) {
			View v = getChildAt(i);
			CustomDim cd = viewProps.get(v);
			if (cd.getLeftX() <= rowcolPressed[0]
					&& rowcolPressed[0] <= cd.getRightX())
				if (cd.getLeftY() <= rowcolPressed[1]
						&& rowcolPressed[1] <= cd.getRightY()) {
					Log.d("position",
							"HIT HIT falls within " + viewProps.get(v));
					return v;
				}

		}
		return null;
	}

	private int[] getLeftUp(int row, int col) {
		Log.d("bert", "width is " + this.getWidth());
		Log.d("bert", "height is " + this.getHeight());
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
		super.addView(child);
		viewProps.put(child,
				new CustomDim(leftRow, leftCol, rightRow, rightCol));
		child.setOnLongClickListener(this);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < this.getChildCount(); i++) {
			View v = getChildAt(i);
			CustomDim cd = viewProps.get(v);
			int[] leftUp = getLeftUp(cd.getLeftY(), cd.getLeftX());
			int[] rightDown = getRightDown(cd.getRightY(), cd.getRightX());
			Log.d("onLayout", "component " + ((TextView) v).getText()
					+ "leftup " + getCo(leftUp) + " rightdown "
					+ getCo(rightDown));
			v.layout(leftUp[0], leftUp[1], rightDown[0], rightDown[1]);
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

	@Override
	public boolean onLongClick(View v) {
		Log.d("bert", "long click");
		return false;
	}
}
