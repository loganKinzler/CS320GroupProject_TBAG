package edu.ycp.cs320.TBAG.tbagdb.persist;

import java.io.File;
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
import edu.ycp.cs320.TBAG.model.Inventory;
import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.PlayerModel;
import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.Weapon;

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
	
	public List<Room> getRooms() {
		return executeTransaction(new Transaction<List<Room>>() {
			@Override
			public List<Room> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					// Get all rooms with the same room id as id
					stmt = conn.prepareStatement(
							"select * from rooms"
					);
					
					
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
					
					
					if (!found) {
						System.out.println("No rooms were found in the rooms table.");
					}
					
					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	public List<Room> getConnections() {
		return executeTransaction(new Transaction<List<Room>>() {
			@Override
			public List<Room> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					// Get all rooms with the same room id as id
					stmt = conn.prepareStatement(
							"select * from connections"
					);
					
					
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
					
					
					if (!found) {
						System.out.println("No connections were found in the connections table.");
					}
					
					return result;
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
	
	public List<Room> DirectionsByRoomIdQuery(int id) {
		return executeTransaction(new Transaction<List<Room>>() {
			@Override
			public List<Room> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					// Get all rooms with the same room id as id
					stmt = conn.prepareStatement(
							"select connections.* " +
							"from connections " + 
							" where connections.room_id = ?"
					);
					stmt.setInt(1, id);
					
					List<Room> result = new ArrayList<Room>();
					
					resultSet = stmt.executeQuery();
					
					// for testing that a result was returned
					Boolean found = false;
					
					while (resultSet.next()) {
						found = true;
						Room connect = loadConnection(resultSet); 
						result.add(connect);
					}
//					System.out.print(result);
					
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
	
	private Room loadConnection(ResultSet resultSet) throws SQLException{
		int index = 1;
		int roomID = resultSet.getInt(index++);
		int north = resultSet.getInt(index++);
		int east = resultSet.getInt(index++);
		int south = resultSet.getInt(index++);
		int west = resultSet.getInt(index++);
		
		Room toOut = new Room(roomID);
		toOut.setConnectedRoom("north", north);
		toOut.setConnectedRoom("east", east);
		toOut.setConnectedRoom("south", south);
		toOut.setConnectedRoom("west", west);
		//System.out.println(resultSet);
		//System.out.println(toOut.getConnectedRoom("north") + ", " + toOut.getConnectedRoom("east") + ", " + toOut.getConnectedRoom("south") + ", " + toOut.getConnectedRoom("west"));
		
		return toOut;
		
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
						+ "generated always as identity (start with 1, increment by 1), "
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
				//north|east|south|west
				//0 = no room connection
				PreparedStatement connectionsStatement = conn.prepareStatement(
						"Create table connections ("
						+ "room_id int primary key "
						+ "generated always as identity (start with 1, increment by 1), "
						+ "north int, east int, south int, west int)"
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

				//GameHistory
				PreparedStatement historystmt = null;
				try {
					historystmt = conn.prepareStatement(
					"create table GameHistory ("+
					" printout varchar(10000))"
					);
					historystmt.executeUpdate();
				} catch (SQLException sql){
				if(sql.getMessage().matches("Table/View '.*' already exists in Scheme 'APP'.")) {
					isNewDatabase = false;
				}
				}catch (Exception e){
				throw e;
				}finally{
					historystmt=null;
				DBUtil.closeQuietly(historystmt);
				
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
					resetTable("connections", "room_id", 1);
					resetTable("rooms", "room_id", 1);
					resetTable("weaponSlots", "slot_id", 2);// reset dependencies first (inventory_source)
					resetTable("inventories", "inventory_id", 2);
					resetTable("weaponTypes", "weapon_id", 1);
					resetTable("itemTypes", "item_id", 1);// reset dependencies first (item_id)
					resetTable("entities", "id", 1);
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
						"insert into connections (north, east, south, west) values (?, ?, ?, ?)");
						
				
				
				for(Room room : connections) {
					insertConnectionsStatement.setInt(1, room.getConnectedRoom("north"));
					insertConnectionsStatement.setInt(2, room.getConnectedRoom("east"));
					insertConnectionsStatement.setInt(3, room.getConnectedRoom("south"));
					insertConnectionsStatement.setInt(4, room.getConnectedRoom("west"));
					insertConnectionsStatement.executeUpdate();
				}
				return true;
			}
		});
	}
	
	public void create() {
		DerbyDatabase db = new DerbyDatabase();
		
		System.out.println("Creating tables...");
		Boolean isNewDatabase = db.createTables();
		System.out.println(isNewDatabase);
		
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
							"select entities.* from entities " +
							"where entities.id = 1"
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
	public Double UpdatePlayerHealth(double health) {
		return executeTransaction(new Transaction<Double>() {
			public Double execute(Connection conn) throws SQLException {
				PreparedStatement insertStatement = null;
				
				insertStatement = conn.prepareStatement(
					"update entities "
					+ "set health = ? "
					+ "where entities.id = 1"
				);
				
				insertStatement.setDouble(1, health);
				
				try {
					insertStatement.executeUpdate();
					conn.commit();
				}
				finally {
					DBUtil.closeQuietly(insertStatement);
				}
				
				return health;
			}
		});
	}

	@Override
	public Integer UpdatePlayerRoom(int room) {
		return executeTransaction(new Transaction<Integer>() {
			public Integer execute(Connection conn) throws SQLException {
				PreparedStatement insertStatement = null;
				
				insertStatement = conn.prepareStatement(
					"update entities "
					+ "set currentRoom = ? "
					+ "where entities.id = 1"
				);
				
				insertStatement.setInt(1, room);
				
				try {
					insertStatement.executeUpdate();
					conn.commit();
				}
				finally {
					DBUtil.closeQuietly(insertStatement);
				}
				
				return room;
			}
		});
		
	}

	@Override
	public Double UpdatePlayerMaxHealth(double maxHealth) {
		return executeTransaction(new Transaction<Double>() {
			public Double execute(Connection conn) throws SQLException {
				PreparedStatement insertStatement = null;
				
				insertStatement = conn.prepareStatement(
					"update entities "
					+ "set maxHealth = ? "
					+ "where entities.id = 1"
				);
				
				insertStatement.setDouble(1, maxHealth);
				
				try {
					insertStatement.executeUpdate();
					conn.commit();
				}
				finally {
					DBUtil.closeQuietly(insertStatement);
				}
				
				return maxHealth;
			}
		});
		
	}

	@Override
	public Integer UpdatePlayerLives(int lives) {
		return executeTransaction(new Transaction<Integer>() {
			public Integer execute(Connection conn) throws SQLException {
				PreparedStatement insertStatement = null;
				
				insertStatement = conn.prepareStatement(
					"update entities "
					+ "set lives = ? "
					+ "where entities.id = 1"
				);
				
				insertStatement.setInt(1, lives);
				
				try {
					insertStatement.executeUpdate();
					conn.commit();
				}
				finally {
					DBUtil.closeQuietly(insertStatement);
				}
				
				return lives;
			}
		});
		
	}
	
	@Override
	public Double UpdateEnemyHealthById(int id, double health) {
		return executeTransaction(new Transaction<Double>() {
			public Double execute(Connection conn) throws SQLException {
				PreparedStatement insertStatement = null;
				
				insertStatement = conn.prepareStatement(
					"update entities "
					+ "set health = ? "
					+ "where entities.id = ?"
				);

				insertStatement.setDouble(1, health);
				insertStatement.setInt(2, id);
				
				try {
					insertStatement.executeUpdate();
					conn.commit();
				}
				finally {
					DBUtil.closeQuietly(insertStatement);
				}
				
				return health;
			}
		});
	}
	
	public static void main(String[] args) {
		DerbyDatabase db = new DerbyDatabase();
		
		System.out.println("Creating tables...");
		Boolean isNewDatabase = db.createTables();
		System.out.println(isNewDatabase);
		
		System.out.println("Loading initial data...");
		db.loadInitialData(isNewDatabase);
		
		System.out.println("Success!");
	}

	@Override
	public Double UpdateEnemyMaxHealthById(int id, double maxHealth) {
		return executeTransaction(new Transaction<Double>() {
			public Double execute(Connection conn) throws SQLException {
				PreparedStatement insertStatement = null;
				
				insertStatement = conn.prepareStatement(
					"update entities "
					+ "set maxHealth = ? "
					+ "where entities.id = ?"
				);

				insertStatement.setDouble(1, maxHealth);
				insertStatement.setInt(2, id);
				
				try {
					insertStatement.executeUpdate();
					conn.commit();
				}
				finally {
					DBUtil.closeQuietly(insertStatement);
				}
				
				return maxHealth;
			}
		});
	}

	@Override
	public Integer UpdateEnemyLivesById(int id, int lives) {
		return executeTransaction(new Transaction<Integer>() {
			public Integer execute(Connection conn) throws SQLException {
				PreparedStatement insertStatement = null;
				
				insertStatement = conn.prepareStatement(
					"update entities "
					+ "set lives = ? "
					+ "where entities.id = ?"
				);

				insertStatement.setInt(1, lives);
				insertStatement.setInt(2, id);
				
				try {
					insertStatement.executeUpdate();
					conn.commit();
				}
				finally {
					DBUtil.closeQuietly(insertStatement);
				}
				
				return lives;
			}
		});
	}

	@Override
	public Integer UpdateEnemyRoomById(int id, int roomId) {
		return executeTransaction(new Transaction<Integer>() {
			public Integer execute(Connection conn) throws SQLException {
				PreparedStatement insertStatement = null;
				
				insertStatement = conn.prepareStatement(
					"update entities "
					+ "set currentRoom = ? "
					+ "where entities.id = ?"
				);

				insertStatement.setInt(1, roomId);
				insertStatement.setInt(2, id);
				
				try {
					insertStatement.executeUpdate();
					conn.commit();
				}
				finally {
					DBUtil.closeQuietly(insertStatement);
				}
				
				return roomId;
			}
		});
	}

	@Override
	public EnemyModel getEnemyById(int id) {
		return executeTransaction(new Transaction<EnemyModel>() {
			@Override
			public EnemyModel execute(Connection conn) throws SQLException {
				PreparedStatement getEnemyStatement = null;
				ResultSet resultSet = null;
				
				try {
					// retreive all attributes from both Books and Authors tables
					getEnemyStatement = conn.prepareStatement(
							"select entities.* " +
							" where entities.id = ?"
					);
					
					EnemyModel enemy = null;
					
					resultSet = getEnemyStatement.executeQuery();
					
					// for testing that a result was returned
					Boolean found = false;
					
					while (resultSet.next()) {
						found = true;
						
						// create new Author object
						// retrieve attributes from resultSet starting with index 1
						enemy = loadEnemy(resultSet);
					}
					
					// check if the title was found
					if (!found) {
						System.out.println("Enemy was not found in the entities table");
					}
					
					return enemy;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(getEnemyStatement);
				}
			}
		});
	}

	@Override
	public EntityInventory getPlayerInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String UpdateEnemyNameById(int id, String name) {
		return executeTransaction(new Transaction<String>() {
			public String execute(Connection conn) throws SQLException {
				PreparedStatement insertStatement = null;
				
				insertStatement = conn.prepareStatement(
					"update entities "
					+ "set name = ? "
					+ "where entities.id = ?"
				);

				insertStatement.setString(1, name);
				insertStatement.setInt(2, id);
				
				try {
					insertStatement.executeUpdate();
					conn.commit();
				}
				finally {
					DBUtil.closeQuietly(insertStatement);
				}
				
				return name;
			}
		});
	}

	@Override
	public String UpdateEnemyDescriptionById(int id, String description) {
		return executeTransaction(new Transaction<String>() {
			public String execute(Connection conn) throws SQLException {
				PreparedStatement insertStatement = null;
				
				insertStatement = conn.prepareStatement(
					"update entities "
					+ "set description = ? "
					+ "where entities.id = ?"
				);

				insertStatement.setString(1, description);
				insertStatement.setInt(2, id);
				
				try {
					insertStatement.executeUpdate();
					conn.commit();
				}
				finally {
					DBUtil.closeQuietly(insertStatement);
				}
				
				return description;
			}
		});
	}
	
	//--------Change Made By Andrew ----- It deletes the DB
	@Override
	public void deleteDb(String dbName, String dblocation) {
		// Set Derby system home if specified
       if (dblocation != null && !dblocation.isEmpty()) {
           System.setProperty("derby.system.home", dblocation);
       }
       try {
           // Load Derby driver
           Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
          
           // Derby uses a special shutdown URL to close the database
           String shutdownUrl = "jdbc:derby:;shutdown=true";
           try {
               DriverManager.getConnection(shutdownUrl);
           } catch (SQLException e) {
               // Expected exception when shutting down Derby
               if (!e.getSQLState().equals("08006")) {
                   throw e;
               }
           }
          
           // Delete the directory path
           String dbPath = (dblocation == null) ? dbName : dblocation + "/" + dbName;
           deleteDirectory(new java.io.File(dbPath));
          
           System.out.println("Derby database '" + dbName + "' deleted successfully");
          
       } catch (ClassNotFoundException e) {
           System.err.println("Derby driver not found: " + e.getMessage());
       } catch (SQLException e) {
           System.err.println("Error deleting Derby database: " + e.getMessage());
       }
   }
	private static boolean deleteDirectory(File directory) {
       if (directory.exists()) {
           File[] files = directory.listFiles();
           if (files != null) {
               for (File file : files) {
                   if (file.isDirectory()) {
                       deleteDirectory(file);
                   } else {
                       file.delete();
                   }
               }
           }
           return directory.delete();
       }
       return false;
   }
	
	//----Change Made by Andrew --- it gets the history from the DB and returns a list of strings for the printout stuff
		public List<String> loadHistory(){
			Connection conn = null;
			PreparedStatement getHistoryStmt = null;
			ResultSet resultSet = null;
			List<String> history = new ArrayList<>();
			try {
				conn = connect();
			getHistoryStmt = conn.prepareStatement(
					"SELECT * FROM GameHistory"
					);
			resultSet = getHistoryStmt.executeQuery();
			
			
			while(resultSet.next()) {
				String rowinfo = resultSet.getString(1);
			history.add(rowinfo);	
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally {
				try {
					if(resultSet != null) resultSet.close();
					if(getHistoryStmt != null) getHistoryStmt.close();
					if(conn != null) conn.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			return history;
		}
		
		//---Change Made by Andrew --- adds a string to the List and to the db
		public void addToHistory(String add) {
			Connection conn = null;
			PreparedStatement addHistory = null;
			try {
				conn = connect();
				addHistory = conn.prepareStatement(
						"INSERT INTO GameHistory(printout) "+
					    "VALUES(?)"
						);
				addHistory.setString(1,add);
				addHistory.executeUpdate();	
				conn.commit();
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				try {
					if(addHistory != null)addHistory.close();
					if(conn != null)conn.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
}
