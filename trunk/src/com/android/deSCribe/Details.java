package com.android.deSCribe;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Details extends Activity {
	/** Called when the activity is first created. */
	EditText etEnd;
	public static TextView  tvPath;
	//Neha added start
	private SensorManager mSensorManager;
	private float[] mValues;
	Float finalOrientation;	
	Integer sensorCounter = 0;
	Integer sensorChangeCounter = 0;
	private LocationManager locationManager;
	private Criteria criteria;	
	Long systemTime  = 0L;
	//Location currlocation;
	//Neha added stop
	Button btnStart;
	ArrayList<RoadDetails> buildings;
	String TABLENAME1 = "Buildings";
	//double currentLat = 34.021466374397;
	//double currentLng = -118.2830625772;
	long lastUpdated=0;
	CoordinateConversion myConverter;
	
	//Neha added start
	 private final SensorListener mListener = new SensorListener() 
	 {
	        public void onSensorChanged(int sensor, float[] values) 
	        {
	
	        	mValues = values;
	        	if(System.currentTimeMillis()-systemTime>2000)
				{	
					//if(tTask!=null)
	        		if(finalPath!=null)
	        		//Log.d("few","yenti raa"+finalPath.size());
	        		{
					getDirections();
				       if (Config.LOGD) Log.d("compass changed", "sensorChanged (" +mValues[0]+ ")");
	        		}
					systemTime = System.currentTimeMillis();
				}
	        	
	            
	        }

	        public void onAccuracyChanged(int sensor, int accuracy) {
	            // TODO Auto-generated method stub
	        }
	 };
	       
   //Neha added stop
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_details);
		double currentLat = 0;
		double currentLng = 0;
		//currlocation=Tabs.currentLocation;
		//Neha added start
		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		//locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		myConverter= new CoordinateConversion();
		//deSCribe forDB = new deSCribe();
		etEnd = (EditText) findViewById(R.id.etEnd);
		btnStart = (Button) findViewById(R.id.btnPath);
		tvPath = (TextView) findViewById(R.id.tvPath);
		btnStart.setOnClickListener(btnPathClick);
		}

		View.OnClickListener testTTS = new View.OnClickListener() {

		public void onClick(View v) {
			Intent myIntent = new Intent();
			myIntent.setClass(Details.this, HelloWorldTTS.class);
			myIntent.putExtra("message", "Hello World");
			myIntent.putExtra("view","main_details" );
			startActivity(myIntent);
			finish();
		}
		};
		
	//neha added start
		@Override
	    protected void onResume()
	    {
	        
	        super.onResume();
    		mSensorManager.registerListener(mListener, 
    		SensorManager.SENSOR_ORIENTATION,
    		SensorManager.SENSOR_DELAY_UI);
	    }
	View.OnClickListener btnPathClick = new View.OnClickListener() {

		public void onClick(View v) {
			deSCribe forDB= new deSCribe();
			String endId = forDB.GetIdfromName(etEnd.getText().toString());
			if(endId == null){
				finalPath=new Vector<String>();
				tvPath.setText("End Location Name is incorrect");
				return;
			}
			Double diff = 0.001;
			String query = "";
			query = "";
			Cursor c = deSCribe.db.query("Roads_Details", new String[] { "id",
					"latitude", "longitude", "slope", "perpendicularslope",
					"x", "y" }, null, null, null, null, null);
			//	Toast.makeText(Details.this, "In Show" + c.getCount(),Toast.LENGTH_SHORT).show();
			if (c.getCount() > 0) {
				buildings = new ArrayList<RoadDetails>();
				c.moveToFirst();
				do 
				{
					RoadDetails l = new RoadDetails();
//					Toast.makeText(Details.this,c.getString(1) , Toast.LENGTH_SHORT).show();
					l.Lat = Double.parseDouble(c.getString(1));
					l.Lng = Double.parseDouble(c.getString(2));
					l.Id = c.getString(0);
					l.slope = Double.parseDouble(c.getString(3));
					l.perpendicularSlope= Double.parseDouble(c.getString(4));
					l.x = Double.parseDouble(c.getString(5));
					l.y= Double.parseDouble(c.getString(6));
					buildings.add(l);
					
				} while (c.moveToNext());
			} else {
				diff *= 5;
			}
			double lat=0,lng=0;
			 c = deSCribe.db.query(TABLENAME1, new String[] {"latitude","longitude"}, "id="+endId, null, null, null, null);
			if(c.getCount()>0){
				c.moveToFirst();
				lat = Double.parseDouble(c.getString(0));
				lng = Double.parseDouble(c.getString(1));
				
			}
			//tvPath.setText(lat+","+lng+".");
			
			getCloserPointOnRoad(lat,lng);	
			
		}
		
	};
	String startIdThis,endIdThis;
	void getCloserPointOnRoad(double lat,double lng)
	{
		double currentLat = 0;
		double currentLng=0;
		/*double lat=0,lng=0;
		Cursor c = deSCribe.db.query(TABLENAME1, new String[] {"latitude","longitude"}, "id="+id, null, null, null, null);
		if(c.getCount()>0){
			c.moveToFirst();
			lat = Double.parseDouble(c.getString(0));
			lng = Double.parseDouble(c.getString(1));
			
		}*/
		if (Tabs.currentLocation != null) {
			currentLat = Tabs.currentLocation.getLatitude();
			currentLng = Tabs.currentLocation.getLongitude();
		}
	//tvPath.setText(lat+":"+lng+"\n");
		
	CoordinateConversion converter = new CoordinateConversion();
	double[] myxy=	converter.latLon2UTM(lat, lng);
	double x2=myxy[0];
	double y2=myxy[1];
	double m2,c2;
	double []finalPoint=new double[2];
	int minID = 0;
	double x1,y1,m1,c1;
	double minD=100;
	for(int i=0;i<buildings.size();i++)
	{
	
		RoadDetails rd=buildings.get(i);
		double dist=converter.LatLngDistance(rd.Lat, rd.Lng, lat,lng);
		if(minD>dist)
			{
			minD=dist;
			minID=i;
			}
	}
	Log.d("found end id",minID+"");
	RoadDetails rd=buildings.get(minID);
		x1=rd.x;
		y1=rd.y;
		m1=rd.slope;
		m2=rd.perpendicularSlope;
		c1=converter.getC(x1, y1, m1);
		
		c2=converter.getC(x2, y2, m2);
		double pointOfintersectionX=(c2-c1)/(m1-m2);
		double pointOfintersectionY=(m1*c2-m2*c1)/(m1-m2);
			finalPoint=converter.utm2LatLon("11 S "+pointOfintersectionX+" "+pointOfintersectionY);
			tvPath.setText(tvPath.getText().toString()+finalPoint[0]+","+finalPoint[1]+","+rd.Id);	
			endIdThis=rd.Id;
		
		
	 myxy=converter.latLon2UTM(currentLat, currentLng);
	 x2=myxy[0];
	 y2=myxy[1];
	 
	double []startPoint=new double[2];
	 minD=100;
	for(int i=0;i<buildings.size();i++)
	{
		
		rd=buildings.get(i);
		double dist=converter.LatLngDistance(rd.Lat, rd.Lng, currentLat,currentLng);
		Log.d("calculating dist","calc dist to "+rd.Id+" is "+dist);
		if(minD>dist)
		{
			minD=dist;
			minID=i;
		}
	}
	rd=buildings.get(minID);
	Log.d("found startID id",minID+"");
	x1=rd.x;
	y1=rd.y;
	m1=rd.slope;
	m2=rd.perpendicularSlope;
	c1=converter.getC(x1, y1, m1);
	c2=converter.getC(x2, y2, m2);
	pointOfintersectionX=(c2-c1)/(m1-m2);
	pointOfintersectionY=(m1*c2-m2*c1)/(m1-m2);
		{
			startPoint=converter.utm2LatLon("11 S "+pointOfintersectionX+" "+pointOfintersectionY);
			tvPath.setText(tvPath.getText().toString()+startPoint[0]+","+startPoint[1]+","+rd.Id);	
			startIdThis=rd.Id;
		}
	
	startlat=startPoint[0];
	startlng=startPoint[1];
    endlat=finalPoint[0];
	endlng=finalPoint[1];
	startId = startIdThis;
	endId = endIdThis;
	Astar searchObj = new A_Search();
	Log.d("searching","startid: "+startIdThis+" end ID "+ endIdThis);
	
	finalPath=new Vector<String>();
	if(searchObj.Search(startId, endId))
	{
		path = searchObj.finalPath;
		Log.d("got a path",path);
		DisplayPath();
	}
	tobeReachedID=finalPath.firstElement();
	newDesLatandLong=getDetailsAboutId(tobeReachedID);
	//tTask= new Timer();
	//tTask.scheduleAtFixedRate(new giveDirections(), 0,2000);
	getDirections();
	}
