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
}
