package edu.ycp.cs320.TBAG.tbagdb;

import java.util.Scanner;

import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.tbagdb.persist.IDatabase;
import edu.ycp.cs320.TBAG.tbagdb.persist.DerbyDatabase;

public class ItemsByNameQuery {
	private static IDatabase database;
	
	public ItemsByNameQuery(IDatabase db) {
		database = db;
	}
	
	public static Item queryDatabase(String itemName) {
		return database.ItemsByNameQuery(itemName);
	}
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		Scanner keyboard = new Scanner(System.in);
		database = new DerbyDatabase("test");
		
		// prompt user for item name
		System.out.print("Item name to query: ");
		String itemName = keyboard.next();
		
		Item itemWithName = ItemsByNameQuery.queryDatabase(itemName);
		
		System.out.println("   NAME   | DESCRIPTION ");
		System.out.println(String.format("%10s|%s", itemWithName.GetName(), itemWithName.GetDescription()));
	}
}
