package edu.ycp.cs320.TBAG.tbagdb;

import java.util.ArrayList;

import edu.ycp.cs320.TBAG.model.EnemyModel;
import edu.ycp.cs320.TBAG.model.EntityInventory;
import edu.ycp.cs320.TBAG.model.PlayerModel;
import edu.ycp.cs320.TBAG.tbagdb.persist.IDatabase;

public class DBController {
	
	//Player methods
	public static int getPlayerLives(IDatabase db) {
		PlayerModel player = db.GetPlayer();
		return player.getLives();
	}
	public static double getHealth(IDatabase db) {
		PlayerModel player = db.GetPlayer();
		return player.getHealth();
	}
	public static double getMaxHealth(IDatabase db) {
		PlayerModel player = db.GetPlayer();
		return player.getMaxHealth();
	}
	public static double getCurrentRoom(IDatabase db) {
		PlayerModel player = db.GetPlayer();
		return player.getCurrentRoomIndex();
	}
	public static EntityInventory getPlayerInventory(IDatabase db) {
		EntityInventory inv = db.getPlayerInventory();
		return inv;
	}
	
	public static PlayerModel getPlayer(IDatabase db) {
		return db.GetPlayer();
	}
	
	public static void updatePlayerHealth(IDatabase db, double health) {
		db.UpdatePlayerHealth(health);
	}
	
	public static void updatePlayerMaxHealth(IDatabase db, double maxHealth) {
		db.UpdatePlayerMaxHealth(maxHealth);
	}
	public static void updatePlayerLives(IDatabase db, int lives) {
		db.UpdatePlayerLives(lives);
	}
	public static void updatePlayerRoom(IDatabase db, int room) {
		db.UpdatePlayerRoom(room);
	}
	
	//Enemy methods
	public static ArrayList<EnemyModel> getEnemiesByRoomId(IDatabase db, int roomId) {
		return db.GetEnemiesInRoom(roomId);
	}
	
	public static double getEnemyHealthById(IDatabase db, int id) {
		return db.getEnemyById(id).getHealth();
	}
	public static double getEnemyMaxHealthById(IDatabase db, int id) {
		return db.getEnemyById(id).getMaxHealth();
	}
	public static int getEnemyLivesById(IDatabase db, int id) {
		return db.getEnemyById(id).getLives();
	}
	public static int getEnemyRoomById(IDatabase db, int id) {
		return db.getEnemyById(id).getCurrentRoomIndex();
	}
	public static String getEnemyNameById(IDatabase db, int id) {
		return db.getEnemyById(id).getName();
	}
	public static String getEnemyDescriptionById(IDatabase db, int id) {
		return db.getEnemyById(id).getDescription();
	}
	
	public static void setEnemyHealthById(IDatabase db, int id, double health) {
		db.UpdateEnemyHealthById(id, health);
	}
	public static void setEnemyMaxHealthById(IDatabase db, int id, double maxHealth) {
		db.UpdateEnemyMaxHealthById(id, maxHealth);
	}
	public static void setEnemyLivesById(IDatabase db, int id, int lives) {
		db.UpdateEnemyLivesById(id, lives);
	}
	public static void setEnemyRoomById(IDatabase db, int id, int room) {
		db.UpdateEnemyRoomById(id, room);
	}
	public static void setEnemyNameById(IDatabase db, int id, String name) {
		db.UpdateEnemyNameById(id, name);
	}
	public static void setEnemyDescriptionById(IDatabase db, int id, String description) {
		db.UpdateEnemyDescriptionById(id, description);
	}
}
