package com.jstrgames.sharepoint.element;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.jstrgames.sharepoint.element.Batch;
import com.jstrgames.sharepoint.element.Method;
import com.jstrgames.sharepoint.model.Executable;
import com.microsoft.schemas.sharepoint.soap.UpdateListItemsDocument.UpdateListItems.Updates;

public class TestBatch {
	private static final String MOCK_VIEWGUID = "THIS.IS.A.MOCK.VIEW.GUID";
	
	private static final String EXPECTED_XMLTEXT_CREATE_SINGLE = 
			"<Batch OnError=\"Continue\" ListVersion=\"1\" ViewName=\"" + MOCK_VIEWGUID + 
			"\"><Method ID=\"1\" Cmd=\"" + Command.CREATE + 
			"\"/></Batch>";
	private static final String EXPECTED_XMLTEXT_CREATE_MULTI = 
			"<Batch OnError=\"Continue\" ListVersion=\"1\" ViewName=\"" + MOCK_VIEWGUID + 
			"\"><Method ID=\"1\" Cmd=\"" + Command.CREATE + 
			"\"/><Method ID=\"2\" Cmd=\"" + Command.CREATE + 
			"\"/></Batch>";
	private static final String EXPECTED_XMLTEXT_UPDATE_SINGLE = 
			"<Batch OnError=\"Continue\" ListVersion=\"1\" ViewName=\"" + MOCK_VIEWGUID + 
			"\"><Method ID=\"1\" Cmd=\"" + Command.UPDATE + 
			"\"/></Batch>";
	private static final String EXPECTED_XMLTEXT_UPDATE_MULTI= 
			"<Batch OnError=\"Continue\" ListVersion=\"1\" ViewName=\"" + MOCK_VIEWGUID + 
			"\"><Method ID=\"1\" Cmd=\"" + Command.UPDATE + 
			"\"/><Method ID=\"2\" Cmd=\"" + Command.UPDATE +
			"\"/></Batch>";
	private static final String EXPECTED_XMLTEXT_DELETE_SINGLE = 
			"<Batch OnError=\"Continue\" ListVersion=\"1\" ViewName=\"" + MOCK_VIEWGUID + 
			"\"><Method ID=\"1\" Cmd=\"" + Command.DELETE + 
			"\"/></Batch>";
	private static final String EXPECTED_XMLTEXT_DELETE_MULTI= 
			"<Batch OnError=\"Continue\" ListVersion=\"1\" ViewName=\"" + MOCK_VIEWGUID + 
			"\"><Method ID=\"1\" Cmd=\"" + Command.DELETE + 
			"\"/><Method ID=\"2\" Cmd=\"" + Command.DELETE + 
			"\"/></Batch>";
	
	@Test
	public void testGenerateSingleCreateBatch() {		
		Executable item = mock(Executable.class);
		when(item.getCommand()).thenReturn(Command.CREATE);
		
		Method method = new Method(item);
		Batch batch = new Batch(method, MOCK_VIEWGUID);
		Updates updates = batch.generateUpdates();
		assertNotNull(updates);
		final String actualXmlText = updates.xmlText();
		assertEquals(EXPECTED_XMLTEXT_CREATE_SINGLE, actualXmlText);
		
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testGenerateMultiCreateBatch() {		
		Executable item1 = mock(Executable.class);
		when(item1.getCommand()).thenReturn(Command.CREATE);
		Executable item2 = mock(Executable.class);
		when(item2.getCommand()).thenReturn(Command.CREATE);
		
		List<Executable> list = (List<Executable>)mock(List.class);		
		Iterator<Executable> iter = (Iterator<Executable>) mock(Iterator.class);
		when(list.iterator()).thenReturn(iter);
		// only return 2
		when(iter.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
		when(iter.next()).thenReturn(item1).thenReturn(item2);
		
		Method method = new Method(list);
		Batch batch = new Batch(method, MOCK_VIEWGUID);
		Updates updates = batch.generateUpdates();
		assertNotNull(updates);
		final String actualXmlText = updates.xmlText();
		assertEquals(EXPECTED_XMLTEXT_CREATE_MULTI, actualXmlText);
		
	}
	
	@Test
	public void testGenerateSingleUpdateBatch() {		
		Executable item = mock(Executable.class);
		when(item.getCommand()).thenReturn(Command.UPDATE);
		
		Method method = new Method(item);
		Batch batch = new Batch(method, MOCK_VIEWGUID);
		Updates updates = batch.generateUpdates();
		assertNotNull(updates);
		final String actualXmlText = updates.xmlText();
		assertEquals(EXPECTED_XMLTEXT_UPDATE_SINGLE, actualXmlText);
		
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testGenerateMultiUpdateBatch() {		
		Executable item1 = mock(Executable.class);
		when(item1.getCommand()).thenReturn(Command.UPDATE);
		Executable item2 = mock(Executable.class);
		when(item2.getCommand()).thenReturn(Command.UPDATE);
		
		List<Executable> list = (List<Executable>)mock(List.class);		
		Iterator<Executable> iter = (Iterator<Executable>) mock(Iterator.class);
		when(list.iterator()).thenReturn(iter);
		// only return 2
		when(iter.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
		when(iter.next()).thenReturn(item1).thenReturn(item2);
		
		Method method = new Method(list);
		Batch batch = new Batch(method, MOCK_VIEWGUID);
		Updates updates = batch.generateUpdates();
		assertNotNull(updates);
		final String actualXmlText = updates.xmlText();
		assertEquals(EXPECTED_XMLTEXT_UPDATE_MULTI, actualXmlText);
		
	}
	
	@Test
	public void testGenerateSingleDeleteBatch() {		
		Executable item = mock(Executable.class);
		when(item.getCommand()).thenReturn(Command.DELETE);
		
		Method method = new Method(item);
		Batch batch = new Batch(method, MOCK_VIEWGUID);
		Updates updates = batch.generateUpdates();
		assertNotNull(updates);
		final String actualXmlText = updates.xmlText();
		assertEquals(EXPECTED_XMLTEXT_DELETE_SINGLE, actualXmlText);
		
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testGenerateMultiDeleteBatch() {		
		Executable item1 = mock(Executable.class);
		when(item1.getCommand()).thenReturn(Command.DELETE);
		Executable item2 = mock(Executable.class);
		when(item2.getCommand()).thenReturn(Command.DELETE);
		
		List<Executable> list = (List<Executable>)mock(List.class);		
		Iterator<Executable> iter = (Iterator<Executable>) mock(Iterator.class);
		when(list.iterator()).thenReturn(iter);
		// only return 2
		when(iter.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
		when(iter.next()).thenReturn(item1).thenReturn(item2);
		
		Method method = new Method(list);
		Batch batch = new Batch(method, MOCK_VIEWGUID);
		Updates updates = batch.generateUpdates();
		assertNotNull(updates);
		final String actualXmlText = updates.xmlText();
		assertEquals(EXPECTED_XMLTEXT_DELETE_MULTI, actualXmlText);
		
	}

}
