package com.jstrgames.sharepoint.query;

/**
 * represents the less than expression
 * 
 * @author Johnathan Ra
 *
 */
public class LessThan extends BaseExpression {
	
	public LessThan(Field field) {
		super(field);
	}

	@Override
	protected String getNodeName() {
		return NODENAME_LT;
	}

}
