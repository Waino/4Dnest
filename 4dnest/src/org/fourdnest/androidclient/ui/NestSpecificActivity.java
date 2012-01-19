package org.fourdnest.androidclient.ui;

import org.fourdnest.androidclient.FourDNestApplication;
import org.fourdnest.androidclient.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Activity that places a button on the top of the screen. The button displays
 * the name of the current nest. Pressing the button sends the user back to
 * ListStreamActivity.
 */
public abstract class NestSpecificActivity extends Activity {

	protected ViewGroup contentView;
	private FourDNestApplication application;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.application = ((FourDNestApplication)getApplication());
		setContentView(R.layout.nest_specific_view);
		
		inflateNestView();

//		findViewById(R.id.nest_button).setOnClickListener(
//				new OnClickListener() {
//
//					public void onClick(View v) {
//						Intent intent = new Intent(v.getContext(),
//								ListStreamActivity.class);
//						v.getContext().startActivity(intent);
//					}
//				});

		this.contentView = (FrameLayout) findViewById(R.id.content_view);

		LayoutInflater inflater = LayoutInflater.from(contentView.getContext());
		View view = inflater.inflate(getLayoutId(), contentView, false);
		contentView.addView(getContentLayout(view));
	}
	
	/** Called when the activity is resumed. */
	@Override
	protected void onResume() {
		inflateNestView();
		super.onResume();
	}

	/**
	 * Initializes the main content view. The main content view is the entire
	 * area under the Nest display.
	 * 
	 * @param view
	 *            The ViewGroup where you want to display your functional
	 *            content.
	 */
	public abstract View getContentLayout(View view);

	/**
	 * Returns the id of the layout definition that is used to fill the content
	 * layout. Acquired with R.layout.your_layout_id.
	 * 
	 * @return An integer representing the id of the specified layout.
	 */
	public abstract int getLayoutId();

	/**
	 * Inflates the nest_view in nest_specific_view with the appropriate view,
	 * depending on kiosk settings.
	 */
	private void inflateNestView() {
		
		String nestName = this.application.getCurrentNest().getName();
		TextView nestLabel;
		
		FrameLayout nestView = (FrameLayout) findViewById(R.id.nest_view);
		nestView.removeAllViews();
		LayoutInflater inflater = LayoutInflater.from(nestView.getContext());
		
		if (this.application.getKioskModeEnabled()) {
			inflater.inflate(R.layout.nest_view_kiosk_enabled, nestView);
			nestLabel = (TextView)nestView.findViewById(R.id.nest_label);
		} else {
			inflater.inflate(R.layout.nest_view_kiosk_disabled, nestView);
			nestLabel = (TextView)nestView.findViewById(R.id.nest_label);
//			((Button)view.findViewById(R.id.nest_button)).setText(nestName);
		}
		nestLabel.setText(nestName);
	}

}