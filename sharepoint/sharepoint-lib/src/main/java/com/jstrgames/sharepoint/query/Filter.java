package com.jstrgames.sharepoint.query;

import org.apache.xmlbeans.XmlCursor;

import com.microsoft.schemas.sharepoint.soap.GetListItemsDocument.GetListItems.Query;

/**
 * represents sharepoint query Filter
 * 
 * @author Johnathan Ra
 *
 */
public class Filter {

	private Where clause;
	
	public Query generateQuery() {
		Query query = Query.Factory.newInstance();
		final XmlCursor xmlCursor = query.newCursor();
		xmlCursor.toNextToken();
		xmlCursor.beginElement(Queryable.NODENAME_QUERY);
		
		this.clause.write(xmlCursor);
		xmlCursor.toParent(); // move to NODENAME_QUERY
		
		return query;
	}
	
	/**
	 * Filter.newFilter().where(and(equals(x,y), notEquals(x,y)))
	 * 
	 * @return
	 */
	public static Filter newFilter() {
		return new Filter();
	}

	public Filter where(Condition condition) {
		this.clause = new Where(condition);
		return this;
	}
	
	public Filter where(Expression expression) {
		this.clause = new Where(expression);
		return this;
	}
}
