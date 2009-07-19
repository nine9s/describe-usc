package com.android.deSCribe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class deSCribe extends Activity implements View.OnClickListener {
	/** Called when the activity is first created. */
	public static SQLiteDatabase db;
	String DBNAME = "SCRoute";
	String fileN = "/sdcard/map_data__modified3";
	String fileN3 = "/sdcard/street_json2";
	String fileN4 = "/sdcard/street_json_detailed";
	String CREATE_TABLE1 = "create table if not exists "
			+ " Buildings (id integer primary key, "
			+ " latitude text not null, longitude text not null, "
			+ " campus text, photo text, name text, code text, short text, address text, "
			+ " type text, url text);";

	String CREATE_ROADS = "create table if not exists "
			+ " Roads (id integer primary key, "
			+ "latitude VARCHAR(200), longitude VARCHAR(200), campus VARCHAR(200), photo VARCHAR(200), name VARCHAR(200), "
			+ "code VARCHAR(200), short VARCHAR(1000), address VARCHAR(200), type VARCHAR(200), road_id VARCHAR(200), bldgs VARCHAR(500), url VARCHAR(200));";

	String CREATE_ROADS_DETAILS = "create table if not exists "
			+ " Roads_Details (id integer primary key, "
			+ "latitude text, longitude text, slope text, perpendicularslope text, x text, "
			+ "y text);";

	String TABLENAME1 = "Buildings";
	String TABLENAME3 = "Roads";
	String TABLENAME4 = "Roads_Details";
	String[] databases;
	ToneGenerator tn;
	// ProgressBar pBar;

	public static String user_pref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		user_pref = "Coarse"; // default value of user preference
		Button myButton = (Button) findViewById(R.id.go_button);
		myButton.setOnClickListener(this);
	}

	public void onStart() {
		super.onStart();
		CheckifBuildingsExists();
		CheckifRoadsExists();
		CheckifRoadsDetailsExists();
	
	}
	public String GetIdfromName(String name){
		String id = null;
		Cursor c;
		
		if(db != null){
			
			if(name.length()==3){
				name = name.toUpperCase();
				c = db.query(TABLENAME1, new String[] {"id"}, "code = '"+name+"'", null, null, null, null);
			}
			else{
				c = db.query(TABLENAME1, new String[] {"id"}, "name = '"+name+"'", null, null, null, null);
			}
			if(c.getCount()>0){
				c.moveToFirst();
				id = c.getString(0);
			}
		}

		return id;

	}
	
	
	public void CheckifBuildingsExists() {
		openDatabaseForRead();
		db.execSQL(CREATE_TABLE1);
		Cursor c = db.query(TABLENAME1, new String[] { "id" }, "id=25", null,
				null, null, null);
		if (c.getCount() == 0) {
			InsertIntoTable1();
		}

	}

	public void CheckifRoadsExists() {
		openDatabaseForRead();
		db.execSQL(CREATE_ROADS);
		Cursor c = db.query(TABLENAME3, new String[] { "id" }, "id=171", null,
				null, null, null);
		if (c.getCount() == 0) {
			InsertIntoTable3();
		}
	}

	public void CheckifRoadsDetailsExists() {
		openDatabaseForRead();
		db.execSQL(CREATE_ROADS_DETAILS);
		Cursor c = db.query(TABLENAME4, new String[] { "id" }, "id=171", null,
				null, null, null);
		if (c.getCount() == 0) {
			InsertIntoTable4();
		}
	}

	private void openDatabaseForRead() {
		try {
			db = openOrCreateDatabase(DBNAME, MODE_WORLD_WRITEABLE, null);
		} catch (Exception e) {
			Log.i("SCRoute", "Error opening DB or creating new DB. " + e);
		}
	}

	private void InsertIntoTable1() {

		Toast.makeText(this, "Installing Buildings database",
				Toast.LENGTH_SHORT).show();
		// pBar.setVisibility(ProgressBar.VISIBLE);
		String data = readFileData(fileN);

		JSONTokener jsontkn = new JSONTokener(data);

		try {

			JSONArray array = new JSONArray(jsontkn);

			// putting into database

			openDatabase();

			JSONObject obj1 = null;
			Integer rowCount = 0;
			for (int i = 0; i < array.length(); i++) {

				obj1 = (JSONObject) array.get(i);
				Integer id = Integer.parseInt((String) obj1.get("id"));
				String lat = (String) obj1.get("latitude");
				String lng = (String) obj1.get("longitude");
				String campus = (String) obj1.get("campus");
				String photo = (String) obj1.get("photo");
				String name = (String) obj1.get("name");
				String code = (String) obj1.get("code");
				String short1 = (String) obj1.get("short");
				String address = (String) obj1.get("address");
				String type = (String) obj1.get("type");
				String url = (String) obj1.get("url");

				if (insertRow(id, lat, lng, campus, photo, name, code, short1,
						address, type, url)) {
					rowCount++;
				}
			}

			Log.d("JSONParse", rowCount.toString());
		} catch (JSONException e) {
			Log.d("JSONParse", e.toString());
		}

	}

	public void InsertIntoTable3() {
		String data = readFileData(fileN3);
		JSONTokener jsontkn = new JSONTokener(data);
		try {
			JSONArray array = new JSONArray(jsontkn);
			// putting into database

			openDatabase();
			JSONObject obj1 = null;
			JSONObject obj2 = null;
			JSONArray distances;
			Integer rowCount = 0;
			String neighbors;
			String nDistances;
			for (int i = 0; i < array.length(); i++) {

				obj1 = (JSONObject) array.get(i);
				Integer id = Integer.parseInt((String) obj1.get("id"));
				String lat = (String) obj1.get("latitude");
				String lng = (String) obj1.get("longitude");
				String campus = (String) obj1.get("campus");
				String photo = (String) obj1.get("photo");
				String name = (String) obj1.get("name");
				String code = (String) obj1.get("code");
				String short1 = (String) obj1.get("short");
				String address = (String) obj1.get("address");
				String type = (String) obj1.get("type");
				String road_id = (String) obj1.get("road_id");
				String bldgs = (String) obj1.get("bldgs");
				String url = (String) obj1.get("url");

				if (insertRow3(id, lat, lng, campus, photo, name, code, short1,
						address, type, road_id, bldgs, url)) {
					rowCount++;
				}

			}

			Log.d("JSONParse", rowCount.toString());

			

		} catch (JSONException e) {
			Log.d("JSONParse", e.toString());
		}
		
	}

	public void InsertIntoTable4() {
		String data = readFileData(fileN4);
		JSONTokener jsontkn = new JSONTokener(data);
		try {
			JSONArray array = new JSONArray(jsontkn);
			// putting into database

			openDatabase();
			JSONObject obj1 = null;
			JSONObject obj2 = null;
			JSONArray distances;
			Integer rowCount = 0;
			String neighbors;
			String nDistances;
			for (int i = 0; i < array.length(); i++) {
				obj1 = (JSONObject) array.get(i);
				Integer id = Integer.parseInt((String) obj1.get("id"));
				String lat = (String) obj1.get("latitude");
				String lng = (String) obj1.get("longitude");
				String slope = (String) obj1.get("slope");
				String perpendicularslope = (String) obj1
						.get("perpendicularslope");

				String x = (String) obj1.get("x");
				String y = (String) obj1.get("y");

				if (insertRow4(id, lat, lng, slope, perpendicularslope, x, y)) {
					rowCount++;
				}

			}

			Log.d("JSONParse", rowCount.toString());

			

		} catch (JSONException e) {
			Log.d("JSONParse", e.toString());
		}
	
	}

	public String readFileData(String fileN) {
		String data = "";
		String str = "";
		try {

			FileReader fin = new FileReader(fileN);
			BufferedReader bf = new BufferedReader(fin);
			while ((str = bf.readLine()) != null) {
				data += str;
			}

			bf.close();
			// et.setText(data);

		} catch (IOException e) {
			// et.setText("No File");

		}
		return data;
	}

