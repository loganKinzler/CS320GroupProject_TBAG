//TODO: count if there are multiple of one parenthesis

package edu.ycp.cs320.TBAG.controller;

public class ConsoleInterpreter {
	public static final int NORTH = 1;
	public static final int EAST  = 2;
	public static final int SOUTH = 3;
	public static final int WEST  = 4;

	private static final String MOVE   = "move";
	private static final String PICKUP = "pickup";
	private static final String ATTACK = "attack";
	
	public static final int INVALID_SYNTAX = -1;
	public static final int INVALID_COMMAND = -2;
	
	private static int countChar(char toCount, String str) {
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == toCount) count++;
		}
		return count;
	}
	
	public static int ReadConsoleInput(String input) {
		int parenthesisStart = input.indexOf('('); //Get index of first parenthesis to learn how long command string is
		int parenthesisEnd = input.indexOf(')'); //Get index of first parenthesis to learn how long command string is

		int countStartPar = countChar('(', input);
		int countEndPar = countChar(')', input);
		//Check for invalid syntax (individual cases for debugging)
		if (parenthesisStart == -1) {
			System.out.println("Syntax was invalid: Starting parenthesis");
			return INVALID_SYNTAX;
		}
		if (parenthesisEnd == -1) {
			System.out.println("Syntax was invalid: Ending parenthesis");
			return INVALID_SYNTAX;
		}
		if (parenthesisEnd < parenthesisStart) {
			System.out.println("Syntax was invalid: Unordered parenthesis");
			return INVALID_SYNTAX;
		}
		if (parenthesisEnd < input.length() - 1) {
			System.out.println("Syntax was invalid: Last parenthesis not at end");
			return INVALID_SYNTAX;
		}
		if (countStartPar != 1 || countEndPar != 1) {
			System.out.println("Syntax was invalid: Parenthesis count");
			return INVALID_SYNTAX;
		}

		//Split up input into separate parts
		String command = input.substring(0, parenthesisStart).toLowerCase(); //String of command is found before first (
		String param = input.substring(parenthesisStart + 1, parenthesisEnd).toLowerCase(); //String of param is found between ( and )
		
		switch (command) {
		case MOVE:
			if (param.equals("north")) return NORTH;
			else if (param.equals("east")) return EAST;
			else if (param.equals("south")) return SOUTH;
			else if (param.equals("west")) return WEST;
			else {
				System.out.println("Invalid command: move");
				return INVALID_COMMAND; //If no valid command is found, return
			}
		case PICKUP:
		case ATTACK:
		default:
			System.out.println("No valid command was entered");
			return INVALID_COMMAND; //If no valid command is found, return
		}
	}
}
