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
		return this.connections.get(direction);
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
