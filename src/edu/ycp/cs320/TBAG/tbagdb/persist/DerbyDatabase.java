package edu.ycp.cs320.TBAG.tbagdb.persist;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.ycp.cs320.TBAG.model.EnemyModel;
import edu.ycp.cs320.TBAG.model.EntityInventory;
import edu.ycp.cs320.TBAG.model.EntityModel;
import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.PlayerModel;
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
					
					while (resultSet.next()) {
						found = true;
						
						// create new Author object
						// retrieve attributes from resultSet starting with index 1
						Item item = loadItem(resultSet, 2);
						
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
	
	// END OF QUERIES / INSERTS

	
	// OUR CLASS LOADING METHODS GO HERE
	private Item loadItem(ResultSet resultSet, int index) throws SQLException {
		return new Item(
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
							"   item_id int constraint item_id references itemTypes, " + 
							"	damage double" +
							")"
				);
				
				try {
					weaponStmt.executeUpdate();
					
				} catch (SQLException sql) {
					if (sql.getMessage().matches("Table/View '.*' already exists in Schema 'APP'.")) 
						isNewDatabase = false;
					
				} catch (Exception e) {
					throw e;
					
				} finally {
					DBUtil.closeQuietly(weaponStmt);
				}
				
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
					
				} catch (Exception e) {
					throw e;
					
				} finally {
					DBUtil.closeQuietly(entitiesStatement);
				}
				
				

				
//				// ROOMS
//				try {
//					stmt = conn.prepareStatement(
//						"create table rooms (" +
//							"	room_id int primary key" + 
//							"       generated always as identity (start with 1, increment by 1), " +									
//							"   name varchar(16)," + 
//							"   description varchar(16)" + 
//							")"
//					);
//					
//					stmt.executeUpdate();
//					
//				} catch (SQLException sql) {
//					if (sql.getMessage().matches("Table/View '.*' already exists in Schema 'APP'.")) 
//						isNewDatabase = false;
//					
//				} catch (Exception e) {
//					throw e;
//					
//				} finally {
//					DBUtil.closeQuietly(stmt);
//					stmt = null;
//				}
//				
//				
//				
//				// CONNECTIONS
//				try {
//					stmt = conn.prepareStatement(
//						"create table connections (" +
//							"	room_id int constraint room_id references rooms, " + 
//							"   direction varchar(8)," + 
//							"	destination_id int constraint room_id references rooms" +
//							")"
//					);
//					
//					stmt.executeUpdate();
//					
//				} catch (SQLException sql) {
//					if (sql.getMessage().matches("Table/View '.*' already exists in Schema 'APP'.")) 
//						isNewDatabase = false;
//					
//				} catch (Exception e) {
//					throw e;
//					
//				} finally {
//					DBUtil.closeQuietly(stmt);
//					stmt = null;
//				}
				
				
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
					
					return true;
				} catch(Exception e) {
					throw e;
					
				} finally {
					DBUtil.closeQuietly(clearDatabase);
				}
			}
		});
	}
	
	public void loadInitialData(Boolean isNewDatabase) {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				List<Item> itemList;
				Map<Weapon, Integer> weaponMap;
				PlayerModel player;
				List<EnemyModel> enemies;
				
				try {
					itemList = InitialData.getItemTypes();
					weaponMap = InitialData.getWeaponTypes();
					player = InitialData.getPlayer();
					enemies = InitialData.getEnemies();
					
				} catch (IOException e) {
					throw new SQLException("Couldn't read initial data", e);
				}

				
				// if database already exists, reset it first
				if (!isNewDatabase) {
					resetTable("weaponTypes", "weapon_id", 1);// reset dependency first (item_id)
					resetTable("itemTypes", "item_id", 1);
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
					for (Weapon weapon : weaponMap.keySet()) {
						insertWeapon.setInt(1, weaponMap.get(weapon));
						insertWeapon.setDouble(2, weapon.GetDamage());
						
						insertWeapon.addBatch();
					}

					insertWeapon.executeBatch();
					
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
				
				try {
					insertStatement.executeUpdate();
					conn.commit();
				}
				finally {
					DBUtil.closeQuietly(insertStatement);
				}
				
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
				
				try {
					insertStatement.executeUpdate();
					conn.commit();
				}
				finally {
					DBUtil.closeQuietly(insertStatement);
				}
				
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
				
				try {
					insertStatement.executeUpdate();
					conn.commit();
				}
				finally {
					DBUtil.closeQuietly(insertStatement);
				}
				
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
				
				try {
					insertStatement.executeUpdate();
					conn.commit();
				}
				finally {
					DBUtil.closeQuietly(insertStatement);
				}
				
				return GetPlayer();
			}
		});
		
	}
	
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
}
