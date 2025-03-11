package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.EntityModel;

public class EntityController {
	private EntityModel model;
	private double maxHealth, health;
	private int lives;
	
	public EntityController(EntityModel model, double maxHealth, int lives, int currentRoomIndex) {
			this.model = model;
			
			if (this.model != null) {
				this.model.setLives(lives);
				this.model.setMaxHealth(maxHealth);
				this.model.setHealth(maxHealth);
				this.model.setCurrentRoomIndex(currentRoomIndex);
			}
	}
	
	public EntityController() {
		this(null, 100, 1, -1);
	}
	
	public EntityModel getModel() {
		return model;
	}

	public void setModel(EntityModel model) {
		this.model = model;
	}

	public void Attack(EntityController receiver) {
		//TODO fill this in
	}
	public void Die() {
		//TODO fill this in
	}
	public void PickUp() {
		//TODO fill this in
	}
}
