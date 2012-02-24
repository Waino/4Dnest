package org.fourdnest.androidclient.services;

import java.util.List;

import org.fourdnest.androidclient.Egg;
import org.fourdnest.androidclient.EggManager;
import org.fourdnest.androidclient.FourDNestApplication;
import org.fourdnest.androidclient.comm.FourDNestProtocol;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class StreamReaderService extends IntentService {

	public static final String TAG = "STREAMREADERSERVICE";

	/**
	 * Intent should have this category when stream should be read from the
	 * server
	 */
	public static final String READ_STREAM = "READ_STREAM_CATEGORY";
	public static final String STREAM_FREQ = "STREAM_FREQUENCY_CATEGORY";
	public static final String NEW_FREQUENCY = "NEW_FRQUENCY";
	public static final String STREAM_SIZE = "STREAM_SIZE_CATEGORY";
	public static final String NEW_STREAM_SIZE = "NEW_STREAM_SIZE";

	/** Broadcast has this action when stream has been updated */
	public static final String ACTION_STREAM_UPDATED = "org.fourdnest.androidclient.STREAM_UPDATED";

	/** How long we initially wait before fetching the Stream
	 * This is done each time the service is started (which happens frequently despite START_STICKY,
	 * not only when initially starting the app. Therefore setting this to 0 will cause double fetching.
	 */
	public static final long FIRST_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

	public static final String THUMBNAIL_SAVE_LOCATION = "thumbnails";

	/**
	 * Default Frequency in seconds, as a String so it can be used in the
	 * getPreference method
	 */
	private static final String DEFAULT_FREQUENCY = "600";

	/**
	 * Default Frequency in seconds, as a String so it can be used in the
	 * getPreference method
	 */
	private static final String DEFAULT_SIZE = "10";

	private FourDNestApplication app;
	private int size = Integer.parseInt(DEFAULT_SIZE);
	private LocalBroadcastManager mLocalBroadcastManager;

	public StreamReaderService() {
		super(StreamReaderService.class.getName());
	}

	public StreamReaderService(String name) {
		super(name);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "Intentcreated");
		app = FourDNestApplication.getApplication();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(app);
		int frequency = Integer.parseInt(prefs.getString("stream_frequency",
				String.valueOf(DEFAULT_FREQUENCY))) * 60000;
		size = Integer.parseInt(prefs.getString("stream_size",
				String.valueOf(DEFAULT_SIZE)));
		Log.d(TAG, "Value of freq after read: " + frequency);
		Log.d(TAG, "Value of size after read: " + size);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(app, StreamReaderService.class);
		intent.addCategory(READ_STREAM);
		am.setInexactRepeating( // Be power efficient: we don't need exact
								// timing
				AlarmManager.ELAPSED_REALTIME, // Don't wake up phone just for
												// this
				System.currentTimeMillis() + FIRST_INTERVAL, // Time of first execution
				frequency, // Update frequency
				PendingIntent.getService(app, // The context
						0, intent, 0));
		this.mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
	}

	/**
	 * Requests an immediate update
	 * 
	 * @param context
	 *            The application context
	 */
	public static void requestUpdate(Context context) {
		Intent intent = new Intent(context, StreamReaderService.class);
		intent.addCategory(READ_STREAM);
		context.startService(intent);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "Handling intent");
		if (intent.hasCategory(READ_STREAM)) {
			Log.d(TAG, "Got READ_STREAM category");
			EggManager em = app.getStreamEggManager();
			em.deleteAllEggs();
			List<Egg> eggList = app.getCurrentNest().getProtocol()
					.getStream(size);
			Log.d(TAG, "Egglist size: " + eggList.size());
			for (int i = 0; i < eggList.size(); i++) {

				Egg egg = eggList.get(i);
				em.saveEgg(egg);
				if (egg.getRemoteThumbnailUri() != null) {
					//Log.d(TAG, "ThumbailUri" + egg.getRemoteThumbnailUri());
					app.getCurrentNest()
							.getProtocol()
							.getThumbnail(egg,
									FourDNestProtocol.THUMBNAIL_SIZE_SMALL);
				}

			}
			Log.d(TAG, "Saved eggs");
			List<Egg> eggs = em.listEggs();
			Log.d("EGGAMOUNT2", String.valueOf(eggs.size()));

			// broadcast that the stream is updated
			Intent broadcastIntent = new Intent(ACTION_STREAM_UPDATED);
			mLocalBroadcastManager.sendBroadcast(broadcastIntent);
		}
		if (intent.hasCategory(STREAM_FREQ)) {
			Log.d(TAG, "Got updated frequency");
		}
	}

}
