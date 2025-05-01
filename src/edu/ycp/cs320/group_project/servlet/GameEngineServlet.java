package edu.ycp.cs320.group_project.servlet;

import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ycp.cs320.TBAG.controller.ASCIIOutput;
// our imports
import edu.ycp.cs320.TBAG.controller.ConsoleInterpreter;
import edu.ycp.cs320.TBAG.controller.EntityController;
import edu.ycp.cs320.TBAG.controller.FightController;
import edu.ycp.cs320.TBAG.controller.PlayerController;
import edu.ycp.cs320.TBAG.controller.RoomContainer;
import edu.ycp.cs320.TBAG.model.Action;
import edu.ycp.cs320.TBAG.model.EnemyModel;
import edu.ycp.cs320.TBAG.model.EntityInventory;
import edu.ycp.cs320.TBAG.model.EntityModel;
import edu.ycp.cs320.TBAG.model.RoomInventory;
import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.PlayerModel;
import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.Weapon;
import edu.ycp.cs320.TBAG.tbagdb.DBController;
import edu.ycp.cs320.TBAG.tbagdb.persist.DerbyDatabase;
import edu.ycp.cs320.TBAG.tbagdb.persist.IDatabase;

public class GameEngineServlet extends HttpServlet {

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	HttpSession session = req.getSession();
        String userInput = (String) session.getAttribute("userInput");

        if (userInput != null) {
            req.setAttribute("userInput", userInput);
            session.removeAttribute("userInput");
        }
        List<String> foundCommands = (List<String>) session.getAttribute("foundCommands");
        List<String> gameHistory = (List<String>) session.getAttribute("gameHistory");
        if (gameHistory == null) {
            DerbyDatabase db = (DerbyDatabase) session.getAttribute("db");
            if (db == null) {
                db = new DerbyDatabase("test");
                session.setAttribute("db", db);
            }
            gameHistory = db.getGameHistory();
            foundCommands = db.getFoundCommands();
            session.setAttribute("gameHistory", gameHistory);
            session.setAttribute("foundCommands", foundCommands);
        }
        
        req.setAttribute("gameHistory", gameHistory);
        req.setAttribute("foundCommands", foundCommands);        
        sessionPlaySound(req, "playHakeSound");
 
        
        int sudoStage = 0;
        if (session.getAttribute("sudoStage") != null) {
        	sudoStage = ((Integer) session.getAttribute("sudoStage"));
        	if (sudoStage > 0) {
        		sudoStage ++;
            	session.setAttribute("sudoStage", sudoStage);
        	}
        }

//        if (sudoStage == 0) req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
//        else {
//        	System.out.println("Started sudo easter egg");
//        }
        req.getRequestDispatcher("/_view/game.jsp").forward(req, resp);
    }
    


    
    private RoomContainer createRooms() {
        RoomContainer rooms = new RoomContainer();
        
        Room yellow = new Room("Yellow Room", "This is the center room.");
        yellow.setConnectedRoom("east", 1);// to orange
        yellow.setConnectedRoom("north", 2);// to red
        rooms.addRoom(yellow);
        
        Room orange = new Room("Orange Room", "This room connects to the center room.");
        orange.setConnectedRoom("west", 0);// to yellow
        orange.setConnectedRoom("east", 3);// to blue
        rooms.addRoom(orange);
        
        Room red = new Room("Red Room", "This room connects to the Orange room.");
        red.setConnectedRoom("south", 0);// to yellow
        rooms.addRoom(red);
        
        Room blue = new Room("Blue Room", "This room leads to the Green room.");
        blue.setConnectedRoom("west", 1);// to orange
        blue.setConnectedRoom("north", 4);// to green
        rooms.addRoom(blue);
        
        Room green = new Room("Green Room", "This room connects to 3 rooms.");
        green.setConnectedRoom("south", 3);// to blue
        green.setConnectedRoom("east", 5);// to purple
        green.setConnectedRoom("north", 6);// to white
        rooms.addRoom(green);
        
        Room purple = new Room("Purple Room", "This room connects to the Green room.");
        purple.setConnectedRoom("west", 4);// to green
        rooms.addRoom(purple);
        
        Room white = new Room("White Room", "This room is a dead end.");
        white.setConnectedRoom("south", 4);// to green
        rooms.addRoom(white);
        
        return rooms;
    }

    
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get or create the session
        HttpSession session = req.getSession(true);
        
        DerbyDatabase db = (DerbyDatabase) session.getAttribute("db");
        if (db == null) {
            db = new DerbyDatabase("test");
            session.setAttribute("db", db);
        }

        // Always pull fresh game history from DB
        List<String> gameHistory = db.getGameHistory();
        session.setAttribute("gameHistory", gameHistory);
        ConsoleInterpreter interpreter = new ConsoleInterpreter();
        
        // Retrieve the game history from the session (or create a new one if it doesn't exist)
