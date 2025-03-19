//TODO: count if there are multiple of one parenthesis

package edu.ycp.cs320.TBAG.controller;

import java.util.ArrayList;
import java.util.Arrays;

import edu.ycp.cs320.TBAG.model.Action;

public class ConsoleInterpreter {
	
	public static final String MOVE   = "move";
	public static final String PICKUP = "pickup";
	public static final String DESCRIBE = "describe";
	public static final String USE = "use";
	public static final String ATTACK = "attack";
	
	public ConsoleInterpreter() {}
	
    public Action ValidateInput(String userInput) {
    	ArrayList<String> inputWords = new ArrayList<String>(Arrays.asList( userInput.toLowerCase().split(" ") ));
    	
    	// no command given
    	if (inputWords.size() == 0) return new Action("No input given.");
    	
    	// no command indicator
    	if (inputWords.get(0).indexOf(":") != inputWords.get(0).length()-1) return new Action("Input was not a command.");
    	inputWords.set(0, inputWords.get(0).replace(":", ""));// remove indicator
    	
    	// check if command exists
    	Integer commandType = 0;
    	
    	switch (inputWords.get(0)) {
    		case MOVE: commandType = 1; break;
    		case PICKUP: commandType = 1; break;
    		case DESCRIBE: commandType = 1; break;
    		case USE: commandType = 1; break;
    	
    		case ATTACK: commandType = 2; break;
    	}
    	
    	switch (commandType) {
    	
    		// command doesn't exist
    		case 0:
    			return new Action("Command given doesn't exist.");
    		
    	
    		// format: '<command>: <param1>'
    		case 1:
    			if (inputWords.size() == 1) return new Action("No parameters given.");// no params
    			
    			// combine the rest of the words
    			String secondWord = inputWords.get(1);
    			
    			if (inputWords.size() > 2) {
    				Integer fullSize = inputWords.size();
    				
    				for (int i=2; i<fullSize; i++) {
    					secondWord += " " + inputWords.get(2);
    					inputWords.remove(2);
    				}
    			}
    				
    			return new Action(inputWords.get(0),new ArrayList<String>( Arrays.asList(secondWord) ));
    		
    			
    		// format: 'attack: <enemy> with <weapon> using <attack type>'
    		case 2:
    			if (!(inputWords.contains("with") && inputWords.contains("using"))) return new Action("Keyword 'using' or 'with' is missing.");// doesn't have with nor using
    			
    			
    			// combine words between 'attack:' and 'with'
    			String target = inputWords.get(1);

    			if (target.equals("with")) return new Action("No target was included.");// there is no target included
    			
    			if (inputWords.indexOf("with") > 2) {
    				Integer withIndex = inputWords.indexOf("with");
    				
    				for (int i=2; i<withIndex; i++) {
    					target += " " + inputWords.get(2);
    					inputWords.remove(2);
    				}
    			}
    			
    			
    			// combine words between 'with' and 'using'
    			if (inputWords.size() <= 4) return new Action("No parameters after keyword 'with' given.");// nothing after 'with'
    			String weapon = inputWords.get(3);
    			
    			if (weapon.equals("using")) return new Action("No weapon was included.");// there is no weapon included
    			
    			if (inputWords.indexOf("using") > 4) {
    				for (int i=4; i<inputWords.indexOf("using"); i++) {
    					weapon += " " + inputWords.get(4);
    					inputWords.remove(4);
    				}
    			}
    			
    			
    			// combine words after 'using'
    			if (inputWords.size() < 6) return new Action("No attack type was given.");// nothing after 'using'
    			String attackType = inputWords.get(5);
    			
    			if (inputWords.size() > 6) {
    				for (int i=6; i<inputWords.size(); i++) {
    					attackType += " " + inputWords.get(i);
    					inputWords.remove(6);
    				}
    			}
    				
    			return new Action("attack", new ArrayList<String>(
    					Arrays.asList(new String[]{target, weapon, attackType})));
    	}
    	
    	// command doesn't exist
    	return new Action("How did you get here??");
    }
}
