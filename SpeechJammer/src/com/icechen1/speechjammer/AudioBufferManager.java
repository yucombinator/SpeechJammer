package com.icechen1.speechjammer;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class AudioBufferManager extends Thread{
	String LOG_TAG = "SpeechJammer";
	AudioRecord arecord;
	AudioTrack atrack;
	int SAMPLE_RATE;
	int buffersize;
	private boolean started = true;
	double delay;
	
	public AudioBufferManager(int time) {
		// TODO Auto-generated constructor stub
		delay=time;
	}

    @Override
    public void run() { 
           android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
           //AudioRecord recorder = null;
           
		// Prepare the AudioRecord & AudioTrack
		try {
			//Find the best supported sample rate
			for (int rate : new int[] {8000, 11025, 16000, 22050, 44100}) {  // add the rates you wish to check against
				int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_DEFAULT , AudioFormat.ENCODING_PCM_16BIT);
				if (bufferSize > 0) {
					// buffer size is valid, Sample rate supported
					SAMPLE_RATE = rate;
					buffersize = bufferSize;
					Log.i(LOG_TAG,"Recording sample rate:" + SAMPLE_RATE + " with buffer size:"+ buffersize);
				}
			}
			Log.i(LOG_TAG,"Initializing Audio Record and Audio Playing objects");
			Log.i(LOG_TAG,"Delay time is: " + delay + " ms");
			
			//Set up the recorder and the player
			arecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
					SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, buffersize * 1);

			atrack = new AudioTrack(AudioManager.STREAM_MUSIC,
					SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT, buffersize * 1,
					AudioTrack.MODE_STREAM);
			

			atrack.setPlaybackRate(SAMPLE_RATE);
		} catch (Throwable t) {
			Log.e(LOG_TAG, "Initializing Audio Record and Play objects Failed "+t.getLocalizedMessage());
		}

		//Create our buffers
        byte[] buffer  = new byte[buffersize];
        //A circular buffer
        CircularByteBuffer circBuffer = new CircularByteBuffer(SAMPLE_RATE*10);
        //Add an offset to the circular buffer
        final int emptySamples = (int)(SAMPLE_RATE * (delay/1000)); //ms to secs
        byte[] emptyBuf = new byte[emptySamples];
        Arrays.fill(emptyBuf, (byte)Byte.MIN_VALUE );
        try {
			circBuffer.getOutputStream().write(emptyBuf, 0, emptySamples);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
		arecord.startRecording();
        atrack.play();

        // start recording and playing back
        while(started) {
        	try {
        		//Write
        		arecord.read(buffer, 0, buffersize);
        		//Read the byte array data to the circular buffer
        		circBuffer.getOutputStream().write(buffer, 0, buffersize);
        		//Read the beginning of the circular buffer to the normal byte array until one sample rate of content
        		circBuffer.getInputStream().read(buffer, 0, buffersize);
        		//Play the byte array content
                atrack.write(buffer, 0, buffersize);
            } catch (Exception e) {
                    e.printStackTrace();
                    started = false;
            }finally{
            	//If the user has clicked stop
            	if(interrupted()){
                    started = false;
            	}
            }     

        }

        arecord.stop();
        arecord.release();
        atrack.stop();

		Log.i(LOG_TAG, "loopback exit");
		return;
	}
	public void close(){
		started = false;
		arecord.release();
	}
}
