package com.jstrgames.sharepoint;

public class InvalidListItemException extends RuntimeException {

	private static final long serialVersionUID = -7018063929338333649L;

	public InvalidListItemException(String msg) {
		super(msg);
	}
}
