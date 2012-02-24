package org.fourdnest.androidclient.test.tools;

import java.util.Random;

import org.fourdnest.androidclient.tools.LocationHelper;
import org.json.JSONObject;

import android.location.Location;
import android.test.AndroidTestCase;

public class LocationHelperTest extends AndroidTestCase {

	
	public void testLocationJSON() {
		
		try {
			
			Location loc1 = this.getDummyLocation(1);			
			
			// Convert to JSON
			JSONObject o1 = LocationHelper.locationToJSON(loc1);
			
			// Convert back to Location
			Location loc2 = LocationHelper.locationFromJSON(o1);
			assertTrue(LocationHelper.locationsEqual(loc1, loc2));
			
			// Convert new location to JSON again, compare string representations
			JSONObject o2 = LocationHelper.locationToJSON(loc2);
			assertTrue(o1.toString().equals(o2.toString()));
		
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Generates a dummy location with given seed
	 * @param seed
	 * @return Location
	 */
	private Location getDummyLocation(long seed) {
		Location loc = new Location("gps");
		Random r = new Random(seed);
		
		loc.setAccuracy(r.nextFloat());
		loc.setAltitude(r.nextDouble());
		loc.setBearing(r.nextFloat());
		loc.setLatitude(r.nextDouble());
		loc.setLongitude(r.nextDouble());
		loc.setSpeed(r.nextFloat());
		loc.setTime(r.nextLong());
		
		return loc;
	}
	
}
