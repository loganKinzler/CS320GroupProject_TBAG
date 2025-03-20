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
        // Redirect GET requests to POST
        doPost(req, resp);
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

        
        // hard code the rooms
        RoomContainer rooms = createRooms();
        
        
        // Process user input
        String userInput = req.getParameter("userInput");
        gameHistory.add("C:&bsol;Users&bsol;exampleUser&gt; " + userInput);// add user input to console (for user's reference)

        
        if (userInput != null && !userInput.trim().isEmpty()) {
            // Add user input to the game history

            String systemResponse;
            
            Action userAction = interpreter.ValidateInput(userInput);
            systemResponse = userAction.GetErrorMessage();// if the userAction isn't valid, it stays as the error msg
            
            // action details here (strings for now, need more structure for true game)
            if (userAction.IsValid()) {
            	ArrayList<String> params = userAction.GetParams();
            	
            	switch (userAction.GetMethod()) {
            	
            		// TYPE 1 COMMANDS:
            		case "move":
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
            			systemResponse = String.format("Used %s...", params.get(0));
            		break;
            		
            		case "pickup":
            			systemResponse = String.format("Picked up %s...", params.get(0));
            		break;
            		
            		case "describe":
            			switch (params.get(0)) {
            				case "room":
            					systemResponse = String.format("Describing room...<br><br>%s<br>%s",
                    					rooms.getShortRoomDescription( player.getCurrentRoomIndex() ),
                    					rooms.getLongRoomDescription( player.getCurrentRoomIndex() ));
            				break;
            				
            				case "moves":
            					systemResponse = String.format("Describing moves...<br><br>Possible moves:<br>");
            					
            					for (String direction : rooms.getAllKeys( player.getCurrentRoomIndex() )) {
            						String camelCaseDirection = direction.substring(0, 1).toUpperCase() + direction.substring(1);
            						
            						systemResponse += String.format(" - %s &mdash;&mdash;&#62; %s<br>", camelCaseDirection,
            								rooms.getShortRoomDescription(
            									rooms.nextConnection(player.getCurrentRoomIndex(), direction)
            								));
            					}
            				break;
            				
            				case "commands":
            					systemResponse = ""
            							+ "Describing commands...<br><br>"
            							+ "Command Structure:<br>"
            							+ "[command]: [param1] [param2] ...<br><br>"
            							+ "Command List:<br>"
            							+ " - move: [direction]<br>"
            							+ " - describe: [room / moves / commands]";
            				break;
            				
            				default:
            					systemResponse = String.format("Cannot describe %s",
            							params.get(0));
            				break;
            			}
            			
            		break;
            		
            		// TYPE 2 COMMANDS
            		case "attack":
            			systemResponse = String.format("Attacked %s with %s using %s.", userAction.GetParams().get(0),
            					userAction.GetParams().get(1),userAction.GetParams().get(2));
            		break;
            		
            		default:
            			systemResponse = String.format("User inputted valid command of type: %s", userAction.GetMethod());
            		break;
            	}
            }

            //increment player's room index every valid input, then store it to session info
            session.setAttribute("playerCurrentRoom", player.getCurrentRoomIndex());
            gameHistory.add(systemResponse);

            gameHistory.add("<br>");
            gameHistory.add("~-===================-~");// end of turn line break
            gameHistory.add("<br>");

        }

        // Set the game history as a request attribute for the JSP
        req.setAttribute("gameHistory", gameHistory);

        // Forward to the JSP file
        req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
    }
}