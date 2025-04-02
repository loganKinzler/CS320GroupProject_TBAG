package edu.ycp.cs320.TBAG.controller;

import static org.junit.Assert.*;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.TBAG.model.EnemyModel;
import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.controller.RoomContainer;

public class RoomContainerTest {
	private RoomContainer container;
	private Room r1;
	private Room r2;
	private Room r3;
	private ArrayList<Room> rooms = new ArrayList<Room>();
	
	@Before
	public void setUp() {
		container = new RoomContainer(rooms);
		r1 = new Room("room 1", "roooooom 1");
		r2 = new Room("room2", "rooooom 2");
		r3 = new Room ("room3", "rooooom3");
		container.addRoom(r1);
		container.addRoom(r2);
		container.addRoom(r3);
	}
	
	
	
	//Setters/Getters
	@Test
	public void testsetConnectedRoom() {
		String direction = "North";
		Integer new_room_id = 3;
		container.setConnectedRoom(0, direction, new_room_id);
		assertTrue(new_room_id == container.getConnectedRoom(0, direction));
	}
	
	@Test
	public void testsetLongRoomDescription() {
		String description = "this a test";
		container.setLongRoomDescription(1, description);
		assertTrue(description.equals(container.getLongRoomDescription(1)));
	}
	
	@Test
	public void testsetShortRoomDescription() {
		String description = "test";
		container.setShortRoomDescription(2, description);
		assertTrue(description.equals(container.getShortRoomDescription(2)));
	}
	
	@Test
	public void testgetAllKeys() {
		container.setConnectedRoom(0, "North", 2);
		container.setConnectedRoom(0, "South", 6);
		Set <String> keys = this.container.getAllKeys(0);
		System.out.println(this.container.getAllKeys(0));
		
		assertTrue(keys.equals(this.container.getAllKeys(0)));
	}
	
	@Test
	public void testgetAllConnection() {
		container.setConnectedRoom(1, "North", 2);
		container.setConnectedRoom(1, "South", 6);
		List <String> connect = this.container.getAllConnections(1);
		System.out.println(this.container.getAllConnections(1));
		
		assertTrue(connect.equals(this.container.getAllConnections(1)));
	}
	
	@Test
	public void testgetHashMap() {
		container.setConnectedRoom(0, "East", 9);
		container.setConnectedRoom(0, "South", 6);
		Map<String, Integer> map = this.container.getHashMap(0);
		System.out.println(this.container.getHashMap(0));
		
		assertTrue(map.equals(this.container.getHashMap(0)));
	}
	
	@Test
	public void testgetRoomIndex() {
		assertTrue(0 == container.getRoomIndex(r1));
	}
	
	@Test
	public void testgetRoom() {
		assertTrue(r1 == this.container.getRoom(0));
	}
	
	@Test
	public void testgetItems() {
		Item carrot = new Item("carrot", "It's shaped like a cane");
		container.AddItem(carrot, 0);
		HashMap<Item, Integer> items = this.container.getItems(0);
		
		assertTrue(items.equals(this.container.getItems(0)));
	}
	
	@Test
	public void testgetItemAmount() {
		Item carrot = new Item("carrot", "It's shaped like a cane");
		container.AddItem(carrot, 0);
		assertTrue(1 == this.container.getItemAmount(carrot, 0));
	}
	
	@Test
	public void testgetHealth() {
		EnemyModel enemy1 = new EnemyModel(4.0, 2, 0);
		this.container.AddEnemy(0, enemy1);
		this.container.setHealth(3.0, 0, 0);
		assertTrue(this.container.getHealth(0, 0) == 3.0);
		
	}
	
	@Test
	public void testgetLives() {
		EnemyModel enemy1 = new EnemyModel(4.0, 2, 0);
		this.container.AddEnemy(0, enemy1);
		this.container.setLives(3, 0, 0);
		assertTrue(this.container.getLives(0, 0) == 3);
	}
	
	@Test
	public void testgetMaxHealth() {
		EnemyModel enemy1 = new EnemyModel(4.0, 2, 0);
		this.container.AddEnemy(0, enemy1);
		this.container.setMaxHealth(5.0, 0, 0);
	}
	
