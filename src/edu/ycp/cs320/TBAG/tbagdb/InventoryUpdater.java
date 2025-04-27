package edu.ycp.cs320.TBAG.tbagdb;

import java.util.HashMap;
import java.util.Scanner;

import edu.ycp.cs320.TBAG.tbagdb.InventoryBySourceQuery;

import edu.ycp.cs320.TBAG.tbagdb.persist.IDatabase;
import edu.ycp.cs320.TBAG.tbagdb.persist.DerbyDatabase;

import edu.ycp.cs320.TBAG.model.Inventory;
import edu.ycp.cs320.TBAG.model.RoomInventory;
import edu.ycp.cs320.TBAG.model.EntityInventory;

import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.Weapon;


public class InventoryUpdater {
	private static IDatabase database;
	
	public InventoryUpdater(IDatabase db) {
		InventoryUpdater.database = db;
	}
	
	public static void update(Integer inventorySource, Inventory updateInventory) {
		database.UpdateInventoryBySourceID(inventorySource, updateInventory);
	}
	
	public static void main(String[] args) throws Exception {
		InventoryUpdater.database = new DerbyDatabase();
		System.out.println("Updating Inventory...");
		
		Inventory testUpdateInventory = new EntityInventory();		
		((EntityInventory) testUpdateInventory).EquipWeapon("Left Hand", new Weapon(4, "Steel Cutlass", "Swing!", 7.5));
		
//		testUpdateInventory.AddItems(new Item(1, "Shovel", "Don't dig straight down."), 2);
		testUpdateInventory.AddItems(new Item(2, "Moss", "It's light and moist."), 1);
		testUpdateInventory.AddItems(new Item(3, "Golden Key", "It opens golden doors."), 4);
		testUpdateInventory.AddItems(new Item(4, "Steel Cutlass", "Swing!"), 4);
		
		Integer offset = 1;
		Integer source = 1;
		Integer sourceID = offset + (source<<1);
		
		InventoryUpdater.update(sourceID, testUpdateInventory);
		System.out.println("Success!\n");
		
		Inventory resultInventory = database.InventoryBySourceID(sourceID);
		
		
		// print inventory
		HashMap<Item, Integer> items = resultInventory.GetItems();
		String systemResponse = String.format("Describing inventory...");
		
		// entity inventory
		if (offset == 1) {
			HashMap<String, Weapon> equips = ((EntityInventory) resultInventory).GetWeaponsAsSlots();
			
			if (items.size() == 0 && equips.size() == 0) {
				systemResponse += String.format("\nThere are no items in your inventory.");
				
				System.out.println(systemResponse);
				System.out.println("\nFinished printing updated inventory!");
				return;
			}
			
			systemResponse += String.format("Items in the inventory:");			
			for (String slot : equips.keySet()) 
				systemResponse += String.format("\n\n%s: %s\n - %s",
						slot, equips.get(slot).GetName(),
						equips.get(slot).GetDescription());
		}
		
		// no items in room inventory
		if (items.size() == 0) {
			systemResponse += String.format("\nThere are no items in your inventory.");
			
			System.out.println(systemResponse);
			System.out.println("\nFinished printing updated inventory!");
			return;
		}
		
		// print room inventory
		for (Item item : items.keySet())
			systemResponse += String.format("\n\n%s: %d\n - %s",
					item.GetName(), items.get(item),
					item.GetDescription());
		
		System.out.println(systemResponse);
		System.out.println("\nFinished printing updated inventory!");
		return;
	}
}
