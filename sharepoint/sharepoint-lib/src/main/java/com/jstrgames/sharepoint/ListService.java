package com.jstrgames.sharepoint;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.microsoft.schemas.sharepoint.soap.AddAttachmentDocument;
import com.microsoft.schemas.sharepoint.soap.AddAttachmentDocument.AddAttachment;
import com.microsoft.schemas.sharepoint.soap.AddAttachmentResponseDocument;
import com.microsoft.schemas.sharepoint.soap.GetAttachmentCollectionDocument;
import com.microsoft.schemas.sharepoint.soap.GetAttachmentCollectionDocument.GetAttachmentCollection;
import com.microsoft.schemas.sharepoint.soap.GetAttachmentCollectionResponseDocument;
import com.microsoft.schemas.sharepoint.soap.GetAttachmentCollectionResponseDocument.GetAttachmentCollectionResponse;
import com.microsoft.schemas.sharepoint.soap.GetAttachmentCollectionResponseDocument.GetAttachmentCollectionResponse.GetAttachmentCollectionResult;
import com.microsoft.schemas.sharepoint.soap.GetListItemsDocument;
import com.microsoft.schemas.sharepoint.soap.GetListItemsDocument.GetListItems;
import com.microsoft.schemas.sharepoint.soap.GetListItemsResponseDocument;
import com.microsoft.schemas.sharepoint.soap.GetListItemsResponseDocument.GetListItemsResponse;
import com.microsoft.schemas.sharepoint.soap.GetListItemsResponseDocument.GetListItemsResponse.GetListItemsResult;
import com.microsoft.schemas.sharepoint.soap.UpdateListItemsDocument;
import com.microsoft.schemas.sharepoint.soap.UpdateListItemsDocument.UpdateListItems;
import com.microsoft.schemas.sharepoint.soap.UpdateListItemsResponseDocument;
import com.microsoft.schemas.sharepoint.soap.UpdateListItemsResponseDocument.UpdateListItemsResponse;
import com.microsoft.schemas.sharepoint.soap.UpdateListItemsResponseDocument.UpdateListItemsResponse.UpdateListItemsResult;
import com.microsoft.sharepoint.ListsStub;

import com.jstrgames.sharepoint.element.Batch;
import com.jstrgames.sharepoint.element.Command;
import com.jstrgames.sharepoint.element.Method;
import com.jstrgames.sharepoint.model.Retrievable;
import com.jstrgames.sharepoint.model.Executable;
import com.jstrgames.sharepoint.query.Filter;

/**
 * this is a wrapper class to interact with SharePoint List Soap Service. 
 * 
 * @author Johnathan Ra
 * @param <T>
 */
public class ListService<T> {
	private static final Logger LOG = LoggerFactory.getLogger(ListService.class);
	
	private static final String LISTRESULT_PRIMARY_NODE_NAME = "listitems";
	private static final String LISTRESULT_ROW_NODE_NAME = "z:row";
	private static final String RESULT_ID_ATTRIBUTE_NAME = "ID";
	
	private final ListsStub soapEndPoint;
	private final Configuration config;
	private final Class<T> expectedClass;
		
	/**
	 * default constructor
	 * 
	 * @param listProp
	 * @param soapService
	 * @param expectedClass
	 */
	public ListService(Configuration config, ListsStub soapService, Class<T> expectedClass) {
		if(! Retrievable.class.isAssignableFrom(expectedClass)) {
			LOG.error("Specified class does not implement ListItem interface " + 
					  expectedClass.getCanonicalName());
			throw new InvalidListItemException(
					"Specified class does not implement ListItem interface " + 
					expectedClass.getCanonicalName());
		}		
		this.soapEndPoint = soapService;		
		this.config = config;
		this.expectedClass = expectedClass;
		setupOptions();
	}
	
	/**
	 * attach specified file to associated SharePoint ListItem id
	 * 
	 * @param listItemId
	 * @param file
	 * @throws ServiceUnreachableException
	 * @throws IOException
	 */
	public void attachFile(int listItemId, File file) 
			throws ServiceUnreachableException, IOException {
		final byte[] fileBytes = FileUtils.readFileToByteArray(file);
		attachFile(listItemId, file.getName(), fileBytes);
	}
	
