package edu.ycp.cs320.tbag.model;

import java.util.ArrayList;
import java.util.Collections;
import edu.ycp.cs320.tbag.model.Item;

public class Inventory {
	
	// vars
	protected ArrayList<Item> items;
	
	
	// constructors
	public Inventory() {}
	
	public Inventory(ArrayList<Item> items) {
		this.items = items;
	}
	
	
	// getters / setters
	public Item GetItem(Integer index) {return items.get(index);}
	public void SetItem(Integer index, Item item) {this.items.set(index, item);}
	
	public Integer GetIndexOfItem(Item searchItem) {return items.indexOf(searchItem);}
	public Boolean RemoveItem(Integer index) {return items.remove( items.get(index) );}
	public Boolean RemoveItem(Item removeItem) {return items.remove(removeItem);}

	public ArrayList<Item> GetInventory() {return items;}
}
