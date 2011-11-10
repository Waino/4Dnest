package org.fourdnest.androidclient.comm;

import java.util.ArrayList;

import org.fourdnest.androidclient.Egg;
import org.fourdnest.androidclient.Nest;
import org.fourdnest.androidclient.Tag;


public interface Protocol {
	
	public String sendEgg(Egg egg, Nest nest);
	public ArrayList<Tag> topTags(int count);
	//public String getTagSuggestion(String prefix);
}
