package org.fourdnest.androidclient.comm;

import java.util.ArrayList;
import java.util.List;

import org.fourdnest.androidclient.Egg;
import org.fourdnest.androidclient.ThumbnailManager;
import org.fourdnest.androidclient.tools.MapTools;

import android.net.Uri;
import android.util.Log;

/**
 * Class for retrieving static map images from OpenStreetMap.
 */
public class OsmStaticMapGetter implements StaticMapGetter {
	private static final String TAG = "StaticMapGetter";
	private static final String BASEURI = "http://pafciu17.dev.openstreetmap.org/?module=map";
	private static final String JPG_TYPE = "imgType=jpg";
	private static final int DEFAULT_SIZE = 600;
	private static final float MARGIN = 0.0001f;
	private static final String FLOAT_TO_STRING_FORMAT = "%.6f";

	
	/**
	 * Retrieves static map image for a route egg using OSM static maps API.
	 * 
	 * @param egg Egg that has a route file as local file
	 * @return boolean value of result (Ok / not OK)
	 */
	public boolean getStaticMap(Egg egg) {
		String internalUriString = ThumbnailManager.getThumbnailUriString(egg, null); // size not needed
		Log.d(TAG, internalUriString);
		List<String> list = new ArrayList<String>();
		try {
			list = MapTools.getLocationListFromEgg(egg);
		} catch (Exception e) {
			Log.d(TAG, "Failed to produce location list from location file");
			return false;
		}
		String uriString = BASEURI;
		uriString = setBoundBox(uriString, list);
		uriString = addPath(uriString, list);
		uriString = setHeight(uriString, DEFAULT_SIZE);
		uriString = setWidth(uriString, DEFAULT_SIZE);
		
		uriString = uriString + JPG_TYPE;
		Log.d("thumbs", uriString);
		boolean val = CommUtils.getNetFile(Uri.parse(uriString), Uri.parse(internalUriString));
		Log.d("map_val", String.valueOf(val));
		return val;
	}
	/**
	 * Set map limits (bound box)
	 */
	private String setBoundBox(String uriString, List<String> list) {
		
		Float leftBoundLongitude = Float.POSITIVE_INFINITY;
		Float topBoundLatitude = Float.NEGATIVE_INFINITY;
		Float rightBoundLongitude = Float.NEGATIVE_INFINITY;
		Float lowBoundLatitude = Float.POSITIVE_INFINITY;
		Float lat;
		Float lon;
		String[] temp;
		
		// find limits
		for (String locString : list) {
			temp = locString.split(",");
			lat = Float.valueOf(temp[1]);
			lon = Float.valueOf(temp[0]);
			if (lon < leftBoundLongitude) {
				leftBoundLongitude = lon;
			}
			if (lon > rightBoundLongitude) {
				rightBoundLongitude = lon;
			}
			if (lat < lowBoundLatitude) {
				lowBoundLatitude = lat;
			}
			if (lat > topBoundLatitude) {
				topBoundLatitude = lat;
			}
		}
		// add margins, static at the moment
		topBoundLatitude = topBoundLatitude + MARGIN;
		lowBoundLatitude = lowBoundLatitude - MARGIN;
		leftBoundLongitude = leftBoundLongitude - MARGIN;
		rightBoundLongitude = rightBoundLongitude + MARGIN;
		
		// generate request attribute
		String attribute = "&bbox="
		 + String.format(FLOAT_TO_STRING_FORMAT, leftBoundLongitude)
		 + "," + String.format(FLOAT_TO_STRING_FORMAT, topBoundLatitude)
		 + "," + String.format(FLOAT_TO_STRING_FORMAT, rightBoundLongitude)
		 + "," + String.format(FLOAT_TO_STRING_FORMAT, lowBoundLatitude);
		
		return uriString + attribute;
	}
	
	/*
	 * Add path information from list of locations to request string
	 */
	private String addPath(String uriString, List<String> list) {
	    StringBuilder strb = new StringBuilder();
	    String attribute = "&paths=";
	    String separator = ",";
	    uriString = uriString + attribute;
	    strb.append(uriString);
	    for (String location : list) {
	    strb.append(location + separator); // there can be a , in the end, too
	    }
	    return strb.toString();
	    }
	
	private String setWidth(String uriString, int width) {
		return uriString + "&width=" + width;
	}
	
	private String setHeight(String uriString, int height) {
		return uriString + "&height=" + height;
	}
	
}
