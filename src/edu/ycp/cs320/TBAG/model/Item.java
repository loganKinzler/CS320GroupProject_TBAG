package edu.ycp.cs320.TBAG.model;

public class Item {
	
	// vars
	protected String name, description;
	
	
	// constructers
	public Item(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	
	// getters / setters
	public String GetName() {return this.name;}
	public void SetName(String name) {this.name = name;}
	
	public String GetDescription() {return this.description;}
	public void SetDescription(String description) {this.description = description;}
	
	public Boolean equals(Item item) {
		return this.name.equals(item.GetName()) && this.description.equals(item.GetDescription());
	}
}