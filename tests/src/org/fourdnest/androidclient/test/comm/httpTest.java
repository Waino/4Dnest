package org.fourdnest.androidclient.test.comm;

import org.fourdnest.androidclient.Egg;
import org.fourdnest.androidclient.Nest;
import org.fourdnest.androidclient.Tag;
import org.fourdnest.androidclient.comm.*;
import org.fourdnest.androidclient.test.comm.MemoryCardInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;





public class httpTest extends AndroidTestCase {

	@Before
	protected void setUp() throws Exception {
		super.setUp();
	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}
/*
	@Test
	public void testHttpGet() throws Exception {
		
		FourDNestProtocol protocol = new FourDNestProtocol();
		String get = protocol.getTest();
		assertTrue(get.length() != 0);
		Log.v("httpget", get);
	}
*/	
	@Test
	public void testHttpPost() throws Exception {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		tags.add(new Tag("Programming"));
		tags.add(new Tag("Video Games"));
		MemoryCardInitializer.initialize(this.getContext());
		Uri uri = Uri.parse("/sdcard/test.jpg");
		Log.v("Path", uri.getPath());


		Egg egg = new Egg(5, 10, "Meitsi", uri, null, null, "tägei tulee", tags, 100);
		Log.d("EGGTYPE", egg.getMimeType().toString());

        /* SELECT to use local or web server */

		Nest nest = new Nest(007, "testNest", "testNest", new URI("http://test42.4dnest.org/"), ProtocolFactory.PROTOCOL_4DNEST, "testuser", "secretkey");
		//Nest nest = new Nest(007, "testNest", "testNest", new URI("http://10.0.2.2:8000/"), ProtocolFactory.PROTOCOL_4DNEST, "testuser", "secretkey");
		Protocol protocol = nest.getProtocol();

		protocol.setNest(nest);
		ProtocolResult protResult = protocol.sendEgg(egg);
		String post = String.valueOf(protResult.getStatusCode()) + " "; 
		if (protResult.getUrl() != null) {
		    post += protResult.getUrl();
		}
	    Log.v("httppost", post);
		assertTrue(protResult.getStatusCode() == ProtocolResult.RESOURCE_UPLOADED);

	}
	
	@Test

	public void testOverwriteEgg() throws Exception {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		tags.add(new Tag("Programming"));
		tags.add(new Tag("Video Games"));
		MemoryCardInitializer.initialize(this.getContext());
		Uri uri = Uri.parse("/sdcard/kuva.jpg");
		Egg egg = new Egg(5, 10, "Old author", uri, null, null, "Before overwrite, should not be seen", tags, 100);
		Nest nest = new Nest(007, "testNest", "testNest", new URI("http://test42.4dnest.org/"), ProtocolFactory.PROTOCOL_4DNEST, "testuser", "secretkey");
		Protocol protocol = nest.getProtocol();
		protocol.setNest(nest);
		ProtocolResult protResult = protocol.sendEgg(egg);
		String[] parts = protResult.getUrl().split("/");
		egg.setExternalId(parts[parts.length-1]);
		egg.setCaption("Now the tags should have changed");
		egg.setAuthor("New Author");
		ArrayList<Tag> newtags = new ArrayList<Tag>();
        newtags.add(new Tag("newTag"));
        newtags.add(new Tag("Singing"));
        egg.setTags(newtags);
		protResult = protocol.overwriteEgg(egg);
		Log.d("OVERWRITERESULT", String.valueOf(protResult.getStatusCode()));
		Egg got = protocol.getEgg(egg.getExternalId());
		assertTrue(got.getCaption().equals(egg.getCaption()));
	}
	
	@Test
	public void testGetEgg() throws Exception{
		ArrayList<Tag> tags = new ArrayList<Tag>();
		tags.add(new Tag("Programming"));
		tags.add(new Tag("Video Games"));
		MemoryCardInitializer.initialize(this.getContext());
		String eggId = "";
		Uri uri = Uri.parse("/sdcard/kuva.jpg");
		Egg egg = new Egg(5, 10, "Egg sender", uri, null, null, "Sending egg to test retrieve with tags", tags, 100);
		Nest nest = new Nest(007, "testNest", "testNest", new URI("http://test42.4dnest.org/"), ProtocolFactory.PROTOCOL_4DNEST, "testuser", "secretkey");
		Protocol protocol = nest.getProtocol();
		protocol.setNest(nest);
		ProtocolResult protResult = protocol.sendEgg(egg);
		String[] parts = protResult.getUrl().split("/");
		eggId = parts[parts.length-1];
		Egg got = protocol.getEgg(eggId);
		assertTrue(got != null);
		assertTrue(egg.getCaption().compareToIgnoreCase(got.getCaption()) == 0);
		assertTrue(egg.getTags().size() > 0);
		Log.d("EGGCAPTION", egg.getCaption());
		Log.d("EGGDATE", egg.getCreationDate().toGMTString());
	}
	
	@Test
	public void testGetStream() throws Exception {
		Nest nest = new Nest(007, "testNest", "testNest", new URI("http://test42.4dnest.org/"), ProtocolFactory.PROTOCOL_4DNEST, "testuser", "secretkey");
		Protocol protocol = nest.getProtocol();
		protocol.setNest(nest);
		List<Egg> eggList = protocol.getStream(10);
		assertTrue(eggList.size() > 0);
	}
	
	@Test
	public void testTopTags() throws Exception {
		Nest nest = new Nest(007, "testNest", "testNest", new URI("http://test42.4dnest.org/"), ProtocolFactory.PROTOCOL_4DNEST, "testuser", "secretkey");
		Protocol protocol = nest.getProtocol();
		protocol.setNest(nest);
		List<Tag> tags = protocol.topTags(2);
		assertTrue(tags.size() == 2);
		Log.d("TAGSIZE", ": " + tags.size());
	}
	
	private void getPreviouslySentEgg() {
	    
	}

}
