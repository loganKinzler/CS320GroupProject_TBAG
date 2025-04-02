package edu.ycp.cs320.TBAG.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import edu.ycp.cs320.TBAG.controller.PlayerController;
import edu.ycp.cs320.TBAG.controller.RoomContainer;

public class PickupTest {
	private PlayerController player = new PlayerController(new PlayerModel(100.0, 3, 0));
	private RoomContainer rooms = new RoomContainer();
	
	@Before
	public void SetUp() {
		rooms.createHardcodedRooms();
	}

	@Test
	public void PickupOneItemTest() {
		SetUp();
		player.PickUp(rooms, Inventory.LEAD_PIPE);
		assertTrue(player.getInventory().ContainsItem(Inventory.LEAD_PIPE));
	}
	
	@Test
	public void PickupMultipleItemsTest() {
		SetUp();
		player.PickUp(rooms, Inventory.SHWARMA);
		assertTrue(player.getInventory().ContainsExactly(Inventory.SHWARMA, 5));
	}
	@Test
	public void TestWrongRoom() {
		SetUp();
		player.setCurrentRoomIndex(1);
		player.PickUp(rooms, Inventory.LEAD_PIPE);
		assertFalse(player.getInventory().ContainsItem(Inventory.LEAD_PIPE));
	}
}
