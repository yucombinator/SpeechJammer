package com.icechen1.speechjammer;

import org.codechimp.apprater.AppRater;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.appmsg.AppMsg;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
/*
 * SpeechJammer
 * Copyright (C) 2013 Yu Chen Hou
 */
public class MainActivity extends SherlockFragmentActivity implements HeadsetConnectionReceiver.onAction {
	static boolean started = false;
	boolean mShowingBack = false;
	Menu menu;
	static int delay_time;
	public static int AudioSessionID = 0;
	
	HeadsetConnectionReceiver mHeadsetConnectionReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		delay_time= PreferenceManager.getDefaultSharedPreferences(this).getInt("delay_time", 150);

		if(savedInstanceState!=null){
			started = savedInstanceState.getBoolean("mShowingBack");
			mShowingBack = savedInstanceState.getBoolean("started");
			
			if(started){
				
			}
			if (mShowingBack){
				
			}
		}
		
        if (savedInstanceState == null) {
        	getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new ConfigureFragment(),"frag_conf")
                    .commit();
        }
        
		AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if(!amanager.isBluetoothA2dpOn() && !amanager.isWiredHeadsetOn()){
			//Display a feedback warning
			AppMsg.makeText(this, getResources().getString (R.string.no_headphone_message), AppMsg.STYLE_ALERT).show();
		}
	    // Look up the AdView as a resource and load a request.
	    AdView adView = (AdView)this.findViewById(R.id.adView);
	    AdRequest request = new AdRequest();
	    adView.loadAd(request);
		AppRater.app_launched(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.menu, menu);
		if(started){
			menu.findItem(R.id.toggle).setTitle(getResources().getString (R.string.toggle_stop));
		}
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
	    	delay_time = 150;
            editor.putInt("delay_time", 150);              
            editor.commit();
            try{
            	//Try to change the seekbar value
            	ConfigureFragment fragment = (ConfigureFragment) getSupportFragmentManager().findFragmentByTag("frag_conf");
                fragment.invalidateSeekBar(); 
    			AppMsg.makeText(this, getResources().getString (R.string.restart_1), AppMsg.STYLE_INFO).show();
            }catch(Exception e){
    			AppMsg.makeText(this, getResources().getString (R.string.restart), AppMsg.STYLE_INFO).show();

            }

	    	return true;
	    case R.id.toggle:
	    	//Turn off or on
			if(started){
				item.setTitle(getResources().getString (R.string.toggle_start));
				mHeadsetConnectionReceiver = new HeadsetConnectionReceiver(this);
				registerReceiver(mHeadsetConnectionReceiver, 
		                new IntentFilter(Intent.ACTION_HEADSET_PLUG));
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
	protected void onPause() {
	    super.onPause();
	    try{
	    unregisterReceiver(mHeadsetConnectionReceiver);
	    }catch(Exception e){
	    	
	    }
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    if(started){
		registerReceiver(mHeadsetConnectionReceiver, 
                new IntentFilter(Intent.ACTION_HEADSET_PLUG));
	    }
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
	
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
       savedInstanceState.putBoolean("mShowingBack", mShowingBack);
       savedInstanceState.putBoolean("started", started);
       super.onSaveInstanceState(savedInstanceState);

     } 

	@Override
	public void onPlug() {
		
	}

	@Override
	public void onUnPlug() {
		   if (MainActivity.started){
			   MenuItem item = menu.findItem(R.id.toggle);
			   onOptionsItemSelected(item);
		   }
	}

}

