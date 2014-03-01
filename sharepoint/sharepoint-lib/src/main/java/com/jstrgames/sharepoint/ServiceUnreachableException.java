package com.jstrgames.sharepoint;

public class ServiceUnreachableException extends Exception {

	private static final long serialVersionUID = -3080437934558199099L;

	public ServiceUnreachableException(String msg, Throwable t) {
		super(msg, t);
	}
}
