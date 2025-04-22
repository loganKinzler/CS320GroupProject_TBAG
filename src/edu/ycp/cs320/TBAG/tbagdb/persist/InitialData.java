package edu.ycp.cs320.TBAG.tbagdb.persist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.Weapon;
import edu.ycp.cs320.TBAG.model.Inventory;

import edu.ycp.cs320.TBAG.comparator.ItemByIDComparator;

import edu.ycp.cs320.TBAG.tbagdb.persist.ReadCSV;

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
		
		Collections.sort(InitialData.itemTypes, new ItemByIDComparator());
		return InitialData.itemTypes;
	}
	
	public static List<Weapon> getWeaponTypes() throws IOException {
		if (InitialData.itemTypes == null) InitialData.getItemTypes();
		
		List<Weapon> weaponTypes = new ArrayList<Weapon>();
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
				
				weaponTypes.add(weapon);
			}	
		} finally {
			readWeaponTypes.close();
		}

		return weaponTypes;
	}
	
	public static Map<Integer, Inventory> getInventories() throws IOException {
		Map<Integer, Inventory> inventories = new HashMap<Integer, Inventory>();
		Map<Integer, Map<String, Weapon>> weaponSlots = new HashMap<Integer, Map<String, Weapon>>();
		
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
					inventories.put(inventorySource, new Inventory());
				}
				
			}	
		} finally {
			readInventories.close();
		}
		
		
		return inventories;
	}
}
