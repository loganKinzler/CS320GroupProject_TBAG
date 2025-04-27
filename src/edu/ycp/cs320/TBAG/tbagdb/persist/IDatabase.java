package edu.ycp.cs320.TBAG.tbagdb.persist;

import java.util.ArrayList;
import java.util.List;

import edu.ycp.cs320.TBAG.model.EnemyModel;
import edu.ycp.cs320.TBAG.model.EntityInventory;
import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.PlayerModel;
import edu.ycp.cs320.TBAG.model.Inventory;
import edu.ycp.cs320.TBAG.model.EntityInventory;
import edu.ycp.cs320.TBAG.model.RoomInventory;

public interface IDatabase {
	public abstract Item ItemsByNameQuery(String itemName);
	public abstract Integer GetItemIDQuery(String itemName, String itemDescrition);
	
	public abstract Inventory InventoryBySourceID(Integer sourceID);
	public abstract EntityInventory GetPlayerInventory();
	public abstract EntityInventory GetEnemyInvetoryByID(Integer enemyID);
	public abstract RoomInventory GetRoomInventoryByID(Integer roomID);

	public abstract void UpdateInventoryBySourceID(Integer sourceID, Inventory updateInventory);
	public abstract void UpdatePlayerInventory(EntityInventory playerInventory);
	public abstract void UpdateEnemyInventory(Integer enemyID, EntityInventory enemyInventory);
	public abstract void UpdateRoomInventory(Integer roomID, RoomInventory roomInventory);
	
	public abstract PlayerModel GetPlayer();	
	public abstract PlayerModel UpdatePlayerHealth(PlayerModel player);
	public abstract PlayerModel UpdatePlayerRoom(PlayerModel player);
	public abstract PlayerModel UpdatePlayerMaxHealth(PlayerModel player);
	public abstract PlayerModel UpdatePlayerLives(PlayerModel player);
	
	public abstract ArrayList<EnemyModel> GetEnemiesInRoom(int roomIndex);
}
