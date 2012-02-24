package org.fourdnest.androidclient.test.services;

import java.net.URI;
import java.util.ArrayList;

import org.fourdnest.androidclient.Egg;
import org.fourdnest.androidclient.FourDNestApplication;
import org.fourdnest.androidclient.Nest;
import org.fourdnest.androidclient.NestManager;
import org.fourdnest.androidclient.Tag;
import org.fourdnest.androidclient.comm.Protocol;
import org.fourdnest.androidclient.comm.ProtocolFactory;
import org.fourdnest.androidclient.services.SendQueueService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;

/**
 * Tests the send queue service.
 */
public class SendQueueServiceTest extends ServiceTestCase<SendQueueService> {
	
	private final static int NEST_ID = 1234;
	public SendQueueServiceTest() {
		super(SendQueueService.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		FourDNestApplication app = new FourDNestApplication();
		
		Protocol testProtocol = new SendQueueTestProtocol();
		
		app.getNestManager().deleteAllNests();
		
		Nest nest = new Nest(NEST_ID, "Nest", "Nest descr", new URI("http://127.0.0.1"), testProtocol.getProtocolId(), "username", "secretkey");
		app.getNestManager().saveNest(nest);
		app.setCurrentNestId(nest.getId());
		
		setApplication(app);
		setContext(app);
	}
	
	
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

	/*
	private NestManager testNestManager;
	private SendQueueService service;
	private static SendQueueServiceTest tester;
	private static long DELAY = 100;
	
	private Egg massEgg;
	private Egg trueEgg;
	private Egg falseEgg;
	private Egg manualEgg;
	private boolean trueEggSeen = false;
	private boolean falseEggSeen = false;
	private boolean manualEggSeen = false;

	static {
		ProtocolFactory.registerProtocol(1024, SendQueueTestProtocol.class);
	}

	@Before
	public void setUp() throws Exception {
		tester = this;
		this.testNestManager = new NestManager(this.getContext());
		this.testNestManager.saveNest(new Nest(
				1024,
				"DummyProtocolNest",
				"A nest for testing SendQueueService",
				new URI("http://127.0.0.1"),
				1024,
				"testuser", "secretkey"
		));
		this.service = new SendQueueService();
		this.service.setDelay(DELAY);
		this.service.onCreate();
		this.massEgg = new Egg(
				0,
				1024,
				"Matti",
				null,
				null,
				"Mass Egg",
				new ArrayList<Tag>(),
				System.currentTimeMillis()
			);
		this.trueEgg = new Egg(
				1,
				1024,
				"Matti",
				null,
				null,
				"True Egg",
				new ArrayList<Tag>(),
				System.currentTimeMillis()
			);
		this.falseEgg = new Egg(
				2,
				1024,
				"Matti",
				null,
				null,
				"False Egg",
				new ArrayList<Tag>(),
				System.currentTimeMillis()
			);
		this.manualEgg = new Egg(
				3,
				1024,
				"Matti",
				null,
				null,
				"Manual Egg",
				new ArrayList<Tag>(),
				System.currentTimeMillis()
			);
	}

	@After
	public void tearDown() throws Exception {
		this.service.onDestroy();
	}
	
	@Test
	public void testFastScheduling() {
		long allowed = 1000;	// Using 1s, real maximum is 10s
		long timestamp = System.currentTimeMillis();
		// Schedule 1000 pieces of work
		for(int i=0; i<1000; i++) {
			this.service.queueEgg(this.massEgg,true);
		}
		long duration = System.currentTimeMillis() - timestamp;
		System.out.printf("Scheduling duration: %d\n", duration);
		assertTrue(duration <= allowed);
	}

	@Test
	public void testQueueEgg() {
		trueEggSeen = false;
		falseEggSeen = false;
		manualEggSeen = false;
		this.service.queueEgg(this.trueEgg, true);
		this.service.queueEgg(this.falseEgg, false);
		this.guaranteeSleep(3*DELAY);
		assertTrue(trueEggSeen);
		assertFalse(falseEggSeen);
	}
	
	public void testSendQueuedEgg() {
		trueEggSeen = false;
		falseEggSeen = false;
		manualEggSeen = false;
		// send the interesting Eggs and some mass eggs for filling
		this.service.queueEgg(this.massEgg, false);
		this.service.queueEgg(this.trueEgg, false);
		this.service.queueEgg(this.falseEgg, false);
		this.service.queueEgg(this.manualEgg, false);
		this.service.queueEgg(this.massEgg, false);
		// simulates the user doing other stuff while the Eggs go into queue
		this.guaranteeSleep(2*DELAY);
		
		// then the user interacts with the queue: removes one and manually sends one
		this.service.removeQueuedEgg(this.falseEgg);
		this.service.sendQueuedEgg(this.manualEgg);
		
		// wait for these to take effect before checking
		this.guaranteeSleep(3*DELAY);
		assertTrue(manualEggSeen);	// manual should have been sent
		assertFalse(trueEggSeen);	// this is not sent yet
		assertFalse(falseEggSeen);	// this should have been removed
		
		
		this.service.sendAllQueuedEggs();
		this.guaranteeSleep(3*DELAY);
		assertTrue(manualEggSeen);	// manual should have been sent
		assertTrue(trueEggSeen);	// this should have been sent by sendAllQueuedEggs
		assertFalse(falseEggSeen);
	}*/
	
	/* We need to sleep before running tests to make sure that the thread
	 * has time to see the work and run it. */
	
	/*private void guaranteeSleep(long delay) {
		boolean slept = false;
		try {
			while(!slept) {
				Thread.sleep(delay);
				slept = true;
			}
		} catch(InterruptedException ex) { }
	}

	public static void eggSent(Egg egg) {
		if(egg.equals(tester.trueEgg)) {
			tester.trueEggSeen = true;
		} else if(egg.equals(tester.falseEgg)) {
			tester.falseEggSeen = true;
		} else if(egg.equals(tester.manualEgg)) {
			tester.manualEggSeen = true;
		}
	}	
	
	*/


}
