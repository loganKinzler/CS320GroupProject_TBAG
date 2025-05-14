package edu.ycp.cs320.TBAG.controller;

import java.util.HashSet;
import java.util.Set;

import edu.ycp.cs320.TBAG.model.EntityInventory;
import edu.ycp.cs320.TBAG.model.EntityModel;
import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.RoomInventory;
import edu.ycp.cs320.TBAG.model.Weapon;
import edu.ycp.cs320.TBAG.tbagdb.persist.IDatabase;

public class EntityController {
	private EntityModel model;
	
	public EntityController(EntityModel model) {
			this.model = model;
	}
	
	public EntityController() {
		this(null);
	}

	public void setModel(EntityModel model) {this.model = model;}
	public EntityModel getModel() {return model;}
	
	public void setMaxHealth(double maxHealth) {this.model.setMaxHealth(maxHealth);}
	public double getMaxHealth() {return this.model.getMaxHealth();}
	
	public void setHealth(double health) {this.model.setHealth(health);}
	public double getHealth() {return this.model.getHealth();}
	
	public void setLives(int lives) {this.model.setLives(lives);}
	public int getLives() {return this.model.getLives();}
	
	public void setCurrentRoomIndex(int currentRoomIndex) {this.model.setCurrentRoomIndex(currentRoomIndex);}
	public int getCurrentRoomIndex() {return this.model.getCurrentRoomIndex();}
	
	public void AddHealth(double amount) {
		this.model.setHealth(this.model.getHealth() + amount);
	}
	public void AddHealthClamped(double amount) { //Needed if adding health that would exceed health bounds but just want to go up to the bound
		//Clips amount to add within the bounds and then adds it to health
		double finalHealth = this.model.getHealth() + amount;
		if (finalHealth > this.model.getMaxHealth()) finalHealth = this.model.getMaxHealth();
		if (finalHealth < 0) finalHealth = 0;
		this.model.setHealth(finalHealth);
	}
	public EntityInventory getInventory() {return this.model.getInventory();}
	
	public void AddToInventory(Item toAdd, int amount) {
		this.model.getInventory().AddItems(toAdd, amount);
	}
	
	public void EquipWeapon(String weaponSlot, Weapon weapon) {
		this.model.getInventory().EquipWeapon(weaponSlot, weapon);
	}

	public Integer PickUp(Room room, Item toPickUp, Integer quantity) {
		Integer pickedUp = room.getRoomInventory().ExtractItems(toPickUp, quantity);
		AddToInventory(toPickUp, pickedUp);
		return pickedUp;
	}
	
	public Integer Drop(Room room, Item toDrop, Integer quantity) {
		if (room.getRoomInventory() == null) {
	        room.setRoomInventory(new RoomInventory());
	    }
		
		Integer dropped = model.getInventory().ExtractItems(toDrop, quantity);
		room.getRoomInventory().AddItems(toDrop, dropped);
		return dropped;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean Die(IDatabase db, Room room) {
		Set<Item> items = new HashSet( this.model.getInventory().GetItems().keySet() );
		
		// drop all enemy items
		for (Item item : items) this.Drop(room, item, Integer.MAX_VALUE);
		db.UpdateRoomInventory(room.getRoomId(), room.getRoomInventory());
		db.UpdateEnemyInventory(this.model.getId(), this.getInventory());
		
		// remove 1 life
		this.model.setLives( Math.max(0, this.model.getLives() - 1) );
		db.UpdateEnemyLivesById(this.model.getId(), this.getLives());
		
		return this.model.getLives() == 0;
	}

}
