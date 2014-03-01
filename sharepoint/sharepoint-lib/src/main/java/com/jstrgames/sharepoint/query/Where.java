package com.jstrgames.sharepoint.query;

import org.apache.xmlbeans.XmlCursor;

/**
 * root of clause must be an single expression or a condition. this class 
 * implements fluent interface for readable query building:
 * 
 * For example, the following construct will filter where (startDate > 1/1/2004 
 * and endDate < 12/31/2004) and assignedTo = 'SP Demo'
 * 
 * Filter.newFilter().where( and(and(gt(fieldOne),lt(fieldTwo)),
 *                               eq(fieldThree)));
 * 
 * @author Johnathan Ra
 *
 */
public class Where implements Queryable {
	public static final String NODENAME_WHERE = "Where";
	
	private Condition condition;
	private Expression expression;
	
	public Where(Expression expression) {
		this.condition = null;
		this.expression = expression;
	}
	
	public Where(Condition condition) {
		this.condition = condition;
		this.expression = null;
	}
	
	@Override
	public void write(XmlCursor xmlCursor) {
		xmlCursor.beginElement(NODENAME_WHERE);
		if(condition != null) {
			this.condition.write(xmlCursor);
		} else {
			this.expression.write(xmlCursor);
		}
		xmlCursor.toParent(); // move to NODENAME_WHERE
	}

	// following are fluent interfaces
	// the should be imported as static methods
	public static Condition and(Expression left, Expression right) {
		return new And(left, right);
	}
	
	public static Condition and(Expression left, Condition right) {
		return new And(left, right);
	}
	
	public static Condition and(Condition left, Expression right) {
		return new And(left, right);
	}
	
	public static Condition and(Condition left, Condition right) {
		return new And(left, right);
	}
	
	public static Condition or(Expression left, Expression right) {
		return new Or(left, right);
	}
	
	public static Condition or(Expression left, Condition right) {
		return new Or(left, right);
	}
	
	public static Condition or(Condition left, Expression right) {
		return new Or(left, right);
	}
	
	public static Condition or(Condition left, Condition right) {
		return new Or(left, right);
	}
	
	public static Expression eq(Field field) {
		return new Equals(field);
	}
	
	public static Expression ne(Field field) {
		return new NotEquals(field);
	}
	
	public static Expression lt(Field field) {
		return new LessThan(field);
	}
	
	public static Expression gt(Field field) {
		return new GreaterThan(field);
	}
}
