package edu.ycp.cs320.TBAG.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class WeaponTest {
	private Weapon model;
	
	@Before
	public void SetUp() {
		this.model = new Weapon("Baseball Bat", "Swing, batta-batta! | Deals 5 dmg", new Double(5));
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
	public void DamageTest() {
		this.model.SetDamage(new Double(2));
		assertEquals(this.model.GetDamage(), new Double(2));
	}
}