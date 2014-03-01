package com.jstrgames.sharepoint;

import org.apache.axis2.transport.http.HttpTransportProperties.Authenticator;

/**
 * this class holds the properties needed to interact with test
 * SharePoint list. 
 * 
 * @author Johnathan Ra
 *
 */
public class TestConfiguration implements Configuration {
	
	private final int rowLimit; 
	private final String listGUID;
	private final String viewGUID;
	private final Authenticator authenticator;
	
	/**
	 * construct this class with target list name guid and list view guid.
	 * Target view must be a valid part of target list. Default row limit is
	 * 200
	 * 
	 * @param listGUID
	 * @param viewGUID
	 */
	public TestConfiguration(String listGUID, String viewGUID) {
		this.listGUID = listGUID;
		this.viewGUID = viewGUID;
		this.rowLimit = DEFAULT_MAX_ROWLIMIT;
		this.authenticator = null;
	}
	
	/**
	 * construct this class with target list name guid, list view guid, and 
	 * row limit. Row limit is used to override the default row limit of 200
	 * 
	 * @param listGUID
	 * @param viewGUID
	 * @param rowLimit
	 */
	public TestConfiguration(String listGUID, String viewGUID, int rowLimit) {
		this.listGUID = listGUID;
		this.viewGUID = viewGUID;
		this.rowLimit = rowLimit;
		this.authenticator = null;
	}
	
	/**
	 * construct this class with http authenticator (BASIC/DIGEST), target 
	 * list name guid, and list view guid.
	 *  
	 * @param authenticator
	 * @param listGUID
	 * @param viewGUID
	 */
	public TestConfiguration(Authenticator authenticator, 
						  String listGUID,
						  String viewGUID) {
		this.listGUID = listGUID;
		this.viewGUID = viewGUID;
		this.rowLimit = DEFAULT_MAX_ROWLIMIT;
		this.authenticator = authenticator;
	}
	
	/**
	 * construct this class with http authenticator (BASIC/DIGEST), target 
	 * list name guid, list view guid, and row limit.
	 * 
	 * @param authenticator
	 * @param listGUID
	 * @param viewGUID
	 * @param rowLimit
	 */
	public TestConfiguration(Authenticator authenticator,
					  	  String listGUID, 
					  	  String viewGUID,
					  	  int rowLimit) {
		this.listGUID = listGUID;
		this.viewGUID = viewGUID;
		this.rowLimit = rowLimit;
		this.authenticator = authenticator;
	}
	
	public String getListGUID() {
		return this.listGUID;
	}
	
	public String getViewGUID() {
		return this.viewGUID;
	}
	
	public int getRowLimit() {
		return this.rowLimit;
	}
	
	public Authenticator getAuthenticator() {
		return this.authenticator;
	}

}
