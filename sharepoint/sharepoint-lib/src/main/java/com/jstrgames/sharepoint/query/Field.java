package com.jstrgames.sharepoint.query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.xmlbeans.XmlCursor;

/**
 * represents SharePoint List field element
 * 
 * @author Johnathan Ra
 *
 */
public class Field implements Queryable {	
	public static final String CAML_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String LOOKUP_DELIMITER = ";#";
	
	private static final String ATTRIBUTE_FIELDREF_NAME = "Name";
	private static final String ATTRIBUTE_FIELDREF_TYPE = "Type";
	
	// use thread-safe formatter
	private static final ThreadLocal<DateFormat> THREAD_LOCAL_DATEFORMAT =
			new ThreadLocal<DateFormat>() {
			    protected DateFormat initialValue() {
			        return new SimpleDateFormat(CAML_DATETIME_FORMAT);
			    }
			};
	
	public enum Type {
		DEFAULT("Text"),		
		DATETIME("DateTime"),
		NUMBER("Number"),
		TEXT("Text");
		
		private String text;
		private Type(String value) {
			this.text = value;
		}
		
		public static Type fromString(String value) {
			Type retVal = null;
			if(DATETIME.toString().equals(value)) {
				retVal = DATETIME;
			} else if(NUMBER.toString().equals(value)) {
				retVal = NUMBER;
			} else if(TEXT.toString().equals(value)) {
				retVal = TEXT;
			} else {
				retVal = DEFAULT;
			}
			return retVal;
		}
		
		@Override
		public String toString() {
			return this.text;
		}
	}
	
	private final String name;
	private final Object value;
	private final Type type;
	
	public Field(String name, Object value) {
		this.name = name;
		this.value = value;
		this.type = Type.DEFAULT;
	}
	
	public Field(String name, Date value) {
		this.name = name;
		this.value = value;
		this.type = Type.DATETIME;
	}
	
	public Field(String name, String value) {
		this.name = name;
		this.value = value;
		this.type = Type.TEXT;
	}
	
	public Field(String name, Integer value) {
		this.name = name;
		this.value = value;
		this.type = Type.NUMBER;
	}

	public String getName() {
		return this.name;
	}
	
	public Object getValue() {
		return this.value;
	}
	
	public Type getType() {
		return this.type;
	}
	
	private String dateTimeValue() {
		DateFormat df = THREAD_LOCAL_DATEFORMAT.get();
		return df.format(this.value);
	}
	
	@Override
	public void write(XmlCursor xmlCursor) {
		xmlCursor.beginElement(NODENAME_FIELDREF);
		xmlCursor.insertAttributeWithValue(ATTRIBUTE_FIELDREF_NAME, this.name);
		xmlCursor.toNextToken();
		xmlCursor.beginElement(NODENAME_FIELDREF_VALUE);
		xmlCursor.insertAttributeWithValue(ATTRIBUTE_FIELDREF_TYPE, 
										   this.type.toString());
		switch(this.type) {
			case DATETIME:
				xmlCursor.insertChars(dateTimeValue());
				break;
			case TEXT:
				xmlCursor.insertChars((String)this.value);
				break;
			case NUMBER:	
			default:
				xmlCursor.insertChars(String.valueOf(this.value));				
				break;
				 
		}
		xmlCursor.toParent(); // move to NODENAME_FIELDREF_VALUE		
	}

}
