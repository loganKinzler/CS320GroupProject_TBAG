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
		this.model = new Action("move", new ArrayList<String>( Arrays.asList(new String[] {"right"}) ));
	}

	@Test
	public void ValidityTest() {
		this.model.SetValidity(false);
		assertTrue(!this.model.IsValid());
	}
	
	@Test
	public void ErrorMessageTest() {
		this.model.SetErrorMessage("This wasn't a valid command.");
		assertEquals(this.model.GetErrorMessage(), "This wasn't a valid command.");
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