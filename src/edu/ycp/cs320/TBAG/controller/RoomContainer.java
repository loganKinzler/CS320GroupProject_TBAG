package edu.ycp.cs320.TBAG.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ycp.cs320.TBAG.model.Room;

public class RoomContainer {

	public static String NORTH = "north";
	public static String EAST = "east";
	public static String SOUTH = "south";
	public static String WEST = "west";
	
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
	
	//Get the room at index
	public Room getRoom(Integer index) {
		return this.rooms.get(index);
	}
	
	public void createHardcodedRooms() {
		//Initialize rooms
		Room startRoom = new Room("Robot Room", "This is the starting room.");
		Room hall1 = new Room("Hallway", "You hear shuffling in front of you.<br>The door to your left is locked.");
		Room loot1 = new Room("Loot Room", "There's treasure!");
		Room enemy1 = new Room("Hallway", "Egad! There's an enemy!");
		Room hall2 = new Room("Hallway", "You hear shuffling to your right!");
		Room hall3 = new Room("Loot Hall", "There are loot rooms all around you!");
		Room lootHallWest = new Room("Loot Room", "There's treasure!");
		Room lootHallNorth = new Room("Loot Room", "There's treasure!");
		Room lootHallEast = new Room("Loot Room", "There's treasure!");
		Room enemy2 = new Room("Hallway", "Egad! There's an enemy!");
		Room g1 = new Room("Hallway", "There's a hole in the ceiling! It's raining acid! Better not stay in here too long!");
		Room hall4 = new Room("Hallway", "There's a loud clambering to your right. Better make sure you're prepared for whatever it may be.<br>The door to your left is locked.");
		Room loot2 = new Room("Loot Room", "There's treasure!");
		Room miniBoss1 = new Room("Arena", "There's a big baddie in here! You can't run away!");
		
		//Put rooms in container
		rooms.add	 (startRoom); //0
		rooms.add		 (hall1); //1
		rooms.add        (loot1); //2
		rooms.add       (enemy1); //3
		rooms.add        (hall2); //4
		rooms.add        (hall3); //5
		rooms.add (lootHallWest); //6
		rooms.add(lootHallNorth); //7
		rooms.add (lootHallEast); //8
		rooms.add       (enemy2); //9
		rooms.add 			(g1); //10
		rooms.add 		 (hall4); //11
		rooms.add 		 (loot2); //12
		rooms.add    (miniBoss1); //13
		
		//Establish connections
		startRoom.setConnectedRoom(WEST, 1);

		hall1.setConnectedRoom(WEST, 2);
		hall1.setConnectedRoom(EAST, 0);
		hall1.setConnectedRoom(NORTH, 3);
		
		loot1.setConnectedRoom(EAST, 1);

		enemy1.setConnectedRoom(SOUTH, 1);
		enemy1.setConnectedRoom(EAST, 4);

		hall2.setConnectedRoom(WEST, 3);
		hall2.setConnectedRoom(NORTH, 5);
		hall2.setConnectedRoom(EAST, 9);

		hall3.setConnectedRoom(SOUTH, 4);
		hall3.setConnectedRoom(WEST, 6);
		hall3.setConnectedRoom(NORTH, 7);
		hall3.setConnectedRoom(EAST, 8);

		lootHallWest.setConnectedRoom(EAST, 5);
		lootHallNorth.setConnectedRoom(SOUTH, 5);
		lootHallEast.setConnectedRoom(WEST, 5);

		enemy2.setConnectedRoom(EAST, 10);
		enemy2.setConnectedRoom(WEST, 4);

		g1.setConnectedRoom(WEST, 9);
		g1.setConnectedRoom(SOUTH, 11);

		hall4.setConnectedRoom(NORTH, 10);
		hall4.setConnectedRoom(WEST, 12);
		hall4.setConnectedRoom(EAST, 13);
		
		loot2.setConnectedRoom(EAST, 11);
		
		miniBoss1.setConnectedRoom(WEST, 11);
		
		//Add items
	}
}
