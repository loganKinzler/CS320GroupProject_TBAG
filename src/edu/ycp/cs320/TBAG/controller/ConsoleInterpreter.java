//TODO: count if there are multiple of one parenthesis

package edu.ycp.cs320.TBAG.controller;

import java.util.ArrayList;
import java.util.Arrays;

import edu.ycp.cs320.TBAG.model.Action;

public class ConsoleInterpreter {
	public static final String NORTH = "north";
	public static final String EAST  = "east";
	public static final String SOUTH = "south";
	public static final String WEST  = "west";

	public static final String MOVE   = "move";
	public static final String PICKUP = "pickup";
	public static final String ATTACK = "attack";
	
	public static final Action INVALID_SYNTAX = new Action(false);
	public static final Action INVALID_COMMAND = new Action(false);
	
	private static int countChar(char toCount, String str) {
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == toCount) count++;
		}
		return count;
	}
	
	public static Action ReadConsoleInput(String input) {
		int parenthesisStart = input.indexOf('('); //Get index of first parenthesis to learn how long command string is
		int parenthesisEnd = input.indexOf(')'); //Get index of first parenthesis to learn how long command string is

		int countStartPar = countChar('(', input);
		int countEndPar = countChar(')', input);
		//Check for invalid syntax (individual cases for debugging)
		if (parenthesisStart == -1) {
			System.out.println("Syntax was invalid: Starting parenthesis");
			return new Action(false);
		}
		if (parenthesisEnd == -1) {
			System.out.println("Syntax was invalid: Ending parenthesis");
			return new Action(false);
		}
		if (parenthesisEnd < parenthesisStart) {
			System.out.println("Syntax was invalid: Unordered parenthesis");
			return new Action(false);
		}
		if (parenthesisEnd < input.length() - 1) {
			System.out.println("Syntax was invalid: Last parenthesis not at end");
			return new Action(false);
		}
		if (countStartPar != 1 || countEndPar != 1) {
			System.out.println("Syntax was invalid: Parenthesis count");
			return new Action(false);
		}

		//Split up input into separate parts
		String command = input.substring(0, parenthesisStart).toLowerCase(); //String of command is found before first (
		String param = input.substring(parenthesisStart + 1, parenthesisEnd).toLowerCase(); //String of param is found between ( and )
		
		switch (command) {
		case MOVE:
			if (param.equals("north")) return new Action(true, MOVE, null, new ArrayList<>(Arrays.asList(NORTH)));
			else if (param.equals("east")) return new Action(true, MOVE, null, new ArrayList<>(Arrays.asList(EAST)));
			else if (param.equals("south")) return new Action(true, MOVE, null, new ArrayList<>(Arrays.asList(SOUTH)));
			else if (param.equals("west")) return new Action(true, MOVE, null, new ArrayList<>(Arrays.asList(WEST)));
			else {
				System.out.println("Invalid parameter for command: " + MOVE);
				return new Action(false); //If no valid command is found, return
			}
		case PICKUP:
		case ATTACK:
		default:
			System.out.println("No valid command was entered");
			return new Action(false); //If no valid command is found, return
		}
	}
}
