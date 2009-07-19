package com.android.deSCribe;

import com.google.tts.TTS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.Toast;

public class HelloWorldTTS extends Activity {
	private TTS myTts;
	String message;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras=getIntent().getExtras();
        String messag=extras.getString("message");
        String view=extras.getString("view");
        message=messag;
       // Toast.makeText(this, "see"+message, Toast.LENGTH_SHORT).show();
        //setContentView(getResources().getIdentifier("R.layout."+view, null,null));
        setContentView(R.layout.main_compass);
        if(myTts!=null)
        	myTts.shutdown();
        myTts = new TTS(this, ttsInitListener, true);
        //myTts.shutdown();
        Intent myIntent = new Intent();
		myIntent.setClass(HelloWorldTTS.this, Tabs.class);
		startActivity(myIntent);
		finish();
    }
    
    private TTS.InitListener ttsInitListener = new TTS.InitListener() {
        public void onInit(int version) {
        	AudioManager a = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    		a.setStreamVolume(AudioManager.STREAM_MUSIC, a.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
    		myTts.speak(message, 0, null);
        }
      };
}