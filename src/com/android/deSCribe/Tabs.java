package com.android.deSCribe;

import com.google.tts.TTS;



import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class Tabs extends TabActivity{
	TabHost myTabHost;
	private LocationManager locationManager;
    private Criteria criteria;
    public static Location currentLocation;
    private  TTS tts;
    private Tabs self;
    String msg;
	@Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // String msg="";
        Bundle extras=getIntent().getExtras();
       if(extras!=null)
        {
        msg=extras.getString("message");
        if(msg!=null)
        {
        	
        	//Toast.makeText(this, "lol"+msg, Toast.LENGTH_SHORT).show();
        	tts = new TTS(this, ttsInitListener, true);
        	//msg=null;
        }
        }TabHost tabs = getTabHost();
        TabSpec tabspec3 = tabs.newTabSpec("TAG3");		
		tabspec3.setIndicator("Get information");
		Intent myIntent3 = new Intent();
		myIntent3.setClass(this, compass.class);
		
		tabspec3.setContent(myIntent3);
		tabs.addTab(tabspec3);
		
        
		TabSpec tabspec = tabs.newTabSpec("TAG1");
		Intent myIntent = new Intent(this,Route.class);		
		tabspec.setContent(myIntent);
		tabspec.setIndicator("Map");
		tabs.addTab(tabspec);       

		TabSpec tabspec2 = tabs.newTabSpec("TAG2");		
		tabspec2.setIndicator("Route Details");
		Intent myIntent2 = new Intent();
		myIntent2.setClass(this, Details.class);
		tabspec2.setContent(myIntent2);
		tabs.addTab(tabspec2);		
         
		
		/*Neha .. This should be another tab for the camera code - image capture*/
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
   }
	public void onStart() {
	      super.onStart();
	      // Find a Location Provider to use.
	      String provider = locationManager.getBestProvider(criteria, true);
	      // Update the GUI with the last known position.
	      if(provider!=null)
	      {
	      Location location = locationManager.getLastKnownLocation(provider);
	      updateWithNewLocation(location);
	      // Register the LocationListener to listen for location changes
	      // using the provider found above.
	      locationManager.requestLocationUpdates(provider, 1000, 0, locationListener);
	      }else	
	      {
	    	  Toast.makeText(this, "Cannot find any providers to use to get Location Try again try switching on the GPS in setting" ,Toast.LENGTH_SHORT).show();  
	    	  
	      }
	    }
	protected void  onDestroy()  {
      super.onDestroy();
     
      locationManager.removeUpdates(locationListener);
      //Toast.makeText(Tabs.this, "Switching off" ,Toast.LENGTH_SHORT).show();
      super.onDestroy();
      if(tts!=null)
      tts.shutdown();
      System.gc();
  }
	
public void speakText(String message){
	tts.speak(message,0, null);
}
	
	
  private final LocationListener locationListener = new LocationListener() {
      public void onLocationChanged(Location location) {
        updateWithNewLocation(location);
      }

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			  updateWithNewLocation(null);	
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
  };
  
  private TTS.InitListener ttsInitListener = new TTS.InitListener() {
	    public void onInit(int version) {
	      //String pkgName = Tabs.class.getPackage().getName();
	    	AudioManager a = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    		a.setStreamVolume(AudioManager.STREAM_MUSIC, a.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
	    	tts.speak(msg, 0,null);
	     // tts.addSpeech("Nothing to delete", pkgName, R.raw.nothing_to_delete);

	     
	    }
	  };

  private void updateWithNewLocation(Location location) {
  	  // Update your current location
  	  currentLocation = location;

  	  // Refresh the ArrayList of contacts
     
  	  // Geocode your current location to find an address.
  	  String latLongString = "";
  	  String addressString = "No address found";
  	  
      if (location != null) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        latLongString = "Lat:" + lat + "\nLong:" + lng;

     
      } else {
        latLongString = "No location found";
      }

    // Toast.makeText(Tabs.this, "Your Current Position is:\n" + latLongString + "\n" + addressString,Toast.LENGTH_SHORT).show();
     }


}







