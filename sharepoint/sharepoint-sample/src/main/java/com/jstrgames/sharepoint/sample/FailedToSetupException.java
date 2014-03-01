package com.jstrgames.sharepoint.sample;

public class FailedToSetupException extends RuntimeException {
	private static final long serialVersionUID = -1744564100141391404L;

	public FailedToSetupException(String msg) {
		super(msg);
	}
	
	public FailedToSetupException(String msg, Throwable t) {
		super(msg, t);
	}
}