	@Test
	public void testgetAllEnemies() {
		EnemyModel enemy1 = new EnemyModel(4.0, 2, 0);
		this.container.AddEnemy(0, enemy1);
		String enemies = this.container.getAllEnemies(0);
		assertTrue(this.container.getAllEnemies(0).equals(enemies));
	}
	
	@Test
	public void testgetEnemy() {
		EnemyModel enemy1 = new EnemyModel(4.0, 2, 0);
		this.container.AddEnemy(0, enemy1);
		EnemyModel enemy = this.container.getEnemy(0, 0);
		assertTrue(this.container.getEnemy(0, 0).equals(enemy));
	}
	
	@Test
	public void testgetEnemiesinRoom() {
		EnemyModel enemy1 = new EnemyModel(4.0, 2, 0);
		this.container.AddEnemy(0, enemy1);
		ArrayList<EnemyModel> enemy = this.container.getEnemiesinRoom(0);
		assertTrue(this.container.getEnemiesinRoom(0).equals(enemy));
	}
	
	
	
	
	
	@Test
	public void testaddRoom() {
		Room r4 = new Room("Room4","Roooooom4");
		container.addRoom(r4);
		assertTrue("Room4".equals(container.getShortRoomDescription(3)));
		assertTrue("Roooooom4".equals(container.getLongRoomDescription(3)));
	}
	
	@Test
	public void testnextConnection() {
		//Going North takes you to r3 since r3 has an index of 2 in the ArrayList
		container.setConnectedRoom(0, "North", 2);
		assertTrue(2 == container.getConnectedRoom(0, "North"));
	}
	
	@Test
	public void testAddItem() {
		Item coin = new Item("coin", "it's made out of copper");
		container.AddItem(coin, 0);
		assertTrue(container.ContainsExactly(coin, 1, 0));
	}
	
	@Test
	public void testAddItems() {
		Item carrot = new Item("carrot", "It's shaped like a cane");
		container.AddItems(carrot, 4, 0);
		assertTrue(container.ContainsExactly(carrot, 4, 0));
	}
	
	@Test
	public void testExtractItem() {
		Item carrot = new Item("carrot", "It's shaped like a cane");
		container.AddItem(carrot, 0);
		assertTrue(1 == container.ExtractItem(carrot, 0));
	}
	
	@Test
	public void testExtractItems() {
		Item carrot = new Item("carrot", "It's shaped like a cane");
		container.AddItems(carrot, 2, 0);
		assertTrue(2 == container.ExtractItems(carrot, 2, 0));
	}
	
	@Test
	public void testContainsItem() {
		Item carrot = new Item("carrot", "It's shaped like a cane");
		container.AddItem(carrot, 0);
		assertTrue(true == container.ContainsItem(carrot, 0));
	}
	
	@Test
	public void testContainsMoreThan() {
		Item carrot = new Item("carrot", "It's shaped like a cane");
		container.AddItem(carrot, 0);
		assertTrue(true == container.ContainsMoreThan(carrot, 0, 0));
	}
	
	@Test
	public void testContainsLessThan() {
		Item carrot = new Item("carrot", "It's shaped like a cane");
		container.AddItem(carrot, 0);
		assertTrue(true == container.ContainsLessThan(carrot, 2, 0));
	}
	
	@Test
	public void testContainsExactly() {
		Item carrot = new Item("carrot", "it's shaped like a cane");
		container.AddItem(carrot, 0);
		assertTrue(true == container.ContainsExactly(carrot, 1, 0));
	}
	
	@Test
	public void testAddHealth() {
		EnemyModel enemy1 = new EnemyModel(4.0, 2, 0);
		this.container.AddEnemy(0, enemy1);
		this.container.AddHealth(0,0,-2.0);
		assertTrue(this.container.getHealth(0, 0) ==  2.0);
	}
	
	@Test
	public void testAddHealthClamped() {
		EnemyModel enemy1 = new EnemyModel(4.0, 2, 0);
		this.container.AddEnemy(0, enemy1);
		this.container.AddHealth(0, 0, -3.0);
		this.container.AddHealthClamped(0, 0, 5.0);
		
		assertTrue(this.container.getHealth(0, 0) == 4.0);
	}
	
	@Test
	public void testAddEnemy() {
		EnemyModel enemy1 = new EnemyModel(4.0, 2, 0);
		this.container.AddEnemy(0, enemy1);
		assertTrue(this.container.getEnemy(0, 0).equals(enemy1));
	}
	
	

}
