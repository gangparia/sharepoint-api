package com.jstrgames.sharepoint;

import org.apache.axis2.transport.http.HttpTransportProperties.Authenticator;

/**
 * The user of this interface has the precise control of which SharePoint to 
 * interact with by defining the below properties 
 * 
 * @author Johnathan Ra
 */
public interface Configuration {
	public static final int DEFAULT_MAX_ROWLIMIT = 200; 
	
	String getListGUID();	
	String getViewGUID();	
	int getRowLimit();	
	Authenticator getAuthenticator();
}
