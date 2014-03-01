package com.jstrgames.sharepoint.query;

public interface Expression extends Queryable {
	public static final String NODENAME_EQUALS = Type.EQUALS.toString();	
	public static final String NODENAME_GT = Type.GT.toString();
	public static final String NODENAME_LT = Type.LT.toString();
	public static final String NODENAME_NOTEQUALS = Type.NOTEQUALS.toString();
	
	public enum Type { 
		EQUALS, 		
		GT,
		LT,
		NOTEQUALS;
		
		@Override
		public String toString() {
			String retVal = null;
			switch(this) {
				case EQUALS:
					retVal = "Eq";
					break;
				case GT:
					retVal = "Gt";
					break;
				case LT:
					retVal = "Lt";
					break;
				case NOTEQUALS:
					retVal = "Neq";
					break;
				default:
					// Should not get here
					break;
			}
			return retVal;
		}
		
	}
	
}
