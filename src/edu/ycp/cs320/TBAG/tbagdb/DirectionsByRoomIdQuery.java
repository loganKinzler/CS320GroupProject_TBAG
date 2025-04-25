/*package edu.ycp.cs320.TBAG.tbagdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.tbagdb.persist.IDatabase;
import edu.ycp.cs320.TBAG.tbagdb.persist.DerbyDatabase;

public class DirectionsByRoomIdQuery {
	private static IDatabase database;
	
	public DirectionsByRoomIdQuery(IDatabase db) {
		database = db;
	}
	
	public static List<String> queryDatabase(int id) {
		return database.DirectionsByRoomIdQuery(id);
	}
	
	public static void main(String[] args) throws Exception {
		Scanner keyboard = new Scanner(System.in);
		database = new DerbyDatabase();
		
		// prompt user for item name
		System.out.print("Room id to query: ");
		int room_id = keyboard.nextInt();
		
		List<String> roomsWithId = new ArrayList<>();
		roomsWithId = DirectionsByRoomIdQuery.queryDatabase(room_id);
		
		System.out.println("   NAME   | DESCRIPTION ");
		for (int i = 0; i<roomsWithId.size(); i++) {
			System.out.println(String.format("%10s", roomsWithId.get(i)));
		}
	}
}
*/
