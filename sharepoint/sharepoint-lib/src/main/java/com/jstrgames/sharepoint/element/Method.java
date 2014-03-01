package com.jstrgames.sharepoint.element;

import java.util.LinkedList;
import java.util.List;

import org.apache.xmlbeans.XmlCursor;

import com.jstrgames.sharepoint.Element;
import com.jstrgames.sharepoint.model.Executable;

/**
 * Represents collection of methods xml element. The Method element is used in batch processing 
 * to specify commands within the Batch element. For each Executable object, a method xml node will 
 * be created.
 * 
 * @author Johnathan Ra
 *
 */
public class Method implements Element {	
	private final static String NODENAME_METHOD = "Method";
	
	private final static String ATTRIBUTENAME_ID = "ID";
	private final static String ATTRIBUTENAME_CMD = "Cmd";
	
	private final List<Executable> list;
	
	/**
	 * default constructor for a single Executeable item
	 * 
	 * @param item
	 */
	public Method(Executable item) {
		this.list = new LinkedList<Executable>();
		this.list.add(item);
	}
	
	/**
	 * default constructor for a collection of Executable item. Each item will be represented
	 * as a Method Element
	 * 
	 * @param list
	 */
	public Method(List<Executable> list) {
		this.list = list;
	}
	
	@Override
	public void write(XmlCursor xmlCursor) {
		int counter = 1;
		// iterate through collection of Executable items
		for(Executable item : list) {
			xmlCursor.beginElement(NODENAME_METHOD);		
			xmlCursor.insertAttributeWithValue(ATTRIBUTENAME_ID, String.valueOf(counter++));
			xmlCursor.insertAttributeWithValue(ATTRIBUTENAME_CMD, item.getCommand().toString());
			
			item.write(xmlCursor);
			xmlCursor.toNextToken();
		}
		xmlCursor.toParent();
	}
	
}
