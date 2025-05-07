package edu.ycp.cs320.group_project.servlet;

import java.util.List;

import edu.ycp.cs320.TBAG.tbagdb.persist.IDatabase;

public class LogsController {
	public static void addToGameHistory(IDatabase db, List<String> gameHistory, String toAdd) {
    	gameHistory.add(toAdd);
    	db.addToGameHistory(toAdd);
    }
    
    public static void addToFoundCommands(IDatabase db, List<String> foundCommands, String toAdd) {
    	foundCommands.add(toAdd);
    	db.addToFound(toAdd);
    }
}
