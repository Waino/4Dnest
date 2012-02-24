package org.fourdnest.androidclient.ui;

import org.fourdnest.androidclient.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * The preference activity for 4DNest that handles all the preference specific activity.
 */
public class PrefsActivity extends PreferenceActivity {
    /**
     * Determines what happens when the PreferenceActivity is created.
     *
     * @param savedInstanceState Contains information regarding the state of the activity
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	addPreferencesFromResource(R.xml.stream_prefs);
	}
}
