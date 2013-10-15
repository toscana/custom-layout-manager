package com.example.customlayoutmanager;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Object mChartView;
	
	  private GraphicalView mChart;

	    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

	    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

	    private XYSeries mCurrentSeries;

	    private XYSeriesRenderer mCurrentRenderer;

	    private MyRelativeLayout mCustomLayout;
	    
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		mCustomLayout = new MyRelativeLayout(this,R.drawable.bullet);
		mCustomLayout.setLayoutEditable(true);
		TextView b = new TextView(this);
		//b.setPadding(0,0,0,0);
		b.setText("BERTIE BERTIE");
		b.setBackgroundColor(Color.RED);
		mCustomLayout.addView(b,300,300,600,600);
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("jos","view clicked");
			}
		});
		
		b = new TextView(this);
		//b.setPadding(0,0,0,0);
		b.setText("Test TextView2");
		b.setBackgroundColor(Color.BLUE);
		mCustomLayout.addView(b,600,600,800,800);
		
		b = new TextView(this);
		//b.setPadding(0,0,0,0);
		b.setText("Test TextView3");
		b.setBackgroundColor(Color.GREEN);
		mCustomLayout.addView(b,800,800,1000,1000);
		
		
		 if (mChart == null) {
	            initChart();
	            addSampleData();
	            mChart = ChartFactory.getCubeLineChartView(this, mDataset, mRenderer, 0.3f);
	            mCustomLayout.addView(mChart,1000,1000,1200,1200);
	        } else {
	            mChart.repaint();
	        }
		
		 
		setContentView(mCustomLayout);
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
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.guimode:
            	mCustomLayout.setLayoutEditable(!mCustomLayout.isLayoutEditable());
            	Log.d("bert","layout not editable anymore");
            	invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem i = menu.findItem(R.id.guimode);
		if(mCustomLayout.isLayoutEditable())
			i.setIcon(R.drawable.device_access_dial_pad);
		else
			i.setIcon(R.drawable.device_access_dial_pad_closed);
		return super.onPrepareOptionsMenu(menu);
	}
    
    
  

}
