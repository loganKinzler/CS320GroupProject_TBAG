package edu.ycp.cs320.TBAG.model;

import java.util.HashMap;
import java.lang.Math;


public class Inventory {
	
	// vars
	protected HashMap<Item, Integer> items;
	
	
	// constructors
	public Inventory() {
		this.items = new HashMap<Item, Integer>();
	}
	
	public Inventory(HashMap<Item, Integer> items) {
		this.items = items;
	}
	
	// getters / setters
	public HashMap<Item, Integer> GetItems() {return this.items;}
	public Integer GetItemAmount(Item item) {return this.GetItemAmount(item);}
	
	public void AddItem(Item item) {
		this.AddItems(item, 1);// this is equivalent to adding one item
	}
	
	public void AddItems(Item item, Integer itemAmount) {
		if (!this.items.containsKey(item)) this.items.put(item, 0);// add key if item isn't in inventory
		this.items.put(item, this.GetItemAmount(item) + itemAmount);// add items amount to stack
	}

	public Integer RemoveItem(Item item) {
		return this.RemoveItem(item, 1);// this is equivalent to removing one item
	}
	
	public Integer RemoveItem(Item item, Integer itemAmount) {
		if (itemAmount < 0) return null;// user tries to remove a negative number of items
		if (!this.items.containsKey(item)) return null;// item doesn't exist
		
		if (this.GetItemAmount(item) < itemAmount) {
			this.items.remove(item);// this is the last item in the stack
			return Math.min(this.GetItemAmount(item), itemAmount);// tell the user that some of the items were received
		}
		
		this.items.put(item, this.GetItemAmount(item) - itemAmount);// remove the amount of items that is being taken
		return itemAmount;// tell the user that all of the items were received
	}
}
