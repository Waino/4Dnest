package org.fourdnest.androidclient.ui;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.fourdnest.androidclient.Egg;
import org.fourdnest.androidclient.FourDNestApplication;
import org.fourdnest.androidclient.R;
import org.fourdnest.androidclient.Tag;
import org.fourdnest.androidclient.ThumbnailManager;
import org.fourdnest.androidclient.comm.FourDNestProtocol;
import org.fourdnest.androidclient.comm.MediaManager;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewEggActivity extends NestSpecificActivity {

	private int eggID;
	private ImageView thumbnail;
	private Egg egg;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		this.application = (FourDNestApplication) getApplication();
		
		Bundle startingExtras = getIntent().getExtras();
		this.eggID = (Integer) startingExtras
				.get(EggItemOnClickListener.INTENT_EGG_ID);

		super.onCreate(savedInstanceState);

		
		setContentView(R.layout.egg_view);

		this.egg = this.application.getStreamEggManager().getEgg(eggID);

		TextView timestamp = (TextView) findViewById(R.id.timestamp);
		TextView message = (TextView) findViewById(R.id.message);
		TextView tags = (TextView) findViewById(R.id.tags);

		if(egg != null) {
			Date creationDate = egg.getCreationDate();
			if(creationDate != null) {
				timestamp.setText(DateFormat.getDateTimeInstance().format(creationDate));
			}
			message.setText(egg.getCaption());
			
			if (egg.getMimeType() != Egg.fileType.TEXT) {
				this.thumbnail = (ImageView) findViewById(R.id.file_thumbnail);
				// show a (non-spinning) spinner while loading the thumbnail
				this.thumbnail.setImageResource(R.drawable.spinner);
				new ThumbnailTask().execute(egg);
				new MediaFileTask().execute(egg);
			}
			List<Tag> tagList = egg.getTags();
			if (tagList.size() > 0) { // if there are no tags, leave default message
				// (no tags)
				tags.setText(EggAdapter.tagListToString(tagList));
			}
					
			final Button button = (Button) findViewById(R.id.button);
	        button.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                Intent intent = new Intent(v.getContext(), MapViewActivity.class);
	                intent.putExtra(MapViewActivity.EGG_ID, ViewEggActivity.this.eggID);
	                intent.putExtra(NewEggActivity.NEW_EGG, false);
	                v.getContext().startActivity(intent);
	            }
	        });
			
			//TODO: Inflate based on egg type
	
	//		FrameLayout mediaView = (FrameLayout) findViewById(R.id.media_view);
	//		LayoutInflater inflater = LayoutInflater.from(mediaView.getContext());
	//		View view = inflater.inflate(R.layout.egg_element_large, mediaView, false);
	//		mediaView.addView(view);
		}
		super.onCreate(savedInstanceState);
	}

	/**
	 * Creates the options menu on the press of the Menu button.
	 * 
	 * @param menu
	 *            The menu to inflate
	 * @return Boolean indicating success of creating the menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_menu, menu);
		return true;
	}

	/**
	 * Specifies the action to perform when a menu item is pressed.
	 * 
	 * @param item
	 *            The MenuItem that was pressed
	 * @return Boolean indicating success of identifying the item
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_view_pref:
			startActivity(new Intent(this, PrefsActivity.class));
			return true;
		case R.id.menu_view_help:
			startActivity(this.application.helpBrowserIntent());
			return true;
		/*
		// FIXME Actually these would be better as on-screen buttons instead of menu buttons
		case R.id.menu_view_edit:
			//FIXME unimplemented, button hidden. Uncomment from xml when implemented
			return true;
		case R.id.menu_view_delete:
			//FIXME unimplemented, button hidden. Uncomment from xml when implemented
			return true;
		*/
		}
		return false;
	}

	/**
	 * Asynchronous task for background retrieval of larger thumbnail
	 */
	private class ThumbnailTask extends AsyncTask<Egg, Void, Void> {
		protected Void doInBackground(Egg... eggs) {
			Egg egg = eggs[0]; 
			application.getCurrentNest().getProtocol()
			.getThumbnail(egg, FourDNestProtocol.THUMBNAIL_SIZE_LARGE);
			final Uri thumbUri = Uri.parse(ThumbnailManager.getThumbnailUriString(
					egg,
					FourDNestProtocol.THUMBNAIL_SIZE_LARGE
			));
			
			// Android allows only the UI thread to touch views
			runOnUiThread(new Runnable() {
			     public void run() {
			    	 thumbnail.setImageURI(thumbUri);
			    }
			});

			return null;	// because Void is an Android placeholder, not true void
		}

		protected void onProgressUpdate() {
		}

		protected void onPostExecute() {
		}
	 }

	/**
	 * Asynchronous task for background retrieval of mediafile
	 * Yes this is hairy (anonymous inner class within another
	 * anonymous inner class within a named inner class).
	 * Handle with care.
	 */
	private class MediaFileTask extends AsyncTask<Egg, Void, Void> {
		protected Void doInBackground(Egg... eggs) {
			Egg egg = eggs[0];
			Uri remoteUri = egg.getRemoteFileURI();
			if(remoteUri == null) return null;
			
			final String mediaPath = MediaManager.getMediaUriString(egg);
			application.getCurrentNest().getProtocol()
				.getRelativeMediaFile(remoteUri.toString(), mediaPath);
			
			// Android allows only the UI thread to touch views
			runOnUiThread(new Runnable() {
			     public void run() {
			    		ViewEggActivity.this.thumbnail.setOnClickListener(new OnClickListener() {

			    	        public void onClick(View arg0) {
			    	            // in onCreate or any event where your want the user to
			    	            // select a file
			    	        	Intent i = new Intent(Intent.ACTION_VIEW);
			    	        	/*
			    	        	 * Creates an intent for previewing media with correct type of media
			    	        	 * selected
			    	        	 * 
			    	        	 * LEET HACKS, needs file:// to the front or will crash !!!!!!!!!
			    	        	 */
			    	        	
			    	        	Egg.fileType mime = ViewEggActivity.this.egg.getMimeType();
			    	        	switch(mime) {
			    	        	case IMAGE:
			    	            	i.setDataAndType(Uri.parse("file://"+mediaPath), "image/*");
			    	            	break;
			    	        	case AUDIO:
			    	        		i.setDataAndType(Uri.parse("file://"+mediaPath), "audio/*");
			    	        		break;
			    	        	case VIDEO:
			    	            	i.setDataAndType(Uri.parse("file://"+mediaPath), "video/*");
			    	            	break;
			    	        	case TEXT:
			    	        		i = null;
			    	        		break;
			    	        	case ROUTE:
			    	        		i.setDataAndType(Uri.parse("file://" + mediaPath), "image/*");
			    	        		break;
			    	        	case NOT_SUPPORTED:
			    	        		Log.d("MediaFileTask", "Mime type NOT_SUPPORTED");
			    	        		i = null;
			    	        		break;
			    	        	}
			    	        	if(i != null) {
			    	        		startActivity(i);
			    	        	}
			    	        }
			    	    });
			    }
			});

			return null;	// because Void is an Android placeholder, not true void
		}

		protected void onProgressUpdate() {
		}

		protected void onPostExecute() {
		}
	 }

	

}