private void openDatabase() {
		try {
		db = openOrCreateDatabase(DBNAME, MODE_WORLD_WRITEABLE, null);
		db.execSQL(CREATE_ROADS);
		} catch (Exception e) {
			Log.i("SCRoute", "Error opening DB or creating new DB. " + e);
		}
	}

	private boolean insertRow(Integer id, String latitude, String longitude,
			String campus, String photo, String name, String code,
			String short1, String address, String type, String url) {
		Long ret = null;
		ContentValues values = new ContentValues();
		values.put("id", id);
		values.put("latitude", latitude);
		values.put("longitude", longitude);
		values.put("campus", campus);
		values.put("photo", photo);
		values.put("name", name);
		values.put("code", code);
		values.put("short", short1);
		values.put("address", address);
		values.put("type", type);
		values.put("url", url);
		if (db != null) {
			ret = db.insert(TABLENAME1, null, values);
		}
		if (ret > 0) {
			return true;
		} else {
			return false;
		}
	}
	private boolean insertRow3(Integer id, String latitude, String longitude,
			String campus, String photo, String name, String code,
			String short1, String address, String type, String road_id,
			String bldgs, String url) {
		Long ret = null;
		ContentValues values = new ContentValues();
		values.put("id", id);
		values.put("latitude",latitude );
		values.put("longitude", longitude );
		values.put("campus", "");
		values.put("photo", "");
		values.put("name", "");
		values.put("code", "");
		values.put("short", "");
		values.put("address", "");
		values.put("type", type);
		values.put("road_id",road_id);
		values.put("bldgs", "");
		values.put("url", "");
		if (db != null) {

			ret = db.insert(TABLENAME3, null, values);
		}
		if (ret > 0) {
			return true;
		} else {
			return false;
		}

	}

	private boolean insertRow4(Integer id, String lat, String lng,
			String slope, String perpendicularslope, String x, String y) {
		Long ret = null;
		ContentValues values = new ContentValues();
		values.put("id", id);
		values.put("latitude",  lat );
		values.put("longitude", lng );
		values.put("longitude", lng );
		values.put("slope", slope);
		values.put("perpendicularslope",  perpendicularslope );
		values.put("x", x);
		values.put("y", y);
		if (db != null) {

			ret = db.insert(TABLENAME4, null, values);
		}
		if (ret > 0) {
			return true;
		} else {
			return false;
		}

	}

	public void onClick(View arg0) {

		Intent myIntent = new Intent();
		myIntent.setClass(deSCribe.this, Tabs.class);
		startActivity(myIntent);
		finish();
	}
}