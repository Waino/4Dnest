package org.fourdnest.androidclient.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.fourdnest.androidclient.Nest;
import org.fourdnest.androidclient.NestManager;
import org.fourdnest.androidclient.comm.ProtocolFactory;
import org.fourdnest.androidclient.comm.UnknownProtocolException;

import android.test.AndroidTestCase;

public class NestManagerTest extends AndroidTestCase {
	
	NestManager nestManager;

	protected void setUp() throws Exception {
		super.setUp();
		
		this.nestManager = new NestManager(this.getContext());
		
		// Delete all saved nests
		this.nestManager.deleteAllNests();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testListNests() {
		try {
			Nest nest1 = new Nest(1, "Home Nest", "Nest hosted in my home server", new URI("http://127.0.0.1"), ProtocolFactory.PROTOCOL_4DNEST, "testuser", "secretkey");
			Nest nest2 = new Nest(2, "Another nest", "Some random nest", new URI("http://10.0.0.1"), ProtocolFactory.PROTOCOL_4DNEST, "testuser", "secretkey");
			
			this.nestManager.saveNest(nest1);
			this.nestManager.saveNest(nest2);
			
			List<Nest> nests = this.nestManager.listNests();
			
			// 2 nests should be saves
			assertEquals(2, nests.size());
					
			// Nest1 should be returned as #2, result set is  ordered by name
			assertTrue(nest1.equals(nests.get(1)));
			assertTrue(nest2.equals(nests.get(0)));
		
		} catch(URISyntaxException e) {
			fail("URISyntaxException");
		} catch(UnknownProtocolException upe) {
			fail("UnknownProtocolException");
		}
		
	}

	public void testSaveAndGetNest() {
		try {
			Nest nest = new Nest(1, "Home Nest", "Nest hosted in my home server", new URI("http://127.0.0.1"), ProtocolFactory.PROTOCOL_4DNEST, "testuser", "secretkey");

			this.nestManager.saveNest(nest);

			Nest resultNest = this.nestManager.getNest(1);
			// Check that same Nest is returned
			assertTrue(nest.equals(resultNest));

			// Check that some other id does not return equal nest
			assertFalse(nest.equals(this.nestManager.getNest(2)));
		} catch(URISyntaxException e) {
			fail("URISyntaxException");
		} catch(UnknownProtocolException upe) {
			fail("UnknownProtocolException");
		}
	}
	
	public void testDeleteNest() {
		try {
			Nest nest = new Nest(1, "Home Nest", "Nest hosted in my home server", new URI("http://127.0.0.1"), ProtocolFactory.PROTOCOL_4DNEST, "testuser", "secretkey");
			this.nestManager.saveNest(nest);
			
			// Check that 1 nest is deleted
			int result = this.nestManager.deleteNest(1);
			assertEquals(result, 1);
			
			// Check that nest with that id no longer exists
			Nest n = this.nestManager.getNest(1);
			assertNull(n);
			
		} catch(URISyntaxException e) {
			fail("URISyntaxException");
		} catch(UnknownProtocolException upe) {
			fail("UnknownProtocolException");
		}
	}
	
	public void testDeleteAllNests() {
		try {
			Nest nest1 = new Nest(1, "Home Nest", "Nest hosted in my home server", new URI("http://127.0.0.1"), ProtocolFactory.PROTOCOL_4DNEST, "testuser", "secretkey");
			Nest nest2 = new Nest(2, "Another nest", "Some random nest", new URI("http://10.0.0.1"), ProtocolFactory.PROTOCOL_4DNEST, "testuser", "secretkey");
			
			this.nestManager.saveNest(nest1);
			this.nestManager.saveNest(nest2);
			
			this.nestManager.deleteAllNests();
			
			assertNull(this.nestManager.getNest(1));
			assertNull(this.nestManager.getNest(2));
			
			assertEquals(this.nestManager.listNests().size(), 0);
			
		} catch(URISyntaxException e) {
			fail("URISyntaxException");
		} catch(UnknownProtocolException upe) {
			fail("UnknownProtocolException");
		}
		
	}

}
