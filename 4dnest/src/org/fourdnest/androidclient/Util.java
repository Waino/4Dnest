package org.fourdnest.androidclient;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

/**
 * A class for static utility methods.
 * also @see org.fourdnest.androidclient.tools.GenericUtils
 */
public final class Util {
	/** Private constructor to prevent instantiation */
	private Util() { }
	
	/**
	 * Null-safe comparison between two objects.
	 * @param o1 One of the objects to compare
	 * @param o2 The other object to compare
	 * @return true if both objects are equal or both objects are null
	 */
	public static boolean objectsEqual(Object o1, Object o2) {
		if(o1 != null) {
			return o1.equals(o2);
		} else {
			return o2 == null; // One null, other not -> fail
		}
	}
	
	/**
	 * Checks whether the requested Service is already running
	 * @param ctx Application context
	 * @param serviceClass The class of the needed Service
	 * @return true if the Service is running, false otherwise
	 */
	public static boolean isServiceRunning(Context ctx, Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}
