package edu.ycp.cs320.TBAG.controller;

import java.util.ArrayList;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;

import edu.ycp.cs320.TBAG.model.Action;

public class ConsoleInterpreter {
	
	public static final String MOVE   = "move";
	public static final String PICKUP = "pickup";
	public static final String DROP = "drop";
	public static final String DESCRIBE = "describe";
	public static final String USE = "use";
	public static final String ATTACK = "attack";
	public static final String EQUIP = "equip";
	public static final String UNEQUIP = "unequip";
	
	public static final Set<String> MOVE_DIRECTIONS = new HashSet<String>(Arrays.asList(
			new String[]{"north", "east", "south", "west"}));
	
	public ConsoleInterpreter() {}
	
    public Action ValidateInput(String userInput) {
    	//sudo rm -rf \ easter egg
    	if (userInput.equals("sudo rm -rf \\")) {
    		return new Action("sudoEasterEgg", new ArrayList<String>( Arrays.asList("sudo rm -rf \\") ));
    	}
    	else if (userInput.equals("hake")) {
    		return new Action("hakeTest", new ArrayList<String>(Arrays.asList("hakeypoo")));
    	}
    	else if (userInput.equals("babcock")) {
    		return new Action("babcockTest", new ArrayList<String>(Arrays.asList("babeypoo")));
    	}
    	else if (userInput.equals("new save")) {
    		return new Action("newSave", new ArrayList<String>(Arrays.asList("newsaveypoo")));
    	}
    	else if (userInput.equals("clear chat")) {
    		return new Action("clearChat", new ArrayList<String>(Arrays.asList("cleareypoo")));
    	}
    	else if (userInput.equals("show map")) {
    		return new Action("showMap", new ArrayList<String>(Arrays.asList("mappypoo")));
    	}
    	else if (userInput.equals("quit")) {
    		return new Action("quit", new ArrayList<String>(Arrays.asList("quitting")));
    	}
    	ArrayList<String> inputWords = new ArrayList<String>(Arrays.asList( userInput.toLowerCase().split(" ") ));
    	
    	
    	// no command given
    	if (inputWords.size() == 0) return new Action("No input given.");
    	
    	// check if command exists
    	Integer commandType = 0;
    	
    	switch (inputWords.get(0)) {
    		case MOVE: commandType = 1; break;
    		case DESCRIBE: commandType = 1; break;
    	
    		case PICKUP: commandType = 2; break;
    		case DROP: commandType = 2; break;
    		
    		case ATTACK: commandType = 3; break;

    		case EQUIP: commandType = 4; break;
    		case UNEQUIP: commandType = 5; break;
    		
    		case USE: commandType = 6; break;
    	}
  	
    	
    	switch (commandType) {
    	
    		// command doesn't exist
    		case 0: return new Action("Command given was not valid.");
    		
    	
    		// format: '<command>: <param1>'
    		case 1:
    			if (inputWords.size() == 1) return new Action("No parameters given.");// no params
    			
    			// combine the rest of the words
    			String secondWord = inputWords.get(1);
    			
    			if (inputWords.size() > 2) {
    				for (int i=2; i<inputWords.size(); i++) {
    					secondWord += " " + inputWords.get(2);
    					inputWords.remove(2);
    				}
    			}
    			
    			switch (inputWords.get(0)) {
    				case "move":
    					if (!MOVE_DIRECTIONS.contains( secondWord ))
    						return new Action("Direction given isn't valid.");
    				break;
    			}
    				
    			return new Action(inputWords.get(0),
    					new ArrayList<String>( Arrays.asList(secondWord) ));
    		
    		case 2:
    			if (inputWords.size() == 1) return new Action("No parameters given.");// no params
    			if (inputWords.size() == 2) return new Action("Not enough parameters.");
    			
    			String itemQuantity = inputWords.get(1).toLowerCase();
    			
    			Integer wordSize = inputWords.size();
    			String itemName = inputWords.get(2).toLowerCase();
				for (int i=3; i<wordSize; i++) {
					itemName += " " + inputWords.get(3).toLowerCase();
					inputWords.remove(3);
				}
    			

    			if (inputWords.get(1).toLowerCase().equals("all")) {
    				if (itemName.equals("items"))
        				return new Action(inputWords.get(0), new ArrayList<String>(
            					Arrays.asList(new String[]{"all", "items"})));
    			
    				return new Action(inputWords.get(0), new ArrayList<String>(
    					Arrays.asList(new String[]{"all", itemName})));
    			}
    			
    			try {
    				Integer.parseInt(itemQuantity);
    			}
    			catch(Exception e) {
    				return new Action("Given quantity was not a number or 'all'.");
    			}
    			
				return new Action(inputWords.get(0), new ArrayList<String>(
    					Arrays.asList(new String[]{itemQuantity, itemName})));
    			
    			
    		// format: 'attack: <enemy> with <weapon>// using <attack type>'
    		case 3:
    			if (!(inputWords.contains("with"))) return new Action("Keyword 'using' is missing.");// doesn't have with nor using
    			//  && inputWords.contains("using")
    			// or 'with' 
    			
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
    			if (inputWords.size() <= 3) return new Action("No parameters after keyword 'with' given.");// nothing after 'with'
    			String weapon = inputWords.get(3);
    			
    			// if (weapon.equals("using")) return new Action("No weapon was included.");// there is no weapon included
    			
    			// inputWords.indexOf("using")
    			// inputWords.indexOf("using")
    			if (inputWords.size() > 4) {
    				
    				Integer inputSize = inputWords.size();
    				for (int i=4; i<inputSize; i++) {
    					weapon += " " + inputWords.get(4);
    					inputWords.remove(4);
    				}
    			}
    			
    			
    			// combine words after 'using'
//    			if (inputWords.size() < 6) return new Action("No attack type was given.");// nothing after 'using'
//    			String attackType = inputWords.get(5);
//    			
//    			if (inputWords.size() > 6) {
//    				for (int i=6; i<inputWords.size(); i++) {
//    					attackType += " " + inputWords.get(i);
//    					inputWords.remove(6);
//    				}
//    			}
    			
    			// , attackType
    			return new Action("attack", new ArrayList<String>(
    					Arrays.asList(new String[]{target, weapon})));
    			
    		case 4:
    			if (inputWords.size() == 1) return new Action("No parameters given.");// no params
    			if (inputWords.size() <= 3) return new Action("Not enough parameters.");
    			
    			if (inputWords.indexOf("into") < 0) return new Action("Keyword 'into' is missing.");
    			
    			Integer intoIndex = inputWords.indexOf("into");
    			String weaponName = inputWords.get(1).toLowerCase();
				for (int i=2; i<intoIndex; i++) {
					weaponName += " " + inputWords.get(2).toLowerCase();
					inputWords.remove(2);
				}
				
				Integer inputSize = inputWords.size();
				String slotName = inputWords.get(3).toLowerCase();
				for (int i=4; i<inputSize; i++) {
					slotName += " " + inputWords.get(4).toLowerCase();
					inputWords.remove(4);
				}
    			
				return new Action(inputWords.get(0), new ArrayList<String>(
    					Arrays.asList(new String[]{weaponName, slotName})));
				
    		case 5:
    			if (inputWords.size() == 1) return new Action("No parameters given.");
    			
    			inputSize = inputWords.size();
    			String unequipName = inputWords.get(1).toLowerCase();
				for (int i=2; i<inputSize; i++) {
					unequipName += " " + inputWords.get(2).toLowerCase();
					inputWords.remove(2);
				}
				
				return new Action(inputWords.get(0), new ArrayList<String>(
    					Arrays.asList(new String[]{unequipName})));
				
    		case 6:
    			if (!(inputWords.contains("on"))) return new Action("Keyword 'on' is missing.");// doesn't have with nor using
    			//  && inputWords.contains("using")
    			// or 'with' 
    			
    			// combine words between 'attack:' and 'with'
    			String item = inputWords.get(1);

    			if (item.equals("on")) return new Action("No target was included.");// there is no target included
    			
    			if (inputWords.indexOf("on") > 2) {
    				Integer withIndex = inputWords.indexOf("on");
    				
    				for (int i=2; i<withIndex; i++) {
    					item += " " + inputWords.get(2);
    					inputWords.remove(2);
    				}
    			}
    			
    			
    			// combine words between 'with' and 'using'
    			if (inputWords.size() <= 3) return new Action("No parameters after keyword 'on' given.");// nothing after 'with'
    			String location = inputWords.get(3);
    			
    			// if (weapon.equals("using")) return new Action("No weapon was included.");// there is no weapon included
    			
    			// inputWords.indexOf("using")
    			// inputWords.indexOf("using")
    			if (inputWords.size() > 4) {
    				for (int i=4; i<inputWords.size(); i++) {
    					location += " " + inputWords.get(4);
    					inputWords.remove(4);
    				}
    			}
    			
    			if(inputWords.contains("door")) {
    				inputWords.remove(inputWords.indexOf("door"));
    			}
    			
    			
    			// combine words after 'using'
//    			if (inputWords.size() < 6) return new Action("No attack type was given.");// nothing after 'using'
//    			String attackType = inputWords.get(5);
//    			
//    			if (inputWords.size() > 6) {
//    				for (int i=6; i<inputWords.size(); i++) {
//    					attackType += " " + inputWords.get(i);
//    					inputWords.remove(6);
//    				}
//    			}
    			
    			// , attackType
    			return new Action("use", new ArrayList<String>(
    					Arrays.asList(new String[]{item, location})));
    	}
    	
    	// command doesn't exist
    	return new Action("How did you get here??");
    }
}
