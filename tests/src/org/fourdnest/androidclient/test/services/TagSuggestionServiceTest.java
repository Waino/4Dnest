package org.fourdnest.androidclient.test.services;

import org.fourdnest.androidclient.FourDNestApplication;
import org.fourdnest.androidclient.services.SendQueueService;
import org.fourdnest.androidclient.services.TagSuggestionService;

import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;

/**
 * Tests the send queue service.
 */
public class TagSuggestionServiceTest extends ServiceTestCase<TagSuggestionService> {
	
	private final static int NEST_ID = 1;
	public TagSuggestionServiceTest() {
		super(TagSuggestionService.class);
	}
	@Override
	protected void setUp() throws Exception {
		super.setUp();
/*		
		FourDNestApplication app = new FourDNestApplication();
		
		app.setCurrentNestId(NEST_ID);
		
		setApplication(app);
		setContext(app);
*/
	}
	
	/*
	public void testStartable() {
		Intent startIntent = new Intent();
		startIntent.setClass(getContext(), SendQueueService.class);
		startService(startIntent);
	}
	
    public void testBindable() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), SendQueueService.class);
        IBinder service = bindService(startIntent); 
    }
*/

}
