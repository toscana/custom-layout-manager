package com.example.customlayoutmanager;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.View.OnLongClickListener;

public class CustomLayout extends ViewGroup {

	private static final int ROWS = 500;
	private static final int COLS = 1200;
	private int mDx;
	private int mDy;

	private View mToMove = null;
	private View mToScale = null;

	private HashMap<View, Rect> mViewProps;
	private boolean mPressed = false;

	public CustomLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mViewProps = new HashMap<View, Rect>();
	}

	/*
	 * This Method has to be overridden because we want to handle touch events
	 * ourselves in this class.
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}

	/*
	 * @param x X-value of the pressed pixel
	 * 
	 * @param y X-value of the pressed pixel
	 * 
	 * @return an array containing the x and y values of the pressed column and
	 * row
	 */
	private int[] getRowColPressed(int x, int y) {
		int xPressed = x / (getWidth() / COLS);
		int yPressed = y / (getHeight() / ROWS);
		return new int[] { xPressed, yPressed };
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		int pointerCount = ev.getPointerCount();
		if (pointerCount == 2 && mToMove != null) {
			Log.d("bert", "pointercount is " + pointerCount);

			PointerCoords p1 = new PointerCoords();
			PointerCoords p2 = new PointerCoords();

			ev.getPointerCoords(0, p1);
			ev.getPointerCoords(1, p2);

			Log.d("point", "first is " + p1.x + "," + p1.y);
			Log.d("point", "second is " + p2.x + "," + p2.y);

			if (p1.x < p2.x && p1.y > p2.y) {
				// first finger is left down, second is right up
				Rect r = mViewProps.get(mToMove);
				int[] leftDown = getRowColPressed((int) p1.x, (int) p1.y);
				int[] rightUp = getRowColPressed((int) p2.x, (int) p2.y);
				r.set(leftDown[0], rightUp[1], rightUp[0], leftDown[1]);
				Log.d("bert", "chagend to" + leftDown[0] + " " + rightUp[1]
						+ " " + rightUp[0] + " " + leftDown[1]);
				this.requestLayout();
				invalidate();
			} else if (p1.x < p2.x && p1.y < p2.y) {
				// first finger is left up, second finger is right down
				Rect r = mViewProps.get(mToMove);
				int[] leftUp = getRowColPressed((int) p1.x, (int) p1.y);
				int[] rightDown = getRowColPressed((int) p2.x, (int) p2.y);
				r.set(leftUp[0], leftUp[1], rightDown[0], rightDown[1]);
				Log.d("bert", "chagned to " + leftUp[0] + " " + leftUp[1] + " "
						+ rightDown[0] + " " + rightDown[1]);
				this.requestLayout();
				invalidate();
			} else if (p1.x > p2.x && p1.y < p2.y) {
				// first finger is right up, second finger is left down
				Rect r = mViewProps.get(mToMove);
				int[] rightUp = getRowColPressed((int) p1.x, (int) p1.y);
				int[] leftDown = getRowColPressed((int) p2.x, (int) p2.y);
				r.set(leftDown[0], rightUp[1], rightUp[0], leftDown[1]);
				// Log.d("bert","chagned to " + leftUp[0]+" " + leftUp[1]+" " +
				// rightDown[0]+" " + rightDown[1]);
				this.requestLayout();
				invalidate();
			} else {
				// first finger is right down, second finger is left up
				Rect r = mViewProps.get(mToMove);
				int[] rightDown = getRowColPressed((int) p1.x, (int) p1.y);
				int[] leftUp = getRowColPressed((int) p2.x, (int) p2.y);
				r.set(leftUp[0], leftUp[1], rightDown[0], rightDown[1]);
				Log.d("bert", "chagned to " + leftUp[0] + " " + leftUp[1] + " "
						+ rightDown[0] + " " + rightDown[1]);
				this.requestLayout();
				invalidate();
			}
			return true;

		} else if (pointerCount == 1) {

			// movement gesture
			int action = ev.getActionMasked();

			int[] pos = getRowColPressed((int) ev.getX(), (int) ev.getY());
			View v = getTouchedView((int) ev.getX(), (int) ev.getY());

			switch (action) {
			case (MotionEvent.ACTION_DOWN):
				if (v != null) {
					v.setBackgroundColor(Color.RED);
					mToMove = v;
				}

				return true;
			case (MotionEvent.ACTION_MOVE):

				// Log.d("bert", "Action was MOVE");
				if (mToMove != null) {

					Rect dims = mViewProps.get(mToMove);
					int[] rowcolPressed = getRowColPressed((int) ev.getX(),
							(int) ev.getY());
					// Log.d("position", "rowcol is " + rowcolPressed[0] + "x" +
					// rowcolPressed[1]);
					if (!mPressed) {
						mDx = dims.left - rowcolPressed[0];
						mDy = dims.top - rowcolPressed[1];
						mPressed = true;
					}

					// Log.d("position", "dx" + mDx);
					// Log.d("position", "dy" + mDy);

					rowcolPressed[0] += mDx;
					rowcolPressed[1] += mDy;
					// Log.d("position", "going to " + rowcolPressed[0] + "," +
					// rowcolPressed[1]);

					/*
					 * 
					 * start computing new center
					 */

					// todo doest work when 1 size width and gheight

					Rect tomoveRect = mViewProps.get(mToMove);

					Rect tmp = new Rect(tomoveRect);
					tmp.offsetTo(rowcolPressed[0], rowcolPressed[1]);
					// Log.d("bert","can be moved " + mCanBeMoved);
					if (liesWithinBoundaries(tmp)) {
						tomoveRect.offsetTo(rowcolPressed[0], rowcolPressed[1]);
					} else {
						if (verticalMovementPossible(tmp)) {
							tomoveRect.offsetTo(tomoveRect.left,
									rowcolPressed[1]);
						} else if (horizontalMovementPossible(tmp)) {
							tomoveRect.offsetTo(rowcolPressed[0],
									tomoveRect.top);
						}

					}

					this.requestLayout();
					this.invalidate();

				}
				// Log.d("bert","moved to X" + ev.getX());
				// Log.d("bert","moved to Y" + ev.getY());
				return true;
			case (MotionEvent.ACTION_UP):
				// Log.d("bert", "Action was UP");
				if (v != null)
					v.setBackgroundColor(Color.BLACK);
				mToMove = null;
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
				if (mToMove != null) {
					if (v != null) {
						mToScale = v;
						// Log.d("bert", "toscale HIT HIT HIT HIT");
					}
				}
				return true;
			default:
				return super.onTouchEvent(ev);
			}

		}
		return super.onTouchEvent(ev);
		// return false;
	}

	private boolean horizontalMovementPossible(Rect tmp) {
		int[] rightDown = getRowColPressed(this.getWidth(), this.getHeight());
		return !(tmp.left < 0 || tmp.right > rightDown[0]);

	}

	private boolean verticalMovementPossible(Rect tmp) {
		int[] rightDown = getRowColPressed(this.getWidth(), this.getHeight());

		return !(tmp.top < 0 || tmp.bottom > rightDown[1]);
	}

	private boolean liesWithinBoundaries(Rect tmp) {
		int[] rightDown = getRowColPressed(this.getWidth(), this.getHeight());
		return !(tmp.left < 0 || tmp.top < 0 || tmp.right > rightDown[0] || tmp.bottom > rightDown[1]);
	}

	private View getTouchedView(int x, int y) {
		int nbChildren = getChildCount();
		int[] rowcolPressed = getRowColPressed(x, y);
		for (int i = 0; i < nbChildren; i++) {
			View v = getChildAt(i);
			Rect cd = mViewProps.get(v);
			if (cd.left <= rowcolPressed[0] && rowcolPressed[0] <= cd.right)
				if (cd.top <= rowcolPressed[1] && rowcolPressed[1] <= cd.bottom) {
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
		super.addView(child);
		mViewProps.put(child, new Rect(leftRow, leftCol, rightRow, rightCol));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < this.getChildCount(); i++) {
			View v = getChildAt(i);
			Rect cd = mViewProps.get(v);
			int[] leftUp = getLeftUp(cd.top, cd.left);
			int[] rightDown = getRightDown(cd.bottom, cd.right);
			// Log.d("onLayout", "component " + ((TextView) v).getText()
			// + "leftup " + getCo(leftUp) + " rightdown "
			// + getCo(rightDown));
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
}
