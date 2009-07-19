package com.android.deSCribe;
import com.google.tts.ConfigurationManager;
import com.google.tts.TTS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Config;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
public class compass extends Activity {
    /** Called when the activity is first created.*/
	Button btnInfo;
	//public static boolean GPSDATAUPDATED=false;
	ArrayList<Loc> buildings;
	private LocationManager locationManager;
	private Criteria criteria;
	//public static Location currentLocation;
    private static final String TAG = "Compass";
    String TABLENAME1 = "Buildings";
	private SensorManager mSensorManager;
	private TTS myTts;
	Menu menuSuper;
    private static  float[] mValues;

    private final SensorListener mListener = new SensorListener() {
        public void onSensorChanged(int sensor, float[] values) {
          //  if (Config.LOGD) Log.d(TAG, "sensorChanged (" + values[0] + ", " + values[1] + ", " + values[2] + ")");
            mValues = values;
            
        }

        public void onAccuracyChanged(int sensor, int accuracy) {
            // TODO Auto-generated method stub

        }
    };

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        setContentView(R.layout.main_compass);
        btnInfo = (Button)findViewById(R.id.info_button);
		btnInfo.setOnClickListener(btnInfoClick);
		//
    }	
	 
	/*private TTS.InitListener ttsInitListener = new TTS.InitListener() {
        public void onInit(int version) {
        	AudioManager a = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    		a.setStreamVolume(AudioManager.STREAM_MUSIC, a.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
    		myTts.speak("Good morning take left", 0, null);
        }
      };*/
	  /*private TTS.InitListener ttsInitListener = new TTS.InitListener() {
		    public void onInit(int version) {
		    	String pkgName = compass.class.getPackage().getName();

		      myTts.addSpeech("Nothing to delete", pkgName, R.raw.nothing_to_delete);

		    }
		  };
*/
	View.OnClickListener btnInfoClick = new View.OnClickListener(){

		public void onClick(View v){
			double currentLat=0;
			double currentLng=0;
			currentLat=34.021466374397;
			currentLng=-118.2830625772;
			if(Tabs.currentLocation!=null)
			{
			currentLat=Tabs.currentLocation.getLatitude();
			currentLng=Tabs.currentLocation.getLongitude();
			}
			
			if(currentLat == 0){
				//Toast.makeText(this, "Please wait for GPS to get current location", Toast.LENGTH_SHORT).show();
				return;				
			}
			buildings = new ArrayList<Loc>();
			Double diff = 0.001;
			String query = "";			
				query = "abs(latitude - "+currentLat+") <= "+diff+" AND abs(longitude - "+currentLng+") <= "+diff +" AND (type = 'Libraries' OR type = 'Building' OR type='Residential' OR type='Athletics')"; 
				Cursor c = deSCribe.db.query(TABLENAME1, new String[] {"id","latitude","longitude","name","code"}, query,null, null, null, null);
				
				if(c.getCount()>0){
					c.moveToFirst();
					Loc l = new Loc();
					l.Lat = Double.parseDouble(c.getString(1));
					l.Lng = Double.parseDouble(c.getString(2));
					l.Id = c.getString(0);
					l.name = c.getString(3);
					l.code = c.getString(4);
					buildings.add(l);
					getAllInfo(currentLat,currentLng,l);
					while(c.moveToNext()){
						l = new Loc();
						l.Lat = Double.parseDouble(c.getString(1));
						l.Lng = Double.parseDouble(c.getString(2));
						l.Id = c.getString(0);
						l.name = c.getString(3);
						l.code = c.getString(4);
						buildings.add(l);
						getAllInfo(currentLat,currentLng,l);
						
					}

				}
				else{
					diff *= 5;					
				}				
				String str="";				
				assignAngles();
			
		}


	};
	
	void getAllInfo(double currLatitude, double currLongitude, Loc loc)
	{
		double angle=0;
	
		CoordinateConversion c = new CoordinateConversion();
	double d=c.LatLngDistance(currLatitude,currLongitude,loc.Lat,loc.Lng);	
	double[] temp1=c.latLon2UTM(currLatitude,currLongitude);
	double[] temp2=c.latLon2UTM(loc.Lat,loc.Lng);
	loc.distance=d;
	if(d<0.02)
		 angle=160;
	if(d>0.02 && d<0.03)
		 angle=100;
	if(d>0.03&&d<0.04)
		 angle=80;
	 if(d>0.04&&d<0.05)
		 angle=60;
	 if(d>0.05&&d<0.06)
		 angle=40;
	 if(d>0.06&&d<0.07)
		 angle=20;
	 if(d>0.07)
		 angle=0;
	double currAngles=c.getAngle(temp1[0], temp1[1],temp2[0], temp2[1]);
	loc.startAngle=currAngles-angle/2;
	loc.endAngle=currAngles+angle/2;
	if(temp2[0]-temp1[0]>0)
		loc.sign=1;
	else
		loc.sign=-1;
	 if (Config.LOGD) Log.d(TAG, "sensorChanged (" +loc.startAngle  + ", " + loc.endAngle + ", " + loc.name + "sign"+loc.sign+" x: "+""+temp2[1]+" x: "+""+temp1[1]+")");
	}
