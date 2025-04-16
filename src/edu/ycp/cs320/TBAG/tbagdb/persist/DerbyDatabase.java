package edu.ycp.cs320.TBAG.tbagdb.persist;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.tbagdb.persist.InitialData;
import edu.ycp.cs320.TBAG.tbagdb.persist.DBUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
							"  from itemTypes " +
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
						Item item = loadItem(resultSet, 1);
						
						result.add(item);
					}
					
					// check if the title was found
					if (!found) {
						System.out.println("<" + itemName + "> was not found in the books table");
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
	
	public void createTables() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				
				try {
					stmt1 = conn.prepareStatement(
						"create table itemTypes (" +
						"	item_id integer primary key " +
						"		generated always as identity (start with 1, increment by 1), " +									
						"	name varchar(15)," +
						"	description varchar(60)" +
						")"
					);
					stmt1.executeUpdate();
					
					return true;
				} finally {
					DBUtil.closeQuietly(stmt1);
				}
			}
		});
	}
	
	public void loadInitialData() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				List<Item> itemList;
				
				try {
					itemList = InitialData.getItemTypes();
				} catch (IOException e) {
					throw new SQLException("Couldn't read initial data", e);
				}

				
				PreparedStatement insertItem = null;

				try {
					// populate authors table (do authors first, since author_id is foreign key in books table)
					insertItem = conn.prepareStatement("insert into itemTypes (name, description) values (?, ?)");
					for (Item item : itemList) {
						insertItem.setString(1, item.GetName());
						insertItem.setString(2, item.GetDescription());
						insertItem.addBatch();
					}
					insertItem.executeBatch();
					
					return true;
				} finally {
					DBUtil.closeQuietly(insertItem);
				}
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
}
