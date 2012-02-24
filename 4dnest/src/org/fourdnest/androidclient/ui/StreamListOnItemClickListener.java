package org.fourdnest.androidclient.ui;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class StreamListOnItemClickListener implements OnItemClickListener {

	public static final String INTENT_EGG_ID = "INTENT_EGG_ID";
	private ListView streamList;

	public StreamListOnItemClickListener(ListView streamList) {
		this.streamList = streamList;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(arg1.getContext(), ViewEggActivity.class);
		intent.putExtra(INTENT_EGG_ID, ((EggAdapter) streamList.getAdapter())
				.getItem(arg2).getId());
		arg0.getContext().startActivity(intent);

	}

}
