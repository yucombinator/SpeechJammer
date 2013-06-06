package com.icechen1.speechjammer;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.appmsg.AppMsg;
import com.fima.chartview.LinearSeries;
import com.fima.chartview.LinearSeries.LinearPoint;
import com.icechen1.speechjammer.ConfigureFragment.HeadsetConnectionReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
/*
 * SpeechJammer
 * Copyright (C) 2013 Yu Chen Hou
 */
public class MainActivity extends SherlockFragmentActivity {
	AudioBufferManager audiosource;
	static boolean started = false;
	boolean mShowingBack = false;
	Menu menu;
	static int delay_time = 200;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//TODO Toggle
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        if (savedInstanceState == null) {
        	getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new ConfigureFragment())
                    .commit();
        }
        
		AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if(!amanager.isBluetoothA2dpOn() && !amanager.isWiredHeadsetOn()){
			//Display a feedback warning
			AppMsg.makeText(this, getResources().getString (R.string.no_headphone_message), AppMsg.STYLE_ALERT).show();
		}
		registerReceiver(new HeadsetConnectionReceiver(), 
                new IntentFilter(Intent.ACTION_HEADSET_PLUG));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.menu, menu);
		this.menu = menu;
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.about:
	        return true;
	    case R.id.toggle:
	    	//Turn off or on
			if(started){
				flipCard();
				audiosource.interrupt();
				item.setTitle(getResources().getString (R.string.toggle_start));
				started = false;
				return true;
			}else{
				flipCard();
				try{
					audiosource = new AudioBufferManager(delay_time);
					audiosource.start();
				}catch(Exception e){
					audiosource.interrupt();
					audiosource = new AudioBufferManager(delay_time);
					audiosource.start();
				}
				item.setTitle(getResources().getString (R.string.toggle_stop));
				started = true;
				return true;
			}
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    audiosource.interrupt();
	}
	public void updateChart(View view){
	    Visualizer v = new Visualizer(0);
	    v.setEnabled(true);
	    byte[] waveform = new byte[v.getCaptureSize()];
	    v.getWaveForm(waveform);
	    int rate = v.getSamplingRate();
	    
		// Find the chart view
		//ChartView chartView = (ChartView) findViewById(R.id.chart_view);

		// Create the data points
		LinearSeries series = new LinearSeries();
		series.setLineColor(0xFF0099CC);
		series.setLineWidth(2);
		int i=0;
		for (byte b: waveform) {
			series.addPoint(new LinearPoint(i, (int)b));
			Log.d("SpeechJammer", i +" "+ b);
			i++;
			
		}

		// Add chart view data
		//chartView.addSeries(series);
		//chartView.setLeftLabelAdapter(new ValueLabelAdapter(this, LabelOrientation.VERTICAL));
		//chartView.setBottomLabelAdapter(new ValueLabelAdapter(this, LabelOrientation.HORIZONTAL));
	}
	
	private void flipCard() {
	    if (mShowingBack) {
	    	getSupportFragmentManager().popBackStack();
	    	mShowingBack = false;
	        return;
	    }

	    // Flip to the back.

	    mShowingBack = true;

	    // Create and commit a new fragment transaction that adds the fragment for the back of
	    // the card, uses custom animations, and is part of the fragment manager's back stack.

	    getSupportFragmentManager()
	            .beginTransaction()

	            // Replace the default fragment animations with animator resources representing
	            // rotations when switching to the back of the card, as well as animator
	            // resources representing rotations when flipping back to the front (e.g. when
	            // the system Back button is pressed).
	            .setCustomAnimations(
	                    R.anim.animation_enter, R.anim.animation_leave,
	                    R.anim.animation_enter, R.anim.animation_leave)

	            // Replace any fragments currently in the container view with a fragment
	            // representing the next page (indicated by the just-incremented currentPage
	            // variable).
	            .replace(R.id.container, new VisualizationFragment())

	            // Add this transaction to the back stack, allowing users to press Back
	            // to get to the front of the card.
	            .addToBackStack(null)

	            // Commit the transaction.
	            .commit();
	}
	
	@Override
	public void onBackPressed() {
		//Close recording when back is pressed
		if(started){
			MenuItem item = menu.findItem(R.id.toggle);
			onOptionsItemSelected(item);
		}
		if(!getSupportFragmentManager().popBackStackImmediate()){
			super.onBackPressed();
		}
	}
	
	public class HeadsetConnectionReceiver extends BroadcastReceiver {
		public boolean headsetConnected = false;
		 public void onReceive(Context context, Intent intent) {
			  if (intent.hasExtra("state")){
			   if (headsetConnected && intent.getIntExtra("state", 0) == 0){
				   headsetConnected = false;
				   if (MainActivity.started){
					   MenuItem item = menu.findItem(R.id.toggle);
					   onOptionsItemSelected(item);
				   }
			   }
			   else if (!headsetConnected && intent.getIntExtra("state", 0) == 1){
				   headsetConnected = true;
			   }

			  }
		 }
	}

}

