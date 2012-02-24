package org.fourdnest.androidclient.comm;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.fourdnest.androidclient.FourDNestApplication;
import org.fourdnest.androidclient.tools.GenericUtils;

import android.net.Uri;
import android.util.Log;

public class CommUtils {
	private static final String UNICODE = "UTF-8";
	private static final int HTTP_STATUSCODE_OK = 200;
	private static final String TAG = "CommUtils";
	private static final int CONNECTION_TIMEOUT = 15000;
	/** Default HTTP port number */
	private static final int HTTP_PORT = 80;
	/**Default HTTPS port number*/
	private static final int HTTPS_PORT = 443;

	/**
	 * Hashes the file with the given path in MD5 and returns a base64 encoded
	 * string of the hash
	 * 
	 * @param Path
	 *            of the file
	 * 
	 * @return Base64 encoded md5 hash
	 **/
	public static String md5FromFile(String path) {
		String result = "";
		try {
			FileInputStream fis = new FileInputStream(new File(path));
			byte[] bytes = DigestUtils.md5(fis);
			result = new String(Hex.encodeHex(bytes));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block

		} catch (IOException e) {
			// TODO Auto-generated catch block

		}
		return result;

	}

	/**
	 * Hashes the string in MD5 and returns a base64 encoded string of the hash
	 * 
	 * @param String
	 *            to be hashed
	 * 
	 * @return Base64 encoded md5 hash
	 **/
	public static String md5FromString(String s) {
		String result = "";
		if (s != null) {
			try {
				byte[] bytes = DigestUtils.md5(s.getBytes(UNICODE));
				result = new String(Hex.encodeHex(bytes));

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block

			}

		}
		return result;
	}

	public static String responseToString(HttpResponse response)
			throws IOException {
		InputStream content = null;
		content = response.getEntity().getContent();
		BufferedReader r = new BufferedReader(new InputStreamReader(content));
		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
		    total.append(line);
		}
		return total.toString();
	}

	/**
	 * Creates a new HTTPClient with configured parameters and schemes.
	 * 
	 * @return DefaultHttpClient
	 */
	public static DefaultHttpClient createHttpClient() {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		// http scheme
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), HTTP_PORT));
		FourDNestApplication app = FourDNestApplication.getApplication();
		boolean aac;
		if (app == null) {
			Log.d(TAG, "app was null");
			aac = true;
		} else {
			aac = app.getAllowAllCerts();
		}
		if (aac) {
			// https scheme, all certs allowed
			schemeRegistry.register(new Scheme("https",
					new EasySSLSocketFactory(), HTTPS_PORT));
		} else {
			// doesn't allow all certs
			schemeRegistry.register(new Scheme("https", SSLSocketFactory
					.getSocketFactory(), HTTPS_PORT));
		}
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				CONNECTION_TIMEOUT);
		HttpProtocolParams.setContentCharset(params, UNICODE);
		HttpProtocolParams.setHttpElementCharset(params, UNICODE);
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params,
				schemeRegistry);
		return new DefaultHttpClient(cm, params);
	}

	/**
	 * Creates the MultipartEntity from name-value -pair list
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public static MultipartEntity createEntity(List<NameValuePair> pairs)
			throws UnsupportedEncodingException {
		Charset charset = Charset.forName(UNICODE);
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.STRICT,
				null, charset);

		for (int i = 0; i < pairs.size(); i++) {
			File file = new File(pairs.get(i).getValue());
			if (pairs.get(i).getName().equalsIgnoreCase("file")) {
				entity.addPart(pairs.get(i).getName(), new FileBody(file));
			} else {
				StringBody strbd = new StringBody(pairs.get(i).getValue(),
						charset);
				entity.addPart(pairs.get(i).getName(), strbd);
				// Log.d("STRINGBODY", strbd.getCharset());
			}
		}
		Log.d("CONTENTTYPE", entity.getContentType().getValue());
		return entity;
	}

	public static boolean getNetFile(Uri uri, Uri localUri) {
		HttpClient client = CommUtils.createHttpClient();
		try {
			HttpGet request = new HttpGet(new URI(uri.toString()));
			HttpResponse resp = client.execute(request);
			if (resp.getStatusLine().getStatusCode() != HTTP_STATUSCODE_OK) {
				return false;
			}
			InputStream is = resp.getEntity().getContent();
			GenericUtils.writeInputStreamToFile(is, localUri.toString());
			return true;

		} catch (URISyntaxException e) {
			Log.e(TAG, "getMediaFile: Invalid URI");
		} catch (ClientProtocolException e) {
			Log.e(TAG, "getMediaFile: Execute failed");
		} catch (IOException e) {
			Log.e(TAG, "getMediaFile: Write operation failed");
		}

		return false;

	}

}