	/**
	 * attach specified file byte array to associated SharePoint ListItem id
	 * 
	 * @param listItemId
	 * @param fileName
	 * @param fileBytes
	 * @throws ServiceUnreachableException
	 */
	public void attachFile(int listItemId, String fileName, byte[] fileBytes) 
			throws ServiceUnreachableException {
		final AddAttachment attachment = AddAttachment.Factory.newInstance();
		attachment.setFileName(fileName);
		attachment.setAttachment(fileBytes);
		attachment.setListItemID(String.valueOf(listItemId));
		attachment.setListName(this.config.getListGUID());
		
		callSoapService(attachment);
	}
	
	/**
	 * retrieve attachment URL locations for specified SharePoint ListItem id.
	 * 
	 * @param listItemId
	 * @return
	 * @throws ServiceUnreachableException
	 */
	public List<URL> getAttachments(int listItemId) throws ServiceUnreachableException {
		final List<URL> listFileURL = new LinkedList<URL>();
		final GetAttachmentCollection getAttachmentCollection = 
				GetAttachmentCollection.Factory.newInstance();
		getAttachmentCollection.setListItemID(String.valueOf(listItemId));
		getAttachmentCollection.setListName(this.config.getListGUID());
		
		callSoapService(getAttachmentCollection, listFileURL);
		
		return listFileURL;
	}
	
	/**
	 * this method will return all items within the target list/view 
	 * 
	 * @return
	 */
	public List<T> retrieve() throws ServiceUnreachableException {
		final GetListItems targetList = GetListItems.Factory.newInstance();
		targetList.setListName(config.getListGUID());
		targetList.setViewName(config.getViewGUID());
		targetList.setRowLimit(String.valueOf(config.getRowLimit()));
		
		return retrieve(targetList);
	}
	
	/**
	 * this method will return only the filtered items from target list/view
	 *  
	 * @param filter
	 * @return
	 */
	public List<T> retrieve(Filter filter) throws ServiceUnreachableException {
		final GetListItems targetList = GetListItems.Factory.newInstance();
		targetList.setListName(config.getListGUID());
		targetList.setViewName(config.getViewGUID());
		targetList.setRowLimit(String.valueOf(config.getRowLimit()));
		targetList.setQuery(filter.generateQuery());
		
		return retrieve(targetList);
	}
	
	/**
	 * execute specified Executable (CREATE, UPDATE, or DELETE) item
	 * 
	 * @param item
	 * @throws ServiceUnreachableException
	 */
	public void execute(Executable item) throws ServiceUnreachableException {
		List<Executable> list = new LinkedList<Executable>();
		list.add(item);
		
		execute(list);
	}
		
	/**
	 * execute collection of Executable (CREATE, UPDATE, & DELETE) item
	 * 
	 * @param list
	 * @throws ServiceUnreachableException
	 */
	public void execute(List<Executable> list) throws ServiceUnreachableException {		
		final Method method = new Method(list);
		final Batch batch = new Batch(method, this.config.getViewGUID());
		
		final UpdateListItems updateItems = UpdateListItems.Factory.newInstance();
		updateItems.setListName(this.config.getListGUID());
		updateItems.setUpdates(batch.generateUpdates());
		
		callSoapService(updateItems, list);
	}
	
	/**
	 * helper method to send batch updates to sharepoint service endpoint
	 * 
	 * @param updateItems
	 * @throws ServiceUnreachableException
	 */
	protected void callSoapService(final UpdateListItems updateItems, List<Executable> list) 
			throws ServiceUnreachableException {
		final UpdateListItemsDocument command = UpdateListItemsDocument.Factory.newInstance();
		command.setUpdateListItems(updateItems);
		try {
			UpdateListItemsResponseDocument response = this.soapEndPoint.updateListItems(command);
			if(LOG.isDebugEnabled()) { 
				LOG.debug(response.toString());
			}
			parseUpdateResponse(response, list);
			
		} catch (RemoteException e) {
			LOG.error("Failed to connect to specified resources!");
			throw new ServiceUnreachableException(
					"Failed to connect to specified resources!", e);
		}		
	}
	
