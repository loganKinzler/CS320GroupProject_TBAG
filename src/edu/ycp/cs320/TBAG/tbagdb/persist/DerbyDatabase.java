package edu.ycp.cs320.TBAG.tbagdb.persist;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.ycp.cs320.TBAG.comparator.ItemPassComparator;
import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.Weapon;

import edu.ycp.cs320.TBAG.tbagdb.persist.InitialData;
import edu.ycp.cs320.TBAG.tbagdb.persist.DBUtil;

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
				
				try {
					itemList = InitialData.getItemTypes();
					weaponMap = InitialData.getWeaponTypes();
				} catch (IOException e) {
					throw new SQLException("Couldn't read initial data", e);
				}

				
				// if database already exists, reset it first
				if (!isNewDatabase) {
					resetTable("weaponTypes", "weapon_id", 1);// reset dependency first (item_id)
					resetTable("itemTypes", "item_id", 1);
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
}
