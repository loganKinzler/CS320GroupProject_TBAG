package edu.ycp.cs320.TBAG.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.ArrayList;

public class ActionTest {
	private Action model;
	
	private ArrayList<String> swingParams = new ArrayList<String>( Arrays.asList(new String[] {"BaseballBat", "Swing", "Ogre"}) );
	
	@Before
	public void SetUp() {
		this.model = new Action(true, "Movement", "move rooms",
			new ArrayList<String>( Arrays.asList(new String[] {"right"}) ));
	}

	@Test
	public void ValidityTest() {
		this.model.SetValidity(false);
		assertTrue(!this.model.IsValid());
	}
	
	@Test
	public void TypeTest() {
		this.model.SetType("Quit");
		assertEquals(this.model.GetType(), "Quit");
	}
	
	@Test
	public void MethodTest() {
		this.model.SetMethod("attack");
		assertEquals(this.model.GetMethod(), "attack");
	}
	
	@Test
	public void ParamsTest() {
		this.model.SetParams(swingParams);
		assertEquals(this.model.GetParams(),
				new ArrayList<String>( Arrays.asList(new String[] {"BaseballBat", "Swing", "Ogre"}) ));
	}
}