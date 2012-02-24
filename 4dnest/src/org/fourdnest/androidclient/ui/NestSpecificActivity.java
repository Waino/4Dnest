package org.fourdnest.androidclient.ui;

import org.fourdnest.androidclient.FourDNestApplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

/**
 * Activity that places a button on the top of the screen. The button displays
 * the name of the current nest. Pressing the button sends the user back to
 * ListStreamActivity.
 */
public abstract class NestSpecificActivity extends Activity {

	protected ViewGroup contentLayout;
	protected FourDNestApplication application;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	// Code for hiding elements in kiosk mode (orphaned because the method it was in was refactored out 
	/*
		if (this.application.getKioskModeEnabled()) {
			nestButton.setVisibility(View.GONE);
		} else {
			nestButton.setVisibility(View.GONE);		//Remove this to show Nest Button when kiosk mode is disabled.
			setNestSpecificOnClickListener(nestButton);
			List<Nest> nests = this.application.getNestManager().listNests();
			Spinner nestSpinner = (Spinner) findViewById(R.id.nest_spinner);
			if (nests.size() > 1) {
				initializeNestSpinner(nestSpinner, nests);
				nestSpinner.setVisibility(View.VISIBLE);
			}
		}
	}
	*/

	// code for multi-nest ui-elements in top bar.
	// Disabled, because client did not want multi-nest at this point
	/*
	public void setNestSpecificOnClickListener(Button nestButton) {
		nestButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),
						ListStreamActivity.class);
				v.getContext().startActivity(intent);
			}
		});
	}

	private void initializeNestSpinner(Spinner nestSpinner, List<Nest> nests) {
		NestAdapter nestAdapter = new NestAdapter(nestSpinner, nests);
		nestSpinner.setAdapter(nestAdapter);
		nestSpinner
				.setOnItemSelectedListener(new nestSpinnerOnItemSelectedListener(
						this.application, nestAdapter));
	}
	*/

	/**
	 * A simple ArrayAdapter implementation for displaying a spinner list of
	 * configured Nests.
	 */
	/*
	private class NestAdapter extends ArrayAdapter<Nest> {

		private Spinner spinner;

		public NestAdapter(Spinner spinner, List<Nest> objects) {
			super(spinner.getContext(), R.layout.nest_spinner_element,
					R.id.nest_name, objects);
			this.spinner = spinner;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = findViewById(R.layout.nest_spinner_element);
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(this.spinner
						.getContext());
				convertView = inflater.inflate(R.layout.nest_spinner_element,
						parent, false);
			}
			TextView nameField = (TextView) convertView
					.findViewById(R.id.nest_name);
			nameField.setText(getString(R.string.change_nest_prompt));

			return convertView;
		}

		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			convertView = findViewById(R.layout.nest_spinner_element);
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(this.spinner
						.getContext());
				convertView = inflater.inflate(R.layout.nest_spinner_element,
						parent, false);
			}
			TextView nameField = (TextView) convertView
					.findViewById(R.id.nest_name);
			nameField.setText(this.getItem(position).getName());

			return convertView;
		}
	}

	private class nestSpinnerOnItemSelectedListener implements
			OnItemSelectedListener {

		private FourDNestApplication application;
		private NestAdapter adapter;

		public nestSpinnerOnItemSelectedListener(
				FourDNestApplication application, NestAdapter adapter) {
			this.application = application;
			this.adapter = adapter;
		}

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			application.setCurrentNestId(adapter.getItem(arg2).getId());
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			//Do nothing. Method stub required for implementation.

		}

	}
	*/

}
