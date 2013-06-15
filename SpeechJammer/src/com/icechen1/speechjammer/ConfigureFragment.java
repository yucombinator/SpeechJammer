package com.icechen1.speechjammer;

import com.actionbarsherlock.view.MenuItem;
import com.devspark.appmsg.AppMsg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ConfigureFragment extends Fragment implements HeadsetConnectionReceiver.onAction {
	TextView headphone_status;
	HeadsetConnectionReceiver mHeadsetConnectionReceiver;
	SeekBar mSeekbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	//GET PREFERENCES
    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
    	final Editor editor = pref.edit();
    	
    	//LOAD VIEW
    	View v = inflater.inflate(R.layout.configure_fragment, container, false);
	    mSeekbar = (SeekBar) v.findViewById(R.id.seekBar);
	    mSeekbar.setProgress(MainActivity.delay_time);
	    final TextView seekTime = (TextView) v.findViewById(R.id.current_delay);
	    seekTime.setText(MainActivity.delay_time+" ms");
	    mSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
	    {
	       public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	       {
	            seekTime.setText(progress + " ms");
	            MainActivity.delay_time = progress;
	            editor.putInt("delay_time", progress);              
	            editor.commit();
	       }

	      public void onStartTrackingTouch(SeekBar seekBar) {}

	      public void onStopTrackingTouch(SeekBar seekBar) {}
	    });
		//Set up headphone unplugged failsafe
	    mHeadsetConnectionReceiver = new HeadsetConnectionReceiver(this);
		getActivity().registerReceiver(mHeadsetConnectionReceiver, 
                new IntentFilter(Intent.ACTION_HEADSET_PLUG));
	    headphone_status = (TextView) v.findViewById(R.id.headphone_status);
	    
		AudioManager amanager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		if(!amanager.isBluetoothA2dpOn() && !amanager.isWiredHeadsetOn()){
			//Display a feedback warning
			   headphone_status.setText(getResources().getString (R.string.headphone_status2));
		}
        return v;
    }
    
    @Override
    public void onDestroyView(){
    	super.onDestroyView();
    	getActivity().unregisterReceiver(mHeadsetConnectionReceiver);
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	try{
    	invalidateSeekBar();
    	}catch(Exception e){
    		
    	}
    }
    @Override
    public void onStart(){
    	super.onStart();
		getActivity().registerReceiver(mHeadsetConnectionReceiver, 
                new IntentFilter(Intent.ACTION_HEADSET_PLUG));

    }

	@Override
	public void onPlug() {
		headphone_status.setText(getResources().getString (R.string.headphone_status1));
	}

	@Override
	public void onUnPlug() {

		headphone_status.setText(getResources().getString (R.string.headphone_status2));
	}
	
	public void invalidateSeekBar(){
		mSeekbar.setProgress(MainActivity.delay_time);
	}
    


}