//public static Timer tTask ;
public static double startlat;
public static double endlat;
public static double startlng;
public static double endlng;
public static String startId;
public static String endId;
public static String path;
public static Vector<String> finalPath=null;
String tobeReachedID="";
double []newDesLatandLong=new double[2];
public void getDirections() 
{
	//Log.d("in dire","WTF"+finalPath.size());
	double dist;
	if(finalPath.size()>0)
	{
	Log.d("WHILE", tobeReachedID);
	if((dist=myConverter.LatLngDistance(Tabs.currentLocation.getLatitude(), Tabs.currentLocation.getLongitude(), newDesLatandLong[0], newDesLatandLong[1]))<0.008)
	{
		
		finalPath.removeElementAt(0);
		tobeReachedID=finalPath.firstElement();
		Log.d("WHILE", "in if true"+dist+" "+finalPath.size());
		newDesLatandLong=getDetailsAboutId(tobeReachedID);
		tvPath.setText("new to be reached"+tobeReachedID);
	}
	else
	{
		if(tobeReachedID.length()==0)
			return;
		double destLat=newDesLatandLong[0];
		double destLong=newDesLatandLong[1];
		double []xy1=myConverter.latLon2UTM(destLat, destLong);
		double []xy2=myConverter.latLon2UTM(Tabs.currentLocation.getLatitude(), Tabs.currentLocation.getLongitude());
		int quad=0;
		int signx,signy;
		if(xy1[1]-xy2[1]>0)
		{
		signx=1;	
		}else
		signx=-1;
		if(xy1[0]-xy2[0]>0)
		{
		signy=1;	
		}else
		signy=-1;
		double angleToChange=getAngleToMove(newDesLatandLong);
		if(signx>0)
		{
		angleToChange=90-angleToChange;	
		}else
		angleToChange=270-angleToChange;
		double diffAngles=Math.abs((mValues[0]-angleToChange));
		String direction="";
		if((mValues[0]-angleToChange)>0)
		{
			if(diffAngles>22.5 &&diffAngles<67.5 )
				direction="left";
			else	if(diffAngles>67.5 &&diffAngles<112.5 )
				direction="downward";
			else if(diffAngles>112.5 &&diffAngles<167.5 )
				direction="right";
			else
				direction="striaght";
		}
		else
		{
			if(diffAngles>22.5 &&diffAngles<67.5 )
				direction="right";
			else if(diffAngles>67.5 &&diffAngles<112.5 )
				direction="downward";
			else if(diffAngles>112.5 &&diffAngles<167.5 )
				direction="left";
			else
				direction="striaght";
		}
		
		Log.d("WHILE", "in if false"+dist+" size:"+finalPath.size()+" angle to change from current compass"+angleToChange+"direction"+direction+"current compass:"+mValues[0]);
		tvPath.setText("trying to reach"+tobeReachedID+"still"+dist+"angle to change from compass"+angleToChange+" x: "+signx+" y: "+signy+direction);
	}
	}else
	{
		Log.d("WHILE", "cacelling timer"+" "+finalPath.size());
		tvPath.setText("reached destination");
		if(finalPath!=null)
		{
		finalPath.clear();
		finalPath=null;
		}
		//tTask=null;
	}
}

