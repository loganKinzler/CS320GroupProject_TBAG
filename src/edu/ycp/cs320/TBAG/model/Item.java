package edu.ycp.cs320.TBAG.model;

import java.util.Objects;

public class Item {
	
	// vars
	protected Integer itemID;
	protected String name, description;
	
	
	// constructers
	public Item(Integer id, String name, String description) {
		this.itemID = id;
		this.name = name;
		this.description = description;
	}
	
	public Item(Integer id) {
		this.itemID = id;
		this.name = "";
		this.description = "";
	}
	
	
	// getters / setters
	public Integer GetID() {return this.itemID;}
	public void SetID(Integer id) {this.itemID = id;}
	
	public String GetName() {return this.name;}
	public void SetName(String name) {this.name = name;}
	
	public String GetDescription() {return this.description;}
	public void SetDescription(String description) {this.description = description;}
	
	@Override
	public boolean equals(Object obj) {
		Item item = (Item) obj;
        return item.GetID() == this.GetID();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.GetID());
	}
}