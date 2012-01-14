package org.fourdnest.androidclient.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fourdnest.androidclient.FourDNestApplication;
import org.fourdnest.androidclient.Nest;
import org.fourdnest.androidclient.NestManager;
import org.fourdnest.androidclient.Tag;
import org.fourdnest.androidclient.comm.Protocol;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * A service that keeps caches of recent and/or popular tags,
 * both local and remote. 
 */
public class TagSuggestionService extends IntentService {
	/** Tag string used to indicate source in logging */	
	public static final String TAG = TagSuggestionService.class.getSimpleName();

	/** Intent should have this category when remote tags should be updated */
	public static final String UPDATE_REMOTE_TAGS = "UPDATE_REMOTE_TAGS_CATEGORY";
	/** Intent should have this category when requesting tag broadcast */
	public static final String GET_TAGS = "GET_TAGS_CATEGORY";
	/** Intent should have this category when setting last used tags */
	public static final String SET_LAST_USED_TAGS = "SET_LAST_USED_TAGS_CATEGORY";

	/** Broadcast Intent will have this action if it contains autocomplete suggestions */
	public static final String ACTION_AUTOCOMPLETE_TAGS = "corg.fourdnest.androidclient.AUTOCOMPLETE_TAGS";
	/** Broadcast Intent will have this action if it contains last used tags */
	public static final String ACTION_LAST_USED_TAGS = "corg.fourdnest.androidclient.LAST_USED_TAGS";

	
	/** Key for current Nest id in Intent extras */
	public static final String BUNDLE_NEST_ID = "BUNDLE_NEST_ID";
	/** Key for tag list in Intent extras */
	public static final String BUNDLE_TAG_LIST = "BUNDLE_TAG_LIST";
	
	/** How long to initially wait before fetching remote tags */
	private static final long FIRST_INTERVAL = 0;

	private static final int REMOTE_TAG_COUNT = 1024;
	
	private LocalBroadcastManager mLocalBroadcastManager;
	
	/** Use String[] instead of List<Tag>, to avoid converting back and forth.
	 * Intents only support standard types. */
	private Map<Integer, String[]> lastUsedTags;
	private Map<Integer, HashSet<String>> localTags;
	private Map<Integer, String[]> remoteTags;

	private FourDNestApplication app;
	
