package com.icechen1.speechjammer;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.appmsg.AppMsg;
import com.icechen1.speechjammer.ConfigureFragment.HeadsetConnectionReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
/*
 * SpeechJammer
 * Copyright (C) 2013 Yu Chen Hou
 */
public class MainActivity extends SherlockFragmentActivity {
	static boolean started = false;
	boolean mShowingBack = false;
	Menu menu;
	static int delay_time;
	public static int AudioSessionID = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//TODO Toggle
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		delay_time= PreferenceManager.getDefaultSharedPreferences(this).getInt("delay_time", 150);

		
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
	    case R.id.defaults:
	    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
	    	final Editor editor = pref.edit();
            editor.putInt("delay_time", 150);              
            editor.commit();
			AppMsg.makeText(this, getResources().getString (R.string.restart), AppMsg.STYLE_INFO).show();

	    	return true;
	    case R.id.toggle:
	    	//Turn off or on
			if(started){
				item.setTitle(getResources().getString (R.string.toggle_start));
				started = false;
				flipCard();
				return true;
			}else{
				item.setTitle(getResources().getString (R.string.toggle_stop));
				flipCard();
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
	                    R.anim.animation_leave_2, R.anim.animation_enter_2)

	            // Replace any fragments currently in the container view with a fragment
	            // representing the next page (indicated by the just-incremented currentPage
	            // variable).
	            .replace(R.id.container, new RecordFragment())

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

