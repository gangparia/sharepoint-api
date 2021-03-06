package com.jstrgames.sharepoint.query;

import static org.junit.Assert.*;
import static com.jstrgames.sharepoint.query.Where.*; 

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;

public class TestAnd {
	private static final String SCENARIO1_FIELD1_NAME = "foo";
	private static final String SCENARIO1_FIELD1_VALUE = "bar";
	private static final String SCENARIO1_FIELD2_NAME = "hello";
	private static final String SCENARIO1_FIELD2_VALUE = "world";
	private static final String EXPECTED_SCENARIO1_XML = 
			"<And><Eq><FieldRef Name=\""+SCENARIO1_FIELD1_NAME+
			"\"/><Value Type=\"Text\">"+SCENARIO1_FIELD1_VALUE+
			"</Value></Eq><Neq><FieldRef Name=\""+SCENARIO1_FIELD2_NAME+
			"\"/><Value Type=\"Text\">"+SCENARIO1_FIELD2_VALUE+
			"</Value></Neq></And>";
	
	private static final String SCENARIO2_FIELD1_NAME = "foo";
	private static final String SCENARIO2_FIELD1_VALUE = "bar";
	private static final String SCENARIO2_FIELD2_NAME = "hello";
	private static final String SCENARIO2_FIELD2_VALUE = "world";
	private static final String SCENARIO2_FIELD3_NAME = "hello";
	private static final String SCENARIO2_FIELD3_VALUE = "me";
	private static final String EXPECTED_SCENARIO2_XML = 
			"<And><Eq><FieldRef Name=\""+SCENARIO2_FIELD1_NAME+
			"\"/><Value Type=\"Text\">"+SCENARIO2_FIELD1_VALUE+
			"</Value></Eq><And><Eq><FieldRef Name=\""+SCENARIO2_FIELD2_NAME+
			"\"/><Value Type=\"Text\">"+SCENARIO2_FIELD2_VALUE+
			"</Value></Eq><Neq><FieldRef Name=\""+SCENARIO2_FIELD3_NAME+
			"\"/><Value Type=\"Text\">"+SCENARIO2_FIELD3_VALUE+
			"</Value></Neq></And></And>";

	private static final String SCENARIO3_FIELD1_NAME = "foo";
	private static final String SCENARIO3_FIELD1_VALUE = "bar";
	private static final String SCENARIO3_FIELD2_NAME = "hello";
	private static final String SCENARIO3_FIELD2_VALUE = "world";
	private static final String SCENARIO3_FIELD3_NAME = "hello";
	private static final String SCENARIO3_FIELD3_VALUE = "me";
	private static final String EXPECTED_SCENARIO3_XML = 
			"<And><And><Eq><FieldRef Name=\""+SCENARIO3_FIELD1_NAME+
			"\"/><Value Type=\"Text\">"+SCENARIO3_FIELD1_VALUE+
			"</Value></Eq><Eq><FieldRef Name=\""+SCENARIO3_FIELD2_NAME+
			"\"/><Value Type=\"Text\">"+SCENARIO3_FIELD2_VALUE+
			"</Value></Eq></And><Neq><FieldRef Name=\""+SCENARIO3_FIELD3_NAME+
			"\"/><Value Type=\"Text\">"+SCENARIO3_FIELD3_VALUE+
			"</Value></Neq></And>";
	
	private static final String SCENARIO4_FIELD1_NAME = "foo";
	private static final String SCENARIO4_FIELD1_VALUE = "bar";
	private static final String SCENARIO4_FIELD2_NAME = "hello";
	private static final String SCENARIO4_FIELD2_VALUE = "world";
	private static final String SCENARIO4_FIELD3_NAME = "hello";
	private static final String SCENARIO4_FIELD3_VALUE = "me";
	private static final String SCENARIO4_FIELD4_NAME = "foo";
	private static final String SCENARIO4_FIELD4_VALUE = "me";
	private static final String EXPECTED_SCENARIO4_XML = 
			"<And><And><Eq><FieldRef Name=\""+SCENARIO4_FIELD1_NAME+
			"\"/><Value Type=\"Text\">"+SCENARIO4_FIELD1_VALUE+
			"</Value></Eq><Eq><FieldRef Name=\""+SCENARIO4_FIELD2_NAME+
			"\"/><Value Type=\"Text\">"+SCENARIO4_FIELD2_VALUE+
			"</Value></Eq></And><And><Neq><FieldRef Name=\""+SCENARIO4_FIELD3_NAME+
			"\"/><Value Type=\"Text\">"+SCENARIO4_FIELD3_VALUE+
			"</Value></Neq><Eq><FieldRef Name=\""+SCENARIO4_FIELD4_NAME+
			"\"/><Value Type=\"Text\">"+SCENARIO4_FIELD4_VALUE+
			"</Value></Eq></And></And>";
	
	
	private XmlObject root;
	
