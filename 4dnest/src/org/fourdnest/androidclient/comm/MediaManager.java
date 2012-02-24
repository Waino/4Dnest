package org.fourdnest.androidclient.comm;

import java.io.File;

import org.fourdnest.androidclient.Egg;

import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

/**
 * Manages the storage locations of the full-size media files. 
 */
public class MediaManager {
    public static String MEDIA_LOCATION = "/fourdnest/media/";
    
    /**
     * Check if given file exists
     * 
     * @param egg
     *            for which the check is done
     * @return boolean
     */
    public static boolean thumbNailExists(Egg egg) {
        String path = getMediaUriString(egg);
        if ((new File(path)).exists()) {
            return true;
        }else {
            return false;   
        }
    }
    
    public static String getMediaUriString(Egg egg) {
    	String mediaDir = Environment.getExternalStorageDirectory()
			+ MEDIA_LOCATION;
    	if (!new File(mediaDir).exists()) {
			if (!new File(mediaDir).mkdirs()) {
				return mediaDir + "fail";	//silly
			}
		}
    	Uri remoteUri = egg.getRemoteFileURI();
        if (remoteUri == null) { // this shouldn't happen
            return mediaDir + "fail";
		} else {
			String remoteUriString = remoteUri.toString();
			return mediaDir
					+ egg.getExternalId()
					+ "." + MimeTypeMap.getFileExtensionFromUrl(remoteUriString);
		}

    }

}
