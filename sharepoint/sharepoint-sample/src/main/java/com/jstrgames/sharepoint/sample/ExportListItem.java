package com.jstrgames.sharepoint.sample;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.jstrgames.sharepoint.model.Retrievable;

public class ExportListItem implements Retrievable {

	private final Map<String,String> map;
	
	public ExportListItem() {
		this.map = new HashMap<String,String>();
	}
	
	public Map<String,String> getMap() {
		return this.map;
	}
	
	@Override
	public void fromNode(Node node) {
		final NamedNodeMap attributes = node.getAttributes();
		int size = attributes.getLength();

		for(int i = 0; i < size; i++) {			
			final Node namedAttribute = attributes.item(i);
			final String key = namedAttribute.getNodeName();
			final String value = namedAttribute.getNodeValue();		
			this.map.put(key, value);
		}
	}

}
