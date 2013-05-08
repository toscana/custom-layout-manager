package com.example.customlayoutmanager;

import android.util.Log;

public class CustomDim {

	private int leftY;
	private int leftX;

	private int rightY;
	private int rightX;

	

	public CustomDim(int leftRow, int leftCol, int rightRow, int righCol) {
		super();
		this.leftY = leftRow;
		this.leftX = leftCol;
		this.rightY = rightRow;
		this.rightX = righCol;
	}
	
	public void translate(int dx,int dy){
		rightX = rightX + dx;
		leftX = leftX + dx;
		
		rightY = rightY + dy;
		leftY = leftY + dy;
	}

	public void changeCenterPosition(int[] coord) {
		// TODO Auto-generated method stub
		int centerX = coord[0];
		int centerY = coord[1];
		//todo doest work when 1 size width and gheight
		
		int width = rightX - leftX;
		int height = rightY - leftY;

		rightX = centerX + (width/2);
		rightY = centerY + (height/2);
		leftX = centerX - (width/2);
		leftY = centerY - (height/2);
		if (width % 2 != 0)
			rightX = rightX + 1;
		if (height % 2 != 0)
			rightY = rightY + 1;
		Log.d("bert","changed dims to " + toString());

	}
	
	public String toString(){
		return "["+leftX + "," + leftY+ "] - [" + rightX + "," + rightY +"]";
	}

	public int getLeftY() {
		return leftY;
	}

	public void setLeftY(int leftY) {
		this.leftY = leftY;
	}

	public int getLeftX() {
		return leftX;
	}

	public void setLeftX(int leftX) {
		this.leftX = leftX;
	}

	public int getRightY() {
		return rightY;
	}

	public void setRightY(int rightY) {
		this.rightY = rightY;
	}

	public int getRightX() {
		return rightX;
	}

	public void setRightX(int rightX) {
		this.rightX = rightX;
	}
	
	public void enlarge(){
		rightX+=2;
		rightY+=2;
	}

	public void smallen() {
		// TODO Auto-generated method stub
		rightX--;
		rightY--;
		
	}

	public int getCenterX() {
		// TODO Auto-generated method stub
		return (rightX + leftX)/2;
	}

	public int getCenterY() {
		// TODO Auto-generated method stub
		return (rightY + leftY )/2;
	}

}
