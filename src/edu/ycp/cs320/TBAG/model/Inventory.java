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
	public Integer GetItemAmount(Item item) {
		Integer quantity = this.items.get(item);
		return quantity == null? 0 : quantity;
	}
	
	public void AddItem(Item item) {
		this.AddItems(item, 1);// this is equivalent to adding one item
	}
	
	public void AddItems(Item item, Integer itemAmount) {
		if (!this.items.containsKey(item)) this.items.put(item, 0);// add key if item isn't in inventory
		this.items.put(item, this.GetItemAmount(item) + itemAmount);// add items amount to stack
	}

	public Integer ExtractItem(Item item) {
		return this.ExtractItems(item, 1);
	}
	
	public Integer ExtractItems(Item item, Integer itemAmount) {
		if (itemAmount < 0) return 0;// user tries to remove a negative number of items
		if (!this.items.containsKey(item)) return 0;// item doesn't exist
		
		if (this.GetItemAmount(item) <= itemAmount) {// extracting last item in the stack
			Integer extractedCount = Math.min(this.GetItemAmount(item), itemAmount);// save amount before removing
			this.items.remove(item);// remove the value entirely
			return extractedCount;// tell the user that some/all of the items were received
		}
		
		this.items.put(item, this.GetItemAmount(item) - itemAmount);// remove all of items that are being taken
		return itemAmount;// tell the user that all of the items were received
	}
	
	public Boolean ContainsItem(Item item) {
		return this.items.containsKey(item);
	}
	
	public Boolean ContainsMoreThan(Item item, Integer itemAmount) {
		if (!this.ContainsItem(item)) return false;// no items is equal to 0 items, always less than
		return this.GetItemAmount(item) > itemAmount;
	}
	
	public Boolean ContainsExactly(Item item, Integer itemAmount) {
		if (!this.ContainsItem(item) && itemAmount == 0) return true;// no items is equal to 0 items
		if (!this.ContainsItem(item)) return false;// items in inventory are not equal to 0 quantity
		return this.GetItemAmount(item) == itemAmount;
	}
	
	public Boolean ContainsLessThan(Item item, Integer itemAmount) {
		if (!this.ContainsItem(item)) return true;// item must be in inventory
		return this.GetItemAmount(item) < itemAmount;
	}
}
