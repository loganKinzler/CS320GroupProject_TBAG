package edu.ycp.cs320.TBAG.controller;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.TBAG.controller.ConsoleInterpreter;
import edu.ycp.cs320.TBAG.model.Action;

public class ConsoleInterpreterTest {
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testInterpreterSyntax() {
		//Correct Syntax
		Action test = ConsoleInterpreter.ReadConsoleInput("move(north)");
		assertEquals(test.GetType(), "move");
		
		//First parenthesis
		test = ConsoleInterpreter.ReadConsoleInput("movenorth)");
		assertEquals(test.IsValid(), false);
		
		//Last parenthesis
		test = ConsoleInterpreter.ReadConsoleInput("move(north");
		assertEquals(test.IsValid(), false);
		
		//Unordered parenthesis
		test = ConsoleInterpreter.ReadConsoleInput("move)north(");
		assertEquals(test.IsValid(), false);
		
		//End parenthesis not at end
		test = ConsoleInterpreter.ReadConsoleInput("move()north");
		assertEquals(test.IsValid(), false);
		
		//Count start parenthesis
		test = ConsoleInterpreter.ReadConsoleInput("move((north)");
		assertEquals(test.IsValid(), false);
		
		//Count end parenthesis
		test = ConsoleInterpreter.ReadConsoleInput("move(north))");
		assertEquals(test.IsValid(), false);
	}
	
	@Test
	public void testInterpreterValidCommand() {
		//Correct Syntax
		Action test = ConsoleInterpreter.ReadConsoleInput("move(north)");
		assertEquals(test.GetParams().get(0), "north");
		
		//Invalid command
		test = ConsoleInterpreter.ReadConsoleInput("movee(north)");
		assertEquals(test.IsValid(), false);
		
		//Invalid parameter
		test = ConsoleInterpreter.ReadConsoleInput("move(forward)");
		assertEquals(test.IsValid(), false);
		
	}

}