//        gameHistory = (List<String>) session.getAttribute("gameHistory");
//        if (gameHistory == null) {
//            gameHistory = new ArrayList<>();
//            session.setAttribute("gameHistory", gameHistory);
//        }
        
        //Check if player current room is in session history, set if yes, initialize if not
        PlayerModel playerModel = (PlayerModel)session.getAttribute("player");
        playerModel = db.GetPlayer();
        
        PlayerController player = new PlayerController(playerModel);
        
        
        // get found commands
        List<String> foundCommands = db.getFoundCommands();


        List<Room> rooms = new ArrayList<>();
        //Room dummy = new Room();
       // rooms.add(dummy);
        List<Room> connections = new ArrayList<>();
        //connections.add(dummy);
//        rooms = (List<Room>)session.getAttribute("rooms");
        rooms = db.getRooms();
        connections = db.getConnections();
        
        for(int i = 0; i<rooms.size(); i++) {
        	rooms.get(i).setConnections(connections.get(i).getHashMap());
        }

//        if (rooms == null) {
//        	//rooms.createHardcodedRooms();
//        }
        
        
        
        if (session.getAttribute("sudoStage") == null) {
        	session.setAttribute("sudoStage", 0);
        }
        
        int sudoStage = (Integer) session.getAttribute("sudoStage");
        
        
        // Process user input
        String userInput = req.getParameter("userInput");
        req.getSession().setAttribute("userInput", userInput);
        String systemResponse;

        
        //Case for sudo easter egg (does not require input after initial stage so just goes anyway
        if (sudoStage > 0) {
    		systemResponse = "";
    		
        	switch (sudoStage) {
        	case 2:
        		systemResponse = "<br><br>[INFO] Removing /etc...<br>";
        		break;
        	case 3:
        		systemResponse = "[INFO] Removing /bin...<br>";
        		break;
        	case 4:
        		systemResponse = "[INFO] Removing /home...<br>";
        		break;
        	case 5:
        		systemResponse = "[INFO] Removing /reality...<br>";
        		break;
        	case 6:
        		systemResponse = "[INFO] Removing /fourth_wall...<br>";
        		break;
        	case 7:
        		systemResponse = "[ERROR] Failed to delete /player_conscience: Access Denied<br>";
        		break;
        	case 8:
        		systemResponse = "[INFO] Corrupting game files...<br>";
        		break;
        	case 9:
        		systemResponse = "<span class=\"ascii--art\">[INFO] Game integrity: 0%</span><br>";
        		break;
        	case 10:
        		systemResponse = "<br>Segmentation fault (core melted)<br>";
        		break;
        	case 11:
        		systemResponse = "[GLITCH] ░D░A░T░A░ ░C░O░R░R░U░P░T░E░D░. ░H░E░L░P░.";
        		break;
        	default:
        		System.exit(0); //Closes the program (crashing breaks the illusion cause it throws an error. The only way to make this work is to close the program entirely)
        		break;
        	}
        	addToGameHistory(db, gameHistory, systemResponse);
        }
        
        if (userInput != null && !userInput.trim().isEmpty() && sudoStage == 0) {
            // Add user input to the game history

        	addToGameHistory(db, gameHistory, "C:&bsol;Users&bsol;exampleUser&gt; " + ((userInput == null)? "": userInput));// add user input to console (for user's reference)
            
            Action userAction = interpreter.ValidateInput(userInput);
            systemResponse = userAction.GetErrorMessage();// if the userAction isn't valid, it stays as the error msg
            
            // action details here (strings for now, need more structure for true game)
            if (userAction.IsValid()) {
            	ArrayList<String> params = userAction.GetParams();
            	
            	switch (userAction.GetMethod()) {
        			//sudo rm -rf \ easter egg
	        		case "sudoEasterEgg" :
	        			systemResponse = "Warning: executing 'rm -rf /' is extremely dangerous.<br>"
	        					+ "Proceeding anyway...<br>"
	        					+ "Deleting system...";
	        			sudoStage = 1;
	        	        session.setAttribute("sudoStage", sudoStage);
        			break;
        			
	        		case "quit":
	        			resp.sendRedirect("index");
	        		break;
        			
        			//hake easter egg test
	        		case "hakeTest" :
	        			systemResponse = ASCIIOutput.profAsciiEasterEgg(this, "hake");
	        			session.setAttribute("playHakeSound", true);
	        		break;
	        		case "babcockTest":
	        			systemResponse = ASCIIOutput.profAsciiEasterEgg(this, "babcock");
	        		break;
	        		case "newSave":
	        			db = new DerbyDatabase("test");
	        			db.create();
	        			systemResponse = "Creating new save...";
	        		break;
	        		case "clearChat":
	        			db.clearGameHistory();
	        			gameHistory.clear();
	        			addToGameHistory(db, gameHistory, "Chat logs cleared...");
	        		break;
	        		case "showMap":
	        			systemResponse = showMapString();
	        			if (!foundCommands.contains("showMap")) addToFoundCommands(db,foundCommands,"showMap");
	        		break;
            	
            		// TYPE 1 COMMANDS:
            		case "move":
            			if (!foundCommands.contains("move")) addToFoundCommands(db,foundCommands,"move");
            			
            			//Integer nextRoom = rooms.nextConnection(player.getCurrentRoomIndex(),
            					//params.get(0));
            			//System.out.println(player.getCurrentRoomIndex());
            			//boolean doesconnectionexist = rooms.get(player.getCurrentRoomIndex()).doesKeyExist(params.get(0));
            			Integer nextRoom = null;
            			
            			
            				nextRoom = rooms.get(player.getCurrentRoomIndex() - 1).getConnectedRoom(params.get(0));
            				//for now the room will be null if it doesn't exist or is locked
            				if(nextRoom <= 0) {
            					nextRoom = null;
            				}
            			
            			
            			
            			
            			if (nextRoom == null) {
            				systemResponse = String.format("The current room doesn't have a room %s of it.",
            						params.get(0));
            				break;
            			}
            			
            			player.setCurrentRoomIndex(nextRoom);
            			db.UpdatePlayerRoom(player.getCurrentRoomIndex());
            			
            			//These used to be offset by 1
            			String short_description = rooms.get(nextRoom - 1).getShortRoomDescription();
            			String long_description = rooms.get(nextRoom - 1).getLongRoomDescription();
            			systemResponse = String.format("Moving %s...<br><br>Entered %s.<br>%s",
            					params.get(0),
            					short_description,
            					long_description);
            		break;
            		
            		case "use":
            			if (!foundCommands.contains("use")) addToFoundCommands(db,foundCommands,"use");
            			
            			systemResponse = String.format("Used %s...", params.get(0));
            		break;
            		
            		// TYPE 3 COMMANDS
            		case "pickup":
            			if (!foundCommands.contains("pickup")) addToFoundCommands(db,foundCommands,"pickup");
            			
            			systemResponse = String.format("Picking up %s...<br><br>", params.get(1));
            			
            			Integer pickupQuantity;
            			if (params.get(0).equals("all")) pickupQuantity = Integer.MAX_VALUE;
            			else pickupQuantity = Integer.parseInt(params.get(0));
            			
            			// pickup all items
            			if (params.get(0).equals("all") && params.get(1).equals("items")) {
            				
            				
            				Set<Item> roomInventoryKeys = new HashSet<Item>();
            				roomInventoryKeys.addAll(rooms.get(player.getCurrentRoomIndex() - 1).getItems().keySet());            			
            				if (roomInventoryKeys.isEmpty()){
                				systemResponse = String.format("This room does not contain any items to pickup.<br>");
                				break;
                			}
            				
            				for (Item roomItem : roomInventoryKeys) {
            					Integer itemQuantity = player.PickUp(rooms.get(player.getCurrentRoomIndex() - 1), roomItem, pickupQuantity);
                    			systemResponse += String.format("Picked up %d %s<br>",
                    					itemQuantity, roomItem.GetName());
            				}
            				
            				db.UpdateRoomInventory(player.getCurrentRoomIndex(), rooms.get(player.getCurrentRoomIndex() - 1).getRoomInventory());
            				db.UpdatePlayerInventory(player.getInventory());
            				break;
            			}
            			
            			Item pickupItem = rooms.get(player.getCurrentRoomIndex() - 1).getRoomInventory().GetItemByName(params.get(1));
            			if (pickupItem == null) {
            				systemResponse = String.format("This room does not contain an item named %s.<br>",
            						params.get(1));
            				break;
            			}
            			
            			Integer roomQuantity = player.PickUp(rooms.get(player.getCurrentRoomIndex() - 1), pickupItem, pickupQuantity);
            			systemResponse += String.format("Picked up %d %s<br>",
            					roomQuantity, params.get(1));
            			
        				db.UpdateRoomInventory(player.getCurrentRoomIndex(), rooms.get(player.getCurrentRoomIndex() - 1).getRoomInventory());
        				db.UpdatePlayerInventory(player.getInventory());
            		break;
            		
            		case "drop":
            			if (!foundCommands.contains("drop")) addToFoundCommands(db,foundCommands,"drop");
            			
            			systemResponse = String.format("Dropping %s...<br><br>", params.get(1));
            			
            			Integer dropQuantity;
            			if (params.get(0).equals("all")) dropQuantity = Integer.MAX_VALUE;
            			else dropQuantity = Integer.parseInt(params.get(0));
            			
            			// pickup all items
            			if (params.get(0).equals("all") && params.get(1).equals("items")) {
            				
            				Set<Item> playerInventoryKeys = new HashSet<Item>();
            				playerInventoryKeys.addAll(player.getInventory().GetItems().keySet());
            				
            				if (playerInventoryKeys.isEmpty()){
                				systemResponse = String.format("The player does not have any items to drop.<br>");
                				break;
                			}
            				
            				for (Item playerItem : playerInventoryKeys) {
            					Integer itemQuantity = player.Drop(
            							rooms.get(player.getCurrentRoomIndex() - 1),
            							playerItem, dropQuantity);
            					
                    			systemResponse += String.format("Dropped %d %s<br>",
                    					itemQuantity, playerItem.GetName());
            				}
            				
            				db.UpdateRoomInventory(player.getCurrentRoomIndex(), rooms.get(player.getCurrentRoomIndex() - 1).getRoomInventory());
            				db.UpdatePlayerInventory(player.getInventory());
            				break;
            			}
            			
            			Item dropItem = player.getInventory().GetItemByName(params.get(1));
            			if (dropItem == null) {
            				systemResponse = String.format("This room does not contain an item named %s.<br>",
            						params.get(1));
            				break;
            			}
            			
            			Integer playerQuantity = player.Drop(
            					rooms.get(player.getCurrentRoomIndex() - 1),
            					dropItem, dropQuantity);
            			
            			systemResponse += String.format("Dropped %d %s<br>",
            					playerQuantity, params.get(1));
            			
        				db.UpdateRoomInventory(player.getCurrentRoomIndex(), rooms.get(player.getCurrentRoomIndex() - 1).getRoomInventory());
        				db.UpdatePlayerInventory(player.getInventory());
            		break;
            		
            		case "equip":
            			if (!foundCommands.contains("equip")) addToFoundCommands(db,foundCommands,"equip");
            			
            			systemResponse = String.format("Equipping %s...<br><br>", params.get(0));
            			
            			
            			Weapon equipItem = (Weapon) player.getInventory().GetWeaponByName(params.get(0));
            			if (equipItem == null) {
            				systemResponse = String.format("The player does not have a weapon named %s.<br>",
            						params.get(0));
            				break;
            			}
            			
            			String weaponSlot = "";
               			for (String word : params.get(1).split(" "))
               				weaponSlot += word.substring(0, 1).toUpperCase() + word.substring(1) + " ";
               			weaponSlot = weaponSlot.trim();
            			
            			if (!EntityInventory.WeaponSlots.contains(weaponSlot)) {
            				systemResponse = String.format("The player does not have a weapon slot named %s.<br>",
            						params.get(1));
            				break;
            			}
            			
            			player.getInventory().ExtractItem(equipItem);
            			player.getInventory().EquipWeapon(weaponSlot, equipItem);
            			
            			systemResponse += String.format("Equipped %s into %s.<br>",
            					params.get(0), weaponSlot);
            			
            			db.UpdatePlayerInventory(player.getInventory());
            		break;
            		
            		case "unequip":
            			Map<String, Weapon> weaponSlots = player.getInventory().GetWeaponsAsSlots();
            			String unequipName = "";
            			
            			// make camel case
            			for (String word : params.get(0).split(" "))
            				unequipName += word.substring(0, 1).toUpperCase() + word.substring(1) + " ";
            			unequipName = unequipName.trim();
            			
            			// using slot name
            			if (weaponSlots.containsKey(unequipName)) {
            				if (!foundCommands.contains("unequip")) addToFoundCommands(db,foundCommands,"unequip");
            				if (!foundCommands.contains("unequip_slot")) addToFoundCommands(db,foundCommands,"unequip_slot");
            				
            				Weapon unequippedWeapon = player.getInventory().UnequipWeaponInSlot(unequipName);
            				
            				systemResponse = String.format("Unequipping %s from %s...<br><br>", 
            						unequippedWeapon.GetName(), params.get(0));
            				
            				db.UpdatePlayerInventory(player.getInventory());
            				break;
            			}
            			
            			// no weapon in slot
            			if (EntityInventory.WeaponSlots.contains(unequipName)) {
            				if (!foundCommands.contains("unequip")) addToFoundCommands(db,foundCommands,"unequip");
            				if (!foundCommands.contains("unequip_slot")) addToFoundCommands(db,foundCommands,"unequip_slot");
            				
            				systemResponse = String.format("There is no weapon equipped in the %s.<br>", 
            						params.get(0));
        					break;
            			}
            			
            			// name could be the name of a weapon
        				for (String slotName : weaponSlots.keySet()) {
        					if (weaponSlots.get(slotName).GetName().toLowerCase().equals(unequipName)) {
        						unequipName = slotName;
        						break;
        					}
        				}
        				
        				// using weapon name
        				if (weaponSlots.containsKey(unequipName)) {
        					if (!foundCommands.contains("unequip")) addToFoundCommands(db,foundCommands,"unequip");
        					if (!foundCommands.contains("unequip_weapon")) addToFoundCommands(db,foundCommands,"unequip_weapon");
        					
            				Weapon unequippedWeapon = player.getInventory().UnequipWeaponInSlot(unequipName);
            				
            				systemResponse = String.format("Unequipping %s from %s...<br><br>", 
            						unequippedWeapon.GetName(), params.get(0));
            				
            				db.UpdatePlayerInventory(player.getInventory());
            				break;
        				}
            			
            			systemResponse = String.format("There is no weapon equipped or slot named '%s'.",
            					params.get(0));
            		break;
            		
            		// DESCRIBE COMMANDS
            		case "describe":
            			switch (params.get(0)) {
            				case "room":
            					if (!foundCommands.contains("describeGroup_room")) addToFoundCommands(db,foundCommands,"describeGroup_room");
            					if (!foundCommands.contains("describe_room")) addToFoundCommands(db,foundCommands,"describe_room");
            					
            					System.out.println(db.GetPlayer().getCurrentRoomIndex());
            					systemResponse = String.format("Describing room...<br><br>%s<br>%s",
                    					rooms.get(db.GetPlayer().getCurrentRoomIndex() - 1).getShortRoomDescription(),
                    					rooms.get(db.GetPlayer().getCurrentRoomIndex() - 1).getLongRoomDescription());
            				break;
            				
            				//  [######--]
            				
            				case "stats":
            					if (!foundCommands.contains("describeGroup_attack")) addToFoundCommands(db,foundCommands,"attack");
            					if (!foundCommands.contains("describe_stats")) addToFoundCommands(db,foundCommands,"describe_stats");
            					
            					Integer healthBarSize = 10;
            					Double lifeRatio = player.getHealth() / player.getMaxHealth();
            					Integer healthBarLength = (int) Math.round(lifeRatio * healthBarSize);
            					
            					systemResponse = String.format("Describing stats...<br><br>Lives: %d<br>Health: [%s%s] (%.1f / %.1f)",
            							player.getLives(),
            							repeatString("#", healthBarLength),
										repeatString("-", healthBarSize - healthBarLength),
										player.getHealth(),
            							player.getMaxHealth());
            				break;
            				
            				case "enemies":
            					if (!foundCommands.contains("describeGroup_attack")) addToFoundCommands(db,foundCommands,"describeGroup_attack");
            					if (!foundCommands.contains("describe_enemies")) addToFoundCommands(db,foundCommands,"describe_enemies");
                				
            					//TODO: Use this method once set up
//            					ArrayList<EnemyModel> enemies = DBController.getEnemiesByRoomId(db, DBController.getPlayerCurrentRoom(db));
            					ArrayList<EnemyModel> enemies = db.GetEnemiesInRoom(DBController.getPlayerCurrentRoom(db));
            					systemResponse = String.format("Describing enemies...<br><br>");
            					
            					// remove dead enemies
            					for (int i=enemies.size()-1; i>=0; i--)
            						if (enemies.get(i).getHealth() == 0)
            							enemies.remove(i);
            					
            					// no enemies in room
            					if (enemies.size() == 0) {
            						systemResponse += String.format("There are no enemies in this room.");
            						break;
            					}
            					
            					systemResponse += String.format("Enemies in this room:");
            					
            					Integer enemyCount = 0;
            					for (int i=0; i<enemies.size(); i++) {
            						if (enemies.get(i).getHealth() == 0) continue;
            						enemyCount++;
            						
                					healthBarSize = 10;
                					lifeRatio = enemies.get(i).getHealth() / enemies.get(i).getMaxHealth();
                					healthBarLength = (int) Math.round(lifeRatio * healthBarSize);
            						
            						systemResponse += String.format("<br>&num;%d: %s<br> - Health: [%s%s] (%.1f / %.1f)<br> - %s<br>",
            								enemies.size(), enemies.get(i).getName(),
            								repeatString("#", healthBarLength),
    										repeatString("-", healthBarSize - healthBarLength),
    										enemies.get(i).getHealth(), enemies.get(i).getMaxHealth(),
            								enemies.get(i).getDescription());
            					}
            					
            					// all enemies are dead
            					if (enemies.size() == 0) {
            						systemResponse = String.format("Describing enemies...<br><br>");
            						systemResponse += String.format("There are no enemies in this room.");
            					}
            				break;
            				
            				case "moves":
            					if (!foundCommands.contains("describeGroup_room")) addToFoundCommands(db,foundCommands,"describeGroup_room");
            					if (!foundCommands.contains("describe_moves")) addToFoundCommands(db,foundCommands,"describe_moves");
            					
            					systemResponse = String.format("Describing moves...<br><br>Possible moves:");
            					
            					List<String> roomConnections = rooms.get(player.getCurrentRoomIndex() - 1).getAllConnections();
            					List<String> directions = new ArrayList<String>(Arrays.asList(new String[] {
            							"East", "South", "North", "West"}));
            					
            					
            					for (int i=0; i<roomConnections.size(); i++) {
            						String direction = directions.get(i);
            						String camelCaseDirection = direction.substring(0, 1).toUpperCase() + direction.substring(1).toLowerCase();
            						Integer connectionID = Integer.parseInt(roomConnections.get(i));
            						
            						// connection doesn't exist
            						if (connectionID == 0) continue;
            						
            						System.out.println(String.format("%s : %d", roomConnections.get(i), connectionID));
            						systemResponse += String.format("<br> - %s &mdash;&mdash;&#62; %s", camelCaseDirection,
            								rooms.get(connectionID - 1).getShortRoomDescription());
            					}
            				break;
            				
            				case "directions":
            					if (!foundCommands.contains("describeGroup_room")) addToFoundCommands(db,foundCommands,"describeGroup_room");
            					if (!foundCommands.contains("describe_directions")) addToFoundCommands(db,foundCommands,"describe_directions");
            					
            					systemResponse = String.format("Describing directions...<br><br>Possible directions:<br>");
            				
            					for (String direction : ConsoleInterpreter.MOVE_DIRECTIONS)
            						systemResponse += String.format(" - %s<br>",
            								direction.substring(0, 1).toUpperCase() + direction.substring(1).toLowerCase());
            				break;
            				
            				case "inventory":
            					if (!foundCommands.contains("describeGroup_items")) addToFoundCommands(db,foundCommands,"describeGroup_items");
            					if (!foundCommands.contains("describe_inventory")) addToFoundCommands(db,foundCommands,"describe_inventory");
                				
            					EntityInventory playerInventory = db.GetPlayerInventory();
            					HashMap<Item, Integer> playerItems = playerInventory.GetItems();
            					HashMap<String, Weapon> playerEquips = playerInventory.GetWeaponsAsSlots();
            					systemResponse = String.format("Describing inventory...<br><br>");

            					// no items in inventory
            					if (playerItems.size() == 0 && playerEquips.size() == 0) {
            						systemResponse += String.format("There are no items in your inventory.");
            						break;
            					}
            					
            					systemResponse += String.format("Items in your inventory:");
            					
            					for (String slot : playerEquips.keySet()) 
            						systemResponse += String.format("<br><br>%s: %s<br> - Damage: %.1f<br> - %s",
            								slot, playerEquips.get(slot).GetName(),
            								playerEquips.get(slot).GetDamage(),
            								playerEquips.get(slot).GetDescription());
            					
            					for (Item playerItem : playerItems.keySet()) {
            						systemResponse += String.format("<br><br>%s: %d<br> - %s",
            								playerItem.GetName(), playerItems.get(playerItem),
            								playerItem.GetDescription());
            						
            						// if item is a weapon, also display the damage
            						if (playerItem.getClass().equals(Weapon.class))
            							systemResponse += String.format("<br> - Damage: %.1f",
            									((Weapon) playerItem).GetDamage());
            					}
            						
            				break;
            				
            				case "items":
            					if (!foundCommands.contains("describeGroup_items")) addToFoundCommands(db,foundCommands,"describeGroup_items");
            					if (!foundCommands.contains("describe_items")) addToFoundCommands(db,foundCommands,"describe_items");
                				
            					RoomInventory roomInventory = db.GetRoomInventoryByID(player.getCurrentRoomIndex());
            					HashMap<Item, Integer> roomItems = roomInventory.GetItems();
            					systemResponse = String.format("Describing items...<br><br>");
            					
            					// no enemies in room
            					if (roomItems.size() == 0) {
            						systemResponse += String.format("There are no items in this room.");
            						break;
            					}
            					
            					systemResponse += String.format("Items in this room:");
            					
            					for (Item roomItem : roomItems.keySet()) {
            						systemResponse += String.format("<br><br>%s: %d<br> - %s",
            								roomItem.GetName(), roomItems.get(roomItem),
            								roomItem.GetDescription());

            						// if item is a weapon, also display the damage
            						if (roomItem.getClass().equals(Weapon.class))
            							systemResponse += String.format("<br> - Damage: %.1f",
            									((Weapon) roomItem).GetDamage());
            					}
            				break;
            				
            				default:
            					systemResponse = String.format("Cannot describe %s.",
            							params.get(0));
            				break;
            			}
            			
            		break;
            		
            		// TYPE 3 COMMANDS
            		case "attack":
            			if (!foundCommands.contains("attack")) { 
            				addToFoundCommands(db,foundCommands,"attack");
            			}
            			
            			EntityInventory playerInventory = db.GetPlayerInventory();
            			weaponSlots = playerInventory.GetWeaponsAsSlots();
            			String attackName = "";
            			
            			// make camel case
            			for (String word : params.get(1).split(" "))
            				attackName += word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ";
            			attackName = attackName.trim();
            			
            			
            			// get weapon slot
            			if (!weaponSlots.containsKey(attackName)) {
            				if (EntityInventory.WeaponSlots.contains(attackName.toLowerCase())) {
            					systemResponse = String.format("There is no weapon equipped in the %s.<br>", 
            						attackName);
        						break;
            				}
            				
            				// name could be the name of a weapon
            				for (String slotName : weaponSlots.keySet()) {
            					if (weaponSlots.get(slotName).GetName().toLowerCase().equals(attackName.toLowerCase())) {
            						attackName = slotName;
            						break;
            					}
            				}
            			}
            			
            			if (!weaponSlots.containsKey(attackName)) {
        					systemResponse = String.format("There is no weapon equipped in the %s.<br>", 
            						attackName);
        						break;
            			}
            			
            			//TODO: Use this method once set up
//            			ArrayList<EnemyModel> roomEnemies = DBController.getEnemiesByRoomId(db, DBController.getPlayerCurrentRoom(db));
            			ArrayList<EnemyModel> roomEnemies = db.GetEnemiesInRoom(player.getCurrentRoomIndex());
            			ArrayList<EntityModel> fighters = new ArrayList<EntityModel>();
            			fighters.add(db.GetPlayer());
            			fighters.addAll(roomEnemies);

            			Integer attackIndex = -1;         			
            			try {
            				attackIndex = Integer.parseInt(params.get(0));
            			} catch (Exception e) {
            				
            				// index might be the name of an enemy
            				for (EnemyModel enemy : roomEnemies) 
            					if (enemy.getName().toLowerCase().equals(params.get(0).toLowerCase()))
            						attackIndex = fighters.indexOf(enemy);
            			}
            			
            			if (attackIndex == -1) {
        					systemResponse = String.format("There is no enemy with index or name of %s.<br>", 
            						params.get(0));
            				break;
            			}
            			
            			
            			FightController fightController = new FightController(fighters, db);
            			if (fightController.GetFighter(attackIndex).getHealth() == 0) {
        					systemResponse = String.format("There is no enemy with index or name of %s.<br>", 
            						params.get(0));
            				break;
            			}

            			String attackedName = ((EnemyModel) fightController.GetFighter(attackIndex)).getName();
            			
            			Double attackDamage = fightController.TakePlayerTurn(0, attackIndex, attackName);
            			systemResponse = String.format("Attacked %s with %s.<br><br>%s took %.1f damage.<br>",
            					attackedName,
            					userAction.GetParams().get(1),
            					attackedName,
            					attackDamage
            					);
            			
            			if (fightController.GetFighter(attackIndex).getHealth() == 0) {
            				new EntityController(fightController.GetFighter(attackIndex)).Die(
            						db, rooms.get(player.getCurrentRoomIndex() - 1));
            				
            				systemResponse += String.format("%s has died.<br>",
            						attackedName);
            			}
            			
            			for (int i=0; i<roomEnemies.size(); i++) {
            				if (i != 0) systemResponse += "<br>";
            				
            				if (player.getHealth() == 0) {
            					player.Die(db, rooms.get(player.getCurrentRoomIndex() - 1));
            					break;
            				}
            				
            				if (roomEnemies.get(i).getHealth() == 0) continue;
            				Double enemyDamage = fightController.TakeEnemyTurn(i + 1);
            				
            				if (enemyDamage == 0) continue;
            				systemResponse += String.format("%s attacked. You took %.1f damage.",
            						((EnemyModel) fightController.GetFighter(i + 1)).getName(),
            						enemyDamage);
            			}
            		break;
            		
            		default:
            			systemResponse = String.format("User inputted valid command of type: %s<br>", userAction.GetMethod());
            		break;
            	}
            }

            addToGameHistory(db, gameHistory, systemResponse);
            
            if (sudoStage == 0) {
            	addToGameHistory(db, gameHistory, "<br>");
            	addToGameHistory(db, gameHistory, "~-==============================-~");
            	addToGameHistory(db, gameHistory, "<br>");
            }
        }

        // Set the game history as a request attribute for the JSP
        session.setAttribute("player", playerModel);
        session.setAttribute("rooms", rooms);
        req.setAttribute("gameHistory", gameHistory);
        req.setAttribute("foundCommands", foundCommands);
        req.setAttribute("sudoStage", sudoStage);

        // Forward to the JSP file
