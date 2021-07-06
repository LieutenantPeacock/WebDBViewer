package com.colonelparrot.dbviewer.commons.exceptions;

/**
 * @author ColonelParrot
 * @version 1.1
 */
public class MetadataRetrievalException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7280066924664575507L;

	public MetadataRetrievalException() {
	}

	public MetadataRetrievalException(String message) {
		super(message);
	}

	public MetadataRetrievalException(Throwable cause) {
		super(cause);
	}

	public MetadataRetrievalException(String message, Throwable cause) {
		super(message, cause);
	}

	public MetadataRetrievalException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
