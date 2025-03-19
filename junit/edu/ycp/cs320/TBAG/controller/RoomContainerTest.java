package edu.ycp.cs320.TBAG.controller;

import static org.junit.Assert.*;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

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
		container.setConnectedRoom(r1, direction, new_room_id);
		assertTrue(new_room_id == container.getConnectedRoom(r1, direction));
	}
	
	@Test
	public void testsetLongRoomDescription() {
		String description = "this a test";
		container.setLongRoomDescription(r2, description);
		assertTrue(description.equals(container.getLongRoomDescription(r2)));
	}
	
	@Test
	public void testsetShortRoomDescription() {
		String description = "test";
		container.setShortRoomDescription(r3, description);
		assertTrue(description.equals(container.getShortRoomDescription(r3)));
	}
	
	@Test
	public void testgetAllKeys() {
		container.setConnectedRoom(r1, "North", 2);
		container.setConnectedRoom(r1, "South", 6);
		Set <String> keys = this.container.getAllKeys(r1);
		System.out.println(this.container.getAllKeys(r1));
		
		assertTrue(keys.equals(this.container.getAllKeys(r1)));
	}
	
	@Test
	public void testgetAllConnection() {
		container.setConnectedRoom(r2, "North", 2);
		container.setConnectedRoom(r2, "South", 6);
		List <String> connect = this.container.getAllConnections(r2);
		System.out.println(this.container.getAllConnections(r2));
		
		assertTrue(connect.equals(this.container.getAllConnections(r2)));
	}
	
	@Test
	public void testgetHashMap() {
		container.setConnectedRoom(r1, "East", 9);
		container.setConnectedRoom(r1, "South", 6);
		Map<String, Integer> map = this.container.getHashMap(r1);
		System.out.println(this.container.getHashMap(r1));
		
		assertTrue(map.equals(this.container.getHashMap(r1)));
	}
	
	@Test
	public void testgetRoomIndex() {
		assertTrue(0 == container.getRoomIndex(r1));
	}
	
	
	
	
	@Test
	public void testaddRoom() {
		Room r4 = new Room("Room4","Roooooom4");
		container.addRoom(r4);
		assertTrue("Room4".equals(container.getShortRoomDescription(r4)));
		assertTrue("Roooooom4".equals(container.getLongRoomDescription(r4)));
	}
	
	@Test
	public void testnextConnection() {
		//Going North takes you to r3 since r3 has an index of 2 in the ArrayList
		container.setConnectedRoom(r1, "North", 2);
		assertTrue(2 == container.getConnectedRoom(r1, "North"));
	}
	
	
	
	
	
	

}