/**
 * @param newDesLatandLong
 */
private double getAngleToMove(double[] newDesLatandLong) {
	// TODO Auto-generated method stub
	double destLat=newDesLatandLong[0];
	double destLong=newDesLatandLong[1];
	double []xy1=myConverter.latLon2UTM(destLat, destLong);
	double []xy2=myConverter.latLon2UTM(Tabs.currentLocation.getLatitude(), Tabs.currentLocation.getLongitude());
	double angle=myConverter.getAngle(xy1[0],xy1[1],xy2[0],xy2[1]);
	return angle;
	/*if((xy2[1]-xy1[1])>=0)
	{
		return 90-angle; 
	}else
		return 270-angle;*/
}

/* (non-Javadoc)
 * @see java.util.TimerTask#run()
 */


	public void DisplayPath(){
	String path1 = path;
	StringTokenizer st = new StringTokenizer(path1," ");
	String namePath = "";
	String id="";
	if(finalPath==null)
		finalPath=new Vector<String>();
	Cursor c = null;
	double[] deslatlong=new double[2];
	while(st.hasMoreTokens()){
		if(namePath != ""){
			namePath += "->";
		}
		id = st.nextToken();
		finalPath.add(id);
		namePath+=id;
		
	}
 	tvPath.setText("Path:"+namePath);
	//return deslatlong;
}
public double[] getDetailsAboutId(String id)
{
	Cursor c;
	Log.d("in Display path","startid: "+id);
	c = deSCribe.db.query("Roads", new String[] {"latitude","longitude"}, "id="+id, null, null, null, null);
	double []deslatlong=new double[2];
	if(c.getCount()>0)
	{
		c.moveToFirst();
		deslatlong[0]=Double.parseDouble(c.getString(0));
		deslatlong[1]=Double.parseDouble(c.getString(1));
	}
return deslatlong;
}
}

class RoadDetails
{
	Double Lat;
	Double Lng;
	String Id;
	Double slope;
	Double perpendicularSlope;
	Double x;
	Double y;
}
	