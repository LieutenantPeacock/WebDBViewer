package com.colonelparrot.dbviewer.commons.exceptions;

/**
 * @author ColonelParrot
 * @version 1.1
 */
public class TableRetrievalException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8868749389849077532L;

	public TableRetrievalException() {
	}

	public TableRetrievalException(String message) {
		super(message);
	}

	public TableRetrievalException(Throwable cause) {
		super(cause);
	}

	public TableRetrievalException(String message, Throwable cause) {
		super(message, cause);
	}

	public TableRetrievalException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
