package org.fourdnest.androidclient.ui;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class DraftListOnItemClickListener implements OnItemClickListener {

	public static final String INTENT_EGG_ID = "eggID";
	private ListView draftList;

	public DraftListOnItemClickListener(ListView draftList) {
		this.draftList = draftList;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(arg1.getContext(), ListStreamActivity.class);
		intent.putExtra(INTENT_EGG_ID, ((EggAdapter) draftList.getAdapter())
				.getItem(arg2).getId());
		arg0.getContext().startActivity(intent);

	}
}
