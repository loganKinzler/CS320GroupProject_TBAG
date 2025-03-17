package edu.ycp.cs320.TBAG.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.ycp.cs320.TBAG.model.Room;

public class RoomContainer {
	
	//TODO: I might potentially make a hashmap for the ArrayList instead of having to rely on it's index in the array for it's value
	//TODO: We are definitely going to need to draw out a map layout in lucidchart and label it
	
	//Fields
	//The index of the ArrayList is where the room will be "Located" aka it's value to get to that room
	private ArrayList<Room> rooms = new ArrayList<Room>();
	
	//Constructor
	public RoomContainer(ArrayList<Room> rooms) {
		this.rooms = rooms;
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
	public void setConnectedRoom(Room r, String direction, Integer new_room_id) {
		int index = getRoomIndex(r);
		this.rooms.get(index).setConnectedRoom(direction, new_room_id);
	}
	
	//Set the room description in Room r with a value of room_id
	public void setRoomDescription(Room r, String description, Integer room_id) {
		int index = getRoomIndex(r);
		this.rooms.get(index).setRoomDescription(description, room_id);
	}
	
	
	
	
	//Get the value of the key in Room r with the String direction
	public Integer getConnectedRoom(Room r, String direction) {
		int index = getRoomIndex(r);
		return this.rooms.get(index).getConnectedRoom(direction);
	}
	
	//Get the description of a value of room_id in Room r
	public String getRoomDescription(Room r, Integer room_id) {
		int index = getRoomIndex(r);
		return this.rooms.get(index).getRoomDescription(room_id);
	}
	
	//Get the index of Room r in the ArrayList of rooms
	public int getRoomIndex(Room r) {
		return rooms.indexOf(r);
	}
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
		
}
