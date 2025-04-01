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
		Action userInput = model.ValidateInput("take");
		assertEquals(userInput.IsValid(), false);
		assertEquals(userInput.GetErrorMessage(), "Command given was not valid.");
		
		
		userInput = model.ValidateInput("create");
		assertEquals(userInput.IsValid(), false);
		assertEquals(userInput.GetErrorMessage(), "Command given was not valid.");
		
		
		userInput = model.ValidateInput("use ");
		assertEquals(userInput.IsValid(), false);
		assertEquals(userInput.GetErrorMessage(), "No parameters given.");
		
		
		userInput = model.ValidateInput("describe inventory");
		assertEquals(userInput.IsValid(), true);
		assertEquals(userInput.GetErrorMessage(), null);
		assertEquals(userInput.GetMethod(), "describe");
		assertEquals(userInput.GetParams().get(0), "inventory");

		
		userInput = model.ValidateInput("use health potion");
		assertEquals(userInput.IsValid(), true);
		assertEquals(userInput.GetErrorMessage(), null);
		assertEquals(userInput.GetMethod(), "use");
		assertEquals(userInput.GetParams().get(0), "health potion");
		
		
		userInput = model.ValidateInput("attack ");
		assertEquals(userInput.IsValid(), false);
		assertEquals(userInput.GetErrorMessage(), "Keyword 'using' is missing.");
	
		userInput = model.ValidateInput("attack Person Sword");
		assertEquals(userInput.IsValid(), false);
		assertEquals(userInput.GetErrorMessage(), "Keyword 'using' is missing.");
		
		userInput = model.ValidateInput("attack with weapon");
		assertEquals(userInput.IsValid(), false);
		assertEquals(userInput.GetErrorMessage(), "No target was included.");
		
		userInput = model.ValidateInput("attack Bat with Bow");
		assertEquals(userInput.IsValid(), true);
		assertEquals(userInput.GetMethod(), "attack");
		
		assertEquals(userInput.GetParams().get(0), "bat");
		assertEquals(userInput.GetParams().get(1), "bow");
	}
}
