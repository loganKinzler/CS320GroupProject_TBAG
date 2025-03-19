package edu.ycp.cs320.TBAG.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.TBAG.model.EntityModel;
import edu.ycp.cs320.TBAG.controller.EntityController;

public class EntityControllerTest {
	private EntityController entity;
	private EntityModel model;
	
	@Before
	public void setUp() {
		model = new EntityModel(100, 1, 1);
		entity = new EntityController(model);
	}
	
	@Test
	public void testAddHealth() {
		setUp();
		assertEquals(100, entity.getHealth(), .001);
		
		entity.AddHealth(-20);
		assertEquals(80, entity.getHealth(), .001);
		
		entity.AddHealth(40);
		assertEquals(80, entity.getHealth(), .001);
		
	}
	@Test
	public void testAddHealthClamped() {
		setUp();
		assertEquals(100, entity.getHealth(), .001);
		
		entity.AddHealthClamped(-20);
		assertEquals(80, entity.getHealth(), .001);
		
		entity.AddHealthClamped(40);
		assertEquals(100, entity.getHealth(), .001);
		
		entity.AddHealthClamped(-120);
		assertEquals(0, entity.getHealth(), .001);
	}
}
