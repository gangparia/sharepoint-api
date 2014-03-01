package com.jstrgames.sharepoint.model;

import org.w3c.dom.Node;

import com.jstrgames.sharepoint.Element;
import com.jstrgames.sharepoint.element.Command;

/**
 * The user of this interface has the precise control of which SharePoint
 * command to execute (CREATE, UPDATE, or DELETE).
 * 
 * @author Johnathan Ra
 */
public interface Executable extends Element {
	public final static String NODENAME_FIELD= "Field";
	public final static String ATTRIBUTE_NAME = "Name";
	public final static String FIELDNAME_ID = "ID";
	
	Command getCommand();
	boolean hasAttachment();
	void setId(int id);
	boolean isMatch(Node node);
}
