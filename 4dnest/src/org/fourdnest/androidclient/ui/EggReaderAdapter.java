package org.fourdnest.androidclient.ui;

import java.util.ArrayList;

import org.fourdnest.androidclient.Egg;
import org.fourdnest.androidclient.R;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * An adapter for serving a list of Egg views to Egg lists in
 * ListStoredEggsFragment. The listed Egg view layouts are defined in layout
 * file egg_element_large.
 * 
 * @see EggListAdapter.
 */
public class EggReaderAdapter extends EggListAdapter {

	public EggReaderAdapter(ViewGroup parent) {
		super(parent);
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {

		if (arg1 == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			arg1 = inflater.inflate(R.layout.egg_element_large, parent, false);
		}

		Egg egg = (Egg) this.getItem(arg0);

		ImageView thumbnail = (ImageView) arg1.findViewById(R.id.thumbnail);
		TextView message = (TextView) arg1.findViewById(R.id.message);
		TextView date = (TextView) arg1.findViewById(R.id.date);

		// There might be listeners for ListViews which don't require setting
		// individual onClickListeners for each egg preview.
		arg1.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),
						ViewEggActivity.class);
				parent.getContext().startActivity(intent);

			}
		});

		return arg1;
	}

	/**
	 * Sets this adapter to serve the given ArrayList of eggs
	 * 
	 * @param eggs
	 *            An ArrayList that contains the eggs that the parent view
	 *            should display.
	 */
	public void setEggs(ArrayList<Egg> eggs) {
		// TODO: Get a real implementation for this
		this.eggs = new ArrayList<Egg>();
		for (int i = 0; i < 8; i++) {
			this.eggs.add(new Egg());
		}
	}

}
