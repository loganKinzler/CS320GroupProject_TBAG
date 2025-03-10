package edu.ycp.cs320.lab02.controller;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class NumbersControllerTest {
//	private GuessingGame model;
	private NumbersController controller;
	
	@Before
	public void setUp() {
//		model = new Numbers();
		controller = new NumbersController();
		
//		model.setMin(1);
//		model.setMax(100);
		
//		controller.setModel(model);
	}

	@Test
	public void testAdd() {
//		int currentGuess = model.getGuess();
//		controller.setNumberIsGreaterThanGuess();
//		assertTrue(model.getGuess() > currentGuess);
		assertTrue(controller.add(4.0, 2.0) == 6.0);
		assertTrue(controller.add(4.0, 2.0, 3.0) == 9.0);
	}
	@Test
	public void testMultiply() {
		assertTrue(controller.multiply(4.0, 2.0) == 8.0);
	}
}
