package edu.ycp.cs320.TBAG.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.Room;

public class RoomContainer {	
	//Fields
	//The index of the ArrayList is where the room will be "Located" AKA it's value to get to that room
	private ArrayList<Room> rooms = new ArrayList<Room>();
	
	//Constructor
	public RoomContainer() {
		this.rooms = new ArrayList<Room>();
	}
	
	public RoomContainer(ArrayList<Room> rooms) {
		this.rooms = rooms;
	}
	
	
	//Adds and item into the room inventory at index
		public void AddItem(Item item, Integer index) {
			this.rooms.get(index).getRoomInventory().AddItem(item);
		}
		
		//Adds itemAmount of items into the room inventory at index
		public void AddItems(Item item, Integer itemAmount, Integer index) {
			this.rooms.get(index).getRoomInventory().AddItems(item, itemAmount);
		}
		
		/*Removes 1 of that item from the room inventory at index and returns
		the item removed along with a 1 to indicate 1 item was removed*/
		public Integer ExtractItem(Item item, Integer index) {
			return this.rooms.get(index).getRoomInventory().ExtractItem(item);
		}
		
		/*Removes itemAmount of item from the room inventory and returns
		 * the amount of that item removed. 
		 * Note: 0 will be return if....
		 * 1. a negative number of items is removed
		 * 2. the item doesn't exist
		 */
		public Integer ExtractItems(Item item, Integer itemAmount, Integer index) {
			return this.rooms.get(index).getRoomInventory().ExtractItems(item, itemAmount);
		}
		
		//Check if item is in the room inventory
		public Boolean ContainsItem(Item item, Integer index) {
			return this.rooms.get(index).getRoomInventory().ContainsItem(item);
		}
		
		/*This will return true if the amount of that item is greater than 
		itemAmount*/
		public Boolean ContainsMoreThan(Item item, Integer itemAmount, Integer index) {
			return this.rooms.get(index).getRoomInventory().ContainsMoreThan(item, itemAmount);
		}
		
		//will return true if the amount of items is equal to itemAmount//
		public Boolean ContainsExactly(Item item, Integer itemAmount, Integer index) {
			return this.rooms.get(index).getRoomInventory().ContainsExactly(item, itemAmount);
		}
		
		/*This will return true if the amount of that item is less than the
		itemAmount*/
		public Boolean ContainsLessThan(Item item, Integer itemAmount, Integer index) {
			return this.rooms.get(index).getRoomInventory().ContainsLessThan(item, itemAmount);
		}
		
	/*Adds a new room to the ArrayList of rooms.
	The key and value of the new room is set using the setRoomlocation function,
	passing in the input and room_id parameters*/
	public void addRoom(Room r) {
		this.rooms.add(r);
	}
	
	
	/*This method will return the Integer value in the key with the same String name as direction if it exist.
	If the key does not exist in the current room, the player cannot move in that direction and the method return null*/
	public Integer nextConnection(Integer current_room, String direction) {
		if(this.rooms.get(current_room).doesKeyExist(direction) == true) {
			return this.rooms.get(current_room).getConnectedRoom(direction);
		}
		
		else {
			return null;
		}
		
	}
	
	
	
	
	/*Set the value of the key in room r to the String stored in new_room_id
	NOTE: This method will not function properly if rooms in the ArrayList have the same variable name*/
	public void setConnectedRoom(Integer index, String direction, Integer new_room_id) {
		this.rooms.get(index).setConnectedRoom(direction, new_room_id);
	}
	
	//Set the long room description in Room r equal to the String description
	public void setLongRoomDescription(Integer index, String description) {
		this.rooms.get(index).setLongRoomDescription(description);
	}
	
	//Set the short room description in Room r equal to the String description
	public void setShortRoomDescription(Integer index, String description) {
		this.rooms.get(index).setShortRoomDescription(description);
	}
	
	
	
	
	//Get the value of the key in Room r with the String direction
	public Integer getConnectedRoom(Integer index, String direction) {
		return this.rooms.get(index).getConnectedRoom(direction);
	}
	
	//Get the long room description of Room r
	public String getLongRoomDescription(Integer index) {
		return this.rooms.get(index).getLongRoomDescription();
	}
	
	//Get the short room description of Room r
	public String getShortRoomDescription(Integer index) {
		return this.rooms.get(index).getShortRoomDescription();
	}
		
	//Get all the keys of Room r
	public Set <String> getAllKeys(Integer index){
		return this.rooms.get(index).getAllKeys();
	}
		
	//Get all of the connections for Room r
	public List <String> getAllConnections(Integer index){
		return this.rooms.get(index).getAllConnections();
	}
		
	//Get the entire HashMap of Room r
	public Map<String, Integer> getHashMap(Integer index){
		return this.rooms.get(index).getHashMap();
	}
	
	//Get the index of Room r in the ArrayList of rooms
	public int getRoomIndex(Room r) {
		return rooms.indexOf(r);
	}
	
	//Get the room at index
	public Room getRoom(Integer index) {
		return this.rooms.get(index);
	}
	
	//Get all the Items in room at index and returns them as a HashMap
	public HashMap<Item, Integer> getItems(Integer index){
		return this.rooms.get(index).getItems();
	}
	
	//Get the amount of the Item in the room at index
	public Integer getItemAmount(Item item, Integer index) {
		return this.rooms.get(index).getItemAmount(item);
	}
}
