package edu.ycp.cs320.TBAG.db;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import edu.ycp.cs320.TBAG.tbagdb.persist.DerbyDatabase;

public class PlayerDbTest {
	private DerbyDatabase db = new DerbyDatabase();
	
	@Before
	public void setup() {
		db = new DerbyDatabase();
		db.create();
	}
	
	@Test
	public void startRoomTest() {
		setup();
		assertEquals(db.GetPlayer().getCurrentRoomIndex(), 0);
	}
	
	@Test
	public void setRoomTest() {
		setup();
		db.UpdatePlayerRoom(2);
		assertEquals(db.GetPlayer().getCurrentRoomIndex(), 2);
	}
}
