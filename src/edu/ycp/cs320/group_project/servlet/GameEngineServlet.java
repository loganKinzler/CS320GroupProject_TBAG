package edu.ycp.cs320.group_project.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// our imports
import edu.ycp.cs320.TBAG.controller.*;
import edu.ycp.cs320.TBAG.model.*;

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
        
        ConsoleInterpreter interpreter = new ConsoleInterpreter();
        
        // Retrieve the game history from the session (or create a new one if it doesn't exist)
        List<String> gameHistory = (List<String>) session.getAttribute("gameHistory");
        if (gameHistory == null) {
            gameHistory = new ArrayList<>();
            session.setAttribute("gameHistory", gameHistory);
        }
        
        //Check if player current room is in session history, set if yes, initialize if not
        PlayerModel playerModel = (PlayerModel)session.getAttribute("player");
        if (playerModel == null) {
        	playerModel = new PlayerModel(50, 3, 0);
        }
        
        PlayerController player = new PlayerController(playerModel);
        
        
        // get found commands
        List<String> foundCommands = (List<String>) session.getAttribute("foundCommands");
        if (foundCommands == null) {
        	foundCommands = new ArrayList<>();
        	session.setAttribute("foundCommands", foundCommands);
        }

        RoomContainer rooms = (RoomContainer)session.getAttribute("rooms");
        if (rooms == null) {
        	rooms = new RoomContainer();
        	rooms.createHardcodedRooms();
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
        		systemResponse = "[INFO] Game integrity: 0%<br>";
        		break;
        	case 10:
        		systemResponse = "<br>Segmentation fault (core melted)<br>";
        		break;
        	case 11:
        		systemResponse = "[GLITCH] ░D░A░T░A░ ░C░O░R░R░U░P░T░E░D░. . . ░H░E░L░P░";
        		break;
        	default:
        		System.exit(0); //Closes the program (crashing breaks the illusion cause it throws an error. The only way to make this work is to close the program entirely)
        		break;
        	}
        	gameHistory.add(systemResponse);
        }
        
        if (userInput != null && !userInput.trim().isEmpty() && sudoStage == 0) {
            // Add user input to the game history

        	gameHistory.add("C:&bsol;Users&bsol;exampleUser&gt; " + ((userInput == null)? "": userInput));// add user input to console (for user's reference)
            
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
            	
            		// TYPE 1 COMMANDS:
            		case "move":
            			if (!foundCommands.contains("move")) foundCommands.add("move");
            			
            			Integer nextRoom = rooms.nextConnection(player.getCurrentRoomIndex(),
            					params.get(0));
            			
            			if (nextRoom == null) {
            				systemResponse = String.format("The current room doesn't have a room %s of it.",
            						params.get(0));
            				break;
            			}
            			
            			player.setCurrentRoomIndex(nextRoom);
            			
            			systemResponse = String.format("Moving %s...<br><br>Entered %s.<br>%s",
            					params.get(0),
            					rooms.getShortRoomDescription(nextRoom),
            					rooms.getLongRoomDescription(nextRoom));
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
            		
            		
            		// DESCRIBE COMMANDS
            		case "describe":
            			switch (params.get(0)) {
            				case "room":
            					if (!foundCommands.contains("describe_room")) foundCommands.add("describe_room");
            					
            					systemResponse = String.format("Describing room...<br><br>%s<br>%s",
                    					rooms.getShortRoomDescription( player.getCurrentRoomIndex() ),
                    					rooms.getLongRoomDescription( player.getCurrentRoomIndex() ));
            				break;
            				
            				case "moves":
            					if (!foundCommands.contains("describe_moves")) foundCommands.add("describe_moves");
            					
            					systemResponse = String.format("Describing moves...<br><br>Possible moves:<br>");
            					
            					for (String direction : rooms.getAllKeys( player.getCurrentRoomIndex() )) {
            						String camelCaseDirection = direction.substring(0, 1).toUpperCase() + direction.substring(1);
            						
            						systemResponse += String.format(" - %s &mdash;&mdash;&#62; %s<br>", camelCaseDirection,
            								rooms.getShortRoomDescription(
            									rooms.nextConnection(player.getCurrentRoomIndex(), direction)
            								));
            					}
            				break;
            				
            				case "directions":
            					if (!foundCommands.contains("describe_directions")) foundCommands.add("describe_directions");
            					
            					systemResponse = String.format("Describing directions...<br><br>Possible directions:<br>");
            				
            					for (String direction : ConsoleInterpreter.MOVE_DIRECTIONS)
            						systemResponse += String.format(" - %s<br>",
            								direction.substring(0, 1).toUpperCase() + direction.substring(1));
            				break;
            				
            				case "enemies":
            					if (!foundCommands.contains("describe_enemies")) foundCommands.add("describe_enemies");
                				
            					ArrayList<EnemyModel> enemies = rooms.getEnemiesinRoom( player.getCurrentRoomIndex() );
            					systemResponse = String.format("Describing enemies...<br><br>");
            					
            					// no enemies in room
            					if (enemies.size() == 0) {
            						systemResponse += String.format("There are no enemies in this room.");
            						break;
            					}
            					
            					systemResponse += String.format("Enemies in this room:");
            					
            					for (Integer i=0; i<enemies.size(); i++)
            						systemResponse += String.format("<br>&num;%d: %s | Health: %.1f / %.1f<br> - %s<br>",
            								i+1, enemies.get(i).getName(),
            								enemies.get(i).getHealth(), enemies.get(i).getMaxHealth(),
            								enemies.get(i).getDescription());
            				break;
            				
            				case "inventory":
            					if (!foundCommands.contains("describe_inventory")) foundCommands.add("describe_inventory");
                				
            					HashMap<Item, Integer> playerItems = player.getInventory().GetItems();
            					systemResponse = String.format("Describing inventory...<br><br>");
            					
            					// no enemies in room
            					if (playerItems.size()== 0) {
            						systemResponse += String.format("There are no items in your inventory.");
            						break;
            					}
            					
            					systemResponse += String.format("Items in your inventory:<br>");
            					
            					for (Item playerItem : playerItems.keySet())
            						systemResponse += String.format(" - %s %s<br>",
            								playerItems.get(playerItem), playerItem.GetName());
            				break;
            				
            				case "items":
            					if (!foundCommands.contains("describe_items")) foundCommands.add("describe_items");
                				
            					HashMap<Item, Integer> roomItems = rooms.getItems( player.getCurrentRoomIndex() );
            					systemResponse = String.format("Describing items...<br><br>");
            					
            					// no enemies in room
            					if (roomItems.size()== 0) {
            						systemResponse += String.format("There are no items in this room.");
            						break;
            					}
            					
            					systemResponse += String.format("Items in this room:<br>");
            					
            					for (Item roomItem : roomItems.keySet())
            						systemResponse += String.format(" - %s %s<br>",
            								roomItems.get(roomItem), roomItem.GetName());
            				break;
            				
            				default:
            					systemResponse = String.format("Cannot describe %s",
            							params.get(0));
            				break;
            			}
            			
            		break;
            		
            		// TYPE 3 COMMANDS
            		case "attack":
            			if (!foundCommands.contains("attack")) foundCommands.add("attack");
            			
            			//  using %s
            			// , userAction.GetParams().get(2)
            			systemResponse = String.format("Attacked %s with %s.", userAction.GetParams().get(0),
            					userAction.GetParams().get(1));
            		break;
            		
            		default:
            			systemResponse = String.format("User inputted valid command of type: %s", userAction.GetMethod());
            		break;
            	}
            }

            gameHistory.add(systemResponse);

            if (sudoStage == 0) {
            	gameHistory.add("<br>");
                gameHistory.add("~-==============================-~");// end of turn line break
                gameHistory.add("<br>");
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
}