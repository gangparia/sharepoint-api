package com.jstrgames.sharepoint.query;

/**
 * represents an Equals expression
 * 
 * @author Johnathan Ra
 *
 */
public class Equals extends BaseExpression {

	public Equals(Field field) {
		super(field);
	}

	@Override
	protected String getNodeName() {
		return NODENAME_EQUALS;
	}

}
