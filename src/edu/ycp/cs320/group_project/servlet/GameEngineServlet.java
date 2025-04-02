package edu.ycp.cs320.group_project.servlet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ycp.cs320.TBAG.controller.*;
// our imports
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

    //Use this section for when we end up having a database for storing the information
    /*@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userInput = req.getParameter("userInput");
        if (userInput != null && !userInput.trim().isEmpty()) {
            // Simulate a system response
            String systemResponse = "System: You entered '" + userInput + "'";

            // Save to the database
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/game_db", "username", "password")) {
                String sql = "INSERT INTO game_history (user_input, system_response) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, userInput);
                    stmt.setString(2, systemResponse);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Forward to the JSP file
        req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
    }
    */
    
    
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get or create the session
        HttpSession session = req.getSession(true);
        
    	PlayerController player = new PlayerController(new PlayerModel(100.0, 3, 0));
        ConsoleInterpreter interpreter = new ConsoleInterpreter();
        
        // Retrieve the game history from the session (or create a new one if it doesn't exist)
        List<String> gameHistory = (List<String>) session.getAttribute("gameHistory");
        if (gameHistory == null) {
            gameHistory = new ArrayList<>();
            session.setAttribute("gameHistory", gameHistory);
        }
        
        //Check if player current room is in session history, set if yes, initialize if not
        if (session.getAttribute("playerCurrentRoom") == null) {
        	session.setAttribute("playerCurrentRoom", player.getCurrentRoomIndex());
        }
        else {
        	int roomIndex = ((Integer) session.getAttribute("playerCurrentRoom")).intValue();
        	player.setCurrentRoomIndex(roomIndex);
        }
        
        // get found commands
        List<String> foundCommands = (List<String>) session.getAttribute("foundCommands");
        if (foundCommands == null) {
        	foundCommands = new ArrayList<>();
        	session.setAttribute("foundCommands", foundCommands);
        }

        RoomContainer rooms = new RoomContainer();
        if (session.getAttribute("rooms") == null) {
        	rooms = new RoomContainer();
        	rooms.createHardcodedRooms();
        }
        else {
        	//Do something to pull rooms info (it cant be cleanly pulled... may be better to just use database
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
        		systemResponse = "[GLITCH] ░D░A░T░A░ ░C░O░R░R░U░P░T░E░D░ ░H░E░L░P░";
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
            		
            		case "pickup":
            			if (!foundCommands.contains("pickup")) foundCommands.add("pickup");
            			//insert code to have player pick up item
            			//consider a map of all items in the game that can be grabbed using string input from the user input
            			//then use player pickup() method
            			
            			systemResponse = String.format("Picked up %s...", params.get(0));
            		break;
            		
            		case "describe":
            			if (!foundCommands.contains("describe")) foundCommands.add("describe");
            			
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
            					
            					systemResponse = String.format("Describing directions...<br><br>Possible moves:<br>");
            				
            					for (String direction : ConsoleInterpreter.MOVE_DIRECTIONS)
            						systemResponse += String.format(" - %s<br>",
            								direction.substring(0, 1).toUpperCase() + direction.substring(1));
            				break;
            				
            				default:
            					systemResponse = String.format("Cannot describe %s",
            							params.get(0));
            				break;
            			}
            			
            		break;
            		
            		// TYPE 2 COMMANDS
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

            session.setAttribute("playerCurrentRoom", player.getCurrentRoomIndex());
            gameHistory.add(systemResponse);

            if (sudoStage == 0) {
            	gameHistory.add("<br>");
                gameHistory.add("~-==============================-~");// end of turn line break
                gameHistory.add("<br>");
            }
        }

        // Set the game history as a request attribute for the JSP
        req.setAttribute("gameHistory", gameHistory);
        req.setAttribute("foundCommands", foundCommands);
        req.setAttribute("sudoStage", sudoStage);

        // Forward to the JSP file
//        req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
        resp.sendRedirect("game");
    }
}