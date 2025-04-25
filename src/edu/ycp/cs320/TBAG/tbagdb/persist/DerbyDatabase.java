package edu.ycp.cs320.TBAG.tbagdb.persist;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ycp.cs320.TBAG.comparator.ItemByIDComparator;
import edu.ycp.cs320.TBAG.controller.RoomContainer;
import edu.ycp.cs320.TBAG.model.EnemyModel;
import edu.ycp.cs320.TBAG.model.EntityInventory;
import edu.ycp.cs320.TBAG.model.EntityModel;
import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.PlayerModel;
import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.Weapon;
import edu.ycp.cs320.TBAG.model.Inventory;

public class DerbyDatabase implements IDatabase {
	static {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (Exception e) {
			throw new IllegalStateException("Could not load Derby driver");
		}
	}
	
	private interface Transaction<ResultType> {
		public ResultType execute(Connection conn) throws SQLException;
	}

	private static final int MAX_ATTEMPTS = 10;
	
	
	// OUR QUERIES / INSERTS GO HERE
	public List<Item> ItemsByNameQuery(String itemName) {
		return executeTransaction(new Transaction<List<Item>>() {
			@Override
			public List<Item> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					// retreive all attributes from both Books and Authors tables
					stmt = conn.prepareStatement(
							"select itemTypes.* " +
							" from itemTypes " + 
							" where itemTypes.name = ?"
					);
					stmt.setString(1, itemName);
					
					List<Item> result = new ArrayList<Item>();
					
					resultSet = stmt.executeQuery();
					
					// for testing that a result was returned
					Boolean found = false;
					Integer itemID = 0;
					
					while (resultSet.next()) {
						found = true;
						itemID++;
						
						// create new Author object
						// retrieve attributes from resultSet starting with index 1
						Item item = loadItem(resultSet, 2, itemID);
						result.add(item);
					}
					
					// check if the title was found
					if (!found) {
						System.out.println("<" + itemName + "> was not found in the items table");
					}
					
					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	
	public Integer GetItemIDQuery(String itemName, String itemDescription) {
		return executeTransaction(new Transaction<Integer>() {
			@Override
			public Integer execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					// retreive all attributes from both Books and Authors tables
					stmt = conn.prepareStatement(
							"select itemTypes.* " +
							" from itemTypes " + 
							" where itemTypes.name = ?" + 
							" and itemTypes.description = ?"
					);
					
					stmt.setString(1, itemName);
					stmt.setString(2, itemDescription);
					
					List<Item> result = new ArrayList<Item>();
					
					resultSet = stmt.executeQuery();
					
					// for testing that a result was returned
					Boolean found = false;
					
					if (resultSet.next()) {
						found = true;
						return resultSet.getInt(1);
					}
					
					System.out.println("<" + itemName + "> was not found in the items table");
					return -1;

				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	
	
	
	public List<Room> RoomsByIdQuery(int id) {
		return executeTransaction(new Transaction<List<Room>>() {
			@Override
			public List<Room> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					// Get all rooms with the same room id as id
					stmt = conn.prepareStatement(
							"select rooms.* " +
							" from rooms " + 
							" where rooms.room_id = ?"
					);
					stmt.setInt(1, id);
					
					List<Room> result = new ArrayList<Room>();
					
					resultSet = stmt.executeQuery();
					
					// for testing that a result was returned
					Boolean found = false;
					
					while (resultSet.next()) {
						found = true;
						
						
						// create new Room object
						// retrieve attributes from resultSet starting with index 1
						Room room = loadRoom(resultSet);
						
						result.add(room);
					}
					
					// check if a room with the id was found
					if (!found) {
						System.out.println("<" + id + "> was not found in the rooms table");
					}
					
					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	public List<String> DirectionsByRoomIdQuery(int id) {
		return executeTransaction(new Transaction<List<String>>() {
			@Override
			public List<String> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					// Get all rooms with the same room id as id
					stmt = conn.prepareStatement(
							"select connections.direction " +
							" from connections " + 
							" where connections.room_id = ?"
					);
					stmt.setInt(1, id);
					
					List<String> result = new ArrayList<>();
					
					resultSet = stmt.executeQuery();
					
					// for testing that a result was returned
					Boolean found = false;
					
					while (resultSet.next()) {
						found = true;
						String direction = loadConnection(resultSet); 
						result.add(direction);
					}
					
					// check if a room with the id was found
					if(!found) {
						System.out.println("<" + id + "> was not found in the rooms table");
						return null;
					}
					
					return result;
					
					
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
				
			}
		});
	}
	
	// END OF QUERIES / INSERTS

	
	// OUR CLASS LOADING METHODS GO HERE
	private Item loadItem(ResultSet resultSet, int index, int itemID) throws SQLException {
		return new Item(
			itemID,
			resultSet.getString(index++),
			resultSet.getString(index++)
		);
	}
	
	private PlayerModel loadPlayer(ResultSet resultSet) throws SQLException {
		int index = 2;
		
		double health = resultSet.getDouble(index++);
		double maxHealth = resultSet.getDouble(index++);
		int lives = resultSet.getInt(index++);
		int currentRoom = resultSet.getInt(index++);
		
		PlayerModel toOut = new PlayerModel(health, lives, currentRoom);
		toOut.setMaxHealth(maxHealth);
		
		//TODO: Use sql controller to get player inventory
		
		return toOut;
	}
	
	private EnemyModel loadEnemy(ResultSet resultSet) throws SQLException {
		int index = 2;
		
		double health = resultSet.getDouble(index++);
		double maxHealth = resultSet.getDouble(index++);
		int lives = resultSet.getInt(index++);
		int currentRoom = resultSet.getInt(index++);
		String name = resultSet.getString(index++);
		String description = resultSet.getString(index++);
		
		EnemyModel toOut = new EnemyModel(health, lives, currentRoom, name, description);
		toOut.setMaxHealth(maxHealth);
		
		//TODO: Use sql controller to get enemy inventory
		
		return toOut;
	}
	
	private Room loadRoom(ResultSet resultSet) throws SQLException {
		int index = 2;
		String name = resultSet.getString(index++);
		String description = resultSet.getString(index++);
		Room toOut = new Room(name, description);
		
		return toOut;
	}
	
	private String loadConnection(ResultSet resultSet) throws SQLException{
		int index = 1;
		String direction = resultSet.getString(index++);
		return direction;
	}
	
	
	// END OF CLASS LOADING METHODS
	
	public<ResultType> ResultType executeTransaction(Transaction<ResultType> txn) {
		try {
			return doExecuteTransaction(txn);
		} catch (SQLException e) {
			throw new PersistenceException("Transaction failed", e);
		}
	}
	
	public<ResultType> ResultType doExecuteTransaction(Transaction<ResultType> txn) throws SQLException {
		Connection conn = connect();
		
		try {
			int numAttempts = 0;
			boolean success = false;
			ResultType result = null;
			
			while (!success && numAttempts < MAX_ATTEMPTS) {
				try {
					result = txn.execute(conn);
					conn.commit();
					success = true;
				} catch (SQLException e) {
					if (e.getSQLState() != null && e.getSQLState().equals("41000")) {
						// Deadlock: retry (unless max retry count has been reached)
						numAttempts++;
					} else {
						// Some other kind of SQLException
						throw e;
					}
				}
			}
			
			if (!success) {
				throw new SQLException("Transaction failed (too many retries)");
			}
			
			// Success!
			return result;
		} finally {
			DBUtil.closeQuietly(conn);
		}
	}

	private Connection connect() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:derby:test.db;create=true");
		
		// Set autocommit to false to allow execution of
		// multiple queries/statements as part of the same transaction.
		conn.setAutoCommit(false);
		
		return conn;
	}
	
	public Boolean createTables() {
		return executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {				
				Boolean isNewDatabase = true;
				
				// ITEM TYPES				
				PreparedStatement itemStmt = conn.prepareStatement(
						"create table itemTypes (" +
								"	item_id int primary key" + 
								"       generated always as identity (start with 1, increment by 1), " +									
								"	name varchar(16), " +
								"	description varchar(64) " +
								")"
						);

				try {
					itemStmt.executeUpdate();
					
				} catch (SQLException sql) {
					if (sql.getMessage().matches("Table/View '.*' already exists in Schema 'APP'.")) 
						isNewDatabase = false;
					else throw sql;
					
				} catch (Exception e) {
					throw e;
					
				} finally {
					DBUtil.closeQuietly(itemStmt);
				}
				
				
				
				// WEAPON TYPES
				PreparedStatement weaponStmt = conn.prepareStatement(
					"create table weaponTypes (" +
							"	weapon_id int primary key" + 
							"       generated always as identity (start with 1, increment by 1), " + 
							"   item_id int unique, " + 
							"		constraint item_id foreign key (item_id) references itemTypes(item_id), " + 
							"	damage double" +
							")"
				);
				
				try {
					weaponStmt.executeUpdate();
					
				} catch (SQLException sql) {
					if (sql.getMessage().matches("Table/View '.*' already exists in Schema 'APP'.")) 
						isNewDatabase = false;
					else throw sql;
					
				} catch (Exception e) {
					throw e;
					
				} finally {
					DBUtil.closeQuietly(weaponStmt);
				}
				
				
				// ENTITIES
				PreparedStatement entitiesStatement = conn.prepareStatement(
						"create table entities ("
						+ "id int primary key "
						+ "	generated always as identity (start with 1, increment by 1), "
						+ "health double, "
						+ "maxHealth double, "
						+ "lives int, "
						+ "currentRoom int, "
						+ "name varchar(16), "
						+ "description varchar(64)"
						+ ")"
						);
				try {
					entitiesStatement.executeUpdate();
					
				} catch (SQLException sql) {
					if (sql.getMessage().matches("Table/View '.*' already exists in Schema 'APP'.")) 
						isNewDatabase = false;
					else throw sql;
					
				} catch (Exception e) {
					throw e;
					
				} finally {
					DBUtil.closeQuietly(entitiesStatement);
				}
				
				
				
				// INVENTORY
				PreparedStatement inventoryStmt = conn.prepareStatement(
					"create table inventories (" +
							"   inventory_id int primary key" +
							"       generated always as identity (start with 2, increment by 1), " + 
							"	inventory_source int unique, " + 
							"   item_id int, " + 
							"		constraint inv_itemID foreign key (item_id) references itemTypes(item_id), " + 
							"	item_quantity int" +
					")"
				);
				
				try {
					inventoryStmt.executeUpdate();
					
				} catch (SQLException sql) {
					if (sql.getMessage().matches("Table/View '.*' already exists in Schema 'APP'.")) 
						isNewDatabase = false;
					else throw sql;
					
				} catch (Exception e) {
					throw e;
					
				} finally {
					DBUtil.closeQuietly(inventoryStmt);
				}
				
				
				
				// WEAPON SLOTS
				PreparedStatement weaponSlotsStmt = conn.prepareStatement(
						"create table weaponSlots (" +
								"   slot_id int primary key" +
								"       generated always as identity (start with 1, increment by 1), " + 
								"	inventory_source int, " +  
								"   slot_name varchar(16), " + 
								"	weapon_id int, " + 
								"		constraint weaponID foreign key (weapon_id) references itemTypes(item_id)" +
						")"
					);
					
					try {
						weaponSlotsStmt.executeUpdate();
						
					} catch (SQLException sql) {
						if (sql.getMessage().matches("Table/View '.*' already exists in Schema 'APP'.")) 
							isNewDatabase = false;
						else throw sql;
						
					} catch (Exception e) {
						throw e;
						
					} finally {
						DBUtil.closeQuietly(weaponSlotsStmt);
					}
				
				
				// ROOMS
				PreparedStatement roomsStatement = conn.prepareStatement(
						"Create table rooms ("
						+ "room_id int primary key"
						+ " generated always as identity (start with 1, increment by 1), "
						+ "name varchar(16), "
						+ "description varchar(64)"
						+ ")"
						);
				
				try {
					roomsStatement.executeUpdate();
					
				} catch (SQLException sql) {
					if (sql.getMessage().matches("Table/View '.*' already exists in Schema 'APP'.")) 
						isNewDatabase = false;
					else throw sql;
					
				} catch (Exception e) {
					throw e;
					
				} finally {
					DBUtil.closeQuietly(roomsStatement);
				}
				
				
				//CONNECTIONS
				PreparedStatement connectionsStatement = conn.prepareStatement(
						"Create table connections ("
						+ "index int primary key"
						+ " generated always as identity (start with 1, increment by 1), "
						+ "room_id int, "
						+ "	constraint roomID foreign key (room_id) references rooms(room_id), "
						+ "direction varchar(16), "
						+ "connection_id int)"
						);
				
				
				
				try {
					connectionsStatement.executeUpdate();
					
					
				} catch (SQLException sql) {
					if (sql.getMessage().matches("Table/View '.*' already exists in Schema 'APP'.")) 
						isNewDatabase = false;
					else
						throw sql;
					
				} catch (Exception e) {
					throw e;
					
				} finally {
					DBUtil.closeQuietly(connectionsStatement);
				}
				
				return isNewDatabase;
			}
		});
	}
	
	public void resetTable(String tableName, String primaryKeyName, Integer startingNum) {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement clearDatabase = conn.prepareStatement("delete from " + tableName);
				
				try {
					clearDatabase.execute();
					DBUtil.closeQuietly(clearDatabase);
		
					clearDatabase = conn.prepareStatement(
							"alter table " + tableName + 
							" alter column " + primaryKeyName + 
							" restart with " + startingNum.toString()
					);
					
					clearDatabase.execute();
				} catch(Exception e) {
					throw e;
					
				} finally {
					DBUtil.closeQuietly(clearDatabase);
				}
				
				return true;
			}
		});
	}
	
	public void loadInitialData(Boolean isNewDatabase) {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				List<Item> itemList;
				List<Weapon> weaponList;
				Map<Integer, Inventory> inventoryMap;
				PlayerModel player;
				List<EnemyModel> enemies;
				List<Room> rooms;
				List<Room> connections;
				
				try {
					itemList = InitialData.getItemTypes();
					weaponList = InitialData.getWeaponTypes();
					inventoryMap = InitialData.getInventories();

					player = InitialData.getPlayer();
					enemies = InitialData.getEnemies();
					rooms = InitialData.getRooms();
					connections = InitialData.getConnections();
					
				} catch (IOException e) {
					throw new SQLException("Couldn't read initial data", e);
				}

				
				// if database already exists, reset it first
				if (!isNewDatabase) {
					resetTable("connections", "index", 1);
					resetTable("rooms", "room_id", 1);
					resetTable("weaponSlots", "slot_id", 2);// reset dependencies first (inventory_source)
					resetTable("inventories", "inventory_id", 2);
					resetTable("weaponTypes", "weapon_id", 1);
					resetTable("itemTypes", "item_id", 1);// reset dependencies first (item_id)
				}
				
				
				// INSERT ITEMS
				PreparedStatement insertItem = conn.prepareStatement(
						"insert into itemTypes (name, description) values (?, ?)");

				try {
					for (Item item : itemList) {
						insertItem.setString(1, item.GetName());
						insertItem.setString(2, item.GetDescription());
						insertItem.addBatch();
					}
					
					insertItem.executeBatch();
					
				} catch(Exception e) {
					throw e;
					
				} finally {
					DBUtil.closeQuietly(insertItem);
				}
				
				
				// INSERT WEAPONS
				PreparedStatement insertWeapon = conn.prepareStatement(
						"insert into weaponTypes (item_id, damage) values (?, ?)");
				
				try {
					for (Weapon weapon : weaponList) {
						insertWeapon.setInt(1, weapon.GetID());
						insertWeapon.setDouble(2, weapon.GetDamage());
						insertWeapon.addBatch();
					}

					insertWeapon.executeBatch();
					
				} catch(Exception e) { 
					throw e;
					
				} finally {
					DBUtil.closeQuietly(insertWeapon);
				}
				
				
				// INSERT INVENTORIES
				PreparedStatement insertInventory = conn.prepareStatement(
						"insert into inventories (inventory_source, item_id, item_quantity) values (?, ?, ?)");
				
				PreparedStatement insertWeaponSlot = conn.prepareStatement(
						"insert into weaponSlots (inventory_source, slot_name, weapon_id) values (?, ?, ?)");
				
				try {
					for (Integer inventory_source : inventoryMap.keySet()) {
						Map<Item, Integer> inventoryItems = inventoryMap.get(inventory_source).GetItems();
						
						// loop through inventory items
						for (Item item : inventoryItems.keySet()) {
							
							// sources are split by 2 (even: entities, odd: rooms)
							// the source should be shifted left by 1 to read from the database
							// and be subsequently shifted right by 1 and added by 1 (accordingly) to push to the database
							insertInventory.setInt(1, inventory_source);
							insertInventory.setInt(2, item.GetID());
							insertInventory.setInt(3, inventoryItems.get(item));
							insertInventory.addBatch();
						}
						
						// add equipped weapons
						if (inventory_source % 2 == 1) {
							Map<String, Weapon> weaponSlots = ((EntityInventory) inventoryMap.get(inventory_source)).GetWeaponsAsSlots();
							
							for (String slot : weaponSlots.keySet()) {									
								insertWeaponSlot.setInt(1, inventory_source);
								insertWeaponSlot.setString(2, slot);
								insertWeaponSlot.setInt(3, weaponSlots.get(slot).GetID());
								insertWeaponSlot.addBatch();
							}
						}
					}

					insertInventory.executeBatch();
					insertWeaponSlot.executeBatch();
					
				} catch(Exception e) { 
					throw e;
					
				} finally {
					DBUtil.closeQuietly(insertWeapon);
				}
				
				
				//Insert entities
				PreparedStatement insertEntityStatement = conn.prepareStatement(
						"insert into entities (health, maxHealth, lives, currentRoom, name, description) values (?, ?, ?, ?, ?, ?)");
				//Player
				insertEntityStatement.setDouble(1, player.getHealth());
				insertEntityStatement.setDouble(2, player.getMaxHealth());
				insertEntityStatement.setInt(3, player.getLives());
				insertEntityStatement.setInt(4, player.getCurrentRoomIndex());
				insertEntityStatement.setString(5, "");
				insertEntityStatement.setString(6, "");
				
				insertEntityStatement.executeUpdate();
				
				//Enemies
				for (EnemyModel enemy : enemies) {
					insertEntityStatement.setDouble(1, enemy.getHealth());
					insertEntityStatement.setDouble(2, enemy.getMaxHealth());
					insertEntityStatement.setInt(3, enemy.getLives());
					insertEntityStatement.setInt(4, enemy.getCurrentRoomIndex());
					insertEntityStatement.setString(5, enemy.getName());
					insertEntityStatement.setString(6, enemy.getDescription());
					
					insertEntityStatement.executeUpdate();
				}
				

				//Insert Rooms
				PreparedStatement insertRoomStatement = conn.prepareStatement(
						"insert into rooms (name, description) values (?, ?)");
						
				
				for(Room room : rooms) {
					insertRoomStatement.setString(1, room.getShortRoomDescription());
					insertRoomStatement.setString(2, room.getLongRoomDescription());
					insertRoomStatement.executeUpdate();
				}
				
				//Insert Connections
				PreparedStatement insertConnectionsStatement = conn.prepareStatement(
						"insert into connections (room_id, direction, connection_id) values (?, ?, ?)");
						
				
				
				for(Room room : connections) {
					for(String connection : room.getAllKeys()) {
						
						insertConnectionsStatement.setInt(1, room.getRoomId());
						insertConnectionsStatement.setString(2, connection);
						insertConnectionsStatement.setInt(3, room.getConnectedRoom(connection));
						insertConnectionsStatement.executeUpdate();
					}
				}
				return true;
			}
		});
	}
	
	// The main method creates the database tables and loads the initial data.
	public static void main(String[] args) throws IOException {
		DerbyDatabase db = new DerbyDatabase();
		
		System.out.println("Creating tables...");
		Boolean isNewDatabase = db.createTables();
		
		System.out.println("Loading initial data...");
		db.loadInitialData(isNewDatabase);
		
		System.out.println("Success!");
	}

	@Override
	public PlayerModel GetPlayer() {
		return executeTransaction(new Transaction<PlayerModel>() {
			@Override
			public PlayerModel execute(Connection conn) throws SQLException {
				PreparedStatement getPlayerStatement = null;
				ResultSet resultSet = null;
				
				try {
					// retreive all attributes from both Books and Authors tables
					getPlayerStatement = conn.prepareStatement(
							"select entities.* " +
							" where entities.id = 1"
					);
					
					PlayerModel player = null;
					
					resultSet = getPlayerStatement.executeQuery();
					
					// for testing that a result was returned
					Boolean found = false;
					
					while (resultSet.next()) {
						found = true;
						
						// create new Author object
						// retrieve attributes from resultSet starting with index 1
						player = loadPlayer(resultSet);
					}
					
					// check if the title was found
					if (!found) {
						System.out.println("Player was not found in the entities table");
					}
					
					return player;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(getPlayerStatement);
				}
			}
		});
	}


	@Override
	public ArrayList<EnemyModel> GetEnemiesInRoom(int roomIndex) {
		return executeTransaction(new Transaction<ArrayList<EnemyModel>>() {
			@Override
			public ArrayList<EnemyModel> execute(Connection conn) throws SQLException {
				PreparedStatement getEnemiesStatement = null;
				ResultSet resultSet = null;
				
				try {
					// retreive all attributes from both Books and Authors tables
					getEnemiesStatement = conn.prepareStatement(
							"select entities* " +
							" where entities.id > 1 "
							+ "and entities.currentRoom = ?"
					);
					getEnemiesStatement.setInt(1, roomIndex);
					
					ArrayList<EnemyModel> enemies = new ArrayList<>();
					
					resultSet = getEnemiesStatement.executeQuery();
					
					// for testing that a result was returned
					Boolean found = false;
					
					while (resultSet.next()) {
						found = true;
						
						// create new Author object
						// retrieve attributes from resultSet starting with index 1
						enemies.add(loadEnemy(resultSet));
					}
					
					// check if the title was found
					if (!found) {
						System.out.println("No enemies were found in the entities table");
					}
					
					return enemies;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(getEnemiesStatement);
				}
			}
		});
	}
	

	@Override
	public PlayerModel UpdatePlayerHealth(PlayerModel player) {
		return executeTransaction(new Transaction<PlayerModel>() {
			public PlayerModel execute(Connection conn) throws SQLException {
				PreparedStatement insertStatement = null;
				
				insertStatement = conn.prepareStatement(
					"update entities "
					+ "set health = ? "
					+ "where entities.id = 1"
				);
				
				insertStatement.setDouble(1, player.getHealth());
				
				insertStatement.executeUpdate();
				
				return GetPlayer();
			}
		});
	}


	@Override
	public PlayerModel UpdatePlayerRoom(PlayerModel player) {
		return executeTransaction(new Transaction<PlayerModel>() {
			public PlayerModel execute(Connection conn) throws SQLException {
				PreparedStatement insertStatement = null;
				
				insertStatement = conn.prepareStatement(
					"update entities "
					+ "set currentRoom = ? "
					+ "where entities.id = 1"
				);
				
				insertStatement.setInt(1, player.getCurrentRoomIndex());
				
				insertStatement.executeUpdate();
				
				return GetPlayer();
			}
		});
		
	}


	@Override
	public PlayerModel UpdatePlayerMaxHealth(PlayerModel player) {
		return executeTransaction(new Transaction<PlayerModel>() {
			public PlayerModel execute(Connection conn) throws SQLException {
				PreparedStatement insertStatement = null;
				
				insertStatement = conn.prepareStatement(
					"update entities "
					+ "set maxHealth = ? "
					+ "where entities.id = 1"
				);
				
				insertStatement.setDouble(1, player.getMaxHealth());
				
				insertStatement.executeUpdate();
				
				return GetPlayer();
			}
		});
		
	}


	@Override
	public PlayerModel UpdatePlayerLives(PlayerModel player) {
		return executeTransaction(new Transaction<PlayerModel>() {
			public PlayerModel execute(Connection conn) throws SQLException {
				PreparedStatement insertStatement = null;
				
				insertStatement = conn.prepareStatement(
					"update entities "
					+ "set lives = ? "
					+ "where entities.id = 1"
				);
				
				insertStatement.setInt(1, player.getLives());
				
				insertStatement.executeUpdate();
				
				return GetPlayer();
			}
		});
		
	}


	@Override
	public EntityInventory getPlayerInventory() {
		// TODO Auto-generated method stub
		return null;
	}
}
