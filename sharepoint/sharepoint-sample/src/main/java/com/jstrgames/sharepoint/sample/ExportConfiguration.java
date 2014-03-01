package com.jstrgames.sharepoint.sample;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.axis2.transport.http.HttpTransportProperties.Authenticator;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstrgames.sharepoint.Configuration;

public class ExportConfiguration implements Configuration {
	private static final Logger LOG = LoggerFactory.getLogger(ExportConfiguration.class);
	
	public static final String DELIMITER = ",";
	
	private static final String DEFAULT_PROPERTIES_FILENAME = "sharepoint.properties";
	
	private static final String KEY_DELIMITED_FIELDS = "DELIMITED_FIELDS";
	private static final String KEY_EXPORT_HEADERS = "EXPORT_HEADERS";
	private static final String KEY_LISTNAME_GUID = "LISTNAME_GUID";
	private static final String KEY_LOGIN_REQUIRED = "LOGIN_REQUIRED";
	private static final String KEY_PASSWORD = "PASSWORD";
	private static final String KEY_PREEMPTIVE_AUTH = "PREEMPTIVE_AUTH";
	private static final String KEY_ROWLIMIT = "ROWLIMIT";
	private static final String KEY_SOURCE_URL = "SOURCE_URL";
	private static final String KEY_TARGET_FILENAME = "TARGET_FILENAME";
	private static final String KEY_USERNAME = "USERNAME";
	private static final String KEY_VIEWNAME_GUID = "VIEWNAME_GUID";
	
	private final java.util.Properties props;
	private String[] delimitedFields;
	
	public ExportConfiguration() {
		this.props = new java.util.Properties();
		loadProperties(DEFAULT_PROPERTIES_FILENAME);
	}
	
	public ExportConfiguration(String resourceName) {
		this.props = new java.util.Properties();
		loadProperties(resourceName);
	}
	
	public String getTargetFileName() {
		return this.props.getProperty(KEY_TARGET_FILENAME);
	}
	
	public String getSourceUrl() {
		return this.props.getProperty(KEY_SOURCE_URL);
	}
	
	public String getExportHeader() {
		return this.props.getProperty(KEY_EXPORT_HEADERS);
	}
	
	public String[] getFields() {
		if(this.delimitedFields == null) {
			final String value = this.props.getProperty(KEY_DELIMITED_FIELDS);
			this.delimitedFields = value.split(DELIMITER);
		}		
		return this.delimitedFields;
	}
	@Override
	public String getListGUID() {
		return this.props.getProperty(KEY_LISTNAME_GUID);
	}

	@Override
	public String getViewGUID() {
		return this.props.getProperty(KEY_VIEWNAME_GUID);
	}

	@Override
	public int getRowLimit() {
		int rowLimit;
		try {
			rowLimit = Integer.parseInt(this.props.getProperty(KEY_ROWLIMIT));
		} catch (NumberFormatException e) {
			rowLimit = DEFAULT_MAX_ROWLIMIT;
		}
		
		return rowLimit;
	}

	@Override
	public Authenticator getAuthenticator() {
		final Authenticator auth = new Authenticator();
		if(Boolean.parseBoolean(this.props.getProperty(KEY_LOGIN_REQUIRED))) {			
			auth.setUsername(this.props.getProperty(KEY_USERNAME));
			auth.setPassword(this.props.getProperty(KEY_PASSWORD));
			auth.setPreemptiveAuthentication(
					Boolean.parseBoolean(this.props.getProperty(KEY_PREEMPTIVE_AUTH)));
		}
		return auth;
	}

	private void loadProperties(String resourceName) {
		InputStream inputStream = null;
		// always attempt to read from file system
		try {
			inputStream = FileUtils.openInputStream(new File(resourceName));
		} catch (IOException e) {
			LOG.info("Failed to locate file!", e);
		}
		
		if(inputStream == null) {
			// if not a system file, try looking up in classpath
			final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
			inputStream = classLoader.getResourceAsStream(resourceName);
		}
		
		if(inputStream == null) {
			// if not check relevant to this class
			final ClassLoader classLoader = ExportConfiguration.class.getClass().getClassLoader();
			inputStream = classLoader.getResourceAsStream(resourceName);
		}
		
		if(inputStream == null) {
			throw new FailedToSetupException(
					"Failed to locate required properties file:" + resourceName);
		}
		
		try {
			this.props.load(inputStream);
		} catch (IOException e) {
			throw new FailedToSetupException(
					"Unexpected Error reading properties file:" + resourceName, e);
		}
	}
}
