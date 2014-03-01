package com.jstrgames.sharepoint;

import static org.junit.Assert.*;
import static com.jstrgames.sharepoint.query.Where.*;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.axis2.transport.http.HttpTransportProperties;
import org.junit.Before;
import org.junit.Test;

import com.microsoft.sharepoint.ListsStub;
import com.jstrgames.sharepoint.element.Command;
import com.jstrgames.sharepoint.model.JsonListItem;
import com.jstrgames.sharepoint.model.TaskListItem;
import com.jstrgames.sharepoint.query.Field;
import com.jstrgames.sharepoint.query.Filter;

/**
 * This class will perform integration test against publicly accessible SharePoint 2007 site.
 * 
 * @author Johnathan Ra
 */
public class TestIntegrationListService {
	public static final String LIST_GUID = "{699DF22B-2F89-4178-AA2D-B0C2CB073976}";
	public static final String VIEW_GUID = "{51451612-1648-4DBA-975C-AE66D856CBDF}";
	public static final String TARGET_URL = "http://spdemo.utopiasp.net/_vti_bin/Lists.asmx";
		
	public static final String ATTACHMENT_FILENAME1 = "foobar1.txt";
	public static final String ATTACHMENT_FILECONTENT1 = "hello world!";
	public static final String ATTACHMENT_FILENAME2 = "foobar2.txt";
	public static final String ATTACHMENT_FILECONTENT2 = "hello me!";
	
	public static final String FIELD_NAME_ASSIGNEDTO = "AssignedTo";
	public static final String FIELD_NAME_ASSIGNEDTO_SPDEMO = "SP Demo";
	public static final String FIELD_NAME_ASSIGNEDTO_SPDEMOID = "4";
	public static final String FIELD_NAME_STATUS = "Status";
	public static final String FIELD_NAME_STATUS_COMPLETED = "Completed";
	public static final String FIELD_NAME_STATUS_INPROGRESS = "In Progress";
	public static final String FIELD_NAME_DUEDATE = "DueDate";
	public static final String FIELD_NAME_DUEDATE_VALUE1 = "2004-01-01T00:00:00Z";
	public static final String FIELD_NAME_DUEDATE_VALUE2 = "2004-12-31T00:00:00Z";
	
	public static final String STANDARD_SHAREPOINT_PROPERTY_ID = "ows_ID";
	public static final String STANDARD_SHAREPOINT_PROPERTY_UNIQUEID = "ows_UniqueId";
	
	public static final String USERNAME = "spdemo";
	public static final String PASSWORD = "utopia";
	
	private final SimpleDateFormat parser = new SimpleDateFormat(Field.CAML_DATETIME_FORMAT);	
	private ListsStub soapEndPoint;
	private Configuration config;
		
	@Before
	public void setUp() throws Exception {
		final HttpTransportProperties.Authenticator auth = 
				new HttpTransportProperties.Authenticator();
		auth.setUsername(USERNAME);
		auth.setPassword(PASSWORD);
		auth.setPreemptiveAuthentication(true);
		
		this.config = new TestConfiguration(auth, LIST_GUID, VIEW_GUID);
		this.soapEndPoint = new ListsStub(TARGET_URL);
	}

	/**
	 * Scenario #1: retrieval all tasks from the task list in spdemo.utopiasp.net web site
	 * 
	 * @link http://spdemo.utopiasp.net/Lists/Tasks/AllItems.aspx 
	 */
	@Test
	public void testScenario1_Default() {		
		final ListService<JsonListItem> service = 
				new ListService<JsonListItem>(config, soapEndPoint, JsonListItem.class);
		try {
			List<JsonListItem> result = service.retrieve();
			for(JsonListItem item : result) {
				String json = item.getJson();
				
				assertNotNull(json);
				assertTrue(json.contains(STANDARD_SHAREPOINT_PROPERTY_ID));
				assertTrue(json.contains(STANDARD_SHAREPOINT_PROPERTY_UNIQUEID));
			}
		} catch (ServiceUnreachableException e) {
			fail("failed to reach SharePoint Service!");
		}
	}
	
