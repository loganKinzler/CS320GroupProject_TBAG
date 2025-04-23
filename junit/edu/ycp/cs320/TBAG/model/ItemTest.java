package edu.ycp.cs320.TBAG.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ItemTest {
	private Item model;
	
	@Before
	public void SetUp() {
		this.model = new Item(1, "Pipe", "It's made of aluminium and hollow.");
	}

	@Test
	public void NameTest() {
		this.model.SetName("Moss");
		assertEquals(this.model.GetName(), "Moss");
	}
	
	@Test
	public void DescriptionTest() {
		this.model.SetDescription("Green and fluffy! | Heals 10hp");
		assertEquals(this.model.GetDescription(), "Green and fluffy! | Heals 10hp");
	}
	
	@Test
	public void IDTest() {
		this.model.SetID(3);
		assertTrue(this.model.GetID() == 3);
	}
}