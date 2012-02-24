package org.fourdnest.androidclient.comm;

/**
 * The requested protocol id (protocolName) was not found
 */
public class UnknownProtocolException extends Exception {
	private static final long serialVersionUID = -4959770447870739918L;

	/**
	 * @param protocolName Numerical id of the requested protocol
	 */
	public UnknownProtocolException(final int protocolName) {
		super("Unknown protocol " + protocolName);
	}
}
