package org.fourdnest.androidclient.tools;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.fourdnest.androidclient.Egg;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Utility functions for processing of routes and other map data. 
 */
public class MapTools {
	private static final String TAG = "Maptools";
	private static final String FLOAT_TO_STRING_FORMAT = "%.6f";
	
	/**
	 * Generate a list of locations from egg's route file
	 * @param egg A route-type egg
	 * @return A list of locations describing the route
	 * @throws NumberFormatException, IOException
	 */
	public static List<String> getLocationListFromEgg(Egg egg) throws NumberFormatException, IOException  {
		List<String> locList = new ArrayList<String>();
		FileInputStream fstream = new FileInputStream(egg.getLocalFileURI().getEncodedPath());
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader buffRead = new BufferedReader(new InputStreamReader(in));
		String line;
		try {
			while ((line = buffRead.readLine()) != null) {
				JSONObject temp = new JSONObject(line);
				Float lat = Float.valueOf(temp.optString(LocationHelper.JSON_LATITUDE));
				Float lon = Float.valueOf(temp.optString(LocationHelper.JSON_LONGITUDE));
				locList.add(String.format(FLOAT_TO_STRING_FORMAT + "," + FLOAT_TO_STRING_FORMAT, lon, lat));			
			}
		} catch (JSONException e) {
			Log.d(TAG, "Could not convert location file line to json object");
		} finally {
			buffRead.close();
			in.close();
			fstream.close();
		}
		return locList;
	}
}
