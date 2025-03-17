package edu.ycp.cs320.TBAG.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InventoryTest {
	private Inventory model;
	
	private Item moss = new Item("Moss", "Green and fluffy! | Heals 10hp");
	private Item hotChocolate = new Item("Hot Chocolate", "Steamy & cozy.");
	private Weapon baseballBat = new Weapon("Baseball Bat", "Swing, batta-batta! | Deals 5 dmg", new Double(5));
	private Weapon umbrella = new Weapon("Umbrella", "Keeps you dry. | Deals 2 dmg", new Double(2));
	
	@Before
	public void SetUp() {
		this.model = new Inventory();
		
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
	
	@Test public void GetItemAmountTest() {
		assertEquals(this.model.GetItemAmount(moss), new Integer(5));
	}
	
	
	@Test public void ContainsItemTest() {assertTrue( this.model.ContainsItem(hotChocolate) );}
	@Test public void ContainsExactlyTest() {assertTrue( this.model.ContainsExactly(baseballBat, 1) );}
	@Test public void ContainsLessThanTest() {assertTrue( this.model.ContainsLessThan(umbrella, 3) );}
	@Test public void ContainsMoreThanTest() {assertTrue( this.model.ContainsMoreThan(moss, 3) );}
}