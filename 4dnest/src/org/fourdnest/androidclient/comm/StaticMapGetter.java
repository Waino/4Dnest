package org.fourdnest.androidclient.comm;

import org.fourdnest.androidclient.Egg;

/**
 * Interface that allows modularizing the map data source
 */
public interface StaticMapGetter {
	/**
	 * Retrieves static map image for a route egg
	 * @param egg The egg
	 * @return boolean value of result (Ok / not OK)
	 */
	boolean getStaticMap(Egg egg);
}
