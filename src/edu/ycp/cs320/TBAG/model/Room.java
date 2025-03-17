package edu.ycp.cs320.TBAG.model;

import java.util.HashMap;
import java.util.Map;

public class Room {
	//Fields
	private Map<String, Integer> connections = new HashMap<>();
	
	//Constructors
	public Room() {
		
	}
	
	//Get the room id of the room
	public Integer getRoomlocation(String input) {
		return this.connections.get(input);
	}
	
	
	//Set the room id of a room
	public void setRoomlocation(String input, Integer new_room_id) {
		connections.put(input, new_room_id);
	}
	
	//Delete's the room with the associated input direction
	public void deleteRoom(String input) {
		connections.remove(input);
	}
	
	
	
	
	

}
