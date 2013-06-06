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
	Visualizer visualizer;
	AudioBufferManager audiosource;
	VisualizerView eqView;
	VisualizerView fftView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.chart_fragment, container, false);
		eqView = (VisualizerView) view.findViewById(R.id.eq_view);
		fftView = (VisualizerView) view.findViewById(R.id.fft_view);
		eqView.setType(VisualizerView.WAVE);
		fftView.setType(VisualizerView.FFT);
		
		//chartView.setLeftLabelAdapter(new ValueLabelAdapter(this, LabelOrientation.VERTICAL));
		//chartView.setBottomLabelAdapter(new ValueLabelAdapter(this, LabelOrientation.HORIZONTAL));
		

		visualizer = new Visualizer(MainActivity.AudioSessionID);
	    visualizer.setEnabled(false);
	   // fftView.setFftSamples(10);
	    visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
	    visualizer.setDataCaptureListener(
	            new Visualizer.OnDataCaptureListener() {

	                public void onWaveFormDataCapture(Visualizer visualizer,
	                        byte[] bytes, int samplingRate) {
	                	//Log.d("SpeechJammer", "Received Wave " + bytes.length);
	                	eqView.updateVisualizer(bytes);
	                }

	                public void onFftDataCapture(Visualizer visualizer,
	                        byte[] bytes, int samplingRate) {
	                	//Log.d("SpeechJammer", "Received FFT " + bytes.length);
	                //	if(bytes)
	                	fftView.updateVisualizer(bytes);
	                }
	            }, Visualizer.getMaxCaptureRate(), true, true);
	    //OPTION: Rate and colors
	    visualizer.setEnabled(true);
        return view;
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	visualizer.setEnabled(false);
		audiosource.interrupt();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	
    	try{
			audiosource = new AudioBufferManager(MainActivity.delay_time,this);
			audiosource.start();
		}catch(Exception e){
			audiosource.interrupt();
			audiosource = new AudioBufferManager(MainActivity.delay_time,this);
			audiosource.start();
		}
    	
    	visualizer.setEnabled(true);

    }

    @Override
    public void onBufferUpdate(byte[] samp) {

    	eqView.updateVisualizer(samp);
		fftView.updateVisualizer(samp);
	}
}
