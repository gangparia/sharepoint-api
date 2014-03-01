package com.jstrgames.sharepoint.query;

/**
 * represents an And condition
 * 
 * @author Johnathan Ra
 *
 */
public class And extends BaseCondition {

	public And(Condition left, Condition right) {
		super(left, right);
	}
	
	public And(Condition left, Expression right) {
		super(left, right);
	}
	
	public And(Expression left, Condition right) {
		super(left, right);
	}
	
	public And(Expression left, Expression right) {
		super(left, right);
	}

	@Override
	protected String getNodeName() {
		return NODENAME_AND;
	}
	
}
