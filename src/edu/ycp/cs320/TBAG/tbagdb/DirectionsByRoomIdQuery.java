package edu.ycp.cs320.TBAG.tbagdb;

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
	
	public static List<Room> queryDatabase(int id) {
		return database.DirectionsByRoomIdQuery(id);
	}
	
	public static void main(String[] args) throws Exception {
		Scanner keyboard = new Scanner(System.in);
		database = new DerbyDatabase("test");
		
		// prompt user for item name
		System.out.print("Room id to query: ");
		int room_id = keyboard.nextInt();
		
		List<Room> roomsWithId = new ArrayList<>();
		roomsWithId = DirectionsByRoomIdQuery.queryDatabase(room_id);
		
		System.out.println("   ROOM_ID   |   NORTH   |     EAST    |   SOUTH   |    WEST");
		for (Room room : roomsWithId) {
			System.out.println(String.format("%d|%d|%d|%d|%d", room.getRoomId(), room.getConnectedRoom("north"), room.getConnectedRoom("east"), room.getConnectedRoom("south"), room.getConnectedRoom("west")));
		}
	}
}

