package org.fourdnest.androidclient.tools;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

/**
 * Helper class for managing Location-JSON conversion and equality
 */
public class LocationHelper {
	
	private static final String TAG = LocationHelper.class.getSimpleName();
	
	/**
	 * JSONification keywords
	 */
	private static final String JSON_ACCURACY = "accuracy";
	private static final String JSON_ALTITUDE = "altitude";
	private static final String JSON_BEARING = "bearing";
	public static final String JSON_LATITUDE = "latitude";
	public static final String JSON_LONGITUDE = "longitude";
	private static final String JSON_SPEED = "speed";
	private static final String JSON_TIME = "time";
	private static final String JSON_LOCATIONARRAY = "locations";
	
	/**
	 * Serializes given Location list to a JSON array
	 * @param locations List of Locations to serialize
	 * @return JSONObject representation of given list or null on failure
	 */
	public static JSONObject listToJSON(List<Location> locations) {
		try {
			// Create location array
			JSONArray arr = new JSONArray();
			for(Location loc : locations) {
				JSONObject json = LocationHelper.locationToJSON(loc);
				if(json != null) {
					arr.put(json);
				}
			}
					
			JSONObject wrapper = new JSONObject();
			wrapper.put(JSON_LOCATIONARRAY, arr);
			
			return wrapper;
		} catch(JSONException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	}	
	
	/**
	 * Converts a Location object to JSON object with relevant information
	 * @param loc Location to serialize
	 * @return JSON representation of Location or null on failure
	 */
	public static JSONObject locationToJSON(Location loc) {
		try {
	    	JSONObject json = new JSONObject();
	    	
			json.put(JSON_ACCURACY, loc.getAccuracy());
			json.put(JSON_ALTITUDE, loc.getAltitude());
			json.put(JSON_BEARING, loc.getBearing());
			json.put(JSON_LATITUDE, loc.getLatitude());
			json.put(JSON_LONGITUDE, loc.getLongitude());
			json.put(JSON_SPEED, loc.getSpeed());
			json.put(JSON_TIME, loc.getTime()); // TODO: Change to proper time stamp instead of system time
			
			return json;
		} catch(JSONException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
    }

	/**
	 * @param json object containing relevant location information
	 * @return Location object
	 * @throws JSONException
	 */
	public static Location locationFromJSON(JSONObject json) {
		Location loc = new Location("gps");
				
		try {
			if(!json.isNull(JSON_ACCURACY)) {
				loc.setAccuracy(Float.parseFloat(json.getString(JSON_ACCURACY)));
			}
			if(!json.isNull(JSON_ALTITUDE)) {
				loc.setAltitude(Double.parseDouble(json.getString(JSON_ALTITUDE)));
			}
			if(!json.isNull(JSON_BEARING)) {
				loc.setBearing(Float.parseFloat(json.getString(JSON_BEARING)));
			}
			if(!json.isNull(JSON_LATITUDE)) {
				loc.setLatitude(Double.parseDouble(json.getString(JSON_LATITUDE)));
			}
			if(!json.isNull(JSON_LONGITUDE)) {
				loc.setLongitude(Double.parseDouble(json.getString(JSON_LONGITUDE)));
			}
			if(!json.isNull(JSON_SPEED)) {
				loc.setSpeed(Float.parseFloat(json.getString(JSON_SPEED)));
			}
			if(!json.isNull(JSON_TIME)) {
				loc.setTime(json.getLong(JSON_TIME));
			}
		} catch(JSONException exc) {
			Log.e(TAG, "JSONException extracting Location from JSON: " + exc.getMessage());
		}
		
		return loc; 
	}
	
	/**
	 * Compares two locations for equality. They are equal if
	 * accuracy, altitude, bearing, latitude, longitude, speed and time
	 * are equal
	 * 
	 * @param loc1
	 * @param loc2
	 * @return the two are equal
	 */
	public static boolean locationsEqual(Location loc1, Location loc2) {
		if(loc1 == null || loc2 == null) return false;
		
		if(!(loc1.getAccuracy() == loc2.getAccuracy())) return false;
		if(!(loc1.getAltitude() == loc2.getAltitude())) return false;
		if(!(loc1.getBearing() == loc2.getBearing())) return false;
		if(!(loc1.getLatitude() == loc2.getLatitude())) return false;
		if(!(loc1.getLongitude() == loc2.getLongitude())) return false;
		if(!(loc1.getSpeed() == loc2.getSpeed())) return false;
		if(!(loc1.getTime() == loc2.getTime())) return false;
		 
		return true;
	}
}
