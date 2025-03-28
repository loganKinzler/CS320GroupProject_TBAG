package edu.ycp.cs320.TBAG.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Room {
	//Fields
	private Map<String, Integer> connections = new HashMap<>();
	
	private String long_description;
	private String short_description;
	private RoomInventory room_inventory;
	//TODO: Need an ArrayList of enemy in the rooms
	//TODO: Need an Inventory for each of the rooms
	
	
	//Constructors
	//This constructor creates a new RoomInventory
	public Room(String short_description, String long_description) {
		this.short_description = short_description;
		this.long_description = long_description;
		this.room_inventory = new RoomInventory();
	}
	
	//This constructor takes an existing RoomInventory and set's it equal to the room_inventory field
	public Room(String short_description, String long_description, RoomInventory room_inventory) {
		this.short_description = short_description;
		this.long_description = long_description;
		this.room_inventory = room_inventory;
	}
	
	
	
	
	/*This method will return true if there is a key with the same String as input.
	If it is not it will return false*/
	public boolean doesKeyExist(String direction) {
		return this.connections.containsKey(direction);
	}
	
	//Adds and item into the room inventory
	public void AddItem(Item item) {
		this.room_inventory.AddItem(item);
	}
	
	//Adds itemAmount of items into the room inventory
	public void AddItems(Item item, Integer itemAmount) {
		this.room_inventory.AddItems(item, itemAmount);
	}
	
	/*Removes 1 of that item from the room inventory and returns
	the item removed along with a 1 to indicate 1 item was removed*/
	public Integer ExtractItem(Item item) {
		return this.room_inventory.ExtractItem(item);
	}
	
	/*Removes itemAmount of item from the room inventory and returns
	 * the amount of that item removed. 
	 * Note: 0 will be return if....
	 * 1. a negative number of items is removed
	 * 2. the item doesn't exist
	 */
	public Integer ExtractItems(Item item, Integer itemAmount) {
		return this.room_inventory.ExtractItems(item, itemAmount);
	}
	
	//Check if item is in the room inventory
	public Boolean ContainsItem(Item item) {
		return this.room_inventory.ContainsItem(item);
	}
	
	/*This will return true if the amount of that item is greater than 
	itemAmount*/
	public Boolean ContainsMoreThan(Item item, Integer itemAmount) {
		return this.room_inventory.ContainsMoreThan(item, itemAmount);
	}
	
	//will return true if the amount of items is equal to itemAmount//
	public Boolean ContainsExactly(Item item, Integer itemAmount) {
		return this.room_inventory.ContainsExactly(item, itemAmount);
	}
	
	/*This will return true if the amount of that item is less than the
	itemAmount*/
	public Boolean ContainsLessThan(Item item, Integer itemAmount) {
		return this.room_inventory.ContainsLessThan(item, itemAmount);
	}
	
	
	
	
	//This will return the long room description of the room
	public String getLongRoomDescription() {
		return this.long_description;
	}
	
	//This will return the short room description of the room
		public String getShortRoomDescription() {
			return this.short_description;
		}
	
	//This will get the value attached to a key with the same String as direction
	public Integer getConnectedRoom(String direction) {
		return this.connections.get(direction);
	}
	
	//This will get all of the Items in the room as a HashMap
	public HashMap<Item, Integer> getItems() {
		return this.room_inventory.GetItems();
	}
	
	//Returns the room's Inventory
	public RoomInventory getRoomInventory() {
		return this.room_inventory;
	}
	
	/*This will return all of the keys as a Set string called "keys"
	It being a Set String means there will be no duplicates*/
	public Set <String> getAllKeys(){
		Set <String> keys;
		keys = this.connections.keySet();
		return keys;
	}
	
	//This will get all of the connections the room has and return it as a List of Strings called "values"
	public List<String> getAllConnections(){
		 List<String> values = new ArrayList<>();
	        for (Integer value : this.connections.values()) {
	            values.add(String.valueOf(value));
	        }
	        
	        return values;
	}
	
	//This will get the entire HashMap for the room
	public  Map<String, Integer> getHashMap(){
		return this.connections;
	}
	
	
	
	
	//Set the value of the key to the String stored in new_room_id.
	//This will change the connection the key has to new_room_id.
	public void setConnectedRoom(String direction, Integer new_room_id) {
		this.connections.put(direction, new_room_id);
	}
	
	//Set the long description equal to the String description
	public void setLongRoomDescription(String description) {
		this.long_description = description;
	}
	
	//Set the short description equal to the String description
		public void setShortRoomDescription(String description) {
			this.short_description = description;
		}
}
