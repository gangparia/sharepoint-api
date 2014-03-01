package com.jstrgames.sharepoint.query;

/**
 * represents the not equals expression
 * 
 * @author Johnathan Ra
 *
 */
public class NotEquals extends BaseExpression {
	
	public NotEquals(Field field) {
		super(field);
	}

	@Override
	protected String getNodeName() {
		return NODENAME_NOTEQUALS;
	}

}
