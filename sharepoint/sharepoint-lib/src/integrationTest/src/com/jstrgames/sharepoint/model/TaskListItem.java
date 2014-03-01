package com.jstrgames.sharepoint.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.xmlbeans.XmlCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.jstrgames.sharepoint.TestIntegrationListService;
import com.jstrgames.sharepoint.element.Command;
import com.jstrgames.sharepoint.query.Field;

/**
 * This is an sample ListItem implementation for integration testing. Given a list 
 * item rows, it will return a single object representing a z:row with only the 
 * the fields of interest. In this case, only ows_ID, ows_UID, ows_AssignedTo, and 
 * ows_DueDate are retrieved.
 * 
 * @author Johnathan Ra
 *
 */
public class TaskListItem implements Retrievable, Executable {
	private static final Logger LOG = LoggerFactory.getLogger(TaskListItem.class);
	private static final SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static final String FIELDNAME_TITLE = "Title";
	
	private final Command cmd;
	private boolean hasFile;
	private int id;
	private String uid;
	private String title;
	private TestPerson assignedTo;
	private Date dueDate;
	
	public TaskListItem() {
		this.cmd = Command.RETRIEVE;
	}
	
	public TaskListItem(Command cmd) {
		this.cmd = cmd;
	}
			
	public int getId() {
		return this.id;
	}
	
	public String getUid() {
		return this.uid;
	}
	
	public TestPerson getAssignedTo (){
		return this.assignedTo;
	}
	
	public Date getDueDate() {
		return this.dueDate;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}	

	@Override
	public Command getCommand() {
		return this.cmd;
	}
	
	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public boolean isMatch(Node node) {
		boolean retVal = false;
		final NamedNodeMap attributes = node.getAttributes();
		if(attributes == null) {
			LOG.warn("Failed to retrieve attributes for z:row");			
		} else if(this.title != null) {
			// title is the unique field for our scenario
			final Node titleAttr = 
					attributes.getNamedItem(DEFAULT_PREFIX + FIELDNAME_TITLE);
			
			if(this.title.equals(titleAttr.getNodeValue())) {
				retVal = true;
			}
		}
		return retVal;
	}
	
	@Override
	public boolean hasAttachment() {
		return this.hasFile;
	}
	
	@Override
	public void write(XmlCursor xmlCursor) {
		xmlCursor.beginElement(NODENAME_FIELD);
		xmlCursor.insertAttributeWithValue(ATTRIBUTE_NAME, FIELDNAME_ID);
		if(this.cmd == Command.CREATE) {
			xmlCursor.insertChars(this.cmd.toString());
		} else {
			xmlCursor.insertChars(String.valueOf(this.id));
		}
		xmlCursor.toNextToken();
		xmlCursor.beginElement(NODENAME_FIELD);
		xmlCursor.insertAttributeWithValue(ATTRIBUTE_NAME, FIELDNAME_TITLE);
		xmlCursor.insertChars(this.title);
	}
	
	@Override
	public void fromNode(Node node) {
		final NamedNodeMap attributes = node.getAttributes();
		int size = attributes.getLength();			
		for(int i = 0; i < size; i++) {
			final Node namedAttribute = attributes.item(i);
			final String name = namedAttribute.getNodeName();
			final String value = namedAttribute.getNodeValue();
			
			if(name.equals(TestIntegrationListService.STANDARD_SHAREPOINT_PROPERTY_ID)) {
				this.id = Integer.valueOf(value);
			} else if(name.equals(TestIntegrationListService.STANDARD_SHAREPOINT_PROPERTY_UNIQUEID)){
				this.uid = value;
			} else if(name.equals(DEFAULT_PREFIX + TestIntegrationListService.FIELD_NAME_ASSIGNEDTO)) {
				this.assignedTo = new TestPerson(value);
			} else if(name.equals(DEFAULT_PREFIX + FIELDNAME_TITLE)) {
				this.title = value;
			} else if(name.equals(DEFAULT_PREFIX + TestIntegrationListService.FIELD_NAME_DUEDATE)) {
				try {
					this.dueDate = parser.parse(value);
				} catch (ParseException e) {
					this.dueDate = null;
				}
			}
		}
	}
	
	/**
	 * Test class representing SharePoint Account.
	 * 
	 * @author Johnathan Ra
	 *
	 */
	public class TestPerson {
		private final int id;
		private final String name;
		
		public TestPerson(String text) {
			int pivot = text.indexOf(Field.LOOKUP_DELIMITER);
			this.id = Integer.valueOf(text.substring(0, pivot));
			this.name = text.substring(pivot+Field.LOOKUP_DELIMITER.length());
		}
		
		public int getId() {
			return this.id;
		}
		
		public String getName() {
			return this.name;
		}
		
	}

}
