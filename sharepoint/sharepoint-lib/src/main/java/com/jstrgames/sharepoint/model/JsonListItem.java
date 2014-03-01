package com.jstrgames.sharepoint.model;

import org.apache.xmlbeans.XmlCursor;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.jstrgames.sharepoint.Element;
import com.jstrgames.sharepoint.element.NotSupportedException;

/**
 * this class is the default implementation of ListItem interface. This class 
 * will take the "z:row" record and return this class containing JSON string
 * representing a single row record 
 * 
 * @author Johnathan Ra
 *
 */
public class JsonListItem implements Element, Retrievable {
	
	private String json;
		
	public String getJson() {
		return this.json;
	}	

	@Override
	public void write(XmlCursor xmlCursor) {
		throw new NotSupportedException("this implementation only support retrieval action!");
	}
	
	@Override
	public void fromNode(Node node) {		
		final StringBuilder builder = new StringBuilder("{");
		
		final NamedNodeMap attributes = node.getAttributes();
		int size = attributes.getLength();
		boolean isFirst = true;
		for(int i = 0; i < size; i++) {
			if(isFirst) {
				isFirst = false;
			} else {
				builder.append(", ");
			}
			final Node namedAttribute = attributes.item(i);
			final String name = namedAttribute.getNodeName();
			final String value = namedAttribute.getNodeValue();
			builder.append("\"");
			builder.append(name);
			builder.append("\": ");
			builder.append("\"");
			builder.append(value);
			builder.append("\"");
		}
		builder.append("}");
		
		this.json = builder.toString();
	}

}
