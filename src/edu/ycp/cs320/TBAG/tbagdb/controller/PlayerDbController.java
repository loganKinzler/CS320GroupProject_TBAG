package edu.ycp.cs320.TBAG.tbagdb.controller;

import edu.ycp.cs320.TBAG.model.EntityInventory;
import edu.ycp.cs320.TBAG.model.PlayerModel;
import edu.ycp.cs320.TBAG.tbagdb.persist.IDatabase;

public class PlayerDbController {
	public int getPlayerLives(IDatabase db) {
		PlayerModel player = db.GetPlayer();
		return player.getLives();
	}
	public double getHealth(IDatabase db) {
		PlayerModel player = db.GetPlayer();
		return player.getHealth();
	}
	public double getMaxHealth(IDatabase db) {
		PlayerModel player = db.GetPlayer();
		return player.getMaxHealth();
	}
	public double getCurrentRoom(IDatabase db) {
		PlayerModel player = db.GetPlayer();
		return player.getCurrentRoomIndex();
	}
	public EntityInventory getPlayerInventory(IDatabase db) {
		EntityInventory inv = db.getPlayerInventory();
		return inv;
	}
	
	public PlayerModel getPlayer(IDatabase db) {
		return db.GetPlayer();
	}
	
	public void updatePlayerHealth(IDatabase db, PlayerModel player) {
		db.UpdatePlayerHealth(player);
	}
	
	public void updatePlayerMaxHealth(IDatabase db, PlayerModel player) {
		db.UpdatePlayerMaxHealth(player);
	}
	public void updatePlayerLives(IDatabase db, PlayerModel player) {
		db.UpdatePlayerLives(player);
	}
	public void updatePlayerRoom(IDatabase db, PlayerModel player) {
		db.UpdatePlayerRoom(player);
	}
}
