package edu.ycp.cs320.TBAG.tbagdb.persist;

import java.io.IOException;
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

public class InitialData {
	private static List<Item> itemTypes;
	
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

		return InitialData.itemTypes;
	}
	
	public static Map<Weapon, Integer> getWeaponTypes() throws IOException {
		if (InitialData.itemTypes == null) InitialData.getItemTypes();
		
		Map<Weapon, Integer> weaponTypesMap = new HashMap<Weapon, Integer>();
		ReadCSV readWeaponTypes = new ReadCSV("weaponTypes.csv");
		
		try {
			while (true) {
				List<String> tuple = readWeaponTypes.next();
				if (tuple == null) break;
				
				Iterator<String> i = tuple.iterator();
				
				Integer weaponID = Integer.parseInt(i.next());
				Item weaponItem = itemTypes.get(weaponID - 1);
				
				Weapon weapon = new Weapon(
						weaponID,
						weaponItem.GetName(),
						weaponItem.GetDescription(),
						Double.parseDouble(i.next()));
				
				weaponTypesMap.put(weapon, weaponID + 1);
			}	
		} finally {
			readWeaponTypes.close();
		}

		return weaponTypesMap;
	}
	
	public static PlayerModel getPlayer() throws IOException {
		PlayerModel toOut = new PlayerModel(100,3,1);
		ReadCSV readPlayer = new ReadCSV("entityTypes.csv");
		
		try {
			List<String> tuple = readPlayer.next();
			
			Iterator<String> i = tuple.iterator();
			
			Double health = Double.parseDouble(i.next());
			Double maxHealth = Double.parseDouble(i.next());
			Integer lives = Integer.parseInt(i.next());
			Integer currentRoom = Integer.parseInt(i.next());
			
			toOut = new PlayerModel(health, lives, currentRoom);
			
		}
		finally {
			readPlayer.close();
		}
		
		
		return toOut;
	}
	
	public static List<EnemyModel> getEnemies() throws IOException {
		List<EnemyModel> enemies = new ArrayList<>();
		ReadCSV readEnemies = new ReadCSV("entityTypes.csv");
		
		try {
			
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
				
				EnemyModel enemy = new EnemyModel(health, 1, currentRoom, name, desc);
				
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
			readRooms.next();
			
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
