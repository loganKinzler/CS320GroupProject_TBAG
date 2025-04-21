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
import edu.ycp.cs320.TBAG.model.Weapon;

public class InitialData {
	private static List<Item> itemTypes;
	
	public static List<Item> getItemTypes() throws IOException {
		InitialData.itemTypes = new ArrayList<Item>();
		ReadCSV readItemTypes = new ReadCSV("itemTypes.csv");
		
		try {
			while (true) {
				List<String> tuple = readItemTypes.next();
				if (tuple == null) break;
				
				Iterator<String> i = tuple.iterator();
				
				Item item = new Item(i.next(),i.next());
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
				
				Integer weaponItemIndex = Integer.parseInt(i.next()) - 1;
				Item weaponItem = itemTypes.get(weaponItemIndex);
				System.out.println(weaponItemIndex + 1);
				
				Weapon weapon = new Weapon(
						weaponItem.GetName(),
						weaponItem.GetDescription(),
						Double.parseDouble(i.next()));
				
				weaponTypesMap.put(weapon, weaponItemIndex + 1);
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
}
