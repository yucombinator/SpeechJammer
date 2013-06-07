package com.icechen1.speechjammer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HeadsetConnectionReceiver extends BroadcastReceiver {
	public interface onAction {	
		void onPlug();
		void onUnPlug();
	}
	onAction mOnAction;
	HeadsetConnectionReceiver(onAction action){
		mOnAction = action;
	}
	public boolean headsetConnected = false;
	 public void onReceive(Context context, Intent intent) {
		  if (intent.hasExtra("state")){
		   if (headsetConnected && intent.getIntExtra("state", 0) == 0){
			   headsetConnected = false;
			   mOnAction.onUnPlug();
		   }
		   else if (!headsetConnected && intent.getIntExtra("state", 0) == 1){
			   headsetConnected = true;
			   mOnAction.onPlug();
		   }

		  }
	 }
}