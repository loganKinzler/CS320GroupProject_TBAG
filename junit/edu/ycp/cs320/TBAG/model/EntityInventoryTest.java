package edu.ycp.cs320.TBAG.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EntityInventoryTest {
	private EntityInventory model;
	
	private Item moss = new Item("Moss", "Green and fluffy! | Heals 10hp");
	private Item hotChocolate = new Item("Hot Chocolate", "Steamy & cozy.");
	private Weapon baseballBat = new Weapon("Baseball Bat", "Swing, batta-batta! | Deals 5 dmg", new Double(5));
	private Weapon umbrella = new Weapon("Umbrella", "Keeps you dry. | Deals 2 dmg", new Double(2));
	
	@Before
	public void SetUp() {
		this.model = new EntityInventory();
		
		this.model.AddItem(hotChocolate);
		this.model.AddItems(moss, 5);
		this.model.EquipWeapon("Left Hand", baseballBat);
	}

	@Test
	public void AddItemTest() {
		this.model.AddItem(umbrella);
		assertTrue(this.model.ContainsItem(umbrella));
	}
	
	@Test
	public void ExtractItemTest() {
		this.model.ExtractItems(moss, 3);
		assertTrue(this.model.GetItemAmount(moss) == 2);
	}
	
	@Test
	public void GetItemAmountTest() {
		assertEquals(this.model.GetItemAmount(moss), new Integer(5));
	}
	
	@Test
	public void EquipWeaponTest() {
		this.model.EquipWeapon("Right Hand", baseballBat);
		assertTrue(this.model.WeaponIsEquiped(baseballBat));
	}
	
	@Test
	public void DropWeaponTest() {
		this.model.DropWeaponInSlot("Left Hand");
		assertTrue(this.model.WeaponSlotIsEmpty("Left Hand"));
	}
	
	@Test public void WeaponIsEquipedTest() {assertTrue( this.model.WeaponIsEquiped(baseballBat) );}
	@Test public void SlotIsFullTest() {assertTrue( this.model.WeaponSlotIsFull("Left Hand") );}
	@Test public void SlotIsEmptyTest() {assertTrue( this.model.WeaponSlotIsEmpty("Right Hand") );}
	
	@Test public void ContainsItemTest() {assertTrue( this.model.ContainsItem(baseballBat) );}
	@Test public void ContainsExactlyTest() {assertTrue( this.model.ContainsExactly(baseballBat, 1) );}
	@Test public void ContainsLessThanTest() {assertTrue( this.model.ContainsLessThan(umbrella, 3) );}
	@Test public void ContainsMoreThanTest() {assertTrue( this.model.ContainsMoreThan(moss, 3) );}
}