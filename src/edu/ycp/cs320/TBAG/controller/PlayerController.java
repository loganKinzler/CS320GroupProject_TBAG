package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.EntityModel;

public class PlayerController extends EntityController {
	
	public PlayerController(EntityModel model, double maxHealth, int lives, int currentRoomIndex) {
		super(model, maxHealth, lives, currentRoomIndex);
	}
	public PlayerController() {
		super();
	}
	
	public void Respawn() {
		//TODO fill this in
	}

}
