package com.jstrgames.sharepoint.query;

import static org.junit.Assert.*;
import static com.jstrgames.sharepoint.query.Where.*;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;

import com.microsoft.schemas.sharepoint.soap.GetListItemsDocument.GetListItems.Query;

public class TestFilter {
	private static final String SCENARIO1_FIELD_NAME = "foo";
	private static final String SCENARIO1_FIELD_VALUE = "bar";
	private static final String EXPECTED_SCENARIO1_XML = 
			"<Query><Where><Eq><FieldRef Name=\"" + SCENARIO1_FIELD_NAME +
			"\"/><Value Type=\"Text\">" + SCENARIO1_FIELD_VALUE +
			"</Value></Eq></Where></Query>";
	
	@Test
	public void testScenario1_DefaultTypeFieldGeneratedXml() {
		final Field field = new Field(SCENARIO1_FIELD_NAME, SCENARIO1_FIELD_VALUE);
		final Filter filter = Filter.newFilter().where(eq(field));
		final Query query = filter.generateQuery();
		
		final StringWriter writer = new StringWriter();
		try {
			query.save(writer);
			final String actualXml = writer.toString();
			assertNotNull(actualXml);
			assertEquals(EXPECTED_SCENARIO1_XML, actualXml);
		} catch (IOException e) {
			fail("failed to write content");
		}
	}

}
