package edu.ycp.cs320.TBAG.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {
	//Fields
	private Map<String, Integer> connections = new HashMap<>();
	
	private ArrayList<String> description;
	
	//TODO: Need an ArrayList of enemy in the rooms
	//TODO: Need an Inventory for each of the rooms
	//TODO: Rename input variables to direction for all of them?!
	
	
	//Constructors
	public Room(Map<String, Integer> connections) {
		this.connections = connections;
	}
	
	/*This method will return true if there is a key with the same String as input.
	If it is not it will return false*/
	public boolean doesKeyExist(String direction) {
		return this.connections.containsKey(direction);
	}
	
	
	
	
	//This will return the RoomDescription of a value of room_id
	public String getRoomDescription(Integer room_id) {
		return this.description.get(room_id);
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
	
	//Set the description of a value of room_id
	public void setRoomDescription(String description, Integer room_id) {
		this.description.set(room_id, description);
	}
	
}
