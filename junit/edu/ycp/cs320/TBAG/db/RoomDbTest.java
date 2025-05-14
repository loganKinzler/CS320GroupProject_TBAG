package edu.ycp.cs320.TBAG.db;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.tbagdb.DBController;
import edu.ycp.cs320.TBAG.tbagdb.persist.DerbyDatabase;

public class RoomDbTest {
	private DerbyDatabase db = new DerbyDatabase("test");
	private List<Room> rooms = new ArrayList<>();
	List<Room> connections = new ArrayList<>();
	
	@Before
	public void setup() {
		db = new DerbyDatabase("test");
		db.create();
        //connections.add(dummy);
//        rooms = (List<Room>)session.getAttribute("rooms");
        rooms = db.getRooms();
        connections = db.getConnections();
        
        for(int i = 0; i<rooms.size(); i++) {
        	rooms.get(i).setConnections(connections.get(i).getHashMap());
        }
	}
	
	@Test
	public void getRoomKey() {
		assertEquals(rooms.get(0).getRoom_key(), "none");
	}
	
	@Test
	public void updateLockedRoom() {
		setup();
        db.UpdateLockedRoom(2, "none");
        rooms = db.getRooms();
        assertEquals(rooms.get(1).getRoom_key(), "none");
	}
	
	@Test
	public void getShortDescription() {
		setup();
		assertEquals(rooms.get(0).getShortRoomDescription(), "Robot Room");
	}
	
	@Test
	public void getLongDescription() {
		setup();
		assertEquals(rooms.get(0).getLongRoomDescription(), "There are more robots hooked up to the wall...");
	}
	
	@Test
	public void getX_Position() {
		setup();
		assertEquals(rooms.get(0).getX_Position(),2);
	}
	
	@Test
	public void getY_Position() {
		setup();
		assertEquals(rooms.get(0).getY_Position(),5);
	}
	
	@Test
	public void getConnectedRoom() {
		setup();
		assertTrue(rooms.get(0).getConnectedRoom("west") == 2);
	}
	
	
	

}
