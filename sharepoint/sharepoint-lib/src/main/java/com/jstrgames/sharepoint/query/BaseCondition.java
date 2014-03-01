package com.jstrgames.sharepoint.query;

import org.apache.xmlbeans.XmlCursor;

/**
 * Base condition defined by CAML comparison
 * 
 * @author Johnathan Ra
 * @link http://msdn.microsoft.com/en-us/library/office/ms467521.aspx
 */
public abstract class BaseCondition implements Condition {

	private final Condition leftCondtion;
	private final Condition rightCondtion;
	private final Expression leftExpression;
	private final Expression rightExpression;
	
	public BaseCondition(Condition left, Condition right) {
		this.leftCondtion = left;
		this.rightCondtion = right;
		this.leftExpression = null;
		this.rightExpression = null;
	}
	
	public BaseCondition(Condition left, Expression right) {
		this.leftCondtion = left;
		this.rightCondtion = null;
		this.leftExpression = null;
		this.rightExpression = right;
	}
	
	public BaseCondition(Expression left, Condition right) {
		this.leftCondtion = null;
		this.rightCondtion = right;
		this.leftExpression = left;
		this.rightExpression = null;
	}
	
	public BaseCondition(Expression left, Expression right) {
		this.leftCondtion = null;
		this.rightCondtion = null;
		this.leftExpression = left;
		this.rightExpression = right;
	}
	
	protected abstract String getNodeName(); 
	
	@Override
	public void write(XmlCursor xmlCursor) {
		xmlCursor.beginElement(getNodeName());
		if(this.rightCondtion != null) {
			this.rightCondtion.write(xmlCursor);
		} else {
			this.rightExpression.write(xmlCursor);
		}
		xmlCursor.toParent(); // move to NODENAME_AND
		xmlCursor.toNextToken(); 
		if(this.leftCondtion != null) {
			this.leftCondtion.write(xmlCursor);
		} else {
			this.leftExpression.write(xmlCursor);
		}
		xmlCursor.toParent(); // move to NODENAME_AND
	}

}
