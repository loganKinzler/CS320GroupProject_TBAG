package edu.ycp.cs320.TBAG.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WeaponTest {
	private Weapon model;
	
	@Before
	public void SetUp() {
		this.model = new Weapon(1, "Baseball Bat", "Swing, batta-batta! | Deals 5 dmg", 5.0);
	}

	@Test
	public void NameTest() {
		this.model.SetName("Crowbar");
		assertEquals(this.model.GetName(), "Crowbar");
	}
	
	@Test
	public void DescriptionTest() {
		this.model.SetDescription("The criminal's can opener. | Deals 2 dmg");
		assertEquals(this.model.GetDescription(), "The criminal's can opener. | Deals 2 dmg");
	}
	
	@Test
	public void IDTest() {
		this.model.SetID(3);
		assertTrue(this.model.GetID() == 3);
	}
	
	@Test
	public void DamageTest() {
		this.model.SetDamage(2.0);
		assertTrue(this.model.GetDamage() == 2.0);
	}
}