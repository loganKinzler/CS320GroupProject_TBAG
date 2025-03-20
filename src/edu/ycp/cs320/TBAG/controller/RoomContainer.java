package edu.ycp.cs320.TBAG.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ycp.cs320.TBAG.model.Room;

public class RoomContainer {
	
	//TODO: I might potentially make a hashmap for the ArrayList instead of having to rely on it's index in the array for it's value
	//TODO: We are definitely going to need to draw out a map layout in lucidchart and label it
	
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
}
