package com.example.customlayoutmanager;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.widget.TextView;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		CustomLayout c = new CustomLayout(this);
		TextView b = new TextView(this);
		b.setPadding(0,0,0,0);
		b.setText("Test TextView");
		b.setBackgroundColor(Color.BLACK);
		c.addView(b,0,0,2,2);
		
		b = new TextView(this);
		b.setPadding(0,0,0,0);
		b.setText("Test TextView2");
		b.setBackgroundColor(Color.BLACK);
		c.addView(b,3,3,5,5);
		
		b = new TextView(this);
		b.setPadding(0,0,0,0);
		b.setText("Test TextView3");
		b.setBackgroundColor(Color.BLACK);
		c.addView(b,6,6,8,8);
		
		
		b = new TextView(this);
		b.setPadding(0,0,0,0);
		b.setText("Test TextView4");
		b.setBackgroundColor(Color.BLACK);
		c.addView(b,9,9,11,11);
		
		b = new TextView(this);
		b.setPadding(0,0,0,0);
		b.setText("Test TextView5");
		b.setBackgroundColor(Color.BLACK);
		c.addView(b,12,12,20,20);
		
		
		setContentView(c);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
