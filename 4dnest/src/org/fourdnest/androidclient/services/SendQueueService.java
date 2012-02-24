package org.fourdnest.androidclient.services;
import org.fourdnest.androidclient.Egg;
import org.fourdnest.androidclient.FourDNestApplication;
import org.fourdnest.androidclient.Nest;
import org.fourdnest.androidclient.R;
import org.fourdnest.androidclient.comm.ProtocolResult;
import org.fourdnest.androidclient.ui.ListStreamActivity;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

/**
 * A service for sending Egg to it's specified Nest in the background.
 * Jobs are sent via Intent (use sendEgg convenience method) and queued
 * for sending automatically. 
 */
public class SendQueueService extends IntentService {
	
	/** Tag string used to indicate source in logging */	
	public static final String TAG = SendQueueService.class.getSimpleName();
	
	/** Intent should have this category when Egg is meant to be extracted from extras */
	public static final String SEND_EGG = "SEND_EGG_CATEGORY";
	/** Key for Egg id in Intent extras */
	public static final String BUNDLE_EGG_ID = "BUNDLE_EGG_ID";

	public static final String ACTION_DRAFTS_UPDATED = "org.fourdnest.androidclient.DRAFTS_UPDATED";;
	
	/** Internal Handler for displaying Toast after job completes */
	private Handler handler;
	//private NotificationManager notificationManager;
	
	private LocalBroadcastManager mLocalBroadcastManager;
	
	/**
	 * Constructor, simply calls super. Never used explicitly in user code.
	 */
	public SendQueueService() {
		super(SendQueueService.class.getName());
	}
	/**
	 * Constructor, simply calls super. Never used explicitly in user code.
	 * @param name
	 */
	public SendQueueService(String name) {
		super(name);
	}
	
	/**
	 * Override for default onCreate method. Creates a Handler for Toasts
	 * and calls super()
	 */
	@Override
	public void onCreate() {
		this.handler = new Handler();
		//this.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		this.mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		super.onCreate();
	}
	
	/**
	 * Static method to be called with an Egg that needs to be put
	 * to the send queue. Handles all the ugly details.
	 * 
	 * @param context Current application context
	 * @param egg Egg to be put to queue
	 * @param isDraft Is the Egg already in the draft database (sending of saved drafts)
	 */
	public static void sendEgg(Context context, Egg egg, boolean isDraft) {
		FourDNestApplication app = (FourDNestApplication)context; 
		
		Egg savedEgg;
		if(!isDraft) {
			Nest currentNest = app.getCurrentNest();		
			if(currentNest == null) {
				Toast.makeText(app, context.getString(R.string.sendqueue_active_nest_not_set), Toast.LENGTH_SHORT);
				return;
			}
			savedEgg = app.getDraftEggManager().saveEgg(egg);
		} else {
			savedEgg = egg;
		}
		
		
		
		Intent intent = new Intent(context, SendQueueService.class);
		intent.addCategory(SendQueueService.SEND_EGG);
		intent.putExtra(SendQueueService.BUNDLE_EGG_ID, savedEgg.getId());

		context.startService(intent);
		Toast.makeText(context, context.getString(R.string.sendqueue_egg_queued), Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Default Intent handler for IntentService. All Intents get sent here.
	 * Extracts Egg id from Intent extras, finds an Egg with that id and
	 * sends it to the Nest specified in the Egg. 
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent called");
		
		// If Intent it categorized as egg-sending intent, act accordingly 
		if(intent.hasCategory(SEND_EGG)) {			
			FourDNestApplication app = (FourDNestApplication) this.getApplication();
			
			Notification notification = new Notification(R.drawable.icon, getText(R.string.sendqueue_egg_queued), System.currentTimeMillis());

			// Prepare intent to start desired activity when notification is clicked
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ListStreamActivity.class), 0);        

	        // Set status bar info
	        notification.setLatestEventInfo(this, getText(R.string.sendqueue_statusbar_title), getText(R.string.sendqueue_egg_queued), contentIntent);

	        // Start service in foreground, checking for null to avoid Android testing bug
	        if(getSystemService(ACTIVITY_SERVICE) != null) {
	        	this.startForeground(R.string.sendqueue_egg_queued, notification);
	        }
			
			
			
			// Get Egg id from Intent, fetch the Egg from db
			int eggId = intent.getIntExtra(BUNDLE_EGG_ID, -1);
			Egg egg = app.getDraftEggManager().getEgg(eggId);
			
			if(egg == null) {
				Log.d(TAG, "Egg with id " + eggId + " not found");
			} else {
				ProtocolResult res = app.getNestManager().getNest(egg.getNestId()).getProtocol().sendEgg(egg);
				if(res.getStatusCode() == ProtocolResult.RESOURCE_UPLOADED) {
					// Display message
					this.handler.post(new ToastDisplay(app, getString(R.string.sendqueue_egg_send_complete), Toast.LENGTH_LONG));
					// Delete Egg from drafts
					app.getDraftEggManager().deleteEgg(eggId);
					
					Log.d(TAG, "Send completed");
					
			    	Log.d(TAG, "Requesting stream update in onHandleIntent");
					StreamReaderService.requestUpdate(app);
					
					// broadcast that the draft list is updated
					Intent broadcastIntent = new Intent(ACTION_DRAFTS_UPDATED);
					mLocalBroadcastManager.sendBroadcast(broadcastIntent);
				} else {
					// Something went wrong, transform code to message
					String message = "";
					
					switch (res.getStatusCode()) {
					case ProtocolResult.AUTHORIZATION_FAILED:
						message = (String) getText(R.string.protocolerror_authfail);
						break;
					case ProtocolResult.SENDING_FAILED:
						message = (String) getText(R.string.protocolerror_sendfail);
						break;
					case ProtocolResult.SERVER_INTERNAL_ERROR:
						message = (String) getText(R.string.protocolerror_servererror);
						break;
					case ProtocolResult.UNKNOWN_REASON:
						message = (String) getText(R.string.protocolerror_unknown);
						break;
					}
					// Display message and die
					this.handler.post(new ToastDisplay(app, message, Toast.LENGTH_LONG));
					Log.d(TAG, "Send failed: " + res.getStatusCode());
				}
				
				
			}
		}
		
		
	}
	
	/**
	 * Private class to display Toasts in main thread
	 */
	private static class ToastDisplay implements Runnable {
		private String message;
		private Context context;
		private int duration;
		
		/**
		 * Runnable ToastDisplayer, added to a Handler to be run at a later time or
		 * in a different thread
		 * @param context Target context to display Toast in
		 * @param message Message content
		 * @param duration Toast.LENGTH_SHORT or Toast.LENGTH_LONG
		 */
		public ToastDisplay(Context context, String message, int duration) {
			this.context = context;
			this.message = message;
			this.duration = duration;
		}
		
		/**
		 * Called when Runnable is executed. displays Toast as specified when object was created
		 */
		public void run() {
			Toast.makeText(this.context, this.message, this.duration).show();
		}
	
	}
}
