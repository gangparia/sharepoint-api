package com.jstrgames.sharepoint.query;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;

public class TestField {
	private static final String SCENARIO1_FIELD_NAME = "foo";
	private static final String SCENARIO1_FIELD_VALUE = "bar";
	// note, we expect to xml to be wrap with <xml-fragment></xml-fragment> as Field is 
	// not a well-formed xml. (i.e has no root note)
	private static final String EXPECT_SCENARIO1_XML = 
			"<xml-fragment><FieldRef Name=\"foo\"/><Value Type=\"Text\">bar</Value></xml-fragment>";

	private static final String SCENARIO2_FIELD_NAME = "foobar";
	private static final String SCENARIO2_FIELD_VALUE = "2011-02-07T00:00:00Z";
	private static final String EXPECT_SCENARIO2_XML = 
			"<xml-fragment><FieldRef Name=\"foobar\"/><Value Type=\"DateTime\">" +
			SCENARIO2_FIELD_VALUE +
			"</Value></xml-fragment>";

	
	private XmlObject root;
	
	@Before
	public void setup() {
		this.root = XmlObject.Factory.newInstance();
	}
	
	@Test
	public void testScenario1_DefaultTypeFieldGeneratedXml() {
		final XmlCursor xmlCursor = root.newCursor();
		xmlCursor.toNextToken();
		
		final Field field = new Field(SCENARIO1_FIELD_NAME, SCENARIO1_FIELD_VALUE);
		field.write(xmlCursor);
		
		final String actualXml = this.root.xmlText();
		assertNotNull(actualXml);
		assertEquals(EXPECT_SCENARIO1_XML, actualXml);
	}

	@Test
	public void testScenario2_DateTimeTypeFieldGeneratedXml() {
		final XmlCursor xmlCursor = root.newCursor();
		xmlCursor.toNextToken();
		final SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); 
		
		try {
			final Field field = new Field(SCENARIO2_FIELD_NAME, 
							  			  parser.parse(SCENARIO2_FIELD_VALUE));
			
			field.write(xmlCursor);
			
			String actualXml = this.root.xmlText();
			assertNotNull(actualXml);
			assertEquals(EXPECT_SCENARIO2_XML, actualXml);
			
		} catch (ParseException e) {
			fail("failed to parse test date!");
		}

	}

}