	/**
	 * Scenarios #2: retrieve only task items where status is "Completed" 
	 * 
	 * Note: inbound field name should not be pre-pended with "ows_". However, outbound
	 * field names will always have "ows_" pre-pended.
	 */
	@Test
	public void testScenario2_WithSimpleExpressionFilter() {		
		final ListService<JsonListItem> service = 
				new ListService<JsonListItem>(config, soapEndPoint, JsonListItem.class);
		final Field field = new Field(FIELD_NAME_STATUS, FIELD_NAME_STATUS_COMPLETED);
		
		// where status = "Completed"
		final Filter filter = Filter.newFilter().where(eq(field));
		
		try {
			List<JsonListItem> result = service.retrieve(filter);
			for(JsonListItem item : result) {
				String json = item.getJson();
				
				assertNotNull(json);
				assertTrue(json.contains(STANDARD_SHAREPOINT_PROPERTY_ID));
				assertTrue(json.contains(STANDARD_SHAREPOINT_PROPERTY_UNIQUEID));
				assertTrue(json.contains("\"ows_" + FIELD_NAME_STATUS + "\": \"" +
										 FIELD_NAME_STATUS_COMPLETED + "\""));
			}
		} catch (ServiceUnreachableException e) {
			fail("failed to reach SharePoint Service!");
		}
	}

	/**
	 * Scenarios #3: retrieve all tasks where status is "In Progress" and is assigned to "SP Demo"
	 */
	@Test
	public void testScenario3_WithSimpleAndConditionFilter() {		
		final ListService<JsonListItem> service = 
				new ListService<JsonListItem>(config, soapEndPoint, JsonListItem.class);
		
		final Field fieldOne = new Field(FIELD_NAME_STATUS, FIELD_NAME_STATUS_INPROGRESS);
		final Field fieldTwo = new Field(FIELD_NAME_ASSIGNEDTO, FIELD_NAME_ASSIGNEDTO_SPDEMO);
		
		// Where status = "In Progress" AND assignedTo = "SP Demo"
		final Filter filter = 
				Filter.newFilter() 
					  .where(and(eq(fieldOne), eq(fieldTwo)));
		
		try {
			List<JsonListItem> result = service.retrieve(filter);
			for(JsonListItem item : result) {
				String json = item.getJson();
				
				assertNotNull(json);
				assertTrue(json.contains(STANDARD_SHAREPOINT_PROPERTY_ID));
				assertTrue(json.contains(STANDARD_SHAREPOINT_PROPERTY_UNIQUEID));
				assertTrue(json.contains("\"ows_" + FIELD_NAME_STATUS + "\": \"" +
										 FIELD_NAME_STATUS_INPROGRESS + "\""));
				// ";#" is the delimiter with left of delimiter representing the id and right representing display name
				assertTrue(json.contains("\"ows_" + FIELD_NAME_ASSIGNEDTO + "\": \""+
										 FIELD_NAME_ASSIGNEDTO_SPDEMOID + Field.LOOKUP_DELIMITER +
										 FIELD_NAME_ASSIGNEDTO_SPDEMO + "\""));
			}
		} catch (ServiceUnreachableException e) {
			fail("failed to reach SharePoint Service!");
		}
	}
	
