package edu.ycp.cs320.TBAG.tbagdb.controller;

import java.util.ArrayList;

import edu.ycp.cs320.TBAG.model.EnemyModel;
import edu.ycp.cs320.TBAG.tbagdb.persist.IDatabase;

public class EnemyDbController {
	public ArrayList<Double> getEnemyHealthByRoomId(IDatabase db, int room) {
		ArrayList<EnemyModel> enemies = db.GetEnemiesInRoom(room);
		
		ArrayList<Double> healths = new ArrayList<>();
		
		for (EnemyModel enemy : enemies) {
			healths.add(enemy.getHealth());
		}
		
		return healths;
	}
	
	public ArrayList<EnemyModel> getEnemiesByRoomId(IDatabase db, int roomId) {
		return db.GetEnemiesInRoom(roomId);
	}
	
	public double getEnemyHealthById(IDatabase db, int id) {
		return db.getEnemyById(id).getHealth();
	}
	public double getEnemyMaxHealthById(IDatabase db, int id) {
		return db.getEnemyById(id).getMaxHealth();
	}
	public int getEnemyLivesById(IDatabase db, int id) {
		return db.getEnemyById(id).getLives();
	}
	public int getEnemyRoomById(IDatabase db, int id) {
		return db.getEnemyById(id).getCurrentRoomIndex();
	}
	public String getEnemyNameById(IDatabase db, int id) {
		return db.getEnemyById(id).getName();
	}
	public String getEnemyDescriptionById(IDatabase db, int id) {
		return db.getEnemyById(id).getDescription();
	}
	
	public void setEnemyHealthById(IDatabase db, int id, double health) {
		db.UpdateEnemyHealthById(id, health);
	}
	public void setEnemyMaxHealthById(IDatabase db, int id, double maxHealth) {
		db.UpdateEnemyMaxHealthById(id, maxHealth);
	}
	public void setEnemyLivesById(IDatabase db, int id, int lives) {
		db.UpdateEnemyLivesById(id, lives);
	}
	public void setEnemyRoomById(IDatabase db, int id, int room) {
		db.UpdateEnemyRoomById(id, room);
	}
	public void setEnemyNameById(IDatabase db, int id, String name) {
		db.UpdateEnemyNameById(id, name);
	}
	public void setEnemyDescriptionById(IDatabase db, int id, String description) {
		db.UpdateEnemyDescriptionById(id, description);
	}
}
