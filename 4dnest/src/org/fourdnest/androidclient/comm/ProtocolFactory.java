package org.fourdnest.androidclient.comm;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * Creates Protocol instances.
 * It is unlikely that this class needs changing:
 * New protocols can be added by registering them using the registerProtocol method.
 * Make sure that the protocol id is unique.
 */
public final class ProtocolFactory {
	/** Id for the 4DNest protocol */
	public static final int PROTOCOL_4DNEST = 0;
	private static final String TAG = "ProtocolFactory";
	
	private static final Map<Integer, Class<? extends Protocol>> protocols =
		new HashMap<Integer, Class<? extends Protocol>>();

	static {
		protocols.put(Integer.valueOf(PROTOCOL_4DNEST), FourDNestProtocol.class);
	}
	
	/** Private constructor to to prevent instantiation */
	private ProtocolFactory() { };
	
	/**
	 *  Creates a new Protocol object of the requested type
	 *  @param protocolId The type of protocol.
	 *  @throws UnknownProtocolException 
	 *   */
	public static Protocol createProtocol(int protocolId) throws UnknownProtocolException {
		Class<? extends Protocol> c = protocols.get(protocolId);
		if(c != null) {
			try {
				return c.newInstance();
			} catch (IllegalAccessException e) {
				// This should never happen
				Log.e(TAG, e.getMessage());
			} catch (InstantiationException e) {
				// This should never happen
				Log.e(TAG, e.getMessage());
			}
		}
		throw new UnknownProtocolException(protocolId);
	}
	
	/**
	 * Plugs in a new protocol type, linking it to a given protocolId
	 * @param protocolId the unique id for this protocol.
	 * @param c the class object from which the Protocol objects will be instantiated.
	 */
	public static void registerProtocol(int protocolId, Class<? extends Protocol> c) {
		protocols.put(Integer.valueOf(protocolId), c);
	}
}
