package com.jstrgames.sharepoint.element;

public enum Command {
	CREATE("New"),
	// NOT SharePoint does not have a concept of "Retrieve"!
	RETRIEVE("Retrieve"), 
	UPDATE("Update"),
	DELETE("Delete");
	
	private final String command;
	
	Command(String command) {
		this.command = command;
	}
	
	@Override
	public String toString() {
		return this.command;
	}
}
