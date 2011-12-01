package org.fourdnest.androidclient.ui;

import org.fourdnest.androidclient.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CreateScreenActivity extends Activity {

	// YOU CAN EDIT THIS TO WHATEVER YOU WANT
	private static final int SELECT_PICTURE = 1;
	private static final int RESULT_OK = -1; // apparently its -1... dunno

	private String selectedImagePath;
	// ADDED
	private String filemanagerstring;
	private Context currentContext;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_view);

		((Button) findViewById(R.id.openGalleryButton))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View arg0) {
						// in onCreate or any event where your want the user to
						// select a file
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(
								Intent.createChooser(intent, "Select Picture"),
								SELECT_PICTURE);

					}
				});
	}

	// UPDATED
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
			Uri selectedImageUri = data.getData();

			// OI FILE Manager
			filemanagerstring = selectedImageUri.getPath();

			// MEDIA GALLERY
			selectedImagePath = getPath(selectedImageUri);
			
			// NOW WE HAVE OUR WANTED STRING
			String imagePath = "";
			if (selectedImagePath != null) {
				imagePath = selectedImagePath;
				System.out
						.println("selectedImagePath is the right one for you!");
			} else {
				imagePath = filemanagerstring;
				System.out
						.println("filemanagerstring is the right one for you!");
			}
			Intent myIntent = new Intent(this.currentContext,
					NewPhotoEggActivity.class);
			myIntent.putExtra("pictureURL", imagePath);
			startActivityForResult(myIntent, 0);
		}
	}

	// UPDATED!
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };

		CursorLoader loader = new CursorLoader(this.currentContext, uri,
				projection, null, null, null);
		Cursor cursor = loader.loadInBackground();
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int columnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(columnIndex);
		} else {
			return null;
		}
	}
}
