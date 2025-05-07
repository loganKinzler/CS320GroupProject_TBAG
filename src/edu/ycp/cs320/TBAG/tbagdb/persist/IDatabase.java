package edu.ycp.cs320.TBAG.tbagdb.persist;

import java.util.ArrayList;
import java.util.List;

import edu.ycp.cs320.TBAG.model.EnemyModel;
import edu.ycp.cs320.TBAG.model.EntityInventory;
import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.PlayerModel;
import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.Inventory;
import edu.ycp.cs320.TBAG.model.EntityInventory;
import edu.ycp.cs320.TBAG.model.RoomInventory;

public interface IDatabase {
	public abstract List<Room> DirectionsByRoomIdQuery(int id);
	public abstract Item ItemsByNameQuery(String itemName);
	public abstract Integer GetItemIDQuery(String itemName, String itemDescrition);

	public abstract List<Room> getRooms();
	public abstract List<Room> getConnections();
	public abstract Integer UpdateEnteredRoom(boolean entered, int id);
	public abstract Integer UpdateLockedRoom(int id);
	public abstract List<Room> RoomsByIdQuery(int id);
	
	public abstract Inventory InventoryBySourceID(Integer sourceID);
	public abstract EntityInventory GetPlayerInventory();
	public abstract EntityInventory GetEnemyInventoryByID(Integer enemyID);
	public abstract RoomInventory GetRoomInventoryByID(Integer roomID);

	public abstract void UpdateInventoryBySourceID(Integer sourceID, Inventory updateInventory);
	public abstract void UpdatePlayerInventory(EntityInventory playerInventory);
	public abstract void UpdateEnemyInventory(Integer enemyID, EntityInventory enemyInventory);
	public abstract void UpdateRoomInventory(Integer roomID, RoomInventory roomInventory);

	public abstract PlayerModel GetPlayer();
	public abstract Double UpdatePlayerHealth(double health);
	public abstract Integer UpdatePlayerRoom(int room);
	public abstract Double UpdatePlayerMaxHealth(double maxHealth);
	public abstract Integer UpdatePlayerLives(int lives);
	public abstract ArrayList<EnemyModel> GetEnemiesInRoom(int roomIndex);
	
	public abstract Double UpdateEnemyHealthById(int id, double health);
	public abstract Double UpdateEnemyMaxHealthById(int id, double maxHealth);
	public abstract Integer UpdateEnemyLivesById(int id, int lives);
	public abstract Integer UpdateEnemyRoomById(int id, int roomId);
	public abstract String UpdateEnemyNameById(int id, String name);
	public abstract String UpdateEnemyDescriptionById(int id, String description);
	public abstract EnemyModel getEnemyById(int id);
	
	public void deleteDb(String dbName, String dblocation);
	public String addToGameHistory(String add);
	List<String> getGameHistory();
	
	public String addToFound(String add);
	List<String> getFoundCommands();
	public Boolean checkFound(String check);
	public abstract Room getConnectionsByRoomId(int roomId);
}
