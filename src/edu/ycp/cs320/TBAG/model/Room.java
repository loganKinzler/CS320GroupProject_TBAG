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
	private RoomInventory room_inventory;
	private ArrayList<EnemyModel> enemies = new ArrayList<EnemyModel>();
	//TODO: Might have to move doesKeyExist into the container as well
	
	//Constructors
	//This constructor creates a new RoomInventory
	public Room(String short_description, String long_description) {
		this.short_description = short_description;
		this.long_description = long_description;
		this.room_inventory = new RoomInventory();
		this.enemies = new ArrayList<EnemyModel>();
	}
	
	public Room(String short_description, String long_description, ArrayList<EnemyModel> enemies) {
		this.short_description = short_description;
		this.long_description = long_description;
		this.room_inventory = new RoomInventory();
		this.enemies = enemies;
	}
	
	/*This constructor takes an existing RoomInventory and set's it equal to the room_inventory field.
	It also takes an existing ArrayList of EnemyModel and set's it equal to the enemies ArrayList*/
	public Room(String short_description, String long_description, RoomInventory room_inventory, ArrayList<EnemyModel> enemies) {
		this.short_description = short_description;
		this.long_description = long_description;
		this.room_inventory = room_inventory;
		this.enemies = enemies;
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
}
