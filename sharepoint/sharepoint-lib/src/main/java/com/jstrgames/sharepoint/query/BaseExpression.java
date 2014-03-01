package com.jstrgames.sharepoint.query;

import org.apache.xmlbeans.XmlCursor;

/**
 * Base expression defined by CAML comparison
 * 
 * @author Johnathan Ra
 * @link http://msdn.microsoft.com/en-us/library/office/ms467521.aspx
 */
public abstract class BaseExpression implements Expression {
	private final Field field;
	
	public BaseExpression(Field field) {
		this.field = field;
	}
	
	protected abstract String getNodeName(); 

	@Override
	public void write(XmlCursor xmlCursor) {
		xmlCursor.beginElement(getNodeName());		
		field.write(xmlCursor);
		xmlCursor.toParent(); // move to NODENAME_NOTEQUALS
	}
}

