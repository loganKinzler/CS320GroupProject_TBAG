package edu.ycp.cs320.TBAG.tbagdb;

import java.util.List;
import java.util.Scanner;

import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.tbagdb.persist.IDatabase;
import edu.ycp.cs320.TBAG.tbagdb.persist.DerbyDatabase;

public class ItemsByNameQuery {
	private static IDatabase database;
	
	public ItemsByNameQuery(IDatabase db) {
		database = db;
	}
	
	public static List<Item> queryDatabase(String itemName) {
		return database.ItemsByNameQuery(itemName);
	}
	
	public static void main(String[] args) throws Exception {
		Scanner keyboard = new Scanner(System.in);
		database = new DerbyDatabase();
		
		// prompt user for item name
		System.out.print("Item name to query: ");
		String itemName = keyboard.next();
		
		List<Item> itemsWithName = ItemsByNameQuery.queryDatabase(itemName);
		
		System.out.println("   NAME   | DESCRIPTION ");
		for (Item item : itemsWithName) {
			System.out.println(String.format("%10s|%s", item.GetName(), item.GetDescription()));
		}
	}
}
