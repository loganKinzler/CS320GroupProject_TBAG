package edu.ycp.cs320.TBAG.tbagdb.persist;

import java.util.ArrayList;
import java.util.List;

import edu.ycp.cs320.TBAG.model.EnemyModel;
import edu.ycp.cs320.TBAG.model.EntityInventory;
import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.PlayerModel;
import edu.ycp.cs320.TBAG.model.Room;

public interface IDatabase {
	public abstract List<Item> ItemsByNameQuery(String itemName);
	public abstract List<Room> RoomsByIdQuery(int id);
	public abstract List<Room> DirectionsByRoomIdQuery(int id);
	
	public abstract PlayerModel GetPlayer();
	public abstract EntityInventory getPlayerInventory();
	public abstract List<Room> getRooms();
	public abstract List<Room> getConnections();
	
	public abstract Double UpdatePlayerHealth(double health);
	public abstract Integer UpdatePlayerRoom(int room);
	public abstract Double UpdatePlayerMaxHealth(double maxHealth);
	public abstract Integer UpdatePlayerLives(int lives);
	
	
	public abstract ArrayList<EnemyModel> GetEnemiesInRoom(int roomIndex);
	
	public Double UpdateEnemyHealthById(int id, double health);
	public Double UpdateEnemyMaxHealthById(int id, double maxHealth);
	public Integer UpdateEnemyLivesById(int id, int lives);
	public Integer UpdateEnemyRoomById(int id, int roomId);
	public String UpdateEnemyNameById(int id, String name);
	public String UpdateEnemyDescriptionById(int id, String description);
	public EnemyModel getEnemyById(int id);
	
	public List<String> loadHistory();
	public void deleteDb(String dbName, String dblocation);
	public void addToHistory(String add);
}
