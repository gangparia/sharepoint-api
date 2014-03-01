package com.jstrgames.sharepoint.query;

/**
 * represents the Or condition
 * 
 * @author Johnathan Ra
 *
 */
public class Or extends BaseCondition {

	public Or(Condition left, Condition right) {
		super(left, right);
	}
	
	public Or(Condition left, Expression right) {
		super(left, right);
	}
	
	public Or(Expression left, Condition right) {
		super(left, right);
	}
	
	public Or(Expression left, Expression right) {
		super(left, right);
	}

	@Override
	protected String getNodeName() {
		return NODENAME_OR;
	}
	

}
