package edu.ycp.cs320.TBAG.db;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import edu.ycp.cs320.TBAG.tbagdb.persist.DerbyDatabase;

import edu.ycp.cs320.TBAG.model.Inventory;
import edu.ycp.cs320.TBAG.model.RoomInventory;
import edu.ycp.cs320.TBAG.model.EntityInventory;

import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.Weapon;

public class ItemDbTest {
	private DerbyDatabase db = new DerbyDatabase("test");
	private Inventory testInv;
	
	
	
	@Before
	public void setup() {
		db = new DerbyDatabase("test");
		db.create();
	}
	
	@Test
	public void getItemByNameTest() {
		assertEquals(db.ItemByNameQuery("Shovel"), new Item(1, "Shovel", "Don't dig straight down."));
		assertEquals(db.ItemByNameQuery("I don't exist"), null);
	}
	
	@Test
	public void getWeaponByNameTest() {
		assertEquals(db.ItemByNameQuery("Shovel"), new Weapon(1, "Shovel", "Don't dig straight down", 12.5));
		assertEquals(db.ItemByNameQuery("I don't exist"), null);
	}
	
	@Test
	public void getItemIDTest() {
		assertTrue(db.GetItemIDQuery("Flimsy Orange Key", "A 3D printed key made of orange filament") == 3);
		assertTrue(db.GetItemIDQuery("Nonexistent Item", "I don't exist") == -1);
	}
	
	@Test
	public void getRoomInventoryTest() {
		RoomInventory testRoomInv = new RoomInventory();
		testRoomInv.AddItems(new Weapon(1, "Shovel", "Don't dig straight down", 5.0), 1);
		testRoomInv.AddItems(new Item(4, "Biiig shovel", "It's like really big"), 1);
		testRoomInv.AddItems(new Item(5, "Mirror", "Its covered in dirt, but still usable."), 1);
		
		RoomInventory dbRoomInv = db.GetRoomInventoryByID(1);
		assertEquals(testRoomInv, dbRoomInv);
	}
	
	@Test
	public void getPlayerInventoryTest() {
		EntityInventory testPlayerInv = new EntityInventory();
		testPlayerInv.AddItems(new Item(5, "Mirror", "Its covered in dirt, but still usable."), 1);
		testPlayerInv.AddItems(new Item(6, "Camera", "It lets you see things"), 1);
		
		EntityInventory dbPlayerInv = db.GetPlayerInventory();
		assertEquals(testPlayerInv, dbPlayerInv);
	}
	
	@Test
	public void getEntityInventoryTest() {
		EntityInventory testEntityInv = new EntityInventory();
		EntityInventory dbEntityInv = db.GetEnemyInventoryByID(7);
		assertEquals(testEntityInv, dbEntityInv);
	}
	
	@Test
	public void UpdateInventoryTest() {
		RoomInventory intialRoomInv = db.GetRoomInventoryByID(1);
		
		RoomInventory testRoomInv = new RoomInventory();
		testRoomInv.AddItems(new Weapon(1, "Shovel", "Don't dig straight down", 5.0), 4);
//		testRoomInv.AddItems(new Item(4, "Biiig shovel", "It's like really big"), 1);
		testRoomInv.AddItems(new Item(5, "Mirror", "Its covered in dirt, but still usable."), 1);
		
		db.UpdateInventoryBySourceID(2, testRoomInv);
		RoomInventory dbRoomInv = db.GetRoomInventoryByID(1);
		assertEquals(testRoomInv, dbRoomInv);
		
		testRoomInv = new RoomInventory();
		testRoomInv.AddItems(new Weapon(1, "Shovel", "Don't dig straight down", 5.0), 4);
		testRoomInv.AddItems(new Item(4, "Biiig shovel", "It's like really big"), 1);
		testRoomInv.AddItems(new Item(5, "Mirror", "Its covered in dirt, but still usable."), 1);
		
		db.UpdateInventoryBySourceID(2, testRoomInv);
		dbRoomInv = db.GetRoomInventoryByID(1);
		assertEquals(testRoomInv, dbRoomInv);
		
		// put the initial room back to how it was
		db.UpdateRoomInventory(1, intialRoomInv);
	}
}