	@Before
	public void setup() {
		this.root = XmlObject.Factory.newInstance();
	}
	
	@Test
	public void testScenario1_TwoExpressionsGeneratedXml() {
		final XmlCursor xmlCursor = root.newCursor();
		xmlCursor.toNextToken();
		
		final Field field1 = new Field(SCENARIO1_FIELD1_NAME, SCENARIO1_FIELD1_VALUE);
		final Field field2 = new Field(SCENARIO1_FIELD2_NAME, SCENARIO1_FIELD2_VALUE);

		final Condition condition = and(eq(field1), ne(field2));
		condition.write(xmlCursor);
		
		final String actualXml = root.xmlText();
		assertNotNull(actualXml);
		assertEquals(EXPECTED_SCENARIO1_XML, actualXml);
	}
	
	@Test
	public void testScenario2_ExpressionThenAndConditionGeneratedXml() {
		final XmlCursor xmlCursor = root.newCursor();
		xmlCursor.toNextToken();
		
		final Field field1 = new Field(SCENARIO2_FIELD1_NAME, SCENARIO2_FIELD1_VALUE);
		final Field field2 = new Field(SCENARIO2_FIELD2_NAME, SCENARIO2_FIELD2_VALUE);
		final Field field3 = new Field(SCENARIO2_FIELD3_NAME, SCENARIO2_FIELD3_VALUE);
		
		final Condition condition = and(eq(field1), and(eq(field2), ne(field3)));
		condition.write(xmlCursor);
		
		final String actualXml = root.xmlText();
		assertNotNull(actualXml);
		assertEquals(EXPECTED_SCENARIO2_XML, actualXml);
	}
	
	@Test
	public void testScenario3_AndConditionThenExpressionGeneratedXml() {
		final XmlCursor xmlCursor = root.newCursor();
		xmlCursor.toNextToken();
		
		final Field field1 = new Field(SCENARIO3_FIELD1_NAME, SCENARIO3_FIELD1_VALUE);
		final Field field2 = new Field(SCENARIO3_FIELD2_NAME, SCENARIO3_FIELD2_VALUE);
		final Field field3 = new Field(SCENARIO3_FIELD3_NAME, SCENARIO3_FIELD3_VALUE);

		final Condition condition = and(and(eq(field1), eq(field2)), ne(field3));
		condition.write(xmlCursor);
		
		final String actualXml = root.xmlText();
		assertNotNull(actualXml);
		assertEquals(EXPECTED_SCENARIO3_XML, actualXml);
	}
	
	@Test
	public void testScenario4_TwoAndConditionsGeneratedXml() {
		final XmlCursor xmlCursor = root.newCursor();
		xmlCursor.toNextToken();
		
		final Field field1 = new Field(SCENARIO4_FIELD1_NAME, SCENARIO4_FIELD1_VALUE);
		final Field field2 = new Field(SCENARIO4_FIELD2_NAME, SCENARIO4_FIELD2_VALUE);
		final Field field3 = new Field(SCENARIO4_FIELD3_NAME, SCENARIO4_FIELD3_VALUE);
		final Field field4 = new Field(SCENARIO4_FIELD4_NAME, SCENARIO4_FIELD4_VALUE);
		
		final Condition condition = and(and(eq(field1), eq(field2)), 
								  and(ne(field3), eq(field4)));
		condition.write(xmlCursor);
		
		final String actualXml = root.xmlText();
		assertNotNull(actualXml);
		assertEquals(EXPECTED_SCENARIO4_XML, actualXml);
	}
	

}
