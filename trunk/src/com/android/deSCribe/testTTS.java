package com.android.deSCribe;

import com.google.tts.TTS;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;

public class testTTS  {
	private TTS myTts;
    /** Called when the activity is first created. */
    Context context;
    public void makeTTS(Context c) {
    	context=c;
        myTts = new TTS(c, ttsInitListener, true);
        myTts.shutdown();
    }
    
    private TTS.InitListener ttsInitListener = new TTS.InitListener() {
        public void onInit(int version) {
        	AudioManager a = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    		a.setStreamVolume(AudioManager.STREAM_MUSIC, a.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
    	
        	myTts.speak("Good morning take left", 0, null);
        }
      };
}