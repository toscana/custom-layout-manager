package com.example.customlayoutmanager;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Object mChartView;
	
	  private GraphicalView mChart;

	    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

	    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

	    private XYSeries mCurrentSeries;

	    private XYSeriesRenderer mCurrentRenderer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		MyRelativeLayout c = new MyRelativeLayout(this);
		c.setLayoutEditable(true);
		TextView b = new TextView(this);
		//b.setPadding(0,0,0,0);
		b.setText("Test TextView");
		b.setBackgroundColor(Color.RED);
		c.addView(b,300,300,301,301);
		
		b = new TextView(this);
		//b.setPadding(0,0,0,0);
		b.setText("Test TextView2");
		b.setBackgroundColor(Color.RED);
		c.addView(b,50,50,100,100);
		
		b = new TextView(this);
		//b.setPadding(0,0,0,0);
		b.setText("Test TextView3");
		b.setBackgroundColor(Color.RED);
		c.addView(b,100,100,200,200);
		
		
		b = new TextView(this);
		//b.setPadding(0,0,0,0);
		b.setText("Test TextView4");
		b.setBackgroundColor(Color.RED);
		c.addView(b,300,300,500,500);
		
		b = new TextView(this);
	//	b.setPadding(0,0,0,0);
		b.setText("Test TextView5");
		b.setBackgroundColor(Color.RED);
		c.addView(b,600,600,800,800);
		
		Button s = new Button(this);
		//b.setPadding(0,0,0,0);
		b.setText("Test TextView5");
		b.setBackgroundColor(Color.RED);
		c.addView(s,800,800,1000,1000);
		
		
		
		
		 if (mChart == null) {
	            initChart();
	            addSampleData();
	            mChart = ChartFactory.getCubeLineChartView(this, mDataset, mRenderer, 0.3f);
	            c.addView(mChart,1000,1000,1500,1300);
	        } else {
	            mChart.repaint();
	        }
		
		setContentView(c);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	private void initChart() {
        mCurrentSeries = new XYSeries("Sample Data");
        mDataset.addSeries(mCurrentSeries);
        mCurrentRenderer = new XYSeriesRenderer();
        mRenderer.addSeriesRenderer(mCurrentRenderer);
    }

    private void addSampleData() {
        mCurrentSeries.add(1, 2);
        mCurrentSeries.add(2, 3);
        mCurrentSeries.add(3, 2);
        mCurrentSeries.add(4, 5);
        mCurrentSeries.add(5, 4);
    }

  

}
