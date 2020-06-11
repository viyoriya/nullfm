package com.viyoriya.fm.exception;

//Never used
public class FmException extends Exception {

	private static final long serialVersionUID = 1L;

	public FmException(String errorMessage) {
		super(errorMessage);
	}
	public FmException(Throwable cause) {
	    super(cause);
	}		
	public FmException(String errorMessage, Throwable cause) {
	    super(errorMessage, cause);
	}

}
