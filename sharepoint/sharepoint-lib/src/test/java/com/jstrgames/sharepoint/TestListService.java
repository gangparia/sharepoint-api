package com.jstrgames.sharepoint;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.jstrgames.sharepoint.model.JsonListItem;
import com.microsoft.schemas.sharepoint.soap.GetListItemsDocument;
import com.microsoft.schemas.sharepoint.soap.GetListItemsResponseDocument;
import com.microsoft.schemas.sharepoint.soap.GetListItemsResponseDocument.GetListItemsResponse;
import com.microsoft.schemas.sharepoint.soap.GetListItemsResponseDocument.GetListItemsResponse.GetListItemsResult;
import com.microsoft.sharepoint.ListsStub;

public class TestListService {
	private static final String STANDARD_SHAREPOINT_PROPERTY_ID = "ows_ID";
	private static final String STANDARD_SHAREPOINT_PROPERTY_UNIQUEID = "ows_UniqueId";
	
	private static final String MOCK_LIST_NAMEGUID = "MOCK_LIST_NAMEGUID";
	private static final String MOCK_LIST_VIEWGUID = "MOCK_LIST_VIEWGUID";
	private static final int MOCK_LIST_ROWLIMIT = 100;
	private static final int EXPECTED_ROWLIMIT = 10;
	
	private ListsStub mockSoapEndPoint;
	private Configuration mockProp;
	private Node mockNode;
	
	@Before
	public void setUp() throws Exception {
		mockSoapEndPoint = mock(ListsStub.class);
		mockProp = mock(Configuration.class);
		mockNode = loadFromMockXML();
		
	}

	@Test
	public void testSuccessScenarioWithRowLimitHigherThanResultset() {		
		final ServiceClient mockServiceClient = mock(ServiceClient.class);
		final Options mockOptions = mock(Options.class);
		final GetListItemsResponseDocument mockResponseDoc = mock(GetListItemsResponseDocument.class);
		final GetListItemsResponse mockResponse = mock(GetListItemsResponse.class);
		final GetListItemsResult mockResult = mock(GetListItemsResult.class);
		
		try {
			when(mockProp.getAuthenticator()).thenReturn(null);
			when(mockProp.getListGUID()).thenReturn(MOCK_LIST_NAMEGUID);
			when(mockProp.getViewGUID()).thenReturn(MOCK_LIST_VIEWGUID);
			when(mockProp.getRowLimit()).thenReturn(MOCK_LIST_ROWLIMIT);
			when(mockSoapEndPoint._getServiceClient()).thenReturn(mockServiceClient);
			when(mockServiceClient.getOptions()).thenReturn(mockOptions);
			when(mockSoapEndPoint.getListItems(any(GetListItemsDocument.class))).thenReturn(mockResponseDoc);
			when(mockResponseDoc.getGetListItemsResponse()).thenReturn(mockResponse);
			when(mockResponse.getGetListItemsResult()).thenReturn(mockResult);
			when(mockResult.getDomNode()).thenReturn(mockNode);
			
			final ListService<JsonListItem> service = 
					new ListService<JsonListItem>(mockProp, mockSoapEndPoint, JsonListItem.class);
			List<JsonListItem> result = service.retrieve();
			assertNotNull(result);
			assertTrue(result.size() < MOCK_LIST_ROWLIMIT);
			
			for(JsonListItem item : result) {
				String json = item.getJson();
				
				assertNotNull(json);
				assertTrue(json.contains(STANDARD_SHAREPOINT_PROPERTY_ID));
				assertTrue(json.contains(STANDARD_SHAREPOINT_PROPERTY_UNIQUEID));
			}
		} catch (RemoteException e) {
			fail("mock setup failure!");
		} catch (ServiceUnreachableException e) {
			fail("failed to reach SharePoint Service!");
		}		
	}
	
	@Test
	public void testSuccessScenarioWithRowLimitLowerThanExpected() {		
		final ServiceClient mockServiceClient = mock(ServiceClient.class);
		final Options mockOptions = mock(Options.class);
		final GetListItemsResponseDocument mockResponseDoc = mock(GetListItemsResponseDocument.class);
		final GetListItemsResponse mockResponse = mock(GetListItemsResponse.class);
		final GetListItemsResult mockResult = mock(GetListItemsResult.class);
		
		try {
			when(mockProp.getAuthenticator()).thenReturn(null);
			when(mockProp.getListGUID()).thenReturn(MOCK_LIST_NAMEGUID);
			when(mockProp.getViewGUID()).thenReturn(MOCK_LIST_VIEWGUID);
			when(mockProp.getRowLimit()).thenReturn(EXPECTED_ROWLIMIT);
			when(mockSoapEndPoint._getServiceClient()).thenReturn(mockServiceClient);
			when(mockServiceClient.getOptions()).thenReturn(mockOptions);
			when(mockSoapEndPoint.getListItems(any(GetListItemsDocument.class))).thenReturn(mockResponseDoc);
			when(mockResponseDoc.getGetListItemsResponse()).thenReturn(mockResponse);
			when(mockResponse.getGetListItemsResult()).thenReturn(mockResult);
			when(mockResult.getDomNode()).thenReturn(mockNode);
			
			final ListService<JsonListItem> service = 
					new ListService<JsonListItem>(mockProp, mockSoapEndPoint, JsonListItem.class);
			List<JsonListItem> result = service.retrieve();
			assertNotNull(result);
			assertEquals(EXPECTED_ROWLIMIT, result.size());
			
			for(JsonListItem item : result) {
				String json = item.getJson();
				
				assertNotNull(json);
				assertTrue(json.contains(STANDARD_SHAREPOINT_PROPERTY_ID));
				assertTrue(json.contains(STANDARD_SHAREPOINT_PROPERTY_UNIQUEID));
			}
		} catch (RemoteException e) {
			fail("mock setup failure!");
		} catch (ServiceUnreachableException e) {
			fail("failed to reach SharePoint Service!");
		}		
	}
	
	private Node loadFromMockXML() throws Exception {
		final File file = new File("src/test/resources/mock.list.result.xml");
		final InputStream inputStream = FileUtils.openInputStream(file);
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		
		DocumentBuilder builder = factory.newDocumentBuilder();
		final Document doc = builder.parse(inputStream);
		inputStream.close();
		return doc;
	}

}
