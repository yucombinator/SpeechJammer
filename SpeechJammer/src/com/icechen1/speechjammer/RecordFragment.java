package com.icechen1.speechjammer;

import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RecordFragment extends Fragment implements AudioBufferManager.BufferCallBack {
	AudioBufferManager audiosource;
	VisualizerView eqView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.chart_fragment, container, false);
		eqView = (VisualizerView) view.findViewById(R.id.eq_view);
		eqView.setType(VisualizerView.WAVE);
		//TODO FFT chart
	    //OPTION: Rate and colors
    	try{
    		audiosource.interrupt();
			audiosource = new AudioBufferManager(MainActivity.delay_time,this);
			audiosource.start();
		}catch(Exception e){
			audiosource = new AudioBufferManager(MainActivity.delay_time,this);
			audiosource.start();
		}
        return view;
    }
    
    @Override
    public void onDestroyView(){
    	super.onDestroyView();
		audiosource.interrupt();
    }

    @Override
    public void onBufferUpdate(int[] samp) {

    	eqView.updateVisualizer(samp);
		//fftView.updateVisualizer(samp);
	}
}
