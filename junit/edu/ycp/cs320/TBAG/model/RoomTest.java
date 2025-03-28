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
	
	@Before
	public void setUp() {
		model = new Room(short_description, long_description);
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
	public void testgetItems() {
		Item carrot = new Item("carrot", "It's shaped like a cane");
		model.AddItem(carrot);
		Item coin = new Item("coin", "it's made out of copper");
		model.AddItem(coin);
		HashMap<Item, Integer> item = this.model.getItems();
		
		assertTrue(item.equals(this.model.getItems()));
	}
	
	@Test
	public void testgetRoomInventory() {
		assertTrue(this.model.getRoomInventory() == this.model.getRoomInventory());
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
	
	@Test
	public void testAddItem() {
		Item coin = new Item("coin", "it's made out of copper");
		model.AddItem(coin);
		assertTrue(model.ContainsExactly(coin, 1));
	}
	
	@Test
	public void testAddItems() {
		Item carrot = new Item("carrot", "It's shaped like a cane");
		model.AddItems(carrot, 4);
		assertTrue(model.ContainsExactly(carrot, 4));
	}
	
	@Test
	public void testExtractItem() {
		Item carrot = new Item("carrot", "It's shaped like a cane");
		model.AddItem(carrot);
		assertTrue(1 == model.ExtractItem(carrot));
	}
	
	@Test
	public void testExtractItems() {
		Item carrot = new Item("carrot", "It's shaped like a cane");
		model.AddItems(carrot, 2);
		assertTrue(2 == model.ExtractItems(carrot, 2));
	}
	
	@Test
	public void testContainsItem() {
		Item carrot = new Item("carrot", "It's shaped like a cane");
		model.AddItem(carrot);
		assertTrue(true == model.ContainsItem(carrot));
	}
	
	@Test
	public void testContainsMoreThan() {
		Item carrot = new Item("carrot", "It's shaped like a cane");
		model.AddItem(carrot);
		assertTrue(true == model.ContainsMoreThan(carrot, 0));
	}
	
	@Test
	public void testContainsLessThan() {
		Item carrot = new Item("carrot", "It's shaped like a cane");
		model.AddItem(carrot);
		assertTrue(true == model.ContainsLessThan(carrot, 2));
	}
}
