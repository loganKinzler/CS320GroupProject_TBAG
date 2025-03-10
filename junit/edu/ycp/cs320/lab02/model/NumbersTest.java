package edu.ycp.cs320.lab02.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class NumbersTest {
	private Numbers model;
	
	@Before
	public void setUp() {
		model = new Numbers();
		model.setNum1(0);
		model.setNum2(0);
		model.setNum3(0);
		model.setFactor1(0);
		model.setFactor2(0);
	}
	@Test
	public void testNum1() {
		model.setNum1(1.0);
		assertEquals(1.0, model.getNum1(), 0.0001);
	}
	@Test
	public void testNum2() {
		model.setNum2(1.0);
		assertEquals(1.0, model.getNum2(), 0.0001);
	}
	@Test
	public void testNum3() {
		model.setNum3(1.0);
		assertEquals(1.0, model.getNum3(), 0.0001);
	}
	@Test
	public void testFactor1() {
		model.setFactor1(1.0);
		assertEquals(1.0, model.getFactor1(), 0.0001);
	}
	@Test
	public void testFactor2() {
		model.setFactor2(1.0);
		assertEquals(1.0, model.getFactor2(), 0.0001);
	}
}
