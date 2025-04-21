package edu.ycp.cs320.TBAG.tbagdb.persist;

import java.util.ArrayList;
import java.util.List;

import edu.ycp.cs320.TBAG.model.EnemyModel;
import edu.ycp.cs320.TBAG.model.EntityInventory;
import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.PlayerModel;

public interface IDatabase {
	public abstract List<Item> ItemsByNameQuery(String itemName);
	
	public abstract PlayerModel GetPlayer();
	public abstract EntityInventory getPlayerInventory();
	
	public abstract PlayerModel UpdatePlayerHealth(PlayerModel player);
	public abstract PlayerModel UpdatePlayerRoom(PlayerModel player);
	public abstract PlayerModel UpdatePlayerMaxHealth(PlayerModel player);
	public abstract PlayerModel UpdatePlayerLives(PlayerModel player);
	
	
	public abstract ArrayList<EnemyModel> GetEnemiesInRoom(int roomIndex);
}
