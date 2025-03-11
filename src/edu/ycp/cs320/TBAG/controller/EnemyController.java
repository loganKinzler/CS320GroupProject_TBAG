package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.EntityModel;

public class EnemyController extends EntityController {
	
	public EnemyController(EntityModel model, double maxHealth, int lives, int currentRoomIndex) {
		super(model, maxHealth, lives, currentRoomIndex);
	}
	public EnemyController() {
		super();
	}
}
