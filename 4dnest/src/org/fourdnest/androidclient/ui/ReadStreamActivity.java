package org.fourdnest.androidclient.ui;

import org.fourdnest.androidclient.R;
import org.fourdnest.androidclient.services.SendQueueService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

/**
 * The starting activity of the application. Displays a list of eggs present on
 * the server. Also provides functionality to access the create view and for
 * toggling route tracking.
 */
public class ReadStreamActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_stream_view);

		ListView streamList = (ListView) findViewById(R.id.egg_list);
		EggReaderAdapter adapter = new EggReaderAdapter(streamList);
		adapter.setEggs(null);
		streamList.setAdapter(adapter);
		streamList.setOnItemClickListener(new EggElementOnClickListener());
		
		//TODO: Put this somewhere else
		Intent startServiceIntent = new Intent(this.getApplicationContext(), SendQueueService.class);
		this.getApplicationContext().startService(startServiceIntent);
	}

}
