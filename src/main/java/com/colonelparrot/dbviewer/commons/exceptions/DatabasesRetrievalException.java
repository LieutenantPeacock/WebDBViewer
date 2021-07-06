package com.colonelparrot.dbviewer.commons.exceptions;

/**
 * @author ColonelParrot
 * @version 1.1
 */
public class DatabasesRetrievalException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4665275837190365442L;

	public DatabasesRetrievalException() {
	}

	public DatabasesRetrievalException(String message) {
		super(message);
	}

	public DatabasesRetrievalException(Throwable cause) {
		super(cause);
	}

	public DatabasesRetrievalException(String message, Throwable cause) {
		super(message, cause);
	}

	public DatabasesRetrievalException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
