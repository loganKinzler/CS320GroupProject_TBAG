package edu.ycp.cs320.TBAG.db;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import edu.ycp.cs320.TBAG.tbagdb.DBController;
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
		assertEquals(db.GetPlayer().getCurrentRoomIndex(), 1);
	}
	
	@Test
	public void setRoomTest() {
		setup();
		db.UpdatePlayerRoom(2);
		assertEquals(db.GetPlayer().getCurrentRoomIndex(), 2);
	}
	
	@Test
	public void startHealthTest() {
		setup();
		assertEquals(db.GetPlayer().getHealth(), 50.0, .001);
	}
	
	@Test
	public void setHealthTest() {
		setup();
		db.UpdatePlayerHealth(150.0);
		assertEquals(db.GetPlayer().getHealth(), 150.0, .001);
	}
	
	@Test
	public void startMaxHealthTest() {
		setup();
		assertEquals(db.GetPlayer().getMaxHealth(), 100.0, .001);
	}
	
	@Test
	public void setMaxHealthTest() {
		setup();
		db.UpdatePlayerMaxHealth(150.0);
		assertEquals(db.GetPlayer().getMaxHealth(), 150.0, .001);
	}
	
	@Test
	public void startLivesTest() {
		setup();
		assertEquals(db.GetPlayer().getLives(), 3);
	}
	
	@Test
	public void setLivesTest() {
		setup();
		db.UpdatePlayerLives(2);
		assertEquals(db.GetPlayer().getLives(), 2);
	}
	
	@Test
	public void decLivesTest() {
		setup();
		DBController.decrementPlayerLives(db);
		assertEquals(db.GetPlayer().getLives(), 2);
	}
}