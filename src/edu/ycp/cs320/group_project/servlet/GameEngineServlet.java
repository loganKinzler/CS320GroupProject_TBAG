package edu.ycp.cs320.group_project.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ycp.cs320.TBAG.controller.ASCIIOutput;
// our imports
import edu.ycp.cs320.TBAG.controller.ConsoleInterpreter;
import edu.ycp.cs320.TBAG.controller.PlayerController;
import edu.ycp.cs320.TBAG.model.Action;
import edu.ycp.cs320.TBAG.model.EntityInventory;
import edu.ycp.cs320.TBAG.model.Inventory;
import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.PlayerModel;
import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.RoomInventory;
import edu.ycp.cs320.TBAG.model.Weapon;
import edu.ycp.cs320.TBAG.tbagdb.persist.DerbyDatabase;

public class GameEngineServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	HttpSession session = req.getSession();
        String userInput = (String) session.getAttribute("userInput");

        if (userInput != null) {
            req.setAttribute("userInput", userInput);
            session.removeAttribute("userInput");
        }
        
        DerbyDatabase db = (DerbyDatabase) session.getAttribute("db");
        if (db == null) db = new DerbyDatabase("test");
        session.setAttribute("db", db);
        
		List<String> foundCommands = (List<String>) session.getAttribute("foundCommands");
        List<String> gameHistory = (List<String>) session.getAttribute("gameHistory");

        if (gameHistory == null) {
            
            
            gameHistory = db.getGameHistory();
            foundCommands = db.getFoundCommands();
            session.setAttribute("gameHistory", gameHistory);
            session.setAttribute("foundCommands", foundCommands);
        }
        
        gameHistory = db.getGameHistory();
        foundCommands = db.getFoundCommands();

        req.setAttribute("gameHistory", gameHistory);
        req.setAttribute("foundCommands", foundCommands);   
        SessionInfoController.sessionPlaySound(req, "playHakeSound");
        
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
      
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get or create the session
        HttpSession session = req.getSession(true);
        
        session.setAttribute("playHakeSound", false); //dont ask
        
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
        Integer nextRoom = null;
        //Room dummy = new Room();
       // rooms.add(dummy);
        List<Room> connections = new ArrayList<>();
        //connections.add(dummy);
//        rooms = (List<Room>)session.getAttribute("rooms");
        rooms = db.getRooms();
        connections = db.getConnections();
        
        for(int i = 0; i<rooms.size(); i++) {
        	rooms.get(i).setConnections(connections.get(i).getHashMap());
        	
        	//This will check what room the player is starting in
        	if(rooms.get(i).getRoomId() == player.getCurrentRoomIndex()) {
        		rooms.get(i).setHas_Entered_Room(true);
        		db.UpdateEnteredRoom(true, i);
        	}
        	
        	
        	
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
        	systemResponse = CommandsController.sudoEasterEgg(sudoStage);
        	LogsController.addToGameHistory(db, gameHistory, systemResponse);
        }
        
        if (userInput != null && !userInput.trim().isEmpty() && sudoStage == 0) {
            // Add user input to the game history

        	LogsController.addToGameHistory(db, gameHistory, "C:&bsol;Users&bsol;exampleUser&gt; " + ((userInput == null)? "": userInput));// add user input to console (for user's reference)
            
            Action userAction = interpreter.ValidateInput(userInput);
            systemResponse = userAction.GetErrorMessage();// if the userAction isn't valid, it stays as the error msg
            
            Map<String, Weapon> weaponSlots = player.getInventory().GetWeaponsAsSlots();
            
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
	        			return;
        			
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
	        			LogsController.addToGameHistory(db, gameHistory, "Chat logs cleared...");
	        		break;
	        		case "showMap":
	        			systemResponse = MapController.modularMakeMap(db);
	        			if (!foundCommands.contains("showMap")) LogsController.addToFoundCommands(db,foundCommands,"showMap");
	        		break;
	        		case "mirrorEasterEgg":
	        			systemResponse = CommandsController.mirrorEasterEgg(this, session, db, player, systemResponse);
	        		break;
            	
            		// TYPE 1 COMMANDS:
            		case "move":
            			systemResponse = CommandsController.moveCommand(db, foundCommands, params, rooms, player, systemResponse);
            		break;
            		
            		//TYPE 6 COMMANDS
            		case "use":

            			systemResponse = CommandsController.useCommand(db, foundCommands, params, rooms, systemResponse, player);

            		break;
            		
            		// TYPE 3 COMMANDS
            		case "pickup":

            			systemResponse = CommandsController.pickupCommand(this, db, rooms, foundCommands, params, gameHistory, player, systemResponse);
            		break;
            		
            		case "drop":
            			systemResponse = CommandsController.dropCommand(db, foundCommands, params, connections, player, systemResponse);
            		break;
            		
            		case "equip":
            			systemResponse = CommandsController.equipCommand(db, foundCommands, params, player, systemResponse);
            		break;
            		
            		case "unequip":
            			weaponSlots = player.getInventory().GetWeaponsAsSlots();
            			systemResponse = CommandsController.unequipCommand(db, player, foundCommands, params, weaponSlots, systemResponse);
            		break;
            		
            		// DESCRIBE COMMANDS
            		case "describe":
            			systemResponse = CommandsController.describeCommand(db, params, foundCommands, player, rooms, systemResponse);
            		break;
            		
            		// TYPE 3 COMMANDS
            		case "attack":
            			systemResponse = CommandsController.attackCommand(db, userAction, foundCommands, params, weaponSlots, rooms, player, systemResponse);
            		break;
            		
            		default:
            			systemResponse = String.format("User inputted valid command of type: %s<br>", userAction.GetMethod());
            		break;
            	}
            }

            LogsController.addToGameHistory(db, gameHistory, systemResponse);
            
            if (sudoStage == 0) {
            	LogsController.addToGameHistory(db, gameHistory, "<br>");
            	LogsController.addToGameHistory(db, gameHistory, "~-==============================-~");
            	LogsController.addToGameHistory(db, gameHistory, "<br>");
            }
        }

        // Set the game history as a request attribute for the JSP
        session.setAttribute("player", playerModel);
        session.setAttribute("rooms", rooms);
        req.setAttribute("gameHistory", gameHistory);
        req.setAttribute("foundCommands", foundCommands);
        req.setAttribute("sudoStage", sudoStage);

        // Forward to the JSP file
        resp.sendRedirect("game");
    }
    
    public String repeatString(String toRepeat, int amount) {
    	String toOut = "";
    	for (int i = 0; i < amount; i++) {
    		toOut += toRepeat;
    	}
    	return toOut;
    }
}