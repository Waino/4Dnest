package org.fourdnest.androidclient.test;

import java.util.ArrayList;
import java.util.List;

import org.fourdnest.androidclient.Egg;
import org.fourdnest.androidclient.EggManager;
import org.fourdnest.androidclient.Tag;

import android.test.AndroidTestCase;

public class EggManagerTest extends AndroidTestCase {
	
	EggManager eggManager;

	protected void setUp() throws Exception {
		super.setUp();
		
		this.eggManager = new EggManager(this.getContext());
		
		this.eggManager.deleteAllEggs();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		
		this.eggManager.close();
	}
	
	public void testSaveAndGetEgg() {
		long now = System.currentTimeMillis();
		ArrayList<Tag> tags = new ArrayList<Tag>();
		Egg e = new Egg(
			null,
			1,
			"Matti",
			null,
			null,
			"TestCaption",
			tags,
			now
		);
		
		e = this.eggManager.saveEgg(e);
		
		Egg fetchedEgg = this.eggManager.getEgg(e.getId());
		assertTrue(e.equals(fetchedEgg));
		
		
	}

	public void testListEggs() {
		long now = System.currentTimeMillis();
		ArrayList<Tag> tags = new ArrayList<Tag>();
		
		Egg e1 = new Egg(
			null,
			1,
			"Matti",
			null,
			null,
			"CaptionForEgg",
			tags,
			now
		);
		e1 = this.eggManager.saveEgg(e1);
		
		Egg e2 = new Egg(
			null,
			2,
			"Matti",
			null,
			null,
			"CaptionToo!",
			tags,
			now
		);
		e2 = this.eggManager.saveEgg(e2);
		
		Egg e3 = new Egg(
			null,
			10,
			"Matti",
			null,
			null,
			null,
			tags,
			now
		);
		e3 = this.eggManager.saveEgg(e3);
		
		List<Egg> eggs = this.eggManager.listEggs();
		
		assertTrue(eggs.get(0).equals(e1));
		assertTrue(eggs.get(1).equals(e2));
		assertTrue(eggs.get(2).equals(e3));
		
	}
	
	public void testDeleteEgg() {
		long now = System.currentTimeMillis();
		ArrayList<Tag> tags = new ArrayList<Tag>();
		
		Egg e1 = new Egg(
				0,
				1,
				"Matti",
				null,
				null,
				"CaptionForEgg",
				tags,
				now
		);
		this.eggManager.saveEgg(e1);
		
		// Check that 1 nest is deleted
		int result = this.eggManager.deleteEgg(0);
		assertEquals(1, result);
		
		// Check that nest with that id no longer exists
		Egg e = this.eggManager.getEgg(0);
		assertNull(e);
	}
	
	public void testDeleteAllNests() {			
		long now = System.currentTimeMillis();
		ArrayList<Tag> tags = new ArrayList<Tag>();
		
		Egg e1 = new Egg(
				null,
				1,
				"Matti",
				null,
				null,
				"CaptionForEgg",
				tags,
				now
		);
		e1 = this.eggManager.saveEgg(e1);
			
		Egg e2 = new Egg(
				null,
				2,
				"Matti",
				null,
				null,
				"CaptionToo!",
				tags,
				now
		);
		e2 = this.eggManager.saveEgg(e2);
		
		this.eggManager.deleteAllEggs();
		
		assertNull(this.eggManager.getEgg(1));
		assertNull(this.eggManager.getEgg(2));
		
		assertEquals(this.eggManager.listEggs().size(), 0);		
	}
	

}
