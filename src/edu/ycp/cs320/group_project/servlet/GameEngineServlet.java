package edu.ycp.cs320.group_project.servlet;

import java.io.IOException;
import java.util.ArrayList;
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
        
        List<String> gameHistory = (List<String>) session.getAttribute("gameHistory");
        List<String> foundCommands = (List<String>) session.getAttribute("foundCommands");
        
        

        req.setAttribute("gameHistory", gameHistory);
        req.setAttribute("foundCommands", foundCommands);
        
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
        req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get or create the session
        HttpSession session = req.getSession(true);
        
        DerbyDatabase db = new DerbyDatabase();
        
        ConsoleInterpreter interpreter = new ConsoleInterpreter();
        
        // Retrieve the game history from the session (or create a new one if it doesn't exist)
        List<String> gameHistory = (List<String>) session.getAttribute("gameHistory");
        if (gameHistory == null) {
            gameHistory = new ArrayList<>();
            session.setAttribute("gameHistory", gameHistory);
        }
//        db.UpdatePlayerHealth(new PlayerModel(150.0,1,1));
        
        //Check if player current room is in session history, set if yes, initialize if not
        PlayerModel playerModel = (PlayerModel)session.getAttribute("player");
        if (playerModel == null) {
        	playerModel = db.GetPlayer();
        }
        
        PlayerController player = new PlayerController(playerModel);
        
        
        // get found commands
        List<String> foundCommands = (List<String>) session.getAttribute("foundCommands");
        if (foundCommands == null) {
        	foundCommands = new ArrayList<>();
        	session.setAttribute("foundCommands", foundCommands);
        }

        List<Room> rooms = new ArrayList<>();
        List<Room> connections = new ArrayList<>();
        rooms = (List<Room>)session.getAttribute("rooms");
        if (rooms == null) {
        	rooms = db.getRooms();
        	connections = db.getConnections();
        	//rooms.createHardcodedRooms();
        }
        
        
        
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
        			
        			//hake easter egg test
	        		case "hakeTest" :
	        			systemResponse = ASCIIOutput.profAsciiEasterEgg(this, "hake");
	        		break;
	        		case "babcockTest":
	        			systemResponse = ASCIIOutput.profAsciiEasterEgg(this, "babcock");
	        		break;
	        		case "newSave":
	        			db = new DerbyDatabase();
	        			db.create();
	        			systemResponse = "Creating new save...";
	        		break;
            	
            		// TYPE 1 COMMANDS:
            		case "move":
            			if (!foundCommands.contains("move")) foundCommands.add("move");
            			
            			//Integer nextRoom = rooms.nextConnection(player.getCurrentRoomIndex(),
            					//params.get(0));
            			boolean doesconnectionexist = connections.get(player.getCurrentRoomIndex()).doesKeyExist(params.get(0));
            			Integer nextRoom;
            			
            			if(doesconnectionexist == true) {
            				nextRoom = connections.get(player.getCurrentRoomIndex()).getConnectedRoom(params.get(0));
            				//for now the room will be null if it doesn't exist or is locked
            				if(nextRoom <= 0) {
            					nextRoom = null;
            				}
            			}
            			
            			if(doesconnectionexist == false) {
            				nextRoom = null;
            			}
            			
            			
            			
            			if (nextRoom == null) {
            				systemResponse = String.format("The current room doesn't have a room %s of it.",
            						params.get(0));
            				break;
            			}
            			
            			player.setCurrentRoomIndex(nextRoom);
            			db.UpdatePlayerRoom(player.getCurrentRoomIndex());
            			String short_description = rooms.get(nextRoom).getShortRoomDescription();
            			String long_description = rooms.get(nextRoom).getLongRoomDescription();
            			systemResponse = String.format("Moving %s...<br><br>Entered %s.<br>%s",
            					params.get(0),
            					short_description,
            					long_description);
            		break;
            		
            		case "use":
            			if (!foundCommands.contains("use")) foundCommands.add("use");
            			
            			systemResponse = String.format("Used %s...", params.get(0));
            		break;
            		
            		// TYPE 3 COMMANDS
            		case "pickup":
            			if (!foundCommands.contains("pickup")) foundCommands.add("pickup");
            			
            			systemResponse = String.format("Picking up %s...<br><br>", params.get(1));
            			
            			Integer pickupQuantity;
            			if (params.get(0).equals("all")) pickupQuantity = Integer.MAX_VALUE;
            			else pickupQuantity = Integer.parseInt(params.get(0));
            			
            			// pickup all items
            			if (params.get(0).equals("all") && params.get(1).equals("items")) {
            				
            				
            				Set<Item> roomInventoryKeys = new HashSet<Item>();
            				roomInventoryKeys.addAll(rooms.getRoomInventory(player.getCurrentRoomIndex()).GetItems().keySet());
            			
            				if (roomInventoryKeys.isEmpty()){
                				systemResponse = String.format("This room does not contain any items to pickup.<br>");
                				break;
                			}
            				
            				for (Item roomItem : roomInventoryKeys) {
            					Integer itemQuantity = player.PickUp(rooms, roomItem, pickupQuantity);
                    			systemResponse += String.format("Picked up %d %s<br>",
                    					itemQuantity, roomItem.GetName());
            				}
            				
            				break;
            			}
            			
            			Item pickupItem = rooms.getRoomInventory(player.getCurrentRoomIndex()).GetItemByName(params.get(1));
            			if (pickupItem == null) {
            				systemResponse = String.format("This room does not contain an item named %s.<br>",
            						params.get(1));
            				break;
            			}
            			
            			Integer roomQuantity = player.PickUp(rooms, pickupItem, pickupQuantity);
            			systemResponse += String.format("Picked up %d %s<br>",
            					roomQuantity, params.get(1));
            		break;
            		
            		case "drop":
            			if (!foundCommands.contains("drop")) foundCommands.add("drop");
            			
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
            					Integer itemQuantity = player.Drop(rooms, playerItem, dropQuantity);
                    			systemResponse += String.format("Dropped %d %s<br>",
                    					itemQuantity, playerItem.GetName());
            				}
            				
            				break;
            			}
            			
            			Item dropItem = player.getInventory().GetItemByName(params.get(1));
            			if (dropItem == null) {
            				systemResponse = String.format("This room does not contain an item named %s.<br>",
            						params.get(1));
            				break;
            			}
            			
            			Integer playerQuantity = player.Drop(rooms, dropItem, dropQuantity);
            			
            			systemResponse += String.format("Dropped %d %s<br>",
            					playerQuantity, params.get(1));
            		break;
            		
            		case "equip":
            			if (!foundCommands.contains("equip")) foundCommands.add("equip");
            			
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
            				if (!foundCommands.contains("unequip")) foundCommands.add("unequip");
            				if (!foundCommands.contains("unequip_slot")) foundCommands.add("unequip_slot");
            				
            				Weapon unequippedWeapon = player.getInventory().UnequipWeaponInSlot(unequipName);
            				
            				systemResponse = String.format("Unequipping %s from %s...<br><br>", 
            						unequippedWeapon.GetName(), params.get(0));
            				break;
            			}
            			
            			// no weapon in slot
            			if (EntityInventory.WeaponSlots.contains(unequipName)) {
            				if (!foundCommands.contains("unequip")) foundCommands.add("unequip");
            				if (!foundCommands.contains("unequip_slot")) foundCommands.add("unequip_slot");
            				
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
        					if (!foundCommands.contains("unequip")) foundCommands.add("unequip");
        					if (!foundCommands.contains("unequip_weapon")) foundCommands.add("unequip_weapon");
        					
            				Weapon unequippedWeapon = player.getInventory().UnequipWeaponInSlot(unequipName);
            				
            				systemResponse = String.format("Unequipping %s from %s...<br><br>", 
            						unequippedWeapon.GetName(), params.get(0));
            				break;
        				}
            			
            			systemResponse = String.format("There is no weapon equipped or slot named '%s'.",
            					params.get(0));
            		break;
            		
            		// DESCRIBE COMMANDS
            		case "describe":
            			switch (params.get(0)) {
            				case "room":
            					if (!foundCommands.contains("describeGroup_room")) foundCommands.add("describeGroup_room");
            					if (!foundCommands.contains("describe_room")) foundCommands.add("describe_room");
            					
            					systemResponse = String.format("Describing room...<br><br>%s<br>%s",
                    					rooms.get(db.GetPlayer().getCurrentRoomIndex()).getShortRoomDescription(),
                    					rooms.get(db.GetPlayer().getCurrentRoomIndex()).getLongRoomDescription());
            				break;
            				
            				//  [######--]
            				
            				case "stats":
            					if (!foundCommands.contains("describeGroup_attack")) foundCommands.add("describeGroup_attack");
            					if (!foundCommands.contains("describe_stats")) foundCommands.add("describe_stats");
            					
            					Integer healthBarSize = 10;
            					Double lifeRatio = player.getHealth() / player.getMaxHealth();
            					Integer healthBarLength = (int) Math.round(lifeRatio * healthBarSize);
            					
            					systemResponse = String.format("Describing stats...<br><br>Lives: %d<br>Health: [%s%s] (%.1f / %.1f)",
            							DBController.getPlayerLives(db),
            							repeatString("#", healthBarLength),
										repeatString("-", healthBarSize - healthBarLength),
										DBController.getPlayerHealth(db),
            							DBController.getPlayerMaxHealth(db));
            				break;
            				
            				case "enemies":
            					if (!foundCommands.contains("describeGroup_attack")) foundCommands.add("describeGroup_attack");
            					if (!foundCommands.contains("describe_enemies")) foundCommands.add("describe_enemies");
                				
            					//TODO: Use this method once set up
//            					ArrayList<EnemyModel> enemies = DBController.getEnemiesByRoomId(db, DBController.getPlayerCurrentRoom(db));
            					ArrayList<EnemyModel> enemies = db.GetEnemiesInRoom(db.GetPlayer().getCurrentRoomIndex());
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
            					
            					for (int i=0; i<enemies.size(); i++) {
            						if (enemies.get(i).getHealth() == 0) continue;
            						
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
            					if (!foundCommands.contains("describeGroup_room")) foundCommands.add("describeGroup_room");
            					if (!foundCommands.contains("describe_moves")) foundCommands.add("describe_moves");
            					
            					systemResponse = String.format("Describing moves...<br><br>Possible moves:");
            					
            					for (String direction : connections.get(DBController.getPlayerCurrentRoom(db)).getAllKeys()) {
            						String camelCaseDirection = direction.substring(0, 1).toUpperCase() + direction.substring(1).toLowerCase();
            						
            						systemResponse += String.format("<br> - %s &mdash;&mdash;&#62; %s", camelCaseDirection,
            								rooms.get(player.getCurrentRoomIndex()).getShortRoomDescription());
            									
            								
            					}
            				break;
            				
            				case "directions":
            					if (!foundCommands.contains("describeGroup_room")) foundCommands.add("describeGroup_room");
            					if (!foundCommands.contains("describe_directions")) foundCommands.add("describe_directions");
            					
            					systemResponse = String.format("Describing directions...<br><br>Possible directions:<br>");
            				
            					for (String direction : ConsoleInterpreter.MOVE_DIRECTIONS)
            						systemResponse += String.format(" - %s<br>",
            								direction.substring(0, 1).toUpperCase() + direction.substring(1).toLowerCase());
            				break;
            				
            				case "inventory":
            					if (!foundCommands.contains("describeGroup_items")) foundCommands.add("describeGroup_items");
            					if (!foundCommands.contains("describe_inventory")) foundCommands.add("describe_inventory");
                				
//            					HashMap<Item, Integer> playerItems = db.getPlayerInventory();
            					HashMap<Item, Integer> playerItems = player.getInventory().GetItems();
            					HashMap<String, Weapon> playerEquips = player.getInventory().GetWeaponsAsSlots();
            					systemResponse = String.format("Describing inventory...<br><br>");

            					
            					// no items in inventory
            					if (playerItems.size() == 0 && playerEquips.size() == 0) {
            						systemResponse += String.format("There are no items in your inventory.");
            						break;
            					}
            					
            					systemResponse += String.format("Items in your inventory:");
            					
            					for (String slot : playerEquips.keySet()) 
            						systemResponse += String.format("<br><br>%s: %s<br> - %s",
            								slot, playerEquips.get(slot).GetName(),
            								playerEquips.get(slot).GetDescription());
            					
            					for (Item playerItem : playerItems.keySet())
            						systemResponse += String.format("<br><br>%s: %d<br> - %s",
            								playerItem.GetName(), playerItems.get(playerItem),
            								playerItem.GetDescription());
            				break;
            				
            				case "items":
            					if (!foundCommands.contains("describeGroup_items")) foundCommands.add("describeGroup_items");
            					if (!foundCommands.contains("describe_items")) foundCommands.add("describe_items");
                				
            					HashMap<Item, Integer> roomItems = rooms.getItems( player.getCurrentRoomIndex() );
            					systemResponse = String.format("Describing items...<br><br>");
            					
            					// no enemies in room
            					if (roomItems.size() == 0) {
            						systemResponse += String.format("There are no items in this room.");
            						break;
            					}
            					
            					systemResponse += String.format("Items in this room:");
            					
            					for (Item roomItem : roomItems.keySet())
            						systemResponse += String.format("<br><br>%s: %d<br> - %s",
            								roomItem.GetName(), roomItems.get(roomItem),
            								roomItem.GetDescription());
            				break;
            				
            				default:
            					systemResponse = String.format("Cannot describe %s.",
            							params.get(0));
            				break;
            			}
            			
            		break;
            		
            		// TYPE 3 COMMANDS
            		case "attack":
            			if (!foundCommands.contains("attack")) foundCommands.add("attack");
            			
            			weaponSlots = player.getInventory().GetWeaponsAsSlots();
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
            			ArrayList<EnemyModel> roomEnemies = rooms.getRoom(player.getCurrentRoomIndex()).getAllEnemies();
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
            			
            			
            			FightController fightController = new FightController(fighters);
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
            				new EntityController(fightController.GetFighter(attackIndex)).Die(db, rooms);
            				
            				systemResponse += String.format("%s has died.<br>",
            						attackedName);
            			}
            			
            			for (int i=0; i<roomEnemies.size(); i++) {
            				if (i != 0) systemResponse += "<br>";
            				
            				if (DBController.getPlayerHealth(db) == 0) {
            					player.Die(db, rooms);
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
//    	db.addToHistory(toAdd);
    }
 
    
    public String repeatString(String toRepeat, int amount) {
    	String toOut = "";
    	for (int i = 0; i < amount; i++) {
    		toOut += toRepeat;
    	}
    	return toOut;
    }
}