package com.jstrgames.sharepoint;

import org.apache.xmlbeans.XmlCursor;

public interface Element {
	public static final String DEFAULT_PREFIX = "ows_";
	void write(XmlCursor xmlCursor);
}
