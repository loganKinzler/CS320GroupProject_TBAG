package edu.ycp.cs320.TBAG.controller;

import java.util.Set;
import java.util.HashSet;

import edu.ycp.cs320.TBAG.model.*;
import edu.ycp.cs320.TBAG.controller.*;

public class EntityController {
	private EntityModel model;
	private double maxHealth, health;
	private int lives;
	
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
	
	public void Die(RoomContainer rooms) {
		Set<Item> items = new HashSet( this.model.getInventory().GetItems().keySet() );
		
		// drop all enemy items
		for (Item item : items)
			this.Drop(rooms, item, Integer.MAX_VALUE);
		
		this.model.setLives( Math.min(0, this.model.getLives() - 1) );
	}

	public Integer PickUp(RoomContainer rooms, Item toPickUp, Integer quantity) {
		Integer pickedUp = rooms.ExtractItems(toPickUp, quantity, getCurrentRoomIndex());
		AddToInventory(toPickUp, pickedUp);
		return pickedUp;
	}
	
	public Integer Drop(RoomContainer rooms, Item toDrop, Integer quantity) {
		Integer dropped = model.getInventory().ExtractItems(toDrop, quantity);
		rooms.AddItems(toDrop, dropped, getCurrentRoomIndex());
		return dropped;
	}
}
