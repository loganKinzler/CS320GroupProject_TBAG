package edu.ycp.cs320.TBAG.tbagdb.persist;

import java.io.IOException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.ycp.cs320.TBAG.model.EnemyModel;
import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.PlayerModel;
import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.Weapon;
import edu.ycp.cs320.TBAG.model.Inventory;
import edu.ycp.cs320.TBAG.model.RoomInventory;
import edu.ycp.cs320.TBAG.model.EntityInventory;

import edu.ycp.cs320.TBAG.comparator.ItemByIDComparator;

public class InitialData {
	private static List<Item> itemTypes;
	private static List<Weapon> weaponTypes;
	
	public static List<Item> getItemTypes() throws IOException {
		InitialData.itemTypes = new ArrayList<Item>();
		ReadCSV readItemTypes = new ReadCSV("itemTypes.csv");
		
		try {
			Integer itemID = 0;
			
			while (true) {
				List<String> tuple = readItemTypes.next();
				if (tuple == null) break;
				itemID++;
				
				Iterator<String> i = tuple.iterator();
				
				Item item = new Item(itemID, i.next(),i.next());
				InitialData.itemTypes.add(item);
			}
		} finally {
			readItemTypes.close();
		}
		
		Collections.sort(InitialData.itemTypes, new ItemByIDComparator());
		return InitialData.itemTypes;
	}
	
	public static List<Weapon> getWeaponTypes() throws IOException {
		if (InitialData.itemTypes == null) InitialData.getItemTypes();
		
		InitialData.weaponTypes = new ArrayList<Weapon>();
		ReadCSV readWeaponTypes = new ReadCSV("weaponTypes.csv");
		
		try {
			while (true) {
				List<String> tuple = readWeaponTypes.next();
				if (tuple == null) break;
				
				Iterator<String> i = tuple.iterator();
				
				Integer weaponID = Integer.parseInt(i.next());

				Item weaponItem = InitialData.itemTypes.get(weaponID - 1);

				
				Weapon weapon = new Weapon(
						weaponID,
						weaponItem.GetName(),
						weaponItem.GetDescription(),
						Double.parseDouble(i.next()));
				

				InitialData.weaponTypes.add(weapon);

			}	
		} finally {
			readWeaponTypes.close();
		}

		return InitialData.weaponTypes;
	}
	
	public static Map<Integer, Inventory> getInventories() throws IOException {
		if (InitialData.itemTypes == null) InitialData.getItemTypes();
		if (InitialData.weaponTypes == null) InitialData.getWeaponTypes();
		
		Map<Integer, Inventory> inventories = new HashMap<Integer, Inventory>();
		ReadCSV readInventories = new ReadCSV("inventories.csv");
		ReadCSV readWeaponSlots = new ReadCSV("weaponSlots.csv");
		
		try {
			while (true) {
				List<String> tuple = readInventories.next();
				if (tuple == null) break;
				
				Iterator<String> i = tuple.iterator();
				Integer inventorySource = Integer.parseInt(i.next());
				
				// add new inventory
				if (inventories.get(inventorySource) == null) {
					
					// even sources are rooms, odd are entities
					if (inventorySource % 2 == 0) {
						inventories.put(inventorySource, new RoomInventory());
					} else {
						inventories.put(inventorySource, new EntityInventory());
					}
				}
				
				inventories.get(inventorySource).AddItem(InitialData.itemTypes.get(Integer.parseInt(i.next()) - 1));
			}
			
			while (true) {
				List<String> tuple = readWeaponSlots.next();
				if (tuple == null) break;
				
				Iterator<String> i = tuple.iterator();
				Integer inventorySource = 1 + (Integer.parseInt(i.next()) << 1);			
				EntityInventory slotInventory = (EntityInventory) inventories.get(inventorySource);

				// if inventory doesn't exist
				if (slotInventory == null) {
					slotInventory = new EntityInventory();
					inventories.put(inventorySource, slotInventory);
				}
				
				slotInventory.EquipWeapon(
						i.next(),
						weaponTypes.get(Integer.parseInt(i.next()) - 1));
			}
		} finally {
			readInventories.close();
			readWeaponSlots.close();
		}
		
		return inventories;
	}
	
	public static PlayerModel getPlayer() throws IOException {
		PlayerModel toOut = new PlayerModel(100,3,1);
		ReadCSV readPlayer = new ReadCSV("entities.csv");
		
		try {
			readPlayer.next();
			List<String> tuple = readPlayer.next();
			
			Iterator<String> i = tuple.iterator();
			
			Double health = Double.parseDouble(i.next());
			Double maxHealth = Double.parseDouble(i.next());
			Integer lives = Integer.parseInt(i.next());
			Integer currentRoom = Integer.parseInt(i.next());
			
			PlayerModel player = new PlayerModel(maxHealth, lives, currentRoom);
			player.setHealth(health);
			toOut = player;
			
		}
		finally {
			readPlayer.close();
		}
		
		
		return toOut;
	}
	
	public static List<EnemyModel> getEnemies() throws IOException {
		List<EnemyModel> enemies = new ArrayList<>();

		ReadCSV readEnemies = new ReadCSV("entities.csv");

		
		
		try {

			readEnemies.next();
			readEnemies.next();
			
			while (true) {
				List<String> tuple = readEnemies.next();
				
				if (tuple == null) break;
				
				Iterator<String> i = tuple.iterator();
				
				Double health = Double.parseDouble(i.next());
				Double maxHealth = Double.parseDouble(i.next());
				Integer lives = Integer.parseInt(i.next());
				Integer currentRoom = Integer.parseInt(i.next());
				String name = i.next();
				String desc = i.next();
				
				EnemyModel enemy = new EnemyModel(maxHealth, 1, currentRoom, name, desc);
				enemy.setHealth(health);
				
				enemies.add(enemy);
			}
			
		}
		finally {
			readEnemies.close();
		}
		
		
		return enemies;
	}
	
	public static List<Room> getRooms() throws IOException{
		List<Room> rooms = new ArrayList<>();
		ReadCSV readRooms = new ReadCSV("rooms.csv"); 
		
		try {
			
			
			while (true) {
				List<String> tuple = readRooms.next();
				
				if(tuple == null) break;
				
				Iterator<String> i = tuple.iterator();
				
				
				String name = i.next();
				String description = i.next();
				
				Room room = new Room(name, description);
				rooms.add(room);
			}
		}
		
		finally {
			readRooms.close();
		}
		
		return rooms;
		
	}
}