//        req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
        resp.sendRedirect("game");
    }
    
    public void addToGameHistory(IDatabase db, List<String> gameHistory, String toAdd) {
    	gameHistory.add(toAdd);
    	db.addToGameHistory(toAdd);
    }
    
    public void addToFoundCommands(IDatabase db, List<String> foundCommands, String toAdd) {
    	foundCommands.add(toAdd);
    	db.addToFound(toAdd);
    }
    
    public String repeatString(String toRepeat, int amount) {
    	String toOut = "";
    	for (int i = 0; i < amount; i++) {
    		toOut += toRepeat;
    	}
    	return toOut;
    }
    
    public void sessionPlaySound(HttpServletRequest req, String attribute) {
    	HttpSession session = req.getSession();
    	
    	Boolean playSound = (Boolean) session.getAttribute(attribute);
        if (playSound != null && playSound) {
            req.setAttribute(attribute, true);
            session.removeAttribute(attribute); // ensure it's only played once
        }
    }
    
    public String showMapString() {
    	String mapString = 
    			  "        [L]                                    <br>" //1
    	    	+ "         |                                     <br>" //vertical connection
    	    	+ "    [L]–[H]–[L]                 [E]            <br>" //2
    	    	+ "         |                       |             <br>" //vertical connection
    	    	+ "    [E]–[H]–[E]–[G]     [M]     [H] [M]–[H]–[e]<br>" //3
    	    	+ "     |   v       |       |       |   |         <br>" //vertical connection
    	    	+ "[L]–[H]–[S] [L]–[H]–[b]–[H]–[E]–[H]–[L]        <br>" //4
    	    	+ "         |           |           |   v         <br>" //vertical connection
    	    	+ "        [e]         [H]         [G]–[H]        <br>" //5
    	    	+ "                                 |   ^         <br>" //vertical connection
    	    	+ "                                [E]–[L]        <br>" //6
    	    	+ "                                 |             <br>" //vertical connection
    	    	+ "                                [b]            <br>" //7
    	    	+ "                                 |             <br>" //vertical connection
    	    	+ "                                [C]            <br>"; //8
    	mapString = mapString.replace(" ", "&nbsp"); 	
    	String toOut =
    	  "<p class=\"map-string\">"
    	+ mapString
    	+ "</p>"; 
    	
    	return toOut;
    }
}