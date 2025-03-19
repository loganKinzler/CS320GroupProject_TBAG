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

import edu.ycp.cs320.TBAG.model.Action;
import edu.ycp.cs320.TBAG.controller.ConsoleInterpreter;
import edu.ycp.cs320.TBAG.controller.RoomContainer;

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

        
        // hard code the rooms
        
        
        // Process user input
        String userInput = req.getParameter("userInput");
        gameHistory.add( userInput );// add user input to console (for user's reference)
        
        
        if (userInput != null && !userInput.trim().isEmpty()) {
            // Add user input to the game history

            String systemResponse;
            
            Action userAction = interpreter.ValidateInput(userInput);
            systemResponse = userAction.GetErrorMessage();// if the userAction isn't valid, it stays as the error msg
            
            // action details here (strings for now, need more structure for true game)
            if (userAction.IsValid()) {
            	switch (userAction.GetMethod()) {
            	
            		// TYPE 1 COMMANDS:
            		case "move":
            			systemResponse = String.format("Moving to %s...", userAction.GetParams().get(0));
            		break;
            		
            		case "use":
            			systemResponse = String.format("Used %s...", userAction.GetParams().get(0));
            		break;
            		
            		case "pickup":
            			systemResponse = String.format("Picked up %s...", userAction.GetParams().get(0));
            		break;
            		
            		case "describe":
            			systemResponse = String.format("Describing %s...", userAction.GetParams().get(0));
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
            
            gameHistory.add(systemResponse);
            gameHistory.add("~-===================-~");// end of turn line break
        }

        // Set the game history as a request attribute for the JSP
        req.setAttribute("gameHistory", gameHistory);

        // Forward to the JSP file
        req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
    }
}