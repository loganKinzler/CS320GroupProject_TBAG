package edu.ycp.cs320.TBAG.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RoomInventoryTest {
	private RoomInventory model;
	
	private Item moss = new Item(1, "Moss", "Green and fluffy! | Heals 10hp");
	private Item hotChocolate = new Item(2, "Hot Chocolate", "Steamy & cozy.");
	private Weapon baseballBat = new Weapon(3, "Baseball Bat", "Swing, batta-batta! | Deals 5 dmg", 5.0);
	private Weapon umbrella = new Weapon(4, "Umbrella", "Keeps you dry. | Deals 2 dmg", 2.0);
	
	@Before
	public void SetUp() {
		this.model = new RoomInventory();
		
		this.model.AddItem(hotChocolate);
		this.model.AddItems(moss, 5);
		this.model.AddItem(baseballBat);
	}

	@Test
	public void AddItemTest() {
		this.model.AddItem(umbrella);
		assertTrue(this.model.ContainsItem(umbrella));
	}
	
	@Test
	public void RemoveItemTest() {
		Integer beforeAmount = this.model.GetItemAmount(moss);
		this.model.ExtractItems(moss, 3);
		assertTrue(this.model.GetItemAmount(moss) == beforeAmount - 3);
	}
	
	@SuppressWarnings("removal")
	@Test public void GetItemAmountTest() {
		assertEquals(this.model.GetItemAmount(moss), new Integer(5));
	}
	
	
	@Test public void ContainsItemTest() {assertTrue( this.model.ContainsItem(hotChocolate) );}
	@Test public void ContainsExactlyTest() {assertTrue( this.model.ContainsExactly(baseballBat, 1) );}
	@Test public void ContainsLessThanTest() {assertTrue( this.model.ContainsLessThan(umbrella, 3) );}
	@Test public void ContainsMoreThanTest() {assertTrue( this.model.ContainsMoreThan(moss, 3) );}
}