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
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		Scanner keyboard = new Scanner(System.in);
		database = new DerbyDatabase("test");
		
		// prompt user for item name
		System.out.print("Room id to query: ");
		int room_id = keyboard.nextInt();
		
		List<Room> roomsWithId = RoomsByIdQuery.queryDatabase(room_id);
		
		System.out.println("   NAME   | DESCRIPTION | X_POSITION | Y_POSITION | HAS_ENTERED_ROOM | KEY");
		for (Room room : roomsWithId) {
			System.out.println(String.format("%10s|%s|%d|%d|%b|%s", room.getShortRoomDescription(), room.getLongRoomDescription(), room.getX_Position(), room.getY_Position(), room.getHas_Entered_Room(), room.getRoom_key()));
		}
	}
}
