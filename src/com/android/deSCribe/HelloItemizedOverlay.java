package com.android.deSCribe;
import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * 
 */

/**
 * @author dheeraj
 *
 */
public class HelloItemizedOverlay extends ItemizedOverlay {
	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#draw(android.graphics.Canvas, com.google.android.maps.MapView, boolean)
	 */
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// TODO Auto-generated method stub
	
		super.draw(canvas, mapView, false);
	}
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	/**
	 * @param defaultMarker
	 */
	public HelloItemizedOverlay(Drawable defaultMarker) {
		//super(defaultMarker);
			super(boundCenter(defaultMarker));
			
			// TODO Auto-generated constructor stub
	}
	public HelloItemizedOverlay(Drawable defaultMarker,boolean bottom) {
		//super(defaultMarker);
			super(boundCenterBottom(defaultMarker));
			
			// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */


	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#createItem(int)
	 */
	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		  return mOverlays.get(i);
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#size()
	 */
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mOverlays.size();
	}
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
}
