package edu.ycp.cs320.TBAG.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.ycp.cs320.TBAG.model.Room;

public class RoomController {
	private Room model;
	public void setModel(Room model) {
		this.model = model;
	}
	
	//Fields
	private ArrayList<Room> rooms = new ArrayList<Room>();
	
	//Constructor
	public RoomController(ArrayList<Room> rooms) {
		this.rooms = rooms;
	}
	
	
	
	public void addRoom(Room r) {
		//this.model.setRoomlocation(input, room_id);
		this.rooms.add(r);
	}
	
	public void deleteRoom(Room r) {
		this.rooms.remove(r);
	}
	
	
	public void setRoomlocation(int num, String input, Integer new_room_id) {
		this.rooms.get(num).setRoomlocation(input, new_room_id);
	}
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
		
}