public void assignAngles(){
CoordinateConversion c = new CoordinateConversion();
Loc loc = new Loc();
boolean foundBuildingInfo=false;
MovieComparator comparator = new MovieComparator();
Collections.sort(buildings, comparator);

for(int i=0;i<buildings.size();i++)
{
	loc=buildings.get(i);
	//tvInfo.setText("lol"+loc.startAngle);
	if(loc.sign>0)
	{
		loc.startAngle=90-loc.startAngle;
		loc.endAngle=90-loc.endAngle;
	}else
		{
		loc.startAngle=270-loc.startAngle;
		loc.endAngle=270-loc.endAngle;
		}
		
	if(loc.startAngle>mValues[0]&&loc.endAngle<mValues[0])
		{
		//tvInfo.setText(loc.name);
		//Toast.makeText(compass.this, loc.name, Toast.LENGTH_LONG).show();
		display_info(loc.name,loc.code);
		foundBuildingInfo=true;
		break;
		}
			
}
if(!foundBuildingInfo)
{
 String text = "I am shortsighted. Could you move a little closer please";
 Toast.makeText(compass.this,text,Toast.LENGTH_LONG).show();
 speakText(text);	
 /*Intent myIntent = new Intent();
	myIntent.setClass(compass.this, HelloWorldTTS.class);
	myIntent.putExtra("message", text);
	startActivity(myIntent);
	finish();
	*/
}
}
    @Override
    protected void onResume()
    {
        if (Config.LOGD) Log.d(TAG, "onResume");
        super.onResume();
        		mSensorManager.registerListener(mListener, 
        		SensorManager.SENSOR_ORIENTATION,
        		SensorManager.SENSOR_DELAY_UI);
    }
    /*void makeSpeechFromText(String s){
    	myTts = new TTS(this, ttsInitListener, true);
    	myTts.shutdown();
    }*/
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuSuper = menu;
		menu.add(0, 0, 0, "Detail Level");		
		return true;
	}
	private void speakText(String text){
		Intent myIntent = new Intent();
		myIntent.setClass(compass.this, Tabs.class);
		myIntent.putExtra("message", text);
		startActivity(myIntent);
		finish();
    	
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			// set up radio buttons, set default to fine			
			Intent myIntent = new Intent();
			myIntent.setClass(compass.this, Radio.class);
			startActivity(myIntent);
			finish();
			return true;		
		}
		return false;
	}
	 private void display_info(String name,String code) {
	     
	    String text;
	    text = "You are pointing to " + name;
	    if(deSCribe.user_pref == "Coarse")
	    {
	    Toast.makeText(compass.this,text ,Toast.LENGTH_LONG).show();
	    speakText(text);
	        //myTts.speak(text, 0, null);
	    	/*Intent myIntent = new Intent();
			myIntent.setClass(compass.this, HelloWorldTTS.class);
			myIntent.putExtra("message", text);
			
			startActivity(myIntent);
			
			finish();
	    	*/
	    	return;
	    }
	    //if reached here means you want "fine" details	
	    String query = "";			
		query = "code = '"+code+"' and  name ='"+name+"'"; 
		Cursor c = deSCribe.db.query(TABLENAME1, new String[] {"short"}, query,null, null, null, null);
		if(c.getCount()>0){
			c.moveToFirst();
			String description = c.getString(0);
			text += description;			
		}	
		String query1 = "";			
		query1 = "code ='"+code+"' and name !='"+name+"'"; 
		Cursor results = deSCribe.db.query(TABLENAME1, new String[] {"name"}, query1,null, null, null, null);
		if(results.getCount()>0){
			text += "Also present in the building are";
			results.moveToFirst();
			String bldg_name = c.getString(0);
			text += bldg_name;
			do{
				String neigh_name = c.getString(0);
				text += neigh_name;	
				
			}while(results.moveToNext());

		}

	     Toast.makeText(compass.this,text,5/*Toast.LENGTH_LONG*/).show();
	     speakText(text);
	     /*Intent myIntent = new Intent();
		myIntent.setClass(compass.this, HelloWorldTTS.class);
		myIntent.putExtra("message", text);
		
		startActivity(myIntent);
		finish();
		*/
	      //myTts.speak(text, 0, null);
  
	}

}

class Loc{
	Double Lat;
	Double Lng;
	String Id;
	String name;
	String type;
	Double startAngle;
	Double endAngle;
	Double distance;
	Integer sign;
	String code;
}
	




class MovieComparator implements Comparator<Loc>{
	@Override
	public int compare(Loc object1, Loc object2) {
		// TODO Auto-generated method stub
		double rank1 = object1.distance;
        double rank2 = object2.distance;

        if (rank1 > rank2){
            return +1;
        }else if (rank1 < rank2){
            return -1;
        }else{
            return 0;
        }
	}
}
	
