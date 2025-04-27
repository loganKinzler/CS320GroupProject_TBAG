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

import edu.ycp.cs320.TBAG.comparator.ItemByIDComparator;
import edu.ycp.cs320.TBAG.model.EnemyModel;
import edu.ycp.cs320.TBAG.model.EntityInventory;
import edu.ycp.cs320.TBAG.model.EntityModel;
import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.PlayerModel;
import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.RoomInventory;
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
	@Override
	public Item ItemsByNameQuery(String itemName) {
		return executeTransaction(new Transaction<Item>() {
			@Override
			public Item execute(Connection conn) throws SQLException {
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
						Item item = loadItem(resultSet, 1);
						result.add(item);
					}
					
					// check if the title was found
					if (!found) {
						System.out.println("<" + itemName + "> was not found in the items table");
						return null;
					}
					
					conn.commit();
					return result.getFirst();
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	@Override
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
						
						conn.commit();
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
	
	@Override
	public Inventory InventoryBySourceID(Integer sourceID) {
		return executeTransaction(new Transaction<Inventory>() {
			@Override
			public Inventory execute(Connection conn) throws SQLException {
				PreparedStatement equippedWeaponsStmt = null;
				ResultSet equippedWeaponsResults = null;
				
				PreparedStatement onlyItemsStmt = null;
				ResultSet onlyItemsResults = null;
				
				PreparedStatement onlyWeaponsStmt = null;
				ResultSet onlyWeaponsResults = null;
				
				List<Item> result = new ArrayList<Item>();
				Inventory resultInventory = new EntityInventory();
				
				try {
					
					// entity inventory weapon slots
					if (sourceID % 2 == 1) {		
						
						// weaponSlots
						equippedWeaponsStmt = conn.prepareStatement(
								"select itemTypes.item_id, itemTypes.name, itemTypes.description, weaponTypes.damage, slotNames.slot_name" +
								"	from weaponSlots" +
								"		inner join weaponTypes on weaponTypes.item_id = weaponSlots.item_id" +
								"		inner join itemTypes on itemTypes.item_id = weaponSlots.item_id" +
								"		inner join slotNames on slotNames.slot_id = weaponSlots.slot_id" +
								"	where weaponSlots.inventory_source = ?"
						);
						
						equippedWeaponsStmt.setInt(1, sourceID);
						equippedWeaponsResults = equippedWeaponsStmt.executeQuery();
						
						while (equippedWeaponsResults.next()) {
							Weapon resultWeapon = loadWeapon(equippedWeaponsResults, 1);
							((EntityInventory) resultInventory).EquipWeapon(equippedWeaponsResults.getString(5), resultWeapon);
						}
						
					} else {
						resultInventory = new RoomInventory();
					}
					
					
					// items (without weapons)
					onlyItemsStmt = conn.prepareStatement(
						"select itemTypes.item_id, itemTypes.name, itemTypes.description, inventories.item_quantity" +
								"	from inventories, itemTypes" +
								"	where inventories.inventory_source = ?" +
								"		and inventories.item_id = itemTypes.item_id" +
								"	and not exists (" +
								"		select 1" +
								"			from weaponTypes" +
								"			where inventories.item_id = weaponTypes.item_id)"
					);

					onlyItemsStmt.setInt(1, sourceID);
					onlyItemsResults = onlyItemsStmt.executeQuery();

					while (onlyItemsResults.next()) {
						Item resultItem = loadItem(onlyItemsResults, 1);
						resultInventory.AddItems(resultItem, onlyItemsResults.getInt(4));
					}
					
					
					// weapons (without base items)
					onlyWeaponsStmt = conn.prepareStatement(
						"select weaponTypes.item_id, itemTypes.name, itemTypes.description, weaponTypes.damage, inventories.item_quantity" +
						"		from inventories," +
						"			weaponTypes join itemTypes on itemTypes.item_id = weaponTypes.item_id" +
						"		where inventories.inventory_source = ?" +
						"			and inventories.item_id = weaponTypes.item_id"
					);

					onlyWeaponsStmt.setInt(1, sourceID);
					onlyWeaponsResults = onlyWeaponsStmt.executeQuery();
					
					while (onlyWeaponsResults.next()) {
						Weapon resultWeapon = loadWeapon(onlyWeaponsResults, 1);
						resultInventory.AddItems(resultWeapon, onlyWeaponsResults.getInt(5));
					}
					
					
					
					return resultInventory;
				} finally {
					
					DBUtil.closeQuietly(equippedWeaponsStmt);
					DBUtil.closeQuietly(equippedWeaponsResults);
					
					DBUtil.closeQuietly(onlyItemsStmt);
					DBUtil.closeQuietly(onlyItemsResults);
					
					DBUtil.closeQuietly(onlyWeaponsStmt);
					DBUtil.closeQuietly(onlyWeaponsResults);
				}
			}
		});
	}
	
	@Override
	public void UpdateInventoryBySourceID(Integer sourceID, Inventory updateInventory) {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement slotNamesStmt = null;
				ResultSet slotNamesResults = null;

				PreparedStatement databaseSlotsStmt = null;
				ResultSet databaseSlotsResults = null;
				
				PreparedStatement slotsUpdateStmt = null;
				PreparedStatement slotsInsertStmt = null;
				PreparedStatement slotsDeleteStmt = null;
				PreparedStatement slotsShiftStmt = null;
				
				PreparedStatement slotsLastStmt = null;
				ResultSet slotsLastResults = null;
					
				// update weapon slots (if updateInventory is an entityInventory)
				if (sourceID % 2 == 1) {
					try {
						slotNamesStmt = conn.prepareStatement(
							"select slotNames.slot_id, slotNames.slot_name from slotNames"
						);
						
						slotNamesResults = slotNamesStmt.executeQuery();
						
						while (slotNamesResults.next()) {
							Integer slotID = slotNamesResults.getInt(1);
							String slotName = slotNamesResults.getString(2);
							
							// determine existence combination (database and updateInventory)
							databaseSlotsStmt = conn.prepareStatement(
								"select weaponSlots.slot_id, weaponSlots.item_id" +
									"	from weaponSlots" +
									"	where weaponSlots.inventory_source = ?" +
									"		and weaponSlots.slot_id = ?"
							);
							
							databaseSlotsStmt.setInt(1, sourceID);
							databaseSlotsStmt.setInt(2, slotID);
						
							databaseSlotsResults = databaseSlotsStmt.executeQuery();
							
							Boolean existsInDatabase = databaseSlotsResults.next();
							Boolean existsInInventory = ((EntityInventory) updateInventory).WeaponSlotIsFull(slotName);
							
							Integer inventoryItemID = null;
							if (existsInInventory) inventoryItemID = ((EntityInventory) updateInventory).GetWeapon(slotName).GetID();
							
							Integer databaseSlotID = null;
							Integer databaseItemID = null;
							if (existsInDatabase) {
								databaseSlotID = databaseSlotsResults.getInt(1);
								databaseItemID = databaseSlotsResults.getInt(2);
							}
							
							
							// item isn't in database nor inventory (nothing to do, continue)
							if (!(existsInDatabase || existsInInventory)) continue;
							
							// both slots are full
							else if (existsInDatabase && existsInInventory) {
								if (inventoryItemID == databaseItemID) continue;
								
								// the database weapon is different than the inventory (update it)
								slotsUpdateStmt = conn.prepareStatement(
									"update weaponSlots" +
										"	set weaponSlots.item_id = ?" +
										"	where weaponSlots.inventory_source = ?" +
										"	and weaponSlots.slot_id = ?"
								);
									
								slotsUpdateStmt.setInt(1, inventoryItemID);
								slotsUpdateStmt.setInt(2, sourceID);
								slotsUpdateStmt.setInt(3, slotID);
								slotsUpdateStmt.executeUpdate();
							}
							
							
							// exists in inventory, but not database (insert it)
							else if (!existsInDatabase && existsInInventory) {
								slotsInsertStmt = conn.prepareStatement(
									"insert into weaponSlots (inventory_source, slot_id, item_id)" +
										"	values (?, ?, ?)"
								);
									
								slotsInsertStmt.setInt(1, sourceID);
								slotsInsertStmt.setInt(2, slotID);
								slotsInsertStmt.setInt(3, inventoryItemID);
								slotsInsertStmt.executeUpdate();
							}
							
							// exists in database, but not inventory (remove it)
							else if (existsInDatabase && !existsInInventory) {
								
								// get num items in all inventories
								slotsLastStmt = conn.prepareStatement(
									"select max(inventories.inventory_id) from inventories"
								);
								
								slotsLastResults = slotsLastStmt.executeQuery();
								
								Integer numSlots = null;
								if (slotsLastResults.next()) numSlots = slotsLastResults.getInt(1);
								
								
								// run through all items that are above and shift them down 1
								// this will overwrite the current item and place the duplicate into the last slot
								// which will then be removed from database
								for (int i=databaseSlotID; i<numSlots; i++) {
									slotsShiftStmt = conn.prepareStatement(
										"update weaponSlots" +
											"	set weaponSlots.inventory_source = (" +
											"		select slots.inventory_source" +
											"			from weaponSlots as slots" +
											"			where slots.slot_num = weaponSlots.slot_num + 1), " +
											
											"	weaponSlots.slot_id = (" +
											"		select slots.slot_id" +
											"			from weaponSlots as slots" +
											"			where slots.slot_num = weaponSlots.slot_num + 1), " +
											
											"	weaponSlots.item_id = (" +
											"		select slots.item_id" +
											"			from weaponSlots as slots" +
											"			where slots.slot_num = weaponSlots.slot_num + 1)" +
											"	where weaponSlots.slot_num = ?"
									);

									slotsShiftStmt.setInt(1, i);
									slotsShiftStmt.executeUpdate();
								}
								
								// delete the slot
								slotsDeleteStmt = conn.prepareStatement(
									"delete from weaponSlots" +
										"	where weaponSlots.slot_num = (" +
										"		select max(slots.slot_num) from weaponSlots as slots)"
								);
								
								slotsDeleteStmt.executeUpdate();
							}
						}
					} catch (Exception e) {
						throw e;
						
					} finally {
						DBUtil.closeQuietly(slotNamesStmt);
						DBUtil.closeQuietly(slotNamesResults);

						DBUtil.closeQuietly(databaseSlotsStmt);
						DBUtil.closeQuietly(databaseSlotsResults);
						
						DBUtil.closeQuietly(slotsUpdateStmt);
						DBUtil.closeQuietly(slotsInsertStmt);
						DBUtil.closeQuietly(slotsDeleteStmt);
						DBUtil.closeQuietly(slotsShiftStmt);
					}
				}
				
				
				// repeat for rest of inventory
				PreparedStatement invStmt = null;
				ResultSet invResults = null;

				PreparedStatement databaseInvStmt = null;
				ResultSet databaseInvResults = null;
				
				PreparedStatement invUpdateStmt = null;
				PreparedStatement invInsertStmt = null;
				PreparedStatement invDeleteStmt = null;
				PreparedStatement invShiftStmt = null;
				
				PreparedStatement lastInvStmt = null;
				ResultSet lastInvResults = null;
				
				try {
					// get all items
					invStmt = conn.prepareStatement(
						"select itemTypes.* from itemTypes"
					);
					
					invResults = invStmt.executeQuery();
					
					// run through all items 
					while (invResults.next()) {
						Item item = loadItem(invResults, 1);
						
						databaseInvStmt = conn.prepareStatement(
							"select inventories.inventory_id, inventories.item_quantity" +
								"	from inventories" +
								"	where inventories.inventory_source = ?" +
								"	and inventories.item_id = ?"
						);
						
						databaseInvStmt.setInt(1, sourceID);
						databaseInvStmt.setInt(2, item.GetID());
						databaseInvResults = databaseInvStmt.executeQuery();
						
						
						Boolean existsInDatabase = databaseInvResults.next();
						Boolean existsInInventory = updateInventory.ContainsItem(item);
						
						Integer inventoryItemQuantity = updateInventory.GetItemAmount(item);
						
						Integer databaseInventoryID = null;
						Integer databaseItemQuantity = null;
						if (existsInDatabase) {
							databaseInventoryID = databaseInvResults.getInt(1);
							databaseItemQuantity = databaseInvResults.getInt(2);
						}
						
						
						// item isn't in database nor inventory (nothing to do, continue)
						if (!existsInDatabase && !existsInInventory) continue;
						
						// both slots are full
						else if (existsInDatabase && existsInInventory) {
							
							// the database quantity is different than the inventory (update it)
							if (inventoryItemQuantity == databaseItemQuantity) continue;
							
							invUpdateStmt = conn.prepareStatement(
								"update inventories" +
									"	set inventories.item_quantity = ?" +
									"	where inventories.inventory_source = ?" +
									"		and inventories.item_id = ?"
							);
								
							invUpdateStmt.setInt(1, inventoryItemQuantity);
							invUpdateStmt.setInt(2, sourceID);
							invUpdateStmt.setInt(3, item.GetID());
							invUpdateStmt.executeUpdate();
						}
						
						// exists in inventory, but not database (insert it)
						else if (!existsInDatabase && existsInInventory) {
							
							invInsertStmt = conn.prepareStatement(
								"insert into inventories (inventory_source, item_id, item_quantity) values (?, ?, ?)"
							);
								
							invInsertStmt.setInt(1, sourceID);
							invInsertStmt.setInt(2, item.GetID());
							invInsertStmt.setInt(3, inventoryItemQuantity);
							invInsertStmt.executeUpdate();
						}
						
						// exists in database, but not inventory (remove it)
						else if (existsInDatabase && !existsInInventory) {
							
							// get num items in all inventories
							lastInvStmt = conn.prepareStatement(
								"select max(inventories.inventory_id) from inventories"
							);
							
							lastInvResults = lastInvStmt.executeQuery();
							
							Integer numInvs = null;
							if (lastInvResults.next()) numInvs = lastInvResults.getInt(1);
							
							
							// run through all items that are above and shift them down 1
							// this will overwrite the current item and place the duplicate into the last slot
							// which will then be removed from database
							for (int i=databaseInventoryID; i<numInvs; i++) {
								invShiftStmt = conn.prepareStatement(
									"update inventories" +
										"	set inventories.inventory_source = (" +
										"		select inv.inventory_source" +
										"			from inventories as inv" +
										"			where inv.inventory_id = inventories.inventory_id + 1), " +
										
										"	inventories.item_id = (" +
										"		select inv.item_id" +
										"			from inventories as inv" +
										"			where inv.inventory_id = inventories.inventory_id + 1), " +
										
										"	inventories.item_quantity = (" +
										"		select inv.item_quantity" +
										"			from inventories as inv" +
										"			where inv.inventory_id = inventories.inventory_id + 1)" +
										"	where inventories.inventory_id = ?"
								);

								invShiftStmt.setInt(1, i);
								invShiftStmt.executeUpdate();
							}
							
							// delete the slot
							invDeleteStmt = conn.prepareStatement(
								"delete from inventories" +
									"	where inventories.inventory_id = (" +
									"		select max(inv.inventory_id) from inventories as inv)"
							);
							
							invDeleteStmt.executeUpdate();
						}
					}
				} catch (Exception e) {
					throw e;
				
				} finally {
					DBUtil.closeQuietly(invStmt);
					DBUtil.closeQuietly(invResults);

					DBUtil.closeQuietly(databaseInvStmt);
					DBUtil.closeQuietly(databaseInvResults);
					
					DBUtil.closeQuietly(invUpdateStmt);
					DBUtil.closeQuietly(invInsertStmt);
					DBUtil.closeQuietly(invDeleteStmt);
					DBUtil.closeQuietly(invShiftStmt);
				}
				
				return true;
			}
		});
	}
	
	// END OF QUERIES / INSERTS

	
	// OUR CLASS LOADING METHODS GO HERE
	private Item loadItem(ResultSet resultSet, int index) throws SQLException {
		return new Item(
			resultSet.getInt(index++),
			resultSet.getString(index++),
			resultSet.getString(index++)
		);
	}
	
	private Weapon loadWeapon(ResultSet resultSet, int index) throws SQLException {
		return new Weapon(
				resultSet.getInt(index++),
				resultSet.getString(index++),
				resultSet.getString(index++),
				resultSet.getDouble(index++)
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
		
		EntityInventory playerInventory = this.GetPlayerInventory();
		toOut.setInventory(playerInventory);
		
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
		
		EntityInventory enemyInventory = this.GetEnemyInvetoryByID(1 + (resultSet.getInt(1) << 1));// im assuming 1 is the enemy id
		toOut.setInventory(enemyInventory);
		
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
							"       generated always as identity (start with 1, increment by 1), " + 
							"	inventory_source int, " + 
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
				
				
				
				// SLOT NAMES
				PreparedStatement slotNamesStmt = conn.prepareStatement(
					"create table slotNames (" +
							"   slot_id int primary key" +
							"       generated always as identity (start with 1, increment by 1), " + 
							"	slot_name varchar(16)" +
					")"
				);
				
				
				try {
					slotNamesStmt.executeUpdate();
					
				} catch (SQLException sql) {
					if (sql.getMessage().matches("Table/View '.*' already exists in Schema 'APP'.")) 
						isNewDatabase = false;
					else throw sql;
					
				} catch (Exception e) {
					throw e;
					
				} finally {
					DBUtil.closeQuietly(slotNamesStmt);
				}
				
				
				
				// WEAPON SLOTS
				PreparedStatement weaponSlotsStmt = conn.prepareStatement(
					"create table weaponSlots (" +
							"   slot_num int primary key" +
							"       generated always as identity (start with 1, increment by 1), " + 
							"	inventory_source int, " +
							"   slot_id int, " +
							"		constraint slotID foreign key (slot_id) references slotNames(slot_id), " +
							"	item_id int, " + 
							"		constraint weaponID foreign key (item_id) references itemTypes(item_id)" +
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
				List<String> slotList;
				List<Item> itemList;
				List<Weapon> weaponList;
				Map<Integer, Inventory> inventoryMap;
				PlayerModel player;
				List<EnemyModel> enemies;
				
				try {
					slotList = InitialData.getSlotNames();
					itemList = InitialData.getItemTypes();
					weaponList = InitialData.getWeaponTypes();
					inventoryMap = InitialData.getInventories();

					player = InitialData.getPlayer();
					enemies = InitialData.getEnemies();
					
				} catch (IOException e) {
					throw new SQLException("Couldn't read initial data", e);
				}

				
				// if database already exists, reset it first
				if (!isNewDatabase) {
					resetTable("weaponSlots", "slot_num", 1);// reset dependencies first (inventory_source)
					resetTable("slotNames", "slot_id", 1);// reset dependencies first (slot_id)
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
				
				
				
				// INSERT (WEAPON) SLOT NAMES
				PreparedStatement insertSlot = conn.prepareStatement(
					"insert into slotNames (slot_name) values (?)"
				);
				
				try {
					for (String name : slotList) {
						insertSlot.setString(1, name);
						insertSlot.addBatch();
					}

					insertSlot.executeBatch();
					
				} catch(Exception e) { 
					throw e;
					
				} finally {
					DBUtil.closeQuietly(insertSlot);
				}
				
				
				
				// INSERT INVENTORIES
				PreparedStatement insertInventory = conn.prepareStatement(
						"insert into inventories (inventory_source, item_id, item_quantity) values (?, ?, ?)");
				
				PreparedStatement insertWeaponSlot = conn.prepareStatement(
						"insert into weaponSlots (inventory_source, slot_id, item_id) values (?, ?, ?)");
				
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
								insertWeaponSlot.setInt(2, slotList.indexOf(slot) + 1);
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
							"select entities.* " +
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
	public EntityInventory GetPlayerInventory() {
		return (EntityInventory) this.InventoryBySourceID(2);
	}

	@Override
	public void UpdatePlayerInventory(EntityInventory playerInventory) {
		this.UpdateInventoryBySourceID(2, playerInventory);
	}
	
	@Override
	public EntityInventory GetEnemyInvetoryByID(Integer enemyID) {
		return (EntityInventory) this.InventoryBySourceID((enemyID << 1) + 1);
	}

	@Override
	public RoomInventory GetRoomInventoryByID(Integer roomID) {
		return (RoomInventory) this.InventoryBySourceID(roomID << 1);
	}

	@Override
	public void UpdateEnemyInventory(Integer enemyID, EntityInventory enemyInventory) {
		this.UpdateInventoryBySourceID((enemyID << 1) + 1, enemyInventory);
	}

	@Override
	public void UpdateRoomInventory(Integer roomID, RoomInventory roomInventory) {
		this.UpdateInventoryBySourceID(roomID << 1, roomInventory);
	}
}
