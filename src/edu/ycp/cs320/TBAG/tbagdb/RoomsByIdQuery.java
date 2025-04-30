package edu.ycp.cs320.TBAG.tbagdb;

import java.util.List;
import java.util.Scanner;

import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.tbagdb.persist.IDatabase;
import edu.ycp.cs320.TBAG.tbagdb.persist.DerbyDatabase;

public class RoomsByIdQuery {
	private static IDatabase database;
	
	public RoomsByIdQuery(IDatabase db) {
		database = db;
	}
	
	public static List<Room> queryDatabase(int id) {
		return database.RoomsByIdQuery(id);
	}
	
	public static void main(String[] args) throws Exception {
		Scanner keyboard = new Scanner(System.in);
		database = new DerbyDatabase("test");
		
		// prompt user for item name
		System.out.print("Room id to query: ");
		int room_id = keyboard.nextInt();
		
		List<Room> roomsWithId = RoomsByIdQuery.queryDatabase(room_id);
		
		System.out.println("   NAME   | DESCRIPTION ");
		for (Room room : roomsWithId) {
			System.out.println(String.format("%10s|%s", room.getShortRoomDescription(), room.getLongRoomDescription()));
		}
	}
}