	/**
	 * Scenario #4: retrieve all tasks where due date is greater than 1/1/2004 but less than 
	 * 				12/31/2004 and is assiged to "SP Demo"
	 */
	@Test
	public void testScenario4_WithComplexAndConditionFilter() {		
		final ListService<TaskListItem> service = 
				new ListService<TaskListItem>(config, soapEndPoint, TaskListItem.class);
		
		try {
			final Date startDate = parser.parse(FIELD_NAME_DUEDATE_VALUE1);
			final Date endDate = parser.parse(FIELD_NAME_DUEDATE_VALUE2);
			
			final Field fieldOne = new Field(FIELD_NAME_DUEDATE, startDate);
			final Field fieldTwo = new Field(FIELD_NAME_DUEDATE, endDate);
			final Field fieldThree = new Field(FIELD_NAME_ASSIGNEDTO, FIELD_NAME_ASSIGNEDTO_SPDEMO);

			// where (startDate > 1/1/2004 and endDate < 12/31/2004) 
			//       and assignedTo = 'SP Demo'
			final Filter filter = 
					Filter.newFilter()
						  .where(and(and(gt(fieldOne),lt(fieldTwo)),
								  	 eq(fieldThree)));
					
			List<TaskListItem> result = service.retrieve(filter);
			for(TaskListItem item : result) {
				assertNotNull(item);
				assertNotNull(item.getUid());
				
				Date actualDate = item.getDueDate();
				assertNotNull(actualDate);
				assertTrue(actualDate.after(startDate));
				assertTrue(actualDate.before(endDate));
				
				TaskListItem.TestPerson actualAssignedTo = item.getAssignedTo();
				assertNotNull(actualAssignedTo);
				assertTrue(Integer.valueOf(FIELD_NAME_ASSIGNEDTO_SPDEMOID) == actualAssignedTo.getId());
				assertEquals(FIELD_NAME_ASSIGNEDTO_SPDEMO, actualAssignedTo.getName());
			}
		} catch (ParseException e) {
			fail("failed to parse test date!");
		} catch (ServiceUnreachableException e) {
			fail("failed to reach SharePoint Service!");
		}
	}
	
	/**
	 * Scenario #5: create a new task, verify new, update it, verify update, attach documents,
	 * verify attachments, delete it, and verify deletion.
	 */
	@Test
	public void testScenario5_CreateNewTask_Then_UpdateTask_Then_AttachToTask_Then_DeleteIt() {
		final String expectedTitle = "JUnit." + System.currentTimeMillis();
		
		final ListService<TaskListItem> service = 
				new ListService<TaskListItem>(config, soapEndPoint, TaskListItem.class);
		final TaskListItem item = new TaskListItem(Command.CREATE);
		item.setTitle(expectedTitle);
		try {
			service.execute(item);
			assertTrue(item.getId() > 0);
			
			// now verify
			final Field field = new Field(TaskListItem.FIELDNAME_ID, item.getId());
			final Filter filter = Filter.newFilter().where(eq(field));
			
			final List<TaskListItem> addResult = service.retrieve(filter);
			assertNotNull(addResult);
			assertTrue(addResult.size() == 1);
			assertEquals(expectedTitle, addResult.get(0).getTitle());
			
			// now update
			final String revisedTitle = "JUnit." + System.currentTimeMillis();
			final TaskListItem updItem = new TaskListItem(Command.UPDATE);
			updItem.setId(item.getId());
			updItem.setTitle(revisedTitle);
			
			// validate update
			service.execute(updItem);
			final List<TaskListItem> updResult = service.retrieve(filter);
			assertNotNull(updResult);
			assertTrue(updResult.size() == 1);
			assertEquals(revisedTitle, updResult.get(0).getTitle());
			
			// now add 2 attachments
			service.attachFile(item.getId(), 
							   ATTACHMENT_FILENAME1, 
							   ATTACHMENT_FILECONTENT1.getBytes());
			service.attachFile(item.getId(), 
							   ATTACHMENT_FILENAME2, 
							   ATTACHMENT_FILECONTENT2.getBytes());
			
			// validate 2 attachments
			List<URL> attachments = service.getAttachments(item.getId());
			assertNotNull(attachments);
			assertTrue(attachments.size() == 2);
			assertTrue(attachments.get(0).getFile().endsWith(ATTACHMENT_FILENAME1));
			assertTrue(attachments.get(1).getFile().endsWith(ATTACHMENT_FILENAME2));
			
			// now delete
			final TaskListItem delItem = new TaskListItem(Command.DELETE);
			delItem.setId(item.getId());
			delItem.setTitle(expectedTitle);
			
			// validate delete
			service.execute(delItem);
			final List<TaskListItem> delResult = service.retrieve(filter);
			assertNotNull(delResult);
			assertTrue(delResult.size() == 0);
			
		} catch (ServiceUnreachableException e) {
			fail("failed to reach SharePoint Service!");
		}
	}
}
