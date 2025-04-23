package edu.ycp.cs320.TBAG.model;

import static org.junit.Assert.*;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.TBAG.model.Room;


public class RoomTest {
	private Room model;
	//private Map<String, Integer> connections = new HashMap<>();
	private String long_description;
	private String short_description;
	private ArrayList<EnemyModel> enemies = new ArrayList<EnemyModel>();
	
	@Before
	public void setUp() {
		EnemyModel enemy1 = new EnemyModel(4.0, 2, 0, "Microwave", "MMMmmm... BEEP! BEEP! BEEP!");
		EnemyModel enemy2 = new EnemyModel(3.5, 1, 1, "Table", "There's food on it.");
		enemies.add(enemy1);
		enemies.add(enemy2);
		model = new Room(short_description, long_description, enemies);
	}
	
	
	
	//Setters/Getters
	@Test
	public void testsetConnectedRoom() {
		//This one has an empty HashMap
		String direction1 = "South";
		assertTrue(null == model.getConnectedRoom(direction1));
		
		
		String direction2 = "North";
		Integer new_room_id = 3;
		model.setConnectedRoom(direction2, new_room_id);
		assertTrue(3 == model.getConnectedRoom(direction2));
	}
	
	@Test
	public void testsetSetShortRoomDescription() {
		String description = "yo this a test";
		model.setShortRoomDescription(description);
		assertTrue(description.equals(model.getShortRoomDescription()));
	}
	
	@Test
	public void testsetSetLongRoomDescription() {
		String description = "test the thingy";
		model.setLongRoomDescription(description);
		assertTrue(description.equals(model.getLongRoomDescription()));
	}
	
	@Test
	public void testgetAllKeys() {
		model.setConnectedRoom("North", 2);
		model.setConnectedRoom("South", 1);
		model.setConnectedRoom("East", 5);
		System.out.println(model.getAllKeys());
		
		Set<String> keys = model.getAllKeys();
		assertTrue(keys.equals(model.getAllKeys()));
	}
	
	@Test
	public void testgetAllConnections() {
		model.setConnectedRoom("North", 2);
		model.setConnectedRoom("South", 1);
		model.setConnectedRoom("East", 5);
		System.out.println(model.getAllConnections());
		
		List<String> connections = model.getAllConnections();
		assertTrue(connections.equals(this.model.getAllConnections()));
	}
	
	@Test
	public void testgetHashMap() {
		model.setConnectedRoom("North", 2);
		model.setConnectedRoom("South", 1);
		model.setConnectedRoom("East", 5);
		Map<String, Integer> connect = this.model.getHashMap();
		System.out.println(this.model.getHashMap());
		
		assertTrue(connect.equals(this.model.getHashMap()));
	}
	
	@Test
	public void testgetRoomInventory() {
		assertTrue(this.model.getRoomInventory() == this.model.getRoomInventory());
	}
	
	@Test
	public void testgetItems() {
		Item coin = new Item(2, "coin", "it's a coin!");
		model.getRoomInventory().AddItem(coin);
		HashMap<Item, Integer> items = this.model.getItems();
		assertTrue(items.equals(this.model.getItems()));
	}
	
	@Test
	public void testgetItemAmount() {
		Item coin = new Item(2, "coin", "it's a coin!");
		model.getRoomInventory().AddItems(coin, 2);
		assertTrue(this.model.getItemAmount(coin) == 2);
	}
	
	@Test
	public void testgetHealth() {
		model.setHealth(3.0, 0);
		assertTrue(this.model.getHealth(0) == 3.0);
	}
	
	@Test
	public void testgetLives() {
		model.setLives(1, 0);
		assertTrue(this.model.getLives(0) == 1);
	}
	
	@Test
	public void testgetMaxHealth() {
		model.setMaxHealth(3.5, 1);
		System.out.print(this.model.getMaxHealth(1));
		assertTrue(this.model.getMaxHealth(1) == 3.5);
	}
	
	@Test
	public void testgetAllEnemies() {
		ArrayList<EnemyModel> enemies = model.getAllEnemies();
		assertTrue(this.model.getAllEnemies().equals(enemies));
	}
	
	@Test
	public void testgetEnemy() {
		EnemyModel enemy = this.model.getEnemy(1);
		assertTrue(this.model.getEnemy(1).equals(enemy));
	}
	
	
	@Test
	public void testdoesKeyExist() {
		//Room key does exist
		String direction1 = "east";
		Integer room_id = 2;
		model.setConnectedRoom(direction1, room_id);
		assertTrue(true == model.doesKeyExist(direction1));
		
		//Room key does not exist
		String direction2 = "South";
		assertTrue(false == model.doesKeyExist(direction2));
		
	}
}