	protected void callSoapService(final AddAttachment addAttachment) 
			throws ServiceUnreachableException {
		final AddAttachmentDocument command = AddAttachmentDocument.Factory.newInstance();
		command.setAddAttachment(addAttachment);
		
		try {
			final AddAttachmentResponseDocument response = 
					this.soapEndPoint.addAttachment(command);
			if(LOG.isDebugEnabled()) { 
				LOG.debug(response.toString());
			}
		} catch (RemoteException e) {
			LOG.error("Failed to connect to specified resources!");
			throw new ServiceUnreachableException(
					"Failed to connect to specified resources!", e);
		}
	}
	
	protected void callSoapService(final GetAttachmentCollection getAttachmentCollection,
								   final List<URL> listFileURL) 
		throws ServiceUnreachableException {
		final GetAttachmentCollectionDocument command = 
				GetAttachmentCollectionDocument.Factory.newInstance();
		command.setGetAttachmentCollection(getAttachmentCollection);
		
		try {
			final GetAttachmentCollectionResponseDocument response = 
					this.soapEndPoint.getAttachmentCollection(command);
			
			if(LOG.isDebugEnabled()) { 
				LOG.debug(response.toString());
			}
			
			parseAttachmentResponse(response, listFileURL);
			
		} catch (RemoteException e) {
			LOG.error("Failed to connect to specified resources!");
			throw new ServiceUnreachableException(
					"Failed to connect to specified resources!", e);
		}
	}
	
	/**
	 * helper method to send query to sharepoint service endpoint
	 * 
	 * @param targetList
	 * @return
	 * @throws ServiceUnreachableException
	 */
	protected GetListItemsResult callSoapService(final GetListItems targetList) 
			throws ServiceUnreachableException {
		GetListItemsResult result = null;
		final GetListItemsDocument command = GetListItemsDocument.Factory.newInstance();
		command.setGetListItems(targetList);		
		
		try {
			GetListItemsResponseDocument client = this.soapEndPoint.getListItems(command);
			GetListItemsResponse response = client.getGetListItemsResponse();
			result = response.getGetListItemsResult();			
		} catch (RemoteException e) {
			LOG.error("Failed to connect to specified resources!");
			throw new ServiceUnreachableException(
					"Failed to connect to specified resources!", e);
		}
		
		return result;
	}
			
	/**
	 * this is a helper method to retrieve list result from target sharepoint list/view. It
	 * will return a list of objects based on specified class name
	 * 
	 * @param targetList
	 * @return
	 */
	private List<T> retrieve(GetListItems targetList) throws ServiceUnreachableException {
		final List<T> list = new LinkedList<T>();
		final GetListItemsResult result = callSoapService(targetList);		
		final Node rootNode = result.getDomNode();
		final Node listItemsNode = rootNode.getChildNodes().item(0);
		if(LISTRESULT_PRIMARY_NODE_NAME.equals(listItemsNode.getNodeName())) {
			// ignore the first node #text and start with second node rs:data			
			Node rsDataNodes = listItemsNode.getChildNodes().item(1);
			NodeList rsItemNodeList = rsDataNodes.getChildNodes();
							
			int rsCnt = rsItemNodeList.getLength();
			int index = 0;			
			while(index < rsCnt && list.size() < config.getRowLimit()) {				
				Node node = rsItemNodeList.item(index);
				// only act on "z:row" node, ignore all others
				if(LISTRESULT_ROW_NODE_NAME.equals(node.getNodeName())) {
					T item = newInstance();
					((Retrievable)item).fromNode(node);
					list.add(item);
				}				
				index++;
			}
			
		} else {
			LOG.info("No match found! Returning empty results.");
		}
		return list;
	}
	
