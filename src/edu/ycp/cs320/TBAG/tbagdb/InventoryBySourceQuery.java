package edu.ycp.cs320.TBAG.tbagdb;

import java.util.HashMap;
import java.util.Scanner;

import edu.ycp.cs320.TBAG.tbagdb.persist.IDatabase;
import edu.ycp.cs320.TBAG.tbagdb.persist.DerbyDatabase;

import edu.ycp.cs320.TBAG.model.Inventory;
import edu.ycp.cs320.TBAG.model.EntityInventory;

import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.Weapon;


public class InventoryBySourceQuery {
	private static IDatabase database;
	
	public InventoryBySourceQuery(IDatabase db) {
		InventoryBySourceQuery.database = db;
	}
	
	public static Inventory query(Integer inventorySource) {
		return database.InventoryBySourceID(inventorySource);
	}
	
	public static void main(String[] args) throws Exception {
		Scanner keyboard = new Scanner(System.in);
		InventoryBySourceQuery.database = new DerbyDatabase();
		
		System.out.print("What kind of inventory? <Room: 0, Entity: 1> ");
		Integer offset = keyboard.nextInt() % 2;
		
		System.out.print("What is the source index? ");
		Integer source = keyboard.nextInt();
		
		Inventory resultInventory = query(offset + (source<<1));
		
		// print inventory
		HashMap<Item, Integer> items = resultInventory.GetItems();
		String systemResponse = String.format("Describing inventory...\n\n");
		
		// entity inventory
		if (offset == 1) {
			HashMap<String, Weapon> equips = ((EntityInventory) resultInventory).GetWeaponsAsSlots();
			
			if (items.size() == 0 && equips.size() == 0) {
				systemResponse += String.format("There are no items in your inventory.");
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
			systemResponse += String.format("There are no items in your inventory.");
			return;
		}
		
		// print room inventory
		for (Item item : items.keySet())
			systemResponse += String.format("\n\n%s: %d\n - %s",
					item.GetName(), items.get(item),
					item.GetDescription());
		
		System.out.println(systemResponse);
	}
}
