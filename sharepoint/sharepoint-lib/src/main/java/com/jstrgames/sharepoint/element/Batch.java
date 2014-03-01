package com.jstrgames.sharepoint.element;

import org.apache.xmlbeans.XmlCursor;

import com.jstrgames.sharepoint.Element;
import com.microsoft.schemas.sharepoint.soap.UpdateListItemsDocument.UpdateListItems.Updates;

/**
 * Represents a single Batch XML element. It provides batch processing of commands 
 * within HTTP protocol. It must contain at least one Method element. It allows 
 * the client application to post more than one command to the server at a time.
 * 
 * @author Johnathan Ra
 * @link http://msdn.microsoft.com/en-us/library/ms437562(v=office.12).aspx
 *  
 */
public class Batch implements Element {
	private final static String NODENAME_BATCH = "Batch";
	
	private final static String ATTRIBUTENAME_LISTVERSION = "ListVersion";
	private final static String ATTRIBUTENAME_ONERROR = "OnError";
	private final static String ATTRIBUTENAME_VIEWNAME= "ViewName";
	
	private final static String LISTVERSION_DEFAULT = "1";
	private final static String ONERROR_CONTINUE = "Continue";
	
	private final String viewNameGuid;
	private final Method method;
	
	/**
	 * default constructor
	 * 
	 * @param method
	 * @param viewNameGuid
	 */
	public Batch(Method method, String viewNameGuid) {
		this.method = method;
		this.viewNameGuid = viewNameGuid;		
	}
	
	/**
	 * returns a single Updates elements containing a single Batch element
	 * 
	 * @return
	 */
	public Updates generateUpdates() {
		final Updates updates = Updates.Factory.newInstance(); 
		final XmlCursor xmlCursor = updates.newCursor();
		
		write(xmlCursor);
		
		return updates;
	}

	@Override
	public void write(XmlCursor xmlCursor) {
		xmlCursor.toNextToken();
		xmlCursor.beginElement(NODENAME_BATCH);
		xmlCursor.insertAttributeWithValue(ATTRIBUTENAME_ONERROR, ONERROR_CONTINUE);
		xmlCursor.insertAttributeWithValue(ATTRIBUTENAME_LISTVERSION, LISTVERSION_DEFAULT);
		xmlCursor.insertAttributeWithValue(ATTRIBUTENAME_VIEWNAME, this.viewNameGuid);
		
		// calls method to add command
		this.method.write(xmlCursor);
		
		xmlCursor.toParent();
	}
	
}
