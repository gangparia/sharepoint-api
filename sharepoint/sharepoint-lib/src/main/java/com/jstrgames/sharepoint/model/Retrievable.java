package com.jstrgames.sharepoint.model;

import org.w3c.dom.Node;

/**
 * The user of this interface has the precise control of which SharePoint
 * fields to parse and return.
 * 
 * @author Johnathan Ra
 */
public interface Retrievable {	
	void fromNode(Node node);	
}
