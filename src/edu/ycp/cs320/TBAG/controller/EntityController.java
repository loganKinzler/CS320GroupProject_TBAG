package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.*;

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

	public void Attack(EntityController receiver) {
		//TODO fill this in
	}
	public void Die() {
		//TODO fill this in
	}
	public boolean PickUp(RoomContainer rooms, Item toPickUp) {
		RoomInventory roomInv = rooms.getRoom(this.model.getCurrentRoomIndex()).getRoomInventory();
		
		if (roomInv.ContainsItem(toPickUp)) {
			int amount = roomInv.GetItemAmount(toPickUp);
			
			this.AddToInventory(toPickUp, amount);
			roomInv.ExtractItems(toPickUp, amount);
			
			System.out.println("----------");
			System.out.println(this.model.getInventory() + " from player");
			System.out.println(rooms.getRoom(this.model.getCurrentRoomIndex()).getRoomInventory() + " from room");
			
			return true;
		}
		else {
			System.out.println("item not found");
			return false;
		}
	}
}
