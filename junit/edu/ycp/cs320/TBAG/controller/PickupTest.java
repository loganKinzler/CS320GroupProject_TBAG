package edu.ycp.cs320.TBAG.controller;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import edu.ycp.cs320.TBAG.controller.PlayerController;
import edu.ycp.cs320.TBAG.controller.RoomContainer;
import edu.ycp.cs320.TBAG.model.Inventory;
import edu.ycp.cs320.TBAG.model.PlayerModel;

public class PickupTest {
	private PlayerController player = new PlayerController(new PlayerModel(100.0, 3, 0));
	private RoomContainer rooms = new RoomContainer();
	
	@Before
	public void SetUp() {
		rooms = new RoomContainer();
		rooms.createHardcodedRooms();
		player = new PlayerController(new PlayerModel(100.0, 3, 0));
	}

	@Test
	public void PickupOneItemTest() {
		SetUp();
		player.PickUp(rooms, Inventory.LEAD_PIPE, 1);
		assertTrue(player.getInventory().ContainsItem(Inventory.LEAD_PIPE));
	}
	
	@Test
	public void PickupMultipleItemsTest() {
		SetUp();
		player.PickUp(rooms, Inventory.SHWARMA, 3);
		player.Drop(rooms, Inventory.SHWARMA, 1);
		assertTrue(player.getInventory().ContainsExactly(Inventory.SHWARMA, 2));
	}
	@Test
	public void TestWrongRoom() {
		SetUp();
		player.setCurrentRoomIndex(3);
		player.PickUp(rooms, Inventory.LEAD_PIPE, 1);
		assertFalse(player.getInventory().ContainsItem(Inventory.LEAD_PIPE));
	}
}
