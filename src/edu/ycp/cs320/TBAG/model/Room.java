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
	//Each room MUST have a unique room_id
	private int room_id;
	//This hashset will be used for checking that each room created is assigned a unique room_id
	private static Set<Integer> room_id_set = new HashSet<>();
	private String long_description;
	private String short_description;
	private RoomInventory room_inventory;
	private ArrayList<EnemyModel> enemies = new ArrayList<EnemyModel>();
	private boolean isLockedRoom;
	//A locked room will have an item stored here
	private Item RoomKey = null;
	private int x_position;
	private int y_position;
	private boolean has_entered_room;
	//TODO: Might have to move doesKeyExist into the container as well
	
	//Constructors
	//This constructor is for the database
	public Room(int room_id) {
		//Returns true if the room_id is able to be added to the HashSet
		if(this.room_id_set.add(room_id)) {
			this.room_id = room_id;
		}
		this.isLockedRoom = false;
		/*
		else {
			throw new IllegalArgumentException("This room id already exists :(");
		}
		this.room_inventory = new RoomInventory();
		this.enemies = new ArrayList<EnemyModel>();
		*/
	}
	
	public Room() {
		
	}
	
	public Room(String name, String description, int x_position, int y_position, boolean has_entered_room) {
		this.short_description = name;
		this.long_description = description;
		this.x_position = x_position;
		this.y_position = y_position;
		this.has_entered_room = has_entered_room;
	}
	
	//This constructor is a LOCKED room that requires a certain amount of an item to open
	public Room(int room_id, Item item) {
		if(this.room_id_set.add(room_id)) {
			this.room_id = room_id;
		}
		this.isLockedRoom = true;
	}
	
	public Room(String short_description, String long_description) {
		this.short_description = short_description;
		this.long_description = long_description;
		this.room_inventory = new RoomInventory();
		this.enemies = new ArrayList<EnemyModel>();
		this.isLockedRoom = false;
	}
	
	public Room(String short_description, String long_description, ArrayList<EnemyModel> enemies) {
		this.short_description = short_description;
		this.long_description = long_description;
		this.room_inventory = new RoomInventory();
		this.enemies = enemies;
		this.isLockedRoom = false;
	}
	
	/*This constructor takes an existing RoomInventory and set's it equal to the room_inventory field.
	It also takes an existing ArrayList of EnemyModel and set's it equal to the enemies ArrayList*/
	public Room(String short_description, String long_description, RoomInventory room_inventory, ArrayList<EnemyModel> enemies) {
		this.short_description = short_description;
		this.long_description = long_description;
		this.room_inventory = room_inventory;
		this.enemies = enemies;
		this.isLockedRoom = false;
	}
	
	
	
	
	/*This method will return true if there is a key with the same String as input.
	If it is not it will return false*/
	public boolean doesKeyExist(String direction) {
		return this.connections.containsKey(direction);
	}
	
	
	
	
	public int getRoomId() {
		return this.room_id;
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
	
	//This will return the amount of that Item in the room inventory
	public Integer getItemAmount(Item item) {
		return this.room_inventory.GetItemAmount(item);
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
	public Map<String, Integer> getHashMap(){
		return this.connections;
	}
	
	//This will get the Health of the enemy at index
	public double getHealth(Integer index) {
		return this.enemies.get(index).getHealth();
	}
	
	//This will get the Lives of the enemy at index
	public int getLives(Integer index) {
		return this.enemies.get(index).getLives();
	}
	
	//This will get the MaxHealth of the enemy at index
	public double getMaxHealth(Integer index) {
		return this.enemies.get(index).getMaxHealth();
	}
	
	//This will return all of the enemies as a String
	public ArrayList<EnemyModel> getAllEnemies(){
		return this.enemies;
	}
	
	//This will return the enemy at index in the ArrayList
	public EnemyModel getEnemy(Integer index) {
		return this.enemies.get(index);
	}
	
	public ArrayList<EnemyModel> getEnemiesinRoom() {
		return this.enemies;
	}
	
	public int getX_Position() {
		return this.x_position;
	}
	
	public int getY_Position() {
		return this.y_position;
	}
	
	public boolean getHas_Entered_Room() {
		return this.has_entered_room;
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
		
	//Set the Lives of the enemy at index equal to lives
	public void setLives(int lives, Integer index) {
		this.enemies.get(index).setLives(lives);
	}
	
	//Set the health of the enemy at index equal to health
	public void setHealth(double health, Integer index) {
		this.enemies.get(index).setHealth(health);
	}
		
	//Set the MaxHealth of the enemy at index equal to health
	public void setMaxHealth(double maxHealth, Integer index) {
		this.enemies.get(index).setHealth(maxHealth);
	}
	
	public void setRoomInventory(RoomInventory inventory) {
		this.room_inventory = inventory;
	}
	
	public void setConnections(Map<String, Integer> connections) {
		this.connections = connections;
	}
}
