package org.fourdnest.androidclient.test.mapview;

import org.fourdnest.androidclient.comm.OsmStaticMapGetter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.test.AndroidTestCase;


public class MapTest extends AndroidTestCase {
	@Before
	protected void setUp() throws Exception {
		super.setUp();
	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testGetMediaFile() throws Exception {
		boolean val = (new OsmStaticMapGetter().getStaticMap(null));
		assertTrue(val);
	}

}

