package com.android.deSCribe;



import java.util.List;



import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Route extends MapActivity {
    /** Called when the activity is first created. */
	GeoPoint p;
	
	private Integer[] mImageIds = {
			R.drawable.tile_0_0,
			R.drawable.tile_0_1,
			R.drawable.tile_0_2,
			R.drawable.tile_1_0,
			R.drawable.tile_1_1,
			R.drawable.tile_1_2,
			R.drawable.tile_2_0,
			R.drawable.tile_2_1,
			R.drawable.tile_2_2,
			R.drawable.tile_3_0,
			R.drawable.tile_3_1,
			R.drawable.tile_3_2
		
	};
	long systemTime=0L;
	//double []mValues;
	List<Overlay> mapOverlays;
	Drawable drawable[]=new Drawable[mImageIds.length];
	HelloItemizedOverlay itemizedOverlay[]= new HelloItemizedOverlay[mImageIds.length];
	MapView mapView;
	MapController mc;
	GeoPoint currentForMap;
	GeoPoint nextTargetForMap;
	private SensorManager mSensorManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_route);       
        
       
        mapView = (MapView) findViewById(R.id.mapview1);
       // mapView = (MapView) findViewById(R.id.mapview1);
		mc = mapView.getController();
		mc.setZoom(17);
		//mc.setCenter(new GeoPoint(34021335,-118288002));
		mapOverlays = mapView.getOverlays();
		// GeoPoint point= null;
		int latChng=-4535;
		int longChng=5481;
		int startLat=34027631;
		int startLong=-118292520;
		int currLat=startLat;
		int currLong=startLong;
		//    int awdq=;
		for(int i=0;i<mImageIds.length;i++)
			
		{
			drawable[i] = this.getResources().getDrawable(mImageIds[i]);
			itemizedOverlay[i] = new HelloItemizedOverlay(drawable[i]);

			//GeoPoint point = new GeoPoint(19240000,-99120000);
			if(i%3==0&&i>0)
			{
				currLat=startLat;
				currLong+=longChng;
			}
			//	if(i==3)
			{
				GeoPoint point = new GeoPoint(currLat,currLong);

				OverlayItem overlayitem = new OverlayItem(point, "", "");
				itemizedOverlay[i].addOverlay(overlayitem);
				mapOverlays.add(itemizedOverlay[i]);
			}
			currLat+=latChng;
		}
		
		
		 mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
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

    @Override 
    protected boolean isRouteDisplayed() { 
            return false; 
    }    
    
    GeoPoint currentLocation=null;
    GeoPoint nextLocation=null;
	class MyLocationOverlay extends com.google.android.maps.Overlay implements android.view.View.OnClickListener {
		/**
		 * 
		 */
		
		GeoPoint start;
		GeoPoint next;
		Projection projection;
		public MyLocationOverlay(GeoPoint pnt,Projection pro) {
			// TODO Auto-generated constructor stub
			p=pnt;
			projection=pro;
		}
		public MyLocationOverlay(GeoPoint p1,GeoPoint p2,Projection pro) {
			// TODO Auto-generated constructor stub
			start=p1;
			next=p2;
			projection=pro;
		}
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
	//		if(p!=null)
			{
				super.draw(canvas, mapView, false);
				Paint paint = new Paint();    	
				Point myScreenCoords = new Point();
				
				mapView.getProjection().toPixels(currentLocation, myScreenCoords);
				paint.setStrokeWidth(1);
				paint.setARGB(255, 255, 255, 255);
				paint.setStyle(Paint.Style.STROKE);
				Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.mappin_red);
				canvas.drawBitmap(bmp, myScreenCoords.x-bmp.getWidth(), myScreenCoords.y-bmp.getHeight(), paint);
			if(nextLocation!=null)
			{
				mapView.getProjection().toPixels(nextLocation, myScreenCoords);
				paint.setStrokeWidth(1);
				paint.setARGB(255, 255, 255, 255);
				paint.setStyle(Paint.Style.STROKE);
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.mappin_blue);
				canvas.drawBitmap(bmp, myScreenCoords.x-bmp.getWidth(), myScreenCoords.y-bmp.getHeight(), paint);
			}
			}
		/*	else
			{
				//Double latitude = start.getLatitudeE6();
				//Double longitude = location.getLongitude()*1E6;
				//GeoPoint geopoint = new GeoPoint(start.getLatitudeE6(),longitude.intValue());
				//Projection projection = mapView.getProjection();
				Paint paint = new Paint();    	
				Point startPoint=new Point();
				Point endPoint=new Point();
				projection.toPixels(start,startPoint);
				projection.toPixels(start,endPoint);

				//GeoPoint geopoint = new GeoPoint(latitude.intValue(),longitude.intValue());
				canvas.drawLine(startPoint.x, startPoint.y,endPoint.x, endPoint.y, paint);
			}
*/			//paint.setARGB(255,0,0,0);
			//canvas.drawText("The picture was taken here..", myScreenCoords.x, myScreenCoords.y, paint);    	
			return true;

		}

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Toast.makeText(Route.this, "lol" , Toast.LENGTH_SHORT).show();
		}

	}
	
	private final SensorListener mListener = new SensorListener() 
	 {
	        public void onSensorChanged(int sensor, float[] values) 
	        {
	
	        	
	        	if(System.currentTimeMillis()-systemTime>2000)
				{	
					
	        		
	        		
	        		{
					
	        			updateMap();
	        		}
					systemTime = System.currentTimeMillis();
				}
	        	
	            
	        }

	        public void onAccuracyChanged(int sensor, int accuracy) {
	            // TODO Auto-generated method stub
	        }
	 };
	       
private void updateMap()
{
	if(Tabs.currentLocation!=null)
	{
	currentLocation =new GeoPoint((int)(Tabs.currentLocation.getLatitude()*1000000),(int)(Tabs.currentLocation.getLongitude()*1000000));
	mc.setCenter(currentLocation);
	mc.animateTo(currentLocation);
	//mc.setCenter();
	//Toast.makeText(Route.this, p.getLatitudeE6()+"",Toast.LENGTH_LONG).show();
	}
	if(Details.finalPath!=null)
	{
		double[] nextDest=getDetailsAboutId(Details.finalPath.elementAt(0));
		nextLocation=new GeoPoint((int)(nextDest[0]*1000000),(int)(nextDest[1]*1000000));
	}
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(currentLocation,mapView.getProjection());
        List<Overlay> list = mapView.getOverlays();
        list.add(myLocationOverlay);
        mapView.invalidate();	
}
@Override
protected void onResume()
{
    
    super.onResume();
	mSensorManager.registerListener(mListener, 
	SensorManager.SENSOR_ORIENTATION,
	SensorManager.SENSOR_DELAY_UI);
}
}