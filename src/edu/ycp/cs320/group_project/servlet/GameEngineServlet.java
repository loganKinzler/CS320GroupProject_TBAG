package edu.ycp.cs320.group_project.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

        // Retrieve the game history from the session (or create a new one if it doesn't exist)
        List<String> gameHistory = (List<String>) session.getAttribute("gameHistory");
        if (gameHistory == null) {
            gameHistory = new ArrayList<>();
            session.setAttribute("gameHistory", gameHistory);
        }

        // Process user input
        String userInput = req.getParameter("userInput");
        if (userInput != null && !userInput.trim().isEmpty()) {
            // Add user input to the game history
            gameHistory.add("User: " + userInput);

            // Simulate a system response      
            /*
             * 
             * When get an actual response put it equal to the string and it be happy
             * 
             */
            String systemResponse = "System: You entered '" + userInput + "'";
            gameHistory.add(systemResponse);
        }

        // Set the game history as a request attribute for the JSP
        req.setAttribute("gameHistory", gameHistory);

        // Forward to the JSP file
        req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
    }
}