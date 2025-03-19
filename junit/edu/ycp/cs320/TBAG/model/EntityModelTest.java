package edu.ycp.cs320.TBAG.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.TBAG.model.EntityModel;

public class EntityModelTest {
	private EntityModel model;
	
	@Before
	public void setUp() {
		model = new EntityModel(100, 1, 1);
	}
	
	@Test
	public void testMaxHealth() {
		setUp();
		assertEquals(100, model.getMaxHealth(), .001);
		
		model.setMaxHealth(50);
		assertEquals(50, model.getMaxHealth(), .001);
		
		model.setMaxHealth(-2);
		assertEquals(50, model.getMaxHealth(), .001);
	}
	@Test
	public void testHealth() {
		setUp();
		assertEquals(100, model.getHealth(), .001);
		
		model.setHealth(25);
		assertEquals(25, model.getHealth(), .001);
		
		model.setHealth(250);
		assertEquals(25, model.getHealth(), .001);
		
		model.setHealth(-1);
		assertEquals(25, model.getHealth(), .001);
	}
}
