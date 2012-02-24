package org.fourdnest.androidclient.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A class for static utility methods.
 * also @see org.fourdnest.androidclient.Util 
 */
public class GenericUtils {

	/**
	 * Write an InputStream to a file
	 * @param is InputStream
	 * @param path output file location
	 * @throws IOException
	 */
	public static void writeInputStreamToFile(InputStream is, String path)
			throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		FileOutputStream os = new FileOutputStream(new File(path));
		BufferedOutputStream bos = new BufferedOutputStream(os);
		int c;
		try {
			while ((c = bis.read()) != -1) {
				bos.write(c);
			}
		} finally {
			bos.close();
			bis.close();
		}
	}

}
