package edu.ycp.cs320.TBAG.tbagdb.persist;

import java.io.File;
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
import edu.ycp.cs320.TBAG.model.Inventory;
import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.PlayerModel;
import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.RoomInventory;
import edu.ycp.cs320.TBAG.model.Weapon;

public class DerbyDatabase implements IDatabase {
	private String dbType;
	
	
	static {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (Exception e) {
			throw new IllegalStateException("Could not load Derby driver");
		}
	}
	
	// constructor
	public DerbyDatabase(String dbType) {
		this.dbType = dbType;
	    if (this.dbExists("test")) {
	        // Verify tables exist
	        if (!verifyTablesExist()) {
	            create(); // Recreate if tables are missing
	        }
	        return;
	    }
	    this.create();
	}

	private boolean verifyTablesExist() {
	    try {
	        // Try a simple query against one of the tables
	        getFoundCommands();
	        return true;
	    } catch (Exception e) {
	        return false;
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
					
//					List<Item> result = new ArrayList<Item>();
					
					resultSet = stmt.executeQuery();
					
					// for testing that a result was returned
					Boolean found = false;
					
					if (resultSet.next()) {
						found = true;
						
						conn.commit();
						return resultSet.getInt(1);
					}
					
					if (!found) {
						System.out.println("<" + itemName + "> was not found in the items table");
					}
				
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
							"select * from rooms order by room_id asc"
					);
					
					
					List<Room> result = new ArrayList<Room>();
					
					resultSet = stmt.executeQuery();
					
					// for testing that a result was returned
					Boolean found = false;
					
					while (resultSet.next()) {
						found = true;
						
						RoomInventory roomInventory = GetRoomInventoryByID(resultSet.getInt(1));
						
						// create new Room object
						// retrieve attributes from resultSet starting with index 1
						Room room = loadRoom(resultSet);
						room.setRoomInventory(roomInventory);
						
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
							"select * from connections order by room_id asc"
					);
					
					
					List<Room> result = new ArrayList<Room>();
					
					resultSet = stmt.executeQuery();
					
					// for testing that a result was returned
					Boolean found = false;
					
					while (resultSet.next()) {
						found = true;
						
						
						// create new Room object
						// retrieve attributes from resultSet starting with index 1
						Room room = loadConnection(resultSet);
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
	
	@Override
	public Integer UpdateEnteredRoom(boolean entered, int id) {
		return executeTransaction(new Transaction<Integer>() {
			public Integer execute(Connection conn) throws SQLException {
				PreparedStatement insertStatement = null;
				
				insertStatement = conn.prepareStatement(
					"update rooms "
					+ "set has_entered_room = ? "
					+ "where rooms.room_id = ?"
				);
				
				insertStatement.setBoolean(1, entered);
				insertStatement.setInt(2, id);
				
				try {
					insertStatement.executeUpdate();
					conn.commit();
				}
				finally {
					DBUtil.closeQuietly(insertStatement);
				}
				
				return id;
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
	
	public Room getConnectionsByRoomId(int id) {
		return executeTransaction(new Transaction<Room>() {
			@Override
			public Room execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				Room toOut = new Room();
				
				try {
					// Get all rooms with the same room id as id
					stmt = conn.prepareStatement(
							"select connections.* " +
							"from connections " + 
							" where connections.room_id = ?"
					);
					stmt.setInt(1, id);
					
//					List<Room> result = new ArrayList<Room>();
					
					resultSet = stmt.executeQuery();
					
					// for testing that a result was returned
					Boolean found = false;
					
					while (resultSet.next()) {
						found = true;
						toOut = loadConnection(resultSet);
					}
					
//					System.out.print(result);
					
					// check if a room with the id was found
					if(!found) {
						System.out.println("<" + id + "> was not found in the rooms table");
						return null;
					}
					
					return toOut;
					
					
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
				
//				List<Item> result = new ArrayList<Item>();
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
	
	//Entities~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	@Override
	public PlayerModel GetPlayer() {
		return executeTransaction(new Transaction<PlayerModel>() {
			@Override
			public PlayerModel execute(Connection conn) throws SQLException {
				PreparedStatement getPlayerStatement = null;
				ResultSet resultSet = null;
				
				try {
					// Retrieve all attributes from both Books and Authors tables
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
							"select entities.* from entities" +
							" where entities.id > 1 " +
							" and entities.currentRoom = ?"
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
	public EntityInventory GetPlayerInventory() {
		return (EntityInventory) this.InventoryBySourceID(3);
	}

	@Override
	public void UpdatePlayerInventory(EntityInventory playerInventory) {
		this.UpdateInventoryBySourceID(2, playerInventory);
	}
	
	@Override
	public EntityInventory GetEnemyInventoryByID(Integer enemyID) {
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
	
	//GameHistory~~~~~~
	
	@Override
	public List<String> getGameHistory() {
		return executeTransaction(new Transaction<List<String>>() {
			@Override
			public List<String> execute(Connection conn) throws SQLException {
				PreparedStatement getGameHistoryStatement = null;
				ResultSet resultSet = null;
				List<String> history = new ArrayList<>();
				
				try {
					// retreive all attributes from both Books and Authors tables
					getGameHistoryStatement = conn.prepareStatement(
							"select GameHistory.* from GameHistory"
					);
					
					
					resultSet = getGameHistoryStatement.executeQuery();
					
					// for testing that a result was returned
					Boolean found = false;
					
					while (resultSet.next()) {
						found = true;
						
						history.add(resultSet.getString(1));
					}
					
					// check if the title was found
					if (!found) {
						System.out.println("Line was not found in the GameHistory table");
					}
					
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(getGameHistoryStatement);
				}

				return history;
			}
		});
	}
	public String addToGameHistory(String toAdd) {
		return executeTransaction(new Transaction<String>() {
			public String execute(Connection conn) throws SQLException {
				PreparedStatement insertStatement = null;
				
				insertStatement = conn.prepareStatement(
					"insert into GameHistory(printout) "
					+ "values(?)"
				);

				insertStatement.setString(1, toAdd);
				
				try {
					insertStatement.executeUpdate();
					conn.commit();
				}
				finally {
					DBUtil.closeQuietly(insertStatement);
				}
				
				return toAdd;
			}
		});
	}
	
	public Boolean clearGameHistory() {
		return executeTransaction(new Transaction<Boolean>() {
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement clearStatement = null;
				
				clearStatement = conn.prepareStatement(
					"delete from GameHistory"
				);
				
				try {
					clearStatement.executeUpdate();
					conn.commit();
				}
				finally {
					DBUtil.closeQuietly(clearStatement);
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
	        resultSet.getInt(index++),    // itemID
	        resultSet.getString(index++), // name
	        resultSet.getString(index++), // description
	        resultSet.getDouble(index++), // damage
	        resultSet.getString(index++), // damage_type
	        resultSet.getInt(index++),    // crit_chance
	        resultSet.getString(index++)  // status_effect
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
		
		EntityInventory enemyInventory = this.GetEnemyInventoryByID(1 + (resultSet.getInt(1) << 1));
		toOut.setInventory(enemyInventory);
		
		return toOut;
	}
	
	private Room loadRoom(ResultSet resultSet) throws SQLException {
		int index = 1;
		int id = resultSet.getInt(index++);
		String name = resultSet.getString(index++);
		String description = resultSet.getString(index++);
		int x_position = resultSet.getInt(index++);
		int y_position = resultSet.getInt(index++);
		boolean has_entered_room = resultSet.getBoolean(index++);
		Room toOut = new Room(id, name, description, x_position, y_position, has_entered_room);
		
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

	//TODO: add support for multiple databases
	//Will likely need to have a field in the class for the type because connect is done every db method and i digress against having the db type as a param for every call
	private Connection connect() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:derby:" + this.dbType + ".db;create=true");
		
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
				        "   weapon_id int primary key" + 
				        "       generated always as identity (start with 1, increment by 1), " + 
				        "   item_id int unique, " + 
				        "       constraint item_id foreign key (item_id) references itemTypes(item_id), " + 
				        "   damage double, " +
				        "   damage_type VARCHAR(50), " +  
				        "   crit_chance INT, " +         
				        "   status_effect VARCHAR(50)" + 
				    ")"
				);

				try {
				    weaponStmt.executeUpdate();
				    
				} catch (SQLException sql) {
				    if (sql.getMessage().matches("Table/View '.*' already exists in Schema 'APP'.")) {
				        isNewDatabase = false;
				        // Table exists, so we need to add columns separately
				        }
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
				
				
				// ROOMS
				PreparedStatement roomsStatement = conn.prepareStatement(
						"Create table rooms ("
						+ "room_id int primary key"
						+ " generated always as identity (start with 1, increment by 1), "
						+ "name varchar(16), "
						+ "description varchar(64), "
						+ "x_position int, "
						+ "y_position int, "
						+ "has_entered_room boolean"
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
					" printout varchar(32672))"
					);
					historystmt.executeUpdate();
				} catch (SQLException sql){
				if(sql.getMessage().matches("Table/View '.*' already exists in Schema 'APP'.")) {
					isNewDatabase = false;
				}
				}catch (Exception e){
				throw e;
				}finally{
					DBUtil.closeQuietly(historystmt);
				}
				
				
				//GameHistory
				PreparedStatement foundCommandstmt = null;
				try {
				    foundCommandstmt = conn.prepareStatement(
				        "create table FoundCommands (" +
				        "   CommandName varchar(32672)" +
				        ")"
				    );
				    foundCommandstmt.executeUpdate();
				} catch (SQLException sql) {
				    if (!sql.getMessage().matches("Table/View '.*' already exists in Schema 'APP'.")) {
				        throw sql; // Only ignore "already exists" errors
				    }
				    isNewDatabase = false;
				} finally {
				    DBUtil.closeQuietly(foundCommandstmt);
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
				List<String> slotList;
				List<Item> itemList;
				List<Weapon> weaponList;
				Map<Integer, Inventory> inventoryMap;
				PlayerModel player;
				List<EnemyModel> enemies;
				List<Room> rooms;
				List<Room> connections;
				
				try {
					slotList = InitialData.getSlotNames();
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
					resetTable("weaponSlots", "slot_num", 2);// reset dependencies first (inventory_source)
					resetTable("slotNames", "slot_id", 1);// reset dependencies first (slot_id)
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
				

				PreparedStatement insertRoomStatement = conn.prepareStatement(
						"insert into rooms (name, description, x_position, y_position, has_entered_room) values (?, ?, ?, ?, ?)");
						
				
				for(Room room : rooms) {
					insertRoomStatement.setString(1, room.getShortRoomDescription());
					insertRoomStatement.setString(2, room.getLongRoomDescription());
					insertRoomStatement.setInt(3, room.getX_Position());
					insertRoomStatement.setInt(4, room.getY_Position());
					insertRoomStatement.setBoolean(5, room.getHas_Entered_Room());
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
//		System.out.println("Creating tables...");
		Boolean isNewDatabase = createTables();
		System.out.println(isNewDatabase);
		
//		System.out.println("Loading initial data...");
		loadInitialData(isNewDatabase);
		
//		System.out.println("Success!");
	}

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
		
	public boolean dbExists(String type) {
		boolean exists = false;
		
		File dbFolder = new File("" + type + ".db");
		
		if (dbFolder.exists() && dbFolder.isDirectory()) {
			System.out.println("Database of type <" + type + "> was found.");
			exists = true;
		}
		else {
			System.out.println("Database of type <" + type + "> was not found.");
		}
		
		return exists;
	}
	
	
	public List<String> getFoundCommands() {
		return executeTransaction(new Transaction<List<String>>() {
			@Override
			public List<String> execute(Connection conn) throws SQLException {
				PreparedStatement getFoundStmt = null;
				ResultSet resultSet = null;
				List<String> foundCommands = new ArrayList<>();
				
				try {
					// retreive all attributes from both Books and Authors tables
					getFoundStmt = conn.prepareStatement(
							"select FoundCommands.* from FoundCommands"
					);
					
					
					resultSet = getFoundStmt.executeQuery();
					
					// for testing that a result was returned
					Boolean found = false;
					
					while (resultSet.next()) {
						found = true;
						
						foundCommands.add(resultSet.getString(1));
					}
					
					// check if the title was found
					if (!found) {
						System.out.println("Line was not found in the GameHistory table");
					}
					
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(getFoundStmt);
				}

				return foundCommands;
			}
		});
	}
	
	public String addToFound(String toAdd) {
		return executeTransaction(new Transaction<String>() {
			public String execute(Connection conn) throws SQLException {
				PreparedStatement insertStatement = null;
				
				insertStatement = conn.prepareStatement(
					"insert into FoundCommands(CommandName) "
					+ "values(?)"
				);

				insertStatement.setString(1, toAdd);
				
				try {
					insertStatement.executeUpdate();
					conn.commit();
				}
				finally {
					DBUtil.closeQuietly(insertStatement);
				}
				
				return toAdd;
			}
		});
	}
	
	public Boolean checkFound(String toCheck) {
	    return executeTransaction(new Transaction<Boolean>() {
	        @Override
	        public Boolean execute(Connection conn) throws SQLException {
	            PreparedStatement checkStatement = null;
	            ResultSet resultSet = null;
	            
	            try {
	                // Prepare SQL query to check if the command exists
	                checkStatement = conn.prepareStatement(
	                    "SELECT 1 FROM FoundCommands WHERE CommandName = ?"
	                );
	                checkStatement.setString(1, toCheck);
	                
	                resultSet = checkStatement.executeQuery();
	                
	                // If there's at least one row, the command exists
	                return resultSet.next();
	                
	            } finally {
	                DBUtil.closeQuietly(resultSet);
	                DBUtil.closeQuietly(checkStatement);
	            }
	        }
	    });
	}
	
	
}
