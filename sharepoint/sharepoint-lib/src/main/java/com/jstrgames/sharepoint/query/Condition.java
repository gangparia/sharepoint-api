package com.jstrgames.sharepoint.query;

public interface Condition extends Queryable {
	public static final String NODENAME_AND = Type.AND.toString();	
	public static final String NODENAME_OR = Type.OR.toString();
	
	public enum Type { 
		AND,
		OR;
		
		@Override
		public String toString() {
			String retVal = null;
			switch(this) {
				case AND:
					retVal = "And";
					break;
				case OR:
					retVal = "Or";
					break;
				default:
					// Should not get here
					break;
			}
			return retVal;
		}
	};
}
