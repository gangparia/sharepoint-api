package com.jstrgames.sharepoint.query;

/**
 * represents the greater than expression
 * 
 * @author Johnathan Ra
 *
 */
public class GreaterThan extends BaseExpression {
	
	public GreaterThan(Field field) {
		super(field);
	}

	@Override
	protected String getNodeName() {
		return NODENAME_GT;
	}

}