	/**
	 * helper method to construct target ListItem 
	 * 
	 * @return
	 */
	private T newInstance() throws InvalidListItemException {
		final T expectedInstance;		
		try {
			expectedInstance = this.expectedClass.newInstance();
		} catch (InstantiationException e) {
			LOG.error(this.expectedClass.getName() + " cannot be instantiated!", e);
			throw new InvalidListItemException(
					this.expectedClass.getName()  + " cannot be instantiated!");
		} catch (IllegalAccessException e) {
			LOG.error(this.expectedClass.getName()  + " cannot be accessed!", e);
			throw new InvalidListItemException(
					this.expectedClass.getName()  + " cannot be accessed!");
		}
		
		return expectedInstance;
	}
	
	/**
	 * helper method to set default options on the HTTP Client
	 */
	private void setupOptions() {
		final Options options = this.soapEndPoint._getServiceClient().getOptions();
		options.setProperty(HTTPConstants.CHUNKED, Boolean.FALSE);
		options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Boolean.TRUE);
		HttpTransportProperties.Authenticator auth = this.config.getAuthenticator();
		if(auth != null) {
			options.setProperty(HTTPConstants.AUTHENTICATE, auth);
		}
	}
	
	private void parseAttachmentResponse(GetAttachmentCollectionResponseDocument responseDocument,
										 List<URL> listFileURL) {
		final GetAttachmentCollectionResponse response = 
				responseDocument.getGetAttachmentCollectionResponse();
		final GetAttachmentCollectionResult attachCollectResult = 
				response.getGetAttachmentCollectionResult();
		final Node resultsNode = attachCollectResult.getDomNode();  // <GetAttachmentCollectionResult />
		final Node attachmentsNode = resultsNode.getFirstChild(); // <Attachments />
		final NodeList nodeList = attachmentsNode.getChildNodes(); // <Attachment />
		final int size = nodeList.getLength();
		
		for(int i = 0; i < size; i++) {
			final Node attachment = nodeList.item(i);
			final Node childNode = attachment.getFirstChild();
			if(Node.TEXT_NODE == childNode.getNodeType()) {
				Text textNode = (Text)childNode;
				final String fileLocation = textNode.getData();
				try {
					listFileURL.add(new URL(fileLocation));
				} catch (MalformedURLException e) {
					LOG.error("Unexpected error occurred! Attachment location is not valid!" + fileLocation );				
				}
			}			
		}		
	}
	
	/**
	 * helper method to parse UpdateListItemsResponse
	 * 
	 * @param responseDocument
	 * @param list
	 */
	private void parseUpdateResponse(UpdateListItemsResponseDocument responseDocument,
									 List<Executable> list) {
		final UpdateListItemsResponse response = responseDocument.getUpdateListItemsResponse();
		final UpdateListItemsResult result = response.getUpdateListItemsResult();
		final Node itemResultsNode = result.getDomNode(); // <UpdateListItemsResult />
		final Node resultsNode = itemResultsNode.getFirstChild(); // <Results />
		final NodeList resultsNodeList = resultsNode.getChildNodes(); // <Result ID="1,New">
		final int size = resultsNodeList.getLength();
		for(int i = 0; i < size; i++) {
			final Node resultNode = resultsNodeList.item(i);
			if(! isNew(resultNode)) continue; // skip non New
			
			final Node rowNode = resultNode.getLastChild();
			for(Executable item : list) {
				if(!item.isMatch(rowNode)) continue;
								
				final NamedNodeMap attributes = rowNode.getAttributes();
				final Node idAttr = attributes.getNamedItem(Element.DEFAULT_PREFIX + Executable.FIELDNAME_ID);
				item.setId(Integer.valueOf(idAttr.getNodeValue()));
				
				break;				
			}
		}		
	}
	
	/**
	 * helper method to determine if result node corresponds to New result
	 * 
	 * @param node
	 * @return
	 */
	private boolean isNew(Node node) {
		boolean retVal = false;
		final NamedNodeMap attributes = node.getAttributes();
		final Node idAttr = attributes.getNamedItem(RESULT_ID_ATTRIBUTE_NAME);
		if(idAttr.getNodeValue().endsWith(Command.CREATE.toString())) {
			retVal = true;
		}
		return retVal;
	}
	
}
