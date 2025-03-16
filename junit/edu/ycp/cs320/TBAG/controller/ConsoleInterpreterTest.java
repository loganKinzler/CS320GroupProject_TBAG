package edu.ycp.cs320.TBAG.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.TBAG.controller.ConsoleInterpreter;

public class ConsoleInterpreterTest {
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testInterpreterSyntax() {
		//Correct Syntax
		int test = ConsoleInterpreter.ReadConsoleInput("move(north)");
		assertEquals(test, ConsoleInterpreter.NORTH);
		
		//First parenthesis
		test = ConsoleInterpreter.ReadConsoleInput("movenorth)");
		assertEquals(test, ConsoleInterpreter.INVALID_SYNTAX);
		
		//Last parenthesis
		test = ConsoleInterpreter.ReadConsoleInput("move(north");
		assertEquals(test, ConsoleInterpreter.INVALID_SYNTAX);
		
		//Unordered parenthesis
		test = ConsoleInterpreter.ReadConsoleInput("move)north(");
		assertEquals(test, ConsoleInterpreter.INVALID_SYNTAX);
		
		//End parenthesis not at end
		test = ConsoleInterpreter.ReadConsoleInput("move()north");
		assertEquals(test, ConsoleInterpreter.INVALID_SYNTAX);
		
		//Count start parenthesis
		test = ConsoleInterpreter.ReadConsoleInput("move((north)");
		assertEquals(test, ConsoleInterpreter.INVALID_SYNTAX);
		
		//Count end parenthesis
		test = ConsoleInterpreter.ReadConsoleInput("move(north))");
		assertEquals(test, ConsoleInterpreter.INVALID_SYNTAX);
	}
	
	@Test
	public void testInterpreterValidCommand() {
		//Correct Syntax
		int test = ConsoleInterpreter.ReadConsoleInput("move(north)");
		assertEquals(test, ConsoleInterpreter.NORTH);
		
		//Invalid command
		test = ConsoleInterpreter.ReadConsoleInput("movee(north)");
		assertEquals(test, ConsoleInterpreter.INVALID_COMMAND);
		
		//Invalid parameter
		test = ConsoleInterpreter.ReadConsoleInput("move(forward)");
		assertEquals(test, ConsoleInterpreter.INVALID_COMMAND);
		
	}

}
