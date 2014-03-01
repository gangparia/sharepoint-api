package com.jstrgames.sharepoint.sample;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.axis2.AxisFault;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstrgames.sharepoint.ListService;
import com.jstrgames.sharepoint.ServiceUnreachableException;
import com.microsoft.sharepoint.ListsStub;

/**
 * Sample app using the SharePoint API to extract specific fields from task list 
 * into comma separated file  
 * 
 * @author Johnathan Ra
 *
 */
public class ExportClient {
	private static final Logger LOG = LoggerFactory.getLogger(ExportClient.class);
		
	private static final String EMPTY_STRING = "";
	private static final String OPTION_HELP = "help";
	private static final String OPTION_PROPFILE = "propfile";
	private static final String OPTION_PROPFILE_ARGFILE = "file";
	private static final String PROP_LINE_SEPARATOR = "line.separator";
	private static final String SHAREPOINT_REF_SEPARATOR = ";#";
	
	private final ExportConfiguration config;
	private final ListsStub soapEndPoint;
	private final String lineSeparator;
	
	public ExportClient(String propFile) {
		this.config = new ExportConfiguration(propFile);
		this.lineSeparator = System.getProperty(PROP_LINE_SEPARATOR);
		try {
			this.soapEndPoint = new ListsStub(this.config.getSourceUrl());
		} catch (AxisFault e) {
			throw new FailedToSetupException("Unable to setup endpoint!", e);
		}
	}
	
	/**
	 * follows simple command pattern to execute this class based on properties file
	 * provided
	 */
	public void execute() {		
		final String[] fieldList = this.config.getFields();
		final ListService<ExportListItem> service = 
				new ListService<ExportListItem>(this.config, this.soapEndPoint, ExportListItem.class);
		try {
			final File filename = new File(this.config.getTargetFileName());
			filename.createNewFile();
			final OutputStream outStream = FileUtils.openOutputStream(filename);
			
			// write header
			IOUtils.write(this.config.getExportHeader(), outStream);
			IOUtils.write(this.lineSeparator, outStream);
			
			// write results
			final List<ExportListItem> list = service.retrieve();
			for(ExportListItem item : list) {
				final StringBuilder builder = new StringBuilder();
				final Map<String,String> map = item.getMap();
				
				boolean isFirst = true;
				for(String field : fieldList) {
					if(isFirst) {
						isFirst = false;
					} else {
						builder.append(ExportConfiguration.DELIMITER);
					}
					final String value = trimReferenceNumber(map.get(field));
					builder.append(quoteValueIfDelimiterExists(value));
				}
				builder.append(this.lineSeparator);
				IOUtils.write(builder.toString(), outStream);				
			}
			
		} catch (ServiceUnreachableException e) {
			LOG.error("Failed to connect to source sharepoint site", e);
		} catch (IOException e) {
			LOG.error("Failed to write results to file", e);
		}
	}
	
	/**
	 * helper method to split out text for reference type sharepoint field
	 * 
	 * @param value
	 * @return
	 */
	private String trimReferenceNumber(String value) {
		String retVal;
		
		if(value != null) {
			final int idxOf = value.indexOf(SHAREPOINT_REF_SEPARATOR);
			if(idxOf > -1) {
				retVal = value.substring(idxOf+SHAREPOINT_REF_SEPARATOR.length()); 
			} else {
				retVal = value;
			}
		} else {
			retVal = EMPTY_STRING;
		}
		
		return retVal;
	}
	
	/**
	 * helper method to quote field value if comma exists as this can unnecessarily
	 * shift column on output
	 * 
	 * @param value
	 * @return
	 */
	private String quoteValueIfDelimiterExists(String value) {
		String retVal;
		
		if(value != null) {
			final int idxOf = value.indexOf(ExportConfiguration.DELIMITER);
			if(idxOf > -1) {
				retVal = "\"" + value.substring(idxOf+ExportConfiguration.DELIMITER.length()) + "\""; 
			} else {
				retVal = value;
			}
		} else {
			retVal = EMPTY_STRING;
		}
		
		return retVal;
	}
	
	/**
	 * main entry point. caller must provide a single argument - property file with
	 * details on which sharepoint to connect to 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final Options options = createOptions();
		final CommandLineParser parser = new GnuParser();
		
		try {
			CommandLine cmdLine = parser.parse(createOptions(), args);
			if(cmdLine.hasOption("help")) {
				printHelp(options);
			} else if(cmdLine.hasOption(OPTION_PROPFILE)) {
				final String propFile = cmdLine.getOptionValue(OPTION_PROPFILE);
				final ExportClient client = new ExportClient(propFile);
				client.execute();
			} else {
				printHelp(options);
			}
			
		} catch (ParseException e) {
			printHelp(options);
		}
	}
	
	/**
	 * helper method to build command options/arguments
	 * 
	 * @return
	 */
	@SuppressWarnings("static-access")
	private static Options createOptions() {
		final Options options = new Options();
		
		Option propfile = OptionBuilder.withArgName(OPTION_PROPFILE_ARGFILE)
                .hasArg()
                .withDescription( "use specified properties file" )
                .create(OPTION_PROPFILE);
		options.addOption(propfile);
	
		options.addOption(new Option(OPTION_HELP, "display this message"));
		return options;
	}

	/**
	 * helper method to display help on screen 
	 * 
	 * @param options
	 */
	private static  void printHelp(Options options) {
		HelpFormatter format = new HelpFormatter();
		format.printHelp("client", options);
	}
}
