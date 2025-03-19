package edu.ycp.cs320.TBAG.controller;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.TBAG.controller.ConsoleInterpreter;
import edu.ycp.cs320.TBAG.model.Action;

public class ConsoleInterpreterTest {
	ConsoleInterpreter model;
	
	@Before
	public void setUp() {
		model = new ConsoleInterpreter();
	}
	
	@Test
	public void testInterpreter() {
		//Correct Syntax
		Action userInput = model.ValidateInput("using");
		assertEquals(userInput.IsValid(), false);
		assertEquals(userInput.GetErrorMessage(), "Input was not a command.");
		
		
		userInput = model.ValidateInput("create:");
		assertEquals(userInput.IsValid(), false);
		assertEquals(userInput.GetErrorMessage(), "Command given doesn't exist.");
		
		
		userInput = model.ValidateInput("use: ");
		assertEquals(userInput.IsValid(), false);
		assertEquals(userInput.GetErrorMessage(), "No parameters given.");
		
		
		userInput = model.ValidateInput("describe: inventory");
		assertEquals(userInput.IsValid(), true);
		assertEquals(userInput.GetErrorMessage(), null);
		assertEquals(userInput.GetMethod(), "describe");
		assertEquals(userInput.GetParams().get(0), "inventory");

		
		userInput = model.ValidateInput("use: health potion");
		assertEquals(userInput.IsValid(), true);
		assertEquals(userInput.GetErrorMessage(), null);
		assertEquals(userInput.GetMethod(), "use");
		assertEquals(userInput.GetParams().get(0), "health potion");
		
		
		userInput = model.ValidateInput("attack: ");
		assertEquals(userInput.IsValid(), false);
		assertEquals(userInput.GetErrorMessage(), "Keyword 'using' or 'with' is missing.");
		
		
		userInput = model.ValidateInput("attack: using nothing, totally nothing ever");
		assertEquals(userInput.IsValid(), false);
		assertEquals(userInput.GetErrorMessage(), "Keyword 'using' or 'with' is missing.");
	
		userInput = model.ValidateInput("attack: Person Sword using Slash");
		assertEquals(userInput.IsValid(), false);
		assertEquals(userInput.GetErrorMessage(), "Keyword 'using' or 'with' is missing.");
		
		userInput = model.ValidateInput("attack: with weapon using ultra slash");
		assertEquals(userInput.IsValid(), false);
		assertEquals(userInput.GetErrorMessage(), "No target was included.");
		

		userInput = model.ValidateInput("attack: Drone with using Red Laser");
		assertEquals(userInput.IsValid(), false);
		assertEquals(userInput.GetErrorMessage(), "No weapon was included.");
		
		
		userInput = model.ValidateInput("attack: Ogre with Big Sword using");
		assertEquals(userInput.IsValid(), false);
		assertEquals(userInput.GetErrorMessage(), "No attack type was given.");
		
		
		userInput = model.ValidateInput("attack: Bat with Bow using Fire Arrow");
		assertEquals(userInput.IsValid(), true);
		assertEquals(userInput.GetMethod(), "attack");
		assertEquals(userInput.GetParams().get(0), "Bat");
		assertEquals(userInput.GetParams().get(1), "Bow");
		assertEquals(userInput.GetParams().get(2), "Fire Arrow");
	}
}
