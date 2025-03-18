package edu.ycp.cs320.TBAG.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {
	//Fields
	private Map<String, Integer> connections = new HashMap<>();
	
	private String long_description;
	private String short_description;
	
	//TODO: Need an ArrayList of enemy in the rooms
	//TODO: Need an Inventory for each of the rooms
	
	
	//Constructors
	public Room(String short_description, String long_description) {
		this.short_description = short_description;
		this.long_description = long_description;
	}
	
	
	
	
	/*This method will return true if there is a key with the same String as input.
	If it is not it will return false*/
	public boolean doesKeyExist(String direction) {
		return this.connections.containsKey(direction);
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
		return connections.get(direction);
	}
	
	
	
	//Set the value of the key to the String stored in new_room_id.
	//This will change the connection the key has to new_room_id
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