	/**
	 * Constructor, simply calls super. Never used explicitly in user code.
	 */
	public TagSuggestionService() {
		super(TagSuggestionService.class.getName());
	}
	/**
	 * Constructor, simply calls super. Never used explicitly in user code.
	 * @param name Name of service
	 */	
	public TagSuggestionService(String name) {
		super(name);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		this.lastUsedTags = new HashMap<Integer, String[]>();
		this.localTags = new HashMap<Integer, HashSet<String>>();
		this.remoteTags = new HashMap<Integer, String[]>();
		this.app = FourDNestApplication.getApplication();
		this.mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);


		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(	//Be power efficient: we don't need exact timing
        		AlarmManager.ELAPSED_REALTIME,	// Don't wake up phone just for this
        		FIRST_INTERVAL,								
        		AlarmManager.INTERVAL_HOUR,		// Update frequency
        		PendingIntent.getService(
        				app,					// The context
        				0,
        				new Intent(app, TagSuggestionService.class),
        				0
        		)
        );
	}

	/**
	 * Request broadcasting of the current tag suggestions
	 */
	public static void requestTagBroadcast(Context context) {
		FourDNestApplication app = (FourDNestApplication) context;
		Integer currentNestId = Integer.valueOf(app.getCurrentNestId());
		
		Intent intent = new Intent(context, TagSuggestionService.class);
		intent.addCategory(GET_TAGS);
		intent.putExtra(BUNDLE_NEST_ID, currentNestId);
		context.startService(intent);
	}
	
	/**
	 * Stores the tags used in sending an Egg,
	 * so that when sending the next one they can be retrieved
	 * and used as suggestions.
	 * @param tags The tags attached to the last sent Egg.
	 */
	public static void setLastUsedTags(Context context, List<Tag> tags) {
		FourDNestApplication app = (FourDNestApplication) context;
		Integer currentNestId = Integer.valueOf(app.getCurrentNestId());

		Intent intent = new Intent(context, TagSuggestionService.class);
		intent.addCategory(SET_LAST_USED_TAGS);
		intent.putExtra(BUNDLE_NEST_ID, currentNestId);
		intent.putExtra(BUNDLE_TAG_LIST, tagListToStringArray(tags));
		context.startService(intent);
	}

	/**
	 * Default Intent handler for IntentService. All Intents get sent here.
	 * Updates the remote tag cache.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent called");
		if(intent.hasCategory(UPDATE_REMOTE_TAGS)) {
			updateRemoteTags();
		} else if(intent.hasCategory(GET_TAGS)) {
			broadcastTags(intent);
		} else if(intent.hasCategory(SET_LAST_USED_TAGS)) {
			handleLastUsedTags(intent);
		}
	}
	/**
	 * Loops through all Nests updating their tag cache
	 */
	private synchronized void updateRemoteTags() {
		NestManager nestManager = app.getNestManager();
		List<Nest> nests = nestManager.listNests();
		for(Nest nest : nests) {
			Integer nestId = Integer.valueOf(nest.getId());
			Protocol protocol = nest.getProtocol();
			List<Tag> tags = protocol.topTags(REMOTE_TAG_COUNT);
			if(tags != null) {
				Log.d(TAG, "Tags updated for Nest with id " + nestId);
				this.remoteTags.put(nestId, tagListToStringArray(tags));
			} else {
				Log.w(TAG, "Nest with id " + nestId + " returned null topTags");
			}
		}
	}

	/**
	 * Loops through all Nests updating their tag cache
	 */
	private synchronized void broadcastTags(Intent intent) {
		// First broadcast the autocomplete suggestions
		Integer currentNestId = Integer.valueOf(intent.getIntExtra(BUNDLE_NEST_ID, -1));
		List<String> out = new ArrayList<String>();
		Set<String> lt = this.localTags.get(currentNestId);
		if(lt != null) {
			out.addAll(lt);
		}
		String[] rt = this.remoteTags.get(currentNestId);
		if(rt != null) {
			for(String tag : rt)
			out.add(tag);
		}
		Intent broadcastIntent = new Intent(ACTION_AUTOCOMPLETE_TAGS);
		broadcastIntent.putExtra(BUNDLE_NEST_ID, currentNestId);
		broadcastIntent.putExtra(BUNDLE_TAG_LIST, out.toArray(new String[0]));
		mLocalBroadcastManager.sendBroadcast(broadcastIntent);

		// Then broadcast the last used tags
		broadcastIntent.putExtra(BUNDLE_NEST_ID, currentNestId);
		broadcastIntent = new Intent(ACTION_LAST_USED_TAGS);
		String[] tags = this.lastUsedTags.get(currentNestId);
		if(tags == null) {
			tags = new String[0];
		};
		broadcastIntent.putExtra(BUNDLE_TAG_LIST, tags);
		mLocalBroadcastManager.sendBroadcast(broadcastIntent);
	}

	private synchronized void handleLastUsedTags(Intent intent) {
		Integer currentNestId = Integer.valueOf(intent.getIntExtra(BUNDLE_NEST_ID, -1));
		String[] tags = intent.getStringArrayExtra(BUNDLE_TAG_LIST);
		this.lastUsedTags.put(currentNestId, tags);
	}

	private static String[] tagListToStringArray(List<Tag> tags) {
		String[] out = new String[tags.size()];
		int i = 0;
		for(Tag tag : tags) {
			out[i++] = tag.getName();
		}
		return out;
	}
}
