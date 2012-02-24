package org.fourdnest.androidclient.test.comm;

import org.fourdnest.androidclient.comm.CommUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.net.Uri;
import android.test.AndroidTestCase;

public class GetTest extends AndroidTestCase {
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
		boolean res = CommUtils.getNetFile(Uri.parse("http://users.tkk.fi/~aetolone/test.jpg"), Uri.parse("/sdcard/test.jpg"));
		assertTrue(res);
	}

}
