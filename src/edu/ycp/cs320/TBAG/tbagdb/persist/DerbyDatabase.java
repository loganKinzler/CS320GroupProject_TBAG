package edu.ycp.cs320.TBAG.tbagdb.persist;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.ycp.cs320.TBAG.controller.EnemyController;
import edu.ycp.cs320.TBAG.controller.EntityController;
import edu.ycp.cs320.TBAG.model.EntityInventory;
import edu.ycp.cs320.TBAG.model.EntityModel;
import edu.ycp.cs320.TBAG.model.Item;

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
	
	public void createTables() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;
				
				try {
					stmt1 = conn.prepareStatement(
						"create table authors (" +
						"	author_id integer primary key " +
						"		generated always as identity (start with 1, increment by 1), " +									
						"	lastname varchar(40)," +
						"	firstname varchar(40)" +
						")"
					);	
					stmt1.executeUpdate();
					
					stmt2 = conn.prepareStatement(
							"create table books (" +
							"	book_id integer primary key " +
							"		generated always as identity (start with 1, increment by 1), " +
							"	author_id integer constraint author_id references authors, " +
							"	title varchar(70)," +
							"	isbn varchar(15)," +
							"   published integer " +
							")"
					);
					stmt2.executeUpdate();
					
					return true;
				} finally {
					DBUtil.closeQuietly(stmt1);
					DBUtil.closeQuietly(stmt2);
				}
			}
		});
	}
	
	public void loadInitialData() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				return true;
			}
		});
	}
	
	// The main method creates the database tables and loads the initial data.
	public static void main(String[] args) throws IOException {
		System.out.println("Creating tables...");
		DerbyDatabase db = new DerbyDatabase();
		db.createTables();
		
		System.out.println("Loading initial data...");
		db.loadInitialData();
		
		System.out.println("Success!");
	}
	
	public void updateAllPlayerInfo(int currentRoom, double maxHealth, double health, int lives, EntityInventory inventory) {
		
	}
	
	public void insertIntoPlayerInventory(Item item) {
		
	}
	
	public EntityInventory getPlayerInventory() {
		String getStatement = 
		"select entities.inventoryId, inventories.* "
		+ "where entities.inventoryId = inventories.id "
		+ "and entities.id = 1";
		return null;
	}
	
	public Item getItemFromPlayerInventory(Item toGet) {
		//Get item name from param and get from inventory by item name
		String nameToGetBy = toGet.GetName();
		String getStatement = 
		"";
		return null;
	}
	
	public double getPlayerHealth() {
		String getStatement = 
		"select health from entities "
		+ "where entities.id = 1";
		return 0.0;
	}
	
	public double getPlayerMaxHealth() {
		String getStatement = 
		"select maxHealth from entities "
		+ "where entities.id = 1";
		return 0.0;
	}
	
	public int getPlayerLives() {
		String getStatement = 
		"select lives from entities "
		+ "where entities.id = 1";
		return 0;
	}
	
	public int getPlayerCurrentRoom() {
		String getStatement = 
		"select currentRoom from entities "
		+ "where entities.id = 1";
		return 0;
	}
	
	public void updatePlayerHealth(EntityModel player) {
		String updateStatement = 
		"update entities " + 
		"set health = ?";
	}
	
	public void updatePlayerMaxHealth(EntityModel player) {
		String updateStatement = 
		"update entities "
		+ "set maxHealth = ? "
		+ "wher eentities.id = 1";
	}
	
	public void updatePlayerCurrentRoom(EntityModel player) {
		String updateStatement = 
		"update entities " + 
		"set currentRoom = ? "
		+ "where entities.id = 1";
	}
	
	public void updatePlayerLives(EntityModel player) {
		String updateStatement = 
		"update entities "
		+ "set lives = ? "
		+ "where entities.id = 1";
	}
	
	public EnemyController getEnemyByName(String name) {
		String getStatement = 
		"select * from entities " + 
		"where enemies.name = ?";
		return null;
	}
	public ArrayList<EntityController> getEnemiesByRoomId(int roomId) {
		ArrayList<EntityController> enemies = new ArrayList<>();
		
		String getStatement = 
		"select * from entities "
		+ "where entities.currentRoom = ? "
		+ "and entities.id != 1";
		
		return null;
	}
}
