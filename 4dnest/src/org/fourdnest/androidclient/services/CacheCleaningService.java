package org.fourdnest.androidclient.services;

import java.io.File;

import org.fourdnest.androidclient.FourDNestApplication;
import org.fourdnest.androidclient.ThumbnailManager;
import org.fourdnest.androidclient.comm.MediaManager;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

/**
 * Service for cleaning cache files related with the 4dnest application
 * Currently only handles the cleaning of thumbnails but can be easily extended to other parts as well.
 *
 */

public class CacheCleaningService extends IntentService {
	
	private static final String TAG = "CacheCleaningService";
	public static final String CLEAN_THUMBNAILS = "cleanThumbnails";
	public static final String CLEAN_MEDIA = "cleanMedia";
	private static final long ONE_DAY_IN_MILLIS = 86400000;
	
	public CacheCleaningService() {
		super(CacheCleaningService.class.getName());
	}

	public CacheCleaningService(String name) {
		super(name);
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG,"Intent Created");
		super.onCreate();
		FourDNestApplication app = FourDNestApplication.getApplication();
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent in = new Intent(app, CacheCleaningService.class);
		in.addCategory(CLEAN_THUMBNAILS);
		in.addCategory(CLEAN_MEDIA);
		am.setInexactRepeating( // Be power efficient: we don't need exact  timing
				AlarmManager.ELAPSED_REALTIME, // Don't wake up phone just for this
				0, // Time of first	 execution
				AlarmManager.INTERVAL_DAY, // Clean the thumbnails once a day.
				PendingIntent.getService(app, // The context
						0, in, 0));
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG,"Handling intent");
		if (intent.hasCategory(CLEAN_THUMBNAILS)) {
			Log.d(TAG,"Got thumbnail clean command");
			String thumbnailDir = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + ThumbnailManager.THUMBNAIL_LOCATION;
			cleanFolder(thumbnailDir);
		}
		if (intent.hasCategory(CLEAN_MEDIA)) {
			Log.d(TAG,"Got thumbnail media command");
			String mediaDir = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + MediaManager.MEDIA_LOCATION;
			cleanFolder(mediaDir);
		}
		
	}
	
	public static void requestClean(Context context) {
		Intent i = new Intent(context, CacheCleaningService.class);
		i.addCategory(CLEAN_THUMBNAILS);
		i.addCategory(CLEAN_MEDIA);
		context.startService(i);
	}
	
	private void cleanFolder(String path) {
		File dir = new File(path);
		//Delete all files that are older than one day
		long timeLimit = System.currentTimeMillis() - ONE_DAY_IN_MILLIS;
		if (dir.exists() && dir.isDirectory()) {
			File list[] = dir.listFiles();
			if (list != null) {
				for (File f : list) {
					String name = f.getName();
					Log.d(TAG, "Checking file " + name);
					long lastModified = f.lastModified();
					if (timeLimit > lastModified) {
						Log.d(TAG, "Trying to delete file " + name);
						if (f.delete()) {
							Log.d(TAG, "Deleted file succesfully");
						}
					}else {
						Log.d(TAG, "No need to remove file " + name);
					}
				}
			}
		}
	}

}
